package net.tenie.Sqlucky.sdk.utility;

import javafx.beans.property.StringProperty;
import javafx.scene.control.IndexRange;
import net.tenie.Sqlucky.sdk.po.MyRange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jasypt.util.text.BasicTextEncryptor;

import java.io.File;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StrUtils {
    private static Logger logger = LogManager.getLogger(StrUtils.class);
    public final static SimpleDateFormat sdf_Date = new SimpleDateFormat("yyyy-MM-dd");
    public final static SimpleDateFormat sdf_DateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat sdf_Time = new SimpleDateFormat("HH:mm");
    public static String dateFormateL = "yyyy-MM-dd HH:mm:ss";
    public static String dateTimeForFileName = "yyyy-MM-dd_HH_mm_ss";
    public final static String EMPTY_STRING = "";
    public final static String BLANK_SPRING_STRING = " ";
    public final static String CHINESE_BLANK_CHAR  = "　";
    public static final char CHAR_TILDE = '~';
    // HTML空白字符 &nbsp;&ensp;&emsp;
    public final static String HTML_BLANK_STRING="   ";
    public final static String HTML_BLANK_CHAR_NBSP =" ";
    public final static String HTML_BLANK_CHAR_ENSP =" ";
    public final static String HTML_BLANK_CHAR_EMSP =" ";



    // 字符串正则
//    public static final  String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"|'([^'\\\\]|\\\\.)*'|`([^`\\\\]|\\\\.)*`";

    public static final  String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"|'([^'\\\\]|\\\\.)*'";
    // 注释
    public static final  String COMMENT_PATTERN = "//[^\n]*" + "|" + "--[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
    public static final  String COMMENT_PATTERN_1 =  "--[^\n]*";
    public static final  String COMMENT_PATTERN_2 =  "#[^\n]*";
    // 注释
    public static final  String COMMENT_ANNOTATIONS_PATTERN = "//[^\n]*" + "|" + "--[^\n]*" +  "|" + "@[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    // xml 元素
    public static final  String XML_ELE_PATTERN =  "\\<.[^<>]*\\>";

    // xml 元素
    public static final  String MYBATIS_PARAM_PATTERN =  "#\\{(.|\\R)*?\\}" + "|"  + "\\$\\{(.|\\R)*?\\}";
    // 占位符
    public static final String PLACEHOLDERS =" _placeholders-yYJMhfbTQtnkg9lt2fHv_ ";

    /**
     * 文本中对 区间内的行加注解
     * @param text
     * @param i
     * @return
     */
    public static String textAddCommentsByRange(String text, IndexRange i) {
        int start = i.getStart();
        int end = i.getEnd();
        StringBuilder rsStr = new StringBuilder();

        // 修正开始下标 , 获取开始之前的字符串, 找到最接近start 的换行符
        String frontTxt = text.substring(0, start);
        int lidx = frontTxt.lastIndexOf('\n'); // 找到最后一个换行符
        if (lidx > 0) {
            lidx = frontTxt.length() - lidx - 1; // 获取换行符的位置, 不包括换行符自己
            start = start - lidx; // start的位置定位到最后一个换行符之后
        } else { // 如果没有找到换行符, 说明在第一行, 把start置为0
            start = 0;
        }
        // 获取文本
        String txt = text.substring(start, end);
        // 添加注释
        if (!StrUtils.beginWith(txt.trim(), "--")) {
            txt = txt.replaceAll("\n", "\n-- ");
            txt = "-- " + txt;
        }
        rsStr.append(text.substring(0, start));
        rsStr.append(txt);
        rsStr.append(text.substring(end));
        return rsStr.toString();
    }


    /**
     * 根据下标从文本中找到下标所在的行
     * @return
     */
    public static int findIndexLine(String text, int strIdx){
        List<Integer> idxList = findStrAllIndex(text, "\n", false);
        int tmpIdx = -1;
        if (idxList.isEmpty()){
            tmpIdx = 0;
        }else {
            idxList.add(0, 0);
        }
        for(int i = 0 ; i < idxList.size(); i++){
            int idx = idxList.get(i);
            if( strIdx < idx){
                if(i == 0){
                    tmpIdx = idxList.get( i );
                }else {
                    tmpIdx = idxList.get( i -1);
                }
                break;
            }
        }
        return  tmpIdx;
    }

    /**
     * 根据下标从文本中找到下标所在的行
     * @return
     */
    public static MyRange findIndexLineRange(String text, int strIdx){
        List<Integer> idxList = findStrAllIndex(text, "\n", false);
        int tmpIdxStart = -1;
        int tmpIdxEnd = -1;
        MyRange mr = null;
        if (idxList.isEmpty()){
            tmpIdxStart = 0;
        }else {
            idxList.add(0, 0);
        }
        for(int i = 0 ; i < idxList.size(); i++){
            int idx = idxList.get(i);
            if( strIdx < idx){
                if(i == 0){
                    tmpIdxStart = 0;
                    tmpIdxEnd = 0;

                }else {
                    tmpIdxStart = idxList.get( i -1);
                    tmpIdxEnd = idx-1;
                }
                break;
            }
        }
        if(tmpIdxEnd > -1 && tmpIdxStart > -1){
             mr = new MyRange(tmpIdxStart, tmpIdxEnd);
        }

        return  mr;
    }


    /**
     * 替换字符串
     * @param val
     * @param oldStr
     * @param strNew
     * @return
     */
    public static String myReplaceAll(String val , String oldStr, String strNew, boolean sensitive){
        List<Integer> rsList =  findStrAllIndex(val, oldStr, sensitive);
        StringBuilder str = new StringBuilder();
        int beginIdx = 0;
        for(Integer idx : rsList){
           String tmp1 =  val.substring(beginIdx,  idx);
            str.append(tmp1);
            str.append(strNew);
            beginIdx = idx + oldStr.length();

        }
        String tmp1 =  val.substring(beginIdx);
        str.append(tmp1);
        return str.toString();
    }

    /**
     * 查找字符串， 找到所以匹配字符所在的位置
     * @param valStr
     * @param findStr
     * @return
     */
    public static List<Integer> findStrAllIndex(String valStr , String findStr, boolean sensitive){
        if(! sensitive){
            valStr =  valStr.toUpperCase();
            findStr =  findStr.toUpperCase();
        }
        List<Integer> rsList = new ArrayList<>();
        int fromIndex = 0;
        int currentIndex = valStr.indexOf(findStr, fromIndex);
        while (currentIndex > -1 ){
            rsList.add(currentIndex);
            fromIndex = currentIndex + findStr.length();
            currentIndex = valStr.indexOf(findStr, fromIndex);
        }


        return rsList;
    }

    // 字符串list 排序
    public static List<Integer> StrListToIntList(List<String> ls) {
        List<Integer> rs = new ArrayList<>();
        for (String str : ls) {
            Integer v = Integer.valueOf(str.trim());
            rs.add(v);
        }
        rs.sort(Comparator.comparing(Integer::intValue));
        return rs;
    }

    // 获取字符串的前缀空白字符
    public static String prefixBlankStr(String txt) {
        StringBuilder strb = new StringBuilder("");
        for (int i = 0; i < txt.length(); i++) {
            char c = txt.charAt(i);
            if (c == ' ' || c == '\t') {
                strb.append(c);
            } else {
                break;
            }
        }
        return strb.toString();
    }

    // 提取string 中的单词
    public static Set<String> splitWordByStr(String str) {
        String tmp = str.replaceAll(" ", "\n");
        tmp = tmp.replaceAll("\t", "\n");
        String[] vals = tmp.split("\n");

        Set<String> rsset = new HashSet<>();
        if (vals != null) {
            for (var v : vals) {
                String rsstr = v.trim();
                if (!rsstr.isEmpty()) {
                    rsset.add(rsstr);
                }
            }
        }

        return rsset;
    }

    // 驼峰命名转下划线
    public static String CamelCaseUnderline(String str) {
        StringBuilder rs = new StringBuilder();

        rs.append(str.charAt(0));
        String blankStr = " \t_";
        for (int i = 1; i < str.length(); i++) {
            char strChar = str.charAt(i);
            char frontChar = str.charAt(i - 1);
            if (strChar >= 'A' && strChar <= 'Z') {
                if (!(frontChar >= 'A' && frontChar <= 'Z')
                        && !blankStr.contains(frontChar + "")) {
                    rs.append("_");
                }
                rs.append(strChar);
            } else {
                rs.append(strChar);
            }
        }
        return rs.toString();
    }

    /**
     * 去除多行注释
     * @param sql
     * @return
     */
    // /*  ??  */   "(/\\*[\\s\\S]*?\\*/)";
    public static String rmMultiLineComment(String sql) {
        String ps = "/\\*([\\s\\S]*?)\\*/";
        Pattern p = Pattern.compile(ps); // Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(sql);
        return m.replaceAll("");
    }

    /**
     * 多行注释 转为空格
     * @param sql
     * @return
     */
    // /*  ??  */   "(/\\*[\\s\\S]*?\\*/)";
    @Deprecated
    public static String multiLineCommentToSpace(String sql) {
        String ps = "/\\*([\\s\\S]*?)\\*/";
        Pattern p = Pattern.compile(ps);
        Matcher m = p.matcher(sql);

        String val = m.replaceAll(" ");
        return val;
    }

    // 去除2边指定的字符
    public static String trimChar(String str, String tag) {
        String rs = "";
        str = str.trim();
        if (str.indexOf(tag) == 0) {
            rs = str.substring(1);
        } else {
            rs = str;
        }
        if (rs.lastIndexOf(tag) == rs.length() - 1) {
            rs = rs.substring(0, rs.length() - 1);
        }

        return rs;
    }

    public static String trimRightChar(String str, String tag) {
        String rs = str.trim();
        if (rs.lastIndexOf(tag) == rs.length() - 1) {
            rs = rs.substring(0, rs.length() - 1);
        }
        return rs;
    }

    /**
     * 找到下标位置它所在的一个完整单词, 前后是空格 或 换行符的单子 , 如: 123 456 7809, 下标为5, 找到456
     * @param str
     * @param idx
     * @return
     */
    public static String findContinuousWordByIndex(String str, int idx ){

        int prefixIdx =  str.lastIndexOf(" ", idx);
        if(prefixIdx == -1){
              prefixIdx =  str.lastIndexOf("\n", idx);
              if(prefixIdx == -1){
                  prefixIdx = 0;
              }
        }
        if(prefixIdx > 0 ){
            prefixIdx++;
        }
        int suffixIdx = str.indexOf(" ", idx);
        if(suffixIdx == -1){
            suffixIdx =  str.indexOf("\n", idx);
            if(suffixIdx == -1){
                suffixIdx = str.length();
            }
        }else {
            int tmpSuffixIdx = str.indexOf("\n", idx);
            if(tmpSuffixIdx > -1){
                if(tmpSuffixIdx < suffixIdx){
                    suffixIdx = tmpSuffixIdx;
                }
            }
        }

        String rs = str.substring(prefixIdx, suffixIdx);

        return rs;
    }

    /**
     * str 中的所有下划线单词转换为驼峰命名单词
     * @param str
     * @return
     */
    public static String underlineCaseCamel2(String str) {
        if(str.contains("_")){
            List<Integer> idxList = findStrAllIndex(str,"_", true);
            Set<String> strSet = new HashSet<>();
            for(Integer idx : idxList){
                String tmmStr = findContinuousWordByIndex(str, idx);
                strSet.add(tmmStr);
            }
            // 单词转换-> 单词替换
            for(String oldStr: strSet){
                String newStr = underlineWordToCaseCamel(oldStr);
                str = str.replaceAll(oldStr, newStr);
            }
        }

        return str;
    }

    /**
     * 单词转为驼峰命名
     * @param word
     * @return
     */
    public static String wordUnderlineCaseCamel(String word) {
        String newWord = underlineCaseCamel(word);
        if(word.equals(newWord)){
            return word.toLowerCase();
        }
        return newWord;
    }

    /**
     * 字符串转为驼峰命名
     * @param str
     * @return
     */
    public static String underlineCaseCamel(String str) {
        StringBuilder strVal = new StringBuilder();
        if(str.contains("_")){
            List<String> lineStrList = new ArrayList<>();
           if(str.contains("\n")){
               String[] lineArr = str.split("\n");
               lineStrList.addAll(List.of(lineArr));
           }else {
               lineStrList.add(str);
           }

           if(lineStrList.size() > 1){
               int listSize = lineStrList.size();
               for(int i = 0 ; i < listSize; i++){
                   String lineStr = lineStrList.get(i);
                   if(lineStr.contains("\t")){
                       lineStr = lineStr.replaceAll("\t", " ");
                   }
                   if(lineStr.contains(" ")){
                      String[] wordArr =  lineStr.split(" ");
                      for(String word : wordArr){
                          if(word.contains("_")){
                              String tmpStr = underlineWordToCaseCamel(word);
                              strVal.append(tmpStr + " ");
                          }else {
                              strVal.append(word + " ");
                          }
                      }
                   }else {
                       if(lineStr.contains("_")){
                           String tmpStr = underlineWordToCaseCamel(lineStr);
                           strVal.append(tmpStr );
                       }else {
                           strVal.append(lineStr );
                       }
                   }

                   if(i < (listSize -1)){
                       strVal.append("\n");
                   }
               }
           }else {
               String tmpStr = underlineWordToCaseCamel(str);
               strVal.append(tmpStr);
           }

//            List<Integer> idxList = findStrAllIndex(str,"_", true);
//            Set<String> strSet = new HashSet<>();
//            for(Integer idx : idxList){
//                String tmmStr = findContinuousWordByIndex(str, idx);
//                strSet.add(tmmStr);
//            }
//            // 单词转换-> 单词替换
//            for(String oldStr: strSet){
//                String newStr = underlineWordToCaseCamel(oldStr);
//                str = str.replaceAll(oldStr, newStr);
//            }
        }
        if(StrUtils.isNullOrEmpty(strVal.toString())){
                return str;
        }
        return strVal.toString();
//        return str;
    }

    // 下划线单词 轉 驼峰命名单词
    public static String underlineWordToCaseCamel(String str) {
        StringBuilder rs = new StringBuilder();
        boolean tf = false;
        char previousChar = ' ';
        for (int i = 0; i < str.length(); i++) {
            char strChar = str.charAt(i);
            if (i > 0) {
                previousChar = str.charAt(i - 1);
            }
            if (strChar == '_') {
                tf = true;
            } else {
                //  下划线之后的匹配
                if (tf) {
                    // 如果字符是小写字母转换为大写
                    if (strChar >= 'a' && strChar <= 'z') {
                        strChar -= 32;
                    }

                } else {  // 不是下划线就转为小写
                    if (previousChar >= 'a' && previousChar <= 'z'
                            && strChar >= 'A' && strChar <= 'Z') {
                        previousChar = strChar;
                    } else if (strChar >= 'A' && strChar <= 'Z') {
                        strChar += 32;
                    }
                }
                rs.append(strChar);
                tf = false;
            }
        }
        return rs.toString();
    }

    /**
     * 清除html的空白字符串
     * @return
     */
    public static String cleanHtmlBlankChar(String str){
        str = str.replaceAll(HTML_BLANK_CHAR_EMSP," ");
        str = str.replaceAll(HTML_BLANK_CHAR_NBSP," ");
        str = str.replaceAll(HTML_BLANK_CHAR_ENSP," ");
        str = str.replaceAll(CHINESE_BLANK_CHAR," ");

        return str;
    }

    /**
     * 清除多余空白符, 字符串中的空格会有问题
     * @return
     */
    public static String cleanrRedundantBlank(String str){
        // 清除特殊的空白字符串
        str =  cleanHtmlBlankChar(str);

        // 清除制表符
        if(str.contains("\t")){
            str = str.replaceAll("\t", " ");
        }

        // 循环替换双空格
        String blankStr = "  ";
        while (str.contains(blankStr)){
            str = str.replaceAll(blankStr, " ");
        }

        return str;
    }


    /**
     *  去除字符串中的非数字部分
     * @param str
     * @return
     */
    public static String clearStrToNumericStr(String str) {
        StringBuffer val = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c >= '0' && c <= '9') {
                val.append(c);
            }
        }
        return val.toString();
    }

    /**
     * 判断字符串是不是数字
     * @param str
     * @return 是数字返回true
     */
    public static boolean isNumeric(String str) {
        boolean tf = false;
        if(!str.isEmpty() ){
            tf = str.matches("-?\\d+(\\.\\d+)?");
        }
        return  tf;
    }

    /**
     * 去除字符串中的非数字部分, 包含. 如: 文本框只能输入ip字符串
     * @param str
     * @return
     */
    public static String clearIpStr(String str) {
        StringBuffer val = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if ((c >= '0' && c <= '9') || c == '.') {
                val.append(c);
            }
        }
        return val.toString();
    }

    /**
     *  压缩字符串, 除注释行外所有的行合并成一行
     * @param text
     * @return
     */
    public static String pressString(String text) {
        StringBuilder str = new StringBuilder();
        text = replaceAllCommentType(text);
        text = text.replaceAll("\r", "");
        String[] val = text.split("\n");
        if (val.length > 0) {
            for (String v : val) {
                str.append(" ").append(v);
            }

            String dest = str.toString().trim();
            dest = dest.replaceAll("\t", " ");
            int sz = dest.length();
            while (true) {
                dest = dest.replaceAll(" {2}", " ");
                int tmpSz = dest.length();
                if (tmpSz == sz) {
                    break;
                } else {
                    sz = tmpSz;

                }
            }

            dest = dest.replaceAll("\n ", "\n");

            return dest.trim();
        } else {
            return text;
        }

    }


    /**
     * 删除空行
     * @param text
     * @return
     */
    public static String cleanEmptyLine(String text) {
        StringBuilder str = new StringBuilder();
        text = text.replaceAll("\r", "");
        String[] val = text.split("\n");
        if (val.length > 0) {
            for (String line : val) {
                if (!line.trim().isEmpty()){
                    str.append(line).append("\n");
                }
            }
            return str.toString();
        }
        return text;
    }

    /**
     * 获取uuid
     * @return
     */
    public static UUID getRandomUUID() {
        return UUID.randomUUID();
    }

    /**
     * 判断字符串开始的字符串时候是 tag
     * @param str
     * @param tag
     * @return
     */
    public static boolean beginWith(String str, String tag) {
        boolean tf = false;
        if (str != null && tag != null && str.length() >= tag.length()) {
            if (str.subSequence(0, tag.length()).equals(tag)) {
                tf = true;
            }
        }
        return tf;
    }

    /**
     * 判断字符串开始的字符串时候是 tag, 大小写不敏感
     * @param str
     * @param tag
     * @return
     */
    public static boolean beginWithNotSensitive(String str, String tag) {
        boolean tf = false;
        if (str != null && tag != null && str.length() > tag.length()) {
            if (str.subSequence(0, tag.length()).toString().toUpperCase().equals(tag.toUpperCase())) {
                tf = true;
            }
        }
        return tf;
    }

    /**
     *    去除多余空白字符， 转化为一个" "
     */
    public static String clearBlank(String str) {
        String temp = str.replaceAll("\r", "");
        temp = temp.replaceAll("\n", " ");
        temp = temp.replaceAll("\t", " ");
        temp = temp.trim();
        String rs = "";
        if (temp.indexOf(' ') > 0) {
            String[] arr = temp.split(" ");
            for (int i = 0; i < arr.length; i++) {
                String val = arr[i].trim();
                if (val.length() > 0) {
                    rs += val + " ";
                }
            }
        }

        return rs;
    }

    /**
     * 生成一定长度的空白字符串
     * @param len
     * @return
     */
    public static String createBlankString(int len) {
        StringBuilder strb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            strb.append(" ");
        }
        return strb.toString();
    }

    // 去除注释
    @Deprecated
    public static String trimComment(String sql, String symbol) {
        if (!sql.contains(symbol)) {
            return sql;
        }
        String str = sql.replaceAll(symbol, "\n" + symbol);
        if (str.contains("\r")) {
            str = str.replace("\r", "");
        }

        String[] sa = str.split("\n");
        String nstr = "";
        if (sa != null && sa.length > 1) {
            for (int i = 0; i < sa.length; i++) {
                String temp = sa[i];
                if (!beginWith(temp, symbol)) {
                    nstr += temp + "\n";
                } else {
                    nstr += createBlankString(temp.length());
                }
            }
        }
        if (nstr.isEmpty()) {
            nstr = sql;
        }
        return nstr.trim();
    }

    /**
     * 创建Matcher
     */
    public static Matcher createMatcher(String patternStr, String val){
        //去掉空格符合换行符
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(val);
        return matcher;
    }

    /**
     * 是否存在要查找的关键字
     */
    public static boolean hasKeyWord(String valText, String keyWord){
        String KEYWORD_PATTERN = "\\b(" + keyWord + ")\\b";
        String patternString = "(?<KEYWORD>" + KEYWORD_PATTERN + ")";
        Pattern pattern = Pattern.compile(patternString); //去掉空格符合换行符
        Matcher matcher = pattern.matcher(valText);

        return matcher.find();
    }

    /**
     * 替换单行注解 -- # ,便于压缩sql
     */
    public static String replaceAllCommentType( String text){
        text = replaceCommentType(COMMENT_PATTERN_2, "#", text);
        return replaceCommentType(COMMENT_PATTERN_1, "--", text);
    }

    /**
     * 把单行注释改为多行注释, 便于压缩sql
     *  把 -- 注释改成 /*
     */
    public static String replaceCommentType(String comPat, String comStr, String text){
        List<IndexRange> ls = findAllCommentByPatternStr(comPat, text);
        if(ls.isEmpty()){
            return text;
        }
        List<String> textLs = new ArrayList<>();
        int start = 0;
        int end = 0;
        for(var idxR : ls){
            start = end;
            end =  idxR.getStart();
            String tmp = text.substring(start, end);
            textLs.add(tmp);
            tmp = text.substring(idxR.getStart(), idxR.getEnd());
            tmp = "/* " + tmp.replaceAll(comStr, "")+ " */";
            textLs.add(tmp);
            end = idxR.getEnd();
        }
        if(end < text.length()){
            String tmp = text.substring(end);
            textLs.add(tmp);
        }

        return String.join("", textLs);
    }

    // 根据正则匹配, 替换字符串
    public static String replaceStrByPattern(String commentPatternStr, String newStr, String text){
        String patternString = "(?<STRING>" + STRING_PATTERN + ")"
                + "|(?<COMMENT>" + commentPatternStr + ")";

        // 找到所有注释
        Matcher matcher = createMatcher(patternString, text);
        while (matcher.find()){
            if( matcher.group("COMMENT") != null){
                text = matcher.replaceAll(newStr);
            }
        }
        return text;
    }

    /**
     * 找到所有的注释
     */
    public static List<IndexRange> findAllComment(String text){
        return findAllCommentByPatternStr(COMMENT_PATTERN, text);
    }

    /**
     * 找到所有的注释
     */
    public static List<IndexRange> findAllCommentByPatternStr(String commentPatternStr, String text){

        String patternString = "(?<STRING>" + STRING_PATTERN + ")"
                        + "|(?<COMMENT>" + commentPatternStr + ")";
        // 找到所有注释
        Matcher matcher = createMatcher(patternString, text);
        List<IndexRange> commentlist =new ArrayList<>();
        while (matcher.find()){
            if( matcher.group("COMMENT") != null){
                IndexRange ir = new IndexRange(matcher.start(), matcher.end());
                commentlist.add(ir);
            }
        }
        return  commentlist;
    }

    public static String TestTrimAllComment(String textVal) {
        System.out.println("====================");
        trimAllComment(textVal);
        System.out.println("====================");
        return "";
    }

    /**
     * 去除所有注释
     * @param textVal
     * @return
     */
    @Deprecated
    public static String trimAllComment(String textVal) {
        List<IndexRange>  ls =  findAllComment(textVal);
        StringBuilder sb = new StringBuilder();
        // 有用字符串的开始
        int idxStart = 0;
        // 有用字符串的开始
        int idxEnd = 0;

        for (IndexRange ir : ls){
            int start = ir.getStart();
            int end = ir.getEnd();
            // 如果第一个字符就是注释起始, 就跳过
            if(idxStart == 0 && start == 0){
                idxStart = end;
                continue;
            }
            // 注释的开始是原字符串上要截取的结束位置
            idxEnd = start;

            // 去除\r\n
            if(textVal.length()> idxEnd){
                int tmp = 0;
                char charVal = textVal.charAt(idxStart);
                // \r
                if(charVal == 13){
                    tmp++;
                    charVal = textVal.charAt(idxStart+1);
                    // \n
                    if (charVal == 10){
                        tmp++;
                    }
                    // \n
                }else if (charVal == 10 ){
                    tmp++;
                }
                idxStart += tmp;
            }
            sb.append(textVal.substring(idxStart, idxEnd))  ;

            // 下一次的开始位置是 注释字符串的结尾
            idxStart = end;
        }

        // 最后一部字符次
        if(idxStart < textVal.length()){
            sb.append(textVal.substring(idxStart))  ;
        }

        return textVal;
    }
    /**
     * 注释转化为空格字符
     * @param textVal
     * @return
     */
    public static String trimAllCommentToBlank(String textVal) {
        List<IndexRange>  ls =  findAllComment(textVal);
        StringBuilder sb = new StringBuilder(textVal);

        for (IndexRange ir : ls){
            int start = ir.getStart();
            int end = ir.getEnd();
            // 创建一个注释字符串一样长度的空白字符串
            String str = createBlankString(end-start);
            // 使用空白字符串替换掉注释
            sb.replace(ir.getStart(), ir.getEnd(), str);
        }

        return sb.toString();
    }

    /**
     * 去除所有注释, 简单粗暴, 会把字符串中的注释去掉
     * @param textVal
     * @return
     */
    public static String trimAllComment2(String textVal) {
        Matcher matcher = createMatcher(COMMENT_PATTERN,textVal );
        if (matcher.find()){
            textVal =  matcher.replaceAll("");
        }
        return textVal;
    }


    /**
     * 去除所有注释, 注解
     * @param textVal
     * @return
     */
    public static String trimAllCommentAnnotations(String textVal) {
        // 1. 先把文本中的字符串替换调
        matherString msVal = getStringMatcher(textVal);
        // 替换后的文本
        String textNew = msVal.newString();

        Matcher matcher = createMatcher(COMMENT_ANNOTATIONS_PATTERN,textNew );
        if (matcher.find()){
            textNew =  matcher.replaceAll("");
        }
        // 还原占位符原本的字符串
        textNew = recoverStringMatcher(msVal, textNew);
        return textNew;
    }

    /**
     * 所有的注释替换成空格
     * 1. 找到所有的文本中的字符串, 用特殊符号占位( 避免字符串中的注释字符串也被清掉)
     * 2. 找到所有文本中的注释, 替换成空格字符串
     * 3. 还原第一步操作
     * @param textVal
     * @return
     */
    public static String replaceAllCommentToSpace(String textVal) {
        // 1. 先把文本中的字符串替换调
        matherString msVal = getStringMatcher(textVal);
        // 替换后的文本
        String textNew = msVal.newString();

        Matcher matcher = createMatcher(COMMENT_PATTERN,textNew );

        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        int rangeBegin = 0;
        while (matcher.find()) {
            count++;
            String blankStr = createBlankString(matcher.end() - matcher.start());
            System.out.println("found: " + count + " : " + matcher.start() + " - " + matcher.end());
            String tmp1 = textNew.substring(rangeBegin,  matcher.start() );
            stringBuilder.append(tmp1);
            stringBuilder.append(blankStr);
            rangeBegin  = matcher.end();

        }
        String endst = textNew.substring(rangeBegin);
        stringBuilder.append(endst);
        String strVal = stringBuilder.toString();
        // 还原占位符原本的字符串
        strVal = recoverStringMatcher(msVal, strVal);
        return strVal;
    }


    /**
     * 文本中的字符串替换成空格
     * 1. 找到所有的文本中的字符串, 用特殊符号占位( 避免字符串中的注释字符串也被清掉)
     * 2. 找到所有文本中的注释, 替换成空格字符串
     * 3. 还原第一步操作
     * @param textVal
     * @return
     */
    public static String replaceAllStringToSpace(String textVal) {
        // 1. 先把文本中的字符串替换调
        matherString msVal = getStringMatcher(textVal);
        // 替换后的文本
        String textNew = msVal.newString();

        Matcher matcher = createMatcher(COMMENT_PATTERN,textNew );

        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        int rangeBegin = 0;
        while (matcher.find()) {
            count++;
            String blankStr = createBlankString(matcher.end() - matcher.start());
            System.out.println("found: " + count + " : " + matcher.start() + " - " + matcher.end());
            String tmp1 = textNew.substring(rangeBegin,  matcher.start() );
            stringBuilder.append(tmp1);
            stringBuilder.append(blankStr);
            rangeBegin  = matcher.end();

        }
        String endst = textNew.substring(rangeBegin);
        stringBuilder.append(endst);
        String strVal = stringBuilder.toString();
        // 还原占位符原本的字符串
        strVal = recoverStringMatcher(msVal, strVal);
        return strVal;
    }
    /**
     * 找到所以注释,和字符串的区间, 发现;不在字符串和注释中, 那么这个;可以进行分割字符串返回sql集合
     */
    public static List<String> findSqlListFromText(String text){
        List<String> sql = new ArrayList<>();
        if(!text.contains(";")){
            sql.add(text);
            return sql;
        }

        Matcher matcher = createMatcher(COMMENT_PATTERN, text);
        List<IndexRange> list =new ArrayList<>();
        while (matcher.find()){
            IndexRange ir = new IndexRange(matcher.start(), matcher.end());
            list.add(ir);
        }
        matcher = createMatcher(STRING_PATTERN, text);
        while (matcher.find()){
            IndexRange ir = new IndexRange(matcher.start(), matcher.end());
            list.add(ir);
        }

        List<Integer>  idxList= StrUtils.findStrAllIndex(text, ";", true);
        // 等待被替换的idx
        List<Integer> needIdx = new ArrayList<>();
        for (Integer idx : idxList){
            boolean tf = false;
            for (IndexRange ir : list){
                if(idx > ir.getStart() && idx < ir.getEnd()){
                    tf = true;
                    break;
                }
            }

            if(!tf){
                needIdx.add(idx);
            }
        }


        if(! needIdx.isEmpty()){
            int begin = 0;
            for (Integer idx : needIdx){
               String sqlTmp =  text.substring(begin, idx);
               if(StrUtils.isNotNullOrEmpty(sqlTmp)){
                  String tempStr = StrUtils.trimAllCommentToBlank(sqlTmp).trim();
                  if(tempStr.length() > 10){
                      sql.add(sqlTmp);
                  }
               }
                begin = idx +1;
            }
            if(begin > 0 ){
                String sqlTmp =  text.substring(begin);
                if(StrUtils.isNotNullOrEmpty(sqlTmp)){
                    String tempStr = StrUtils.trimAllCommentToBlank(sqlTmp).trim();
                    if(tempStr.length() > 10){
                        sql.add(sqlTmp);
                    }
                }
            }

        }else {
            sql.add(text);
            return sql;
        }
        return sql;
    }

    /*
     * 根据";" 分割字符串, 找到要执行的sql, 并排除sql字符串中含有;的情况
     * 1. 先在原始文本中找到sql的字符串, 替换为空白字符串,得到一个新文本
     * 2. 在新文本中根据 ; 分割字符串, 得到每个分割出来的子串在文本中的区间
     * 3. 根据区间, 在原始文本中 提炼出sql语句
     */
    @Deprecated
    public static List<String> findSQLFromTxt1(String textVal) {
        // 1. 先把文本中的字符串替换为空格
       String  textValNew =  replaceAllStringToSpace(textVal);

        // 找到所有的sql分割符 ";"
        List<Integer> idxList = findStrAllIndex(textValNew,";", false);

        // 分割
        List<String> stringList = new ArrayList<>();
        if(!idxList.isEmpty()) {
            int start = 0;
            for (Integer idx : idxList) {
                String tmp = textVal.substring(start, idx);
                start = idx+1;
                if (isNotNullOrEmpty(tmp)) {
                    stringList.add(tmp);
                }
            }
            String tmp = textVal.substring(start);
            if (isNotNullOrEmpty(tmp) &&  !"".equals(tmp.trim())) {
                stringList.add(tmp);
            }
        }
        return stringList;
    }

    @Deprecated
    public static List<String> findSQLFromTxt2(String text) {
        String patternString = "(?<STRING>" + STRING_PATTERN + ")";
        Matcher matcher = createMatcher(patternString,text );
        String txtTmp = "";
        int lastKwEnd = 0;
        // 把匹配到的sql的字符串替换为对应长度的空白字符串, 得到一个和原始文本一样长度的新字符串
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            int len = end - start;
            String space = StrUtils.createBlankString(len);
            txtTmp += text.substring(lastKwEnd, start) + space;
            lastKwEnd = end;
        }
        if (lastKwEnd > 0) {
            String txtEnd = text.substring(lastKwEnd, text.length());
            txtTmp += txtEnd;
        } else {
            txtTmp = text;
        }

        // 在新字符上面, 提取字sql语句的区间
        String str = txtTmp;
        // 根据区间提炼出真正要执行的sql语句
        List<String> sqls = new ArrayList<>();
        if (str.contains(";")) {
            List<MyRange> idxs = new ArrayList<>();
            String[] all = str.split(";"); // 分割多个语句
            if (all != null && all.length > 0) {
                int ss = 0;
                for (int i = 0; i < all.length; i++) {
                    String s = all[i];
                    int end = ss + s.length();
                    if (end > str.length()) {
                        end--;
                    }
                    MyRange mr = new MyRange(ss, end);
                    ss = end + 1;
                    idxs.add(mr);
                }
            }
            for (MyRange mr : idxs) {
                int s = mr.getStart();
                int e = mr.getEnd();
                String tmps = text.substring(s, e);
                sqls.add(tmps);
            }
        } else {
            sqls.add(text);
        }

        return sqls;
    }


    /**
     将注释部分转换为空格字符,保持字符串的长度
     注意: 考虑字符串中的注释符号
     * symbol 注释的符号
     *
     */
    @Deprecated
    public static String trimCommentToSpace(String sql, String symbol) {
        if (!sql.contains(symbol)) {
            return sql;
        }

        // 对包含在字符串中的 symbol 字符串不做处理, 用正则把字符串使用占位符替换掉
        matherString msVal = getStringMatcher(sql);
        String sqlNew = msVal.newString();
        // 在symbol前插入换行符, 之后就是对行的处理
        String str = sqlNew.replaceAll(symbol, "\n" + symbol);
        if (str.contains("\r")) {
            str = str.replace("\r", "");
        }

        String[] sa = str.split("\n");
        String nstr = "";
        if (sa != null && sa.length > 1) {
            // 遍历行
            for (int i = 0; i < sa.length; i++) {
                String temp = sa[i];
                // 如果不是以symbol开头的字符串就保持到nstr字符串
                if (!StrUtils.beginWith(temp, symbol)) {
                    nstr += temp + "\n";
                } else {
                    // 生成空白行的字符串
                    String space = StrUtils.createBlankString(temp.length());

                    nstr = nstr.substring(0, nstr.length() - 1);
                    nstr += space + "\n";
                }
            }
        }
        if (nstr.isEmpty()) {
            nstr = sql;
        }else {
            nstr = recoverStringMatcher(msVal, nstr);
        }

        return nstr;
    }
    /**
     * 用来字符串替换的数据结构
     * @param matcherObj
     * @param newString
     * @param replaceStr
     */
    public record matherString(Matcher matcherObj, String newString, List<String> replaceStr ) {}

    /**
     * 正则匹配字符串, 找出字符串中的字符串 如: "字符串" '字符串'
     */
    public static matherString getStringMatcher(String valStr) {
        Matcher matr = createMatcher(STRING_PATTERN, valStr );
        List<String> tmpStrLs = new ArrayList<>();
        while (matr.find()) {
            String cutstr = valStr.substring(matr.start(), matr.end());
            tmpStrLs.add(cutstr);
        }
        String str2 = matr.replaceAll(PLACEHOLDERS);
        var rs = new matherString(matr, str2, tmpStrLs);
        return rs;
    }

    /**
     * 正则匹配字符串, 找出字符串中的字符串 如: "字符串" '字符串'
     */
    public static matherString getCommentMatcher(String valStr) {
        Matcher matr = createMatcher(STRING_PATTERN, valStr );
        List<String> tmpStrLs = new ArrayList<>();
        while (matr.find()) {
            String cutstr = valStr.substring(matr.start(), matr.end());
            tmpStrLs.add(cutstr);
        }
        String str2 = matr.replaceAll(PLACEHOLDERS);
        var rs = new matherString(matr, str2, tmpStrLs);
        return rs;
    }


    /**
     * 正则匹配字符串, 找出xml元素, 换成占位符, 如 <></>
     */
    public static matherString getXmlEleMatcher(String valStr) {
        Matcher matr = createMatcher(XML_ELE_PATTERN, valStr );
        List<String> tmpStrLs = new ArrayList<>();
        while (matr.find()) {
            String cutstr = valStr.substring(matr.start(), matr.end());
            tmpStrLs.add(cutstr);
        }
        String str2 = matr.replaceAll(PLACEHOLDERS);
        var rs = new matherString(matr, str2, tmpStrLs);
        return rs;
    }

    /**
     * 正则匹配字符串, 找出myBatis元素, 换成占位符, 如 ${} #{}
     */
    public static List<IndexRange>  getMyBatisEleRangeList(String valStr) {
        Matcher matr = createMatcher(MYBATIS_PARAM_PATTERN, valStr );
        List<IndexRange> indexRangeList = new ArrayList<>();
        while (matr.find()) {
            IndexRange ir = new IndexRange(matr.start(), matr.end());
            indexRangeList.add(ir);
        }
        return indexRangeList;
    }
    public static IndexRange  getMyBatisEleRangeFirst(String valStr) {
        Matcher matr = createMatcher(MYBATIS_PARAM_PATTERN, valStr );
        IndexRange ir = null;
        if (matr.find()) {
              ir = new IndexRange(matr.start(), matr.end());
        }
        return ir;
    }

    /**
     * 恢复字符串中的字符串
     */
    public static String recoverStringMatcher(matherString msval, String strVal) {
        List<String> ls = msval.replaceStr();
        for(String str : ls) {
            strVal = strVal.replaceFirst(PLACEHOLDERS, str);
        }
        return strVal;
    }


    /**
     * 恢复字符串的时候, 将字符串注释掉
     */
    public static String recoverStringMatcherToComment(matherString msval, String strVal) {
        List<String> ls = msval.replaceStr();
        for(String str : ls) {
            strVal = strVal.replaceFirst(PLACEHOLDERS, "/*"+str +"*/");
        }
        return strVal;
    }
    /**
     * check if null or empty string
     */
    public static boolean isNullOrEmpty(String obj) {
        return (null == obj || EMPTY_STRING.equals(obj));
    }

    public static boolean isNotNullOrEmpty(String obj) {
        return (!isNullOrEmpty(obj));
    }

    public static boolean isNullOrEmpty(StringProperty obj) {

        return (null == obj || isNullOrEmpty(obj.getValue()));
    }

    public static boolean isNotNullOrEmpty(StringProperty obj) {
        return (!isNullOrEmpty(obj));
    }

    /**
     * 检查是否相等
     */
    public static boolean isEquals(String obj, String expectValue) {
        if (isNullOrEmpty(obj) || isNullOrEmpty(expectValue)) {
            return false;
        }
        return expectValue.equals(obj.toString());
    }

    /**
     * 检查是否相等（忽略大小写）
     */
    public static boolean isEqualsNoCasetive(String obj, String expectValue) {
        if (isNullOrEmpty(obj) || isNullOrEmpty(expectValue)) {
            return false;
        }
        return expectValue.toUpperCase().equals(obj.toString().toUpperCase());
    }

    /**
     * 判断是否是符合的正则表达示
     */
    public static boolean isMatcherPatten(String pattenStr, String extensions) {
        Pattern patten = Pattern.compile(pattenStr);
        Matcher matcher = patten.matcher(extensions);
        if (matcher.find()) {
            return true;
        } else {
            return false;
        }
    }

    // 返回一个时间字符串, 可用于文件名的一部分
    public static String currentDateTimeToFileNameStr() {
        return DateUtils.dateToStr(new Date(), dateTimeForFileName);
    }

    // 字符串转时间
    public static Date StrToDate_L(String str) {
        try {
            return new Date(sdf_DateTime.parse(str).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    // 字符串转时间
    public static java.sql.Date StrToDate_S(String str) {

        try {
            return new java.sql.Date(sdf_Date.parse(str).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

    }

    public static String StrPlitJoin(String str, String regex, String delimiter) {
        StringBuilder rs = new StringBuilder();
        String[] temp = str.split(regex);
        if (temp != null && temp.length > 0) {
            int len = temp.length;
            for (int i = 0; i < len; i++) {
                String s = temp[i];
                if (!s.isEmpty() && !s.trim().isEmpty()) {
                    rs.append(s).append(delimiter);
                }
            }
        }
        if (!rs.isEmpty()) {
            rs = new StringBuilder(rs.substring(0, rs.length() - 1));
        }
        return rs.toString();
    }

    // 字符串计数
    public static int countSubString(String str, String sub) {
        if (StrUtils.isNullOrEmpty(sub) || StrUtils.isNullOrEmpty(str)) {
            return 0;
        }
        int idx = str.indexOf(sub);
        int count = 0;
        while (idx > -1) {
            count++;
            idx = str.indexOf(sub, idx + sub.length());
        }
        return count;

    }

    public static String MenuItemNameFormat(String name) {
        String str = MenuItemNameFormat(name, 40);
        return str;
    }

    public static String MenuItemNameFormat(String name, int size) {
        return String.format("  %-" + size + "s", name);
    }

    // 匹配文件名
    public static boolean matchFileName(String fileTyleStr, File file) {

        String fileName = file.getName();
        boolean rtVal = false;
        if (StrUtils.isNullOrEmpty(fileTyleStr)) {
            rtVal = true;
        }

        if ("*.*".equals(fileTyleStr)) {
            // all
            if (fileName.contains(".")) {
                rtVal = true;
            }

        } else {
            if (fileTyleStr.startsWith("*") && !fileTyleStr.endsWith("*")) {
                String queryStr = fileTyleStr.substring(1);
                // 前缀
                if (fileName.endsWith(queryStr)) {
                    rtVal = true;
                }

            } else if (!fileTyleStr.startsWith("*") && fileTyleStr.endsWith("*")) {
                String queryStr = fileTyleStr.substring(0, fileTyleStr.lastIndexOf("*"));
                // 后缀
                if (fileName.startsWith(queryStr)) {
                    rtVal = true;
                }

            } else if (fileTyleStr.startsWith("*") && fileTyleStr.endsWith("*")) {
                // 包含
                String queryStr = fileTyleStr.substring(1, fileTyleStr.lastIndexOf("*"));
                if (fileName.contains(queryStr)) {
                    rtVal = true;
                }

            } else if (fileTyleStr.contains("*")) {
                // 前后包含
                String[] arrStr = fileTyleStr.split("\\*");
                String qStr1 = arrStr[0];
                String qStr2 = arrStr[1];
                if (fileName.startsWith(qStr1) && fileName.endsWith(qStr2)) {
                    rtVal = true;
                }

            } else if (!fileTyleStr.contains("*")) {
                // 全匹配
                if (fileTyleStr.equals(fileName)) {
                    rtVal = true;
                }

            }

        }
        return rtVal;
    }

    public static String html2Text(String inputString) {
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


    /**
     * 首字母大写
     */
    public static String initialUpperCase(String str) {
        if (str != null) {
            if (str.length() > 1) {
                str = str.substring(0, 1).toUpperCase() + str.substring(1);
            } else {
                str = str.toLowerCase();
            }
        }

        return str;
    }

    /**
     * 将首字母转换为小写
     */
    public static String firstCharToLowerCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            char[] arr = str.toCharArray();
            arr[0] += ('a' - 'A');
            return new String(arr);
        }
        return str;
    }

    /**
     * 将首字母转换为大写
     */
    public static String firstCharToUpperCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'a' && firstChar <= 'z') {
            char[] arr = str.toCharArray();
            arr[0] -= ('a' - 'A');
            return new String(arr);
        }
        return str;
    }



    public static String dbFieldStyleToJavaFieldStyle(String str) {
        str = wordUnderlineCaseCamel(str);
        return str;
    }

    /**
     * 查找 表名/schema名
     */
    public static String tableSchemaName(String tableName, boolean isTable){
        String rs = "";
        if(isTable){ // 找tabale
            if(tableName.contains(".")){
               String[] arr =  tableName.split("\\.");
               rs = arr[1];
            }else {
                rs = tableName;
            }

        }else { // 找schema
            if(tableName.contains(".")){
                String[] arr =  tableName.split("\\.");
                rs = arr[0];
            }else {
                rs = "";
            }
        }


        return rs;
    }

    /**
     *  返回加密后的字符串
     */
    public static String encrypt(String password, String val) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(password);//盐值
        String encryptVal = textEncryptor.encrypt(val);
        return encryptVal;
    }

    /**
     * 返回解密后的字符串
     */
    public static  String decrypt(String password, String encryptStr){
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(password);//盐值
        String  stringVal  = textEncryptor.decrypt(encryptStr);
        return stringVal;
    }


    /**
     *字符串转 byte
     */
    public static byte[] bytes(CharSequence str, Charset charset) {
        if (str == null) {
            return null;
        }

        if (null == charset) {
            return str.toString().getBytes();
        }
        return str.toString().getBytes(charset);
    }

    /**
     * 编码字符串
     *
     * @param str     字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 编码后的字节码
     */
    public static byte[] bytes(CharSequence str, String charset) {
        return bytes(str, isNullOrEmpty(charset) ? Charset.defaultCharset() : Charset.forName(charset));
    }

    /**
     * byte 转字符串
     */
    public static String str(byte[] bytes, String charset) {
        return str(bytes, Charset.forName(charset));
    }

    public static String str(byte[] data, Charset charset) {
        if (data == null) {
            return null;
        }

        if (null == charset) {
            return new String(data);
        }
        return new String(data, charset);
    }

    /**
     * 将已有字符串填充为规定长度，如果已有字符串超过这个长度则返回这个字符串<br>
     * 字符填充于字符串前
     *
     * @param str        被填充的字符串
     * @param filledChar 填充的字符
     * @param len        填充长度
     * @return 填充后的字符串
     * @since 3.1.2
     */
    public static String fillBefore(String str, char filledChar, int len) {
        return fill(str, filledChar, len, true);
    }
    /**
     * 将已有字符串填充为规定长度，如果已有字符串超过这个长度则返回这个字符串
     *
     * @param str        被填充的字符串
     * @param filledChar 填充的字符
     * @param len        填充长度
     * @param isPre      是否填充在前
     * @return 填充后的字符串
     * @since 3.1.2
     */
    public static String fill(String str, char filledChar, int len, boolean isPre) {
        final int strLen = str.length();
        if (strLen > len) {
            return str;
        }

        String filledStr = StrUtils.repeat(filledChar, len - strLen);
        return isPre ? filledStr.concat(str) : str.concat(filledStr);
    }
    /**
     * 字符串常量：空字符串 {@code ""}
     */
    public static final String EMPTY = "";
    /**
     * 重复某个字符
     *
     * <pre>
     * StrUtil.repeat('e', 0)  = ""
     * StrUtil.repeat('e', 3)  = "eee"
     * StrUtil.repeat('e', -2) = ""
     * </pre>
     *
     * @param c     被重复的字符
     * @param count 重复的数目，如果小于等于0则返回""
     * @return 重复字符字符串
     */
    public static String repeat(char c, int count) {
        if (count <= 0) {
            return EMPTY;
        }

        char[] result = new char[count];
        for (int i = 0; i < count; i++) {
            result[i] = c;
        }
        return new String(result);
    }

}
