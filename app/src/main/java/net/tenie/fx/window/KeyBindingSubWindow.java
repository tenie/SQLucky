package net.tenie.fx.window;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.tableview2.FilteredTableView;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
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
import net.tenie.Sqlucky.sdk.config.ConfigVal;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.po.SheetTableData;
import net.tenie.Sqlucky.sdk.ui.IconGenerator;
import net.tenie.Sqlucky.sdk.ui.LoadingAnimation;
import net.tenie.Sqlucky.sdk.ui.SqluckyStage;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.DBTools;
import net.tenie.Sqlucky.sdk.utility.JsonTools;
import net.tenie.Sqlucky.sdk.utility.net.HttpUtil;

/**
 * 
 * @author tenie
 *
 */
public class KeyBindingSubWindow {
	// 编辑连接时记录连接状态
	private static Logger logger = LogManager.getLogger(KeyBindingSubWindow.class);
	
	private Stage stageWindow = null ;
	
	private String actionId;
	public KeyBindingSubWindow(String id) {
		actionId = id;
	}
	
	public static void show(String id) {
		KeyBindingSubWindow window = new KeyBindingSubWindow(id);
		window.layout();
	}
	// 创建窗口
	public Stage CreateModalWindow(VBox vb,  String title) {
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

		stageWindow.initModality(Modality.APPLICATION_MODAL);
		
		stageWindow.setTitle(title);
		stageWindow.setMaximized(false);
		stageWindow.setResizable(false);
		stageWindow.setOnCloseRequest(v->{
			stageWindow = null;
		});
		return stageWindow;
	}
 
 
	// 控件布局, 并显示窗口
	public void layout( ) {
		VBox vb = new VBox();
		List<Region> list = new ArrayList<>();
		// 下载按钮
		Button btn = new Button("Save"); 
		btn.setDisable(true);
		btn.setOnAction(v->{
		});
		list.add(null);
		list.add(btn);
		
//		vb.getChildren().add( );
		 
		
		
		GridPane grid = new GridPane();
		vb.getChildren().add(grid);
		vb.setPadding( new Insets(5));
		Stage stage = CreateModalWindow(vb, "Key Binding");
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
		
		stage.show();
	}
	
}
