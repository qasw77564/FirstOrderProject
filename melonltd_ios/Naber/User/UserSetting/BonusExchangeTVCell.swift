//
//  BonusExchangeTVCell.swift
//  Naber
//
//  Created by melon on 2018/8/2.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation
import UIKit

class BonusExchangeTVCell: UITableViewCell {
    
    @IBOutlet var point: UILabel!
    @IBOutlet var productName: UILabel!
    @IBOutlet var remarks: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
}
