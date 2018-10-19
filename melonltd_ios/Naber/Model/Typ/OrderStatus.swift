//
//  OrderStatus.swift
//  Naber
//
//  Created by 王淳彦 on 2018/7/28.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation
import UIKit

enum OrderStatus : String {
    case LIVE = "LIVE"
    case UNFINISH = "UNFINISH"
    case PROCESSING = "PROCESSING"
    case CAN_FETCH = "CAN_FETCH"
    case CANCEL = "CANCEL"
    case FAIL = "FAIL"
    case FINISH = "FINISH"
    case UNKNOWN = "UNKNOWN"
    
    func get() -> Status {
        switch self {
        case .LIVE:
            return Status(name: "LIVE", value: "即時", color: NaberConstant.COLOR_BASIS_BRIGHT_YELLOW)
        case .UNFINISH:
            return Status(name: "UNFINISH", value: "未處理", color: NaberConstant.COLOR_BASIS_BLUE)
        case .PROCESSING:
            return Status(name: "PROCESSING", value: "製作中", color: NaberConstant.COLOR_BASIS_ORANGE)
        case .CAN_FETCH:
            return Status(name: "CAN_FETCH", value: "可領取", color: NaberConstant.COLOR_BASIS_BRIGHT_YELLOW)
        case .CANCEL:
            return Status(name: "CANCEL", value: "取消", color: NaberConstant.COLOR_BASIS_RED)
        case .FAIL:
            return Status(name: "FAIL", value: "跑單", color: NaberConstant.COLOR_BASIS_RED)
        case .FINISH:
            return Status(name: "FINISH", value: "完成", color: NaberConstant.COLOR_BASIS_GREEN)
        case .UNKNOWN:
            return Status(name: "UNKNOWN", value: "", color: NaberConstant.COLOR_BASIS_RED)
        }
    }
    
    static func of(name : String!) -> OrderStatus {
        switch name {
        case "LIVE":
            return .LIVE
        case "UNFINISH":
            return .UNFINISH
        case "PROCESSING":
            return .PROCESSING
        case "CAN_FETCH":
            return .CAN_FETCH
        case "CANCEL":
            return .CANCEL
        case "FAIL":
            return .FAIL
        case "FINISH":
            return .FINISH
        default :
            return .UNKNOWN
        }
    }
}

class Status {
    var name: String!
    var value: String!
    var color: UIColor!

    init(name: String, value: String, color: UIColor) {
        self.name = name
        self.value = value
        self.color = color
    }
}
