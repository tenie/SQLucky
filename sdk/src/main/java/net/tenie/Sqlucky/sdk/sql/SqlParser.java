package net.tenie.Sqlucky.sdk.sql;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitorAdapter;
import net.sf.jsqlparser.util.TablesNamesFinder;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SqlParser {

    public static boolean isValidSql(String sql) {
        try {
            if(StrUtils.isNotNullOrEmpty(sql)){
                CCJSqlParserUtil.parse(sql);
                return true;
            }
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取sql中的查询列名称
     *
     * @param sqlStr
     * @param useAliasName 是否使用alias name作为字段名称返回
     * @return
     */
    public static List<String> selectQueryColumn(String sqlStr, boolean useAliasName) {
        Select stmt = null;
        List<String> rs = new ArrayList<>();
        try {
            stmt = (Select) CCJSqlParserUtil.parse(sqlStr);
            for (SelectItem selectItem : ((PlainSelect) stmt.getSelectBody()).getSelectItems()) {
                selectItem.accept(new SelectItemVisitorAdapter() {
                    @Override
                    public void visit(SelectItem item) {
                        if (item.getAlias() != null) {
                            rs.add(item.getAlias().getName());
                        } else {
                            rs.add(item.getExpression().toString());
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
     *
     * @param sqlStr
     * @return
     */
    public static List<String> selectSqlWhereColumn(String sqlStr) {
        List<String> rs = new ArrayList<>();
        try {
            Select stmt = (Select) CCJSqlParserUtil.parse(sqlStr);
            PlainSelect select = (PlainSelect) stmt.getSelectBody();
            Expression where = select.getWhere();
            if (where == null) return rs;
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
     *
     * @param sqlStr
     * @return
     */
    public static Set<String> selectSqlTableNames(String sqlStr) {
        Set<String> tableNames = new HashSet<>();
        try {
            tableNames = TablesNamesFinder.findTables(sqlStr);
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
        return tableNames;
    }
}
