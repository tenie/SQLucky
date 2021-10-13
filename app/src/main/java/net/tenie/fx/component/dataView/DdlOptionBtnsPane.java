package net.tenie.fx.component.dataView;

import java.util.List;
import java.util.function.Consumer;

import com.jfoenix.controls.JFXButton;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.ButtonAction;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.RunSQLHelper;
import net.tenie.fx.component.AllButtons;
import net.tenie.fx.component.MyTooltipTool;
import net.tenie.fx.component.CodeArea.HighLightingCodeArea;
import net.tenie.fx.component.CodeArea.MyCodeArea;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.factory.ButtonFactory;
import net.tenie.fx.window.ModalDialog;
import net.tenie.fx.window.ProcedureExecuteWindow;
import net.tenie.fx.window.TableDataDetail;
import net.tenie.lib.tools.IconGenerator;

public class DdlOptionBtnsPane extends  AnchorPane{
	
//	public DdlOptionBtnsPane(MyTabData mytb,  boolean disable, String time , String rows, String connName, List<ButtonBase> optionBtns , boolean isLock) {
	 
	// TODO 数据表格 操作按钮们
		public   DdlOptionBtnsPane(MyTabData mytb, String ddl, boolean isRunFunc ,boolean isProc, String name ) {
		 
			// 锁 
			JFXButton lockbtn = ButtonFactory.createLockBtn(mytb );
//			btns.add(lockbtn);
			// 保存
			JFXButton saveBtn = new JFXButton();
//			btns.add(saveBtn);
			saveBtn.setGraphic(IconGenerator.svgImageDefActive("save"));
			saveBtn.setOnMouseClicked(e -> { 
				//TODO 保存存储过程
				RunSQLHelper.runSQLMethod(mytb.getSqlArea().getCodeArea().getText(), null, true);
				saveBtn.setDisable(true);
				
			});
			saveBtn.setTooltip(MyTooltipTool.instance("save"));
			saveBtn.setDisable(true);
//			btns.add(saveBtn);
			
			//编辑
			JFXButton editBtn = new JFXButton();
//			btns.add(editBtn);
			editBtn.setGraphic(IconGenerator.svgImageDefActive("edit"));
			editBtn.setOnMouseClicked(e -> {
//				SqlEditor.createTabFromSqlFile(ddl, "", "");
				if (mytb.getSqlArea() != null) {
					MyCodeArea codeArea = mytb.getSqlArea().getCodeArea();
					codeArea.setEditable(true);
					saveBtn.setDisable(false);
//					myEvent.btnClick( lockbtn);
					ButtonFactory.lockLockBtn(mytb, lockbtn);

				}
			});
			editBtn.setTooltip(MyTooltipTool.instance("Edit"));
			editBtn.setId(AllButtons.SAVE);
//			btns.add(editBtn);
			
			// 隐藏按钮
			JFXButton hideBottom = new JFXButton();
//			btns.add(hideBottom);
			hideBottom.setGraphic(IconGenerator.svgImageDefActive("caret-square-o-down"));
			hideBottom.setOnMouseClicked(CommonEventHandler.hideBottom());

			
			
			
			double offset = 30.0;
			AnchorPane.setLeftAnchor(editBtn, offset);
			
			// 运行按钮
			if (isRunFunc) {
				JFXButton runFuncBtn = new JFXButton();
				runFuncBtn.setGraphic(IconGenerator.svgImageDefActive("play"));
				runFuncBtn.setOnMouseClicked(e -> {
					Consumer<String> caller;
					ButtonFactory.lockLockBtn(mytb, lockbtn);
					if (isProc) {
						var fields = CommonUtility.getProcedureFields(ddl);
						if (fields.size() > 0) {
							// 有参数的存储过程
							new ProcedureExecuteWindow( name, fields);
						} else {
							// 调用无参数的存储过程
							caller = x -> {
								SqluckyConnector dpo = DBConns.getCurrentConnectPO(); 
								RunSQLHelper.callProcedure(name, dpo, fields);
							};
							ModalDialog.showExecWindow("Run Procedure", name, caller);

						}

					} else {
						caller = x -> {
							SqluckyConnector dpo = DBConns.getCurrentConnectPO();
							String sql = dpo.getExportDDL().exportCallFuncSql(x);
							RunSQLHelper.runSQLMethodRefresh(dpo, sql, null, false);
						};
						ModalDialog.showExecWindow("Run function",  name + "()", caller);
					}

				});
				runFuncBtn.setTooltip(MyTooltipTool.instance("Run"));
//				btns.add(runFuncBtn);
				this.getChildren().add(runFuncBtn);
				AnchorPane.setLeftAnchor(runFuncBtn, offset + 30.0);
			}
			
			this.getChildren().addAll(saveBtn, editBtn, hideBottom , lockbtn);
			this.prefHeight(25); 
			AnchorPane.setRightAnchor(hideBottom, 0.0);
			AnchorPane.setRightAnchor(lockbtn, 30.0);
		 
		}

}
