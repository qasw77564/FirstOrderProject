//
//  Advertisement.swift
//  Naber
//
//  Created by 王淳彦 on 2018/7/19.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation


class AdvertisementResp: Codable {
    var status: String!
    var err_code: String!
    var err_msg: String!
    var data: [AdvertisementVo]! = []
    
    public static func toJson(structs: AdvertisementResp) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public  static func parse(src : String) -> AdvertisementResp? {
        do {
            return try JSONDecoder().decode(AdvertisementResp.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}


class AdvertisementVo: Codable {
    var title: String!
    var content_text: String!
    var photo: String!
    var pad_photo: String!
    var link_type: String!
    var link_to: String!
    
    public static func toJson(structs: AdvertisementVo) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public  static func parse(src : String) -> AdvertisementVo? {
        do {
            return try JSONDecoder().decode(AdvertisementVo.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}


class LinkToVo: Codable {
    var android: String!
    var ios: String!
    
    public static func toJson(structs: LinkToVo) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public  static func parse(src : String) -> LinkToVo? {
        do {
            return try JSONDecoder().decode(LinkToVo.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}
