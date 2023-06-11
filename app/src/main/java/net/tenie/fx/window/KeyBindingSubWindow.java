package net.tenie.fx.window;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.db.SqluckyAppDB;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.ui.SqluckyStage;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.fx.Action.SettingKeyBinding;

/**
 * 
 * @author tenie
 *
 */
public class KeyBindingSubWindow {
	// 编辑连接时记录连接状态
	private static Logger logger = LogManager.getLogger(KeyBindingSubWindow.class);
	
	private Stage stageWindow = null ;
	private Consumer<String> caller;
	private String actionId;
	public KeyBindingSubWindow(String id, Consumer<String> caller) {
		actionId = id;
		this.caller = caller;
	}
	
	public static void show(String id, String currentKey, Consumer<String> caller) {
		KeyBindingSubWindow window = new KeyBindingSubWindow(id, caller);
		window.layout(currentKey);
	}
	 
 
 
	// 控件布局, 并显示窗口
	public void layout(String currentKey ) {
		VBox vb = new VBox();
		// 显示输入的快捷键字符串
		Label valLb = new Label(currentKey);
		
		List<Region> list = new ArrayList<>();
		// 下载按钮
		Button btn = new Button("Save"); 
		btn.setDisable(true);
		btn.setOnAction(e->{
			String v = valLb.getText();
			String selectSQL = "select count(*) from KEYS_BINDING where BINDING = '"+v+"' ";
			String count = SqluckyAppDB.selectOne(selectSQL);
			if(StrUtils.isNotNullOrEmpty(count)) {
				if(! "0".equals(count)) {
					MyAlert.errorAlert("快捷以存在:" + v);
					return;
				}
			}
//			保存数据库 
			String updateSQL = "update KEYS_BINDING set BINDING = '"+v+"' where id = " + actionId;
			SqluckyAppDB.execDDL(updateSQL);
			this.caller.accept("");
			stageWindow.close();
		});
		Consumer<String> setLabel = v->{
			valLb.setText(v);
			btn.setDisable(false);
			
		};
		list.add(null);
		list.add(btn);
		 
		
		
		vb.getChildren().add(valLb);
		VBox.setMargin(valLb, new  Insets(60, 30, 60, 150));
		
		GridPane grid = new GridPane();
		vb.getChildren().add(grid);
		vb.setPadding( new Insets(5));
//		Stage stage = CreateModalWindow(vb);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 10, 10, 10));
		
		
		int i = 0;
		int j = 0;
		
		for(int k = 0 ; k< list.size() ; k+=2) {
			var node1 = list.get(k);
			var node2 = list.get(k+1);
			int idxi= i++;
			int idxj= j++;
			if(node1 !=null ) grid.add(node1, 0, idxi);
			if(node2 !=null ) grid.add(node2, 1, idxj);
		}
		
		SqluckyStage sqlStage = new SqluckyStage(vb);
		stageWindow = sqlStage.getStage();
		Scene scene = sqlStage.getScene();
		vb.getStyleClass().add("connectionEditor");
		
		vb.setPrefWidth(400);
		vb.maxWidth(400);
		AnchorPane bottomPane = new AnchorPane();
		bottomPane.setPadding(new Insets(10));

		vb.getChildren().add(bottomPane);
		KeyCodeCombination escbtn = new KeyCodeCombination(KeyCode.ESCAPE);
		KeyCodeCombination spacebtn = new KeyCodeCombination(KeyCode.SPACE);
		scene.getAccelerators().put(escbtn, () -> {
			stageWindow.close();
			
		});
		scene.getAccelerators().put(spacebtn, () -> {
			stageWindow.close();
			
		});
		
		 
		SettingKeyBinding.sceneEventFilter(scene, setLabel);
		stageWindow.initModality(Modality.APPLICATION_MODAL);
		stageWindow.setTitle("请直接再键盘上输入新的快捷键");
//		stageWindow.setTitle(title);
		stageWindow.setMaximized(false);
		stageWindow.setResizable(false);
		stageWindow.setOnCloseRequest(v->{
			stageWindow = null;
		});
		stageWindow.showAndWait();
	}
	

}
