package net.tenie.fx.component.CodeArea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Tab;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.component.MyTab;
import net.tenie.Sqlucky.sdk.SqluckyCodeAreaHolder;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.SqlcukyEditor;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyleSpansBuilder;

/**
 * sql文本编辑组件
 * @author tenie
 *
 */
public class HighLightingCodeArea implements SqluckyCodeAreaHolder {
	private static Logger logger = LogManager.getLogger(HighLightingCodeArea.class);
	private static final String sampleCode = String.join("\n", new String[] { "" });
	private StackPane codeAreaPane;
	private MyCodeArea codeArea;
	private ExecutorService executor; 
	private ChangeListener<String>  cl;
	private CodeAreaHighLightingHelper highLightingHelper;
	private MyAutoComplete myAuto ;
	
	public void  hideAutoComplete() {
		myAuto.hide();
	}
	
	public void showAutoComplete(double x , double y , String str) {
		myAuto.showPop(x, y+7, str); 
	}
	
	public void nextBookmark(boolean tf) {
		codeArea.getMylineNumber().nextBookmark(tf);
	}
	
	 

	public void setContextMenu(ContextMenu cm) {
	    if(cm != null) {
	    	 codeArea.setContextMenu(cm);
	    }
	}
//    HighLightingSqlCodeAreaContextMenu cm = new  HighLightingSqlCodeAreaContextMenu(this); 

	public HighLightingCodeArea(MyAutoComplete myAuto) {
		this.myAuto = myAuto;
		highLightingHelper = new CodeAreaHighLightingHelper();
		executor = Executors.newSingleThreadExecutor();
		codeArea = new MyCodeArea();
	    cl = (obj, o ,n ) ->{ 
			Consumer< String >  caller = x ->{
				Tab tb = SqlcukyEditor.mainTabPaneSelectedTab();
				MyTab mtb = (MyTab) tb;
				if (tb != null) {
					Platform.runLater(()->{
						String title = CommonUtility.tabText(tb);  
						if (!title.endsWith("*")) { 
							CommonUtility.setTabName(tb, title + "*");
							mtb.setModify(true);
						}
						this.highLighting();
					});

					// 缓存单词 
					if( myAuto != null ) {
						myAuto.cacheTextWord();
					}
					
				}
			};
			
			CommonUtility.delayRunThread(caller, 500);
		
	    };
	    // 行号主题色
	    changeCodeAreaLineNoThemeHelper(); 
	   
		// 事件KeyEvent  
		codeArea.addEventFilter(KeyEvent.KEY_PRESSED , e->{
				
			if(myAuto != null) {
				// 提示框还在的情况下又有输入 
				if(myAuto.isShow()) {
					// 输入的是退格键, 需要判断是否要隐藏提示框
					if (e.getCode() == KeyCode.BACK_SPACE) {
						myAuto.backSpaceHide(codeArea);  
					}
					if(myAuto.isShow()) {
						 myAuto.hide();
						 callPopup();
					} 
				}
			}
			
			
			// 文本缩进
			if(e.getCode() == KeyCode.TAB ) {
				codeAreaTab(e, codeArea);
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
							callPopup();
						}
					});
				};
				CommonUtility.runThread(caller);
				
			}
			else if(e.getCode() == KeyCode.A ) {
				codeAreaCtrlShiftA(e);
			}
			else if(e.getCode() == KeyCode.E ) {
				codeAreaCtrlShiftE(e);
			}
			else if(e.getCode() == KeyCode.W ) {
				codeAreaCtrlShiftW(e);				
			}
			else if(e.getCode() == KeyCode.U ) {
				codeAreaCtrlShiftU(e);				
			}
			else if(e.getCode() == KeyCode.K ) {
				codeAreaCtrlShiftK(e);				
			}
			else if(e.getCode() == KeyCode.D ) {
				codeAreaAltShiftD(e);
				codeAreaCtrlShiftD(e);		
			} 
			else if(e.getCode() == KeyCode.H ) {
				codeAreaCtrlShiftH(e);				
			}			
			else if(e.getCode() == KeyCode.ENTER ) { 
				addNewLine(e);	
			}
			else if (e.getCode() == KeyCode.BACK_SPACE || e.getCode() == KeyCode.DELETE) {
				codeAreaBackspaceDelete(e, cl); 
			}
			else if(e.getCode() == KeyCode.V ) { // 黏贴的时候, 防止页面跳到自己黏贴
				codeAreaCtrlV(e);
			}
			else if(e.getCode() == KeyCode.Z ) {  // 文本的样式变化会导致页面跳动, 在撤销的时候去除文本变化监听事件
				codeAreaCtrlZ(e, cl);
			}
			
		});
		//TODO 输入事件
		codeArea.textProperty().addListener( cl );	 
		codeArea.replaceText(0, 0, sampleCode);
		
		 
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
		codeArea.setOnMouseReleased(mouseEvent -> {
			if (mouseEvent.getButton() == MouseButton.PRIMARY) {  // 鼠标左键
				if (mouseEvent.getClickCount() == 1) {
					if(myAuto != null ) {
						myAuto.hide();
					}
					
				} 
				String str = codeArea.getSelectedText();
				String trimStr = str.trim();
				int strSz = trimStr.length();
				boolean isContinue = true;
				if (mouseEvent.getClickCount() == 2) {
					if (trimStr.length() == 0) {
						// 选中的内容为空白符, 就选中当前行
						codeArea.selectLine();
					} else {
						// 针对括号() {} []的双击, 选中括号内的文本
						isContinue = selectSQLDoubleClicked(codeArea); // 如果选中了内容, 就会返回false
					}

				} else if (mouseEvent.getClickCount() == 1) { // 鼠标单击
					// 单击 括号() {} []的双击, 找到下一个括号, 对括号添加选中样式
					isContinue = oneClickedFindParenthesis(codeArea);

				}

				// 上面已经选中了东西这里就不继续往下走了
				if (isContinue) {
					if (strSz > 0 && !"*".equals(trimStr)) {
						// 查找选中的字符
						highLighting( str);
					} 
				}

			}

		});
		
	}
	

	
	// 针对括号() {} []的双击, 选中括号内的文本
	 // 如果选中了内容, 就会返回false
	public static boolean selectSQLDoubleClicked( CodeArea codeArea) {
		boolean tf = true;
		String str  = codeArea.getSelectedText();
		String trimStr = str.trim();
		int strSz = trimStr.length();
		if(strSz > 0 ) {
			IndexRange i = codeArea.getSelection(); // 获取当前选中的区间 
    		int start = i.getStart();
    		Set<String> keys = charMap.keySet();
    		
    		for(String key : keys) { 
    			if(trimStr.endsWith(key)) { 
    				String val = charMap.get(key);
    	    		int endIdx = str.lastIndexOf(key);
    	    		int is = start + endIdx +1; 
    	    		int end = CommonUtility.findBeginParenthesisRange(codeArea.getText(), is, key , val );
    	    		if( end != 0 && end > is) {
    	    			codeArea.selectRange(is, end);
    	    		}
    	    		tf = false;
    	    		break;
    			}
    		}
    		
    		if(tf) {
    			keys = charMapPre.keySet();
    			for(String key : keys) { 
        			if(trimStr.endsWith(key)) { 
        				String val = charMapPre.get(key);        	    		
        	    		int endIdx = str.lastIndexOf(key);
        	    		int end = start + endIdx ; 
        	    		int is = CommonAction.findEndParenthesisRange(codeArea.getText(), end, key , val);
        	    		if( end > is) {
        	    			codeArea.selectRange(is, end);
        	    		}
        	    		
        	    		tf = false;
        	    		break;
        			}
        		}
    		}
			if (tf) {
				for(String v: charList) {
					if (trimStr.endsWith( v)) {
						int endIdx = str.lastIndexOf(v);
						int end = start + endIdx;
						IndexRange ir = CommonAction.findStringRange(codeArea.getText(), end, v);
						if ( (ir.getStart() + ir.getEnd()) > 0) {
							int st = ir.getStart();
							int en =ir.getEnd();
//							codeArea.selectRange(ir.getStart(), ir.getEnd());
							String tmpcheck = codeArea.getText(ir.getStart(), ir.getEnd());
							if(tmpcheck.endsWith(v)) {
								en--;
							}
							if(tmpcheck.startsWith(v)) {
								st++;
							}
							codeArea.selectRange(st, en);
							
						}

						tf = false; 
						break;
					}
				} 
			} 
    		
    		if(tf) {
    			if(trimStr.toUpperCase().endsWith("SELECT")) {
    	    		int endIdx = str.toUpperCase().lastIndexOf("SELECT"); 
    	    		int is = start + endIdx + 6; 
    	    		int end = CommonAction.findBeginStringRange(codeArea.getText(), is, "SELECT", "FROM");
    	    		if( end != 0 && end > is) {
    	    			codeArea.selectRange(is - 6 , end + 4);
    	    		}
    	    		tf = false; 
    			}else if(trimStr.toUpperCase().endsWith("FROM")) {
    	    		int endIdx = str.toUpperCase().lastIndexOf("FROM");
    	    		int end = start + endIdx ; 
    	    		int is = CommonAction.findEndStringRange(codeArea.getText(), end, "FROM", "SELECT");
    	    		if( end > is) {
    	    			codeArea.selectRange(is - 6, end + 5);
    	    		}
    	    		tf = false; 
    			} 
    			
    		} 
    		
    		if(tf) {
    			if(trimStr.toUpperCase().endsWith("CASE")) {
    	    		int endIdx = str.toUpperCase().lastIndexOf("CASE");
    	    		int is = start + endIdx + 4; 
    	    		int end = CommonAction.findBeginStringRange(codeArea.getText(), is, "CASE", "END");
    	    		if( end != 0 && end > is) {
    	    			codeArea.selectRange(is - 4, end + 3);
    	    		}
    	    		tf = false; 
    			}else if(trimStr.toUpperCase().endsWith("END")) {
    	    		int endIdx = str.toUpperCase().lastIndexOf("END");
    	    		int end = start + endIdx ; 
    	    		int is = CommonAction.findEndStringRange(codeArea.getText(), end, "END", "CASE");
    	    		if( end > is) {
    	    			codeArea.selectRange(is - 4, end + 4);
    	    		}
    	    		tf = false; 
    			}   
    		} 
    		 
    	} 
		return tf;
	}
	
	
	public StackPane getCodeAreaPane() {
		if( codeAreaPane == null) { 
			return getCodeAreaPane(null, true);
		}else {
			return codeAreaPane;
		}
	}
	public StackPane getCodeAreaPane(String text, boolean editable) {
		if( codeAreaPane == null) { 
			codeAreaPane = new StackPane(new VirtualizedScrollPane<>(codeArea));
			codeAreaPane.getStyleClass().add("my-tag");
		}
		
		if (text != null) {
			codeArea.appendText(text); 
			highLighting();
		}
		codeArea.setEditable(editable);
		
		return codeAreaPane;
	}
	
	
	public void highLighting(String str) {
		try {
			highLightingHelper.applyFindWordHighlighting(codeArea, str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public void highLighting() {
		try {
			highLightingHelper.applyHighlighting(codeArea);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void errorHighLighting( int begin , int length , String str) {
		try {
			highLightingHelper.applyErrorHighlighting(codeArea , begin ,   length ,  str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	

	public void stop() {
		executor.shutdown();
	}

	public MyCodeArea getCodeArea() {
		return codeArea;
	}

 
	 
//	public static ChangeListener< String> codetxtChange(HighLightingCodeArea obj){
//		return new ChangeListener<String>() {
//			@Override
//			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {}
//		};
//	}

	
	
	static Map<String, String> charMap = new HashMap<>();
	static Map<String, String> charMapPre = new HashMap<>(); 
	static List<String> charList = new ArrayList<>();
	
	
	static {
		charMap.put("(" , ")");
		charMap.put("[" , "]");
		charMap.put("{" , "}"); 
		
		charMapPre.put(")" , "("); 
		charMapPre.put("]" , "[");
		charMapPre.put("}" , "{");
		
		charList.add("\"");
		charList.add("'");
		charList.add("`");
		charList.add("%");
		 
		
	}
	// 改变样式
	public   void changeCodeAreaLineNoThemeHelper( ) {
		MyLineNumberNode nbf = null;
		List<String> lines = null;
		if(codeArea.getMylineNumber() != null ) {
			 lines =codeArea.getMylineNumber().getLineNoList(); 
		}
		
		if(ConfigVal.THEME.equals(CommonConst.THEME_DARK)) {
			nbf = MyLineNumberNode.get(codeArea ,"#606366" , "#313335", lines);
		}else if(ConfigVal.THEME.equals(CommonConst.THEME_YELLOW)) {
			nbf = MyLineNumberNode.get(codeArea ,"#ffffff" , "#000000", lines);
		}else if(ConfigVal.THEME.equals(CommonConst.THEME_LIGHT)) {
			nbf = MyLineNumberNode.get(codeArea, "#666", "#ddd", lines);
		}
		
		codeArea.setParagraphGraphicFactory(nbf);
		codeArea.setMylineNumber(nbf);
		 
	} 
		
		// 鼠标单击找到括号对, 标记一下
		public static boolean  oneClickedFindParenthesis( CodeArea codeArea) {
			boolean tf = true;
		
			int anchor = codeArea.getAnchor();
			int start = anchor == 0 ? anchor : anchor - 1 ; 
			int end = anchor +1;
			
			String text = codeArea.getText();
			if(text.length() == anchor) {
				return false;
			}
		 
			
			String str  = codeArea.getText(start, end);// codeArea.getSelectedText();
			String trimStr = str.trim();
			int strSz = trimStr.length();
//			logger.info("单击选中 |"+ trimStr+"|" );
			if(strSz > 0 ) { 
				logger.info("鼠标单击找到括号对, 标记一下 |"+ trimStr+"|" );
				
	    		Set<String> keys = charMap.keySet();
	    		
	    		for(String key : keys) { 
	    			if(trimStr.endsWith(key)) { 
	    				String val = charMap.get(key);
	    	    		int endIdx = str.lastIndexOf(key);
	    	    		int is = start + endIdx +1; 
	    	    		end = CommonUtility.findBeginParenthesisRange(codeArea.getText(), is, key , val );
	    	    		if( end != 0 && end > is) {
	    	    			
	    	    			setStyleSpans( codeArea, is -1, 1);
	    	    			setStyleSpans( codeArea, end, 1);
	    	    			
	    	    			
	    	    		}
	    	    		tf = false;
	    	    		break;
	    			}
	    		}
	    		
	    		if(tf) {
	    			keys = charMapPre.keySet();
	    			for(String key : keys) { 
	        			if(trimStr.endsWith(key)) { 
	        				String val = charMapPre.get(key);        	    		
	        	    		int endIdx = str.lastIndexOf(key);
	        	    	    end = start + endIdx ; 
	        	    		int is = CommonAction.findEndParenthesisRange(codeArea.getText(), end, key , val);
	        	    		if( end > is) {
	        	    			setStyleSpans( codeArea, is -1, 1);
	        	    			setStyleSpans( codeArea, end, 1);
	        	    			
	        	    		}
	        	    		
	        	    		tf = false;
	        	    		break;
	        			}
	        		}
	    		}
	    		
	    		 
	    		
		   }
		return tf;
		}
	    public static void  setStyleSpans(CodeArea codeArea , int idx, int size) {
	    	StyleSpansBuilder<Collection<String>> spansBuilder  = new StyleSpansBuilder<>();
			spansBuilder.add(Collections.emptyList(), 0);
	        spansBuilder.add(Collections.singleton("findparenthesis"),  size);
	        codeArea.setStyleSpans( idx , spansBuilder.create());
	    }
		

		/**
		 * 自动补全提示
		 * @param e
		 * @param codeArea
		 */
		public   void codePopup(KeyEvent e) { 
			if(myAuto == null ) return;
			if(e.isAltDown()) { 
				callPopup( );
				
			} 
		}
		public   void callPopup( ) {
			if(myAuto == null ) return;
			Platform.runLater(()->{
				Bounds  bd = codeArea.caretBoundsProperty().getValue().get();
				double x = bd.getCenterX();
				double y = bd.getCenterY(); 
				int anchor = codeArea.getAnchor();
				String str = "";
				for(int i = 1 ; anchor-i >= 0 ; i++) {
					var tmp = codeArea.getText(anchor-i, anchor);
					int tmplen = tmp.length();
					int idx =    anchor - tmplen ;
//					System.out.println(tmp 
//							+ tmp.startsWith(" ") + 
//							tmp.startsWith("\t") + 
//							tmp.startsWith("\n") +
//							(idx <= 0) +" ==="
//							);
					
					if(tmp.startsWith(" ") || tmp.startsWith("\t") || tmp.startsWith("\n") ||   idx <= 0   ) {
						str = tmp;
						break;
					}
				}
				myAuto.showPop(x, y+9 , str);
			});
		}

		/**
		 * 移动光标到当前行的行首
		 * @param e
		 * @param codeArea
		 */
		public  void codeAreaCtrlShiftA(KeyEvent e) {
			if(e.isShiftDown() && e.isControlDown()) {
				logger.info("光标移动到行首"+e.getCode() ); 
				moveAnchorToLineBegin();
			}
		}
		public  void moveAnchorToLineBegin( ) {
			int idx = codeArea.getCurrentParagraph(); // 获取当前行号
			codeArea.moveTo(idx, 0);	
		}
		
		/**
		 * 移动光标到当前行的行尾
		 * @param e
		 * @param codeArea
		 */
		public   void codeAreaCtrlShiftE(KeyEvent e ) {
			if(e.isShiftDown() && e.isControlDown()) {
				logger.info("光标移动到行尾"+e.getCode() ); 
				moveAnchorToLineEnd( );
			}
		}
		public   void moveAnchorToLineEnd(   ) {
			int idx = codeArea.getCurrentParagraph(); // 获取当前行号
			Paragraph<Collection<String>, String, Collection<String>>   p = codeArea.getParagraph(idx);
			String ptxt = p.getText();
			codeArea.moveTo(idx, ptxt.length()); 
		}
		/**
		 * 操作当前行的光标之前的单词
		 * @param e
		 * @param codeArea
		 */
		public   void codeAreaCtrlShiftW(KeyEvent e ) {
			if(e.isShiftDown() && e.isControlDown()) {
				logger.info("删除一个光标前的单词"+e.getCode() ); 
				delAnchorBeforeWord( );
			}
		}
		
		public   void delAnchorBeforeWord(   ) {
			int anchor =  codeArea.getAnchor(); //光标位置
			String txt = codeArea.getText(0, anchor); 
			
			int[] a = {0, 0, 0};
			a[0] = txt.lastIndexOf(" ");
			a[1] = txt.lastIndexOf("\t");
			a[2] = txt.lastIndexOf("\n") + 1;
			int max = CommonUtility.getMax(a);
			codeArea.deleteText(max, anchor);
		}
		
		/**
		 * 删除光标前一个字符
		 * @param e
		 * @param codeArea
		 */
		public   void codeAreaCtrlShiftH(KeyEvent e) {
			if(e.isShiftDown() && e.isControlDown()) {
				logger.info("删除一个光标前字符"+e.getCode() ); 
				delAnchorBeforeChar();
			}
		}
		
		public  void delAnchorBeforeChar( ) {
			int anchor =  codeArea.getAnchor(); //光标位置
			String txt = codeArea.getText(anchor-1, anchor); 
			if(!txt.equals("\n"))
				codeArea.deleteText(anchor-1, anchor);
		}
		/**
		 * 删除光标后一个单词
		 * @param e
		 * @param codeArea
		 */
		public   void codeAreaAltShiftD(KeyEvent e) {
			if(e.isShiftDown() && e.isAltDown()) {
				logger.info("删除一个光标后单词"+e.getCode() ); 
				delAnchorAfterWord( );
			}
		}
		public   void delAnchorAfterWord(  ) {
			int anchor =  codeArea.getAnchor(); //光标位置
			String txt = codeArea.getText(); 
			int txtLen = txt.length();
			int[] a = {0, 0, 0};
			int val = 0;
			val = txt.indexOf(" ", anchor) ;
			a[0] = val  == -1 ? txtLen : val + 1;
			val = txt.indexOf("\t", anchor) ;
			a[1] = val  == -1 ? txtLen : val + 1;
			val = txt.indexOf("\n", anchor) ;
			a[2] = val  == -1 ? txtLen : val;
			int min = CommonUtility.getMin(a);
			codeArea.deleteText(anchor, min );
		}
		
		/**
		 * 删除一个光标后字符
		 * @param e
		 * @param codeArea
		 */
		public   void codeAreaCtrlShiftD(KeyEvent e ) {
			if(e.isShiftDown() && e.isControlDown()) {
				logger.info("删除一个光标后字符"+e.getCode() ); 
				delAnchorAfterChar( );
			}
		}
		public   void delAnchorAfterChar() {
			int anchor =  codeArea.getAnchor(); //光标位置
			String txt = codeArea.getText(anchor, anchor+1); 
			if(!txt.equals("\n"))
				codeArea.deleteText(anchor, anchor+1);
		}
		/**
		 * 删除光标前的字符串
		 * @param e
		 * @param codeArea
		 */
		public   void codeAreaCtrlShiftU(KeyEvent e ) {
			if(e.isShiftDown() && e.isControlDown()) {
				logger.info("删除光标前的字符串"+e.getCode()); 
				delAnchorBeforeString( );
			}
		}
		public   void delAnchorBeforeString(      ) {
			int anchor =  codeArea.getAnchor(); //光标位置
			String txt = codeArea.getText(0, anchor); 
			 
			int idx = txt.lastIndexOf("\n");
			if( idx == -1) {
				idx = 0;
			}else {
				idx++;
			}
			codeArea.deleteText(idx, anchor);
		}
		/**
		 * 删除光标后的字符串
		 * @param e
		 * @param codeArea
		 */
		public   void codeAreaCtrlShiftK(KeyEvent e ) {
			if(e.isShiftDown() && e.isControlDown()) { 
				logger.info("删除光标后的字符串"+e.getCode());
				delAnchorAfterString( );
			}
		}
		
		public   void delAnchorAfterString(  ) {
			int anchor =  codeArea.getAnchor(); //光标位置
			String txt = codeArea.getText(); 
			 
			int idx = txt.indexOf("\n" , anchor);
			if( idx == -1) {
				idx = 0;
			}else {
				idx++;
			}
			codeArea.deleteText(anchor ,idx -1);
		}
		/**
		 * ctrl + d 
		 * @param e
		 * @param codeArea
		 */
		public  void codeAreaCtrlD(KeyEvent e) {
			if( e.isControlDown()) {
				logger.info("删除选中的内容或删除光标所在的行"+e.getCode() ); 
				delLineOrSelectTxt();
			}
		}
		/**
		 * 删除选中的内容或删除光标所在的行
		 * @param codeArea
		 */
		public  void delLineOrSelectTxt() {
			var selectTxt = codeArea.getSelectedText();
			if(StrUtils.isNullOrEmpty(selectTxt)) {
				moveAnchorToLineEnd( );
				delAnchorBeforeString( );
			}else {
				// 删除选中的内容 
				codeArea.deleteText( codeArea.getSelection());
			}
		}
		
		public  void addNewLine(KeyEvent e) {

			// 换行缩进, 和当前行的缩进保持一致
			logger.info("换行缩进 : "+e.getCode() );
			String seltxt = codeArea.getSelectedText();
			int idx = codeArea.getCurrentParagraph(); // 获取当前行号
			int anchor =  codeArea.getAnchor(); //光标位置
			
			if(seltxt.length() == 0) {//没有选中文本, 存粹换行, 才进行缩进计算 
				// 根据行号获取该行的文本
				Paragraph<Collection<String>, String, Collection<String>>   p = codeArea.getParagraph(idx);
				String ptxt = p.getText();
				
				// 获取文本开头的空白字符串
				if(StrUtils.isNotNullOrEmpty(ptxt)) { 
					
					// 一行的前缀空白符
					String strb = StrUtils.prefixBlankStr(ptxt);
					int countSpace = strb.length();
					
					// 获取光标之后的空白符, 如果后面的字符包含空白符, 换行的时候需要修正前缀补充的字符, 补多了换行越来越长 
//					String afterAnchorText =  codeArea.getText(anchor,codeArea.getText().length());
//					String strafter = StrUtils.prefixBlankStr(afterAnchorText);
					String strafter = paragraphPrefixBlankStr( anchor);
					
					String fstr = "";
					if(strafter.length() > 0 &&  strb.length() >  strafter.length()) {
						fstr = strb.substring(0 , strb.length() - strafter.length());
					}else {
						fstr = strb;
					}
					
					// 在新行插入空白字符串
					if(fstr.length() > 0) {
						e.consume();
						String addstr = "\n"+fstr; 
						codeArea.insertText(anchor , addstr);
						codeArea.moveTo(idx + 1, countSpace);
					}else {
						//如果光标在起始位, 那么回车后光标移动到起始再会到回车后的位置, 目的是防止页面不滚动
						if( anchor == 0) {
							Platform.runLater(() -> {
								codeArea.moveTo(0); // 光标移动到起始位置
								Platform.runLater(() -> {
								    codeArea.moveTo(1);
								});  
							});
						}else {
							e.consume();   
							codeArea.insertText(anchor , "\n");
						}
					}
					
				}
				
			}  
		}
		/**
		 * 触发删除按钮
		 * @param e
		 * @param codeArea
		 * @param cl
		 */
		public  void codeAreaBackspaceDelete(KeyEvent e, ChangeListener<String>  cl) { 
			// 删除选中字符串防止页面滚动, 自己删			
			codeArea.textProperty().removeListener(cl);
			Platform.runLater(() -> {
				codeArea.textProperty().addListener( cl );
			});  
		}
		
		/**
		 *  黏贴的时候, 防止页面跳到自己黏贴
		 * @param e
		 * @param codeArea
		 */
		public  void codeAreaCtrlV(KeyEvent e) {
			if( e.isShortcutDown()) {
				String val =  CommonUtility.getClipboardVal();
				logger.info("黏贴值==" + val);
				if(val.length() > 0) {
					String seltxt = codeArea.getSelectedText();
					if(seltxt.length() > 0) {
						IndexRange idx = codeArea.getSelection();
						codeArea.deleteText(idx);
						codeArea.insertText(codeArea.getAnchor(), val);
						e.consume(); 
					}
				} 
			}
		}
		
		/**
		 * 文本的样式变化会导致页面跳动, 在撤销的时候去除文本变化监听事件
		 * @param e
		 * @param codeArea
		 */
		public  void codeAreaCtrlZ(KeyEvent e, ChangeListener<String>  cl) {
			
			if( e.isShortcutDown()) {
				codeArea.textProperty().removeListener(cl);
				Platform.runLater(() -> {
					codeArea.textProperty().addListener( cl );
				});  
			}
		}

		private  String paragraphPrefixBlankStr( int anchor) {
			int a = anchor;
			int b = anchor + 1;
			int len = codeArea.getText().length();
			
			StringBuilder strb2 = new StringBuilder("");
		
			while(true) {
					if(a >= len) break;
					 
				    String sc =  codeArea.getText(a, b);  
					if(" ".equals(sc) || "\t".equals(sc)) {
						strb2.append(sc);
					}else {
						break;
					} 
					a++;
					b++;
					
			}
			
			return strb2.toString();
		}
		
		/**
		 * 文本缩进
		 * @param e
		 * @param codeArea
		 */
		public static void codeAreaTab(KeyEvent e, CodeArea codeArea) { 
			if (codeArea.getSelectedText().contains("\n") ) { 
				logger.info("文本缩进 : "+e.getCode() ); 
				e.consume();
				if(e.isShiftDown()) {
					CommonAction.minus4Space();
				}else { 
					CommonAction.add4Space();
				}
			} 
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