package net.tenie.Sqlucky.sdk.excel;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import net.tenie.Sqlucky.sdk.db.InsertDao;
import net.tenie.Sqlucky.sdk.db.InsertPreparedStatementDao;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.SheetFieldPo;

/**
 * excel 导入到数据库
 * 
 * @author tenie
 *
 */
public class ExcelToDB {

	private static Logger logger = LogManager.getLogger(ExcelToDB.class);

	public static void toTable(SqluckyConnector dbc, String tablename, String excelFile, List<SheetFieldPo> fields,
			Integer beginRowIdx, Integer count) {
		String insertSql = InsertPreparedStatementDao.createPreparedStatementSql(tablename, fields);
		Connection conn = dbc.getConn();
		try {
			Workbook workbook = ExcelUtil.readFileToWorkbok(excelFile);

			// 读excel
			for (int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++) {
				Sheet sheet = workbook.getSheetAt(numSheet);
				if (sheet == null) {
					continue;
				}

				logger.debug("行数: = " + sheet.getLastRowNum());
				System.out.println("行数: = " + sheet.getLastRowNum());
				logger.debug("\n第一行idx: = " + sheet.getFirstRowNum());
				System.out.println("\n第一行idx: = " + sheet.getFirstRowNum());
				int begin = sheet.getFirstRowNum();
				int end = sheet.getLastRowNum();

				if (beginRowIdx != null && beginRowIdx > -1) {
					begin = beginRowIdx;
				}
				if (count != null && count > 0 && count < (end - begin)) {
					end = count;
				}

				// Read the Row
				int idx = 0;
				List<List<String>> rowVals = new ArrayList<>();
				for (int rowNum = begin; rowNum <= end; rowNum++) {
					Row hssfRow = sheet.getRow(rowNum);
					if (hssfRow != null) {
						List<String> cellVals = new ArrayList<>();
						rowVals.add(cellVals);
						// hssfRow.getLastCellNum() 有多少个列
						for (int j = 0; j < hssfRow.getLastCellNum(); j++) {
							Cell cell = hssfRow.getCell(j);
							if (cell != null) {
								String cellStr = cell.toString();
								cellVals.add(cellStr);
							} else {
								cellVals.add("");
							}
						}

						idx++;
						if (idx % 100 == 0) {
							InsertDao.execInsertBySheetFieldPo(conn, tablename, fields, rowVals);
							rowVals.clear();
						}
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
