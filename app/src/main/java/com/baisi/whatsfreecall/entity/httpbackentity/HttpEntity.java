package com.baisi.whatsfreecall.entity.httpbackentity;

/**
 * Created by nick on 2018/2/6.
 */

public class HttpEntity<T> {

    private int err_code;
    private String err_msg;
    private T data;

    public int getErr_code() {
        return err_code;
    }

    public void setErr_code(int err_code) {
        this.err_code = err_code;
    }

    public String getErr_msg() {
        return err_msg;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
