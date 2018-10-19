//
//  AppVersionLog.swift
//  Naber
//
//  Created by 王淳彦 on 2018/8/24.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation

class AppVersionLogResp : Codable {
    
    var status : String!
    var err_code : String!
    var err_msg : String!
    var data : AppVersionLogVo!
    
    public static func toJson(structs : AppVersionLogResp) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> AppVersionLogResp? {
        do {
            return try JSONDecoder().decode(AppVersionLogResp.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
    
}



class AppVersionLogVo : Codable {
    var version: String!
    var category: String!
    var need_upgrade: String!
    var create_date: String!
    
    public static func toJson(structs : AppVersionLogVo) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> AppVersionLogVo? {
        do {
            return try JSONDecoder().decode(AppVersionLogVo.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}
