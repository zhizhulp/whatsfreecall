package com.baisi.whatsfreecall.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.adsconfig.NativeAdType;
import com.baisi.whatsfreecall.adsconfig.StatisticalConfig;
import com.baisi.whatsfreecall.application.WhatsFreeCallApplication;
import com.baisi.whatsfreecall.configs.MyCallType;
import com.baisi.whatsfreecall.configs.SpConfig;
import com.baisi.whatsfreecall.configs.UrlConfig;
import com.baisi.whatsfreecall.entity.CallMsgEntity;
import com.baisi.whatsfreecall.entity.RecoderEntity;
import com.baisi.whatsfreecall.entity.httpbackentity.CallEndEntity;
import com.baisi.whatsfreecall.entity.requestentity.PostCallId;
import com.baisi.whatsfreecall.sinchcall.base.CallBaseActivity;
import com.baisi.whatsfreecall.utils.adsutils.FullAdsUtils;
import com.baisi.whatsfreecall.utils.adsutils.NativeAdsUtils;
import com.baisi.whatsfreecall.utils.budleutils.BundleUtils;
import com.baisi.whatsfreecall.utils.firebaseutils.Firebase;
import com.baisi.whatsfreecall.utils.httputils.HttpClentUtils;
import com.baisi.whatsfreecall.utils.httputils.IntenetUtil;
import com.baisi.whatsfreecall.utils.sputils.SpUtils;
import com.baisi.whatsfreecall.view.CircleImageView;
import com.bestgo.adsplugin.ads.activity.ShowAdFilter;
import com.bumptech.glide.Glide;
import com.sinch.android.rtc.calling.Call;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;


public class CallRequestErrorActivity extends CallBaseActivity implements View.OnClickListener, ShowAdFilter {

    private Toolbar mCallResultToolbarNo;
    private CircleImageView mCivCallResultIconNo;
    /**
     * ANNA
     */
    private TextView mTvCallResultNameNo;
    /**
     * No Answer
     */
    private TextView mTvErrorState;
    /**
     * Redial
     */
    FrameLayout mFlWrapAds;
    /*watchVIdeo*/
    FrameLayout mFlVideo;
    Button mBtnRecharge;
    private Button mBtnCallRedial;
    private LinearLayout mLlCallRequestEmail;
    Bundle bundle;
    String callId;
    int countryflag;
    String states;
    String countryName;
    int countryCode;
    String callNumber;
    String callError;
    String contactName;
    String contactPhoto;
    private long startTime;
    private int balanceStates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_request_error);
        bundle = getIntent().getExtras();
        getMsg(bundle);
        initView();
         /*链接状态*/
        /*通话结束*/
        FullAdsUtils.isShowFull(FullAdsUtils.SHOW_ENTER_CALLEND_FULL, false, FullAdsUtils.SHOW_ENTER_CALLEND_FULL);
    }

    private void getMsg(Bundle bundle) {
        if (bundle != null) {
            callId = bundle.getString(BundleUtils.CALL_ID);
            countryflag = bundle.getInt(BundleUtils.COUNTRY_FLAG);
            countryName = bundle.getString(BundleUtils.COUNTRY_NAME);
            states = bundle.getString(BundleUtils.CALL_STATES);
            callNumber = bundle.getString(BundleUtils.CALL_NUMBER);
            callError = bundle.getString(BundleUtils.CALL_ERROR);
            contactName = bundle.getString(BundleUtils.CONTACT_NAME);
            contactPhoto = bundle.getString(BundleUtils.CONTACT_PHOTO);
            countryCode = bundle.getInt(BundleUtils.COUNTRY_CODE);
          /*  Map<String, String> map = new HashMap<>();
            map.put(StatisticalConfig.CALL_END_CALL_ID, callId);
            map.put(StatisticalConfig.CALL_END_CALL_NUMBER, callNumber.replace("+", ""));
            map.put(StatisticalConfig.CALL_END_ERROR_STATES, states);*/
            Bundle bundleMsg=new Bundle();
            bundleMsg.putString(StatisticalConfig.CALL_END_CALL_ID, callId);
            bundleMsg.putString(StatisticalConfig.CALL_END_CALL_NUMBER, callNumber.replace("+", ""));
            bundleMsg.putString(StatisticalConfig.CALL_END_ERROR_STATES, states);
            Firebase.getInstance(getApplicationContext()).logEvents(StatisticalConfig.CALL_END_ERROR, bundleMsg);
        }
        RecoderEntity recoderEntity = new RecoderEntity(contactName,
                callNumber,
                MyCallType.CALL_OUT_ERROR,
                System.currentTimeMillis(), contactPhoto, countryName, countryCode, countryflag);
        if (WhatsFreeCallApplication.getInstance().getDbManager().checkContain(recoderEntity)) {
            WhatsFreeCallApplication.getInstance().getDbManager().updateCallRecoderResult(recoderEntity);
        } else {
            WhatsFreeCallApplication.getInstance().getDbManager().insertInto(recoderEntity);
        }
        /*数据库更新之后要重新获取一下数据*/
        WhatsFreeCallApplication.getInstance().loadRecoder();
    }

    private void initView() {
        mCallResultToolbarNo = (Toolbar) findViewById(R.id.call_result_toolbar_no);
        initToolbar(mCallResultToolbarNo, R.string.call);
        mFlWrapAds = (FrameLayout) findViewById(R.id.fl_native_wrap);
        mFlVideo = (FrameLayout) findViewById(R.id.fl_watch_videos);
        mBtnRecharge = (Button) findViewById(R.id.btn_call_recharge);
        mBtnRecharge.setOnClickListener(this);
        mFlVideo.setOnClickListener(this);
        NativeAdsUtils.setAdvView(NativeAdType.NATIVE_END, mFlWrapAds, NativeAdsUtils.SHOW_CALLEND_NATIVE, false, StatisticalConfig.ENDADS);
        mCivCallResultIconNo = (CircleImageView) findViewById(R.id.civ_call_result_icon_no);
        mTvCallResultNameNo = (TextView) findViewById(R.id.tv_call_result_name_no);
        mTvCallResultNameNo.setText(callNumber);
        mTvErrorState = (TextView) findViewById(R.id.tv_error_state);
        mTvErrorState.setText(states);
        Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.SINCH_CALL_CONNECTION, StatisticalConfig.NO, states);
        mBtnCallRedial = (Button) findViewById(R.id.btn_call_redial);
        mBtnCallRedial.setOnClickListener(this);
        mLlCallRequestEmail = (LinearLayout) findViewById(R.id.ll_call_request_email_error);
        mLlCallRequestEmail.setOnClickListener(this);
        setMsg(contactName, callNumber, "+" + countryCode, contactPhoto);
        showProgress(R.id.ll_progress, R.id.pb_include, R.id.tv_progress_msg, getResources().getString(R.string.load_progree_msg), R.color.colorPrimary);
        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_call_recharge:
            case R.id.fl_watch_videos:
                skip(ChargeActivity.class);
                finish();
                break;
            case R.id.btn_call_redial:
                /*拨通电话*/
                Map<String, String> map = new HashMap<>();
                map.put("token", SpUtils.getString(getApplicationContext(), SpConfig.USER_TOKEN, ""));
                Call call = getSinchServiceInterface().callPhoneNumber("+" + countryCode + callNumber, map);
                callId = call.getCallId();
                CallMsgEntity callMsgEntity = new CallMsgEntity(callId, countryflag, countryName, countryCode, contactName, contactPhoto, callNumber);
                skip(CallScreenActivity.class, BundleUtils.getBundleUtils().getCallMsgBundle(callMsgEntity));
                finish();
                break;
            case R.id.ll_call_request_email_error:
                String title = "CallId: " + callId;
                String body = "PackageName: " + getApplicationContext().getPackageName() + "\nCountry: " + countryName + "\nPhoneNumber: +" + countryCode + callNumber +
                        "\nDevice: " + android.os.Build.MODEL + "\nNetWork: " +
                        IntenetUtil.getNetworkState(getApplicationContext()) + "\nErrorMsg: " +
                        callError + "\n" + "BalanceStates: " + balanceStates;

                if (!startEmail(title, body)) {
                    skip(FeedBackActivity.class);
                }
                break;
        }
    }

    private void setMsg(String name, String number, String code, String photoUri) {
        if (TextUtils.isEmpty(photoUri)) {
            Glide.with(this).load(R.drawable.avatar).into(mCivCallResultIconNo);
        } else {
            Glide.with(this).load(photoUri).into(mCivCallResultIconNo);
        }
        if (TextUtils.isEmpty(name)) {
            mTvCallResultNameNo.setText(code + number);
        } else {
            mTvCallResultNameNo.setText(name);
        }

    }

    @Override
    public boolean allowShowAd() {
        return false;
    }

    Handler handler = new Handler();
    //延迟请求信息
    Runnable runnable = (new Runnable() {
        @Override
        public void run() {
            startTime = System.currentTimeMillis();
            getCallEndInfos(callId);
        }
    });

    /**
     * 余额不足显示的充值
     *
     * @param isUnBalance
     */
    private void setShowBalanceUnKnow(boolean isUnBalance) {
        if (isUnBalance) {
            mBtnRecharge.setVisibility(View.VISIBLE);
            mBtnCallRedial.setVisibility(View.GONE);
            mFlVideo.setVisibility(View.VISIBLE);
            mTvErrorState.setText(R.string.balance_un);
        }
    }

    interface GetCallEnd_Interface {
        @POST(UrlConfig.CALL_LOG)
        retrofit2.Call<CallEndEntity> getCallInfo(@Header("Authorization") String author, @Body PostCallId body);
    }

    public void getCallEndInfos(String callId) {
        //步骤4:创建Retrofit对象
        OkHttpClient okhttpClient = HttpClentUtils.getOkhttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlConfig.BASE_URL) // 设置 网络请求 Url
                .client(okhttpClient)
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();
        // 步骤5:创建 网络请求接口 的实例
        GetCallEnd_Interface request = retrofit.create(GetCallEnd_Interface.class);
        //对 发送请求 进行封装( //对 发送请求 进行封装(因为传递json 所以将参数用实体包裹转成json))
        PostCallId postCallId = new PostCallId(callId);
        String userToken = SpUtils.getString(WhatsFreeCallApplication.getInstance().getApplicationContext(), SpConfig.USER_TOKEN, "");
        retrofit2.Call<CallEndEntity> call = request.getCallInfo("Bearer " + userToken, postCallId);
        call.enqueue(new Callback<CallEndEntity>() {
            @Override
            public void onResponse(retrofit2.Call<CallEndEntity> call, Response<CallEndEntity> response) {
                long endTime = System.currentTimeMillis() - startTime;
                if (response != null) {
                    CallEndEntity callEndEntity = response.body();
                    if (callEndEntity != null) {
                        if (callEndEntity.getErr_code() == 0) {
                            updateUserInfo(callEndEntity);
                            Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.LOAD_CALLEND_INFO, StatisticalConfig.OK, endTime);
                            balanceStates = callEndEntity.getData().getDeny_reason();
                            switch (callEndEntity.getData().getDeny_reason()) {
                                default:
                                    break;
                                case 3005:
                                    setShowBalanceUnKnow(true);
                                    break;
                            }
                        } else {
                            showShortToast(callEndEntity.getErr_msg());
                            Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.LOAD_CALLEND_INFO, StatisticalConfig.NO, endTime);
                            Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.LOAD_CALLEND_INFO, StatisticalConfig.LOAD_CALLEND_INFO_ERROR, callEndEntity.getErr_code() + callEndEntity.getErr_msg());
                        }
                    } else {
                        Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.LOAD_CALLEND_INFO, StatisticalConfig.NULLDATA);
                    }
                    hideProgress(R.id.ll_progress);
                } else {
                    //统计
                    System.out.println("CallResultErrorActivity.onResponse空值");
                    Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.LOAD_CALLEND_INFO, StatisticalConfig.NO, endTime);
                    Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.LOAD_CALLEND_INFO, StatisticalConfig.LOAD_CALLEND_INFO_ERROR, StatisticalConfig.NULLDATA);
                    hideProgress(R.id.ll_progress);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<CallEndEntity> call, Throwable t) {
                long endTime = System.currentTimeMillis() - startTime;
                Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.LOAD_CALLEND_INFO, StatisticalConfig.NO, endTime);
                Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.LOAD_CALLEND_INFO, StatisticalConfig.LOAD_CALLEND_INFO_ERROR, t.getMessage().toString());
                System.out.println("CallResultErrorActivity.onFailure" + t.getMessage());
                hideProgress(R.id.ll_progress);
            }
        });
    }

    private void updateUserInfo(CallEndEntity callEndEntity) {
        SpUtils.putString(getApplicationContext(), SpConfig.USER_NAME, callEndEntity.getData().getProfile().getName());
        SpUtils.putInt(getApplicationContext(), SpConfig.USER_BALABCE, callEndEntity.getData().getProfile().getCredit_micro());
        SpUtils.putString(getApplicationContext(), SpConfig.USER_STRING_BALANCE, callEndEntity.getData().getProfile().getCredit_string());
        SpUtils.putString(getApplicationContext(), SpConfig.USER_PHOTO_URL, callEndEntity.getData().getProfile().getPicture());
    }
}
