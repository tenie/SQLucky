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
package org.girod.javafx.svgimage;

import java.net.URL;

/**
 * Represents the SVG content origin.
 *
 * @since 1.0
 */
public class SVGContent {
   /**
    * The url of the SVG file.
    */
   public final URL url;
   /**
    * The SVG content as a String.
    */
   public final String content;
   /**
    * The loader parameters.
    */
   public final LoaderParameters params;

   /**
    * Constructor.
    *
    * @param url the url of the SVG file
    * @param params the loader parameters
    */
   public SVGContent(URL url, LoaderParameters params) {
      this.url = url;
      this.params = params;
      this.content = null;
   }

   /**
    * Constructor.
    *
    * @param content the SVG content as a String
    * @param params the loader parameters
    */
   public SVGContent(String content, LoaderParameters params) {
      this.url = null;
      this.params = params;
      this.content = content;
   }

   /**
    * Return true if the SVG content if from an URL.
    *
    * @return true if the SVG content if from an URL
    */
   public boolean isFromURL() {
      return url != null;
   }

   /**
    * Return true if the SVG content if from a String.
    *
    * @return true if the SVG content if from a String
    */
   public boolean isFromString() {
      return content != null;
   }
}
