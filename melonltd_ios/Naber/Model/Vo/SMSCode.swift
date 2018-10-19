//
//  SMSCode.swift
//  Naber
//
//  Created by 王淳彦 on 2018/7/19.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation

class SMSCodeResp: Codable {
    var status : String!
    var err_code : String!
    var err_msg : String!
    var data : SMSCodeVo!
    
    public static func toJson(structs: SMSCodeResp) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> SMSCodeResp? {
        do {
            return try JSONDecoder().decode(SMSCodeResp.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
    
}


class SMSCodeVo: Codable {
    var batch_id: String!
    var phone: String!
    var code: String!
    
    public static func toJson(structs: SMSCodeVo) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public  static func parse(src : String) -> SMSCodeVo? {
        do {
            return try JSONDecoder().decode(SMSCodeVo.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}
