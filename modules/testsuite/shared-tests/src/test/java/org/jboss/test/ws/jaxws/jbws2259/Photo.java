/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.test.ws.jaxws.jbws2259;

import jakarta.activation.DataHandler;
import jakarta.xml.bind.annotation.XmlMimeType;

/**
 * Representation of a photo to test marshalling.
 * 
 * @author darran.lofthouse@jboss.com
 * @since 27th March 2009
 */
public class Photo
{

   private String caption;

   private String expectedContentType;
   
   private DataHandler image;

   public String getCaption()
   {
      return caption;
   }

   public void setCaption(String caption)
   {
      this.caption = caption;
   }

   public String getExpectedContentType()
   {
      return expectedContentType;
   }

   public void setExpectedContentType(String expectedContentType)
   {
      this.expectedContentType = expectedContentType;
   }

   @XmlMimeType("*/*")
   public DataHandler getImage()
   {
      return image;
   }

   public void setImage(DataHandler image)
   {
      this.image = image;
   }

}
