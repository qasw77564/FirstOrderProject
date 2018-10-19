//
//  HomeUserTableTableViewController.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/5/26.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit
import NVActivityIndicatorView
import CoreLocation

class HomeMainVC: UIViewController,UITableViewDataSource, UITableViewDelegate ,FSPagerViewDataSource, FSPagerViewDelegate, CLLocationManagerDelegate{
    
    var LM : CLLocationManager!; //座標管理元件
    var location : CLLocation!
    var cycleBulletinView : LLCycleScrollView!
    
    
    @IBOutlet weak var adPagerView: FSPagerView! {
        didSet {
            self.adPagerView.register(FSPagerViewCell.self, forCellWithReuseIdentifier: "cell")
            self.adPagerView.itemSize = .zero
            self.adPagerView.automaticSlidingInterval = 3.0
            self.adPagerView.isInfinite = true
        }
    }
    
    @IBOutlet weak var adPageControl: FSPageControl! {
        didSet {
            self.adPageControl.numberOfPages = Model.ADVERTISEMENTS.count
            self.adPageControl.contentHorizontalAlignment = .center
            self.adPageControl.setPath(UIBezierPath(ovalIn: CGRect(x: 0, y: 0, width: 10, height: 10)), for: .normal)
            self.adPageControl.setPath(UIBezierPath(ovalIn: CGRect(x: 0, y: 0, width: 10, height: 10)), for: .selected)
            self.adPageControl.interitemSpacing = 12
            self.adPageControl.contentInsets = UIEdgeInsets(top: 0, left: 30, bottom: 10, right: 30)
        }
    }

    @IBOutlet weak var naberBulletinView: UIView!
    
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
 

    func loadData(refresh: Bool){
        if refresh {
            Model.TOP_RESTAURANT_LIST.removeAll()
            self.tableView.reloadData()
        }

        // 重新取得定位
        self.location = self.LM.location
        let req : ReqData = ReqData()
        req.search_type = "TOP"
        ApiManager.restaurantList(req: req, ui: self, onSuccess: { restaurantInfos in
            Model.TOP_RESTAURANT_LIST.append(contentsOf: restaurantInfos)
            self.tableView.reloadData()
        }) { err_msg in
            print(err_msg)
            self.tableView.reloadData()
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.configLLCycleScrollView()
        // 確認 GPS 權限
        enableBasicLocationServices()
        
        // 加載輪播圖
        ApiManager.advertisement(ui: self, onSuccess: { advertisements in
            Model.ADVERTISEMENTS.removeAll()
            Model.ADVERTISEMENTS.append(contentsOf: advertisements)
            self.adPageControl.numberOfPages = Model.ADVERTISEMENTS.count
            self.adPagerView.reloadData()
        }) { err_msg in
            print(err_msg)
        }
        
        ApiManager.bulletin(ui: self, onSuccess: { bulletins in
            Model.ALL_BULLETINS.removeAll()
            Model.NABER_BULLETINS.removeAll()
            bulletins.forEach({ b in
                Model.ALL_BULLETINS[(b?.bulletin_category)!] = b?.content_text
            })
            let naberBulletins: [String] = Model.ALL_BULLETINS["HOME"]!.components(separatedBy: "$split")
            Model.NABER_BULLETINS.append(contentsOf: naberBulletins)
//            Model.NABER_BULLETINS.append(contentsOf: ["1、TEXT ONE "])
            self.cycleBulletinView.titles = Model.NABER_BULLETINS
            self.cycleBulletinView.reloadInputViews()
        }) { err_msg in
            print(err_msg)
        }
        
        self.loadData(refresh: true)
    }
    
    
    func configLLCycleScrollView() {
        cycleBulletinView = LLCycleScrollView.llCycleScrollViewWithTitles(frame: CGRect.init(x: 0, y: 0, width: UIScreen.main.bounds.size.width, height: 40 )) { index in
        }
        cycleBulletinView.customPageControlStyle = .none
        cycleBulletinView.scrollDirection = .vertical
        cycleBulletinView.font = UIFont.systemFont(ofSize: 14)
        cycleBulletinView.textColor = UIColor.black
        cycleBulletinView.titleBackgroundColor = UIColor.white
        cycleBulletinView.numberOfLines = 2
        cycleBulletinView.autoScroll = false
        cycleBulletinView.infiniteLoop = true
        cycleBulletinView.autoScrollTimeInterval = 5.0
        cycleBulletinView.customPageControlIndicatorPadding = 0.0
        cycleBulletinView.pageControlPosition = .center
        cycleBulletinView.titles = Model.NABER_BULLETINS
        // 文本　Leading约束
        cycleBulletinView.titleLeading = 16
        self.naberBulletinView.addSubview(cycleBulletinView)
    }
    
   override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: UIIdentifier.CELL.rawValue, for: indexPath) as! RestaurantTVCell
        
        cell.name.text = Model.TOP_RESTAURANT_LIST[indexPath.row].name
        cell.address.text = Model.TOP_RESTAURANT_LIST[indexPath.row].address
        cell.time.text = Model.TOP_RESTAURANT_LIST[indexPath.row].store_start + " ~ " + Model.TOP_RESTAURANT_LIST[indexPath.row].store_end

        cell.photo.setImage(with: URL(string: Model.TOP_RESTAURANT_LIST[indexPath.row].photo ?? ""), transformer: TransformerHelper.transformer(identifier: Model.TOP_RESTAURANT_LIST[indexPath.row].photo ?? ""),  completion: { image in
            if image == nil {
                cell.photo.image = UIImage(named: "Logo")
            }
        })
        
        cell.workStatus.textColor = UIColor.init(red: 234/255, green: 33/255, blue: 5/255, alpha: 1.0)
        if Model.TOP_RESTAURANT_LIST[indexPath.row].not_business.count > 0 {
            cell.workStatus.text = "今日已結束接單"
        } else if Model.TOP_RESTAURANT_LIST[indexPath.row].is_store_now_open.uppercased().elementsEqual("FALSE") {
            cell.workStatus.text = "該商家尚未營業"
        } else {
            cell.workStatus.text = "接單中"
            cell.workStatus.textColor = UIColor.init(red: 128/255, green: 228/255, blue: 56/255, alpha: 1.0)
        }
        
        if self.location != nil {
            let lant: Double = Double(Model.TOP_RESTAURANT_LIST[indexPath.row].latitude)!
            let long: Double = Double(Model.TOP_RESTAURANT_LIST[indexPath.row].longitude)!
            let distance: Double = self.location!.distance(from: CLLocation.init(latitude: lant, longitude: long))
            cell.distance.text = String(format: "%.01f 公里", (distance / 1000) < 0.1 ? 0.1 : (distance / 1000))
            if distance / 1000 >= 1000 {
               cell.distance.text = String(format: "%.01f Mm", (distance / 1000 / 1000) < 0.1 ? 0.1 : (distance / 1000 / 1000))
            }
        } else {
            cell.distance.text = ""
        }
        return cell
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - Table view data source
    func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return Model.TOP_RESTAURANT_LIST.count
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if let vc = UIStoryboard(name: UIIdentifier.USER.rawValue, bundle: nil).instantiateViewController(withIdentifier: "RestaurantStoreInfo") as? RestaurantStoreInfoVC {
            vc.restaurantIndex = indexPath.row
            vc.pageType = .HOME
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }


    // MARK:- FSPagerView DataSource
    public func numberOfItems(in pagerView: FSPagerView) -> Int {
        return Model.ADVERTISEMENTS.count
    }
    
    public func pagerView(_ pagerView: FSPagerView, cellForItemAt index: Int) -> FSPagerViewCell {
        let cell = pagerView.dequeueReusableCell(withReuseIdentifier: "cell", at: index)
        
        // isPad insert pad_photo
        if UIDevice.current.model.range(of: "iPad") != nil{
            cell.imageView?.setImage(with: URL(string: Model.ADVERTISEMENTS[index].pad_photo ?? ""), transformer: TransformerHelper.transformer(identifier: Model.ADVERTISEMENTS[index].pad_photo ?? ""),  completion: { image in
                if image == nil {
                    cell.imageView?.image = UIImage(named: "naber_pad_default_image.png")
                }
            })
        } else {
            cell.imageView?.setImage(with: URL(string: Model.ADVERTISEMENTS[index].photo ?? ""), transformer: TransformerHelper.transformer(identifier: Model.ADVERTISEMENTS[index].photo ?? ""),  completion: { image in
                if image == nil {
                    cell.imageView?.image = UIImage(named: "naber_default_image.png")
                }
            })
        }
        
        cell.imageView?.contentMode = .scaleAspectFill
        cell.imageView?.clipsToBounds = true
        cell.textLabel?.text = ""
        return cell
    }
    
    // MARK:- FSPagerView Delegate
    func pagerView(_ pagerView: FSPagerView, didSelectItemAt index: Int) {
        pagerView.deselectItem(at: index, animated: true)
        pagerView.scrollToItem(at: index, animated: true)
        self.adPageControl.currentPage = index
        if UIDevice.current.model.range(of: "iPad") != nil{
            // Pad not click event
        } else {
            if let type: String = Model.ADVERTISEMENTS[index].link_type {
                if let to: String = Model.ADVERTISEMENTS[index].link_to {
                    switch type {
                    case "APP":
                        if let url: String = LinkToVo.parse(src: to)?.ios {
                            print(url)
                            if #available(iOS 10.0, *) {
                                UIApplication.shared.open(URL(string: url)!, options: [ : ], completionHandler: { success in
                                })
                            } else {
                                UIApplication.shared.openURL(URL(string: url)!)
                            }
                        }
                        break
                    case "WEB":
                        if #available(iOS 10.0, *) {
                            UIApplication.shared.open(URL(string: to)!, options: [ : ], completionHandler: { success in
                            })
                        } else {
                            UIApplication.shared.openURL(URL(string: to)!)
                        }
                        break
                    case "INSIDE":
                        Model.AD_RESTAURANT_LIST.removeAll()
                        let req: ReqData = ReqData()
                        req.search_type = "DISTANCE"
                        req.uuids = [to]
                        ApiManager.restaurantList(req: req, ui: self, onSuccess: { restaurants in
                            Model.AD_RESTAURANT_LIST.append(contentsOf: restaurants)
                            if let vc = UIStoryboard(name: UIIdentifier.USER.rawValue, bundle: nil).instantiateViewController(withIdentifier: "RestaurantStoreInfo") as? RestaurantStoreInfoVC {
                                vc.restaurantIndex = 0
                                vc.pageType = .AD
                                self.navigationController?.pushViewController(vc, animated: true)
                            }
                        }) { err_msg in
                            
                        }
                        break
                    default:
                        break
                    }
                }
            }
        }
    }
    
    func pagerViewDidScroll(_ pagerView: FSPagerView) {
        guard self.adPageControl.currentPage != pagerView.currentIndex else {
            return
        }
        // Or Use KVO with property "currentIndex"
        self.adPageControl.currentPage = pagerView.currentIndex
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        //因為ＧＰＳ功能很耗電,所以被敬執行時關閉定位功能
        self.LM.stopUpdatingLocation();
    }
    
    override func show(_ vc: UIViewController, sender: Any?) {
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
        case .restricted:
            let alert = UIAlertController( title: "GPS權限已關閉", message: "我們無法幫您計算店家距離，\n如要開啟GPS權限，可以點\"前往設置\"，\n將位置權限開啟。", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "前往設置", style: .default) { _ in
                let url = URL(string: UIApplicationOpenSettingsURLString)
                if UIApplication.shared.canOpenURL(url!){
                    UIApplication.shared.open(url!, options: [:])
                }
            })
            alert.addAction(UIAlertAction(title: "返回", style: .destructive))
            self.present(alert, animated: true, completion: nil)
            break
        case .denied:
            break
        case .authorizedWhenInUse:
            self.LM.startUpdatingLocation()
            self.location = self.LM.location
            self.tableView.reloadData()
            break
        case .notDetermined, .authorizedAlways:
            break
        }
    }
    
}
