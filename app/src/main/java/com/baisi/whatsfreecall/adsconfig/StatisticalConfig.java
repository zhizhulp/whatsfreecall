package com.baisi.whatsfreecall.adsconfig;

/**
 * Created by MnyZhao on 2018/1/12.
 * 统计信息配置
 */

public class StatisticalConfig {
    /*ads status*/
    public static final String OK = "OK";//准备好
    public static final String NO = "NO";//未准备好
    public static final String SHOW = "SHOW";//显示
    /*广告位置信息*/
    /*闪屏界面native*/
    public static final String SLPHAADS = "SLPHAADS_ADS";
    /*首页native*/
    public static final String HOMEADS = "HOMEADS_ADS";
    /*拨号界面native*/
    public static final String DIALADS = "DIALADS_ADS";
    /*通话结束页面navtive*/
    public static final String ENDADS = "ENDADS_ADS";
    /*进入全屏*/
    public static final String ENTERFULL = "ENTERFULL_ADS";
    /*视频广告*/
    public static final String VIDEO_ADS_LOAD = "VIDEO_ADS_LOAD";
    public static final String VIDEO_ADS = "VIDEO_ADS";
    /*视频广告结束全屏*/
    public static final String VIDEO_OVER_FULL_ADS = "VIDEO_OVER_FULL_ADS";

    /*统计配置,信息,以及响应代码*/
    //响应代码
    public static final String RESULT_CODE_CLICK = "CLICK";
    public static final String GO_IN = "GO_IN";
    public static final String ACTIVITY_SHOW = "ACTIVITY_SHOW";
    //拨号界面点击国家
    public static final String DIAL_SELECT_COUNTRY = "DIAL_SELECT_COUNTRY";
    //通讯录进拨号界面检测手机号
    public static final String DIAL_CONTACT_GO_CHECK_NUMBER = "DIAL_CONTACT_GO_CHECK_NUMBER";
    //拨号盘输入手机号检测
    public static final String DIAL_CHECK_PHONE_NUMBER_CODE = "DIAL_CHECK_PHONE_NUMBER_CODE";
    //点击call 按钮
    public static final String DIAL_CALL_CLICK = "DIAL_CALL_CLICK";
    //侧边栏进界面
    //费率界面
    public static final String NAV_GO_RATE = "NAV_GO_RATE";
    public static final String NAV_GO_BALANCE = "NAV_GO_BALANCE";
    public static final String NAV_GO_SHARE = "NAV_GO_SHARE";
    public static final String NAV_GO_FEEDBACK = "NAV_GO_FEEDBACK";
    //首页
    //右上角充值
    public static final String HOME_CHARGE = "HOME_CHARGE";
    //进入登陆页面 以及点击登陆事件  初始  auth
    public static final String LOGIN_SHOW = "LOGIN_SHOW";
    public static final String LOGIN_NATIVE = "LOGIN_NATIVE";
    public static final String LOGIN_AUTHO = "LOGIN_AUTHO";
    //侧边栏进入
    public static final String GO_LOGIN_NAV = "GO_LOGIN_NAV";
    //拨号按钮进入
    public static final String GO_LOGIN_DIAL = "GO_LOGIN_DIAL";
    //联系人
    public static final String GO_LOGIN_CONTACT = "GO_LOGIN_CONTACT";
    //联系人界面点击联系人
    public static final String CONTACT_CLICK = "CONTACT_CLICK";
    //通话记录
    public static final String GO_LOGIN_RECODER = "GO_LOGIN_RECODER";
    //通话记录点击
    public static final String RECODER_CLICK = "RECODER_CLICK";
    //视频广告未登陆
    public static final String GO_LOGIN_VIDEO = "GO_LOGIN_VIDEO";
    //购买页面
    public static final String CHARGE_ACTIVITY = "CHARGE_ACTIVITY";
    //SKU
    public static final String SKU_ID = "SKU_ID";
    //错误页面
    public static final String ERROR_MSG = "ERROR_MSG";
    //通话链接开始之前准备时间
    public static final String READY_TIME = "READY_TIME";
    /*投放需要*/
    public static final String BILLING_PAY_OK = "内支付完成";


    /*获取通话信息*/
    public static final String LOAD_CALLEND_INFO = "LOAD_CALLEND_INFO";
    public static final String LOAD_CALLEND_INFO_ERROR = "LOAD_CALLEND_INFO_ERROR";
    public static final String NULLDATA = "NULLDATA";
    /*sinch*/
    public static final String SINCH_START_ERROR = "SINCH_START_ERROR";
    public static final String CLICK_CALL_SINCH_STATES = "CLICK_CALL_SINCH_STATES";
    /*通话状态*/
    //连通
    public static final String SINCH_CALL_CONNECTION = "SINCH_CALL_CONNECTION";
    //费率接口
    public static final String RATE = "RATE";
    public static final String GOOGLE_PAY_ERROR = "GOOGLE_PAY_ERROR";
    /*充值界面查询商品适配出现错误*/
    public static final String SKU_ADAPTER = "SKU_ADAPTER";
    /*充值页面广告视频充值返回空值*/
    public static final String CHARGE_RESPONSE_VIDEO_BACK = "CHARGE_RESPONSE_VIDEO_BACK";
    /*充值页面请求网络返回的responsecode*/
    public static final String CHARGE_RESPONSE_CODE = "CHARGE_RESPONSE_CODE";
    public static final String LOGIN_RESPONSE_CODE = "LOGIN_RESPONSE_CODE";
    public static final String CONSUME_RESPONSE_CODE = "CONSUME_RESPONSE_CODE";
    public static final String CONSUME_RESPONSE_STATES = "CONSUME_RESPONSE_STATES";
    public static final String CONSUME_RESPONSE_MSG = "CONSUME_RESPONSE_MSG";
    public static final String RRESPONSE_NULL = "RRESPONSE_NULL";
    public static final String CALL_SCREEN_GET_MSG_RESPOSE = "CALL_SCREEN_GET_MSG_RESPOSE";
    public static final String CALL_SCREEN_GET_MSG_RESPOSE_CODE = "CALL_SCREEN_GET_MSG_RESPOSE_CODE";
    public static final String CALL_SCREEN_GET_MSG_RESPOSE_MSG = "CALL_SCREEN_GET_MSG_RESPOSE_MSG";

    /*视频广告开始播放到播放结束的时间*/
    public static final String VIDEO_START_PLAY_END = "VIDEO_START_PLAY_END";
    /*视频广告播放过程中点击取消*/
    public static final String VIDEO_PLAY_CANCEL = "VIDEO_PLAY_CANCEL";
    public static final String CANCEL = "CANCEL";
    /*新增 电话结束状态Error*/
    public static final String CALL_END_ERROR = "CALL_END_ERROR";
    public static final String CALL_END_CALL_ID="CALL_END_CALL_ID";
    public static final String CALL_END_CALL_NUMBER="CALL_END_CALL_NUMBER";
    public static final String CALL_END_ERROR_STATES="CALL_END_ERROR_STATES";

}
