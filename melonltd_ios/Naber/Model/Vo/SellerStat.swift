//
//  SellerStat.swift
//  Naber
//
//  Created by melon on 2018/7/23.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation


class SellerStatResp : Codable {
    var status : String!
    var err_code : String!
    var err_msg : String!
    var data : SellerStatVo!
    
    public static func toJson(structs : SellerStatResp) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> SellerStatResp? {
        do {
            return try JSONDecoder().decode(SellerStatResp.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
    
}
class SellerStatVo : Codable {
    var  year_income:String!
    var  month_income:String!
    var  day_income:String!
    var  finish_count:String!
    var  status_dates: [String]! = []
    var  unfinish_count:String!
    var  processing_count:String!
    var  can_fetch_count:String!
    var  cancel_count:String!
    
    public static func toJson(structs : SellerStatVo) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> SellerStatVo? {
        do {
            return try JSONDecoder().decode(SellerStatVo.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}
