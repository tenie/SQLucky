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
package org.girod.javafx.svgimage.xml.builders;

import java.util.ArrayList;
import org.girod.javafx.svgimage.xml.specs.MarkerContext;
import org.girod.javafx.svgimage.xml.specs.MarkerSpec;
import org.girod.javafx.svgimage.xml.parsers.XMLNode;
import org.girod.javafx.svgimage.xml.parsers.ParserUtils;
import java.util.Iterator;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.shape.VLineTo;
import org.girod.javafx.svgimage.LoaderContext;
import org.girod.javafx.svgimage.xml.parsers.SVGTags;
import org.girod.javafx.svgimage.xml.parsers.TransformUtils;
import org.girod.javafx.svgimage.xml.specs.Viewport;
import static org.girod.javafx.svgimage.xml.parsers.SVGTags.CIRCLE;
import static org.girod.javafx.svgimage.xml.parsers.SVGTags.ELLIPSE;
import static org.girod.javafx.svgimage.xml.parsers.SVGTags.LINE;
import static org.girod.javafx.svgimage.xml.parsers.SVGTags.PATH;
import static org.girod.javafx.svgimage.xml.parsers.SVGTags.POLYGON;
import static org.girod.javafx.svgimage.xml.parsers.SVGTags.POLYLINE;
import static org.girod.javafx.svgimage.xml.parsers.SVGTags.RECT;
import org.girod.javafx.svgimage.xml.specs.Viewbox;

/**
 *
 * @since 1.0
 */
public class MarkerBuilder implements SVGTags {
   private MarkerBuilder() {
   }

   public static void buildMarkers(Group parent, Node node, XMLNode xmlNode, MarkerContext markerContext, LoaderContext context, Viewport viewport, boolean visible) {
      if (parent == null || markerContext == null || markerContext.isEmpty()) {
         return;
      }
      switch (xmlNode.getName()) {
         case POLYGON:
            buildMarkers(parent, (Polygon) node, markerContext, context, viewport, visible);
            break;
         case POLYLINE:
            buildMarkers(parent, (Polyline) node, markerContext, context, viewport, visible);
            break;
         case LINE:
            buildMarkers(parent, (Line) node, markerContext, context, viewport, visible);
            break;
         case PATH:
            buildMarkers(parent, (SVGPath) node, markerContext, context, viewport, visible);
            break;
      }
   }

   private static void buildMarkers(Group parent, Polyline polyline, MarkerContext markerContext, LoaderContext context, Viewport viewport, boolean visible) {
      ObservableList<Double> points = polyline.getPoints();
      int countPoints = points.size();

      markerContext.setContextNode(polyline);
      if (markerContext.hasMarkerStart() && countPoints >= 2) {
         MarkerSpec spec = markerContext.getMarkerStart();
         Node markerNode = createMarker(parent, spec, markerContext, context, viewport);
         if (markerNode != null) {
            markerNode.setLayoutX(points.get(0) + getLength(spec, spec.getRefX(), true));
            markerNode.setLayoutY(points.get(1) + getLength(spec, spec.getRefY(), false));
         }
      }
      if (markerContext.hasMarkerEnd() && countPoints >= 4) {
         MarkerSpec spec = markerContext.getMarkerEnd();
         Node markerNode = createMarker(parent, spec, markerContext, context, viewport);
         if (markerNode != null) {
            markerNode.setLayoutX(points.get(points.size() - 2) + getLength(spec, spec.getRefX(), true));
            markerNode.setLayoutY(points.get(points.size() - 1) + getLength(spec, spec.getRefY(), false));
         }
      }
      if (markerContext.hasMarkerMid() && countPoints >= 6) {
         MarkerSpec spec = markerContext.getMarkerMid();
         for (int i = 2; i < countPoints - 2; i += 2) {
            Node markerNode = createMarker(parent, spec, markerContext, context, viewport);
            if (markerNode != null) {
               markerNode.setLayoutX(points.get(i) + getLength(spec, spec.getRefX(), true));
               markerNode.setLayoutY(points.get(i + 1) + getLength(spec, spec.getRefY(), false));
            }
         }
      }
   }

   private static void buildMarkers(Group parent, Polygon polygon, MarkerContext markerContext, LoaderContext context, Viewport viewport, boolean visible) {
      ObservableList<Double> points = polygon.getPoints();
      int countPoints = points.size();

      markerContext.setContextNode(polygon);
      if (markerContext.hasMarkerStart() && countPoints >= 2) {
         MarkerSpec spec = markerContext.getMarkerStart();
         Node markerNode = createMarker(parent, spec, markerContext, context, viewport);
         if (markerNode != null) {
            markerNode.setLayoutX(points.get(0) + getLength(spec, spec.getRefX(), true));
            markerNode.setLayoutY(points.get(1) + getLength(spec, spec.getRefY(), false));
         }
      }
      if (markerContext.hasMarkerEnd() && countPoints >= 4) {
         MarkerSpec spec = markerContext.getMarkerEnd();
         Node markerNode = createMarker(parent, spec, markerContext, context, viewport);
         if (markerNode != null) {
            markerNode.setLayoutX(points.get(points.size() - 2) + getLength(spec, spec.getRefX(), true));
            markerNode.setLayoutY(points.get(points.size() - 1) + getLength(spec, spec.getRefY(), false));
         }
      }
      if (markerContext.hasMarkerMid() && countPoints >= 6) {
         MarkerSpec spec = markerContext.getMarkerMid();
         for (int i = 2; i < countPoints - 2; i += 2) {
            Node markerNode = createMarker(parent, spec, markerContext, context, viewport);
            if (markerNode != null) {
               markerNode.setLayoutX(points.get(i) + getLength(spec, spec.getRefX(), true));
               markerNode.setLayoutY(points.get(i + 1) + getLength(spec, spec.getRefY(), false));
            }
         }
      }
   }

   private static void buildMarkers(Group parent, Line line, MarkerContext markerContext, LoaderContext context, Viewport viewport, boolean visible) {
      markerContext.setContextNode(line);
      if (markerContext.hasMarkerStart()) {
         MarkerSpec spec = markerContext.getMarkerStart();
         Node markerNode = createMarker(parent, spec, markerContext, context, viewport);
         if (markerNode != null) {
            markerNode.setLayoutX(line.getStartX() + getLength(spec, spec.getRefX(), true));
            markerNode.setLayoutY(line.getStartY() + getLength(spec, spec.getRefY(), false));
         }
      }
      if (markerContext.hasMarkerEnd()) {
         MarkerSpec spec = markerContext.getMarkerEnd();
         Node markerNode = createMarker(parent, spec, markerContext, context, viewport);
         if (markerNode != null) {
            markerNode.setLayoutX(line.getEndX() + getLength(spec, spec.getRefX(), true));
            markerNode.setLayoutY(line.getEndY() + getLength(spec, spec.getRefY(), false));
         }
      }
   }

   private static double getLength(MarkerSpec spec, double length, boolean isWidth) {
      if (!spec.hasViewbox()) {
         return length;
      } else {
         return spec.getViewbox().scaleValue(isWidth, length);
      }
   }

   private static void buildMarkers(Group parent, SVGPath svgPath, MarkerContext markerContext, LoaderContext context, Viewport viewport, boolean visible) {
      markerContext.setContextNode(svgPath);
      Path path = getPath(svgPath);
      if (markerContext.hasMarkerStart()) {
         MarkerSpec spec = markerContext.getMarkerStart();
         Node markerNode = createMarker(parent, spec, markerContext, context, viewport);
         if (markerNode != null) {
            MoveTo start = getStart(path);
            markerNode.setLayoutX(start.getX() + getLength(spec, spec.getRefX(), true));
            markerNode.setLayoutY(start.getY() + getLength(spec, spec.getRefY(), false));
         }
      }
      if (markerContext.hasMarkerEnd()) {
         MarkerSpec spec = markerContext.getMarkerEnd();
         Node markerNode = createMarker(parent, spec, markerContext, context, viewport);
         if (markerNode != null) {
            MoveTo end = getEnd(path);
            markerNode.setLayoutX(end.getX() + getLength(spec, spec.getRefX(), true));
            markerNode.setLayoutY(end.getY() + getLength(spec, spec.getRefY(), false));
         }
      }
   }

   private static MoveTo getEnd(Path path) {
      double x = 0;
      double y = 0;
      Iterator<PathElement> it = path.getElements().iterator();
      while (it.hasNext()) {
         PathElement elt = it.next();
         if (elt instanceof MoveTo) {
            MoveTo moveTo = (MoveTo) elt;
            x = moveTo.getX();
            y = moveTo.getY();
         } else if (elt instanceof LineTo) {
            LineTo lineTo = (LineTo) elt;
            x = lineTo.getX();
            y = lineTo.getY();
         } else if (elt instanceof HLineTo) {
            HLineTo lineTo = (HLineTo) elt;
            x = lineTo.getX();
         } else if (elt instanceof VLineTo) {
            VLineTo lineTo = (VLineTo) elt;
            y = lineTo.getY();
         } else if (elt instanceof ArcTo) {
            ArcTo arcTo = (ArcTo) elt;
            x = arcTo.getX();
            y = arcTo.getY();
         } else if (elt instanceof CubicCurveTo) {
            CubicCurveTo curveTo = (CubicCurveTo) elt;
            x = curveTo.getX();
            y = curveTo.getY();
         } else if (elt instanceof QuadCurveTo) {
            QuadCurveTo curveTo = (QuadCurveTo) elt;
            x = curveTo.getX();
            y = curveTo.getY();
         }
      }
      return new MoveTo(x, y);
   }

   private static MoveTo getStart(Path path) {
      Iterator<PathElement> it = path.getElements().iterator();
      while (it.hasNext()) {
         PathElement elt = it.next();
         if (elt instanceof MoveTo) {
            return (MoveTo) elt;
         } else {
            return new MoveTo(0, 0);
         }
      }
      return new MoveTo(0, 0);
   }

   private static Path getPath(SVGPath svgPath) {
      Path path = (Path) (Shape.subtract(svgPath, new Rectangle(0, 0)));
      return path;
   }

   private static Node createMarker(Group parent, MarkerSpec spec, MarkerContext markerContext, LoaderContext context, Viewport viewport) {
      XMLNode xmlNode = spec.getXMLNode();
      Group group = new Group();
      Viewbox viewbox = spec.getViewbox();
      Iterator<XMLNode> it = xmlNode.getChildren().iterator();
      while (it.hasNext()) {
         XMLNode childNode = it.next();
         List<? extends Node> nodes = null;
         String name = childNode.getName();
         switch (name) {
            case RECT:
               Node node = SVGShapeBuilder.buildRect(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            case CIRCLE:
               node = SVGShapeBuilder.buildCircle(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            case ELLIPSE:
               node = SVGShapeBuilder.buildEllipse(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            case PATH:
               boolean hasFill = SVGStyleBuilder.hasFill(childNode);
               nodes = SVGShapeBuilder.buildPath(childNode, null, viewbox, viewport, hasFill);
               break;
            case POLYGON:
               node = SVGShapeBuilder.buildPolygon(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            case LINE:
               node = SVGShapeBuilder.buildLine(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            case POLYLINE:
               node = SVGShapeBuilder.buildPolyline(childNode, null, viewbox, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
         }
         if (nodes != null) {
            Iterator<? extends Node> it2 = nodes.iterator();
            while (it2.hasNext()) {
               Node node = it2.next();
               group.getChildren().add(node);
               SVGStyleBuilder.setNodeStyle(markerContext, node, childNode, context, viewport);
               ParserUtils.setOpacity(node, xmlNode);
               TransformUtils.setTransforms(node, childNode, viewport);
            }
         }
      }
      if (!group.getChildren().isEmpty()) {
         parent.getChildren().add(group);
      } else {
         group = null;
      }
      return group;
   }

   public static MarkerContext createMarkerContext(XMLNode xmlNode, LoaderContext context) {
      if (!context.hasMarkers()) {
         return null;
      }
      switch (xmlNode.getName()) {
         case POLYGON:
         case POLYLINE:
         case LINE:
         case PATH:
            MarkerContext markerContext = getMarkerContext(xmlNode, context);
            return markerContext;
         default:
            return null;
      }
   }

   private static MarkerContext getMarkerContext(XMLNode node, LoaderContext context) {
      MarkerSpec startMarker = null;
      if (node.hasAttribute(MARKER_START)) {
         String id = getMarkerID(node, MARKER_START);
         if (context.hasMarker(id)) {
            startMarker = context.getMarker(id);
         }
      }

      MarkerSpec midMarker = null;
      if (node.hasAttribute(MARKER_MID)) {
         String id = getMarkerID(node, MARKER_MID);
         if (context.hasMarker(id)) {
            midMarker = context.getMarker(id);
         }
      }

      MarkerSpec endMarker = null;
      if (node.hasAttribute(MARKER_END)) {
         String id = getMarkerID(node, MARKER_END);
         if (context.hasMarker(id)) {
            endMarker = context.getMarker(id);
         }
      }
      MarkerContext markerContext = new MarkerContext();
      markerContext.setMarkerStart(startMarker);
      markerContext.setMarkerMid(midMarker);
      markerContext.setMarkerEnd(endMarker);
      return markerContext;
   }

   private static String getMarkerID(XMLNode node, String key) {
      String value = node.getAttributeValue(key);
      return ParserUtils.getURL(value);
   }
}
