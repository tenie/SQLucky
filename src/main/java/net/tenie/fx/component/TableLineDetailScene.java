package net.tenie.fx.component;

import org.reactfx.inhibeans.property.SimpleIntegerProperty;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.tenie.fx.PropertyPo.SqlFieldPo;
import net.tenie.fx.config.ConfigVal;
/*   @author tenie */
public class TableLineDetailScene {
	
	public static void show( ObservableList<SqlFieldPo> fields) { 

        JFXTreeTableColumn<ShowPo, String> fieldColumn = new JFXTreeTableColumn<>("Field");
        fieldColumn.setPrefWidth(150);
        fieldColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<ShowPo, String> param) -> {
            if (fieldColumn.validateValue(param)) {
                return param.getValue().getValue().columnLabel;
            } else {
                return fieldColumn.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<ShowPo, String> valueColumn = new JFXTreeTableColumn<>("Value");
        valueColumn.setPrefWidth(150);
        valueColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<ShowPo, String> param) -> {
            if (valueColumn.validateValue(param)) {
                return param.getValue().getValue().value;
            } else {
                return valueColumn.getComputedValue(param);
            }
        });

       valueColumn.setCellFactory((TreeTableColumn<ShowPo, String> param) -> new GenericEditableTreeTableCell<>(
            new TextFieldEditorBuilder()));
        fieldColumn.setCellFactory((TreeTableColumn<ShowPo, String> param) -> new GenericEditableTreeTableCell<>(
            new TextFieldEditorBuilder()));
        // data
        ObservableList<ShowPo> ShowPos = FXCollections.observableArrayList();
        for(SqlFieldPo sfp : fields) {
        	ShowPo sp = new ShowPo( sfp.getColumnName(), 
				        			sfp.getColumnClassName(),
				        			sfp.getColumnDisplaySize(),
				        			sfp.getColumnLabel(),
				        			sfp.getColumnType(),
				        			sfp.getColumnTypeName(),
				        			sfp.getScale(),
				        			sfp.getValue() );
        	ShowPos.add(sp);
        }

        // build tree
        final TreeItem<ShowPo> root = new RecursiveTreeItem<>(ShowPos, RecursiveTreeObject::getChildren);

        JFXTreeTableView<ShowPo> treeView = new JFXTreeTableView<>(root);
        treeView.setShowRoot(false);
        treeView.setEditable(true);
        treeView.getColumns().setAll(fieldColumn, valueColumn);

        
 
        VBox subvb = new VBox();  
        FlowPane fp = new FlowPane();
        Label lb = new Label();
        lb.setGraphic(ImageViewGenerator.svgImageDefActive("search"));
        JFXTextField filterField = new JFXTextField();
        fp.getChildren().add(lb);
        fp.getChildren().add(filterField);
        subvb.getChildren().add(fp);
		subvb.getChildren().add(treeView);
		VBox.setVgrow(treeView, javafx.scene.layout.Priority.ALWAYS);
         

        filterField.textProperty().addListener((o, oldVal, newVal) -> {
            treeView.setPredicate(prePo -> {
                final ShowPo po = prePo.getValue(); 
                return  po.value.get().contains(newVal)
                    || po.columnLabel.get().contains(newVal) ;
            });
        });

        
    	final Stage stage = new Stage();
		JFXDecorator decorator = new JFXDecorator(stage,subvb  ,false, true, true );
        decorator.setCustomMaximize(false); 
        Scene scene = new Scene(decorator, 475, 500);
        scene.getStylesheets().addAll(ConfigVal.cssList);
        
		stage.initModality(Modality.WINDOW_MODAL); 
		stage.setTitle("");  

		
		stage.setScene(scene);
		
		
		
		stage.show();
        
	}
	
	 private static final class ShowPo extends RecursiveTreeObject<ShowPo> {
		     StringProperty columnName;
			 StringProperty columnClassName;
			 IntegerProperty columnDisplaySize;
			 StringProperty columnLabel;
			 IntegerProperty columnType;
			 StringProperty columnTypeName;
			 IntegerProperty scale;
			 StringProperty value;

			ShowPo( String columnName,
					String columnClassName,
					Integer columnDisplaySize,
					String columnLabel,
					Integer columnType,
					String columnTypeName,
					Integer scale, 
					String value
					) {
				this.columnName   = new SimpleStringProperty(columnName );
				this.columnClassName   = new SimpleStringProperty(columnClassName );
				this.columnDisplaySize   = new SimpleIntegerProperty( columnDisplaySize );
				this.columnLabel   = new SimpleStringProperty(columnLabel );
				this.columnType   = new SimpleIntegerProperty( columnType );
				this.columnTypeName   = new SimpleStringProperty(columnTypeName );
				this.scale   = new SimpleIntegerProperty( scale );
				this.value   = new SimpleStringProperty(value );

	        }
			
			ShowPo( 
					 StringProperty columnName,
					 StringProperty columnClassName,
					 IntegerProperty columnDisplaySize,
					 StringProperty columnLabel,
					 IntegerProperty columnType,
					 StringProperty columnTypeName,
					 IntegerProperty scale,
					 StringProperty value
					) {
				this.columnName   = columnName ;
				this.columnClassName   = columnClassName ;
				this.columnDisplaySize   =  columnDisplaySize ;
				this.columnLabel   = columnLabel ;
				this.columnType   =  columnType ;
				this.columnTypeName   = columnTypeName ;
				this.scale   =  scale ;
				this.value   = value ;

	        }
	    }
}
