package net.tenie.fx.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckTreeView;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.ComponentGetter;
import net.tenie.fx.component.ImageViewGenerator;
import net.tenie.fx.component.ModalDialog;
import net.tenie.fx.component.container.ConnItemContainer;
import net.tenie.fx.component.container.ConnItemDbObjects;
import net.tenie.fx.component.container.MyTreeItem;
import net.tenie.fx.component.container.TaskCellFactory;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.config.MainTabs;
import net.tenie.fx.dao.TransferTabeDataDao;
import net.tenie.lib.db.DBTools;
import net.tenie.lib.db.ExportDDL;
import net.tenie.lib.po.DbConnectionPo;
import net.tenie.lib.tools.StrUtils;

public class TransferDataController implements Initializable {
	private static Logger logger = LogManager.getLogger(TransferDataController.class);
	private static final String TABLE = "Table"; 
	private static final String VIEW = "View";
	private static final String FUNCTION = "Function";
	private static final String PROCEDURE = "Procedure";
	private static final String TRIGGER = "Trigger";
	private static final String INDEX = "Index";
	private static final String SEQUENCE = "Sequence";
	
	
	
	
	private static Thread currentThread; 
	
	
	@FXML private HBox treePane;
	
	@FXML private JFXComboBox<Label>  soDB;
	@FXML private JFXComboBox<Label>  soSC;
	
	@FXML private JFXComboBox<Label>  taDB;
	
	@FXML private JFXComboBox<Label>  taSC;
	
	@FXML private JFXCheckBox isIgnore; 
	@FXML private JFXCheckBox isDel;
	
	@FXML private JFXCheckBox tabData; 
	@FXML private JFXCheckBox tabStruct; 
	
	@FXML private JFXCheckBox chView;
	@FXML private JFXCheckBox chFun;
	@FXML private JFXCheckBox chPro;
	@FXML private JFXCheckBox chTri;
	@FXML private JFXCheckBox chIndex; 
	@FXML private JFXCheckBox chSeq;
	
	
	@FXML private Label queryLabel;
	@FXML private JFXButton execBtn;
	@FXML private JFXButton stopBtn;
	@FXML private JFXButton bRun;
	@FXML private TextField	filterTxt;
	
	
	private CheckTreeView<String> checkTreeView;
	private CheckBoxTreeItem<String> root;
//	private CheckBoxTreeItem<String> filterRoot;
	 ObservableList<Label > empty = FXCollections.observableArrayList();
	
	// 清除 check Box 
	private void cleanCheckBox() {
		isIgnore.setSelected(false);
		isDel.setSelected(false);
		
		tabData.setSelected(false);
		tabStruct.setSelected(false);
		
		chView.setSelected(false);
		chFun.setSelected(false);
		chPro.setSelected(false);
		chTri.setSelected(false);
		chIndex.setSelected(false);
		chSeq.setSelected(false); 
		
		
	}
	 

	
	// 初始化方法, 这边在初始化的时候添加按钮的点击事件
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		filterTxtInitialize();
		queryLabel.setGraphic(ImageViewGenerator.svgImageUnactive("search"));
//		execBtn.getStyleClass().add("my-run-btn");
//		execBtn.setStyle("-fx-background-color: green");
		 
		soDB.setItems(DBConns.getChoiceBoxItems());
		taDB.setItems(DBConns.getChoiceBoxItems());
//		soDB.setItems( getConnComboBoxList()); 
//		taDB.setItems( getConnComboBoxList());

		soDB.getSelectionModel().selectedItemProperty()
				.addListener((ChangeListener<? super Label>) (observable, oldValue, newValue) -> {
					soSC.setItems(empty);
					cleanCheckBox();
					String str = newValue.getText();
					soSC.setItems(getSchemaLabels(str));

					root.getChildren().removeAll(root.getChildren()); 
					filterTxt.clear();
				});
		soSC.getSelectionModel().selectedItemProperty()
				.addListener((ChangeListener<? super Label>) (observable, oldValue, newValue) -> {
					root.getChildren().removeAll(root.getChildren());
					cleanCheckBox();
					filterTxt.clear();
				});

		taDB.getSelectionModel().selectedItemProperty()
				.addListener((ChangeListener<? super Label>) (observable, oldValue, newValue) -> {
					String str = newValue.getText();
					taSC.setItems(getSchemaLabels(str));
				});

		root = new CheckBoxTreeItem<String>("全选");
		root.setExpanded(true); 
	    checkTreeView = new CheckTreeView<>(root);
		checkTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		treePane.getChildren().addAll(checkTreeView);
		HBox.setHgrow(checkTreeView, Priority.ALWAYS);

		// check box
		tabData.selectedProperty().addListener((ChangeListener<? super Boolean>) (observable, oldValue, newValue) -> {
			if (newValue && 
				!tabStruct.isSelected()&& 
				soDB.getValue() != null && 
				soSC.getValue() !=null ) {
					String dbname = soDB.getValue().getText();
					String schename = "";
					if(soSC.getValue() != null) {
						schename = soSC.getValue().getText();
					}
					
					if (StrUtils.isNotNullOrEmpty(dbname) && StrUtils.isNotNullOrEmpty(schename)) {
						TreeItem<TreeNodePo> schemaNode = ComponentGetter.getSchemaNode(dbname, schename);

						addNodeHelper(schemaNode, TABLE);
					}
			}

			removeNode(!newValue && !tabStruct.isSelected() , TABLE);
			 

		});
		tabStruct.selectedProperty().addListener((ChangeListener<? super Boolean>) (observable, oldValue, newValue) -> {
			if (newValue && 
				!tabData.isSelected()&& 
				soDB.getValue() != null && 
				soSC.getValue() !=null ) {
					String dbname = soDB.getValue().getText();
					String schename = soSC.getValue().getText(); 
					if(soSC.getValue() != null) {
						schename = soSC.getValue().getText();
					}
					if (StrUtils.isNotNullOrEmpty(dbname) && StrUtils.isNotNullOrEmpty(schename)) {
						TreeItem<TreeNodePo> schemaNode = ComponentGetter.getSchemaNode(dbname, schename);
						
						addNodeHelper(schemaNode, TABLE);
					}
			}

			removeNode(!newValue && !tabData.isSelected(), TABLE);
		});

		chView.selectedProperty().addListener(CheckListener(VIEW));
		chFun.selectedProperty().addListener(CheckListener(FUNCTION));
		chPro.selectedProperty().addListener(CheckListener(PROCEDURE));
		chTri.selectedProperty().addListener(CheckListener(TRIGGER));
		chIndex.selectedProperty().addListener(CheckListener(INDEX));
		chSeq.selectedProperty().addListener(CheckListener(SEQUENCE));
		
		//TODO 执行按钮
		execBtn.setOnAction(e->{ 
			btnController(true);
			currentThread = new Thread() {
				public void run() {
					runBtnAction(); 
					btnController(false);
				};
			};
			currentThread.start();
		});
		// 停止按钮
		stopBtn.setOnAction(e->{
			if(currentThread != null) {
				currentThread.stop();
				btnController(false);
			}
		});
		
		
		bRun.setOnAction(e->{ 
			ComponentGetter.dataTransferStage.hide();
		});

	}
	
	
	
	// 按钮变化控制
	private void btnController(boolean tf) {
		Platform.runLater(() -> {
			if (tf) {
				execBtn.setDisable(true);
				execBtn.getStyleClass().add("transfer-run-btn");
				stopBtn.getStyleClass().add("transfer-stop-btn");
			} else {
				execBtn.setDisable(false);
				execBtn.getStyleClass().remove("transfer-run-btn");
				stopBtn.getStyleClass().remove("transfer-stop-btn");
			}
		});
	}
	
	private boolean checkDbConn() {
		if (soSC.getValue() != null && 
			soDB.getValue() != null && 
			taDB.getValue() != null && 
			taSC.getValue() != null) {
				String dbname = soDB.getValue().getText();
				String schename = soSC.getValue().getText();
				String targetDBName = taDB.getValue().getText();
				String targetSchename = taSC.getValue().getText();

			if (StrUtils.isNotNullOrEmpty(dbname) && 
				StrUtils.isNotNullOrEmpty(schename) && 
				StrUtils.isNotNullOrEmpty(targetDBName) && 
				StrUtils.isNotNullOrEmpty(targetSchename)) {
					return true;
			} 
		}

		return false;
	}
	
	// 复制一个链接对象
	private DbConnectionPo getNewDbConnectionPo(String dbName , String schema) {
		DbConnectionPo soDbpo = DBConns.get(dbName);
		DbConnectionPo nDBpo  = DbConnectionPo.copyObj(soDbpo, schema);
		return nDBpo;
	}
	
	private void runBtnAction() {  
		if ( checkDbConn() ) {
			String dbname = soDB.getValue().getText();
			String schename = soSC.getValue().getText();
			String targetDBName = taDB.getValue().getText();
			String targetSchename = taSC.getValue().getText();
			
//			DbConnectionPo soDbpo = DBConns.get(dbname);
			DbConnectionPo soDbpo =  getNewDbConnectionPo(dbname, schename);
			Connection  soConn = soDbpo.getConn(); 

			
			DbConnectionPo tarDbpo =  getNewDbConnectionPo(targetDBName, targetSchename);// DBConns.get(targetDBName);
			Connection  tarConn = tarDbpo.getConn();
			
			ExportDDL export = soDbpo.getExportDDL(); 
			
			// 将要执行的sql集合
			List<String> sqls = new ArrayList<>(); 
			// 表结构
			createSynSql(tabStruct.isSelected(), sqls, soConn, export, schename, TABLE, targetSchename);
			// 视图同步
			createSynSql(chView.isSelected(), sqls, soConn, export, schename, VIEW, targetSchename);
			// 函数同步
			createSynSql(chFun.isSelected(), sqls, soConn, export, schename, FUNCTION, targetSchename);
			// 过程同步
			createSynSql(chPro.isSelected(), sqls, soConn, export, schename, PROCEDURE, targetSchename);

			// 触发器同步
			createSynSql(chTri.isSelected(), sqls, soConn, export, schename, TRIGGER, targetSchename);
			// 索引同步
			createSynSql(chIndex.isSelected(), sqls, soConn, export, schename, INDEX, targetSchename);
			// 序列同步
			createSynSql(chSeq.isSelected(), sqls, soConn, export, schename, SEQUENCE, targetSchename);

//			logger.info(sqls);
			// 执行ddl
			DBTools.execListSQL(sqls, tarConn);
			// 数据同步 执行insert 
			if(tabData.isSelected()) { 
				synTabData( soConn ,tarConn ,  export, schename , targetSchename);
			} 
		}
	}
	
 
	//同步表数据 
	private void synTabData(Connection  soConn , Connection  toConn , ExportDDL export,  String schename , String targetSchename) {
			boolean delObj = isDel.isSelected(); 
			TreeItem<String> table = rootSubNode(TABLE);
			try {
				if(table != null) {
					ObservableList<CheckBoxTreeItem<String> > selectNodes = selectNode(table);
					for(CheckBoxTreeItem<String> cb : selectNodes ) { 
						String tabName = cb.getValue();
						// 删语句
						cleanData(delObj, toConn, targetSchename, tabName);
						TransferTabeDataDao.insertData(soConn,  toConn, tabName, schename, targetSchename);
					}
				}
			} catch (SQLException e) { 
				e.printStackTrace();
			}  
		}
	// 删表数据
	private void cleanData(boolean tf, Connection  toConn , String targetSchename, String tablename ) throws SQLException {
		if(tf) {
			String tableName = targetSchename+"."+tablename;
			DBTools.execDelTab(toConn, tableName); 
		}
	}

	// 试图, 函数, 索引等
	private void createSynSql(boolean tf, List<String> sqls, Connection soConn, ExportDDL export, String schename,
			String nodeType, String targetSchename) { 
		if (tf) {
			boolean delObj = isDel.isSelected();
//				List<String> sqls = new ArrayList<>();
			TreeItem<String> table = rootSubNode(nodeType);
			if (table != null) {
				ObservableList<CheckBoxTreeItem<String>> selectNodes = selectNode(table);
				for (CheckBoxTreeItem<String> cb : selectNodes) {
					String checkBoxName = cb.getValue();
					// drop语句
					if (delObj) {
						String drop = getDropDDL(export, nodeType, schename, checkBoxName, targetSchename);
						logger.info(drop);
						sqls.add(drop);
					}
					// create语句
					String create = getCreateDDL(soConn, export, nodeType, schename, checkBoxName, targetSchename);
					logger.info(create);
					sqls.add(create);

				}
			}

		}
//			return sqls;
	}
		 
	
	//  获取drop 语句
	private String  getDropDDL( ExportDDL export, String type, String schename, String objName, String tarSchename) {
		String drop = "";
		if(type.equals(TABLE)) {
			drop = export.exportDropTable(schename, objName);
		}else if(type.equals(VIEW)) {
			drop = export.exportDropView(schename, objName);
		}else if(type.equals(FUNCTION)) {
			drop = export.exportDropFunction(schename, objName);
		}else if(type.equals(PROCEDURE)) {
			drop =  export.exportDropProcedure(schename, objName);
		}else if(type.equals(TRIGGER)) {
			drop =  export.exportDropTrigger(schename, objName);
		}else if(type.equals(INDEX)) {
			drop =  export.exportDropIndex(schename, objName);
		}else if(type.equals(SEQUENCE)) {
			drop =  export.exportDropSequence(schename, objName);
		}
		
		
		drop = drop.replaceAll(schename+"."+objName,  tarSchename+ "." + objName);
		return drop;
	}
	//  获取Create 语句
	private String  getCreateDDL(Connection  conn, ExportDDL export, String type, String schename, String objName, String tarSchename) {
		String drop = "";
		if(type.equals(TABLE)) {
			drop = export.exportCreateTable(conn, schename, objName);
		}else if(type.equals(VIEW)) {
			drop = export.exportCreateView(conn, schename, objName);
		}else if(type.equals(FUNCTION)) {
			drop = export.exportCreateFunction(conn, schename, objName);
		}else if(type.equals(PROCEDURE)) {
			drop =  export.exportCreateProcedure(conn, schename, objName);
		}else if(type.equals(TRIGGER)) {
			drop =  export.exportCreateTrigger(conn, schename, objName);
		}else if(type.equals(INDEX)) {
			drop =  export.exportCreateIndex(conn, schename, objName);
		}else if(type.equals(SEQUENCE)) {
			drop =  export.exportCreateSequence(conn, schename, objName);
		}
		
		
		
		
		drop = drop.replaceAll(schename+"."+objName,  tarSchename+ "." + objName);
		return drop;
	}
		
	
	
	
	// 根据名称获取root 中的子节点
	private TreeItem<String> rootSubNode(String name){
		ObservableList<TreeItem<String> > childs = root.getChildren();
		for(int i = 0; i < childs.size(); i++) {
			TreeItem<String> val = childs.get(i);
			if( val.getValue().equals(name)) {
				return val;
			}
		}
		return null;
	}
	
	// 获取节点下选中的子节点
	private  ObservableList<CheckBoxTreeItem<String> > selectNode(TreeItem<String> node){
		ObservableList<CheckBoxTreeItem<String> > selectNodes = FXCollections.observableArrayList();
		ObservableList<TreeItem<String> > nodeSub = node.getChildren();
		for (int i = 0; i < nodeSub.size(); i++) {
			CheckBoxTreeItem<String> sub = (CheckBoxTreeItem<String>) nodeSub.get(i);
			if(sub.isSelected()) {
				selectNodes.add(sub);
			}
		}
		
		return selectNodes;
	}
	
	// 获取连接名称list
	private ObservableList<Label> getConnComboBoxList() {
		ComboBox<Label> connComboBox = ComponentGetter.connComboBox;
		ObservableList<Label> sos = connComboBox.getItems();
		ObservableList<Label> newVal = FXCollections.observableArrayList();
		for(Label label : sos) {
			Label la = new Label(label.getText());
			
			newVal.add(la);
		}
		return newVal;
	}
	
	// 获取schema名称列表
	private ObservableList<TreeItem<TreeNodePo>> getSchemaComboBoxList(String dbName) {
		 
	    ObservableList<TreeItem<TreeNodePo> > temp = FXCollections.observableArrayList();
	    ObservableList<TreeItem<TreeNodePo> > newVal = FXCollections.observableArrayList();
	    
	    TreeItem<TreeNodePo>  connNode = ComponentGetter.getConnNode(dbName);
	    if(connNode !=null) {
	    	if(connNode.getChildren().size() > 0) {
	    		temp = connNode.getChildren().get(0).getChildren();
		    	if(temp.size() > 0 ) {
	    			for(TreeItem<TreeNodePo> tnp : temp) {
	    				if(tnp.getChildren().size()> 0) {
	    					newVal.add(tnp);
	    				}
	    			}
	    		}
	    	}
	    	
	    }
		return newVal;
	}
	// 获取schema的下拉的数据
	private ObservableList<Label> getSchemaLabels(String dbName ) { 
		ObservableList<TreeItem<TreeNodePo>>  vals = getSchemaComboBoxList(dbName);
		ObservableList<Label> newVal = FXCollections.observableArrayList();
		newVal.add(new  Label(""));
		for(TreeItem<TreeNodePo> val : vals) {
			Label la = new Label(val.getValue().getName());
			
			newVal.add(la);
		}
		return newVal;
	}
	
	// 生成数据checkTreeView
	private void addNode(TreeItem<TreeNodePo> item) {
		CheckBoxTreeItem<String>  pi  = new CheckBoxTreeItem<>(item.getValue().getName());
		ObservableList<CheckBoxTreeItem<String>> newVal = FXCollections.observableArrayList();
		root.getChildren().add(pi);
		ObservableList<TreeItem<TreeNodePo>>  subItem = item.getChildren();
		
		for(TreeItem<TreeNodePo> subNode : subItem) {
			String name = subNode.getValue().getName();
			CheckBoxTreeItem<String> SubCbt  = new CheckBoxTreeItem<>(name);
			
			newVal.add(SubCbt);
		}
		pi.getChildren().addAll( newVal);
	}
	
	private void removeNode(boolean tf , String name) {
		if(tf) { 
			for(int i = 0; i < root.getChildren().size() ; i++) { 
				TreeItem<String> ch = root.getChildren().get(i);
				if(ch.getValue().equals(name)) {
					root.getChildren().remove(i);
					break;
				}
			}
		}
			
	}
	// 数据同步界面
	private  ChangeListener<Boolean> CheckListener(String nodeType) {
		return new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue && soDB.getValue() != null && soSC.getValue() !=null) {
					String dbname = soDB.getValue().getText();
					String schename = soSC.getValue().getText();
					if (StrUtils.isNotNullOrEmpty(dbname) && StrUtils.isNotNullOrEmpty(schename)) {
						TreeItem<TreeNodePo> schemaNode = ComponentGetter.getSchemaNode(dbname, schename);
						
						addNodeHelper(schemaNode, nodeType);
//						 TreeItem<TreeNodePo> val = getSourceNode(schemaNode, nodeType);
//						 if(val != null) {
//							 addNode(val);
//						 }
//						addNode(schemaNode.getChildren().get(position));
					}
				}
				removeNode(!newValue, nodeType);
			}
		};
		 
	}
	
	private void addNodeHelper(TreeItem<TreeNodePo> schemaNode, String nodeType) {
		 TreeItem<TreeNodePo> val = getSourceNode(schemaNode, nodeType);
		 if(val != null) {
			 addNode(val);
		 }
	}
	
	// 从schema中获取节点, 根据名称
	private TreeItem<TreeNodePo>  getSourceNode(TreeItem<TreeNodePo> schemaNode, String name) { 
			for(int i = 0; i < schemaNode.getChildren().size() ; i++) {
				TreeItem<TreeNodePo> ch = schemaNode.getChildren().get(i);
				if(ch.getValue().getName().equals(name)) {
					return ch; 
				}
			} 
			return null;
	}

	private ObservableList<TreeItem<String>> temp = FXCollections.observableArrayList();
	private ObservableList<CheckBoxTreeItem<String>>  filtList = FXCollections.observableArrayList();

	
	/*
	private CheckTreeView<String> checkTreeView;
	private CheckBoxTreeItem<String> root;
	private CheckBoxTreeItem<String> filterRoot;
	 */
	private void filterTxtInitialize() {
		filterTxt.textProperty().addListener((o, oldVal, newVal) -> {

			// 缓存
			ObservableList<TreeItem<String>> connNodes = root.getChildren();
			temp.clear();
			temp.addAll(connNodes);

			// 恢复
			if (StrUtils.isNullOrEmpty(newVal)) {
				if (root != null) {
					checkTreeView.setRoot(root);
					root.setExpanded(true);
				}
			}

			// 查询时
			if (StrUtils.isNotNullOrEmpty(newVal)) {
				filtList.clear();
				// 遍历每一个连接节点, 在节点下查找到了数据, 就会返回一个新节点对象, 最后使用新节点创建一个新的树
				for (int i = 0; i < temp.size(); i++) {
					CheckBoxTreeItem<String> connNode = (CheckBoxTreeItem<String>) temp.get(i);
					//查找
					CheckBoxTreeItem<String> nConnNode = connNodeOption(connNode, newVal);
					// 新节点不是NULL 缓存
					if (nConnNode != null) {
						filtList.add(nConnNode);
					}
				}
				// 创建一个新的树根, 将查询数据挂在新的上面
				CheckBoxTreeItem<String> rootNode = new CheckBoxTreeItem<String>("全选");
				rootNode.getChildren().addAll(filtList);
				checkTreeView.setRoot(rootNode); // 使用新的树根
				rootNode.setExpanded(true);
				for(int i = 0; i < rootNode.getChildren().size(); i++) {
					CheckBoxTreeItem<String> subNode = (CheckBoxTreeItem<String>) rootNode.getChildren().get(i);
					subNode.setExpanded(true);
				}
			}

		});
	}
	
	

	/*
	 * 传递连接节点, 对其进行过滤
	 * 如果节点包含查询内容就返回一个新的节点, 否则返回null
	 */
	private CheckBoxTreeItem<String>  connNodeOption(CheckBoxTreeItem<String> node, String queryStr) {
		// 1. 首先看节点是否激活的(有子节点?)
		if( node.getChildren().size() > 0) {   
			CheckBoxTreeItem<String> nnode	 = new  CheckBoxTreeItem<String>(node.getValue());	 
			int count = 0;
		    int sz =  0;    
			// 开始查找
			 ObservableList<CheckBoxTreeItem<String>>   val =  filter( node.getChildren() , queryStr);  
			 sz =  val.size();
			// 如果找到来数据, 将数据放入到新的数据对象中
			if (sz > 0) {
				nnode.getChildren().setAll(val); 
				count += val.size();
			} 		 
						 
			 // 如果找到了数据, 将新的数据对象, 放入schema数据对象
			 if(count > 0 ) {
				 return nnode;
			 }  
			 
		}
		
		return null;
	}
	private static ObservableList<CheckBoxTreeItem<String>> filter(ObservableList<TreeItem<String>> observableList, String str){
		ObservableList<CheckBoxTreeItem<String>> rs =  FXCollections.observableArrayList();
		String temp = str.toUpperCase();
		observableList.forEach(v ->{
			if(v.getValue().toUpperCase().contains(temp)) {
				rs.add((CheckBoxTreeItem<String>) v);
			}
		});
		return rs;
	}
}
