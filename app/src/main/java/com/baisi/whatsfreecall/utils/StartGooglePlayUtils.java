package com.baisi.whatsfreecall.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * Created by MnyZhao on 2018/1/20.
 */

public class StartGooglePlayUtils {

    public static void startGooglePlayByHttpUrl(Context context, String packageName, String zui) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("http://play.google.com/store/apps/details?id= " + packageName + "&referrer=utm_source%3D" + zui));
        context.startActivity(intent);
    }

    public static boolean startGooglePlayByMarketUrl(Context context, String packageName, String zui) {
        if ((packageName == null) || (packageName.length() == 0)) {
            return false;
        }
        if (isInstalled(context, "com.android.vending")) {
        } else {
            return false;
        }
        String market = "market://details?id=" + packageName + "&referrer=utm_source%3D" + zui;
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setPackage("com.android.vending");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(market));
        return startActivity(context, intent);
    }
    public static boolean isInstalled(Context context, String packageName) {
        boolean bInstalled = false;
        if (packageName == null) {
            return false;
        }
        PackageInfo packageInfo = null;

        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();

        }
        if (packageInfo == null) {
            bInstalled = false;
        } else {
            bInstalled = true;
        }
        return bInstalled;
    }
    public static boolean startActivity(Context context, Intent intent) {
        if ((context == null) || (intent == null))
            return false;
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException activityNotFoundException) {
            activityNotFoundException.printStackTrace();
            return false;
        } catch (IllegalArgumentException illegalArgumentException) {
            illegalArgumentException.printStackTrace();
            return false;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
    }


}
