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
package org.girod.javafx.svgimage.xml.builders;

import org.girod.javafx.svgimage.xml.specs.MarkerContext;
import org.girod.javafx.svgimage.xml.specs.GradientSpec;
import org.girod.javafx.svgimage.xml.specs.RadialGradientSpec;
import org.girod.javafx.svgimage.xml.specs.LinearGradientSpec;
import org.girod.javafx.svgimage.xml.specs.FilterSpec;
import org.girod.javafx.svgimage.xml.specs.ExtendedFontPosture;
import org.girod.javafx.svgimage.xml.parsers.XMLNode;
import org.girod.javafx.svgimage.xml.parsers.ParserUtils;
import org.girod.javafx.svgimage.xml.parsers.PathParser;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Light;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.girod.javafx.svgimage.xml.specs.FilterSpec.FEDiffuseLighting;
import org.girod.javafx.svgimage.xml.specs.FilterSpec.FESpecularLighting;
import org.girod.javafx.svgimage.LoaderContext;
import org.girod.javafx.svgimage.GlobalConfig;
import org.girod.javafx.svgimage.xml.parsers.SVGTags;
import org.girod.javafx.svgimage.xml.specs.SpanGroup;
import org.girod.javafx.svgimage.xml.parsers.TransformUtils;
import org.girod.javafx.svgimage.xml.specs.Viewbox;
import org.girod.javafx.svgimage.xml.specs.Viewport;

/**
 * The shape builder.
 *
 * @version 1.0
 */
public class SVGShapeBuilder implements SVGTags {
   private static final Pattern NUMBER = Pattern.compile("\\d+");

   private SVGShapeBuilder() {
   }

   /**
    * Build a "rect" element.
    *
    * @param xmlNode the node
    * @param bounds an optinal bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @return the shape
    */
   public static Shape buildRect(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      double x = 0.0;
      double y = 0.0;

      if (xmlNode.hasAttribute(X)) {
         x = xmlNode.getPositionValue(X, true, bounds, viewport);
      }
      if (xmlNode.hasAttribute(Y)) {
         y = xmlNode.getPositionValue(Y, false, viewport);
      }
      double width = xmlNode.getLengthValue(WIDTH, true, bounds, viewport, 0);
      double height = xmlNode.getLengthValue(HEIGHT, false, bounds, viewport, 0);
      if (viewbox != null) {
         x = viewbox.scaleValue(true, x);
         y = viewbox.scaleValue(false, y);
         width = viewbox.scaleValue(true, width);
         height = viewbox.scaleValue(false, height);
      }
      Rectangle rect = new Rectangle(x, y, width, height);
      if (xmlNode.hasAttribute(RX)) {
         double rx = 2 * xmlNode.getLengthValue(RX, true, bounds, viewport, 0);
         rect.setArcWidth(rx);
      }
      if (xmlNode.hasAttribute(RY)) {
         double ry = 2 * xmlNode.getLengthValue(RY, false, bounds, viewport, 0);
         rect.setArcHeight(ry);
      }
      if (viewbox != null) {
         viewbox.scaleNode(rect);
      }
      return rect;
   }

   /**
    * Build a "circle" element.
    *
    * @param xmlNode the node
    * @param bounds an optinal bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @return the shape
    */
   public static Shape buildCircle(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      double cx = xmlNode.getPositionValue(CX, true, bounds, viewport, 0);
      double cy = xmlNode.getPositionValue(CY, false, bounds, viewport, 0);
      double r = xmlNode.getLengthValue(R, true, bounds, viewport, 0);
      if (viewbox != null) {
         cx = viewbox.scaleValue(true, cx);
         cy = viewbox.scaleValue(false, cy);
         r = viewbox.scaleValue(true, r);
      }
      Circle circle = new Circle(cx, cy, r);
      if (viewbox != null) {
         viewbox.scaleNode(circle);
      }
      return circle;
   }

   public static void applyFontOblique(Text text) {
      String style = text.getStyle();
      String addStyle = "-fx-font-style: oblique;";
      if (style == null) {
         text.setStyle(addStyle);
      } else {
         style = style + addStyle;
         text.setStyle(style);
      }
   }

   public static void applyTextAnchor(Text text, String value) {
      switch (value) {
         case START:
            text.setTextAlignment(TextAlignment.LEFT);
            break;
         case MIDDLE: {
            text.setTextAlignment(TextAlignment.CENTER);
            text.applyCss();
            double width = text.getLayoutBounds().getWidth();
            text.setX(text.getX() - width / 2);
            break;
         }
         case END: {
            text.setTextAlignment(TextAlignment.RIGHT);
            text.applyCss();
            double width = text.getLayoutBounds().getWidth();
            text.setX(text.getX() - width);
            break;
         }
      }
   }

   public static void applyTextDecoration(Text text, String value) {
      String style = text.getStyle();
      String addStyle = null;
      StringTokenizer tok = new StringTokenizer(value, " ");
      while (tok.hasMoreTokens()) {
         String tk = tok.nextToken().trim();
         if (tk.isEmpty()) {
            continue;
         }
         if (tk.equals(UNDERLINE)) {
            if (addStyle == null) {
               addStyle = "-fx-underline: true;";
            } else {
               addStyle = addStyle + "-fx-underline: true;";
            }
         } else if (tk.equals(LINE_THROUGH)) {
            if (addStyle == null) {
               addStyle = "-fx-strikethrough: true;";
            } else {
               addStyle = addStyle + "-fx-strikethrough: true;";
            }
         }
      }
      if (addStyle == null) {
         return;
      }
      if (style == null) {
         text.setStyle(addStyle);
      } else {
         style = style + addStyle;
         text.setStyle(style);
      }
   }

   public static FontPosture applyFontPosture(Text text, String value) {
      if (value == null) {
         return FontPosture.REGULAR;
      }
      if (value.equals(OBLIQUE)) {
         String style = text.getStyle();
         String addStyle = "-fx-font-style: oblique;";
         if (style == null) {
            text.setStyle(addStyle);
         } else {
            style = style + addStyle;
            text.setStyle(style);
         }
         return FontPosture.REGULAR;
      }
      FontPosture posture = getFontPosture(value);
      return posture;
   }

   public static ExtendedFontPosture getExtendedFontPosture(String value) {
      if (value == null) {
         return new ExtendedFontPosture(FontPosture.REGULAR);
      }
      if (value.equals(ITALIC)) {
         return new ExtendedFontPosture(FontPosture.ITALIC);
      }
      if (value.equals(OBLIQUE)) {
         return new ExtendedFontPosture(true);
      }
      return new ExtendedFontPosture(FontPosture.REGULAR);
   }

   public static FontPosture getFontPosture(String value) {
      FontPosture posture = FontPosture.REGULAR;
      if (value == null) {
         return posture;
      }
      if (value.equals(ITALIC)) {
         posture = FontPosture.ITALIC;
      }
      return posture;
   }

   public static FontWeight getFontWeight(String value) {
      FontWeight weight = FontWeight.NORMAL;
      if (value == null) {
         return weight;
      }
      switch (value) {
         case BOLD:
            weight = FontWeight.BOLD;
            break;
         case BOLDER:
            weight = FontWeight.EXTRA_BOLD;
            break;
         case LIGHTER:
            weight = FontWeight.LIGHT;
            break;
         default:
            Matcher m = NUMBER.matcher(value);
            if (m.matches()) {
               int weightNumber = Integer.parseInt(value);
               weight = FontWeight.findByWeight(weightNumber);
            }
            break;
      }

      return weight;
   }

   /**
    * Build a "text" element with tspan children.
    *
    * @param xmlNode the node
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @return the Text
    */
   public static SpanGroup buildTSpanGroup(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      Group group = new Group();
      double x = xmlNode.getLengthValue(X, true, bounds, viewport, 0);
      double y = xmlNode.getLengthValue(Y, false, bounds, viewport, 0);
      if (viewbox != null) {
         x = viewbox.scaleValue(true, x);
         y = viewbox.scaleValue(false, y);
      }
      group.setLayoutX(x);
      group.setLayoutY(y);
      SpanGroup spanGroup = new SpanGroup(group);
      Iterator<XMLNode> it = xmlNode.getChildren().iterator();
      Text previous = null;
      while (it.hasNext()) {
         XMLNode childNode = it.next();
         String name = childNode.getName();
         List<Text> tspans = null;
         switch (name) {
            case TSPAN: {
               tspans = buildTspan(group, previous, childNode, bounds, viewbox, viewport);
               if (tspans.isEmpty()) {
                  tspans = null;
               } else {
                  previous = tspans.get(tspans.size() - 1);
               }
               break;
            }
         }
         if (tspans != null) {
            Iterator<Text> it2 = tspans.iterator();
            while (it2.hasNext()) {
               Text text = it2.next();
               group.getChildren().add(text);
               spanGroup.addTSpan(childNode, text);
            }
         }
      }

      return spanGroup;
   }

   /**
    * Build a "text" element.
    *
    * @param xmlNode the node
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @return the Text
    */
   public static Text buildText(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      boolean hasFamily = xmlNode.hasAttribute(FONT_FAMILY);
      boolean hasSize = xmlNode.hasAttribute(FONT_SIZE);
      String family = null;
      if (hasFamily) {
         family = xmlNode.getAttributeValue(FONT_FAMILY).replace("'", "");
      }
      double size = 12d;
      if (hasSize) {
         size = ParserUtils.parseFontSize(xmlNode.getAttributeValue(FONT_SIZE));
      }
      size = viewport.scaleLength(size);
      FontWeight weight = getFontWeight(xmlNode.getAttributeValue(FONT_WEIGHT));
      FontPosture posture = getFontPosture(xmlNode.getAttributeValue(FONT_STYLE));
      Font font = Font.font(family, weight, posture, size);

      String cdata = xmlNode.getCDATA();
      if (cdata != null) {
         double x = xmlNode.getPositionValue(X, true, bounds, viewport, 0);
         double y = xmlNode.getPositionValue(Y, false, bounds, viewport, 0);
         if (viewbox != null) {
            x = viewbox.scaleValue(true, x);
            y = viewbox.scaleValue(false, y);
         }
         Text text = new Text(x, y, cdata);
         if (xmlNode.hasAttribute(TEXT_DECORATION)) {
            SVGShapeBuilder.applyTextDecoration(text, xmlNode.getAttributeValue(TEXT_DECORATION));
         }
         if (xmlNode.hasAttribute(TEXT_ANCHOR)) {
            SVGShapeBuilder.applyTextAnchor(text, xmlNode.getAttributeValue(TEXT_ANCHOR));
         }
         if (font != null) {
            text.setFont(font);
         }
         if (viewbox != null) {
            viewbox.scaleNode(text);
         }
         return text;
      } else {
         return null;
      }
   }

   /**
    * Build a "tspan" element.
    *
    * @param group the parent group
    * @param previous the previous node
    * @param xmlNode the node
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @return the Text
    */
   public static List<Text> buildTspan(Group group, Text previous, XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      List<Text> tspans = new ArrayList<>();
      boolean hasFamily = xmlNode.hasAttribute(FONT_FAMILY);
      boolean hasSize = xmlNode.hasAttribute(FONT_SIZE);
      String family = null;
      if (hasFamily) {
         family = xmlNode.getAttributeValue(FONT_FAMILY).replace("'", "");
      }
      double size = 12d;
      if (hasSize) {
         size = ParserUtils.parseFontSize(xmlNode.getAttributeValue(FONT_SIZE));
      }
      size = viewport.scaleLength(size);
      FontWeight weight = getFontWeight(xmlNode.getAttributeValue(FONT_WEIGHT));
      FontPosture posture = getFontPosture(xmlNode.getAttributeValue(FONT_STYLE));
      Font font = Font.font(family, weight, posture, size);

      String cdata = xmlNode.getCDATA();
      if (cdata != null) {
         double x = 0;
         double y = 0;
         if (xmlNode.hasAttribute(DX)) {
            x = xmlNode.getPositionValue(DX, true, bounds, viewport, 0);
         } else if (xmlNode.hasAttribute(X)) {
            double _x = xmlNode.getPositionValue(X, true, bounds, viewport, 0);
            if (viewbox != null) {
               _x = viewbox.scaleValue(true, _x);
            }
            x = _x - group.getLayoutX();
         } else if (previous != null) {
            x = previous.getLayoutX() + previous.getLayoutBounds().getWidth();
         }
         if (xmlNode.hasAttribute(DY)) {
            y = xmlNode.getPositionValue(DY, false, bounds, viewport, 0);
         } else if (xmlNode.hasAttribute(Y)) {
            double _y = xmlNode.getPositionValue(Y, true, bounds, viewport, 0);
            if (viewbox != null) {
               _y = viewbox.scaleValue(false, _y);
            }
            y = _y - group.getLayoutY();
         }
         Text text = new Text(x, y, cdata);
         tspans.add(text);
         if (xmlNode.hasAttribute(TEXT_DECORATION)) {
            SVGShapeBuilder.applyTextDecoration(text, xmlNode.getAttributeValue(TEXT_DECORATION));
         }
         if (font != null) {
            text.setFont(font);
         }
         return tspans;
      } else {
         return null;
      }
   }

   private static List<Stop> convertStops(List<GradientSpec.StopSpec> specstops) {
      List<Stop> stops = new ArrayList<>();
      Iterator<GradientSpec.StopSpec> it = specstops.iterator();
      while (it.hasNext()) {
         GradientSpec.StopSpec theStop = it.next();
         Stop stop = new Stop(theStop.offset, theStop.color);
         stops.add(stop);
      }
      return stops;
   }

   public static void buildRadialGradient(Map<String, GradientSpec> gradientSpecs, Map<String, Paint> gradients, XMLNode xmlNode, Viewport viewport) {
      if (xmlNode.hasAttribute(ID)) {
         String id = xmlNode.getAttributeValue(ID);
         String href = null;
         if (xmlNode.hasAttribute(XLINK_HREF)) {
            href = xmlNode.getAttributeValue(XLINK_HREF);
            if (href.startsWith("#")) {
               href = href.substring(1);
            } else {
               href = null;
            }
         }
         RadialGradientSpec spec = new RadialGradientSpec(xmlNode, href);
         gradientSpecs.put(id, spec);
      }
   }

   private static double getGradientPos(XMLNode xmlNode, String id) {
      String attrvalue = xmlNode.getAttributeValue(id);
      if (attrvalue.endsWith("%") && attrvalue.length() > 1) {
         attrvalue = attrvalue.substring(0, attrvalue.length() - 1);
      }
      return ParserUtils.parseDoubleProtected(attrvalue) / 100;
   }

   public static void buildLinearGradient(Map<String, GradientSpec> gradientSpecs, Map<String, Paint> gradients, XMLNode xmlNode, Viewport viewport) {
      if (xmlNode.hasAttribute(ID)) {
         String id = xmlNode.getAttributeValue(ID);
         String href = null;
         if (xmlNode.hasAttribute(XLINK_HREF)) {
            href = xmlNode.getAttributeValue(XLINK_HREF);
            if (href.startsWith("#")) {
               href = href.substring(1);
            } else {
               href = null;
            }
         }
         LinearGradientSpec spec = new LinearGradientSpec(xmlNode, href);
         gradientSpecs.put(id, spec);
      }
   }

   private static List<Node> createNodeList(Node node) {
      List<Node> list = new ArrayList<>(1);
      list.add(node);
      return list;
   }

   /**
    * Build a "use" element.
    *
    * @param xmlNode the node
    * @param context the context
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewport the viewport
    * @return the shape
    */
   public static List<? extends Node> buildUse(XMLNode xmlNode, LoaderContext context, Bounds bounds, Viewport viewport) {
      String id = null;
      if (xmlNode.hasAttribute(HREF)) {
         id = xmlNode.getAttributeValue(HREF);
      } else if (xmlNode.hasAttribute(XLINK_HREF)) {
         id = xmlNode.getAttributeValue(XLINK_HREF);
      }
      if (id != null && id.startsWith("#")) {
         id = id.substring(1);
      }

      if (id != null && context.hasNamedNode(id)) {
         XMLNode namedNode = context.getNamedNode(id);
         List<? extends Node> nodesFromUse = null;
         Viewbox viewbox = null;
         String name = namedNode.getName();
         SpanGroup spanGroup = null;
         if (context.hasSymbol(id)) {
            viewbox = context.getSymbol(id).getViewbox();
         }
         switch (name) {
            case RECT:
               Node node = buildRect(namedNode, null, viewbox, viewport);
               nodesFromUse = ParserUtils.createNodeList(node);
               break;
            case CIRCLE:
               node = buildCircle(namedNode, null, viewbox, viewport);
               nodesFromUse = ParserUtils.createNodeList(node);
               break;
            case ELLIPSE:
               node = buildEllipse(namedNode, null, viewbox, viewport);
               nodesFromUse = ParserUtils.createNodeList(node);
               break;
            case PATH:
               boolean hasFill = SVGStyleBuilder.hasFill(namedNode);
               nodesFromUse = buildPath(namedNode, null, viewbox, viewport, hasFill);
               break;
            case POLYGON:
               node = buildPolygon(namedNode, null, viewbox, viewport);
               nodesFromUse = ParserUtils.createNodeList(node);
               break;
            case LINE:
               node = buildLine(namedNode, null, viewbox, viewport);
               nodesFromUse = ParserUtils.createNodeList(node);
               break;
            case POLYLINE:
               node = buildPolyline(namedNode, null, viewbox, viewport);
               nodesFromUse = ParserUtils.createNodeList(node);
               break;
            case IMAGE:
               node = buildImage(namedNode, context.url, null, viewbox, viewport);
               nodesFromUse = ParserUtils.createNodeList(node);
               break;
            case G:
            case SYMBOL:
               node = buildGroupForUse(context, namedNode, viewbox, viewport);
               nodesFromUse = ParserUtils.createNodeList(node);
               break;
            case TEXT:
               node = buildText(namedNode, null, viewbox, viewport);
               if (node == null) {
                  spanGroup = buildTSpanGroup(namedNode, null, viewbox, viewport);
               }
               break;
         }
         if (nodesFromUse != null) {
            Iterator<? extends Node> it2 = nodesFromUse.iterator();
            while (it2.hasNext()) {
               Node node = it2.next();
               SVGStyleBuilder.setNodeStyle(node, namedNode, context, viewport);
               if (xmlNode.hasAttribute(X)) {
                  double x = xmlNode.getPositionValue(X, true, viewport);
                  node.setLayoutX(x);
               }
               if (xmlNode.hasAttribute(Y)) {
                  double y = xmlNode.getPositionValue(Y, true, viewport);
                  node.setLayoutY(y);
               }
               SVGStyleBuilder.setNodeStyle(node, xmlNode, context, viewport);
            }
            return nodesFromUse;
         } else if (spanGroup != null) {
            Map<String, String> theStylesMap = ParserUtils.getStyles(namedNode);
            Iterator<SpanGroup.TSpan> it2 = spanGroup.getSpans().iterator();
            SpanGroup.TSpan previous = null;
            while (it2.hasNext()) {
               SpanGroup.TSpan tspan = it2.next();
               Text tspanText = tspan.text;
               String theStyles = ParserUtils.mergeStyles(theStylesMap, tspan.node);
               tspan.node.addAttribute(STYLE, theStyles);
               addStyles(context, null, tspanText, tspan.node, viewport);
               if (tspan.node.hasAttribute(BASELINE_SHIFT)) {
                  // http://www.svgbasics.com/font_effects_italic.html
                  // https://stackoverflow.com/questions/50295199/javafx-subscript-and-superscript-text-in-textflow
                  String shiftValue = tspan.node.getAttributeValue(BASELINE_SHIFT);
                  ParserUtils.setBaselineShift(tspanText, shiftValue);
               }
               // https://vanseodesign.com/web-design/svg-text-tspan-element/
               if (!ParserUtils.hasXPosition(tspan.node) && previous != null) {
                  double width = previous.text.getLayoutBounds().getWidth();
                  tspanText.setLayoutX(width + previous.text.getLayoutX());
               }
               previous = tspan;
            }
            return ParserUtils.createNodeList(spanGroup.getTextGroup());
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static Group buildGroupForUse(LoaderContext context, XMLNode xmlNode, Viewbox viewbox, Viewport viewport) {
      Group group = new Group();
      Iterator<XMLNode> it = xmlNode.getChildren().iterator();
      while (it.hasNext()) {
         XMLNode childNode = it.next();
         List<? extends Node> nodes = null;
         SpanGroup spanGroup = null;
         String name = childNode.getName();
         switch (name) {
            case RECT:
               Node node = buildRect(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            case CIRCLE:
               node = buildCircle(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            case ELLIPSE:
               node = buildEllipse(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            case PATH:
               boolean hasFill = SVGStyleBuilder.hasFill(childNode);
               nodes = buildPath(childNode, null, viewbox, viewport, hasFill);
               break;
            case POLYGON:
               node = buildPolygon(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            case LINE:
               node = buildLine(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            case POLYLINE:
               node = buildPolyline(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            case IMAGE:
               node = buildImage(childNode, context.url, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            case TEXT:
               node = buildText(childNode, null, viewbox, viewport);
               if (node == null) {
                  spanGroup = buildTSpanGroup(childNode, null, viewbox, viewport);
               }
               break;
            case G:
               node = buildGroupForUse(context, childNode, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
         }
         if (nodes != null) {
            Iterator<? extends Node> it2 = nodes.iterator();
            while (it2.hasNext()) {
               Node node = it2.next();
               addStyles(context, group, node, childNode, viewport);
               group.getChildren().add(node);
            }
         } else if (spanGroup != null) {
            Map<String, String> theStylesMap = ParserUtils.getStyles(childNode);
            Iterator<SpanGroup.TSpan> it2 = spanGroup.getSpans().iterator();
            SpanGroup.TSpan previous = null;
            while (it2.hasNext()) {
               SpanGroup.TSpan tspan = it2.next();
               Text tspanText = tspan.text;
               String theStyles = ParserUtils.mergeStyles(theStylesMap, tspan.node);
               tspan.node.addAttribute(STYLE, theStyles);
               addStyles(context, group, tspanText, tspan.node, viewport);
               if (tspan.node.hasAttribute(BASELINE_SHIFT)) {
                  // http://www.svgbasics.com/font_effects_italic.html
                  // https://stackoverflow.com/questions/50295199/javafx-subscript-and-superscript-text-in-textflow
                  String shiftValue = tspan.node.getAttributeValue(BASELINE_SHIFT);
                  ParserUtils.setBaselineShift(tspanText, shiftValue);
               }
               // https://vanseodesign.com/web-design/svg-text-tspan-element/
               if (!ParserUtils.hasXPosition(tspan.node) && previous != null) {
                  double width = previous.text.getLayoutBounds().getWidth();
                  tspanText.setLayoutX(width + previous.text.getLayoutX());
               }
               previous = tspan;
            }
            group.getChildren().add(spanGroup.getTextGroup());
         }
      }

      return group;
   }

   private static void addStyles(LoaderContext context, Group group, Node node, XMLNode xmlNode, Viewport viewport) {
      MarkerContext markerContext = SVGStyleBuilder.setNodeStyle(node, xmlNode, context, viewport);
      ParserUtils.setOpacity(node, xmlNode);
      boolean visible = ParserUtils.setVisibility(node, xmlNode);
      TransformUtils.setTransforms(node, xmlNode, viewport);
      if (markerContext != null) {
         MarkerBuilder.buildMarkers(group, node, xmlNode, markerContext, context, viewport, visible);
      }
   }

   /**
    * Build an "image" node.
    *
    * @param xmlNode the node
    * @param url the reference url
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @return the ImageView
    */
   public static ImageView buildImage(XMLNode xmlNode, URL url, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      double width = xmlNode.getLengthValue(WIDTH, true, bounds, viewport, 0);
      double height = xmlNode.getLengthValue(HEIGHT, false, bounds, viewport, 0);
      double x = xmlNode.getLengthValue(X, true, bounds, viewport, 0);
      double y = xmlNode.getLengthValue(Y, false, bounds, viewport, 0);
      String hrefAttribute = xmlNode.getAttributeValue(HREF);
      if (hrefAttribute == null) {
         hrefAttribute = xmlNode.getAttributeValue(XLINK_HREF);
      }

      if (hrefAttribute != null) {
         if (viewbox != null) {
            width = viewbox.scaleValue(true, width);
            height = viewbox.scaleValue(false, height);
            x = viewbox.scaleValue(true, x);
            y = viewbox.scaleValue(false, y);
         }
      }
      Image image = ParserUtils.getImage(url, hrefAttribute, width, height);
      if (image != null) {
         ImageView view = new ImageView(image);
         view.setX(x);
         view.setY(y);
         if (viewbox != null) {
            viewbox.scaleNode(view);
         }
         return view;
      }

      return null;
   }

   /**
    * Build an "ellipse" element.
    *
    * @param xmlNode the node
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @return the shape
    */
   public static Shape buildEllipse(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      double cx = xmlNode.getPositionValue(CX, true, bounds, viewport, 0);
      double cy = xmlNode.getPositionValue(CY, false, bounds, viewport, 0);
      double rx = xmlNode.getLengthValue(RX, true, bounds, viewport, 0);
      double ry = xmlNode.getLengthValue(RY, false, bounds, viewport, 0);
      if (viewbox != null) {
         cx = viewbox.scaleValue(true, cx);
         cy = viewbox.scaleValue(false, cy);
         rx = viewbox.scaleValue(true, rx);
         ry = viewbox.scaleValue(true, ry);
      }

      Ellipse ellipse = new Ellipse(cx, cy, rx, ry);
      if (viewbox != null) {
         viewbox.scaleNode(ellipse);
      }
      return ellipse;
   }

   /**
    * Build an "path" element.
    *
    * @param xmlNode the node
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @param hasFill true if the parsed shaped are filled
    * @return the shape
    */
   public static List<SVGPath> buildPath(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport, boolean hasFill) {
      String content = xmlNode.getAttributeValue(D);
      FillRule rule = ParserUtils.getFillRule(xmlNode);

      content = content.replace('−', '-');
      PathParser pathParser = new PathParser();
      List<SVGPath> list = pathParser.parsePathContent(content, viewport, hasFill);
      if (list != null) {
         Iterator<SVGPath> it = list.iterator();
         while (it.hasNext()) {
            SVGPath path = it.next();
            if (rule != null) {
               path.setFillRule(rule);
            }
            if (viewbox != null) {
               viewbox.scaleNode(path);
            }
         }
      }
      return list;
   }

   /**
    * Build a "polygon" element.
    *
    * @param xmlNode the node
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @return the shape
    */
   public static Polygon buildPolygon(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      String pointsAttribute = xmlNode.getAttributeValue(POINTS);
      Polygon polygon = new Polygon();

      StringTokenizer tokenizer = new StringTokenizer(pointsAttribute, " ,");
      boolean isX = true;
      while (tokenizer.hasMoreTokens()) {
         String point = tokenizer.nextToken();
         if (isX) {
            double x = ParserUtils.parsePositionValue(point, true, bounds, viewport);
            if (viewbox != null) {
               x = viewbox.scaleValue(true, x);
            }
            polygon.getPoints().add(x);
         } else {
            double y = ParserUtils.parsePositionValue(point, false, bounds, viewport);
            if (viewbox != null) {
               y = viewbox.scaleValue(false, y);
            }
            polygon.getPoints().add(y);
         }
         isX = !isX;
      }
      if (viewbox != null) {
         viewbox.scaleNode(polygon);
      }
      return polygon;
   }

   /**
    * Build a "line" element.
    *
    * @param xmlNode the node
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @return the shape
    */
   public static Line buildLine(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      double x1 = 0;
      double y1 = 0;
      double x2 = 0;
      double y2 = 0;
      if (xmlNode.hasAttribute(X1)) {
         x1 = xmlNode.getPositionValue(X1, true, bounds, viewport);
      }
      if (xmlNode.hasAttribute(Y1)) {
         y1 = xmlNode.getPositionValue(Y1, false, bounds, viewport);
      }
      if (xmlNode.hasAttribute(X2)) {
         x2 = xmlNode.getPositionValue(X2, true, bounds, viewport);
      }
      if (xmlNode.hasAttribute(Y2)) {
         y2 = xmlNode.getPositionValue(Y2, false, bounds, viewport);
      }

      if (viewbox != null) {
         x1 = viewbox.scaleValue(true, x1);
         y1 = viewbox.scaleValue(false, y1);
         x2 = viewbox.scaleValue(true, x2);
         y2 = viewbox.scaleValue(false, y2);
      }
      Line line = new Line(x1, y1, x2, y2);
      if (viewbox != null) {
         viewbox.scaleNode(line);
      }
      return line;
   }

   /**
    * Build a "polyline" element.
    *
    * @param xmlNode the node
    * @param bounds an optional bounds for an object to specify the coordinates of the object relative to it
    * @param viewbox the viewbox of the element (may be null)
    * @param viewport the viewport
    * @return the shape
    */
   public static Polyline buildPolyline(XMLNode xmlNode, Bounds bounds, Viewbox viewbox, Viewport viewport) {
      Polyline polyline = new Polyline();
      String pointsAttribute = xmlNode.getAttributeValue(POINTS);

      StringTokenizer tokenizer = new StringTokenizer(pointsAttribute, " ,");
      boolean isX = true;
      while (tokenizer.hasMoreTokens()) {
         String point = tokenizer.nextToken();
         if (isX) {
            double x = ParserUtils.parsePositionValue(point, true, bounds, viewport);
            if (viewbox != null) {
               x = viewbox.scaleValue(true, x);
            }
            polyline.getPoints().add(x);
         } else {
            double y = ParserUtils.parsePositionValue(point, false, bounds, viewport);
            if (viewbox != null) {
               y = viewbox.scaleValue(false, y);
            }
            polyline.getPoints().add(y);
         }
         isX = !isX;
      }

      return polyline;
   }

   public static void buildFEGaussianBlur(FilterSpec spec, XMLNode node) {
      double stdDeviation = 0d;

      String resultId = node.getAttributeValue(RESULT);
      if (node.hasAttribute(STD_DEVIATION)) {
         String stdDevS = node.getAttributeValue(STD_DEVIATION);
         stdDevS = ParserUtils.parseFirstArgument(stdDevS);
         stdDeviation = ParserUtils.parseDoubleProtected(stdDevS);
      }
      FilterSpec.FEGaussianBlur effect = new FilterSpec.FEGaussianBlur(resultId, stdDeviation);
      if (node.hasAttribute(IN)) {
         effect.setIn(node.getAttributeValue(IN));
      }
      spec.addEffect(resultId, effect);
   }

   public static void buildFEDropShadow(FilterSpec spec, XMLNode node, Viewport viewport) {
      double dx = node.getLengthValue(DX, true, viewport);
      double dy = node.getLengthValue(DY, true, viewport);
      double opacity = 1d;
      double stdDeviation = 0d;
      Color col = Color.BLACK;

      String resultId = node.getAttributeValue(RESULT);
      if (node.hasAttribute(FLOOD_OPACITY)) {
         opacity = node.getDoubleValue(FLOOD_OPACITY, 1d);
      }
      if (node.hasAttribute(STD_DEVIATION)) {
         String stdDevS = node.getAttributeValue(STD_DEVIATION);
         stdDevS = ParserUtils.parseFirstArgument(stdDevS);
         stdDeviation = ParserUtils.parseDoubleProtected(stdDevS);
      }
      if (node.hasAttribute(FLOOD_COLOR)) {
         String colorS = node.getAttributeValue(FLOOD_COLOR);
         col = ParserUtils.getColor(colorS, opacity);
      }
      FilterSpec.FEDropShadow effect = new FilterSpec.FEDropShadow(resultId, dx, dy, stdDeviation, col);
      if (node.hasAttribute(IN)) {
         effect.setIn(node.getAttributeValue(IN));
      }
      spec.addEffect(resultId, effect);
   }

   public static void buildFEFlood(FilterSpec spec, XMLNode node, Viewport viewport) {
      double x = node.getLengthValue(X, true, viewport);
      double y = node.getLengthValue(Y, true, viewport);
      double width = node.getLengthValue(WIDTH, true, viewport);
      double height = node.getLengthValue(HEIGHT, true, viewport);
      double opacity = 1d;
      Color col = Color.BLACK;
      String resultId = node.getAttributeValue(RESULT);

      if (node.hasAttribute(FLOOD_OPACITY)) {
         opacity = node.getDoubleValue(FLOOD_OPACITY, 1d);
      }
      if (node.hasAttribute(FLOOD_COLOR)) {
         String colorS = node.getAttributeValue(FLOOD_COLOR);
         col = ParserUtils.getColor(colorS, opacity);
      }
      FilterSpec.FEFlood effect = new FilterSpec.FEFlood(resultId, x, y, width, height, col);
      if (node.hasAttribute(IN)) {
         effect.setIn(node.getAttributeValue(IN));
      }
      spec.addEffect(resultId, effect);
   }

   public static void buildFEOffset(FilterSpec spec, XMLNode node, Viewport viewport) {
      double dx = node.getLengthValue(DX, true, viewport);
      double dy = node.getLengthValue(DY, true, viewport);
      String resultId = node.getAttributeValue(RESULT);

      FilterSpec.FEOffset effect = new FilterSpec.FEOffset(resultId, dx, dy);
      if (node.hasAttribute(IN)) {
         effect.setIn(node.getAttributeValue(IN));
      }
      spec.addEffect(resultId, effect);
   }

   public static void buildFEImage(FilterSpec spec, URL url, XMLNode node, Viewport viewport) {
      double x = node.getLengthValue(X, true, viewport);
      double y = node.getLengthValue(Y, true, viewport);
      double width = node.getLengthValue(WIDTH, true, viewport);
      double height = node.getLengthValue(HEIGHT, true, viewport);
      String hrefAttribute = node.getAttributeValue(XLINK_HREF);
      String resultId = node.getAttributeValue(RESULT);

      Image image = null;
      URL imageUrl = null;
      try {
         imageUrl = new URL(hrefAttribute);
      } catch (MalformedURLException ex) {
         try {
            imageUrl = new URL(url, hrefAttribute);
         } catch (MalformedURLException ex1) {
            GlobalConfig.getInstance().handleParsingError("URL " + hrefAttribute + " is not well formed");
         }
      }
      if (imageUrl != null) {
         image = new Image(imageUrl.toString(), width, height, true, true);
      }
      FilterSpec.FEImage effect = new FilterSpec.FEImage(resultId, x, y, image);
      spec.addEffect(resultId, effect);
   }

   public static void buildFESpecularLighting(FilterSpec spec, XMLNode node, Viewport viewport) {
      XMLNode child = node.getFirstChild();
      if (child != null) {
         switch (child.getName()) {
            case FE_DISTANT_LIGHT: {
               double surfaceScale = node.getDoubleValue(SURFACE_SCALE, 1.5d);
               double specularConstant = node.getDoubleValue(SPECULAR_CONSTANT, 0.3d);
               double specularExponent = node.getDoubleValue(SPECULAR_EXPONENT, 20d);
               Color col = null;
               if (node.hasAttribute(LIGHTING_COLOR)) {
                  String colorS = node.getAttributeValue(LIGHTING_COLOR);
                  col = ParserUtils.getColor(colorS);
               }
               double azimuth = child.getDoubleValue(AZIMUTH);
               double elevation = child.getDoubleValue(ELEVATION);
               Light.Distant light = new Light.Distant(azimuth, elevation, col);
               String resultId = node.getAttributeValue(RESULT);
               FESpecularLighting effect = new FESpecularLighting(resultId, specularConstant, specularExponent, surfaceScale, light);
               if (node.hasAttribute(IN)) {
                  effect.setIn(node.getAttributeValue(IN));
               }
               spec.addEffect(resultId, effect);
               break;
            }
            case FE_POINT_LIGHT: {
               double surfaceScale = node.getDoubleValue(SURFACE_SCALE, 1.5d);
               double specularConstant = node.getDoubleValue(SPECULAR_CONSTANT, 0.3d);
               double specularExponent = node.getDoubleValue(SPECULAR_EXPONENT, 20d);
               Color col = null;
               if (node.hasAttribute(LIGHTING_COLOR)) {
                  String colorS = node.getAttributeValue(LIGHTING_COLOR);
                  col = ParserUtils.getColor(colorS);
               }
               double x = child.getLengthValue(X, true, viewport);
               double y = child.getLengthValue(Y, true, viewport);
               double z = child.getLengthValue(Z, true, null);
               Light.Point light = new Light.Point(x, y, z, col);
               String resultId = node.getAttributeValue(RESULT);
               FESpecularLighting effect = new FESpecularLighting(resultId, specularConstant, specularExponent, surfaceScale, light);
               if (node.hasAttribute(IN)) {
                  effect.setIn(node.getAttributeValue(IN));
               }
               spec.addEffect(resultId, effect);
               break;
            }
            case FE_SPOT_LIGHT: {
               double surfaceScale = node.getDoubleValue(SURFACE_SCALE, 1.5d);
               double specularConstant = node.getDoubleValue(SPECULAR_CONSTANT, 0.3d);
               double specularExponent = node.getDoubleValue(SPECULAR_EXPONENT, 20d);
               Color col = null;
               if (node.hasAttribute(LIGHTING_COLOR)) {
                  String colorS = node.getAttributeValue(LIGHTING_COLOR);
                  col = ParserUtils.getColor(colorS);
               }
               double x = child.getLengthValue(X, true, viewport);
               double y = child.getLengthValue(Y, false, viewport);
               double z = child.getLengthValue(Z, true, viewport);
               double pointAtX = child.getLengthValue(POINT_AT_X, true, viewport);
               double pointAtY = child.getLengthValue(POINT_AT_Y, false, viewport);
               double pointAtZ = child.getLengthValue(POINT_AT_Z, true, viewport);
               Light.Spot light = new Light.Spot(x, y, z, specularExponent, col);
               light.setPointsAtX(pointAtX);
               light.setPointsAtY(pointAtY);
               light.setPointsAtZ(pointAtZ);
               String resultId = node.getAttributeValue(RESULT);
               FESpecularLighting effect = new FESpecularLighting(resultId, specularConstant, specularExponent, surfaceScale, light);
               if (node.hasAttribute(IN)) {
                  effect.setIn(node.getAttributeValue(IN));
               }
               spec.addEffect(resultId, effect);
               break;
            }
         }
      }
   }

   public static void buildFEDiffuseLighting(FilterSpec spec, XMLNode node, Viewport viewport) {
      XMLNode child = node.getFirstChild();
      if (child != null) {
         switch (child.getName()) {
            case FE_DISTANT_LIGHT: {
               double diffuseConstant = node.getDoubleValue(DIFFUSE_CONSTANT, 0.3d);
               Color col = null;
               if (node.hasAttribute(LIGHTING_COLOR)) {
                  String colorS = node.getAttributeValue(LIGHTING_COLOR);
                  col = ParserUtils.getColor(colorS);
               }
               double azimuth = child.getDoubleValue(AZIMUTH);
               double elevation = child.getDoubleValue(ELEVATION);
               Light.Distant light = new Light.Distant(azimuth, elevation, col);
               String resultId = node.getAttributeValue(RESULT);
               FEDiffuseLighting effect = new FEDiffuseLighting(resultId, diffuseConstant, light);
               if (node.hasAttribute(IN)) {
                  effect.setIn(node.getAttributeValue(IN));
               }
               spec.addEffect(resultId, effect);
               break;
            }
            case FE_POINT_LIGHT: {
               double diffuseConstant = node.getDoubleValue(DIFFUSE_CONSTANT, 0.3d);
               Color col = null;
               if (node.hasAttribute(LIGHTING_COLOR)) {
                  String colorS = node.getAttributeValue(LIGHTING_COLOR);
                  col = ParserUtils.getColor(colorS);
               }
               double x = child.getLengthValue(X, true, viewport);
               double y = child.getLengthValue(Y, true, viewport);
               double z = child.getLengthValue(Z, true, null);
               Light.Point light = new Light.Point(x, y, z, col);
               String resultId = node.getAttributeValue(RESULT);
               FEDiffuseLighting effect = new FEDiffuseLighting(resultId, diffuseConstant, light);
               if (node.hasAttribute(IN)) {
                  effect.setIn(node.getAttributeValue(IN));
               }
               spec.addEffect(resultId, effect);
               break;
            }
            case FE_SPOT_LIGHT: {
               double diffuseConstant = node.getDoubleValue(DIFFUSE_CONSTANT, 0.3d);
               double specularExponent = node.getDoubleValue(SPECULAR_EXPONENT, 20d);
               Color col = null;
               if (node.hasAttribute(LIGHTING_COLOR)) {
                  String colorS = node.getAttributeValue(LIGHTING_COLOR);
                  col = ParserUtils.getColor(colorS);
               }
               double x = child.getLengthValue(X, true, viewport);
               double y = child.getLengthValue(Y, false, viewport);
               double z = child.getLengthValue(Z, true, viewport);
               double pointAtX = child.getLengthValue(POINT_AT_X, true, viewport);
               double pointAtY = child.getLengthValue(POINT_AT_Y, false, viewport);
               double pointAtZ = child.getLengthValue(POINT_AT_Z, true, viewport);
               Light.Spot light = new Light.Spot(x, y, z, specularExponent, col);
               light.setPointsAtX(pointAtX);
               light.setPointsAtY(pointAtY);
               light.setPointsAtZ(pointAtZ);
               String resultId = node.getAttributeValue(RESULT);
               FEDiffuseLighting effect = new FEDiffuseLighting(resultId, diffuseConstant, light);
               if (node.hasAttribute(IN)) {
                  effect.setIn(node.getAttributeValue(IN));
               }
               spec.addEffect(resultId, effect);
               break;
            }
         }
      }
   }

   public static void buildFEComposite(FilterSpec spec, XMLNode node) {
      String resultId = node.getAttributeValue(RESULT);
      String in = node.getAttributeValue(IN);
      String in2 = node.getAttributeValue(IN2);
      String operator = node.getAttributeValue(OPERATOR);
      short type = FilterSpec.FEComposite.OPERATOR_OVER;
      switch (operator) {
         case OPERATOR_OVER:
            type = FilterSpec.FEComposite.OPERATOR_OVER;
            break;
         case OPERATOR_IN:
            type = FilterSpec.FEComposite.OPERATOR_IN;
            break;
         case OPERATOR_OUT:
            type = FilterSpec.FEComposite.OPERATOR_OUT;
            break;
         case OPERATOR_ATOP:
            type = FilterSpec.FEComposite.OPERATOR_ATOP;
            break;
         case OPERATOR_XOR:
            type = FilterSpec.FEComposite.OPERATOR_XOR;
            break;
         case OPERATOR_ARITHMETIC:
            type = FilterSpec.FEComposite.OPERATOR_ARITHMETIC;
            break;
      }
      FilterSpec.FEComposite effect = new FilterSpec.FEComposite(resultId, type, in, in2);
      spec.addEffect(resultId, effect);
   }

   public static void buildFEMerge(FilterSpec spec, XMLNode node) {
      String resultId = node.getAttributeValue(RESULT);
      FilterSpec.FEMerge effect = new FilterSpec.FEMerge(resultId);
      spec.addEffect(resultId, effect);

      Iterator<XMLNode> it = node.getChildren().iterator();
      while (it.hasNext()) {
         XMLNode child = it.next();
         if (child.getName().equals(FE_MERGE_NODE)) {
            String in = child.getAttributeValue(IN);
            if (in != null) {
               effect.addMergeNode(in);
            }
         }
      }
   }
}
