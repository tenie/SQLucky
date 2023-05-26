/*
Copyright (c) 2021, 2022 Hervé Girod
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Alternatively if you have any questions about this project, you can visit
the project website at the project page on https://github.com/hervegirod/fxsvgimage
 */
package org.girod.javafx.svgimage.xml.parsers;

import java.util.Iterator;
import java.util.Objects;

/**
 * A root Node in an XML File.
 *
 * @version 1.0
 */
public class XMLRoot extends XMLNode {
   private String encoding = null;

   /**
    * Create the root Node.
    *
    * @param nodeName the Node name
    */
   public XMLRoot(String nodeName) {
      super(nodeName);
   }

   /**
    * Set the encoding of the XML file.
    *
    * @param encoding the encoding of the XML file
    */
   public void setEncoding(String encoding) {
      this.encoding = encoding;
   }

   /**
    * Return the declared encoding of the XML file. Return null if there is no declared encoding in the file.
    *
    * @return the declared encoding of the XML file
    */
   public String getEncoding() {
      return encoding;
   }

   /**
    * Return true if this node is equal to another Object.
    *
    * @param o the object
    * @return true true if this node is equal to the Object
    */
   @Override
   public boolean equals(Object o) {
      if (!(o instanceof XMLNode)) {
         return false;
      }
      if (!(o.getClass() == XMLRoot.class)) {
         return false;
      }
      XMLRoot node = (XMLRoot) o;
      if (!node.getName().equals(name)) {
         return false;
      }
      if (node.encoding == null && encoding != null) {
         return false;
      }
      if (node.encoding != null && encoding == null) {
         return false;
      }
      if (encoding != null && !node.encoding.equals(encoding)) {
         return false;
      }
      if (node.getAttributes().size() != attributes.size()) {
         return false;
      }
      Iterator<String> it = node.getAttributes().keySet().iterator();
      while (it.hasNext()) {
         String key = it.next();
         if (!attributes.containsKey(key)) {
            return false;
         }
         String value = node.getAttributes().get(key);
         String value2 = attributes.get(key);
         if (!value.equals(value2)) {
            return false;
         }
      }
      if (node.getChildren().size() != children.size()) {
         return false;
      }
      for (int i = 0; i < node.getChildren().size(); i++) {
         XMLNode child = node.getChildren().get(i);
         XMLNode child2 = children.get(i);
         if (!child.equals(child2)) {
            return false;
         }
      }
      return true;
   }

   @Override
   public int hashCode() {
      int hash = 7;
      hash = 59 * hash + Objects.hashCode(this.encoding);
      hash = 59 * hash + Objects.hashCode(this.name);
      hash = 59 * hash + Objects.hashCode(this.children);
      hash = 59 * hash + Objects.hashCode(this.attributes);
      return hash;
   }
}
