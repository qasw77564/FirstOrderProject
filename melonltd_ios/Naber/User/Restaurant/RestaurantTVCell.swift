//
//  TableViewCell.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/5/26.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class RestaurantTVCell: UITableViewCell {

    @IBOutlet var name: UILabel!
    @IBOutlet var workStatus: UILabel!
    @IBOutlet var distance: UILabel!
    @IBOutlet var time: UILabel!
    @IBOutlet var address: UILabel!
    @IBOutlet var photo: UIImageView! {
        didSet {
            photo.image = UIImage(named: "Logo")
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
