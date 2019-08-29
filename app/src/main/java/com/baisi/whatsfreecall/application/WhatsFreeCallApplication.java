package com.baisi.whatsfreecall.application;

import android.app.ActivityManager;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.db.DbManager;
import com.baisi.whatsfreecall.entity.ContactsEntity;
import com.baisi.whatsfreecall.entity.RecoderEntity;
import com.baisi.whatsfreecall.manager.contactmanager.ContactsManagerNew;
import com.baisi.whatsfreecall.utils.firebaseutils.Firebase;
import com.bestgo.adsplugin.ads.AdAppHelper;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.fabric.sdk.android.Fabric;

/**
 * Created by MnyZhao on 2017/12/7.
 */

public class WhatsFreeCallApplication extends MultiDexApplication {
    private static volatile WhatsFreeCallApplication singleton;

    public WhatsFreeCallApplication() {
        singleton = this;
    }
    /*记录视频广告结束后出现的全屏的广告次数来计算那一次显示*/
    private int ShowVideoFullAds=1;
    public int getShowVideoFullAds(){
        return ShowVideoFullAds;
    }
    public void setShowVideoFullAds(int showVideoFullAds){
        this.ShowVideoFullAds=showVideoFullAds;
    }
    public List<ContactsEntity> contactsEntityList=new ArrayList<>();
    public List<RecoderEntity> recoderEntities=new ArrayList<>();
    private String appInstandID="";
    public String getAppInstandID(){
        return  appInstandID;
    }
    public void setAppInstandID(String appInstandID){
        this.appInstandID=appInstandID;
    }
    public static WhatsFreeCallApplication getInstance() {
        if (singleton == null) {
            synchronized (WhatsFreeCallApplication.class) {
                if (singleton == null) {
                    singleton = new WhatsFreeCallApplication();
                }
            }
        }
        return singleton;
    }

    private DbManager dbManager;
    /*线程池*/
    public ExecutorService fixedThread;

    @Override
    public void onCreate() {
        super.onCreate();
        if (isDefaultProcess()) {
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(this);
            Fabric.with(this, new Crashlytics());
            AdAppHelper.GA_RESOURCE_ID = R.xml.app_tracker;
            AdAppHelper.ENABLE_UPLOAD_AD_EVENT = true;
            AdAppHelper.FIREBASE = Firebase.getInstance(this);
            AdAppHelper.getInstance(getApplicationContext()).init(null);
            fixedThread = createFixed(Runtime.getRuntime().availableProcessors(), 200);
            loadData();
            loadRecoder();
        }
    }

    public DbManager getDbManager() {
        if (dbManager == null) {
            dbManager = new DbManager(WhatsFreeCallApplication.this);
        }
        return dbManager;
    }

    /**
     * @param corePoolSize    保存在线程池中的线程数
     * @param maximumPoolSize 最大线程数
     * @return
     */
    private ExecutorService createFixed(int corePoolSize, int maximumPoolSize) {
        ExecutorService service= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
       /* ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("whats-pool-%d").build();

        //Common Thread Pool
        ExecutorService service = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());*/
        return service;

    }

    private void shutdown() {
        if (fixedThread != null) {
            fixedThread.shutdown();//gracefully shutdown
        }
    }

    ContactsManagerNew managerNew;

    private void loadData() {
        managerNew = new ContactsManagerNew(this);
        WhatsFreeCallApplication.getInstance().fixedThread.execute(new Runnable() {
            @Override
            public void run() {
                contactsEntityList = managerNew.getAllContacts();
            }
        });
    }

    public void loadRecoder() {
        WhatsFreeCallApplication.getInstance().fixedThread.execute(new Runnable() {
            @Override
            public void run() {
                recoderEntities = getDbManager().queryData();
            }
        });
    }

    /**
     * 判断是否是主进程
     */
    private boolean isDefaultProcess() {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager mActivityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : mActivityManager.getRunningAppProcesses()) {
            if (appProcessInfo.pid == pid) {
                processName = appProcessInfo.processName;
                break;
            }
        }
        String packageName = this.getPackageName();
        if (processName.equals(packageName)) {
            return true;
        } else {
            return false;
        }
    }

}
