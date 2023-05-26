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
package org.girod.javafx.svgimage.xml.specs;

import javafx.scene.Node;

/**
 * Represents a viewbox.
 *
 * @version 1.0
 */
public class Viewbox {
   /**
    * The width attribute.
    */
   protected double width = 0;
   /**
    * The height attribute.
    */
   protected double height = 0;
   /**
    * True if the viewbox has a width and height.
    */
   protected boolean hasWidthAndHeight = false;
   /**
    * The x position of the viewBox attribute.
    */
   protected double viewboxX = 0;
   /**
    * The y position of the viewBox attribute.
    */
   protected double viewboxY = 0;
   /**
    * The width of the viewBox attribute.
    */
   protected double viewboxWidth = 0;
   /**
    * The height of the viewBox attribute.
    */
   protected double viewboxHeight = 0;
   /**
    * True if the "preserveAspectRatio" attribute has a value different from "none".
    */
   protected boolean preserveAspectRatio = true;

   public Viewbox(double width, double height) {
      this.width = width;
      this.height = height;
      this.hasWidthAndHeight = true;
   }

   /**
    * Set true if the aspect ratio must be preserved. Note that only two states are supported, contrary to the
    * full SVG specification.
    *
    * @param preserveAspectRatio true if the aspect ratio must be preserved
    */
   public void setPreserveAspectRatio(boolean preserveAspectRatio) {
      this.preserveAspectRatio = preserveAspectRatio;
   }

   /**
    * Return true if the aspect ratio must be preserved. Note that only two states are supported, contrary to the
    * full SVG specification.
    *
    * @return true if the aspect ratio must be preserved
    */
   public boolean isPreservingAspectRatio() {
      return preserveAspectRatio;
   }

   /**
    * Set the width and height of the viewBox
    *
    * @param viewboxX the viewBox X coordinate
    * @param viewboxY the viewBox y coordinate
    * @param viewboxWidth the viewBox width
    * @param viewboxHeight the viewBox height
    */
   public void setViewbox(double viewboxX, double viewboxY, double viewboxWidth, double viewboxHeight) {
      this.viewboxX = viewboxX;
      this.viewboxY = viewboxY;
      this.viewboxWidth = viewboxWidth;
      this.viewboxHeight = viewboxHeight;
   }

   /**
    * Scale a Node.
    *
    * @param node the node
    */
   public void scaleNode(Node node) {
      if (hasWidthAndHeight) {
         if (!preserveAspectRatio) {
            if (viewboxWidth != 0 && viewboxHeight != 0) {
               node.setScaleX(width / viewboxWidth);
               node.setScaleY(height / viewboxHeight);
            }
         } else if (viewboxWidth != 0) {
            node.setScaleX(width / viewboxWidth);
            node.setScaleY(width / viewboxWidth);
         }
      }
   }

   /**
    * Scale a SVG element attribute value. The value will only be scaled if the
    * {@link #isPreservingAspectRatio()} returns true.
    *
    * @param isWidth true for a width value
    * @param value the value
    * @return the scaled value
    */
   public double scaleValue(boolean isWidth, double value) {
      if (preserveAspectRatio) {
         return value * width / viewboxWidth;
      } else if (isWidth) {
         return value;
      } else {
         return value;
      }
   }

   /**
    * Return the viewBox x position.
    *
    * @return the x position
    */
   public double getViewboxX() {
      return viewboxX;
   }

   /**
    * Return the viewBox y position.
    *
    * @return the y position
    */
   public double getViewboxY() {
      return viewboxY;
   }

   /**
    * Return the viewBox width.
    *
    * @return the width
    */
   public double getViewboxWidth() {
      return viewboxWidth;
   }

   /**
    * Return the viewBox height.
    *
    * @return the height
    */
   public double getViewboxHeight() {
      return viewboxHeight;
   }

   /**
    * Return the width.
    *
    * @return the width
    */
   public double getWidth() {
      return width;
   }

   /**
    * Return the height.
    *
    * @return the height
    */
   public double getHeight() {
      return height;
   }
}
