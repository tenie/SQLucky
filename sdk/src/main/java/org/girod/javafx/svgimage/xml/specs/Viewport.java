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

import javafx.scene.shape.Shape;

/**
 * Represents the viewport.
 *
 * @version 1.0
 */
public class Viewport extends Viewbox {
   private double scale = -1;
   private boolean isScaled = false;
   private boolean scaleLineWidth = true;

   public Viewport(double width, double height) {
      super(width, height);
      this.hasWidthAndHeight = true;
   }

   public Viewport() {
      super(0, 0);
      this.hasWidthAndHeight = false;
   }

   /**
    * Return the "best" width. It will return the width if it exists, else it will be the viewBox width.
    *
    * @return the width
    */
   public double getBestWidth() {
      if (hasWidthAndHeight) {
         return width;
      } else {
         return viewboxWidth;
      }
   }

   /**
    * Return the height. It will return the height if it exists, else it will be the viewBox height.
    *
    * @return the height
    */
   public double getBestHeight() {
      if (hasWidthAndHeight) {
         return height;
      } else {
         return viewboxHeight;
      }
   }

   /**
    * Scale a shape.
    *
    * @param shape the shape
    */
   public void scaleShape(Shape shape) {
      if (isScaled) {
         shape.setScaleX(scale);
         shape.setScaleY(scale);
      }
   }

   /**
    * Return a default position. Note that for the moment, this method returns the input value.
    *
    * @param defaultValue the default position value
    * @param isWidth true for a width
    * @return the default position
    */
   public double getDefaultPosition(double defaultValue, boolean isWidth) {
      return defaultValue;
   }

   /**
    * Scale a position value.
    *
    * @param value the position
    * @param isWidth true for a widgdth coordinate
    * @return the scaled position
    */
   public double scalePosition(double value, boolean isWidth) {
      if (isScaled) {
         return value * scale;
      } else {
         return value;
      }
   }

   /**
    * Scale a length value.
    *
    * @param length the length
    * @return the scaled length
    */
   public double scaleLength(double length) {
      if (isScaled) {
         return length * scale;
      } else {
         return length;
      }
   }

   /**
    * Scale a line width.
    *
    * @param width the line width.
    * @return the scaled line width
    */
   public double scaleLineWidth(double width) {
      if (scaleLineWidth) {
         return scaleLength(width);
      } else {
         return width;
      }
   }

   /**
    * Set the scale value.
    *
    * @param scale the scale
    * @param scaleLineWidth true if line widths must also be scaled
    */
   public void setScale(double scale, boolean scaleLineWidth) {
      if (scale > 0) {
         this.isScaled = true;
         this.scaleLineWidth = scaleLineWidth;
         this.scale = scale;
      } else {
         this.isScaled = false;
         this.scaleLineWidth = false;
         this.scale = -1;
      }
   }

   /**
    * Return the scale.
    *
    * @return the scale
    */
   public double getScale() {
      return scale;
   }

   /**
    * Return true if lengths must be scaled.
    *
    * @return true if lengths must be scaled
    */
   public boolean isScaled() {
      return isScaled;
   }

   /**
    * Return true if line widths must be scaled.
    *
    * @return true if line widths must be scaled
    */
   public boolean isScalingLineWidth() {
      return scaleLineWidth;
   }
}
