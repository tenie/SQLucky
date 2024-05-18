package net.tenie.Sqlucky.sdk.utility;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.tenie.Sqlucky.sdk.po.MyRange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.StringProperty;


public class StrUtils {
    private static Logger logger = LogManager.getLogger(StrUtils.class);
    public final static SimpleDateFormat sdf_Date = new SimpleDateFormat("yyyy-MM-dd");
    public final static SimpleDateFormat sdf_DateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String dateFormateL = "yyyy-MM-dd HH:mm:ss";
    public static String dateTimeForFileName = "yyyy-MM-dd_HH_mm_ss";
    public final static String EMPTY_STRING = "";
    public final static String BLANK_SPRING_STRING = " ";
    public static final char CHAR_TILDE = '~';

    // 占位符
    public static final String PLACEHOLDERS =" _placeholders-yYJMhfbTQtnkg9lt2fHv_ ";

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
        String vals[] = tmp.split("\n");

        Set<String> rsset = new HashSet<>();
        if (vals != null) {
            for (var v : vals) {
                String rsstr = v.trim();
                if (rsstr.length() > 0) {
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

        int suffixIdx = str.indexOf(" ", idx);
        if(suffixIdx == -1){
            suffixIdx =  str.indexOf("\n", idx);
            if(suffixIdx == -1){
                suffixIdx = str.length();
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
    public static String underlineCaseCamel(String str) {
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
        text = text.replaceAll("\r", "");
        text = text.replaceAll("--", "\n--");
        String val[] = text.split("\n");
        if (val.length > 0) {
            for (String v : val) {
                if (v.startsWith("--")) {
                    str.append(" " + v + "\n");
                } else {
                    str.append(" " + v);
                }
            }

            String dest = str.toString().trim();
            dest = dest.replaceAll("\t", " ");
            int sz = dest.length();
            while (true) {
                dest = dest.replaceAll("  ", " ");
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
        String val[] = text.split("\n");
        if (val.length > 0) {
            for (String line : val) {
                if (line.trim().length() > 0 ){
                    str.append(line + "\n");
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
        StringBuilder strb = new StringBuilder("");
        for (int i = 0; i < len; i++) {
            strb.append(" ");
        }
        return strb.toString();
    }

    // 去除注释
    @Deprecated
    public static String trimComment(String sql, String symbol) {
        if (!sql.contains(symbol)) return sql;
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
        if ("".equals(nstr)) {
            nstr = sql;
        }
        return nstr.trim();
    }

    /**
     * 去除所有注释
     * @param textVal
     * @return
     */
    public static String trimAllComment(String textVal) {
        // 1. 先把文本中的字符串替换调
        matherString msVal = getStringMatcher(textVal);
        // 替换后的文本
        String textNew = msVal.newString();

        String COMMENT_PATTERN = "//[^\n]*" + "|" + "--[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
        Pattern pattern = Pattern.compile(COMMENT_PATTERN); //去掉空格符合换行符
        Matcher matcher = pattern.matcher(textNew);
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

        String COMMENT_PATTERN = "//[^\n]*" + "|" + "--[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
        Pattern pattern = Pattern.compile(COMMENT_PATTERN); //去掉空格符合换行符
        Matcher matcher = pattern.matcher(textNew);

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

        String COMMENT_PATTERN = "\"([^\"\\\\]|\\\\.)*\"|'([^'\\\\]|\\\\.)*'";
        Pattern pattern = Pattern.compile(COMMENT_PATTERN); //去掉空格符合换行符
        Matcher matcher = pattern.matcher(textNew);

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
    /*
     * 根据";" 分割字符串, 找到要执行的sql, 并排除sql字符串中含有;的情况
     * 1. 先在原始文本中找到sql的字符串, 替换为空白字符串,得到一个新文本
     * 2. 在新文本中根据 ; 分割字符串, 得到每个分割出来的子串在文本中的区间
     * 3. 根据区间, 在原始文本中 提炼出sql语句
     */
    public static List<String> findSQLFromTxt(String textVal) {
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
        String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"|'([^'\\\\]|\\\\.)*'";
        String patternString = "(?<STRING>" + STRING_PATTERN + ")";
        Pattern PATTERN = Pattern.compile(patternString);
        Matcher matcher = PATTERN.matcher(text);
        String txtTmp = "";
        int lastKwEnd = 0;
        // 把匹配到的sql的字符串替换为对应长度的空白字符串, 得到一个和原始文本一样长度的新字符串
        while (matcher.find()) {
//			 String styleClass = matcher.group("STRING") != null ? "string" : null;
            int start = matcher.start();
            int end = matcher.end();
            int len = end - start;
            String space = StrUtils.createBlankString(len);
            String tmp = text.substring(start, end);
//			 logger.info("len = "+len+" ; tmp = " + tmp);
            txtTmp += text.substring(lastKwEnd, start) + space;
            lastKwEnd = end;
        }
        if (lastKwEnd > 0) {
            String txtEnd = text.substring(lastKwEnd, text.length());
            txtTmp += txtEnd;
        } else {
            txtTmp = text;
        }
//		logger.info("txtTmp = " + txtTmp);

        // TODO 在新字符上面, 提取字sql语句的区间
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
        if (!sql.contains(symbol))
            return sql;

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
        if ("".equals(nstr)) {
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
     * @param valStr
     * @return
     */
    public static matherString getStringMatcher(String valStr) {
        String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"|'([^'\\\\]|\\\\.)*'|`([^`\\\\]|\\\\.)*`";
        Pattern patn = Pattern.compile(STRING_PATTERN);
        Matcher matr = patn.matcher(valStr);
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
     * 恢复字符串中的字符串
     * @param msval
     * @param strVal
     * @return
     */
    public static String recoverStringMatcher(matherString msval, String strVal) {
        List<String> ls = msval.replaceStr();
        for(String str : ls) {
            strVal = strVal.replaceFirst(PLACEHOLDERS, str);
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
        String rs = "";
        String[] temp = str.split(regex);
        if (temp != null && temp.length > 0) {
            int len = temp.length;
            for (int i = 0; i < len; i++) {
                String s = temp[i];
                if (s.length() > 0 && s.trim().length() > 0) {
                    rs += s + delimiter;
                }
            }
        }
        if (rs.length() > 0)
            rs = rs.substring(0, rs.length() - 1);
        return rs;
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
        String str = String.format("  %-" + size + "s", name);
        return str;
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
                String arrStr[] = fileTyleStr.split("\\*");
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

    public static String dbFieldStyleToJavaFieldStyle(String str) {
        str = underlineCaseCamel(str);
        return str;
    }
}
