package com.baisi.whatsfreecall.task_handler;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baisi.whatsfreecall.ui.adapter.EarnMoneyAdapter;

public class TimeReduceHandler extends Handler {
    private EarnMoneyAdapter adapter;
    public TimeReduceHandler(EarnMoneyAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == TimeReduceTask.WHAT_REDUCE_TIME && null != adapter) {
            adapter.notifyDataSetChanged();
        }
    }
}
