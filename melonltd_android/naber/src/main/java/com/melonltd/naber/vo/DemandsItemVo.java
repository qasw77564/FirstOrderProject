package com.melonltd.naber.vo;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class DemandsItemVo implements Serializable{
    private static final long serialVersionUID = 5542549548503001055L;
    public String name;
    public List<ItemVo> datas = Lists.<ItemVo>newArrayList();

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("name",name)
                .add("datas",datas)
                .toString();
    }
}
