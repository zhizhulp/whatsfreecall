package com.baisi.whatsfreecall.utils.adsutils;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.baisi.whatsfreecall.adsconfig.StatisticalConfig;
import com.baisi.whatsfreecall.utils.firebaseutils.Firebase;
import com.bestgo.adsplugin.ads.AdAppHelper;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by MnyZhao on 2018/1/11.
 */

public class NativeAdsUtils {
    public static final String SHOW_HOME_NATIVE = "show_home_native";
    public static final String SHOW_CALLEND_NATIVE = "show_callend_native";
    public static final String SHOW_DIAL_NATIVE = "show_dial_native";

    /**
     * @param id          广告id
     * @param viewlayout  广告framelayout
     * @param adressName  当前界面名称标记统计
     * @param isShow      是否付费去广告 false 未付费 true 付费
     * @param countrlName 控制开关名字
     */
    public static void setAdvView(int id, FrameLayout viewlayout, String adressName, boolean isShow, String countrlName) {
        if (viewlayout == null) {
            Firebase.getInstance(getApplicationContext()).logEvent(adressName, "FlADV IS NULL");
            return;
        }
        //付费去广告
        if (!isShow) {
            //开关
            String countrlCode = AdAppHelper.getInstance(getApplicationContext()).getCustomCtrlValue(countrlName, "0");
            if ("1".equals(countrlCode)) {
                View view;
                AdAppHelper.getInstance(getApplicationContext()).loadNewNative(id);
                View recomed = AdAppHelper.getInstance(getApplicationContext()).getNative(id);
                Firebase.getInstance(getApplicationContext()).logEvent(adressName, StatisticalConfig.SHOW, adressName);
                if (recomed != null && AdAppHelper.getInstance(getApplicationContext()).isNativeLoaded(id)) {
                    Firebase.getInstance(getApplicationContext()).logEvent(adressName, StatisticalConfig.OK, adressName);
                    view = recomed;
                } else {
                    Firebase.getInstance(getApplicationContext()).logEvent(adressName, StatisticalConfig.NO, adressName);
                    view = AdAppHelper.getInstance(getApplicationContext()).getRecommendNativeView();
                }
                if (view != null) {
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER;
                    viewlayout.setVisibility(View.VISIBLE);
                    viewlayout.addView(view, layoutParams);
                } else {
                    viewlayout.setVisibility(View.GONE);
                }
            }
        }
    }

    public static void setSlphaNative(FrameLayout viewlayout, String addressName) {
        AdAppHelper.getInstance(getApplicationContext()).loadNewSplashAd();
        View view = null;
        View recomed = AdAppHelper.getInstance(getApplicationContext()).getSplashAd();
        Firebase.getInstance(getApplicationContext()).logEvent(addressName, StatisticalConfig.SHOW, addressName);
        if (recomed != null && AdAppHelper.getInstance(getApplicationContext()).isSplashReady()) {
            Firebase.getInstance(getApplicationContext()).logEvent(addressName, StatisticalConfig.OK, addressName);
            view = recomed;
        } else {
            Firebase.getInstance(getApplicationContext()).logEvent(addressName, StatisticalConfig.NO, addressName);
        }
        if (view != null) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            viewlayout.addView(view, layoutParams);
        }
    }
}
