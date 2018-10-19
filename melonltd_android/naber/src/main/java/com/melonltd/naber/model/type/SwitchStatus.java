package com.melonltd.naber.model.type;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.List;

public enum SwitchStatus {
    OPEN("OPEN", true), CLOSE("CLOSE", false);

    private String name;
    private boolean status;

    SwitchStatus(String name, boolean status) {
        this.name = name;
        this.status = status;
    }

    public static SwitchStatus of(String name) {
        if (Strings.isNullOrEmpty(name)) {
            return null;
        }
        for (SwitchStatus type : getEnumValues()) {
            if (type.name().equals(name.toUpperCase())) {
                return type;
            }
        }
        return null;
    }

    public static SwitchStatus of(boolean status) {
        for (SwitchStatus type : getEnumValues()) {
            if (type.status == status) {
                return type;
            }
        }
        return null;
    }

    public boolean getStatus() {
        return this.status;
    }

    public String getName() {
        return this.name;
    }

    public static List<SwitchStatus> getEnumValues() {
        return Lists.newArrayList(OPEN, CLOSE);
    }
}
