package com.baisi.whatsfreecall.entity;

/**
 * Created by MnyZhao on 2018/1/2.
 */

public class CallMsgEntity {
    /*电话id*/
    private String callId;
    /*国旗id*/
    private int countryFlag;
    /*国家名字*/
    private String countryName;
    /*国家代码*/
    private int countryCode;
    /*联系人名字*/
    private String contactName;
    /*联系人头像*/
    private String contactPhoto;
    /*拨打电话号码*/
    private String callNumber;

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public int getCountryFlag() {
        return countryFlag;
    }

    public void setCountryFlag(int countryFlag) {
        this.countryFlag = countryFlag;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(int countryCode) {
        this.countryCode = countryCode;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhoto() {
        return contactPhoto;
    }

    public void setContactPhoto(String contactPhoto) {
        this.contactPhoto = contactPhoto;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public CallMsgEntity(String callId, int countryFlag, String countryName, int countryCode, String contactName, String contactPhoto, String callNumber) {
        this.callId = callId;
        this.countryFlag = countryFlag;
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.contactName = contactName;
        this.contactPhoto = contactPhoto;
        this.callNumber = callNumber;
    }
}
