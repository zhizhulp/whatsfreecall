package com.baisi.whatsfreecall.ui.activity;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.adsconfig.StatisticalConfig;
import com.baisi.whatsfreecall.application.WhatsFreeCallApplication;
import com.baisi.whatsfreecall.configs.SpConfig;
import com.baisi.whatsfreecall.configs.UrlConfig;
import com.baisi.whatsfreecall.entity.httpbackentity.CallScrrenEntity;
import com.baisi.whatsfreecall.entity.requestentity.PostCountry;
import com.baisi.whatsfreecall.manager.notimanager.NotificationManagers;
import com.baisi.whatsfreecall.manager.notimanager.NotificationReciver;
import com.baisi.whatsfreecall.sinchcall.base.CallBaseActivity;
import com.baisi.whatsfreecall.sinchcall.utils.AudioPlayer;
import com.baisi.whatsfreecall.utils.budleutils.BundleUtils;
import com.baisi.whatsfreecall.utils.firebaseutils.Firebase;
import com.baisi.whatsfreecall.utils.firebaseutils.PromotionTracking;
import com.baisi.whatsfreecall.utils.httputils.HttpClentUtils;
import com.baisi.whatsfreecall.utils.sputils.SpUtils;
import com.baisi.whatsfreecall.view.CallScreenKeyBoard;
import com.bestgo.adsplugin.ads.activity.ShowAdFilter;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;


public class CallScreenActivity extends CallBaseActivity implements View.OnClickListener, ShowAdFilter {
    static final String TAG = CallScreenActivity.class.getSimpleName();
    private AudioPlayer mAudioPlayer;
    private Timer mTimer;
    private UpdateCallDurationTask mDurationTask;
    /*call id*/
    private String mCallId;
    /*国家国旗图片id*/
    private int countryFlag;
    /*国家名字*/
    private String countryName;
    /*国家代码*/
    private int countryCode;
    /*电话号码*/
    private String phoneNumber;
    /*联系人姓名*/
    private String contactName;
    /*联系人头像*/
    private String contactPhoto;

    @Override
    public boolean allowShowAd() {
        return false;
    }

    /*更新时间*/
    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            CallScreenActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }

    /**
     * +4415101099390
     */
    private TextView mTvCallNumber;
    /**
     * CallingStates
     */
    private TextView mTvCallStatus;
    /**
     * 00:00
     */
    private TextView mCallDuration;
    private ImageView mIvCallCountryIcon;
    /**
     * United Kingdom
     */
    private TextView mTvCountName;
    /**
     * 2:30AM
     */
    private TextView mTvCallTime;
    /**
     * US$0.1/min
     */
    private TextView mTvCallRate;
    /**
     * US2.56
     */
    private TextView mTvCallUsd;
    /**
     * 25min
     */
    private TextView mTvCallEndtime;
    private ImageView mIvAudio;
    private ImageView mIvKeyboard;
    private ImageView mIvSpeaker;
    /**
     * HIDE
     */
    private Button mCallKeyboardHide;
    private FloatingActionButton mCallEnd;
    private LinearLayout mLlCallShowCallMsg;
    private LinearLayout mLlCallRateMsg;
    private CallScreenKeyBoard mCsbKeyboard;
    private TextView mTvInputMsg;
    /*private String mCallNumber;*/
    private AudioManager audioManager;
    Bundle bundle;
    /*通话对象*/
    Call call;
    private NotificationManagers notificationManagers;

    /**
     * 判断是否需要显示通知栏
     */
    private boolean isShowNoti;
    private long starttime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_screen);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        bundle = getIntent().getExtras();
        if (bundle != null) {
            notificationManagers = new NotificationManagers();
            mAudioPlayer = new AudioPlayer(this);
            mCallId = bundle.getString(BundleUtils.CALL_ID);
            countryFlag = bundle.getInt(BundleUtils.COUNTRY_FLAG);
            countryName = bundle.getString(BundleUtils.COUNTRY_NAME);
            countryCode = bundle.getInt(BundleUtils.COUNTRY_CODE);
            phoneNumber = bundle.getString(BundleUtils.CALL_NUMBER);
            contactName = bundle.getString(BundleUtils.CONTACT_NAME);
            contactPhoto = bundle.getString(BundleUtils.CONTACT_PHOTO);
            isShowNoti = true;
        }
        initView();
        initAudio();
        setShowPhoneNumber(contactName, phoneNumber, "+" + countryCode);
        /*安装多少天未删除*/
        PromotionTracking.getInstance(getApplicationContext()).reportUninstallDayCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        notificationManagers.removeNotification(getApplicationContext());
        registerReciver();
        getCallInfos("+" + countryCode, phoneNumber);
        mTimer = new Timer();
        mDurationTask = new UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isShowNoti) {
            notificationManagers.showNotification(getApplicationContext(), CallScreenActivity.currentACtivity.getTaskId(), countryCode, phoneNumber, countryName, contactPhoto);
        }
        mDurationTask.cancel();
        mTimer.cancel();
    }

    private void initView() {
        mTvCallNumber = (TextView) findViewById(R.id.tv_call_number);
        mTvCallStatus = (TextView) findViewById(R.id.tv_call_status);
        mCallDuration = (TextView) findViewById(R.id.callDuration);
        mCallDuration.setVisibility(View.INVISIBLE);
        mIvCallCountryIcon = (ImageView) findViewById(R.id.iv_call_country_icon);
        mIvCallCountryIcon.setImageDrawable(getResources().getDrawable(countryFlag == 0 ? R.drawable.flag_cc : countryFlag));
        mTvCountName = (TextView) findViewById(R.id.tv_count_name);
        mTvCountName.setText(countryName);
        mTvCallTime = (TextView) findViewById(R.id.tv_call_time);
        mTvCallTime.setText(getTime());
        mTvCallRate = (TextView) findViewById(R.id.tv_call_rate);
        mTvCallUsd = (TextView) findViewById(R.id.tv_call_usd);
        mTvCallEndtime = (TextView) findViewById(R.id.tv_call_endtime);
        mIvAudio = (ImageView) findViewById(R.id.iv_audio);
        mIvAudio.setOnClickListener(this);
        mIvKeyboard = (ImageView) findViewById(R.id.iv_keyboard);
        mIvKeyboard.setOnClickListener(this);
        mIvSpeaker = (ImageView) findViewById(R.id.iv_speaker);
        mIvSpeaker.setOnClickListener(this);
        mCallKeyboardHide = (Button) findViewById(R.id.call_keyboard_hide);
        mCallKeyboardHide.setOnClickListener(this);
        mCallEnd = (FloatingActionButton) findViewById(R.id.fab_call_end);
        mCallEnd.setOnClickListener(this);
        mLlCallShowCallMsg = (LinearLayout) findViewById(R.id.ll_show_call_number_msg);
        mLlCallRateMsg = (LinearLayout) findViewById(R.id.ll_call_rate_msg);
        mCsbKeyboard = (CallScreenKeyBoard) findViewById(R.id.csb_keyboard);
        mCsbKeyboard.setOnNumberClickListener(numberClickListener);
        mTvInputMsg = (TextView) findViewById(R.id.tv_input_msg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.iv_keyboard:
                /*判断是否显示电话号码时间状态布局*/
                showTvCallMsg();
                mCallKeyboardHide.setVisibility(View.VISIBLE);
                mCsbKeyboard.setVisibility(View.VISIBLE);
                mTvInputMsg.setVisibility(View.VISIBLE);
                mLlCallRateMsg.setVisibility(View.INVISIBLE);
                break;
            case R.id.call_keyboard_hide:
                mCsbKeyboard.setVisibility(View.GONE);
                mTvInputMsg.setVisibility(View.GONE);
                mLlCallRateMsg.setVisibility(View.VISIBLE);
                mCallKeyboardHide.setVisibility(View.GONE);
                mLlCallShowCallMsg.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_audio:
                /*true表示静音 则需要关闭静音 图标设置为关 false 表示没静音 图标设置为开*/
                if (audioManager.isMicrophoneMute()) {
                    mIvAudio.setImageDrawable(getResources().getDrawable(R.drawable.mic_off));
                } else {
                    mIvAudio.setImageDrawable(getResources().getDrawable(R.drawable.mic_on));
                }
                audioManager.setMicrophoneMute(!audioManager.isMicrophoneMute());
                break;
            case R.id.iv_speaker:
                  /*true表示扬声器开 则需要关闭扬声器 图标设置为关同时关闭扬声器
                   false 表示没扬声器 图标设置为开同时开启扬声器*/
                if (audioManager.isSpeakerphoneOn()) {
                    mIvSpeaker.setImageDrawable(getResources().getDrawable(R.drawable.speaker_off));
                } else {
                    mIvSpeaker.setImageDrawable(getResources().getDrawable(R.drawable.speaker_on));
                }
                audioManager.setSpeakerphoneOn(!audioManager.isSpeakerphoneOn());
                break;
            case R.id.fab_call_end:
                isShowNoti = false;
                endCall();
                break;
        }
    }

    private CallScreenKeyBoard.OnNumberClickListener numberClickListener = new CallScreenKeyBoard.OnNumberClickListener() {
        @Override
        public void setNumber(String number) {
            mTvInputMsg.setText(mTvInputMsg.getText().toString() + number);
            call.sendDTMF(number);
            showTvCallMsg();
        }
    };

    /**
     * 是否显示电话号码布局 当输入的字符串是空的时候显示callmsg不是空的时候不显示Callmsg
     */
    private void showTvCallMsg() {
        if (TextUtils.isEmpty(mTvInputMsg.getText().toString())) {
            mLlCallShowCallMsg.setVisibility(View.VISIBLE);
        } else {
            mLlCallShowCallMsg.setVisibility(View.INVISIBLE);
        }
    }

    private void endCall() {
        mAudioPlayer.stopProgressTone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
    }

    private void endStatusCall(String status, Call calls) {
        isShowNoti = false;
        notificationManagers.removeNotification(getApplicationContext());
        mAudioPlayer.stopProgressTone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        if (status.equals("HUNG_UP")) {
            skip(CallResultActivity.class, BundleUtils.getBundleUtils().getCallEndMsg(mCallId,
                    calls.getDetails().toString(),
                    "$0.03", "$50", calls.getDetails().getDuration(),
                    phoneNumber, countryName, contactName, contactPhoto, countryCode, countryFlag));
            finish();
        } else {
            skip(CallRequestErrorActivity.class, BundleUtils.getBundleUtils().getCallRedialStatusMsg(status,
                    mCallId,
                    countryFlag,
                    countryName,
                    phoneNumber,
                    calls.getDetails().toString(), contactName, contactPhoto, countryCode));
            finish();
        }
    }

    @Override
    protected void onServiceConnected() {
        call = getSinchServiceInterface().getCall(mCallId);
        /*播放提示音*/
        mAudioPlayer.playProgressTone();
        if (call != null) {
            call.addCallListener(new SinchCallListener());
            starttime = System.currentTimeMillis();
            handler.postDelayed(runnable, 1000);

           /* mTvCallStatus.setText(call.getState().toString().toLowerCase());*/
        } else {
            System.out.println("CallScreenActivity.onServiceConnected>>>>>>>>" + "以无效的callid终止");
            Log.e(TAG, "Started with invalid callId, aborting.");

        }
        super.onServiceConnected();
    }

    /**
     * 设置显示的电话或者姓名
     *
     * @param name
     * @param number
     */
    private void setShowPhoneNumber(String name, String number, String countryCode) {
        if (!TextUtils.isEmpty(name)) {
            mTvCallNumber.setText(name);
        } else {
            mTvCallNumber.setText(countryCode + number);
            for (int i = 0; i < WhatsFreeCallApplication.getInstance().contactsEntityList.size(); i++) {
                if (WhatsFreeCallApplication.getInstance().contactsEntityList != null &&
                        WhatsFreeCallApplication.getInstance().contactsEntityList.size() > 0 &&
                        WhatsFreeCallApplication.getInstance().contactsEntityList.get(i) != null
                        && WhatsFreeCallApplication.getInstance().contactsEntityList.get(i).getPhone() != null) {
                    if (WhatsFreeCallApplication.getInstance().contactsEntityList.get(i).getPhone().contains(number)) {
                        contactName = WhatsFreeCallApplication.getInstance().contactsEntityList.get(i).getName();
                        contactPhoto = WhatsFreeCallApplication.getInstance().contactsEntityList.get(i).getIconUri();
                        mTvCallNumber.setText(contactName);
                    }
                }
            }
        }
    }

    /*链接状态监听*/
    private class SinchCallListener implements CallListener {
        /*电话结束状态 call 里面含有各种信息 */
        @Override
        public void onCallEnded(Call call) {
            call.answer();
            CallEndCause cause = call.getDetails().getEndCause();
            mAudioPlayer.stopProgressTone();
            mTvCallStatus.setText(call.getState().toString().toLowerCase());
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            String endMsg = "Call ended: " + call.getDetails().toString();
            System.out.println("SinchCallListener.onCallEnded" + endMsg);
            endStatusCall(cause.toString(), call);
        }

        /*接通状态结束提示音并开始讲话*/
        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
            mAudioPlayer.stopProgressTone();
            /*移除掉calling...事件*/
            handler.removeCallbacks(runnable);
            mCallDuration.setVisibility(View.VISIBLE);
            mTvCallStatus.setText(call.getState().toString().toLowerCase());
//            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        /*待接通状态可以播放提示音等*/
        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
//            mAudioPlayer.playProgressTone();
            long endtime = System.currentTimeMillis() - starttime;
            Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.READY_TIME, endtime);
            /*mTvCallStatus.setText(call.getState().toString());//设置状态*/
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

    }

    private void updateCallDuration() {
        if (getSinchServiceInterface() != null) {
            Call call = getSinchServiceInterface().getCall(mCallId);
            if (call != null) {
                mCallDuration.setText(formatTimespan(call.getDetails().getDuration()));
            }
        }
    }

    private String formatTimespan(int totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private String getTime() {
        Locale locale = Locale.getDefault();
        SimpleDateFormat simp = new SimpleDateFormat("HH:mm a", locale);
        return simp.format(System.currentTimeMillis());
    }

    /**
     * 设置费率信息等
     *
     * @param callScreenEntity
     */
    private void setCallScreenMsg(CallScrrenEntity callScreenEntity) {
        mTvCallRate.setText(callScreenEntity.getData().getRate_string());
        mTvCallUsd.setText(callScreenEntity.getData().getCredit_string());
        mTvCallEndtime.setText(callScreenEntity.getData().getRemain());
    }


    interface GetCallScreen_Interface {
        @POST(UrlConfig.CALL_INFO)
        retrofit2.Call<CallScrrenEntity> getCallInfo(@Header("Authorization") String author, @Body PostCountry body);
    }


    public void getCallInfos(String code, String number) {
        //步骤4:创建Retrofit对象
        OkHttpClient okhttpClient = HttpClentUtils.getOkhttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlConfig.BASE_URL) // 设置 网络请求 Url
                .client(okhttpClient)
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();
        // 步骤5:创建 网络请求接口 的实例
        GetCallScreen_Interface request = retrofit.create(GetCallScreen_Interface.class);
        //对 发送请求 进行封装( //对 发送请求 进行封装(因为传递json 所以将参数用实体包裹转成json))
        PostCountry postCountry = new PostCountry(code, number);
        String userToken = SpUtils.getString(WhatsFreeCallApplication.getInstance().getApplicationContext(), SpConfig.USER_TOKEN, "");
        retrofit2.Call<CallScrrenEntity> call = request.getCallInfo("Bearer " + userToken, postCountry);
        call.enqueue(new Callback<CallScrrenEntity>() {
            @Override
            public void onResponse(retrofit2.Call<CallScrrenEntity> call, Response<CallScrrenEntity> response) {
                Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.CALL_SCREEN_GET_MSG_RESPOSE_CODE, response.code());
                if (response != null) {
                    CallScrrenEntity callScrrenEntity = response.body();
                    if (callScrrenEntity != null) {
                        if (callScrrenEntity.getErr_code() == 0) {
                            setCallScreenMsg(callScrrenEntity);
                            Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.RATE, StatisticalConfig.OK);
                        } else {
                            Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.RATE, StatisticalConfig.NO);
                        }
                    } else {
                        showShortToast("Rate get error null");
                        Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.CALL_SCREEN_GET_MSG_RESPOSE_MSG, response.message());
                    }
                } else {
                    //统计
                    Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.CALL_SCREEN_GET_MSG_RESPOSE, StatisticalConfig.RRESPONSE_NULL);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<CallScrrenEntity> call, Throwable t) {
                System.out.println("CallScreenActivity.onFailure" + "getCallInfos" + t.getMessage().toString());
                Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.RATE, StatisticalConfig.NO);

            }
        });
    }


    private void initAudio() {
        audioManager.setMicrophoneMute(false);
        audioManager.setSpeakerphoneOn(false);
        mIvAudio.setImageDrawable(getResources().getDrawable(R.drawable.mic_off));
        mIvSpeaker.setImageDrawable(getResources().getDrawable(R.drawable.speaker_off));
    }

    @Override
    public void onBackPressed() {
    }

    NotificationReciver notificationReciver;

    private void registerReciver() {
        notificationReciver = new NotificationReciver();
        notificationReciver.setReciverListener(new NotificationReciver.ReciverListener() {
            @Override
            public void setReciverType(String type) {
                switch (type) {
                    default:
                        break;
                    case NotificationManagers.HUNGUP:
                        endCall();
                        notificationManagers.removeNotification(getApplicationContext());
                        break;
                    case NotificationManagers.SPEAKER:
                        audioManager.setSpeakerphoneOn(!audioManager.isSpeakerphoneOn());
                        break;
                }
            }
        });
        IntentFilter intentFilter = new IntentFilter(NotificationManagers.ACTION);
        registerReceiver(notificationReciver, intentFilter);
    }

    private void unRegister() {
        if (notificationReciver != null) {
            unregisterReceiver(notificationReciver);
        }
    }

    private int count = 1;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegister();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(0);
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (count > 3) {
                count = 1;
            }
            switch (count) {
                case 1:
                    mTvCallStatus.setText("Calling.");
                    break;
                case 2:
                    mTvCallStatus.setText("Calling..");
                    break;
                case 3:
                    mTvCallStatus.setText("Calling...");
                    break;
                default:
                    break;
            }
            count++;
            handler.postDelayed(runnable, 800);
        }
    };
    /*音量控制*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN://音量减
                audioManager.adjustStreamVolume(
                        AudioManager.STREAM_VOICE_CALL,
                        AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
//                setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
                return true;

            case KeyEvent.KEYCODE_VOLUME_UP://音量加
                audioManager.adjustStreamVolume(
                        AudioManager.STREAM_VOICE_CALL,
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
//                setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
                return true;

            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
