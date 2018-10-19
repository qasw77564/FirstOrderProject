package com.melonltd.naber.vo;

import com.google.common.base.MoreObjects;

public class RestaurantTemplate {
    public String restaurant_uuid;
    public double  latitude ;
    public double  longitude;
    public double  distance;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("restaurant_uuid", restaurant_uuid)
                .add("longitude", longitude)
                .add("latitude", latitude)
                .add("distance", distance)
                .toString();
    }
}
