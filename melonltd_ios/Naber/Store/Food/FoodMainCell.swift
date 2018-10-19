//
//  FoodMainCell.swift
//  Naber
//
//  Created by 王淳彦 on 2018/8/7.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class FoodMainCell: UITableViewCell, UITableViewDelegate, UITableViewDataSource {
    
    
    var items: [ItemVo] = []
    var rootIndex: Int!
    
    @IBOutlet weak var name: UILabel!
    @IBOutlet weak var addBtn: UIButton!
    
    @IBOutlet weak var demandsName: UITextField!
    @IBOutlet weak var deleteDemandsBtn: UIButton!
    
    @IBOutlet weak var subTableView: UITableView! {
        didSet {
            self.subTableView.delegate = self
            self.subTableView.dataSource = self
            self.subTableView.sectionHeaderHeight = 40
            self.subTableView.sectionFooterHeight = 0
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        print(self.items)
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.items.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: UIIdentifier.CELL.rawValue, for: indexPath) as! FoodSubCell
        
        cell.name.leftView?.tag = self.rootIndex
        cell.name.tag = indexPath.row
        cell.price.leftView?.tag = self.rootIndex
        cell.price.tag = indexPath.row
        
        cell.name.text = self.items[indexPath.row].name
        
        cell.deleteBtn.tag = self.rootIndex
        cell.deleteBtn.imageView?.tag = indexPath.row
        cell.price.isHidden = true
        cell.priceText.isHidden = true
        
        // 非需求選項顯示價格 input
        if self.demandsName.isHidden {
            cell.price.isHidden = false
            cell.priceText.isHidden = false
            cell.price.text = self.items[indexPath.row].price
        }
        
        return cell
    }    
}
