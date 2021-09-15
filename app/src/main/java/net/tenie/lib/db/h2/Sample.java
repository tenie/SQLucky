package net.tenie.lib.db.h2;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import net.tenie.fx.PropertyPo.TableFieldPo;
import net.tenie.fx.PropertyPo.TablePo;
import net.tenie.fx.PropertyPo.TablePrimaryKeysPo;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

public class Sample
{
	
	
	public static List<TablePo> fetchAllTableViewName(Connection conn, boolean istable)
			throws Exception {
		ResultSet tablesResultSet = null;
		ResultSet rs = null;
		Statement sm = null;
		List<TablePo> tbls = new ArrayList<TablePo>();
		try {
			DatabaseMetaData dbMetaData = conn.getMetaData();


			tablesResultSet = dbMetaData.getTables(null, null, null, null);
			while (tablesResultSet.next()) {
				String tableType = tablesResultSet.getString("TABLE_TYPE");
				if(tableType == null ) {
					tableType = "";
				}
				if (istable) {
					if (tableType.contains("VIEW")) {
						continue;
					}
				} else {
					if (!tableType.contains("VIEW")) {
						continue;
					}
				}

				String tableName = tablesResultSet.getString("TABLE_NAME");
				String remarks = tablesResultSet.getString("REMARKS");
//				System.out.println("tableName = "+ tableName);
//				System.out.println("REMARKS = "+ remarks);
				TablePo po = new TablePo();
				po.setTableName(tableName);
				po.setTableRemarks(remarks);
				po.setTableSchema("");
				po.setTableType(tableType);
				LinkedHashSet<TableFieldPo> fields = new LinkedHashSet<TableFieldPo>();
				po.setFields(fields);
				ArrayList<TablePrimaryKeysPo> tpks = new ArrayList<TablePrimaryKeysPo>();
				po.setPrimaryKeys(tpks);
				tbls.add(po);
			}

		} finally {
			if (tablesResultSet != null)
				tablesResultSet.close();
			if (rs != null)
				rs.close();
			if (sm != null)
				sm.close();
		}
		return tbls;
	}
	
  public static void main(String[] args) throws Exception
  {
    Connection connection = null;
    try
    {
      // create a database connection
      connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
      Statement statement = connection.createStatement();
      statement.setQueryTimeout(30);  // set timeout to 30 sec.

      statement.executeUpdate("drop table if exists person");
      statement.executeUpdate("create table person (id integer, name string)");
      statement.executeUpdate("insert into person values(1, 'leo')");
      statement.executeUpdate("insert into person values(2, 'yui')");
      ResultSet rs = statement.executeQuery("select * from person");
      while(rs.next())
      {
        // read the result set
        System.out.println("name = " + rs.getString("name"));
        System.out.println("id = " + rs.getInt("id"));
      }
      
      
       
      fetchAllTableViewName(connection, false);
    }
    catch(SQLException e)
    {
      // if the error message is "out of memory",
      // it probably means no database file is found
      System.err.println(e.getMessage());
    }
    finally
    {
      try
      {
        if(connection != null)
          connection.close();
      }
      catch(SQLException e)
      {
        // connection close failed.
        System.err.println(e.getMessage());
      }
    }
  }
}