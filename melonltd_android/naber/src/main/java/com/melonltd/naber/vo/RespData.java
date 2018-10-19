package com.melonltd.naber.vo;

import com.google.common.base.MoreObjects;

public class RespData {
    public String status;
    public String err_msg;
    public String err_code;
    public Object data;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                .add("status",status)
                .add("err_msg",err_msg)
                .add("err_code",err_code)
                .add("data",data)
                .toString();
    }
}
