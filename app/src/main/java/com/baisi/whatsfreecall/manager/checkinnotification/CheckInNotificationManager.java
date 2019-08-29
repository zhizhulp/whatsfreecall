package com.baisi.whatsfreecall.manager.checkinnotification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;

import com.baisi.whatsfreecall.R;
import com.baisi.whatsfreecall.ui.activity.checkin.CheckInActivity;

/**
 * Created by nick on 2018/2/7.
 */

public class CheckInNotificationManager {

    private Context mContext;
    private NotificationManager notificationManager;

    public CheckInNotificationManager(Context context){
        this.mContext = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void openNotification(){
        Intent intent = new Intent(mContext, CheckInActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);//设置该通知优先级
        mBuilder.setSmallIcon(R.drawable.notification_logo);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.ic_launcher));
        mBuilder.setContentTitle("Daily check-in reminder");
        mBuilder.setContentText("Click to get bonus money!");
        mBuilder.setContentIntent(pIntent);
        mBuilder.setAutoCancel(false);

        //无法滑动取消
        //mBuilder.setOngoing(true);
        notificationManager.notify(1011, mBuilder.build());
    }

    public void closeNotification(){
        notificationManager.cancel(1011);
    }




}
