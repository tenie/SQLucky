package net.tenie.Sqlucky.sdk.ui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.girod.javafx.svgimage.LoaderParameters;
import org.girod.javafx.svgimage.SVGImage;
import org.girod.javafx.svgimage.SVGLoader;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Path;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;

/**
 * 图标生成
 * @author tenie
 *
 */
public final class IconGenerator {
	
	
	/**
	 * LoaderParameters params = LoaderParameters.createWidthParameters(25);
      SVGImage result = SVGLoader.load(url, params);
      Image img = result.toImage();
	 * @return
	 */
	public static Image sqluckyLogoSVGImage(){
		Image svgImage = null;
		try {
				URL url = IconGenerator.class.getResource("/s.svg");
				LoaderParameters params = LoaderParameters.createWidthParameters(25);
		      SVGImage result = SVGLoader.load(url, params);
		      svgImage = result.toImage();
		      
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return svgImage;
	}
	
	public static Node sqluckyLogoSVG(){
//		SVGImage svgImage = null;
		ImageView iv = null;
		try {
//			String val = IconGenerator.class.getResource("/s.svg").toExternalForm();
			URL URL = IconGenerator.class.getResource("/s.svg");
//			File svgFile = new File("D:\\myGit\\Learning_Notes\\图标\\s.svg");
			SVGImage  svgImage = SVGLoader.load(URL);
			svgImage.scaleTo(16); 
			Image img = svgImage.toImage();
		     iv = new ImageView(img);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return iv;
	}
	
	public static SVGPath gitHubSvg() {
		SVGPath githubIcon = new SVGPath();
		githubIcon.setContent("m12 .5c-6.63 0-12 5.28-12 11.792 0 5.211 3.438 9.63 8.205 11.188.6.111.82-.254.82-.567 0-.28-.01-1.022-.015-2.005-3.338.711-4.042-1.582-4.042-1.582-.546-1.361-1.335-1.725-1.335-1.725-1.087-.731.084-.716.084-.716 1.205.082 1.838 1.215 1.838 1.215 1.07 1.803 2.809 1.282 3.495.981.108-.763.417-1.282.76-1.577-2.665-.295-5.466-1.309-5.466-5.827 0-1.287.465-2.339 1.235-3.164-.135-.298-.54-1.497.105-3.121 0 0 1.005-.316 3.3 1.209.96-.262 1.98-.392 3-.398 1.02.006 2.04.136 3 .398 2.28-1.525 3.285-1.209 3.285-1.209.645 1.624.24 2.823.12 3.121.765.825 1.23 1.877 1.23 3.164 0 4.53-2.805 5.527-5.475 5.817.42.354.81 1.077.81 2.182 0 1.578-.015 2.846-.015 3.229 0 .309.21.678.825.56 4.801-1.548 8.236-5.97 8.236-11.173 0-6.512-5.373-11.792-12-11.792z");
	    githubIcon.setFill(Color.web("#81c483"));
	    
	
	    return githubIcon;
	}
	
	public static Shape gitHubSvgShape() {
		
		SVGPath githubIcon = new SVGPath();
		githubIcon.setContent("m12 .5c-6.63 0-12 5.28-12 11.792 0 5.211 3.438 9.63 8.205 11.188.6.111.82-.254.82-.567 0-.28-.01-1.022-.015-2.005-3.338.711-4.042-1.582-4.042-1.582-.546-1.361-1.335-1.725-1.335-1.725-1.087-.731.084-.716.084-.716 1.205.082 1.838 1.215 1.838 1.215 1.07 1.803 2.809 1.282 3.495.981.108-.763.417-1.282.76-1.577-2.665-.295-5.466-1.309-5.466-5.827 0-1.287.465-2.339 1.235-3.164-.135-.298-.54-1.497.105-3.121 0 0 1.005-.316 3.3 1.209.96-.262 1.98-.392 3-.398 1.02.006 2.04.136 3 .398 2.28-1.525 3.285-1.209 3.285-1.209.645 1.624.24 2.823.12 3.121.765.825 1.23 1.877 1.23 3.164 0 4.53-2.805 5.527-5.475 5.817.42.354.81 1.077.81 2.182 0 1.578-.015 2.846-.015 3.229 0 .309.21.678.825.56 4.801-1.548 8.236-5.97 8.236-11.173 0-6.512-5.373-11.792-12-11.792z");
	    githubIcon.setFill(Color.web("#81c483"));
	    
	    SVGPath githubIcon2 = new SVGPath();
	    githubIcon2.setContent("m12 .5c-6.63 0-12 5.28-12 11.792 0 5.211 3.438 9.63 8.205 11.188.6.111.82-.254.82-.567 0-.28-.01-1.022-.015-2.005-3.338.711-4.042-1.582-4.042-1.582-.546-1.361-1.335-1.725-1.335-1.725-1.087-.731.084-.716.084-.716 1.205.082 1.838 1.215 1.838 1.215 1.07 1.803 2.809 1.282 3.495.981.108-.763.417-1.282.76-1.577-2.665-.295-5.466-1.309-5.466-5.827 0-1.287.465-2.339 1.235-3.164-.135-.298-.54-1.497.105-3.121 0 0 1.005-.316 3.3 1.209.96-.262 1.98-.392 3-.398 1.02.006 2.04.136 3 .398 2.28-1.525 3.285-1.209 3.285-1.209.645 1.624.24 2.823.12 3.121.765.825 1.23 1.877 1.23 3.164 0 4.53-2.805 5.527-5.475 5.817.42.354.81 1.077.81 2.182 0 1.578-.015 2.846-.015 3.229 0 .309.21.678.825.56 4.801-1.548 8.236-5.97 8.236-11.173 0-6.512-5.373-11.792-12-11.792z");
	    githubIcon2.setFill(Color.web("#81c483"));
	    
	    
	    Shape shape = Path.subtract(githubIcon, githubIcon2); 
	    return shape;
	}
	
//	public static   List<Region> icons = new ArrayList<>();
	// 修改颜色
	public static void changeColor(Node node, String color) {
		node.setStyle("-fx-background-color: " + color + ";");
	}

	public static Region svgImageUnactive(String name) {
		return svgImage(name, 16, "#A9A9A9");
	}
	
	public static Region svgImageUnactive(String name, int size) {
		return svgImage(name, size, "#A9A9A9");
	}
	
	public static Region svgImageDefActive(String name) {
		Region rs = svgImage(name, 16, "");   
		rs.getStyleClass().add("Icon-color");
		return rs;
	}

	public static Region svgImageDefActive(String name, double size) {
		Region rs = svgImage(name, size, "");
		rs.getStyleClass().add("Icon-color");
		return rs;
	}

	public static Region svgImage(String name, String color) {
		return svgImage(name, 16, color);
	}
	
	public static Region svgImageCss(String name,  double width, double height, String css) {
//		return svgImage(name, 16, color);
		Region rs = svgImage(name, width, height, "");
		rs.getStyleClass().add(css);
		return rs;
	}

	// svg 图片
	public static Region svgImage(String name, double size, String color ) {
		SVGPath p = new SVGPath();
		p.setContent(getSvgStr(name));

		Region svgShape = new Region();
		svgShape.setShape(p);
		svgShape.setMinSize(size, size);
		svgShape.setPrefSize(size, size);
		svgShape.setMaxSize(size, size);
		if( ! "".equals(color)) {
			svgShape.setStyle("-fx-background-color: " + color + ";");
		}
		
		return svgShape;
	}
	
	

	// svg 图片
	public static Region svgImage(String name, double width, double height, String color ) {
		SVGPath p = new SVGPath();
		p.setContent(getSvgStr(name));

		Region svgShape = new Region();
		svgShape.setShape(p);
		svgShape.setMinSize(width, height);
		svgShape.setPrefSize(width, height);
		svgShape.setMaxSize(width, height);
		if( ! "".equals(color)) {
			svgShape.setStyle("-fx-background-color: " + color + ";");
		}
		
		return svgShape;
	}

	// 修改 Label中字体颜色
	public static void changeColor(Label lb, Color color) {
		lb.setTextFill(color);
	}

	//svgObj
	public static String getSvgStr(String name) {
		String val = SvgIcon.svgObj.get(name);
		if(val == null) {
			val = "";
		}
		return val;		
	}
	
	public static void addSvgStr(String name, String val) {
		if( !SvgIcon.svgObj.containsKey(name)) {
			SvgIcon.svgObj.put(name, val);
		} 
	} 
}
