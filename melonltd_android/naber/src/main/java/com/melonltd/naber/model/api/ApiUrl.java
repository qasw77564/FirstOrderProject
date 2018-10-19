package com.melonltd.naber.model.api;

/**
 * Created by evan on 2018/1/5.
 */

public class ApiUrl {
//      public final static String DOMAIN = "http://192.168.31.14:8080/melonltd-ap";
    public final static String DOMAIN = "http://192.168.1.108";
//    public final static String DOMAIN = "http://192.168.1.107:8080/melonltd-ap";
//    public final static String DOMAIN = "https://192.168.2.100";

//    public final static String DOMAIN = "https://ap.melonltd.com.tw";
    public final static String LOGIN = DOMAIN + "/login";
    public final static String LOGOUT = DOMAIN + "/logout";
    public final static String GET_SMS_CODE = DOMAIN + "/sms/get/code";
    public final static String SMS_VERIFY_CODE = DOMAIN + "/sms/verify/code";
    public final static String USER_REGISTERED = DOMAIN + "/registered/user";
    public final static String SELLER_REGISTERED = DOMAIN + "/registered/seller";

    // common
    public final static String APP_INTRO_BULLETIN = DOMAIN + "/common/app/intro/bulletin";
    public final static String STORE_CATEGORY_LIST = DOMAIN + "/common/store/category/list";
    public final static String STORE_AREA_LIST = DOMAIN + "/common/store/area/list";
    public final static String CHECK_APP_VERSION = DOMAIN + "/common/check/app/version";
    public final static String SCHOOL_DIVIDED_LIST = DOMAIN + "/common/school/divided/list";
    //紅利兌換
    public final static String ACT_LIST = DOMAIN + "/common/activities/list";
    public final static String SUBJECTION_REGIONS = DOMAIN + "/common/subjection/region/list";
    public final static String ACT_SUBMIT = DOMAIN + "/activities/submit";
    public final static String SERIAL_SUBMIT = DOMAIN + "/serial/number/submit";
    public final static String SERIAL = DOMAIN + "/serial/res/event/submit";

    // user
    public final static String ADVERTISEMENT = DOMAIN + "/naber/advertisement";
    public final static String BULLETIN = DOMAIN + "/naber/bulletin";
    public final static String RESTAURANT_TEMPLATE = DOMAIN + "/restaurant/location/template";
    public final static String RESTAURANT_LIST = DOMAIN + "/restaurant/list";
    public final static String RESTAURANT_DETAIL = DOMAIN + "/restaurant/detail";
    public final static String RESTAURANT_FOOD_LIST = DOMAIN + "/restaurant/food/list";
    public final static String RESTAURANT_FOOD_DETAIL = DOMAIN + "/restaurant/food/detail";
    public final static String USER_ORDER_HISTORY = DOMAIN + "/user/order/history";
    public final static String ORDER_SUBMIT = DOMAIN + "/user/order/subimt";
    public final static String FIND_ACCOUNT_INFO = DOMAIN + "/account/find/info";
    public final static String RESEAT_PSW = DOMAIN + "/account/update/password";
    public final static String FORGET_PSW = DOMAIN + "/account/forget/password";


    public final static String IMAGE_UPLOAD = DOMAIN + "/image/upload";

    // seller
    public final static String SELLER_SORT_FOOD = DOMAIN + "/seller/food/sort";
    public final static String SELLER_SORT_CATEGORY = DOMAIN + "/seller/category/sort";

    public final static String QUICK_SEARCH = DOMAIN + "/seller/quick/search";
    public final static String CHANGE_ORDER = DOMAIN + "/seller/update/order";
    public final static String BUSINESS_TIME = DOMAIN + "/seller/business/time";
    public final static String CHANGE_BUSINESS_TIME = DOMAIN + "/seller/change/daily/business/time";
    public final static String ORDER_LIST = DOMAIN + "/seller/ordar/list";
    public final static String ORDER_LIVE = DOMAIN + "/seller/ordar/live";
    public final static String SELLER_STAT = DOMAIN + "/seller/stat";
    public final static String SELLER_STAT_LOG = DOMAIN + "/seller/stat/log";

    public final static String SELLER_CATEGORY_LIST = DOMAIN + "/seller/category/list";
    public final static String SELLER_ADD_CATEGORY = DOMAIN + "/seller/category/add";
    public final static String SELLER_CHANGE_CATEGORY = DOMAIN + "/seller/category/update";
    public final static String SELLER_DELETE_CATEGORY = DOMAIN + "/seller/category/delete";
    public final static String SELLER_FOOD_LIST = DOMAIN + "/seller/food/list";
    public final static String SELLER_ADD_FOOD = DOMAIN + "/seller/food/add";
    public final static String SELLER_CHANGE_FOOD = DOMAIN + "/seller/food/update";
    public final static String SELLER_DELETE_FOOD = DOMAIN + "/seller/food/delete";
    public final static String SELLER_RESTAURANT_INFO = DOMAIN + "/seller/setting/find/restaurant";
    public final static String SELLER_RESTAURANT_SETTING = DOMAIN + "/seller/setting";
    public final static String SELLER_RESTAURANT_SETTING_BUSINESS = DOMAIN + "/seller/setting/business";


//    public final static String test = "https://211.75.132.15:8443/api/eca/1/fen/device/list?group_id=11&query_type=4&apsystem=ECA&user_id=ecadmin&dev_categorys=SENSOR";
//    public final static String test = "https://ap.melonltd.com.tw/login";
//    public final static String test = "https://www.ap.melonltd.com.tw/login";
//    public final static String test ="http://ap.melonltd.com.tw/users";

}
