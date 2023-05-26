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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.geometry.Bounds;
import org.girod.javafx.svgimage.xml.specs.Viewport;

/**
 * This utility class parse a length value.
 *
 * @version 1.0
 */
public class LengthParser {
   private static final Pattern NUMBER = Pattern.compile("\\-?\\d*(\\.\\d+)?");
   private static final Pattern NUMBER_UNIT = Pattern.compile("(\\-?\\d+)(\\.\\d*)?([a-z%A-Z]+)");
   private static final double INCH = 1 / 96d;
   private static final Viewport DEFAULT_VIEWPORT = new Viewport();

   private LengthParser() {
   }

   /**
    * Parse a node attribute as a length value.
    *
    * @param node the node
    * @param viewport the viewport
    * @param attrName the attribute name
    * @return the value
    */
   public static double parseLength(XMLNode node, Viewport viewport, String attrName) {
      return parseLength(node, true, null, viewport, attrName);
   }

   /**
    * Parse a node attribute as a length value.
    *
    * @param node the node
    * @param isWidth for a width unit
    * @param viewport the viewport
    * @param attrName the attribute name
    * @return the value
    */
   public static double parseLength(XMLNode node, boolean isWidth, Viewport viewport, String attrName) {
      String valueAsString = node.getAttributeValue(attrName);
      if (valueAsString != null) {
         return parseLength(valueAsString, isWidth, null, viewport);
      } else {
         return 0;
      }
   }

   /**
    * Parse a node attribute as a length value.
    *
    * @param node the node
    * @param isWidth for a width unit
    * @param bounds the optional bounds of the figure for which it is relative to
    * @param viewport the viewport
    * @param attrName the attribute name
    * @return the value
    */
   public static double parseLength(XMLNode node, boolean isWidth, Bounds bounds, Viewport viewport, String attrName) {
      String valueAsString = node.getAttributeValue(attrName);
      if (valueAsString != null) {
         return parseLength(valueAsString, isWidth, bounds, viewport);
      } else {
         return 0;
      }
   }

   /**
    * Parse a length value.
    *
    * @param lengthValue the value
    * @param viewport the viewport
    * @return the value
    */
   public static double parseLength(String lengthValue, Viewport viewport) {
      return parseLength(lengthValue, true, null, viewport);
   }

   /**
    * Parse a length value.
    *
    * @param lengthValue the value
    * @param isWidth for a width unit
    * @param viewport the viewport
    * @return the value
    */
   public static double parseLength(String lengthValue, boolean isWidth, Viewport viewport) {
      return parseLength(lengthValue, isWidth, null, viewport);
   }

   /**
    * Parse a position value.
    *
    * @param lengthValue the value
    * @param isWidth true for a width length
    * @param bounds the optional bounds of the figure for which it is relative to
    * @param viewport the viewport
    * @return the value
    */
   public static double parsePosition(String lengthValue, boolean isWidth, Bounds bounds, Viewport viewport) {
      if (viewport == null) {
         viewport = DEFAULT_VIEWPORT;
      }
      lengthValue = lengthValue.trim();
      lengthValue = lengthValue.replace('−', '-');
      Matcher m = NUMBER.matcher(lengthValue);
      if (m.matches()) {
         if (bounds == null) {
            return viewport.scalePosition(Double.parseDouble(lengthValue), isWidth);
         } else if (isWidth) {
            return viewport.scalePosition(bounds.getMinX() + Double.parseDouble(lengthValue) * bounds.getWidth(), isWidth);
         } else {
            return viewport.scalePosition(bounds.getMinY() + Double.parseDouble(lengthValue) * bounds.getHeight(), isWidth);
         }
      }
      m = NUMBER_UNIT.matcher(lengthValue);
      if (m.matches()) {
         String unitS = m.group(m.groupCount());
         String startDigits = m.group(1);
         String endDigit = null;
         if (m.groupCount() > 1) {
            endDigit = m.group(2);
         }
         double parsedValue;
         if (endDigit == null) {
            parsedValue = Double.parseDouble(startDigits);
         } else {
            parsedValue = Double.parseDouble(startDigits + "." + endDigit);
         }
         switch (unitS) {
            case "px":
               return viewport.scalePosition(parsedValue, isWidth);
            case "pt":
               return viewport.scalePosition(parsedValue / INCH * 72d / 96d, isWidth);
            case "in":
               return viewport.scalePosition(parsedValue / INCH, isWidth);
            case "cm":
               return viewport.scalePosition(parsedValue / INCH * 72d / (96d * 2.54d), isWidth);
            case "mm":
               return viewport.scalePosition(parsedValue / INCH * 72d / (96d * 2.54d * 10), isWidth);
            case "%":
               if (viewport == null) {
                  return 0;
               } else if (isWidth) {
                  return viewport.scalePosition(parsedValue * viewport.getBestWidth() / 100, isWidth);
               } else {
                  return viewport.scalePosition(parsedValue * viewport.getBestHeight() / 100, isWidth);
               }
            default:
               return parsedValue;
         }
      }
      return 0d;
   }

   /**
    * Parse a line width value.
    *
    * @param lengthValue the value
    * @param viewport the viewport
    * @return the value
    */
   public static double parseLineWidth(String lengthValue, Viewport viewport) {
      if (viewport == null) {
         viewport = DEFAULT_VIEWPORT;
      }
      lengthValue = lengthValue.trim();
      lengthValue = lengthValue.replace('−', '-');
      Matcher m = NUMBER.matcher(lengthValue);
      if (m.matches()) {
         if (viewport == null) {
            return Double.parseDouble(lengthValue);
         } else {
            return viewport.scaleLineWidth(Double.parseDouble(lengthValue));
         }
      }
      m = NUMBER_UNIT.matcher(lengthValue);
      if (m.matches()) {
         String unitS = m.group(m.groupCount());
         String startDigits = m.group(1);
         String endDigit = null;
         if (m.groupCount() > 1) {
            endDigit = m.group(2);
         }
         double parsedValue;
         if (endDigit == null) {
            parsedValue = Double.parseDouble(startDigits);
         } else {
            parsedValue = Double.parseDouble(startDigits + endDigit);
         }
         switch (unitS) {
            case "px":
               return viewport.scaleLineWidth(parsedValue);
            case "pt":
               return viewport.scaleLineWidth(parsedValue * 96d / 72d);
            case "in":
               return viewport.scaleLineWidth(parsedValue / INCH);
            case "cm":
               return viewport.scaleLineWidth(parsedValue / INCH * 72d / (96d * 2.54d));
            case "mm":
               return viewport.scaleLineWidth(parsedValue / INCH * 72d / (96d * 2.54d * 10));
            case "%":
               return viewport.scaleLineWidth(parsedValue * viewport.getBestWidth() / 100);
            default:
               return viewport.scaleLineWidth(parsedValue);
         }
      }
      return 0d;
   }

   /**
    * Parse a length value.
    *
    * @param lengthValue the value
    * @param isWidth true for a width length
    * @param bounds the optional bounds of the figure for which it is relative to
    * @param viewport the viewport
    * @return the value
    */
   public static double parseLength(String lengthValue, boolean isWidth, Bounds bounds, Viewport viewport) {
      if (viewport == null) {
         viewport = DEFAULT_VIEWPORT;
      }
      lengthValue = lengthValue.trim();
      lengthValue = lengthValue.replace('−', '-');
      Matcher m = NUMBER.matcher(lengthValue);
      if (m.matches()) {
         if (viewport == null) {
            return Double.parseDouble(lengthValue);
         } else if (bounds == null) {
            return viewport.scaleLength(Double.parseDouble(lengthValue));
         } else if (isWidth) {
            return viewport.scaleLength(Double.parseDouble(lengthValue) * bounds.getWidth());
         } else {
            return viewport.scaleLength(Double.parseDouble(lengthValue) * bounds.getHeight());
         }
      }
      m = NUMBER_UNIT.matcher(lengthValue);
      if (m.matches()) {
         String unitS = m.group(m.groupCount());
         String startDigits = m.group(1);
         String endDigit = null;
         if (m.groupCount() > 1) {
            endDigit = m.group(2);
         }
         double parsedValue;
         if (endDigit == null) {
            parsedValue = Double.parseDouble(startDigits);
         } else {
            parsedValue = Double.parseDouble(startDigits + endDigit);
         }
         switch (unitS) {
            case "px":
               return viewport.scaleLength(parsedValue);
            case "pt":
               return viewport.scaleLength(parsedValue * 96d / 72d);
            case "em":
               return viewport.scaleLength(parsedValue * 16);
            case "ex":
               // this is an approximation
               // see https://stackoverflow.com/questions/918612/what-is-the-value-of-the-css-ex-unit
               return viewport.scaleLength(parsedValue * 16 * (1.5d / 3d));
            case "in":
               return viewport.scaleLength(parsedValue / INCH);
            case "cm":
               return viewport.scaleLength(parsedValue * (96 / 2.54));
            case "mm":
               return viewport.scaleLength(parsedValue * (96 / (10 * 2.54)));
            case "%":
               if (isWidth) {
                  return viewport.scaleLength(parsedValue * viewport.getBestWidth() / 100);
               } else {
                  return viewport.scaleLength(parsedValue * viewport.getBestHeight() / 100);
               }
            default:
               return viewport.scaleLength(parsedValue);
         }
      }
      return 0d;
   }
}
