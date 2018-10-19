//
//  LeftSideMenuTableViewCell.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/6/7.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class LeftSideMenuTVCell: UITableViewCell {

    @IBOutlet weak var time: UILabel!
    @IBOutlet weak var timeSwitch: UISwitch!
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
