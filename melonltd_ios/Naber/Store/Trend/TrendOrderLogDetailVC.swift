//
//  TrendOrderLogDetailVC.swift
//  Naber
//
//  Created by 王淳彦 on 2018/8/4.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation
import UIKit
class TrendOrderLogDetailVC : UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    
    var order: OrderVo!
    
    @IBOutlet weak var tableView: UITableView!{
        didSet {
            self.tableView.dataSource = self
            self.tableView.delegate = self
        }
    }
    
    @IBOutlet weak var count: UILabel!
    @IBOutlet weak var userMessage: UILabel!
    @IBOutlet weak var fetchTime: UILabel!
    
    @IBOutlet weak var phone: UILabel!
    @IBOutlet weak var name: UILabel!
    @IBOutlet weak var price: UILabel!
    
    @IBOutlet weak var finishBtn: UIButton!
    @IBOutlet weak var cancelBtn: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.count.text = "(" + self.order.order_detail.orders.count.description + ")"
        self.price.text = "$ " + self.order.order_price
        if self.order.order_detail.use_bonus != nil {
            let price: Int = Int(self.order.order_price)! - (Int(self.order.order_detail.use_bonus)! / 10 * 3)
            self.price.text = "$" + price.description + ", div. " + self.order.order_detail.use_bonus
        }else {
            self.price.text = "$" + self.order.order_price
        }
        
        self.name.text = self.order.order_detail.user_name
        self.fetchTime.text = DateTimeHelper.formToString(date: self.order.create_date, from: "dd日 HH時 mm分")
        self.userMessage.text = self.order.user_message
        
        if OrderStatus.CANCEL == OrderStatus.of(name: self.order.status) {
          self.finishBtn.isHidden = true
        } else {
          self.cancelBtn.isHidden = true
        }
    }
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.order.order_detail.orders.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: UIIdentifier.CELL.rawValue, for: indexPath) as! TrendOrderLogDetailTVCell
        
        let o: OrderData = self.order.order_detail.orders[indexPath.row]
        var content: String = ""
        content +=  o.item.category_name + ": " +
            StringsHelper.padEnd(str: o.item.food_name, minLength: 10, of: " ") +
            StringsHelper.padEnd(str: "x" + o.count, minLength: 15, of: " ") +
            "$ " + o.item.price +
            "\n規格: " +
            StringsHelper.padEnd(str: o.item.scopes[0].name, minLength: 20 , of: " ") +
            "$ " +
            StringsHelper.padEnd(str: o.item.scopes[0].price, minLength: 10 , of: " ") +
            "\n附加: " +
            o.item.opts.reduce("", { (s: String, i: ItemVo) -> String in
                return s + "\n" + StringsHelper.padEnd(str:"- " + i.name, minLength: 20 , of: " ") +
                    StringsHelper.padEnd(str: "  ", minLength: 10 , of: " ") + "$ " + i.price
            }) + (o.item.opts.count == 0 ? " - 無\n" : "\n") +
        "需求: "
        for d in o.item.demands {
            content += d.name + ": " + d.datas[0].name + ", "
        }
        content += "\n------------------------------------------\n\n"
        cell.foodData.text = content
        return cell
    }
    
}
