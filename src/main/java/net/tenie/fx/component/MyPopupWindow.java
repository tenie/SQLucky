package net.tenie.fx.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.PropertyPo.DbConnectionPo;
import net.tenie.fx.PropertyPo.DbSchemaPo;
import net.tenie.fx.PropertyPo.TablePo;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.factory.TreeNodeCellFactory;
import net.tenie.fx.factory.TreeNodeCellFactory2;
import net.tenie.lib.tools.StrUtils;

public class MyPopupWindow {
	private static List<TablePo> tbs = new ArrayList<>();
	private static Popup pop ;
	private static VBox vb ;
	private static TreeItem<TablePo> rootNode  ;
	private static TreeView<TablePo> treeView ;
	private	static List<TablePo> tmpls = new ArrayList<>();
	private static String filterStr = "";
	
	
	static {
		tbs.add(new TablePo("SELECT"));
		tbs.add(new TablePo("FROM"));
		tbs.add(new TablePo("WHERE"));
		tbs.add(new TablePo("LEFT JOIN"));
		
		vb = new VBox();
		vb.setPrefHeight(250);
		vb.setPrefWidth(300);
		rootNode = new TreeItem<>();
		treeView = new TreeView<>(rootNode); 
		
		treeView.setShowRoot(false);
		vb.getChildren().add(treeView);
		VBox.setVgrow(treeView, Priority.ALWAYS);

		treeView.setCellFactory(new TreeNodeCellFactory2());

		// 鼠标点击的时候
		treeView.setOnMouseClicked(e -> {
			var it = treeView.getSelectionModel().getSelectedItem();
			String selectVal = it.getValue().getTableName();
			System.out.println(selectVal);
			codeAreaReplaceString(selectVal);
			pop.hide();
		});

		// 回车的时候
		treeView.setOnKeyPressed(e -> {
			System.out.println(e.getCode());
			if (KeyCode.ENTER.equals(e.getCode())) {
				var it = treeView.getSelectionModel().getSelectedItem();
				String selectVal = it.getValue().getTableName();
				System.out.println(selectVal);
				codeAreaReplaceString(selectVal);
				pop.hide();
			} else if (KeyCode.ESCAPE.equals(e.getCode())) {
				CommonAction.pressBtnESC();
			}
		});
		
	}
	
	public static void hide() {
		if(pop !=null ) {
			pop.hide();
			pop.getContent().clear();
			pop = null;
		}
	
	}
	
	public static boolean isShow() {
		if(pop == null ) {
			return false;
		}else {
			return pop.isShowing();
		}
	}
	
	
	 

	public static void showPop(double x, double y, String fStr) {
		filterStr = fStr.trim().toUpperCase();
			
	    pop = new Popup();
		pop.setX(x);
		pop.setY(y);
//		pop.setWidth(100);
//		pop.setHeight(100);
//		pop.setHideOnEscape(true);
		pop.setAutoHide(true); 
		pop.getContent().add(vb);
		
		if(! isShow()) {
			tmpls = new ArrayList<>();
			DbConnectionPo po = CommonAction.getDbConnectionPoByComboBoxDbConnName();
			if (po != null) {
				Map<String, DbSchemaPo> map = po.getSchemas();
				DbSchemaPo spo = map.get(po.getDefaultSchema());
				if (spo != null) { 
					tmpls.addAll(tbs  ) ;  
					tmpls.addAll(spo.getTabs()  ) ; 
					tmpls.addAll(spo.getViews() ) ; 
				}
			}else {
				tmpls.addAll(tbs  ) ;  
			}
		
		}  
		
		boolean tf =false;
		if( tmpls != null && tmpls.size()>0) {
			rootNode.getChildren().clear();
			for (var tb : tmpls) {
				if( tb.getTableName().contains(filterStr) ){
//					System.out.println(filterStr + " === " + tb.getTableName()); 
					TreeItem<TablePo> item = new TreeItem<>(tb);
					rootNode.getChildren().add(item);
					tf = true;
				}
				
			} 	
			if(tf) {
				pop.show(ComponentGetter.primaryStage); 
				Platform.runLater(() -> { 
					treeView.getSelectionModel().select(0); 
				}); 
			}else {
				pop = null;
			}
			
		}
		
		
		
		 
			
			
			
			
			
	 
	}
	
	//替换输入
	public static void codeAreaReplaceString(String selectVal ) {
		int len = selectVal.length();
		var codeArea = SqlEditor.getCodeArea();
		int anc = codeArea.getAnchor();
		
		int start = 0;
		int end = anc;
		if(anc > len) {
			start = anc - len;
		}
		
		if(filterStr.length() > 0 ) {
			start = anc - filterStr.length();
		}
		
		String caStr = codeArea.getText(start, end);
		if(StrUtils.isNotNullOrEmpty(caStr)) {
			caStr = caStr.toUpperCase();
			for(int i = 0 ; i < len -1; i++) {
				String tmp = caStr.substring(i);
				if(selectVal.startsWith(tmp)) {
					int begin = start - i;
					if(begin < 0) {
						begin = 0;
					}
					codeArea.deleteText(begin, end);
					codeArea.insertText(begin, selectVal);
					return ;
				}
	 		}
			
		}
		if(filterStr.length() > 0 ) {
			int begin = anc - filterStr.length(); 
			if(begin < 0) {
				begin = 0;
			}
			codeArea.deleteText(begin, anc); 
			codeArea.insertText(begin, selectVal);
			return;
		}
		// 没有匹配到直接输入
		codeArea.insertText(anc, selectVal);
		
	}
	
}
