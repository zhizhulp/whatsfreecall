package com.baisi.whatsfreecall.entity;

/**
 * Created by MnyZhao on 2018/1/3.
 */

public class NumberEntity {
    private String numberType;
    private String number;

    public NumberEntity(String numberType, String number) {
        this.numberType = numberType;
        this.number = number;
    }

    public String getNumberType() {
        return numberType;
    }

    public void setNumberType(String numberType) {
        this.numberType = numberType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return getNumberType()+">>>>"+getNumber();
    }
}
