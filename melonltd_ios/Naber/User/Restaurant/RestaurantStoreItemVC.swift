//
//  RestaurantStoreItemViewController.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/6/1.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class RestaurantStoreItemVC: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    var categoryRel : CategoryRelVo!
    var restaurantInfo : RestaurantInfoVo!
    var foodList: [FoodVo?] = []
    
    @IBOutlet weak var categoryName: UILabel!
    @IBOutlet weak var tableView: UITableView! {
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
        self.loadData(refresh: true)
    }
    
    
    func loadData(refresh: Bool) {
        if refresh {
            self.foodList.removeAll()
            self.tableView.reloadData()
        }
        
        if self.categoryRel != nil {
            self.categoryName.text = self.categoryRel.category_name
            let uuid : String = self.categoryRel.category_uuid
            ApiManager.restaurantFoodList(uuid: uuid, ui: self, onSuccess: { foods in
                let sortedArray = foods.sorted(by: { (o1, o2) -> Bool in
                    Int(o1.top)! < Int(o2.top)!
                })
                self.foodList.append(contentsOf: sortedArray)
                self.tableView.reloadData()
            }) { err_msg in
                print(err_msg)
            }
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        self.loadData(refresh: true)
    }
    
    override func show(_ vc: UIViewController, sender: Any?) {
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.foodList.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: UIIdentifier.CELL.rawValue, for: indexPath) as! RestaurantStoreItemTVCell
        
        cell.name.text = self.foodList[indexPath.row]?.food_name
        cell.price.text = "$ " + (self.foodList[indexPath.row]?.default_price)!
        cell.photo.image = UIImage(named: "Logo")
        if self.foodList[indexPath.row]?.photo != nil {
            cell.photo.setImage(with: URL(string: (self.foodList[indexPath.row]?.photo)!), transformer: TransformerHelper.transformer(identifier: (self.foodList[indexPath.row]?.photo)!))
        }else {
            cell.photo.image = UIImage(named: "Logo")
        }
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let storyBoard: UIStoryboard = UIStoryboard(name: UIIdentifier.USER.rawValue, bundle: nil)
        let vc = storyBoard.instantiateViewController(withIdentifier: "RestaurantStoreSelect") as! RestaurantStoreSelectVC
        
        ApiManager.restaurantFoodDetail(uuid: (self.foodList[indexPath.row]?.food_uuid)!, ui: self, onSuccess: { food in
            self.foodList[indexPath.row] = food
            vc.food = self.foodList[indexPath.row]
            vc.restaurantInfo = self.restaurantInfo
            vc.categoryRel = self.categoryRel
            self.navigationController?.pushViewController(vc, animated: true)            
        }) { err_msg in
            
        }
    }

}

