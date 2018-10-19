package com.melonltd.naber.vo;

import java.lang.reflect.Array;
import java.util.List;

public class SubjectionRegionVo {
    public String city;
    public List<ArearVo> areas ;


    @Override
    public String toString() {
        return city;
    }

    public class ArearVo {
        public  String area;
        public  String postal_code;

        @Override
        public String toString() {
            return area;
        }
    }
}



