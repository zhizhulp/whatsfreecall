package com.bestgo.adsplugin.ads;


import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bestgo.adsplugin.R;
import com.bestgo.adsplugin.ads.listener.AdStateListener;
import com.bestgo.adsplugin.ads.listener.RewardedListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAdView;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.ArrayList;
import java.util.List;

public class AdMobAd {
    private Context mContext;

    private boolean enableBanner;
    private boolean enableNative;
    private boolean enableNativeEN;
    private boolean enableNativeAN;
    private boolean enableInterstitial;
    private boolean enableVideo;

    private long firstFullAdTime = -1;

    private AdStateListener mAdListener;
    private RewardedListener mRewardedListener;

    private String mVideoAdId;
    private AdMobInterstitialAd[] fullAds;
    private NativeClassAd[] nativeAds;
    private NativeANClassAd[] nativeANAds;
    private BannerClassAd[] bannerAds;

    private RewardedVideoAd mRewardedVideoAd;
    private boolean videoRequest;
    private boolean videoLoaded;
    private boolean videoRewarded;
    private boolean videoClicked;

    private class AdMobInterstitialAd {
        public InterstitialAd ad;
        public String id;
        public boolean requested;
        public boolean loaded;
        public long lastRequestTime;
        public long loadedTime;
        public int errorCount;
    }

    private class BannerClassAd {
        private AdView mBannerView;
        private String mBannerId;
        private boolean bannerRequest;
        private boolean bannerLoaded;
        private int errorCount;
        private long lastBannerRequestTime;
        private boolean pendingRefresh;
    }

    private class NativeANClassAd {
        public NativeAdView mNativeANAdView;
        private String mNativeANId;
        private boolean nativeANLoaded;
        private boolean nativeImpression;
        private boolean nativeClicked;
        private boolean nativeANRequest;
        private long lastRequestNativeANTime;
        private boolean pendingRefreshAN;
        private int width;
        private int height;
    }

    private class NativeClassAd {
        private AdView mNativeAdView;
        public NativeExpressAdView mNativeENAdView;
        private String mNativeId;
        private String mNativeENId;
        private boolean nativeLoaded;
        private boolean nativeENLoaded;
        private boolean nativeRequest;
        private boolean nativeENRequest;
        private long lastRequestNativeENTime;
        private long lastRequestNativeEime;
        private int errorCount;
        private int width;
        private int height;
        private boolean pendingRefresh;
    }

    public AdMobAd(Context context) {
        this.mContext = context;
        AdConfig config = AdAppHelper.getInstance(context).getConfig();
        if (config.admob_banner_ids.count > 0 && config.admob_banner_ids.ids != null) {
            bannerAds = new BannerClassAd[config.admob_banner_ids.count];
            for (int i = 0; i < bannerAds.length; i++) {
                bannerAds[i] = new BannerClassAd();
                if (config.admob_banner_ids.count > 0 && config.admob_banner_ids != null && i < config.admob_banner_ids.count) {
                    bannerAds[i].mBannerId = config.admob_banner_ids.ids[i].id;
                }
            }
        }
        if (config.admob_full_ids.count > 0 && config.admob_full_ids.ids != null) {
            fullAds = new AdMobInterstitialAd[config.admob_full_ids.count];
            for (int i = 0; i < fullAds.length; i++) {
                fullAds[i] = new AdMobInterstitialAd();
                fullAds[i].id = config.admob_full_ids.ids[i].id;
            }
        }
        if (config.admob_native_ids.count > 0 && config.admob_native_ids.ids != null) {
            nativeAds = new NativeClassAd[config.admob_native_ids.count];
            for (int i = 0; i < nativeAds.length; i++) {
                nativeAds[i] = new NativeClassAd();
                if (config.admob_banner_native_ids.count > 0 && config.admob_banner_native_ids != null && i < config.admob_banner_native_ids.count) {
                    nativeAds[i].mNativeId = config.admob_banner_native_ids.ids[i].id;
                } else if (bannerAds != null && bannerAds.length > i) {
                    nativeAds[i].mNativeId = bannerAds[i].mBannerId;
                }
                nativeAds[i].mNativeENId = config.admob_native_ids.ids[i].id;
                nativeAds[i].width = config.admob_native_ids.ids[i].width > 0 ? config.admob_native_ids.ids[i].width : -1;
                nativeAds[i].height = config.admob_native_ids.ids[i].height;
            }
        }
        if (config.admob_an_ids.count > 0 && config.admob_an_ids.ids != null) {
            nativeANAds = new NativeANClassAd[config.admob_an_ids.count];
            for (int i = 0; i < nativeANAds.length; i++) {
                nativeANAds[i] = new NativeANClassAd();
                nativeANAds[i].mNativeANId = config.admob_an_ids.ids[i].id;
                nativeANAds[i].width = config.admob_an_ids.ids[i].width > 0 ? config.admob_an_ids.ids[i].width : -1;
                nativeANAds[i].height = config.admob_an_ids.ids[i].height;
            }
        }
        mVideoAdId = config.video_ids.admob;
    }

    public void resetId() {
        AdConfig config = AdAppHelper.getInstance(mContext).getConfig();
        if (config.admob_banner_ids.count > 0 && config.admob_banner_ids.ids != null) {
            if (bannerAds == null || bannerAds.length != config.admob_banner_ids.count) {
                bannerAds = new BannerClassAd[config.admob_banner_ids.count];
                for (int i = 0; i < bannerAds.length; i++) {
                    bannerAds[i] = new BannerClassAd();
                    if (config.admob_banner_ids.count > 0 && config.admob_banner_ids != null && i < config.admob_banner_ids.count) {
                        bannerAds[i].mBannerId = config.admob_banner_ids.ids[i].id;
                    }
                }
            }
        }
        if (config.admob_full_ids.count > 0 && config.admob_full_ids.ids != null) {
            if (fullAds == null || fullAds.length != config.admob_full_ids.count) {
                fullAds = new AdMobInterstitialAd[config.admob_full_ids.count];
                for (int i = 0; i < fullAds.length; i++) {
                    fullAds[i] = new AdMobInterstitialAd();
                    fullAds[i].id = config.admob_full_ids.ids[i].id;
                }
            } else {
                for (int i = 0; i < fullAds.length; i++) {
                    if (!fullAds[i].id.equals(config.admob_full_ids.ids[i].id)) {
                        fullAds[i] = new AdMobInterstitialAd();
                        fullAds[i].id = config.admob_full_ids.ids[i].id;
                    }
                }
            }
        }
        if (config.admob_native_ids.count > 0 && config.admob_native_ids.ids != null) {
            if (nativeAds == null || nativeAds.length != config.admob_native_ids.count) {
                nativeAds = new NativeClassAd[config.admob_native_ids.count];
                for (int i = 0; i < nativeAds.length; i++) {
                    nativeAds[i] = new NativeClassAd();
                    if (config.admob_banner_native_ids.count > 0 && config.admob_banner_native_ids != null && i < config.admob_banner_native_ids.count) {
                        nativeAds[i].mNativeId = config.admob_banner_native_ids.ids[i].id;
                    } else if (bannerAds != null && bannerAds.length > i) {
                        nativeAds[i].mNativeId = bannerAds[i].mBannerId;
                    }
                    nativeAds[i].mNativeENId = config.admob_native_ids.ids[i].id;
                    nativeAds[i].width = config.admob_native_ids.ids[i].width > 0 ? config.admob_native_ids.ids[i].width : -1;
                    nativeAds[i].height = config.admob_native_ids.ids[i].height;
                }
            } else {
                for (int i = 0; i < nativeAds.length; i++) {
                    if (!nativeAds[i].mNativeENId.equals(config.admob_native_ids.ids[i].id)) {
                        nativeAds[i] = new NativeClassAd();
                        if (config.admob_banner_native_ids.count > 0 && config.admob_banner_native_ids != null && i < config.admob_banner_native_ids.count) {
                            nativeAds[i].mNativeId = config.admob_banner_native_ids.ids[i].id;
                        } else if (bannerAds != null && bannerAds.length > i) {
                            nativeAds[i].mNativeId = bannerAds[i].mBannerId;
                        }
                        nativeAds[i].mNativeENId = config.admob_native_ids.ids[i].id;
                        nativeAds[i].width = config.admob_native_ids.ids[i].width > 0 ? config.admob_native_ids.ids[i].width : -1;
                        nativeAds[i].height = config.admob_native_ids.ids[i].height;
                    }
                }
            }
        }
        if (config.admob_an_ids.count > 0 && config.admob_an_ids.ids != null) {
            if (nativeANAds == null || nativeANAds.length != config.admob_an_ids.count) {
                nativeANAds = new NativeANClassAd[config.admob_an_ids.count];
                for (int i = 0; i < nativeANAds.length; i++) {
                    nativeANAds[i] = new NativeANClassAd();
                    nativeANAds[i].mNativeANId = config.admob_an_ids.ids[i].id;
                    nativeANAds[i].width = config.admob_an_ids.ids[i].width > 0 ? config.admob_an_ids.ids[i].width : -1;
                    nativeANAds[i].height = config.admob_an_ids.ids[i].height;
                }
            } else {
                for (int i = 0; i < nativeANAds.length; i++) {
                    if (!nativeANAds[i].mNativeANId.equals(config.admob_an_ids.ids[i].id)) {
                        nativeANAds[i] = new NativeANClassAd();
                        nativeANAds[i].mNativeANId = config.admob_an_ids.ids[i].id;
                        nativeANAds[i].width = config.admob_an_ids.ids[i].width > 0 ? config.admob_an_ids.ids[i].width : -1;
                        nativeANAds[i].height = config.admob_an_ids.ids[i].height;
                    }
                }
            }
        }
        mVideoAdId = config.video_ids.admob;
    }

    public void setAdListener(AdStateListener listener) {
        this.mAdListener = listener;
    }
    public void setRewardedListener(RewardedListener listener) {
        mRewardedListener = listener;
    }

    public void onResume() {
//        if (mBannerView != null) {
//            mBannerView.resume();
//        }
//        if (nativeAds != null) {
//            for (int i = 0; i < nativeAds.length; i++) {
//                if (nativeAds[i].mNativeAdView != null) {
//                    nativeAds[i].mNativeAdView.resume();
//                }
//                if (nativeAds[i].mNativeENAdView != null) {
//                    nativeAds[i].mNativeENAdView.resume();
//                }
//            }
//        }
    }

    public void onPause() {
//        if (mBannerView != null) {
//            mBannerView.pause();
//        }
//        if (nativeAds != null) {
//            for (int i = 0; i < nativeAds.length; i++) {
//                if (nativeAds[i].mNativeAdView != null) {
//                    nativeAds[i].mNativeAdView.pause();
//                }
//                if (nativeAds[i].mNativeENAdView != null) {
//                    nativeAds[i].mNativeENAdView.pause();
//                }
//            }
//        }
    }

    public void setVideoEnabled(boolean flag) {
        enableVideo = flag;
    }

    public void setBannerEnabled(boolean flag) {
        enableBanner = flag;
    }

    public void setNativeEnabled(boolean flag) {
        enableNative = flag;
    }

    public void setNativeENEnabled(boolean flag) {
        enableNativeEN = flag;
    }

    public void setNativeANEnabled(boolean flag) {
        enableNativeAN = flag;
    }

    public void setInterstitialEnabled(boolean flag) {
        enableInterstitial = flag;
    }

    public boolean isVideoLoaded() {
        return videoLoaded;
    }

    public boolean isBannerLoaded() {
        for (int i = 0; bannerAds != null && i < bannerAds.length; i++) {
            if (bannerAds[i].bannerLoaded) {
                return true;
            }
        }
        return false;
    }

    public boolean isBannerLoaded(int index) {
        if (bannerAds == null) return false;
        if (index >= bannerAds.length || index < 0) return false;
        return bannerAds[index].bannerLoaded;
    }

    public boolean isNativeLoaded() {
        for (int i = 0; nativeAds != null && i < nativeAds.length; i++) {
            if (nativeAds[i].nativeLoaded) {
                return true;
            }
        }
        return false;
    }

    public boolean isNativeLoaded(int index) {
        if (nativeAds == null) return false;
        if (index >= nativeAds.length || index < 0) return false;
        return nativeAds[index].nativeLoaded;
    }

    public boolean isNativeENLoaded() {
        for (int i = 0; nativeAds != null && i < nativeAds.length; i++) {
            if (nativeAds[i].nativeENLoaded) {
                return true;
            }
        }
        return false;
    }

    public boolean isNativeENLoaded(int index) {
        if (nativeAds == null) return false;
        if (index >= nativeAds.length || index < 0) return false;
        return nativeAds[index].nativeENLoaded;
    }

    public boolean isNativeANLoaded(int index) {
        if (nativeANAds == null) return false;
        if (index >= nativeANAds.length || index < 0) return false;
        return nativeANAds[index].nativeANLoaded;
    }

    public boolean isNativeANLoaded() {
        for (int i = 0; nativeANAds != null && i < nativeANAds.length; i++) {
            if (nativeANAds[i].nativeANLoaded) {
                return true;
            }
        }
        return false;
    }

    public boolean isInterstitialLoaded() {
        for (int i = 0; fullAds != null && i < fullAds.length; i++) {
            if (fullAds[i].loaded && (System.currentTimeMillis() - fullAds[i].loadedTime) < AdAppHelper.MAX_AD_ALIVE_TIME) {
                return true;
            }
        }
        return false;
    }

    public boolean isInterstitialLoaded(int index) {
        if (fullAds == null) return false;
        if (index >= fullAds.length || index < 0) return false;
        return fullAds[index].loaded && (System.currentTimeMillis() - fullAds[index].loadedTime) < AdAppHelper.MAX_AD_ALIVE_TIME;
    }

    public View getBanner() {
        for (int i = 0; bannerAds != null && i < bannerAds.length; i++) {
            if (bannerAds[i].bannerLoaded) {
                return bannerAds[i].mBannerView;
            }
        }
        return null;
    }
    public View getBanner(int index) {
        for (int i = 0; bannerAds != null && i < bannerAds.length; i++) {
            if (index != i) continue;
            if (bannerAds[i].bannerLoaded) {
                return bannerAds[i].mBannerView;
            }
        }
        return null;
    }

    public View getNative() {
        for (int i = 0; nativeAds != null && i < nativeAds.length; i++) {
            return getNative(i);
        }
        return null;
    }

    public View getNativeEN() {
        if (nativeAds == null) return null;
        for (int i = 0; i < nativeAds.length; i++) {
            if (nativeAds[i].nativeENLoaded) {
                final NativeClassAd nativeClassAd = nativeAds[i];
                if (!nativeClassAd.pendingRefresh) {
                    nativeClassAd.pendingRefresh = true;
                    //去掉手动刷新控制
                    nativeClassAd.mNativeENAdView.postDelayed(new ReloadNativeTask(nativeClassAd, i), 30000);
                }
                return nativeClassAd.mNativeENAdView;
            }
        }
        return null;
    }

    public View getNativeAN() {
        if (nativeANAds == null) return null;
        for (int i = 0; i < nativeANAds.length; i++) {
            if (nativeANAds[i].nativeANLoaded) {
                final NativeANClassAd nativeClassAd = nativeANAds[i];
                if (!nativeClassAd.pendingRefreshAN) {
                    nativeClassAd.pendingRefreshAN = true;
                    //去掉手动刷新控制
                    nativeClassAd.mNativeANAdView.postDelayed(new ReloadNativeANTask(nativeClassAd, i), 30000);
                }
                return nativeClassAd.mNativeANAdView;
            }
        }
        return null;
    }

    private class ReloadNativeTask implements Runnable {
        private NativeClassAd nativeClassAd;
        private int index;

        public ReloadNativeTask(NativeClassAd ad, int index) {
            nativeClassAd = ad;
            this.index = index;
        }

        @Override
        public void run() {
            if (nativeClassAd.mNativeENAdView == null) return;
            if (AdAppHelper.getInstance(mContext).isScreenOn() && nativeClassAd.mNativeENAdView.isShown() &&
                    (System.currentTimeMillis() - nativeClassAd.lastRequestNativeENTime) >= AdAppHelper.NATIVE_REFRESH_TIME) {
                nativeClassAd.nativeLoaded = false;
                nativeClassAd.nativeRequest = false;
                nativeClassAd.pendingRefresh = false;
                loadNewNativeENAd(index);
                nativeClassAd.mNativeENAdView.postDelayed(new ReloadNativeTask(nativeClassAd, index), 1000);
            } else if (nativeClassAd.pendingRefresh) {
                nativeClassAd.mNativeENAdView.postDelayed(new ReloadNativeTask(nativeClassAd, index), 1000);
            }
        }
    }

    private class ReloadNativeANTask implements Runnable {
        private NativeANClassAd nativeClassAd;
        private int index;

        public ReloadNativeANTask(NativeANClassAd ad, int index) {
            nativeClassAd = ad;
            this.index = index;
        }

        @Override
        public void run() {
            if (nativeClassAd.mNativeANAdView == null) return;
            if (AdAppHelper.getInstance(mContext).isScreenOn() && nativeClassAd.mNativeANAdView.isShown() &&
                    ((System.currentTimeMillis() - nativeClassAd.lastRequestNativeANTime) >= AdAppHelper.NATIVE_REFRESH_TIME && nativeClassAd.nativeImpression) || nativeClassAd.nativeClicked) {
                nativeClassAd.nativeANLoaded = false;
                nativeClassAd.nativeImpression = false;
                nativeClassAd.nativeANRequest = false;
                nativeClassAd.pendingRefreshAN = false;
                loadNewNativeANAd(index);
                nativeClassAd.mNativeANAdView.postDelayed(new ReloadNativeANTask(nativeClassAd, index), 1000);
            } else if (nativeClassAd.pendingRefreshAN) {
                nativeClassAd.mNativeANAdView.postDelayed(new ReloadNativeANTask(nativeClassAd, index), 1000);
            }
        }
    }

    public View getNative(int index) {
        if (nativeAds == null) return null;
        if (index >= nativeAds.length || index < 0) return null;
        return nativeAds[index].mNativeAdView;
    }

    public View getNativeEN(final int index) {
        if (nativeAds == null) return null;
        if (index >= nativeAds.length || index < 0) return null;
        final NativeClassAd nativeClassAd = nativeAds[index];
        if (!nativeClassAd.pendingRefresh && nativeClassAd.mNativeENAdView != null) {
            nativeClassAd.pendingRefresh = true;
            //去掉手动刷新控制
            nativeClassAd.mNativeENAdView.postDelayed(new ReloadNativeTask(nativeClassAd, index), 30000);
        }
        return nativeClassAd.mNativeENAdView;
    }

    public View getNativeAN(final int index) {
        if (nativeANAds == null) return null;
        if (index >= nativeANAds.length || index < 0) return null;
        final NativeANClassAd nativeClassAd = nativeANAds[index];
        if (!nativeClassAd.pendingRefreshAN && nativeClassAd.mNativeANAdView != null) {
            nativeClassAd.pendingRefreshAN = true;
            //去掉手动刷新控制
            nativeClassAd.mNativeANAdView.postDelayed(new ReloadNativeANTask(nativeClassAd, index), 30000);
        }
        return nativeClassAd.mNativeANAdView;
    }

    public void showRewardVideo() {
        if (videoLoaded && mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    public void showInterstitial() {
        for (int i = 0; fullAds != null && i < fullAds.length; i++) {
            if (fullAds[i].ad != null && fullAds[i].ad.isLoaded()) {
                try {
                    showInterstitial(i);
                } catch (Exception ex) {
                }
                break;
            }
        }
    }

    public void showInterstitial(int index) {
        for (int i = 0; fullAds != null && i < fullAds.length; i++) {
            if (i != index) continue;
            if (fullAds[i].ad != null && fullAds[i].ad.isLoaded()) {
                try {
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAds[i].id, Const.ACTION_OPEN);
                    fullAds[i].loaded = false;
                    fullAds[i].ad.show();
                } catch (Exception ex) {
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD, "错误", ex.getMessage());
                }
                break;
            }
        }
    }

    public void loadNewVideo() {
        if (videoLoaded) return;
        if (videoRequest) return;
        if (!enableVideo) return;

        if (TextUtils.isEmpty(mVideoAdId)) return;

        videoRequest = true;
        videoRewarded = false;
        videoClicked = false;
        if (mRewardedVideoAd == null) {
            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(mContext);
        }

        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                videoLoaded = true;
                videoRequest = false;
                if (mAdListener != null) {
                    mAdListener.onAdLoaded(new AdType(AdType.ADMOB_VIDEO_AD), 0);
                }
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, mVideoAdId, Const.ACTION_LOAD);

            }

            @Override
            public void onRewardedVideoAdOpened() {
                if (mAdListener != null) {
                    mAdListener.onAdOpen(new AdType(AdType.ADMOB_VIDEO_AD), 0);
                }
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, mVideoAdId, Const.ACTION_SHOW);
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, mVideoAdId, Const.ACTION_SHOW_FULL);
                AdAppHelper.getInstance(mContext).getFacebook().logEvent(Const.CATEGORY_FB_AD_POSISTION, mVideoAdId, Const.ACTION_SHOW_FULL);
            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {
                videoLoaded = false;
                videoRequest = false;
                if (!videoRewarded) {
                    if (mRewardedListener != null) {
                        mRewardedListener.onRewardCancel();
                    }
                } else {
                    if (mRewardedListener != null) {
                        mRewardedListener.onReward(videoClicked);
                    }
                }
                AdAppHelper.getInstance(mContext).loadNewRewardedVideoAd();
                if (mAdListener != null) {
                    mAdListener.onAdClosed(new AdType(AdType.ADMOB_VIDEO_AD), 0);
                }
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, mVideoAdId, Const.ACTION_CLOSE_FULL);
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                videoRewarded = true;
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                videoClicked = true;
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, mVideoAdId, Const.ACTION_CLICK);
                if (mAdListener != null) {
                    mAdListener.onAdClick(new AdType(AdType.ADMOB_VIDEO_AD), 0);
                }
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                videoLoaded = false;
                videoRequest = false;
                if (mAdListener != null) {
                    mAdListener.onAdLoadFailed(new AdType(AdType.ADMOB_VIDEO_AD), 0, getAdLoadError(i));
                }
            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        });
        AdRequest.Builder builder = new AdRequest.Builder();
        ArrayList<String> testIdList = AdAppHelper.getTestDeviceIdList(AdNetwork.Admob);
        for (String s : testIdList) {
            builder.addTestDevice(s);
        }
        AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, mVideoAdId, Const.ACTION_REQUEST);
        mRewardedVideoAd.loadAd(mVideoAdId, builder.build());
    }

    public void loadNewBanner() {
        for (int i = 0; bannerAds != null && i < bannerAds.length; i++) {
            loadNewBanner(i);
        }
    }

    public void loadNewBanner(final int index) {
        if (bannerAds == null) return;
        if (index < 0 || index >= bannerAds.length) return;

        long now = System.currentTimeMillis();
        final BannerClassAd bannerAd = bannerAds[index];
        if (TextUtils.isEmpty(bannerAd.mBannerId)) return;
        if (bannerAd.bannerLoaded) return;
        if (bannerAd.bannerRequest) return;
        if (!enableBanner) return;

        bannerAd.bannerRequest = true;
        bannerAd.lastBannerRequestTime = System.currentTimeMillis();

        if (bannerAd.mBannerView == null) {
            bannerAd.mBannerView = new AdView(mContext);
            bannerAd.mBannerView.setAdUnitId(bannerAd.mBannerId);
            bannerAd.mBannerView.setAdSize(AdSize.BANNER);

            bannerAd.mBannerView.setAdListener(new AdListener() {
                @Override
                public void onAdLeftApplication() {
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, bannerAd.mBannerId, Const.ACTION_CLICK);
                    if (mAdListener != null) {
                        mAdListener.onAdClick(new AdType(AdType.ADMOB_BANNER), 0);
                    }
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    bannerAd.bannerRequest = false;
                    bannerAd.bannerLoaded = false;
                    if (mAdListener != null) {
                        mAdListener.onAdLoadFailed(new AdType(AdType.ADMOB_BANNER), 0, getAdLoadError(i));
                    }
                    if (AdAppHelper.getInstance(mContext).isNetworkConnected(mContext)) {
                        AdConfig config = AdAppHelper.getInstance(mContext).getConfig();
                        if (bannerAd.errorCount++ < config.ad_ctrl.admob_fail_reload_count) {
                            loadNewBanner(index);
                        }
                    }
                }

                @Override
                public void onAdLoaded() {
                    bannerAd.bannerLoaded = true;
                    bannerAd.bannerRequest = false;
                    bannerAd.errorCount = 0;
                    if (mAdListener != null) {
                        mAdListener.onAdLoaded(new AdType(AdType.ADMOB_BANNER), 0);
                    }
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, bannerAd.mBannerId, Const.ACTION_LOAD);
                }

                @Override
                public void onAdOpened() {
                    if (mAdListener != null) {
                        mAdListener.onAdOpen(new AdType(AdType.ADMOB_BANNER), 0);
                    }
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, bannerAd.mBannerId, Const.ACTION_SHOW_BANNER);
                    AdAppHelper.getInstance(mContext).getFacebook().logEvent(Const.CATEGORY_FB_AD_POSISTION, bannerAd.mBannerId, Const.ACTION_SHOW_BANNER);
                }
            });
        }

        AdRequest.Builder builder = new AdRequest.Builder();
        ArrayList<String> testIdList = AdAppHelper.getTestDeviceIdList(AdNetwork.Admob);
        for (String s : testIdList) {
            builder.addTestDevice(s);
        }
        bannerAd.mBannerView.loadAd(builder.build());
        AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, bannerAd.mBannerId, Const.ACTION_REQUEST);
    }

    public void loadNewInterstitial() {
        for (int i = 0; fullAds != null && i < fullAds.length; i++) {
            loadNewInterstitial(i);
        }
    }

    public void loadNewInterstitial(final int index) {
        if (fullAds == null) return;
        if (index < 0 || index >= fullAds.length) return;

        long now = System.currentTimeMillis();
        final AdMobInterstitialAd fullAd = fullAds[index];
        if (TextUtils.isEmpty(fullAd.id)) return;
        if (fullAd.loaded && (System.currentTimeMillis() - fullAd.loadedTime) < AdAppHelper.MAX_AD_ALIVE_TIME) return;
        if (fullAd.requested && (System.currentTimeMillis() - fullAd.lastRequestTime) < 1000 * 60) return;
        if (!enableInterstitial) return;

        fullAd.requested = true;
        fullAd.lastRequestTime = now;
        if (firstFullAdTime == -1) {
            firstFullAdTime = now;
        }

        if (fullAd.ad == null) {
            fullAd.ad = new InterstitialAd(mContext);
            fullAd.ad.setAdUnitId(fullAd.id);
            fullAd.ad.setAdListener(new AdListener() {
                @Override
                public void onAdLeftApplication() {
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAd.id, Const.ACTION_CLICK);
                    if (mAdListener != null) {
                        mAdListener.onAdClick(new AdType(AdType.ADMOB_FULL), index);
                    }
                }

                @Override
                public void onAdClosed() {
                    fullAd.errorCount = 0;
                    fullAd.loaded = false;
                    fullAd.requested = false;
                    AdAppHelper.getInstance(mContext).loadNewInterstitial(index);
                    if (mAdListener != null) {
                        mAdListener.onAdClosed(new AdType(AdType.ADMOB_FULL), index);
                    }
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAd.id, Const.ACTION_CLOSE_FULL);
                }

                @Override
                public void onAdLoaded() {
                    fullAd.loaded = true;
                    fullAd.requested = false;
                    fullAd.loadedTime = System.currentTimeMillis();
                    if (mAdListener != null) {
                        mAdListener.onAdLoaded(new AdType(AdType.ADMOB_FULL), index);
                    }
                    long cost = (System.currentTimeMillis() - fullAd.lastRequestTime) / 1000;
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD, "Admob全屏加载时间", cost);
                    if (firstFullAdTime != -2) {
                        AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD, "AdMob第一个全屏", System.currentTimeMillis() - firstFullAdTime);
                        firstFullAdTime = -2;
                    }

                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAd.id, Const.ACTION_LOAD);
                }

                @Override
                public void onAdOpened() {
                    if (mAdListener != null) {
                        mAdListener.onAdOpen(new AdType(AdType.ADMOB_FULL), index);
                    }
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAd.id, Const.ACTION_SHOW);
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAd.id, Const.ACTION_SHOW_FULL);
                    AdAppHelper.getInstance(mContext).getFacebook().logEvent(Const.CATEGORY_FB_AD_POSISTION, fullAd.id, Const.ACTION_SHOW_FULL);
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    fullAd.loaded = false;
                    fullAd.requested = false;
                    if (mAdListener != null) {
                        mAdListener.onAdLoadFailed(new AdType(AdType.ADMOB_FULL), index, getAdLoadError(i));
                    }
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_ERROR, fullAd.id, getAdLoadError(i));
                    if (AdAppHelper.getInstance(mContext).isNetworkConnected(mContext)) {
                        AdConfig config = AdAppHelper.getInstance(mContext).getConfig();
                        if (fullAd.errorCount++ < config.ad_ctrl.admob_fail_reload_count) {
                            loadNewInterstitial(index);
                        }
                    }
                }
            });
        }
        AdRequest.Builder builder = new AdRequest.Builder();
        ArrayList<String> testIdList = AdAppHelper.getTestDeviceIdList(AdNetwork.Admob);
        for (String s : testIdList) {
            builder.addTestDevice(s);
        }
        fullAd.ad.loadAd(builder.build());
        AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, fullAd.id, Const.ACTION_REQUEST);
    }

    public void loadNewNativeAd() {
        for (int i = 0; nativeAds != null && i < nativeAds.length; i++) {
            loadNewNativeAd(i);
        }
    }

    public void loadNewNativeAd(final int index) {
        if (nativeAds == null) return;
        if (index < 0 || index >= nativeAds.length) return;

        final NativeClassAd nativeClassAd = nativeAds[index];
        if (TextUtils.isEmpty(nativeClassAd.mNativeId)) return;
        if (nativeClassAd.nativeLoaded) return;
        if (nativeClassAd.nativeRequest && (System.currentTimeMillis() - nativeClassAd.lastRequestNativeEime) < 1000 * 60) return;
        if (!enableNative) return;

        if (nativeClassAd.height == NativeAdSize.AUTO_HEIGHT) {
            return;
        }

        nativeClassAd.nativeRequest = true;
        nativeClassAd.lastRequestNativeEime = System.currentTimeMillis();

        if (nativeClassAd.mNativeAdView == null) {
            nativeClassAd.mNativeAdView = new AdView(mContext);
            nativeClassAd.mNativeAdView.setAdUnitId(nativeClassAd.mNativeId);
            nativeClassAd.mNativeAdView.setAdSize(new AdSize(nativeClassAd.width, nativeClassAd.height));

            nativeClassAd.mNativeAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLeftApplication() {
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.mNativeId, Const.ACTION_CLICK);
                    if (mAdListener != null) {
                        mAdListener.onAdClick(new AdType(AdType.ADMOB_NATIVE), index);
                    }
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    nativeClassAd.nativeRequest = false;
                    nativeClassAd.nativeLoaded = false;
                    if (mAdListener != null) {
                        mAdListener.onAdLoadFailed(new AdType(AdType.ADMOB_NATIVE), index, getAdLoadError(i));
                    }
                    if (AdAppHelper.getInstance(mContext).isNetworkConnected(mContext)) {
                        AdConfig config = AdAppHelper.getInstance(mContext).getConfig();
                        if (nativeClassAd.errorCount++ < config.ad_ctrl.admob_fail_reload_count) {
                            loadNewNativeAd(index);
                        }
                    }
                }

                @Override
                public void onAdLoaded() {
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.mNativeId, Const.ACTION_LOAD);
                    nativeClassAd.nativeLoaded = true;
                    nativeClassAd.nativeRequest = false;
                    nativeClassAd.errorCount = 0;
                    if (mAdListener != null) {
                        mAdListener.onAdLoaded(new AdType(AdType.ADMOB_NATIVE), index);
                    }
                }

                @Override
                public void onAdOpened() {
                    if (mAdListener != null) {
                        mAdListener.onAdOpen(new AdType(AdType.ADMOB_NATIVE), index);
                    }
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.mNativeId, Const.ACTION_SHOW_NATIVE);
                    AdAppHelper.getInstance(mContext).getFacebook().logEvent(Const.CATEGORY_FB_AD_POSISTION, nativeClassAd.mNativeId, Const.ACTION_SHOW_NATIVE);
                }
            });
        }

        AdRequest.Builder builder = new AdRequest.Builder();
        ArrayList<String> testIdList = AdAppHelper.getTestDeviceIdList(AdNetwork.Admob);
        for (String s : testIdList) {
            builder.addTestDevice(s);
        }
        nativeClassAd.mNativeAdView.loadAd(builder.build());
        AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.mNativeId, Const.ACTION_REQUEST);
    }

    public void loadNewNativeENAd() {
        for (int i = 0; nativeAds != null && i < nativeAds.length; i++) {
            loadNewNativeENAd(i);
        }
    }

    public void loadNewNativeENAd(final int index) {
        if (nativeAds == null) return;
        if (index < 0 || index >= nativeAds.length) return;

        final NativeClassAd nativeClassAd = nativeAds[index];
        if (TextUtils.isEmpty(nativeClassAd.mNativeENId)) return;
        if (nativeClassAd.nativeENLoaded) return;
        if (nativeClassAd.nativeENRequest) return;
        if (!enableNativeEN) return;

        if (nativeClassAd.height == NativeAdSize.AUTO_HEIGHT) {
            return;
        }

        nativeClassAd.nativeENRequest = true;
        nativeClassAd.lastRequestNativeENTime = System.currentTimeMillis();

        if (nativeClassAd.mNativeENAdView == null) {
            nativeClassAd.mNativeENAdView = new NativeExpressAdView(mContext);
            nativeClassAd.mNativeENAdView.setAdUnitId(nativeClassAd.mNativeENId);
            nativeClassAd.mNativeENAdView.setAdSize(new AdSize(nativeClassAd.width, nativeClassAd.height));

            nativeClassAd.mNativeENAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLeftApplication() {
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.mNativeENId, Const.ACTION_CLICK);
                    if (mAdListener != null) {
                        mAdListener.onAdClick(new AdType(AdType.ADMOB_NATIVE_EN), index);
                    }
                }

                @Override
                public void onAdOpened() {
                    if (mAdListener != null) {
                        mAdListener.onAdOpen(new AdType(AdType.ADMOB_NATIVE_EN), index);
                    }
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.mNativeENId, Const.ACTION_SHOW_NATIVE);
                    AdAppHelper.getInstance(mContext).getFacebook().logEvent(Const.CATEGORY_FB_AD_POSISTION, nativeClassAd.mNativeENId, Const.ACTION_SHOW_NATIVE);
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    nativeClassAd.nativeENRequest = false;
                    nativeClassAd.nativeENLoaded = false;
                    if (mAdListener != null) {
                        mAdListener.onAdLoadFailed(new AdType(AdType.ADMOB_NATIVE_EN), index, getAdLoadError(i));
                    }
                }

                @Override
                public void onAdLoaded() {
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.mNativeENId, Const.ACTION_LOAD);
                    nativeClassAd.nativeENLoaded = true;
                    nativeClassAd.nativeENRequest = false;
                    if (mAdListener != null) {
                        mAdListener.onAdLoaded(new AdType(AdType.ADMOB_NATIVE_EN), index);
                    }
                }
            });
        }

        AdRequest.Builder builder = new AdRequest.Builder();
        ArrayList<String> testIdList = AdAppHelper.getTestDeviceIdList(AdNetwork.Admob);
        for (String s : testIdList) {
            builder.addTestDevice(s);
        }
        nativeClassAd.mNativeENAdView.loadAd(builder.build());
        AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.mNativeENId, Const.ACTION_REQUEST);
    }

    public void loadNewNativeANAd() {
        for (int i = 0; nativeANAds != null && i < nativeANAds.length; i++) {
            loadNewNativeANAd(i);
        }
    }

    public void loadNewNativeANAd(final int index) {
        if (nativeANAds == null) return;
        if (index < 0 || index >= nativeANAds.length) return;

        final NativeANClassAd nativeClassAd = nativeANAds[index];
        if (TextUtils.isEmpty(nativeClassAd.mNativeANId)) return;
        if (nativeClassAd.nativeANLoaded) return;
        if (nativeClassAd.nativeANRequest && (System.currentTimeMillis() - nativeClassAd.lastRequestNativeANTime) < 1000 * 60) return;
        if (!enableNativeAN) return;

        nativeClassAd.nativeANRequest = true;
        nativeClassAd.nativeImpression = false;
        nativeClassAd.nativeANLoaded = false;
        nativeClassAd.lastRequestNativeANTime = System.currentTimeMillis();
        nativeClassAd.nativeClicked = false;

        AdLoader.Builder builder = new AdLoader.Builder(mContext, nativeClassAd.mNativeANId);

        builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
            @Override
            public void onAppInstallAdLoaded(NativeAppInstallAd ad) {
                int layoutId = R.layout.adsplugin_native_250_ad_layout_admob_appinstall;
                switch (nativeClassAd.height) {
                    case NativeAdSize.SIZE_50:
                        layoutId = R.layout.adsplugin_native_50_ad_layout_admob_appinstall;
                        break;
                    case NativeAdSize.SIZE_80:
                        layoutId = R.layout.adsplugin_native_80_ad_layout_admob_appinstall;
                        break;
                    case NativeAdSize.SIZE_150:
                        layoutId = R.layout.adsplugin_native_150_ad_layout_admob_appinstall;
                        break;
                    case NativeAdSize.SIZE_180:
                        layoutId = R.layout.adsplugin_native_180_ad_layout_admob_appinstall;
                        break;
                    case NativeAdSize.SIZE_250:
                        layoutId = R.layout.adsplugin_native_250_ad_layout_admob_appinstall;
                        break;
                    case NativeAdSize.SIZE_300:
                        layoutId = R.layout.adsplugin_native_300_ad_layout_admob_appinstall;
                        break;
                    case NativeAdSize.AUTO_HEIGHT:
                        layoutId = R.layout.adsplugin_native_match_parent_ad_layout_admob_appinstall;
                        break;
                }
                NativeAppInstallAdView adView = (NativeAppInstallAdView) LayoutInflater.from(mContext)
                        .inflate(layoutId, null);
                nativeClassAd.nativeANLoaded = true;
                nativeClassAd.nativeANRequest = false;
                nativeClassAd.mNativeANAdView = adView;
                try {
                    populateAppInstallAdView(ad, adView, index);
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.mNativeANId, Const.ACTION_LOAD);
                    nativeClassAd.nativeANLoaded = true;
                    nativeClassAd.nativeANRequest = false;
                    if (mAdListener != null) {
                        mAdListener.onAdLoaded(new AdType(AdType.ADMOB_NATIVE_AN), index);
                    }
                } catch (Exception ex) {
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD, "错误", ex.getMessage());
                }
            }
        });

        builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
            @Override
            public void onContentAdLoaded(NativeContentAd ad) {
                int layoutId = R.layout.adsplugin_native_50_ad_layout_admob;
                switch (nativeClassAd.height) {
                    case NativeAdSize.SIZE_50:
                        layoutId = R.layout.adsplugin_native_50_ad_layout_admob;
                        break;
                    case NativeAdSize.SIZE_80:
                        layoutId = R.layout.adsplugin_native_80_ad_layout_admob;
                        break;
                    case NativeAdSize.SIZE_150:
                        layoutId = R.layout.adsplugin_native_150_ad_layout_admob;
                        break;
                    case NativeAdSize.SIZE_180:
                        layoutId = R.layout.adsplugin_native_180_ad_layout_admob;
                        break;
                    case NativeAdSize.SIZE_250:
                        layoutId = R.layout.adsplugin_native_250_ad_layout_admob;
                        break;
                    case NativeAdSize.SIZE_300:
                        layoutId = R.layout.adsplugin_native_300_ad_layout_admob;
                        break;
                    case NativeAdSize.AUTO_HEIGHT:
                        layoutId = R.layout.adsplugin_native_match_parent_ad_layout_admob;
                        break;
                }
                NativeContentAdView adView = (NativeContentAdView)LayoutInflater.from(mContext)
                        .inflate(layoutId, null);
                nativeClassAd.nativeANLoaded = true;
                nativeClassAd.nativeANRequest = false;
                nativeClassAd.mNativeANAdView = adView;
                try {
                    populateContentAdView(ad, adView, index);
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.mNativeANId, Const.ACTION_LOAD);
                    nativeClassAd.nativeANLoaded = true;
                    nativeClassAd.nativeANRequest = false;
                    if (mAdListener != null) {
                        mAdListener.onAdLoaded(new AdType(AdType.ADMOB_NATIVE_AN), index);
                    }
                } catch (Exception ex) {
                    AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD, "错误", ex.getMessage());
                }
            }
        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setImageOrientation(NativeAdOptions.ORIENTATION_LANDSCAPE)
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                if (mAdListener != null) {
                    mAdListener.onAdOpen(new AdType(AdType.ADMOB_NATIVE_AN), index);
                }
                nativeClassAd.nativeImpression = true;
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.mNativeANId, Const.ACTION_SHOW_NATIVE);
                AdAppHelper.getInstance(mContext).getFacebook().logEvent(Const.CATEGORY_FB_AD_POSISTION, nativeClassAd.mNativeANId, Const.ACTION_SHOW_NATIVE);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                nativeClassAd.nativeClicked = true;
                AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.mNativeANId, Const.ACTION_CLICK);
                if (mAdListener != null) {
                    mAdListener.onAdClick(new AdType(AdType.ADMOB_NATIVE_AN), index);
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                nativeClassAd.nativeANLoaded = false;
                nativeClassAd.nativeANRequest = false;
                if (mAdListener != null) {
                    mAdListener.onAdLoadFailed(new AdType(AdType.ADMOB_NATIVE_AN), index, getAdLoadError(i));
                }
            }
        }).build();

        AdRequest.Builder builder1 = new AdRequest.Builder();
        ArrayList<String> testIdList = AdAppHelper.getTestDeviceIdList(AdNetwork.Admob);
        for (String s : testIdList) {
            builder1.addTestDevice(s);
        }
        adLoader.loadAd(builder1.build());
        AdAppHelper.getInstance(mContext).getFireBase().logEvent(Const.CATEGORY_AD_POSISTION, nativeClassAd.mNativeANId, Const.ACTION_REQUEST);
    }

    /**
     * Populates a {@link NativeAppInstallAdView} object with data from a given
     * {@link NativeAppInstallAd}.
     *
     * @param nativeAppInstallAd the object containing the ad's assets
     * @param adView             the view to be populated
     */
    private void populateAppInstallAdView(NativeAppInstallAd nativeAppInstallAd,
                                          NativeAppInstallAdView adView, int index) {
        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAppInstallAd.getVideoController();

        // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
        // VideoController will call methods on this object when events occur in the video
        // lifecycle.
        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            public void onVideoEnd() {
                // Publishers should allow native ads to complete video playback before refreshing
                // or replacing them with another ad in the same UI location.
                super.onVideoEnd();
            }
        });

        adView.setHeadlineView(adView.findViewById(R.id.ads_plugin_native_ad_title));
        adView.setBodyView(adView.findViewById(R.id.ads_plugin_native_ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ads_plugin_native_ad_call_to_action));
        View iconView = adView.findViewById(R.id.ads_plugin_native_ad_icon);
        if (iconView != null) {
            adView.setIconView(iconView);
        }

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());
        if (iconView != null) {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAppInstallAd.getIcon().getDrawable());
        }

        View root = adView.findViewById(R.id.ads_plugin_native_ad_unit);
        AdAppHelper helper = AdAppHelper.getInstance(mContext);
        if (helper.NATIVE_BG_COLOR_LIST != null && index < helper.NATIVE_BG_COLOR_LIST.length) {
            root.setBackgroundColor(helper.NATIVE_BG_COLOR_LIST[index]);
        }
        boolean textColorSet = false;
        int textColor = Color.parseColor("#000000");
        if (helper.NATIVE_TEXT_COLOR_LIST != null && index < helper.NATIVE_TEXT_COLOR_LIST.length) {
            textColorSet = true;
            textColor = helper.NATIVE_TEXT_COLOR_LIST[index];
        }
        if (textColorSet) {
            ((TextView) adView.getHeadlineView()).setTextColor(textColor);
            ((TextView) adView.getBodyView()).setTextColor(textColor);
        }

        MediaView mediaView = (MediaView)adView.findViewById(R.id.ads_plugin_native_ad_media);
        ImageView mainImageView = (ImageView)adView.findViewById(R.id.ads_plugin_native_main_image);

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeAppInstallAd has a video asset.
        if (vc.hasVideoContent() && mediaView != null) {
            adView.setMediaView(mediaView);
            mainImageView.setVisibility(View.GONE);
        } else if (mainImageView != null) {
            adView.setImageView(mainImageView);
            mediaView.setVisibility(View.GONE);

            // At least one image is guaranteed.
            List<NativeAd.Image> images = nativeAppInstallAd.getImages();
            if (images.size() > 0) {
                mainImageView.setImageDrawable(images.get(0).getDrawable());
            }
        }

        adView.setNativeAd(nativeAppInstallAd);
    }

    private void populateContentAdView(NativeContentAd nativeContentAd,
                                       NativeContentAdView adView, int index) {

        adView.setHeadlineView(adView.findViewById(R.id.ads_plugin_native_ad_title));
        ImageView mainImageView = (ImageView)adView.findViewById(R.id.ads_plugin_native_main_image);
        adView.setImageView(mainImageView);
        adView.setBodyView(adView.findViewById(R.id.ads_plugin_native_ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ads_plugin_native_ad_call_to_action));
        View iconView = adView.findViewById(R.id.ads_plugin_native_ad_icon);
        if (iconView != null) {
            adView.setLogoView(iconView);
        }

        // Some assets are guaranteed to be in every NativeContentAd.
        ((TextView) adView.getHeadlineView()).setText(nativeContentAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeContentAd.getBody());
        ((TextView) adView.getCallToActionView()).setText(nativeContentAd.getCallToAction());

        View root = adView.findViewById(R.id.ads_plugin_native_ad_unit);
        AdAppHelper helper = AdAppHelper.getInstance(mContext);
        if (helper.NATIVE_BG_COLOR_LIST != null && index < helper.NATIVE_BG_COLOR_LIST.length) {
            root.setBackgroundColor(helper.NATIVE_BG_COLOR_LIST[index]);
        }
        boolean textColorSet = false;
        int textColor = Color.parseColor("#000000");
        if (helper.NATIVE_TEXT_COLOR_LIST != null && index < helper.NATIVE_TEXT_COLOR_LIST.length) {
            textColorSet = true;
            textColor = helper.NATIVE_TEXT_COLOR_LIST[index];
        }
        if (textColorSet) {
            ((TextView) adView.getHeadlineView()).setTextColor(textColor);
            ((TextView) adView.getBodyView()).setTextColor(textColor);
        }

        List<NativeAd.Image> images = nativeContentAd.getImages();

        if (images.size() > 0 && mainImageView != null) {
            ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
        }

        // Some aren't guaranteed, however, and should be checked.
        NativeAd.Image logoImage = nativeContentAd.getLogo();

        if (logoImage == null && iconView != null) {
            adView.getLogoView().setVisibility(View.INVISIBLE);
        } else if (iconView != null) {
            ((ImageView) adView.getLogoView()).setImageDrawable(logoImage.getDrawable());
            adView.getLogoView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeContentAd);
    }


    private String getAdLoadError(int code) {
        String reason = "ERROR_CODE_INTERNAL_ERROR";
        switch (code) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                reason = "ERROR_CODE_INTERNAL_ERROR";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                reason = "ERROR_CODE_INVALID_REQUEST";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                reason = "ERROR_CODE_NETWORK_ERROR";
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                reason = "ERROR_CODE_NO_FILL";
                break;
        }
        return reason;
    }
}
