package com.ihewro.focus.helper;

import com.blankj.ALog;
import com.ihewro.focus.util.StringUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Logger;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2019/05/23
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class EncodingInterceptor implements Interceptor {


    /**
     * 自定义编码
     */
    private String encoding;

    public EncodingInterceptor(String encoding) {
        this.encoding = encoding;
    }

    @Override public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        long start = System.nanoTime();
//        ALog.d("Sending request: {}, headers: {}, request: {}", request.url(), request.headers(), request.body().toString());
        Response response = chain.proceed(request);
        long end = System.nanoTime();
//        ALog.d(String.format("Received response for %s in %.1fms%n %s", response.request().url(), (end - start) / 1e6d, response.headers().toString()));
        settingClientCustomEncoding(response);
        return response;
    }

    /**
     * setting client custom encoding when server not return encoding
     * @param response
     * @throws IOException
     */
    private void settingClientCustomEncoding(Response response) throws IOException {
        setBodyContentType(response);
    }

    /**
     * set body contentType
     * @param response
     * @throws IOException
     */
    private void setBodyContentType(Response response) throws IOException {
        ResponseBody body = response.body();
        // setting body contentTypeString using reflect
        Class<? extends ResponseBody> aClass = body.getClass();
        try {
            Field field = aClass.getDeclaredField("contentTypeString");
            field.setAccessible(true);
            String contentTypeString = String.valueOf(field.get(body));
//            ALog.d(contentTypeString);
//            field.set(body, (!StringUtil.trim(contentTypeString).equals("") ? contentTypeString + "; ":"" ) + "charset=" + encoding);
            field.set(body, "text/xml;charset=" + encoding);

        } catch (NoSuchFieldException e) {
            throw new IOException("use reflect to setting header occurred an error", e);
        } catch (IllegalAccessException e) {
            throw new IOException("use reflect to setting header occurred an error", e);
        }
    }
}