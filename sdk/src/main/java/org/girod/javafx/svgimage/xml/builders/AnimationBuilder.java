/*
Copyright (c) 2021, 2022, 2023 Hervé Girod
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
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.StrokeTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.WritableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import org.girod.javafx.svgimage.xml.parsers.ParserUtils;
import org.girod.javafx.svgimage.xml.parsers.PathParser;
import org.girod.javafx.svgimage.xml.parsers.SVGTags;
import org.girod.javafx.svgimage.xml.parsers.TransformUtils;
import org.girod.javafx.svgimage.xml.parsers.XMLNode;
import org.girod.javafx.svgimage.xml.specs.Viewport;

/**
 * The animation builder.
 *
 * @version 1.1
 */
public class AnimationBuilder implements SVGTags {
   private static final short TYPE_TRANSLATE = 0;
   private static final short TYPE_SCALE = 1;
   private static final short TYPE_ROTATE = 2;
   private static final short TYPE_SKEW_X = 3;
   private static final short TYPE_SKEW_Y = 4;
   private static final short ANIMATE_DEFAULT = 0;
   private static final short ANIMATE_VISIBILITY = 1;
   private static final short ANIMATE_STROKE = 2;
   private static final short ANIMATE_FILL = 3;
   private static final Pattern DURATION_PAT = Pattern.compile("(\\d+)(\\w+)");

   private AnimationBuilder() {
   }

   /**
    * Build a "rect" element.
    *
    * @param xmlNode the node
    * @param node the node
    * @param xmlAnims the animations specifications
    * @param viewport the viewport
    * @return the corresponding transitions
    */
   public static List<Animation> buildAnimations(XMLNode xmlNode, Node node, List<XMLNode> xmlAnims, Viewport viewport) {
      ParallelTransition parallel = null;
      List<Animation> transitionsList = new ArrayList<>();
      if (xmlAnims.size() > 1) {
         parallel = new ParallelTransition(node);
         transitionsList.add(parallel);
      }
      Iterator<XMLNode> it = xmlAnims.iterator();
      while (it.hasNext()) {
         XMLNode xmlAnim = it.next();
         String name = xmlAnim.getName();
         switch (name) {
            case ANIMATE:
               Animation animate = buildAnimate(xmlNode, xmlAnim, node, parallel, viewport);
               if (animate != null) {
                  transitionsList.add(animate);
               }
               break;
            case ANIMATE_MOTION:
               Animation motionTransition = buildAnimateMotion(xmlNode, xmlAnim, node, parallel, viewport);
               if (motionTransition != null) {
                  transitionsList.add(motionTransition);
               }
               break;
            case ANIMATE_TRANSFORM:
               Animation transition = buildAnimateTransform(xmlNode, xmlAnim, node, parallel, viewport);
               if (transition != null) {
                  transitionsList.add(transition);
               }
               break;
            case SET:
               break;
         }
      }
      return transitionsList;
   }

   private static Duration parseDuration(String attrValue) {
      if (attrValue.equals(INDEFINITE)) {
         return Duration.INDEFINITE;
      } else {
         Matcher m = DURATION_PAT.matcher(attrValue);
         if (m.matches()) {
            int time = Integer.parseInt(m.group(1));
            String type = m.group(2);
            if (type.equals("s")) {
               return Duration.seconds(time);
            } else if (type.equals("ms")) {
               return Duration.millis(time);
            } else if (type.equals("min")) {
               return Duration.minutes(time);
            } else {
               return Duration.ZERO;
            }
         } else {
            return Duration.ZERO;
         }
      }
   }

   private static Animation buildAnimate(XMLNode xmlNode, XMLNode xmlAnim, Node node, ParallelTransition parallel, Viewport viewport) {
      if (!xmlAnim.hasAttribute(ATTRIBUTE_NAME)) {
         return null;
      }
      WritableValue value = null;
      short animateType = ANIMATE_DEFAULT;

      String attrName = xmlAnim.getAttributeValue(ATTRIBUTE_NAME);
      String nodeName = xmlNode.getName();
      switch (nodeName) {
         case G:
            Group group = (Group) node;
            switch (attrName) {
               case X:
                  value = group.translateXProperty();
                  break;
               case Y:
                  value = group.translateYProperty();
                  break;
               case OPACITY:
                  value = group.opacityProperty();
                  break;
               case VISIBILITY:
                  value = group.opacityProperty();
                  animateType = ANIMATE_VISIBILITY;
                  break;
            }
            break;
         case RECT:
            Rectangle rect = (Rectangle) node;
            switch (attrName) {
               case X:
                  value = rect.xProperty();
                  break;
               case Y:
                  value = rect.yProperty();
                  break;
               case WIDTH:
                  value = rect.widthProperty();
                  break;
               case HEIGHT:
                  value = rect.heightProperty();
                  break;
               case OPACITY:
                  value = rect.opacityProperty();
                  break;
               case VISIBILITY:
                  value = rect.opacityProperty();
                  animateType = ANIMATE_VISIBILITY;
                  break;
               case FILL:
                  value = rect.fillProperty();
                  animateType = ANIMATE_FILL;
                  break;
               case STROKE:
                  value = rect.strokeProperty();
                  animateType = ANIMATE_STROKE;
                  break;
            }
            break;
         case IMAGE:
            ImageView imgView = (ImageView) node;
            switch (attrName) {
               case X:
                  value = imgView.xProperty();
                  break;
               case Y:
                  value = imgView.yProperty();
                  break;
               case WIDTH:
                  value = imgView.fitWidthProperty();
                  break;
               case HEIGHT:
                  value = imgView.fitHeightProperty();
                  break;
               case OPACITY:
                  value = imgView.opacityProperty();
                  break;
               case VISIBILITY:
                  value = imgView.opacityProperty();
                  animateType = ANIMATE_VISIBILITY;
                  break;
            }
            break;
         case CIRCLE:
            Circle circle = (Circle) node;
            switch (attrName) {
               case CX:
                  value = circle.centerXProperty();
                  break;
               case CY:
                  value = circle.centerYProperty();
                  break;
               case R:
                  value = circle.radiusProperty();
                  break;
               case OPACITY:
                  value = circle.opacityProperty();
                  break;
               case VISIBILITY:
                  value = circle.opacityProperty();
                  animateType = ANIMATE_VISIBILITY;
                  break;
               case FILL:
                  value = circle.fillProperty();
                  animateType = ANIMATE_FILL;
                  break;
               case STROKE:
                  value = circle.strokeProperty();
                  animateType = ANIMATE_STROKE;
                  break;
            }
            break;
         case ELLIPSE:
            Ellipse ellipse = (Ellipse) node;
            switch (attrName) {
               case CX:
                  value = ellipse.centerXProperty();
                  break;
               case CY:
                  value = ellipse.centerYProperty();
                  break;
               case RX:
                  value = ellipse.radiusXProperty();
                  break;
               case RY:
                  value = ellipse.radiusYProperty();
                  break;
               case OPACITY:
                  value = ellipse.opacityProperty();
                  break;
               case VISIBILITY:
                  value = ellipse.opacityProperty();
                  animateType = ANIMATE_VISIBILITY;
                  break;
               case FILL:
                  value = ellipse.fillProperty();
                  animateType = ANIMATE_FILL;
                  break;
               case STROKE:
                  value = ellipse.strokeProperty();
                  animateType = ANIMATE_STROKE;
                  break;
            }
            break;
         case LINE:
            Line line = (Line) node;
            switch (attrName) {
               case X1:
                  value = line.startXProperty();
                  break;
               case Y1:
                  value = line.startYProperty();
                  break;
               case X2:
                  value = line.endXProperty();
                  break;
               case Y2:
                  value = line.endYProperty();
                  break;
               case OPACITY:
                  value = line.opacityProperty();
                  break;
               case VISIBILITY:
                  value = line.opacityProperty();
                  animateType = ANIMATE_VISIBILITY;
                  break;
               case STROKE:
                  value = line.strokeProperty();
                  animateType = ANIMATE_STROKE;
                  break;
            }
            break;
         case POLYGON:
            Polygon polygon = (Polygon) node;
            switch (attrName) {
               case OPACITY:
                  value = polygon.opacityProperty();
                  break;
               case VISIBILITY:
                  value = polygon.opacityProperty();
                  animateType = ANIMATE_VISIBILITY;
                  break;
               case FILL:
                  value = polygon.fillProperty();
                  animateType = ANIMATE_FILL;
                  break;
               case STROKE:
                  value = polygon.strokeProperty();
                  animateType = ANIMATE_STROKE;
                  break;
            }
            break;
         case POLYLINE:
            Polyline polyline = (Polyline) node;
            switch (attrName) {
               case OPACITY:
                  value = polyline.opacityProperty();
                  break;
               case VISIBILITY:
                  value = polyline.opacityProperty();
                  animateType = ANIMATE_VISIBILITY;
                  break;
               case STROKE:
                  value = polyline.strokeProperty();
                  animateType = ANIMATE_STROKE;
                  break;
            }
         case PATH:
            SVGPath thePath = (SVGPath) node;
            switch (attrName) {
               case OPACITY:
                  value = thePath.opacityProperty();
                  break;
               case VISIBILITY:
                  value = thePath.opacityProperty();
                  animateType = ANIMATE_VISIBILITY;
                  break;
               case STROKE:
                  value = thePath.strokeProperty();
                  animateType = ANIMATE_STROKE;
                  break;
            }
            break;
      }
      List<Double> fromArgs = null;
      List<Double> toArgs = null;
      if (animateType == ANIMATE_DEFAULT) {
         fromArgs = getFromArguments(xmlAnim, viewport);
         toArgs = getToArguments(xmlAnim, viewport);
      }
      if (animateType == ANIMATE_DEFAULT && (fromArgs == null || toArgs == null || fromArgs.isEmpty() || toArgs.isEmpty())) {
         return null;
      }
      if (value != null) {
         Animation animation;
         Duration beginDur = Duration.ZERO;
         if (xmlAnim.hasAttribute(BEGIN)) {
            String begin = xmlAnim.getAttributeValue(BEGIN);
            beginDur = parseDuration(begin);
         }
         Duration duration = Duration.ZERO;
         if (xmlAnim.hasAttribute(DUR)) {
            duration = parseDuration(xmlAnim.getAttributeValue(DUR));
         }
         if (animateType == ANIMATE_VISIBILITY) {
            boolean fromVisible = getFromVisibilityArgument(xmlAnim);
            boolean toVisible = getToVisibilityArgument(xmlAnim);
            KeyValue fromValue = new KeyValue(value, fromVisible ? 1 : 0, Interpolator.DISCRETE);
            KeyValue toValue = new KeyValue(value, toVisible ? 1 : 0, Interpolator.DISCRETE);
            KeyFrame fromFrame = new KeyFrame(beginDur, fromValue);
            KeyFrame toFrame = new KeyFrame(duration, toValue);
            Timeline timeline = new Timeline(fromFrame, toFrame);
            animation = timeline;
         } else if (animateType == ANIMATE_STROKE) {
            Color fromColor = getFromColorArgument(xmlAnim);
            Color toColor = getToColorArgument(xmlAnim);
            if (fromColor == null || toColor == null) {
               return null;
            }
            StrokeTransition transition;
            if (parallel != null) {
               transition = new StrokeTransition(duration);
            } else {
               transition = new StrokeTransition(duration, (Shape) node);
            }
            transition.setFromValue(fromColor);
            transition.setToValue(toColor);
            animation = transition;
         } else if (animateType == ANIMATE_FILL) {
            Color fromColor = getFromColorArgument(xmlAnim);
            Color toColor = getToColorArgument(xmlAnim);
            if (fromColor == null || toColor == null) {
               return null;
            }
            FillTransition transition;
            if (parallel != null) {
               transition = new FillTransition(duration);
            } else {
               transition = new FillTransition(duration, (Shape) node);
            }
            transition.setFromValue(fromColor);
            transition.setToValue(toColor);
            animation = transition;
         } else {
            Timeline timeline = new Timeline();
            animation = timeline;
            KeyValue fromValue = new KeyValue(value, fromArgs.get(0));
            KeyValue toValue = new KeyValue(value, toArgs.get(0));
            KeyFrame fromFrame = new KeyFrame(beginDur, fromValue);
            KeyFrame toFrame = new KeyFrame(duration, toValue);
            timeline.getKeyFrames().addAll(fromFrame, toFrame);
         }
         if (xmlAnim.hasAttribute(REPEAT_COUNT)) {
            String repeatValue = xmlAnim.getAttributeValue(REPEAT_COUNT);
            if (repeatValue.equals(INDEFINITE)) {
               animation.setCycleCount(Transition.INDEFINITE);
            } else {
               int count = ParserUtils.parseIntProtected(repeatValue);
               animation.setCycleCount(count);
            }
         }
         if (parallel != null) {
            parallel.getChildren().add(animation);
            return null;
         } else {
            return animation;
         }
      } else {
         return null;
      }
   }

   private static Transition buildAnimateMotion(XMLNode xmlNode, XMLNode xmlAnim, Node node, ParallelTransition parallel, Viewport viewport) {
      if (!xmlAnim.hasAttribute(PATH)) {
         return null;
      }
      String content = xmlAnim.getAttributeValue(PATH);
      content = content.replace('−', '-');
      PathParser pathParser = new PathParser();
      List<SVGPath> list = pathParser.parsePathContent(content, viewport, false);
      Duration duration = Duration.ZERO;
      if (xmlAnim.hasAttribute(DUR)) {
         duration = parseDuration(xmlAnim.getAttributeValue(DUR));
      }
      SVGPath path = list.get(0);
      PathTransition transition;
      if (parallel != null) {
         transition = new PathTransition(duration, path);
      } else {
         transition = new PathTransition(duration, path, node);
      }
      transition.setDuration(duration);
      if (xmlAnim.hasAttribute(REPEAT_COUNT)) {
         String repeatValue = xmlAnim.getAttributeValue(REPEAT_COUNT);
         if (repeatValue.equals(INDEFINITE)) {
            transition.setCycleCount(Transition.INDEFINITE);
         } else {
            int count = ParserUtils.parseIntProtected(repeatValue);
            transition.setCycleCount(count);
         }
      }
      if (xmlAnim.hasAttribute(BEGIN)) {
         String begin = xmlAnim.getAttributeValue(BEGIN);
         Duration beginDur = parseDuration(begin);
         if (beginDur != null) {
            transition.setDelay(beginDur);
         }
      }
      return transition;
   }

   private static Transition buildAnimateTransform(XMLNode xmlNode, XMLNode xmlAnim, Node node, ParallelTransition parallel, Viewport viewport) {
      List<Double> fromArgs = getFromArguments(xmlAnim, viewport);
      List<Double> toArgs = getToArguments(xmlAnim, viewport);
      if (fromArgs == null || toArgs == null) {
         return null;
      }
      short type = TYPE_TRANSLATE;
      if (xmlAnim.hasAttribute(TYPE)) {
         switch (xmlAnim.getAttributeValue(TYPE)) {
            case SCALE:
               type = TYPE_SCALE;
               break;
            case ROTATE:
               type = TYPE_ROTATE;
               break;
            case SKEW_X:
               type = TYPE_SKEW_X;
               break;
            case SKEW_Y:
               type = TYPE_SKEW_Y;
               break;
         }
      }
      Duration duration = Duration.ZERO;
      if (xmlAnim.hasAttribute(DUR)) {
         duration = parseDuration(xmlAnim.getAttributeValue(DUR));
      }
      Transition transition = null;
      switch (type) {
         case TYPE_ROTATE:
            RotateTransition rotate;
            if (parallel != null) {
               rotate = new RotateTransition(duration);
            } else {
               rotate = new RotateTransition(duration, node);
            }
            rotate.setDuration(duration);
            transition = rotate;
            if (fromArgs.size() == 1) {
               rotate.setFromAngle(fromArgs.get(0));
            } else if (fromArgs.size() == 3) {
               rotate.setFromAngle(fromArgs.get(0));
            }
            if (toArgs.size() >= 1) {
               rotate.setToAngle(toArgs.get(0));
            }
            break;
         case TYPE_TRANSLATE:
            TranslateTransition translate;
            if (parallel != null) {
               translate = new TranslateTransition(duration);
            } else {
               translate = new TranslateTransition(duration, node);
            }
            translate.setDuration(duration);
            transition = translate;
            if (fromArgs.size() == 2) {
               translate.setFromX(fromArgs.get(0));
               translate.setFromY(fromArgs.get(1));
            } else if (fromArgs.size() == 1) {
               translate.setFromX(fromArgs.get(0));
            }
            if (toArgs.size() == 2) {
               translate.setToX(toArgs.get(0));
               translate.setToY(toArgs.get(1));
            } else if (toArgs.size() == 1) {
               translate.setToX(toArgs.get(0));
            }
            break;
         case TYPE_SCALE:
            ScaleTransition scale;
            if (parallel != null) {
               scale = new ScaleTransition(duration);
            } else {
               scale = new ScaleTransition(duration, node);
            }
            scale.setDuration(duration);
            transition = scale;
            if (fromArgs.size() == 1) {
               scale.setFromX(fromArgs.get(0));
               scale.setFromY(fromArgs.get(0));
            } else if (fromArgs.size() == 2) {
               scale.setFromX(fromArgs.get(0));
               scale.setFromY(fromArgs.get(1));
            }
            if (toArgs.size() == 1) {
               scale.setToX(toArgs.get(0));
               scale.setToY(toArgs.get(0));
            } else if (toArgs.size() == 2) {
               scale.setToX(toArgs.get(0));
               scale.setToY(toArgs.get(1));
            }
            break;
      }
      if (transition != null) {
         if (xmlAnim.hasAttribute(REPEAT_COUNT)) {
            String repeatValue = xmlAnim.getAttributeValue(REPEAT_COUNT);
            if (repeatValue.equals(INDEFINITE)) {
               transition.setCycleCount(Transition.INDEFINITE);
            } else {
               int count = ParserUtils.parseIntProtected(repeatValue);
               transition.setCycleCount(count);
            }
         }
         if (xmlAnim.hasAttribute(BEGIN)) {
            String begin = xmlAnim.getAttributeValue(BEGIN);
            Duration beginDur = parseDuration(begin);
            if (beginDur != null) {
               transition.setDelay(beginDur);
            }
         }
         if (parallel != null) {
            parallel.getChildren().add(transition);
            return null;
         } else {
            return transition;
         }
      } else {
         return null;
      }
   }

   private static Color getFromColorArgument(XMLNode xmlAnim) {
      if (xmlAnim.hasAttribute(FROM)) {
         String content = xmlAnim.getAttributeValue(FROM);
         return ParserUtils.getColor(content);
      } else {
         return null;
      }
   }

   private static Color getToColorArgument(XMLNode xmlAnim) {
      if (xmlAnim.hasAttribute(TO)) {
         String content = xmlAnim.getAttributeValue(TO);
         return ParserUtils.getColor(content);
      } else {
         return null;
      }
   }

   private static boolean getFromVisibilityArgument(XMLNode xmlAnim) {
      if (xmlAnim.hasAttribute(FROM)) {
         String content = xmlAnim.getAttributeValue(FROM);
         return ParserUtils.parseVisibility(content);
      } else {
         return true;
      }
   }

   private static boolean getToVisibilityArgument(XMLNode xmlAnim) {
      if (xmlAnim.hasAttribute(TO)) {
         String content = xmlAnim.getAttributeValue(TO);
         return ParserUtils.parseVisibility(content);
      } else {
         return true;
      }
   }

   private static List<Double> getFromArguments(XMLNode xmlAnim, Viewport viewport) {
      if (xmlAnim.hasAttribute(FROM)) {
         String content = xmlAnim.getAttributeValue(FROM);
         return TransformUtils.getTransformArgumentsForAnimation(content, viewport);
      } else {
         return null;
      }
   }

   private static List<Double> getToArguments(XMLNode xmlAnim, Viewport viewport) {
      if (xmlAnim.hasAttribute(TO)) {
         String content = xmlAnim.getAttributeValue(TO);
         return TransformUtils.getTransformArgumentsForAnimation(content, viewport);
      } else {
         return null;
      }
   }
}
