package com.baisi.whatsfreecall.entity.requestentity;

/**
 * Created by MnyZhao on 2018/1/12.
 * 通话界面请求费率信息
 */

public class PostCountry {
    public String country_code;
    public String national_number;

    public PostCountry(String country_code, String national_number) {
        this.country_code = country_code;
        this.national_number = national_number;
    }
}
