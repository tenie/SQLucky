package net.tenie.fx.component;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
/*   @author tenie */
public class SqlCodeAreaHighLightingHelper {

	private static final String[] KEYWORDS = new String[] { "SELECT", "FROM", "UPDATE", "WHERE", "CASE", "CHAR", "OR",
			"LEFT", "RIGHT", "DOUBLE", "INNER", "JOIN", "EXISTS", "FLOAT", "ALTER", "TABLE", "DATA", "TYPE", "INT",
			"VARCHAR", "LONG", "SET", "SHORT", "TIMESTAMP", "GROUP", "BY", "ON", "AS", "DECIMAL", "PRIMARY", "NULL",
			"CHARACTER", "CONSTRAINT", "CREATE", "SEQUENCE", "WITH", "ELSE", "NUMERIC", "COLUMN", "ADD", "FETCH", "UNION",
			"DEFAULT", "CURRENT", "DROP", "FOR", "NEXT", "START", "BIGINT", "MAXVALUE", "INCREMENT", "FIRST", "ROWS",
			"ONLY", "DATE", "ALL", "CALL", "OUTER", "IS", "NOT", "VALUE", "VIEW", "INSERT", "INTO", "VALUES", "AND",
			"ORDER", "DESC", "BEGIN", "DECLARE", "END", "CLOSE", "OPEN",  "LIKE", "DELETE" , "DISTINCT", "WHEN", "THEN"

	};

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
      
    private static final String SEMICOLON_PATTERN = "\\;";
    // 字符串
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"|'([^'\\\\]|\\\\.)*'";
    // 注释
    private static final String COMMENT_PATTERN = "//[^\n]*" +"|"+"--[^\n]*"+ "|" + "/\\*(.|\\R)*?\\*/";
//    private static final String COMMENT_PATTERN2 = "--[^\n]*";  '([^'\\\\]|\\\\.)*'
    private static String patternString =  
    		  "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            
//            + "|(?<PAREN>" + PAREN_PATTERN2 + ")"
//            + "|(?<BRACE>" + BRACE_PATTERN2 + ")"
//            + "|(?<BRACKET>" + BRACKET_PATTERN2 + ")"
            
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
				    		 ;
    
    private static final Pattern PATTERN = Pattern.compile(patternString  );
    
 
 
    
    public static  StyleSpans<Collection<String>> findEqualyWord(String str, String text) {
    	String mypatternString = patternString + "|(?<FINDWORD>(" + str.toUpperCase() + "))"; 
    	Pattern pattern = Pattern.compile( mypatternString  );
    	Matcher matcher = pattern.matcher(text.toUpperCase());
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
            	    matcher.group("FINDWORD") != null ? "findword" :
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("COMMENT") != null ? "comment" : 
                    null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
      
    }

    public static void applyHighlighting(CodeArea codeArea) {
//    	codeArea.textProperty().removeListener();
//    	StyleSpans<Collection<String>> highlighting  = findEqualyWord("foo", codeArea.getText());
    	if(codeArea.getText().length() > 0) {
    		StyleSpans<Collection<String>> highlighting  = 	computeHighlighting(codeArea.getText());
            codeArea.setStyleSpans(0, highlighting);
    	}
    	
    }
    public static void applyFindWordHighlighting(CodeArea codeArea,String str) {
    	StyleSpans<Collection<String>> highlighting  = findEqualyWord(str, codeArea.getText()); 
    	codeArea.setStyleSpans(0, highlighting);
    	
    }  
    public static void applyErrorHighlighting( int begin , int length) {
    	CodeArea codeArea  = SqlEditor.getCodeArea();
    	StyleSpansBuilder<Collection<String>> spansBuilder  = new StyleSpansBuilder<>();
    	spansBuilder.add(Collections.singleton("errorword"), length);
    	StyleSpans<Collection<String>> highlighting  = spansBuilder.create();
    	codeArea.setStyleSpans(begin, highlighting);
    	
    }
    
    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
    	 
        Matcher matcher = PATTERN.matcher(text.toUpperCase());
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("COMMENT") != null ? "comment" :
                    null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
    
    
    private static StyleSpans<Collection<String>> computeErrorHighlighting(String text) {
   	 
        Matcher matcher = PATTERN.matcher(text.toUpperCase());
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) { 
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword-error" :
                    matcher.group("PAREN") != null ? "paren-error" :
                    matcher.group("BRACE") != null ? "brace-error" :
                    matcher.group("BRACKET") != null ? "bracket-error" :
                    matcher.group("SEMICOLON") != null ? "semicolon-error" :
                    matcher.group("STRING") != null ? "string-error" :
                    matcher.group("COMMENT") != null ? "comment-error" :
                    null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.singleton("sql-error"), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.singleton("sql-error"), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
    
    
}
