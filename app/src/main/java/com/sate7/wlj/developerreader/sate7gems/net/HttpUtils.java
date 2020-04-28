package com.sate7.wlj.developerreader.sate7gems.net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jingbin on 2017/2/14.
 * updated by jingbin on 2019/2/25.
 * 网络请求工具类
 * 使用时请在"CloudReaderApplication"下初始化。
 */

public class HttpUtils {

    private static HttpUtils instance;
    private Context context;
    /**
     * 分页数据，每页的数量
     */
    public static int per_page = 10;
    public static int per_page_more = 20;

    public static HttpUtils getInstance() {
        if (instance == null) {
            synchronized (HttpUtils.class) {
                if (instance == null) {
                    instance = new HttpUtils();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        HttpHead.init(context);
    }






    public OkHttpClient getUnsafeOkHttpClient(Context context) {
        try {
            // Install the all-trusting trust manager TLS
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            //cache url
            File httpCacheDirectory = new File(context.getCacheDir(), "responses");
            // 50 MiB
            int cacheSize = 50 * 1024 * 1024;
            Cache cache = new Cache(httpCacheDirectory, cacheSize);
            // Create an ssl socket factory with our all-trusting manager
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
            okBuilder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            okBuilder.readTimeout(30, TimeUnit.SECONDS);
            okBuilder.connectTimeout(30, TimeUnit.SECONDS);
            okBuilder.writeTimeout(30, TimeUnit.SECONDS);
            okBuilder.addInterceptor(new HttpHeadInterceptor());
            // 持久化cookie
            okBuilder.addInterceptor(new ReceivedCookiesInterceptor(context));
            okBuilder.addInterceptor(new AddCookiesInterceptor(context));
            // 添加缓存，无网访问时会拿缓存,只会缓存get请求
//            okBuilder.addInterceptor(new AddCacheInterceptor(context));
            okBuilder.cache(cache);
            /*okBuilder.addInterceptor(new HttpLoggingInterceptor()
                    .setLevel(BuildConfig.DEBUG ?
                            HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE));*/
            okBuilder.hostnameVerifier(new HostnameVerifier() {
                @SuppressLint("BadHostnameVerifier")
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return okBuilder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }



    private class HttpHeadInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request.Builder builder = request.newBuilder();
            builder.addHeader("Accept", "application/json;versions=1");
            if (CheckNetwork.isNetworkConnected(context)) {
                int maxAge = 60;
                builder.addHeader("Cache-Control", "public, max-age=" + maxAge);
            } else {
                int maxStale = 60 * 60 * 24 * 28;
                builder.addHeader("Cache-Control", "public, only-if-cached, max-stale=" + maxStale);
            }
            return chain.proceed(builder.build());
        }
    }

    private class AddCacheInterceptor implements Interceptor {
        private Context context;

        AddCacheInterceptor(Context context) {
            super();
            this.context = context;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {

            CacheControl.Builder cacheBuilder = new CacheControl.Builder();
            cacheBuilder.maxAge(0, TimeUnit.SECONDS);
            cacheBuilder.maxStale(365, TimeUnit.DAYS);
            CacheControl cacheControl = cacheBuilder.build();
            Request request = chain.request();
            if (!CheckNetwork.isNetworkConnected(context)) {
                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
            }
            Response originalResponse = chain.proceed(request);
            if (CheckNetwork.isNetworkConnected(context)) {
                // read from cache
                int maxAge = 0;
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public ,max-age=" + maxAge)
                        .build();
            } else {
                // tolerate 4-weeks stale
                int maxStale = 60 * 60 * 24 * 28;
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    }

    private class ReceivedCookiesInterceptor implements Interceptor {
        private Context context;

        ReceivedCookiesInterceptor(Context context) {
            super();
            this.context = context;

        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            return null;
        }
    }

    private class AddCookiesInterceptor implements Interceptor {
        private Context context;

        AddCookiesInterceptor(Context context) {
            super();
            this.context = context;

        }

        @Override
        public Response intercept(Chain chain) throws IOException {

            final Request.Builder builder = chain.request().newBuilder();
            SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            String cookie = sharedPreferences.getString("cookie", "");
            builder.addHeader("Cookie", cookie);
            return chain.proceed(builder.build());
        }
    }

    final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }};

}
