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

/**
 * This utility class parse a percent value.
 *
 * @version 1.0
 */
public class PercentParser {
   private static final Pattern NUMBER = Pattern.compile("\\d+(\\.\\d+)?");
   private static final Pattern PERCENT_UNIT = Pattern.compile("(\\d+)(\\.\\d+)?%");

   private PercentParser() {
   }

   /**
    * Parse a node attribute as a percent value.
    *
    * @param node the node
    * @param attrName the attribute name
    * @param allowAbsolute true if absolute values must be allowed
    * @return the value clamped between and 1
    */
   public static double parseValue(XMLNode node, String attrName, boolean allowAbsolute) {
      String valueAsString = node.getAttributeValue(attrName);
      if (valueAsString != null) {
         return parseValue(valueAsString, allowAbsolute);
      } else {
         return 0;
      }
   }

   /**
    * Parse a node attribute as a percent value.
    *
    * @param node the node
    * @param attrName the attribute name
    * @return the value clamped between and 1
    */
   public static double parseValue(XMLNode node, String attrName) {
      return parseValue(node, attrName, false);
   }

   /**
    * Parse a percent value.
    *
    * @param value the value
    * @return the value clamped between and 1
    */
   public static double parseValue(String value) {
      return parseValue(value, false);
   }

   /**
    * Parse a percent value.
    *
    * @param value the value
    * @param allowAbsolute true if absolute values must be allowed
    * @return the value
    */
   public static double parseValue(String value, boolean allowAbsolute) {
      value = value.trim();
      Matcher m = NUMBER.matcher(value);
      if (m.matches()) {
         double parsedValue = Double.parseDouble(value);
         if (parsedValue < 0) {
            parsedValue = 0;
         } else if (!allowAbsolute && parsedValue > 1) {
            parsedValue = 1;
         }
         return parsedValue;
      }
      m = PERCENT_UNIT.matcher(value);
      if (m.matches()) {
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
         parsedValue = parsedValue / 100;
         if (parsedValue < 0) {
            parsedValue = 0;
         } else if (parsedValue > 1) {
            parsedValue = 1;
         }
         return parsedValue;
      }
      return 0d;
   }
}
