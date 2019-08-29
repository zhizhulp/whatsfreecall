package com.baisi.whatsfreecall.utils.adsutils;

import com.baisi.whatsfreecall.adsconfig.StatisticalConfig;
import com.baisi.whatsfreecall.utils.firebaseutils.Firebase;
import com.bestgo.adsplugin.ads.AdAppHelper;
import com.bestgo.adsplugin.ads.listener.RewardedListener;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by MnyZhao on 2018/1/12.
 */

public class VideoAdsUtils {
    public static final int OVER = 1;
    public static final int CANCEL = 2;
    public static final String VIDEO_ADS_TYPE = "rewarded_video";
    //再次观看视频间隔时间
    public static final String VIDEO_BTN_TIME_INTERVAL = "video_btn_time_interval";

    /**
     * 位置名称
     *
     * @param adressName
     */
    private static long startTime;

    public static void showVideoAds(String adressName, final VideoOnPlayListener videoOnPlayListener) {
        startTime = System.currentTimeMillis();
        AdAppHelper.getInstance(getApplicationContext()).showVideoAd();
        Firebase.getInstance(getApplicationContext()).logEvent(adressName, StatisticalConfig.OK);
        AdAppHelper.getInstance(getApplicationContext()).setRewardedListener(new RewardedListener() {
            @Override
            public void onReward(boolean clicked) {
                long endTime = System.currentTimeMillis() - startTime;
                Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.VIDEO_START_PLAY_END, endTime);
                videoOnPlayListener.setState(OVER,clicked);
            }

            @Override
            public void onRewardCancel() {
                Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.VIDEO_PLAY_CANCEL, StatisticalConfig.CANCEL);
                videoOnPlayListener.setState(CANCEL,false);
            }
        });
        /*观看结束后再请求一次*/
        AdAppHelper.getInstance(getApplicationContext()).loadNewRewardedVideoAd();
       /* if (AdAppHelper.getInstance(getApplicationContext()).isVideoReady()) {
            //加载成功显示视频广告*/

      /*  } else {
            //加载失败
            Firebase.getInstance(getApplicationContext()).logEvent(adressName, StatisticalConfig.NO);
        }*/
    }

    /**
     * 视频广告监听器
     */
    public interface VideoOnPlayListener {
        /**
         * 标记是取消还是观看结束
         *
         * @param state
         */
        void setState(int state,boolean isClick);
    }

    //视频广告监听
    VideoOnPlayListener videoOnPlayListener;

    /**
     * 设置视频广告监听器
     *
     * @param videoOnPlayListener
     */
    public void setOnVideoOnPlayListener(VideoOnPlayListener videoOnPlayListener) {
        this.videoOnPlayListener = videoOnPlayListener;
    }
}
