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
import org.girod.javafx.svgimage.xml.parsers.PercentParser;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.transform.Transform;
import org.girod.javafx.svgimage.xml.parsers.TransformUtils;

/**
 * Contains the specification for a radial gradient.
 *
 * @version 1.0
 */
public class RadialGradientSpec extends GradientSpec {
   private RadialGradient gradient = null;

   public RadialGradientSpec(XMLNode node) {
      super(node);
   }

   public RadialGradientSpec(XMLNode node, String href) {
      super(node, href);
   }

   public void setRadialGradient(RadialGradient gradient) {
      this.gradient = gradient;
   }

   @Override
   public void resolve(Map<String, GradientSpec> gradients, Viewport viewport) {
      if (isResolved) {
         return;
      }
      RadialGradientSpec radialSpec = null;
      if (href != null && gradients.containsKey(href)) {
         GradientSpec spec = gradients.get(href);
         if (spec instanceof RadialGradientSpec) {
            radialSpec = (RadialGradientSpec) spec;
            radialSpec.resolve(gradients, viewport);
         }
      }
      Double fx = null;
      Double fy = null;
      Double cx = null;
      Double cy = null;
      Double r = null;
      CycleMethod cycleMethod = CycleMethod.NO_CYCLE;
      isResolved = true;
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
               hasSpread = false;
               break;
            case FX:
               fx = PercentParser.parseValue(xmlNode, attrname, true);
               hasPos = true;
               break;
            case FY:
               fy = PercentParser.parseValue(xmlNode, attrname, true);
               hasPos = true;
               break;
            case CX:
               cx = PercentParser.parseValue(xmlNode, attrname, true);
               isProportional = isProportional || ParserUtils.isPercent(xmlNode, attrname);
               hasPos = true;
               break;
            case CY:
               cy = PercentParser.parseValue(xmlNode, attrname, true);
               isProportional = isProportional || ParserUtils.isPercent(xmlNode, attrname);
               hasPos = true;
               break;
            case R:
               r = PercentParser.parseValue(xmlNode, attrname, true);
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
      specStops = buildStops(this, xmlNode, RADIAL_GRADIENT);
      if (specStops.isEmpty() && radialSpec != null) {
         specStops = radialSpec.getStops();
      }
      if (transformList == null && radialSpec != null) {
         transformList = radialSpec.getTransformList();
      }
      if (!hasPos && radialSpec != null) {
         RadialGradient refGradient = radialSpec.gradient;
         fx = refGradient.getFocusAngle();
         fy = refGradient.getFocusDistance();
         cx = refGradient.getCenterX();
         cy = refGradient.getCenterY();
         r = refGradient.getRadius();
         isProportional = refGradient.isProportional();
      }
      if (!hasSpread && radialSpec != null) {
         RadialGradient refGradient = radialSpec.gradient;
         cycleMethod = refGradient.getCycleMethod();
      }

      if (cx != null && cy != null && r != null) {
         double fDistance = 0.0;
         double fAngle = 0.0;

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
               double tempCx = cx;
               double tempCy = cy;
               double tempR = r;
               cx = tempCx * concatTransform.getMxx() + tempCy * concatTransform.getMxy() + concatTransform.getTx();
               cy = tempCx * concatTransform.getMyx() + tempCy * concatTransform.getMyy() + concatTransform.getTy();

               r = Math.sqrt(tempR * concatTransform.getMxx() * tempR * concatTransform.getMxx() + tempR * concatTransform.getMyx() * tempR * concatTransform.getMyx());
               if (fx != null && fy != null) {
                  double tempFx = fx;
                  double tempFy = fy;
                  fx = tempFx * concatTransform.getMxx() + tempFy * concatTransform.getMxy() + concatTransform.getTx();
                  fy = tempFx * concatTransform.getMyx() + tempFy * concatTransform.getMyy() + concatTransform.getTy();
               } else {
                  fAngle = Math.asin(concatTransform.getMyx()) * 180.0 / Math.PI;
                  fDistance = Math.sqrt((cx - tempCx) * (cx - tempCx) + (cy - tempCy) * (cy - tempCy));
               }
            }
         }
         if (fx != null && fy != null) {
            fDistance = Math.sqrt((fx - cx) * (fx - cx) + (fy - cy) * (fy - cy)) / r;
            fAngle = Math.atan2(cy - fy, cx - fx) * 180.0 / Math.PI;
         }
         List<Stop> stops = convertStops(specStops);
         gradient = new RadialGradient(fAngle, fDistance, cx, cy, r, isProportional, cycleMethod, stops);
         isResolved = true;
      }
   }

   public RadialGradient getRadialGradient() {
      return gradient;
   }

   @Override
   public Paint getPaint() {
      return gradient;
   }

}
