//
//  Model.swift
//  Naber
//
//  Created by 王淳彦 on 2018/7/25.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation
import Firebase

class Model {
    
    public static var CURRENT_FIRUSER: User! = Optional.none
    public static var ADVERTISEMENTS : [AdvertisementVo] = []
    // AD
    public static var AD_RESTAURANT_LIST : [RestaurantInfoVo] = []
    // HOME
    public static var TOP_RESTAURANT_LIST : [RestaurantInfoVo] = []
    // TMPE
    public static var TMPE_RESTAURANT_LIST : [RestaurantInfoVo] = []
    public static var AREA_RESTAURANT_LIST : [RestaurantInfoVo] = []
//    public static var TOP_RESTAURANT_LIST : [RestaurantInfoVo] = []
//    public static var TOP_RESTAURANT_LIST : [RestaurantInfoVo] = []
    public static var ALL_BULLETINS : [String: String] = [:]
    public static var NABER_BULLETINS : [String] = []
    
    public static var USER_CACHE_SHOPPING_CART : [OrderDetail] = []
    public static var STORE_DATE_RANGES: [DateRangeVo] = []
    
//    var shoppingCartDatas: [OrderDetail] = []
    
//    public static var HONE_ADIMAGES : [UIImageView] = []
    
    public static var SELLER_ORDERS_TIME: String = ""
    
}
