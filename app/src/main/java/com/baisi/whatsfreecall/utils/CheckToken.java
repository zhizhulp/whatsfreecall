package com.baisi.whatsfreecall.utils;

import android.text.TextUtils;

import com.baisi.whatsfreecall.application.WhatsFreeCallApplication;
import com.baisi.whatsfreecall.configs.SpConfig;
import com.baisi.whatsfreecall.utils.sputils.SpUtils;

/**
 * Created by MnyZhao on 2017/12/9.
 */

public class CheckToken {
    /**
     * true  null  未登录
     * false UnNull 登陆
     * @return
     */
    public static boolean checkToken() {
        return TextUtils.isEmpty(SpUtils.getString(WhatsFreeCallApplication.getInstance().getApplicationContext(), SpConfig.USER_TOKEN, ""));
    }
}
