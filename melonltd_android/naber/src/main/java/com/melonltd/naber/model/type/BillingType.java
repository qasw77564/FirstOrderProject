package com.melonltd.naber.model.type;


import com.google.common.collect.Lists;

import java.util.List;

/**
 *
 * ORIGINAL("ORIGINAL") 原價 -- 預設
 * DISCOUNT("DISCOUNT") 折價
 * COUPON("COUPON") 兌換
 */
public enum BillingType {
    ORIGINAL("ORIGINAL"),
    DISCOUNT("DISCOUNT"),
    COUPON("COUPON");

    private String name;

    BillingType(String name){
        this.name = name;
    }

    public static BillingType of(String name) {
        for (BillingType type : getEnumValues()) {
            if (type.name.equals(name.toUpperCase())) {
                return type;
            }
        }
        return BillingType.ORIGINAL;
    }

    public static List<BillingType> getEnumValues() {
        return Lists.newArrayList(ORIGINAL, DISCOUNT, COUPON);
    }
}
