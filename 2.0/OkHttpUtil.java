package com.szrlh.wfcat.utils;

import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ejl_001 on 2016/7/12.
 */
public class OkHttpUtil {
    public static final int CACHE_FORCE_NET = 111;
    public static final int CACHE_FORCE_CACHE = 112;
    public static final int CACHE_NORMAL = 113;
    private volatile static OkHttpClient okHttpClient = null;
    private static Call call;
    private static final String CACHE_DIE = "/mnt/sdcard/wfcat";
    //           120.27.149.167                      192.168.0.122
    private final static String URL_ = "http://120.27.149.167:8888";

    private final static String BaseUrl = URL_ + "/framexc/main/entrance.action?project=flm_jk&method=";

    private final static String Head_Base = URL_ + "/framexc/";

    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .removeHeader("network")
                    .header("Cache-Control", "max-age=60")
                    .build();
        }
    };

    private static OkHttpClient getClient() {
        if (okHttpClient == null) {
            synchronized (OkHttpUtil.class) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient();
                    okHttpClient.newBuilder().readTimeout(10, TimeUnit.SECONDS);
                    okHttpClient.newBuilder().connectTimeout(10, TimeUnit.SECONDS);
                    Cache cache = new Cache(new File(CACHE_DIE, "cache"), 10 * 1024 * 1024);
                    okHttpClient.newBuilder().cache(cache);
                    okHttpClient.newBuilder().addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR);
                }
            }
        }
        return okHttpClient;
    }

    public static <T> void get(String url, Map<String, String> map, OkCallBack<T> callback, int... cacheType) {
        if (map != null && !map.isEmpty()) {
            url += "&parameter=" + new Gson().toJson(map);
        }
        callback.onStart();
        Request request;
        if (cacheType != null && cacheType.length != 0) {
            if (cacheType[0] == CACHE_FORCE_NET) {
                request = new Request.Builder().cacheControl(CacheControl.FORCE_NETWORK).url(BaseUrl + url).build();
            } else if (cacheType[0] == CACHE_FORCE_CACHE) {
                request = new Request.Builder().cacheControl(CacheControl.FORCE_CACHE).url(BaseUrl + url).build();
            } else {
                request = new Request.Builder().url(BaseUrl + url).build();
            }
        } else {
            request = new Request.Builder().url(BaseUrl + url).build();
        }
        Log.d("ok", BaseUrl + url);
        call = getClient().newCall(request);
        call.enqueue(callback);
    }

    public static <T> void post(String url, RequestBody body, OkCallBack<T> callback) {
        callback.onStart();
        call = getClient().newCall(new Request.Builder().url(Head_Base + url).post(body).build());
        call.enqueue(callback);
    }

    public static void cancelCall() {
        if (call != null && !call.isExecuted()) {
            call.cancel();
        }
    }
}
