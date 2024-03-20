package net.tenie.Sqlucky.sdk.ui;

import javafx.application.Platform;
import net.tenie.Sqlucky.sdk.utility.StrUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author tenie
 *
 */
public class CodeAreaHighLightingNoKeyWordHelper extends CodeAreaHighLightingHelper {





	@Override
	protected String getPatternString(String appendstr) {
	       String PAREN_PATTERN = "\\(|\\)|=|<|>|!";
	       String BRACE_PATTERN = "\\{|\\}";
	       String BRACKET_PATTERN = "\\[|\\]";
	       String SEMICOLON_PATTERN = "\\;";
	        // 字符串
	       String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"|'([^'\\\\]|\\\\.)*'|`([^`\\\\]|\\\\.)*`";

	       String patternString =  
	                  "(?<PAREN>" + PAREN_PATTERN + ")"
	                + "|(?<BRACE>" + BRACE_PATTERN + ")"
	                + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
	                + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
	                + "|(?<STRING>" + STRING_PATTERN + ")";
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





//	public 	StyleSpansBuilder<Collection<String>>  createStyleSpansBuilder(Matcher matcher, String text){
//		StyleSpansBuilder<Collection<String>> spansBuilder
//				= new StyleSpansBuilder<>();
//		int lastKwEnd = 0;
//		while(matcher.find()) {
//			String styleClass =
//					matcher.group("FINDWORD") != null ? "findword" :
//					matcher.group("KEYWORD") != null ? "keyword" :
//					matcher.group("PAREN") != null ? "paren" :
//					matcher.group("BRACE") != null ? "brace" :
//					matcher.group("BRACKET") != null ? "bracket" :
//					matcher.group("SEMICOLON") != null ? "semicolon" :
//					matcher.group("STRING") != null ? "string" :
//					null; /* never happens */ assert styleClass != null;
//			spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
//			spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
//			lastKwEnd = matcher.end();
//		}
//		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
//		return spansBuilder;
//	}
	@Override
    public  StyleSpans<Collection<String>> findEqualyWord(String str, String text) {
		str = makeQueryStringAllRegExp(str);
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
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
      
    }

	@Override
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
//    public   void applyFindWordHighlighting(CodeArea codeArea,String str) {
//    	try {
//    		StyleSpans<Collection<String>> highlighting  = findEqualyWord(str, codeArea.getText());
//        	if(highlighting !=null) {
//        		Platform.runLater(() -> {
//        			codeArea.setStyleSpans(0, highlighting);
//        		});
//        	}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//    }
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
    
    private StyleSpans<Collection<String>> computeHighlighting(String text) {
    	var pattern = getPattern();
    	if(pattern == null ) return null;
        Matcher matcher = pattern.matcher(text.toUpperCase());
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
//                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :	
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    } 

}
