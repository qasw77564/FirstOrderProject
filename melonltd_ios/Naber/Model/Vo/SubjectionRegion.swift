//
//  SubjectionRegion.swift
//  Naber
//
//  Created by 王淳彦 on 2018/9/22.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation

class SubjectionRegionResp: Codable {
    var status : String!
    var err_code : String!
    var err_msg : String!
    var data : [SubjectionRegionVo] = []
    
    public static func toJson(structs : SubjectionRegionResp) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> SubjectionRegionResp? {
        do {
            return try JSONDecoder().decode(SubjectionRegionResp.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}


class SubjectionRegionVo: Codable {
    var city : String!
    var areas : [AreaVo] = []
    
    public static func toJson(structs : SubjectionRegionVo) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func toJsonArray(structs : [SubjectionRegionVo]) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> SubjectionRegionVo? {
        do {
            return try JSONDecoder().decode(SubjectionRegionVo.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
    
    public static func parseArray(src : String) -> [SubjectionRegionVo] {
        do {
            return try JSONDecoder().decode([SubjectionRegionVo].self, from: src.data(using:.utf8)!)
        }catch {
            return []
        }
    }
    
}

class AreaVo: Codable {
    var area: String!
    var postal_code: String!
    
    public static func toJson(structs : AreaVo) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> AreaVo? {
        do {
            return try JSONDecoder().decode(AreaVo.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
   
}
