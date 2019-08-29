package com.baisi.whatsfreecall.ui.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.adsconfig.NativeAdType;
import com.baisi.whatsfreecall.adsconfig.StatisticalConfig;
import com.baisi.whatsfreecall.application.WhatsFreeCallApplication;
import com.baisi.whatsfreecall.configs.SpConfig;
import com.baisi.whatsfreecall.entity.CallMsgEntity;
import com.baisi.whatsfreecall.entity.ContactsEntity;
import com.baisi.whatsfreecall.sinchcall.base.CallBaseActivity;
import com.baisi.whatsfreecall.sinchcall.service.SinchService;
import com.baisi.whatsfreecall.utils.adsutils.NativeAdsUtils;
import com.baisi.whatsfreecall.utils.budleutils.BundleUtils;
import com.baisi.whatsfreecall.utils.firebaseutils.Firebase;
import com.baisi.whatsfreecall.utils.libphonenumberutils.GeoUtil;
import com.baisi.whatsfreecall.utils.sputils.SpUtils;
import com.baisi.whatsfreecall.view.KeyBoardView;
import com.bestgo.adsplugin.ads.activity.ShowAdFilter;
import com.sahooz.library.CountryEntity;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DialActivity extends CallBaseActivity implements ShowAdFilter {
    public static final int REAUEST_CODE = 100;
    public static final int RESULT_CODE = 101;
    private ImageView mIvDialShowCountryIcon;
    private LinearLayout mLlDailGoCountry;
    /**
     * +77
     */
    private TextView mTvCountry;
    /**
     * 74001090349
     */
    private EditText mEtNumber;
    private KeyBoardView mMykb;
    private TextView mTvCountryName;
    /*广告位置*/
    private FrameLayout mFlDialAds;
    List<CountryEntity> models = new ArrayList<>();
    CountryEntity entity;
    Bundle bundle;
    private int bundleCountryCode;
    ContactsEntity contactsEntity;
    /*据此判断是从哪个页面进入 若是空 则是从拨号按钮进入 若不是 则说明是从 通讯录或者其他页面进入*/
    private boolean isBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial);
        isBundle = false;
        initView();
        checkBundleIsNull();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCountryInfo(entity);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        super.onNewIntent(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void initView() {
        mTvCountryName = (TextView) findViewById(R.id.tv_country_name);
        mIvDialShowCountryIcon = (ImageView) findViewById(R.id.iv_dial_show_country_icon);
        mLlDailGoCountry = (LinearLayout) findViewById(R.id.ll_dail_go_country);
        mLlDailGoCountry.setOnClickListener(listener);
        mTvCountry = (TextView) findViewById(R.id.tv_country_number_dial);
        mTvCountry.setOnClickListener(listener);
        mEtNumber = (EditText) findViewById(R.id.et_number);
        mFlDialAds = (FrameLayout) findViewById(R.id.fl_dial_80);
        NativeAdsUtils.setAdvView(NativeAdType.NATIVE_DIAL_80, mFlDialAds, StatisticalConfig.DIALADS, false, NativeAdsUtils.SHOW_DIAL_NATIVE);
        hideSoftInputMethod(mEtNumber);
        mMykb = (KeyBoardView) findViewById(R.id.mykb);
        mMykb.setOnCallClickListener(callClickListener);
        mMykb.setOnDeleteClickListener(deleteClickListener);
        mMykb.setOnHideClickListener(hideClickListener);
        mMykb.setOnNumberClickListener(numberClickListener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                default:
                    break;
                case R.id.ll_dail_go_country:
                case R.id.tv_country_number_dial:
                    Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.DIAL_SELECT_COUNTRY, StatisticalConfig.RESULT_CODE_CLICK);
                    Intent intent = new Intent(DialActivity.this, SelectConutryActivity.class);
                    startActivityForResult(intent, REAUEST_CODE);
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REAUEST_CODE == requestCode) {
            if (RESULT_CODE == resultCode) {
                if ((CountryEntity) data.getSerializableExtra("entity") != null) {
                    entity = (CountryEntity) data.getSerializableExtra("entity");
                }
                if (!(entity.getCode() == 0 || TextUtils.isEmpty(entity.getName()))) {
                    setCountryInfo(entity);
                }
            }
        }
    }


    KeyBoardView.OnDeleteClickListener deleteClickListener = new KeyBoardView.OnDeleteClickListener() {
        @Override
        public void setDeleteListener(int mode) {
            switch (mode) {
                case KeyBoardView.CLICK:
                    deleteNumber();
                    break;
                case KeyBoardView.LONG_CLICK:
                    mEtNumber.setText("");
                    break;
                default:
                    break;
            }

        }
    };
    KeyBoardView.OnHideClickListener hideClickListener = new KeyBoardView.OnHideClickListener() {
        @Override
        public void setHide(boolean isHide) {
            finish();
        }
    };
    KeyBoardView.OnNumberClickListener numberClickListener = new KeyBoardView.OnNumberClickListener() {
        @Override
        public void setNumber(String number) {
            mEtNumber.requestFocus();//获取焦点 光标出现
            insertNumber(number);
        }
    };

    private void deleteNumber() {
        int index = mEtNumber.getSelectionStart();
        if (mEtNumber.length() != 0 && index > 0) {
            Editable editable = mEtNumber.getText();
            editable.delete(index - 1, index);
        }
    }

    private void insertNumber(String number) {
        int index = mEtNumber.getSelectionStart();
        Editable editable = mEtNumber.getText();
        editable.insert(index, number);
    }

    KeyBoardView.OnCallClickListener callClickListener = new KeyBoardView.OnCallClickListener() {
        @Override
        public void setCallListener() {
           /* if (Double.parseDouble((SpUtils.getString(getApplicationContext(), SpConfig.USER_STRING_BALANCE, "0").replace("$", ""))) > 0) {*/
            if (SpUtils.getInt(getApplicationContext(), SpConfig.USER_BALABCE, 0) > 0) {
                CallGo();
            } else {
                showShortToast(R.string.insufficient_balance);
            }
        }
    };

    /**
     * 根据电话号码 查询国家代码 并判断 number中是否含有区号 若有去除若没有显示自动匹配大盘的
     *
     * @param phoneNumber
     */
    private void checkSetCountryAndCode(String phoneNumber) {
        if (GeoUtil.getCountryCode(WhatsFreeCallApplication.getInstance().getApplicationContext(), phoneNumber) == 0) {
            showShortToast(R.string.check_phone_msg);
            getCountryEntity();
            Firebase.getInstance(getApplicationContext())._logEvent(StatisticalConfig.DIAL_CONTACT_GO_CHECK_NUMBER, entity.getCode() + "", phoneNumber);
            return;
        } else {
            if (phoneNumber.startsWith("+" + GeoUtil.getCountryCode(getApplicationContext(), phoneNumber))) {
                String strCode = "+" + GeoUtil.getCountryCode(getApplicationContext(), phoneNumber);
                mEtNumber.setText(phoneNumber.substring(strCode.length(), phoneNumber.length()));
            } else if (phoneNumber.startsWith("" + GeoUtil.getCountryCode(this, phoneNumber))) {
                String strNoneCode = "" + GeoUtil.getCountryCode(this, phoneNumber);
                mEtNumber.setText(phoneNumber.substring(strNoneCode.length(), phoneNumber.length()));
            } else {
                mEtNumber.setText(phoneNumber);
            }
            mTvCountry.setText("+" + GeoUtil.getCountryCode(getApplicationContext(), phoneNumber));
        }
    }

    /**
     * 判断bundle是否是空 是空表示按拨号进入不是表示点击通讯录进入
     */
    private void checkBundleIsNull() {
        bundle = getIntent().getExtras();
        models.addAll(CountryEntity.getAll(this, null));
        if (bundle != null) {
            isBundle = true;
            contactsEntity = bundle.getParcelable(BundleUtils.CONTACT_ENTITY);
            if (contactsEntity != null) {
                bundleCountryCode = GeoUtil.getCountryCode(getApplicationContext(), contactsEntity.getPhone());
                if (bundleCountryCode != 0) {
                    getCountryEntity(bundleCountryCode);
                } else {
                    getCountryEntity();
                }
                checkSetCountryAndCode(contactsEntity.getPhone());
            } else {
                isBundle = false;
                getCountryEntity();
            }
        } else {
            isBundle = false;
            getCountryEntity();
        }
    }

    private void getCountryEntity() {
        for (int i = 0; i < models.size(); i++) {
            System.out.println("DialActivity.onCreate" + i + "---------" + models.get(i).locale);
            if (GeoUtil.getCurrentCountryIso(getApplicationContext()).equals(models.get(i).locale)) {
                entity = models.get(i);
                break;
            }
        }
    }

    private void setCountryInfo(CountryEntity entity) {
        if (entity != null) {
            if (mIvDialShowCountryIcon != null) {
                try {
                    mIvDialShowCountryIcon.setImageDrawable(getResources().getDrawable(entity.flag));
                }catch (Resources.NotFoundException e){
                    mIvDialShowCountryIcon.setImageDrawable(getResources().getDrawable(R.drawable.flag_cn));
                }
                mTvCountry.setText("+" + entity.code);
                mTvCountryName.setText(entity.getName());
            }
        } else {
            getCountryEntity();
        }

    }

    /**
     * 获取国家实体对象
     *
     * @param code
     */
    private void getCountryEntity(int code) {
        for (int i = 0; i < models.size(); i++) {
            if (code == models.get(i).getCode()) {
                entity = models.get(i);
                break;
            }
        }
    }

    private void CallGo() {
        /*应该先验证手机号是否正确*/
        if (!TextUtils.isEmpty(mEtNumber.getText().toString().trim())) {
            StringBuilder builder = new StringBuilder();
            String countryCode = "";
            if (entity != null) {
                countryCode = "+" + entity.getCode();
                builder.append(countryCode);
            } else {
                countryCode = mTvCountry.getText().toString();
                builder.append(countryCode);
            }
            builder.append(mEtNumber.getText().toString().trim());
            if (!builder.toString().contains("46000000000")) {
                if (!GeoUtil.checkPhoneNumber(getApplicationContext(), builder.toString())) {
                    if (TextUtils.isEmpty(countryCode)) {
                    } else {
                        Firebase.getInstance(getApplicationContext())._logEvent(StatisticalConfig.DIAL_CHECK_PHONE_NUMBER_CODE, countryCode.replace("+", ""), mEtNumber.getText().toString().trim());
                    }
                    showShortToast(R.string.check_phone_msg);
                    return;
                }
            }
            if (checkSinchisStartAtNull()) {
                Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.DIAL_CALL_CLICK, StatisticalConfig.OK);
                Map<String, String> map = new HashMap<>();
                map.put("token", SpUtils.getString(getApplicationContext(), SpConfig.USER_TOKEN, ""));
                Call call = getSinchServiceInterface().callPhoneNumber(builder.toString(), map);
                String callId = call.getCallId();
            /*如果是从通讯录进入则传递名字和头像id*/
                CallMsgEntity callMsgEntity;
                if (entity == null) {
                    showShortToast("The area does not support this feature");
                    return;
                }
                if (isBundle) {
                    if (contactsEntity.getPhone().contains(mEtNumber.getText().toString().trim().replace(" ", ""))) {
                        callMsgEntity = new CallMsgEntity(callId, entity.getFlag(),
                                entity.getName(), entity.getCode(), contactsEntity.getName(), contactsEntity.getIconUri(),
                                mEtNumber.getText().toString().trim().replace(" ", ""));
                    } else {
                        callMsgEntity = new CallMsgEntity(callId, entity.getFlag(),
                                entity.getName(), entity.getCode(), "", "",
                                mEtNumber.getText().toString().trim().replace(" ", ""));
                    }
                } else {
                    callMsgEntity = new CallMsgEntity(callId, entity.getFlag(),
                            entity.getName(), entity.getCode(), "", "",
                            mEtNumber.getText().toString().trim().replace(" ", ""));
                }
                skip(CallScreenActivity.class, BundleUtils.getBundleUtils().getCallMsgBundle(callMsgEntity));
                finish();
            } else {
                showShortToast("Sinch is no Start");
                Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.CLICK_CALL_SINCH_STATES, "Sinch is no Start");
            }
        } else {
            showShortToast(R.string.enter_phone);
        }
    }

    /*******************Sinch********************/
    @Override
    protected void onServiceConnected() {
        if (!checkSinchisStartAtNull()) {
            signSinch();
            getSinchServiceInterface().setStartListener(new SinchService.StartFailedListener() {
                @Override
                public void onStartFailed(SinchError error) {
                    System.out.println("DialActivity.onStartFailed" + error.toString());
                    Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.SINCH_START_ERROR, "DialActivity" + error.toString());
                }

                @Override
                public void onStarted() {
                    System.out.println("DialActivity.onStarted");
                }
            });
        }
        System.out.println("DialActivity.onServiceConnected>>>>>>>>>>>>>>>>>>>>" + getSinchServiceInterface().isStarted());
    }

    @Override
    public boolean allowShowAd() {
        return false;
    }
}
