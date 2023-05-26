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
package org.girod.javafx.svgimage.xml.parsers;

import org.girod.javafx.svgimage.xml.specs.Viewport;
import org.girod.javafx.svgimage.xml.builders.SVGShapeBuilder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;

/**
 * This class handles the list of defined clipping paths.
 *
 * @version 1.0
 */
public class ClippingFactory implements SVGTags {
   private final Map<String, XMLNode> clipSpecs = new HashMap<>();

   public ClippingFactory() {
   }

   public void addClipSpec(String id, XMLNode node) {
      clipSpecs.put(id, node);
   }

   public boolean hasClip(String id) {
      return clipSpecs.containsKey(id);
   }

   /**
    * Creates a clip.
    *
    * @param id the clip id
    * @param node the node to clip
    * @param viewport the viewport
    * @return the clip
    */
   public Shape createClip(String id, Node node, Viewport viewport) {
      XMLNode xmlNode = clipSpecs.get(id);
      if (clipSpecs.containsKey(id)) {
         Bounds objectBoundingBox = null;
         Shape theShape = null;
         if (xmlNode.hasAttribute(CLIP_PATH_UNITS)) {
            String units = xmlNode.getAttributeValue(CLIP_PATH_UNITS);
            if (units.equals(OBJECT_BOUNDINGBOX)) {
               objectBoundingBox = node.getBoundsInLocal();
            }
         }
         Iterator<XMLNode> it = xmlNode.getChildren().iterator();
         while (it.hasNext()) {
            XMLNode childNode = it.next();
            Shape shape = null;
            String name = childNode.getName();
            switch (name) {
               case CIRCLE:
                  shape = SVGShapeBuilder.buildCircle(childNode, objectBoundingBox, null, viewport);
                  break;
               case PATH:
                  List<? extends Shape> shapes = SVGShapeBuilder.buildPath(childNode, objectBoundingBox, null, viewport, true);
                  if (shape != null) {
                     shape = shapes.get(0);
                     FillRule rule = ParserUtils.getClipRule(childNode);
                     if (rule != null) {
                        ((SVGPath) shape).setFillRule(rule);
                     }
                  }
                  break;
               case POLYLINE:
                  shape = SVGShapeBuilder.buildPolyline(xmlNode, objectBoundingBox, null, viewport);
                  break;
               case POLYGON:
                  shape = SVGShapeBuilder.buildPolygon(xmlNode, objectBoundingBox, null, viewport);
                  break;
               case ELLIPSE:
                  shape = SVGShapeBuilder.buildEllipse(childNode, objectBoundingBox, null, viewport);
                  break;
               case RECT:
                  shape = SVGShapeBuilder.buildRect(childNode, objectBoundingBox, null, viewport);
                  break;
               case LINE:
                  shape = SVGShapeBuilder.buildLine(childNode, objectBoundingBox, null, viewport);
                  break;
               case TEXT:
                  shape = SVGShapeBuilder.buildText(childNode, objectBoundingBox, null, viewport);
                  break;
            }
            if (theShape == null) {
               theShape = shape;
            } else {
               theShape = Shape.union(theShape, shape);
            }
            if (xmlNode.hasAttribute(TRANSFORM)) {
               TransformUtils.setTransforms(theShape, xmlNode, viewport);
            }
         }
         return theShape;
      } else {
         return null;
      }
   }
}
