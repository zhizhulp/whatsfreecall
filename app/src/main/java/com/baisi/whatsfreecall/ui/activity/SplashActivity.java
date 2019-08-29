package com.baisi.whatsfreecall.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.FrameLayout;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.adsconfig.StatisticalConfig;
import com.baisi.whatsfreecall.application.WhatsFreeCallApplication;
import com.baisi.whatsfreecall.base.BaseActivity;
import com.baisi.whatsfreecall.utils.adsutils.NativeAdsUtils;
import com.baisi.whatsfreecall.utils.firebaseutils.Firebase;
import com.bestgo.adsplugin.ads.AdAppHelper;
import com.bestgo.adsplugin.ads.activity.ShowAdFilter;

/**
 * @author MnyZhao
 */
public class SplashActivity extends BaseActivity implements ShowAdFilter {
    private long enterTime = -1;
    private FrameLayout mFlSlphaAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slpha);
        /**
         * 加载全局广告 native  全屏广告 视频广告
         */
        AdAppHelper.getInstance(getApplicationContext()).loadNewNative();
        initView();
        AdAppHelper.getInstance(getApplicationContext()).loadNewInterstitial();
        AdAppHelper.getInstance(getApplicationContext()).loadNewRewardedVideoAd();
        handler1.sendEmptyMessageDelayed(1000, 3*1000);
        /*获取appInstanceid*/
        Firebase.getInstance(getApplicationContext()).getAppInstanceId(new Firebase.getAppInstanceIdListener() {
            @Override
            public void setAppInstanceId(String appInstanceId) {
                WhatsFreeCallApplication.getInstance().setAppInstandID(appInstanceId);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1000) {
                try {
                    if (enterTime == -1) {
                        enterTime = System.currentTimeMillis();
                    }
                    if (System.currentTimeMillis() - enterTime >= AdAppHelper.getInstance(getApplicationContext()).getHomeDelayTime()) {

                        if (!AdAppHelper.getInstance(getApplicationContext()).isFullAdLoaded()) {
                            Firebase.getInstance(getApplicationContext()).logEvent("广告", "广告没准备好", "全屏全部");
                            AdAppHelper.getInstance(getApplicationContext()).loadNewInterstitial();//没准备好再加载一次
                        } else {
                            Firebase.getInstance(getApplicationContext()).logEvent("广告", "广告准备好", "全屏全部");
                        }
                        startHomeActivcity();
                    } else {
                        if (AdAppHelper.getInstance(getApplicationContext()).isFullAdLoaded()) {
                            Firebase.getInstance(getApplicationContext()).logEvent("广告", "广告准备好", "全屏全部");
                            startHomeActivcity();
                        } else {
                            handler1.sendEmptyMessageDelayed(1000, 1000);
                        }
                    }
                } catch (Exception ex) {
                }
            }
        }
    };

    private void startHomeActivcity() {
        skip(HomeActivity.class);
        finish();
    }

    @Override
    public boolean allowShowAd() {
        return false;
    }

    private void initView() {
        mFlSlphaAds = (FrameLayout) findViewById(R.id.fl_slpha_ads);
        NativeAdsUtils.setSlphaNative(mFlSlphaAds, StatisticalConfig.SLPHAADS);
    }
}
