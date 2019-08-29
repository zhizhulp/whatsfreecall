package com.baisi.whatsfreecall.task_handler;

import android.os.Handler;

import com.baisi.whatsfreecall.entity.EarnListEntity;

import java.util.List;
import java.util.TimerTask;

public class TimeReduceTask extends TimerTask {
    private List<EarnListEntity.EarnEntity> data;
    public static final int WHAT_REDUCE_TIME = 1;
    public static final int INTERVAL_TIME = 1000;
    private Handler handler;

    public TimeReduceTask(List<EarnListEntity.EarnEntity> data, Handler handler) {
        this.data = data;
        this.handler = handler;
    }

    @Override
    public void run() {
        if (!allTimeOver()) {
            perReduceTime();
            handler.sendEmptyMessageDelayed(WHAT_REDUCE_TIME, INTERVAL_TIME);
        }
    }

    private boolean allTimeOver() {
        if (data == null || data.size() == 0) return true;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).leftTime > 0) return false;
        }
        return true;
    }

    private void perReduceTime() {
        for (int i = 0; i < data.size(); i++) {
            EarnListEntity.EarnEntity earnListEntity = data.get(i);
            long leftTime = earnListEntity.leftTime*1000;
            if (leftTime >= INTERVAL_TIME) (earnListEntity.leftTime) -= INTERVAL_TIME/1000;
        }
    }
}
