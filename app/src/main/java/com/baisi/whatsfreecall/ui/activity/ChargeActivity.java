package com.baisi.whatsfreecall.ui.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.adsconfig.StatisticalConfig;
import com.baisi.whatsfreecall.application.WhatsFreeCallApplication;
import com.baisi.whatsfreecall.base.BaseActivity;
import com.baisi.whatsfreecall.configs.SpConfig;
import com.baisi.whatsfreecall.configs.UrlConfig;
import com.baisi.whatsfreecall.entity.httpbackentity.AdsEntity;
import com.baisi.whatsfreecall.entity.requestentity.AdsCharge;
import com.baisi.whatsfreecall.ui.activity.checkin.CheckInActivity;
import com.baisi.whatsfreecall.ui.activity.earn_money.EarnMoneyListActivity;
import com.baisi.whatsfreecall.ui.adapter.SkuAdapter;
import com.baisi.whatsfreecall.utils.CheckToken;
import com.baisi.whatsfreecall.utils.GoogleUtilsLoginAndPay;
import com.baisi.whatsfreecall.utils.adsutils.FullAdsUtils;
import com.baisi.whatsfreecall.utils.adsutils.VideoAdsUtils;
import com.baisi.whatsfreecall.utils.budleutils.BundleUtils;
import com.baisi.whatsfreecall.utils.firebaseutils.Firebase;
import com.baisi.whatsfreecall.utils.httputils.HttpClentUtils;
import com.baisi.whatsfreecall.utils.sputils.SpUtils;
import com.baisi.whatsfreecall.utils.utilpay.IabBroadcastReceiver;
import com.baisi.whatsfreecall.utils.utilpay.IabHelper;
import com.baisi.whatsfreecall.utils.utilpay.IabResult;
import com.baisi.whatsfreecall.utils.utilpay.Inventory;
import com.baisi.whatsfreecall.utils.utilpay.Purchase;
import com.baisi.whatsfreecall.utils.utilpay.SkuDetails;
import com.bestgo.adsplugin.ads.AdAppHelper;
import com.bestgo.adsplugin.ads.activity.ShowAdFilter;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;



public class ChargeActivity extends BaseActivity implements View.OnClickListener, IabBroadcastReceiver.IabBroadcastListener, ShowAdFilter {
    public static String TAG = "WhatsFreeCall";
    private TextView mTvChargeShowBalance;
    private RecyclerView mRlv;
    private RelativeLayout mLlChargeWatch;
    private RelativeLayout mRlSignIn;
    private LinearLayout mLlChargeProgress;
    private ProgressBar mPbProgessBar;
    private TextView mTvShowLoadSkuMsg;
    private ArrayList<String> skuList = new ArrayList<>();
    /*google登陆*/
    private GoogleUtilsLoginAndPay googleUtilsLoginAndPay = new GoogleUtilsLoginAndPay();
    private GoogleSignInClient mGoogleSignInClient;
    /*支付相关*/

    // The helper object
    IabHelper mHelper;
    //提供购买通知
    IabBroadcastReceiver mBroadcastReceiver;

    /**********/
    private AlertDialog inventDialog;
    /*标记是否有库存*/
    private boolean isInventy;
    /*库存商品对象*/
    private Purchase purchase;
    /**
     * $0.0003~$3
     */
    private TextView mChargeBtn1;
    /*标记hhelper 是否启动*/
    private boolean isStartHelper;
    /*progressbar*/
    private ProgressBar mPbBar;
    String mTimeInterval;
    int progressMax;
    int progress = 1;
    public static final int SET_PROGRESS = 1002;
    private boolean isVideoClick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);
        /*全屏*/
        FullAdsUtils.isShowFull(FullAdsUtils.SHOW_ENTER_CHARGE_FULL,false,FullAdsUtils.SHOW_ENTER_CHARGE_FULL);
        Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.CHARGE_ACTIVITY, StatisticalConfig.ACTIVITY_SHOW);
        createInventDialog();
        isInventy = false;
        initView();
        initSkuList();
        setmHelper();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initVideTimeInterVal();
        //加载视频广告
        handler.postDelayed(runnable, 0);
        /*设置显示的余额*/
        mTvChargeShowBalance.setText(SpUtils.getString(getApplicationContext(), SpConfig.USER_STRING_BALANCE, "$0.00"));
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.charge_toolbar);
        toolbar.setTitle(getResources().getString(R.string.charge));
        setSupportActionBar(toolbar);
        //设置toolbar后调用setDisplayHomeAsUpEnabled
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 设置视频广告等待进度条
     */
    private void initProgress() {
        try {
            progressMax = Integer.parseInt(mTimeInterval);
        } catch (NumberFormatException e) {
            progressMax = 5;
        }
        mPbBar.setMax(progressMax);
    }
    private void initVideTimeInterVal() {
        progress = 1;
        mPbBar.setProgress(0);
        mPbBar.setVisibility(View.VISIBLE);
        mChargeBtn1.setBackground(getResources().getDrawable(R.drawable.charge_bottom_tv_1_shape_un));
        mLlChargeWatch.setEnabled(false);
        mLlChargeWatch.setClickable(false);
    }

    private void initView() {
        initToolbar();
        mPbProgessBar = (ProgressBar) findViewById(R.id.pb_charge);
        //再次观看视频间隔时间
        mTimeInterval = AdAppHelper.getInstance(getApplicationContext()).getCustomCtrlValue(VideoAdsUtils.VIDEO_BTN_TIME_INTERVAL, "5");
        mPbBar = (ProgressBar) findViewById(R.id.charge_progress);
        initProgress();
        setProgressBarColor(R.color.colorPrimary, mPbProgessBar);
        mTvShowLoadSkuMsg = (TextView) findViewById(R.id.tv_show_laod_charge_success);
        mTvChargeShowBalance = (TextView) findViewById(R.id.tv_charge_show_balance);
        mRlv = (RecyclerView) findViewById(R.id.rlv_charge);
        mLlChargeProgress = (LinearLayout) findViewById(R.id.charge_ll_progress);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setAutoMeasureEnabled(true);
        layoutManager.setSmoothScrollbarEnabled(true);
        mRlv.setHasFixedSize(true);
        mRlv.setNestedScrollingEnabled(false);
        mRlv.setLayoutManager(layoutManager);
        mLlChargeWatch = (RelativeLayout) findViewById(R.id.ll_charge_watch);
        mLlChargeWatch.setOnClickListener(this);
        mRlSignIn = (RelativeLayout) findViewById(R.id.rl_sign_in);
        mRlSignIn.setOnClickListener(this);
        mChargeBtn1 = (TextView) findViewById(R.id.charge_btn_1);
       /* setWatchVideoStates(AdAppHelper.getInstance(getApplicationContext()).isVideoReady());*/

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.ll_charge_watch:
                if (CheckToken.checkToken()) {
                    Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.GO_LOGIN_VIDEO, StatisticalConfig.LOGIN_SHOW);
                    skip(LoginActivity.class, BundleUtils.getBundleUtils().getLoginBundle(false, BundleUtils.ENTER_CHARGE_VIDEO, "", null));
                    finish();
                } else {
                    showVideoAds(StatisticalConfig.VIDEO_ADS);
                }
                break;
            case R.id.rl_sign_in:
                if (CheckToken.checkToken()) {
                    Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.GO_LOGIN_VIDEO, StatisticalConfig.LOGIN_SHOW);
                    skip(LoginActivity.class, BundleUtils.getBundleUtils().getLoginBundle(false, BundleUtils.ENTER_CHARGE_VIDEO, "", null));
                    finish();
                } else {
                    skip(CheckInActivity.class);
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*当IabHelpoer启动的时候才会成功注册于启动*/
        if (isStartHelper) {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    /********************************支付购买相关********************************************/
    /**
     * 初始化商品信息
     */
    private void initSkuList() {
        Resources resources = getResources();
        String[] sku = resources.getStringArray(R.array.sku_list);
        for (int i = 0; i < sku.length; i++) {
            skuList.add(sku[i]);
        }

    }

    private void setmHelper() {
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
                    showShortToast("Problem setting up in-app billing: " + result);
                    mTvShowLoadSkuMsg.setText(R.string.load_end_msg);
                    mPbProgessBar.setVisibility(View.GONE);
                    isStartHelper = false;
                    return;
                } else {
                    isStartHelper = true;
                }
                if (mHelper == null) {
                    return;
                }
                mBroadcastReceiver = new IabBroadcastReceiver(ChargeActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    /*查询库存并查询可售商品详细信息*/
                    mHelper.queryInventoryAsync(true, skuList, null, mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    showShortToast("Error querying inventory. Another async operation in progress.");
                }
            }
        });
    }

    private void createInventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.error);
        builder.setTitle("Warning");
        builder.setMessage("There is not the consumption of goods, consumption can continue to buy!");
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*有库存去登陆消耗界面*/
                skip(LoginActivity.class, BundleUtils.getBundleUtils().getLoginBundle(true, BundleUtils.ENTER_SPLASH, "", purchase));
                finish();
            }
        });
        inventDialog = builder.create();
    }

    //查询结束监听 包括查询到的商品详情
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            if (inventory != null) {
                final List<SkuDetails> list = inventory.getAllSkus(skuList);
                if (list != null && list.size() > 0) {
                    for (int i = list.size() - 1; i >= 0; i--) {
                        if (list.get(i) != null) {
                            if (list.get(i).getTitle().contains("Inactive")) {
                                list.remove(i);
                            }
                        } else {
                            list.remove(i);
                        }
                    }
                    if (list != null && list.size() > 0) {
                        SkuAdapter skuAdapter = new SkuAdapter(ChargeActivity.this, list);
                        mRlv.setAdapter(skuAdapter);
                        skuAdapter.setOnItemClickListener(new SkuAdapter.OnItemClickListener() {
                            @Override
                            public void OnItemClick(View view, int position) {
                                if (!isInventy) {
                                    System.out.println("ChargeActivity.OnItemClick" + "SKU" + list.get(position).getSku());
                                    Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.SKU_ID, list.get(position).getSku(), list.get(position).getTitle());
                                    skip(LoginActivity.class, BundleUtils.getBundleUtils().getLoginBundle(true, BundleUtils.ENTER_CHARGE, list.get(position).getSku(), null));
                                    finish();
                                } else {
                                    inventDialog.show();
                                }
                            }
                        });
                    } else {
                        mTvShowLoadSkuMsg.setText(R.string.load_end_msg);
                        mPbProgessBar.setVisibility(View.GONE);
                        return;
                    }
                }
                mLlChargeProgress.setVisibility(View.GONE);
                if (inventory.getAllPurchases() != null) {
                    if (inventory.getAllPurchases().size() > 0) {
                        System.out.println("ChargeActivity.onQueryInventoryFinished" + "库存" + inventory.getAllPurchases().size());
                        inventDialog.show();
                        purchase = inventory.getAllPurchases().get(0);
                        isInventy = true;
                    } else {
                        isInventy = false;
                        System.out.println("没有库存!=null");
                    }
                } else {
                    isInventy = false;
                    System.out.println("没有库存=null");
                }
            }
            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) {
                return;
            }

            // 错误
            if (result.isFailure()) {
                System.out.println("ChargeActivity.onQueryInventoryFinished>>>" + "Failed to query inventory: " + result);
                mTvShowLoadSkuMsg.setText(R.string.load_end_msg);
                mPbProgessBar.setVisibility(View.GONE);
                Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.GOOGLE_PAY_ERROR, "Failed to query inventory: " + result);
//                skip(ErrorActivity.class, BundleUtils.getBundleUtils().getErrorBundle("Failed to query inventory: " + result));
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            // 如果我们拥有库存 立即消耗
            //下面应该是消耗代码
        }
    };

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        //收到广播通知，说明项目库存已更改
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            showShortToast("Error querying inventory. Another async operation in progress.");
        }
    }

    /*************************网络请求广告****************************/
    @Override
    public boolean allowShowAd() {
        return false;
    }


    interface ChargeAda_interface {
        @POST(UrlConfig.WATCH_VIDEO_ADS_PAY)
        Call<AdsEntity> getAdsChargeInfo(@Header("Authorization") String author, @Body AdsCharge body);
    }

    public void getChargeAdsEndInfos(String adsType, String isClick) {
        //步骤4:创建Retrofit对象
        OkHttpClient okhttpClient = HttpClentUtils.getOkhttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlConfig.BASE_URL) // 设置 网络请求 Url
                .client(okhttpClient)
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();
        // 步骤5:创建 网络请求接口 的实例
        ChargeAda_interface request = retrofit.create(ChargeAda_interface.class);
        //对 发送请求 进行封装( //对 发送请求 进行封装(因为传递json 所以将参数用实体包裹转成json))
        AdsCharge ads = new AdsCharge(adsType, isClick);
        String userToken = SpUtils.getString(WhatsFreeCallApplication.getInstance().getApplicationContext(), SpConfig.USER_TOKEN, "");
        Call<AdsEntity> call = request.getAdsChargeInfo("Bearer " + userToken, ads);
        call.enqueue(new Callback<AdsEntity>() {
            @Override
            public void onResponse(Call<AdsEntity> call, Response<AdsEntity> response) {
                hideProgress(R.id.ll_progress);
                Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.CHARGE_RESPONSE_CODE, response.code());
                if (response != null) {
                    AdsEntity adsEntity = response.body();
                    if (adsEntity != null) {
                        if (adsEntity.getErr_code() == 0) {
                            updateUserInfo(adsEntity);
                            showDialog(adsEntity);
                        } else {
                            showShortToast(adsEntity.getErr_msg());
                        }
                    } else {
                        Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.CHARGE_RESPONSE_VIDEO_BACK, response.message());
                    }
                } else {
                    Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.CHARGE_RESPONSE_VIDEO_BACK, StatisticalConfig.RRESPONSE_NULL);
                }
            }

            @Override
            public void onFailure(Call<AdsEntity> call, Throwable t) {
                hideProgress(R.id.ll_progress);
                System.out.println("CallRequestErrorActivity.onFailure" + t.getMessage());
                showShortToast(t.getMessage());
            }
        });
    }

    private void updateUserInfo(AdsEntity adsEntity) {
        SpUtils.putString(getApplicationContext(), SpConfig.USER_NAME, adsEntity.getData().getProfile().getName());
        SpUtils.putInt(getApplicationContext(), SpConfig.USER_BALABCE, adsEntity.getData().getProfile().getCredit_micro());
        SpUtils.putString(getApplicationContext(), SpConfig.USER_STRING_BALANCE, adsEntity.getData().getProfile().getCredit_string());
        SpUtils.putString(getApplicationContext(), SpConfig.USER_PHOTO_URL, adsEntity.getData().getProfile().getPicture());
    }

    private void showDialog(AdsEntity adsEntity) {
        final AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(ChargeActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.video_over_dialog, null);
        builder.setView(view);
        Button buttonOk = (Button) view.findViewById(R.id.btn_over);
        ImageView ivIcon= (ImageView) view.findViewById(R.id.iv_video_click);
        ImageView ivIconUnClick= (ImageView) view.findViewById(R.id.iv_video_click_un);
        if (isVideoClick) {
            ivIcon.setVisibility(View.VISIBLE);
            ivIconUnClick.setVisibility(View.GONE);
        }else{
            ivIcon.setVisibility(View.GONE);
            ivIconUnClick.setVisibility(View.VISIBLE);
        }
        TextView tvMsg = (TextView) view.findViewById(R.id.tv_show_balance_dialog);
        dialog = builder.create();
        tvMsg.setText(" + " + adsEntity.getData().getReward());
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                FullAdsUtils.isShowRandomAdsFull(StatisticalConfig.VIDEO_OVER_FULL_ADS, false, FullAdsUtils.SHOW_RANDOM_FULL_VIDEO);
            }
        });
        if (!isFinishing()) {
            dialog.show();
        }
    }

    private void showVideoAds(String addressName) {
        VideoAdsUtils.showVideoAds(addressName, new VideoAdsUtils.VideoOnPlayListener() {
            @Override
            public void setState(int state, boolean isClick) {
                switch (state) {
                    default:
                        break;
                    case VideoAdsUtils.OVER:
                        Message msg = new Message();
                        msg.what = VideoAdsUtils.OVER;
                        msg.obj = isClick;
                        handler.sendMessage(msg);
//                        handler.sendEmptyMessage(VideoAdsUtils.OVER);
                        break;
                    case VideoAdsUtils.CANCEL:
                        Message msgCancel = new Message();
                        msgCancel.what = VideoAdsUtils.CANCEL;
                        msgCancel.obj = isClick;
                        handler.sendMessage(msgCancel);
//                        handler.sendEmptyMessage(VideoAdsUtils.CANCEL);
                        break;
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
           /* AdAppHelper.getInstance(getApplicationContext()).loadNewRewardedVideoAd();
            setWatchVideoStates(AdAppHelper.getInstance(getApplicationContext()).isVideoReady());*/
            switch (msg.what) {
                default:
                    break;
                case VideoAdsUtils.CANCEL:
                    showShortToast(R.string.video_cancel);
                    System.out.println("ChargeActivity.setState>>>" + "Cancel 没有奖励" + msg.obj);
                    break;
                case VideoAdsUtils.OVER:
                    boolean isClick = (boolean) msg.obj;
                    isVideoClick=isClick;
                    System.out.println("ChargeActivity.setState>>>" + "over 有奖励" + isClick);
                    showProgress(R.id.ll_progress, R.id.pb_include, R.id.tv_progress_msg, getResources().getString(R.string.reward), R.color.colorPrimary);
                    getChargeAdsEndInfos(VideoAdsUtils.VIDEO_ADS_TYPE, isClick + "");
                    break;
                case LOAD_VIDEO_ADS:
                    /*成功则不请求 不成功则两秒后在请求*/
                    if (AdAppHelper.getInstance(getApplicationContext()).isVideoReady()) {
                        handler.postDelayed(SetProgress, 1 * 1000);
                       /* handler.removeCallbacks(runnable);*/
                    } else {
                        handler.postDelayed(runnable, 2 * 1000);
                        setWatchVideoStates(AdAppHelper.getInstance(getApplicationContext()).isVideoReady());
                    }
                    break;
                case SET_PROGRESS:
                    mPbBar.setProgress(progress);
                    progress += 1;
                    if (progress > progressMax) {
                        setWatchVideoStates(true);
                    } else {
                        handler.postDelayed(SetProgress, 1 * 1000);
                    }
                    break;
            }
        }
    };
    Runnable SetProgress = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(SET_PROGRESS);
        }
    };
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    public static final int LOAD_VIDEO_ADS = 101;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            AdAppHelper.getInstance(getApplicationContext()).loadNewRewardedVideoAd();
            handler.sendEmptyMessage(LOAD_VIDEO_ADS);
        }
    };

    /*   setWatchVideoStates(AdAppHelper.getInstance(getApplicationContext()).isVideoReady());*/
    private void setWatchVideoStates(boolean isVideoReady) {
        if (isVideoReady) {
            Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.VIDEO_ADS_LOAD, StatisticalConfig.OK);
            mChargeBtn1.setBackground(getResources().getDrawable(R.drawable.charge_bottom_tv_1_shape));
            mLlChargeWatch.setEnabled(true);
            mLlChargeWatch.setClickable(true);
        } else {
            Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.VIDEO_ADS_LOAD, StatisticalConfig.NO);
            mChargeBtn1.setBackground(getResources().getDrawable(R.drawable.charge_bottom_tv_1_shape_un));
            mLlChargeWatch.setEnabled(false);
            mLlChargeWatch.setClickable(false);
        }
    }

    public void goToEarnMoney(View view) {
        if (CheckToken.checkToken()) {
            ///Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.GO_LOGIN_VIDEO, StatisticalConfig.LOGIN_SHOW);
            skip(LoginActivity.class, BundleUtils.getBundleUtils().getLoginBundle(false, BundleUtils.ENTER_CHARGE_VIDEO, "", null));
            finish();
        } else {
            skip(EarnMoneyListActivity.class);
        }
    }
}
