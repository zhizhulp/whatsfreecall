package com.baisi.whatsfreecall.utils.budleutils;

import android.os.Bundle;

import com.baisi.whatsfreecall.entity.CallMsgEntity;
import com.baisi.whatsfreecall.entity.ContactsEntity;
import com.baisi.whatsfreecall.utils.utilpay.Purchase;

/**
 * Created by MnyZhao on 2017/12/9.
 */

public class BundleUtils {
    private static BundleUtils bundleUtils;

    public static BundleUtils getBundleUtils() {
        if (bundleUtils == null) {
            bundleUtils = new BundleUtils();
        }
        return bundleUtils;
    }

    ;

    public BundleUtils() {

    }

    /*是否存在库存*/
    public static final String INENTORY_EXISTS = "INENTORY_EXISTS";
    /**
     * 进入页面是谁
     * 1 闪屏页面判断库存进  {@link #ENTER_SPLASH}.
     * 2 购买页面进入        {@link #ENTER_CHARGE}
     * 3 打电话拨号页面进     {@link #ENTER_CALL}
     */
    public static final String ENTER_THE_PAGE = "ENTER_THE_PAGE";

    /*有库存充值界面Dialog进入*/
    public static final int ENTER_SPLASH = 1;
    /*充值界面进入*/
    public static final int ENTER_CHARGE = 2;
    /*充值界面VIDEO视频按钮*/
    public static final int ENTER_CHARGE_VIDEO=4;
    /*通讯库进入*/
    public static final int ENTER_CALL = 3;

    public static final String SKU_ID = "SKUID";
    public static final String INVENTY_PURCHASE="INVENTY_PURCHASE";

    /*错误信息*/
    public static final String ERROR_CONFIG = "ERROR_CONFIG";

    /**
     * @param inventory 是否存在库存  {@link #INENTORY_EXISTS}
     * @param enterPage 标记进入页面 {@link #ENTER_THE_PAGE}
     * @param skuid 购买时商品id {@link #SKU_ID}
     * @param purchase 查询到有库存时 库存对象
     * @return
     */
    public Bundle getLoginBundle(boolean inventory, int enterPage, String skuid, Purchase purchase) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(BundleUtils.INENTORY_EXISTS, inventory);
        bundle.putInt(BundleUtils.ENTER_THE_PAGE, enterPage);
        bundle.putString(SKU_ID, skuid);
        bundle.putParcelable(INVENTY_PURCHASE,purchase);
        return bundle;
    }
    /**
     * 错误信息bundle
     *
     * @param error 错误信息
     * @return
     */
    public Bundle getErrorBundle(String error) {
        Bundle bundle = new Bundle();
        bundle.putString(ERROR_CONFIG, error);
        return bundle;
    }
    /**
     * 错误信息bundle
     *
     * @param error 错误信息
     * @param flag 标记进入页面
     * @return
     */
    public static final String FLAG="LOGIN";
    public static final String FLAGACTIVITY="FLAGACTIVITY";
    public Bundle getErrorBundle(String error,String flag) {
        Bundle bundle = new Bundle();
        bundle.putString(ERROR_CONFIG, error);
        bundle.putString(FLAG,flag);
        return bundle;
    }

    /*联系人名字 联系人头像*/
    public static final String CONTACT_NAME = "CONTACT_NAME";
    public static final String CONTACT_PHOTO = "CONTACT_PHOTO";
    /*电话状态*/
    public static final String CALL_STATES = "CALL_STATES";
    public static final String COUNTRY_FLAG = "COUNTRY_FLAG";
    public static final String COUNTRY_NAME = "COUNTRY_NAME";
    public static final String COUNTRY_CODE = "COUNTRY_CODE";
    public static final String CALL_ERROR = "CALL_ERROR";
    public static final String CALL_ID = "CALL_ID";
    public static final String CALL_TIME = "CALLTIME";
    public static final String EXPENSE = "EXPENSE";
    public static final String BALANCE = "BALANCE";
    public static final String CALL_NUMBER = "CALL_NUMBER";
    /*挂断*/
    public static final String HUNG_UP = "HUNG_UP";
    /*拒接*/
    public static final String NO_ANSWER = "NO_ANSWER";
    /*错误多为网络*/
    public static final String FAILURE = "FAILURE";
    /*拒绝*/
    public static final String DENIED = "DENIED";
    /*拥堵*/
    public static final String BUSY = "BUSY";
    /*接通*/
    public static final String ANSWER = "ANSWER";

    /**
     * 重播
     *
     * @param status
     * @param callId
     * @param countryFlag
     * @param countryName
     * @param number
     * @return
     */
    public Bundle getCallRedialStatusMsg(String status, String callId, int countryFlag,
                                         String countryName, String number, String error,
                                         String contactName, String contactPhoto, int countryCode) {
        Bundle bundle = new Bundle();
        bundle.putString(CALL_STATES, status);
        bundle.putString(CALL_ID, callId);
        bundle.putInt(COUNTRY_FLAG, countryFlag);
        bundle.putString(COUNTRY_NAME, countryName);
        bundle.putString(CALL_NUMBER, number);
        bundle.putString(CALL_ERROR, error);
        bundle.putString(CONTACT_NAME, contactName);
        bundle.putString(CONTACT_PHOTO, contactPhoto);
        bundle.putInt(COUNTRY_CODE, countryCode);
        return bundle;
    }

    /**
     * 正常挂断
     *
     * @param number
     * @param expencse
     * @param balance
     * @return
     */
    public Bundle getCallEndMsg(String callId, String msg, String expencse, String balance,
                                int time, String number, String countryName,
                                String contactName, String contactPhoto, int countryCode,int countryFlag) {
        Bundle bundle = new Bundle();
        bundle.putString(CALL_ID, callId);
        bundle.putString(CALL_ERROR, msg);
        bundle.putString(EXPENSE, expencse);
        bundle.putString(BALANCE, balance);
        bundle.putString(CALL_NUMBER, number);
        bundle.putInt(CALL_TIME, time);
        bundle.putString(COUNTRY_NAME, countryName);
        bundle.putString(CONTACT_NAME, contactName);
        bundle.putString(CONTACT_PHOTO, contactPhoto);
        bundle.putInt(COUNTRY_CODE, countryCode);
        bundle.putInt(COUNTRY_FLAG, countryFlag);
        return bundle;
    }

    public static final String CONTACT_ENTITY = "CONTACT_ENTITY";

    /**
     * 获取从联系人界面传递进来的bundle
     *
     * @param contactsEntity
     * @return
     */
    public Bundle getContactBundle(ContactsEntity contactsEntity) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(CONTACT_ENTITY, contactsEntity);
        return bundle;
    }

    /**
     * 获取从拨号盘进入通话界面（重拨进入通话界面）或者从通话记录进入通话界面的bundle
     *
     * @param callMsgEntity
     * @return
     */
    public Bundle getCallMsgBundle(CallMsgEntity callMsgEntity) {
        Bundle bundle = new Bundle();
        bundle.putString(CALL_ID, callMsgEntity.getCallId());
        bundle.putString(CALL_NUMBER, callMsgEntity.getCallNumber());
        bundle.putString(CONTACT_NAME, callMsgEntity.getContactName());
        bundle.putString(CONTACT_PHOTO, callMsgEntity.getContactPhoto());
        bundle.putString(COUNTRY_NAME, callMsgEntity.getCountryName());
        bundle.putInt(COUNTRY_FLAG, callMsgEntity.getCountryFlag());
        bundle.putInt(COUNTRY_CODE, callMsgEntity.getCountryCode());
        return bundle;
    }
}
