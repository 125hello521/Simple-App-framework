package com.net.volley;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtils {
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
    private volatile static OkHttpClient okHttpClient = null;
    private static Call call;

    private static OkHttpClient getClient() {
        if (okHttpClient == null) {
            synchronized (OkHttpUtils.class) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient();
                    okHttpClient.newBuilder().readTimeout(10, TimeUnit.SECONDS);
                    okHttpClient.newBuilder().connectTimeout(10, TimeUnit.SECONDS);
                }
            }
        }
        return okHttpClient;
    }

    public static void get(String url, RequestParams params) {
        if (params != null) {
            // Construct the query string and trim it, in case it
            // includes any excessive white spaces.
            ConcurrentHashMap<String, String> map = params.urlParams;
            // Only add the query string if it isn't empty and it
            // isn't equal to '?'.
            if (map != null && !map.isEmpty()) {
                Iterator iter = map.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
                    url += url.contains("?") ? "&" : "?";
                    url += entry.getKey() + "=" + entry.getValue();
                }
            }
        }
        call = getClient().newCall(new Request.Builder().url(url).build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("vvv", response.body().string());
            }
        });
    }

    public static void cancelCall() {
        if (!call.isExecuted()) {
            call.cancel();
        }
    }

    public static void post(String url, RequestParams params) {
        if (params.haveData()) {
            RequestBody body = null;
            if (!params.urlParams.isEmpty() && params.streamParams.isEmpty() && params.fileParams.isEmpty() && params.fileArrayParams.isEmpty()) {
                FormBody.Builder builder = new FormBody.Builder();
                Iterator iter = params.urlParams.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
                    builder.add(entry.getKey(), entry.getValue());
                }
                body = builder.build();
            } else {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                if (!params.urlParams.isEmpty()) {
                    Iterator iter = params.urlParams.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
                        builder.addFormDataPart(entry.getKey(), entry.getValue());
                    }
                }
                if (!params.fileParams.isEmpty()) {
                    Iterator iter = params.fileParams.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<String, RequestParams.FileWrapper> entry = (Map.Entry<String, RequestParams.FileWrapper>) iter.next();
                        RequestParams.FileWrapper fileWrapper = entry.getValue();
                        File file = fileWrapper.file;
                        if (file.getName().endsWith(".jpg")) {
                            builder.addFormDataPart(entry.getKey(), file.getName(), RequestBody.create(MEDIA_TYPE_JPG, entry.getValue().file));
                        }
                        if (file.getName().endsWith(".png")) {
                            builder.addFormDataPart(entry.getKey(), file.getName(), RequestBody.create(MEDIA_TYPE_PNG, entry.getValue().file));
                        }

                    }
                }
                body = builder.build();
            }
            call = getClient().newCall(new Request.Builder().url(url).post(body).build());
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d("vvv", response.body().string());
                }
            });
        }
    }
}
