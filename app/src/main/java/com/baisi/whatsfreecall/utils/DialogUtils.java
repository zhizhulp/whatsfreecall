package com.baisi.whatsfreecall.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import com.baisi.whatsfreecall.R;

public class DialogUtils {
    public static Dialog showPopupWindow(Context context,String get,String todayGet) {
        final Dialog dialog=new Dialog(context, R.style.transpanrent_theme);
        dialog.setContentView(R.layout.pick_up_money);
        dialog.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog.isShowing())dialog.dismiss();
            }
        });
        ((TextView) dialog.findViewById(R.id.tv_count)).setText(get);
        ((TextView) dialog.findViewById(R.id.tv_today_count)).setText(todayGet);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }
    public static Dialog showPopupWindow(Context context, String get, String todayGet, DialogInterface.OnDismissListener dismissListener) {
        final Dialog dialog=new Dialog(context, R.style.transpanrent_theme);
        dialog.setContentView(R.layout.pick_up_money);
        dialog.setOnDismissListener(dismissListener);
        dialog.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog.isShowing())dialog.dismiss();
            }
        });
        ((TextView) dialog.findViewById(R.id.tv_count)).setText(get);
        ((TextView) dialog.findViewById(R.id.tv_today_count)).setText(todayGet);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }
}
