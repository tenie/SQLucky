package net.tenie.fx.component.dataView;

import java.util.function.Consumer;

import com.jfoenix.controls.JFXButton;

import javafx.scene.layout.AnchorPane;
import net.tenie.Sqlucky.sdk.component.MyCodeArea;
import net.tenie.Sqlucky.sdk.component.MyBottomTab;
import net.tenie.Sqlucky.sdk.component.MyTooltipTool;
import net.tenie.Sqlucky.sdk.component.SdkComponent;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.subwindow.ModalDialog;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.IconGenerator;
import net.tenie.fx.Action.CommonEventHandler;
import net.tenie.fx.Action.RunSQLHelper;
import net.tenie.fx.config.DBConns;
import net.tenie.fx.factory.ButtonFactory;
import net.tenie.fx.window.ProcedureExecuteWindow;
/**
 * 建表语句, 视图 等的创建语句展示
 * @author tenie
 *
 */
public class DdlOptionBtnsPane extends  AnchorPane{
	
	 
	// TODO 数据表格 操作按钮们
		public   DdlOptionBtnsPane(MyBottomTab mytb, String ddl, boolean isRunFunc ,boolean isProc, String name ) {
		 
			// 锁 
			JFXButton lockbtn = SdkComponent.createLockBtn(mytb );
			// 保存
			JFXButton saveBtn = new JFXButton();
			saveBtn.setGraphic(IconGenerator.svgImageDefActive("save"));
			saveBtn.setOnMouseClicked(e -> { 
				//TODO 保存存储过程
				RunSQLHelper.runSQLMethod(mytb.getSqlArea().getCodeArea().getText(), null, true, null);
				saveBtn.setDisable(true);
				
			});
			saveBtn.setTooltip(MyTooltipTool.instance("save"));
			saveBtn.setDisable(true);
			mytb.setSaveBtn(saveBtn);
			
			//编辑
			JFXButton editBtn = new JFXButton();
			editBtn.setGraphic(IconGenerator.svgImageDefActive("edit"));
			editBtn.setOnMouseClicked(e -> {
				if (mytb.getSqlArea() != null) {
					MyCodeArea codeArea = mytb.getSqlArea().getCodeArea();
					codeArea.setEditable(true);
					saveBtn.setDisable(false);
					ButtonFactory.lockLockBtn(mytb, lockbtn);

				}
			});
			editBtn.setTooltip(MyTooltipTool.instance("Edit"));
//			editBtn.setId(AllButtons.SAVE);
			
			// 隐藏按钮
			JFXButton hideBottom = new JFXButton();
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
							RunSQLHelper.refresh(dpo, sql, null, false);
						};
						ModalDialog.showExecWindow("Run function",  name + "()", caller);
					}

				});
				runFuncBtn.setTooltip(MyTooltipTool.instance("Run"));
				this.getChildren().add(runFuncBtn);
				AnchorPane.setLeftAnchor(runFuncBtn, offset + 30.0);
			}
			
			this.getChildren().addAll(saveBtn, editBtn, hideBottom , lockbtn);
			this.prefHeight(25); 
			AnchorPane.setRightAnchor(hideBottom, 0.0);
			AnchorPane.setRightAnchor(lockbtn, 30.0);
		 
		}

}
