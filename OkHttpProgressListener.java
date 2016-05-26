package com.net.volley;

public interface OkHttpProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
