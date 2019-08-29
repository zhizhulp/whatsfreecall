package com.baisi.whatsfreecall.base.net;


public interface HttpResponseHandler<T> {
    void handleException(Throwable t);
    void handle200(T result);
    void handleExcept200(String string);
    void onFinish();
}
