package net.tenie.fx.component.CodeArea;
 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import javafx.application.Platform;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
/**
 * 
 * @author tenie
 *
 */
public class CodeAreaHighLightingHelper { 


	private List<String> keywords ; 
	
	
	public CodeAreaHighLightingHelper(List<String> ks) {
		keywords = ks;
	}
	
	public CodeAreaHighLightingHelper() {  
	  keywords = new ArrayList<>(Arrays.asList(  "SELECT", "FROM", "UPDATE", "WHERE", "CASE", "CHAR", "OR",
			"LEFT", "RIGHT", "DOUBLE", "INNER", "JOIN", "EXISTS", "FLOAT", "ALTER", "TABLE", "DATA", "TYPE", "INT",
			"VARCHAR", "LONG", "SET", "SHORT", "TIMESTAMP", "GROUP", "BY", "ON", "AS", "DECIMAL", "PRIMARY", "NULL",
			"CHARACTER", "CONSTRAINT", "CREATE", "SEQUENCE", "WITH", "ELSE", "NUMERIC", "COLUMN", "ADD", "FETCH", "UNION",
			"DEFAULT", "CURRENT", "DROP", "FOR", "NEXT", "START", "BIGINT", "MAXVALUE", "INCREMENT", "FIRST", "ROWS",
			"ONLY", "DATE", "ALL", "CALL", "OUTER", "IS", "NOT", "VALUE", "VIEW", "INSERT", "INTO", "VALUES", "AND",
			"ORDER", "DESC", "BEGIN", "DECLARE", "END", "CLOSE", "OPEN",  "LIKE", "DELETE" , "DISTINCT", "WHEN", "THEN"

	  ));
		
		
	}
	
	private String getPatternString(String appendstr) {
		   String KEYWORD_PATTERN = "\\b(" + String.join("|", keywords) + ")\\b";
	       String PAREN_PATTERN = "\\(|\\)";
	       String BRACE_PATTERN = "\\{|\\}";
	       String BRACKET_PATTERN = "\\[|\\]";
	       String SEMICOLON_PATTERN = "\\;";
	        // 字符串
	       String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"|'([^'\\\\]|\\\\.)*'";
	        // 注释
	       String COMMENT_PATTERN = "//[^\n]*" +"|"+"--[^\n]*"+ "|" + "/\\*(.|\\R)*?\\*/";
	       String patternString =  
	        		  "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
	                + "|(?<PAREN>" + PAREN_PATTERN + ")"
	                + "|(?<BRACE>" + BRACE_PATTERN + ")"
	                + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
	                + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
	                + "|(?<STRING>" + STRING_PATTERN + ")"
	                + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
	    		    		 ;
	       if(StrUtils.isNotNullOrEmpty(appendstr)) {
		    	  patternString += appendstr;
		      };
	       return patternString;   
	}
	
	private Pattern getPattern(String patternString) { 
	   Pattern pattern = null;
	   try {
		    pattern = Pattern.compile(patternString  );
		} catch (Exception e) {
			e.printStackTrace();
		}
      
       return pattern;
    }
    
	private Pattern getPattern() { 
    	String patternString = getPatternString("");
    	Pattern pattern = null;
    	try {
    		pattern = Pattern.compile(patternString  );
		} catch (Exception e) {
		    e.printStackTrace();
		}
        
        return pattern;
     }
 
    
    public  StyleSpans<Collection<String>> findEqualyWord(String str, String text) {
    	String mypatternString = "|(?<FINDWORD>(" + str.toUpperCase() + "))"; 
    	mypatternString = getPatternString(mypatternString); 
    	Pattern pattern = getPattern(mypatternString); 
    	if(pattern == null ) return null;
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
    
    public   StyleSpans<Collection<String>> findErrorWord(String preTxt , String sufTxt, String errTxt){
    	var pattern = getPattern();
    	if(pattern == null ) return null;
    	Matcher matcher = pattern.matcher(preTxt.toUpperCase());
        int lastKwEnd = 0;
        var spansBuilder  = new StyleSpansBuilder<Collection<String>>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("COMMENT") != null ? "comment" :
                    null; 
            /* never happens */ 
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), preTxt.length() - lastKwEnd);
        
        //  errorword
        spansBuilder.add(Collections.singleton("errorword"), errTxt.length());
        
        // 后半部分
        matcher = pattern.matcher(sufTxt.toUpperCase());  
        lastKwEnd = 0; 
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("COMMENT") != null ? "comment" :
                    null; 
            /* never happens */ 
            assert styleClass != null; 
            spansBuilder.add(Collections.emptyList(),  matcher.start() - lastKwEnd );
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start() );
            lastKwEnd = matcher.end();
        }
        
        spansBuilder.add(Collections.emptyList(), sufTxt.length() - lastKwEnd);
        
        return spansBuilder.create();
    
    }
    public  void applyHighlighting(CodeArea codeArea) { 
    	applyHighlighting(codeArea, 0);
    }
    public  void applyHighlighting(CodeArea codeArea, int begin) { 
    	try {
    		String text = codeArea.getText();
    		if(text.length() > 0 && begin < text.length()) {
    			
    			String subText = codeArea.getText().substring(begin);
    			StyleSpans<Collection<String>> highlighting  = 	computeHighlighting(subText);
    			if(highlighting == null ) return;
    			Platform.runLater(() -> {  
            		 codeArea.setStyleSpans(begin, highlighting);
    			});    	    	
        	}
		} catch (Exception e) {
			e.printStackTrace();
		} 
    	
    }
    public   void applyFindWordHighlighting(CodeArea codeArea,String str) {
    	try {
    		StyleSpans<Collection<String>> highlighting  = findEqualyWord(str, codeArea.getText()); 
        	if(highlighting !=null) { 
        		Platform.runLater(() -> {
        			codeArea.setStyleSpans(0, highlighting);
        		});
        	}
        		
		} catch (Exception e) {
			e.printStackTrace();
		} 
    	
    }  
    public  void applyErrorHighlighting(CodeArea codeArea , int begin  , String str) {
    	int length = str.length();
    	if(str.endsWith("\n")) {
    		length--;
    	}
	    StyleSpansBuilder<Collection<String>> spansBuilder  = new StyleSpansBuilder<>();
    	spansBuilder.add(Collections.singleton("errorword"), length);  // str.length()-1
    	StyleSpans<Collection<String>> highlighting  = spansBuilder.create();
    	Platform.runLater(() -> {
    		codeArea.setStyleSpans(begin, highlighting); 
		});   	
    }
    
    private StyleSpans<Collection<String>> computeHighlighting(String text) {
    	var pattern = getPattern();
    	if(pattern == null ) return null;
        Matcher matcher = pattern.matcher(text.toUpperCase());
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

}
