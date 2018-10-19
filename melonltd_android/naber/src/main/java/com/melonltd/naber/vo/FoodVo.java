package com.melonltd.naber.vo;

import com.google.common.base.MoreObjects;
import com.melonltd.naber.model.type.SwitchStatus;

import java.io.Serializable;

public class FoodVo implements Serializable{
    private static final long serialVersionUID = 3254163581453552772L;

    public String food_uuid;
    public String category_uuid;
    public String food_name;
    public String default_price;
    public String photo;
    public String photo_type;
    public FoodItemVo food_data;
    public SwitchStatus status;

    public String top;
    public String enable;
    public String create_date;

    @Override
    public String toString() {
        return MoreObjects
                .toStringHelper(this.getClass())
                .add("food_uuid",food_uuid)
                .add("category_uuid",category_uuid)
                .add("food_name",food_name)
                .add("default_price",default_price)
                .add("photo",photo)
                .add("photo_type",photo_type)
                .add("food_data",food_data)
                .add("top",top)
                .toString();
    }
}
