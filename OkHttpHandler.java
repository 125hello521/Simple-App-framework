package com.net.volley;

import java.io.InputStream;
/**
 * linshao    lhl_012@163.com
 * 2016年5月26日18:03:34
 * 异步处理   不用interface主要因为部分不需要
 */
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
