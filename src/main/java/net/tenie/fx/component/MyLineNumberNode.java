package net.tenie.fx.component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

import org.fxmisc.richtext.GenericStyledArea;
import org.reactfx.collection.LiveList;
import org.reactfx.value.Val;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import net.tenie.fx.factory.MenuFactory;

/*   @author tenie */
/**
 *  代码行号
 * @author tenie
 *
 */
public class MyLineNumberNode implements IntFunction<Node> {

	private GenericStyledArea<?, ?, ?> area;
	private static final Insets DEFAULT_INSETS = new Insets(0.0, 5.0, 0.0, 5.0);
	private static   Paint DEFAULT_TEXT_FILL = Color.web("#606366");
	private static final Font DEFAULT_FONT = Font.font("monospace", FontPosture.ITALIC, 13);
	private static   Background DEFAULT_BACKGROUND = new Background(
			new BackgroundFill(Color.web("#313335"), null, null));
	public static MyLineNumberNode get(GenericStyledArea<?, ?, ?> area, String textcolor, String backgroundcolor) {
		DEFAULT_TEXT_FILL = Color.web(textcolor);
		DEFAULT_BACKGROUND  = new Background( new BackgroundFill(Color.web(backgroundcolor ), null, null));
		return get(area, digits -> "%1$" + digits + "s");
	}
	
	public static MyLineNumberNode get(GenericStyledArea<?, ?, ?> area, String textcolor, String backgroundcolor, List<String> lines) {
		DEFAULT_TEXT_FILL = Color.web(textcolor);
		DEFAULT_BACKGROUND  = new Background( new BackgroundFill(Color.web(backgroundcolor ), null, null));
		if(lines !=null) {	
			return get(area, digits -> "%1$" + digits + "s", lines);
		}else {
			return get(area, digits -> "%1$" + digits + "s");
		}
		
	}
	
//	public static IntFunction<Node> get(GenericStyledArea<?, ?, ?> area) {
//		return get(area, digits -> "%1$" + digits + "s");
//	}

	public static  MyLineNumberNode get(GenericStyledArea<?, ?, ?> area, IntFunction<String> format) {
		return new MyLineNumberNode(area, format);
	}
	public static  MyLineNumberNode get(GenericStyledArea<?, ?, ?> area, IntFunction<String> format, List<String> lines ) {
		return new MyLineNumberNode(area, format, lines);
	}

	private final Val<Integer> nParagraphs;
	private final IntFunction<String> format;
	
	private List<String> lineNoList = new ArrayList<>();
	public List<String> getLineNoList() {
		return lineNoList;
	}
	public void setLineNoList(List<String> lineNoList) {
		this.lineNoList = lineNoList;
	}
	

	private MyLineNumberNode(GenericStyledArea<?, ?, ?> area, IntFunction<String> format , List<String> lines) {
		nParagraphs = LiveList.sizeOf(area.getParagraphs());
		this.format = format;
		this.area = area;
		this.lineNoList = lines;
	}
	
	private MyLineNumberNode(GenericStyledArea<?, ?, ?> area, IntFunction<String> format) {
		nParagraphs = LiveList.sizeOf(area.getParagraphs());
		this.format = format;
		this.area = area;
	}
	

	@Override
	public Node apply(int idx) {
		Val<String> formatted = nParagraphs.map(n -> format(idx + 1, n));

		Label lineNo = new Label();
		lineNo.setFont(DEFAULT_FONT);
		lineNo.setBackground(DEFAULT_BACKGROUND);
		lineNo.setTextFill(DEFAULT_TEXT_FILL);
		lineNo.setPadding(DEFAULT_INSETS);
		lineNo.setAlignment(Pos.TOP_RIGHT);
		lineNo.getStyleClass().add("myLineNumberlineno");
//		lineNo.setGraphic(ImageViewGenerator.svgImageDefActive("NULL"));
		
		lineNo.setContextMenu( MenuFactory.CreateLineNoMenu(lineNoList, lineNo) );
		lineNo.setOnMouseClicked( mouseEvent -> {
			//TODO 单击行号, 选中当前行
			if (mouseEvent.getClickCount() == 1) {
				this.area.selectLine();   
			}else if(mouseEvent.getClickCount() == 2) {// 双击加书签
//				lineNo.getStyleClass().add("myLineBookMark");
				
				if(lineNoList.contains(lineNo.getText())) {
					lineNo.setGraphic(ImageViewGenerator.svgImageDefActive("NULL",12));
					lineNoList.remove(lineNo.getText());
				}else {
					lineNo.setGraphic(ImageViewGenerator.svgImageDefActive("chevron-circle-right", 12));
					lineNoList.add(lineNo.getText());
				} 
			} 
		});
		

		// bind label's text to a Val that stops observing area's paragraphs
		// when lineNo is removed from scene
		lineNo.textProperty().bind(formatted.conditionOnShowing(lineNo));
		
		// 刷新的时候, 看是否要加上图标
		if(lineNoList.contains(lineNo.getText())) {
			lineNo.setGraphic(ImageViewGenerator.svgImageDefActive("chevron-circle-right",12));
		}else {
			lineNo.setGraphic(ImageViewGenerator.svgImageDefActive("NULL",12));
		}
		return lineNo;
	}

	private String format(int x, int max) {
		int digits = (int) Math.floor(Math.log10(max)) + 1;
		return String.format(format.apply(digits), x);
	}

}
