package net.tenie.fx.component;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonListener;
import net.tenie.fx.utility.CommonUtility;
import net.tenie.lib.tools.StrUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

/**
 * sql文本编辑组件
 * @author tenie
 *
 */
public class HighLightingSqlCodeArea {
	private static Logger logger = LogManager.getLogger(HighLightingSqlCodeArea.class);
	private static final String sampleCode = String.join("\n", new String[] { "" });

	private MyCodeArea codeArea;
	private ExecutorService executor; 
	private ChangeListener<String>  cl;
	


	public StackPane getObj(String text, boolean editable) {
		executor = Executors.newSingleThreadExecutor();
		codeArea = new MyCodeArea();
	    cl = CommonListener.codetxtChange(codeArea); 
	    // 行号主题色
	    SqlEditor.changeCodeAreaLineNoThemeHelper(codeArea); 
	    
	    HighLightingSqlCodeAreaContextMenu cm = new  HighLightingSqlCodeAreaContextMenu(); 
	    codeArea.setContextMenu(cm.getContextMenu());
		// 事件KeyEvent  
		codeArea.addEventFilter(KeyEvent.KEY_PRESSED , e->{
				
			// 提示框还在的情况下又有输入 
			if(MyAutoComplete.isShow()) {
				// 输入的是退格键, 需要判断是否要隐藏提示框
				if (e.getCode() == KeyCode.BACK_SPACE) {
					MyAutoComplete.backSpaceHide(codeArea);  
				}
				if(MyAutoComplete.isShow()) {
					MyAutoComplete.hide();
					SqlEditor.callPopup(codeArea);
				} 
			}
			
			// 文本缩进
			if(e.getCode() == KeyCode.TAB ) {
				SqlEditor.codeAreaTab(e, codeArea);
			}
			//  按 "." 跳出补全提示框
			else if(e.getCode() == KeyCode.PERIOD ){ 
				int anchor = codeArea.getAnchor();
				Consumer< String >  caller = x ->{ 
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) { 
						e1.printStackTrace();
					}
					Platform.runLater(()->{
						int lateAnchor = codeArea.getAnchor();
						if((lateAnchor-1) == anchor) {
							SqlEditor.callPopup( codeArea);
						}
					});
				};
				CommonUtility.runThread(caller);
				
			}
			else if(e.getCode() == KeyCode.A ) {
				SqlEditor.codeAreaCtrlShiftA(e, codeArea);
			}
			else if(e.getCode() == KeyCode.E ) {
				SqlEditor.codeAreaCtrlShiftE(e, codeArea);
			}
			else if(e.getCode() == KeyCode.W ) {
				SqlEditor.codeAreaCtrlShiftW(e, codeArea);				
			}
			else if(e.getCode() == KeyCode.U ) {
				SqlEditor.codeAreaCtrlShiftU(e, codeArea);				
			}
			else if(e.getCode() == KeyCode.K ) {
				SqlEditor.codeAreaCtrlShiftK(e, codeArea);				
			}
			else if(e.getCode() == KeyCode.D ) {
				SqlEditor.codeAreaAltShiftD(e, codeArea);
				SqlEditor.codeAreaCtrlShiftD(e, codeArea);		
			} 
			else if(e.getCode() == KeyCode.H ) {
				SqlEditor.codeAreaCtrlShiftH(e, codeArea);				
			}			
			else if(e.getCode() == KeyCode.ENTER ) { 
				SqlEditor.addNewLine(e, codeArea);	
			}
			else if (e.getCode() == KeyCode.BACK_SPACE || e.getCode() == KeyCode.DELETE) {
				SqlEditor.codeAreaBackspaceDelete(e, codeArea, cl); 
			}
			else if(e.getCode() == KeyCode.V ) { // 黏贴的时候, 防止页面跳到自己黏贴
				SqlEditor.codeAreaCtrlV(e, codeArea);
			}
			else if(e.getCode() == KeyCode.Z ) {  // 文本的样式变化会导致页面跳动, 在撤销的时候去除文本变化监听事件
				SqlEditor.codeAreaCtrlZ(e, codeArea, cl);
			}
			
		});
		//TODO 输入事件
		codeArea.textProperty().addListener( cl );	 
		codeArea.replaceText(0, 0, sampleCode);
		if (text != null) codeArea.appendText(text);
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
				int start =  ComponentGetter.codeAreaAnchor  ;  //codeArea.getAnchor();
				logger.debug("ComponentGetter.codeAreaAnchor = " + start);
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
			MyAutoComplete.hide();
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
//		    		SqlCodeAreaHighLightingHelper.applyHighlighting(codeArea);
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

	public MyCodeArea getCodeArea() {
		return codeArea;
	}

	public void setCodeArea(MyCodeArea codeArea) {
		this.codeArea = codeArea;
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