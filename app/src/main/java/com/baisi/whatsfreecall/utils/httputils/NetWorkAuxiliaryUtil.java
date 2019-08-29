package com.baisi.whatsfreecall.utils.httputils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.baisi.whatsfreecall.adsconfig.NetWorkStates;
import com.baisi.whatsfreecall.utils.firebaseutils.Firebase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MnyZhao on 2018/1/20.
 */

public class NetWorkAuxiliaryUtil {
    public static final int MSGWHAT = 2031;
    private int count = 6;
    private int size = 0;
    private String sinchDns, sinchPing;
    private String myDns, myPing;
    private String myService;
    private String googleService;
    private Context mContext;
    private static NetWorkAuxiliaryUtil instance;

    public NetWorkAuxiliaryUtil(Context context) {
        this.mContext = context;
    }

    public static NetWorkAuxiliaryUtil getInstance(Context context) {
        if (instance == null) {
            instance = new NetWorkAuxiliaryUtil(context);
        }
        return instance;

    }

    public void sendSinchFireBase() {
        NetWorkUtil.isNetWorkAvailableOfDNS(NetWorkUtil.SINCH_DNS, new Comparable<Boolean>() {
            @Override
            public int compareTo(@NonNull Boolean states) {
                sinchDns = states + "";
                size += 1;
                handler.sendEmptyMessage(MSGWHAT);
                return 0;
            }
        });
        NetWorkUtil.isNetWorkAvailablePing(NetWorkUtil.SINCH_DNS, new Comparable<Boolean>() {
            @Override
            public int compareTo(@NonNull Boolean states) {
                sinchPing = states + "";
                size += 1;
                handler.sendEmptyMessage(MSGWHAT);
                return 0;
            }
        });
        NetWorkUtil.isNetWorkAvailableOfDNS(NetWorkUtil.MY_DNS, new Comparable<Boolean>() {
            @Override
            public int compareTo(@NonNull Boolean states) {
                myDns = states + "";
                size += 1;
                handler.sendEmptyMessage(MSGWHAT);
                return 0;
            }
        });
        NetWorkUtil.isNetWorkAvailablePing(NetWorkUtil.MY_DNS, new Comparable<Boolean>() {
            @Override
            public int compareTo(@NonNull Boolean states) {
                myPing = states + "";
                size += 1;
                handler.sendEmptyMessage(MSGWHAT);
                return 0;
            }
        });
        NetWorkUtil.isNetWorkAvailableOfGetGoogle(NetWorkUtil.GOOGLE_HTTP, new Comparable<Boolean>() {
            @Override
            public int compareTo(@NonNull Boolean states) {
                googleService = states + "";
                size += 1;
                handler.sendEmptyMessage(MSGWHAT);
                return 0;
            }
        });
        NetWorkUtil.isNetWorkAvailableOfGet(NetWorkUtil.MY_HTTP, new Comparable<Boolean>() {
            @Override
            public int compareTo(@NonNull Boolean states) {
                myService = states + "";
                size += 1;
                handler.sendEmptyMessage(MSGWHAT);
                return 0;
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSGWHAT:
                    if (size >= 6) {
                        Map<String, String> map = new HashMap<>();
                        map.put(NetWorkStates.SINCH_DNS, sinchDns);
                        map.put(NetWorkStates.SINCH_PING, sinchPing);
                        map.put(NetWorkStates.MY_DNS, myDns);
                        map.put(NetWorkStates.MY_PING, myPing);
                        map.put(NetWorkStates.MY_SERVICE, myService);
                        map.put(NetWorkStates.GOOGLE_SERVICE, googleService);
                        map.put(NetWorkStates.INTENET_STATES, IntenetUtil.isNetworkConnected(mContext) + "");
                        map.put(NetWorkStates.INTENET_TYPE, IntenetUtil.getNetworkState(mContext));
                        Firebase.getInstance(mContext).logEvent(NetWorkStates.NET_WORK_CONNECTION, map);
                        size = 0;
                    }
                    break;
            }

        }
    };
}
