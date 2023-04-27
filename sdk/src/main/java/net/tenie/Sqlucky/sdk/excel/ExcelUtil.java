package net.tenie.Sqlucky.sdk.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {
	private static final String EXCEL_XLS = "xls";
	private static final String EXCEL_XLSX = "xlsx";
	/* 
	 * 读取excel文件, 根据xls和xlsx格式调用不同的函数
	 */
//	public static 	List<ArrayList<String>>  readExcelFile(String filename) throws IOException {
//		String suffixStr = filename.substring(filename.lastIndexOf("."), filename.length());
//		System.out.println(suffixStr);
//		
//		List<ArrayList<String>>  rs = new ArrayList<>(); //new ReadExcel2().readXls(filename);
//		
//		if(".xls".equals(suffixStr)){
//			 System.out.println("===== 开始执行 xls 方法=====");
//			 rs =  new ReadExcel().readXls(filename);
//		}else if(".xlsx".equals(suffixStr)){
//			 System.out.println("===== 开始执行 xlsx 方法=====");
//			 rs =  new ReadExcel().readXlsx(filename);
//		}else{
//			 System.out.println("===== 没有执行 xls 方法=====");
//		}
//		return rs; 
//	}
	
	
	/**
	 * 创建一个 .xls文件, 如果文件存在,就先删除旧文件
	 * @param fileName
	 * @throws IOException
	 */
	public static Workbook createBlankXlsFile(File file, String sheetName) throws IOException {
		
		if(file.exists()) {
			file.delete();
			file.createNewFile();
		}
		FileOutputStream fileOutputStream = null;
		Workbook hWorkbook = new HSSFWorkbook();
		try {
			// 创建workbook
			
			hWorkbook.createSheet(sheetName);
			fileOutputStream = new FileOutputStream(file);
			hWorkbook.write(fileOutputStream);
		} finally {
			if(fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			}

			hWorkbook.close();
				
		}
		return hWorkbook;

	}
	
	 /**
	    * 创建一个. xls , 并写入数据
	    * @param dataList 	数据
	    * @param cloumnCount  
	    * @param finalXlsxPath 文件存在就删除,再创建
	    * @throws Exception 
	    */
		public static void createXlsWritValue(ExcelDataPo dataPo, File finalXlsxFile ) throws Exception {
			OutputStream out = null;
			Workbook workBook = null;
			try {
				var sheetName = dataPo.getSheetName();
				 
			    workBook = createBlankXlsFile(finalXlsxFile, sheetName);
			
				// 获取第一个sheet 
				Sheet sheet = workBook.getSheetAt(0); 
				
				// 给sheet 设置表头
				int tableHeader = 0;  // 表头行数, 用于写入数据时, 确保数据在表头之下
				List<String> fields = dataPo.getHeaderFields();
				if(fields != null && fields.size() != 0) {
					// 创建表头
					Row headerRow = sheet.createRow(0);
					for(int i = 0 ; i< fields.size(); i++) {
						String fieldName = fields.get(i);
						Cell cellTmp = headerRow.createCell(i);
						cellTmp.setCellValue(fields.get(i));
						
						// 设置列宽带
						var cw = sheet.getColumnWidth(i);
						int colWidth =  fieldName.length()* 330;
						if(colWidth > cw) {
							sheet.setColumnWidth(i,  colWidth);
						}
						
					}
					tableHeader++;
					
				}
				/**
				 * 往Excel中写新数据
				 */
				var dataList = dataPo.getDatas();
				for (int j = 0; j < dataList.size(); j++) {
					// 创建一行数据：在表头之下, 如果没有表头就是第一行开始
					Row row = sheet.createRow(j + tableHeader);
				
					// 得到要插入的每一条记录
					List<String> data = dataList.get(j); 
					for (int k = 0; k < data.size(); k++) {
						// 在一行内循环
						Cell cell = row.createCell(k); 
						cell.setCellValue(data.get(k)); 
					}
				}
				// 创建文件输出流，准备输出电子表格：这个必须有，否则你在sheet上做的任何操作都不会有效
				out = new FileOutputStream(finalXlsxFile);
				workBook.write(out);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				try {
					if (out != null) {
						out.flush();
						out.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(workBook !=null) { 
					try {
						workBook.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			System.out.println("数据导出成功");
		}
	
		/**
		 * 判断Excel的版本,获取Workbook
		 * 
		 * @param in
		 * @param filename
		 * @return
		 * @throws IOException
		 */
		public static Workbook getWorkbok(File file) throws IOException {
			if(file.exists() == false) {
				file.createNewFile();
			}
			Workbook wb = null;
			FileInputStream in = new FileInputStream(file);
			if (file.getName().endsWith(EXCEL_XLS)) { // Excel&nbsp;2003
				wb = new HSSFWorkbook(in);
			} else if (file.getName().endsWith(EXCEL_XLSX)) { // Excel 2007/2010
				wb = new XSSFWorkbook(in);
			}
			return wb;
		}
	public static void main(String[] args) {
		try {
//			createBlankXlsFile(new File("D:/textXls2.xls"));
			
			ArrayList<String> data1 = new ArrayList<String>();
			data1.add("BankName?");
			data1.add("aaa?");
			data1.add("bbb?");
			data1.add("ccc?");
			 
			
			ArrayList<String> data2 = new ArrayList<String>();
			data2.add("111?");
			data2.add("2222?");
			data2.add("333?");
			data2.add("44444555555?");
			ArrayList<String> data3 = new ArrayList<String>();
			data3.add("333?");
			data3.add("333?");
			data3.add("333?");
			data3.add("333?");
			 
			List<List<String>> list = new ArrayList<>();
			list.add(data2);
			list.add(data3); 
			
			ExcelDataPo po = new ExcelDataPo();
			po.setSheetName("数据1");
			po.setHeaderFields(data1);
			po.setDatas(list);
			
			createXlsWritValue(po, new File("D:/workbook2.xls"));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
