package com.baisi.whatsfreecall.ui.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baisi.whatsfreecall.BuildConfig;
import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.adsconfig.StatisticalConfig;
import com.baisi.whatsfreecall.application.WhatsFreeCallApplication;
import com.baisi.whatsfreecall.configs.SpConfig;
import com.baisi.whatsfreecall.configs.UrlConfig;
import com.baisi.whatsfreecall.entity.requestentity.SignEntity;
import com.baisi.whatsfreecall.entity.httpbackentity.LoginConsumeBackEntity;
import com.baisi.whatsfreecall.sinchcall.base.CallBaseActivity;
import com.baisi.whatsfreecall.sinchcall.service.SinchService;
import com.baisi.whatsfreecall.utils.budleutils.BundleUtils;
import com.baisi.whatsfreecall.utils.firebaseutils.Firebase;
import com.baisi.whatsfreecall.utils.httputils.HttpClentUtils;
import com.baisi.whatsfreecall.utils.sputils.SpUtils;
import com.bestgo.adsplugin.ads.activity.ShowAdFilter;
import com.sinch.android.rtc.SinchError;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.TokenResponse;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;


public class ErrorActivity extends CallBaseActivity implements View.OnClickListener, ShowAdFilter {

    /**
     * Error:We are sorry about this error
     */
    private TextView mTvShowError;
    /**
     * LOGIN WITH GOOGLE
     */
    private Button mBtnError;
    /**
     * CANCEL
     */
    private Button mBtnClose;
    /*ProgressBar*/
    private LinearLayout mLlProgressBar;
    private ProgressBar mPb;
    private TextView mTvShowProressMsg;
    /**
     * auth 登陆
     */
    private Button mBtnLoginWeb;
    private static final String AUTH_STATE = "AUTH_STATE";
    private static final String USED_INTENT = "USED_INTENT";
    public String LOG_TAG = "WhatsFreeCall-Auth";
    String error;
    String flag;
    /*判断是否点击auth登陆后显示的progress*/
    private boolean isFirstLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        if (getIntent().getExtras() != null) {
            error = "Error: " + getIntent().getExtras().getString(BundleUtils.ERROR_CONFIG, getResources().getString(R.string.report_the_error));
            Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.ERROR_MSG, error);
            flag = getIntent().getExtras().getString(BundleUtils.FLAG);
        }
        initView();
    }

    private void initView() {
        initProgress();
        mTvShowError = (TextView) findViewById(R.id.tv_show_error);
        mTvShowError.setText(error);
        mBtnError = (Button) findViewById(R.id.btn_error);
        mBtnError.setOnClickListener(this);
        mBtnClose = (Button) findViewById(R.id.btn_close);
        mBtnClose.setOnClickListener(this);
        mBtnLoginWeb = (Button) findViewById(R.id.btn_web_login);
        if (BundleUtils.FLAGACTIVITY.equals(flag)) {
            mBtnLoginWeb.setVisibility(View.VISIBLE);
        } else {
            mBtnLoginWeb.setVisibility(View.GONE);
        }
        mBtnLoginWeb.setOnClickListener(this);
    }

    private void initProgress() {
        mTvShowProressMsg = (TextView) findViewById(R.id.tv_progress_msg);
        mPb = (ProgressBar) findViewById(R.id.pb_include);
        mLlProgressBar = (LinearLayout) findViewById(R.id.ll_progress);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_error:
                showShortToast("发送ERROR");
                if (!startEmail("", error)) {
                    skip(FeedBackActivity.class);
                }
                break;
            case R.id.btn_close:
                skip(HomeActivity.class);
                break;
            case R.id.btn_web_login:
                isFirstLogin = true;
                showProgress(mLlProgressBar, mPb, mTvShowProressMsg, "Check the landing environment...", R.color.colorPrimary);
                createAuthLogin();
                Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.LOGIN_AUTHO, StatisticalConfig.RESULT_CODE_CLICK);
                break;
        }
    }

    private void createAuthLogin() {
        //创建授权请求
        AuthorizationServiceConfiguration serviceConfiguration = new AuthorizationServiceConfiguration(
                Uri.parse("https://accounts.google.com/o/oauth2/v2/auth") /* auth endpoint*/,
                Uri.parse("https://www.googleapis.com/oauth2/v4/token") /* token endpoint */
        );

        AuthorizationService authorizationService = new AuthorizationService(ErrorActivity.this);

//        String clientId = "167220273530-dn5ic7m4plvv9o5ne4tfinb3b5ijt7p0.apps.googleusercontent.com";
        String clientId = getResources().getString(R.string.client_id);
//        Uri redirectUri = Uri.parse("com.storebuff.whatsfreecall:/oauth2callback");
        Uri redirectUri = Uri.parse(BuildConfig.AUTH_BACK);
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                serviceConfiguration,
                clientId,
                AuthorizationRequest.RESPONSE_TYPE_CODE,
                redirectUri
        );
        builder.setScopes("profile");
            /*执行授权请求*/
        AuthorizationRequest request = builder.build();
//        String action = "com.storebuff.whatsfreecall.HANDLE_AUTHORIZATION_RESPONSE";
        String action = BuildConfig.RECIVER_FILTER;
        Intent postAuthorizationIntent = new Intent(action);
        PendingIntent pendingIntent = PendingIntent.getActivity(ErrorActivity.this, request.hashCode(), postAuthorizationIntent, 0);
        authorizationService.performAuthorizationRequest(request, pendingIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFirstLogin) {
            handler.postDelayed(runnable, 1 * 1000);
        }
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            hideProgress(mLlProgressBar);
            isFirstLogin = false;
        }
    };

    @Override
    public void onBackPressed() {
        if (isProgressShow(R.id.ll_progress)) {
            hideProgress(mLlProgressBar);
        } else {
            super.onBackPressed();
        }
    }

    /*处理授权响应*/
    @Override
    protected void onNewIntent(Intent intent) {
        checkIntent(intent);
    }

    private void checkIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.LOGIN_AUTHO, StatisticalConfig.OK);
                switch (action) {
//                    case "com.storebuff.whatsfreecall.HANDLE_AUTHORIZATION_RESPONSE":
                    case BuildConfig.RECIVER_FILTER:
                        if (!intent.hasExtra(USED_INTENT)) {
                            showProgress(mLlProgressBar, mPb, mTvShowProressMsg, "Landing...", R.color.colorPrimary);
                        /*获取信息*/
                            handleAuthorizationResponse(intent);
                            intent.putExtra(USED_INTENT, true);
                        }
                        break;
                    default:
                        break;
                }
            } else {
                Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.LOGIN_AUTHO, StatisticalConfig.NO);
                showShortToast("action null");
            }
        } else {
            Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.LOGIN_AUTHO, StatisticalConfig.NO);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkIntent(getIntent());
    }

    private void handleAuthorizationResponse(@NonNull Intent intent) {
        AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
        AuthorizationException error = AuthorizationException.fromIntent(intent);
        final AuthState authState = new AuthState(response, error);
        if (response != null) {
            Log.i(LOG_TAG, String.format("Handled Authorization Response %s ", authState.toJsonString()));
            AuthorizationService service = new AuthorizationService(this);
            service.performTokenRequest(response.createTokenExchangeRequest(), new AuthorizationService.TokenResponseCallback() {
                @Override
                public void onTokenRequestCompleted(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException exception) {
                    if (exception != null) {
                        Log.w(LOG_TAG, "Token Exchange failed", exception);
                    } else {
                        if (tokenResponse != null) {
                            authState.update(tokenResponse, exception);
                            startLogin(tokenResponse.idToken, SignEntity.OAUTH);
                            Log.i(LOG_TAG, "accessToken:" + tokenResponse.accessToken);
                            Log.i(LOG_TAG, "idToken:" + tokenResponse.idToken);
                            Log.i(LOG_TAG, String.format("Token Response [ Access Token: %s, ID Token: %s ]", tokenResponse.accessToken, tokenResponse.idToken));
                        }
                    }
                }
            });
        }
    }

    @Override
    public boolean allowShowAd() {
        return false;
    }

    public interface PostLogin_Interface {
        @POST(UrlConfig.LOGIN)
        Call<LoginConsumeBackEntity> getCall(@Header("Authorization") String userToken, @Body SignEntity values);
    }

    /**
     * 登陆接口--登陆
     *
     * @param token
     */
    public void startLogin(String token, String type) {
        //步骤4:创建Retrofit对象
        OkHttpClient okhttpClient = HttpClentUtils.getOkhttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlConfig.BASE_URL) // 设置 网络请求 Url
                .client(okhttpClient)
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();
        PostLogin_Interface request = retrofit.create(PostLogin_Interface.class);
        //对 发送请求 进行封装( //对 发送请求 进行封装(因为传递json 所以将参数用实体包裹转成json))
        SignEntity sign = new SignEntity(token, type, WhatsFreeCallApplication.getInstance().getAppInstandID());
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
                            signSinch();
                            updateUserInfo(loginConsumeBackEntity);
                            hideProgress(mLlProgressBar);
                            skip(HomeActivity.class);
                            showShortToast("Login Successful");
                        } else {
                            hideProgress(mLlProgressBar);
                            skip(ErrorActivity.class, BundleUtils.getBundleUtils().getErrorBundle("Login " + loginConsumeBackEntity.getErr_code()));
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginConsumeBackEntity> call, Throwable t) {
                System.out.println("请求失败登陆");
                hideProgress(mLlProgressBar);
                System.out.println(t.getMessage());
                skip(ErrorActivity.class, BundleUtils.getBundleUtils().getErrorBundle("Login " + t.getMessage()));
                finish();
            }
        });
    }

    private void updateUserInfo(LoginConsumeBackEntity consumeBackEntity) {
        SpUtils.putString(getApplicationContext(), SpConfig.USER_TOKEN, consumeBackEntity.getData().getToken());
        SpUtils.putString(getApplicationContext(), SpConfig.USER_NAME, consumeBackEntity.getData().getProfile().getName());
        SpUtils.putInt(getApplicationContext(), SpConfig.USER_BALABCE, consumeBackEntity.getData().getProfile().getCredit_micro());
        SpUtils.putString(getApplicationContext(), SpConfig.USER_STRING_BALANCE, consumeBackEntity.getData().getProfile().getCredit_string());
        SpUtils.putString(getApplicationContext(), SpConfig.USER_PHOTO_URL, consumeBackEntity.getData().getProfile().getPicture());
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
