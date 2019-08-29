package com.baisi.whatsfreecall.base.net;

import retrofit2.Call;

/**
 * Created by ASUS on 2018/4/1.
 */

public interface CallLife {
    void createCall(Call call);
    void destroyCall();
}
