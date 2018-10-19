//
//  ShoppingCardSubTableViewCell.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/5/28.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit


class ShoppingCardSubTVCell: UITableViewCell {
    
    @IBOutlet weak var foodPhoto: UIImageView!
    @IBOutlet weak var foodName: UILabel!
    @IBOutlet weak var foodPrice: UILabel!
    @IBOutlet weak var foodCount: UILabel!
    @IBOutlet weak var foodDatas: UILabel!
    @IBOutlet weak var countStepper: UIStepper!
    @IBOutlet weak var deleteFoodBtn: UIButton!

    override func awakeFromNib() {
        super.awakeFromNib()
        self.countStepper.maximumValue = 50
        self.countStepper.minimumValue = 1
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

    }
}
