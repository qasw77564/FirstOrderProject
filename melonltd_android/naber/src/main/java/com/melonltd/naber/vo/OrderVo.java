package com.melonltd.naber.vo;

import com.google.common.base.MoreObjects;

import java.io.Serializable;

public class OrderVo implements Serializable{
    private static final long serialVersionUID = 3927830460393882600L;
    public String order_uuid;
    public String account_uuid;
    public String user_message;
    public String create_date;
    public String update_date;
    public String order_price;
    public String order_bonus;
    public String use_bonus;
    public String fetch_date;
    public String order_data;
    public String status;

    public OrderDetail order_detail;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("order_uuid", order_uuid)
                .add("account_uuid", account_uuid)
                .add("user_message", user_message)
                .add("create_date", create_date)
                .add("update_date", update_date)
                .add("order_price", order_price)
                .add("order_bonus", order_bonus)
                .add("fetch_date", fetch_date)
                .add("order_data", order_data)
                .add("status", status)
                .toString();
    }
}
