//
//  DateRangeStatus.swift
//  Naber
//
//  Created by 王淳彦 on 2018/8/2.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation


enum SwitchStatus: String {
    case OPEN = "OPEN"
    case CLOSE = "CLOSE"
    case UNKNOWN = "UNKNOWN"
    
    func status() -> Bool {
        switch self {
        case .OPEN:
            return true
        case .CLOSE:
            return false
        case .UNKNOWN:
            return false
        }
    }
    
    static func of(name: String) -> SwitchStatus{
        switch name.uppercased() {
        case "OPEN":
            return .OPEN
        case "CLOSE":
            return .CLOSE
        default:
            return .UNKNOWN
        }
    }
    
    static func of(bool: Bool) -> String {
        switch bool {
        case true:
            return self.OPEN.rawValue
        case false:
            return self.CLOSE.rawValue
        }
    }
    
    
}
