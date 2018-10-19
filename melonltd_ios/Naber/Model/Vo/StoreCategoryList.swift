//
//  StoreCategoryList.swift
//  Naber
//
//  Created by 王淳彦 on 2018/8/21.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation


class StoreCategoryListResp: Codable{
    var status : String!
    var err_code : String!
    var err_msg : String!
    var data : [String]! = []
    
    public static func toJson(structs : StoreCategoryListResp) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> StoreCategoryListResp? {
        do {
            return try JSONDecoder().decode(StoreCategoryListResp.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}
