package com.net.volley;

/**
 * Created by admin on 2016/5/25.
 */
public abstract class OkHttpHandler {
    void start() {
    }

    abstract void success(String result);

    abstract void failure();
}
