package net.tenie.Sqlucky.sdk.component.CodeArea;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.IntFunction;
import org.fxmisc.richtext.CodeArea;
import org.reactfx.collection.LiveList;
import org.reactfx.value.Val;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import net.tenie.Sqlucky.sdk.component.ImageViewGenerator;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/**
 *  代码行号
 * @author tenie
 *
 */
public class MyLineNumberNode implements IntFunction<Node> {

	private MyCodeArea  area;
	private static final Insets DEFAULT_INSETS = new Insets(0.0, 5.0, 0.0, 5.0);
	private static   Paint DEFAULT_TEXT_FILL = Color.web("#606366");
	private static final Font DEFAULT_FONT = Font.font("monospace", FontPosture.ITALIC, 13);
	private static   Background DEFAULT_BACKGROUND = new Background(
			new BackgroundFill(Color.web("#313335"), null, null));
	public static MyLineNumberNode get(CodeArea  area, String textcolor, String backgroundcolor) {
		DEFAULT_TEXT_FILL = Color.web(textcolor);
		DEFAULT_BACKGROUND  = new Background( new BackgroundFill(Color.web(backgroundcolor ), null, null));
		return get(area, digits -> "%1$" + digits + "s");
	}
	
	public static MyLineNumberNode get(CodeArea  area, String textcolor, String backgroundcolor, List<String> lines) {
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

	public static  MyLineNumberNode get(CodeArea area, IntFunction<String> format) {
		return new MyLineNumberNode(area, format);
	}
	public static  MyLineNumberNode get(CodeArea area, IntFunction<String> format, List<String> lines ) {
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
	

	private MyLineNumberNode(CodeArea area, IntFunction<String> format , List<String> lines) {
		nParagraphs = LiveList.sizeOf(area.getParagraphs());
		this.format = format;
		this.area = (MyCodeArea) area;
		this.lineNoList = lines;
	}
	
	private MyLineNumberNode(CodeArea area, IntFunction<String> format) {
		nParagraphs = LiveList.sizeOf(area.getParagraphs());
		this.format = format;
		this.area = (MyCodeArea) area;
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
		
		lineNo.setContextMenu(  CreateLineNoMenu(lineNoList, lineNo) );
		lineNo.setOnMouseClicked( mouseEvent -> {
			//TODO 单击行号, 选中当前行
			if (mouseEvent.getClickCount() == 1) {
				this.area.selectLine();  
//				Platform.runLater(() -> {
//					
//					IndexRange ir = this.area.getSelection();
//					int start = ir.getStart();
//					int end   = ir.getEnd() + 1;
//					this.area.selectRange(start, end);
//					
//				});
				
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
	//行号 右键菜单
	public   ContextMenu CreateLineNoMenu(List<String> lineNoList , Label lineNo) {
			ContextMenu contextMenu = new ContextMenu();

			MenuItem add = new MenuItem("Add/Remove Bookmark");
			add.setOnAction(e -> { 
				if(lineNoList.contains(lineNo.getText())) {
					lineNo.setGraphic(ImageViewGenerator.svgImageDefActive("NULL",12));
					lineNoList.remove(lineNo.getText());
				}else {
					lineNo.setGraphic(ImageViewGenerator.svgImageDefActive("chevron-circle-right", 12));
					lineNoList.add(lineNo.getText());
				} 
				
			});
			add.setGraphic(ImageViewGenerator.svgImageDefActive("chevron-circle-right"));

			MenuItem next = new MenuItem("Next");
			next.setOnAction(e->{
				nextBookmark(true);
			});
			next.setGraphic(ImageViewGenerator.svgImageDefActive("chevron-circle-down"));

			MenuItem previous = new MenuItem("Previous");
			previous.setOnAction(e->{
				nextBookmark(false);
			});
			previous.setGraphic(ImageViewGenerator.svgImageDefActive("chevron-circle-up"));

			 

			contextMenu.getItems().addAll(add, next, previous);

			return contextMenu;
		}

	/**
	 * bookmark next
	 * @param isNext true: 从上往下找
	 */
	public   void nextBookmark( boolean isNext) {
		  
		MyCodeArea codeArea =     area; // SqlEditor.getCodeArea();  
		int idx = codeArea.getCurrentParagraph(); // 获取当前行号
		List<String> strs = codeArea.getMylineNumber().getLineNoList();
		
		
		int moveto = -1;
		if(strs !=null && strs.size() > 0) {
			List<Integer> rs = StrUtils.StrListToIntList(strs);
			if(! isNext) {
				rs.sort(Comparator.comparing(Integer::intValue).reversed()); 
			} 
			moveto = rs.get(0) - 1;
			for(Integer v : rs) {
				int i = v - 1;
				
				if(isNext) {
					if(idx < i ) {
						moveto = i ; 
						break;
					}
				}else {
					if(idx > i ) {
						moveto = i ; 
						break;
					}
				}
				
				
			}
		}
		if(moveto > -1 ) {
			codeArea.moveTo(moveto, 0);
			codeArea.showParagraphAtTop(moveto < 10 ? 0 : (moveto - 9));
		}
	
	}
}

