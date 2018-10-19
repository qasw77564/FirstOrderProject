//
//  SchoolDivided.swift
//  Naber
//
//  Created by 王淳彦 on 2018/10/5.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation



class SchoolDividedResp: Codable {
    var status : String!
    var err_code : String!
    var err_msg : String!
    var data : [SchoolDividedVo] = []
    
    public static func toJson(structs : SchoolDividedResp) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> SchoolDividedResp? {
        do {
            return try JSONDecoder().decode(SchoolDividedResp.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
    
}

class SchoolDividedVo: Codable {
    var area: String!
    var schools: [String] = []
    
    public static func toJson(structs : SchoolDividedVo) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> SchoolDividedVo? {
        do {
            return try JSONDecoder().decode(SchoolDividedVo.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
    
    public static func parseArray(src: String) -> [SchoolDividedVo] {
        do {
            return try JSONDecoder().decode([SchoolDividedVo].self, from: src.data(using:.utf8)!)
        }catch {
            return []
        }
    }
}
