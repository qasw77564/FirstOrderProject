//
//  SellerRegistered.swift
//  Naber
//
//  Created by 王淳彦 on 2018/7/19.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation

class SellerRegisteredResp: Codable {
    var status: String!
    var err_code: String!
    var err_msg: String!
    var data: SellerRegisteredVo!
    
    public static func toJson(structs: SellerRegisteredResp) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public  static func parse(src : String) -> SellerRegisteredResp? {
        do {
            return try JSONDecoder().decode(SellerRegisteredResp.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}


class SellerRegisteredVo: Codable {
    var seller_name: String!
    var phone: String!
    var address: String!
    var name: String!
    var device_id: String!
    
    public static func toJson(structs: SellerRegisteredVo) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public  static func parse(src : String) -> SellerRegisteredVo? {
        do {
            return try JSONDecoder().decode(SellerRegisteredVo.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}
