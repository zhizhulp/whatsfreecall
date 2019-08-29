package com.baisi.whatsfreecall.ui.activity.earn_money;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.adsconfig.StatisticalConfig;
import com.baisi.whatsfreecall.application.WhatsFreeCallApplication;
import com.baisi.whatsfreecall.base.net.BaseNetActivity;
import com.baisi.whatsfreecall.base.net.ResponseHandlerImpl;
import com.baisi.whatsfreecall.configs.SpConfig;
import com.baisi.whatsfreecall.entity.EarnListEntity;
import com.baisi.whatsfreecall.entity.httpbackentity.HttpEntity;
import com.baisi.whatsfreecall.entity.requestentity.AdsCharge;
import com.baisi.whatsfreecall.retrofit_services.NetService;
import com.baisi.whatsfreecall.task_handler.TimeReduceHandler;
import com.baisi.whatsfreecall.task_handler.TimeReduceTask;
import com.baisi.whatsfreecall.ui.activity.LoginActivity;
import com.baisi.whatsfreecall.ui.adapter.EarnMoneyAdapter;
import com.baisi.whatsfreecall.utils.DialogUtils;
import com.baisi.whatsfreecall.utils.adsutils.VideoAdsUtils;
import com.baisi.whatsfreecall.utils.budleutils.BundleUtils;
import com.baisi.whatsfreecall.utils.httputils.HttpClentUtils;
import com.baisi.whatsfreecall.utils.sputils.SpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * 挣钱列表
 */
public class EarnMoneyListActivity extends BaseNetActivity implements EarnMoneyAdapter.OnItemChildClick, VideoAdsUtils.VideoOnPlayListener {
    private EarnListEntity.EarnEntity currentEarnEntity;
    private int position;
    private static final int REQUEST_STAR = 2018;
    public static final String STARS_ENTITY = "EarnEntity";
    private RecyclerView recyclerView;
    private List<EarnListEntity.EarnEntity> data = new ArrayList<>();
    private EarnMoneyAdapter adapter;
    private TextView tvEarnAll;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earn_money_list);
        initView();
    }

    private void initView() {
        tvEarnAll = (TextView) findViewById(R.id.tv_earning);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolbar(toolbar, "");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EarnMoneyAdapter(data);
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(this);
        getMissionList();
    }

    private void getMissionList() {
        showProgress(R.id.ll_progress, R.id.pb_include, R.id.tv_progress_msg, getResources().getString(R.string.wait), R.color.colorPrimary);
        NetService netService = HttpClentUtils.getRetorfit().create(NetService.class);
        String userToken = "Bearer " +
                SpUtils.getString(WhatsFreeCallApplication.getInstance(), SpConfig.USER_TOKEN, "");
        doNetwork(netService.missionList(userToken), new ResponseHandlerImpl<HttpEntity<EarnListEntity>>() {
            @Override
            public void handle200(HttpEntity<EarnListEntity> result) {
                if (result.getErr_code()==0){
                    data.clear();
                    data.addAll(operateList(result.getData().list));
                    adapter.notifyDataSetChanged();
                    timeCutDown();
                }else {
                    showShortToast(result.getErr_msg());
                    if(result.getErr_code()==1005){
                        skip(LoginActivity.class, BundleUtils.getBundleUtils().getLoginBundle(false, BundleUtils.ENTER_CHARGE_VIDEO, "", null));
                        finish();
                    }
                }
            }

            @Override
            public void onFinish() {
                hideProgress((R.id.ll_progress));
            }
        });
    }

    private List<EarnListEntity.EarnEntity> operateList(List<EarnListEntity.EarnEntity> list) {
        if (list == null || list.size() == 0) return list;
        EarnListEntity.EarnEntity earnEntityInvite = new EarnListEntity.EarnEntity();
        earnEntityInvite.type = 1006;
        list.add(earnEntityInvite);
        for (int i = 0; i < list.size(); i++) {
            EarnListEntity.EarnEntity earnEntity = list.get(i);
            switch (earnEntity.type) {
                case 1008://video card
                    earnEntity.resId = R.mipmap.videoad;
                    earnEntity.color =  getResources().getColor(R.color.video_card_left);
                    earnEntity.rightColor =  getResources().getColor(R.color.video_card_right);
                    earnEntity.prtResColor = getResources().getColor(R.color.video_card_bg);
                    earnEntity.desc = getString(R.string.earn_by_video);
                    //earnEntity.leftTime = 10;
                    //earnEntity.totalTime = 10;
                    break;
                case 1007://collect stars card
                    earnEntity.resId = R.mipmap.star;
                    earnEntity.color =  getResources().getColor(R.color.star_card_left);
                    earnEntity.rightColor =  getResources().getColor(R.color.star_card_right);
                    earnEntity.prtResColor =  getResources().getColor(R.color.collect_star_bg);
                    earnEntity.desc = getString(R.string.earn_by_stars);
                    earnEntity.reward = getString(R.string.earn_by_stars_sub);
                    break;
                case 1006:
                    earnEntity.resId = R.mipmap.star;
                    earnEntity.color =  getResources().getColor(R.color.invite_card_left);
                    earnEntity.rightColor =  getResources().getColor(R.color.invite_card_right);
                    earnEntity.prtResColor =  getResources().getColor(R.color.invite_bg);
                    earnEntity.desc = getString(R.string.earn_by_invite);
                    earnEntity.reward = getString(R.string.earn_by_invite_sub);
                    break;
            }
        }
        return list;
    }

    private void timeCutDown() {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimeReduceTask(data, new TimeReduceHandler(adapter)), 0, TimeReduceTask.INTERVAL_TIME);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }

    @Override
    public void onItemChildClick(View view, int position) {
        this.position =position;
        currentEarnEntity = data.get(position);
        switch (currentEarnEntity.type) {
            case 1007://collect stars card
                Intent intent = new Intent(this, CollectStarActivity.class);
                intent.putExtra(STARS_ENTITY, currentEarnEntity);
                startActivityForResult(intent, REQUEST_STAR);
                break;
            case 1008://video card
                //applyStars( VideoAdsUtils.OVER,false);
                VideoAdsUtils.showVideoAds(StatisticalConfig.VIDEO_ADS, this);
                break;
            case 1006:
                skip(InviteActivity.class);
                break;
        }
    }

    @Override
    public void setState(final int state, final boolean isClick) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                applyStars(state, isClick);
            }
        });
    }

    public void applyStars(int state, boolean isClick) {
        NetService netService = HttpClentUtils.getRetorfit().create(NetService.class);
        String userToken = "Bearer " +
                SpUtils.getString(WhatsFreeCallApplication.getInstance(), SpConfig.USER_TOKEN, "");
        showDefaultProgress();
        doNetwork(netService.applyVideo(userToken, new AdsCharge(
                        state == VideoAdsUtils.OVER ? VideoAdsUtils.VIDEO_ADS_TYPE : null, String.valueOf(isClick))),
                new ResponseHandlerImpl<HttpEntity<EarnListEntity.EarnEntity>>() {
                    @Override
                    public void handle200(final HttpEntity<EarnListEntity.EarnEntity> result) {
                        if (result.getErr_code()==0){
                            DialogUtils.showPopupWindow(EarnMoneyListActivity.this, result.getData().reward,
                                    result.getData().profile.credit_string, new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            EarnListEntity.EarnEntity earnEntity = data.get(position);
                                            earnEntity.leftTime = result.getData().leftTime;
                                            earnEntity.totalTime = result.getData().totalTime;
                                            //earnEntity.leftTime = 10;
                                            //earnEntity.totalTime =20;
                                            //adapter.notifyDataSetChanged();
                                        }
                                    });
                        }else {
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
    protected void onResume() {
        super.onResume();
        getMissionList();
    }
}
