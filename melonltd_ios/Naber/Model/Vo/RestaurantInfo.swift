//
//  RestaurantInfo.swift
//  Naber
//
//  Created by melon on 2018/7/23.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation


class RestaurantInfoResp : Codable {
    var status : String!
    var err_code : String!
    var err_msg : String!
    var data : RestaurantInfoVo!
    
    public static func toJson(structs : RestaurantInfoResp) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> RestaurantInfoResp? {
        do {
            return try JSONDecoder().decode(RestaurantInfoResp.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
    
}
class RestaurantInfoVo : Codable {
    var restaurant_uuid: String!
    var name: String!
    var address: String!
    var store_start: String!
    var store_end: String!
    var is_store_now_open: String!
    var not_business: [String]! = []
    var delivery_types: [String]! = []
    var can_store_range: [DateRangeVo]! = []
    var can_discount: String!
    var restaurant_category: String!
    var latitude: String!
    var longitude: String!
    var bulletin: String!
    var photo: String!
    var background_photo: String!
    var top: String!
    var distance : Double!
    var status: String!
    var date: String!
    
    var isShowOne: Bool! = true
    
    public static func toJson(structs : RestaurantInfoVo) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> RestaurantInfoVo? {
        do {
            return try JSONDecoder().decode(RestaurantInfoVo.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}
