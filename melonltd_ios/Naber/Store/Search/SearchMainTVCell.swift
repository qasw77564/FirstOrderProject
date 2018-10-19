//
//  SearchMainTVCell.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/6/8.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class SearchMainTVCell: UITableViewCell {

    @IBOutlet weak var orderStatus: UILabel!
    @IBOutlet weak var orderType: UILabel!
    @IBOutlet weak var count: UILabel!
    @IBOutlet weak var foodDatas: UILabel!
    @IBOutlet weak var userMessage: UILabel!
    @IBOutlet weak var fetchTime: UILabel!
    
    @IBOutlet weak var phone: UILabel!
    @IBOutlet weak var name: UILabel!
    @IBOutlet weak var price: UILabel!
    
    @IBOutlet weak var cancelBtn: UIButton!{
        didSet{
            self.cancelBtn.isHidden = true
        }
    }
    @IBOutlet weak var failureBtn: UIButton!{
        didSet{
            self.failureBtn.isHidden = true
        }
    }
    @IBOutlet weak var processingBtn: UIButton!{
        didSet{
            self.processingBtn.isHidden = true
        }
    }
    @IBOutlet weak var canFetchBtn: UIButton!{
        didSet{
            self.canFetchBtn.isHidden = true
        }
    }
    @IBOutlet weak var finishBtn: UIButton!{
        didSet{
            self.finishBtn.isHidden = true
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
