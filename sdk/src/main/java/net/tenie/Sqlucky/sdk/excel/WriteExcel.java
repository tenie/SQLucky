package net.tenie.Sqlucky.sdk.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import net.tenie.Sqlucky.sdk.subwindow.MyAlert;

public class WriteExcel {

	/**
	 * 创建一个. xls , 并写入数据 	workBook = createBlankXlsFile(finalXlsxFile, sheetName);
	 * 
	 * @param dataList      数据
	 * @param cloumnCount
	 * @param finalXlsxPath 文件存在就删除,再创建
	 * @throws Exception
	 */
	public static void createExcel(Workbook workBook  , ExcelDataPo dataPo, File finalXlsxFile) throws Exception {
		OutputStream out = null;
		try {
			// 获取第一个sheet
			Sheet sheet = workBook.getSheetAt(0);

			// 给sheet 设置表头
			int tableHeader = 0; // 表头行数, 用于写入数据时, 确保数据在表头之下
			List<String> fields = dataPo.getHeaderFields();
			if (fields != null && fields.size() != 0) {
				// 创建表头
				Row headerRow = sheet.createRow(0);
				for (int i = 0; i < fields.size(); i++) {
					String fieldName = fields.get(i);
					Cell cellTmp = headerRow.createCell(i);
					cellTmp.setCellValue(fields.get(i));

					// 设置列宽带
					var cw = sheet.getColumnWidth(i);
					int colWidth = fieldName.length() * 330;
					if (colWidth > cw) {
						sheet.setColumnWidth(i, colWidth);
					}

				}
				tableHeader++;

			}
			/**
			 * 往Excel中写新数据
			 */
			var dataList = dataPo.getDatas();
			int exportRowSize = dataList.size();
			if (exportRowSize > 65536) {
				boolean tf = MyAlert
						.myConfirmationShowAndWait("Excel 最大导出为 65536行, 当前数据为: " + exportRowSize + ", 继续导出?");
				if (tf) {
					exportRowSize = 65536;
				}
			}

			for (int j = 0; j < exportRowSize; j++) {
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
			if (workBook != null) {
				try {
					workBook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("数据导出成功");
	}
	

}	
