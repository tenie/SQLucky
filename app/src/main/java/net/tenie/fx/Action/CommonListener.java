package net.tenie.fx.Action;

import java.util.Set;

import com.jfoenix.controls.JFXComboBox;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.DBConns;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.utility.AppCommonAction;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.window.ConnectionEditor;

/**
 * 
 * @author tenie
 *
 */
public class CommonListener {

	// 文本框只能输入ip字符串
	public static ChangeListener<String> textFieldIp(TextField rows) {
		return new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				String str = StrUtils.clearIpStr(newValue);
				rows.setText(str);
			}
		};
	}

	// 文本框只能输入数字
	public static ChangeListener<String> textFieldNumChange(TextField rows) {
		return new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (StrUtils.isNotNullOrEmpty(newValue)) {
					String str = StrUtils.clearString(newValue);
					if (str != null && str.length() > 0) {
						ConfigVal.MaxRows = Integer.valueOf(str);
					} else {
						ConfigVal.MaxRows = 1;
					}
					rows.setText(str);
				}
			}
		};
	}

	// 文本框限制长度
	public static ChangeListener<Number> textFieldLimit(TextField textField, int LIMIT) {
		return new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (newValue.intValue() > oldValue.intValue()) {
					// Check if the new character is greater than LIMIT
					if (textField.getText().length() >= LIMIT) {
						textField.setText(textField.getText().substring(0, LIMIT));
					}
				}
			}
		};
	}


	// 选择框选择连接时, 如果未连接, 进行连接
	public static ChangeListener<Label> choiceBoxChange2(JFXComboBox<String> cbSchemas, Label lbSchemas) {
		return new ChangeListener<Label>() {
			@Override
			public void changed(ObservableValue<? extends Label> observable, Label oldValue, Label newValue) {
				if (newValue != null) {
					SqluckyConnector cnnpo = DBConns.get(newValue.getText());
					if (cnnpo != null ) { // && !cnnpo.isAlive()
						// 清除查找字符串
						IndexRange ir = MyEditorSheetHelper.getSelection();
						CommonUtils.pressBtnESC();
						AppCommonAction.shrinkTreeView();
						ConnectionEditor.openConn(cnnpo.getConnName());
						MyEditorSheetHelper.selectRange(ir);
					}
					// 切换 schemas 列表和值
					if(cnnpo != null &&
							cnnpo.getDBConnectorInfoPo() != null 
							&& cnnpo.getDBConnectorInfoPo().getSchemas() != null) {
						if( cnnpo.getDBConnectorInfoPo().isShowSchemas()) {
							cbSchemas.setVisible(true);
							lbSchemas.setVisible(true);
							Set<String > items = cnnpo.getDBConnectorInfoPo().getSchemas().keySet();
							if(items != null && items.size() > 0) {
								// 默认 schema
								String defaultSchema =  cnnpo.getDBConnectorInfoPo().getDefaultSchema();
								// 临时schema
								String tmpSchema =  cnnpo.getDBConnectorInfoPo().getTmpSchema();
								String equalsSchema = defaultSchema;
								if(tmpSchema != null && tmpSchema.length() > 0) {
									 equalsSchema = tmpSchema;
								}
								
								ObservableList itemList = FXCollections.observableArrayList();
								itemList.addAll(items);
								cbSchemas.setItems(itemList);
								for(int i=0; i< itemList.size(); i++) {
									// 如何临时schema匹配下拉选就设置为临时schema
									if(equalsSchema.equals(itemList.get(i))) {
//										System.out.println("==connsComboBox =" + equalsSchema + "|"  + cbSchemas.getSelectionModel());
										cbSchemas.getSelectionModel().select(i);
//										cnnpo.getDBConnectorInfoPo().setTmpSchema(equalsSchema);
										break;
									}
								}
								
							}
						}else {
							// 隐藏
							cbSchemas.setVisible(false);
							lbSchemas.setVisible(false);
						}
						
						
					}else {
						cbSchemas.getItems().clear();
					}
				}

			}
		};
	}

}
