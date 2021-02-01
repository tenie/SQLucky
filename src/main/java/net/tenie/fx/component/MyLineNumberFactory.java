package net.tenie.fx.component;

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

/*   @author tenie */
/**
 *  代码行号
 * @author tenie
 *
 */
public class MyLineNumberFactory implements IntFunction<Node> {

	private GenericStyledArea<?, ?, ?> area;
	private static final Insets DEFAULT_INSETS = new Insets(0.0, 5.0, 0.0, 5.0);
	private static   Paint DEFAULT_TEXT_FILL = Color.web("#606366");
	private static final Font DEFAULT_FONT = Font.font("monospace", FontPosture.ITALIC, 13);
	private static   Background DEFAULT_BACKGROUND = new Background(
			new BackgroundFill(Color.web("#313335"), null, null));
	public static IntFunction<Node> get(GenericStyledArea<?, ?, ?> area, String textcolor, String backgroundcolor) {
		DEFAULT_TEXT_FILL = Color.web(textcolor);
		DEFAULT_BACKGROUND  = new Background( new BackgroundFill(Color.web(backgroundcolor ), null, null));
		return get(area, digits -> "%1$" + digits + "s");
	}
//	public static IntFunction<Node> get(GenericStyledArea<?, ?, ?> area) {
//		return get(area, digits -> "%1$" + digits + "s");
//	}

	public static IntFunction<Node> get(GenericStyledArea<?, ?, ?> area, IntFunction<String> format) {
		return new MyLineNumberFactory(area, format);
	}

	private final Val<Integer> nParagraphs;
	private final IntFunction<String> format;

	private MyLineNumberFactory(GenericStyledArea<?, ?, ?> area, IntFunction<String> format) {
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
		
		lineNo.setOnMouseClicked( mouseEvent -> {
			if (mouseEvent.getClickCount() == 1) {
				this.area.selectLine();
			}
		});
		

		// bind label's text to a Val that stops observing area's paragraphs
		// when lineNo is removed from scene
		lineNo.textProperty().bind(formatted.conditionOnShowing(lineNo));
		
		return lineNo;
	}

	private String format(int x, int max) {
		int digits = (int) Math.floor(Math.log10(max)) + 1;
		return String.format(format.apply(digits), x);
	}

}
