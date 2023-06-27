package net.tenie.Sqlucky.sdk.excel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadExcel2 {

	private static Logger logger = LogManager.getLogger(ReadExcel2.class);

	/**
	 * Read the Excel 2010
	 * 
	 */
	public static List<ArrayList<String>> readXlsx(String path, Integer beginRowIdx, Integer endRowIdx)
			throws IOException {

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
			int lastRowNum = xssfSheet.getLastRowNum();
			logger.debug("lastRowNum =" + lastRowNum);

			int begin = 0;
			int end = lastRowNum;

			if (endRowIdx != null && endRowIdx > 0 && endRowIdx < end) {
				end = endRowIdx;
			}
			if (beginRowIdx != null && beginRowIdx > -1) {
				begin = beginRowIdx;
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
	public static List<ArrayList<String>> readXls(String path, Integer beginRowIdx, Integer endRowIdx)
			throws IOException {

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

			int begin = 0;
			int end = hssfSheet.getLastRowNum();

			if (endRowIdx != null && endRowIdx > 0 && endRowIdx < end) {
				end = endRowIdx;
			}
			if (beginRowIdx != null && beginRowIdx > -1) {
				begin = beginRowIdx;
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
