package com.baisi.whatsfreecall.utils.firebaseutils;

/**
 * Created by MnyZhao on 2018/1/13.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.format.DateUtils;


import java.util.Calendar;
import java.util.HashSet;

/**
 * Created by huangjian on 2017/11/20.
 */

public class PromotionTracking {
    private Context mContext;
    private static PromotionTracking mInstance;
    private Firebase mFirebase;
    private SharedPreferences mSharedPreference;
    private SharedPreferences.Editor mEditor;
    private HashSet<String> mPhoneModels;
    private HashSet<String> mPhoneBrands;

    private void initPhoneModels() {
        String[] models = ("SM-J200G\n" +
                "SM-J200G\n" +
                "SM-J700F\n" +
                "SM-G530H\n" +
                "GT-I9060I\n" +
                "SM-G531H\n" +
                "SM-G360H\n" +
                "SM-G355H\n" +
                "SM-G531F\n" +
                "GT-I9300\n" +
                "SM-G610F\n" +
                "SM-J500F\n" +
                "GT-S7582\n" +
                "SM-G7102\n" +
                "SM-J210F\n" +
                "SM-G361H\n" +
                "GT-I9500\n" +
                "SM-N9005\n" +
                "SM-G900F\n" +
                "SM-J710F\n" +
                "SM-A500F\n" +
                "GT-I9301I\n" +
                "GT-I9060I\n" +
                "GT-I9082\n" +
                "GT-N7100\n" +
                "Motorola XT1080\n" +
                "SM-J110F\n" +
                "SM-G532F\n" +
                "SM-J120G\n" +
                "SM-G350E\n" +
                "SM-J500H\n" +
                "SM-N910C\n" +
                "SM-G316HU\n" +
                "GT-I8262\n" +
                "SM-G313HU\n" +
                "HTC Desire 820G\n" +
                "SM-G570F\n" +
                "SM-A700FD\n" +
                "SM-J100F\n" +
                "SM-J320F\n" +
                "SM-J200H\n" +
                "SM-J200F\n" +
                "SM-A500G\n" +
                "SM-J510F\n" +
                "SM-G318HZ\n" +
                "SM-J110G\n" +
                "SM-G313HZ \n" +
                "Lenovo A536\n" +
                "SM-G935F\n" +
                "SM-G550FY\n" +
                "GT-S7562\n" +
                "GT-I8552\n" +
                "HTC Desire 526G\n" +
                "SM-N900T\n" +
                "SM-G532G\n" +
                "SM-A500H\n" +
                "LG MS345\n" +
                "GT-S7580\n" +
                "GT-I9100\n" +
                "GT-I8200N\n" +
                "SM-G530F\n" +
                "SM-N900\n" +
                "SM-A510F\n" +
                "Motorola XT907\n" +
                "SM-G600FY\n" +
                "SM-J120F\n" +
                "SM-G900H\n" +
                "SM-E500H\n" +
                "SM-G900V\n" +
                "Micromax A106\n" +
                "SM-J320H\n" +
                "SM-A310F\n" +
                "SM-G900A\n" +
                "SM-J510FN\n" +
                "GT-I9300I\n" +
                "SM-A300H\n" +
                "SM-A710F\n" +
                "SM-N920C\n" +
                "SM-J111F\n" +
                "SM-J320M\n" +
                "GT-I8190\n" +
                "SM-G357FZ \n" +
                "SM-A300FU\n" +
                "SM-G313H\n" +
                "Symphony V75\n" +
                "SM-G361F\n" +
                "GT-I9195\n" +
                "Motorola XT923\n" +
                "SM-A300FU\n" +
                "SM-N910F\n" +
                "SM-E700H\n" +
                "GT-N7000").toLowerCase().split("\n");
        mPhoneModels = new HashSet<>(models.length);
        for (String model:models){
            mPhoneModels.add(model);
        }
    }

    private void initPhoneBrands() {
        String[] brands = ("xiaomi\n" +
                "TRUE\n" +
                "tecno\n" +
                "zte\n" +
                "wiko\n" +
                "vivo\n" +
                "tecno\n" +
                "vodafone\n" +
                "sony_ericsson\n" +
                "sony\n" +
                "smartfren\n" +
                "sharp\n" +
                "samsung\n" +
                "oppo\n" +
                "Oneplus\n" +
                "Nyx\n" +
                "Micromax\n" +
                "Motorola\n" +
                "Lg\n" +
                "Lenovo\n" +
                "Lava\n" +
                "Itel\n" +
                "Infinix\n" +
                "Huawei\n" +
                "Htc\n" +
                "Google\n" +
                "Gionee\n" +
                "Docomo\n" +
                "Condor\n" +
                "Asus\n" +
                "Amazon\n" +
                "Alcatel").toLowerCase().split("\n");
        mPhoneBrands = new HashSet<>(brands.length);
        for (String brand : brands) {
            mPhoneBrands.add(brand);
        }
    }

    private PromotionTracking(Context context) {
        mContext = context.getApplicationContext();
        mFirebase = Firebase.getInstance(mContext);
        mSharedPreference = DefaultSharedPrefeencesUtil.getDefaultSharedPreferences(mContext);
        mEditor = mSharedPreference.edit();
        initPhoneModels();
        initPhoneBrands();
    }

    public static PromotionTracking getInstance(Context context) {
        if (mInstance == null) {
            synchronized (PromotionTracking.class) {
                if (mInstance == null) {
                    mInstance = new PromotionTracking(context);
                }
            }
        }
        return mInstance;
    }

    private boolean isNewDay(Calendar lastEventCalendar, Calendar current) {
        return lastEventCalendar.get(Calendar.DATE) != current.get(Calendar.DATE);
    }

    public void reportOpenMainPageCount() {
        Calendar currentCalendar = Calendar.getInstance();
        long last = mSharedPreference.getLong(SharedPreferenceKey.OPEN_MAIN_PAGE_TIME, currentCalendar.getTimeInMillis());
        Calendar lastCalendar = Calendar.getInstance();
        lastCalendar.setTimeInMillis(last);
        int count = mSharedPreference.getInt(SharedPreferenceKey.OPEN_MAIN_PAGE_COUNT, 0);
        if (isNewDay(lastCalendar, currentCalendar)) {
            lastCalendar = currentCalendar;
            count = 0;
        }
        count++;
        if (count == 2) {
            mFirebase.logEvent("每天2次打开主页的用户", "广告投放统计");
        } else if (count == 3){
            mFirebase.logEvent("每天3次打开主页的用户", "广告投放统计");
        }
        mEditor.putLong(SharedPreferenceKey.OPEN_MAIN_PAGE_TIME, lastCalendar.getTimeInMillis())
                .putInt(SharedPreferenceKey.OPEN_MAIN_PAGE_COUNT, count)
                .apply();
    }

    private boolean isContinousDay(Calendar lastEventCalendar, Calendar current) {
        return current.get(Calendar.DAY_OF_YEAR) - lastEventCalendar.get(Calendar.DAY_OF_YEAR) == 1
                || (lastEventCalendar.get(Calendar.MONTH) == Calendar.DECEMBER
                && lastEventCalendar.get(Calendar.DATE) == 31
                && current.get(Calendar.MONTH) == Calendar.JANUARY
                && current.get(Calendar.DATE) == 1);
    }

    public void reportSwitchCountry() {
        mFirebase.logEvent("切换国家", "广告投放统计");
    }

    public void reportAppInstall() {
        Calendar currentCalendar = Calendar.getInstance();
        long last = mSharedPreference.getLong(SharedPreferenceKey.INSTALL_APP_TIME, currentCalendar.getTimeInMillis() - DateUtils.DAY_IN_MILLIS);
        Calendar lastCalendar = Calendar.getInstance();
        lastCalendar.setTimeInMillis(last);
        if (isNewDay(lastCalendar, currentCalendar)) {
            lastCalendar = currentCalendar;
            PackageManager packageManager = mContext.getPackageManager();
            try{
                packageManager.getPackageInfo("com.facebook.katana", 0);
                mFirebase.logEvent("装过Facebook", "广告投放统计");
            } catch (Exception e) {
            }
            try{
                packageManager.getPackageInfo("com.skype.raider", 0);
                mFirebase.logEvent("装过Skype", "广告投放统计");
            } catch (Exception e) {
            }
            try{
                packageManager.getPackageInfo("com.twitter.android", 0);
                mFirebase.logEvent("装过Twitter", "广告投放统计");
            } catch (Exception e) {
            }
            try{
                packageManager.getPackageInfo("com.whatsapp", 0);
                mFirebase.logEvent("装过WhatsApp", "广告投放统计");
            } catch (Exception e) {
            }
            mEditor.putLong(SharedPreferenceKey.INSTALL_APP_TIME, lastCalendar.getTimeInMillis()).apply();
        }
    }

    public void reportPhoneModelAndAndroidOS() {
        Calendar currentCalendar = Calendar.getInstance();
        long last = mSharedPreference.getLong(SharedPreferenceKey.PHONE_MODEL_OS_TIME, currentCalendar.getTimeInMillis() - DateUtils.DAY_IN_MILLIS);
        Calendar lastCalendar = Calendar.getInstance();
        lastCalendar.setTimeInMillis(last);
        if (isNewDay(lastCalendar, currentCalendar)) {
            lastCalendar = currentCalendar;
            String model = Build.MODEL.toLowerCase();
            if (mPhoneModels.contains(model)) {
                mFirebase.logEvent("手机型号" + model, "广告投放统计");
            }
            String brand = Build.MODEL.toLowerCase();
            if (mPhoneBrands.contains(brand)) {
                mFirebase.logEvent("手机品牌" + brand, "广告投放统计");
            }
            mFirebase.logEvent("手机版本" + Build.VERSION.RELEASE, "广告投放统计");
            mEditor.putLong(SharedPreferenceKey.INSTALL_APP_TIME, lastCalendar.getTimeInMillis()).apply();
        }
    }

    public void reportContinuousDayCount() {
        Calendar currentCalendar = Calendar.getInstance();
        long last = mSharedPreference.getLong(SharedPreferenceKey.CONTINOUS_DAY_TIME, currentCalendar.getTimeInMillis() - DateUtils.DAY_IN_MILLIS);
        Calendar lastCalendar = Calendar.getInstance();
        lastCalendar.setTimeInMillis(last);
        int count = mSharedPreference.getInt(SharedPreferenceKey.CONTINOUS_DAY_COUNT, 0);
        if (isNewDay(lastCalendar, currentCalendar)) {
            if (isContinousDay(lastCalendar, currentCalendar)) {
                count++;
            } else {
                count = 0;
            }
            lastCalendar = currentCalendar;
            if (count >= 2 && count < 8) {
                mFirebase.logEvent("连续打开" + count + "天", "广告投放统计");
            } else if (count >=8 && count < 14){
                mFirebase.logEvent("连续打开7天", "广告投放统计");
            } else if (count >=14 && count < 30) {
                mFirebase.logEvent("连续打开14天", "广告投放统计");
            } else if (count >= 30) {
                mFirebase.logEvent("连续打开30天", "广告投放统计");
            }
            mEditor.putLong(SharedPreferenceKey.CONTINOUS_DAY_TIME, lastCalendar.getTimeInMillis())
                    .putInt(SharedPreferenceKey.CONTINOUS_DAY_COUNT, count)
                    .apply();
        }
    }

    public void reportUninstallDayCount() {
        Calendar currentCalendar = Calendar.getInstance();
        long last = mSharedPreference.getLong(SharedPreferenceKey.UNINSTALL_DAY_TIME, currentCalendar.getTimeInMillis() - DateUtils.DAY_IN_MILLIS);
        Calendar lastCalendar = Calendar.getInstance();
        lastCalendar.setTimeInMillis(last);
        if (isNewDay(lastCalendar, currentCalendar)) {
            try {
                PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                long installTime = packageInfo.firstInstallTime;
                int day = (int) ((currentCalendar.getTimeInMillis() - installTime) / DateUtils.DAY_IN_MILLIS);
                int uninstallDay = day - 1;
                if (uninstallDay >= 2 && uninstallDay < 8) {
                    mFirebase.logEvent("安装" + uninstallDay + "天未删除", "广告投放统计");
                } else if (uninstallDay >=8 && uninstallDay <14){
                    mFirebase.logEvent("安装7天未删除", "广告投放统计");
                } else if (uninstallDay >=14 && uninstallDay < 30) {
                    mFirebase.logEvent("安装14天未删除", "广告投放统计");
                } else if (uninstallDay >= 30) {
                    mFirebase.logEvent("安装30天未删除", "广告投放统计");
                }
                lastCalendar = currentCalendar;
                mEditor.putLong(SharedPreferenceKey.UNINSTALL_DAY_TIME, lastCalendar.getTimeInMillis())
                        .apply();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
