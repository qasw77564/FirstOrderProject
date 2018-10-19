//
//  RestaurantStoreSelectViewController.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/6/3.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class RestaurantStoreSelectVC: UIViewController {
    
    @IBOutlet weak var tableView: UITableView! {
        didSet {
            tableView.delegate = self
            tableView.dataSource = self
        }
    }

    var food: FoodVo!
    var itemVo: FoodItemVo!
    var restaurantInfo: RestaurantInfoVo!
    var categoryRel: CategoryRelVo!
    
    @IBOutlet weak var orderTotal: UILabel!
    @IBOutlet weak var orderNumber: UILabel!
    @IBOutlet weak var numberStepper: UIStepper!
    
    @IBAction func numberStepperAction(_ sender: UIStepper) {
        self.orderNumber.text = String(Int(sender.value))
        calculatMoney()
    }
    
    @IBAction func addToShoppingCart(_ sender: UIButton) {
        var msg: String  = "規格："
        msg += self.itemVo.scopes[0].name + "\n"
        
        self.itemVo.demands.forEach{ demand in
            msg += demand.name + ": "
            msg += demand.datas[0].name + "\n"
        }
        
        if !self.itemVo.opts.isEmpty {
            msg += "追加項目："
            self.itemVo.opts.forEach{ opt in
                msg += opt.name + ","
            }
            msg += "\n"
        }

        msg += "數量：" + self.orderNumber.text! + "\n"
        msg += "金額：" + self.orderTotal.text!
        
        let alert = UIAlertController(title: "已成功加入購物車", message: msg , preferredStyle: .alert)
        let message: UILabel = alert.view.subviews[0].subviews[0].subviews[0].subviews[0].subviews[0].subviews[1] as! UILabel
        message.textAlignment = .left
        
        // 取出儲存購物車資料
        var shoppingCartDatas = UserSstorage.getShoppingCartDatas()

        let order: OrderData = OrderData()
        order.item = self.itemVo
        order.count = self.orderNumber.text
        order.category_uuid = self.food.category_uuid
        
        order.item.category_name = self.categoryRel.category_name
        order.item.food_uuid = self.food.food_uuid
        order.item.food_name = self.food.food_name
        order.item.price = self.orderTotal.text
        order.item.food_photo = self.food.photo
        
        let account = UserSstorage.getAccountInfo()
        var has: Bool = false;
        
        for o in shoppingCartDatas {
            if self.restaurantInfo.restaurant_uuid == o.restaurant_uuid {
                o.restaurant_name = self.restaurantInfo.name
                // TODO 位置配送類型 ＆ 結算類型，預設 原價 自取
                o.order_type = OrderType.setDefault()
                o.restaurant_address = self.restaurantInfo.address
                o.user_name = account?.name
                o.can_discount = self.restaurantInfo.can_discount
                o.user_phone = account?.phone
                o.orders.insert(order, at: 0)
                has = true
            }
        }
        
        if !has {
            let detail: OrderDetail = OrderDetail()
            detail.restaurant_uuid = self.restaurantInfo.restaurant_uuid
            detail.restaurant_name = self.restaurantInfo.name
            // TODO 位置配送類型 ＆ 結算類型，預設 原價 自取
            detail.order_type = OrderType.setDefault()
            detail.restaurant_address = self.restaurantInfo.address
            detail.can_discount = self.restaurantInfo.can_discount
            detail.user_name = account?.name
            detail.user_phone = account?.phone
            detail.orders.insert(order, at: 0)
            shoppingCartDatas.insert(detail, at: 0)
        }
        
        
//        can_discount
        UserSstorage.setShoppingCartDatas(datas: shoppingCartDatas)
 
        alert.addAction(UIAlertAction(title: "前往購物車", style: .default){ _ in
            // 使用 tabbar 控制到 購物車頁面
            // 並把當前頁面返回
            self.tabBarController?.selectedIndex = 2
            self.navigationController?.popToRootViewController(animated: false)
        })
        
        alert.addAction(UIAlertAction(title: "繼續購物", style: .default){ _ in
            // 反回上兩層定義
            // vcs = Main餐館列表 or Main首頁 >> 餐館細節（種類列表）>> 品項列表 >> 品項細節
            // vcs.count == 4
            // back to index 2 餐館細節（種類列表）
            // pop to vcs.count[ vcs.count - 3 ]
            self.navigationController?.popToViewController((self.navigationController?.viewControllers[(self.navigationController?.viewControllers.count)! - 3])!, animated: true)
        })
        self.present(alert, animated: false)
        
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.orderTotal.text = "0"
        self.orderNumber.text = "1"
        self.numberStepper.maximumValue = 50
        self.numberStepper.minimumValue = 1
        self.numberStepper.value = 1
        
        self.itemVo = FoodItemVo();
        for i in 0..<self.food.food_data.scopes.count {
            if self.food.food_data.scopes[i].price == self.food.default_price {
                if self.itemVo.scopes.isEmpty {
                    self.itemVo.scopes.append(ItemVo.init(name: self.food.food_data.scopes[i].name, price: self.food.food_data.scopes[i].price, tag: i))
                }
            }
            self.food.food_data.scopes[i].tag = i
        }
        
        for i in 0..<self.food.food_data.opts.count {
            self.food.food_data.opts[i].tag = i
        }
        
        for i in 0..<self.food.food_data.demands.count {
            self.itemVo.demands.append(DemandsItemVo.init(name: self.food.food_data.demands[i].name))
            for j in 0..<self.food.food_data.demands[i].datas.count {
                if self.itemVo.demands[i].datas.isEmpty {
                    self.itemVo.demands[i].datas.append(ItemVo.init(name: self.food.food_data.demands[i].datas[j].name, price: self.food.food_data.demands[i].datas[j].price, tag: j))
                }
                self.food.food_data.demands[i].datas[j].tag = j
            }
        }
        self.calculatMoney()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

}

extension RestaurantStoreSelectVC: UITableViewDelegate, UITableViewDataSource {

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        switch (section) {
        case 0:
            return self.food.food_data.scopes.count
        case 1:
            return self.food.food_data.opts.count
        case 2...:
            return self.food.food_data.demands[section - 2].datas.count
        default:
            return 0
        }
    }
    
    // 計算 Sections Cell 數量給 tableView
    func numberOfSections(in tableView: UITableView) -> Int {
        return self.food.food_data.demands.count + 2
    }

    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 36
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let  cell = tableView.dequeueReusableCell(withIdentifier: "Header") as! RestaurantStoreSelectTCCellHead
        switch (section) {
        case 0:
            cell.name.text = "規格(必選)"
        case 1:
            cell.name.text = "追加項目";
        case 2...:
            cell.name.text = self.food.food_data.demands[section - 2].name
        default:
            cell.name.text = ""
        }
        return cell
    }

    func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        let  footerCell = tableView.dequeueReusableCell(withIdentifier: "Footer") as! RestaurantStoreSelectTVCellFoot
        return footerCell
    }

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 36
    }
    
    // 改寫初始資料給每個cell
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        if let c = cell as? RestaurantStoreSelectTVCell {
            switch (indexPath.section) {
            case 0:
                c.item = self.food.food_data.scopes[indexPath.row]
                c.tag = indexPath.row
            case 1:
                c.item = self.food.food_data.opts[indexPath.row]
                c.tag = indexPath.row
            case 2...:
                c.item = self.food.food_data.demands[indexPath.section - 2].datas[indexPath.row]
                c.tag = indexPath.row
            default:
                break
            }
            c.cellWillAppear()
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: UIIdentifier.CELL.rawValue, for: indexPath) as! RestaurantStoreSelectTVCell
        
        switch (indexPath.section) {
        case 0:
            cell.radioButton.isSelected = self.itemVo.scopes[0].equal(item: self.food.food_data.scopes[indexPath.row])
        case 1:
            cell.radioButton.isSelected = self.itemVo.opts.contains { opt -> Bool in
                return opt.equal(item: self.food.food_data.opts[indexPath.row])
            }
        case 2...:
            cell.radioButton.isSelected = self.itemVo.demands[indexPath.section - 2].datas[0].equal(item: self.food.food_data.demands[indexPath.section - 2].datas[indexPath.row])
        default:
            break
        }
        cell.radioButton.tag = indexPath.section
        cell.radioButton.imageView?.tag = indexPath.row
        cell.triggerRadioStatus(cell.radioButton.isSelected)
        return cell
    }

    
    // 點擊事件
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        let cell : RestaurantStoreSelectTVCell = (tableView.cellForRow(at: indexPath) as? RestaurantStoreSelectTVCell)!
        switch (indexPath.section) {
        case 0: // 必選 單選 規格
            self.itemVo.scopes.removeAll()
            self.itemVo.scopes.append(cell.item)
        case 1: // 多選 追加項目
            var index: Int!
            for i in 0..<self.itemVo.opts.count {
                if self.itemVo.opts[i].equal(item: cell.item) {
                    index = i
                }
            }
            if index != nil {
                self.itemVo.opts.remove(at: index)
            }else {
                self.itemVo.opts.append(cell.item)
            }
        case 2...: // 必選多需求
            self.itemVo.demands[indexPath.section - 2].datas.removeAll()
            self.itemVo.demands[indexPath.section - 2].datas.append(cell.item)
        default:
            break
        }
        self.calculatMoney()
        tableView.reloadData()

    }
    
    @IBAction func radioBtnSelected (_ sender: UIButton){
        self.tableView.selectRow(at: IndexPath(row: (sender.imageView?.tag)!, section: sender.tag), animated: true, scrollPosition: .none)
        self.tableView.delegate?.tableView!(self.tableView, didSelectRowAt: IndexPath(row: (sender.imageView?.tag)!, section: sender.tag))
    }
    
    func calculatMoney(){
        let orderNumber = self.orderNumber.text
        let number:Int = Int (orderNumber!)!
        
        var selectMoney: Int = 0
        for scopes in self.itemVo.scopes {
            selectMoney += Int(scopes.price)!
        }
        for opt in self.itemVo.opts {
            selectMoney += Int(opt.price)!
        }
        
        selectMoney = selectMoney * number
        self.orderTotal.text = String(selectMoney)
    }
}


extension CALayer {
    
    func addBorder(edge: UIRectEdge, color: UIColor, thickness: CGFloat) {
        
        let border = CALayer()
        let screenSize: CGRect = UIScreen.main.bounds
        
        switch edge {
        case UIRectEdge.top:
            border.frame = CGRect(x:0, y:0, width:screenSize.width , height:thickness)
            break
        case UIRectEdge.bottom:
            border.frame = CGRect(x:0, y:frame.size.height - thickness, width:frame.size.width, height:thickness)
            break
        case UIRectEdge.left:
            border.frame = CGRect(x:0, y:0, width:thickness, height:frame.size.height)
            break
        case UIRectEdge.right:
            border.frame = CGRect(x:frame.size.width - thickness, y:0, width:thickness, height:frame.size.height)
            break
        default:
            break
        }
        
        border.backgroundColor = color.cgColor
        
        self.addSublayer(border)
    }
    
}
