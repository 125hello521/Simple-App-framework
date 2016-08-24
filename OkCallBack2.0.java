package com.szrlh.wfcat.utils;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.szrlh.wfcat.bean.NetResultBean;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by ejl_001 on 2016/8/24.
 */
public abstract class OkCallBack<T> implements Callback {
    private static final int IO_ = 1111;
    private static final int JSON_ = 1112;
    private static final int UNKNOW_ = 1113;
    private static final int NET_ = 1114;
    private static final int ADD_ = 1115;
    private static final int OPERA_ = 1116;

    public void onStart() {

    }

    public abstract void onSuccess(T t, String msg);

    public abstract void onError(int errCode, String msg);

    @Override
    public void onFailure(Call call, IOException e) {
        final int errCode;
        final String errMsg;
        if (e == null || e.getMessage() == null) {
            errCode = UNKNOW_;
            errMsg = "未知错误";
        } else {
            if (e.getMessage().contains("No address associated with hostname")) {
                errCode = NET_;
                errMsg = "网络异常";
            } else if (e.getMessage().contains("Failed to connect to")) {
                errCode = ADD_;
                errMsg = "服务器未响应";
            } else {
                errCode = UNKNOW_;
                errMsg = "未知错误";
            }
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                onError(errCode, errMsg);
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        String json = response.body().string();
        final NetResultBean netResult = new Gson().fromJson(json, NetResultBean.class);
        if (netResult.getCode() == 0) {
            try {
                Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
                final T t = new Gson().fromJson(netResult.getBackStr(), type);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onSuccess(t, netResult.getMessage());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onError(OPERA_, netResult.getMessage());
                    }
                });
            }
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onError(JSON_, "数据异常");
                }
            });
        }
    }
}
