/*
Copyright (c) 2021, Hervé Girod
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
package org.girod.javafx.svgimage;

/**
 * The parameters used for loading a SVG file or URL. By default:
 * <ul>
 * <li>No styleSheets is used</li>
 * <li>No scaling is applied</li>
 * <li>No width setting is applied</li>
 * <li>The animations are auto started</li>
 * <li>The resulting image is not centered</li>
 * <li>Ther viewPort position is taken into account</li>
 * </ul>
 *
 * @version 0.6.1
 */
public class LoaderParameters implements Cloneable {
   /**
    * The styleSheets. The default is null, which means that no styleSheets is used.
    */
   public String styleSheets = null;
   /**
    * The scale. The default is -1, which means that no scaling is applied.
    */
   public double scale = -1;
   /**
    * True if line widths must also be scaled. The default is true, which means that line widths are also scaled.
    */
   public boolean scaleLineWidth = true;
   /**
    * The width. The default is -1, which means that no width setting is applied.
    */
   public double width = -1;
   /**
    * True if animations should be auto-started. The default is true.
    */
   public boolean autoStartAnimations = true;
   /**
    * True if the resulting image must be centered. The default is false.
    */
   public boolean centerImage = false;
   /**
    * True if the x and y position of the viewPort is applied. The default is true.
    */
   public boolean applyViewportPosition = true;

   /**
    * Create a clone of the parameters.
    *
    * @return the cloned parameters
    */
   @Override
   public LoaderParameters clone() {
      try {
         LoaderParameters params = (LoaderParameters) super.clone();
         return params;
      } catch (CloneNotSupportedException ex) {
         // we should never go there
         return this;
      }
   }

   /**
    * Create parameters with a width property.
    *
    * @param width the width
    * @return the LoaderParameters
    */
   public static LoaderParameters createWidthParameters(double width) {
      LoaderParameters params = new LoaderParameters();
      params.width = width;
      return params;
   }

   /**
    * Create a parameters with a scale property.
    *
    * @param scale the scale
    * @return the LoaderParameters
    */
   public static LoaderParameters createScaleParameters(double scale) {
      LoaderParameters params = new LoaderParameters();
      params.scale = scale;
      return params;
   }
}
