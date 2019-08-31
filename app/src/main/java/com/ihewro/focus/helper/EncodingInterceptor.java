package com.ihewro.focus.helper;

import com.blankj.ALog;
import com.ihewro.focus.util.StringUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.SocketTimeoutException;
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
        Response response = chain.proceed(request);

        settingClientCustomEncoding(response);
        return response;
    }

    /**
     * setting client custom encoding when server not return encoding
     * @param response
     * @throws IOException
     */
    private void settingClientCustomEncoding(Response response) throws IOException {
//        setHeaderContentType(response);
        setBodyContentType(response);

    }

    /**
     * set contentType in headers
     * @param response
     * @throws IOException
     */
    private void setHeaderContentType(Response response) throws IOException {
        String contentType = response.header("Content-Type");

        // build new headers
        Headers headers = response.headers();
        Headers.Builder builder = headers.newBuilder();
        builder.removeAll("Content-Type");
        builder.add("Content-Type", "charset=" + encoding);
        headers = builder.build();
        // setting headers using reflect
        Class  _response = Response.class;
        try {
            Field field = _response.getDeclaredField("headers");
            field.setAccessible(true);
            field.set(response, headers);
        } catch (NoSuchFieldException e) {
            throw new IOException("use reflect to setting header occurred an error", e);
        } catch (IllegalAccessException e) {
            throw new IOException("use reflect to setting header occurred an error", e);
        }
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
            field.set(body, "application/rss+xml;charset=" + encoding);
            ALog.d("修改前"+contentTypeString + "修改后" + "application/rss+xml;charset=" + encoding);

        } catch (NoSuchFieldException e) {
            throw new IOException("use reflect to setting header occurred an error", e);
        } catch (IllegalAccessException e) {
            throw new IOException("use reflect to setting header occurred an error", e);
        }
    }

}
