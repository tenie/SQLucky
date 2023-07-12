package net.tenie.Sqlucky.sdk.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CsvUtil { 
	// 获取excel头部(第一行)
	public static List<ExcelHeadCellInfo> readCsvFileHead(String filename) {
		String suffixStr = filename.substring(filename.lastIndexOf("."), filename.length());

		List<ExcelHeadCellInfo> rs = new ArrayList<>();
		try {
			System.out.println("===== 开始执行 csv 方法=====");
			rs = readCsvHeadInfo(filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	
	
	/**
	 * 读取excel 第一页第一行
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static List<ExcelHeadCellInfo> readCsvHeadInfo(String path)  {

		List<ExcelHeadCellInfo> innerlist = new ArrayList<>();

		try (Reader reader = Files.newBufferedReader(Paths.get(path))) {
		    Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);
		    
		    for (CSVRecord record : records) {
		        System.out.println("Record #: " + record.getRecordNumber());
		        Iterator<String> vals =   record.iterator();
//		        System.out.println("ID: " + record.get(0));
//		        System.out.println("Name: " + record.get(1));
//		        System.out.println("Email: " + record.get(2));
//		        System.out.println("Country: " + record.get(3));
		        int i = 0;
		        while(vals.hasNext()) {
		        	String  val = vals.next();
		        	ExcelHeadCellInfo headInfo = new ExcelHeadCellInfo();
		        	headInfo.setCellAddress("");
					headInfo.setCellIdx(i++);
					headInfo.setCellVal(val);
					innerlist.add(headInfo);
		        }
		        break;
		    }
		} catch (IOException ex) {
		    ex.printStackTrace();
		}

		return innerlist;
	}
	public static void main(String[] args) throws IOException {
		List<ExcelHeadCellInfo> ls = readCsvHeadInfo("C:\\Users\\tenie\\app_ver.csv");
		System.out.println(ls);
	}
}
