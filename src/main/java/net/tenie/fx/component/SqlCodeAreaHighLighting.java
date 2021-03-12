package net.tenie.fx.component;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.IndexRange;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.utility.EventAndListener.CommonEventHandler;
import net.tenie.lib.tools.StrUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

/*   @author tenie */
public class SqlCodeAreaHighLighting {
	private static Logger logger = LogManager.getLogger(SqlCodeAreaHighLighting.class);
	private static final String sampleCode = String.join("\n", new String[] { "" });

	private CodeArea codeArea;
	private ExecutorService executor;

	public StackPane getObj(String text, boolean editable) {
		executor = Executors.newSingleThreadExecutor();
		codeArea = new CodeArea();
		
		if(ConfigVal.THEME.equals("DARK")) {
			codeArea.setParagraphGraphicFactory(MyLineNumberFactory.get(codeArea ,"#606366" , "#313335"));
		}else {
			codeArea.setParagraphGraphicFactory(MyLineNumberFactory.get(codeArea, "#666", "#ddd"));
		} 
		// 事件KeyEvent 
		// 文本缩进
		codeArea.addEventFilter(KeyEvent.KEY_PRESSED , e->{
			if(e.getCode() == KeyCode.TAB ) {
				logger.info("文本缩进 ;   "+e.getCode() );
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
		
		// 当表被拖拽进入到code editor , 将表名插入到 光标处
		codeArea.setOnDragEntered(e->{
			String val = ComponentGetter.dragTreeItemName;
			if(StrUtils.isNotNullOrEmpty(val)) {
//				IndexRange i = codeArea.getSelection(); // 获取当前选中的区间
//				int start = i.getStart();
				int start =  ComponentGetter.codeAreaAnchor  ;  //codeArea.getAnchor();
				codeArea.insertText(start, " "+val);
				codeArea.selectRange(start+1, start + 1 + val.length());
			}
			
		});
		
		// 鼠标退出界面, 记录光标位置
		codeArea.setOnMouseExited(mouseEvent->{
			ComponentGetter.codeAreaAnchor =  codeArea.getAnchor();
		});
		
		
		 
		// 当鼠标释放, 判断是否为双击, 是双击选中对应的内容, 在判断有没有选择的文本, 有的话就修改所有相同的文本
		codeArea.setOnMouseReleased(mouseEvent->{
			
			String str  = codeArea.getSelectedText();
			String trimStr = str.trim();
			int strSz = trimStr.length(); 
			boolean isContinue = true;
			if (mouseEvent.getClickCount() == 2) {
				if(trimStr.length() == 0) {
					// 选中的内容为空白符, 就选中当前行
					codeArea.selectLine();
				}else {
					// 针对括号() {} []的双击, 选中括号内的文本
					isContinue = CommonAction.selectSQLDoubleClicked(codeArea); // 如果选中了内容, 就会返回false
				}
				
			}else if (mouseEvent.getClickCount() == 1) { //鼠标单击
					//单击  括号() {} []的双击, 找到下一个括号, 对括号添加选中样式
				isContinue = CommonAction.oneClickedFindParenthesis(codeArea);  
				  
				
			}
			
			// 上面已经选中了东西这里就不继续往下走了
			if(isContinue){ 
				if(strSz > 0 && !"*".equals(trimStr)) {
					// 查找选中的字符 
		    		SqlCodeAreaHighLightingHelper.applyFindWordHighlighting(codeArea, str); 
	  	    	}else {        
		    		SqlCodeAreaHighLightingHelper.applyHighlighting(codeArea);
		    	}
			}
			
	    	
	    	
	    	
		});
		
		
 
		// 选中事件
//		codeArea.selectedTextProperty().addListener(new ChangeListener<String>() {
//		    @Override
//		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//		    	
//		    	
//		    	String str = newValue.trim();
//		    	if(str.length() > 0) {
//		    		SqlCodeAreaHighLightingHelper.applyFindWordHighlighting(codeArea, ); 
//		    	}else {
//		    		SqlCodeAreaHighLightingHelper.applyHighlighting(codeArea);
//		    	}
//		    	
//		    
//		    }
//		});
		
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
	private static Logger logger = LogManager.getLogger(InputMethodRequestsObject.class);
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
    	logger.info("输入法软件展示");
        // a very rough example, only tested under macOS
        Optional<Bounds> caretPositionBounds = area.getCaretBounds();
        if (caretPositionBounds.isPresent()) {
            Bounds bounds = caretPositionBounds.get();
            return new Point2D(bounds.getMaxX() - 5, bounds.getMaxY());
        } 
        throw new NullPointerException();
    }
    
}