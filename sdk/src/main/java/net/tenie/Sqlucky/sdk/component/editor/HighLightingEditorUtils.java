package net.tenie.Sqlucky.sdk.component.editor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.richtext.CodeArea;

import javafx.scene.control.IndexRange;
import net.tenie.Sqlucky.sdk.SqluckyEditor;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;

public class HighLightingEditorUtils {
	private static Logger logger = LogManager.getLogger(HighLightingEditorUtils.class);

	// 创建默认的带有提示功能的 高亮sql编辑器
	public static SqluckyEditor sqlEditor() {
		MyAutoComplete myAuto = new MyAutoComplete();
		SqluckyEditor sqlCodeAreaEditor = new HighLightingEditor(myAuto);
//		右键菜单
		HighLightingEditorContextMenu cm = new HighLightingEditorContextMenu(sqlCodeAreaEditor);
		sqlCodeAreaEditor.setContextMenu(cm);
		return sqlCodeAreaEditor;
	}

	// 根据括号) 向前寻找配对的括号( 所在的位置.
	public static int findEndParenthesisRange(String text, int start, String pb, String pe) {
		String startStr = text.substring(0, start);
		int end = 0;
		int strSz = startStr.length();
		if (strSz == 0)
			return end;
		if (!startStr.contains(pe))
			return end;
		int idx = 1;
		for (int i = start; i != 0; i--) {
			if (idx == 0)
				break;
			String tmp = startStr.substring(i - 1, i);

			if (pe.equals(tmp)) {
				idx--;
				end = i;
			} else if (pb.equals(tmp)) {
				idx++;
			}
		}
		return end;
	}

	// 引号( ' " `) 之间的字符串区间
	public static IndexRange findStringRange(String text, int start, String pe) {
		IndexRange ir = new IndexRange(0, 0);
		int strSz = text.length();
		if (strSz == 0)
			return ir;
		int idx = -1;
		for (int i = 0; i < strSz; i++) {
			String tmp = text.substring(i, i + 1);
			if (tmp.equals(pe)) {
				if (idx == -1) {
					idx = i;
				} else {
					if (idx == start || i == start) {
						ir = new IndexRange(idx + 1, i);
						break;
					}
					idx = -1;

				}
			}
		}
		return ir;
	}

	// 减少前置tab符号
	public static void minus4Space() {
		CodeArea code = MyEditorSheetHelper.getCodeArea();
		IndexRange i = code.getSelection(); // 获取当前选中的区间
		int start = i.getStart();
		int end = i.getEnd();

		// 修正开始下标 , 获取开始之前的字符串, 找到最接近start 的换行符
		String frontTxt = code.getText(0, start);
		int lidx = frontTxt.lastIndexOf('\n'); // 找到最后一个换行符
		if (lidx > 0) {
			lidx = frontTxt.length() - lidx - 1; // 获取换行符的位置, 不包括换行符自己
			start = start - lidx; // start的位置定位到最后一个换行符之后
		} else { // 如果没有找到换行符, 说明在第一行, 把start置为0
			start = 0;
		}
		// 获取文本
		String txt = code.getText(start, end);

		String valStr = "";

		String[] strArr = txt.split("\n");
		String endtxt = "";
		if (strArr.length > 0) {
			endtxt = txt.substring(txt.length() - 1);
			// 遍历每一行
			for (String val : strArr) {
				// 获取没有空格的纯字符串
				String trimStr = val.trim();
				// 找到纯字符串, 在原本行里的下标位置
				int subscript = val.indexOf(trimStr);
//				下标为0 就是没有必要去除空格
				if (subscript == 0) {
					valStr += val + "\n";
				} else { // 开始去除行的前4个空格
					String SpaceStr = val.substring(0, subscript);
					// 如果有tab键就 换成4个空格
					if (SpaceStr.contains("\t")) {
						SpaceStr = SpaceStr.replaceAll("\t", "    ");
					}
					// 如果空格大于4个减去4个, 否则空格归零
					if (SpaceStr.length() > 4) {
						SpaceStr = SpaceStr.substring(3, SpaceStr.length());
					} else {
						SpaceStr = "";
					}
					valStr += SpaceStr + trimStr + "\n";
				}
			}
		}
		if (!"\n".equals(endtxt)) { // 去除最后一个换行符
			valStr = valStr.substring(0, valStr.length() - 1);
		}
		// 将原文本删除
		code.deleteText(start, end);
		// 插入 注释过的文本
		code.insertText(start, valStr);

		code.selectRange(start, start + valStr.length());
		MyEditorSheetHelper.currentSqlCodeAreaHighLighting();
	}

	// 添加tab符号
	public static void add4Space() {

		String replaceStr1 = "\n    ";
		String replaceStr2 = "    ";

		CodeArea code = MyEditorSheetHelper.getCodeArea();
		IndexRange i = code.getSelection(); // 获取当前选中的区间
		int start = i.getStart();
		int end = i.getEnd();
		int begin = start;
		int over = end;

		// 修正开始下标 , 获取开始之前的字符串, 找到最接近start 的换行符
		String frontTxt = code.getText(0, start);
		int lidx = frontTxt.lastIndexOf('\n'); // 找到最后一个换行符
		if (lidx > 0) {
			lidx = frontTxt.length() - lidx - 1; // 获取换行符的位置, 不包括换行符自己
			start = start - lidx; // start的位置定位到最后一个换行符之后
		} else { // 如果没有找到换行符, 说明在第一行, 把start置为0
			start = 0;
		}
		// 获取文本
		String txt = code.getText(start, end);
		logger.info("txt = " + txt);
		String temp = "";
		for (int t = 0; t < start; t++) {
			temp += " ";
		}
		txt = txt.replaceAll("\n", replaceStr1);
		txt = temp + replaceStr1 + txt;
		logger.info(txt);
		int k = txt.indexOf('\n', 0);
		int count = 0;
		while (k >= 0) {
			count++;
			code.insertText(k, replaceStr2);
			k = txt.indexOf('\n', k + 1);
		}
		code.selectRange(begin, over + (count * 4));
	}

	// 根据括号( 寻找配对的 结束)括号所在的位置.
	public static int findBeginStringRange(String text, int start, String pb, String pe) {
		String startStr = text.substring(start).toUpperCase();
		int end = 0;
		int strSz = startStr.length();
		if (strSz == 0)
			return end;
		if (!startStr.contains(pe))
			return end;
		int idx = 1;
		int peSz = pe.length();
		for (int i = 0; i < strSz; i++) {
			if (idx == 0)
				break;
			String tmp = startStr.substring(i, i + peSz);
			if (pb.equals(tmp)) {
				idx++;
			}
			if (pe.equals(tmp)) {
				idx--;
				end = i;
			}
		}
		return start + end;
	}

	// 根据括号) 向前寻找配对的括号( 所在的位置.
	public static int findEndStringRange(String text, int start, String pb, String pe) {
		String startStr = text.substring(0, start).toUpperCase();
		;
		int end = 0;
		int strSz = startStr.length();
		if (strSz == 0)
			return end;
		if (!startStr.contains(pe))
			return end;
		int idx = 1;
		int peSz = pe.length();
		for (int i = start; i != 0; i--) {
			if (idx == 0)
				break;
			String tmp = startStr.substring(i - peSz, i);
			if (pb.equals(tmp)) {
				idx++;
			}
			if (pe.equals(tmp)) {
				idx--;
				end = i;
			}
		}
		return end;
	}
}
