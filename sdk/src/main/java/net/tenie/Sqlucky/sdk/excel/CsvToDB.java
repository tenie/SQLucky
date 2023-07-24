package net.tenie.Sqlucky.sdk.excel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;

import net.tenie.Sqlucky.sdk.db.InsertDao;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.ExcelFieldPo;
import net.tenie.Sqlucky.sdk.utility.FileTools;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/**
 * excel 导入到数据库
 * 
 * @author tenie
 *
 */
public class CsvToDB {

	private static Logger logger = LogManager.getLogger(CsvToDB.class);

	/**
	 * csv 数据 插入到数据库
	 * 
	 * @param dbc         数据库链接
	 * @param tablename   数据库表
	 * @param excelFile   excel文件路径
	 * @param fields      excel要插入的字段
	 * @param beginRowIdx 从excel第几行开始插入
	 * @param count       定义插入的行数
	 * @throws Exception
	 */
	public static void toTable(SqluckyConnector dbc, String tablename, String csvFile, String saveSqlFile,
			List<ExcelFieldPo> fields, Integer beginRowIdx, Integer count, boolean onlySaveSql, boolean saveSql,
			String splitSymbol) throws Exception {
		Connection conn = dbc.getConn();
		String errorData = "";
		try {

			int begin = 0;
			int end = -1;

			if (beginRowIdx != null && beginRowIdx > -1) {
				begin = beginRowIdx;
			}
			if (count != null && count > 0 && count < (end - begin)) {
				end = count;
			}

			// Read the Row
			int idx = 0;
			List<List<String>> rowVals = new ArrayList<>();
			File filecsv = new File(csvFile);
			String charset = FileTools.detectFileCharset(filecsv);
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(filecsv), charset))) {
				String str = null;
				while ((str = reader.readLine()) != null) {
					List<String> cellVals = new ArrayList<>();
					rowVals.add(cellVals);
					String[] record = str.split(splitSymbol);
					if (record != null) {
						for (ExcelFieldPo epo : fields) {
//							// 匹配到excel的列
//							String rowIdxStr = epo.getExcelRowIdx().get();
//							if (StrUtils.isNotNullOrEmpty(rowIdxStr)) { // 空表示没有匹配
//								Integer rowidx = Integer.valueOf(rowIdxStr);
//								// 下标从0开始, 需要减1
//								String cellStr = record[rowidx - 1];
//								cellVals.add(cellStr);
//							} else { // 使用固定值
//								String fixVal = epo.getFixedValue().get();
//								cellVals.add(fixVal);
//							}

							Integer rowIdx = epo.getRowIdx();
							if (rowIdx > -1) {
								// 下标从0开始, 需要减1
								String cellStr = record[rowIdx];
								cellVals.add(cellStr);
							} else { // 使用固定值
								String fixVal = epo.getFixedValue().get();
								cellVals.add(fixVal);
							}

						}
					}

					idx++;
					if (idx % 100 == 0) {
						errorData = InsertDao.execInsertByCsvField(conn, tablename, fields, rowVals, saveSqlFile,
								onlySaveSql, saveSql);
						rowVals.clear();
					}

				}
				if (rowVals.size() > 0) {
					errorData = InsertDao.execInsertByCsvField(conn, tablename, fields, rowVals, saveSqlFile,
							onlySaveSql, saveSql);
					rowVals.clear();
				}

			} catch (IOException ex) {
				ex.printStackTrace();
				throw ex;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}
}
