package net.tenie.Sqlucky.sdk.utility;

import java.io.File;

import org.apache.commons.io.FileUtils;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.config.ConfigVal;

/**
 * 操作系统的文件选择窗口
 * 
 * @author tenie
 *
 */
public final class FileOrDirectoryChooser {

	// 获取打开文件的目录
	public static File getOpenfileDir() {
//		return  FileUtils.getUserDirectory();
		if (StrUtils.isNullOrEmpty(ConfigVal.openfileDir)) {
			return FileUtils.getUserDirectory();
		} else {

			File f = new File(ConfigVal.openfileDir);
			if (f.exists()) {
				if (f.isFile()) {
					String fp = f.getParent();
					f = new File(fp);
				}
			} else {
				return FileUtils.getUserDirectory();
			}

			return f;
		}
	}

	// save file
	private static FileChooser fileChooser = new FileChooser();
	private static DirectoryChooser directoryChooser = new DirectoryChooser();

	// default configure
	private static void configureFileChooser(final FileChooser fileChooser, String title) {
		configureFileChooser(fileChooser, title, null);
	}

	private static void configureFileChooser(final FileChooser fileChooser, String title, String filename) {
		fileChooser.setTitle(title);
		File dir = getOpenfileDir();

		fileChooser.setInitialDirectory(dir);
		fileChooser.getExtensionFilters().clear();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("sql", "*.sql"),
				new FileChooser.ExtensionFilter("txt", "*.txt"), new FileChooser.ExtensionFilter("csv", "*.csv"),
				new FileChooser.ExtensionFilter("All", "*.*"));
		if (filename != null) {
			fileChooser.setInitialFileName(filename);

		}
	}

	private static void allFileChooser(final FileChooser fileChooser, String title) {
		fileChooser.setTitle(title);
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().clear();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All", "*.*"));
	}

	private static void jsonFileChooser(final FileChooser fileChooser, String title) {
		fileChooser.setTitle(title);
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().clear();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("json", "*.json"),
				new FileChooser.ExtensionFilter("All", "*.*"));

	}

	/**
	 * 指定文件类型
	 * 
	 * @param fileChooser
	 * @param title
	 * @param Type
	 */
	private static void fileChooser(final FileChooser fileChooser, String title, String Type) {
		fileChooser.setTitle(title);
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().clear();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(Type, "*." + Type));
	}

	// 选择目录
	public static DirectoryChooser getDirChooser(String title) {
		directoryChooser.setTitle(title);
		File dir = getOpenfileDir();

		directoryChooser.setInitialDirectory(dir);
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

	public static FileChooser getFileChooserInitFileName(String title, String filename) {
		configureFileChooser(fileChooser, title, filename);
		return fileChooser;
	}

	public static FileChooser getFileChooser(String title, String filename, File dir) {
		fileChooser.setTitle(title);
//		File dir =  new File(openPath);

		fileChooser.setInitialDirectory(dir);
		fileChooser.getExtensionFilters().clear();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("txt", "*.txt"),
				new FileChooser.ExtensionFilter(".md", "*.md"), new FileChooser.ExtensionFilter("sql", "*.sql"),
				new FileChooser.ExtensionFilter("All", "*.*"));
		if (filename != null) {
			fileChooser.setInitialFileName(filename);
		}
		return fileChooser;
	}

	public static FileChooser getExcelFileChooser() {
		fileChooser.setTitle("");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().clear();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("xls", "*.xls"),
				new FileChooser.ExtensionFilter("xlsx", "*.xlsx"));
//		if (filename != null) {
//			fileChooser.setInitialFileName(filename);
//		}
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
		File file = fc.showDialog(stage);
		return file;
	}

	// 获取保存的文件绝对路径名(默认)
	public static File showSaveDefault(String title, Stage stage) {
		configureFileChooser(fileChooser, title);
		File file = fileChooser.showSaveDialog(stage);
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
		configureFileChooser(fileChooser, title);
		File file = fileChooser.showOpenDialog(stage);
		return file;
	}

	// 所以类型的文件
	public static File showOpenAllFile(String title, Stage stage) {
		allFileChooser(fileChooser, title);
		File file = fileChooser.showOpenDialog(stage);
		return file;
	}

	// json类型的文件
	public static File showOpenJsonFile(String title, Stage stage) {
		jsonFileChooser(fileChooser, title);
		File file = fileChooser.showOpenDialog(stage);
		return file;
	}

	// 指定文件类型
	public static File showOpen(String title, String type, Stage stage) {
		fileChooser(fileChooser, title, type);
		File file = fileChooser.showOpenDialog(stage);
		return file;
	}

	// 指定文件类型
	public static File selectExcelFile(Stage stage) {
		getExcelFileChooser();
		File file = fileChooser.showOpenDialog(stage);
		return file;
	}

}
