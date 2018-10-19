//
//  LoyaltyCardActionVC.swift
//  Naber
//
//  Created by 王淳彦 on 2018/10/12.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class LoyaltyCardActionVC: UIViewController, UITableViewDataSource, UITableViewDelegate {
    
    var IS_STORE_DETAIL: Bool = false

    @IBOutlet weak var storeName: UILabel! {
        didSet {
            self.storeName.isHidden = IS_STORE_DETAIL
        }
    }
    
    @IBOutlet weak var tableView: UITableView! {
        didSet {
            self.tableView.dataSource = self
            self.tableView.delegate = self
            let refreshControl: UIRefreshControl = UIRefreshControl()
            refreshControl.attributedTitle = NSAttributedString(string: "Pull to refresh", attributes: [NSAttributedStringKey.foregroundColor: UIColor(red: 188/255, green: 188/255, blue: 188/255, alpha: 1.0)])
            refreshControl.addTarget(self, action: #selector(refresh), for: .valueChanged)
            refreshControl.tintColor = UIColor.clear
            self.tableView.addSubview(refreshControl)
            
            let gestureRecognizer: UITapGestureRecognizer = UITapGestureRecognizer.init(target: self, action: #selector(hideKeyboard))
            gestureRecognizer.numberOfTapsRequired = 1
            gestureRecognizer.cancelsTouchesInView = false
            self.tableView.addGestureRecognizer(gestureRecognizer)
        }
    }
    
    // UITableView click bk hide keyboard
    @objc func hideKeyboard(sender: Any){
        self.view.endEditing(true)
    }
    
    @objc func refresh(sender: UIRefreshControl){
        self.loadData(refresh: true){
            sender.endRefreshing()
            self.tableView.reloadData()
        }
    }
    
    func loadData(refresh: Bool, complete: @escaping() -> ()){
        complete()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    
    
    @IBAction func exchangeAction (_ sender: UIButton){
        print("exchangeAction")
    }
    
    // table View
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 13
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: UIIdentifier.CELL.rawValue, for: indexPath) as! LoyaltyCardActionTVCell
        
        cell.name.isHidden = IS_STORE_DETAIL
        cell.exchangeBtn.isHidden = indexPath.row % 2 == 0
//        cell.content.text = ""
//        cell.loyaltyStatus.left
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
       
    }
    
    
    
}
