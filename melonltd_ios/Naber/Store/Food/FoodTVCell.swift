//
//  FoodClassTVCell.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/6/9.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class FoodTVCell: UITableViewCell, UITextFieldDelegate {

    @IBOutlet weak var sortNum: UITextField! {
        didSet {
            self.sortNum.isEnabled = false
            self.sortNum.delegate = self
        }
    }
    @IBOutlet weak var name: UILabel!
    @IBOutlet weak var price: UILabel!
    @IBOutlet weak var photo: UIImageView!{
        didSet {
            self.photo.image = UIImage(named: "Logo")
        }
    }
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
    
    
    // 限制輸入長度
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        guard let text = textField.text else { return true }
        let newLength: Int = text.count + string.count - range.length
        if !CharacterSet.decimalDigits.isSuperset(of: CharacterSet(charactersIn: string)) {
            return false
        }
        return newLength <= 2
    }

}
