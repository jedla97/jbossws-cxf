/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.test.ws.jaxws.cxf.noIntegration;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.wsf.test.JBossWSTest;
import org.jboss.wsf.test.JBossWSTestHelper;
import org.jboss.wsf.test.JBossWSTestHelper.BaseDeployment;

/**
 * [AS7-537] Filter Apache CXF and dependencies
 * 
 * Verifies deployment fails if the webservices subsystem is not disabled for the current deployment
 * 
 * @author alessio.soldano@jboss.com
 * @since 15-Apr-2013
 */
public class AS7537TestCase extends JBossWSTest
{
   public static BaseDeployment<?>[] createDeployments() {
      final File springDir = new File(new File(JBossWSTestHelper.getTestResourcesDir()).getParentFile(), "spring");
      final File embeddedCXFDir = new File(new File(JBossWSTestHelper.getTestResourcesDir()).getParentFile(), "cxf-embedded");
      List<BaseDeployment<?>> list = new LinkedList<BaseDeployment<?>>();
      list.add(new JBossWSTestHelper.WarDeployment("jaxws-cxf-embedded-fail.war") { {
         archive
               .setManifest(new StringAsset("Manifest-Version: 1.0\n"
                     + "Dependencies: javax.wsdl4j.api,org.apache.ws.xmlschema,org.apache.neethi,org.codehaus.woodstox\n"))
               .addClass(org.jboss.test.ws.jaxws.cxf.noIntegration.EchoImpl.class)
               .addAsWebInfResource(new File(JBossWSTestHelper.getTestResourcesDir() + "/jaxws/cxf/noIntegration/embedded/WEB-INF/beans.xml"), "beans.xml")
               .setWebXML(new File(JBossWSTestHelper.getTestResourcesDir() + "/jaxws/cxf/noIntegration/embedded/WEB-INF/web.xml"))
               .addAsLibrary(new File(springDir, "spring-aop-3.0.3.RELEASE.jar"))
               .addAsLibrary(new File(springDir, "spring-asm-3.0.3.RELEASE.jar"))
               .addAsLibrary(new File(springDir, "spring-beans-3.0.3.RELEASE.jar"))
               .addAsLibrary(new File(springDir, "spring-context-3.0.3.RELEASE.jar"))
               .addAsLibrary(new File(springDir, "spring-core-3.0.3.RELEASE.jar"))
               .addAsLibrary(new File(springDir, "spring-expression-3.0.3.RELEASE.jar"))
               .addAsLibrary(new File(springDir, "spring-web-3.0.3.RELEASE.jar"))
               .addAsLibrary(new File(embeddedCXFDir, "cxf-api-2.6.6.jar"))
               .addAsLibrary(new File(embeddedCXFDir, "cxf-rt-bindings-soap-2.6.6.jar"))
               .addAsLibrary(new File(embeddedCXFDir, "cxf-rt-core-2.6.6.jar"))
               .addAsLibrary(new File(embeddedCXFDir, "cxf-rt-databinding-jaxb-2.6.6.jar"))
               .addAsLibrary(new File(embeddedCXFDir, "cxf-rt-frontend-jaxws-2.6.6.jar"))
               .addAsLibrary(new File(embeddedCXFDir, "cxf-rt-frontend-simple-2.6.6.jar"))
               .addAsLibrary(new File(embeddedCXFDir, "cxf-rt-transports-http-2.6.6.jar"))
               .addAsLibrary(new File(embeddedCXFDir, "cxf-rt-ws-policy-2.6.6.jar"));
         }
      });
      return list.toArray(new BaseDeployment<?>[list.size()]);
   }

   public void testFailureWithoutJBossDeploymentStructure() throws Exception {
      boolean undeploy = true;
      final String deploymentName = JBossWSTestHelper.writeToFile(createDeployments());
      try {
         JBossWSTestHelper.deploy(deploymentName);
         fail("Deployment failure expected");
      } catch (Exception e) {
         undeploy = false;
         assertTrue(e.getMessage().contains("JBAS015599") || e.getMessage().contains("WFLYWS0059"));
      } finally {
         if (undeploy) {
            try {
               JBossWSTestHelper.undeploy(deploymentName);
            } catch (Exception e) {
               //ignore
            }
         }
      }
   }
}