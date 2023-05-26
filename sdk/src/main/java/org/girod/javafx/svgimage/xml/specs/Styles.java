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

import org.girod.javafx.svgimage.xml.builders.SVGShapeBuilder;
import org.girod.javafx.svgimage.xml.parsers.ParserUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;

/**
 * Represents a "style" node in the SVG content.
 *
 * @version 1.0
 */
public class Styles {
   public static final short FILL = 0;
   public static final short STROKE = 1;
   public static final short STROKE_WIDTH = 2;
   public static final short STROKE_DASHARRAY = 3;
   public static final short FONT_SIZE = 4;
   public static final short FONT_WEIGHT = 5;
   public static final short FONT_STYLE = 6;
   public static final short FONT_FAMILY = 7;
   public static final short TEXT_DECORATION = 8;
   public static final short OPACITY = 9;
   public static final short FILL_OPACITY = 10;
   public static final short TRANSFORM = 11;
   private final Map<String, Rule> rules = new HashMap<>();

   public Styles() {
   }

   public void addRule(Rule rule) {
      rules.put(rule.getStyleClass(), rule);
   }

   public boolean hasRule(String styleClass) {
      return rules.containsKey(styleClass);
   }

   public Rule getRule(String styleClass) {
      return rules.get(styleClass);
   }

   public static class Rule {
      private final String styleClass;
      private final Map<String, Property> properties = new HashMap<>();

      public Rule(String styleClass) {
         this.styleClass = styleClass;
      }

      public String getStyleClass() {
         return styleClass;
      }

      public void addProperty(String key, short type, Object value) {
         properties.put(key, new Property(type, value));
      }

      public Map<String, Property> getProperties() {
         return properties;
      }

      public void apply(Node node) {
         FontWeight fontWeight = FontWeight.NORMAL;
         ExtendedFontPosture fontPosture = new ExtendedFontPosture(FontPosture.REGULAR);
         double fontSize = 12d;
         String fontFamily = null;
         boolean hasFontProperties = false;

         Iterator<Property> it = properties.values().iterator();
         while (it.hasNext()) {
            Property property = it.next();
            Object value = property.value;
            switch (property.type) {
               case FILL:
                  if (node instanceof Shape) {
                     ((Shape) node).setFill((Color) value);
                  }
                  break;
               case STROKE:
                  if (node instanceof Shape) {
                     ((Shape) node).setStroke((Color) value);
                  }
                  break;
               case STROKE_WIDTH:
                  if (node instanceof Shape) {
                     ((Shape) node).setStrokeWidth((Double) value);
                  }
                  break;
               case STROKE_DASHARRAY:
                  if (node instanceof Shape) {
                     ObservableList<Double> strokeArray = ((Shape) node).getStrokeDashArray();
                     List<Double> theArray = (List<Double>) value;
                     strokeArray.addAll(theArray);
                  }
                  break;
               case FONT_FAMILY:
                  if (node instanceof Text) {
                     fontFamily = ((String) value).replace("'", "");
                     hasFontProperties = true;
                  }
                  break;
               case FONT_WEIGHT:
                  if (node instanceof Text) {
                     fontWeight = (FontWeight) value;
                     hasFontProperties = true;
                  }
                  break;
               case FONT_STYLE:
                  if (node instanceof Text) {
                     fontPosture = (ExtendedFontPosture) value;
                     hasFontProperties = true;
                  }
                  break;
               case FONT_SIZE:
                  if (node instanceof Text) {
                     fontSize = (Double) value;
                     hasFontProperties = true;
                  }
                  break;
               case TEXT_DECORATION:
                  if (node instanceof Text) {
                     SVGShapeBuilder.applyTextDecoration((Text) node, (String) value);
                  }
                  break;
               case OPACITY:
                  if (node instanceof Shape) {
                     double opacity = (Double) value;
                     ((Shape) node).setOpacity(opacity);
                  }
                  break;
               case FILL_OPACITY:
                  if (node instanceof Shape) {
                     double fillOpacity = (Double) value;
                     ParserUtils.setFillOpacity((Shape) node, fillOpacity);
                  }
                  break;
               case TRANSFORM:
                  Transform transform = (Transform) value;
                  node.getTransforms().add(transform);
                  break;
            }
            if (hasFontProperties && node instanceof Text) {
               Font font = Font.font(fontFamily, fontWeight, fontPosture.posture, fontSize);
               if (fontPosture.isOblique) {
                  SVGShapeBuilder.applyFontOblique((Text) node);
               }
               ((Text) node).setFont(font);
            }
         }
      }
   }

   public static class Property {
      public final short type;
      public final Object value;

      public Property(short type, Object value) {
         this.type = type;
         this.value = value;
      }
   }
}
