package com.melonltd.naber.vo;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class RestaurantInfoVo implements Serializable {
    private static final long serialVersionUID = 5060744626989216857L;

    public String restaurant_uuid;
    public String name;
    public String address;
    public String store_start;
    public String store_end;
    public String is_store_now_open;
    public List<String> not_business = Lists.newArrayList();
    public List<DateRangeVo> can_store_range;
    public String can_discount;
    public String restaurant_category;
    public String latitude;
    public String longitude;
    public String bulletin;
    public String photo;
    public String background_photo;
    public String top;
    public double distance;

    public boolean isShowOne = true;

    public String getRestaurant_uuid() {
        return restaurant_uuid;
    }

    public void setRestaurant_uuid(String restaurant_uuid) {
        this.restaurant_uuid = restaurant_uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStore_start() {
        return store_start;
    }

    public void setStore_start(String store_start) {
        this.store_start = store_start;
    }

    public String getStore_end() {
        return store_end;
    }

    public void setStore_end(String store_end) {
        this.store_end = store_end;
    }

    public String getIs_store_now_open() {
        return is_store_now_open;
    }

    public void setIs_store_now_open(String is_store_now_open) {
        this.is_store_now_open = is_store_now_open;
    }

    public List<String> getNot_business() {
        return not_business;
    }

    public void setNot_business(List<String> not_business) {
        this.not_business = not_business;
    }

    public List<DateRangeVo> getCan_store_range() {
        return can_store_range;
    }

    public void setCan_store_range(List<DateRangeVo> can_store_range) {
        this.can_store_range = can_store_range;
    }

    public String getRestaurant_category() {
        return restaurant_category;
    }

    public void setRestaurant_category(String restaurant_category) {
        this.restaurant_category = restaurant_category;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getBulletin() {
        return bulletin;
    }

    public void setBulletin(String bulletin) {
        this.bulletin = bulletin;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getBackground_photo() {
        return background_photo;
    }

    public void setBackground_photo(String background_photo) {
        this.background_photo = background_photo;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("restaurant_uuid", restaurant_uuid)
                .add("name", name)
                .add("address", address)
                .add("store_start", store_start)
                .add("store_end", store_end)
                .add("not_business", not_business)
                .add("can_store_range", can_store_range)
                .add("can_discount", can_discount)
                .add("restaurant_category", restaurant_category)
                .add("latitude", latitude)
                .add("longitude", longitude)
                .add("bulletin", bulletin)
                .add("photo", photo)
                .add("background_photo", background_photo)
                .add("top", top)
                .toString();
    }
}
