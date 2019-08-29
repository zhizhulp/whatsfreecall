package com.baisi.whatsfreecall.entity.requestentity;

/**
 * Created by MnyZhao on 2018/1/9.
 */

public class SignEntity {
    public static final String NATIVE="native";
    public static final String OAUTH="oauth2";
    public String gt;
    /*native and oauth2*/
    public String type;
    /*Firebase用来统计appinstanceid*/
    public String app_instance_id;
    public SignEntity(String gt,String type,String app_instance_id) {
        this.gt = gt;
        this.type=type;
        this.app_instance_id=app_instance_id;
    }
}
