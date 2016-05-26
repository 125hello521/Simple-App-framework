package com.ejlchina.ejl.utils;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.ejlchina.ejl.base.EjlApplication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * linshao    lhl_012@163.com
 * 2016年5月26日18:03:34
 * 网络请求主体  支持get，post（key-file,string-string）
 */
public class OkHttpUtils {
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");
    private volatile static OkHttpClient okHttpClient = null;
    private static Call call;
    private static Cache cache;

    private static OkHttpClient getClient() {
        if (okHttpClient == null) {
            synchronized (OkHttpUtils.class) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient();
                    okHttpClient.newBuilder().readTimeout(10, TimeUnit.SECONDS);
                    okHttpClient.newBuilder().connectTimeout(10, TimeUnit.SECONDS);
                    cache = new Cache(new File(Environment.getExternalStorageDirectory() + "/ejlchina/cache.tmp"), Long.valueOf(5 * 1024 * 1024));
                }
            }
        }
        return okHttpClient;
    }

    public static void get(String url, OkHttpParams params, final OkHttpHandler handler) {
        final Handler uiHandler = new Handler(Looper.getMainLooper());
        if (params != null && params.haveData()) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                url += url.contains("?") ? "&" : "?";
                url += entry.getKey() + "=" + entry.getValue();
            }
        }
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                handler.start();
            }
        });
        call = getClient().newCall(new Request.Builder().url(url).build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        handler.failure();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) {
                try {
                    final String result = response.body().string();
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            handler.success(result);
                            if (EjlApplication.isDebug) {
                                Log.d("vvv", result);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            handler.failure();
                        }
                    });
                }
            }
        });
    }

    public static void getCache(String url, OkHttpParams params, final OkHttpHandler handler) {
        final Handler uiHandler = new Handler(Looper.getMainLooper());
        if (params != null && params.haveData()) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                url += url.contains("?") ? "&" : "?";
                url += entry.getKey() + "=" + entry.getValue();
            }
        }
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                handler.start();
            }
        });
        call = getClient().newBuilder().cache(cache).build().newCall(new Request.Builder().url(url).build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        handler.failure();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) {
                try {
                    final String result = response.body().string();
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            handler.success(result);
                            if (EjlApplication.isDebug) {
                                Log.d("vvv", result);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            handler.failure();
                        }
                    });
                }
            }
        });
    }

    public static void post(String url, OkHttpParams params, final OkHttpHandler handler) {
        final Handler uiHandler = new Handler(Looper.getMainLooper());
        if (params != null && params.haveData()) {
            RequestBody body;
            if (!params.urlParams.isEmpty() && params.streamParams.isEmpty() && params.fileParams.isEmpty() && params.fileArrayParams.isEmpty()) {
                FormBody.Builder builder = new FormBody.Builder();
                for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                    builder.add(entry.getKey(), entry.getValue());
                }
                body = builder.build();
            } else {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                if (!params.urlParams.isEmpty()) {
                    for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                        builder.addFormDataPart(entry.getKey(), entry.getValue());
                    }
                }
                if (!params.fileParams.isEmpty()) {
                    for (Map.Entry<String, OkHttpParams.FileWrapper> entry : params.fileParams.entrySet()) {
                        OkHttpParams.FileWrapper fileWrapper = entry.getValue();
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
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    handler.start();
                }
            });
            call = getClient().newCall(new Request.Builder().url(url).post(body).build());
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            handler.failure();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String result = response.body().string();
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            handler.success(result);
                            if (EjlApplication.isDebug) {
                                Log.d("vvv", result);
                            }
                        }
                    });
                }
            });
        } else {
            get(url, null, handler);
        }
    }

    public static void downLoad(String url, OkHttpParams params, final OkHttpHandler handler) {
        final Handler uiHandler = new Handler(Looper.getMainLooper());
        if (params != null && params.haveData()) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                url += url.contains("?") ? "&" : "?";
                url += entry.getKey() + "=" + entry.getValue();
            }
        }
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                handler.start();
            }
        });
        getClient().newBuilder().addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new OkHttpProgressResponseBody(originalResponse.body(), new OkHttpProgressListener() {
                            @Override
                            public void update(final long bytesRead, final long contentLength, final boolean done) {
                                uiHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        handler.progress(bytesRead, contentLength, done);
                                        Log.d("vvv", bytesRead + "---" + contentLength + "---" + done);
                                    }
                                });
                            }
                        })).build();
            }
        }).build().newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        handler.failure();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                handler.success(inputStream);
            }
        });
    }

    public static void cancelCall() {
        if (call != null && !call.isExecuted()) {
            call.cancel();
        }
    }
}
