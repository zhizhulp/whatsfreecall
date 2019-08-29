package com.baisi.whatsfreecall.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.adsconfig.NativeAdType;
import com.baisi.whatsfreecall.adsconfig.StatisticalConfig;
import com.baisi.whatsfreecall.application.WhatsFreeCallApplication;
import com.baisi.whatsfreecall.configs.SpConfig;
import com.baisi.whatsfreecall.db.DbManager;
import com.baisi.whatsfreecall.entity.CallMsgEntity;
import com.baisi.whatsfreecall.entity.ContactsEntity;
import com.baisi.whatsfreecall.entity.RecoderEntity;
import com.baisi.whatsfreecall.manager.contactmanager.ContactsManagerNew;
import com.baisi.whatsfreecall.sinchcall.base.CallBaseActivity;
import com.baisi.whatsfreecall.sinchcall.service.SinchService;
import com.baisi.whatsfreecall.ui.adapter.ViewPagerAdapter;
import com.baisi.whatsfreecall.ui.fragment.CallRecoderFragment;
import com.baisi.whatsfreecall.ui.fragment.ContactFragment;
import com.baisi.whatsfreecall.utils.CheckToken;
import com.baisi.whatsfreecall.utils.GlideCircleTransform;
import com.baisi.whatsfreecall.utils.StartGooglePlayUtils;
import com.baisi.whatsfreecall.utils.adsutils.FullAdsUtils;
import com.baisi.whatsfreecall.utils.adsutils.NativeAdsUtils;
import com.baisi.whatsfreecall.utils.budleutils.BundleUtils;
import com.baisi.whatsfreecall.utils.firebaseutils.Firebase;
import com.baisi.whatsfreecall.utils.firebaseutils.PromotionTracking;
import com.baisi.whatsfreecall.utils.httputils.NetWorkAuxiliaryUtil;
import com.baisi.whatsfreecall.utils.sputils.SpUtils;
import com.bestgo.adsplugin.ads.activity.ShowAdFilter;
import com.bumptech.glide.Glide;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeActivity extends CallBaseActivity implements ShowAdFilter {
    public static final int LOAD_CONTACT = 1;
    public static final int LOAD_RECODER = 2;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ImageView mHeaderUserIcon;
    private TextView mHeaderUserName;
    private Toolbar mToolbar;
    private TabLayout mTablayout;
    private ViewPager mViewpager;
    private TextView mTvShowBalance;
    private List<Fragment> mFragments;
    private List<String> mFragmentTitles;
    private ContactFragment contactFragment;
    private CallRecoderFragment callRecoderFragment;
    ContactsManagerNew managerNew;
    private DbManager dbManager;
    private FloatingActionButton mFab;
    /*页面第一次启动的时候并不执行因为在resume方法里sinchserviceinterface没初始化成功
    第二次获取焦点再去执行*/
    //ada广告位
    private FrameLayout mHome80;
    public List<ContactsEntity> contactsEntityList;
    public List<RecoderEntity> recoderEntities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        FullAdsUtils.isShowFull(StatisticalConfig.ENTERFULL, false, FullAdsUtils.SHOW_ENTER_FULL_CTRL);
        /*每天几次打开*/
        PromotionTracking.getInstance(getApplicationContext()).reportOpenMainPageCount();
        /*连续多少天*/
        PromotionTracking.getInstance(getApplicationContext()).reportContinuousDayCount();
        /*安装过什么*/
        PromotionTracking.getInstance(getApplicationContext()).reportAppInstall();
        /*链接状态*/
        NetWorkAuxiliaryUtil.getInstance(getApplicationContext()).sendSinchFireBase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        loadRecoder();
        mTvShowBalance.setText(SpUtils.getString(getApplicationContext(), SpConfig.USER_STRING_BALANCE, "$0.00"));
        setUser(SpUtils.getString(getApplicationContext(), SpConfig.USER_NAME, "Please Login"),
                SpUtils.getString(getApplicationContext(), SpConfig.USER_PHOTO_URL, ""));
        /*sinchinterface!=null 且 sinch未启动*/
        if (getSinchServiceInterface() != null) {
            if (!getSinchServiceInterface().isStarted()) {
                signSinch();
            }
        }
         /*安装多少天未删除*/
        PromotionTracking.getInstance(getApplicationContext()).reportUninstallDayCount();
    }

    public DbManager getDbManager() {
        if (dbManager == null) {
            dbManager = new DbManager(WhatsFreeCallApplication.getInstance());
        }
        return dbManager;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOAD_CONTACT:
                    System.out.println("HomeActivity.handleMessage>>>>>" + "LoadContact");
                    WhatsFreeCallApplication.getInstance().contactsEntityList.clear();
                    WhatsFreeCallApplication.getInstance().contactsEntityList.addAll(contactsEntityList);
                    contactFragment.setData(contactsEntityList);
                    break;
                case LOAD_RECODER:
                    System.out.println("HomeActivity.handleMessage>>>>>" + "RECODER");
                    WhatsFreeCallApplication.getInstance().recoderEntities.clear();
                    WhatsFreeCallApplication.getInstance().recoderEntities.addAll(recoderEntities);
                    callRecoderFragment.setDate(recoderEntities);
                    break;
                default:
                    break;
            }
        }
    };

    private void loadData() {
        managerNew = new ContactsManagerNew(this);
        WhatsFreeCallApplication.getInstance().fixedThread.execute(new Runnable() {
            @Override
            public void run() {
                contactsEntityList = managerNew.getAllContacts();
                handler.sendEmptyMessage(LOAD_CONTACT);
            }
        });
    }

    public void loadRecoder() {
        WhatsFreeCallApplication.getInstance().fixedThread.execute(new Runnable() {
            @Override
            public void run() {
                recoderEntities = getDbManager().queryData();

                handler.sendEmptyMessage(LOAD_RECODER);
            }
        });
    }

    /*******************Sinch********************/
    @Override
    protected void onServiceConnected() {
        signSinch();
        super.onServiceConnected();
        System.out.println("HomeActivity.onServiceConnected");
        getSinchServiceInterface().setStartListener(new SinchService.StartFailedListener() {
            @Override
            public void onStartFailed(SinchError error) {
                System.out.println("HomeActivity.onStartFailed" + error.toString());
                Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.SINCH_START_ERROR, "HOME" + error.toString());
            }

            @Override
            public void onStarted() {
                System.out.println("HomeActivity.onStarted");
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        super.onNewIntent(intent);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mTablayout = (TabLayout) findViewById(R.id.tl_home);
        mViewpager = (ViewPager) findViewById(R.id.vp_home);
        mHome80 = (FrameLayout) findViewById(R.id.home_80);
        NativeAdsUtils.setAdvView(NativeAdType.NATIVE_HOME_80, mHome80, StatisticalConfig.HOMEADS, false, NativeAdsUtils.SHOW_HOME_NATIVE);
        mTvShowBalance = (TextView) findViewById(R.id.tv_show_balance);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckToken.checkToken()) {
                    skip(DialActivity.class);
                } else {
                    /*未登录去往登陆界面*/
                    Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.GO_LOGIN_DIAL, StatisticalConfig.LOGIN_SHOW);
                    startLogin();
                }
            }
        });
        initToolbar();
        initNavigationView();
        setViewPager();
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.home_toolbar);
        mToolbar.setTitle(R.string.app_name_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setElevation(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setElevation(0);
        }
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_balance:
                        Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.HOME_CHARGE, StatisticalConfig.RESULT_CODE_CLICK);
                        skip(ChargeActivity.class);
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 初始化标题以及Fragment
     */
    private void initTitleAndFragment() {
        mFragments = new ArrayList<>();
        mFragmentTitles = new ArrayList<>();
        contactFragment = ContactFragment.Instance("CONTACT");
        contactFragment.setTestInterface(testInterface);
        callRecoderFragment = CallRecoderFragment.Instance("RECODER");
        callRecoderFragment.setOnRecoderItemClickListener(recoderItemClickListener);
        mFragments.add(contactFragment);
        mFragments.add(callRecoderFragment);
        mFragmentTitles.add(getResources().getString(R.string.title_contacts));
        mFragmentTitles.add(getResources().getString(R.string.title_recents));
    }

    ContactFragment.TestInterface testInterface = new ContactFragment.TestInterface() {
        @Override
        public void setTestListener(String number, int counterCode) {
            Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.RECODER_CLICK, StatisticalConfig.RESULT_CODE_CLICK);
            if (!CheckToken.checkToken()) {
                if (checkSinchisStartAtNull()) {
                    String locale = "SE";
                    int countryFlag = HomeActivity.this.getResources().getIdentifier("flag_" + locale.toLowerCase(), "drawable", HomeActivity.this.getPackageName());
                    Map<String, String> map = new HashMap<>();
                    map.put("token", SpUtils.getString(getApplicationContext(), SpConfig.USER_TOKEN, ""));
                    Call call = getSinchServiceInterface().callPhoneNumber("+" + counterCode + number, map);
                    String callid = call.getCallId();
                    CallMsgEntity callMsgEntity = new CallMsgEntity(callid, countryFlag,
                            "瑞典", counterCode, "Test Number",
                            "", number);
                    skip(CallScreenActivity.class, BundleUtils.getBundleUtils().getCallMsgBundle(callMsgEntity));
                } else {
                    showShortToast("Sinch is no start");
                }
            } else {
                startLogin();
                Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.GO_LOGIN_RECODER, StatisticalConfig.LOGIN_SHOW);
            }
        }
    };
    CallRecoderFragment.OnRecoderItemClickListener recoderItemClickListener = new CallRecoderFragment.OnRecoderItemClickListener() {
        @Override
        public void setRecoderEntity(RecoderEntity recoderEntity) {
            Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.RECODER_CLICK, StatisticalConfig.RESULT_CODE_CLICK);
            if (!CheckToken.checkToken()) {
                if (checkSinchisStartAtNull()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("token", SpUtils.getString(getApplicationContext(), SpConfig.USER_TOKEN, ""));
                    Call call = getSinchServiceInterface().callPhoneNumber("+" + recoderEntity.getCountryCode() + recoderEntity.getPhoneNumber(), map);
                    String callid = call.getCallId();
                    CallMsgEntity callMsgEntity = new CallMsgEntity(callid, recoderEntity.getCountryFlag(),
                            recoderEntity.getCounryName(), recoderEntity.getCountryCode(), recoderEntity.getName(),
                            recoderEntity.getPhotoUri(), recoderEntity.getPhoneNumber());
                    skip(CallScreenActivity.class, BundleUtils.getBundleUtils().getCallMsgBundle(callMsgEntity));
                } else {
                    showShortToast("Sinch is no start");
                }
            } else {
                startLogin();
                Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.GO_LOGIN_RECODER, StatisticalConfig.LOGIN_SHOW);
            }

        }
    };

    /**
     * 设置viewpager绑定tablayout
     */


    private void setViewPager() {
        initTitleAndFragment();
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mFragmentTitles, mFragments);
        mViewpager.setAdapter(viewPagerAdapter);
        mTablayout.setupWithViewPager(mViewpager);
    }

    /**
     * 初始化navigationView 侧边栏
     */
    private void initNavigationView() {
        /**toolbar+NavigationView（侧滑前提组合DrawerLyout其中的控件可以设置为NavigationView也可以是任意一个控件）*/
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //创建toggle（抽屉栏开关）
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //将togger设置给drawablelayout的监听器 显示左上角图标
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mHeaderUserIcon = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_iv_icon);
        mHeaderUserName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_tv_name);
        //设置选中item的颜色以及默认的颜色
        Resources resource = (Resources) getBaseContext().getResources();
        ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.navigation_menu_item_color);
        navigationView.setItemTextColor(csl);
        navigationView.setItemIconTintList(null);//取消统一风格的图标颜色 显示图标本身style
        navigationView.setNavigationItemSelectedListener(navListener);//绑定menu的点击事件
        disableNavigationViewScrollbars(navigationView);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                switch (flag) {
                    case R.id.nav_balance:
                        skip(ChargeActivity.class);
                        Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.NAV_GO_BALANCE, StatisticalConfig.GO_IN);
                        break;
                    case R.id.nav_call_rate:
                        Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.NAV_GO_RATE, StatisticalConfig.GO_IN);
                        skip(RateActivity.class);
                        break;
                    case R.id.nav_share:
                        Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.NAV_GO_SHARE, StatisticalConfig.GO_IN);
                        share("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
                        break;
                    case R.id.nav_feedback:
                        Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.NAV_GO_FEEDBACK, StatisticalConfig.GO_IN);
                        skip(FeedBackActivity.class);
                        break;
                    case R.id.nav_hand:
                        if (!StartGooglePlayUtils.startGooglePlayByMarketUrl(HomeActivity.this, getPackageName(), "dialog_five")) {
                            StartGooglePlayUtils.startGooglePlayByHttpUrl(HomeActivity.this, getPackageName(), "dialog_five");
                        }
                        break;
                }
                flag = 0;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }
    /*记录点击那个按钮*/
    public int flag;
    NavigationView.OnNavigationItemSelectedListener navListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            //关闭drawer
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawers();
            }
            int id = item.getItemId();
            flag = id;
            return true;
        }
    };

    /*去除scrollerbar*/
    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_toolbar_menu, menu);
        return true;
    }

    /**
     * @param userName    用户名
     * @param picturePath 头像路径
     */
    private void setUser(String userName, String picturePath) {
        if (userName.equals("Please Login")) {
            navigationView.getHeaderView(0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //关闭drawer
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawers();
                    }
                    Firebase.getInstance(getApplicationContext()).logEvent(StatisticalConfig.GO_LOGIN_NAV, StatisticalConfig.LOGIN_SHOW);
                    startLogin();
                }
            });
        } else {
            navigationView.getHeaderView(0).setOnClickListener(null);
        }
        //这种方法将图片设置成了圆形并转换成圆角将GLide默认加载成的RGB565转换成ARGB888 下面那种方法没有
        if (TextUtils.isEmpty(picturePath)) {
            Glide.with(this).load(R.drawable.avatar).asBitmap().transform(new GlideCircleTransform(HomeActivity.this)).into(mHeaderUserIcon);
        } else {
            Glide.with(this).load(picturePath).asBitmap().transform(new GlideCircleTransform(HomeActivity.this)).into(mHeaderUserIcon);
        }
        mHeaderUserName.setText(userName);
    }

    private void startLogin() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.putExtras(BundleUtils.getBundleUtils().getLoginBundle(false, BundleUtils.ENTER_CALL, "", null));
        startActivity(intent);
    }

    @Override
    public boolean allowShowAd() {
        return false;
    }
}
