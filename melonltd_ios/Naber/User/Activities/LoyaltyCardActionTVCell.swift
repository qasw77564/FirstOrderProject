//
//  LoyaltyCardActionTVCell.swift
//  Naber
//
//  Created by 王淳彦 on 2018/10/12.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit


class LoyaltyCardActionTVCell: UITableViewCell {

    @IBOutlet weak var name: NaberUILabel!
    @IBOutlet weak var content: NaberUILabel!
    @IBOutlet weak var loyaltyStatus: NaberUILabel!
    @IBOutlet weak var exchangeBtn: UIButton! {
        didSet{
            self.exchangeBtn.isHidden = true
        }
    }
        
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        // Configure the view for the selected state
    }

}
