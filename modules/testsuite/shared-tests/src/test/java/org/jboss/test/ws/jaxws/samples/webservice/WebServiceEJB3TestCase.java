/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.test.ws.jaxws.samples.webservice;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import jakarta.xml.ws.Service;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
//----------------

/**
 * Test the JSR-181 annotation: jakarta.jws.WebService
 *
 * @author Thomas.Diesler@jboss.org
 * @author <a href="jason.greene@jboss.com">Jason T. Greene</a>
 * @since 29-Apr-2005
 */
@RunWith(Arquillian.class)
public class WebServiceEJB3TestCase extends JBossWSTest
{
   @ArquillianResource
   private URL baseURL;

   private static final String WEBSERVICE_02 = "jaxws-samples-webservice02-ejb3";

   @Deployment(name = WEBSERVICE_02, testable = false)
   public static JavaArchive createDeployment2() {
      JavaArchive archive = ShrinkWrap.create(JavaArchive.class, WEBSERVICE_02 + ".jar");
      archive
         .addManifest()
         .addClass(org.jboss.test.ws.jaxws.samples.webservice.EJB3Bean02.class)
         .addClass(org.jboss.test.ws.jaxws.samples.webservice.EJB3RemoteInterface.class)
         .addAsManifestResource(new File(JBossWSTestHelper.getTestResourcesDir() + "/jaxws/samples/webservice/META-INF02/wsdl/TestService.wsdl"), "wsdl/TestService.wsdl");
      return archive;
   }

   private static final String WEBSERVICE_01 = "jaxws-samples-webservice01-ejb3";

   @Deployment(name = WEBSERVICE_01, testable = false)
   public static JavaArchive createDeployment1() {
      JavaArchive archive = ShrinkWrap.create(JavaArchive.class, WEBSERVICE_01 + ".jar");
      archive
         .addManifest()
         .addClass(org.jboss.test.ws.jaxws.samples.webservice.EJB3Bean01.class)
         .addClass(org.jboss.test.ws.jaxws.samples.webservice.EJB3RemoteInterface.class);
      return archive;
   }

   private static final String WEBSERVICE_03 = "jaxws-samples-webservice03-ejb3";

   @Deployment(name = WEBSERVICE_03, testable = false)
   public static JavaArchive createDeployment3() {
      JavaArchive archive = ShrinkWrap.create(JavaArchive.class, WEBSERVICE_03 + ".jar");
      archive
         .addManifest()
         .addClass(org.jboss.test.ws.jaxws.samples.webservice.EJB3Bean03.class)
         .addClass(org.jboss.test.ws.jaxws.samples.webservice.EJB3RemoteInterface.class)
         .addClass(org.jboss.test.ws.jaxws.samples.webservice.EndpointInterface03.class);
      return archive;
   }
   
   private EndpointInterface getPort(String endpointURI) throws MalformedURLException
   {
      QName serviceName = new QName("http://www.openuri.org/2004/04/HelloWorld", "EndpointService");
      URL wsdlURL = new URL("http://" + baseURL.getHost() + ":" + baseURL.getPort() + "/" + endpointURI + "?wsdl");

      Service service = Service.create(wsdlURL, serviceName);
      return service.getPort(EndpointInterface.class);
   }

   private EndpointInterface03 getPort03(String endpointURI) throws MalformedURLException
   {
      QName serviceName = new QName("http://www.openuri.org/2004/04/HelloWorld", "EndpointService");
      URL wsdlURL = new URL("http://" + baseURL.getHost() + ":" + baseURL.getPort() + "/" + endpointURI + "?wsdl");

      Service service = Service.create(wsdlURL, serviceName);
      return service.getPort(EndpointInterface03.class);
   }

   public void webServiceTest(String endpointURI) throws Exception
   {
      String helloWorld = "Hello world!";
      Object retObj = getPort(endpointURI).echo(helloWorld);
      assertEquals(helloWorld, retObj);
   }

   public void webServiceWsdlLocationTest(String endpointURI) throws Exception
   {
      String helloWorld = "Hello world!";
      Object retObj = getPort(endpointURI).echo(helloWorld);
      assertEquals(helloWorld, retObj);
   }

   public void webServiceEndpointInterfaceTest(String endpointURI) throws Exception
   {
      String helloWorld = "Hello Interface!";
      Object retObj = getPort03(endpointURI).echo(helloWorld);
      assertEquals(helloWorld, retObj);
   }

   @Test
   @RunAsClient
   @OperateOnDeployment(WEBSERVICE_01)
   public void testWebServiceTest() throws Exception
   {
      webServiceTest("jaxws-samples-webservice01-ejb3");
   }

   @Test
   @RunAsClient
   @OperateOnDeployment(WEBSERVICE_02)
   public void testWebServiceWsdlLocationTest() throws Exception
   {
      webServiceWsdlLocationTest("jaxws-samples-webservice02-ejb3");
   }

   @Test
   @RunAsClient
   @OperateOnDeployment(WEBSERVICE_03)
   public void testWebServiceEndpointInterfaceTest() throws Exception
   {
      webServiceEndpointInterfaceTest("jaxws-samples-webservice03-ejb3");
   }
 }
