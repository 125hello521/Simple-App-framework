package com.szrlh.wfcat.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.szrlh.wfcat.bean.NetResultBean;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 网络请求基类
 * Created by ejl_001 on 2016/8/24.
 */
public abstract class OkCallBack<T> implements Callback {
    @SuppressWarnings("unused")
    private static final int IO_ = 1111;//IO错误
    private static final int JSON_ = 1112;//JSON数据错误
    private static final int UNKNOW_ = 1113;//未知
    private static final int NET_ = 1114;//404
    private static final int ADD_ = 1115;//服务器不见了
    private static final int OPERA_ = 1116;//请求失败

    private Type ttt;

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
        Log.d("ok", json);
        try {
            ttt = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        } catch (Exception e) {
            ttt = new TypeToken<JsonElement>() {
            }.getType();
        }
        Type type = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                Type[] type = new Type[1];
                type[0] = ttt;
                return type;
            }

            @Override
            public Type getRawType() {
                return NetResultBean.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
        try {
            final NetResultBean<T> netResult = new Gson().fromJson(json, type);
            if (netResult.getCode() == 0) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onSuccess((T) netResult.getBackStr(), netResult.getMessage());
                    }
                });
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onError(netResult.getCode(), netResult.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onError(JSON_, "解析出错");
                }
            });
        }
    }
}
