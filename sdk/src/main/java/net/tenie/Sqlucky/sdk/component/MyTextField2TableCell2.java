package net.tenie.Sqlucky.sdk.component;

 

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.Event;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

public class MyTextField2TableCell2<S, T> extends TextFieldTableCell<S, T> {
    
    public static <S> Callback<TableColumn<S,String>, TableCell<S,String>> forTableColumn() {
        return forTableColumn(new DefaultStringConverter());
    }

    public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(
            final StringConverter<T> converter) {
        return list -> new MyTextField2TableCell2<>(converter);
    }


    private TextField textField;

    public MyTextField2TableCell2() {
        this(null);
    }

    public MyTextField2TableCell2(StringConverter<T> converter) {
        super(converter);
//        this.setMaxHeight(50);
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
    		System.out.println(textField.getText());
    	});
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
    
}
