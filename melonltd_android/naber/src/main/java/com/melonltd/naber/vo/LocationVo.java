package com.melonltd.naber.vo;

public class LocationVo {
    public double latitude;
    public double longitude;

    public static LocationVo of (double latitude, double longitude){
        LocationVo vo = new LocationVo();
        vo.latitude = latitude;
        vo.longitude = longitude;
        return  vo;
    }

    public static LocationVo of (String latitude, String longitude){
        LocationVo vo = new LocationVo();
        vo.latitude = Double.parseDouble(latitude);
        vo.longitude = Double.parseDouble(longitude);
        return  vo;
    }
}
