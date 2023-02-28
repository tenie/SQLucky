package net.tenie.Sqlucky.sdk.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mozilla.universalchardet.UniversalDetector;

import javafx.stage.Stage;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;

/**
 * 
 * @author tenie
 *
 */
public class FileTools {
	private static Logger logger = LogManager.getLogger(FileTools.class);
	
	// 文件类型
	public static List<String> fileTypes = new ArrayList<>();
	static {

		fileTypes.add(".rar");
		fileTypes.add(".zip");
		fileTypes.add(".7z");
		fileTypes.add(".tar.gz");
		fileTypes.add(".tar.xz");
		fileTypes.add(".tgz");

		fileTypes.add(".docx");
		fileTypes.add(".doc");
		fileTypes.add(".xlsx");
		fileTypes.add(".xls");
		fileTypes.add(".pdf");
		fileTypes.add(".azw3");
		fileTypes.add(".epub");

		fileTypes.add(".mp4");
		fileTypes.add(".mp3");
		fileTypes.add(".m3u8");
		fileTypes.add(".avi");
		fileTypes.add(".wav");
		fileTypes.add(".mpg");
		fileTypes.add(".mov");

		fileTypes.add(".jpg");
		fileTypes.add(".png");
		fileTypes.add(".jpeg");
		fileTypes.add(".gif");
		fileTypes.add(".bmp");

		fileTypes.add(".exe");
		fileTypes.add(".com");
		fileTypes.add(".crx");
		fileTypes.add(".apk");
		fileTypes.add(".msi");
		fileTypes.add(".deb");
		fileTypes.add(".iso");

		fileTypes.add(".jar");
		fileTypes.add(".class");
		fileTypes.add(".war");
		fileTypes.add(".pyc");
		fileTypes.add(".mv.db");

		// 库
		fileTypes.add(".dll");
		fileTypes.add(".lib");
		fileTypes.add(".jnilib");
		fileTypes.add(".dylib");
		fileTypes.add(".so");

		fileTypes.add(".o");

		// delphi
		fileTypes.add(".ddp");
		fileTypes.add(".res");
		fileTypes.add(".bpl");
		fileTypes.add(".~bpl");
		fileTypes.add(".dcu");

	}
	// UTF-8 字符串保存到文件
	public static void save(File file, String data) throws IOException {
		FileUtils.writeStringToFile(file, data, "UTF-8");
	}

	public static void saveByEncode(String fileName, String data, String encode) throws IOException {
//		String encode = ComponentGetter.getFileEncode(fileName);
		if (StrUtils.isNotNullOrEmpty(encode)) {
			save(fileName, data, encode);
		} else {
			FileUtils.writeStringToFile(new File(fileName), data, "UTF-8");
		}

	}

	public static void save(String fileName, String data, String encodeing) throws IOException {

		logger.info(fileName);
		logger.info(data);
		logger.info(encodeing);
		FileUtils.writeStringToFile(new File(fileName), data, encodeing);
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
			if (file.exists()) {
				String charset = detectFileCharset(file);
				if (charset != null) {
					val = FileUtils.readFileToString(file, charset);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return val;
	}

	public static String read(File file, String charset) {
		String val = "";
		try {
			val = FileUtils.readFileToString(file, charset);
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
	public static List<File> getFileFromDir(File dirFile) {
		List<File> fls = new ArrayList<>();
		File arrFile[] = dirFile.listFiles();
		if (arrFile != null && arrFile.length > 0) {
			for (File fl : arrFile) {
				if (fl.isFile()) {
					fls.add(fl);
				}
			}
		}

		return fls;
	}

	// 目录下的所有文件, 包括子目录
	public static void getAllFileFromDir(File dir, Function<File, Boolean> caller) {
		File arrFile[] = dir.listFiles();
		if (arrFile != null && arrFile.length > 0) {
			Arrays.sort(arrFile, new Comparator<File>() {
				public int compare(File f1, File f2) {
					long diff = f1.lastModified() - f2.lastModified();
					if (diff > 0)
						return -1;
					else if (diff == 0)
						return 0;
					else
						return 1;// 如果 if 中修改为 返回-1 同时此处修改为返回 1 排序就会是递减
				}

//		            public boolean equals(Object obj) {
//		                return true;
//		            }

			});
			for (File fl : arrFile) {
				if (fl.isFile()) {
					Boolean isStop = caller.apply(fl);
					if (isStop) {
						return;
					}

				}
				if (fl.isDirectory()) {
					getAllFileFromDir(fl, caller);
				}
			}
		}

	}

	public static List<File> getAllFileFromDir(File dir) {
		List<File> fls = new ArrayList<>();

		File arrFile[] = dir.listFiles();
		if (arrFile != null && arrFile.length > 0) {
			for (File fl : arrFile) {
				if (fl.isFile()) {
					fls.add(fl);
				}
				if (fl.isDirectory()) {
					List<File> vals = getAllFileFromDir(fl);
					fls.addAll(vals);
				}
			}
		}

		return fls;
	}

	/**
	 * 读取文件内容, 判断内容中是否包含查询字符串
	 * 
	 * @param file
	 * @param queryStr
	 * @return
	 */
	public static String fileExistQueryStr(File file, String queryStr) {
		if (file.isFile()) {
			String charset = detectFileCharset(file);
			if (charset != null) {
				String valStr = FileTools.read(file, charset);
				if (valStr.contains(queryStr)) {
					return valStr;
				}
			}

		}

		return null;
	}

	public static void main(String[] args) {

		File test = new File("D:\\data\\data.xls");
		isBinaryFile(test);
//		String charset = detectFileCharset(test);
//		System.out.println(charset);

//		boolean isText = isText(test);
//		System.out.println(isText);
//
//		if (isText) {
//			String charset = detectFileCharset(test);
//			System.out.println(charset);
//		}
	}

	/**
	 * 判断文件是否为文本格式的文件
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isText(File file) {
		boolean isText = true;
		try {
			FileInputStream fin = new FileInputStream(file);
			long len = file.length();
			for (int j = 0; j < len; j++) {
				int t = fin.read();
				if (t < 32 && t != 9 && t != 10 && t != 13) {
					isText = false;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isText;
	}

	/**
	 * 判断是否是二进制文件
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isBinaryFile(File file) {
		if (file.exists()) {
			String name = file.getName();
			if (name.contains(".")) {
//				name = name.substring(name.lastIndexOf("."));
				logger.debug("file  = " + name);

				for (String typeStr : fileTypes) {
					if (name.toLowerCase().endsWith(typeStr)) {
						return true;
					}
				}
//				if(fileTypes.contains(name.toLowerCase())) {
//					return true;
//				}
			}
		}

		return false;
	}

	/**
	 * 获取文件的编码
	 * 
	 * @param sourceFile
	 * @return
	 */
	public static String detectFileCharset(File sourceFile) {

		String encoding = null;

		try {
			encoding = UniversalDetector.detectCharset(sourceFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encoding;
//		if (encoding != null) {
//			System.out.println("Detected encoding = " + encoding);
//		} else {
//			System.out.println("No encoding detected.");
//		}
	}
	
	/**
	 * 删除目录, 递归删除
	 * @param dir
	 */
	public static void deleteDir(File dir) {
		if (dir != null && dir.isDirectory() && dir.exists()) {
			File[] fs = dir.listFiles();
			if (fs != null && fs.length > 0) {
				for (int i = 0; i < fs.length; i++) {
					File tmp = fs[i];
					if (tmp.isFile()) {
						tmp.delete();
					} else if (tmp.isDirectory()) {
						deleteDir(tmp);
					}
				}
			}
			dir.delete();
		}
		if(dir != null  && dir.exists() ) {
			dir.delete();
		}
	}
	
	/**
	 * str写入到文件并压缩文件
	 * @param val
	 * @param fileName
	 * @param tmpDir
	 * @return
	 */
	public static String stringZipFile(String val, String fileName, String tmpDir){
		File file = new File(tmpDir, fileName);
		String strFile = file.getAbsolutePath();
		String zipFile = strFile + ".zip";
		try {
			FileTools.save(file, val);
			ZipUtil.zipFile(strFile, zipFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return zipFile;
	}
}
