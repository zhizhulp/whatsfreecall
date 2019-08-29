package com.bestgo.adsplugin.ads;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.bestgo.adsplugin.ads.listener.RewardedListener;
import com.vungle.publisher.*;
import com.vungle.publisher.AdConfig;

import java.util.ArrayList;

/**
 * Created by jikai on 1/4/18.
 */

public class VungleAd {

    private RewardedListener mRewardedListener;

    final VunglePub vunglePub = VunglePub.getInstance();

    private boolean videoEnabled;
    private boolean videoLoaded;

    private com.vungle.publisher.AdConfig globalConfig;

    private String[] placementIds = {};

    public VungleAd() {
    }

    public void init(final Context context) {
        com.bestgo.adsplugin.ads.AdConfig config = AdAppHelper.getInstance(context).getConfig();
        if (config.video_ctrl.exe == 1 && config.video_ctrl.vungle > 0) {
            videoEnabled = true;
        } else {
            videoEnabled = false;
        }
        System.out.println("VungleAd init enter, videoEnabled=" + videoEnabled);

        if (videoEnabled) {
            ArrayList<String> ids = new ArrayList<>();
            for (int i = 0; i < config.vungle_ids.ids.size(); i++) {
                if (TextUtils.isEmpty(config.vungle_ids.ids.get(i).id)) continue;

                ids.add(config.vungle_ids.ids.get(i).id);
            }
            boolean reInit = false;
            for (int i = 0; i < placementIds.length; i++) {
                if (!ids.contains(placementIds[i])) {
                    reInit = true;
                }
            }
            if (ids.size() > 0 && (placementIds.length != ids.size() || reInit)) {
                System.out.println("VungleAd init");
                placementIds = new String[ids.size()];
                vunglePub.init(context, config.vungle_ids.app_id, ids.toArray(placementIds), new VungleInitListener() {
                    @Override
                    public void onSuccess() {
                        System.out.println("VungleAd init success");
                        globalConfig = vunglePub.getGlobalAdConfig();
                        globalConfig.setBackButtonImmediatelyEnabled(false);
                        globalConfig.setImmersiveMode(true);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        System.out.println("VungleAd init failed");
                    }
                });

                vunglePub.clearEventListeners();
                vunglePub.addEventListeners(new VungleAdEventListener() {
                    @Override
                    public void onAdEnd(@NonNull String s, boolean b, boolean b1) {
                        System.out.println("onAdEnd, " + s + ", wasSuccessfulView=" + b + ", wasCallToActionClicked=" + b1);
                        if (b || b1) {
                            mRewardedListener.onReward(b1);
                        } else {
                            mRewardedListener.onRewardCancel();
                        }
                        loadNewVideo();
                        AdAppHelper.getInstance(context).getInnerListener().onAdClosed(new AdType(AdType.VUNGLE_VIDEO_AD), placementIndex(s));
                    }

                    @Override
                    public void onAdStart(@NonNull String s) {
                        System.out.println("onAdStart, " + s);
                        AdAppHelper.getInstance(context).getInnerListener().onAdOpen(new AdType(AdType.VUNGLE_VIDEO_AD), placementIndex(s));
                    }

                    @Override
                    public void onUnableToPlayAd(@NonNull String s, String s1) {
                        System.out.println("onUnableToPlayAd, " + s + ", " + s1);
                    }

                    @Override
                    public void onAdAvailabilityUpdate(@NonNull String s, boolean b) {
                        System.out.println("onAdAvailabilityUpdate, " + s + ", isAdAvailable=" + b);
                        if (b) {
                            AdAppHelper.getInstance(context).getInnerListener().onAdLoaded(new AdType(AdType.VUNGLE_VIDEO_AD), placementIndex(s));
                            videoLoaded = true;
                        }
                    }
                });
            }
        }
    }

    private int placementIndex(String s) {
        int index = -1;
        for (int i = 0; i < placementIds.length; i++) {
            if (s.equals(placementIds[i])) {
                index = i;
                break;
            }
        }
        return index;
    }

    public boolean isVideoLoaded() {
        for (int i = 0; i < placementIds.length; i++) {
            if (vunglePub.isAdPlayable(placementIds[i])) {
                return  true;
            }
        }
        return false;
    }

    public void loadNewVideo() {
        for (int i = 0; i < placementIds.length; i++) {
            if (!vunglePub.isAdPlayable(placementIds[i])) {
                vunglePub.loadAd(placementIds[i]);
            }
        }
    }

    public void setRewardedListener(RewardedListener listener) {
        mRewardedListener = listener;
    }

    public void playAd() {
        for (int i = 0; i < placementIds.length; i++) {
            if (!vunglePub.isAdPlayable(placementIds[i])) {
                vunglePub.loadAd(placementIds[i]);
            }
        }

        for (int i = 0; i < placementIds.length; i++) {
            if (vunglePub.isAdPlayable(placementIds[i])) {
                vunglePub.playAd(placementIds[i], globalConfig);
                break;
            }
        }
    }
}
