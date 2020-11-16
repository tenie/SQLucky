package net.tenie.fx.component;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.concurrent.Task;
import javafx.scene.layout.StackPane;
import net.tenie.fx.utility.EventAndListener.CommonEventHandler;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

/*   @author tenie */
public class SqlCodeAreaHighLighting {

	private static final String[] KEYWORDS = new String[] { "SELECT", "select", "from", "FROM", "where", "update",
			"UPDATE", "case", "WHERE", "char", "CASE", "CHAR", "or", "OR", "left", "LEFT", "right", "double", "RIGHT",
			"DOUBLE", "INNER", "inner", "join", "JOIN", "exists", "EXISTS", "float", "FLOAT", "ALTER", "alter", "TABLE",
			"table", "data", "DATA", "type", "TYPE", "int", "INT", "VARCHAR", "varchar", "long", "LONG", "data", "DATA",
			"private", "protected", "public", "set", "SET", "short", "SHORT", "TIMESTAMP", "timestamp", "group",
			"GROUP", "by", "BY", "ON", "on",

			"AS", "as", "DECIMAL", "decimal", "PRIMARY", "primary", "null", "NULL", "CHARACTER", "character",
			"CONSTRAINT", "constraint",

			"CREATE", "create", "SEQUENCE", "sequence", "WITH", "with", "else", "ELSE", "NUMERIC", "numeric", "COLUMN",
			"column", "ADD", "add",

			"fetch", "FETCH", "union", "UNION", "like", "LIKE", "delete", "DELETE", "DEFAULT", "default", "current",
			"CURRENT", "drop", "DROP",

			"FOR", "for", "NEXT", "next", "START", "start", "BIGINT", "bigint", "MAXVALUE", "maxvalue", "INCREMENT",
			"increment", "first", "FIRST", "rows", "ROWS", "only", "ONLY", "date", "DATE", "all", "ALL",

			"CALL", "call", "OUTER", "outer", "IS", "is", "not", "NOT", "VALUE", "value", "view", "VIEW", "INSERT",
			"insert", "INTO", "into", "INTO", "into", "VALUES", "values",

			"AND", "and", "order", "ORDER", "DESC", "desc"

	};

	private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
	private static final String PAREN_PATTERN = "\\(|\\)";
	private static final String BRACE_PATTERN = "\\{|\\}";
	private static final String BRACKET_PATTERN = "\\[|\\]";

	private static final String SEMICOLON_PATTERN = "\\;";
	// 字符串
	private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"|'([^'\\\\]|\\\\.)*'";
	// 注释
	private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "--[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
//    private static final String COMMENT_PATTERN2 = "--[^\n]*";  '([^'\\\\]|\\\\.)*'

	private static final Pattern PATTERN = Pattern.compile("(?<KEYWORD>" + KEYWORD_PATTERN + ")" + "|(?<PAREN>"
			+ PAREN_PATTERN + ")" + "|(?<BRACE>" + BRACE_PATTERN + ")" + "|(?<BRACKET>" + BRACKET_PATTERN + ")"

//            + "|(?<PAREN>" + PAREN_PATTERN2 + ")"
//            + "|(?<BRACE>" + BRACE_PATTERN2 + ")"
//            + "|(?<BRACKET>" + BRACKET_PATTERN2 + ")"

			+ "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")" + "|(?<STRING>" + STRING_PATTERN + ")" + "|(?<COMMENT>"
			+ COMMENT_PATTERN + ")");

	private static final String sampleCode = String.join("\n", new String[] { "" });

	private CodeArea codeArea;
	private ExecutorService executor;

	public StackPane getObj(String text, boolean editable) {
		executor = Executors.newSingleThreadExecutor();
		codeArea = new CodeArea();
		codeArea.setParagraphGraphicFactory(MyLineNumberFactory.get(codeArea));
		codeArea.setOnKeyPressed(CommonEventHandler.codeAreaChange(codeArea));
		codeArea.replaceText(0, 0, sampleCode);
		if (text != null)
			codeArea.appendText(text);
		StackPane sp = new StackPane(new VirtualizedScrollPane<>(codeArea));
		sp.getStyleClass().add("my-tag");
		SqlCodeAreaHighLightingHelper.applyHighlighting(codeArea);
		codeArea.setEditable(editable);
		return sp;
	}

	public StackPane getObj() {
		return getObj(null, true);
	}

	public void stop() {
		executor.shutdown();
	}

	private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
		String text = codeArea.getText();
		Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
			@Override
			protected StyleSpans<Collection<String>> call() throws Exception {
				return computeHighlighting(text);
			}
		};
		executor.execute(task);
		return task;
	}

	private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
		codeArea.setStyleSpans(0, highlighting);
	}

	private static StyleSpans<Collection<String>> computeHighlighting(String text) {
		Matcher matcher = PATTERN.matcher(text);
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		while (matcher.find()) {
			String styleClass = matcher.group("KEYWORD") != null ? "keyword"
					: matcher.group("PAREN") != null ? "paren"
							: matcher.group("BRACE") != null ? "brace"
									: matcher.group("BRACKET") != null ? "bracket"
											: matcher.group("SEMICOLON") != null ? "semicolon"
													: matcher.group("STRING") != null ? "string"
															: matcher.group("COMMENT") != null ? "comment" : null;
			/* never happens */ assert styleClass != null;
			spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
			spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
			lastKwEnd = matcher.end();
		}
		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		return spansBuilder.create();
	}
}
