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
package org.jboss.test.ws.jaxws.samples.securityDomain;

import jakarta.jws.Oneway;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.jws.soap.SOAPBinding.Style;

@WebService(name = "SecureEndpoint", targetNamespace = "http://org.jboss.ws/securityDomain")
@SOAPBinding(style = Style.RPC)
public interface SecureEndpoint
{

   @WebMethod
   @WebResult(targetNamespace = "http://org.jboss.ws/securityDomain", partName = "return")
   public String echoForAll(@WebParam(name = "arg0", partName = "arg0") String arg0);

   @Oneway
   @WebMethod
   public void helloOneWay(@WebParam(name = "arg0", partName = "arg0")String arg0);

   @WebMethod
   @WebResult(targetNamespace = "http://org.jboss.ws/securityDomain", partName = "return")
   public String echo(@WebParam(name = "arg0", partName = "arg0") String arg0);

   @WebMethod
   @WebResult(targetNamespace = "http://org.jboss.ws/securityDomain", partName = "return")
   public String restrictedEcho(@WebParam(name = "arg0", partName = "arg0") String arg0);

   @WebMethod
   @WebResult(targetNamespace = "http://org.jboss.ws/securityDomain", partName = "return")
   public String defaultAccess(@WebParam(name = "arg0", partName = "arg0") String arg0);

}
