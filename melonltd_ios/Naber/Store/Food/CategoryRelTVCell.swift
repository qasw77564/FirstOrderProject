//
//  FoodTVCell.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/6/8.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class CategoryRelCell: UITableViewCell {


    @IBOutlet weak var sortNum: UITextField!{
        didSet{
            self.sortNum.isEnabled = false
        }
    }
    @IBOutlet weak var name: UILabel!
    @IBOutlet weak var switchBtn: UISwitch!
    @IBOutlet weak var deleteBtn: UIButton!
    @IBOutlet weak var editBtn: UIButton!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
