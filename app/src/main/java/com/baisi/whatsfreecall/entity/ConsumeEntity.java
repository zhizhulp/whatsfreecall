package com.baisi.whatsfreecall.entity;

/**
 * Created by MnyZhao on 2017/12/8.
 */

public class ConsumeEntity {
    String pay_id;
    String package_name;
    String product_id;
    String purchase_token;
    String original_json;
    String sign_ature;
    /*当gt传递进来说明未登陆不传 或者gt为null 说明已经登陆*/
    String gt;

    public ConsumeEntity(String pay_id, String package_name, String product_id, String purchase_token, String original_json, String sign_ature) {
        this.pay_id = pay_id;
        this.package_name = package_name;
        this.product_id = product_id;
        this.purchase_token = purchase_token;
        this.original_json = original_json;
        this.sign_ature = sign_ature;
    }

    public ConsumeEntity(String pay_id, String package_name, String product_id, String purchase_token, String original_json, String sign_ature, String gt) {
        this.pay_id = pay_id;
        this.package_name = package_name;
        this.product_id = product_id;
        this.purchase_token = purchase_token;
        this.original_json = original_json;
        this.sign_ature = sign_ature;
        this.gt=gt;
    }
}
