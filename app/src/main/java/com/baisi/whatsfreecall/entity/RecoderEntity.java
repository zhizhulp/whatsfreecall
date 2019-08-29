package com.baisi.whatsfreecall.entity;

/**
 * Created by MnyZhao on 2017/12/6.
 */

public class RecoderEntity {
    //姓名
    private String name;
    //号码
    private String phoneNumber;
    //类型 呼入 未接 呼出
    private int type;
    //时间
    private long date;
    /*头像地址*/
    private String photoUri;
    //国家名字
    private String counryName;
    //国家code
    private int countryCode;
    /*国旗*/
    private int countryFlag;

    public int getCountryFlag() {
        return countryFlag;
    }

    public void setCountryFlag(int countryFlag) {
        this.countryFlag = countryFlag;
    }

    public String getCounryName() {
        return counryName;
    }

    public void setCounryName(String counryName) {
        this.counryName = counryName;
    }

    public int getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(int countryCode) {
        this.countryCode = countryCode;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public RecoderEntity() {
    }

    public RecoderEntity(String name, String phoneNumber, int type, long date, String photoUri, String counryName, int countryCode,int countryFlag) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.date = date;
        this.photoUri = photoUri;
        this.counryName = counryName;
        this.countryCode = countryCode;
        this.countryFlag=countryFlag;
    }

    @Override
    public String toString() {
        return "name:"+getName()+"number:"+getPhoneNumber()+"type"+getType()+"Date:"+getDate()+"uri:"+getPhotoUri();
    }
}
