package com.baisi.whatsfreecall.manager;

import com.baisi.whatsfreecall.application.WhatsFreeCallApplication;
import com.baisi.whatsfreecall.entity.ConsumeEntity;
import com.baisi.whatsfreecall.entity.httpbackentity.UserEntity;
import com.baisi.whatsfreecall.configs.SpConfig;
import com.baisi.whatsfreecall.configs.UrlConfig;
import com.baisi.whatsfreecall.utils.sputils.SpUtils;
import com.baisi.whatsfreecall.utils.utilpay.Purchase;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by MnyZhao on 2017/12/8.
 * @author MnyZhao
 * 本管理类用来管理登陆，消耗，登陆并消耗的网络请求 以及retorfoit的创建
 */

public class LoginConsumeManager {
    private static Retrofit retrofit;

    public LoginConsumeManager(){
       checkReterfoitIsNull();
    }
    private void checkReterfoitIsNull(){
        if(retrofit==null){
            retrofit=createReterfoit();
        }
    }
    public Retrofit createReterfoit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlConfig.BASE_URL) // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();
        return retrofit;
    }
    /*login*/
    public interface PostLogin_Interface {
        @POST(UrlConfig.LOGIN)
        Call<UserEntity> getCall(@Header("Authorization") String userToken, @Body String values);
    }
    public void login(String token){
        checkReterfoitIsNull();
        // 步骤5:创建 网络请求接口 的实例
        PostLogin_Interface request = retrofit.create(PostLogin_Interface.class);
        //对 发送请求 进行封装(因为传递json 所以将参数用实体包裹转成json)
        Gson gson = new Gson();
        Sign sign = new Sign(token);
        String body = gson.toJson(sign);
        String userToken = SpUtils.getString(WhatsFreeCallApplication.getInstance().getApplicationContext(), SpConfig.USER_TOKEN, "");
        Call<UserEntity> call = request.getCall("Bearer " + userToken, body);
        call.enqueue(new Callback<UserEntity>() {
            @Override
            public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {

            }

            @Override
            public void onFailure(Call<UserEntity> call, Throwable t) {
                System.out.println("请求失败");
                System.out.println(t.getMessage());
            }
        });
    }
    class Sign {
        public String gt;
        public Sign(String gt) {
            this.gt = gt;
        }
    }
    /*consume*/
    public interface Consume_Interface {
        @POST(UrlConfig.PAY_PURCHASE)
        Call<String> getCall(@Header("Authorization") String author, @Body String body);
    }
    boolean isConsume;
    public boolean consume(Purchase purchase){
        // 步骤5:创建 网络请求接口 的实例
        Consume_Interface request = retrofit.create(Consume_Interface.class);
        //对 发送请求 进行封装( //对 发送请求 进行封装(因为传递json 所以将参数用实体包裹转成json))
        Gson gson = new Gson();
        ConsumeEntity consume = new ConsumeEntity(purchase.getDeveloperPayload(), purchase.getPackageName(), purchase.getOrderId(),
                purchase.getToken(), purchase.getOriginalJson(), purchase.getSignature());
        String body = gson.toJson(consume);
        String userToken = SpUtils.getString(WhatsFreeCallApplication.getInstance().getApplicationContext(), SpConfig.USER_TOKEN, "");
        Call<String> call = request.getCall("Bearer " + userToken, body);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                isConsume = true;
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                isConsume = false;
                System.out.println("请求失败");
                System.out.println(t.getMessage());
            }
        });
        return isConsume;
    }

}
