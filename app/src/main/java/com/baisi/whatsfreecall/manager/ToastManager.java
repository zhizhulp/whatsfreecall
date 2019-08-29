package com.baisi.whatsfreecall.manager;

import android.widget.Toast;

import com.baisi.whatsfreecall.application.WhatsFreeCallApplication;

public class ToastManager {
    private static Toast mToast;

    public static void showShortToast(CharSequence string) {
        if (mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(WhatsFreeCallApplication.getInstance(), string, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public static void showLongToast(CharSequence string) {
        if (mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(WhatsFreeCallApplication.getInstance(), string, Toast.LENGTH_LONG);
        mToast.show();
    }
}
