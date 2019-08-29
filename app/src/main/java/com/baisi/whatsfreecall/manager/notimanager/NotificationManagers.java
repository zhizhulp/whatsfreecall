package com.baisi.whatsfreecall.manager.notimanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.application.WhatsFreeCallApplication;
import com.baisi.whatsfreecall.ui.activity.CallScreenActivity;

/**
 * @author MnyZhao
 * @date 2018/1/10
 */

public class NotificationManagers {
    public static final String ACTION = "notificationAction";
    public static final String HUNGUP = "HUNGUP";
    public static final String SPEAKER = "SPEAKER";
    private Bitmap img;
    public NotificationManagers(){

    }

    /**
     * The notification bar shows the conditions
     * Call interface is not visible display
     * click Callend button remove notification
     * notification go callScreenActivity remove notification
     * notification click end remove notification
     * 判断是否需要显示通知栏
     * 通知栏显示条件  拨号中 当前界面不可见 通知栏显示
     * 当用户点击挂断按钮的时候 则不需要弹出通知栏
     * 当从通知栏进入CallScreenActivity的时候 需要移除通知
     * 当电话挂断通知栏需要移除
     */
    public void showNotification(Context mContext, int id, int countryCode, String number, String countryName, String photoUri) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(WhatsFreeCallApplication.getInstance());
        mBuilder.setSmallIcon(R.drawable.notification_icon, 0);//无论默认Noti还是自定义都必须设置这一属性
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification_layout);
        /*设置textView显示*/
        remoteViews.setTextViewText(R.id.tv_noti_country, countryName);
        String format = mContext.getString(R.string.notification_number, "+" + countryCode, number);
        Spanned htmlStr = Html.fromHtml(format);
        remoteViews.setTextViewText(R.id.tv_noti_number, htmlStr);
        /*加载bitmap对象*/
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        img = BitmapFactory.decodeFile(photoUri, options);
        if (TextUtils.isEmpty(photoUri)) {
            remoteViews.setImageViewResource(R.id.iv_noti_icon, R.drawable.avatar);
        } else {
            remoteViews.setImageViewBitmap(R.id.iv_noti_icon, img);
        }

        /*设置removteview点击事件*/
        PendingIntent goHomeIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext, CallScreenActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
        /*设置点击hungup speaker 点击事件 以通知的形式发出 在callScreenActivity接收*/
        Intent intentHungup = new Intent(ACTION);
        intentHungup.putExtra(ACTION, HUNGUP);
        /*参数2 请求码 每个都应该不同 相同的话intent取出来的值是相同的*/
        PendingIntent hungupIntent = PendingIntent.getBroadcast(mContext, 1, intentHungup, 0);
        remoteViews.setOnClickPendingIntent(R.id.ll_noti_hangup, hungupIntent);
        Intent intentSpeaker = new Intent(ACTION);
        intentSpeaker.putExtra(ACTION, SPEAKER);
         /*参数2 请求码 每个都应该不同 相同的话intent取出来的值是相同的*/
        PendingIntent speakerIntent = PendingIntent.getBroadcast(mContext, 2, intentSpeaker, 0);
        remoteViews.setOnClickPendingIntent(R.id.ll_noti_speaker, speakerIntent);


        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);//设置该通知优先级
        mBuilder.setCustomBigContentView(remoteViews);// 默认高度256 超出则显示不全
        mBuilder.setContentIntent(goHomeIntent);
        mBuilder.setTicker(mContext.getResources().getString(R.string.app_name));
        Notification notification = mBuilder.build();
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }

    public void removeNotification(Context mContext) {

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        if (img != null) {
            img.recycle();
            img = null;
        }
    }

}
