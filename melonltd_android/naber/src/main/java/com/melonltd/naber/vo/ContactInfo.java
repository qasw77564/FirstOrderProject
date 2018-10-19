package com.melonltd.naber.vo;

import com.google.common.base.MoreObjects;

import java.io.Serializable;

public class ContactInfo implements Serializable {
    private static final long serialVersionUID = 1279921928557717157L;
    public String name;
    public String phone;
    public String email;
    public String code;
    public String city;
    public String area;
    public String address;

    @Override
    public String toString() {
        return MoreObjects
                .toStringHelper(this.getClass())
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("code", code)
                .add("city", city)
                .add("area", area)
                .add("address", address)
                .toString();
    }

}