//
//  RestaurantTemplate.swift
//  Naber
//
//  Created by melon on 2018/7/20.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation

class RestaurantTemplateResp : Codable {
    var status : String!
    var err_code : String!
    var err_msg : String!
    var data : [RestaurantTemplateVo]! = []
    
    public static func toJson(structs : RestaurantTemplateResp) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> RestaurantTemplateResp? {
        do {
            return try JSONDecoder().decode(RestaurantTemplateResp.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}

class RestaurantTemplateVo : Codable {
    var restaurant_uuid : String!
    var latitude : String!
    var longitude : String!
    var distance : Double!
    
    public static func toJson(structs : RestaurantTemplateVo) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> RestaurantTemplateVo? {
        do {
            return try JSONDecoder().decode(RestaurantTemplateVo.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }

}
