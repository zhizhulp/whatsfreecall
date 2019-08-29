package com.baisi.whatsfreecall.ui.activity.earn_money;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.application.WhatsFreeCallApplication;
import com.baisi.whatsfreecall.base.net.BaseNetActivity;
import com.baisi.whatsfreecall.base.net.ResponseHandlerImpl;
import com.baisi.whatsfreecall.configs.SpConfig;
import com.baisi.whatsfreecall.entity.EarnListEntity;
import com.baisi.whatsfreecall.entity.httpbackentity.HttpEntity;
import com.baisi.whatsfreecall.entity.requestentity.AdsCharge;
import com.baisi.whatsfreecall.retrofit_services.NetService;
import com.baisi.whatsfreecall.utils.DateFormatter;
import com.baisi.whatsfreecall.utils.DialogUtils;
import com.baisi.whatsfreecall.utils.adsutils.FullAdsUtils;
import com.baisi.whatsfreecall.utils.httputils.HttpClentUtils;
import com.baisi.whatsfreecall.utils.sputils.SpUtils;
import com.baisi.whatsfreecall.view.StateStar;
import com.baisi.whatsfreecall.view.progress_button.AnimDownloadProgressButton;
import com.bestgo.adsplugin.ads.AdType;
import com.bestgo.adsplugin.ads.listener.AdStateListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.baisi.whatsfreecall.utils.adsutils.FullAdsUtils.COLLECT_STAR_FULL;

/**
 * 签到页面
 */
public class CollectStarActivity extends BaseNetActivity implements View.OnClickListener {
    private static final long PERIOD = 1000;
    private AnimDownloadProgressButton tvCollect;
    private int[] starResIds = new int[]{R.id.im_01, R.id.im_02, R.id.im_03, R.id.im_04, R.id.im_05,};
    private List<StateStar> stateStarList = new ArrayList<>();
    private EarnListEntity.EarnEntity earnEntity;
    private int mFlashPosition = -1;
    private Timer timer;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handle set status " + earnEntity.leftTime);
            setStatus();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_star);
        tvCollect = (AnimDownloadProgressButton) findViewById(R.id.tv_collect);
        tvCollect.setOnClickListener(this);
        View toolBar = findViewById(R.id.toolbar);
        initToolbar((Toolbar) toolBar, getString(R.string.collect_start_title));

        for (int starResId : starResIds) {
            StateStar stateStar = (StateStar) findViewById(starResId);
            stateStarList.add(stateStar);
        }
        getDataFromIntent();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        earnEntity = (EarnListEntity.EarnEntity)
                intent.getSerializableExtra((EarnMoneyListActivity.STARS_ENTITY));
        Log.d(TAG, "getDataFromIntent total_time " + earnEntity.totalTime + "," + "left_time " + earnEntity.leftTime + " size " + earnEntity.stars.size());
        if (earnEntity == null) {
            finish();
        } else {
            setStatus();
            timeCountDown();
        }
    }

    private void setStatus() {
        setStarStatus();
        setBtnStatus();
    }

    private void setBtnStatus() {
        if (earnEntity.totalTime == -1) {//没有奖励了
            tvCollect.setState(AnimDownloadProgressButton.DOWNLOADING);
            tvCollect.setProgress(0);
            tvCollect.setCurrentText("No more reward");
            tvCollect.setEnabled(false);
        } else {
            float pro;
            if (earnEntity.totalTime == 0) pro = 100;
            else pro = (1 - ((float) earnEntity.leftTime / (float) earnEntity.totalTime)) * 100;
            if (pro == 100) {
                tvCollect.setState(AnimDownloadProgressButton.NORMAL);
                tvCollect.setCurrentText("Go");
                tvCollect.setEnabled(true);
            } else {
                tvCollect.setState(AnimDownloadProgressButton.DOWNLOADING);
                tvCollect.setProgress(pro);
                tvCollect.setCurrentText(DateFormatter.format(new Date(earnEntity.leftTime * 1000), "mm:ss"));
                tvCollect.setEnabled(false);
            }
        }
    }

    private void setStarStatus() {
        List<EarnListEntity.EarnEntity.Stars> stars = earnEntity.stars;
        //set stars status
        for (int i = 0; i < stateStarList.size(); i++) {
            stateStarList.get(i).setEEnable(stars.get(i).status == 2);
        }
        //判断 stars are all complete
        if (isAllComplete(stars)) earnEntity.totalTime = -1;
        //flash star
        if (earnEntity.leftTime == 0) {
            for (int i = 0; i < stateStarList.size(); i++) {
                if (stars.get(i).status == 1) {
                    mFlashPosition = i;
                    Log.d(TAG, "position " + mFlashPosition + " is flashing");
                    stateStarList.get(i).setEEnable(true);
                    stateStarList.get(i).startFlash();
                    return;
                }
            }
        }
    }

    //判断 stars are all complete
    private boolean isAllComplete(List<EarnListEntity.EarnEntity.Stars> stars) {
        for (int i = 0; i < stars.size(); i++) {
            if (stars.get(i).status == 1) return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_collect:
                // TODO: 2018/4/4
                //changeStarState();
                //applyStars(true, true);
                showDefaultProgress();
                final boolean[] params = new boolean[2];
                FullAdsUtils.ShowFull(COLLECT_STAR_FULL, new AdStateListener() {
                    @Override
                    public void onAdClosed(AdType adType, int index) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                applyStars(params[0], params[1]);
                            }
                        });
                    }

                    @Override
                    public void onAdLoadFailed(AdType adType, int index, String reason) {
                        hideDefaultProgress();
                    }

                    @Override
                    public void onAdOpen(AdType adType, int index) {
                        hideDefaultProgress();
                        params[0] = true;
                    }

                    @Override
                    public void onAdClick(AdType adType, int index) {
                        params[1] = true;
                    }
                });
                break;
        }
    }

    private void changeStarState() {
        Log.d(TAG, "request total_time " + earnEntity.totalTime + "," + "left_time " + earnEntity.leftTime);
        List<EarnListEntity.EarnEntity.Stars> starss = earnEntity.stars;
        if (mFlashPosition != -1) {//有正在闪烁的星星
            stateStarList.get(mFlashPosition).stopFlash();
            Log.d(TAG, "flash position: " + mFlashPosition + "is stopped" + ",size " + starss.size());
            starss.get(mFlashPosition).status = 2;
            resetFlashPosition();
            setStatus();
        }
    }

    private void resetFlashPosition() {
        mFlashPosition = -1;
    }

    public void applyStars(boolean isOpen, boolean isClick) {
        NetService netService = HttpClentUtils.getRetorfit().create(NetService.class);
        String userToken = "Bearer " +
                SpUtils.getString(WhatsFreeCallApplication.getInstance(), SpConfig.USER_TOKEN, "");
        showDefaultProgress();
        doNetwork(netService.applyStar(userToken, new AdsCharge(
                        isOpen ? FullAdsUtils.COLLECT_STAR : null, String.valueOf(isClick))),
                new ResponseHandlerImpl<HttpEntity<EarnListEntity.EarnEntity>>() {
                    @Override
                    public void handle200(final HttpEntity<EarnListEntity.EarnEntity> result) {
                        if (result.getErr_code() == 0) {
                            DialogUtils.showPopupWindow(CollectStarActivity.this, result.getData().reward,
                                    result.getData().profile.credit_string, new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            // TODO: 2018/4/4
                                            earnEntity = result.getData();
                                            changeStarState();
                                        }
                                    });
                        } else {
                            showShortToast(result.getErr_msg());
                        }
                    }

                    @Override
                    public void onFinish() {
                        hideDefaultProgress();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }

    private void timeCountDown() {
        if (timer == null) timer = new Timer();
        timer.schedule(new ReduceTask(), 0, PERIOD);
    }

    private class ReduceTask extends TimerTask {

        @Override
        public void run() {
            if (earnEntity.leftTime * 1000 >= PERIOD) {
                (earnEntity.leftTime) -= PERIOD / 1000;
                handler.sendEmptyMessage(0);
            }
        }
    }
}
