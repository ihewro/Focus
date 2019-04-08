package com.ihewro.focus.util;

import com.blankj.ALog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/04/24
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class DateUtil {

    public static String getNowDateStr() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }



    public static String getTimeStringByInt(long time){
        SimpleDateFormat format =  new SimpleDateFormat( "yyyy-MM-dd" );
        return format.format(time);
    }


    public static String getTTimeStringByInt(long time){
        SimpleDateFormat format =  new SimpleDateFormat( "HH:mm:ss" );
        return format.format(time);
    }
    public static boolean isTimeout(String now, String last){

        long to = date2TimeStamp(now);
        long from = date2TimeStamp(last);
        int days = (int) ((to - from)/(1000* 60 * 60 * 24));
        ALog.d("days",days + "diff:" + (to - from));
        return days >= 1;
    }

    /**
     * 日期格式字符串转换成时间戳
     * @param date_str 字符串日期
     * @return
     */
    public static long date2TimeStamp(String date_str){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(date_str).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @see <a href="http://www.ietf.org/rfc/rfc0822.txt">RFC 822</a>
     */
    private static final SimpleDateFormat RFC822 = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss Z", java.util.Locale.ENGLISH);

    /**
     * Parses string as an RFC 822 date/time.
     */
    public static Date parseRfc822(String date) {
        if (date == null){
            date = "Mon, 08 Apr 2019 04:27:58 GMT";
        }
        try {
            return RFC822.parse(date);
        } catch (ParseException e) {
            return new Date();
        }

    }

}
