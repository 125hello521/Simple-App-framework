package com.szrlh.wfcat.bean;

import com.google.gson.JsonElement;

/**
 * 网络请求返回结果Model
 * Created by 林少 on 2015/11/4.
 */
public class NetResultBean<T> {

    /**
     * code : 0
     * message :
     * backStr : {"name":"安卓版","url":"http://www.baidu.com","discript":"安卓版1.0.0新版上线","version":"1.0.0"}
     */

    private int code;
    private String message;
    private T backStr;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getBackStr() {
        return backStr;
    }

    public void setBackStr(T backStr) {
        this.backStr = backStr;
    }
}
