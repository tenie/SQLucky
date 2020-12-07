package net.tenie.fx.component;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.utility.EventAndListener.CommonEventHandler;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

/*   @author tenie */
public class SqlCodeAreaHighLighting {
  
	private static final String sampleCode = String.join("\n", new String[] { "" });

	private CodeArea codeArea;
	private ExecutorService executor;

	public StackPane getObj(String text, boolean editable) {
		executor = Executors.newSingleThreadExecutor();
		codeArea = new CodeArea();
		codeArea.setParagraphGraphicFactory(MyLineNumberFactory.get(codeArea));
		// 事件KeyEvent
		
		codeArea.addEventFilter(KeyEvent.KEY_PRESSED , e->{ 
			if(e.getCode() == KeyCode.TAB ) {
				System.out.println("e.getCode() "+e.getCode() );
				if (codeArea.getSelectedText().contains("\n") ) { 
					e.consume();
					if(e.isShiftDown()) {
						CommonAction.minus4Space();
					}else { 
						CommonAction.add4Space();
					}
				}
				
			}
		});
 
		codeArea.setOnKeyPressed(CommonEventHandler.codeAreaChange(codeArea)); 
		codeArea.replaceText(0, 0, sampleCode);
		if (text != null)
			codeArea.appendText(text);
		StackPane sp = new StackPane(new VirtualizedScrollPane<>(codeArea));
		sp.getStyleClass().add("my-tag");
		SqlCodeAreaHighLightingHelper.applyHighlighting(codeArea);
		codeArea.setEditable(editable);
		 
	    // 中午输入法显示问题
		codeArea.setInputMethodRequests(new InputMethodRequestsObject(codeArea));
		codeArea.setOnInputMethodTextChanged(e ->{ 			
			 if (e.getCommitted() != "") {
				 codeArea.insertText(codeArea.getCaretPosition(), e.getCommitted());
		        }
		});
		return sp;
	}

	public StackPane getObj() {
		return getObj(null, true);
	}

	public void stop() {
		executor.shutdown();
	}

	 

	 
 
}
class InputMethodRequestsObject implements InputMethodRequests {
    private CodeArea area;
	public InputMethodRequestsObject(CodeArea area) {
		this.area = area;
	}
	@Override
	public
    String getSelectedText() {
        return "";
    }
    @Override
	public
    int getLocationOffset(int x, int y) {
        return 0;
    }
    @Override
	public
    void cancelLatestCommittedText() {

    }
    @Override
    public Point2D getTextLocation(int offset) {
        // a very rough example, only tested under macOS
        Optional<Bounds> caretPositionBounds = area.getCaretBounds();
        if (caretPositionBounds.isPresent()) {
            Bounds bounds = caretPositionBounds.get();
            return new Point2D(bounds.getMaxX() - 5, bounds.getMaxY());
        } 
        throw new NullPointerException();
    }
    
}