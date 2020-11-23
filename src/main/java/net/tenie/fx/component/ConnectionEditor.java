package net.tenie.fx.component;

import java.sql.Connection;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.PropertyPo.TreeNodePo;
import net.tenie.fx.component.container.ConnItem;
import net.tenie.fx.component.container.ConnItemParent;
import net.tenie.fx.component.container.DBinfoTree;
import net.tenie.fx.config.ConfigVal;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.config.DbVendor;
import net.tenie.fx.dao.ConnectionDao;
import net.tenie.fx.utility.EventAndListener.CommonListener;
import net.tenie.lib.db.h2.H2Db;
import net.tenie.lib.po.DbConnectionPo;
import net.tenie.lib.tools.StrUtils;

/*   @author tenie */
public class ConnectionEditor {
	public static Stage CreateModalWindow(VBox vb) {

		final Stage stage = new Stage();
		vb.getStyleClass().add("connectionEditor");

		Scene scene = new Scene(vb);
		vb.setPrefWidth(400);
		vb.maxWidth(400);
		AnchorPane bottomPane = new AnchorPane();
		bottomPane.setPadding(new Insets(10));

		vb.getChildren().add(bottomPane);
		KeyCodeCombination escbtn = new KeyCodeCombination(KeyCode.ESCAPE);
		KeyCodeCombination enterbtn = new KeyCodeCombination(KeyCode.ENTER);
		KeyCodeCombination spacebtn = new KeyCodeCombination(KeyCode.SPACE);
		scene.getAccelerators().put(escbtn, () -> {
			stage.close();
		});
		scene.getAccelerators().put(enterbtn, () -> {
			stage.close();
		});
		scene.getAccelerators().put(spacebtn, () -> {
			stage.close();
		});

		scene.getStylesheets().addAll(ConfigVal.cssList);

		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);

		stage.setMaximized(false);
		stage.setResizable(false);
		return stage;
	}

	public static void ConnectionInfoSetting(boolean isEdit, String connNameVal, String userVal, String passwordVal,
			String hostVal, String portVal, String dbDriverVal, String defaultSchemaVal, DbConnectionPo dp) {
		VBox vb = new VBox();
		Label title = new Label("Edit Connection Info");
		title.setPadding(new Insets(15));
		title.setGraphic(ImageViewGenerator.svgImage("gears", 20, "#AFB1B3"));
		vb.getChildren().add(title);

		GridPane grid = new GridPane();
		vb.getChildren().add(grid);
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
		connectionName.setPromptText(connNameStr);
		connectionName.setText(connNameVal);
		connectionName.lengthProperty().addListener(CommonListener.textFieldLimit(connectionName, 100));

		ChoiceBox<String> dbDriver = new ChoiceBox<String>(FXCollections.observableArrayList(DbVendor.getAll()));
		dbDriver.setTooltip(MyTooltipTool.instance("Select DB Type"));

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

		dbDriver.valueProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {

			if (newValue.equals("h2")) {
				lbhostStr.setText("DB File");
				host.setPromptText("DB File");
				port.setDisable(true);

				defaultSchema.setText("PUBLIC");
				defaultSchema.setDisable(true);
			} else {
				lbhostStr.setText(hostStr);
				host.setPromptText(hostStr);
				port.setDisable(false);

				defaultSchema.setText(defaultSchemaVal);
				defaultSchema.setDisable(false);
			}
		});

		if (StrUtils.isNotNullOrEmpty(dbDriverVal)) {
			Platform.runLater(() -> {
				dbDriver.getSelectionModel().select(dbDriverVal);
			});
		}

		int i = 0;
		int j = 0;

		grid.add(lbconnNameStr, 0, i++);
		grid.add(connectionName, 1, j++);

		grid.add(lbdbDriverStr, 0, i++);
		grid.add(dbDriver, 1, j++);

		grid.add(lbhostStr, 0, i++);
		grid.add(host, 1, j++);

		grid.add(lbportStr, 0, i++);
		grid.add(port, 1, j++);

		grid.add(lbdefaultSchemaStr, 0, i++);
		grid.add(defaultSchema, 1, j++);

		grid.add(lbuserStr, 0, i++);
		grid.add(user, 1, j++);

		grid.add(lbpasswordStr, 0, i++);
		grid.add(password, 1, j++);

		Function<String, DbConnectionPo> call = x -> {

			String connName = connectionName.getText();
			// check date
			if (StrUtils.isNullOrEmpty(connName)) {
				ModalDialog.errorAlert("Warn!", "connection name is empty !");
				return null;
			}
			if (StrUtils.isNullOrEmpty(dbDriver.getValue())) {
				ModalDialog.errorAlert("Warn!", "db Driver is empty !");
				return null;
			}
			if (StrUtils.isNullOrEmpty(host.getText())) {
				if (dbDriver.getValue().equals("h2")) {
					ModalDialog.errorAlert("Warn!", "DB File is empty !");
				} else {
					ModalDialog.errorAlert("Warn!", "host is empty !");
				}

				return null;
			}

			if (!dbDriver.getValue().equals("h2")) {
				if (StrUtils.isNullOrEmpty(port.getText())) {
					ModalDialog.errorAlert("Warn!", "port is empty !");
					return null;
				}
			}

			if (StrUtils.isNullOrEmpty(defaultSchema.getText())) {
				ModalDialog.errorAlert("Warn!", "Schema is empty !");
				return null;
			}

			if (StrUtils.isNullOrEmpty(user.getText())) {
				ModalDialog.errorAlert("Warn!", "user is empty !");
				return null;
			}
			if (StrUtils.isNullOrEmpty(password.getText())) {
				ModalDialog.errorAlert("Warn!", "password is empty !");
				return null;
			}
			if (StrUtils.isNullOrEmpty(connName)) {
				ModalDialog.errorAlert("Warn!", "connection name is empty !");
				return null;
			}
			if (!isEdit) {
				if (DBConns.conaction(connName)) {
					ModalDialog.errorAlert("Warn!", "connection name: " + connName + " is exist !");
					return null;
				}
			}

			// 连接信息保存

			DbConnectionPo connpo = new DbConnectionPo(connName, DbVendor.getDriver(dbDriver.getValue()),
					host.getText(), port.getText(), user.getText(), password.getText(), dbDriver.getValue(),
					defaultSchema.getText());
			if (dp != null) {
				dp.closeConn();
				connpo.setId(dp.getId());
			}
			return connpo;

		};

		Button testBtn = new Button("Test");
		grid.add(testBtn, 0, i);

		testBtn.setOnMouseClicked(e -> {
			testBtn.setStyle("-fx-background-color: red ");
			System.out.println("Test connection~~");
			DbConnectionPo connpo = call.apply("");
			if (connpo != null) {
				CommonAction.isAliveTestAlert(connpo, testBtn);
			}
		});

		Button saveBtn = new Button("Save");
		grid.add(saveBtn, 1, i);
		saveBtn.setOnMouseClicked(e -> {

			DbConnectionPo connpo = call.apply("");
			if (connpo != null) {
				// 先删除树中的节点
				if (dp != null) {
					DBConns.remove(dp.getConnName());
					DBinfoTree.rmTreeItemByName(dp.getConnName());
				}
				DBConns.add(connpo.getConnName(), connpo);
				// 缓存数据
				connpo = ConnectionDao.createOrUpdate(H2Db.getConn(), connpo);
				H2Db.closeConn();
				TreeItem<TreeNodePo> item = new TreeItem<>(
						new TreeNodePo(connectionName.getText(), ImageViewGenerator.svgImageUnactive("unlink")));
				DBinfoTree.treeRootAddItem(item);
			} else {
				return;
			}
			stage.close();

		});

		// 默认焦点
		Platform.runLater(() -> connectionName.requestFocus());

		stage.show();

	}

	// 连接设置界面
	public static void ConnectionInfoSetting() {
		ConnectionInfoSetting(false, "", "", "", "", "", "", "", null);
	}

	public static void ConnectionInfoSetting(DbConnectionPo dp) {
		ConnectionInfoSetting(true, dp.getConnName(), dp.getUser(), dp.getPassWord(), dp.getHost(), dp.getPort(),
				dp.getDbVendor(), dp.getDefaultSchema(), dp);
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
		try {
			System.out.println("deleteDbConn()");
			TreeView<TreeNodePo> treeView = ComponentGetter.treeView;
			TreeItem<TreeNodePo> rootNode = treeView.getRoot();
			ObservableList<TreeItem<TreeNodePo>> ls = rootNode.getChildren();

			// 获取选择的节点是不是 连接节点
			TreeItem<TreeNodePo> selectItem = treeView.getSelectionModel().getSelectedItem();
			Connection h2conn = H2Db.getConn();
			for (TreeItem<TreeNodePo> val : ls) {
				if (val.equals(selectItem)) {
					DbConnectionPo po = DBConns.get(val.getValue().getName()); // 找到连接对象
					if (po != null) {
						po.closeConn(); // 关闭它的连接 if(po.getId() !=null )
						ConnectionDao.delete(h2conn, po.getId()); // 删除连接对象在数据库中的数据
						DBConns.remove(po.getConnName());
						// 删除节点
						ls.remove(val);
						break;
					}
				}
			}
		} finally {
			H2Db.closeConn();
		}
	}

	public static void closeAllDbConn() {
		TreeView<TreeNodePo> treeView = ComponentGetter.treeView;
		TreeItem<TreeNodePo> rootNode = treeView.getRoot();
		ObservableList<TreeItem<TreeNodePo>> ls = rootNode.getChildren();
		for (TreeItem<TreeNodePo> val : ls) {
			closeDbConnHelper(val);
		}
		DBConns.flushChoiceBoxGraphic();
	}

	private static void closeDbConnHelper(TreeItem<TreeNodePo> val) {
		String str = val.getValue().getName();
		System.out.println(str);
		DbConnectionPo dp = DBConns.get(str);
		if (dp.isAlive()) {
			// 关闭连接
			dp.closeConn();
			// 修改颜色
			val.getValue().setIcon(ImageViewGenerator.svgImageUnactive("unlink"));
			// 删除子节点
			val.getChildren().remove(0, val.getChildren().size());

		}
	}

	public static void closeDbConn() {
		System.out.println("closeConnEvent()");
		if (DBinfoTree.currentTreeItemIsConnNode()) {
			TreeItem<TreeNodePo> val = DBinfoTree.getTrewViewCurrentItem();
			closeDbConnHelper(val);
		}
		DBConns.flushChoiceBoxGraphic();
	}

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
		item.getValue().setIcon(ImageViewGenerator.svgImage("spinner", "red"));
		ComponentGetter.treeView.refresh();

		Thread t = new Thread() {
			public void run() {
				DbConnectionPo po1 = null;
				try {
					System.out.println("backRunOpenConn()");
					String connName = item.getValue().getName();
					DbConnectionPo po = DBConns.get(connName);
					po1 = po;
					po.setConning(true);

					po.getConn();
					if (po.isAlive()) {
//						ConnItem ci = new ConnItem(po);
						ConnItemParent ci = new  ConnItemParent(po, item);
						ComponentGetter.dbInfoTree.getConnItemParent().add(ci);
						TreeItem<TreeNodePo> s = ci.getSchemaNode();
						Platform.runLater(() -> {
							item.getChildren().add(s);
							item.getValue().setConnItemParent(ci);
							item.getValue().setIcon(ImageViewGenerator.svgImage("link", "#7CFC00"));							
							ci.selectTable(po.getDefaultSchema());
						});
					} else {
						Platform.runLater(() -> {
							ModalDialog.errorAlert("Warn!",
									" Cannot connect ip:" + po.getHost() + " port:" + po.getPort() + "  !");
							item.getValue().setIcon(ImageViewGenerator.svgImageUnactive("unlink"));
							ComponentGetter.treeView.refresh();

						});

					}
				} finally {
					DBConns.flushChoiceBoxGraphic();
					po1.setConning(false);
				}

			}
		};
		t.start();
	}
}
