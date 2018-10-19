//
//  ShoppingCarMainTableViewCell.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/5/28.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class ShoppingCarMainTVCell: UITableViewCell, UITableViewDataSource, UITableViewDelegate {

    // 依照商家所加入的品項列表
    // Model.USER_CACHE_SHOPPING_CART[self.tag].orders

    @IBOutlet weak var storeName: UILabel!
    @IBOutlet weak var itemTable: UITableView! {
        didSet {
            self.itemTable.delegate = self
            self.itemTable.dataSource = self
        }
    }
    
    @IBOutlet weak var price: UILabel!
    @IBOutlet weak var bonus: UILabel!
    @IBOutlet weak var submitBtn: UIButton!
    @IBOutlet weak var cancelBtn: UIButton!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        self.price.text = "0"
        self.bonus.text = "0"
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        var price: Double = 0.0
        for i in 0..<Model.USER_CACHE_SHOPPING_CART[self.tag].orders.count {
            price += Double(Model.USER_CACHE_SHOPPING_CART[self.tag].orders[i].item.price)!
        }
        self.price.text = Int(price).description
        
        if let can_discount: String = Model.USER_CACHE_SHOPPING_CART[self.tag].can_discount {
            self.bonus.text = can_discount == "Y" ? Int(floor(price / 10.0)).description : "該店家不提供紅利"
        }
    }
    
    func cellWillAppear (){
        let price: Double = Model.USER_CACHE_SHOPPING_CART[self.tag].orders.reduce(0.0, { (sum, num) -> Double in
            return sum + Double(num.item.price)!
        })
        self.price.text = Int(price).description
        if let can_discount: String = Model.USER_CACHE_SHOPPING_CART[self.tag].can_discount {
            self.bonus.text = can_discount == "Y" ? Int(floor(price / 10.0)).description : "該店家不提供紅利"
        }
        self.itemTable.reloadData()
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return Model.USER_CACHE_SHOPPING_CART[self.tag].orders.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cellIdentifier = "ItemCell"
        let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as! ShoppingCardSubTVCell
        cell.foodName.text = Model.USER_CACHE_SHOPPING_CART[self.tag].orders[indexPath.row].item.food_name
    
        
        cell.foodPhoto.setImage(with: URL(string: Model.USER_CACHE_SHOPPING_CART[self.tag].orders[indexPath.row].item.food_photo ?? ""), transformer: TransformerHelper.transformer(identifier: Model.USER_CACHE_SHOPPING_CART[self.tag].orders[indexPath.row].item.food_photo ?? ""),  completion: { image in
            if image == nil {
                cell.foodPhoto.image = UIImage(named: "Logo")
            }
        })
        
        
        cell.foodCount.text = Model.USER_CACHE_SHOPPING_CART[self.tag].orders[indexPath.row].count
        cell.foodPrice.text = Model.USER_CACHE_SHOPPING_CART[self.tag].orders[indexPath.row].item.price
        cell.countStepper.autorepeat = false
        cell.countStepper.tag = indexPath.row
        cell.countStepper.value = Double(Model.USER_CACHE_SHOPPING_CART[self.tag].orders[indexPath.row].count)!
        cell.countStepper.addTarget(self, action: #selector(changedCount), for: .touchUpInside)
        // sudIndex
        cell.deleteFoodBtn.tag = indexPath.row
        // rootIndex
        cell.deleteFoodBtn.imageView?.tag = self.tag
        cell.deleteFoodBtn.titleLabel?.text = self.tag.description
        
        var foodDatas: String = ""
        foodDatas += "規格: "
        if !Model.USER_CACHE_SHOPPING_CART[self.tag].orders[indexPath.row].item.scopes.isEmpty {
            Model.USER_CACHE_SHOPPING_CART[self.tag].orders[indexPath.row].item.scopes.forEach { s in
                foodDatas += s.name + ", "
            }
            foodDatas += "\n"
        }else {
            foodDatas += "預設, "
            foodDatas += "\n"
        }
        
        if !Model.USER_CACHE_SHOPPING_CART[self.tag].orders[indexPath.row].item.opts.isEmpty {
            foodDatas += "追加: "
            Model.USER_CACHE_SHOPPING_CART[self.tag].orders[indexPath.row].item.opts.forEach{ o in
                foodDatas +=  o.name + ","
            }
            foodDatas += "\n"
        }
        
        Model.USER_CACHE_SHOPPING_CART[self.tag].orders[indexPath.row].item.demands.forEach { md in
            foodDatas += md.name + ": "
            md.datas.forEach{ sd in
                foodDatas += sd.name
            }
            foodDatas += "  "
        }
        
        cell.foodDatas.text = foodDatas
        return cell
    }
    
    @objc func changedCount(_ sender: UIStepper) {
        let price: Int = Int(Int(Model.USER_CACHE_SHOPPING_CART[self.tag].orders[sender.tag].item.price)! /
        Int(Model.USER_CACHE_SHOPPING_CART[self.tag].orders[sender.tag].count)!)
        Model.USER_CACHE_SHOPPING_CART[self.tag].orders[sender.tag].count = Int(sender.value).description
        Model.USER_CACHE_SHOPPING_CART[self.tag].orders[sender.tag].item.price = Int(price * Int(sender.value)).description
        UserSstorage.setShoppingCartDatas(datas: Model.USER_CACHE_SHOPPING_CART)
    }
    
}


