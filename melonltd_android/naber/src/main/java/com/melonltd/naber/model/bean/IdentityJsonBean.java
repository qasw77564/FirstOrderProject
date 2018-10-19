package com.melonltd.naber.model.bean;

import com.contrarywind.interfaces.IPickerViewData;

import java.util.List;

public class IdentityJsonBean implements IPickerViewData {

    private String name;
    private List<String> datas;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDatas() {
        return datas;
    }

    public void setDatas(List<String> datas) {
        this.datas = datas;
    }

    @Override
    public String getPickerViewText() {
        return this.name;
    }

}
