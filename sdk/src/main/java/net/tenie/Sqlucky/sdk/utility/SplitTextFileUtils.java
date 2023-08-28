package net.tenie.Sqlucky.sdk.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/***
 * 分割大文件 （ SQL 文件太大（insert），第三方工具无法一次性读取，进行分割 生成 一个一个文件 ）
 */
public class SplitTextFileUtils {
	private static Logger logger = LogManager.getLogger(SplitTextFileUtils.class);
	// 使用示例
	public static void test(String[] args) {

		// 目标文件
		String targetFile = "C:\\insertSQL.sql";
		// 存放的目录
		String saveDir = "D:\\splitDir";
		// 自定义的生成文件前缀名
		String saveFileName = "insert";
		// 生成文件格式的后缀
		String suffix = "sql";
		// 自定义 一个文件的行数，这里是 100000 行 一个文件
		long splitSize = 1000;
		try {
			splitFile(targetFile, saveDir, saveFileName, suffix, splitSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param targetFile   目标文件路径
	 * @param saveDir      存放的目录
	 * @param saveFileName 生成文件的前缀名
	 * @param suffix       生成文件的后缀名
	 * @param splitSize    每一个文件 多少行数据
	 */
	public static void splitFile(String targetFile, String saveDir, String saveFileName, String suffix, long splitSize)
			throws Exception {

		if (!saveDir.endsWith("\\")) {
			saveDir += File.separator;
		}

		File file = new File(targetFile);
		if (!file.exists()) {
			throw new Exception("目标路径：[ " + targetFile + " ] 有错误...");
		}
		// 输入缓冲流
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

		String str = null;
		// 行数
		long len = 0;
		logger.info("开始写入......请等待......");
		long startTime = System.currentTimeMillis();
		// 输出缓冲流
		BufferedWriter writer = null;
		while ((str = reader.readLine()) != null) {

			// 当前 行 文件
			long txtSize = (len / splitSize) + 1;
			String fileName = saveDir + saveFileName + txtSize + "." + suffix;
			// 使用 BufferedWriter 如果 不进行 flush 或者 close 写入不了内容。
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true)));
			writer.write(str + System.lineSeparator());
			writer.flush();
			len++;
		}
		writer.close();
		reader.close();

		logger.info(" 写入完毕，一共 " + len + " 记录，耗时 ：" + (System.currentTimeMillis() - startTime) / 1000 + " s");
	}

}
