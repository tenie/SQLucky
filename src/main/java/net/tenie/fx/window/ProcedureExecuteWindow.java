package net.tenie.fx.window;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.controlsfx.control.tableview2.TableColumn2;
import org.controlsfx.control.tableview2.TableView2;
import org.controlsfx.control.tableview2.cell.ComboBox2TableCell;
import org.controlsfx.control.tableview2.cell.TextField2TableCell; 

import com.jfoenix.controls.JFXButton;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.fx.Action.CommonAction;
import net.tenie.fx.PropertyPo.ProcedureFieldPo;
import net.tenie.lib.tools.StrUtils;

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

	public ProcedureExecuteWindow(String sql ,String procedureName ) { 
		sql = StrUtils.multiLineCommentToSpace(sql);
		sql = StrUtils.trimCommentToSpace(sql, "--");
		var fields = CommonAction.getProcedureFields(sql);
		data =	generateData(fields);
		VBox b = new VBox();
		ProcedureTableView  table = new ProcedureTableView(); 
		
		Button btn = new Button("button");
		btn.setOnAction(e->{
			String type = table.getItems().get(0).getType().get();
			System.out.println(type);
			String val = table.getItems().get(0).getValue().get();
			System.out.println(val);
		});
//		StackPane   centerPane = new StackPane(table);
 
		VBox.setVgrow(btn, null);
		b.getChildren().addAll(table, btn);
		final Stage stage = new Stage(); 
	 
		Scene scene = new Scene( b);
		
//		stage.initModality(Modality);
		stage.setScene(scene);
//		ModalDialog.setSceneAndShow(scene, stage);
		stage.show();
	
	}
	 
	 
	 
	  private class ProcedureTableView extends TableView2<Procedure> {
		  private final TableColumn2<Procedure, String> parameterCol = new TableColumn2<>("Parameter");
	      private final TableColumn2<Procedure, String> typeCol = new TableColumn2<>("Type");
	      private final TableColumn2<Procedure, String> valueCol = new TableColumn2<>("Value");
	      
			public ProcedureTableView() {
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
//	        private final IntegerProperty age = new SimpleIntegerProperty();
//	        private final StringProperty city = new SimpleStringProperty();
//	        private final BooleanProperty active = new SimpleBooleanProperty();
//	        private final ObjectProperty<LocalDate> birthday = new SimpleObjectProperty<>();
	        
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
