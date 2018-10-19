package com.melonltd.naber.vo;

import com.google.common.base.MoreObjects;
import com.melonltd.naber.model.type.SwitchStatus;

import java.io.Serializable;

public class CategoryRelVo implements Serializable{
    private static final long serialVersionUID = -9022043460680866466L;

    public String category_uuid;
    public String restaurant_uuid;
    public String category_name;
    public String top;
    public SwitchStatus status;
    public String enable;
    public String create_date;

    @Override
    public String toString() {
        return MoreObjects
                .toStringHelper(this.getClass())
                .add("category_uuid",category_uuid)
                .add("restaurant_uuid",restaurant_uuid)
                .add("category_name",category_name)
                .add("top",top)
                .add("status",status)
                .toString();
    }
}
