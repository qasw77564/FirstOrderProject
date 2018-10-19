package com.melonltd.naber.model.type;

import com.google.common.base.Strings;
import com.melonltd.naber.R;

public enum OrderStatus {
    LIVE("LIVE", "即時", R.color.naber_basis_bright_yellow),
    UNFINISH("UNFINISH", "未處理", R.color.naber_basis_blue),
    PROCESSING("PROCESSING", "製作中", R.color.naber_basis_orange),
    CAN_FETCH("CAN_FETCH", "可領取", R.color.naber_basis_bright_yellow),
    CANCEL("CANCEL", "取消", R.color.naber_basis_red),
    FAIL("FAIL", "跑單", R.color.naber_basis_red),
    FINISH("FINISH", "完成", R.color.naber_basis_bright_green);

    private String name;
    private String text;
    private int color;

    OrderStatus(String name, String text, int color){
        this.name = name;
        this.text = text;
        this.color = color;
    }


    public static OrderStatus of(String name) {
        if (Strings.isNullOrEmpty(name)) {
            return null;
        }
        for (OrderStatus type : OrderStatus.values()) {
            if (type.name.equals(name.toUpperCase())) {
                return type;
            }
        }
        return null;
    }

    public int getColor() {
        return this.color;
    }

    public String getText() {
        return this.text;
    }

}
