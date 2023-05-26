/*
Copyright (c) 2022 Hervé Girod
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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import org.girod.javafx.svgimage.xml.specs.Viewport;

/**
 * This class parse a path content specification.
 *
 * @version 1.0
 */
public class MarkerContextPathParser extends AbstractPathParser {

   public MarkerContextPathParser() {
   }

   /**
    * Parse a path content.
    *
    * @param content the path content
    * @param viewport the viewport
    */
   public void parsePathContent(String content, Viewport viewport) {
      short type = PATH_NONE;
      int index = 0;
      boolean isFirst = true;
      List<String> list = new ArrayList<>();
      StringTokenizer tok = new StringTokenizer(content, " ,");
      while (tok.hasMoreTokens()) {
         String tk = tok.nextToken();
         Matcher m = LETTER.matcher(tk);
         int offset = 0;
         String part = tk;
         while (true) {
            boolean found = m.find(offset);
            if (!found) {
               decomposePart(list, part);
               break;
            }
            int start = m.start();
            int end = m.end();
            if (start > 0) {
               String previousPart = tk.substring(offset, start);
               decomposePart(list, previousPart);
            }
            String letter = tk.substring(start, end);
            list.add(letter);
            offset = end;
            if (offset < tk.length()) {
               part = tk.substring(offset);
            } else {
               break;
            }
         }
      }
      for (int i = 0; i < list.size(); i++) {
         String tk = list.get(i);
         switch (tk) {
            case "m":
            case "M":
               type = MOVE_TO;
               index = -1;
               break;
            case "h":
            case "H":
               type = HORIZONTAL_LINE_TO;
               index = -1;
               break;
            case "v":
            case "V":
               type = VERTICAL_LINE_TO;
               index = -1;
               break;
            case "l":
            case "L":
               type = LINE_TO;
               index = -1;
               break;
            case "c":
            case "C":
               type = CUBIC_CURVE;
               index = -1;
               break;
            case "q":
            case "Q":
               type = QUADRATIC_CURVE;
               index = -1;
               break;
            case "s":
            case "S":
               type = SMOOTH_CUBIC_CURVE;
               index = -1;
               break;
            case "t":
            case "T":
               type = SMOOTH_QUADRATIC_CURVE;
               index = -1;
               break;
            case "a":
            case "A":
               type = ELLIPTICAL_CURVE;
               index = -1;
               break;
            case "z":
            case "Z":
               index = -1;
               type = CLOSE_PATH;
               break;
            default:
               switch (type) {
                  case MOVE_TO:
                  case LINE_TO:
                     index++;
                     if (index == 2) {
                        index = 0;
                     }
                     if (index == 0) {
                        double d = LengthParser.parseLength(tk, true, viewport);
                     } else if (index == 1) {
                        double d = LengthParser.parseLength(tk, false, viewport);
                     } else {
                     }
                     break;
                  case HORIZONTAL_LINE_TO: {
                     index++;
                     double d = LengthParser.parseLength(tk, true, viewport);
                     break;
                  }
                  case VERTICAL_LINE_TO: {
                     index++;
                     double d = LengthParser.parseLength(tk, false, viewport);
                     break;
                  }
                  case CUBIC_CURVE:
                     index++;
                     if (index == 6) {
                        index = 0;
                     }
                     if (index == 0 || index == 2 || index == 4) {
                        double d = LengthParser.parseLength(tk, true, viewport);
                     } else if (index == 1 || index == 3 || index == 5) {
                        double d = LengthParser.parseLength(tk, false, viewport);
                     } else {
                     }
                     break;
                  case SMOOTH_CUBIC_CURVE:
                  case QUADRATIC_CURVE:
                     index++;
                     if (index == 4) {
                        index = 0;
                     }
                     if (index == 0 || index == 2) {
                        double d = LengthParser.parseLength(tk, true, viewport);
                     } else if (index == 1 || index == 3) {
                        double d = LengthParser.parseLength(tk, false, viewport);
                     } else {
                     }
                     break;
                  case SMOOTH_QUADRATIC_CURVE:
                     index++;
                     if (index == 3) {
                        index = 0;
                     }
                     if (index == 0) {
                        double d = LengthParser.parseLength(tk, true, viewport);
                     } else if (index == 1) {
                        double d = LengthParser.parseLength(tk, false, viewport);
                     } else {
                     }
                     break;
                  case ELLIPTICAL_CURVE:
                     index++;
                     if (index == 0) {
                        double d = LengthParser.parseLength(tk, true, viewport);
                     } else if (index == 1) {
                        double d = LengthParser.parseLength(tk, false, viewport);
                     } else if (index == 2) {
                        double d = ParserUtils.parseDoubleProtected(tk);
                     } else if (index == 3 || index == 4) {
                     } else if (index == 5) {
                        double d = LengthParser.parseLength(tk, true, viewport);
                     } else if (index == 6) {
                        double d = LengthParser.parseLength(tk, false, viewport);
                     } else {
                     }
                     break;
                  default:
                     break;
               }
         }
         isFirst = false;
      }
   }
}
