package com.baisi.whatsfreecall.utils.httputils;

import android.util.Log;

import com.baisi.whatsfreecall.BuildConfig;
import com.baisi.whatsfreecall.application.WhatsFreeCallApplication;
import com.baisi.whatsfreecall.configs.SpConfig;
import com.baisi.whatsfreecall.configs.UrlConfig;
import com.baisi.whatsfreecall.utils.DateFormatter;
import com.baisi.whatsfreecall.utils.deviceinfoutils.DeviceInfoUtils;
import com.baisi.whatsfreecall.utils.sputils.SpUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by MnyZhao on 2018/1/9.
 */

public class HttpClentUtils {

    /**
     * 获取okhttp对象设置联网请求超时信息
     *
     * @return
     */
    public static OkHttpClient getOkhttpClient() {
        final String TAG = "HttpClentUtils";
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder.readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        /**添加header这几步一定要分开写 不然header会无效 别问我为什么
                         *我看了build源码 看返回了一个新的对象 猜想是要一个新的对象来接收
                         * 我就只定义了一个新的对象来接受新的Request
                         * 后面应该就可以，但是我没确定是否成功 ，然后我就全部都拆开了吧buider对象
                         * request的新的对象都分开之后 就能看到成功了。。。。巨大的bug 真是让人头疼
                         */
                        Request request = chain.request();
                        Request.Builder requestBuilder = request.newBuilder();
                        requestBuilder.addHeader("Content-Type", "application/json;charset=UTF-8")
                                .addHeader("x-versionname", BuildConfig.VERSION_NAME)
                                .addHeader("x-versioncode", BuildConfig.VERSION_CODE + "")
                                .addHeader("x-deviceId", DeviceInfoUtils.getDeviceInfoUtils(WhatsFreeCallApplication.getInstance().getApplicationContext()).getDeivceId(WhatsFreeCallApplication.getInstance().getApplicationContext()))
                                .addHeader("x-androidId", DeviceInfoUtils.getDeviceInfoUtils(WhatsFreeCallApplication.getInstance().getApplicationContext()).getAndroidID(WhatsFreeCallApplication.getInstance().getApplicationContext()))
                                .addHeader("x-imei", DeviceInfoUtils.getDeviceInfoUtils(WhatsFreeCallApplication.getInstance().getApplicationContext()).getImei(WhatsFreeCallApplication.getInstance().getApplicationContext()))
                                .addHeader("x-imsi", DeviceInfoUtils.getDeviceInfoUtils(WhatsFreeCallApplication.getInstance().getApplicationContext()).getImsi(WhatsFreeCallApplication.getInstance().getApplicationContext()))
                                .addHeader("x-mac", DeviceInfoUtils.getDeviceInfoUtils(WhatsFreeCallApplication.getInstance().getApplicationContext()).getMacAddress())
                                .addHeader("x-uuid", DeviceInfoUtils.getDeviceInfoUtils(WhatsFreeCallApplication.getInstance().getApplicationContext()).getUUID(WhatsFreeCallApplication.getInstance().getApplicationContext()))
                                .addHeader("x-TimeZone", DateFormatter.getCurrentTimeZone())
                               /* .addHeader("Authorization","Bearer "+SpUtils.getString(WhatsFreeCallApplication.getInstance().getApplicationContext(), SpConfig.USER_TOKEN, ""))*/;
                        Request newRequest = requestBuilder.build();
                        Log.d(TAG, "request: " + newRequest.toString());
                        Response response = chain.proceed(newRequest);
                        MediaType mediaType = response.body().contentType();
                        String content= response.body().string();
                        Log.d(TAG, content);
                        return response.newBuilder()
                                .body(ResponseBody.create(mediaType, content))
                                .build();
                    }
                })
                .connectTimeout(5, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS)//设置超时
                .build();
        return client;
    }

    /**
     * 获取retorfit对象
     *
     * @return
     */
    public static Retrofit getRetorfit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlConfig.BASE_URL) // 设置 网络请求 Url
                .client(getOkhttpClient())
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();
        return retrofit;
    }
}
