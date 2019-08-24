package com.ihewro.focus.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.blankj.ALog;
import com.ihewro.focus.helper.EncodingInterceptor;
import com.ihewro.focus.util.HttpsUtil;
import com.ihewro.focus.util.Tls12SocketFactory;
import com.ihewro.focus.util.UIUtil;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class HttpUtil {

    /**
     * 自定义okhttp拦截器，以便能够打印请求地址、请求头等请求信息
     * @param readTimeout 单位s
     * @param writeTimeout 单位s
     * @param connectTimeout 单位s
     * @return OkHttpClient
     */
    public static OkHttpClient getOkHttpClient(String type, int readTimeout, int writeTimeout, int connectTimeout){
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                //打印retrofit日志
                ALog.dTag("RetrofitLog","retrofitBack = "+message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);



        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(100);
        dispatcher.setMaxRequests(100);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .dispatcher(dispatcher)
                .readTimeout(readTimeout, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });//设置连接超时时间

        if (type.equals("String")){
//            ALog.d("添加了编码了");
//            builder.addInterceptor(new EncodingInterceptor("ISO-8859-1"));//全部转换成这个编码
        }

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            try {
                sslContext.init(null, null, null);
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        SSLSocketFactory socketFactory = new Tls12SocketFactory(sslContext.getSocketFactory());
        builder.sslSocketFactory(socketFactory,new HttpsUtil.UnSafeTrustManager());

        OkHttpClient client = builder.build();
      /*  client.dispatcher().setMaxRequests(100);//最大并发数
        client.dispatcher().setMaxRequestsPerHost(100);//单域名的并发数*/

        return client;
    }


    /**
     * 返回retrofit的实体对象
     * @param readTimeout
     * @param writeTimeout
     * @param connectTimeout
     * @return
     */
    public static Retrofit getRetrofit(String type, String requestUrl, int readTimeout, int writeTimeout, int connectTimeout){
        Converter.Factory factory = null;
        if (type.equals("String")){
            factory = ScalarsConverterFactory.create();
        }else if (type.equals("bean")){
            factory = JacksonConverterFactory.create();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(requestUrl)
                .client(HttpUtil.getOkHttpClient(type,readTimeout,writeTimeout,connectTimeout))
                .addConverterFactory(factory)//retrofit已经把Json解析封装在内部了 你需要传入你想要的解析工具就行了
                .build();
        return retrofit;
    }

    /**
     * make true current connect service is wifi
     */
    public static boolean isWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) UIUtil.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

}
