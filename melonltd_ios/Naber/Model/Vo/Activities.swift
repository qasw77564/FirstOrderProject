//
//  Activities.swift
//  Naber
//
//  Created by 王淳彦 on 2018/9/21.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation


class ActivitiesListResp: Codable {
    var status : String!
    var err_code : String!
    var err_msg : String!
    var data: [ActivitiesVo] = []
    
    public static func toJson(structs : ActivitiesListResp) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> ActivitiesListResp? {
        do {
            return try JSONDecoder().decode(ActivitiesListResp.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}

class ActivitiesResp: Codable {
    var status : String!
    var err_code : String!
    var err_msg : String!
    var data: ActivitiesVo!
    
    public static func toJson(structs : ActivitiesResp) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> ActivitiesResp? {
        do {
            return try JSONDecoder().decode(ActivitiesResp.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}


class ActivitiesVo: Codable {
    var act_uuid: String!
    var serial: String!
    var act_category: String!
    var rel_uuid: String!
    var title: String!
    var content_text: String!
    var photo: String!
    var data: String!
    var restrict_func: String!
    var need_bonus: String!
    var restrict_limit_date: String!
    var restrict_send_count: String!
    var restrict_get_count: String!
    var man_date: String!
    var exp_date: String!
    var status: String!
    var enable: String!
    var create_date: String!
    var update_date: String!
    
    
    public static func toJson(structs : ActivitiesVo) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> ActivitiesVo? {
        do {
            return try JSONDecoder().decode(ActivitiesVo.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}
