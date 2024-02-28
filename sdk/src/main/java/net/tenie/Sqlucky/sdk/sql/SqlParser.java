package net.tenie.Sqlucky.sdk.sql;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitorAdapter;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.*;

public class SqlParser {

    /**
     * 获取sql中的查询列名称
     * @param sqlStr
     * @param useAliasName 是否使用alias name作为字段名称返回
     * @return
     */
    public static List<String> selectQueryColumn(String sqlStr, boolean useAliasName){
        Select stmt = null;
        List<String> rs = new ArrayList<>();
        try {
            stmt = (Select) CCJSqlParserUtil.parse( sqlStr);
            for (SelectItem selectItem : ((PlainSelect)stmt.getSelectBody()).getSelectItems()) {
                selectItem.accept(new SelectItemVisitorAdapter() {
                    @Override
                    public void visit(SelectItem item) {
                        if(item.getAlias() != null ){
                            rs.add(item.getAlias().getName()) ;
                        }else {
                            rs.add(item.getExpression().toString()) ;
                        }


                    }
                });
            }

        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }


        return rs;

    }

    /**
     * 获取sql中的where 的条件字段
     * @param sqlStr
     * @return
     */
    public static List<String> selectSqlWhereColumn(String sqlStr){
        List<String> rs = new ArrayList<>();
        try {
            Select stmt = (Select) CCJSqlParserUtil.parse(sqlStr);
            System.out.println("before " + stmt.toString());
            PlainSelect select =   (PlainSelect)stmt.getSelectBody();
            Expression where = select.getWhere();
            if(where == null ) return rs;
            where.accept(new ExpressionVisitorAdapter() {
                @Override
                public void visit(Column column) {
                    String colName = column.getColumnName();
                    rs.add(colName);
                }
            });

        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }

        return rs;
    }

    /**
     * 获取sql中的所有表
     * @param sqlStr
     * @return
     */
    public static Set<String> selectSqlTableNames(String sqlStr){
        Set<String> tableNames = new HashSet<>();
        try {
            tableNames = TablesNamesFinder.findTables(sqlStr);
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
        return tableNames;
    }


    public static void main(String[] args) {
        String sql = "SELECT a.col1 , a.col2 AS b, a.col3 AS c FROM ffo.table a left join tab2 t on a.id = t.pid WHERE a.col_1 = 10 AND a.col_2 = 20 AND a.col_3 = 30";
        List<String > vals = selectSqlWhereColumn(sql);

//        List<String > vals = selectQueryColumn(sql, true);
        System.out.println(vals);


//        Set<String> tna  = selectSqlTableNames(sql);
//        System.out.println(tna);
    }
}
