package net.tenie.fx.window;

import java.util.ArrayList;
import java.util.List;
import org.controlsfx.control.tableview2.TableColumn2;
import org.controlsfx.control.tableview2.TableView2;
import org.controlsfx.control.tableview2.cell.TextField2TableCell; 
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.fx.Action.RunSQLHelper;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.db.ProcedureFieldPo;
import net.tenie.fx.config.DBConns;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/**
 * 存储过程运行界面
 * @author tenie
 *
 */
public class ProcedureExecuteWindow {
	 private final ObservableList<Procedure> data  ;
	 private static ObservableList<String> lsType = FXCollections.observableArrayList();
	 
	 static {
		 lsType.add("String");
		 lsType.add("Integer");
		 lsType.add("Decimal");
		 lsType.add("Time");
	 }
	 
	 private ObservableList<Procedure> generateData(List<ProcedureFieldPo> fields) {
	        ObservableList<Procedure> procedures = FXCollections.observableArrayList( );
	        
	        for (int i = 0; i < fields.size(); i++) { 
	        	ProcedureFieldPo field = fields.get(i);
	            procedures.add(new Procedure(field.getName(),  "", ""));
	        }

	        return procedures;
	    }

	public ProcedureExecuteWindow(String procedureName, List<ProcedureFieldPo> fields ) {  
//		var fields = CommonAction.getProcedureFields(sql);
		final Stage stage = new Stage(); 
		data =	generateData(fields);
		VBox b = new VBox();
		ProcedureTableView  table = new ProcedureTableView(); 
		
//		Button btn = new Button("button");
		JFXButton btn = new JFXButton("Run");
		btn.setOnAction(e->{
			
			var items = table.getItems();
			for(int i = 0; i < fields.size() ; i++) {
				ProcedureFieldPo pfpo = fields.get(i);
				if(pfpo.isIn()) {
					String tabval = items.get(i).getValue().get();
					pfpo.setValue(tabval);  
				}
				if(pfpo.isOut()) {
					String type = table.getItems().get(i).getType().get();
//					pfpo.setTypeName(type);
					if(StrUtils.isNullOrEmpty(type)) {
						MyAlert.errorAlert("Out Field Must Has Type!");
						return ;
					}
				}
				
				String type = table.getItems().get(i).getType().get();
				pfpo.setTypeName(type);
				
			} 
			SqluckyConnector dpo = DBConns.getCurrentConnectPO(); 
			RunSQLHelper.callProcedure(procedureName, dpo, fields);
		 
			stage.close();
		}); 
		
		
		JFXButton cancelBtn = new JFXButton("Cancel");
		cancelBtn.getStyleClass().add("myAlertBtn");
		cancelBtn.setOnAction(value -> {
			stage.close();
		});
		AnchorPane foot = new AnchorPane(); 
		foot.setMinHeight(40);
		double i = 10.0;
		foot.getChildren().add(cancelBtn); 
		AnchorPane.setRightAnchor(cancelBtn, i);
		AnchorPane.setTopAnchor(cancelBtn, 10.0);
		i +=60; 
		foot.getChildren().add(btn); 
		AnchorPane.setRightAnchor(btn, i);
		AnchorPane.setTopAnchor(btn, 10.0);
		
		VBox.setVgrow(foot, null);
		b.getChildren().addAll(table, foot);
		b.getStyleClass().add("myAlert");
		
	 
		List<Node> nds = new ArrayList<>();
		nds.add( table); 
//		nds.add( tit); 
		
		List<Node> btns = new ArrayList<>();
		btns.add( cancelBtn);
		btns.add( btn);
		
		Node vb = DialogTools.setVboxShape(0,0,stage, ComponentGetter.INFO, nds, btns);
		Scene scene = new Scene( (Parent) vb);
//		Scene scene = new Scene( b);
	
		stage.setScene(scene);
//		ModalDialog.setSceneAndShow(scene, stage);
//		CommonAction.loadCss(scene);
//		stage.initStyle(StageStyle.UTILITY); 
		stage.initModality(Modality.APPLICATION_MODAL);
//		stage.show();
		DialogTools.setSceneAndShow(scene, stage);
	}
	 
	 
	 
	  private class ProcedureTableView extends TableView2<Procedure> {
		  private final TableColumn2<Procedure, String> parameterCol = new TableColumn2<>("Parameter");
	      private final TableColumn2<Procedure, String> typeCol = new TableColumn2<>("Type");
	      private final TableColumn2<Procedure, String> valueCol = new TableColumn2<>("Value");
	      
			@SuppressWarnings("unchecked")
			public ProcedureTableView() {
				this.getStyleClass().add("myTableTag");
				this.setEditable(true);
				parameterCol.setCellValueFactory(p -> p.getValue().getParameter());
				parameterCol.setCellFactory(TextField2TableCell.forTableColumn());
				parameterCol.setPrefWidth(130);
				parameterCol.setEditable(false);
				
				typeCol.setCellValueFactory(p -> p.getValue().getType());
				new ChoiceBoxTableCell<String, String>();
			
				typeCol.setCellFactory( 	ComboBoxTableCell.forTableColumn(lsType ) );
//				typeCol.setCellFactory(ComboBox2TableCell.forTableColumn("Name 1", "Name 2", "Name 3", "Name 4"));
				typeCol.setPrefWidth(110);
				typeCol.setEditable(true);
				
				valueCol.setCellValueFactory(p -> p.getValue().getValue());
				valueCol.setCellFactory(TextField2TableCell.forTableColumn());
				valueCol.setPrefWidth(130);  
				
				setItems(data);
				getColumns().setAll(parameterCol, typeCol, valueCol );

			}
	  }
	
	  class Procedure { 
	        private final StringProperty parameter = new SimpleStringProperty();
	        private final StringProperty type = new SimpleStringProperty();
	        private final StringProperty value = new SimpleStringProperty();

	        public Procedure(String p , String t, String v){
	        	parameter.set(p);
	        	type.set(t);
	        	value.set(v);
	        }
			public StringProperty getParameter() {
				return parameter;
			}
			public StringProperty getType() {
				return type;
			}
			public StringProperty getValue() {
				return value;
			}
	        
	  }

}
