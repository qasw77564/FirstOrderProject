//
//  RecordInfoOrderTVCell.swift
//  Naber
//
//  Created by 王淳彦 on 2018/7/29.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation
import UIKit


class RecordInfoOrderTVCell : UITableViewCell {
    
    @IBOutlet var orderDatas: UILabel!
    @IBOutlet var name: UILabel!
    @IBOutlet var count: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
}

