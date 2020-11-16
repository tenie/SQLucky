package net.tenie.fx.component;

import java.io.File;

import org.apache.commons.io.FileUtils;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

/*   @author tenie */
public final class CommonFileChooser {
	// save file
	private static FileChooser fileChooser = new FileChooser();

	// default configure
	private static void configureFileChooser(final FileChooser fileChooser, String title) {
		fileChooser.setTitle(title);

//	        fileChooser.setInitialDirectory( new File(System.getProperty("user.home"))  );
		fileChooser.setInitialDirectory(FileUtils.getUserDirectory());
		fileChooser.getExtensionFilters().clear();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("sql", "*.sql"),
				new FileChooser.ExtensionFilter("txt", "*.txt"), new FileChooser.ExtensionFilter("csv", "*.csv"),
				new FileChooser.ExtensionFilter("All", "*.*"));
	}

	public static FileChooser getDefaultFileChooser(String title) {
		configureFileChooser(fileChooser, title);
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

	// 获取保存的文件绝对路径名(默认)
	public static File showSaveDefault(String title, Stage stage) {
		FileChooser fc = getDefaultFileChooser(title);
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

}
