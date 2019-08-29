package com.baisi.whatsfreecall.manager.notimanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by MnyZhao on 2018/1/10.
 */

public class NotificationReciver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if (NotificationManagers.ACTION.equals(intent.getAction())) {
            switch (intent.getStringExtra(NotificationManagers.ACTION)){
                case NotificationManagers.HUNGUP:
                    if(reciverListener!=null){
                        reciverListener.setReciverType(NotificationManagers.HUNGUP);
                    }
                    break;
                case NotificationManagers.SPEAKER:
                    if(reciverListener!=null){
                        reciverListener.setReciverType(NotificationManagers.SPEAKER);
                    }
                    break;
                default:
                    break;
            }
        }
    }
    public interface ReciverListener{
        void setReciverType(String type);
    }
    ReciverListener reciverListener;
    public void setReciverListener(ReciverListener reciverListener){
        this.reciverListener=reciverListener;
    }
}
