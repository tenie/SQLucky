package net.tenie.lib.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import net.tenie.fx.component.ComponentGetter;
import net.tenie.lib.tools.StrUtils;
/*   @author tenie */
public class SaveFile {
	public static void save(File file, String data ) throws IOException {
		FileUtils.writeStringToFile( file,  data,"UTF-8" ); 
	}
	public static void save(String fileName, String data ) throws IOException {
		String encode = ComponentGetter.getFileEncode(fileName);
		if(StrUtils.isNotNullOrEmpty(encode)) {
			save(fileName, data, encode );
		}else {
			FileUtils.writeStringToFile( new File(fileName),  data,"UTF-8" ); 
		}
	
	}
	
	public static void save(String fileName, String data, String encodeing ) throws IOException {
		FileUtils.writeStringToFile( new File(fileName),  data, encodeing); 
	}
	
	public static String dirName(String filename) { 
		return FilenameUtils.getFullPath(filename);
	}
	
	public static String fileName(String filename) { 
		return FilenameUtils.getName(filename);
	}
	
}
