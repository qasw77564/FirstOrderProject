package com.melonltd.naber.model.bean;

import android.location.Location;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.melonltd.naber.vo.CategoryRelVo;
import com.melonltd.naber.vo.DateRangeVo;
import com.melonltd.naber.vo.FoodVo;
import com.melonltd.naber.vo.OrderVo;
import com.melonltd.naber.vo.RestaurantInfoVo;
import com.melonltd.naber.vo.SellerStatVo;

import java.util.List;
import java.util.Map;

public class Model {

    public static List<String> OPT_ITEM_1 = Lists.newArrayList();
    public static List<List<String>> OPT_ITEM_2 = Lists.newArrayList();

    public static Map<String, Map<String, String>> BULLETIN_VOS = Maps.<String, Map<String, String>>newHashMap();
    public static List<String> BANNER_IMAGES = Lists.<String>newArrayList();
    public static Location LOCATION;
//    public static List<List<RestaurantTemplate>> RESTAURANT_TEMPLATE_PAGS = Lists.<List<RestaurantTemplate>>newArrayList();
//    public static List<RestaurantTemplate> RESTAURANT_TEMPLATE = Lists.<RestaurantTemplate>newArrayList();
//    public static List<RestaurantInfoVo> RESTAURANT_INFO_LIST = Lists.<RestaurantInfoVo>newArrayList();
//    public static List<CategoryRelVo> RESTAURANT_CATEGORY_REL_LIST = Lists.<CategoryRelVo>newArrayList();
    public static List<RestaurantInfoVo> RESTAURANT_INFO_FILTER_LIST = Lists.<RestaurantInfoVo>newArrayList();
    public static List<FoodVo> CATEGORY_FOOD_REL_LIST = Lists.<FoodVo>newArrayList();
//    public static List<OrderVo> USER_ORDER_HISTORY_LIST = Lists.newArrayList();

    // key restaurant_uuid
//    public static List<OrderDetail> USER_CACHE_SHOPPING_CART = Lists.<OrderDetail>newArrayList();




    // seller
    public static List<OrderVo> SELLER_QUICK_SEARCH_ORDERS = Lists.<OrderVo>newArrayList();

    public static List<DateRangeVo> SELLER_BUSINESS_TIME_RANGE = Lists.<DateRangeVo>newArrayList();
    public static List<OrderVo> SELLER_TMP_ORDERS_LIST = Lists.<OrderVo>newArrayList();
    public static List<OrderVo> SELLER_UNFINISH_ORDERS_LIST = Lists.<OrderVo>newArrayList();
    public static List<OrderVo> SELLER_PROCESSING_ORDERS_LIST = Lists.<OrderVo>newArrayList();
    public static List<OrderVo> SELLER_CAN_FETCH_ORDERS_LIST = Lists.<OrderVo>newArrayList();
    public static SellerStatVo SELLER_STAT = new SellerStatVo();

    public static List<OrderVo> SELLER_STAT_LOGS = Lists.<OrderVo>newArrayList();
    public static List<CategoryRelVo> SELLER_CATEGORY_LIST = Lists.<CategoryRelVo>newArrayList();
    public static List<FoodVo> SELLER_FOOD_LIST = Lists.<FoodVo>newArrayList();



}
