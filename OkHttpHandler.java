package com.net.volley;

import java.io.InputStream;

public abstract class OkHttpHandler {
    //开始
    void start() {
    }

    //下载进度
    void progress(long downloadSize, long totalSize, boolean is) {

    }

    //成功
    abstract void success(String result);

    //下载文件成功
    void success(InputStream inputStream) {

    }

    //失败
    abstract void failure();
}
