//
//  RespData.swift
//  Naber
//
//  Created by 王淳彦 on 2018/7/19.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation


class RespData: Codable {
    var status: String!
    var err_code: String!
    var err_msg: String!

    public static func toJson(structs : RespData) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public  static func parse(src : String) -> RespData? {
        do {
            return try JSONDecoder().decode(RespData.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}
