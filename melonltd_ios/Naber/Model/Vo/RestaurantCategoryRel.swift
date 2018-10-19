//
//  RestaurantCategoryRel.swift
//  Naber
//
//  Created by melon on 2018/7/20.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation


class CategoryRelListResp : Codable {
    var status : String!
    var err_code : String!
    var err_msg : String!
    var data : [CategoryRelVo]! = []
    
    public static func toJson(structs : CategoryRelListResp) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> CategoryRelListResp? {
        do {
            return try JSONDecoder().decode(CategoryRelListResp.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}

class CategoryRelResp : Codable {
    var status: String!
    var err_code: String!
    var err_msg: String!
    var data: CategoryRelVo!
    
    public static func toJson(structs : CategoryRelResp) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> CategoryRelResp? {
        do {
            return try JSONDecoder().decode(CategoryRelResp.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}



class CategoryRelVo : Codable {
    var category_uuid: String!
    var restaurant_uuid: String!
    var category_name: String!
    var top: String!
    var status: String!
    
    
    public static func toJsonArray(structs : [CategoryRelVo]) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    
    public static func toJson(structs : CategoryRelVo) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> CategoryRelVo? {
        do {
            return try JSONDecoder().decode(CategoryRelVo.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}
