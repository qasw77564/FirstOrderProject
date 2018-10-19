//
//  TableViewHeadControl.swift
//  Naber
//
//  Created by 王淳彦 on 2018/8/6.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

//import Foundation
import UIKit

class TableViewHeadControl {
    
    
    static func newHeadControl(selector: Selector ) -> UIRefreshControl{
        let refreshControl: UIRefreshControl = UIRefreshControl()
        refreshControl.attributedTitle = NSAttributedString(string: "Pull to refresh", attributes: [NSAttributedStringKey.foregroundColor: UIColor(red: 188/255, green: 188/255, blue: 188/255, alpha: 1.0)])
        refreshControl.addTarget(self, action: selector, for: .valueChanged)
        refreshControl.tintColor = UIColor.clear
        return refreshControl
    }
}
