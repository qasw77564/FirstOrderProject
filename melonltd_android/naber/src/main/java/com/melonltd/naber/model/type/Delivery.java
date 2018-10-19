package com.melonltd.naber.model.type;


import com.google.common.collect.Lists;

import java.util.List;

/**
 * 餐館訂單可接受類型
 * 外送 SEND
 * 內用 IN
 * 外帶 OUT -- 預設
 */
public enum Delivery {
    SEND("SEND", "外送"),
    IN("IN", "內用"),
    OUT("OUT", "自取");

    private String name;
    private String value;

    Delivery(String name, String value){
        this.name = name;
        this.value = value;
    }

    public static Delivery ofName(String name) {
        for (Delivery type : getEnumValues()) {
            if (type.name.equals(name.toUpperCase())) {
                return type;
            }
        }
        return Delivery.OUT;
    }

    public static Delivery ofValue(String value) {
        for (Delivery type : getEnumValues()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }


    public static List<Delivery> getEnumValues() {
        return Lists.newArrayList(SEND, IN, OUT);
    }
}
