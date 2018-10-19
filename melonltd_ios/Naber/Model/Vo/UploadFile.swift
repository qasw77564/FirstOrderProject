//
//  UploadFile.swift
//  Naber
//
//  Created by 王淳彦 on 2018/8/8.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation


class UploadFileResp: Codable{
    var status: String!
    var err_code: String!
    var err_msg: String!
    var data: String!
    
    public static func toJson(structs : UploadFileResp) -> String {
        do {
            return String(data: try JSONEncoder().encode(structs), encoding: .utf8)!
        } catch {
            return ""
        }
    }
    
    public  static func parse(src : String) -> UploadFileResp? {
        do {
            return try JSONDecoder().decode(UploadFileResp.self, from: src.data(using:.utf8)!)
        }catch {
            return nil
        }
    }
}
