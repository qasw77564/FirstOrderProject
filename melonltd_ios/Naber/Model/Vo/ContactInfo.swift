//
//  ContactInfo.swift
//  Naber
//
//  Created by 王淳彦 on 2018/9/27.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation

class ContactInfo: Codable {
    var name: String!
    var phone: String!
    var email: String!
    
    var code: String!
    var city: String!
    var area: String!
    var address: String!
    
    public static func toJson(structs: ContactInfo) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public static func parse(src : String) -> ContactInfo? {
        do {
            return try JSONDecoder().decode(ContactInfo.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
    
}

