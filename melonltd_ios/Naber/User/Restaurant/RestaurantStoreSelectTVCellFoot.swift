//
//  RestaurantStoreSelectFooterTableViewCell.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/6/3.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class RestaurantStoreSelectTVCellFoot: UITableViewCell {
    
    override var frame: CGRect {
        didSet {
            var newFrame = frame
            newFrame.origin.x += 8
            newFrame.size.width -= 16
            //            newFrame.size.height = 37
            super.frame = newFrame
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        self.contentView.layer.addBorder(edge: UIRectEdge.top, color: UIColor(red: 188/255, green: 188/255, blue: 188/255, alpha: 1), thickness: 0.5)
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
