package com.baisi.whatsfreecall.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.adsconfig.StatisticalConfig;
import com.baisi.whatsfreecall.application.WhatsFreeCallApplication;
import com.baisi.whatsfreecall.configs.SpConfig;
import com.baisi.whatsfreecall.configs.UrlConfig;
import com.baisi.whatsfreecall.entity.ConsumeEntity;
import com.baisi.whatsfreecall.entity.httpbackentity.LoginConsumeBackEntity;
import com.baisi.whatsfreecall.entity.httpbackentity.PayloadEntity;
import com.baisi.whatsfreecall.entity.requestentity.MacAdressWif;
import com.baisi.whatsfreecall.entity.requestentity.SignEntity;
import com.baisi.whatsfreecall.sinchcall.base.CallBaseActivity;
import com.baisi.whatsfreecall.sinchcall.service.SinchService;
import com.baisi.whatsfreecall.utils.CheckToken;
import com.baisi.whatsfreecall.utils.GoogleUtilsLoginAndPay;
import com.baisi.whatsfreecall.utils.budleutils.BundleUtils;
import com.baisi.whatsfreecall.utils.firebaseutils.Firebase;
import com.baisi.whatsfreecall.utils.httputils.HttpClentUtils;
import com.baisi.whatsfreecall.utils.httputils.WifiUtils;
import com.baisi.whatsfreecall.utils.sputils.SpUtils;
import com.baisi.whatsfreecall.utils.utilpay.IabBroadcastReceiver;
import com.baisi.whatsfreecall.utils.utilpay.IabHelper;
import com.baisi.whatsfreecall.utils.utilpay.IabResult;
import com.baisi.whatsfreecall.utils.utilpay.Purchase;
import com.bestgo.adsplugin.ads.activity.ShowAdFilter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.sinch.android.rtc.SinchError;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

import static com.baisi.whatsfreecall.utils.GoogleUtilsLoginAndPay.RC_SIGN_IN;


public class LoginActivity extends CallBaseActivity implements ShowAdFilter {
    /*从哪里进入此页面*/
    private int mEnterPage;
    private Bundle mBundle;
    /*login*/
    GoogleUtilsLoginAndPay utils;

    GoogleSignInAccount account;
    /*retorfit*/
    private Retrofit retrofit;
    private long time;
    /*view*/
    private RelativeLayout mIncludeLogin;
    private FrameLayout mIncludeCharge;
    private Button mBtnLogin;
    private Button mBtnCancel;
    private ProgressBar mPbSlpshaOrChargeIn;
    /*ProgressBar*/
    private LinearLayout mLlProgressBar;
    private ProgressBar mPb;
    private TextView mTvShowProressMsg;
    /*Charge界面进入的库存*/
    /*pay*/
    Purchase mPurchase;
    /*Charge页面进入的商品ID*/
    private String skuIdCharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        utils = new GoogleUtilsLoginAndPay(LoginActivity.this);
        mBundle = getIntent().getExtras();
        mEnterPage = mBundle.getInt(BundleUtils.ENTER_THE_PAGE);
        initView();
        time = System.currentTimeMillis();
        enterPage(mEnterPage);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if (CheckToken.checkToken()) {//true 表示未登录
            finish();
        }
        if (isProgressShow(mLlProgressBar)) {
            hideProgress(mLlProgressBar);
        }
    }

    private void initView() {
        initProgress();
        mIncludeLogin = (RelativeLayout) findViewById(R.id.include_login);
        mIncludeCharge = (FrameLayout) findViewById(R.id.include_charge);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(listener);
        mBtnCancel = (Button) findViewById(R.id.btn_cancel);
        mBtnCancel.setOnClickListener(listener);
        mPbSlpshaOrChargeIn = (ProgressBar) findViewById(R.id.pb_splsha_or_charge_in);
        if (CheckToken.checkToken()) {//未登录
            mIncludeLogin.setVisibility(View.VISIBLE);
            mIncludeCharge.setVisibility(View.GONE);
        } else {//登陆
            mIncludeLogin.setVisibility(View.GONE);
            mIncludeCharge.setVisibility(View.VISIBLE);
        }
    }

    private void initProgress() {
        mTvShowProressMsg = (TextView) findViewById(R.id.tv_progress_msg);
        mPb = (ProgressBar) findViewById(R.id.pb_include);
        mLlProgressBar = (LinearLayout) findViewById(R.id.ll_progress);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_login:
                    signIn();
                    Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.LOGIN_NATIVE, StatisticalConfig.RESULT_CODE_CLICK);
                    showProgress(mLlProgressBar, mPb, mTvShowProressMsg, "Check the landing environment", R.color.colorPrimary);
                    break;
                case R.id.btn_cancel:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    private void enterPage(int enterPage) {
        switch (enterPage) {
            case BundleUtils.ENTER_SPLASH:
               /*splash dialog进入 一定有库存*/
                System.out.println("LoginActivity.enterPage" + "闪屏界面进入有库存检查是否登陆？消耗：登陆");
                mIncludeCharge.setVisibility(View.VISIBLE);
                mPurchase = mBundle.getParcelable(BundleUtils.INVENTY_PURCHASE);
                startHelper();
                break;
            case BundleUtils.ENTER_CALL:
                System.out.println("LoginActivity.enterPage" + "打电话进入登陆结束后返回首页或者call 页面");
                mIncludeLogin.setVisibility(View.VISIBLE);
                break;
            case BundleUtils.ENTER_CHARGE_VIDEO:
                System.out.println("LoginActivity.enterPage" + "视频广告进入登陆结束后返回首页或者call 页面");
                mIncludeLogin.setVisibility(View.VISIBLE);
                break;
            case BundleUtils.ENTER_CHARGE:
                System.out.println("LoginActivity.enterPage" + "充值进入检查是否登陆？购买消耗：登陆购买消耗");
                startHelper();
                skuIdCharge = mBundle.getString(BundleUtils.SKU_ID);
                mIncludeCharge.setVisibility(View.VISIBLE);
                break;
        }
    }

    /****************************支付相关**********************************/
    public static String TAG = "WhatsFreeCall";
    // The helper object
    IabHelper mHelper;
    //提供购买通知
    IabBroadcastReceiver mBroadcastReceiver;

    //启动helper
    public void startHelper() {
        mHelper = new IabHelper(this, getResources().getString(R.string.base64EncodedPublicKey));
        /*测试阶段查看log*/
        mHelper.enableDebugLogging(true);
        //开始设置 异步的监听
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    /*初始化支付失败*/
                    skip(ErrorActivity.class, BundleUtils.getBundleUtils().getErrorBundle("CreateBilling: " + result));
                    finish();
                    return;
                }
                if (mHelper == null) {
                    return;
                }
                mBroadcastReceiver = new IabBroadcastReceiver(IabListener);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                Log.d(TAG, "Setup successful. Querying inventory.");
                switch (mEnterPage) {
                    case BundleUtils.ENTER_SPLASH:
                        /**
                         * 一定有库存
                         * 直接检测是否登陆
                         * 没登陆就去登陆并请求消耗并本地消耗
                         * 登陆了就直接请求消耗并本地消耗
                         */
                        System.out.println("Dilaog闪屏界面进入检查库存检查是否登陆？消耗：登陆");
                        if (CheckToken.checkToken()) {
                            if (account == null) {
                                System.out.println("SPLASH_DIALOG" + "有库存未登录，登陆");

                                signIn();
                            }
                        } else {
                            System.out.println("SPLASH" + "有库存登录，消耗");

                            startConsume(null, mPurchase);
                        }
                        break;
                    case BundleUtils.ENTER_CHARGE:
                        /**
                         * charge进入一定没有库存所以直接检测是否登陆
                         * 登陆就直接请求payid》购买》请求消耗》消耗
                         * 没登陆就登陆然后请求payid》够买》请求消耗》消耗
                         */
                        System.out.println("充值进入检查库存检查是否登陆？登陆购买消耗：购买直接消耗");
                        if (CheckToken.checkToken()) {
                            System.out.println("charge>>>>>-》登陆");
                            mPbSlpshaOrChargeIn.setVisibility(View.INVISIBLE);
                            signIn();
                        } else {
                              /*没有库存且没有登陆就要先登陆 在获取wifiMac成功之后再进行设置payLoadid*/
                            System.out.println("charge>>>>>-》获取商品pay_id");
                            startGetPayId();
                        }
                        break;
                    case BundleUtils.ENTER_CALL:
                        break;
                }
            }
        });

    }

    IabBroadcastReceiver.IabBroadcastListener IabListener = new IabBroadcastReceiver.IabBroadcastListener() {
        @Override
        public void receivedBroadcast() {
            // Received a broadcast notification that the inventory of items has changed
            //收到此通知说明库存已经发生变化
            Log.d(TAG, "Received broadcast notification. Querying inventory.");
        }
    };

    /**
     * 消耗
     *
     * @param info 要消耗的商品
     */
    private void consume(Purchase info) {
        try {
            if (mHelper == null) {
                return;
            }
            mHelper.consumeAsync(info, mConsumeFinishedListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            skip(ErrorActivity.class, BundleUtils.getBundleUtils().getErrorBundle("conusumeAsync " + e.toString()));
            finish();
            e.printStackTrace();
        }
    }

    /*消耗监听*/
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        @Override
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);
            // if we were disposed of in the meantime, quit.
            if (mHelper == null) {
                return;
            }
            if (result.isSuccess()) {
                switch (mEnterPage) {

                    case BundleUtils.ENTER_CHARGE:
                        finish();
                        System.out.println("charge>>>>>消耗完成SUCCESS" + (System.currentTimeMillis() - time));

                        break;
                    case BundleUtils.ENTER_SPLASH:
                        System.out.println("SPLASH>>>>>消耗完成SUCCESS");

                        break;
                }
              /*消耗成功*/

                skip(ChargeSuccessActivity.class);
                finish();
                System.out.println("HomeActivity.onConsumeFinished>>>>>>>>>>" + "消耗成功");
            } else {
                switch (mEnterPage) {
                    case BundleUtils.ENTER_CHARGE:
                        System.out.println("charge>>>>>消耗失败");
                        break;
                    case BundleUtils.ENTER_SPLASH:
                        System.out.println("SPLASH>>>>>消耗失败");
                        break;
                }
                skip(ErrorActivity.class, BundleUtils.getBundleUtils().getErrorBundle("consumEnd " + result));
                finish();
                /*complain("Error while consuming: " + result);*/
                System.out.println("HomeActivity.onConsumeFinished>>>>>>>>>>" + "消耗失败" + result.getMessage().toString());
            }
        }
    };

    /**
     * 购买
     *
     * @param activity
     * @param skuid    商品id
     */
    private void buySku(Activity activity, String skuid, String payLoadId) {
        try {
            mPbSlpshaOrChargeIn.setVisibility(View.INVISIBLE);
            if (mHelper == null) {
                return;
            }
            mHelper.launchPurchaseFlow(activity, skuid, 1000, onIabPurchaseFinishedListener, payLoadId);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
            skip(ErrorActivity.class, BundleUtils.getBundleUtils().getErrorBundle("buy " + e.toString()));
            finish();
            System.out.println("MainActivity.onClick>>>>>>>>>>>>" + e.toString());
        }
    }

    /*购买结束监听*/
    IabHelper.OnIabPurchaseFinishedListener onIabPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase info) {
            System.out.println("MainActivity.onIabPurchaseFinished>>>>>>" + "Purchase finished: " + result + ", purchase: " + info);
            if (mHelper == null) {
                return;
            }
            if (result.isFailure()) {
                System.out.println("LoginActivity.onIabPurchaseFinished" + "购买失败");
                /*complain("Erroe purchasing:" + result);*/
                skip(ErrorActivity.class, BundleUtils.getBundleUtils().getErrorBundle("purchasing " + result));
                finish();
                return;
            }
            /*用户未登录token 是空 则要先去登陆google 同时登陆自己的服务器 并携带购买参数*/
            if (result.isSuccess() && info != null) {
                System.out.println("LoginActivity.onIabPurchaseFinished" + "购买成功");
                Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.BILLING_PAY_OK, info.getSku());
                /*消耗*/
                System.out.println("LoginActivity.onIabPurchaseFinished" + "charge>>>>>开始消耗请求服务器");
                mPbSlpshaOrChargeIn.setVisibility(View.VISIBLE);
                startConsume(null, info);
            }
        }
    };

    @Override
    public boolean allowShowAd() {
        return false;
    }

    /*相关网络请求*/

    public interface PostLogin_Interface {
        @POST(UrlConfig.LOGIN)
        Call<LoginConsumeBackEntity> getCall(@Header("Authorization") String userToken, @Body SignEntity values);
    }

    /**
     * 登陆接口--登陆
     *
     * @param token
     */
    public void startLogin(String token) {
        //步骤4:创建Retrofit对象
        OkHttpClient okhttpClient = HttpClentUtils.getOkhttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlConfig.BASE_URL) // 设置 网络请求 Url
                .client(okhttpClient)
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();
        PostLogin_Interface request = retrofit.create(PostLogin_Interface.class);
        //对 发送请求 进行封装( //对 发送请求 进行封装(因为传递json 所以将参数用实体包裹转成json))
        SignEntity sign = new SignEntity(token, SignEntity.NATIVE,WhatsFreeCallApplication.getInstance().getAppInstandID());
        String userToken = SpUtils.getString(WhatsFreeCallApplication.getInstance().getApplicationContext(), SpConfig.USER_TOKEN, "");
        Call<LoginConsumeBackEntity> call = request.getCall("Bearer " + userToken, sign);
        call.enqueue(new Callback<LoginConsumeBackEntity>() {
            @Override
            public void onResponse(Call<LoginConsumeBackEntity> call, Response<LoginConsumeBackEntity> response) {
                if (response != null) {
                    LoginConsumeBackEntity loginConsumeBackEntity = response.body();
                    if (loginConsumeBackEntity != null) {
                        /*更新用户信息*/
                        if (loginConsumeBackEntity.getErr_code() == 0) {
                            updateUserInfo(loginConsumeBackEntity);
                            signSinch();
                            switch (mEnterPage) {
                                case BundleUtils.ENTER_SPLASH:

                                    break;
                                case BundleUtils.ENTER_CALL:
                                 /*打电话进入直接关闭*/
                                    hideProgress(mLlProgressBar);
                                    showShortToast("Landed successfully");
                                    finish();
                                    break;
                                case BundleUtils.ENTER_CHARGE_VIDEO:
                                 /*视频进入直接关闭*/
                                    hideProgress(mLlProgressBar);
                                    showShortToast("Landed successfully");
                                    finish();
                                    break;
                                case BundleUtils.ENTER_CHARGE:
                                 /*没有库存且没有登陆就要先登陆 在获取wifiMac成功之后再进行设置payLoadid*/
                                    System.out.println("charge>>>>>" + "获取pay_id");
                                    mIncludeLogin.setVisibility(View.GONE);
                                    startGetPayId();
                                    break;
                            }
                        } else {
                            skip(ErrorActivity.class, BundleUtils.getBundleUtils().getErrorBundle("Login " + loginConsumeBackEntity.getErr_code()));
                            finish();
                        }
                    } else {
                        //统计 或者进error
                        skip(ErrorActivity.class, BundleUtils.getBundleUtils().getErrorBundle("Login " + "response.body null"));
                        finish();
                    }
                } else {
                    skip(ErrorActivity.class, BundleUtils.getBundleUtils().getErrorBundle("Login " + "response null"));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<LoginConsumeBackEntity> call, Throwable t) {
                System.out.println("请求失败登陆");
                System.out.println(t.getMessage());
                skip(ErrorActivity.class, BundleUtils.getBundleUtils().getErrorBundle("Login " + t.getMessage()));
                finish();
            }
        });
    }


    public interface Consume_Interface {
        @POST(UrlConfig.PAY_PURCHASE)
        Call<LoginConsumeBackEntity> getCall(@Header("Authorization") String author, @Body ConsumeEntity body);
    }

    /**
     * @param googleToken 当未登录的时候传递googleToken 与订单 登陆与消耗在服务端同时进行
     * @param purchase
     */
    public void startConsume(String googleToken, final Purchase purchase) {
        //步骤4:创建Retrofit对象
        OkHttpClient okhttpClient = HttpClentUtils.getOkhttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlConfig.BASE_URL) // 设置 网络请求 Url
                .client(okhttpClient)
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();
        // 步骤5:创建 网络请求接口 的实例
        Consume_Interface request = retrofit.create(Consume_Interface.class);
        //对 发送请求 进行封装( //对 发送请求 进行封装(因为传递json 所以将参数用实体包裹转成json))
        final ConsumeEntity consume = new ConsumeEntity(purchase.getDeveloperPayload(), purchase.getPackageName(), purchase.getSku(),
                purchase.getToken(), purchase.getOriginalJson(), purchase.getSignature(), googleToken);
        String userToken = SpUtils.getString(WhatsFreeCallApplication.getInstance().getApplicationContext(), SpConfig.USER_TOKEN, "");
        Call<LoginConsumeBackEntity> call = request.getCall("Bearer " + userToken, consume);
        call.enqueue(new Callback<LoginConsumeBackEntity>() {
            @Override
            public void onResponse(Call<LoginConsumeBackEntity> call, Response<LoginConsumeBackEntity> response) {
                Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.CONSUME_RESPONSE_CODE,response.code());
                if (response != null) {
                    LoginConsumeBackEntity consumeBackEntity = response.body();
                    if (consumeBackEntity != null) {
                        if (consumeBackEntity.getErr_code() == 0) {
                            updateUserInfo(consumeBackEntity);
                            //消耗
                            if (mHelper != null) {
                                /*mny*/
                                consume(purchase);
                            }
                        } else {
                            Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.CONSUME_RESPONSE_MSG,consumeBackEntity.getErr_code());
                            skip(ErrorActivity.class, BundleUtils.getBundleUtils().getErrorBundle("startConsume" + consumeBackEntity.getErr_code() + ""));
                            finish();
                        }
                        switch (mEnterPage) {
                            case BundleUtils.ENTER_CHARGE:
                                System.out.println("LoginActivity.onResponse" + "charge>>>>>消耗请求服务器结束并真正消耗");
                                break;
                            case BundleUtils.ENTER_SPLASH:
                                System.out.println("LoginActivity.onResponse" + "SPLASH>>>>>消耗请求服务器结束并真正消耗");
                                break;
                        }
                    } else {
                        Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.CONSUME_RESPONSE_MSG,response.message());
                        skip(ErrorActivity.class, BundleUtils.getBundleUtils().getErrorBundle("startConsume" + "response.body null"));
                        finish();
                    }
                } else {
                    //统计 或者进error
                    Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.CONSUME_RESPONSE_MSG,StatisticalConfig.RRESPONSE_NULL);
                    skip(ErrorActivity.class, BundleUtils.getBundleUtils().getErrorBundle("startConsume" + "response null"));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<LoginConsumeBackEntity> call, Throwable t) {

                System.out.println("请求失败消耗");
                skip(ErrorActivity.class, BundleUtils.getBundleUtils().getErrorBundle("startConsume " + t.getMessage()));
                finish();
                System.out.println(t.getMessage());
            }
        });
    }


    public interface PayLoadInterface {
        @POST(UrlConfig.DEVELOPED_PAY_LOAD)
        Call<PayloadEntity> getCall(@Header("Authorization") String author, @Body MacAdressWif body);
    }

    /**
     * 获取pay_id
     */
    private void startGetPayId() {
        //步骤4:创建Retrofit对象
        OkHttpClient okhttpClient = HttpClentUtils.getOkhttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlConfig.BASE_URL) // 设置 网络请求 Url
                .client(okhttpClient)
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();
        // 步骤5:创建 网络请求接口 的实例
        PayLoadInterface request = retrofit.create(PayLoadInterface.class);
        //对 发送请求 进行封装( //对 发送请求 进行封装(因为传递json 所以将参数用实体包裹转成json))
        Gson gson = new Gson();
        MacAdressWif macAdressWif = new MacAdressWif(WifiUtils.getMacAddress(getApplicationContext()));
        String userToken = SpUtils.getString(WhatsFreeCallApplication.getInstance().getApplicationContext(), SpConfig.USER_TOKEN, "");
        Call<PayloadEntity> call = request.getCall("Bearer " + userToken, macAdressWif);
        call.enqueue(new Callback<PayloadEntity>() {
            @Override
            public void onResponse(Call<PayloadEntity> call, Response<PayloadEntity> response) {
                if (response != null) {
                    PayloadEntity payloadEntity = response.body();
                    if (payloadEntity != null) {
                        if (payloadEntity.getErr_code() == 0) {
                            System.out.println("charge>>>>buy" + "开始购买");

                            mPbSlpshaOrChargeIn.setVisibility(View.INVISIBLE);
                            buySku(LoginActivity.this, skuIdCharge, payloadEntity.getData().getPay_id());
                        } else {

                            System.out.println("charge>>>>buy" + "error:" + response.body().toString());
                            skip(ErrorActivity.class, BundleUtils.getBundleUtils().getErrorBundle("getPayId " + payloadEntity.getErr_code() + " " + payloadEntity.getErr_msg()));
                            finish();
                        }
                    } else {
                        //统计 或者进error
                        skip(ErrorActivity.class, BundleUtils.getBundleUtils().getErrorBundle("getPayId " + "response.body null"));
                        finish();
                    }
                } else {
                    skip(ErrorActivity.class, BundleUtils.getBundleUtils().getErrorBundle("getPayId " + "response null"));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PayloadEntity> call, Throwable t) {
                skip(ErrorActivity.class, BundleUtils.getBundleUtils().getErrorBundle("getPayId " + t.getMessage().toString()));
                finish();
                System.out.println("LoginActivity.onFailure>>>>>" + "获取失败" + t.getMessage().toString());
            }
        });
    }

    /************网络请求结束**************/

    /*登陆*/
    private void signIn() {
        System.out.println("LoginActivity.signIn" + "account是空");

        Intent intent = utils.getGoogleSignClient().getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        // login Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        /*pay*/
        if (mHelper == null) {
            return;
        }
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.LOGIN_NATIVE,StatisticalConfig.OK);
            account = completedTask.getResult(ApiException.class);
            switch (mEnterPage) {
                case BundleUtils.ENTER_SPLASH:
                      /*登陆服务器*/
                    startConsume(account.getIdToken(), mPurchase);

                    break;
                case BundleUtils.ENTER_CHARGE:
                    if (mPurchase != null) {
                        /*未登录有库存*/
                        System.out.println("charge>>>>>LoginActivity.signIn" + "未登录有库存");
                        startConsume(account.getIdToken(), mPurchase);
                    } else {
                        /*未登录没库存登陆购买消耗*/
                        System.out.println("charge>>>>>LoginActivity.signIn" + "未登陆没有库存先登陆");
                        mPbSlpshaOrChargeIn.setVisibility(View.VISIBLE);
                        startLogin(account.getIdToken());
                    }

                    break;
                case BundleUtils.ENTER_CALL:
                    showProgress(mLlProgressBar, mPb, mTvShowProressMsg, "Landing...", R.color.colorPrimary);
                    startLogin(account.getIdToken());
                    break;
                case BundleUtils.ENTER_CHARGE_VIDEO:
                    showProgress(mLlProgressBar, mPb, mTvShowProressMsg, "Landing...", R.color.colorPrimary);
                    startLogin(account.getIdToken());
                    break;
                default:
                    break;
            }

        } catch (ApiException e) {
            Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.LOGIN_NATIVE,StatisticalConfig.NO);
            Log.w("SignActivity", "signInResult:failed code=" + e.getStatusCode() + e.toString());
            skip(ErrorActivity.class, BundleUtils.getBundleUtils().getErrorBundle("Sign " + e.getStatusCode() + e.toString(), BundleUtils.FLAGACTIVITY));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        signOut();
        utils.getGoogleSignClient().signOut();
        // very important:
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }

    private void updateUserInfo(LoginConsumeBackEntity consumeBackEntity) {
        SpUtils.putString(getApplicationContext(), SpConfig.USER_TOKEN, consumeBackEntity.getData().getToken());
        SpUtils.putString(getApplicationContext(), SpConfig.USER_NAME, consumeBackEntity.getData().getProfile().getName());
        SpUtils.putInt(getApplicationContext(), SpConfig.USER_BALABCE, consumeBackEntity.getData().getProfile().getCredit_micro());
        SpUtils.putString(getApplicationContext(), SpConfig.USER_STRING_BALANCE, consumeBackEntity.getData().getProfile().getCredit_string());
        SpUtils.putString(getApplicationContext(), SpConfig.USER_PHOTO_URL, consumeBackEntity.getData().getProfile().getPicture());
    }

    private void signOut() {
        if (utils.getGoogleSignClient() != null) {
            utils.getGoogleSignClient().signOut();
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        getSinchServiceInterface().setStartListener(new SinchService.StartFailedListener() {
            @Override
            public void onStartFailed(SinchError error) {
                System.out.println("LoginActivity.onStartFailed" + error.toString());
            }

            @Override
            public void onStarted() {
                System.out.println("LoginActivity.Success");
            }
        });
    }
}
