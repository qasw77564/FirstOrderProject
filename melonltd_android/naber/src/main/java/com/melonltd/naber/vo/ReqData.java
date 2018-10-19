package com.melonltd.naber.vo;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

import java.util.List;

public class ReqData {
    public String uuid = "";
    public List<String> uuids = Lists.<String>newArrayList();
    public String search_type = "";
    public String area = "";
    public int page = 0;
    public int top = 0;
    public String longitude = "0.0";
    public String latitude = "0.0";
    public String category = "";
    public String json_data = "";
    public String date = "";
    public String start_date = "";
    public String end_date = "";
    public String message = "";
    public String type = "";
    public String name = "";
    public String status = "";
    public String data = "";
    public boolean loadingMore = true;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("uuid", uuid)
                .add("uuids", uuids)
                .add("search_type", search_type)
                .add("area", area)
                .add("page", page)
                .add("top", top)
                .add("longitude", longitude)
                .add("latitude", latitude)
                .add("category", category)
                .add("json_data", json_data)
                .add("date", date)
                .add("message", message)
                .add("type", type)
                .add("name", name)
                .add("status", status)
                .add("data", data)
                .toString();
    }
}
