/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.test.ws.jaxws.cxf.jms;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import junit.framework.Test;

import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.jms.JMSConduit;
import org.apache.cxf.transport.jms.JMSConfiguration;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.ws.common.DOMUtils;
import org.jboss.ws.common.IOUtils;
import org.jboss.wsf.test.JBossWSCXFTestSetup;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestHelper;
import org.jboss.wsf.test.JBossWSTestHelper.BaseDeployment;

/**
 * Test case for deploying an archive with a JMS (SOAP-over-JMS 1.0) endpoint only 
 *
 * @author alessio.soldano@jboss.com
 * @since 10-Jun-2011
 */
public class JMSEndpointOnlyDeploymentTestCaseForked extends JBossWSTest
{
   private static volatile boolean waitForResponse;
   
   public static BaseDeployment<?>[] createDeployments() {
      List<BaseDeployment<?>> list = new LinkedList<BaseDeployment<?>>();
      list.add(new JBossWSTestHelper.WarDeployment("jaxws-cxf-jms-only-deployment-test-servlet.war") { {
         archive
               .setManifest(new StringAsset("Manifest-Version: 1.0\n"
                     + "Dependencies: org.jboss.ws.cxf.jbossws-cxf-client services,org.hornetq\n"))
               .addAsWebInfResource(new File(JBossWSTestHelper.getTestResourcesDir() + "/jaxws/cxf/jms/META-INF/wsdl/HelloWorldService.wsdl"), "classes/META-INF/wsdl/HelloWorldService.wsdl")
               .addClass(org.jboss.test.ws.jaxws.cxf.jms.DeploymentTestServlet.class)
               .addClass(org.jboss.test.ws.jaxws.cxf.jms.HelloWorld.class);
         }
      });
      list.add(new JBossWSTestHelper.JarDeployment("jaxws-cxf-jms-only-deployment.jar") { {
         archive
               .setManifest(new StringAsset("Manifest-Version: 1.0\n"
                     + "Dependencies: org.hornetq\n"))
               .addClass(org.jboss.test.ws.jaxws.cxf.jms.HelloWorld.class)
               .addClass(org.jboss.test.ws.jaxws.cxf.jms.HelloWorldImpl.class)
               .addAsManifestResource(new File(JBossWSTestHelper.getTestResourcesDir() + "/jaxws/cxf/jms/META-INF/wsdl/HelloWorldService.wsdl"), "wsdl/HelloWorldService.wsdl");
         }
      });
      return list.toArray(new BaseDeployment<?>[list.size()]);
   }

   public static Test suite()
   {
      return new JBossWSCXFTestSetup(JMSEndpointOnlyDeploymentTestCaseForked.class, JBossWSTestHelper.writeToFile(createDeployments()));
   }
   
   public void testJMSEndpointServerSide() throws Exception
   {
      URL url = new URL("http://" + getServerHost() + ":8080/jaxws-cxf-jms-only-deployment-test-servlet");
      assertEquals("true", IOUtils.readAndCloseStream(url.openStream()));
   }
   
   public void testJMSEndpointClientSide() throws Exception
   {
      URL wsdlUrl = getResourceURL("jaxws/cxf/jms/META-INF/wsdl/HelloWorldService.wsdl");
      QName serviceName = new QName("http://org.jboss.ws/jaxws/cxf/jms", "HelloWorldService");

      Service service = Service.create(wsdlUrl, serviceName);
      HelloWorld proxy = (HelloWorld) service.getPort(new QName("http://org.jboss.ws/jaxws/cxf/jms", "HelloWorldImplPort"), HelloWorld.class);
      setupProxy(proxy);
      try {
         assertEquals("Hi", proxy.echo("Hi"));
      } catch (Exception e) {
         rethrowAndHandleAuthWarning(e);
      }
   }
   
   public void testMessagingClient() throws Exception
   {
      String reqMessage =
         "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
         "  <soap:Body>" +
         "    <ns2:echo xmlns:ns2=\"http://org.jboss.ws/jaxws/cxf/jms\">" +
         "      <arg0>Hi</arg0>" +
         "    </ns2:echo>" +
         "  </soap:Body>" +
         "</soap:Envelope>";

      String resMessage =
         "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
         "  <soap:Body>" +
         "    <ns2:echoResponse xmlns:ns2=\"http://org.jboss.ws/jaxws/cxf/jms\">" +
         "      <return>Hi</return>" +
         "    </ns2:echoResponse>" +
         "  </soap:Body>" +
         "</soap:Envelope>";

      Properties env = new Properties();
      env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
      env.put(Context.PROVIDER_URL, "http-remoting://" + getServerHost() + ":8080");
      env.put(Context.SECURITY_PRINCIPAL, JBossWSTestHelper.getTestUsername());
      env.put(Context.SECURITY_CREDENTIALS, JBossWSTestHelper.getTestPassword());
      InitialContext context = null;
      try {
         context = new InitialContext(env);
      } catch (Exception e) {
         rethrowAndHandleAuthWarning(e);
      }
      QueueConnectionFactory connectionFactory = (QueueConnectionFactory)context.lookup("jms/RemoteConnectionFactory");
      Queue reqQueue = (Queue)context.lookup("jms/queue/test");
      Queue resQueue = (Queue)context.lookup("jms/queue/test");

      QueueConnection con = connectionFactory.createQueueConnection(JBossWSTestHelper.getTestUsername(), JBossWSTestHelper.getTestPassword());
      QueueSession session = con.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
      QueueReceiver receiver = session.createReceiver(resQueue);
      ResponseListener responseListener = new ResponseListener();
      receiver.setMessageListener(responseListener);
      con.start();

      TextMessage message = session.createTextMessage(reqMessage);
      message.setJMSReplyTo(resQueue);
      message.setStringProperty("SOAPJMS_contentType", "text/xml");
      message.setStringProperty("SOAPJMS_requestURI", "jms:queue:testQueue");

      waitForResponse = true;

      QueueSender sender = session.createSender(reqQueue);
      sender.send(message);
      sender.close();

      int timeout = 30000;
      while (waitForResponse && timeout > 0)
      {
         Thread.sleep(100);
         timeout -= 100;
      }

      assertNotNull("Expected response message", responseListener.resMessage);
      assertEquals(DOMUtils.parse(resMessage), DOMUtils.parse(responseListener.resMessage));

      sender.close();
      receiver.close();
      con.stop();
      session.close();
      con.close();
   }
   
   public static class ResponseListener implements MessageListener
   {
      public String resMessage;

      public void onMessage(Message msg)
      {
         TextMessage textMessage = (TextMessage)msg;
         try
         {
            resMessage = textMessage.getText();
            waitForResponse = false;
         }
         catch (Throwable t)
         {
            t.printStackTrace();
         }
      }
   }
   
   private void setupProxy(HelloWorld proxy) {
      JMSConduit conduit = (JMSConduit)ClientProxy.getClient(proxy).getConduit();
      JMSConfiguration config = conduit.getJmsConfig();
      config.setUserName(JBossWSTestHelper.getTestUsername());
      config.setPassword(JBossWSTestHelper.getTestPassword());
      Properties props = conduit.getJmsConfig().getJndiEnvironment();
      props.put(Context.SECURITY_PRINCIPAL, JBossWSTestHelper.getTestUsername());
      props.put(Context.SECURITY_CREDENTIALS, JBossWSTestHelper.getTestPassword());
   }
   
   private static void rethrowAndHandleAuthWarning(Exception e) throws Exception {
      System.out.println("This test requires a testQueue JMS queue and a user with 'guest' role to be available on the application server; " +
            "queue are easily added using jboss-cli.sh/bat, while users are added using add-user.sh/bat. When running test please specify user " +
            "and password using -Dtest.username=\"foo\" -Dtest.password=\"bar\".");
      throw e;
   }
}
