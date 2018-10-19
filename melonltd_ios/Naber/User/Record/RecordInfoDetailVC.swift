//
//  RecordInfoDetailViewController.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/5/28.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class RecordInfoDetailVC: UIViewController, UITableViewDataSource, UITableViewDelegate{


    var order: OrderVo!
    
    @IBOutlet weak var name: UILabel!
    @IBOutlet weak var totalPayment: UILabel!
    @IBOutlet weak var orderTime: UILabel!
    @IBOutlet weak var recordTime: UILabel!
    @IBOutlet weak var address: UILabel!
    @IBOutlet weak var memoInfo: UILabel!
    @IBOutlet weak var bonus: UILabel!
    @IBOutlet weak var useBonus: UILabel!
    
    @IBOutlet weak var tableView: UITableView!{
        didSet {
            self.tableView.dataSource = self
            self.tableView.delegate = self
            let refreshControl: UIRefreshControl = UIRefreshControl()
            refreshControl.attributedTitle = NSAttributedString(string: "Pull to refresh", attributes: [NSAttributedStringKey.foregroundColor: UIColor(red: 188/255, green: 188/255, blue: 188/255, alpha: 1.0)])
            refreshControl.addTarget(self, action: #selector(refresh), for: .valueChanged)
            refreshControl.tintColor = UIColor.clear
            self.tableView.addSubview(refreshControl)
        }
    }
    
    @objc func refresh(sender: UIRefreshControl){
        sender.endRefreshing()
        self.tableView.reloadData()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.name.text = self.order.order_detail.restaurant_name
        
        self.orderTime.text = DateTimeHelper.formToString(date: self.order.create_date, from: "yyyy年 MM月 dd日 HH:mm")
        self.recordTime.text = DateTimeHelper.formToString(date: self.order.fetch_date, from: "yyyy年 MM月 dd日 HH:mm")
        self.address.text = self.order.order_detail.restaurant_address
        self.memoInfo.text = self.order.order_detail.user_message
        self.bonus.text = self.order.order_bonus
        
        if self.order.order_detail.use_bonus != nil {
            let price: Int = Int(self.order.order_price)! - (Int(self.order.order_detail.use_bonus)! / 10 * 3)
            self.totalPayment.text = "$" + price.description
            self.useBonus.text = self.order.order_detail.use_bonus
        }else {
            self.totalPayment.text = "$" + self.order.order_price
        }
    }

    // MARK: - Table view data source
    func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: UIIdentifier.CELL.rawValue, for: indexPath) as! RecordInfoOrderTVCell
        let item: FoodItemVo = order.order_detail.orders[indexPath.row].item
        var datas: String = ""
        cell.name.text = item.food_name
        cell.count.text = "("+order.order_detail.orders[indexPath.row].count+")"
        
        datas += "規格 :"
        if !item.scopes.isEmpty {
            for scopes in item.scopes {
                datas += scopes.name + ", "
            }
            datas += "\n"
        } else {
            datas += "統一規格, "
            datas += "\n"
        }

        if !item.opts.isEmpty {
            datas += "追加 :"
            for opt in item.opts {
                datas += opt.name + ", "
            }
            datas += "\n"
        }

        if !item.demands.isEmpty {
            for demand in item.demands {
                if !demand.datas.isEmpty {
                    datas += demand.name + ": "
                    for d in demand.datas {
                        datas += d.name + ", "
                    }
                }
                datas += "\n"
            }
        }
    
        cell.orderDatas.text = datas
        return cell
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if self.order.order_detail != nil {
            return order.order_detail.orders.count
        }
        return 0
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}
