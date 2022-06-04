package net.tenie.fx.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.tenie.fx.Po.TableFieldProperty;
import net.tenie.fx.Po.TableProperty;
/*   @author tenie */
public class SelectDataToPo {
	/**
	 * 从数据源中获取table数据返回数据集
	 * @param resultSet
	 * @return
	 */
	public static ObservableList<TableProperty> getTable(ResultSet resultSet) {
		ObservableList<TableProperty> data = FXCollections.observableArrayList();
		try {
			while(resultSet.next()){  
				Long id = resultSet.getLong("id");
				String name = resultSet.getString("table_name");
				String comm = resultSet.getString("table_comment"); 
				data.add( new TableProperty(id,name,comm) );
			}
		} catch (SQLException e) { 
			e.printStackTrace();
		} 
		return data;
		
	}
	
	
	/**
	 * 从数据源中获取table数据返回数据集
	 * @param resultSet
	 * @return
	 */
	public static ObservableList<TableFieldProperty> getTableField(ResultSet resultSet) {
		ObservableList<TableFieldProperty> data = FXCollections.observableArrayList();
		try {
			while(resultSet.next()){  
				Long id = resultSet.getLong("id");
				Long tbid = resultSet.getLong("table_id");
				String name = resultSet.getString("field_name");
				String comm = resultSet.getString("field_comment");
				String ty = resultSet.getString("TYPE_NAME"); 
				String isnull = resultSet.getString("IS_NULLABLE"); 
				
				data.add( new TableFieldProperty(id,tbid, name, ty, isnull,comm) );
			}
		} catch (SQLException e) { 
			e.printStackTrace();
		} 
		return data;
		
	}
}
