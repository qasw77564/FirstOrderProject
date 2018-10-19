//
//  ShoppingCarMainViewController.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/5/28.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit


class ShoppingCarMainVC: UIViewController, UITableViewDataSource, UITableViewDelegate {
    
    // 使用者購物車資料 使用外部物件操控
    // Model.USER_CACHE_SHOPPING_CART
    
    @IBOutlet weak var tableView: UITableView! {
        didSet {
            self.tableView.delegate = self
            self.tableView.dataSource = self
            let refreshControl: UIRefreshControl = UIRefreshControl()
            refreshControl.attributedTitle = NSAttributedString(string: "Pull to refresh", attributes: [NSAttributedStringKey.foregroundColor: UIColor(red: 188/255, green: 188/255, blue: 188/255, alpha: 1.0)])
            refreshControl.addTarget(self, action: #selector(refresh), for: .valueChanged)
            refreshControl.tintColor = UIColor.clear
            self.tableView.addSubview(refreshControl)
        }
    }
    
    @objc func refresh(sender: UIRefreshControl){
        Loading.show()
        sender.endRefreshing()
        UserSstorage.setShoppingCartDatas(datas: Model.USER_CACHE_SHOPPING_CART)
        Model.USER_CACHE_SHOPPING_CART.removeAll()
        Model.USER_CACHE_SHOPPING_CART.append(contentsOf: UserSstorage.getShoppingCartDatas())
        self.tableView.reloadData()
        Loading.hide()
    }

    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        Model.USER_CACHE_SHOPPING_CART.removeAll()
        Model.USER_CACHE_SHOPPING_CART.append(contentsOf: UserSstorage.getShoppingCartDatas())
        self.tableView.reloadData()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    // Main tableView 先計算高度 給 main cell ， sun tableView 才能正常顯示出來
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return (CGFloat(Model.USER_CACHE_SHOPPING_CART[indexPath.row].orders.count * 100 + 109))
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return Model.USER_CACHE_SHOPPING_CART.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cellIdentifier = "OrderCell"
        let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as! ShoppingCarMainTVCell
        cell.tag = indexPath.row
        cell.storeName.text = Model.USER_CACHE_SHOPPING_CART[indexPath.row].restaurant_name
        cell.cancelBtn.tag = indexPath.row
        cell.cancelBtn.addTarget(self, action: #selector(cancelOrder), for: .touchUpInside)
        cell.submitBtn.tag = indexPath.row
        cell.submitBtn.addTarget(self, action: #selector(submitOrder), for: .touchUpInside)
        cell.cellWillAppear()
        return cell
    }
    
    
    // 利用 UIButton內的多個View元素來分別儲存 root index & sub index
    @IBAction func deleteFoodReload(_ sender: UIButton) {
        let rootIndex: Int = (sender.imageView?.tag)!
        let subIndex: Int = sender.tag
        if Model.USER_CACHE_SHOPPING_CART[rootIndex].orders.count > 1 {
            Model.USER_CACHE_SHOPPING_CART[rootIndex].orders.remove(at: subIndex)
        }else {
            let alert = UIAlertController(title: Optional.none, message: "剩下最後一筆菜單，無法刪除。\n若要刪除，請直接點選 \"取消訂單\"", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "我知道了", style: .default))
            self.present(alert, animated: false)
        }
        
        UserSstorage.setShoppingCartDatas(datas: Model.USER_CACHE_SHOPPING_CART)
        Model.USER_CACHE_SHOPPING_CART.removeAll()
        Model.USER_CACHE_SHOPPING_CART.append(contentsOf: UserSstorage.getShoppingCartDatas())
        self.tableView.reloadData()
    }
    
    @IBAction func countFoodReload(_ sender: UIStepper) {
        Model.USER_CACHE_SHOPPING_CART.removeAll()
        Model.USER_CACHE_SHOPPING_CART.append(contentsOf: UserSstorage.getShoppingCartDatas())
        self.tableView.reloadData()
    }

    @objc func submitOrder(_ sender: UIButton){
        let price: Int =  Model.USER_CACHE_SHOPPING_CART[sender.tag].orders.reduce(0, { (sum, num) -> Int in
            return sum + Int(num.item.price)!
        })
        // 訂單種額大於 5000 不給提交
        if price > 5000 {
            let alert = UIAlertController(title: Optional.none, message:"單筆訂單不可超過 5000，\n請從重新調整您的訂單內容！", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "我知道了", style: .default))
            self.present(alert, animated: false)
        }else {
            if let vc = UIStoryboard(name: UIIdentifier.USER.rawValue, bundle: nil).instantiateViewController(withIdentifier: "SubmitOrder") as? SubmitOrderVC {
                vc.orderIndex = sender.tag
                self.navigationController?.pushViewController(vc, animated: true)
            }
        }
    }

    @objc func cancelOrder(_ sender: UIButton){
        let alert = UIAlertController(title: Optional.none, message: "確定是否刪除此訂單!!", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "返回", style: .default))
        alert.addAction(UIAlertAction(title: "刪除", style: .default){_ in
            Model.USER_CACHE_SHOPPING_CART.remove(at: sender.tag)
            UserSstorage.setShoppingCartDatas(datas: Model.USER_CACHE_SHOPPING_CART)
            self.tableView.reloadData()
        })
        self.present(alert, animated: false)
    }
    
    // 攔截 submitOrder
    override func show(_ vc: UIViewController, sender: Any?) {
    }
}


