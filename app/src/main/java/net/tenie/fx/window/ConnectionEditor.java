package net.tenie.fx.window;

import java.sql.Connection;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXCheckBox;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyTooltipTool;
import net.tenie.Sqlucky.sdk.db.DBConns;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.db.SqluckyDbRegister;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;
import net.tenie.Sqlucky.sdk.po.TreeItemType;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.ui.SqluckyStage;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.FileTools;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonListener;
import net.tenie.fx.Po.TreeNodePo;
import net.tenie.fx.component.AppWindowComponentGetter;
import net.tenie.fx.component.InfoTree.DBinfoTree;
import net.tenie.fx.config.DbVendor;
import net.tenie.fx.dao.ConnectionDao;

/**
 * 
 * @author tenie
 *
 */
public class ConnectionEditor {
	// 编辑连接时记录连接状态
	public  static boolean editLinkStatus = false;
	private static Logger logger = LogManager.getLogger(ConnectionEditor.class);
	private static   Stage stage;
	public static Stage CreateModalWindow(VBox vb) {
//		stage = new Stage();
		vb.getStyleClass().add("connectionEditor");
		vb.setPrefWidth(450);
		vb.maxWidth(450);
		
//		Scene scene = new Scene(vb);
		SqluckyStage sqluckyStage = new SqluckyStage(vb);
		Scene scene = sqluckyStage.getScene();
		stage = sqluckyStage.getStage();
				
				
		
		AnchorPane bottomPane = new AnchorPane();
		bottomPane.setPadding(new Insets(10));

		vb.getChildren().add(bottomPane);
		KeyCodeCombination escbtn = new KeyCodeCombination(KeyCode.ESCAPE);
		KeyCodeCombination spacebtn = new KeyCodeCombination(KeyCode.SPACE);
		scene.getAccelerators().put(escbtn, () -> {
			stage.close();
		});
		scene.getAccelerators().put(spacebtn, () -> {
			stage.close();
		});

//		CommonUtility.loadCss(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		
//		stage.getIcons().add(ComponentGetter.LogoIcons);
		stage.setMaximized(false);
		stage.setResizable(false);
		stage.setOnHidden(e->{
			//TODO 打开连接
			if( editLinkStatus) {
				Platform.runLater(()->{
					var item = DBinfoTree.getTrewViewCurrentItem();
					CommonAction.openConn(item);
				});
			}
		});
		return stage;
	}

	public static void ConnectionInfoSetting(boolean isEdit, String connNameVal, String userVal, String passwordVal,
			String hostVal, String portVal, String dbDriverVal, String defaultSchemaVal,String dbName, SqluckyConnector dp) {


		String connNameStr = "Connection Name";
		String userStr = "User";
		String passwordStr = "Password";
		String hostStr = "Host or file";
		String portStr = "Port";
		String dbDriverStr = "DB Driver";
		String defaultSchemaStr = "Schema/DB Name";
		Label lbconnNameStr = new Label(connNameStr);
		Label lbdbDriverStr = new Label(dbDriverStr);
		Label lbhostStr = new Label(hostStr);
		Label lbportStr = new Label(portStr);
		Label lbdefaultSchemaStr = new Label(defaultSchemaStr);
		Label lbuserStr = new Label(userStr);
		Label lbpasswordStr = new Label(passwordStr);
		
		Label autoConnect = new Label("Auto Connect");
	    JFXCheckBox autoConnectCB  = new JFXCheckBox();
	    autoConnectCB.setSelected( dp == null ? false : dp.getAutoConnect());

		TextField connectionName = new TextField();
		connectionName.setPrefWidth(250);
		connectionName.setMinWidth(250);
		connectionName.setPromptText(connNameStr);
		connectionName.setText(connNameVal);
		connectionName.lengthProperty().addListener(CommonListener.textFieldLimit(connectionName, 100));

		ChoiceBox<String> dbDriver = new ChoiceBox<String>(FXCollections.observableArrayList(DbVendor.getAll()));
		dbDriver.setTooltip(MyTooltipTool.instance("Select DB Type"));
		dbDriver.setPrefWidth(250);
		dbDriver.setMinWidth(250);
		
		// jdbc url
		JFXCheckBox isUseJdbcUrl = new JFXCheckBox("Use JDBC URL"); 
	
		
		TextField jdbcUrl = new TextField();
		if(dp!= null && dp.isJdbcUrlUse()) { 
			Platform.runLater(()->{ 
				String jdbcUrlVal = dp.getJdbcUrl();  
				isUseJdbcUrl.setSelected(true);
				jdbcUrl.setText(jdbcUrlVal);
				 
			});
			
		}
		
		jdbcUrl.setPromptText("jdbc:db://ip:port/xxxx");
		jdbcUrl.disableProperty().bind(isUseJdbcUrl.selectedProperty().not());

		TextField host = new TextField();
		host.setPromptText(hostStr);
		host.setText(hostVal);
		host.lengthProperty().addListener(CommonListener.textFieldLimit(host, 100));
		host.disableProperty().bind(isUseJdbcUrl.selectedProperty());
		
		

		TextField port = new TextField();
		port.setPromptText(portStr);
		port.setText(portVal);
		port.lengthProperty().addListener(CommonListener.textFieldLimit(port, 5));
		port.textProperty().addListener(CommonListener.textFieldNumChange(port));
		

		TextField user = new TextField();
		user.setPromptText(userStr);
		user.setText(userVal);
		user.lengthProperty().addListener(CommonListener.textFieldLimit(user, 100));

		PasswordField password = new PasswordField();
		password.setPromptText(passwordStr);
		password.setText(passwordVal);
		password.lengthProperty().addListener(CommonListener.textFieldLimit(password, 50));
		
		

		TextField defaultSchema = new TextField();
		defaultSchema.setPromptText(defaultSchemaStr);
		defaultSchema.setText(defaultSchemaVal);
		defaultSchema.lengthProperty().addListener(CommonListener.textFieldLimit(defaultSchema, 50));
		
		// 获取db file
		Button h2FilePath = new Button("...");
		h2FilePath.setVisible(false);
		h2FilePath.disableProperty().bind(isUseJdbcUrl.selectedProperty());
		h2FilePath.setOnAction(e->{ 
			host.setText(FileTools.selectFile());
		});
		
		isUseJdbcUrl.selectedProperty().addListener( (obj, od, n) ->{
				if(n) {
					host.setText("");
					port.setText("");
				}else {
					jdbcUrl.setText("");
				}
		});
		//TODO 
		dbDriver.valueProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			
			var dbpo = DbVendor.register(dbDriver.getValue()); 
			if(dbpo.getMustUseJdbcUrl()) {
				isUseJdbcUrl.setSelected(true);
				isUseJdbcUrl.setDisable(true); 
			}else {
				isUseJdbcUrl.setDisable(false);
			}
			
			//schema/数据库实例名称, h2 ,sqlite 都是固定的
			String insNa = dbpo.getInstanceName();
			if(StrUtils.isNotNullOrEmpty(insNa ) ) {
				defaultSchema.setText(insNa);
				defaultSchema.setDisable(true);
			}else {
				defaultSchema.setText(defaultSchemaVal);
				defaultSchema.setDisable(false);
			}
			
			// isfile
			if( dbpo.getJdbcUrlIsFile() ) {
				port.disableProperty().unbind();
				port.setDisable(true);
				h2FilePath.setVisible(true);
			}else {
				port.disableProperty().bind(isUseJdbcUrl.selectedProperty());
				h2FilePath.setVisible(false);
			}
			
			if(dbpo.hasUser()) {
				user.setDisable(false);
				password.setDisable(false);
			}else {
				user.setDisable(true);
				password.setDisable(true);
			}
			

		});

		if (StrUtils.isNotNullOrEmpty(dbDriverVal)) {
			Platform.runLater(() -> {
				dbDriver.getSelectionModel().select(dbDriverVal);
			});
		}

		
		// 方法
		Function<String, SqluckyConnector> assembleSqlCon = x -> {
			SqluckyDbRegister dbRegister = DbVendor.register(dbDriver.getValue()); 
			String connName = connectionName.getText();
			// check date
			if (StrUtils.isNullOrEmpty(connName)) {
				MyAlert.errorAlert( "connection name is empty !");
				return null;
			}
			if (StrUtils.isNullOrEmpty(dbDriver.getValue())) {
				MyAlert.errorAlert( "db Driver is empty !");
				return null;
			}
			
			if(isUseJdbcUrl.isSelected()) { 
				if (StrUtils.isNullOrEmpty(jdbcUrl.getText())) { 
					MyAlert.errorAlert( "JDBC URL is empty !");
					return null;
				}
			}else {
				if (StrUtils.isNullOrEmpty(host.getText())) { 
					if (dbRegister.getJdbcUrlIsFile()) {
						MyAlert.errorAlert( "DB File is empty !");
					} else {
						MyAlert.errorAlert( "host is empty !");
					} 
					return null;
				} 
				
				if (! dbRegister.getJdbcUrlIsFile() ) {
					if (StrUtils.isNullOrEmpty(port.getText())) {
						MyAlert.errorAlert( "port is empty !");
						return null;
					}
				}
			}
			
			
			if (StrUtils.isNullOrEmpty(defaultSchema.getText())) {
				var textval = lbdefaultSchemaStr.getText();
				MyAlert.errorAlert( textval +" is empty !");
				return null;
			}
			
			if (StrUtils.isNullOrEmpty(connName)) {
				MyAlert.errorAlert( "connection name is empty !");
				return null;
			}
			if (!isEdit) {
				if (DBConns.conaction(connName)) {
					MyAlert.errorAlert( "connection name: " + connName + " is exist !");
					return null;
				}
			}

			//TODO 连接信息保存 DbVendor
			DBConnectorInfoPo connPo = new DBConnectorInfoPo(connName, DbVendor.getDriver(dbDriver.getValue()),
					host.getText(), port.getText(), user.getText(), password.getText(), dbDriver.getValue(),
					defaultSchema.getText(), defaultSchema.getText(), jdbcUrl.getText(), autoConnectCB.isSelected());
//			SqluckyDbRegister reg = DbVendor.register(dbDriver.getValue());
			SqluckyConnector sqluckyConnnector = dbRegister.createConnector(connPo);
			if (dp != null) {
				dp.closeConn();
				sqluckyConnnector.setId(dp.getId());
			}
			return sqluckyConnnector;

		};
//TODO
		Button testBtn = createTestBtn( assembleSqlCon );//new Button("Test");
		Button saveBtn = createSaveBtn( assembleSqlCon , connectionName ,  dp) ; // new Button("Save"); 
		
		layoutAndShow(   lbconnNameStr,   connectionName, 
					  lbdbDriverStr,   dbDriver,
					   isUseJdbcUrl,    jdbcUrl,
					  lbhostStr,       host, h2FilePath,
					  lbportStr,       port,
					  lbdefaultSchemaStr,   defaultSchema, 
					  lbuserStr,       user,
					  lbpasswordStr,   password, 
					  autoConnect,     autoConnectCB,
					  testBtn,         saveBtn );
	
		
	}

	// 连接设置界面
	public static void ConnectionInfoSetting() {
		ConnectionInfoSetting(false, "", "", "", "", "", "", "", "", null);
	}

	public static void ConnectionInfoSetting(SqluckyConnector dp) {
		if(dp != null)
			ConnectionInfoSetting(true, dp.getConnName(), dp.getUser(), dp.getPassWord(), dp.getHostOrFile(), dp.getPort(),
				dp.getDbVendor(), dp.getDefaultSchema(), dp.getDbName(), dp);
	}

	public static void editDbConn() { 
		if (DBinfoTree.currentTreeItemIsConnNode()) {
			TreeItem<TreeNodePo> val = DBinfoTree.getTrewViewCurrentItem();
			boolean tf = ConnectionEditor.treeItemIsLink(val);
			if(tf) {
				ConnectionEditor.closeDbConn();
				ConnectionEditor.editLinkStatus = true;
			}else {
				ConnectionEditor.editLinkStatus = false;
			}
			  
			String str = val.getValue().getName();
			SqluckyConnector dp = DBConns.get(str);
			ConnectionEditor.ConnectionInfoSetting(dp);
		}else {
			MyAlert.showNotifiaction("编辑需要选中连接名称!");
//			MyAlert.notification("Error", "编辑需要选中连接名称!", MyAlert.NotificationType.Error);
		}
	}

	public static void deleteDbConn() {

		SqluckyConnector po = null;
		TreeItem<TreeNodePo> treeNode = null;

		logger.info("deleteDbConn()");
		TreeView<TreeNodePo> treeView = AppWindowComponentGetter.treeView;
		TreeItem<TreeNodePo> rootNode = treeView.getRoot();
		ObservableList<TreeItem<TreeNodePo>> ls = rootNode.getChildren();

		// 获取选择的节点是不是 连接节点
		TreeItem<TreeNodePo> selectItem = treeView.getSelectionModel().getSelectedItem();

		for (TreeItem<TreeNodePo> val : ls) {
			if (val.equals(selectItem)) {
				po = DBConns.get(val.getValue().getName()); // 找到连接对象
				if (po != null) {
					treeNode = val;
					break;
				}
			}
		}
		final SqluckyConnector tmpPo = po;
		final TreeItem<TreeNodePo> tmpTreeNode = treeNode;
		Consumer<String> ok = x -> {
			Connection h2conn = SqluckyAppDB.getConn();
			try {
				if (tmpPo != null ) {
					tmpPo.closeConn(); // 关闭它的连接 if(po.getId() !=null )
					ConnectionDao.delete(h2conn, tmpPo.getId()); // 删除连接对象在数据库中的数据
					DBConns.remove(tmpPo.getConnName());
					// 删除节点
					ls.remove(tmpTreeNode);
				}
			} finally {
				SqluckyAppDB.closeConn(h2conn);
			}

		};
		// TODO
		if (tmpPo != null) {
			MyAlert.myConfirmation("Delete " + tmpPo.getConnName() + " ?", ok);
		}

	}

	public static void closeAllDbConn() {
		TreeView<TreeNodePo> treeView = AppWindowComponentGetter.treeView;
		TreeItem<TreeNodePo> rootNode = treeView.getRoot();
		ObservableList<TreeItem<TreeNodePo>> ls = rootNode.getChildren();
		for (TreeItem<TreeNodePo> val : ls) {
			closeDbConnHelper(val);
		}
		DBConns.flushChoiceBoxGraphic();
		AppWindowComponentGetter.treeView.refresh();
	}

	private static void closeDbConnHelper(TreeItem<TreeNodePo> val) {
		String str = val.getValue().getName();
		logger.info(str);
		SqluckyConnector dp = DBConns.get(str);
		if (dp != null ) {
			// 关闭连接
			dp.closeConn();
			// 修改颜色
			val.getValue().setIcon(IconGenerator.svgImageUnactive("unlink"));
			// 删除子节点
			val.getChildren().clear();

		}
	}
	
	// 判断节点是否连接状态
	public static boolean treeItemIsLink() {
		TreeItem<TreeNodePo> val = DBinfoTree.getTrewViewCurrentItem();
		val = getConnNodeRoot(val);
		return treeItemIsLink(val);
	}
	// 判断节点是否连接状态
	public static boolean treeItemIsLink(TreeItem<TreeNodePo> val) {
		String str = val.getValue().getName();
		logger.info(str);
		SqluckyConnector dp = DBConns.get(str);
		if (dp != null ) {
			if(dp.isAlive()) {
				return true;
			}
		}
		return false;
	}
	
	
	public  static TreeItem<TreeNodePo> getConnNodeRoot(TreeItem<TreeNodePo> val) {
		TreeNodePo tnp = val.getValue();
		if(tnp.getType() != null) {
			if( tnp.getType().equals(TreeItemType.CONNECT_INFO)) {
				return val;
			}
			return getConnNodeRoot(val.getParent());
		}else {
			return getConnNodeRoot(val.getParent());
		}  
		 
	}

	// 关闭链接
	public static void closeDbConn() {
		logger.info("closeConnEvent()");
		TreeItem<TreeNodePo> val = DBinfoTree.getTrewViewCurrentItem();
		val = getConnNodeRoot(val);
		closeDbConnHelper(val);
		DBConns.flushChoiceBoxGraphic();
		AppWindowComponentGetter.treeView.refresh();
	}
	// 打开连接按钮点击事件
	public static void openDbConn() {
		if (DBinfoTree.currentTreeItemIsConnNode()) {
			TreeItem<TreeNodePo> val = DBinfoTree.getTrewViewCurrentItem();
			CommonAction.openConn(val);
		}
	}

	// 打开连接
	public static void openConn(String name) {
		TreeItem<TreeNodePo> item = DBinfoTree.getTreeItemByName(name);
		CommonAction.openConn(item);
	}

	


	
	
	public static Button createTestBtn(Function<String, SqluckyConnector> assembleSqlCon ) {
		Button testBtn = new Button("Test"); 
		testBtn.setOnMouseClicked(e -> {
			testBtn.setStyle("-fx-background-color: red ");
			logger.info("Test connection~~");
			SqluckyConnector connpo = assembleSqlCon.apply("");
			if (connpo != null) {
				CommonAction.isAliveTestAlert(connpo, testBtn);
			}
		});
		return testBtn;
	}
	
	public static Button createSaveBtn(Function<String, SqluckyConnector> assembleSqlCon , TextField connectionName ,SqluckyConnector dp ) {
		Button saveBtn = new Button("Save");
		saveBtn.setOnMouseClicked(e -> {
			SqluckyConnector connpo = assembleSqlCon.apply("");
			var conn = SqluckyAppDB.getConn();
			TreeItem<TreeNodePo> item ;
			if (connpo != null) {  
				// 先删除树中的节点
				if (dp != null) {
					DBConns.remove(dp.getConnName());
					item = DBinfoTree.getTrewViewCurrentItem();
					item.getValue().setName(connectionName.getText() );
					AppWindowComponentGetter.treeView.refresh();
				}else {
					TreeNodePo tnpo = new TreeNodePo(connectionName.getText(), IconGenerator.svgImageUnactive("unlink"));
					tnpo.setType(TreeItemType.CONNECT_INFO);
					item = new TreeItem<>(tnpo );
					DBinfoTree.treeRootAddItem(item); 
				}
			
				// 缓存数据
				DBConns.add(connpo.getConnName(), connpo);
				connpo = ConnectionDao.createOrUpdate(conn, connpo);
				SqluckyAppDB.closeConn(conn );
			} else {
				return;
			}
			stage.close();

		});
		return saveBtn;
	}
	// 组件布局
	public static void layoutAndShow( 
			Control lbconnNameStr, Control connectionName, 
			Control lbdbDriverStr, Control dbDriver,
			Control isUseJU, Control jdbcUrl,
			Control lbhostStr, Control host,  Control h2FilePath,
			Control lbportStr, Control port,
			Control lbdefaultSchemaStr, Control defaultSchema, 
			Control lbuserStr, Control user,
			Control lbpasswordStr, Control password, 
			Control autoConnect, Control autoConnectCB,
			Control testBtn, Control saveBtn
			
			) {
		VBox vb = new VBox();
		Label title = new Label("Edit Connection Info");
		title.setPadding(new Insets(15));
		title.setGraphic(IconGenerator.svgImageDefActive("gears"));
		vb.getChildren().add(title);

		GridPane grid = new GridPane();
		vb.getChildren().add(grid);
		vb.setPadding( new Insets(5));
		Stage stage = CreateModalWindow(vb);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 10, 10, 10));
		
		int i = 0;
		int j = 0;
//	 
		grid.add(lbconnNameStr, 0, i++);
		grid.add(connectionName, 1, j++);

		grid.add(lbdbDriverStr, 0, i++);
		grid.add(dbDriver, 1, j++);
		
		grid.add(isUseJU, 0, i++);
		grid.add(jdbcUrl, 1, j++);

		grid.add(lbhostStr, 0, i++);
		int tmp = j++;
		grid.add(host, 1, tmp);
		grid.add(h2FilePath, 2, tmp);
		

		grid.add(lbportStr, 0, i++);
		grid.add(port, 1, j++);

		grid.add(lbdefaultSchemaStr, 0, i++);
		grid.add(defaultSchema, 1, j++);

		grid.add(lbuserStr, 0, i++);
		grid.add(user, 1, j++);

		grid.add(lbpasswordStr, 0, i++);
		grid.add(password, 1, j++);
		
		grid.add(autoConnect, 0, i++);
		grid.add(autoConnectCB, 1, j++);
		
		
		grid.add(testBtn, 0, i); 
		grid.add(saveBtn, 1, i);
		// 默认焦点
		Platform.runLater(() -> connectionName.requestFocus());
		stage.show();

	}
	
}
