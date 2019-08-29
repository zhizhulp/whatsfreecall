package com.baisi.whatsfreecall.utils.adsutils;

import com.baisi.whatsfreecall.adsconfig.StatisticalConfig;
import com.baisi.whatsfreecall.application.WhatsFreeCallApplication;
import com.baisi.whatsfreecall.utils.firebaseutils.Firebase;
import com.bestgo.adsplugin.ads.AdAppHelper;
import com.bestgo.adsplugin.ads.listener.AdStateListener;
import com.bestgo.adsplugin.ads.listener.RewardedListener;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by MnyZhao on 2018/1/11.
 */

public class FullAdsUtils {
    public static final String SHOW_ENTER_FULL_CTRL = "show_enter_full";
    public static final String SHOW_RANDOM_FULL_VIDEO="show_random_full_video";
    public static final String SHOW_ENTER_CHARGE_FULL="enter_charge_full";
    public static final String SHOW_ENTER_CALLEND_FULL="enter_call_end";
    public static final String SHOW_CHECKINEND_FULL="checkin_end";
    public static final String COLLECT_STAR_FULL="collect_star_full";
    public static final String COLLECT_STAR = "interstitial";
    /*是否显示进入全屏*/

    /**
     * @param adressName 广告位置名称
     */
    public static void ShowFull(String adressName) {
        /**全屏广告*/
        if (AdAppHelper.getInstance(getApplicationContext()).isFullAdLoaded()) {
            Firebase.getInstance(getApplicationContext()).logEvent(adressName, StatisticalConfig.NO, adressName);
        } else {
            Firebase.getInstance(getApplicationContext()).logEvent(adressName, StatisticalConfig.NO, adressName);
        }
        AdAppHelper.getInstance(getApplicationContext()).showFullAd();
    }

    public static void ShowFull(String adressName, AdStateListener listener) {
        ShowFull(adressName);
        AdAppHelper.getInstance(getApplicationContext()).setAdStateListener(listener);
    }

    /*是否显示进入全屏*/

    /**
     * @param adressName  firebase统计信息
     * @param isShow      是否付费 是否付费去广告 false 未付费 true 付费
     * @param countrlName 控制开关名称
     */
    public static void isShowFull(String adressName, boolean isShow, String countrlName) {
        AdAppHelper.getInstance(getApplicationContext()).loadNewInterstitial();
        String enter_full = AdAppHelper.getInstance(getApplicationContext()).getCustomCtrlValue(countrlName, "0");
        if (!isShow) {
            if (enter_full.equals("1")) {//是否开启进入全屏 1 表示开启 未开不处理
                /**全屏广告*/
                if (AdAppHelper.getInstance(getApplicationContext()).isFullAdLoaded()) {
                    Firebase.getInstance(getApplicationContext()).logEvent(adressName, StatisticalConfig.NO, adressName);
                } else {
                    Firebase.getInstance(getApplicationContext()).logEvent(adressName, StatisticalConfig.NO, adressName);
                }
                AdAppHelper.getInstance(getApplicationContext()).showFullAd();
            }
        }
    }

    /**
     * @param adressName  firebase统计信息
     * @param isShow      是否付费 是否付费去广告 false 未付费 true 付费
     * @param countrlName 控制开关
     */
    public static void isShowRandomAdsFull(String adressName, boolean isShow, String countrlName) {
        AdAppHelper.getInstance(getApplicationContext()).loadNewInterstitial();
        //是否开启广告观看结束后全屏 1 2 3表示开启 未开0不处理
        String show_random_full_video = AdAppHelper.getInstance(getApplicationContext()).getCustomCtrlValue(countrlName, "0");
        if (!isShow) {
            switch (show_random_full_video) {

                case "1":
                    //每次都显示
                    ShowFull(adressName);
                    break;
                case "0":
                    break;
                default:
                    int x = 5;
                    try {
                        x = Integer.parseInt(show_random_full_video);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        x = 5;
                    }
                    //每隔多少次显示一次
                    if (WhatsFreeCallApplication.getInstance().getShowVideoFullAds() % x == 1) {
                        ShowFull(adressName);
                    }
                    WhatsFreeCallApplication.getInstance().setShowVideoFullAds(WhatsFreeCallApplication.getInstance().getShowVideoFullAds() + 1);
                    break;
            }
        }
    }
}
