package net.tenie.Sqlucky.sdk.utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;

/**
 * 
 * @author tenie
 *
 */
public class FileTools {
	private static Logger logger = LogManager.getLogger(FileTools.class);
	
	public static void save(File file, String data ) throws IOException {
		FileUtils.writeStringToFile( file,  data,"UTF-8" ); 
	}
	public static void saveByEncode(String fileName, String data , String encode ) throws IOException {
//		String encode = ComponentGetter.getFileEncode(fileName);
		if(StrUtils.isNotNullOrEmpty(encode)) {
			save(fileName, data, encode );
		}else {
			FileUtils.writeStringToFile( new File(fileName),  data,"UTF-8" ); 
		}
	
	}
	
	public static void save(String fileName, String data, String encodeing ) throws IOException {
		
		logger.info(fileName );
		logger.info(data );
		logger.info(encodeing );
		FileUtils.writeStringToFile( new File(fileName),  data, encodeing); 
	}
	
	public static String dirName(String filename) { 
		return FilenameUtils.getFullPath(filename);
	}
	
	public static String fileName(String filename) { 
		return FilenameUtils.getName(filename);
	}
	
	public static String read(String f) {
		File file = new File(f);
		String val = "";
		try {
		     val = FileUtils.readFileToString(file,"utf-8");
		} catch (IOException e) { 
			e.printStackTrace();
		}
		return val;
	}
	public static String read(File file) {
		String val = "";
		try {
		     val = FileUtils.readFileToString(file,"utf-8");
		} catch (IOException e) { 
			e.printStackTrace();
		}
		return val;
	}
	
	
	// 选择文件
	public static String selectFile() {
		String fp = "";
		File f = FileOrDirectoryChooser.showOpenAllFile("Open", new Stage());
		if (f != null) { 
		    fp = f.getAbsolutePath();
		}
//		host.setText(fp);
		return fp;
	}
	
	// 选择文件
	public static File selectJsonFile() {
		// 获取文件
		File f = FileOrDirectoryChooser.showOpenJsonFile("Open", ComponentGetter.primaryStage);
		return f;
	}
	
	
	// 目录下的所有文件
	public static List<File> getFileFromDir(File dirFile){
		 List<File> fls = new ArrayList<>();
		 File arrFile[] =  dirFile.listFiles();
		 for(File fl: arrFile) {
			 if(fl.isFile()) {
				 fls.add(fl);
			 }
		 }
		  	 
		 return fls;
	}
	// 目录下的所有文件, 包括子目录
	public static List<File> getAllFileFromDir(File dir){
		 List<File> fls = new ArrayList<>();
		 
		 File arrFile[] =  dir.listFiles();
		 for(File fl: arrFile) {
			 if(fl.isFile()) {
				 fls.add(fl);
			 }
			 if(fl.isDirectory()) {
				 List<File> vals =  getAllFileFromDir(fl);
				 fls.addAll(vals);
			 } 
		 } 
		 return fls;
	}
	
	/**
	 * 读取文件内容, 判断内容中是否包含查询字符串
	 * @param file
	 * @param queryStr
	 * @return
	 */
	public static String fileExistQueryStr(File file, String queryStr) {
		if(file.isFile()) {
			String valStr = FileTools.read(file);
			if(valStr.contains(queryStr)) {
				return valStr;
			}
		}
		
		return null;
	}
	
}
