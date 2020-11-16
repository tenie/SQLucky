package net.tenie.lib.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.h2.jdbc.JdbcSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.tenie.lib.db.ConnectionPool;

/*   @author tenie */
public class ToolsLib {
	private static final Logger logger = LoggerFactory.getLogger(ToolsLib.class);

	public static Process process;

	// 读取文件内容, 返回文本字符串
	public static String getText(String fileStr) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(fileStr));
		StringBuilder rs = new StringBuilder();
		String str = "";
		while ((str = in.readLine()) != null) {
			rs.append(str + "\n");
		}
		in.close();
		return rs.toString();
	}

	public static boolean tableExists(String table, Connection conn) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select 1 from  " + table);
		} catch (JdbcSQLException e) {
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ConnectionPool.releaseConnection(null, stmt, rs);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;

	}

	public static boolean createTable(String tables[], Connection conn) {
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement();
			for (String sql : tables) {
				System.out.println(sql);
				stmt.execute(sql);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ConnectionPool.releaseConnection(null, stmt, rs);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;

	}

	public static String Html2Text(String inputString) {
		if (inputString == null) {
			return "";
		}

		// 含html标签的字符串
		String htmlStr = inputString.trim();
		String textStr = "";
		Pattern p_script;
		Matcher m_script;
		Pattern p_style;
		Matcher m_style;
		Pattern p_html;
		Matcher m_html;
		Pattern p_space;
		Matcher m_space;
		Pattern p_escape;
		Matcher m_escape;

		try {
			// 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";

			// 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";

			// 定义HTML标签的正则表达式
			String regEx_html = "<[^>]+>";

			// 定义空格回车换行符
			String regEx_space = "\\s*|\t|\r|\n";

			// 定义转义字符
			String regEx_escape = "&.{2,6}?;";

			// 过滤script标签
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll("");

			// 过滤style标签
			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll("");

			// 过滤html标签
			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll("");

//		            // 过滤空格回车标签
//		            p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);
//		            m_space = p_space.matcher(htmlStr);
//		            htmlStr = m_space.replaceAll("");
//
//		            // 过滤转义字符
//		            p_escape = Pattern.compile(regEx_escape, Pattern.CASE_INSENSITIVE);
//		            m_escape = p_escape.matcher(htmlStr);
//		            htmlStr = m_escape.replaceAll("");

			textStr = htmlStr;

		} catch (Exception e) {
			logger.info("Html2Text:{}", e.getMessage());
		}

		// 返回文本字符串
		return textStr;
	}

	/**
	 * 删除所有的HTML标签
	 *
	 * @param source 需要进行除HTML的文本
	 * @return
	 */
	public static String deleteAllHTMLTag(String source) {
		if (source == null) {
			return "";
		}

		String s = source;
		/** 删除普通标签 */
		s = s.replaceAll("<(S*?)[^>]*>.*?|<.*? />", "");
		/** 删除转义字符 */
		s = s.replaceAll("&.{2,6}?;", "");
		return s;
	}

	/// 执行 cmd
	public static void ExecCmd(String cmd) throws IOException, InterruptedException {
		process = Runtime.getRuntime().exec(cmd);
	}

	// 打印 cmd中的输出信息
	private static void printMessage(final InputStream input) {
		new Thread(() -> {
			Reader reader = new InputStreamReader(input);
			BufferedReader bf = new BufferedReader(reader);
			String line = null;
			try {
				while ((line = bf.readLine()) != null) {
					logger.info("shell cmd output = " + line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}

	// map key 转为大写
	public static Map<String, Object> mapKeyUp(Map<String, ?> map) {
		Map<String, Object> obdmap = new HashMap<String, Object>();
		Set<String> se = map.keySet();
		for (String key : se) {
			// 在循环将大写的KEY和VALUE 放到新的Map
			obdmap.put(key.toUpperCase(), map.get(key));
		}

		return obdmap;

	}

	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				System.out.println("" + fileName + "");
				return true;
			} else {
				System.out.println("" + fileName + "失败！");
				return false;
			}
		} else {
			System.out.println("" + fileName + "不存在！");
			return false;
		}
	}

}
