//
//  Food.swift
//  Naber
//
//  Created by melon on 2018/7/20.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation


class FoodResp : Codable {
    var status : String!
    var err_code : String!
    var err_msg : String!
    var data : FoodVo!
    
    public static func toJson(structs : FoodResp) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> FoodResp? {
        do {
            return try JSONDecoder().decode(FoodResp.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
    
}

