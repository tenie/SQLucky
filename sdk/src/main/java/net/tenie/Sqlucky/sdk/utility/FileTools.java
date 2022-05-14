package net.tenie.Sqlucky.sdk.utility;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	
}
