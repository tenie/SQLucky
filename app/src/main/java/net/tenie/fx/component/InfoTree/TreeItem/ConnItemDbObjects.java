package net.tenie.fx.component.InfoTree.TreeItem;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import net.tenie.fx.Po.DBOptionHelper;
import net.tenie.fx.Po.TreeNodePo;
import net.tenie.Sqlucky.sdk.config.CommonConst;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.FuncProcTriggerPo;
import net.tenie.Sqlucky.sdk.po.TablePo;
import net.tenie.Sqlucky.sdk.po.TreeItemType;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;

/*   @author tenie */
public class ConnItemDbObjects {
//	private TreeItem<TreeNodePo> schemaNode;
	
	private TreeItem<TreeNodePo> parentNode;
	private String schemaName;
	
	private TreeItem<TreeNodePo> tableNode; 

	private TreeItem<TreeNodePo> viewNode; 

	private TreeItem<TreeNodePo> funcNode; 

	private TreeItem<TreeNodePo> procNode; 
	
	private TreeItem<TreeNodePo> triggerNode; 
	
	private TreeItem<TreeNodePo> indexNode; 
	
	private TreeItem<TreeNodePo> sequenceNode; 
	
	
	
	private SqluckyConnector connpo;

	public ConnItemDbObjects () {
		
	}
	// 初始化一个空的对象
	public void  initConnItem(SqluckyConnector connpo, String schemaName ) {
		this.connpo  = connpo;
		tableNode = CreateTableNode();
		viewNode = CreateViewNode();
		funcNode= CreateFunctionNode();
		procNode= CreateProceduresNode();
		triggerNode = CreateTriggerNode();
		indexNode = CreateTriggerNode();
		sequenceNode = CreateSequenceNode();
		if ("DB2".equals(connpo.getDbVendor().toUpperCase())) {
			sequenceNode = CreateSequenceNode(connpo, schemaName);
//			parentNode.getChildren().add(sequenceNode);
		}
		// 新数据对象的一些初始化操作
		TreeItem<TreeNodePo> cinewParentNode = new TreeItem<>(new TreeNodePo( schemaName, TreeItemType.SCHEMA,
		    		IconGenerator.svgImage("database", "#7CFC00 ") , connpo));
		setParentNode(cinewParentNode);
	}

	public ConnItemDbObjects(SqluckyConnector connpo, String schemaName ) {
		this.connpo  = connpo;
		this.schemaName = schemaName;
		createConnItem(connpo, schemaName);

	}
	 
	// 构造一个有数据的对象
	public void createConnItem(SqluckyConnector connpo , String schemaName) {

		tableNode = CreateTableNode(connpo, schemaName);
//		setTableNode(CreateTableNode(connpo, schemaName));
		
		viewNode = CreateViewNode(connpo, schemaName );
//		setViewNode(CreateViewNode(connpo, schemaName ));
		
		funcNode = CreateFunctionNode(connpo, schemaName);
//		setFuncNode(CreateFunctionNode(connpo, schemaName));
		procNode = CreateProceduresNode(connpo, schemaName);
//		setProcNode( CreateProceduresNode(connpo, schemaName)); 
		triggerNode = CreateTriggerNode(connpo, schemaName); 
		indexNode = CreateIndexNode(connpo, schemaName);
		
		
		//book-perspective
		
		parentNode = new TreeItem<>(new TreeNodePo(schemaName, TreeItemType.SCHEMA, 
				IconGenerator.svgImage("database", "#7CFC00 ") , connpo));
		
		parentNode.getChildren().add(tableNode);
		parentNode.getChildren().add(viewNode);
		parentNode.getChildren().add(funcNode);
		parentNode.getChildren().add(procNode);
		parentNode.getChildren().add(triggerNode);
		parentNode.getChildren().add(indexNode);
		
		if ("DB2".equals(connpo.getDbVendor().toUpperCase())) {
			sequenceNode = CreateSequenceNode(connpo, schemaName);
			parentNode.getChildren().add(sequenceNode);
		}
		 
		
		
	}
	
	

	// 创建表节点
//	public static TreeItem<TreeNodePo> CreateSchemaNode(DbConnectionPo connpo) {
//
//		TreeItem<TreeNodePo> schemas = new TreeItem<TreeNodePo>(
//				new TreeNodePo("Schemas", TreeItemType.SCHEMA_ROOT, ImageViewGenerator.svgImage("th-list", "#FFD700"), connpo));
//		// 获取schema 数据
//		Set<String> set = connpo.settingSchema();
//		for (String sche : set) {
//			TreeItem<TreeNodePo> item = new TreeItem<>(
//					new TreeNodePo(sche, TreeItemType.SCHEMA, ImageViewGenerator.svgImageUnactive("database"), connpo));
//			schemas.getChildren().add(item);
//		}
//		return schemas;
//	}

	// 创建表节点
	public   TreeItem<TreeNodePo> CreateTableNode() {
		TreeItem<TreeNodePo> Table =
				new TreeItem<TreeNodePo>(new TreeNodePo("Table", TreeItemType.TABLE_ROOT,
						IconGenerator.svgImage("window-restore", "blue"), connpo));
		return Table;
	}
	
	// 创建表格节点
	public   TreeItem<TreeNodePo> CreateTableNode(SqluckyConnector connpo, String sche) {
		TreeItem<TreeNodePo> Table = CreateTableNode();
		List<TablePo> tabs = DBOptionHelper.getTabsName(connpo, sche);
		// 缓存起来
		String cacheKey = connpo.getConnName() + "_" +sche;
		TreeObjCache.tableCache.put(cacheKey, tabs);
		for(TablePo po: tabs) {
			po.setTableType(CommonConst.TYPE_TABLE);
		}
		
		

		ObservableList<TreeItem<TreeNodePo>> sourceList = FXCollections.observableArrayList();
		for (TablePo tbpo : tabs) {
			TreeNodePo po = new TreeNodePo(tbpo.getTableName(), TreeItemType.TABLE,
					IconGenerator.svgImageUnactive("table"), connpo);
			po.setTable(tbpo);
			TreeItem<TreeNodePo> subitem = new TreeItem<>(po);
			sourceList.add(subitem);
		}
		Table.getChildren().setAll(sourceList);

		return Table;
	}

	// 创建View节点
	public   TreeItem<TreeNodePo> CreateViewNode() {
		TreeItem<TreeNodePo> Table = new TreeItem<TreeNodePo>(new TreeNodePo("View", TreeItemType.VIEW_ROOT,
				IconGenerator.svgImage("object-group", "blue"), connpo));
		 
		return Table;
	}

	public   TreeItem<TreeNodePo> CreateViewNode(SqluckyConnector connpo, String sche) {
		TreeItem<TreeNodePo> Table = new TreeItem<TreeNodePo>(new TreeNodePo("View", TreeItemType.VIEW_ROOT,
				IconGenerator.svgImage("object-group", "blue"), connpo));
		List<TablePo> tabs = DBOptionHelper.getViewsName(connpo, sche);// connpo.getViews(sche);
 
		// 缓存起来
		String cacheKey = connpo.getConnName() + "_" +sche;
		TreeObjCache.viewCache.put(cacheKey, tabs);
		for(TablePo po: tabs) {
			po.setTableType(CommonConst.TYPE_VIEW);
		}
		
		
		for (TablePo tbpo : tabs) {
			TreeNodePo po = new TreeNodePo(tbpo.getTableName(), TreeItemType.VIEW,
					IconGenerator.svgImageUnactive("table"), connpo);
			po.setTable(tbpo);
			TreeItem<TreeNodePo> subitem = new TreeItem<>(po);
			Table.getChildren().add(subitem);
		}

		return Table;
	}

	// 创建function节点
	public   TreeItem<TreeNodePo> CreateFunctionNode(SqluckyConnector connpo, String sche) {
		TreeItem<TreeNodePo> Table = new TreeItem<TreeNodePo>(new TreeNodePo("Function", TreeItemType.FUNCTION_ROOT,
				IconGenerator.svgImage("gears", "blue"), connpo));
		List<FuncProcTriggerPo> vals = DBOptionHelper.getFunctions(connpo, sche);// connpo.getFunctions(sche);

		addFuncTreeItem(Table, vals, "gear", TreeItemType.FUNCTION, connpo);

		return Table;
	}
	public   TreeItem<TreeNodePo> CreateFunctionNode( ) {
		TreeItem<TreeNodePo> Table = new TreeItem<TreeNodePo>(new TreeNodePo("Function", TreeItemType.FUNCTION_ROOT,
				IconGenerator.svgImage("gears", "blue"), connpo));
	 
		return Table;
	}

	// 创建Procedure节点
	public   TreeItem<TreeNodePo> CreateProceduresNode(SqluckyConnector connpo, String sche) {
		TreeItem<TreeNodePo> Table = new TreeItem<TreeNodePo>(new TreeNodePo("Procedure", TreeItemType.PROCEDURE_ROOT,
				IconGenerator.svgImage("puzzle-piece", "blue"), connpo));
		List<FuncProcTriggerPo> vals = DBOptionHelper.getProcedures(connpo, sche); // connpo.getProcedures(sche);

		addFuncTreeItem(Table, vals, "gear", TreeItemType.PROCEDURE, connpo);

		return Table;
	}
	public   TreeItem<TreeNodePo> CreateProceduresNode() {
		TreeItem<TreeNodePo> Table = new TreeItem<TreeNodePo>(new TreeNodePo("Procedure", TreeItemType.PROCEDURE_ROOT,
				IconGenerator.svgImage("puzzle-piece", "blue"), connpo));
		 
		return Table;
	}
	// 创建trigger节点
	
	public   TreeItem<TreeNodePo> CreateTriggerNode( ) {
		TreeItem<TreeNodePo> Table = new TreeItem<TreeNodePo>(new TreeNodePo("Trigger", TreeItemType.TRIGGER_ROOT,
				IconGenerator.svgImage("gears", "blue"), connpo));
	 
		return Table;
	}
	
	// 触发器
	public   TreeItem<TreeNodePo> CreateTriggerNode(SqluckyConnector connpo, String sche) {
		TreeItem<TreeNodePo> Table = new TreeItem<TreeNodePo>(new TreeNodePo("Trigger", TreeItemType.TRIGGER_ROOT,
				IconGenerator.svgImage("originals-ray-gun", "blue"), connpo));
		List<FuncProcTriggerPo> vals = DBOptionHelper.getTriggers(connpo, sche); // connpo.getProcedures(sche);

		addFuncTreeItem(Table, vals, "originals-ray-gun", TreeItemType.TRIGGER, connpo);

		return Table;
	}
	

	
	// 索引
	public   TreeItem<TreeNodePo> CreateIndexNode(SqluckyConnector connpo, String sche) {
		TreeItem<TreeNodePo> Table = new TreeItem<TreeNodePo>(new TreeNodePo("Index", TreeItemType.INDEX_ROOT,
				IconGenerator.svgImage("book-perspective", "blue"), connpo));
		List<FuncProcTriggerPo> vals = DBOptionHelper.getIndexs(connpo, sche); // connpo.getProcedures(sche);

		addFuncTreeItem(Table, vals, "book-perspective", TreeItemType.INDEX, connpo);

		return Table;
	}
	
	public   TreeItem<TreeNodePo> CreateIndexNode() {
		TreeItem<TreeNodePo> Table = new TreeItem<TreeNodePo>(new TreeNodePo("Index", TreeItemType.INDEX_ROOT,
				IconGenerator.svgImage("book-perspective", "blue"), connpo));
		 
		return Table;
	}
	
	// seq
	public   TreeItem<TreeNodePo> CreateSequenceNode(SqluckyConnector connpo, String sche) {
		TreeItem<TreeNodePo> Table = new TreeItem<TreeNodePo>(new TreeNodePo("Sequence", TreeItemType.SEQUENCE_ROOT,
				IconGenerator.svgImage("foundation-die-six", "blue"), connpo));
		List<FuncProcTriggerPo> vals = DBOptionHelper.getSequences(connpo, sche, true); // connpo.getProcedures(sche);

		addFuncTreeItem(Table, vals, "foundation-die-six", TreeItemType.SEQUENCE, connpo);

		return Table;
	}
	
	public   TreeItem<TreeNodePo> CreateSequenceNode() {
		TreeItem<TreeNodePo> Table = new TreeItem<TreeNodePo>(new TreeNodePo("Sequence", TreeItemType.SEQUENCE_ROOT,
				IconGenerator.svgImage("foundation-die-six", "blue"), connpo));
		 
		return Table;
	}


	// 默认的schema移动到第一位 , 遍历所有节点, 找默认节点, 从原位置删除, 后再插入到第一个位置
	// 并且添加tableNode
	public  void moveDefaultNodeToTopAddTable(String defSch, TreeItem<TreeNodePo> schemas,
			TreeItem<TreeNodePo> tableNode, TreeItem<TreeNodePo> viewNode, TreeItem<TreeNodePo> funcNode,
			TreeItem<TreeNodePo> procNode) {
		if (defSch != null) {
			ObservableList<TreeItem<TreeNodePo>> ls = schemas.getChildren();
			for (int i = 0; i < ls.size(); i++) {
				TreeItem<TreeNodePo> val = ls.get(i);
				if (val.getValue().getName().equals(defSch)) {
					val.getValue().setIcon(IconGenerator.svgImage("database", "#7CFC00 "));
					val.getChildren().add(tableNode);
					val.getChildren().add(viewNode);
					val.getChildren().add(funcNode);
					val.getChildren().add(procNode);

					ls.remove(i);
					ls.add(0, val);
					setParentNode(val);
					break;
				}
			}
		}
	}

	// TreeItem 添加 子节点
	public   void addFuncTreeItem(TreeItem<TreeNodePo> parent, List<FuncProcTriggerPo> tabs, String img,
			TreeItemType type, SqluckyConnector connpo) {
		if(tabs != null ) {
			for (FuncProcTriggerPo po : tabs) {
				addTreeItem(parent, po, img, type, connpo);
			}
		}
		
	}

	// TreeItem 添加 子节点
	public   void addTreeItem(TreeItem<TreeNodePo> parent, FuncProcTriggerPo fpt, String img, TreeItemType type,
			SqluckyConnector connpo) {
		TreeNodePo po = new TreeNodePo(fpt.getName());
		po.setType(type);
		po.setConnpo(connpo);
		po.setFuncProTri(fpt);
		TreeItem<TreeNodePo> subitem = new TreeItem<>(po);
		if (img != null && img.length() > 0) {
			Node iv = IconGenerator.svgImageUnactive(img);
			po.setIcon(iv);
		}
		parent.getChildren().add(subitem);

	}

//	public TreeItem<TreeNodePo> getSchemaNode() {
//		return schemaNode;
//	}
//
//	public void setSchemaNode(TreeItem<TreeNodePo> schemaNode) {
//
//		this.schemaNode = schemaNode;
//	}

	public TreeItem<TreeNodePo> getTableNode() {
		return tableNode;
	}

	public void setTableNode(TreeItem<TreeNodePo> tableNode) {
		this.tableNode = tableNode;
	}

	public TreeItem<TreeNodePo> getViewNode() {
		return viewNode;
	}

	public void setViewNode(TreeItem<TreeNodePo> viewNode) {
		this.viewNode = viewNode;
	}

	public TreeItem<TreeNodePo> getFuncNode() {
		return funcNode;
	}

	public void setFuncNode(TreeItem<TreeNodePo> funcNode) {
		this.funcNode = funcNode;
	}

	public TreeItem<TreeNodePo> getProcNode() {
		return procNode;
	}

	public void setProcNode(TreeItem<TreeNodePo> procNode) {
		this.procNode = procNode;
	}

	

	public TreeItem<TreeNodePo> getParentNode() {
		return parentNode;
	}

	public void setParentNode(TreeItem<TreeNodePo> parentNode) {
		this.parentNode = parentNode;
	}
	public String getSchemaName() {
		return schemaName;
	}
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public SqluckyConnector getConnpo() {
		return connpo;
	}

	public void setConnpo(SqluckyConnector connpo) {
		this.connpo = connpo;
	}
	public TreeItem<TreeNodePo> getTriggerNode() {
		return triggerNode;
	}
	public void setTriggerNode(TreeItem<TreeNodePo> triggerNode) {
		this.triggerNode = triggerNode;
	}
	 
	public TreeItem<TreeNodePo> getIndexNode() {
		return indexNode;
	}
	public void setIndexNode(TreeItem<TreeNodePo> indexNode) {
		this.indexNode = indexNode;
	}
	 
	public TreeItem<TreeNodePo> getSequenceNode() {
		return sequenceNode;
	}
	public void setSequenceNode(TreeItem<TreeNodePo> sequenceNode) {
		this.sequenceNode = sequenceNode;
	}
	 

}
