package com.baisi.whatsfreecall.base.net;

import android.os.Bundle;

import com.baisi.whatsfreecall.base.BaseActivity;

import retrofit2.Call;

public class BaseNetActivity extends BaseActivity {
    private NetworkManager networkManager = new NetworkManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public <T> void doNetwork(Call<T> call,ResponseHandlerImpl<T> responseHandler) {
        networkManager.addCallToQueue(call,responseHandler );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkManager.destroyCall();
    }

}
