package net.tenie.Sqlucky.sdk.excel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadExcel2 {

	private static Logger logger = LogManager.getLogger(ReadExcel2.class);

	/**
	 * 通过Workbook 读 excel
	 * 
	 */
	public static List<ArrayList<String>> readExcel(Workbook workbook, Integer beginRowIdx, Integer count) throws IOException {

 
		List<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		// Read the Sheet
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
			for (int rowNum = begin; rowNum <= end; rowNum++) {
				Row hssfRow = sheet.getRow(rowNum);
				if (hssfRow != null) {
					ArrayList<String> innerlist = new ArrayList<>();
					// hssfRow.getLastCellNum() 有多少个列
					for (int j = 0; j < hssfRow.getLastCellNum(); j++) {
						if (hssfRow.getCell(j) != null) {
							String cellStr = hssfRow.getCell(j).toString();
							innerlist.add(cellStr);
						} else {
							innerlist.add("");
						}
					}

					list.add(innerlist);
				}
			}
		}
		return list;
	
	}

	
	/**
	 * Read the Excel 2010
	 * 
	 */
	public static List<ArrayList<String>> readXlsx(String path, Integer beginRowIdx, Integer count) throws IOException {

		InputStream is = new FileInputStream(path);
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);

		List<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		// Read the Sheet
		for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
			XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
			if (xssfSheet == null) {
				continue;
			}
			// 读取行数
			logger.debug("lastRowNum =" + xssfSheet.getLastRowNum());

			int begin = xssfSheet.getFirstRowNum();
			int end = xssfSheet.getLastRowNum();

			if (beginRowIdx != null && beginRowIdx > -1) {
				begin = beginRowIdx;
			}
			if (count != null && count > 0 && count < (end - begin)) {
				end = count;
			}

			for (int rowNum = begin; rowNum <= end; rowNum++) {
				XSSFRow xssfRow = xssfSheet.getRow(rowNum);
				if (xssfRow != null) {

					ArrayList<String> innerlist = new ArrayList<>();

					for (int j = 0; j < xssfRow.getLastCellNum(); j++) {
						if (xssfRow.getCell(j) != null) {
							innerlist.add(xssfRow.getCell(j).toString());
						} else {
							innerlist.add("");
						}
					}

					list.add(innerlist);
				}
			}
		}
		logger.debug("excel lines = " + list.size() + ";" + new Date());
		return list;
	}

	/**
	 * Read the Excel 2003-2007
	 */
	public static List<ArrayList<String>> readXls(String path, Integer beginRowIdx, Integer count) throws IOException {

		InputStream is = new FileInputStream(path);
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);

		List<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		// Read the Sheet
		for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
			HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
			if (hssfSheet == null) {
				continue;
			}

			logger.debug("行数: = " + hssfSheet.getLastRowNum());
			System.out.println("行数: = " + hssfSheet.getLastRowNum());
			logger.debug("\n第一行idx: = " + hssfSheet.getFirstRowNum());
			System.out.println("\n第一行idx: = " + hssfSheet.getFirstRowNum());
			int begin = hssfSheet.getFirstRowNum();
			int end = hssfSheet.getLastRowNum();

			if (beginRowIdx != null && beginRowIdx > -1) {
				begin = beginRowIdx;
			}
			if (count != null && count > 0 && count < (end - begin)) {
				end = count;
			}

			// Read the Row
			for (int rowNum = begin; rowNum <= end; rowNum++) {
				HSSFRow hssfRow = hssfSheet.getRow(rowNum);
				if (hssfRow != null) {
					ArrayList<String> innerlist = new ArrayList<>();
					// hssfRow.getLastCellNum() 有多少个列
					for (int j = 0; j < hssfRow.getLastCellNum(); j++) {
						if (hssfRow.getCell(j) != null) {
							String cellStr = hssfRow.getCell(j).toString();
							innerlist.add(cellStr);
						} else {
							innerlist.add("");
						}
					}

					list.add(innerlist);
				}
			}
		}
		return list;
	}

	/**
	 * 读取excel 第一页第一行
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static List<ExcelHeadCellInfo> readXlsHeadInfo(String path) throws IOException {

		InputStream is = new FileInputStream(path);
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
		List<ExcelHeadCellInfo> innerlist = new ArrayList<>();
		// 第一页
		HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
		if (hssfSheet == null) {
			return null;
		}

		logger.debug("行数: = " + hssfSheet.getLastRowNum());
		System.out.println("行数: = " + hssfSheet.getLastRowNum());
		logger.debug("\n第一行idx: = " + hssfSheet.getFirstRowNum());
		System.out.println("\n第一行idx: = " + hssfSheet.getFirstRowNum());

		HSSFRow hssfRow = hssfSheet.getRow(hssfSheet.getFirstRowNum());
		if (hssfRow != null) {

			// hssfRow.getLastCellNum() 有多少个列
			for (int j = 0; j < hssfRow.getLastCellNum(); j++) {
				HSSFCell cell = hssfRow.getCell(j);
				if (cell != null) {
					String cellStr = cell.toString();
					CellAddress address = cell.getAddress();

					ExcelHeadCellInfo headInfo = new ExcelHeadCellInfo();
					headInfo.setCellAddress(address.toString());
					headInfo.setCellIdx(j);
					headInfo.setCellVal(cellStr);

					innerlist.add(headInfo);
				} else {
					innerlist.add(new ExcelHeadCellInfo());
				}
			}

		}

		return innerlist;
	}

	/**
	 * Read the Excel 2010
	 * 
	 */
	public static List<ExcelHeadCellInfo> readXlsxHeadInfo(String path) throws IOException {

		InputStream is = new FileInputStream(path);
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
		List<ExcelHeadCellInfo> innerlist = new ArrayList<>();
		// Read the Sheet
		XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
		if (xssfSheet == null) {
			return null;
		}
		// 读取行数
		int lastRowNum = xssfSheet.getLastRowNum();
		logger.debug("lastRowNum =" + lastRowNum);

		XSSFRow xssfRow = xssfSheet.getRow(xssfSheet.getFirstRowNum());
		if (xssfRow != null) {
			for (int j = 0; j < xssfRow.getLastCellNum(); j++) {
				XSSFCell cell = xssfRow.getCell(j);
				if (cell != null) {

					String cellStr = cell.toString();
					CellAddress address = cell.getAddress();

					ExcelHeadCellInfo headInfo = new ExcelHeadCellInfo();
					headInfo.setCellAddress(address.toString());
					headInfo.setCellIdx(j);
					headInfo.setCellVal(cellStr);

					innerlist.add(headInfo);

				} else {
					innerlist.add(new ExcelHeadCellInfo());
				}
			}

		}
		return innerlist;
	}

//	@SuppressWarnings("static-access")
//	private String getValue(XSSFCell xssfRow) {
//		if (xssfRow.getCellType() == xssfRow.CELL_TYPE_BOOLEAN) {
//			return String.valueOf(xssfRow.getBooleanCellValue());
//		} else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_NUMERIC) {
//			return String.valueOf(xssfRow.getNumericCellValue());
//		} else {
//			return String.valueOf(xssfRow.getStringCellValue());
//		}
//	}
//
//	@SuppressWarnings("static-access")
//	private String getValue(HSSFCell hssfCell) {
//		if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
//			return String.valueOf(hssfCell.getBooleanCellValue());
//		} else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
//			return String.valueOf(hssfCell.getNumericCellValue());
//		} else {
//			return String.valueOf(hssfCell.getStringCellValue());
//		}
//	}
}
