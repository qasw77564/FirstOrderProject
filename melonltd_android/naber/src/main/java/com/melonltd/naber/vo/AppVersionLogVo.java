package com.melonltd.naber.vo;

import com.google.common.base.MoreObjects;

public class AppVersionLogVo {
    public String version;
    public String category;
    public String need_upgrade;
    public String create_date;


    @Override
    public String toString() {
        return MoreObjects
                .toStringHelper(this.getClass())
                .add("version",version)
                .add("category",category)
                .add("need_upgrade",need_upgrade)
                .add("create_date",create_date)
                .toString();
    }
}
