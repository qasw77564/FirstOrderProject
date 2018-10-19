//
//  RestaurantInfo.swift
//  Naber
//
//  Created by melon on 2018/7/23.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation


class RestaurantListResp : Codable {
    var status : String!
    var err_code : String!
    var err_msg : String!
    var data : [RestaurantInfoVo]! = []
    
    public static func toJson(structs : RestaurantListResp) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> RestaurantListResp? {
        do {
            return try JSONDecoder().decode(RestaurantListResp.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
    
}

