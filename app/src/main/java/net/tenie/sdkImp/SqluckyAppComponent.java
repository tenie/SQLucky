package net.tenie.sdkImp;

import java.sql.SQLException;
import java.util.List;

import com.jfoenix.controls.JFXButton;

import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.tenie.Sqlucky.sdk.AppComponent;
import net.tenie.Sqlucky.sdk.SqluckyBottomSheet;
import net.tenie.Sqlucky.sdk.SqluckyTab;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.db.SqluckyDbRegister;
import net.tenie.Sqlucky.sdk.po.BottomSheetDataValue;
import net.tenie.Sqlucky.sdk.po.DocumentPo;
import net.tenie.Sqlucky.sdk.utility.IconGenerator;
import net.tenie.fx.component.MyTab;
import net.tenie.fx.component.CodeArea.HighLightingCodeArea;
import net.tenie.fx.component.dataView.BottomSheetOptionBtnsPane;
import net.tenie.fx.component.dataView.MyTabData;
import net.tenie.fx.config.DbVendor;
import net.tenie.fx.dao.DmlDdlDao;
import net.tenie.lib.db.h2.AppDao; 

public class SqluckyAppComponent implements AppComponent { 

	@Override
	public void addTitledPane(TitledPane tp) {
		Accordion ad = ComponentGetter.infoAccordion;
		if(ad != null) {
			ad.getPanes().add(tp);
		} 
	}

	@Override
	public void addIconBySvg(String name, String svg) {
		IconGenerator.addSvgStr(name, svg);		
	}

	@Override
	public SqluckyTab sqluckyTab() { 
		return new MyTab(false);
	}
//
//	@Override
//	public SqluckyTab sqluckyTab(String TabName) { 
//		return new MyTab(TabName);
//	}

	@Override
	public SqluckyTab sqluckyTab(DocumentPo po) { 
		return new MyTab(po, false);
	}
	/**
	 * 获取图标
	 */
	@Override
	public Region getIconUnactive(String name) {
		return IconGenerator.svgImageUnactive( name); 
	}
	/**
	 * 获取图标
	 */
	@Override
	public Region getIconDefActive(String name) {
		return IconGenerator.svgImageDefActive( name); 
	}
	/**
	 *  保持插件存储的key_value
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
			val = AppDao.readConfig( conn, strb.toString());
		} finally {
			SqluckyAppDB.closeConn( conn);
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
	public  SqluckyBottomSheet sqlDataSheet(BottomSheetDataValue data, int idx, boolean disable) {
		MyTabData rs = new MyTabData(data, idx, disable);
		String time = data.getExecTime() == 0 ? "0" : data.getExecTime() + "";
		String rows = data.getRows() == 0 ? "0" : data.getRows() + "";
		
		VBox vbox = new VBox();
		var btnLs = BottomSheetOptionBtnsPane.sqlDataOptionBtns(rs, disable);
		AnchorPane dtBtnPane = new BottomSheetOptionBtnsPane(btnLs, time, rows, data.getConnName());
		// 添加按钮面板和 数据表格
		vbox.getChildren().add(dtBtnPane);
		vbox.getChildren().add(data.getTable());
		VBox.setVgrow(data.getTable(), Priority.ALWAYS);
		
		rs.setContent(vbox);

		return rs;
	}
	
	public  SqluckyBottomSheet tableViewSheet(BottomSheetDataValue data, List<Node> btnLs) {
		var rs = new MyTabData(data);
		
		VBox vbox = new VBox(); 
		JFXButton LockBtn = SdkComponent.createLockBtn(rs);
		btnLs.add(0, LockBtn);
		AnchorPane dtBtnPane = new BottomSheetOptionBtnsPane(btnLs);
		// 添加按钮面板和 数据表格
		vbox.getChildren().add(dtBtnPane);
		vbox.getChildren().add(data.getTable());
		VBox.setVgrow(data.getTable(), Priority.ALWAYS);
		
		rs.setContent(vbox);

		return rs;
	}
	
	
	
	// 表, 视图 等 数据库对象的ddl语句
	@Override
	public  SqluckyBottomSheet ddlSheet(String name, String ddl, boolean isRunFunc) {
		var mtb = new MyTabData(name);
		mtb.setDDL(true);
		HighLightingCodeArea sqlArea = new HighLightingCodeArea(null);
		mtb.setSqlArea(sqlArea);
		VBox box = DDLBox(mtb, ddl, isRunFunc, false, name);
		mtb.setContent(box);
		return mtb;
	}
	@Override
	public SqluckyBottomSheet ProcedureSheet(String name, String ddl, boolean isRunFunc) {
		var mtb = new MyTabData(name);
		mtb.setDDL(true);
		HighLightingCodeArea sqlArea = new HighLightingCodeArea(null);
		mtb.setSqlArea(sqlArea);
		VBox box = DDLBox(mtb, ddl, isRunFunc, true, name);
		mtb.setContent(box);
		return mtb;
	}
	@Override
	public SqluckyBottomSheet EmptySheet(String name, String message) {
		var mtb = new MyTabData(name);
		mtb.setDDL(true);
		HighLightingCodeArea sqlArea = new HighLightingCodeArea(null);
		mtb.setSqlArea(sqlArea);
		VBox box = DDLBox(mtb, message, false, false, name);
		mtb.setContent(box);
		return mtb;
	}

 
	// 数据tab中的组件
	public static VBox DDLBox(MyTabData mtb, String ddl, boolean isRunFunc, boolean isProc, String name) {
		VBox vb = new VBox();

		StackPane sp = mtb.getSqlArea().getCodeAreaPane(ddl, false);
		// 表格上面的按钮
		List<Node> btnLs = BottomSheetOptionBtnsPane.DDLOptionBtns(mtb, ddl, isRunFunc, isProc, name);
		AnchorPane fp = new BottomSheetOptionBtnsPane(btnLs);
//		AnchorPane fp = new DdlOptionBtnsPane(mtb, ddl, isRunFunc, isProc, name); // ddlOptionBtnsPane(ddl, isRunFunc,
																					// isProc, name);
		vb.getChildren().add(fp);
		vb.getChildren().add(sp);
		VBox.setVgrow(sp, Priority.ALWAYS);
		return vb;
	}

	


	

}
