//
//  DateRange.swift
//  Naber
//
//  Created by melon on 2018/7/23.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation


class DateRangeResp : Codable {
        var status : String!
        var err_code : String!
        var err_msg : String!
        var data : [DateRangeVo]! = []
        
        public static func toJson(structs : DateRangeResp) -> String {
            do {
                return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
            } catch {
                return ""
            }
        }
        
        public static func parse(src : String) -> DateRangeResp? {
            do {
                return try JSONDecoder().decode(DateRangeResp.self, from: src.data(using:.utf8)!)
            }catch {
                return nil
            }
        }
        
    }
class DateRangeVo : Codable {
        var status : String!
        var date : String!
    
        public static func toJson(structs : DateRangeVo) -> String {
            do {
                return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
            } catch {
                return ""
            }
        }
    
        public static func toJsonArray(structs : [DateRangeVo]) -> String {
            do {
                return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
            } catch {
                return ""
            }
        }
        
        public static func parse(src : String) -> DateRangeVo? {
            do {
                return try JSONDecoder().decode(DateRangeVo.self, from: src.data(using:.utf8)!)
            }catch {
                return nil
            }
        }
}




