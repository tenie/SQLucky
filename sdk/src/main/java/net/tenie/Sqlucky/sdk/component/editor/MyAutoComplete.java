package net.tenie.Sqlucky.sdk.component.editor;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.fxmisc.richtext.CodeArea;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import net.tenie.Sqlucky.sdk.AutoComplete;
import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.component.MyCodeArea;
import net.tenie.Sqlucky.sdk.component.MyEditorSheet;
import net.tenie.Sqlucky.sdk.component.MyEditorSheetHelper;
import net.tenie.Sqlucky.sdk.db.Dbinfo;
import net.tenie.Sqlucky.sdk.db.SqluckyConnector;
import net.tenie.Sqlucky.sdk.po.DbSchemaPo;
import net.tenie.Sqlucky.sdk.po.db.TablePo;
import net.tenie.Sqlucky.sdk.utility.CommonUtils;
import net.tenie.Sqlucky.sdk.utility.StrUtils;

/**
 * 自动补全
 * 
 * @author tenie
 *
 */
public class MyAutoComplete implements AutoComplete {
	private static List<TablePo> keyWords = new ArrayList<>();
	private static Popup pop;
	private static VBox vb;
	private static TreeItem<TablePo> rootNode;
	private static TreeView<TablePo> treeView;
	private static HashSet<TablePo> tmpls = new HashSet<>();
	private static String filterStr = "";

	private static Map<Integer, Set<TablePo>> page = new HashMap<>();
	private static Map<Integer, Set<TablePo>> fields = new HashMap<>();

	static {
		keyWords.add(TablePo.noDbObj("SELECT * FROM "));
		keyWords.add(TablePo.noDbObj("SELECT "));
		keyWords.add(TablePo.noDbObj("FROM "));
		keyWords.add(TablePo.noDbObj("WHERE "));
		keyWords.add(TablePo.noDbObj("LEFT JOIN   ON "));
		keyWords.add(TablePo.noDbObj("CASE WHEN   THEN   ELSE   END AS  "));
		keyWords.add(TablePo.noDbObj("ORDER BY "));
		keyWords.add(TablePo.noDbObj("ORDER BY  DESC "));
		keyWords.add(TablePo.noDbObj("DESC "));
		keyWords.add(TablePo.noDbObj("ORDER BY ASC "));
		keyWords.add(TablePo.noDbObj("ASC "));

		keyWords.add(TablePo.noDbObj("GROUP BY "));
		keyWords.add(TablePo.noDbObj("VALUES"));
		keyWords.add(TablePo.noDbObj("CREATE "));
		keyWords.add(TablePo.noDbObj("SEQUENCE "));
		keyWords.add(TablePo.noDbObj("INSERT INTO  "));
		keyWords.add(TablePo.noDbObj("UPDATE  SET  ,  WHERE  "));
		keyWords.add(TablePo.noDbObj("DROP  "));
		keyWords.add(TablePo.noDbObj("DELETE FROM  "));
		keyWords.add(TablePo.noDbObj("DISTINCT  "));
		keyWords.add(TablePo.noDbObj("LIKE '%%' "));
		keyWords.add(TablePo.noDbObj("UNION ALL  "));
		keyWords.add(TablePo.noDbObj("HAVING  "));
		keyWords.add(TablePo.noDbObj("EXISTS  "));
		keyWords.add(TablePo.noDbObj("ALTER TABLE  "));
		keyWords.add(TablePo.noDbObj("WITH   AS  "));

		keyWords.add(TablePo.noDbObj("FETCH FIRST 1 ROWS ONLY  "));
		keyWords.add(TablePo.noDbObj("LIMIT 1  "));
		keyWords.add(TablePo.noDbObj("CURRENT DATE  "));
		keyWords.add(TablePo.noDbObj("CURRENT TIME  "));
		keyWords.add(TablePo.noDbObj("CURRENT TIMESTAMP  "));
		keyWords.add(TablePo.noDbObj("BETWEEN   AND   "));
		keyWords.add(TablePo.noDbObj("DAY (CURRENT TIMESTAMP)   "));
		keyWords.add(TablePo.noDbObj("MONTH (CURRENT TIMESTAMP)   "));
		keyWords.add(TablePo.noDbObj("YEAR (CURRENT TIMESTAMP)   "));
		keyWords.add(TablePo.noDbObj("DATE (CURRENT TIMESTAMP)   "));
		keyWords.add(TablePo.noDbObj("TIME (CURRENT TIMESTAMP)  "));
		keyWords.add(TablePo.noDbObj("DATE('2000-01-01 00.00.59')  "));
		keyWords.add(TablePo.noDbObj("SYSPROC.ADMIN_CMD ( ' REORG TABLE DB2INST1.MY_TABLE' );  "));
//		
//		
		//
//		

		keyWords.add(TablePo.noDbObj("ROUND(3.14555,4)   -- ==  3.14560  \n"));
		keyWords.add(TablePo.noDbObj("CAST( ROUND(3.14555,4) as numeric(20,4) ) -- == 3.1456 \n"));

		vb = new VBox();
		vb.getStyleClass().add("my-tag");
		vb.setPrefHeight(250);
		vb.setPrefWidth(300);
		rootNode = new TreeItem<>();
		treeView = new TreeView<>(rootNode);
		treeView.getStyleClass().add("auto-comolete");

		treeView.setShowRoot(false);
		vb.getChildren().add(treeView);
		VBox.setVgrow(treeView, Priority.ALWAYS);

		treeView.setCellFactory(new AutoCompleteCellFactory());

	}

	public MyAutoComplete() {
		// 鼠标点击的时候
		treeView.setOnMouseClicked(e -> {
			var it = treeView.getSelectionModel().getSelectedItem();
			String selectVal = it.getValue().getTableName();
//			System.out.println(selectVal);
			codeAreaReplaceString(selectVal, it.getValue());
			pop.hide();
		});

		// 回车的时候
		treeView.setOnKeyPressed(e -> {
//			System.out.println(e.getCode());
			if (KeyCode.ENTER.equals(e.getCode())) {
				var it = treeView.getSelectionModel().getSelectedItem();
				String selectVal = it.getValue().getTableName();
//				System.out.println(selectVal);
				codeAreaReplaceString(selectVal, it.getValue());
				pop.hide();
			} else if (KeyCode.ESCAPE.equals(e.getCode())) {
				CommonUtils.pressBtnESC();
			}
		});

	}

	@Override
	public void hide() {
		if (pop != null) {
			pop.hide();
			pop.getContent().clear();
			pop = null;
		}
	}

	// 按backspace按键的时候, 隐藏提示窗口的策略
	@Override
	public void backSpaceHide(MyCodeArea sqluckyCodeArea) {
		if (isShow()) {
			CodeArea codeArea = sqluckyCodeArea.getCodeArea();
			int acr = codeArea.getAnchor();
			int begin = 0;
			if (acr > 0) {
				begin = acr - 1;
			}
			String tmp = codeArea.getText(begin, acr);
			if (tmp.startsWith(".") || tmp.startsWith(" ") || tmp.startsWith("\t") || tmp.startsWith("\n")
					|| begin <= 0) {
				hide();
			}
		}
	}

	@Override
	public boolean isShow() {
		if (pop == null) {
			return false;
		} else {
			return pop.isShowing();
		}
	}

	@Override
	public void showPop(double x, double y, String fStr) {
		filterStr = fStr.trim().toUpperCase();
		String tmpFilterStr = filterStr;
		if (!isShow()) {
			tmpls = new HashSet<>();
			if (tmpFilterStr.contains(".")) {
				tmpFilterStr = tmpFilterStr.substring(tmpFilterStr.indexOf(".") + 1);
				var fs = getCacheTableFields();
				tmpls.addAll(fs);
			} else {
				SqluckyConnector po = CommonUtils.getDbConnectionPoByComboBoxDbConnName();
				if (po != null) {
					Map<String, DbSchemaPo> map = po.getSchemas();
					DbSchemaPo spo = map.get(po.getDefaultSchema());
					if (spo != null) {
						tmpls.addAll(keyWords);
						tmpls.addAll(spo.getTabs());
						tmpls.addAll(spo.getViews());
						tmpls.addAll(getCacheTableFields());
					}
				} else {
					tmpls.addAll(keyWords);
					tmpls.addAll(getCacheTableFields());
				}
			}
		}

		boolean tf = false;
		if (tmpls != null && tmpls.size() > 0) {
			rootNode.getChildren().clear();
			for (var tb : tmpls) {
				if (tb.getTableName().toUpperCase().contains(tmpFilterStr)) {
					TreeItem<TablePo> item = new TreeItem<>(tb);
					rootNode.getChildren().add(item);
					tf = true;
					if (rootNode.getChildren().size() > 100) {
						break;
					}
				}

			}
			if (tf) {
				pop = new Popup();
				pop.setX(x);
				pop.setY(y);
				pop.setAutoHide(true);
				pop.getContent().add(vb);

				pop.show(ComponentGetter.primaryStage);
				Platform.runLater(() -> {
					treeView.getSelectionModel().select(0);
				});
			} else {
				pop = null;
			}

		}
	}

	@Override
	public Integer getMyTabId() {
//		SqluckyTab tb = SqluckyEditorUtils.currentMyTab();
		MyEditorSheet sheet = MyEditorSheetHelper.getActivationEditorSheet();
		if (sheet != null) {
			var scpo = sheet.getDocumentPo();
			if (scpo != null) {
				Integer id = sheet.getDocumentPo().getId();
				return id;
			}
		}
		return null;
	}

	@Override
	public void cacheTablePo(TablePo tabpo) {

		Consumer<String> caller = x -> {
			var fs = tabpo.getFields();
			if (fs == null || fs.size() == 0) {
				SqluckyConnector dpov = CommonUtils.getDbConnectionPoByComboBoxDbConnName();
				if (dpov != null) {
					Connection connv = dpov.getConn();
					try {
						Dbinfo.fetchTableInfo(connv, tabpo);
					} catch (Exception e) {
					}
				}

			}
			if (fs != null && fs.size() > 0) {
				Integer id = getMyTabId();
				if (id != null) {
					Set<TablePo> tmppos;
					if (fields.containsKey(id)) {
						tmppos = fields.get(id);
					} else {
						tmppos = new HashSet<>();
						fields.put(id, tmppos);
					}
					var fis = tabpo.getFields();
					for (var f : fis) {
						var colName = f.getColumnName();
						tmppos.add(TablePo.noDbObj(colName));
					}
				}
			}
		};

		// 是db对象的时候 才缓存
		if (tabpo.getDbObj()) {
			CommonUtils.runThread(caller);
		}

	}

	// 缓存页面单词
	@Override
	public void cacheTextWord() {
//		var mtb = SqluckyEditorUtils.currentMyTab();
//		MyEditorSheet sheet = MyEditorSheetHelper.getActivationEditorSheet();
//		String text = mtb.getSqlCodeArea().getCodeArea().getText();
		String text = MyEditorSheetHelper.getActivationEditorText();
		Consumer<String> caller = x -> {
			Integer id = getMyTabId();
			if (id != null) {
				Set<TablePo> tmppos;
				if (page.containsKey(id)) {
					tmppos = page.get(id);
				} else {
					tmppos = new HashSet<>();
					page.put(id, tmppos);
				}

				// 获取词组
				var words = StrUtils.splitWordByStr(text);
				for (var word : words) {
					tmppos.add(TablePo.noDbObj(word));
				}
			}
		};
		if (StrUtils.isNotNullOrEmpty(text)) {
			CommonUtils.runThread(caller);
		}

	}

	// 获取缓存数据
	@Override
	public Collection<TablePo> getCacheTableFields() {
		Set<TablePo> cacheFs = new HashSet<>();
		Integer id = getMyTabId();
		if (id != null) {

			if (fields.containsKey(id)) {
				cacheFs.addAll(fields.get(id));
			}
			if (page.containsKey(id)) {
				cacheFs.addAll(page.get(id));
			}

		}
		return cacheFs;
	}

	// 替换输入
	@Override
	public void codeAreaReplaceString(String selectVal, TablePo tabpo) {
		// 缓存表(表字段)
		cacheTablePo(tabpo);

		int len = selectVal.length();
		var codeArea = MyEditorSheetHelper.getCodeArea();
		int anc = codeArea.getAnchor();

		int start = 0;
		int end = anc;
		if (anc > len) {
			start = anc - len;
		}
//		String caStr = codeArea.getText(start, end);

		// 有过滤词的情况
		if (filterStr.length() > 0) {
			if (filterStr.contains(".")) {
				filterStr = filterStr.substring(filterStr.indexOf(".") + 1);
			}
			start = anc - filterStr.length();
			int begin = anc - filterStr.length();
			if (begin < 0) {
				begin = 0;
			}
			codeArea.deleteText(begin, anc);
			codeArea.insertText(begin, selectVal);
		} else if (filterStr.length() == 0) {
			codeArea.insertText(anc, selectVal);
		}

		else {
			// 没有匹配到直接输入
			codeArea.insertText(anc, selectVal);
		}

	}

}
