package com.melonltd.naber.vo;

import com.google.common.base.MoreObjects;

import java.io.Serializable;

public class AccountInfoVo implements Serializable{
    private static final long serialVersionUID = 7659391411455993337L;
    public String account;
    public String account_uuid;
    public String restaurant_uuid;
    public String password;
    public String name;
    public String email;
    public String phone;
    public String address;
    public String birth_day;
    public String identity;
    public String school_name;
    public String bonus;
    public String use_bonus;
    public String level;
    public String enable;
    public String is_login;
    public String login_date;
    public String photo;
    public String photo_type;
    public String gender;
    public String device_token;
    public String device_category;

    @Override
    public String toString() {
        return MoreObjects
                .toStringHelper(this.getClass())
                .add("account", account)
                .add("account_uuid", account_uuid)
                .add("restaurant_uuid", restaurant_uuid)
                .add("name", name)
                .add("password", password)
                .add("email", email)
                .add("phone", phone)
                .add("address", address)
                .add("birth_day", birth_day)
                .add("identity", identity)
                .add("school_name", school_name)
                .add("bonus", bonus)
                .add("level", level)
                .add("enable", enable)
                .add("is_login", is_login)
                .add("login_date", login_date)
                .add("photo", photo)
                .add("photo_type", photo_type)
                .add("gender",gender)
                .add("devlcie_token", device_token)
                .add("device_category", device_category)
                .toString();
    }
}
