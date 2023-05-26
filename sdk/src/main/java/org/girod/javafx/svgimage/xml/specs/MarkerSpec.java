/*
Copyright (c) 2022, Hervé Girod
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
package org.girod.javafx.svgimage.xml.specs;

import org.girod.javafx.svgimage.xml.parsers.ParserUtils;
import org.girod.javafx.svgimage.xml.parsers.SVGTags;
import org.girod.javafx.svgimage.xml.parsers.XMLNode;

/**
 * Represents a marker specifiation.
 *
 * @since 1.0
 */
public class MarkerSpec implements SVGTags {
   private Viewbox viewbox = null;
   private final XMLNode node;
   private double refX = 0;
   private double refY = 0;
   private double width = -1;
   private double height = -1;

   public MarkerSpec(XMLNode node) {
      this.node = node;
   }

   public void computeRefPosition(Viewport viewport) {
      if (node.hasAttribute(REFX)) {
         refX = -node.getLengthValue(REFX, viewport);
      }
      if (node.hasAttribute(REFY)) {
         refY = -node.getLengthValue(REFY, viewport);
      }
      if (node.hasAttribute(MARKER_WIDTH)) {
         width = node.getLengthValue(MARKER_WIDTH, viewport);
      }
      if (node.hasAttribute(MARKER_HEIGHT)) {
         height = node.getLengthValue(MARKER_HEIGHT, viewport);
      }
   }

   public double getWidth() {
      return width;
   }

   public double getHeight() {
      return height;
   }

   public double getRefX() {
      return refX;
   }

   public double getRefY() {
      return refY;
   }

   public void setViewbox(Viewbox viewbox) {
      this.viewbox = viewbox;
      if (node.hasAttribute(PRESERVE_ASPECT_RATIO)) {
         String value = node.getAttributeValue(PRESERVE_ASPECT_RATIO);
         boolean preserveAspectRatio = ParserUtils.getPreserveAspectRatio(value);
         viewbox.setPreserveAspectRatio(preserveAspectRatio);
      }
   }

   public boolean hasViewbox() {
      return viewbox != null;
   }

   public Viewbox getViewbox() {
      return viewbox;
   }

   public XMLNode getXMLNode() {
      return node;
   }

   public double scaleWidth(double coord) {
      return coord * 1;
   }

   public double scaleHeight(double coord) {
      return coord * 1;
   }
}
