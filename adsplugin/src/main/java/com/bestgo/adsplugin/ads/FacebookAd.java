package com.bestgo.adsplugin.ads;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestgo.adsplugin.R;
import com.bestgo.adsplugin.ads.activity.AdActivity;
import com.bestgo.adsplugin.ads.activity.ScreenActivity;
import com.bestgo.adsplugin.ads.cache.FBCache;
import com.bestgo.adsplugin.ads.listener.AdStateListener;
import com.bestgo.adsplugin.views.NativeAdContainer;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.internal.server.AdPlacementType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FacebookAd {
    private Context mContext;
    private AdView mBannerView;
    private String mBannerId;

    private boolean enableBanner;
    private boolean enableNative;
    private boolean enableFBN;
    private boolean enableFBNBanner;
    private boolean enableInterstitial;

    private long firstFullAdTime = -1;

    private boolean bannerLoaded;
    private boolean bannerRequest;

    private AdStateListener mAdListener;

    private FBInterstitialAd[] fullAds;
//    private NativeClassAd[] nativeAds;
    private NativeClassAd[] fbnBannerAds;
    private NativeClassAd[] fbnFullAds;

    private ArrayList<NativeClassAd> mNativeAds = new ArrayList<>();

    private class FBInterstitialAd {
        public InterstitialAd ad;
        public InterstitialAd cacheAd;
        public boolean cacheAdRequest;
        public boolean cacheAdLoaded;
        public String id;
        public boolean requested;
        public boolean loaded;
        public int errorCount;
        public long lastRequestTime;
        public long loadedTime;
    }

    private class NativeClassAd {
        private NativeAd nativeAd;
        public NativeAd cacheAd;
        public boolean cacheAdRequest;
        public boolean cacheAdLoaded;
        private NativeAdContainer cacheAdView;
        private String nativeId;
        private boolean nativeLoaded;
        private boolean nativeClicked;
        private boolean nativeImpression;
        private boolean nativeRequest;
        private NativeAdContainer nativeAdView;
        private long lastRequestNativeTime;
        private long lastRequestCacheNativeTime;
        private int width;
        private int height;
        private boolean pendingRefresh;
        private boolean pendingRefreshCache;

        private int placementIndex;//位置索引，表明该native广告是用在程序的指定位置的
    }

    public FacebookAd(Context context) {
        this.mContext = context;
        AdConfig config = AdAppHelper.getInstance(mContext).getConfig();
        this.mBannerId = config.banner_ids.fb;
        if (config.fb_full_ids.count > 0 && config.fb_full_ids.ids != null) {
            fullAds = new FBInterstitialAd[config.fb_full_ids.count];
            for (int i = 0; i < fullAds.length; i++) {
                fullAds[i] = new FBInterstitialAd();
                fullAds[i].id = config.fb_full_ids.ids[i].id;
            }
        }
        if (config.fb_native_ids.count > 0 && config.fb_native_ids.ids != null) {
            for (int i = 0; i < config.fb_native_ids.count; i++) {
                NativeClassAd one = new NativeClassAd();
                one.nativeId = config.fb_native_ids.ids[i].id;
                one.width = config.fb_native_ids.ids[i].width;
                one.height = config.fb_native_ids.ids[i].height;
                mNativeAds.add(one);
            }
        }
        if (config.fbn_banner_ids.count > 0 && config.fbn_banner_ids.ids != null) {
            fbnBannerAds = new NativeClassAd[config.fbn_banner_ids.count];
            for (int i = 0; i < fbnBannerAds.length; i++) {
                fbnBannerAds[i] = new NativeClassAd();
                fbnBannerAds[i].nativeId = config.fbn_banner_ids.ids[i].id;
            }
        }
        if (config.fbn_full_ids.count > 0 && config.fbn_full_ids.ids != null) {
            fbnFullAds = new NativeClassAd[config.fbn_full_ids.count];
            for (int i = 0; i < fbnFullAds.length; i++) {
                fbnFullAds[i] = new NativeClassAd();
                fbnFullAds[i].nativeId = config.fbn_full_ids.ids[i].id;
            }
        }

        ArrayList<String> testIdList = AdAppHelper.getTestDeviceIdList(AdNetwork.Facebook);
        for (String s : testIdList) {
            AdSettings.addTestDevice(s);
        }
    }

    public void resetId() {
        AdConfig config = AdAppHelper.getInstance(mContext).getConfig();
        if (!mBannerId.equals(config.banner_ids.fb)) {
            mBannerId = config.banner_ids.fb;
            mBannerView = null;
            bannerLoaded = false;
            bannerRequest = false;
        }
        if (config.fb_full_ids.count > 0 && config.fb_full_ids.ids != null) {
            if (fullAds.length != config.fb_full_ids.count) {
                fullAds = new FBInterstitialAd[config.fb_full_ids.count];
                for (int i = 0; i < fullAds.length; i++) {
                    fullAds[i] = new FBInterstitialAd();
                    fullAds[i].id = config.fb_full_ids.ids[i].id;
                }
            } else {
                for (int i = 0; i < fullAds.length; i++) {
                    if (!fullAds[i].id.equals(config.fb_full_ids.ids[i].id)) {
                        fullAds[i] = new FBInterstitialAd();
                        fullAds[i].id = config.fb_full_ids.ids[i].id;
                    }
                }
            }
        }
        if (config.fb_native_ids.count > 0 && config.fb_native_ids.ids != null) {
            if (mNativeAds.size() != config.fb_native_ids.count) {
                mNativeAds.clear();
                for (int i = 0; i < config.fb_native_ids.count; i++) {
                    NativeClassAd one = new NativeClassAd();
                    one.nativeId = config.fb_native_ids.ids[i].id;
                    one.width = config.fb_native_ids.ids[i].width;
                    one.height = config.fb_native_ids.ids[i].height;
                    mNativeAds.add(one);
                }
            } else {
                for (int i = 0; i < mNativeAds.size(); i++) {
                    if (!mNativeAds.get(i).nativeId.equals(config.fb_native_ids.ids[i].id)) {
                        NativeClassAd nativeClassAd = mNativeAds.get(i);
                        nativeClassAd.nativeId = config.fb_native_ids.ids[i].id;
                        nativeClassAd.width = config.fb_native_ids.ids[i].width;
                        nativeClassAd.height = config.fb_native_ids.ids[i].height;
                    }
                }
            }
        }
        if (config.fbn_banner_ids.count > 0 && config.fbn_banner_ids.ids != null) {
            if (fbnBannerAds.length != config.fbn_banner_ids.count) {
                fbnBannerAds = new NativeClassAd[config.fbn_banner_ids.count];
                for (int i = 0; i < fbnBannerAds.length; i++) {
                    fbnBannerAds[i] = new NativeClassAd();
                    fbnBannerAds[i].nativeId = config.fbn_banner_ids.ids[i].id;
                }
            } else {
                for (int i = 0; i < fbnBannerAds.length; i++) {
                    if (!fbnBannerAds[i].nativeId.equals(config.fbn_banner_ids.ids[i].id)) {
                        NativeClassAd nativeClassAd = fbnBannerAds[i];
                        nativeClassAd.nativeId = config.fbn_banner_ids.ids[i].id;
                    }
                }

            }
        }
        if (config.fbn_full_ids.count > 0 && config.fbn_full_ids.ids != null) {
            if (fbnFullAds.length != config.fbn_full_ids.count) {
                fbnFullAds = new NativeClassAd[config.fbn_full_ids.count];
                for (int i = 0; i < fbnFullAds.length; i++) {
                    fbnFullAds[i] = new NativeClassAd();
                    fbnFullAds[i].nativeId = config.fbn_full_ids.ids[i].id;
                }
            } else {
                for (int i = 0; i < fbnFullAds.length; i++) {
                    if (!fbnFullAds[i].nativeId.equals(config.fbn_full_ids.ids[i].id)) {
                        NativeClassAd nativeClassAd = fbnFullAds[i];
                        nativeClassAd.nativeId = config.fbn_full_ids.ids[i].id;
                    }
                }

            }
        }
    }

    public void setAdListener(AdStateListener listener) {
        this.mAdListener = listener;
    }

    public void setBannerEnabled(boolean flag) {
        enableBanner = flag;
    }

    public void setNativeEnabled(boolean flag) {
        enableNative = flag;
    }

    public void setInterstitialEnabled(boolean flag) {
        enableInterstitial = flag;
    }

    public void setFBNEnabled(boolean flag) {
        enableFBN = flag;
    }

    public void setFBNBannerEnabled(boolean flag) {
        enableFBNBanner = flag;
    }

    public boolean isBannerLoaded() {
        return bannerLoaded;
    }

    public boolean isNativeLoaded() {
        if (mNativeAds.size() == 0) return false;
        for (int i = 0; i < mNativeAds.size(); i++) {
            if (mNativeAds.get(i).nativeLoaded) {
                return true;
            } else if (mNativeAds.get(i).cacheAdLoaded) {
                return true;
            }
        }
        return false;
    }

    public boolean isNativeLoaded(int index) {
        if (mNativeAds.size() == 0) return false;
        if (index >= mNativeAds.size() || index < 0) return false;
        return mNativeAds.get(index).nativeLoaded || mNativeAds.get(index).cacheAdLoaded;
    }

    public boolean isInterstitialLoaded() {
        if (fullAds == null) return false;
        for (int i = 0; i < fullAds.length; i++) {
            if (fullAds[i].loaded && (System.currentTimeMillis() - fullAds[i].loadedTime) < AdAppHelper.MAX_AD_ALIVE_TIME) {
                return true;
            }
            if (fullAds[i].cacheAdLoaded) {
                return true;
            }
        }
        return false;
    }

    public boolean isInterstitialLoaded(int index) {
        if (fullAds == null) return false;
        if (index >= fullAds.length || index < 0) return false;
        return (fullAds[index].loaded  && (System.currentTimeMillis() - fullAds[index].loadedTime) < AdAppHelper.MAX_AD_ALIVE_TIME)
                || (fullAds[index].cacheAdLoaded);
    }

    public boolean isFBNLoaded() {
        if (fbnFullAds == null) return false;
        for (int i = 0; i < fbnFullAds.length; i++) {
            if (fbnFullAds[i].nativeLoaded && (System.currentTimeMillis() - fbnFullAds[i].lastRequestNativeTime) < AdAppHelper.MAX_AD_ALIVE_TIME) {
                return true;
            } else if (fbnFullAds[i].cacheAdLoaded) {
                return true;
            }
        }
        return false;
    }

    public boolean isFBNLoaded(int index) {
        if (fbnFullAds == null) return false;
        if (index >= fbnFullAds.length || index < 0) return false;
        return (fbnFullAds[index].nativeLoaded && (System.currentTimeMillis() - fbnFullAds[index].lastRequestNativeTime) < AdAppHelper.MAX_AD_ALIVE_TIME)
                || (fbnFullAds[index].cacheAdLoaded);
    }

    public boolean isFBNBannerLoaded() {
        if (fbnBannerAds == null) return false;
        for (int i = 0; i < fbnBannerAds.length; i++) {
            if (fbnBannerAds[i].nativeLoaded) {
                return true;
            }
        }
        return false;
    }

    public boolean isFBNBannerLoaded(int index) {
        if (fbnBannerAds == null) return false;
        if (index >= fbnBannerAds.length || index < 0) return false;
        return fbnBannerAds[index].nativeLoaded;
    }

    public View getBanner() {
        return mBannerView;
    }

    private class ReloadNativeTask implements Runnable {
        private NativeClassAd nativeClassAd;
        private int index;
        private boolean refreshCache;

        public ReloadNativeTask(NativeClassAd ad, int index, boolean refreshCache) {
            nativeClassAd = ad;
            this.index = index;
            this.refreshCache = refreshCache;
        }

        @Override
        public void run() {
            if (refreshCache) {
                if (nativeClassAd.cacheAdView == null) return;
                if (AdAppHelper.getInstance(mContext).isScreenOn() && nativeClassAd.cacheAdView.isShown() &&
                        (System.currentTimeMillis() - nativeClassAd.lastRequestCacheNativeTime) >= AdAppHelper.NATIVE_REFRESH_TIME) {
                    nativeClassAd.cacheAdLoaded = false;
                    nativeClassAd.cacheAdRequest = false;
                    nativeClassAd.pendingRefreshCache = false;
                    loadNewNativeAd(index, false);
                    nativeClassAd.nativeAdView.postDelayed(new ReloadNativeTask(nativeClassAd, index, refreshCache), 1000);
                } else if (nativeClassAd.pendingRefreshCache) {
                    //去掉手动刷新控制
                    nativeClassAd.cacheAdView.postDelayed(new ReloadNativeTask(nativeClassAd, index, refreshCache), 1000);
                }
            } else {
                if (nativeClassAd.nativeAdView == null) return;
                if (AdAppHelper.getInstance(mContext).isScreenOn() && nativeClassAd.nativeAdView.isShown() &&
                        ((System.currentTimeMillis() - nativeClassAd.lastRequestNativeTime) >= AdAppHelper.NATIVE_REFRESH_TIME && nativeClassAd.nativeImpression) || nativeClassAd.nativeClicked) {
                    nativeClassAd.nativeLoaded = false;
                    nativeClassAd.nativeRequest = false;
                    loadNewNativeAd(index, false);
                    nativeClassAd.nativeAdView.postDelayed(new ReloadNativeTask(nativeClassAd, index, refreshCache), 1000);
                } else if (nativeClassAd.pendingRefresh) {
                    //去掉手动刷新控制
                    nativeClassAd.nativeAdView.postDelayed(new ReloadNativeTask(nativeClassAd, index, refreshCache), 1000);
                }
            }
        }
    }

    private class ReloadBannerFBNTask implements Runnable {
        private NativeClassAd nativeClassAd;
        private int index;

        public ReloadBannerFBNTask(NativeClassAd ad, int index) {
            nativeClassAd = ad;
            this.index = index;
        }

        @Override
        public void run() {
            if (nativeClassAd.nativeAdView == null) return;
            if (AdAppHelper.getInstance(mContext).isScreenOn() && nativeClassAd.nativeAdView.isShown() &&
                    (System.currentTimeMillis() - nativeClassAd.lastRequestNativeTime) >= AdAppHelper.NATIVE_REFRESH_TIME) {
                nativeClassAd.nativeLoaded = false;
                nativeClassAd.nativeRequest = false;
                loadNewFBNBanner(index);
                nativeClassAd.nativeAdView.postDelayed(new ReloadBannerFBNTask(nativeClassAd, index), 1000);
            } else if (nativeClassAd.pendingRefresh) {
                //去掉手动刷新控制
                nativeClassAd.nativeAdView.postDelayed(new ReloadBannerFBNTask(nativeClassAd, index), 1000);
            }
        }
    }

    public View getBannerFBN() {
        if (fbnBannerAds == null) return null;
        for (int i = 0; i < fbnBannerAds.length; i++) {
            if (fbnBannerAds[i].nativeLoaded) {
                final NativeClassAd nativeClassAd = fbnBannerAds[i];
                if (!nativeClassAd.pendingRefresh) {
                    nativeClassAd.pendingRefresh = true;
                    //去掉手动刷新控制
                    nativeClassAd.nativeAdView.postDelayed(new ReloadBannerFBNTask(nativeClassAd, i), 30000);
                }
                return nativeClassAd.nativeAdView;
            }
        }
        return null;
    }

    public View getBannerFBN(final int index) {
        if (fbnBannerAds == null) return null;
        if (index < 0 || index >= fbnBannerAds.length) return null;
        final NativeClassAd nativeClassAd = fbnBannerAds[index];
        if (!nativeClassAd.pendingRefresh) {
            nativeClassAd.pendingRefresh = true;
            //去掉手动刷新控制
            nativeClassAd.nativeAdView.postDelayed(new ReloadBannerFBNTask(nativeClassAd, index), 30000);
        }
        return nativeClassAd.nativeAdView;
    }

    public View getNative() {
        if (mNativeAds.size() == 0) return null;
        for (int i = 0; i < mNativeAds.size(); i++) {
            if (mNativeAds.get(i).nativeLoaded) {
                final NativeClassAd nativeClassAd = mNativeAds.get(i);
                if (!nativeClassAd.pendingRefresh) {
                    nativeClassAd.pendingRefresh = true;
                    //去掉手动刷新控制
                    nativeClassAd.nativeAdView.postDelayed(new ReloadNativeTask(nativeClassAd, i, false), 30000);
                }
                return nativeClassAd.nativeAdView;
            } else if (mNativeAds.get(i).cacheAdLoaded) {
                final NativeClassAd nativeClassAd = mNativeAds.get(i);
                if (!nativeClassAd.pendingRefreshCache) {
                    nativeClassAd.pendingRefreshCache = true;
                    //去掉手动刷新控制
                    nativeClassAd.cacheAdView.postDelayed(new ReloadNativeTask(nativeClassAd, i, true), 30000);
                }
                return nativeClassAd.cacheAdView;
            }
        }
        return null;
    }

    public View getNative(final int index) {
        if (mNativeAds.size() == 0) return null;
        if (index < 0 || index >= mNativeAds.size()) return null;
        final NativeClassAd nativeClassAd = mNativeAds.get(index);
        if (nativeClassAd.nativeLoaded) {
            if (!nativeClassAd.pendingRefresh) {
                nativeClassAd.pendingRefresh = true;
                //去掉手动刷新控制
                nativeClassAd.nativeAdView.postDelayed(new ReloadNativeTask(nativeClassAd, index, false), 30000);
            }
            return nativeClassAd.nativeAdView;
        } else if (nativeClassAd.cacheAdLoaded) {
            if (!nativeClassAd.pendingRefreshCache) {
                nativeClassAd.pendingRefreshCache = true;
                //去掉手动刷新控制
                nativeClassAd.cacheAdView.postDelayed(new ReloadNativeTask(nativeClassAd, index, true), 30000);
            }
            return nativeClassAd.cacheAdView;
        }
        return null;
    }

    public void showInterstitial() {
        if (fullAds == null) return;
        for (int i = 0; i < fullAds.length; i++) {
            if ((fullAds[i].ad != null && fullAds[i].ad.isAdLoaded())
                    || (fullAds[i].cacheAd != null && fullAds[i].cacheAd.isAdLoaded())) {
                showInterstitial(i);
                break;
            }
        }
    }

    public void showInterstitial(int index) {
        if (fullAds == null) return;
        AdConfig config = AdAppHelper.getInstance(mContext).getConfig();
        for (int i = 0; i < fullAds.length; i++) {
            if (index != i) continue;
            try {
                if (config.ad_ctrl.cache_first == 1) {
                    if (fullAds[i].cacheAd != null && fullAds[i].cacheAd.isAdLoaded()) {
                        AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAds[index].id, Const.ACTION_CACHE_OPEN);
                        fullAds[i].cacheAdLoaded = false;
                        fullAds[i].cacheAd.show();
                    } else if (fullAds[i].ad != null && fullAds[i].ad.isAdLoaded()) {
                        AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAds[index].id, Const.ACTION_OPEN);
                        fullAds[i].loaded = false;
                        fullAds[i].ad.show();
                    }
                } else {
                     if (fullAds[i].ad != null && fullAds[i].ad.isAdLoaded()) {
                        AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAds[index].id, Const.ACTION_OPEN);
                        fullAds[i].loaded = false;
                        fullAds[i].ad.show();
                    } else if (fullAds[i].cacheAd != null && fullAds[i].cacheAd.isAdLoaded()) {
                        AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAds[index].id, Const.ACTION_CACHE_OPEN);
                        fullAds[i].cacheAdLoaded = false;
                        fullAds[i].cacheAd.show();
                    }
                }
                break;
            } catch (Exception ex) {
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD, "错误", ex.getMessage());
            }
        }
    }

    public void showFBNAdAutoClose() {
        if (fbnFullAds == null) return;
        for (int i = 0; i < fbnFullAds.length; i++) {
            if (fbnFullAds[i].nativeLoaded || fbnFullAds[i].cacheAdLoaded) {
                if (fbnFullAds[i].nativeLoaded) {
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fbnFullAds[i].nativeId, Const.ACTION_OPEN);
                    fbnFullAds[i].nativeLoaded = false;
                    ScreenActivity.mNativeAd = fbnFullAds[i].nativeAd;
                } else {
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fbnFullAds[i].nativeId, Const.ACTION_CACHE_OPEN);
                    fbnFullAds[i].cacheAdLoaded = false;
                    ScreenActivity.mNativeAd = fbnFullAds[i].cacheAd;
                }
                ScreenActivity.AdId = fbnFullAds[i].nativeId;
                ScreenActivity.AdIndex = i;
                Intent intent = new Intent(mContext, ScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ScreenActivity.EXTRA_AUTO_FINISH, true);
                mContext.startActivity(intent);
                break;
            }
        }
    }

    public void showFBNAd() {
        if (fbnFullAds == null) return;
        for (int i = 0; i < fbnFullAds.length; i++) {
            if (fbnFullAds[i].nativeLoaded || fbnFullAds[i].cacheAdLoaded) {
                showFBNAd(i);
                break;
            }
        }
    }

    public void showFBNAd(int index) {
        if (fbnFullAds == null) return;
        if (index < 0 || index >= fbnFullAds.length) return;

        AdConfig config = AdAppHelper.getInstance(mContext).getConfig();
        if (config.ad_ctrl.cache_first == 1) {
            if (fbnFullAds[index].cacheAdLoaded) {
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fbnFullAds[index].nativeId, Const.ACTION_CACHE_OPEN);
                fbnFullAds[index].cacheAdLoaded = false;
                AdActivity.mNativeAd = fbnFullAds[index].cacheAd;
                AdActivity.AdId = fbnFullAds[index].nativeId;
                AdActivity.AdIndex = index;
                Intent intent = new Intent(mContext, AdActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } else if (fbnFullAds[index].nativeLoaded) {
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fbnFullAds[index].nativeId, Const.ACTION_OPEN);
                fbnFullAds[index].nativeLoaded = false;
                AdActivity.mNativeAd = fbnFullAds[index].nativeAd;
                AdActivity.AdId = fbnFullAds[index].nativeId;
                AdActivity.AdIndex = index;
                Intent intent = new Intent(mContext, AdActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        } else {
            if (fbnFullAds[index].nativeLoaded) {
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fbnFullAds[index].nativeId, Const.ACTION_OPEN);
                fbnFullAds[index].nativeLoaded = false;
                AdActivity.mNativeAd = fbnFullAds[index].nativeAd;
                AdActivity.AdId = fbnFullAds[index].nativeId;
                AdActivity.AdIndex = index;
                Intent intent = new Intent(mContext, AdActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } else if (fbnFullAds[index].cacheAdLoaded) {
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fbnFullAds[index].nativeId, Const.ACTION_CACHE_OPEN);
                fbnFullAds[index].cacheAdLoaded = false;
                AdActivity.mNativeAd = fbnFullAds[index].cacheAd;
                AdActivity.AdId = fbnFullAds[index].nativeId;
                AdActivity.AdIndex = index;
                Intent intent = new Intent(mContext, AdActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        }
    }

    public void loadNewBanner() {
        if (TextUtils.isEmpty(mBannerId)) return;
        if (bannerLoaded) return;
        if (bannerRequest) return;
        if (!enableBanner) return;

        bannerRequest = true;

        if (mBannerView == null) {
            mBannerView = new AdView(mContext, mBannerId, AdSize.BANNER_HEIGHT_50);

            mBannerView.setAdListener(new AdListener() {
                @Override
                public void onAdClicked(Ad ad) {
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, mBannerId, Const.ACTION_CLICK);
                    if (mAdListener != null) {
                        mAdListener.onAdClick(new AdType(AdType.FACEBOOK_BANNER), 0);
                    }
                }

                public void onLoggingImpression(Ad ad) {
                    if (mAdListener != null) {
                        mAdListener.onAdOpen(new AdType(AdType.FACEBOOK_BANNER), 0);
                    }
                    String requestId = ad.getRequestId();
                    if (requestId != null) {
                        FBCache.deleteFromCache(requestId, AdPlacementType.BANNER.toString(), mBannerId);
                    }
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, mBannerId, Const.ACTION_SHOW_BANNER);
                    AdAppHelper.getInstance(mContext).getFacebook().logEvent(Const.CATEGORY_FB_AD_POSISTION, mBannerId, Const.ACTION_SHOW_BANNER);
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, mBannerId, Const.ACTION_LOAD);
                    bannerLoaded = true;
                    bannerRequest = false;
                    if (mAdListener != null) {
                        mAdListener.onAdLoaded(new AdType(AdType.FACEBOOK_BANNER), 0);
                    }
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    bannerRequest = false;
                    bannerLoaded = false;
                    if (mAdListener != null) {
                        mAdListener.onAdLoadFailed(new AdType(AdType.FACEBOOK_BANNER), 0, adError.getErrorMessage());
                    }
                }
            });
        }

        mBannerView.loadAd();
        AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, mBannerId, Const.ACTION_REQUEST);
    }

    public void loadNewInterstitial() {
        if (fullAds == null) return;
        for (int i = 0; i < fullAds.length; i++) {
            loadNewInterstitial(i);
        }
    }

    private void loadNewInterstitialFromCache(final int index) {
        if (fullAds == null) return;
        if (index < 0 || index >= fullAds.length) return;

        final FBInterstitialAd fullAd = fullAds[index];
        if (TextUtils.isEmpty(fullAd.id)) return;
        if (fullAd.cacheAdLoaded) return;
        if (fullAd.cacheAdRequest) return;
        if (!enableInterstitial) return;

        fullAd.cacheAdRequest = true;

        if (fullAd.cacheAd == null) {
            fullAd.cacheAd = new InterstitialAd(mContext, fullAd.id);
            fullAd.cacheAd.setUseCache(true);
            fullAd.cacheAd.setAdListener(new InterstitialAdListener() {
                private String requestId = null;
                @Override
                public void onInterstitialDisplayed(Ad ad) {
                    if (mAdListener != null) {
                        mAdListener.onAdOpen(new AdType(AdType.FACEBOOK_FULL), index);
                    }
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    if (requestId != null) {
                        FBCache.releaseObtain(requestId);
                    }
                    fullAd.cacheAdRequest = false;
                    fullAd.cacheAdLoaded = false;
                    if (mAdListener != null) {
                        mAdListener.onAdClosed(new AdType(AdType.FACEBOOK_FULL), index);
                    }
                    AdAppHelper.getInstance(mContext).loadNewInterstitial(index);
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAd.id, Const.ACTION_CACHE_CLOSE_FULL);
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    fullAd.cacheAdLoaded = false;
                    fullAd.cacheAdRequest = false;
                    if (mAdListener != null) {
                        mAdListener.onAdLoadFailed(new AdType(AdType.FACEBOOK_FULL), index, adError.getErrorMessage());
                    }
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    requestId = ad.getRequestId();
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAd.id, Const.ACTION_CACHE_LOAD);
                    fullAd.cacheAdLoaded = true;
                    fullAd.cacheAdRequest = false;
                    if (mAdListener != null) {
                        mAdListener.onAdLoaded(new AdType(AdType.FACEBOOK_FULL), index);
                    }
                }

                @Override
                public void onAdClicked(Ad ad) {
                    if (requestId != null) {
                        FBCache.deleteFromCache(requestId, AdPlacementType.INTERSTITIAL.toString(), fullAd.id);
                    }
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAd.id, Const.ACTION_CACHE_CLICK);
                    if (mAdListener != null) {
                        mAdListener.onAdClick(new AdType(AdType.FACEBOOK_FULL), index);
                    }
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    String requestId = ad.getRequestId();
                    AdConfig config = AdAppHelper.getInstance(mContext).getConfig();
                    if (requestId != null && config.ad_ctrl.reuse_cache != 1) {
                        FBCache.deleteFromCache(requestId, AdPlacementType.INTERSTITIAL.toString(), fullAd.id);
                    }
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAd.id, Const.ACTION_CACHE_SHOW);
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAd.id, Const.ACTION_CACHE_SHOW_FULL);
                    AdAppHelper.getInstance(mContext).getFacebook().logEvent(Const.CATEGORY_FB_AD_POSISTION, fullAd.id, Const.ACTION_CACHE_SHOW_FULL);
                }
            });
        }
        try {
            fullAd.cacheAd.loadAd();
            AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAd.id, Const.ACTION_CACHE_REQUEST);
        } catch (Exception ex) {
            AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD, "错误", ex.getMessage());
        }
    }

    public void loadNewInterstitial(final int index) {
        loadNewInterstitialFromCache(index);
        if (fullAds == null) return;
        if (index < 0 || index >= fullAds.length) return;

        long now = System.currentTimeMillis();
        final FBInterstitialAd fullAd = fullAds[index];
        if (TextUtils.isEmpty(fullAd.id)) return;
        if (fullAd.loaded && (System.currentTimeMillis() - fullAds[index].loadedTime) < AdAppHelper.MAX_AD_ALIVE_TIME) return;
        if (fullAd.requested) return;
        if (!enableInterstitial) return;

        fullAd.requested = true;
        fullAd.lastRequestTime = now;
        if (firstFullAdTime == -1) {
            firstFullAdTime = now;
        }

        if (fullAd.ad == null) {
            fullAd.ad = new InterstitialAd(mContext, fullAd.id);
            fullAd.ad.setAdListener(new InterstitialAdListener() {
                private String requestId = null;
                @Override
                public void onInterstitialDisplayed(Ad ad) {
                    if (mAdListener != null) {
                        mAdListener.onAdOpen(new AdType(AdType.FACEBOOK_FULL), index);
                    }
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    if (mAdListener != null) {
                        mAdListener.onAdClosed(new AdType(AdType.FACEBOOK_FULL), index);
                    }
                    AdAppHelper.getInstance(mContext).loadNewInterstitial(index);
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAd.id, Const.ACTION_CLOSE_FULL);
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    fullAd.loaded = false;
                    fullAd.requested = false;
                    if (mAdListener != null) {
                        mAdListener.onAdLoadFailed(new AdType(AdType.FACEBOOK_FULL), index, adError.getErrorMessage());
                    }
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_ERROR, fullAd.id, adError.getErrorMessage());
                    if (AdAppHelper.getInstance(mContext).isNetworkConnected(mContext)) {
                        AdConfig config = AdAppHelper.getInstance(mContext).getConfig();
                        if (fullAd.errorCount++ < config.ad_ctrl.fb_fail_reload_count) {
                            loadNewInterstitial(index);
                        }
                    }
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (ad != fullAd.ad) {
                        return;
                    }
                    requestId = ad.getRequestId();
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAd.id, Const.ACTION_LOAD);
                    fullAd.loaded = true;
                    fullAd.requested = false;
                    fullAd.errorCount = 0;
                    fullAd.loadedTime = System.currentTimeMillis();
                    if (mAdListener != null) {
                        mAdListener.onAdLoaded(new AdType(AdType.FACEBOOK_FULL), index);
                    }
                    long cost = (System.currentTimeMillis() - fullAd.lastRequestTime) / 1000;
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD, "FB全屏加载时间", cost);
                    if (firstFullAdTime != -2) {
                        AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD, "FB第一个全屏", System.currentTimeMillis() - firstFullAdTime);
                        firstFullAdTime = -2;
                    }
                }

                @Override
                public void onAdClicked(Ad ad) {
                    if (requestId != null) {
                        FBCache.deleteFromCache(requestId, AdPlacementType.INTERSTITIAL.toString(), fullAd.id);
                    }
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAd.id, Const.ACTION_CLICK);
                    if (mAdListener != null) {
                        mAdListener.onAdClick(new AdType(AdType.FACEBOOK_FULL), index);
                    }
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    AdConfig config = AdAppHelper.getInstance(mContext).getConfig();
                    String requestId = ad.getRequestId();
                    if (requestId != null && config.ad_ctrl.reuse_cache != 1) {
                        FBCache.deleteFromCache(requestId, AdPlacementType.INTERSTITIAL.toString(), fullAd.id);
                    }
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAd.id, Const.ACTION_SHOW);
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAd.id, Const.ACTION_SHOW_FULL);
                    AdAppHelper.getInstance(mContext).getFacebook().logEvent(Const.CATEGORY_FB_AD_POSISTION, fullAd.id, Const.ACTION_SHOW_FULL);
                }
            });
        }
        try {
            fullAd.ad.loadAd();
            AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAd.id, Const.ACTION_REQUEST);
        } catch (Exception ex) {
            AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD, "错误", ex.getMessage());
        }
    }

    public void loadNewNativeAd() {
        if (mNativeAds.size() == 0) return;
        for (int i = 0; i < mNativeAds.size(); i++) {
            loadNewNativeAd(i, false);
        }
    }

    public void loadNewNativeAd(boolean autoShow) {
        if (mNativeAds.size() == 0) return;
        for (int i = 0; i < mNativeAds.size(); i++) {
            loadNewNativeAd(i, autoShow);
        }
    }

    /*
    private void loadNewNativeAdFromCache(final int index) {
        if (nativeAds == null) return;
        if (index < 0 || index >= nativeAds.length) return;

        final NativeClassAd nativeClassAd = nativeAds[index];
        if (TextUtils.isEmpty(nativeClassAd.mNativeId)) return;
        if (nativeClassAd.cacheAdLoaded) return;
        if (nativeClassAd.cacheAdRequest) return;
        if (!enableNative) return;

        nativeClassAd.cacheAdRequest = true;
        nativeClassAd.lastRequestCacheNativeTime = System.currentTimeMillis();

        nativeClassAd.cacheAd = new NativeAd(mContext, nativeClassAd.mNativeId);
        nativeClassAd.cacheAd.setUseCache(true);
        nativeClassAd.cacheAd.setAdListener(new AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                if (error.getErrorCode() == 1002) {
                }
                nativeClassAd.cacheAdRequest = false;
                nativeClassAd.cacheAdLoaded = false;
                if (mAdListener != null) {
                    mAdListener.onAdLoadFailed(new AdType(AdType.FACEBOOK_NATIVE), index, error.getErrorMessage());
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.mNativeId, Const.ACTION_CACHE_LOAD);
                nativeClassAd.cacheAdLoaded = true;
                nativeClassAd.cacheAdRequest = false;

                nativeClassAd.cacheAdView.removeAllViews();
                // Add the Ad view into the ad container.
                LayoutInflater inflater = LayoutInflater.from(mContext);
                // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
                int layoutId = R.layout.adsplugin_native_250_ad_layout;
                switch (nativeClassAd.height) {
                    case NativeAdSize.SIZE_50:
                        layoutId = R.layout.adsplugin_native_50_ad_layout;
                        break;
                    case NativeAdSize.SIZE_80:
                        layoutId = R.layout.adsplugin_native_80_ad_layout;
                        break;
                    case NativeAdSize.SIZE_150:
                        layoutId = R.layout.adsplugin_native_150_ad_layout;
                        break;
                    case NativeAdSize.SIZE_180:
                        layoutId = R.layout.adsplugin_native_180_ad_layout;
                        break;
                    case NativeAdSize.SIZE_250:
                        layoutId = R.layout.adsplugin_native_250_ad_layout;
                        break;
                    case NativeAdSize.SIZE_300:
                        layoutId = R.layout.adsplugin_native_300_ad_layout;
                        break;
                    case NativeAdSize.AUTO_HEIGHT:
                        layoutId = R.layout.adsplugin_native_match_parent_ad_layout;
                        break;
                }
                View adView = inflater.inflate(layoutId, nativeClassAd.cacheAdView, false);
                int width = nativeClassAd.width == -1 ?  ViewGroup.LayoutParams.MATCH_PARENT : (int)(nativeClassAd.width * mContext.getResources().getDisplayMetrics().density);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                nativeClassAd.cacheAdView.addView(adView, params);

                // Create native UI using the ad metadata.
                View root = adView.findViewById(R.id.ads_plugin_native_ad_unit);
                AdAppHelper helper = AdAppHelper.getInstance(mContext);
                if (helper.NATIVE_BG_COLOR_LIST != null && index < helper.NATIVE_BG_COLOR_LIST.length) {
                    root.setBackgroundColor(helper.NATIVE_BG_COLOR_LIST[index]);
                }
                boolean textColorSet = false;
                int textColor = Color.parseColor("#aaaaaa");
                if (helper.NATIVE_TEXT_COLOR_LIST != null && index < helper.NATIVE_TEXT_COLOR_LIST.length) {
                    textColorSet = true;
                    textColor = helper.NATIVE_TEXT_COLOR_LIST[index];
                }
                ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.ads_plugin_native_ad_icon);
                TextView nativeAdTitle = (TextView) adView.findViewById(R.id.ads_plugin_native_ad_title);
                MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.ads_plugin_native_ad_media);
                ImageView nativeAdCoverImage = (ImageView) adView.findViewById(R.id.ads_plugin_native_ad_cover_image);
                TextView nativeAdBody = (TextView) adView.findViewById(R.id.ads_plugin_native_ad_body);
                Button nativeAdCallToAction = (Button) adView.findViewById(R.id.ads_plugin_native_ad_call_to_action);

                // Register the Title and CTA button to listen for clicks.
                AdConfig config = AdAppHelper.getInstance(mContext).getConfig();
                int r = new Random().nextInt(100);
                if (r < config.ad_ctrl.native_click) {
                    List<View> clickableViews = new ArrayList<>();
                    if (config.ad_ctrl.only_cta == 1) {
                        clickableViews.add(nativeAdCallToAction);
                        nativeClassAd.cacheAd.registerViewForInteraction(nativeClassAd.cacheAdView, clickableViews);
                    } else {
                        nativeClassAd.cacheAd.registerViewForInteraction(nativeClassAd.cacheAdView);
                    }
                } else {
                    List<View> clickableViews = new ArrayList<>();
                    View ignore = adView.findViewById(R.id.ads_plugin_ignore_btn);
                    clickableViews.add(ignore);
                    nativeClassAd.cacheAd.registerViewForInteraction(nativeClassAd.cacheAdView, clickableViews);
                }

                // Set the Text.
                if (nativeAdTitle != null) {
                    nativeAdTitle.setText(nativeClassAd.cacheAd.getAdTitle());
                    if (textColorSet) nativeAdTitle.setTextColor(textColor);
                }
                if (nativeAdBody != null) {
                    nativeAdBody.setText(nativeClassAd.cacheAd.getAdBody());
                    if (textColorSet) nativeAdBody.setTextColor(textColor);
                }
                if (nativeAdCallToAction != null) {
                    nativeAdCallToAction.setText(nativeClassAd.cacheAd.getAdCallToAction());
                }

                // Download and display the ad icon.
                NativeAd.Image adIcon = nativeClassAd.cacheAd.getAdIcon();
                if (nativeAdIcon != null && adIcon != null) {
                    NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);
                }

                // Download and display the cover image.
                if (nativeAdMedia != null && nativeAdCoverImage != null) {
//                    NativeAd.Image coverImage = nativeClassAd.cacheAd.getAdCoverImage();
//                    if (!nativeClassAd.cacheAd.isVideoAd() && coverImage != null) {
//                        nativeAdCoverImage.setVisibility(View.VISIBLE);
//                        nativeClassAd.cacheAd.downloadAndDisplayImage(coverImage, nativeAdCoverImage);
//                        nativeAdMedia.setVisibility(View.GONE);
//                    } else {
                        nativeAdMedia.setNativeAd(nativeClassAd.cacheAd);
                        nativeAdCoverImage.setVisibility(View.GONE);
                        nativeAdMedia.setVisibility(View.VISIBLE);
//                    }
                }

                // Add the AdChoices icon
                LinearLayout adChoicesContainer = (LinearLayout) adView.findViewById(R.id.ads_plugin_ad_choices_container);
                if (adChoicesContainer != null) {
                    AdChoicesView adChoicesView = new AdChoicesView(mContext, nativeClassAd.cacheAd, true);
                    adChoicesContainer.addView(adChoicesView);
                }

                if (mAdListener != null) {
                    mAdListener.onAdLoaded(new AdType(AdType.FACEBOOK_NATIVE), index);
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                String requestId = ad.getRequestId();
                if (requestId != null) {
                    FBCache.deleteFromCache(requestId, AdPlacementType.NATIVE.toString(), nativeClassAd.mNativeId);
                }
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.mNativeId, Const.ACTION_CACHE_CLICK);
                if (mAdListener != null) {
                    mAdListener.onAdClick(new AdType(AdType.FACEBOOK_NATIVE), index);
                }
            }

            public void onLoggingImpression(Ad ad) {
                AdConfig config = AdAppHelper.getInstance(mContext).getConfig();
                String requestId = ad.getRequestId();
                if (requestId != null && config.ad_ctrl.reuse_cache != 1) {
                    FBCache.deleteFromCache(requestId, AdPlacementType.NATIVE.toString(), nativeClassAd.mNativeId);
                }
                if (mAdListener != null) {
                    mAdListener.onAdOpen(new AdType(AdType.FACEBOOK_NATIVE), index);
                }
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.mNativeId, Const.ACTION_CACHE_SHOW_NATIVE);
                AdAppHelper.getInstance(mContext).getFacebook().logEvent(Const.CATEGORY_FB_AD_POSISTION, nativeClassAd.mNativeId, Const.ACTION_CACHE_SHOW_NATIVE);
            }
        });

        nativeClassAd.cacheAd.loadAd();
        AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.mNativeId, Const.ACTION_CACHE_REQUEST);
    }
    */


    public void loadNewNativeAd(final int index, final boolean autoShow) {
//        loadNewNativeAdFromCache(index);
        if (mNativeAds.size() == 0) return;
        if (index < 0 || index >= mNativeAds.size()) return;

        final NativeClassAd nativeClassAd = mNativeAds.get(index);
        if (TextUtils.isEmpty(nativeClassAd.nativeId)) return;
        if (nativeClassAd.nativeLoaded && !autoShow) return;
        if (nativeClassAd.nativeRequest) return;
        if (!enableNative) return;

        nativeClassAd.nativeRequest = true;
        nativeClassAd.nativeImpression = false;
        nativeClassAd.lastRequestNativeTime = System.currentTimeMillis();
        nativeClassAd.nativeClicked = false;

        nativeClassAd.nativeAd = new NativeAd(mContext, nativeClassAd.nativeId);
        nativeClassAd.nativeAd.setAdListener(new AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                if (error.getErrorCode() == AdError.NO_FILL_ERROR_CODE) {

                }
                nativeClassAd.nativeRequest = false;
                nativeClassAd.nativeLoaded = false;
                if (mAdListener != null) {
                    mAdListener.onAdLoadFailed(new AdType(AdType.FACEBOOK_NATIVE), index, error.getErrorMessage());
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (autoShow) {
                    if (AdAppHelper.getInstance(mContext).allowAutoRiskNative()) {
//                        nativeClassAd.mNativeAd.setIsRisk(true);
                        AdAppHelper.getInstance(mContext).increaseRiskNativeCount();
                    }
                } else {
                    if (AdAppHelper.getInstance(mContext).allowRiskNative()) {
//                        nativeClassAd.mNativeAd.setIsRisk(true);
                        AdAppHelper.getInstance(mContext).increaseRiskNativeCount();
                    }
                }

                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_LOAD);
                nativeClassAd.nativeLoaded = true;
                nativeClassAd.nativeRequest = false;
                if (nativeClassAd.nativeAd != null) {
                    nativeClassAd.nativeAd.unregisterView();
                }

                if (nativeClassAd.nativeAdView == null) {
                    nativeClassAd.nativeAdView = new NativeAdContainer(mContext);
                }

                nativeClassAd.nativeAdView.removeAllViews();
                // Add the Ad view into the ad container.
                LayoutInflater inflater = LayoutInflater.from(mContext);
                // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
                int layoutId = R.layout.adsplugin_native_250_ad_layout;
                switch (nativeClassAd.height) {
                    case NativeAdSize.SIZE_50:
                        layoutId = R.layout.adsplugin_native_50_ad_layout;
                        break;
                    case NativeAdSize.SIZE_80:
                        layoutId = R.layout.adsplugin_native_80_ad_layout;
                        break;
                    case NativeAdSize.SIZE_150:
                        layoutId = R.layout.adsplugin_native_150_ad_layout;
                        break;
                    case NativeAdSize.SIZE_180:
                        layoutId = R.layout.adsplugin_native_180_ad_layout;
                        break;
                    case NativeAdSize.SIZE_250:
                        layoutId = R.layout.adsplugin_native_250_ad_layout;
                        break;
                    case NativeAdSize.SIZE_300:
                        layoutId = R.layout.adsplugin_native_300_ad_layout;
                        break;
                    case NativeAdSize.AUTO_HEIGHT:
                        layoutId = R.layout.adsplugin_native_match_parent_ad_layout;
                        break;
                }
                View adView = inflater.inflate(layoutId, nativeClassAd.nativeAdView, false);
                int width = nativeClassAd.width == -1 ?  ViewGroup.LayoutParams.MATCH_PARENT : (int)(nativeClassAd.width * mContext.getResources().getDisplayMetrics().density);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                nativeClassAd.nativeAdView.addView(adView, params);

                // Create native UI using the ad metadata.
                View root = adView.findViewById(R.id.ads_plugin_native_ad_unit);
                AdAppHelper helper = AdAppHelper.getInstance(mContext);
                if (helper.NATIVE_BG_COLOR_LIST != null && index < helper.NATIVE_BG_COLOR_LIST.length) {
                    root.setBackgroundColor(helper.NATIVE_BG_COLOR_LIST[index]);
                }
                boolean textColorSet = false;
                int textColor = Color.parseColor("#aaaaaa");
                if (helper.NATIVE_TEXT_COLOR_LIST != null && index < helper.NATIVE_TEXT_COLOR_LIST.length) {
                    textColorSet = true;
                    textColor = helper.NATIVE_TEXT_COLOR_LIST[index];
                }
                ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.ads_plugin_native_ad_icon);
                TextView nativeAdTitle = (TextView) adView.findViewById(R.id.ads_plugin_native_ad_title);
                MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.ads_plugin_native_ad_media);
                ImageView nativeAdCoverImage = (ImageView) adView.findViewById(R.id.ads_plugin_native_ad_cover_image);
                TextView nativeAdBody = (TextView) adView.findViewById(R.id.ads_plugin_native_ad_body);
                Button nativeAdCallToAction = (Button) adView.findViewById(R.id.ads_plugin_native_ad_call_to_action);

                // Register the Title and CTA button to listen for clicks.
                AdConfig config = AdAppHelper.getInstance(mContext).getConfig();
                int r = new Random().nextInt(100);
                if (r < config.ad_ctrl.native_click) {
                    List<View> clickableViews = new ArrayList<>();
                    if (config.ad_ctrl.only_cta == 1) {
                        clickableViews.add(nativeAdCallToAction);
                        nativeClassAd.nativeAd.registerViewForInteraction(nativeClassAd.nativeAdView, clickableViews);
                    } else {
                        nativeClassAd.nativeAd.registerViewForInteraction(nativeClassAd.nativeAdView);
                    }
                } else {
                    List<View> clickableViews = new ArrayList<>();
                    View ignore = adView.findViewById(R.id.ads_plugin_ignore_btn);
                    clickableViews.add(ignore);
                    nativeClassAd.nativeAd.registerViewForInteraction(nativeClassAd.nativeAdView, clickableViews);
                }

                // Set the Text.
                if (nativeAdTitle != null) {
                    nativeAdTitle.setText(nativeClassAd.nativeAd.getAdTitle());
                    if (textColorSet) nativeAdTitle.setTextColor(textColor);
                }
                if (nativeAdBody != null) {
                    String body = nativeClassAd.nativeAd.getAdBody();
                    if (body == null || "null".equals(body)) {
                        body = nativeClassAd.nativeAd.getAdSubtitle();
                    }
                    if (body != null && !body.equals("null"))
                        nativeAdBody.setText(body);
                    if (textColorSet) nativeAdBody.setTextColor(textColor);
                }
                if (nativeAdCallToAction != null) {
                    nativeAdCallToAction.setText(nativeClassAd.nativeAd.getAdCallToAction());
                }

                // Download and display the ad icon.
                NativeAd.Image adIcon = nativeClassAd.nativeAd.getAdIcon();
                if (nativeAdIcon != null && adIcon != null) {
                    NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);
                }

                if (nativeAdMedia != null && nativeAdCoverImage != null) {
//                    NativeAd.Image coverImage = nativeClassAd.nativeAd.getAdCoverImage();
//                    if (!nativeClassAd.nativeAd.isVideoAd() && coverImage != null) {
//                        nativeAdCoverImage.setVisibility(View.VISIBLE);
//                        nativeClassAd.nativeAd.downloadAndDisplayImage(coverImage, nativeAdCoverImage);
//                        nativeAdMedia.setVisibility(View.GONE);
//                    } else {
                        nativeAdMedia.setVisibility(View.VISIBLE);
                        nativeAdMedia.setNativeAd(nativeClassAd.nativeAd);
                        nativeAdCoverImage.setVisibility(View.GONE);
//                    }
                }

                // Add the AdChoices icon
                LinearLayout adChoicesContainer = (LinearLayout) adView.findViewById(R.id.ads_plugin_ad_choices_container);
                if (adChoicesContainer != null) {
                    AdChoicesView adChoicesView = new AdChoicesView(mContext, nativeClassAd.nativeAd, true);
                    adChoicesContainer.addView(adChoicesView);
                }

                if (mAdListener != null) {
                    mAdListener.onAdLoaded(new AdType(AdType.FACEBOOK_NATIVE), index);
                }
                if (autoShow) {
                    nativeClassAd.nativeAd.setAutoShow();
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                String requestId = ad.getRequestId();
                if (requestId != null) {
                    FBCache.deleteFromCache(requestId, AdPlacementType.NATIVE.toString(), nativeClassAd.nativeId);
                }
                nativeClassAd.nativeClicked = true;
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_CLICK);
                if (mAdListener != null) {
                    mAdListener.onAdClick(new AdType(AdType.FACEBOOK_NATIVE), index);
                }
            }

            public void onLoggingImpression(Ad ad) {
                nativeClassAd.nativeImpression = true;
                AdConfig config = AdAppHelper.getInstance(mContext).getConfig();
                String requestId = ad.getRequestId();
                if (requestId != null && config.ad_ctrl.reuse_cache != 1) {
                    FBCache.deleteFromCache(requestId, AdPlacementType.NATIVE.toString(), nativeClassAd.nativeId);
                }
                if (mAdListener != null) {
                    mAdListener.onAdOpen(new AdType(AdType.FACEBOOK_NATIVE), index);
                }
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_SHOW_NATIVE);
                AdAppHelper.getInstance(mContext).getFacebook().logEvent(Const.CATEGORY_FB_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_SHOW_NATIVE);
                if (nativeClassAd.nativeAd.isAutoShow()) {
                    nativeClassAd.nativeLoaded = false;
                    nativeClassAd.nativeRequest = false;
                    SharedPreferences sp = mContext.getSharedPreferences(AdAppHelper.SHARED_SP_NAME, 0);
                    int ngsCount = sp.getInt("curr_native_auto_load_count", 0);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("curr_native_auto_load_count", ++ngsCount);
                    editor.commit();
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_AUTO_SHOW);
                }
            }
        });

        nativeClassAd.nativeAd.loadAd();
        AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_REQUEST);
    }

    public void loadNewFBNBanner() {
        if (fbnBannerAds == null) return;
        for (int i = 0; i < fbnBannerAds.length; i++) {
            loadNewFBNBanner(i);
        }
    }

    public void loadNewFBNBanner(final int index) {
        if (fbnBannerAds == null) return;
        if (index < 0 || index >= fbnBannerAds.length) return;

        final NativeClassAd nativeClassAd = fbnBannerAds[index];
        if (TextUtils.isEmpty(nativeClassAd.nativeId)) return;
        if (nativeClassAd.nativeLoaded) return;
        if (nativeClassAd.nativeRequest) return;
        if (!enableFBNBanner) return;

        nativeClassAd.nativeRequest = true;
        nativeClassAd.lastRequestNativeTime = System.currentTimeMillis();
        nativeClassAd.nativeClicked = false;

        nativeClassAd.nativeAd = new NativeAd(mContext, nativeClassAd.nativeId);
        nativeClassAd.nativeAd.setAdListener(new AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                if (error.getErrorCode() == 1002) {
                }
                nativeClassAd.nativeRequest = false;
                nativeClassAd.nativeLoaded = false;
                if (mAdListener != null) {
                    mAdListener.onAdLoadFailed(new AdType(AdType.FACEBOOK_FBN_BANNER), index, error.getErrorMessage());
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_LOAD);
                // Ad loaded callback
                nativeClassAd.nativeLoaded = true;
                nativeClassAd.nativeRequest = false;
                if (nativeClassAd.nativeAd != null) {
                    nativeClassAd.nativeAd.unregisterView();
                }

                nativeClassAd.nativeAdView.removeAllViews();
                // Add the Ad view into the ad container.
                LayoutInflater inflater = LayoutInflater.from(mContext);
                // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
                View adView = inflater.inflate(R.layout.adsplugin_native_50_ad_layout, nativeClassAd.nativeAdView, false);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                nativeClassAd.nativeAdView.addView(adView, params);

                // Create native UI using the ad metadata.
                ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.ads_plugin_native_ad_icon);
                TextView nativeAdTitle = (TextView) adView.findViewById(R.id.ads_plugin_native_ad_title);
                TextView nativeAdBody = (TextView) adView.findViewById(R.id.ads_plugin_native_ad_body);
                Button nativeAdCallToAction = (Button) adView.findViewById(R.id.ads_plugin_native_ad_call_to_action);

                // Register the Title and CTA button to listen for clicks.
                AdConfig config = AdAppHelper.getInstance(mContext).getConfig();
                int r = new Random().nextInt(100);
                if (r < config.ad_ctrl.banner_click) {
                    List<View> clickableViews = new ArrayList<>();
                    if (config.ad_ctrl.only_cta == 1) {
                        clickableViews.add(nativeAdCallToAction);
                        nativeClassAd.nativeAd.registerViewForInteraction(nativeClassAd.nativeAdView, clickableViews);
                    } else {
                        nativeClassAd.nativeAd.registerViewForInteraction(nativeClassAd.nativeAdView);
                    }
                } else {
                    List<View> clickableViews = new ArrayList<>();
                    View ignore = adView.findViewById(R.id.ads_plugin_ignore_btn);
                    clickableViews.add(ignore);
                    nativeClassAd.nativeAd.registerViewForInteraction(nativeClassAd.nativeAdView, clickableViews);
                }

                // Set the Text.
                if (nativeAdTitle != null)
                    nativeAdTitle.setText(nativeClassAd.nativeAd.getAdTitle());
                if (nativeAdBody != null) nativeAdBody.setText(nativeClassAd.nativeAd.getAdBody());
                if (nativeAdCallToAction != null)
                    nativeAdCallToAction.setText(nativeClassAd.nativeAd.getAdCallToAction());

                // Download and display the ad icon.
                NativeAd.Image adIcon = nativeClassAd.nativeAd.getAdIcon();
                if (nativeAdIcon != null && adIcon != null) NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);

                // Add the AdChoices icon
                LinearLayout adChoicesContainer = (LinearLayout) adView.findViewById(R.id.ads_plugin_ad_choices_container);
                if (adChoicesContainer != null) {
                    AdChoicesView adChoicesView = new AdChoicesView(mContext, nativeClassAd.nativeAd, true);
                    adChoicesContainer.addView(adChoicesView);
                }

                if (mAdListener != null) {
                    mAdListener.onAdLoaded(new AdType(AdType.FACEBOOK_FBN_BANNER), index);
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                String requestId = ad.getRequestId();
                if (requestId != null) {
                    FBCache.deleteFromCache(requestId, AdPlacementType.NATIVE.toString(), nativeClassAd.nativeId);
                }
                nativeClassAd.nativeClicked = true;
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_CLICK);
                if (mAdListener != null) {
                    mAdListener.onAdClick(new AdType(AdType.FACEBOOK_FBN_BANNER), index);
                }
            }

            public void onLoggingImpression(Ad ad) {
                AdConfig config = AdAppHelper.getInstance(mContext).getConfig();
                String requestId = ad.getRequestId();
                if (requestId != null && config.ad_ctrl.reuse_cache != 1) {
                    FBCache.deleteFromCache(requestId, AdPlacementType.NATIVE.toString(), nativeClassAd.nativeId);
                }
                if (mAdListener != null) {
                    mAdListener.onAdOpen(new AdType(AdType.FACEBOOK_FBN_BANNER), index);
                }
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_SHOW_BANNER);
                AdAppHelper.getInstance(mContext).getFacebook().logEvent(Const.CATEGORY_FB_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_SHOW_BANNER);
            }
        });

        nativeClassAd.nativeAd.loadAd();
        AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_REQUEST);
    }

    public void loadNewFBNAd() {
        if (fbnFullAds == null) return;
        for (int i = 0; i < fbnFullAds.length; i++) {
            loadNewFBNAd(i, false);
        }
    }

    public void loadNewFBNAd(boolean autoShow) {
        if (fbnFullAds == null) return;
        for (int i = 0; i < fbnFullAds.length; i++) {
            loadNewFBNAd(i, autoShow);
        }
    }

    private void loadNewFBNAdFromCache(final int index) {
        if (fbnFullAds == null) return;
        if (index < 0 || index >= fbnFullAds.length) return;

        final NativeClassAd nativeClassAd = fbnFullAds[index];
        if (TextUtils.isEmpty(nativeClassAd.nativeId)) return;
        if (nativeClassAd.cacheAdRequest) return;
        if (nativeClassAd.cacheAdLoaded) return;
        if (!enableFBN) return;

        nativeClassAd.cacheAdRequest = true;

        nativeClassAd.cacheAd = new NativeAd(mContext, nativeClassAd.nativeId);
        nativeClassAd.cacheAd.setUseCache(true);
        nativeClassAd.cacheAd.setAdListener(new AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                if (error.getErrorCode() == 1002) {
                }
                nativeClassAd.cacheAdRequest = false;
                nativeClassAd.cacheAdLoaded = false;
                if (mAdListener != null) {
                    mAdListener.onAdLoadFailed(new AdType(AdType.FACEBOOK_FBN), 0, error.getErrorMessage());
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_CACHE_LOAD);
                // Ad loaded callback
                nativeClassAd.cacheAdLoaded = true;
                nativeClassAd.cacheAdRequest = false;

                if (mAdListener != null) {
                    mAdListener.onAdLoaded(new AdType(AdType.FACEBOOK_FBN), index);
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_CACHE_CLICK);
            }

            public void onLoggingImpression(Ad ad) {
                String requestId = ad.getRequestId();
                AdConfig config = AdAppHelper.getInstance(mContext).getConfig();
                if (requestId != null && config.ad_ctrl.reuse_cache != 1) {
                    FBCache.deleteFromCache(requestId, AdPlacementType.NATIVE.toString(), nativeClassAd.nativeId);
                }
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_CACHE_SHOW_FULL);
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_CACHE_SHOW);
                AdAppHelper.getInstance(mContext).getFacebook().logEvent(Const.CATEGORY_FB_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_CACHE_SHOW_FULL);
            }
        });

        nativeClassAd.cacheAd.loadAd();
        AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_CACHE_REQUEST);
    }

    public void loadNewFBNAd(final int index, final boolean autoShow) {
        loadNewFBNAdFromCache(index);
        if (fbnFullAds == null) return;
        if (index < 0 || index >= fbnFullAds.length) return;

        final NativeClassAd nativeClassAd = fbnFullAds[index];
        if (TextUtils.isEmpty(nativeClassAd.nativeId)) return;
        if (nativeClassAd.nativeLoaded && !autoShow && (System.currentTimeMillis() - nativeClassAd.lastRequestNativeTime) < AdAppHelper.MAX_AD_ALIVE_TIME) return;
        if (nativeClassAd.nativeRequest) return;
        if (!enableFBN) return;

        nativeClassAd.nativeRequest = true;
        nativeClassAd.lastRequestNativeTime = System.currentTimeMillis();

        nativeClassAd.nativeAd = new NativeAd(mContext, nativeClassAd.nativeId);
        nativeClassAd.nativeAd.setAdListener(new AdListener() {

            @Override
            public void onError(Ad ad, AdError error) {
                if (error.getErrorCode() == 1002) {
                }
                nativeClassAd.nativeRequest = false;
                nativeClassAd.nativeLoaded = false;
                if (mAdListener != null) {
                    mAdListener.onAdLoadFailed(new AdType(AdType.FACEBOOK_FBN), 0, error.getErrorMessage());
                }
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_ERROR, nativeClassAd.nativeId, error.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_LOAD);
                // Ad loaded callback
                nativeClassAd.nativeLoaded = true;
                nativeClassAd.nativeRequest = false;

                if (mAdListener != null) {
                    mAdListener.onAdLoaded(new AdType(AdType.FACEBOOK_FBN), index);
                }
                if (autoShow) {
                    if (nativeClassAd.nativeAdView == null) {
                        nativeClassAd.nativeAdView = new NativeAdContainer(mContext);
                    }
                    if (AdAppHelper.getInstance(mContext).allowAutoRisk()) {
//                        nativeClassAd.mNativeAd.setIsRisk(true);
                        AdAppHelper.getInstance(mContext).increaseRiskCount();
                    }
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_AUTO_LOAD);

                    nativeClassAd.nativeAdView.removeAllViews();
                    // Add the Ad view into the ad container.
                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
                    int layoutId = R.layout.adsplugin_native_250_ad_layout;
                    View adView = inflater.inflate(layoutId, nativeClassAd.nativeAdView, false);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                    nativeClassAd.nativeAdView.addView(adView, params);

                    // Create native UI using the ad metadata.
                    ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.ads_plugin_native_ad_icon);
                    TextView nativeAdTitle = (TextView) adView.findViewById(R.id.ads_plugin_native_ad_title);
                    MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.ads_plugin_native_ad_media);
                    TextView nativeAdBody = (TextView) adView.findViewById(R.id.ads_plugin_native_ad_body);
                    Button nativeAdCallToAction = (Button) adView.findViewById(R.id.ads_plugin_native_ad_call_to_action);

                    // Register the Title and CTA button to listen for clicks.
                    nativeClassAd.nativeAd.registerViewForInteraction(nativeClassAd.nativeAdView);

                    // Download and display the ad icon.
                    NativeAd.Image adIcon = nativeClassAd.nativeAd.getAdIcon();
                    if (nativeAdIcon != null && adIcon != null) {
                        NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);
                    }

                    // Download and display the cover image.
                    if (nativeAdMedia != null) {
//                    NativeAd.downloadAndDisplayImage(adCoverImage, nativeAdMedia);
                        nativeAdMedia.setNativeAd(nativeClassAd.nativeAd);
                    }

                    if (autoShow) {
                        nativeClassAd.nativeAd.setAutoShow();
                    }
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_CLICK);
            }

            public void onLoggingImpression(Ad ad) {
                String requestId = ad.getRequestId();
                AdConfig config = AdAppHelper.getInstance(mContext).getConfig();
                if (requestId != null && config.ad_ctrl.reuse_cache != 1) {
                    FBCache.deleteFromCache(requestId, AdPlacementType.NATIVE.toString(), nativeClassAd.nativeId);
                }
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_SHOW);
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_SHOW_FULL);
                AdAppHelper.getInstance(mContext).getFacebook().logEvent(Const.CATEGORY_FB_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_SHOW_FULL);
                if (nativeClassAd.nativeAd.isAutoShow()) {
                    nativeClassAd.nativeLoaded = false;
                    nativeClassAd.nativeRequest = false;
                    SharedPreferences sp = mContext.getSharedPreferences(AdAppHelper.SHARED_SP_NAME, 0);
                    int ngsCount = sp.getInt("curr_ngs_auto_load_count", 0);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("curr_ngs_auto_load_count", ++ngsCount);
                    editor.commit();
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_AUTO_SHOW);
                }
            }
        });

        nativeClassAd.nativeAd.loadAd();
        AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_REQUEST);
        if (autoShow) {
            AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.nativeId, Const.ACTION_AUTO_REQUEST);
        }
    }
}
