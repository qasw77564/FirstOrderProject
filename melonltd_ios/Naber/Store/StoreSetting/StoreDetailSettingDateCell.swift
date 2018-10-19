//
//  StoreDetailSettingDateCell.swift
//  Naber
//
//  Created by 王淳彦 on 2018/8/8.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class StoreDetailSettingDateCell: UITableViewCell {
    
    @IBOutlet weak var dateName: UILabel!
    @IBOutlet weak var weekName: UILabel!
    @IBOutlet weak var status: UISwitch!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        // Configure the view for the selected state
    }
}
