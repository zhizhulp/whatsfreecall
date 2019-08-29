package com.baisi.whatsfreecall.configs;

import com.baisi.whatsfreecall.BuildConfig;

/**
 * Created by MnyZhao on 2017/12/7.
 */

public class UrlConfig {
    /*服务器测试根地址*/
    /*https://z-storebuff-com-btfzwbs8pakd.runscope.net/voip/v1/*/
    public static final String BASE_URL = BuildConfig.HOST_URL;
    /*登陆*/
    public static final String LOGIN = "auth/login";
    /*验证token*/
    public static final String VERFY_TOKEN = "auth/verify";
    /*获取用户信息*/
    public static final String GET_USER_INFO = "user/info";
    /*充值*/
    public static final String PAY_PURCHASE = "pay/purchase";
    /*观看视频广告后充值*/
    public static final String WATCH_VIDEO_ADS_PAY = "mission/confirm";
    /*获取orderid唯一*/
    public static final String DEVELOPED_PAY_LOAD = "pay/prepay";
    /*通话界面显示当前通话数据*/
    public static final String CALL_INFO = "call/info";
    /*签到页面get post 取不同的数据*/
    public static final String CHECK_IN = "mission/sign";
    /*通话结束界面显示花费价格 参数CallID*/
    public static final String CALL_LOG = "call/log";
    public static final String FEEDBACK_URL = "https://docs.google.com/forms/d/e/1FAIpQLScnvp32OW-Vki-tN4b-tInM9e4GuLkEj-AiBbFBI_rsUtiCRg/viewform";
    public static final String EMAIL = "voip@keplers.art";
    public static final String RATE_TABLE = "page/rate";
    public static final String RATE_PATH = BASE_URL + RATE_TABLE;
    /*任务列表*/
    public static final String MISSION_LIST = "mission/mission_list";
    public static final String MISSION_STARS ="mission/stars";
    public static final String MISSION_VIDEO = "mission/video2";
    public static final String MISSION_INVITE = "mission/invite";
}
