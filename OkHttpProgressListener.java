package com.ejlchina.ejl.utils;

public interface OkHttpProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
