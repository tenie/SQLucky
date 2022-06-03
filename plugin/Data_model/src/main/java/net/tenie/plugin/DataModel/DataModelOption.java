package net.tenie.plugin.DataModel;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;
import com.jfoenix.controls.JFXButton;

import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.po.tools.PoDao;
import net.tenie.Sqlucky.sdk.po.tools.PoDaoUtil;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.CommonUtility;
import net.tenie.Sqlucky.sdk.utility.FileOrDirectoryChooser;
import net.tenie.plugin.DataModel.po.DataModelInfoPo;
import net.tenie.plugin.DataModel.tools.AddModelFile;

public class DataModelOption {
	HBox FilterHbox = new HBox();
	
	public DataModelOption(){
//		Label lb = new Label();
//		var icon = ComponentGetter.getIconDefActive("search");
//		lb.setGraphic(icon);
//		lb.getStyleClass().add("myIcon");
		
		JFXButton queryBtn = new JFXButton();
		queryBtn.setGraphic(ComponentGetter.getIconDefActive("search"));
//		addcodeArea.setOnMouseClicked(CommonEventHandler.addCodeTab());
//		queryBtn.setTooltip(CommonUtility.instanceTooltip("Add Data Model File "));
		
		
		
		TextField txt = new TextField("");
		txt.getStyleClass().add("myTextField");
		
		// 添加按钮
		JFXButton addBtn = new JFXButton();
		addBtn.setGraphic(ComponentGetter.getIconDefActive("plus-square"));
//		addcodeArea.setOnMouseClicked(CommonEventHandler.addCodeTab());
		addBtn.setTooltip(CommonUtility.instanceTooltip("Add Data Model File "));
		addBtn.setOnAction(e->{
//			CommonUtility.openFileReadToString("UTF-8");
//			AddModelFile.test();
			readJosnModel("UTF-8");
		});
		
		FilterHbox.getChildren().addAll(queryBtn, txt, addBtn);
		HBox.setHgrow(txt, Priority.ALWAYS);
	}
	

	public static void readJosnModel(String encode) {
		File f = FileOrDirectoryChooser.showOpenJsonFile("Open", ComponentGetter.primaryStage);
		if (f == null)
			return ;
		String val = "";
		try {
			val = FileUtils.readFileToString(f, encode);
			if(val != null && !"".equals(val) ) { 
				DataModelInfoPo DataModelPoVal = JSONObject.parseObject(val, DataModelInfoPo.class);
//				System.out.println(DataModelPoVal);
				AddModelFile.insertDataModel(DataModelPoVal);
			}
		} catch (IOException e) {
			e.printStackTrace();
			MyAlert.errorAlert( e.getMessage());
		} 
	}
	
	public static DataModelInfoPo readJosnModel(String fileName, String encode) {
		DataModelInfoPo DataModelPoVal =  null;
		File f =  new File(fileName);
		try {
			String val = FileUtils.readFileToString(f, encode);
			if(val != null && !"".equals(val) ) { 
			    DataModelPoVal = JSONObject.parseObject(val, DataModelInfoPo.class);
				System.out.println(DataModelPoVal);
				System.out.println("======================");
				System.out.println(DataModelPoVal.getEntities().get(0)); 
//				AddModelFile.insertDataModel(DataModelPoVal);
//				PoDao.insert(null, null);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
			MyAlert.errorAlert( e.getMessage());
		} 
		
		return DataModelPoVal;
	}
	
	
	public static void main(String[] args) {
		readJosnModel("C:\\Users\\tenie\\Downloads\\infodms.chnr.json", "UTF-8");
	}
	

	public HBox getFilterHbox() {
		return FilterHbox;
	}

	public void setFilterHbox(HBox filterHbox) {
		FilterHbox = filterHbox;
	} 

	 
	
	
}