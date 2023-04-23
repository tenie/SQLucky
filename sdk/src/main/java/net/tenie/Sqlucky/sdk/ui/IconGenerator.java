package net.tenie.Sqlucky.sdk.ui;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

/**
 * 图标生成
 * @author tenie
 *
 */
public final class IconGenerator {
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
