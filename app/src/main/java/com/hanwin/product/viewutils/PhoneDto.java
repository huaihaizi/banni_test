package com.hanwin.product.viewutils;

/**
 * Created by zhaopf on 2018/6/23 0023.
 */

public class PhoneDto {
    private String name;        //联系人姓名
    private String phoneNum;    //电话号码

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public PhoneDto() {
    }

    public PhoneDto(String name, String telPhone) {
        this.name = name;
        this.phoneNum = telPhone;
    }
}
