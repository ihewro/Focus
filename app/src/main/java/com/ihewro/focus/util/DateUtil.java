package com.ihewro.focus.util;

import android.annotation.SuppressLint;

import com.blankj.ALog;
import com.ihewro.focus.bean.Message;

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

    public static String getNowDateRFCStr() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
        return sdf.format(d);
    }


    public static long getNowDateRFCInt() {
        return System.currentTimeMillis();
    }

    public static String getTimeStringByInt(long time){
        SimpleDateFormat format =  new SimpleDateFormat( "yyyy-MM-dd" );
        return format.format(time);
    }

    public static String getRFCStringByInt(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy HH:mm:ss");
        return sdf.format(time);
    }



    public static String getTTimeStringByInt(long time){
        SimpleDateFormat format =  new SimpleDateFormat( "HH:mm" );
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
    static long date2TimeStamp(String date_str){
        //RFC3339
        String[] dateFormats = {"EEE, dd MMM yyyy HH:mm:ss Z","yyyy-MM-dd'T'HH:mm:ss.SSS'Z'","yyyy-MM-dd"};

        long timeStamp = 0L;

        for (String dateFormat : dateFormats) {
            Message message = isDateString(date_str, dateFormat);
            if (message.isFlag()) {
                timeStamp = Long.parseLong(message.getData());
//                ALog.d("时间戳为"+ timeStamp);
                break;
            }
        }
        if (timeStamp == 0L){
            ALog.d("时间格式为|"+date_str +"|未匹配上");
        }
        return timeStamp;
    }


    private static Message isDateString(String datevalue, String dateFormat) {
        if (datevalue == null){
            //先访问这个地方的，反而时间要更大！
            return new Message(true,new Date().getTime()+"");
        }

        datevalue = datevalue.trim();

        try {
            //java.util.Locale.ENGLISH 这个非常重要，否则解析失败
            @SuppressLint("SimpleDateFormat") SimpleDateFormat fmt = new SimpleDateFormat(dateFormat,java.util.Locale.ENGLISH);
            java.util.Date dd = fmt.parse(datevalue);
            return new Message(true,dd.getTime()+"");

        } catch (Exception e) {
            return new Message(false);
        }
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
