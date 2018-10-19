//
//  Identity.swift
//  Naber
//
//  Created by 王淳彦 on 2018/7/20.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
import Foundation
//
//JUNIOR("小學生"),
//INTERMEDIATE("國中生"),
//SENIOR("高中生"),
//UNIVERSITY("大學/大專院校生"),
//NON_STUDENT("社會人士/其它"),
//SELLERS("SELLERS");

enum Identity: String {
    case JUNIOR = "JUNIOR"
    case INTERMEDIATE = "INTERMEDIATE"
    case SENIOR = "SENIOR"
    case UNIVERSITY = "UNIVERSITY"
    case NON_STUDENT = "NON_STUDENT"
    case SELLERS = "SELLERS"
    case UNKNOWN = "UNKNOWN"

    
    func getName() -> String {
        switch self {
        case .JUNIOR:
            return "小學生"
        case .INTERMEDIATE:
            return "國中生"
        case .SENIOR:
            return "高中生"
        case .UNIVERSITY:
            return "大學/大專院校生"
        case .NON_STUDENT:
            return "社會人士/其它"
        case .SELLERS:
            return "SELLERS"
        case .UNKNOWN:
            return "UNKNOWN"
        }
    }
    
    static func toEnum(name : String!) -> Identity {
        switch name {
        case "小學生":
            return .JUNIOR
        case  "國中生":
            return .INTERMEDIATE
        case "高中生":
            return .SENIOR
        case "大學/大專院校生":
            return .UNIVERSITY
        case "社會人士/其它":
            return .NON_STUDENT
        case "SELLERS":
            return .SELLERS
        default:
            return .UNKNOWN
        }
    }
    
    
    static func getUserValues() -> [Identity] {
        return [.JUNIOR, .INTERMEDIATE, .SENIOR, .UNIVERSITY, .NON_STUDENT]
    }

}
