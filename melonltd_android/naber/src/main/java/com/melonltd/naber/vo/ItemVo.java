package com.melonltd.naber.vo;

import com.google.common.base.MoreObjects;

import java.io.Serializable;

public class ItemVo implements Serializable{
    private static final long serialVersionUID = 7186055429619991925L;
    public String name;
    public String price;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("name",name)
                .add("price",price)
                .toString();
    }
}
