package net.tenie.fx.component;

import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import net.tenie.Sqlucky.sdk.component.DataViewContainer;
import net.tenie.fx.Po.TreeNodePo;
import net.tenie.fx.component.InfoTree.DBinfoTree;
import net.tenie.fx.component.container.AppWindow;
 

public class AppWindowComponentGetter {
	public static DataViewContainer dataView; 
	public static TreeView<TreeNodePo> treeView;
	public static DBinfoTree dbInfoTree; 
	public static AnchorPane dbInfoTreeFilter;
	
	
	public static volatile AppWindow app;
}
