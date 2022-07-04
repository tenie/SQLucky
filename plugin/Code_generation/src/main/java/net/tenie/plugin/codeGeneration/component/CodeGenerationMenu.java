package net.tenie.plugin.codeGeneration.component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import net.tenie.Sqlucky.sdk.SqluckyTab;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.DBNodeInfoPo;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.po.TreeItemType;
import net.tenie.Sqlucky.sdk.utility.IconGenerator;
import net.tenie.plugin.codeGeneration.utility.CreateInfoservicePoBean;

public class CodeGenerationMenu {
	
	List<MenuItem> menuItems = new ArrayList<>();
	List<Menu> menus = new ArrayList<>();
	
	public CodeGenerationMenu() {
		
		
		Menu generationMenu = new Menu("Generation Java Code");
		generationMenu.setGraphic(IconGenerator.svgImageDefActive("java"));
		
		MenuItem Infoservice = new MenuItem("Generation Infoservice Po");

		
		Infoservice.setDisable(true);
		Infoservice.setOnAction(v->{
			DBNodeInfoPo info =  ComponentGetter.appComponent.currentDBInfoNode();
			System.out.println("test  :  " +info.getType());
			if( info.getType() == TreeItemType.TABLE) {
				SqluckyConnector sqlcon = info.getConnpo();
				String  tab = info.getName();
				CreateInfoservicePoBean gen = new CreateInfoservicePoBean();
		        try {
				    gen.createPo(tab , sqlcon.getConn());
				    
					DocumentPo fileNode = new DocumentPo();
					fileNode.setTitle( gen.getPoClassFileName());
					fileNode.setText( gen.getPoTxt());
					SqluckyTab mtb = ComponentGetter.appComponent.sqluckyTab(fileNode);
					mtb.mainTabPaneAddTextTab();  // 界面上显示
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
		// 非表格节点就是禁用
		Consumer<String> onShowing = v->{
			if( ComponentGetter.appComponent.currentDBInfoNodeType() == TreeItemType.TABLE) {
				Infoservice.setDisable(false);
			}else {
				Infoservice.setDisable(true);
			}
		};
		ComponentGetter.appComponent.setDBInfoMenuOnShowing(onShowing);
		generationMenu.getItems().add(Infoservice);
		
		menus.add(generationMenu);
	}

	public List<MenuItem> getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(List<MenuItem> menuItems) {
		this.menuItems = menuItems;
	}

	public List<Menu> getMenus() {
		return menus;
	}

	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}
	
	
	
	
	
}
