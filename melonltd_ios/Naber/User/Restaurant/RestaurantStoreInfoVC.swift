//
//  RestaurantStoreInfoViewController.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/5/26.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit
import CoreLocation

class RestaurantStoreInfoVC: UIViewController, UITableViewDataSource, UITableViewDelegate , CLLocationManagerDelegate{
    
    var categoryList: [CategoryRelVo?] = []
    var LM : CLLocationManager!; //座標管理元件
    var restaurantIndex : Int! = Optional.none
    var pageType: PageType = .NONE
//    var username: String = ""
    var restaurantInfo: RestaurantInfoVo! = Optional.none
    
    @IBOutlet weak var loyaltyBtn: UIButton! {
        didSet {
            self.loyaltyBtn.alpha = 0.0
            self.loyaltyBtn.isEnabled = false
        }
    }
    @IBOutlet weak var loyaltyImage: UIImageView! {
        didSet{
            self.loyaltyImage.alpha = 0.0
        }
    }
    @IBOutlet weak var name: UILabel!
    @IBOutlet weak var time: UILabel!
    @IBOutlet weak var address: UILabel!
    @IBOutlet weak var workStatus: UILabel!
    @IBOutlet weak var backgroundPhoto: UIImageView!
    @IBOutlet weak var distance: UILabel!
    @IBOutlet weak var photo: UIImageView!
//    @IBOutlet weak var bulletin: UILabel!
    @IBOutlet weak var bulletin: UITextView!
    
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
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // 確認 GPS 權限
        self.enableBasicLocationServices()
        
        switch self.pageType {
        case .HOME:
            if Model.TOP_RESTAURANT_LIST.count > self.restaurantIndex {
                self.restaurantInfo = Model.TOP_RESTAURANT_LIST[self.restaurantIndex]
            }
        case .RESTAURANT:
            if Model.TMPE_RESTAURANT_LIST.count > self.restaurantIndex {
                self.restaurantInfo = Model.TMPE_RESTAURANT_LIST[self.restaurantIndex]
            }
        case .AD:
            if Model.AD_RESTAURANT_LIST.count > self.restaurantIndex {
                self.restaurantInfo = Model.AD_RESTAURANT_LIST[self.restaurantIndex]
            }
            break
        case .NONE:
            break
        }
    }

    override func viewDidAppear(_ animated: Bool) {
        
        if self.restaurantInfo != nil {
            self.loadData(refresh: true)
            
            // TODO 
            // 判斷該餐館有無集點功能
//            if true {
//                self.loyaltyBtn.alpha = 1.0
//                self.loyaltyBtn.isEnabled = true
//                self.loyaltyImage.alpha = 1.0
//            }
            
            self.address.text = self.restaurantInfo.address
            self.name.text = self.restaurantInfo.name
            self.bulletin.text = self.restaurantInfo.bulletin
            
            self.time.text = self.restaurantInfo.store_start + " ~ " + self.restaurantInfo.store_end

            self.photo.setImage(with: URL(string: self.restaurantInfo.photo ?? ""), transformer: TransformerHelper.transformer(identifier: self.restaurantInfo.photo ?? ""),  completion: { image in
                if image == nil {
                    self.photo.image = UIImage(named: "Logo")
                }
            })
            
            self.backgroundPhoto.setImage(with: URL(string: self.restaurantInfo.background_photo ?? ""), transformer: TransformerHelper.transformer(identifier: self.restaurantInfo.background_photo ?? ""),  completion: { image in
                if image == nil {
                    self.backgroundPhoto.image = UIImage(named: "naber_default_image")
                }
            })
            
            
            if self.LM.location != nil {
                let lant: Double = Double(self.restaurantInfo.latitude)!
                let long: Double = Double(self.restaurantInfo.longitude)!
                let distance: Double = self.LM.location!.distance(from: CLLocation.init(latitude: lant, longitude: long))
                self.distance.text = String(format: "%.01f 公里", (distance / 1000) < 0.1 ? 0.1 : (distance / 1000))
                if distance / 1000 >= 1000 {
                    self.distance.text = String(format: "%.01f Mm", (distance / 1000 / 1000) < 0.1 ? 0.1 : (distance / 1000 / 1000))
                }
            } else {
                self.distance.text = ""
            }
            
            self.workStatus.textColor = UIColor.init(red: 234/255, green: 33/255, blue: 5/255, alpha: 1.0)
            if self.restaurantInfo.not_business.count > 0 {
                self.workStatus.text = "今日已結束接單"
            } else if self.restaurantInfo.is_store_now_open.uppercased() == "FALSE" {
                self.workStatus.text = "該商家尚未營業"
            } else {
                self.workStatus.text = "接單中"
                self.workStatus.textColor = UIColor.init(red: 128/255, green: 228/255, blue: 56/255, alpha: 1.0)
            }
            
            if self.restaurantInfo.isShowOne == nil {
                self.setWarning()
            }
        }
    }

    func setWarning(){
        if !self.restaurantInfo.is_store_now_open.isEmpty {
            self.workStatus.isHidden = true
            if self.restaurantInfo.not_business.count > 0 {
                self.workStatus.isHidden = false
                let alert = UIAlertController(title: Optional.none, message: "該商家今日已結束接單", preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "我知道了", style: .default))
                self.present(alert, animated: false)
            } else if self.restaurantInfo.is_store_now_open.uppercased().elementsEqual("FALSE") {
                self.workStatus.isHidden = false
                let alert = UIAlertController(title: Optional.none, message: "目前時間該商家尚未營業", preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "我知道了", style: .default))
                self.present(alert, animated: false)
            }
            self.restaurantInfo.isShowOne = false
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
    }
    
    func loadData(refresh: Bool){
        if refresh {
            self.categoryList.removeAll()
            self.tableView.reloadData()
        }
        if self.restaurantInfo != nil {
            let uuid: String = self.restaurantInfo.restaurant_uuid
            ApiManager.restaurantCategoryList(uuid: uuid, ui: self, onSuccess: { restaurantCategorys in
                let sortedArray = restaurantCategorys.sorted(by: { (o1, o2) -> Bool in
                    Int(o1.top)! < Int(o2.top)!
                })
                self.categoryList.append(contentsOf: sortedArray)
              
                self.tableView.reloadData()
            }, onFail: { err_msg in

            })
        }
    }

    override func show(_ vc: UIViewController, sender: Any?) {
        
    }
    
    // 到集點內容頁面
    @IBAction func loyaltyCardAction (_ sender: UIButton){
        if let vc = UIStoryboard(name: UIIdentifier.USER.rawValue, bundle: nil).instantiateViewController(withIdentifier: "LoyaltyCardAction") as? LoyaltyCardActionVC {
        
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.categoryList.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: UIIdentifier.CELL.rawValue, for: indexPath) as! RestaurantStoreInfoTVCell
        cell.name.text = self.categoryList[indexPath.row]?.category_name
        return cell
    }
    
//    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPa
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let storyBoard: UIStoryboard = UIStoryboard(name: UIIdentifier.USER.rawValue, bundle: nil)
        let vc = storyBoard.instantiateViewController(withIdentifier: "RestaurantStoreItem") as! RestaurantStoreItemVC
        vc.categoryRel = self.categoryList[indexPath.row]
        vc.restaurantInfo = self.restaurantInfo
        self.navigationController?.pushViewController(vc, animated: true)
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        //因為ＧＰＳ功能很耗電,所以被敬執行時關閉定位功能
        self.LM.stopUpdatingLocation();
    }
    
    func enableBasicLocationServices() {
        self.LM = CLLocationManager()
        self.LM.delegate = self
        switch CLLocationManager.authorizationStatus() {
        case .notDetermined:
            LM.requestWhenInUseAuthorization()
            break
        case .restricted, .denied:
            break
        case .authorizedWhenInUse, .authorizedAlways:
            LM.startUpdatingLocation()
            break
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus ) {
        switch status {
        case .restricted, .denied:
//            let alertController = UIAlertController( title: "定位權限已關閉", message: "我們無法幫您計算店家距離，\n如要開啟GPS權限，可以點\"前往設置\"，\n將位置權限開啟。", preferredStyle: .alert)
//            alertController.addAction(UIAlertAction(title: "確認", style: .default, handler:nil))
//            self.present(alertController, animated: true, completion: nil)
            break
        case .authorizedWhenInUse:
            self.LM.startUpdatingLocation()
            break
        case .notDetermined, .authorizedAlways:
            break
        }
    }
    
}

