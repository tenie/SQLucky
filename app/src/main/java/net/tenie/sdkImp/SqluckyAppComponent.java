package net.tenie.sdkImp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.jfoenix.controls.JFXButton;

import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheet;
import net.tenie.Sqlucky.sdk.SqluckyTab;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyBottomSheet;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.db.SqluckyDbRegister;
import net.tenie.Sqlucky.sdk.po.DBConnectorInfoPo;
import net.tenie.Sqlucky.sdk.po.DBNodeInfoPo;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.po.SheetDataValue;
import net.tenie.Sqlucky.sdk.po.TreeItemType;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.fx.Po.TreeNodePo;
import net.tenie.fx.component.MyAreaTab;
import net.tenie.fx.component.CodeArea.HighLightingCodeArea;
import net.tenie.fx.component.InfoTree.DBinfoTree;
import net.tenie.fx.component.dataView.BottomSheetOptionBtnsPane;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.config.DbVendor;
import net.tenie.fx.dao.ConnectionDao;
import net.tenie.fx.dao.DmlDdlDao;
import net.tenie.fx.window.SignInWindow;
import net.tenie.lib.db.h2.AppDao;

public class SqluckyAppComponent implements AppComponent {
	private Consumer<String> dbInfoMenuOnShowingCaller;

	@Override
	public void addTitledPane(TitledPane tp) {
		Accordion ad = ComponentGetter.infoAccordion;
		if (ad != null) {
			ad.getPanes().add(tp);
		}
	}

	@Override
	public void addIconBySvg(String name, String svg) {
		IconGenerator.addSvgStr(name, svg);
	}

	@Override
	public SqluckyTab sqluckyTab() {
		return new MyAreaTab(false);
	}

	@Override
	public SqluckyTab sqluckyTab(DocumentPo po) {
		return new MyAreaTab(po, false);
	}

	/**
	 * 获取图标
	 */
	@Override
	public Region getIconUnactive(String name) {
		return IconGenerator.svgImageUnactive(name);
	}

	/**
	 * 获取图标
	 */
	@Override
	public Region getIconDefActive(String name) {
		return IconGenerator.svgImageDefActive(name);
	}

	/**
	 * 保持插件存储的key_value
	 */
	@Override
	public void saveData(String name, String key, String value) {
		var conn = SqluckyAppDB.getConn();
		try {
			StringBuilder strb = new StringBuilder();
			strb.append(name);
			strb.append("-");
			strb.append(key);
			AppDao.saveConfig(conn, strb.toString(), value);
		} finally {
			SqluckyAppDB.closeConn(conn);
		}
	}

	/**
	 * 获取插件存储的key_value
	 */
	@Override
	public String fetchData(String name, String key) {
		String val = "";
		var conn = SqluckyAppDB.getConn();
		try {
			StringBuilder strb = new StringBuilder();
			strb.append(name);
			strb.append("-");
			strb.append(key);
			val = AppDao.readConfig(conn, strb.toString());
		} finally {
			SqluckyAppDB.closeConn(conn);
		}

		return val;
	}

	@Override
	public void tabPaneRemoveSqluckyTab(SqluckyTab stb) {
		var myTabPane = ComponentGetter.mainTabPane;
		Tab tb = (Tab) stb;
		if (myTabPane.getTabs().contains(tb)) {
			myTabPane.getTabs().remove(tb);
		}

	}

	@Override
	public void registerDBConnector(SqluckyDbRegister ctr) {
		DbVendor.registerDbConnection(ctr);

	}

	@Override
	public boolean execDML(String sql) {
		boolean succeed = false;
		var conn = SqluckyAppDB.getConn();
		try {
			DmlDdlDao.execDML(conn, sql);
			succeed = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return succeed;
	}

	// 创建sql查询结果数据tableview
	@Override
	public SqluckyBottomSheet sqlDataSheet(SheetDataValue data, int idx, boolean disable) {
		MyBottomSheet rs = new MyBottomSheet(data, idx, disable);
		String time = data.getExecTime() == 0 ? "0" : data.getExecTime() + "";
		String rows = data.getRows() == 0 ? "0" : data.getRows() + "";

		VBox vbox = new VBox();
		List<Node> btnLs = BottomSheetOptionBtnsPane.sqlDataOptionBtns(rs, disable);
		AnchorPane dtBtnPane = new BottomSheetOptionBtnsPane(btnLs, time, rows, data.getConnName());
		// 添加按钮面板和 数据表格
		vbox.getChildren().add(dtBtnPane);
		vbox.getChildren().add(data.getTable());
		VBox.setVgrow(data.getTable(), Priority.ALWAYS);

		rs.getTab().setContent(vbox);

		return rs;
	}

	// 执行ddl 或查询报错的 页面
	public SqluckyBottomSheet infoSheet(SheetDataValue data, int idx, boolean disable) {
		MyBottomSheet rs = new MyBottomSheet(data, idx, disable);
		String time = data.getExecTime() == 0 ? "0" : data.getExecTime() + "";
		String rows = data.getRows() == 0 ? "0" : data.getRows() + "";

		VBox vbox = new VBox();
		List<Node> btnLs = BottomSheetOptionBtnsPane.infoOptionBtns(rs, disable);
		AnchorPane dtBtnPane = new BottomSheetOptionBtnsPane(btnLs, time, rows, data.getConnName());
		// 添加按钮面板和 数据表格
		vbox.getChildren().add(dtBtnPane);
		vbox.getChildren().add(data.getTable());
		VBox.setVgrow(data.getTable(), Priority.ALWAYS);

		rs.getTab().setContent(vbox);

		return rs;
	}

	/**
	 * 
	 */
	@Override
	public SqluckyBottomSheet tableViewSheet(SheetDataValue data, List<Node> btnLs) {
		var rs = new MyBottomSheet(data);

		VBox vbox = new VBox();
		JFXButton LockBtn = SdkComponent.createLockBtn(rs);
		btnLs.add(0, LockBtn);
		AnchorPane dtBtnPane = new BottomSheetOptionBtnsPane(btnLs, "");
		// 添加按钮面板和 数据表格
		vbox.getChildren().add(dtBtnPane);
		vbox.getChildren().add(data.getTable());
		VBox.setVgrow(data.getTable(), Priority.ALWAYS);

		rs.getTab().setContent(vbox);

		return rs;
	}

	/**
	 * 表, 视图 等 数据库对象的ddl语句
	 * 
	 * @param sqluckyConn
	 * @param name
	 * @param ddl
	 * @param isRunFunc   是否显示 运行函数按钮, 如函数, 过程, 需要运行的, true显示运行按钮
	 * @param isSelect    是否显示 查询按钮, 如table view 可以select数据的, true显示select按钮
	 * @return
	 */
	@Override
	public SqluckyBottomSheet ddlSheet(SqluckyConnector sqluckyConn, String name, String ddl, boolean isRunFunc,
			boolean isSelect) {
		var mtb = new MyBottomSheet(name);
		mtb.setDDL(true);
		HighLightingCodeArea sqlArea = new HighLightingCodeArea(null, null);
		mtb.setSqlArea(sqlArea);
		VBox box = DDLBox(sqluckyConn, mtb, ddl, isRunFunc, false, name, isSelect);
		mtb.getTab().setContent(box);
		return mtb;
	}

	@Override
	public SqluckyBottomSheet ProcedureSheet(SqluckyConnector sqluckyConn, String name, String ddl, boolean isRunFunc) {
		var mtb = new MyBottomSheet(name);
		mtb.setDDL(true);
		HighLightingCodeArea sqlArea = new HighLightingCodeArea(null, null);
		mtb.setSqlArea(sqlArea);
		VBox box = DDLBox(sqluckyConn, mtb, ddl, isRunFunc, true, name, false);
		mtb.getTab().setContent(box);
		return mtb;
	}

	@Override
	public SqluckyBottomSheet EmptySheet(SqluckyConnector sqluckyConn, String name, String message) {
		var mtb = new MyBottomSheet(name);
		mtb.setDDL(true);
		HighLightingCodeArea sqlArea = new HighLightingCodeArea(null, null);
		mtb.setSqlArea(sqlArea);
		VBox box = DDLBox(sqluckyConn, mtb, message, false, false, name, false);
		mtb.getTab().setContent(box);
		return mtb;
	}

	// 数据tab中的组件
	public static VBox DDLBox(SqluckyConnector sqluckyConn, MyBottomSheet mtb, String ddl, boolean isRunFunc,
			boolean isProc, String name, boolean isSelect) {
		VBox vb = new VBox();

		StackPane sp = mtb.getSqlArea().getCodeAreaPane(ddl, false);
		// 表格上面的按钮
		List<Node> btnLs = BottomSheetOptionBtnsPane.DDLOptionBtns(sqluckyConn, mtb, ddl, isRunFunc, isProc, name,
				isSelect, vb, sp, null);
		AnchorPane fp = new BottomSheetOptionBtnsPane(btnLs, sqluckyConn.getConnName());
		// isProc, name);
		vb.getChildren().add(fp);
		vb.getChildren().add(sp);
		VBox.setVgrow(sp, Priority.ALWAYS);
		return vb;
	}

	// 数据tab中的组件
	public static VBox tableInfoBox(SqluckyConnector sqluckyConn, MyBottomSheet mtb, TablePo table) {
		VBox vb = new VBox();
		String ddl = table.getDdl();
		StackPane sp = mtb.getSqlArea().getCodeAreaPane(ddl, false);
		// 表格上面的按钮
		List<Node> btnLs = BottomSheetOptionBtnsPane.DDLOptionBtns(sqluckyConn, mtb, ddl, false, false,
				table.getTableName(), true, vb, sp, table);
		AnchorPane fp = new BottomSheetOptionBtnsPane(btnLs, sqluckyConn.getConnName());
		// isProc, name);
		vb.getChildren().add(fp);
		vb.getChildren().add(sp);
		VBox.setVgrow(sp, Priority.ALWAYS);
		return vb;
	}

	// 注册db节点的右键菜单
	@Override
	public void registerDBInfoMenu(List<Menu> otherDBMenu, List<MenuItem> otherDBMenuItem) {
		var contextMenu = ComponentGetter.dbInfoTreeContextMenu;
		if (otherDBMenu != null && otherDBMenu.size() > 0) {
			contextMenu.getItems().add(new SeparatorMenuItem());
			for (var mn : otherDBMenu) {
				contextMenu.getItems().add(mn);
			}
		}
		if (otherDBMenuItem != null && otherDBMenuItem.size() > 0) {
			contextMenu.getItems().add(new SeparatorMenuItem());
			for (var mnitem : otherDBMenuItem) {
				contextMenu.getItems().add(mnitem);
			}
		}

	}

	/**
	 * 获取选中的 dbInfo 节点的类型(表格, 视图, 等)
	 */
	@Override
	public TreeItemType currentDBInfoNodeType() {
		TreeItem<TreeNodePo> item = DBinfoTree.DBinfoTreeView.getSelectionModel().getSelectedItem();
		TreeNodePo np = item.getValue();
		return np.getType();
	}

	@Override
	public DBNodeInfoPo currentDBInfoNode() {
		TreeItem<TreeNodePo> item = DBinfoTree.DBinfoTreeView.getSelectionModel().getSelectedItem();
		TreeNodePo np = item.getValue();
		return np.getDbNodeInfoPo();
	}

	@Override
	public void setDBInfoMenuOnShowing(Consumer<String> caller) {
		dbInfoMenuOnShowingCaller = caller;

	}

	@Override
	public Consumer<String> getDBInfoMenuOnShowing() {
		return dbInfoMenuOnShowingCaller;
	}

	@Override
	public Map<String, SqluckyConnector> getAllConnector() {
		return DBConns.getDbs();
	}

	/**
	 * 获取链接名称
	 */
	@Override
	public List<String> getAllConnectorName() {
		List<String> dbConnNames = new ArrayList<>();
		TreeView<TreeNodePo> tv = DBinfoTree.DBinfoTreeView;
		TreeItem<TreeNodePo> root = tv.getRoot();
		var childrenList = root.getChildren();
		if (childrenList != null && childrenList.size() > 0) {
			for (var item : childrenList) {
				TreeNodePo po = item.getValue();
				String name = po.getName();
				dbConnNames.add(name);
			}
		}
		return dbConnNames;
	}

	@Override
	public List<String> getAllActiveConnectorName() {
		List<String> dbConnNames = new ArrayList<>();
		TreeView<TreeNodePo> tv = DBinfoTree.DBinfoTreeView;
		TreeItem<TreeNodePo> root = tv.getRoot();
		var childrenList = root.getChildren();
		if (childrenList != null && childrenList.size() > 0) {
			for (var item : childrenList) {
				if (item.getChildren() != null && item.getChildren().size() > 0) {
					TreeNodePo po = item.getValue();
					String name = po.getName();
					dbConnNames.add(name);
				}

			}
		}
		return dbConnNames;
	}

	// 登入窗口
	@Override
	public void showSingInWindow(String title) {
		SignInWindow.show(title);
	}

	/**
	 * 使用新数据重建数据库连接节点树
	 */
	@Override
	public void recreateDBinfoTreeData(List<DBConnectorInfoPo> dbciPo) {
		ConnectionDao.DBInfoTreeReCreate(dbciPo);

	}

	/**
	 * 使用新数据合并入数据库连接节点树
	 */
	@Override
	public void mergeDBinfoTreeData(List<DBConnectorInfoPo> dbciPo) {
		ConnectionDao.DBInfoTreeMerge(dbciPo);
	}

	/**
	 * 使用新数据重建数据库连接节点树
	 */
	@Override
	public void recreateScriptTreeData(List<DocumentPo> docs) {
		ConnectionDao.scriptTreeReCreate(docs);

	}

	/**
	 * 使用新数据合并入数据库连接节点树
	 */
	@Override
	public void mergeScriptTreeData(List<DocumentPo> docs) {
		ConnectionDao.scriptTreeMerge(docs);
	}

	// 双击treeview 表格节点, 显示表信息
	@Override
	public SqluckyBottomSheet tableInfoSheet(SqluckyConnector sqluckyConn, TablePo table) {
		String name = table.getTableName();
		var mtb = new MyBottomSheet(name);
		mtb.setDDL(true);
		HighLightingCodeArea sqlArea = new HighLightingCodeArea(null, null);
		mtb.setSqlArea(sqlArea);
		VBox box = tableInfoBox(sqluckyConn, mtb, table);
		mtb.getTab().setContent(box);
		return mtb;
	}

}
