package com.baisi.whatsfreecall.ui.activity.checkin;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.application.WhatsFreeCallApplication;
import com.baisi.whatsfreecall.base.BaseActivity;
import com.baisi.whatsfreecall.configs.SpConfig;
import com.baisi.whatsfreecall.configs.UrlConfig;
import com.baisi.whatsfreecall.entity.checkin.CheckInEntity;
import com.baisi.whatsfreecall.entity.httpbackentity.HttpEntity;
import com.baisi.whatsfreecall.manager.checkinnotification.CheckInNotificationManager;
import com.baisi.whatsfreecall.ui.adapter.CheckInAdapter;
import com.baisi.whatsfreecall.utils.adsutils.FullAdsUtils;
import com.baisi.whatsfreecall.utils.httputils.HttpClentUtils;
import com.baisi.whatsfreecall.utils.sputils.SpUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;


public class CheckInActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar mCheckinToolbar;
    private RecyclerView mRecyclerView;
    private SwitchCompat mSwitch;
    private Dialog dialog;
    private List<CheckInEntity.tasks> mData;
    private CheckInAdapter mAdapter;
    private CheckInNotificationManager mNotificationManager;

    private String mReward = "+$0.000";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        initView();
    }

    private void initView() {
        mCheckinToolbar = (Toolbar) findViewById(R.id.checkin_toolbar);
        initToolbar(mCheckinToolbar, R.string.check_in);

        TextView topTip = (TextView) findViewById(R.id.tv_check_in_top_tip);
        topTip.getPaint().setFakeBoldText(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_every_day_check_in);
        mData = new ArrayList<>();
        mAdapter = new CheckInAdapter(this, mData);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        showData();

        Button checkIn = (Button) findViewById(R.id.btn_check_in_every_day);
        checkIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInEveryDay();
            }
        });

        mSwitch = (SwitchCompat) findViewById(R.id.switch_check_in_reminder);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCheckInNotification(isChecked);
            }

        });

        LinearLayout openReminder = (LinearLayout) findViewById(R.id.ll_check_in_reminder);
        openReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwitch.setChecked(!mSwitch.isChecked());
            }
        });

        mNotificationManager = new CheckInNotificationManager(this);
        mNotificationManager.openNotification();
    }

    private void setCheckInNotification(boolean isChecked) {
        if (isChecked) {
            mNotificationManager.openNotification();
        } else {
            mNotificationManager.closeNotification();
        }
    }

    private void showCheckInDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_check_in);
        dialog.setCanceledOnTouchOutside(false);
        TextView money = (TextView) dialog.findViewById(R.id.tv_check_in_every_day_get_money);
        money.setText(mReward);
        Button ok = (Button) dialog.findViewById(R.id.btn_check_in_every_day);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mAdapter.notifyDataSetChanged();
                 /*签到结束*/
                FullAdsUtils.isShowFull(FullAdsUtils.SHOW_CHECKINEND_FULL, false, FullAdsUtils.SHOW_CHECKINEND_FULL);
            }
        });
        dialog.show();
    }


    private void checkInEveryDay() {
        showProgress(R.id.ll_progress, R.id.pb_include, R.id.tv_progress_msg, getResources().getString(R.string.wait), R.color.colorPrimary);
        postCheckInData();
    }


    private void showData() {
        showProgress(R.id.ll_progress, R.id.pb_include, R.id.tv_progress_msg, getResources().getString(R.string.wait), R.color.colorPrimary);
        getCheckInData();
    }


    private void getCheckInData() {
        getDialCheckIn mGetDialCheckIn = HttpClentUtils.getRetorfit().create(getDialCheckIn.class);
        String userToken = SpUtils.getString(WhatsFreeCallApplication.getInstance().getApplicationContext(), SpConfig.USER_TOKEN, "");
        Call<HttpEntity<CheckInEntity>> call = mGetDialCheckIn.getDialCheckIn("Bearer " + userToken);
        call.enqueue(new Callback<HttpEntity<CheckInEntity>>() {
            @Override
            public void onResponse(Call<HttpEntity<CheckInEntity>> call, Response<HttpEntity<CheckInEntity>> response) {
                hideProgress(R.id.ll_progress);
                if (response != null) {
                    HttpEntity<CheckInEntity> callBack = response.body();
                    if (callBack != null) {
                        if (callBack.getErr_code() == 0) {
                            CheckInEntity mCheckIn = callBack.getData();
                            if (mCheckIn != null) {
                                List<CheckInEntity.tasks> taskses = mCheckIn.getTasks();
                                if (taskses != null && taskses.size() > 0) {
                                    mData.clear();
                                    mData.addAll(taskses);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            showShortToast(callBack.getErr_msg());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<HttpEntity<CheckInEntity>> call, Throwable t) {
                hideProgress(R.id.ll_progress);
                showShortToast(t.getMessage());
            }
        });
    }

    private void postCheckInData() {
        postDialCheckIn mPostDialCheckIn = HttpClentUtils.getRetorfit().create(postDialCheckIn.class);
        String userToken = SpUtils.getString(WhatsFreeCallApplication.getInstance().getApplicationContext(), SpConfig.USER_TOKEN, "");
        Call<HttpEntity<CheckInEntity>> call = mPostDialCheckIn.postDialCheckIn("Bearer " + userToken);
        call.enqueue(new Callback<HttpEntity<CheckInEntity>>() {
            @Override
            public void onResponse(Call<HttpEntity<CheckInEntity>> call, Response<HttpEntity<CheckInEntity>> response) {
                hideProgress(R.id.ll_progress);
                if (response != null) {
                    HttpEntity<CheckInEntity> callBack = response.body();
                    if (callBack != null) {
                        if (callBack.getErr_code() == 0) {
                            CheckInEntity mCheckIn = callBack.getData();
                            updateUserInfo(mCheckIn);
                            if (mCheckIn != null) {
                                if (!TextUtils.isEmpty(mCheckIn.getReward())) {
                                    mReward = mCheckIn.getReward();
                                }
                                List<CheckInEntity.tasks> taskses = mCheckIn.getTasks();
                                if (taskses != null && taskses.size() > 0) {
                                    mData.clear();
                                    mData.addAll(taskses);
                                }
                                showCheckInDialog();
                            }
                        } else {
                            showShortToast(callBack.getErr_msg());
                             /*签到结束*/
                            FullAdsUtils.isShowFull(FullAdsUtils.SHOW_CHECKINEND_FULL, false, FullAdsUtils.SHOW_CHECKINEND_FULL);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<HttpEntity<CheckInEntity>> call, Throwable t) {
                hideProgress(R.id.ll_progress);
                showShortToast(t.getMessage());
                 /*签到结束*/
                FullAdsUtils.isShowFull(FullAdsUtils.SHOW_CHECKINEND_FULL, false, FullAdsUtils.SHOW_CHECKINEND_FULL);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    interface getDialCheckIn {
        @GET(UrlConfig.CHECK_IN)
        Call<HttpEntity<CheckInEntity>> getDialCheckIn(@Header("Authorization") String author);
    }

    interface postDialCheckIn {
        @POST(UrlConfig.CHECK_IN)
        Call<HttpEntity<CheckInEntity>> postDialCheckIn(@Header("Authorization") String author);
    }


    @Override
    protected void onDestroy() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        super.onDestroy();
    }

    private void updateUserInfo(CheckInEntity entity) {
        SpUtils.putString(getApplicationContext(), SpConfig.USER_NAME, entity.getProfile().name);
        SpUtils.putInt(getApplicationContext(), SpConfig.USER_BALABCE, entity.getProfile().credit_micro);
        SpUtils.putString(getApplicationContext(), SpConfig.USER_STRING_BALANCE, entity.getProfile().credit_string);
        SpUtils.putString(getApplicationContext(), SpConfig.USER_PHOTO_URL, entity.getProfile().picture);
    }
}
