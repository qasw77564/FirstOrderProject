package com.melonltd.naber.vo;

import com.google.common.base.MoreObjects;

import java.util.List;

public class SchoolsVo {
    public String area;
    public List<String> schools;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public List<String> getSchools() {
        return schools;
    }

    public void setSchools(List<String> schools) {
        this.schools = schools;
    }

    @Override
    public String toString() {
        return area;
    }
}
