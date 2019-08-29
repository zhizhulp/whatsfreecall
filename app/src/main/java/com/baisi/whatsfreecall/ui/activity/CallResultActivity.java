package com.baisi.whatsfreecall.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.adsconfig.NativeAdType;
import com.baisi.whatsfreecall.adsconfig.StatisticalConfig;
import com.baisi.whatsfreecall.application.WhatsFreeCallApplication;
import com.baisi.whatsfreecall.base.BaseActivity;
import com.baisi.whatsfreecall.configs.MyCallType;
import com.baisi.whatsfreecall.configs.SpConfig;
import com.baisi.whatsfreecall.configs.UrlConfig;
import com.baisi.whatsfreecall.entity.RecoderEntity;
import com.baisi.whatsfreecall.entity.httpbackentity.CallEndEntity;
import com.baisi.whatsfreecall.entity.requestentity.PostCallId;
import com.baisi.whatsfreecall.utils.adsutils.FullAdsUtils;
import com.baisi.whatsfreecall.utils.adsutils.NativeAdsUtils;
import com.baisi.whatsfreecall.utils.budleutils.BundleUtils;
import com.baisi.whatsfreecall.utils.firebaseutils.Firebase;
import com.baisi.whatsfreecall.utils.httputils.HttpClentUtils;
import com.baisi.whatsfreecall.utils.httputils.NetWorkAuxiliaryUtil;
import com.baisi.whatsfreecall.utils.sputils.SpUtils;
import com.baisi.whatsfreecall.view.CircleImageView;
import com.bestgo.adsplugin.ads.activity.ShowAdFilter;
import com.bumptech.glide.Glide;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;


public class CallResultActivity extends BaseActivity implements View.OnClickListener, ShowAdFilter {

    private Toolbar mCallResultToolbar;
    private CircleImageView mCivCallResultIcon;
    /**
     * ANNA
     */
    private TextView mTvCallResultName;
    /**
     * 2mins 12 seceonds
     */
    private TextView mTvCallResultTime;
    private LinearLayout mLlCallRequestEmail;
    private String callId;
    private String msg;
    private String callNumber;
    private String countryName;
    private String balance;
    private String express;
    private int duraction;
    private int countryFlag;
    String contactName;
    String contactPhoto;
    int countryCode;
    /**
     * Expense $0.20
     */
    private TextView mTvExpenseResult;
    /**
     * Balance $2.16
     */
    private TextView mTvBalanceResult;
    private FrameLayout mFlWrapAds;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_result);
        Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.SINCH_CALL_CONNECTION, StatisticalConfig.OK, "");
        getMsg();
        initView();
          /*链接状态*/
        NetWorkAuxiliaryUtil.getInstance(getApplicationContext()).sendSinchFireBase();
         /*通话结束*/
        FullAdsUtils.isShowFull(FullAdsUtils.SHOW_ENTER_CALLEND_FULL, false, FullAdsUtils.SHOW_ENTER_CALLEND_FULL);
    }

    public void getMsg() {
        Bundle bundle = getIntent().getExtras();
        callId = bundle.getString(BundleUtils.CALL_ID);
        //延迟1秒请求信息
        handler.postDelayed(runnable, 1000);
        showProgress(R.id.ll_progress, R.id.pb_include, R.id.tv_progress_msg, getResources().getString(R.string.load_progree_msg), R.color.colorPrimary);
        callNumber = bundle.getString(BundleUtils.CALL_NUMBER);
        msg = bundle.getString(BundleUtils.CALL_ERROR);
        countryName = bundle.getString(BundleUtils.COUNTRY_NAME);
        countryFlag = bundle.getInt(BundleUtils.COUNTRY_FLAG);
        duraction = bundle.getInt(BundleUtils.CALL_TIME);
        contactName = bundle.getString(BundleUtils.CONTACT_NAME);
        contactPhoto = bundle.getString(BundleUtils.CONTACT_PHOTO);
        countryCode = bundle.getInt(BundleUtils.COUNTRY_CODE);
        RecoderEntity recoderEntity = new RecoderEntity(contactName,
                callNumber,
                MyCallType.CALL_OUT_SUCCESS,
                System.currentTimeMillis(), contactPhoto, countryName, countryCode, countryFlag);
        if (WhatsFreeCallApplication.getInstance().getDbManager().checkContain(recoderEntity)) {
            WhatsFreeCallApplication.getInstance().getDbManager().updateCallRecoderResult(recoderEntity);
        } else {
            WhatsFreeCallApplication.getInstance().getDbManager().insertInto(recoderEntity);
        }
        /*数据库更新之后要重新获取一下数据*/
        WhatsFreeCallApplication.getInstance().loadRecoder();
    }

    private void initView() {
        mCallResultToolbar = (Toolbar) findViewById(R.id.call_result_toolbar);
        initToolbar(mCallResultToolbar, null);
        mFlWrapAds = (FrameLayout) findViewById(R.id.fl_native_wrap);
        NativeAdsUtils.setAdvView(NativeAdType.NATIVE_END, mFlWrapAds, NativeAdsUtils.SHOW_CALLEND_NATIVE, false, StatisticalConfig.ENDADS);
        mCivCallResultIcon = (CircleImageView) findViewById(R.id.civ_call_result_icon);
        mTvCallResultName = (TextView) findViewById(R.id.tv_call_result_name);
        mTvCallResultTime = (TextView) findViewById(R.id.tv_call_result_time);
        mLlCallRequestEmail = (LinearLayout) findViewById(R.id.ll_call_request_email);
        mLlCallRequestEmail.setOnClickListener(this);
        setMsg(contactName, callNumber, "+" + countryCode, contactPhoto);
        mTvExpenseResult = (TextView) findViewById(R.id.tv_expense_result);
        mTvBalanceResult = (TextView) findViewById(R.id.tv_balance_result);
    }

    private void setMsg(String name, String number, String code, String photoUri) {
        if (TextUtils.isEmpty(photoUri)) {
            Glide.with(this).load(R.drawable.avatar).into(mCivCallResultIcon);
        } else {
            Glide.with(this).load(photoUri).into(mCivCallResultIcon);
        }
        if (TextUtils.isEmpty(name)) {
            mTvCallResultName.setText(code + number);
        } else {
            mTvCallResultName.setText(name);
        }
        if (duraction / 60 == 0) {
            mTvCallResultTime.setText(duraction % 60 + "seconds");
        } else if (duraction % 60 == 0) {
            mTvCallResultTime.setText(duraction / 60 + "min");
        } else {
            mTvCallResultTime.setText(duraction / 60 + "min" + duraction % 60 + "seconds");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.ll_call_request_email:
                String title = "CallId: " + callId;
                String body = "PackageName: " + getApplicationContext().getPackageName() + "\nCountry: " + countryName +
                        "\nPhoneNumber: +" + countryCode + callNumber;
                if (!startEmail(title, body)) {
                    skip(FeedBackActivity.class);
                }
                break;
        }
    }

    @Override
    public boolean allowShowAd() {
        return false;
    }


    interface GetCallEnd_Interface {
        @POST(UrlConfig.CALL_LOG)
        Call<CallEndEntity> getCallInfo(@Header("Authorization") String author, @Body PostCallId body);
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
        Call<CallEndEntity> call = request.getCallInfo("Bearer " + userToken, postCallId);
        call.enqueue(new Callback<CallEndEntity>() {
            @Override
            public void onResponse(Call<CallEndEntity> call, Response<CallEndEntity> response) {
                long endTime = System.currentTimeMillis() - startTime;
                if (response != null) {
                    CallEndEntity callEndEntity = response.body();
                    if (callEndEntity != null) {
                        if (callEndEntity.getErr_code() == 0) {
                            updateUserInfo(callEndEntity);
                            setShowMsg(callEndEntity);
                            Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.LOAD_CALLEND_INFO, StatisticalConfig.OK, endTime);
                        } else {
                            showShortToast(callEndEntity.getErr_msg());
                            Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.LOAD_CALLEND_INFO, StatisticalConfig.NO, endTime);
                            Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.LOAD_CALLEND_INFO, StatisticalConfig.LOAD_CALLEND_INFO_ERROR, callEndEntity.getErr_code() + callEndEntity.getErr_msg());
                        }
                    }
                    hideProgress(R.id.ll_progress);
                } else {
                    //统计
                    System.out.println("CallResultActivity.onResponse空值");
                    Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.LOAD_CALLEND_INFO, StatisticalConfig.NO, endTime);
                    Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.LOAD_CALLEND_INFO, StatisticalConfig.LOAD_CALLEND_INFO_ERROR, StatisticalConfig.NULLDATA);
                    hideProgress(R.id.ll_progress);
                }
            }

            @Override
            public void onFailure(Call<CallEndEntity> call, Throwable t) {
                long endTime = System.currentTimeMillis() - startTime;
                Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.LOAD_CALLEND_INFO, StatisticalConfig.NO, endTime);
                Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.LOAD_CALLEND_INFO, StatisticalConfig.LOAD_CALLEND_INFO_ERROR, t.getMessage().toString());
                System.out.println("CallResultActivity.onFailure" + t.getMessage());
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

    private void setShowMsg(CallEndEntity callEndEntity) {
        hideProgress(R.id.ll_progress);
        mTvExpenseResult.setText("Expense " + callEndEntity.getData().getCost_string());
        mTvBalanceResult.setText("Balance " + callEndEntity.getData().getProfile().getCredit_string());
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
}
