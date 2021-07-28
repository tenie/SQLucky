package net.tenie.fx.component;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.window.ConnectionEditor;
import net.tenie.lib.tools.StrUtils;

public class HighLightingSqlCodeAreaContextMenu {
	public static List<MenuItem> menuItems = new ArrayList<>();
	ContextMenu contextMenu; 
	
	public HighLightingSqlCodeAreaContextMenu() {
		contextMenu = new ContextMenu();  
		contextMenu.setMinWidth(200);
		contextMenu.setPrefWidth(200);
		MenuItem copy  = new MenuItem(" Copy      "); 
		copy.setOnAction(e->{
			SqlEditor.copySelectionText();
		});
		copy.setGraphic(ImageViewGenerator.svgImageDefActive("files-o"));
//		copy.setDisable(true);
//		copy.setId("OpenConnection");
		
		MenuItem del  = new MenuItem(" Delete      "); 
		del.setOnAction(e->{
			SqlEditor.deleteSelectionText();
		});
		del.setGraphic(ImageViewGenerator.svgImageDefActive("eraser"));
		
		MenuItem  cut = new MenuItem(" Cut      "); 
		cut.setOnAction(e->{
			SqlEditor.cutSelectionText();
		});
		cut.setGraphic(ImageViewGenerator.svgImageDefActive("scissors"));

		MenuItem  sqlFormat = new MenuItem(" Format  SQL    "); 
		sqlFormat.setGraphic(ImageViewGenerator.svgImageDefActive("paragraph")); 
		sqlFormat.setOnAction(e->{
			CommonAction.formatSqlText();
		}); 
		
		MenuItem  formatAll = new MenuItem(" Format All SQL "); 
		formatAll.setOnAction(e->{
			CommonAction.formatSqlText();
		});
		
		MenuItem  sqlUnformat = new MenuItem(" Unformat SQL  "); 
		sqlUnformat.setOnAction(e->{
			CommonAction.pressSqlText();
		}); 
		
		MenuItem  unformatAll = new MenuItem(" unformat All SQL "); 
		unformatAll.setOnAction(e->{
			CommonAction.pressSqlText();
		});
		
		MenuItem  find = new MenuItem(" Find ");  
		find.setGraphic(ImageViewGenerator.svgImageDefActive("search")); 
		find.setOnAction(e->{
			CommonAction.findReplace(false);
		});
		
		
		
		contextMenu.getItems().addAll(copy, del, cut, new SeparatorMenuItem(), 
				sqlFormat , formatAll, sqlUnformat, unformatAll, new SeparatorMenuItem(),
				find);
		menuItems.add(copy);
		menuItems.add(del);
		menuItems.add(cut);
		
		menuItems.add(sqlFormat);
		menuItems.add(formatAll);
		menuItems.add(sqlUnformat);
		menuItems.add(unformatAll);
		
		menuItems.add(find);
		
		// 菜单显示的时刻
		contextMenu.setOnShowing(e->{
			String str = SqlEditor.getCurrentCodeAreaSQLSelectedText();
			if(StrUtils.isNotNullOrEmpty(str)) {
				copy.setDisable(false);
				del.setDisable(false);
				cut.setDisable(false);
				sqlFormat.setDisable(false);
				formatAll.setDisable(true);
				sqlUnformat.setDisable(false);
				unformatAll.setDisable(true);
			}else {
				copy.setDisable(true);
				del.setDisable(true);
				cut.setDisable(true);
				sqlFormat.setDisable(true);
				formatAll.setDisable(false);
				sqlUnformat.setDisable(true);
				unformatAll.setDisable(false);
			}
		});
	}
	
	public ContextMenu getContextMenu() {
		return contextMenu;
	}
	public void setContextMenu(ContextMenu contextMenu) {
		this.contextMenu = contextMenu;
	}



}
