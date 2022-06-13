package net.tenie.fx.component;

import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import net.tenie.fx.Po.TreeNodePo;
import net.tenie.fx.component.InfoTree.DBinfoTree;
import net.tenie.fx.component.container.AppWindow;
import net.tenie.fx.component.container.DataViewContainer;
 

public class AppWindowComponentGetter {
	public static DataViewContainer dataView; 
	public static TreeView<TreeNodePo> treeView;
	public static DBinfoTree dbInfoTree; 
	public static AnchorPane dbInfoTreeFilter;
	
	
	public static volatile AppWindow app;
}
