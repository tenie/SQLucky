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

import org.girod.javafx.svgimage.xml.parsers.XMLNode;
import org.girod.javafx.svgimage.xml.parsers.ParserUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.transform.Transform;
import org.girod.javafx.svgimage.xml.parsers.TransformUtils;

/**
 * Contains the specification for a linear gradient.
 *
 * @version 1.0
 */
public class LinearGradientSpec extends GradientSpec {
   private LinearGradient gradient = null;

   public LinearGradientSpec(XMLNode node) {
      super(node);
   }

   public LinearGradientSpec(XMLNode node, String href) {
      super(node, href);
   }

   public void setLinearGradient(LinearGradient gradient) {
      this.gradient = gradient;
   }

   @Override
   public void resolve(Map<String, GradientSpec> gradients, Viewport viewport) {
      if (isResolved) {
         return;
      }
      LinearGradientSpec linearSpec = null;
      if (href != null && gradients.containsKey(href)) {
         GradientSpec spec = gradients.get(href);
         if (spec instanceof LinearGradientSpec) {
            linearSpec = (LinearGradientSpec) spec;
            linearSpec.resolve(gradients, viewport);
         }
      }
      double x1 = 0;
      double y1 = 0;
      double x2 = 1d;
      double y2 = 0d;
      CycleMethod cycleMethod = CycleMethod.NO_CYCLE;
      boolean hasPos = false;
      boolean hasSpread = false;
      boolean isProportional = false;

      Iterator<String> it = xmlNode.getAttributes().keySet().iterator();
      while (it.hasNext()) {
         String attrname = it.next();
         switch (attrname) {
            case GRADIENT_UNITS:
               String gradientUnits = xmlNode.getAttributeValue(attrname);
               if (!gradientUnits.equals(USERSPACE_ON_USE)) {
                  return;
               }
               break;
            case SPREAD_METHOD:
               String methodS = xmlNode.getAttributeValue(attrname);
               cycleMethod = getCycleMethod(methodS);
               hasSpread = true;
               break;
            case X1:
               x1 = getGradientPos(xmlNode, X1);
               isProportional = isProportional || ParserUtils.isPercent(xmlNode, attrname);
               hasPos = true;
               break;
            case Y1:
               y1 = getGradientPos(xmlNode, Y1);
               isProportional = isProportional || ParserUtils.isPercent(xmlNode, attrname);
               hasPos = true;
               break;
            case X2:
               x2 = getGradientPos(xmlNode, X2);
               isProportional = isProportional || ParserUtils.isPercent(xmlNode, attrname);
               hasPos = true;
               break;
            case Y2:
               y2 = getGradientPos(xmlNode, Y2);
               isProportional = isProportional || ParserUtils.isPercent(xmlNode, attrname);
               hasPos = true;
               break;
            case GRADIENT_TRANSFORM:
               transformList = TransformUtils.extractTransforms(xmlNode.getAttributeValue(attrname), viewport);
               break;
            default:
               break;
         }
      }
      specStops = buildStops(this, xmlNode, LINEAR_GRADIENT);
      if (specStops.isEmpty() && linearSpec != null) {
         specStops = linearSpec.getStops();
      }
      if (transformList == null && linearSpec != null) {
         transformList = linearSpec.getTransformList();
      }
      if (!hasPos && linearSpec != null) {
         LinearGradient refGradient = linearSpec.gradient;
         x1 = refGradient.getStartX();
         y1 = refGradient.getStartY();
         x2 = refGradient.getEndX();
         y2 = refGradient.getEndY();
         isProportional = refGradient.isProportional();
      }
      if (!hasSpread && linearSpec != null) {
         LinearGradient refGradient = linearSpec.gradient;
         cycleMethod = refGradient.getCycleMethod();
      }
      if (!(x1 == 0 && y1 == 0 && x2 == 0 && y2 == 0)) {
         if (transformList != null && !transformList.isEmpty()) {
            Transform concatTransform = null;
            Iterator<Transform> it2 = transformList.iterator();
            while (it2.hasNext()) {
               Transform theTransform = it2.next();
               if (concatTransform == null) {
                  concatTransform = theTransform;
               } else {
                  concatTransform = concatTransform.createConcatenation(theTransform);
               }
            }

            if (concatTransform != null) {
               double x1d = x1;
               double y1d = y1;
               double x2d = x2;
               double y2d = y2;
               x1 = x1d * concatTransform.getMxx() + y1d * concatTransform.getMxy() + concatTransform.getTx();
               y1 = x1d * concatTransform.getMyx() + y1d * concatTransform.getMyy() + concatTransform.getTy();
               x2 = x2d * concatTransform.getMxx() + y2d * concatTransform.getMxy() + concatTransform.getTx();
               y2 = x2d * concatTransform.getMyx() + y2d * concatTransform.getMyy() + concatTransform.getTy();
            }
         }
      }

      List<Stop> stops = convertStops(specStops);
      gradient = new LinearGradient(x1, y1, x2, y2, isProportional, cycleMethod, stops);
      isResolved = true;
   }

   public LinearGradient getLinearGradient() {
      return gradient;
   }

   @Override
   public Paint getPaint() {
      return gradient;
   }

}
