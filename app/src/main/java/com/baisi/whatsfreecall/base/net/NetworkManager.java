package com.baisi.whatsfreecall.base.net;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ASUS on 2018/4/1.
 */

public class NetworkManager implements CallLife {
    private List<Call> callList;
    public <T> void addCallToQueue(Call<T> call,Callback<T> callback){
        createCall(call);//添加call
        call.enqueue(callback);
    }

    @Override
    public void createCall(Call call) {
        if (call == null) return;
        if (callList == null) callList = new ArrayList<>();
        callList.add(call);
    }

    @Override
    public void destroyCall() {
        if (callList == null || callList.size() == 0) return;
        for (Call call : callList) {
            if (call.isExecuted()) call.cancel();
        }
    }
}
