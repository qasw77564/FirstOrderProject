//
//  DailyOpenScheduleVC.swift
//  Naber
//
//  Created by 王淳彦 on 2018/10/11.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit


class SailyOpenScheduleVC : UIViewController, UICollectionViewDelegate, UICollectionViewDataSource,UICollectionViewDelegateFlowLayout {
    
    
    var threeBusinessDate: [String] = []
    var restaurant: RestaurantInfoVo! = RestaurantInfoVo()
    var NOW_WEEK_DAY: Int = 0
    var NOW_MONTH_DAY: Int = 0
    var NOW_MONTH_COUNT: Int = 0
    
    @IBOutlet weak var collectionView: UICollectionView! {
        didSet {
            
            self.collectionView.dataSource = self
            self.collectionView.delegate = self
            let refreshControl: UIRefreshControl = UIRefreshControl()
            refreshControl.attributedTitle = NSAttributedString(string: "Pull to refresh", attributes: [NSAttributedStringKey.foregroundColor: UIColor(red: 188/255, green: 188/255, blue: 188/255, alpha: 1.0)])
            refreshControl.addTarget(self, action: #selector(refresh), for: .valueChanged)
            refreshControl.tintColor = UIColor.clear
            self.collectionView.addSubview(refreshControl)
            
            let gestureRecognizer: UITapGestureRecognizer = UITapGestureRecognizer.init(target: self, action: #selector(hideKeyboard))
            gestureRecognizer.numberOfTapsRequired = 1
            gestureRecognizer.cancelsTouchesInView = false
            self.collectionView.addGestureRecognizer(gestureRecognizer)
        }
    }
    
    // UITableView click bk hide keyboard
    @objc func hideKeyboard(sender: Any){
        self.view.endEditing(true)
    }
    
    @objc func refresh(sender: UIRefreshControl){
        self.loadData(refresh: true) {
            sender.endRefreshing()
            self.collectionView.reloadData()
        }
    }
    
    func loadData(refresh: Bool, complete: @escaping () -> ()){
        self.threeBusinessDate.removeAll()
        self.NOW_MONTH_DAY = Calendar.current.component(.day, from: Date())
        self.NOW_MONTH_COUNT = Calendar(identifier: .gregorian).maximumRange(of: .day)!.count
        self.NOW_WEEK_DAY = (Calendar.current.component(.weekday, from: Date()) - 1)
        for _ in 0 ..< self.NOW_WEEK_DAY {
            self.threeBusinessDate.append("")
        }
        
        for i in 0..<30{
            self.threeBusinessDate.append(DateTimeHelper.startOfDate(day: i))
        }
        
        ApiManager.sellerRestaurantInfo(ui: self, onSuccess: { restaurant in
            self.restaurant = restaurant
            complete()
        }) { err_msg in
            print(err_msg)
            complete()
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.loadData(refresh: true) {
            self.collectionView.reloadData()
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return self.threeBusinessDate.count
    }
    
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: UIIdentifier.CELL.rawValue, for: indexPath) as! DailyOpenScheduleCVCell
        
        cell.day.text = ""
        cell.day.textColor = .white
        cell.backgroundColor = .white
        
        if !self.threeBusinessDate[indexPath.row].elementsEqual("") {
            cell.day.text = DateTimeHelper.formToString(date: self.threeBusinessDate[indexPath.row], from: "dd")
            let status: Bool = self.restaurant.not_business.contains(self.threeBusinessDate[indexPath.row])
            
            cell.day.textColor = .darkGray
            cell.backgroundColor = .white

            
            if indexPath.row > (self.NOW_MONTH_COUNT - (self.NOW_MONTH_DAY - self.NOW_WEEK_DAY )) {
                cell.backgroundColor = UIColor.lightGray
            }
            
            if indexPath.row % 7 == 0 {
                 cell.day.textColor = NaberConstant.COLOR_BASIS_RED
            }
            
            if (indexPath.row + 1) % 7 == 0 {
                cell.day.textColor = NaberConstant.COLOR_BASIS_BLUE
            }
            
            if status {
                cell.day.textColor = .white
                cell.backgroundColor = NaberConstant.COLOR_BASIS_RED
            }
        }
        
        return cell
    }
    
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        
        if indexPath.row < self.NOW_WEEK_DAY {
            return
        }
        
        let status: Bool = self.restaurant.not_business.contains(self.threeBusinessDate[indexPath.row])
        let req: ReqData = ReqData()
        req.date = self.threeBusinessDate[indexPath.row]
        req.status = status ? "OPEN" : "CLOSE"
        
        if !status {
            let alert = UIAlertController(title: Optional.none, message: "關閉營業時段，請先確認該日有無訂單，若有訂單請記得告知使用者。", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "確定", style: .default){ _ in
                self.changeStoerOpenDate(status: status,req: req)
            })
            alert.addAction(UIAlertAction(title: "取消", style: .destructive){ _ in
            })
            
            self.present(alert, animated: false)
        } else {
            self.changeStoerOpenDate(status: status, req: req)
        }
    }
    
    func changeStoerOpenDate (status: Bool, req: ReqData){
        ApiManager.sellerRestaurantSettingBusiness(req: req, ui: self, onSuccess: {
            if !status {
                self.restaurant.not_business.append(req.date)
            }else {
                if let index = self.restaurant.not_business.index(of: req.date) {
                    self.restaurant.not_business.remove(at: index)
                }
            }
            self.collectionView.reloadData()
        }) { err_msg in
            self.collectionView.reloadData()
        }
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumInteritemSpacingForSectionAt section: Int) -> CGFloat {
        return 0
    }
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 0
    }
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        let width = collectionView.frame.width / 7
        
        if UIDevice.current.model.range(of: "iPad") != nil{
            return CGSize(width: width, height: 50)
        }
        return CGSize(width: width, height: width)
    }
   
}

