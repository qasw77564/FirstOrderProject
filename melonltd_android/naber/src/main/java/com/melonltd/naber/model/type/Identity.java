package com.melonltd.naber.model.type;

import com.google.common.collect.Lists;

import java.util.List;

public enum Identity {
    JUNIOR("小學生"),
    INTERMEDIATE("國中生"),
    SENIOR("高中生"),
    UNIVERSITY("大學/大專院校生"),
    NON_STUDENT("社會人士/其它"),
    SELLERS("SELLERS");

    public String name;

    Identity(String name) {
        this.name = name;
    }

    public static Identity of(String name) {
        for (Identity type : getEnumValues()) {
            if (type.name().equals(name.toUpperCase())) {
                return type;
            }
        }
        return null;
    }

    public static Identity ofName(String name) {
        for (Identity type : getEnumValues()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return null;
    }

    public static List<Identity> getUserValues(){
        return Lists.newArrayList(JUNIOR, INTERMEDIATE, SENIOR, UNIVERSITY, NON_STUDENT);
    }

    public static List<Identity> getEnumValues() {
        return Lists.newArrayList(Identity.values());
    }

}
