package com.ihewro.focus.util;

import com.blankj.ALog;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/16
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class StringUtil {
    public static String trim(String str){
        if (str == null){
            return  "";
        }else {
            return str.trim();
        }
    }

    public static String getUrlPrefix(String url){
        if (url == null){
            return  "";
        }
        int pos= 0;
        if (url.startsWith("http://")){
            pos = url.indexOf("/",7);//http://<8>fdsfsdf/
        }else if (url.startsWith("https://")){
            pos = url.indexOf("/",8);//https://<9>fdsfsdf/
        }else {
            ALog.d("地址有误");
            return url;
        }

        if (pos == -1){//末尾没有斜杆
            return  url;
        }else {
            return url.substring(0,pos);//最后没有斜杆
        }
    }

}
