package net.tenie.fx.component;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import net.tenie.Sqlucky.sdk.component.ImageViewGenerator;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.CommonAction; 

public class HighLightingSqlCodeAreaContextMenu extends ContextMenu{
	public static List<MenuItem> menuItems = new ArrayList<>();
 
	
	public HighLightingSqlCodeAreaContextMenu(HighLightingCodeArea codeArea) {
		super();
		this.setPrefWidth(200);
		MenuItem copy  = new MenuItem("Copy                        (ctrl+C)"); 
		copy.setGraphic(ImageViewGenerator.svgImageDefActive("files-o"));
		copy.setOnAction(e->{
			SqlEditor.copySelectionText();
		});
		
		MenuItem Paste  = new MenuItem("Paste                       (ctrl+V)"); 
		Paste.setGraphic(ImageViewGenerator.svgImageDefActive("clipboard"));
		Paste.setOnAction(e->{
			SqlEditor.pasteTextToCodeArea();
		});
		
		
		
		MenuItem del = new MenuItem("Delete                      (ctrl+D)"); 
		del.setOnAction(e->{
//			SqlEditor.deleteSelectionText(); 
			codeArea.delLineOrSelectTxt();
		});
		del.setGraphic(ImageViewGenerator.svgImageDefActive("eraser"));
		
		MenuItem  cut = new MenuItem("Cut                         (ctrl+X)"); 
		cut.setOnAction(e->{
			SqlEditor.cutSelectionText();
		});
		cut.setGraphic(ImageViewGenerator.svgImageDefActive("scissors"));

		MenuItem  sqlFormat = new MenuItem("Format  SQL            (ctrl+shif+F)"); 
		sqlFormat.setGraphic(ImageViewGenerator.svgImageDefActive("paragraph")); 
		sqlFormat.setOnAction(e->{
			CommonAction.formatSqlText();
		}); 
		
		MenuItem  formatAll = new MenuItem("Format All SQL"); 
		formatAll.setOnAction(e->{
			CommonAction.formatSqlText();
		});
		
		MenuItem  sqlUnformat = new MenuItem("Unformat SQL"); 
		sqlUnformat.setOnAction(e->{
			CommonAction.pressSqlText();
		}); 
		
		MenuItem  unformatAll = new MenuItem("Unformat All SQL"); 
		unformatAll.setOnAction(e->{
			CommonAction.pressSqlText();
		});
		
		MenuItem  find = new MenuItem("Find                        (ctrl+F)");  
		find.setGraphic(ImageViewGenerator.svgImageDefActive("search")); 
		find.setOnAction(e->{
			CommonAction.findReplace(false);
		});
		
		MenuItem  replace = new MenuItem("Replace                     (ctrl+R)");   
		replace.setOnAction(e->{
			CommonAction.findReplace(true);
		});
		
		MenuItem  mvB = new MenuItem("Move to begin of line (ctrl+shift+A)");   
		mvB.setGraphic(ImageViewGenerator.svgImageDefActive("step-backward")); 
		mvB.setOnAction(e->{ 
			codeArea.moveAnchorToLineBegin( );
		});
		
		MenuItem  mvE = new MenuItem("Move to end of line   (ctrl+shift+E)");   
		mvE.setGraphic(ImageViewGenerator.svgImageDefActive( "step-forward")); 
		mvE.setOnAction(e->{ 
			codeArea.moveAnchorToLineEnd( );
		});
		
		
		Menu enditLine = new Menu("Edit text on the line"); 
		MenuItem  delWord = new MenuItem("Delete the word before the cursor      (ctrl+shift+W)");   
		delWord.setOnAction(e->{  
			codeArea.delAnchorBeforeWord( );
		});
		
		MenuItem  delChar = new MenuItem("Delete the character before the cursor (ctrl+shift+H)");   
		delChar.setOnAction(e->{ 
			codeArea.delAnchorBeforeChar( );
		});
		
		MenuItem  delAllChar = new MenuItem("Delete all characters before the cursor(ctrl+shift+U)");   
		delAllChar.setOnAction(e->{ 
			codeArea.delAnchorBeforeString( );
		});
		
		
		MenuItem  delWordBackward = new MenuItem("Delete the word after the cursor        (alt+shift+D)");   
		delWordBackward.setOnAction(e->{ 
			codeArea.delAnchorAfterWord( );
		});
		
		MenuItem  delCharBackward = new MenuItem("Delete the character after the cursor  (ctrl+shift+D)");   
		delCharBackward.setOnAction(e->{ 
			codeArea.delAnchorAfterChar( );
		});
		MenuItem  delAllCharBackward = new MenuItem("Delete all characters after the cursor (ctrl+shift+K)");   
		delAllCharBackward.setOnAction(e->{ 
			codeArea.delAnchorAfterString( );
		});
		
		
		
		
		
		this.getItems().addAll(copy, Paste, del, cut, new SeparatorMenuItem(), 
				sqlFormat , formatAll, sqlUnformat, unformatAll, new SeparatorMenuItem(),
				find, replace, new SeparatorMenuItem(),
				mvB, mvE, enditLine
				);
		enditLine.getItems().addAll(delWord, delChar,delAllChar,  delWordBackward, delCharBackward, delAllCharBackward ); 
				menuItems.add(copy);
				menuItems.add(Paste);
				menuItems.add(del);
				menuItems.add(cut);
				menuItems.add(sqlFormat);
				menuItems.add(formatAll);
				menuItems.add(sqlUnformat);
				menuItems.add(unformatAll);

				menuItems.add(find);
				menuItems.add(replace);

				menuItems.add(mvB);
				menuItems.add(mvE);
				menuItems.add(delWord);
				menuItems.add(delChar);
				menuItems.add(delWordBackward);
				menuItems.add(delCharBackward);
				
		
		// 菜单显示的时刻
		this.setOnShowing(e->{
			String str = SqlEditor.getCurrentCodeAreaSQLSelectedText();
			if(StrUtils.isNotNullOrEmpty(str)) {
				copy.setDisable(false);
//				del.setDisable(false);
				cut.setDisable(false);
				sqlFormat.setDisable(false);
				formatAll.setDisable(true);
				sqlUnformat.setDisable(false);
				unformatAll.setDisable(true);
			}else {
				copy.setDisable(true);
//				del.setDisable(true);
				cut.setDisable(true);
				sqlFormat.setDisable(true);
				formatAll.setDisable(false);
				sqlUnformat.setDisable(true);
				unformatAll.setDisable(false);
			}
			
			boolean hasVal = CommonUtility.clipboardHasString();
			if(hasVal) {
				Paste.setDisable(false);
			}else {
				Paste.setDisable(true);
			}
			
		});
	}
	
 


}
