package com.baisi.whatsfreecall.entity.requestentity;

/**
 * Created by MnyZhao on 2018/1/12.
 * 视频观看结束请求充值
 */

public class AdsCharge {
    public String type;
    public String is_click;

    public AdsCharge(String type, String is_click) {
        this.type = type;
        this.is_click = is_click;
    }

    public AdsCharge(String type) {
        this.type = type;
    }
}
