package com.baisi.whatsfreecall.base.net;

import android.util.Log;

import com.baisi.whatsfreecall.manager.ToastManager;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ASUS on 2018/4/1.
 */

public class ResponseHandlerImpl<T> implements HttpResponseHandler<T>, Callback<T> {
    private String TAG="HttpClentUtils";
    @Override
    public void handleException(Throwable t) {
        ToastManager.showShortToast(t.getMessage());
        Log.d(TAG, "handleException: "+t.getMessage());
    }

    @Override
    public void handle200(T result) {

    }

    @Override
    public void handleExcept200(String string) {
        ToastManager.showShortToast(string);
    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        onFinish();
        if (response.isSuccessful()) {
            try {
                handle200(response.body());
            }catch (Exception e){
                handleException(e);
            }
        } else {
            try {
                ResponseBody responseBody = response.errorBody();
                if (responseBody != null) {
                    handleExcept200(responseBody.string());
                }else {
                    handleExcept200(response.message());//?
                }
            } catch (IOException e) {
                handleException(e);
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFinish();
        handleException(t);
    }
}
