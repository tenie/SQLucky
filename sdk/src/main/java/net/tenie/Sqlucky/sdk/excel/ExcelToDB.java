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
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.ExcelFieldPo;

/**
 * excel 导入到数据库
 * 
 * @author tenie
 *
 */
public class ExcelToDB {

	private static Logger logger = LogManager.getLogger(ExcelToDB.class);

	/**
	 * excel 数据 插入到数据库
	 * 
	 * @param dbc         数据库链接
	 * @param tablename   数据库表
	 * @param excelFile   excel文件路径
	 * @param fields      excel要插入的字段
	 * @param beginRowIdx 从excel第几行开始插入
	 * @param count       定义插入的行数
	 * @throws Exception
	 */
	public static void toTable(SqluckyConnector dbc, String tablename, String excelFile, String saveSqlFile,
			List<ExcelFieldPo> fields, Integer beginRowIdx, Integer count, boolean onlySaveSql) throws Exception {
		Connection conn = dbc.getConn();
		String errorData = "";
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
					if (hssfRow == null) {
						continue;
					}
					List<String> cellVals = new ArrayList<>();
					rowVals.add(cellVals);

					for (ExcelFieldPo epo : fields) {
						// 匹配到excel的列
//						String rowIdxStr = epo.getExcelRowIdx().get();
//						String rowIdxStr = "";
//						String rowVal = epo.getExcelRowVal().get();
//						if (StrUtils.isNotNullOrEmpty(rowVal)) {
//							String[] excelInfo = rowVal.split(" - ");
//							rowIdxStr = excelInfo[0];
//						}

//						if (StrUtils.isNotNullOrEmpty(rowIdxStr)) { // 空表示没有匹配
//							Integer rowidx = Integer.valueOf(rowIdxStr);
//							// 下标从0开始, 需要减1
//							Cell cell = hssfRow.getCell(rowidx - 1);
//							String cellStr = cell.toString();
//							cellVals.add(cellStr);
//						} else { // 使用固定值
//							String fixVal = epo.getFixedValue().get();
//							cellVals.add(fixVal);
//						}

						Integer rowIdx = epo.getRowIdx();
						if (rowIdx > -1) {
							Cell cell = hssfRow.getCell(rowIdx);
							String cellStr = cell.toString();
							cellVals.add(cellStr);
						} else { // 使用固定值
							String fixVal = epo.getFixedValue().get();
							cellVals.add(fixVal);
						}

					}

					idx++;
					if (idx % 100 == 0) {
						errorData = InsertDao.execInsertByExcelField(conn, tablename, fields, rowVals, saveSqlFile,
								onlySaveSql);
						rowVals.clear();
					}
					errorData = InsertDao.execInsertByExcelField(conn, tablename, fields, rowVals, saveSqlFile,
							onlySaveSql);
					rowVals.clear();
				}
				if (rowVals.size() > 0) {
					errorData = InsertDao.execInsertByExcelField(conn, tablename, fields, rowVals, saveSqlFile,
							onlySaveSql);
					rowVals.clear();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

}
