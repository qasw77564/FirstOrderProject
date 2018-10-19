//
//  ApiUrl.swift
//  Naber
//
//  Created by 王淳彦 on 2018/7/18.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation

class ApiUrl{   

    public static var DOMAIN: String = "http://192.168.1.108"
//    public static var DOMAIN: String = "http://192.168.31.14:8080/melonltd-ap"
//    public static var DOMAIN: String = "http://192.168.1.107:8080/melonltd-ap"
//    public static var DOMAIN: String = "https://ap.melonltd.com.tw"
    
    public static var LOGIN: String = DOMAIN + "/login"
    public static var LOGOUT: String = DOMAIN + "/logout"
    public static var GET_SMS_CODE: String = DOMAIN + "/sms/get/code"
    public static var SMS_VERIFY_CODE: String = DOMAIN + "/sms/verify/code"
    public static var USER_REGISTERED: String = DOMAIN + "/registered/user"
    public static var SELLER_REGISTERED: String = DOMAIN + "/registered/seller"
    
    // common
    public static var APP_INTRO_BULLETIN: String = DOMAIN + "/common/app/intro/bulletin"
    public static var STORE_CATEGORY_LIST: String = DOMAIN + "/common/store/category/list"
    public static var STORE_AREA_LIST: String = DOMAIN + "/common/store/area/list"
    public static var CHECK_APP_VERSION: String = DOMAIN + "/common/check/app/version"
    public static var ACT_LIST: String = DOMAIN + "/common/activities/list"
    public static var SUBJECTION_REGIONS: String = DOMAIN + "/common/subjection/region/list"
    public static var SCHOOL_DIVIDED: String = DOMAIN + "/common/school/divided/list"
    
    // user
    public static var ADVERTISEMENT: String = DOMAIN + "/naber/advertisement"
    public static var BULLETIN: String = DOMAIN + "/naber/bulletin"
    public static var RESTAURANT_TEMPLATE: String = DOMAIN + "/restaurant/location/template"
    public static var RESTAURANT_LIST: String = DOMAIN + "/restaurant/list"
    public static var RESTAURANT_DETAIL: String = DOMAIN + "/restaurant/detail"
    public static var RESTAURANT_FOOD_LIST: String = DOMAIN + "/restaurant/food/list"
    public static var RESTAURANT_FOOD_DETAIL: String = DOMAIN + "/restaurant/food/detail"
    public static var USER_ORDER_HISTORY: String = DOMAIN + "/user/order/history"
    public static var ORDER_SUBMIT: String = DOMAIN + "/user/order/subimt"
    public static var FIND_ACCOUNT_INFO: String = DOMAIN + "/account/find/info"
    public static var RESEAT_PSW: String = DOMAIN + "/account/update/password"
    public static var FORGET_PSW: String = DOMAIN + "/account/forget/password"
    
    public static var IMAGE_UPLOAD: String = DOMAIN + "/image/upload"
    public static var ACT_SUBMIT: String = DOMAIN + "/activities/submit"
    public static var SERIAL_SUBMIT: String = DOMAIN + "/serial/number/submit"
    public static var RES_EVENT_SUBMIT: String = DOMAIN + "/serial/res/event/submit"
    
    
    
    // seller
    public static var QUICK_SEARCH: String = DOMAIN + "/seller/quick/search"
    public static var CHANGE_ORDER: String = DOMAIN + "/seller/update/order"
    public static var BUSINESS_TIME: String = DOMAIN + "/seller/business/time"
    public static var CHANGE_BUSINESS_TIME: String = DOMAIN + "/seller/change/daily/business/time"
    public static var ORDER_LIST: String = DOMAIN + "/seller/ordar/list"
    public static var ORDER_LIVE: String = DOMAIN + "/seller/ordar/live"
    public static var SELLER_STAT: String = DOMAIN + "/seller/stat"
    public static var SELLER_STAT_LOG: String = DOMAIN + "/seller/stat/log"
    
    public static var SELLER_CATEGORY_LIST: String = DOMAIN + "/seller/category/list"
    public static var SELLER_ADD_CATEGORY: String = DOMAIN + "/seller/category/add"
    public static var SELLER_CHANGE_CATEGORY: String = DOMAIN + "/seller/category/update"
    public static var SELLER_DELETE_CATEGORY: String = DOMAIN + "/seller/category/delete"
    public static var SELLER_SORT_CATEGORY: String = DOMAIN + "/seller/category/sort"
    
    public static var SELLER_FOOD_LIST: String = DOMAIN + "/seller/food/list"
    public static var SELLER_ADD_FOOD: String = DOMAIN + "/seller/food/add"
    public static var SELLER_CHANGE_FOOD: String = DOMAIN + "/seller/food/update"
    public static var SELLER_DELETE_FOOD: String = DOMAIN + "/seller/food/delete"
    public static var SELLER_SORT_FOOD: String = DOMAIN + "/seller/food/sort"
    public static var SELLER_RESTAURANT_INFO: String = DOMAIN + "/seller/setting/find/restaurant"
    public static var SELLER_RESTAURANT_SETTING: String = DOMAIN + "/seller/setting"
    public static var SELLER_RESTAURANT_SETTING_BUSINESS: String = DOMAIN + "/seller/setting/business"
    
}
