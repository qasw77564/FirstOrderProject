//
//  RestaurantStoreSelectScopresTableViewCell.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/6/3.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class RestaurantStoreSelectTVCell: UITableViewCell {

    @IBOutlet weak var price: UILabel!
    @IBOutlet weak var name: UILabel!
    @IBOutlet weak var radioButton: UIButton!
    
    var item: ItemVo!
    
    override var frame: CGRect {
        didSet {
            var newFrame = frame
            newFrame.origin.x += 8
            newFrame.size.width -= 16
            super.frame = newFrame
        }
    }
    
    func cellWillAppear() {
        self.name.text = self.item.name
        self.price.text = self.item.price
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
//        let color: UIColor = UIColor(red: 188/255, green: 188/255, blue: 188/255, alpha: 1)
//        self.layer.addBorder(edge: UIRectEdge.left, color: color, thickness: 0.5)
//        self.layer.addBorder(edge: UIRectEdge.right, color: color, thickness: 0.5)
        
        let borderLeft: UIView = UIView.init()
        borderLeft.backgroundColor = UIColor(red: 188/255, green: 188/255, blue: 188/255, alpha: 1)
        borderLeft.frame = CGRect(x:0, y:0, width:0.5, height:self.frame.size.height)
        borderLeft.autoresizingMask = .flexibleRightMargin
        self.addSubview(borderLeft)
        
        
        let borderRight: UIView = UIView.init()
        borderRight.backgroundColor = UIColor(red: 188/255, green: 188/255, blue: 188/255, alpha: 1)
        borderRight.frame = CGRect(x:self.frame.size.width - 0.5, y:0, width:0.5, height:self.frame.size.height)
        borderRight.autoresizingMask = .flexibleLeftMargin
        self.addSubview(borderRight)

    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }

    func triggerRadioStatus (_ selected: Bool){
        self.radioButton.isSelected = selected
        self.radioButton.setImage(UIImage(named: self.radioButton.isSelected ? "radioSelect" : "radioNoSelect"), for: .normal)
    }
    
    @IBAction func selectedRadio(_ sender: UIButton) {

        let indexPath = IndexPath(row: self.item.tag, section: sender.tag);
        (self.superview as? UITableView)?.selectRow(at: indexPath, animated: true, scrollPosition: .none)
        if let tableView = self.superview as? UITableView {
            tableView.selectRow(at: indexPath, animated: true, scrollPosition: .none)
        }
//        self.triggerRadioStatus(sender.isSelected)
    }

}
