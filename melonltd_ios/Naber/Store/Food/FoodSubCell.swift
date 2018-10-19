//
//  FoodSubCell.swift
//  Naber
//
//  Created by 王淳彦 on 2018/8/7.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit


class FoodSubCell : UITableViewCell, UITextFieldDelegate {
    
    @IBOutlet weak var name: UITextField! {
        didSet{
            self.name.leftViewMode = .always
            self.name.leftView = UIView(frame: CGRect(x: 0, y: 0, width: 10, height: 10))
        }
    }
    @IBOutlet weak var price: UITextField!{
        didSet{
            self.price.leftViewMode = .always
            self.price.leftView = UIView(frame: CGRect(x: 0, y: 0, width: 10, height: 10))
            self.price.delegate = self
        }
    }
    @IBOutlet weak var priceText: UILabel!
    @IBOutlet weak var deleteBtn: UIButton!
    
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
        }else if newLength == 1 && string == "0" {
            return false
        }else {
            return true
        }
    }
}
