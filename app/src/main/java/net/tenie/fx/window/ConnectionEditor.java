package net.tenie.fx.window;

import java.io.File;
import java.sql.Connection;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonListener;
import net.tenie.fx.PropertyPo.DbConnectionPo;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.AppWindowComponentGetter;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.fx.component.MyTooltipTool;
import net.tenie.fx.component.TreeItem.ConnItemContainer;
import net.tenie.fx.component.container.DBinfoTree;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.config.DbVendor;
import net.tenie.fx.dao.ConnectionDao;
import net.tenie.fx.main.Restart;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.lib.db.h2.H2Db;
import net.tenie.lib.tools.IconGenerator;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/*   @author tenie */
public class ConnectionEditor {
	private static Logger logger = LogManager.getLogger(ConnectionEditor.class);
	public static Stage CreateModalWindow(VBox vb) {

		final Stage stage = new Stage();
		vb.getStyleClass().add("connectionEditor");

		Scene scene = new Scene(vb);
		
		vb.setPrefWidth(450);
		vb.maxWidth(450);
		AnchorPane bottomPane = new AnchorPane();
		bottomPane.setPadding(new Insets(10));

		vb.getChildren().add(bottomPane);
		KeyCodeCombination escbtn = new KeyCodeCombination(KeyCode.ESCAPE);
//		KeyCodeCombination enterbtn = new KeyCodeCombination(KeyCode.ENTER);
		KeyCodeCombination spacebtn = new KeyCodeCombination(KeyCode.SPACE);
		scene.getAccelerators().put(escbtn, () -> {
			stage.close();
		});
//		scene.getAccelerators().put(enterbtn, () -> {
//			stage.close();
//		});
		scene.getAccelerators().put(spacebtn, () -> {
			stage.close();
		});

		CommonUtility.loadCss(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		
		stage.getIcons().add(new Image(ConnectionEditor.class.getResourceAsStream(ConfigVal.appIcon)));
		stage.setMaximized(false);
		stage.setResizable(false);
		return stage;
	}

	public static void ConnectionInfoSetting(boolean isEdit, String connNameVal, String userVal, String passwordVal,
			String hostVal, String portVal, String dbDriverVal, String defaultSchemaVal,String dbName, DbConnectionPo dp) {
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

		String connNameStr = "Connection Name";
		String userStr = "User";
		String passwordStr = "Password";
		String hostStr = "Host";
		String portStr = "Port";
		String dbDriverStr = "DB Driver";
		String defaultSchemaStr = "Schema";
		Label lbconnNameStr = new Label(connNameStr);
		Label lbdbDriverStr = new Label(dbDriverStr);
		Label lbhostStr = new Label(hostStr);
		Label lbportStr = new Label(portStr);
		Label lbdefaultSchemaStr = new Label(defaultSchemaStr);
		Label lbuserStr = new Label(userStr);
		Label lbpasswordStr = new Label(passwordStr);

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

		TextField host = new TextField();
		host.setPromptText(hostStr);
		host.setText(hostVal);
		host.lengthProperty().addListener(CommonListener.textFieldLimit(host, 100));
		
		
		

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
		h2FilePath.setOnAction(e->{
			String fp = "";
			File f = FileOrDirectoryChooser.showOpenAllFile("Open", new Stage());
			if (f != null) { 
			    fp = f.getAbsolutePath();
			}
			if (dbDriver.getValue().equals(DbVendor.h2)  ) {
				if(fp.endsWith(".mv.db"))
					fp= fp.substring(0, fp.lastIndexOf(".mv.db"));
				if(fp.endsWith(".trace.db"))
					fp= fp.substring(0, fp.lastIndexOf(".trace.db"));
				if(fp.endsWith(".db"))
					fp= fp.substring(0, fp.lastIndexOf(".db"));
				
			}
			host.setText(fp);
			
				
		});
		
		
		
		dbDriver.valueProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {

		h2FilePath.setVisible(false);
		
		lbhostStr.setText(hostStr);
		host.setPromptText(hostStr);
		port.setDisable(false);

		defaultSchema.setText(defaultSchemaVal);
		defaultSchema.setDisable(false);
			if (newValue.equals(DbVendor.h2) ||newValue.equals(DbVendor.sqlite) ) {
				lbhostStr.setText("DB File");
				host.setPromptText("DB File");
				port.setDisable(true);

				defaultSchema.setText("PUBLIC");
				defaultSchema.setDisable(true);
				h2FilePath.setVisible(true);
				
			}else if( newValue.equals(DbVendor.postgresql)) {
				lbdefaultSchemaStr.setText("DB Name");
				defaultSchema.setPromptText("DB Name");
				defaultSchema.setText( dbName); 
				
			}
			
			
			if (newValue.equals(DbVendor.sqlite) ) {
				user.setDisable(true);
				password.setDisable(true);
			}else {
				user.setDisable(false);
				password.setDisable(false);
			}
			
		});

		if (StrUtils.isNotNullOrEmpty(dbDriverVal)) {
			Platform.runLater(() -> {
				dbDriver.getSelectionModel().select(dbDriverVal);
			});
		}

		
		// 方法
		Function<String, DbConnectionPo> call = x -> { 
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
			if (StrUtils.isNullOrEmpty(host.getText())) {
				if (dbDriver.getValue().equals(DbVendor.h2) || dbDriver.getValue().equals(DbVendor.sqlite)) {
					MyAlert.errorAlert( "DB File is empty !");
				} else {
					MyAlert.errorAlert( "host is empty !");
				} 
				return null;
			} 
			if (!dbDriver.getValue().equals(DbVendor.h2) && !dbDriver.getValue().equals(DbVendor.sqlite) ) {
				if (StrUtils.isNullOrEmpty(port.getText())) {
					MyAlert.errorAlert( "port is empty !");
					return null;
				}
			}
			
			if (StrUtils.isNullOrEmpty(defaultSchema.getText())) {
				var textval = lbdefaultSchemaStr.getText();
				MyAlert.errorAlert( textval +" is empty !");
				return null;
			}
			
			if ( ! dbDriver.getValue().equals(DbVendor.sqlite) ) {
				if (StrUtils.isNullOrEmpty(user.getText())) {
					MyAlert.errorAlert( "user is empty !");
					return null;
				}
				if (StrUtils.isNullOrEmpty(password.getText())) {
					MyAlert.errorAlert( "password is empty !");
					return null;
				}
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

			// 连接信息保存
			DbConnectionPo connpo = new DbConnectionPo(connName, DbVendor.getDriver(dbDriver.getValue()),
					host.getText(), port.getText(), user.getText(), password.getText(), dbDriver.getValue(),
					defaultSchema.getText(), defaultSchema.getText());
			if (dp != null) {
				dp.closeConn();
				connpo.setId(dp.getId());
			}
			return connpo;

		};
//TODO
		Button testBtn = createTestBtn( call );//new Button("Test");
		Button saveBtn = createSaveBtn( call , connectionName ,  dp, stage) ; // new Button("Save"); 
		
		layout( grid, lbconnNameStr,   connectionName, 
					  lbdbDriverStr,   dbDriver,
					  lbhostStr,       host, h2FilePath,
					  lbportStr,       port,
					  lbdefaultSchemaStr,   defaultSchema, 
					  lbuserStr,       user,
					  lbpasswordStr,   password, 
					  testBtn,         saveBtn );
	
		// 默认焦点
		Platform.runLater(() -> connectionName.requestFocus());
//		ModalDialog.windowShell(stage, ModalDialog.INFO);
		stage.show();

	}

	// 连接设置界面
	public static void ConnectionInfoSetting() {
		ConnectionInfoSetting(false, "", "", "", "", "", "", "", "", null);
	}

	public static void ConnectionInfoSetting(DbConnectionPo dp) {
		if(dp != null)
			ConnectionInfoSetting(true, dp.getConnName(), dp.getUser(), dp.getPassWord(), dp.getHost(), dp.getPort(),
				dp.getDbVendor(), dp.getDefaultSchema(), dp.getDbName(), dp);
	}

	public static void editDbConn() {
		if (DBinfoTree.currentTreeItemIsConnNode()) {
			TreeItem<TreeNodePo> val = DBinfoTree.getTrewViewCurrentItem();
			String str = val.getValue().getName();
			DbConnectionPo dp = DBConns.get(str);
			ConnectionEditor.ConnectionInfoSetting(dp);
			 
		}
	}

	public static void deleteDbConn() {

		DbConnectionPo po = null;
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
		final DbConnectionPo tmpPo = po;
		final TreeItem<TreeNodePo> tmpTreeNode = treeNode;
		Consumer<String> ok = x -> {
			try {
				if (tmpPo != null ) {
					Connection h2conn = H2Db.getConn();
					tmpPo.closeConn(); // 关闭它的连接 if(po.getId() !=null )
					ConnectionDao.delete(h2conn, tmpPo.getId()); // 删除连接对象在数据库中的数据
					DBConns.remove(tmpPo.getConnName());
					// 删除节点
					ls.remove(tmpTreeNode);
				}
			} finally {
				H2Db.closeConn();
			}

		};
		// TODO
		if (tmpPo != null) {
			MyAlert.myConfirmation("OK to delete " + tmpPo.getConnName() + " ?", ok);
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
		DbConnectionPo dp = DBConns.get(str);
		if (dp != null ) {
			// 关闭连接
			dp.closeConn();
			// 修改颜色
			val.getValue().setIcon(IconGenerator.svgImageUnactive("unlink"));
			// 删除子节点
//			val.getChildren().remove(0, val.getChildren().size());
			val.getChildren().clear();

		}
	}

	public static void closeDbConn() {
		logger.info("closeConnEvent()");
		if (DBinfoTree.currentTreeItemIsConnNode()) {
			TreeItem<TreeNodePo> val = DBinfoTree.getTrewViewCurrentItem();
			closeDbConnHelper(val);
		}
		DBConns.flushChoiceBoxGraphic();
		AppWindowComponentGetter.treeView.refresh();
	}
	// 打开连接按钮点击事件
	public static void openDbConn() {
		if (DBinfoTree.currentTreeItemIsConnNode()) {
			TreeItem<TreeNodePo> val = DBinfoTree.getTrewViewCurrentItem();
			openConn(val);
		}
	}

	// 打开连接
	public static void openConn(String name) {
		TreeItem<TreeNodePo> item = DBinfoTree.getTreeItemByName(name);
		openConn(item);
	}

	public static void openConn(TreeItem<TreeNodePo> item) {
		// 判断 节点是否已经有子节点
		if (item.getChildren().size() == 0) {
			backRunOpenConn(item);
		}
	}

	// 子线程打开db连接backRunOpenConn
	public static void backRunOpenConn(TreeItem<TreeNodePo> item) { 
		Node nd = IconGenerator.svgImage("spinner", "red");
		CommonUtility.rotateTransition(nd);
		item.getValue().setIcon( nd);
		AppWindowComponentGetter.treeView.refresh();

		Thread t = new Thread() {
			public void run() {
				DbConnectionPo po1 = null;
				try {
					 
					logger.info("backRunOpenConn()");
					String connName = item.getValue().getName();
					DbConnectionPo po = DBConns.get(connName);
					po1 = po;
					po.setConning(true);

					po.getConn();
					if (po.isAlive()) {
						ConnItemContainer connItemContainer = new  ConnItemContainer(po, item);
						TreeItem<TreeNodePo> s = connItemContainer.getSchemaNode();
						Platform.runLater(() -> {
							item.getChildren().add(s);
							item.getValue().setIcon(IconGenerator.svgImage("link", "#7CFC00"));							
							connItemContainer.selectTable(po.getDefaultSchema());
							DBConns.flushChoiceBox(connName);
						});
					} else {
						Platform.runLater(() -> {
							MyAlert.errorAlert( " Cannot connect ip:" + po.getHost() + " port:" + po.getPort() + "  !");
							item.getValue().setIcon(IconGenerator.svgImageUnactive("unlink"));
							AppWindowComponentGetter.treeView.refresh();

						});

					}
				} catch (Exception e) { 
					e.printStackTrace();
					logger.debug(e.getMessage());
					Platform.runLater(() -> {
						MyAlert.errorAlert( " Error !");
						item.getValue().setIcon(IconGenerator.svgImage("unlink", "red"));
						AppWindowComponentGetter.treeView.refresh();
					});
					
				} finally {
					DBConns.flushChoiceBoxGraphic();
					po1.setConning(false);
				}

			}
		};
		t.start();
	}
	
	
	public static Button createTestBtn(Function<String, DbConnectionPo> call ) {
		Button testBtn = new Button("Test"); 
		testBtn.setOnMouseClicked(e -> {
			testBtn.setStyle("-fx-background-color: red ");
			logger.info("Test connection~~");
			DbConnectionPo connpo = call.apply("");
			if (connpo != null) {
				CommonAction.isAliveTestAlert(connpo, testBtn);
			}
		});
		return testBtn;
	}
	
	public static Button createSaveBtn(Function<String, DbConnectionPo> call , TextField connectionName ,DbConnectionPo dp, Stage stage) {
		Button saveBtn = new Button("Save");
		saveBtn.setOnMouseClicked(e -> {
			DbConnectionPo connpo = call.apply("");
			if (connpo != null) {  
				// 先删除树中的节点
				if (dp != null) {
					DBConns.remove(dp.getConnName());
//					DBinfoTree.rmTreeItemByName(dp.getConnName());
					TreeItem<TreeNodePo> val = DBinfoTree.getTrewViewCurrentItem();
					val.getValue().setName(connectionName.getText() );
					AppWindowComponentGetter.treeView.refresh();
				}else {
					TreeItem<TreeNodePo> item = new TreeItem<>(
							new TreeNodePo(connectionName.getText(), IconGenerator.svgImageUnactive("unlink")));
					DBinfoTree.treeRootAddItem(item); 
				}
			
				// 缓存数据
				DBConns.add(connpo.getConnName(), connpo);
				connpo = ConnectionDao.createOrUpdate(H2Db.getConn(), connpo);
				H2Db.closeConn();
			} else {
				return;
			}
			stage.close();

		});
		return saveBtn;
	}
	//TODO
	public static void layout(GridPane grid  , 
			Control lbconnNameStr, Control connectionName, 
			Control lbdbDriverStr, Control dbDriver,
			Control lbhostStr, Control host,  Control h2FilePath,
			Control lbportStr, Control port,
			Control lbdefaultSchemaStr, Control defaultSchema, 
			Control lbuserStr, Control user,
			Control lbpasswordStr, Control password, 
			Control testBtn, Control saveBtn
			) {
		int i = 0;
		int j = 0;
//	 
		grid.add(lbconnNameStr, 0, i++);
		grid.add(connectionName, 1, j++);

		grid.add(lbdbDriverStr, 0, i++);
		grid.add(dbDriver, 1, j++);

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
		
		grid.add(testBtn, 0, i); 
		grid.add(saveBtn, 1, i);
	}
	
}