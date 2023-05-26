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

/**
 * The list of svg tags handled by the library.
 *
 * @version 1.0
 */
public interface SVGTags {
   public static String VIEWBOX = "viewBox";
   public static String FILL = "fill";
   public static String CONTEXT_FILL = "context-fill";
   public static String STROKE = "stroke";
   public static String CONTEXT_STROKE = "context-stroke";
   public static String RECT = "rect";
   public static String CIRCLE = "circle";
   public static String ELLIPSE = "ellipse";
   public static String PATH = "path";
   public static String POLYGON = "polygon";
   public static String LINE = "line";
   public static String POLYLINE = "polyline";
   public static String TEXT = "text";
   public static String IMAGE = "image";
   public static String D = "d";
   public static String SVG = "svg";
   public static String USE = "use";
   public static String MARKER = "marker";
   public static String G = "g";
   public static String SYMBOL = "symbol";
   public static String DEFS = "defs";
   public static String NONE = "none";
   public static String CLIP_PATH_SPEC = "clipPath";
   public static String CLIP_PATH = "clip-path";
   public static String LINEAR_GRADIENT = "linearGradient";
   public static String RADIAL_GRADIENT = "radialGradient";
   public static String GRADIENT_UNITS = "gradientUnits";
   public static String SPREAD_METHOD = "spreadMethod";
   public static String SPREAD_PAD = "pad";
   public static String SPREAD_REFLECT = "reflect";
   public static String SPREAD_REPEAT = "repeat";
   public static String STOP = "stop";
   public static String STOP_COLOR = "stop-color";
   public static String STOP_OPACITY = "stop-opacity";
   public static String GRADIENT_TRANSFORM = "gradientTransform";
   public static String MARKER_START = "marker-start";
   public static String MARKER_MID = "marker-mid";
   public static String MARKER_END = "marker-end";
   public static String MARKER_WIDTH = "markerWidth";
   public static String MARKER_HEIGHT = "markerHeight";
   public static String REFX = "refX";
   public static String REFY = "refY";
   public static String TSPAN = "tspan";
   public static String ID = "id";
   public static String FILL_RULE = "fill-rule";
   public static String CLIP_RULE = "clip-rule";
   public static String CLIP_PATH_UNITS = "clipPathUnits";
   public static String USERSPACE_ON_USE = "userSpaceOnUse";
   public static String OBJECT_BOUNDINGBOX = "objectBoundingBox";
   public static String NON_ZERO = "nonzero";
   public static String EVEN_ODD = "evenodd";
   public static String XLINK_HREF = "xlink:href";
   public static String HREF = "href";
   public static String ANIMATE = "animate";
   public static String ANIMATE_MOTION = "animateMotion";
   public static String ANIMATE_TRANSFORM = "animateTransform";
   public static String SET = "set";
   public static String ATTRIBUTE_NAME = "attributeName";
   public static String REPEAT_COUNT = "repeatCount";
   public static String FROM = "from";
   public static String TO = "to";
   public static String VALUES = "values";
   public static String BEGIN = "begin";
   public static String DUR = "dur";
   public static String TYPE = "type";
   public static String ADDITIVE = "additive";
   public static String TRANSLATE = "translate";
   public static String SCALE = "scale";
   public static String ROTATE = "rotate";
   public static String SKEW_X = "skewX";
   public static String SKEW_Y = "skewY";
   public static String INDEFINITE = "indefinite";
   public static String FILTER = "filter";
   public static String FE_GAUSSIAN_BLUR = "feGaussianBlur";
   public static String FE_DROP_SHADOW = "feDropShadow";
   public static String FE_FLOOD = "feFlood";
   public static String FE_IMAGE = "feImage";
   public static String FE_OFFSET = "feOffset";
   public static String FE_MORPHOLOGY = "feMorphology";
   public static String FE_COMPOSITE = "feComposite";
   public static String FE_MERGE = "feMerge";
   public static String FE_MERGE_NODE = "feMergeNode";
   public static String FE_DISTANT_LIGHT = "feDistantLight";
   public static String FE_POINT_LIGHT = "fePointLight";
   public static String FE_SPOT_LIGHT = "feSpotLight";
   public static String FE_SPECULAR_LIGHTING = "feSpecularLighting";
   public static String FE_DIFFUSE_LIGHTING = "feDiffuseLighting";
   public static String FLOOD_COLOR = "flood-color";
   public static String FLOOD_OPACITY = "flood-opacity";
   public static String STD_DEVIATION = "stdDeviation";
   public static String PRESERVE_ASPECT_RATIO = "preserveAspectRatio";
   public static String IN = "in";
   public static String IN2 = "in2";
   public static String OPERATOR = "operator";
   public static String OPERATOR_OVER = "over";
   public static String OPERATOR_IN = "in";
   public static String OPERATOR_OUT = "out";
   public static String OPERATOR_ATOP = "atop";
   public static String OPERATOR_XOR = "xor";
   public static String OPERATOR_ARITHMETIC = "arithmetic";
   public static String SURFACE_SCALE = "surfaceScale";
   public static String DIFFUSE_CONSTANT = "diffuseConstant";
   public static String SPECULAR_CONSTANT = "specularConstant";
   public static String SPECULAR_EXPONENT = "specularExponent";
   public static String LIGHTING_COLOR = "lighting-color";
   public static String AZIMUTH = "azimuth";
   public static String ELEVATION = "elevation";
   public static String SOURCE_GRAPHIC = "SourceGraphic";
   public static String SOURCE_ALPHA = "SourceAlpha";
   public static String RESULT = "result";
   public static String FX = "fx";
   public static String FY = "fy";
   public static String CX = "cx";
   public static String CY = "cy";
   public static String DX = "dx";
   public static String DY = "dy";
   public static String RX = "rx";
   public static String RY = "ry";
   public static String R = "r";
   public static String X = "x";
   public static String Y = "y";
   public static String Z = "z";
   public static String X1 = "x1";
   public static String Y1 = "y1";
   public static String X2 = "x2";
   public static String Y2 = "y2";
   public static String POINTS = "points";
   public static String RADIUS = "radius";
   public static String POINT_AT_X = "pointsAtX";
   public static String POINT_AT_Y = "pointsAtY";
   public static String POINT_AT_Z = "pointsAtZ";
   public static String DILATE = "dilate";
   public static String OFFSET = "offset";
   public static String STYLE = "style";
   public static String SQUARE = "square";
   public static String ROUND = "round";
   public static String BUTT = "butt";
   public static String BEVEL = "bevel";
   public static String MITER = "miter";
   public static String FONT_FAMILY = "font-family";
   public static String FONT_STYLE = "font-style";
   public static String FONT_SIZE = "font-size";
   public static String FONT_WEIGHT = "font-weight";
   public static String TEXT_DECORATION = "text-decoration";
   public static String TEXT_ANCHOR = "text-anchor";
   public static String START = "start";
   public static String MIDDLE = "middle";
   public static String END = "end";
   public static String NORMAL = "normal";
   public static String BOLD = "bold";
   public static String BOLDER = "bolder";
   public static String LIGHTER = "lighter";
   public static String ITALIC = "italic";
   public static String OBLIQUE = "oblique";
   public static String LINE_THROUGH = "line-through";
   public static String BASELINE_SHIFT = "baseline-shift";
   public static String BASELINE_SUB = "sub";
   public static String BASELINE_SUPER = "super";
   public static String UNDERLINE = "underline";
   public static String WIDTH = "width";
   public static String HEIGHT = "height";
   public static String TRANSFORM = "transform";
   public static String OPACITY = "opacity";
   public static String VISIBILITY = "visibility";
   public static String VISIBLE = "visible";
   public static String HIDDEN = "hidden";
   public static String FILL_OPACITY = "fill-opacity";
   public static String STROKE_WIDTH = "stroke-width";
   public static String STROKE_LINECAP = "stroke-linecap";
   public static String STROKE_MITERLIMIT = "stroke-miterlimit";
   public static String STROKE_LINEJOIN = "stroke-linejoin";
   public static String STROKE_DASHARRAY = "stroke-dasharray";
   public static String STROKE_DASHOFFSET = "stroke-dashoffset";
   public static String CLASS = "class";
}
