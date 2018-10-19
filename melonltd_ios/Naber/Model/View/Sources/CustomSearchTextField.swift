//
//  CustomSearchTextField.swift
//  Naber
//
//  Created by 王淳彦 on 2018/7/26.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation
import UIKit

class CustomSearchTextField: UITextField {
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override func textRect(forBounds bounds: CGRect) -> CGRect {
        // 左，上，右，下
        return UIEdgeInsetsInsetRect(bounds, UIEdgeInsetsMake(0, 16, 0, 16))
    }
    
    override func placeholderRect(forBounds bounds: CGRect) -> CGRect {
        return UIEdgeInsetsInsetRect(bounds, UIEdgeInsetsMake(0, 16, 0, 16))
    }
    
    
    
}
