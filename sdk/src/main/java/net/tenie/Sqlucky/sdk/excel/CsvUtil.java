package net.tenie.Sqlucky.sdk.excel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import net.tenie.Sqlucky.sdk.utility.FileTools;

public class CsvUtil {
	public static final String SPLIT_DOUBLE_QUOTATION = "(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
	public static final String SPLIT_SINGLE_QUOTATION = "(?=([^']*'[^']*')*[^']*$)";

	public static final String SPLIT_NO_QUOTATION = ",";
	public static final String SPLIT_DOUBLE_QUOTATION2 = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
	public static final String SPLIT_SINGLE_QUOTATION2 = ",(?=([^']*'[^']*')*[^']*$)";
	public static final String SPLIT_NO_QUOTATION2 = ",";

	/**
	 * 读取excel 第一页第一行
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static List<ExcelHeadCellInfo> readCsvHeadInfo(String path, String splitSymbol) {

		List<ExcelHeadCellInfo> innerlist = new ArrayList<>();
		File file = new File(path);
		String charset = FileTools.detectFileCharset(file);
		// 输入缓冲流
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))) {
			String str = null;
			while ((str = reader.readLine()) != null) {
				String[] val = str.split(splitSymbol);
				if (val != null) {
					for (int i = 0; i < val.length; i++) {
						ExcelHeadCellInfo headInfo = new ExcelHeadCellInfo();
						headInfo.setCellAddress("");
						headInfo.setCellIdx(i);
						headInfo.setCellVal(val[i]);
						innerlist.add(headInfo);
					}
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return innerlist;
	}

	/**
	 * 读取excel 第一页第一行
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static List<ExcelHeadCellInfo> readCsvHeadInfo2(String path) {

		List<ExcelHeadCellInfo> innerlist = new ArrayList<>();

		try (Reader reader = Files.newBufferedReader(Paths.get(path))) {
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);

			for (CSVRecord record : records) {
				System.out.println("Record #: " + record.getRecordNumber());
				Iterator<String> vals = record.iterator();
//		        System.out.println("ID: " + record.get(0));
//		        System.out.println("Name: " + record.get(1));
//		        System.out.println("Email: " + record.get(2));
//		        System.out.println("Country: " + record.get(3));
				int i = 0;
				while (vals.hasNext()) {
					String val = vals.next();
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

}
