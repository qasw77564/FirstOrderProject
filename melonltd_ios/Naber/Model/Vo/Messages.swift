//
//  Messages.swift
//  Naber
//
//  Created by 王淳彦 on 2018/10/4.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation


class MessagesResq: Codable {
    var status: String!
    var err_code: String!
    var err_msg: String!
    var data: String!
    
    public static func toJson(structs : MessagesResq) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public  static func parse(src : String) -> MessagesResq? {
        do {
            return try JSONDecoder().decode(MessagesResq.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}
