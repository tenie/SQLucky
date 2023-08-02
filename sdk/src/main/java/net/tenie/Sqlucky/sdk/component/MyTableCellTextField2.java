package net.tenie.Sqlucky.sdk.component;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.Event;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import net.tenie.Sqlucky.sdk.db.ResultSetRowPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;

public class MyTableCellTextField2<S, T> extends TextFieldTableCell<S, T> {
    
    public static <S> Callback<TableColumn<S,String>, TableCell<S,String>> forTableColumn() {
    	
        return forTableColumn(new DefaultStringConverter());
    }

    // 返回一个函数
    public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(  final StringConverter<T> converter) {
        return  list -> {
    		return new MyTableCellTextField2<>(converter);
    	};
    }


    private TextField textField;

    public MyTableCellTextField2() {
        this(null);
    }
    /**
     * 初始化右键菜单
     */
    private void initMenu(StringConverter<T> converter) {
    	// 右键菜单
        ContextMenu cm = new ContextMenu();
        // 设置null
        MenuItem setNull = new MenuItem("Set Null ");
        setNull.setOnAction(e->{
        	commitEdit(converter.fromString("<null>"));
        });
		
        // 复制值
        MenuItem copyVal = new MenuItem("Copy Value");
        copyVal.setOnAction(e->{
        	String val =  this.getText();
        	CommonUtils.setClipboardVal(val);
        });
        
        cm.getItems().addAll(copyVal, setNull);
        setContextMenu(cm);
    }
    public MyTableCellTextField2(StringConverter<T> converter) {
        super(converter); 
        initMenu(converter);
        
        // 在表格cell中, 显示不下就以省略号结尾
        this.setTextOverrun(OverrunStyle.ELLIPSIS);
       
        graphicProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (getGraphic() != null && getGraphic() instanceof TextField) {
                    textField = (TextField) getGraphic();
                    
                    // commit on focus lost
                    textField.focusedProperty().addListener((obs, ov, nv) -> {
                        if (! nv) { 
                            commitEdit(converter.fromString(textField.getText()));
                        }  
                    });
                  
                    // 如果做了修改, 修改背景颜色, 并设置列不能再排序
                    textField.textProperty().addListener((obs, ov, nv) -> {
                    	if( nv != null) {
                    		if(! nv.equals(ov)) {
                    			Platform.runLater(()->{ 
                    				setCellColorForNewOrChange();
                    			});
                    		}
                    	}
                    	
                    });
                   
                    
                    // cancel with Escape, on key pressed, not released
                    textField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                        final TableView.TableViewSelectionModel<S> selectionModel = getTableView().getSelectionModel();
                        if (event.getCode() == null || selectionModel == null) {
                            return;
                        }
                        switch (event.getCode()) {
                            case ESCAPE:
                                // restore old value
                                textField.setText(converter.toString(getItem()));
                                cancelEdit();
                                event.consume();
                                break;
                            case TAB: 
                                cancelEdit();
                                event.consume();
                                if (selectionModel.isCellSelectionEnabled()) {
                                    int columnIndex = getTableView().getVisibleLeafIndex(getTableColumn());
                                    if (event.isShiftDown()) {
                                        if (columnIndex > 0) {
                                            // go to previous column
                                            selectionModel.clearAndSelect(getIndex(), getTableView().getVisibleLeafColumn(columnIndex - 1));
                                        } else if (getIndex() > 0) {
                                            // wrap to end of previous row
                                            selectionModel.clearAndSelect(getIndex() - 1, getTableView().getVisibleLeafColumn(getTableView().getVisibleLeafColumns().size() - 1));
                                        }
                                    } else {
                                        if (columnIndex + 1 < getTableView().getVisibleLeafColumns().size()) {
                                            // go to next column
                                            selectionModel.clearAndSelect(getIndex(), getTableView().getVisibleLeafColumn(columnIndex + 1));
                                        } else if (getIndex() < getTableView().getItems().size() - 1) {
                                            // wrap to start of next row
                                            selectionModel.clearAndSelect(getIndex() + 1, getTableView().getVisibleLeafColumn(0));
                                        }
                                    }
                                } else {
                                    // go to prev/next row
                                    selectionModel.clearAndSelect(event.isShiftDown() ? getIndex() - 1 : getIndex() + 1);
                                }
                                break;
                            case UP:
                                cancelEdit();
                                event.consume();
                                selectionModel.clearAndSelect(getIndex() - 1, getTableColumn());
                                break;
                            case DOWN:
                                cancelEdit();
                                event.consume();
                                selectionModel.clearAndSelect(getIndex() + 1, getTableColumn());
                                break;
                            default: 
                                break;
                        }
                    });

                    graphicProperty().removeListener(this);
                }
            }
        });
        
        
    }
    
    public void textFieldSetON(TextField textField) {
    	textField.setOnAction(v->{
//    		System.out.println(textField.getText());
    	});
    }
    
    @Override
	public void updateItem(T item, boolean empty) {
    	super.updateItem(item, empty);
    	if(item != null) {
    		// 如果是手动添加的行, 那么对应的单元格变色
        	TableRow<S> tr = getTableRow();
        	S v = tr.getItem();
        	if(v instanceof ResultSetRowPo) {
        		Boolean isNewAdd = ((ResultSetRowPo) v).getIsNewAdd();
        		if(isNewAdd) {
        			setCellColorForNewOrChange();
        		}
        	}
        	
    	}
    }
    
    
    /** {@inheritDoc} */
    @Override public void commitEdit(T item) {
        if (! isEditing() && ! item.equals(getItem())) {
            TableView<S> table = getTableView();
            if (table != null) {
                TableColumn<S, T> column = getTableColumn();
                TableColumn.CellEditEvent<S, T> event = new TableColumn.CellEditEvent<>(table,
                        new TablePosition<>(table, getIndex(), column), TableColumn.editCommitEvent(), item);
                Event.fireEvent(column, event);
            }
        }
       
        super.commitEdit(item);
    }
    
    // 更改单元格颜色并且让整个表格禁用排序功能
    boolean colSortable = true;  
    public void setCellColorForNewOrChange() {
		 setStyle("-fx-background-color: #1AD0DF; -fx-text-fill: #2B2B2B;"); //#2F65CA
			 if(colSortable ) {
				 // 设置列禁止排序
				 TableView<S> table = getTableView();
				 if(table != null) {
					 var cols =    table.getColumns();
		  	         for(var col: cols) {
		  	        	 col.setSortable(false);
		  	         }
		  	         colSortable = false;
				 }
			 }
	
    }
    
}
