package net.tenie.Sqlucky.sdk.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {
	private static final String EXCEL_XLS = ".xls";
	private static final String EXCEL_XLSX = ".xlsx";

	public static void createExcel(ExcelDataPo dataPo, File finalXlsxFile) {
		var sheetName = dataPo.getSheetName();
		try {
			Workbook workBook = writeFileToWorkbook(finalXlsxFile, sheetName);
			WriteExcel.createExcel(workBook, dataPo, finalXlsxFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断Excel的版本,获取Workbook
	 * 
	 * @param in
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static Workbook readFileToWorkbok(File file) throws IOException {
		if (file.exists() == false) {
			file.createNewFile();
		}
		Workbook wb = null;
		FileInputStream in = new FileInputStream(file);
		if (file.getName().endsWith(EXCEL_XLS)) { // Excel&nbsp;2003
			wb = new HSSFWorkbook(in);
		} else if (file.getName().endsWith(EXCEL_XLSX)) { // Excel 2007/2010
//			wb = new XSSFWorkbook(in);
			try {
				wb = new XSSFWorkbook(OPCPackage.open(file, PackageAccess.READ));
			} catch (InvalidFormatException | IOException e) {
				e.printStackTrace();
			}

		}
		return wb;
	}

	public static Workbook readFileToWorkbok(String fname) throws IOException {
		File file = new File(fname);
		return readFileToWorkbok(file);
	}

	/**
	 * 创建一个 .xls文件, 如果文件存在,就先删除旧文件
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public static Workbook writeFileToWorkbook(File file, String sheetName) throws IOException {

		if (file.exists()) {
			file.delete();
			file.createNewFile();
		}
		FileOutputStream fileOutputStream = null;
		Workbook workbook = null;
		try {
			if (file.getName().endsWith(EXCEL_XLS)) { // Excel&nbsp;2003
				workbook = new HSSFWorkbook();
				workbook.createSheet(sheetName);
				fileOutputStream = new FileOutputStream(file);
				workbook.write(fileOutputStream);
			} else if (file.getName().endsWith(EXCEL_XLSX)) { // Excel 2007/2010
				workbook = new XSSFWorkbook();
				workbook.createSheet(sheetName);
				fileOutputStream = new FileOutputStream(file);
				workbook.write(fileOutputStream);
			}

		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			}

//			workbook.close();

		}
		return workbook;
	}

	/*
	 * 读取excel文件, 根据xls和xlsx格式调用不同的函数
	 */
	public static List<ArrayList<String>> readExcelFile(String filename, Integer beginRowIdx, Integer count) {
		String suffixStr = filename.substring(filename.lastIndexOf("."), filename.length());

		List<ArrayList<String>> rs = new ArrayList<>();
		try {
			if (".xls".equals(suffixStr)) {
				System.out.println("===== 开始执行 xls 方法=====");
				rs = ReadExcel.readXls(filename, beginRowIdx, count);
			} else if (".xlsx".equals(suffixStr)) {
				System.out.println("===== 开始执行 xlsx 方法=====");
				rs = ReadExcel.readXlsx(filename, beginRowIdx, count);
			} else {
				System.out.println("===== 没有执行 xls 方法=====");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	public static List<ArrayList<String>> readExcel(String filename, Integer beginRowIdx, Integer count)
			throws IOException {
		Workbook workbook = readFileToWorkbok(new File(filename));
		List<ArrayList<String>> rs = new ArrayList<>();
		rs = ReadExcel.readExcel(workbook, beginRowIdx, count);
		return rs;
	}

	public static List<ArrayList<String>> readExcelFile(String filename) {
		return readExcelFile(filename, null, null);
	}

	// 获取excel头部(第一行)
	public static List<ExcelHeadCellInfo> readExcelFileHead(String filename) {
		String suffixStr = filename.substring(filename.lastIndexOf("."), filename.length());

		List<ExcelHeadCellInfo> rs = new ArrayList<>();
		try {
			if (".xls".equals(suffixStr)) {
				System.out.println("===== 开始执行 xls 方法=====");
				rs = ReadExcel.readXlsHeadInfo(filename);
			} else if (".xlsx".equals(suffixStr)) {
				System.out.println("===== 开始执行 xlsx 方法=====");
				rs = ReadExcel.readXlsxHeadInfo(filename);
			} else {
				System.out.println("===== 没有执行 xls 方法=====");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

}
