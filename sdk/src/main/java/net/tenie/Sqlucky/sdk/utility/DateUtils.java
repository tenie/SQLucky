package net.tenie.Sqlucky.sdk.utility;

import net.tenie.Sqlucky.sdk.config.ConfigVal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Locale;

/**
 * 时间转换
 *
 * @author tenie
 */
public class DateUtils {
    public static SimpleDateFormat usSdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date asDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime asLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static String localDateTimeToStr(LocalDateTime localDateTime) {
        String dateStr = localDateTime.format(dateTimeFormatter);
        return dateStr;
    }
    public static String localDateToStr(LocalDate localDateTime) {
        return localDateTime.format(dateFormatter);
    }

    public static LocalDateTime strToLocalDateTime(String dateStr) {
        LocalDateTime date2 = LocalDateTime.parse(dateStr, dateTimeFormatter);
        return date2;
    }

    /**
     * 字符串时间, 减天数
     * @param dateStr
     * @return LocalDateTime
     */
    public static LocalDateTime strToLocalDateTimeMinusDay(String dateStr, long minusDays) {
        LocalDateTime ldt = LocalDateTime.parse(dateStr, dateTimeFormatter);
        ldt = ldt.minusDays(minusDays);
        return ldt;
    }




    public static String dateToStrL(Date d) {
        return StrUtils.sdf_DateTime.format(d);
    }
    public static String timeToStr(Date d) {
        return StrUtils.sdf_Time.format(d);
    }

    public static String dateToStr(Date d, String formate) {
        SimpleDateFormat sdf = new SimpleDateFormat(formate);
        return sdf.format(d);
    }

    // us格式转换 Tue Jul 23 23:06:51 CST 2024
    public static String usDateStrToStrL(String usDateStr) {
        Date d = null;
        try {
            d = usSdf.parse(usDateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (d != null){
            return StrUtils.sdf_DateTime.format(d);
        }
        return "";
    }

    // 返回一个时间字符串, 可用于文件名的一部分
    public static String currentDateTimeToFileNameStr() {
        return dateToStr(new Date(), StrUtils.dateTimeForFileName);
    }

    public static Date StrToDate(String str, String formate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(formate);
            return new Date(sdf.parse(str).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    // 日期类型或时间类型转换为字符串
    public static String DateOrDateTimeToString(int type, Date dv) {
        String val = "";
        if (CommonUtils.isDate(type)) {
            val = dateToStr(dv, ConfigVal.dateFormateS);
        } else if (CommonUtils.isDateTime(type)) {
            val = dateToStr(dv, ConfigVal.dateFormateL);
        }

        return val;
    }

    /*
     * 数据库时间类型字段转换为字符串
     */
    public static String DbDateTimeToString(Object obj, int sqlFiledtype) {

        String val = null;
        if (obj instanceof LocalDateTime ldt) {
            Date dv = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
            val = DateUtils.DateOrDateTimeToString(sqlFiledtype, dv);
        } else if (obj instanceof Date dv) {
            val = DateUtils.DateOrDateTimeToString(sqlFiledtype, dv);
        } else if (obj instanceof String stringVal) {
            val = stringVal;
        } else if (obj instanceof Long longVal) {
            Date date = new Date(longVal);
            val = DateUtils.dateToStr(date, ConfigVal.dateFormateL);
        }

        return val;
    }
    /**
     * 字符串转 Date
     *
     * @param dateStr
     * @return
     */
    public static Date strToDate(String dateStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(dateStr);
            return date;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * date 转 字符串
     * @param date
     * @return
     */
    public static String dateToStr(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = simpleDateFormat.format(date);

        return dateStr;
    }


//    /**
//     * 字符串 转 LocalDateTime
//     *
//     * @param dateStr
//     * @return
//     */
//    public static LocalDateTime strToLocalDateTime(String dateStr) {
//        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime ldt = LocalDateTime.parse(dateStr, df);
//        return ldt;
//    }
//
//    /**
//     * LocalDateTime 转 字符串
//     *
//     * @param localDateTime
//     * @return
//     */
//    public static String localDateTimeToStr(LocalDateTime localDateTime) {
//        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String localTime = df.format(localDateTime);
//        return localTime;
//    }

    /**
     * Date 转 LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime;
    }

    /**
     * LocalDateTime 转 Date
     *
     * @param localDateTime
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        Date date = Date.from(instant);
        return date;
    }

    /**
     * LocalDate 转 Date
     *
     * @return
     */
    public static Date localDateToDate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * localDateTime 转 LocalDate
     *
     * @param localDateTime
     * @return
     */
    public static LocalDate localDateTimeToLocalDate(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate();
    }

    /**
     * 当前时间为 昨天的 LocalDateTime
     *
     * @return
     */
    public static LocalDateTime yesterday() {
        LocalDateTime now = LocalDateTime.now();
        return now.plusDays(-1);
    }

    /**
     * 当前时间为 明天的 LocalDateTime
     *
     * @return
     */
    public static LocalDateTime tomorrow() {
        LocalDateTime now = LocalDateTime.now();
        return now.plusDays(1);
    }


    /**
     * 获取北京时区
     */
    public static ZoneId beijingZone() {
        //北京时区
        ZoneId bjZone = ZoneId.of("GMT+08:00");
        return bjZone;
    }

    /**
     * 系统默认时区
     *
     * @return
     */
    public static ZoneId systemDefaultZone() {
        return ZoneId.systemDefault();
    }

    /**
     * 时间比较, 在之前
     *
     * @param dt1
     * @param dt2
     * @return
     */
    public static boolean isBefore(LocalDateTime dt1, LocalDateTime dt2) {
        return dt1.isBefore(dt2);
    }

    /**
     * 时间比较, 在之后
     *
     * @param dt1
     * @param dt2
     * @return
     */
    public static boolean isAfter(LocalDateTime dt1, LocalDateTime dt2) {
        return dt1.isAfter(dt2);
    }


    /**
     * 获取到毫秒级时间戳
     * @param localDateTime 具体时间
     * @return long 毫秒级时间戳
     */
    public static long toEpochMilli(LocalDateTime localDateTime){
        return localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * 毫秒级时间戳转 LocalDateTime
     * @param epochMilli 毫秒级时间戳
     * @return LocalDateTime
     */
    public static LocalDateTime ofEpochMilli(long epochMilli){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneOffset.of("+8"));
    }

    /**
     * 获取到秒级时间戳
     * @param localDateTime 具体时间
     * @return long 秒级时间戳
     */
    public static long toEpochSecond(LocalDateTime localDateTime){
        return localDateTime.toEpochSecond(ZoneOffset.of("+8"));
    }

    /**
     * 秒级时间戳转 LocalDateTime
     * @param epochSecond 秒级时间戳
     * @return LocalDateTime
     */
    public static LocalDateTime ofEpochSecond(long epochSecond){
        return LocalDateTime.ofEpochSecond(epochSecond, 0,ZoneOffset.of("+8"));
    }

    /**
     * 毫秒转 时间字符串 : "2020-02-02 22:22:22"
     * @param epochSecond
     * @return
     */
    public static String EpochMilliToDateString(long epochSecond){
        LocalDateTime ldt  =  ofEpochMilli(epochSecond);
        String str =localDateTimeToStr(ldt);
        return str;
    }
    /**
     * 毫秒转 时间字符串 : "2020-02-02 22:22:22"
     * @param epochSecond
     * @return
     */
    public static String EpochMilliToDateString(String epochSecond){
        String str = "";
        try{
           Long val =  Long.valueOf(epochSecond);
           str = EpochMilliToDateString(val);
        }catch (Exception e){
            e.printStackTrace();
        }
        return str;
    }

    public static LocalDateTime lastDayOfMonth(){
        LocalDateTime now = LocalDateTime.now();
        return now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
    }

    public static LocalDateTime lastDayOfMonth(LocalDateTime  ldt){
        return ldt.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
    }

    public static LocalDateTime firstDayOfMonth(LocalDateTime ldt) {
        return ldt.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
    }






    public static void main(String[] args) {
        LocalDate ld =  Instant.ofEpochMilli(new Date().getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        ld = ld.minusDays(6);
        System.out.println(ld);
        Instant instant = ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Date date =  Date.from(instant);
        System.out.println(date);
//        List<String> list = Arrays.asList("张三");
//        String join = StringUtils.join(list, ",");
//        System.out.println( join);

//        String text = //"==>  Preparing: SELECT * FROM ( SELECT ws.WS_NO_ID wsNoId, ws.WS_NO wsNo, sws.SC_WS_NO_ID scWsNoId, ws.WS_TYPE wsType, ws.WS_STATUS wsStatus, ws.SECOND_REPORT_STATUS secondReportStatus, wiu.SALES_STATUS salesStatus, WIU.WS_INFO_UPDATE_ID wsInfoUpdateId, CASE WHEN wsi.WS_DELI_COUNT IS NOT NULL THEN 10041001 ELSE 10041002 END isWsSale, wsi.WS_DELI_COUNT wsDeliCount, DATE_FORMAT(ws.SUBMIT_TIME,'%Y-%m-%d %H:%i:%s') submitTime, DATE_FORMAT(ws.AUDITING_DATE,'%Y-%m-%d %H:%i:%s') auditingDate, ws.WS_AUDITING_REMARK wsAuditingRemark, ws.POTENTIAL_CUSTOMER_NO potentialCustomerNo, ws.CUSTOMER_NAME customerName, ws.INDUSTRY_FIRST industryFirst, ws.INDUSTRY_SECOND industrySecond, ws.PUBLIC_PRIVATE_TAG publicPrivateTag, ws.WHOLESALE_TYPE_SUB wholesaleTypeSub, ws.WS_APP_TYPE wsAppType, ws.REBATE_TYPE rebateType, ws.CONTACTOR_NAME contactorName, ws.MOBILE mobile, ws.CONFIGURE_REMARK configureRemark, ws.DLR_PRINCIPAL_PHONE dlrPrincipalPhone, ws.IS_SECOND_REPORT isSecondReport, CASE WHEN wiu.SUBMIT_TIME IS NOT NULL THEN 10041001 ELSE 10041002 END isUpdateUpload, DATE_FORMAT(wsc.WS_SUPPLY_START_DATE,'%Y-%m-%d %H:%i:%s') wsSupplyStartDate, DATE_FORMAT(wsc.WS_SUPPLY_END_DATE,'%Y-%m-%d %H:%i:%s') wsSupplyEndDate FROM tt_po_cus_wholesale ws LEFT JOIN tt_po_second_cus_wholesale sws ON sws.WS_NO = ws.WS_NO AND sws.OWNER_CODE = ws.OWNER_CODE AND sws.IS_DELETED = 0 LEFT JOIN (SELECT wsc.WS_NO,wsc.OWNER_CODE,MAX(wsc.WS_SUPPLY_START_DATE) WS_SUPPLY_START_DATE,MAX(wsc.WS_SUPPLY_END_DATE) WS_SUPPLY_END_DATE FROM tt_ws_config_info wsc WHERE wsc.IS_DELETED = 0 AND wsc.OWNER_CODE = ? GROUP BY wsc.WS_NO,wsc.OWNER_CODE) wsc ON wsc.WS_NO=ws.WS_NO AND wsc.OWNER_CODE=ws.OWNER_CODE LEFT JOIN (SELECT COUNT(1) WS_DELI_COUNT,wsi.WS_NO,wsi.OWNER_CODE FROM tt_ws_sales_info wsi WHERE wsi.IS_DELETED = 0 AND wsi.VIN <> '' AND wsi.OWNER_CODE=? GROUP BY wsi.WS_NO,wsi.OWNER_CODE) wsi ON wsi.WS_NO = ws.WS_NO AND wsi.OWNER_CODE = ws.OWNER_CODE LEFT JOIN tm_ws_info_update wiu ON wiu.WS_NO = ws.WS_NO AND wiu.OWNER_CODE = ws.OWNER_CODE AND wiu.LAST_TAG = 10041001 AND wiu.VIN = '' AND wiu.IS_DELETED = 0 WHERE ws.OWNER_CODE = ? and ws.IS_DELETED = 0 AND ws.WS_STATUS in (?) ORDER BY ws.CREATED_AT DESC ) a LIMIT ?\n" +
//                "==> Parameters:";
//        String[] sqlArr = text.split("==> Parameters: ");
//        Date invoiceDate = new Date();
//        int paramVal = 2;
//        LocalDateTime invoiceLocalDateTime =  Instant.ofEpochMilli(invoiceDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
//        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
//        String dateStr = invoiceLocalDateTime.format(fmt);
//        System.out.println("上报材料截止日期为"+dateStr + " + " + paramVal + "月");


//        String begin = "2024-07-29 00:00:00";
//        String end = "2024-08-04 00:00:00";
//        String sql =" and ( ";
//        String strformat = "( a.EXPIRED_MONTH = '%s' and a.EXPIRED_DAY = '%s' )";
//        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime beginldt = LocalDateTime.parse(begin, fmt);
//        LocalDateTime endldt = LocalDateTime.parse(end, fmt);
//        long days = Duration.between(beginldt, endldt).toDays() ;
//        System.out.println("相差的天数: " + days + "天");
//        if(days == 6){
//            int mvTmp =  beginldt.getMonthValue();
//            int dvTmp =  beginldt.getDayOfMonth();
//            sql +=String.format(strformat, mvTmp, dvTmp);
//
//            for(int i = 1 ; i <6 ; i++) {
//                LocalDateTime tmpLdt = beginldt.plusDays(i);
//                mvTmp = tmpLdt.getMonthValue();
//                dvTmp = tmpLdt.getDayOfMonth();
//                sql += " or " + String.format(strformat, mvTmp, dvTmp);
//            }
//
//            mvTmp = endldt.getMonthValue();
//            dvTmp = endldt.getDayOfMonth();
//            sql += " or " + String.format(strformat, mvTmp, dvTmp) + " ) ";
//        }
//
//        System.out.println(sql);
    }
}
