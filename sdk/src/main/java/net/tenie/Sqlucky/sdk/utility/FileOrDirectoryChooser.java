package net.tenie.Sqlucky.sdk.utility;

import java.io.File;
import org.apache.commons.io.FileUtils;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.config.ConfigVal;

/**
 * 操作系统的文件选择窗口
 * @author tenie
 *
 */
public final class FileOrDirectoryChooser {
	
	// 获取打开文件的目录
	public static File getOpenfileDir() { 
//		return  FileUtils.getUserDirectory();
			if(StrUtils.isNullOrEmpty(ConfigVal.openfileDir)) {
				return  FileUtils.getUserDirectory();
			}else {
				 
				File f = new File(ConfigVal.openfileDir);
				if(f.isFile()) {
					String fp = f.getParent(); 
				    f =  new File(fp);
				} 
				return  f;
			} 
	}
	
	// save file
	private static FileChooser fileChooser = new FileChooser(); 
	private static DirectoryChooser directoryChooser  = new DirectoryChooser();

	// default configure
	private static void configureFileChooser(final FileChooser fileChooser, String title) {
		configureFileChooser(fileChooser, title , null);
	}
	
	private static void configureFileChooser(final FileChooser fileChooser, String title, String filename) {
		fileChooser.setTitle(title);
		File dir =  getOpenfileDir();
		
		fileChooser.setInitialDirectory( dir);
		fileChooser.getExtensionFilters().clear();
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("sql", "*.sql"),
				new FileChooser.ExtensionFilter("txt", "*.txt"), 
				new FileChooser.ExtensionFilter("csv", "*.csv"),
				new FileChooser.ExtensionFilter("All", "*.*")); 
		if(filename !=null ) {
			fileChooser.setInitialFileName(filename);
			 
		}
	}

	private static void allFileChooser(final FileChooser fileChooser, String title) {
		fileChooser.setTitle(title);
	    fileChooser.setInitialDirectory( new File(System.getProperty("user.home"))  );
		fileChooser.getExtensionFilters().clear();
		fileChooser.getExtensionFilters().addAll( 
				new FileChooser.ExtensionFilter("All", "*.*"));
	}
	
	
	// 选择目录
	public static DirectoryChooser getDirChooser(String title) {
		directoryChooser.setTitle(title);
		File dir =  getOpenfileDir();
		
		directoryChooser.setInitialDirectory( dir);
//		directoryChooser.getExtensionFilters().clear();
//		fileChooser.getExtensionFilters().addAll(
//				new FileChooser.ExtensionFilter("sql", "*.sql"),
//				new FileChooser.ExtensionFilter("txt", "*.txt"), 
//				new FileChooser.ExtensionFilter("csv", "*.csv"),
//				new FileChooser.ExtensionFilter("All", "*.*")); 
//		if(filename !=null ) {
//			fileChooser.setInitialFileName(filename);
//			 
//		}
		return directoryChooser;	
  }
	
	
	public static FileChooser getDefaultFileChooser(String title) {
		configureFileChooser(fileChooser, title);
		return fileChooser;
	}
	
	public static FileChooser getFileChooserInitFileName(String title, String filename) {
		configureFileChooser(fileChooser, title, filename);
		return fileChooser;
	}
	
	public static FileChooser getAllFileChooser(String title) {
		allFileChooser(fileChooser, title);
		return fileChooser;
	}
	
	
	
	

	// new
	public static FileChooser getFileChooser(String title) {
		return new FileChooser();
	}

	// 获取保存的文件绝对路径名(默认)
	public static String showSaveDefaultStr(String title, Stage stage) {
		File file = showSaveDefault(title, stage);
		String rs = "";
		if (file != null) {
			rs = file.getPath();
		}
		return rs;
	}
	
	
	// 显示目录选择窗口
	public static File showDirChooser(String title, Stage stage) {
		DirectoryChooser fc = getDirChooser(title);
		File file =  fc.showDialog(stage);
		return file;
	}
	
	// 获取保存的文件绝对路径名(默认)
	public static File showSaveDefault(String title, Stage stage) {
		FileChooser fc = getDefaultFileChooser(title);
		File file = fc.showSaveDialog(stage);
		return file;
	}
	
	
	
	public static File showSaveDefault(String title, String fileName, Stage stage) {
		FileChooser fc = getFileChooserInitFileName(title, fileName);
		File file = fc.showSaveDialog(stage);
		return file;
	}

	//
	public static String showSave(FileChooser fc, Stage stage) {
		File file = fc.showSaveDialog(stage);
		String rs = "";
		if (file != null) {
			rs = file.getPath();
		}
		return rs;
	}

	// 打开sql文件
	public static File showOpenSqlFile(String title, Stage stage) {
		FileChooser fc = getDefaultFileChooser(title);
		File file = fc.showOpenDialog(stage);
		return file;
	}

	// 所以类型的文件
	public static File showOpenAllFile(String title, Stage stage) {
		FileChooser fc = getAllFileChooser(title); 
		File file = fc.showOpenDialog(stage);
		return file;
	}

}
