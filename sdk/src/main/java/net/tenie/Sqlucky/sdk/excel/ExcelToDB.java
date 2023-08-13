package net.tenie.Sqlucky.sdk.excel;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import net.tenie.Sqlucky.sdk.db.InsertDao;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.ImportFieldPo;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.DateUtils;

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
	public static void toTable(SqluckyConnector dbc, String tablename, Workbook workbook,
//			String excelFile, 
			String saveSqlFile, List<ImportFieldPo> fields, Integer sheetNo, Integer beginRowIdx, Integer count,
			boolean onlySaveSql, boolean saveSql) throws Exception {
		Connection conn = dbc.getConn();
		String errorData = "";
		try {

			// 读excel
			for (int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++) {
				Sheet sheet = workbook.getSheetAt(numSheet);
				if (sheet == null) {
					continue;
				}
				// 读取指定sheet页
				if (sheetNo != null && sheetNo > 0) {
					if (numSheet != (sheetNo - 1)) {
						continue;
					}
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

					for (ImportFieldPo epo : fields) {
						Integer rowIdx = epo.getFieldIdx();
						if (rowIdx > -1) {
							Cell cell = hssfRow.getCell(rowIdx);
							CellType ct = cell.getCellType();
							if (ct.equals(CellType.NUMERIC)) {
								int javaType = epo.getColumnType().get();
								boolean isDate = CommonUtils.isDateAndDateTime(javaType);
//								boolean isString = CommonUtils.isString(javaType);
								if (isDate) {
									Date cellDate = cell.getDateCellValue();
									String cellDateStr = DateUtils.DateOrDateTimeToString(javaType, cellDate);
									cellVals.add(cellDateStr);
//								} else if (isString) {

								} else {
									String cellStr = cell.toString();
									cellVals.add(cellStr);
								}

							} else {
								String cellStr = cell.toString();
								cellVals.add(cellStr);
							}

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
//					errorData = InsertDao.execInsertByExcelField(conn, tablename, fields, rowVals, saveSqlFile,
//							onlySaveSql);
//					rowVals.clear();
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

	public static void toTable3(SqluckyConnector dbc, String tablename, Workbook workbook,
//			String excelFile, 
			String saveSqlFile, List<ImportFieldPo> fields, Integer sheetNo, Integer beginRowIdx, Integer count,
			boolean onlySaveSql, boolean saveSql) throws Exception {
		Connection conn = dbc.getConn();
		String errorData = "";
		try {
			// 读excel
			for (int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++) {
				Sheet sheet = workbook.getSheetAt(numSheet);
				if (sheet == null) {
					continue;
				}
				// 读取指定sheet页
				if (sheetNo != null && sheetNo > 0) {
					if (numSheet != (sheetNo - 1)) {
						continue;
					}
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

				int line = end + 1;
				int limit = 10000;
				int add = 10000;
				int tmpbegin = begin;
				int tmpend = 0;
//				Thread t = null;
				List<Thread> threadls = new ArrayList<>();
				if (limit < line) {

					while (limit < line) {
						if (limit != 10000) {
							tmpbegin = limit - add;
						}

						tmpend = limit;
						limit += add;
						System.out.println(tmpbegin + " | " + tmpend);
						Thread t = usethreadPool(sheet, fields, conn, tablename, saveSqlFile, onlySaveSql, saveSql,
								tmpbegin, tmpend);
						threadls.add(t);
					}
					System.out.println(limit + " | " + end);

					if (tmpend < line) {
						Thread t = usethreadPool(sheet, fields, conn, tablename, saveSqlFile, onlySaveSql, saveSql,
								tmpend, line);
						threadls.add(t);
					}

				} else {
					Thread t = usethreadPool(sheet, fields, conn, tablename, saveSqlFile, onlySaveSql, saveSql, begin,
							line);
					threadls.add(t);

				}
				if (threadls.size() > 0) {
					threadls.parallelStream().forEach(thread -> {
						try {
							thread.join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					});
				}
				logger.debug("完成1");
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	public static Thread usethreadPool(Sheet sheet, List<ImportFieldPo> fields, Connection conn, String tablename,
			String saveSqlFile, boolean onlySaveSql, boolean saveSql, int begin, int end) {
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					int idx = 0;
					List<List<String>> rowVals = new ArrayList<>();
					for (int rowNum = begin; rowNum < end; rowNum++) {
						logger.debug("rowNum = " + rowNum);
						Row hssfRow = sheet.getRow(rowNum);
						if (hssfRow == null) {
							continue;
						}
						List<String> cellVals = new ArrayList<>();
						rowVals.add(cellVals);

						for (ImportFieldPo epo : fields) {

							Integer rowIdx = epo.getFieldIdx();
							if (rowIdx > -1) {
								Cell cell = hssfRow.getCell(rowIdx);
								CellType ct = cell.getCellType();
								if (ct.equals(CellType.NUMERIC)) {
									int javaType = epo.getColumnType().get();
									boolean isDate = CommonUtils.isDateAndDateTime(javaType);
									boolean isString = CommonUtils.isString(javaType);
									if (isDate) {
										Date cellDate = cell.getDateCellValue();
										String cellDateStr = DateUtils.DateOrDateTimeToString(javaType, cellDate);
										cellVals.add(cellDateStr);
//									} else if (isString) {

									} else {
										String cellStr = cell.toString();
										cellVals.add(cellStr);
									}

								} else {
									String cellStr = cell.toString();
									cellVals.add(cellStr);
								}

							} else { // 使用固定值
								String fixVal = epo.getFixedValue().get();
								cellVals.add(fixVal);
							}

						}

						idx++;
						if (idx % 100 == 0) {
							InsertDao.execInsertByExcelField(conn, tablename, fields, rowVals, saveSqlFile,
									onlySaveSql);
							rowVals.clear();
						}
//						errorData = InsertDao.execInsertByExcelField(conn, tablename, fields, rowVals, saveSqlFile,
//								onlySaveSql);
//						rowVals.clear();
					}
					if (rowVals.size() > 0) {
						InsertDao.execInsertByExcelField(conn, tablename, fields, rowVals, saveSqlFile, onlySaveSql);
						rowVals.clear();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {

				}
			}
		};
		t.start();
//		try {
//			t.join();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		return t;
	}

	public static void main(String[] args) {
		int line = 231200;
		int limit = 10000;
		int add = 10000;
		int begin = 2;
		int end = 0;
		while (limit < line) {
			if (limit != 10000) {
				begin = limit - add;
			}
			end = limit;
			limit += add;
			System.out.println(begin + " | " + end);

		}
		System.out.println(limit + " | " + end);
	}
}
