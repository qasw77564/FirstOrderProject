//
//  AccountInfoResp.swift
//  Naber
//
//  Created by 王淳彦 on 2018/7/18.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation

class AccountInfoResp :Codable {
    var status : String!
    var err_code : String!
    var err_msg : String!
    var data : AccountInfoVo!
    
    public static func toJson(structs : AccountInfoResp) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public  static func parse(src : String) -> AccountInfoResp? {
        do {
            return try JSONDecoder().decode(AccountInfoResp.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}


class AccountInfoVo : Codable{
    var account : String!
    var account_uuid : String!
    var restaurant_uuid : String!
    var password : String!
    var name : String!
    var email : String!
    var phone : String!
    var address : String!
    var birth_day : String!
    var gender: String!
    var identity : String!
    var school_name : String!
    var bonus : String!
    var use_bonus : String!
    var level : String!
    var enable : String!
    var is_login : String!
    var login_date : String!
    var photo : String!
    var photo_type : String!
    var device_token : String!
    var device_category : String!
    
    public static func toJson(structs : AccountInfoVo) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public  static func parse(src : String) -> AccountInfoVo? {
        do {
            return try JSONDecoder().decode(AccountInfoVo.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
    
}
