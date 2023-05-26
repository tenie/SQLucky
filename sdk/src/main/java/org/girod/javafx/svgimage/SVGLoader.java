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
package org.girod.javafx.svgimage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javafx.animation.Animation;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.girod.javafx.svgimage.xml.builders.AnimationBuilder;
import org.girod.javafx.svgimage.xml.specs.FilterSpec;
import org.girod.javafx.svgimage.xml.specs.GradientSpec;
import org.girod.javafx.svgimage.xml.builders.MarkerBuilder;
import org.girod.javafx.svgimage.xml.specs.MarkerContext;
import org.girod.javafx.svgimage.xml.specs.MarkerSpec;
import org.girod.javafx.svgimage.xml.parsers.ParserUtils;
import org.girod.javafx.svgimage.xml.parsers.SVGParsingException;
import org.girod.javafx.svgimage.xml.builders.SVGShapeBuilder;
import org.girod.javafx.svgimage.xml.builders.SVGStyleBuilder;
import org.girod.javafx.svgimage.xml.parsers.SVGTags;
import org.girod.javafx.svgimage.xml.specs.SpanGroup;
import org.girod.javafx.svgimage.xml.specs.SymbolSpec;
import org.girod.javafx.svgimage.xml.parsers.TransformUtils;
import org.girod.javafx.svgimage.xml.specs.Viewbox;
import org.girod.javafx.svgimage.xml.specs.Viewport;
import org.girod.javafx.svgimage.xml.parsers.XMLNode;
import org.girod.javafx.svgimage.xml.parsers.XMLRoot;
import org.girod.javafx.svgimage.xml.parsers.XMLTreeHandler;
import org.xml.sax.SAXException;

/**
 * This class allows to load a svg file and convert it to an Image or a JavaFX tree.
 *
 * @version 1.0
 */
public class SVGLoader implements SVGTags {
   private final SVGContent content;
   private final SVGImage root;
   private Viewport viewport = null;
   private final LoaderContext context;

   private SVGLoader(URL url, LoaderParameters params) {
      this.content = new SVGContent(url, params);
      this.root = new SVGImage(content);
      this.context = new LoaderContext(root, params, url);
   }

   private SVGLoader(String content, LoaderParameters params) {
      this.content = new SVGContent(content, params);
      this.root = new SVGImage(this.content);
      this.context = new LoaderContext(root, params, this.content.url);
   }

   /**
    * Load a svg File.
    *
    * @param file the file
    * @return the SVGImage
    * @throws SVGParsingException if the file cannot be converted to a URL
    */
   public static SVGImage load(File file) throws SVGParsingException {
      try {
         URL url = file.toURI().toURL();
         return load(url);
      } catch (MalformedURLException ex) {
         throw new SVGParsingException(ex);
      }
   }

   /**
    * Load a svg URL.
    *
    * @param url the URL
    * @return the SVGImage
    * @throws SVGParsingException if the SVGLoader cannot be initialized
    */
   public static SVGImage load(URL url) throws SVGParsingException {
      SVGLoader loader = new SVGLoader(url, new LoaderParameters());
      SVGImage img = loader.loadImpl();
      return img;
   }

   /**
    * Load a svg URL.
    *
    * @param content the content
    * @return the SVGImage
    * @throws SVGParsingException if the SVGLoader cannot be initialized
    */
   public static SVGImage load(String content) throws SVGParsingException {
      SVGLoader loader = new SVGLoader(content, new LoaderParameters());
      SVGImage img = loader.loadImpl();
      return img;
   }

   /**
    * Load a svg File, and set the styleSheets of the associated JavaFX Node.
    *
    * @param file the file
    * @param styleSheets the styleSheets
    * @return the SVGImage
    * @throws SVGParsingException if the file cannot be converted to a URL
    */
   public static SVGImage load(File file, String styleSheets) throws SVGParsingException {
      try {
         URL url = file.toURI().toURL();
         return load(url, styleSheets);
      } catch (MalformedURLException ex) {
         throw new SVGParsingException(ex);
      }
   }

   /**
    * Load a svg File.
    *
    * @param file the file
    * @param params the loader parameters
    * @return the SVGImage
    * @throws SVGParsingException if the file cannot be converted to a URL
    */
   public static SVGImage load(File file, LoaderParameters params) throws SVGParsingException {
      try {
         URL url = file.toURI().toURL();
         return load(url, params);
      } catch (MalformedURLException ex) {
         throw new SVGParsingException(ex);
      }
   }

   /**
    * Load a svg URL, and set the styleSheets of the associated JavaFX Node.
    *
    * @param url the URL
    * @param styleSheets the styleSheets
    * @return the SVGImage
    * @throws SVGParsingException if the file cannot be converted to a URL
    */
   public static SVGImage load(URL url, String styleSheets) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.styleSheets = styleSheets;
      return load(url, params);
   }

   /**
    * Load a svg String content, and set the styleSheets of the associated JavaFX Node.
    *
    * @param content the String content
    * @param styleSheets the styleSheets
    * @return the SVGImage
    * @throws SVGParsingException if the SVGLoader cannot be initialized
    */
   public static SVGImage load(String content, String styleSheets) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.styleSheets = styleSheets;
      return load(content, params);
   }

   /**
    * Load a svg File, and scale the associated JavaFX Node.
    *
    * @param file the file
    * @param scale the scale
    * @return the SVGImage
    * @throws SVGParsingException if the SVGLoader cannot be initialized
    */
   public static SVGImage loadScaled(File file, double scale) throws SVGParsingException {
      try {
         URL url = file.toURI().toURL();
         return loadScaled(url, scale);
      } catch (MalformedURLException ex) {
         throw new SVGParsingException(ex);
      }
   }

   /**
    * Load a svg URL, and scale the associated JavaFX Node.
    *
    * @param url the URL
    * @param scale the scale
    * @return the SVGImage
    * @throws SVGParsingException if the SVGLoader cannot be initialized
    */
   public static SVGImage loadScaled(URL url, double scale) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.scale = scale;
      return load(url, params);
   }

   /**
    * Load a svg String content, and scale the associated JavaFX Node.
    *
    * @param content the String content
    * @param scale the scale
    * @return the SVGImage
    * @throws SVGParsingException if the SVGLoader cannot be initialized
    */
   public static SVGImage loadScaled(String content, double scale) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.scale = scale;
      return load(content, params);
   }

   /**
    * Load a svg File, and scale the associated JavaFX Node.
    *
    * @param file the file
    * @param width the resulting width
    * @return the SVGImage
    * @throws SVGParsingException if the SVGLoader cannot be initialized or the file cannot be converted to a URL
    */
   public static SVGImage load(File file, double width) throws SVGParsingException {
      try {
         URL url = file.toURI().toURL();
         return load(url, width);
      } catch (MalformedURLException ex) {
         throw new SVGParsingException(ex);
      }
   }

   /**
    * Load a svg URL, and set the resulting width the associated JavaFX Node.
    *
    * @param url the URL
    * @param width the resulting width
    * @return the SVGImage
    * @throws SVGParsingException if the SVGLoader cannot be initialized
    */
   public static SVGImage load(URL url, double width) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.width = width;
      return load(url, params);
   }

   /**
    * Load a svg String content, and set the resulting width the associated JavaFX Node.
    *
    * @param content the String content
    * @param width the resulting width
    * @return the SVGImage
    * @throws SVGParsingException if the SVGLoader cannot be initialized
    */
   public static SVGImage load(String content, double width) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.width = width;
      return load(content, params);
   }

   /**
    * Load a svg File, set the styleSheets and set the resulting width of the associated JavaFX Node.
    *
    * @param file the File
    * @param width the resulting width
    * @param styleSheets the styleSheets
    * @return the SVGImage
    * @throws SVGParsingException if the SVGLoader cannot be initialized or the file cannot be converted to a URL
    */
   public static SVGImage load(File file, double width, String styleSheets) throws SVGParsingException {
      try {
         URL url = file.toURI().toURL();
         return load(url, width, styleSheets);
      } catch (MalformedURLException ex) {
         throw new SVGParsingException(ex);
      }
   }

   /**
    * Load a svg URL, set the styleSheets and set the resulting width of the associated JavaFX Node.
    *
    * @param url the URL
    * @param width the resulting width
    * @param styleSheets the styleSheets
    * @return the SVGImage
    * @throws SVGParsingException if the SVGLoader cannot be initialized
    */
   public static SVGImage load(URL url, double width, String styleSheets) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.styleSheets = styleSheets;
      params.width = width;
      return load(url, params);
   }

   /**
    * Load a svg String content, set the styleSheets and set the resulting width of the associated JavaFX Node.
    *
    * @param content the String content
    * @param width the resulting width
    * @param styleSheets the styleSheets
    * @return the SVGImage
    * @throws SVGParsingException if the SVGLoader cannot be initialized
    */
   public static SVGImage load(String content, double width, String styleSheets) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.styleSheets = styleSheets;
      params.width = width;
      return load(content, params);
   }

   /**
    * Load a svg File, set the styleSheets and scale of the associated JavaFX Node.
    *
    * @param file the File
    * @param scale the scale
    * @param styleSheets the styleSheets
    * @return the SVGImage
    * @throws SVGParsingException if the SVGLoader cannot be initialized or the file cannot be converted to a URL
    */
   public static SVGImage loadScaled(File file, double scale, String styleSheets) throws SVGParsingException {
      try {
         URL url = file.toURI().toURL();
         return loadScaled(url, scale, styleSheets);
      } catch (MalformedURLException ex) {
         throw new SVGParsingException(ex);
      }

   }

   /**
    * Load a svg URL, set the styleSheets and scale of the associated JavaFX Node.
    *
    * @param url the URL
    * @param scale the scale
    * @param styleSheets the styleSheets
    * @return the SVGImage
    * @throws SVGParsingException if the SVGLoader cannot be initialized
    */
   public static SVGImage loadScaled(URL url, double scale, String styleSheets) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.styleSheets = styleSheets;
      params.scale = scale;
      return load(url, params);
   }

   /**
    * Load a svg String content, set the styleSheets and scale of the associated JavaFX Node.
    *
    * @param content the String content
    * @param scale the scale
    * @param styleSheets the styleSheets
    * @return the SVGImage
    * @throws SVGParsingException if the SVGLoader cannot be initialized
    */
   public static SVGImage loadScaled(String content, double scale, String styleSheets) throws SVGParsingException {
      LoaderParameters params = new LoaderParameters();
      params.styleSheets = styleSheets;
      params.scale = scale;
      return load(content, params);
   }

   /**
    * Load a svg URL, and set the parameters of the associated JavaFX Node.
    *
    * @param url the URL
    * @param params the parameters
    * @return the SVGImage
    * @throws SVGParsingException if the SVGLoader cannot be initialized
    */
   public static SVGImage load(URL url, LoaderParameters params) throws SVGParsingException {
      SVGLoader loader = new SVGLoader(url, params);
      SVGImage img = loader.loadImpl();
      if (params.centerImage) {
         double theWidth = img.getLayoutBounds().getWidth();
         double theHeight = img.getLayoutBounds().getHeight();
         img.setTranslateX(-theWidth / 2);
         img.setTranslateY(-theHeight / 2);
      }
      if (params.styleSheets != null) {
         img.getStylesheets().add(params.styleSheets);
      }
      return img;
   }

   private void setViewportScaleImpl(Viewport viewport, LoaderParameters params) {
      if (params.scale > 0) {
         viewport.setScale(params.scale, params.scaleLineWidth);
      } else if (params.width > 0) {
         double initialWidth = viewport.getBestWidth();
         double scale = params.width / initialWidth;
         viewport.setScale(scale, params.scaleLineWidth);
      }
   }

   /**
    * Load a svg String content, and set the parameters of the associated JavaFX Node.
    *
    * @param content the String content
    * @param params the parameters
    * @return the SVGImage
    * @throws SVGParsingException if the SVGLoader cannot be initialized
    */
   public static SVGImage load(String content, LoaderParameters params) throws SVGParsingException {
      SVGLoader loader = new SVGLoader(content, params);
      SVGImage img = loader.loadImpl();
      if (params.centerImage) {
         double theWidth = img.getLayoutBounds().getWidth();
         double theHeight = img.getLayoutBounds().getHeight();
         img.setTranslateX(-theWidth / 2);
         img.setTranslateY(-theHeight / 2);
      }
      if (params.styleSheets != null) {
         img.getStylesheets().add(params.styleSheets);
      }
      return img;
   }

   private SVGImage loadImpl() throws SVGParsingException {
      if (Platform.isFxApplicationThread()) {
         try {
            return loadImplInJFX();
         } catch (Exception ex) {
            GlobalConfig.getInstance().handleParsingException(ex);
            return null;
         }
      } else {
         // the next instruction is only there to initialize the JavaFX platform
         new JFXPanel();
         FutureTask<SVGImage> future = new FutureTask<>(new Callable<SVGImage>() {
            @Override
            public SVGImage call() throws Exception {
               SVGImage img = loadImplInJFX();
               return img;
            }
         });
         Platform.runLater(future);
         try {
            return future.get();
         } catch (InterruptedException ex) {
            return null;
         } catch (ExecutionException ex) {
            Throwable th = ex.getCause();
            GlobalConfig.getInstance().handleParsingException(th);
            return null;
         }
      }
   }

   private SVGImage loadImplInJFX() throws IOException {
      context.effectsSupported = Platform.isSupported(ConditionalFeature.EFFECT);
      SAXParserFactory saxfactory = SAXParserFactory.newInstance();
      try {
         // see https://stackoverflow.com/questions/10257576/how-to-ignore-inline-dtd-when-parsing-xml-file-in-java
         saxfactory.setFeature("http://xml.org/sax/features/resolve-dtd-uris", false);
         saxfactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
         saxfactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
         SAXParser parser = saxfactory.newSAXParser();
         XMLTreeHandler handler = new XMLTreeHandler();
         if (content.url != null) {
            parser.parse(content.url.openStream(), handler);
         } else {
            InputStream stream = new ByteArrayInputStream(content.content.getBytes());
            parser.parse(stream, handler);
         }
         SVGImage img = walk(handler.getRoot());
         if (img != null) {
            if (!context.animations.isEmpty()) {
               img.setAnimations(context.animations);
               if (context.params.autoStartAnimations) {
                  context.playAnimations();
               }
            }
            if (context.params.applyViewportPosition) {
               Transform transform = Transform.translate(-viewport.getViewboxX(), -viewport.getViewboxY());
               img.getTransforms().add(transform);
            }
         }
         return img;
      } catch (ParserConfigurationException | SAXException ex) {
         GlobalConfig.getInstance().handleParsingException(ex);
         return null;
      }
   }

   private SVGImage walk(XMLRoot xmlRoot) {
      String name = xmlRoot.getName();
      if (name.equals(SVG)) {
         if (viewport == null) {
            viewport = ParserUtils.parseViewport(xmlRoot);
            setViewportScaleImpl(viewport, context.params);
            context.viewport = viewport;
            if (viewport != null) {
               viewport.scaleNode(root);
            }
         }
      }
      buildNode(xmlRoot, root);
      return root;
   }

   private void buildNode(XMLNode xmlNode, Group group) {
      buildNode(xmlNode, group, false);
   }

   private void addMarker(XMLNode xmlNode) {
      if (xmlNode.hasAttribute(ID)) {
         String id = xmlNode.getAttributeValue(ID);
         MarkerSpec marker = new MarkerSpec(xmlNode);
         Viewbox viewbox = ParserUtils.parseMarkerViewbox(xmlNode, viewport);
         marker.computeRefPosition(viewport);
         marker.setViewbox(viewbox);
         context.addMarker(id, marker);
      }
   }

   private void addSymbol(XMLNode xmlNode) {
      if (xmlNode.hasAttribute(ID)) {
         String id = xmlNode.getAttributeValue(ID);
         SymbolSpec symbol = new SymbolSpec(xmlNode);
         Viewbox viewbox = ParserUtils.parseViewbox(xmlNode, viewport);
         symbol.setViewbox(viewbox);
         if (xmlNode.hasAttribute(PRESERVE_ASPECT_RATIO)) {
            boolean preserve = ParserUtils.getPreserveAspectRatio(xmlNode.getAttributeValue(PRESERVE_ASPECT_RATIO));
            viewbox.setPreserveAspectRatio(preserve);
         }
         context.addSymbol(id, symbol);
      }
   }

   private void addNamedNode(XMLNode xmlNode, Node node) {
      if (node != null && xmlNode.hasAttribute(ID)) {
         String id = xmlNode.getAttributeValue(ID);
         context.addNamedNode(id, xmlNode);
      }
   }

   private List<XMLNode> lookForAnimations(XMLNode xmlNode, Node node, Viewport viewport) {
      if (node == null) {
         return new ArrayList<>();
      }
      List<XMLNode> animations = new ArrayList<>();
      Iterator<XMLNode> it = xmlNode.getChildren().iterator();
      while (it.hasNext()) {
         XMLNode childNode = it.next();
         String name = childNode.getName();
         switch (name) {
            case ANIMATE:
            case ANIMATE_MOTION:
            case ANIMATE_TRANSFORM:
            case SET:
               animations.add(childNode);
               break;
         }
      }
      return animations;
   }

   private void buildNode(XMLNode xmlNode, Group group, boolean acceptDefs) {
      if (group == null) {
         group = new Group();
      }
      List<XMLNode> animations = new ArrayList<>();
      Iterator<XMLNode> it = xmlNode.getChildren().iterator();
      while (it.hasNext()) {
         XMLNode childNode = it.next();
         List<? extends Node> nodes = null;
         SpanGroup spanGroup = null;
         String name = childNode.getName();
         switch (name) {
            case STYLE:
               manageSVGStyle(childNode);
               break;
            case RECT:
               Node node = SVGShapeBuilder.buildRect(childNode, null, null, viewport);
               addNamedNode(childNode, node);
               animations = lookForAnimations(childNode, node, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            case CIRCLE:
               node = SVGShapeBuilder.buildCircle(childNode, null, null, viewport);
               addNamedNode(childNode, node);
               animations = lookForAnimations(childNode, node, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            case ELLIPSE:
               node = SVGShapeBuilder.buildEllipse(childNode, null, null, viewport);
               addNamedNode(childNode, node);
               animations = lookForAnimations(childNode, node, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            case PATH:
               boolean hasFill = SVGStyleBuilder.hasFill(childNode);
               nodes = SVGShapeBuilder.buildPath(childNode, null, null, viewport, hasFill);
               if (nodes != null) {
                  Iterator<? extends Node> it2 = nodes.iterator();
                  while (it2.hasNext()) {
                     node = it2.next();
                     addNamedNode(childNode, node);
                     animations = lookForAnimations(childNode, node, viewport);
                  }
               }
               break;
            case POLYGON:
               node = SVGShapeBuilder.buildPolygon(childNode, null, null, viewport);
               addNamedNode(childNode, node);
               animations = lookForAnimations(childNode, node, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            case LINE:
               node = SVGShapeBuilder.buildLine(childNode, null, null, viewport);
               addNamedNode(childNode, node);
               animations = lookForAnimations(childNode, node, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            case POLYLINE:
               node = SVGShapeBuilder.buildPolyline(childNode, null, null, viewport);
               addNamedNode(childNode, node);
               animations = lookForAnimations(childNode, node, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            case USE:
               nodes = SVGShapeBuilder.buildUse(childNode, context, null, viewport);
               break;
            case TEXT:
               node = SVGShapeBuilder.buildText(childNode, null, null, viewport);
               if (node == null) {
                  spanGroup = SVGShapeBuilder.buildTSpanGroup(childNode, null, null, viewport);
                  addNamedNode(childNode, spanGroup.getTextGroup());
                  animations = lookForAnimations(childNode, spanGroup.getTextGroup(), viewport);
               } else {
                  addNamedNode(childNode, node);
                  animations = lookForAnimations(childNode, node, viewport);
               }
               nodes = ParserUtils.createNodeList(node);
               break;
            case IMAGE:
               node = SVGShapeBuilder.buildImage(childNode, content.url, null, null, viewport);
               addNamedNode(childNode, node);
               animations = lookForAnimations(childNode, node, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            case SVG:
               if (viewport == null) {
                  viewport = ParserUtils.parseViewport(childNode);
                  context.viewport = viewport;
               }
               node = buildGroup(childNode);
               nodes = ParserUtils.createNodeList(node);
               break;
            case G:
               node = buildGroup(childNode);
               addNamedNode(childNode, node);
               animations = lookForAnimations(childNode, node, viewport);
               nodes = ParserUtils.createNodeList(node);
               break;
            case SYMBOL:
               addSymbol(childNode);
               break;
            case MARKER:
               addMarker(childNode);
               break;
            case DEFS:
               if (!acceptDefs) {
                  buildDefs(childNode);
                  break;
               }
            case CLIP_PATH_SPEC:
               buildClipPath(childNode);
               break;
            case LINEAR_GRADIENT:
               if (acceptDefs) {
                  SVGShapeBuilder.buildLinearGradient(context.gradientSpecs, context.gradients, childNode, viewport);
                  break;
               }
            case RADIAL_GRADIENT:
               if (acceptDefs) {
                  SVGShapeBuilder.buildRadialGradient(context.gradientSpecs, context.gradients, childNode, viewport);
                  break;
               }
            case FILTER:
               buildFilter(childNode);
               break;
         }
         if (nodes != null) {
            Iterator<? extends Node> it2 = nodes.iterator();
            while (it2.hasNext()) {
               Node node = it2.next();
               group.getChildren().add(node);
               addStyles(group, node, childNode, false);
               if (!animations.isEmpty()) {
                  List<Animation> animationsList = AnimationBuilder.buildAnimations(childNode, node, animations, viewport);
                  if (animationsList != null) {
                     context.addAnimations(animationsList);
                  }
               }
            }
         } else if (spanGroup != null) {
            TransformUtils.setTransforms(spanGroup.getTextGroup(), childNode, viewport);
            Map<String, String> theStylesMap = ParserUtils.getStyles(childNode);
            Iterator<SpanGroup.TSpan> it2 = spanGroup.getSpans().iterator();
            SpanGroup.TSpan previous = null;
            while (it2.hasNext()) {
               SpanGroup.TSpan tspan = it2.next();
               Text tspanText = tspan.text;
               String theStyles = ParserUtils.mergeStyles(theStylesMap, tspan.node);
               tspan.node.addAttribute(STYLE, theStyles);
               addStyles(group, tspanText, tspan.node, true);
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
   }

   private void addStyles(Group parent, Node node, XMLNode xmlNode, boolean isTextSpan) {
      MarkerContext markerContext = setNodeStyle(node, xmlNode);
      boolean visible = ParserUtils.setVisibility(node, xmlNode);
      ParserUtils.setOpacity(node, xmlNode);
      setFilter(node, xmlNode);
      if (!isTextSpan) {
         TransformUtils.setTransforms(node, xmlNode, viewport);
      }
      if (markerContext != null) {
         MarkerBuilder.buildMarkers(parent, node, xmlNode, markerContext, context, viewport, visible);
      }
   }

   private void manageSVGStyle(XMLNode xmlNode) {
      if (context.svgStyle == null) {
         String cdata = xmlNode.getCDATA();
         if (cdata != null) {
            context.svgStyle = SVGStyleBuilder.parseStyle(cdata, viewport);
         }
      }
   }

   private void buildDefs(XMLNode xmlNode) {
      buildNode(xmlNode, null, true);
      if (!context.gradientSpecs.isEmpty()) {
         Map<String, GradientSpec> specs = context.gradientSpecs;
         Iterator<GradientSpec> it = specs.values().iterator();
         while (it.hasNext()) {
            GradientSpec spec = it.next();
            spec.resolve(specs, viewport);
         }
         Iterator<Entry<String, GradientSpec>> it2 = specs.entrySet().iterator();
         while (it2.hasNext()) {
            Entry<String, GradientSpec> entry = it2.next();
            GradientSpec spec = entry.getValue();
            context.gradients.put(entry.getKey(), spec.getPaint());
         }
      }
   }

   private Group buildGroup(XMLNode xmlNode) {
      Group group = new Group();
      buildNode(xmlNode, group);

      return group;
   }

   private void buildFilter(XMLNode xmlNode) {
      if (xmlNode.hasAttribute(ID)) {
         String id = xmlNode.getAttributeValue(ID);
         FilterSpec spec = new FilterSpec();
         context.filterSpecs.put(id, spec);
         buildFilterEffects(spec, xmlNode);
      }
   }

   private void buildFilterEffects(FilterSpec spec, XMLNode xmlNode) {
      Iterator<XMLNode> it = xmlNode.getChildren().iterator();
      while (it.hasNext()) {
         XMLNode childNode = it.next();
         String name = childNode.getName();
         switch (name) {
            case FE_GAUSSIAN_BLUR:
               SVGShapeBuilder.buildFEGaussianBlur(spec, childNode);
               break;
            case FE_DROP_SHADOW:
               SVGShapeBuilder.buildFEDropShadow(spec, childNode, viewport);
               break;
            case FE_FLOOD:
               SVGShapeBuilder.buildFEFlood(spec, childNode, viewport);
               break;
            case FE_IMAGE:
               SVGShapeBuilder.buildFEImage(spec, content.url, childNode, viewport);
               break;
            case FE_OFFSET:
               SVGShapeBuilder.buildFEOffset(spec, childNode, viewport);
               break;
            case FE_COMPOSITE:
               SVGShapeBuilder.buildFEComposite(spec, childNode);
            case FE_MERGE:
               SVGShapeBuilder.buildFEMerge(spec, childNode);
               break;
            case FE_SPECULAR_LIGHTING:
               SVGShapeBuilder.buildFESpecularLighting(spec, childNode, viewport);
               break;
            case FE_DIFFUSE_LIGHTING:
               SVGShapeBuilder.buildFEDiffuseLighting(spec, childNode, viewport);
               break;
         }
      }
   }

   private void buildClipPath(XMLNode xmlNode) {
      if (xmlNode.hasAttribute(ID)) {
         String id = xmlNode.getAttributeValue(ID);
         context.clippingFactory.addClipSpec(id, xmlNode);
      }
   }

   private void setFilter(Node node, XMLNode xmlNode) {
      if (context.effectsSupported && xmlNode.hasAttribute(FILTER)) {
         Effect effect = expressFilter(node, xmlNode.getAttributeValue(FILTER));
         if (effect != null) {
            node.setEffect(effect);
         }
      }
   }

   private Effect expressFilter(Node node, String value) {
      Effect effect = ParserUtils.expressFilter(context.filterSpecs, node, value);
      return effect;
   }

   private MarkerContext setNodeStyle(Node node, XMLNode xmlNode) {
      MarkerContext markerContext = SVGStyleBuilder.setNodeStyle(node, xmlNode, context, viewport);
      return markerContext;
   }
}
