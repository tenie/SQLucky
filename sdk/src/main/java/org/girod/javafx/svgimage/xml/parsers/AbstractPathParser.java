/*
Copyright (c) 2021, 2023 Hervé Girod
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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An abstract path parser.
 *
 * @version 1.1
 */
public abstract class AbstractPathParser {
   public static final short PATH_NONE = -1;
   public static final short MOVE_TO = 0;
   public static final short CLOSE_PATH = 1;
   public static final short LINE_TO = 2;
   public static final short HORIZONTAL_LINE_TO = 3;
   public static final short VERTICAL_LINE_TO = 4;
   public static final short CUBIC_CURVE = 5;
   public static final short SMOOTH_CUBIC_CURVE = 6;
   public static final short QUADRATIC_CURVE = 7;
   public static final short SMOOTH_QUADRATIC_CURVE = 8;
   public static final short ELLIPTICAL_CURVE = 9;
   public static final Pattern LETTER = Pattern.compile("[sSlLhHvVmMcCqQtTaAzZ]");
   public static final Pattern PLUSMINUS = Pattern.compile("[+-]?\\d*([eE][+-]\\d+)?(\\.\\d+)?");

   protected void decomposePart(List<String> list, String token) {
      int offset = 0;
      Matcher m = PLUSMINUS.matcher(token);
      String part = token;
      while (true) {
         boolean found = m.find(offset);
         if (!found) {
            list.add(part);
            break;
         } else {
            int start = m.start();
            int end = m.end();
            String value = token.substring(start, end);
            list.add(value);
            offset = end;
            if (offset < token.length()) {
               part = token.substring(offset);
            } else {
               break;
            }
         }
      }
   }
}
