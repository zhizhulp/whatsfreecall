package com.baisi.whatsfreecall.utils.httputils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by MnyZhao on 2017/12/8.
 */

public class WifiUtils {
    /**
     * 获取MAC地址
     * @param mContext
     * @return
     */
    public static String getMacAddress(Context mContext) {
        String macStr = "";
        WifiManager wifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getMacAddress() != null) {
            macStr = wifiInfo.getMacAddress();// MAC地址
        } else {
            macStr = "null";
        }

        return macStr;
    }
}
