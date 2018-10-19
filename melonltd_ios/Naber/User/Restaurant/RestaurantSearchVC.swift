//
//  RestaurantSearchViewController.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/5/26.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit
import CoreLocation


class RestaurantSearchVC: UIViewController, UITableViewDataSource, UITableViewDelegate , CLLocationManagerDelegate ,UIPickerViewDelegate, UIPickerViewDataSource {

    var LM : CLLocationManager!;
    var location : CLLocation!
    var reqData: ReqData! = Optional.none
    var templates: [[String]] = []
    var uiButtons: [UIButton] = []
    var schoolDivideds: [SchoolDividedVo] = []
    var DIVIDED_INDEX: Int = 0
    var SCHOOL_INDEX: Int = 0
    var INIT_STATUS: Bool = true
    
    
    @IBOutlet weak var storeNameBtn: UIButton!{
        didSet {
            self.storeNameBtn.isEnabled = false
        }
    }
    @IBOutlet weak var otherStoreBtn: UIButton! {
        didSet {
            self.otherStoreBtn.isEnabled = false
        }
    }
    @IBOutlet weak var schoolBtn: UIButton! {
        didSet {
            self.schoolBtn.isEnabled = false
        }
    }
    @IBOutlet weak var distanceBtn: UIButton!
    
    
    var schoolText: UITextField!
    
    var schoolToolbar: UIToolbar {
        get {
            let toolbar = UIToolbar(frame: CGRect(x: 0, y: 0, width: view.frame.width, height: 44))
            let doneBtn = UIBarButtonItem(title: "確定" , style: .done, target: self, action: #selector(onDoneBtn))
            let cancelBtn = UIBarButtonItem(title: "取消" , style: .plain, target: self, action: #selector(onCancelBtn))
            let space = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
            toolbar.items = [cancelBtn, space, doneBtn]
            toolbar.barTintColor = UIColor.white
            return toolbar
        }
    }
    var schoolPicker: UIPickerView {
        get {
            let pickerView = UIPickerView()
            pickerView.dataSource = self
            pickerView.delegate = self
            pickerView.backgroundColor = UIColor.white
            return pickerView
        }
    }
    
    @objc func onDoneBtn(sender: UIBarButtonItem) {
        self.reqData.search_type = "SCHOOL_DIVIDED";
        self.reqData.category = ""
        self.reqData.name = self.schoolDivideds[self.DIVIDED_INDEX].schools[self.SCHOOL_INDEX]
        self.reqData.area = self.schoolDivideds[self.DIVIDED_INDEX].area
        self.reqData.uuids = []
        self.loadData(refresh: true)
        self.setButtonsDefaultColor(sender: self.schoolBtn)
        self.view.endEditing(true)
    }
    
    @objc func onCancelBtn(sender: UIBarButtonItem) {
        self.view.endEditing(true)
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
        }
    }
    
    @objc func refresh(sender: UIRefreshControl){
        sender.endRefreshing()
        self.loadData(refresh: true)
    }
    
    func loadData(refresh: Bool){
        if refresh {
            Model.TMPE_RESTAURANT_LIST.removeAll()
            self.tableView.reloadData()
            self.reqData.page = 0
            self.reqData.loadingMore = true
        }
        
        self.reqData.loadingMore = false
        if self.reqData != nil {
            self.reqData.page = self.reqData.page + 1
            if self.reqData.search_type.elementsEqual("DISTANCE") {
                if self.LM.location != nil {
                    self.reqData.uuids = []
                    if self.templates.count > self.reqData.page - 1 {
                        self.reqData.uuids.append(contentsOf: self.templates[self.reqData.page - 1])
                    }
                }
            }
            ApiManager.restaurantList(req: self.reqData, ui: self, onSuccess: { restaurantInfos in
                if self.reqData.search_type.elementsEqual("DISTANCE") {
                    restaurantInfos.forEach{ tmp in
                        let lant: Double = Double(tmp.latitude)!
                        let long: Double = Double(tmp.longitude)!
                        let distance: Double = self.location.distance(from: CLLocation.init(latitude: lant, longitude: long))
                        tmp.distance = distance
                    }
                    var list = restaurantInfos
                    list.sort(by: { (o1, o2) -> Bool in
                        return o1.distance < o2.distance
                    })
                    
                    Model.TMPE_RESTAURANT_LIST.append(contentsOf: list)
                    self.reqData.loadingMore = self.templates.count > self.reqData.page;
                    if self.INIT_STATUS {
                        self.storeNameBtn.isEnabled = true
                        self.otherStoreBtn.isEnabled = true
                        self.schoolBtn.isEnabled = true
                        self.INIT_STATUS = false
                    }
                }else {
                    Model.TMPE_RESTAURANT_LIST.append(contentsOf: restaurantInfos)
                    self.reqData.loadingMore = restaurantInfos.count % NaberConstant.PAGE == 0 && restaurantInfos.count != 0
                }
                self.tableView.reloadData()
            }) { err_msg in
                self.tableView.reloadData()
            }
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.reqData = ReqData()
        
        self.schoolText = UITextField(frame: CGRect.zero)
        self.view.addSubview(self.schoolText)
        
        self.schoolText.inputView = self.schoolPicker
        self.schoolText.inputAccessoryView = self.schoolToolbar
        
        self.schoolDivideds.append(contentsOf: SchoolDividedVo.parseArray(src: NaberConstant.SCHOOL_DIVIDED_TEMP))
        ApiManager.getSchoolDivides(ui: self, onSuccess: { schoolDivides in
            self.schoolDivideds.removeAll()
            self.schoolDivideds.append(contentsOf: schoolDivides)
        }) { err_msg in
            
        }
        self.searchForDistance(self.distanceBtn)
        self.uiButtons.append(contentsOf: [self.distanceBtn, self.otherStoreBtn, self.schoolBtn, self.storeNameBtn])
    }

    @IBAction func searchForDistance (_ sender: UIButton){
            self.enableBasicLocationServices()
            if self.LM.location != nil {
                self.reqData.search_type = "DISTANCE";
                self.reqData.category = ""
                self.reqData.area = ""
                self.reqData.name = ""
                self.reqData.loadingMore = false
                self.reqData.uuids = []
                self.templates = []
                
                self.setButtonsDefaultColor(sender: sender)
                self.location = self.LM.location!
                ApiManager.restaurantTemplate(ui: self, onSuccess: { tmps in
                    tmps.forEach{ tmp in
                        let lant: Double = Double(tmp!.latitude)!
                        let long: Double = Double(tmp!.longitude)!
                        let distance: Double = self.location.distance(from: CLLocation.init(latitude: lant, longitude: long))
                        tmp?.distance = distance
                    }
                    var list = tmps
                    list.sort(by: {(o1, o2) -> Bool in
                        return o1!.distance < o2!.distance
                    })
                    
                    var uuids: [String] = []
                    for index in 0..<list.count {
                        if index % 10 != 0 || index == 0 {
                            uuids.append((list[index]?.restaurant_uuid)!)
                        } else if index % 10 == 0 && index != 0 {
                            self.templates.append(uuids)
                            uuids = []
                            uuids.append((list[index]?.restaurant_uuid)!)
                        }
                        if index == list.count - 1 {
                            self.templates.append(uuids)
                        }
                    }
                    self.reqData.uuids.append(contentsOf: self.templates[0])
                    self.loadData(refresh: true)
                }) { err_msg in
                
                }
    }
}
    
    // 依照選取區域名稱查找
    // 2018/10/05 改為查詢其它店家，店家為非學餐店家
    @IBAction func searchForOtherStore (_ sender: UIButton){
//        let alert = UIAlertController(title: Optional.none, message: Optional.none, preferredStyle: .actionSheet)
////        alert.addAction(UIAlertAction(title: "請選擇區域", style: .destructive))
//        NaberConstant.FILTER_AREAS.forEach{ name in
//            let itemAction = UIAlertAction(title: name, style: .default) { itemAction in
//                self.setButtonsDefaultColor(sender: sender)
//                self.reqData.search_type = "AREA"
//                self.reqData.area = name
//                self.reqData.name = ""
//                self.reqData.category = ""
//                self.reqData.uuids = []
//                self.loadData(refresh: true)
//            }
//            alert.addAction(itemAction)
//        }
//        alert.addAction(UIAlertAction(title: "取消", style: .destructive))
//        alert.popoverPresentationController?.sourceView = self.view
//        alert.popoverPresentationController?.sourceRect = CGRect.init(x: self.view.bounds.width/2 ,y: self.view.bounds.height , width: 1.0, height: 1.0)
//
//        self.present(alert, animated: false, completion: nil)
        
        
        
        self.setButtonsDefaultColor(sender: sender)
        self.reqData.search_type = "NOT_SCHOOL"
        self.reqData.area = ""
        self.reqData.name = ""
        self.reqData.category = ""
        self.reqData.uuids = []
        self.loadData(refresh: true)
    }
    
    
    // 依照選取種類名稱查找
    // 2018/10/05 改為查詢選擇學校，二級列表搜尋
    @IBAction func searchForSchool (_ sender: UIButton){
        self.schoolText.inputView = self.schoolPicker
        self.DIVIDED_INDEX = 0
        self.SCHOOL_INDEX = 0
        self.schoolText.becomeFirstResponder()
//        self.schoolText.accessibilityElementDidBecomeFocused()
//        becomeFirstResponder
        
//        let alert = UIAlertController(title: Optional.none, message: Optional.none, preferredStyle: .actionSheet)
////        alert.addAction(UIAlertAction(title: "請選擇種類", style: .destructive))
//        NaberConstant.FILTER_CATEGORYS.forEach{ name in
//            let itemAction = UIAlertAction(title: name, style: .default) { itemAction in
//                self.setButtonsDefaultColor(sender: sender)
//                self.reqData.search_type = "CATEGORY";
//                self.reqData.category = name
//                self.reqData.name = ""
//                self.reqData.area = ""
//                self.reqData.uuids = []
//                self.loadData(refresh: true)
//            }
//            alert.addAction(itemAction)
//        }
//        alert.addAction(UIAlertAction(title: "取消", style: .destructive))
//        alert.popoverPresentationController?.sourceView = self.view
//        alert.popoverPresentationController?.sourceRect = CGRect.init(x: self.view.bounds.width/2 ,y: self.view.bounds.height , width: 1.0, height: 1.0)
//        self.present(alert, animated: false, completion: nil)

    }
    
    // 依照輸入店家名稱查找
    @IBAction func searchForStoreName (_ sender: UIButton){
        var foodName: UITextField!
        
        let alert = UIAlertController( title: Optional.none, message: "請輸入查詢店家名稱", preferredStyle: .alert)
        alert.addTextField{ textField in
            textField.placeholder = "店家名稱"
            textField.leftView = UIView(frame: CGRect(x: 0, y: 0, width: 10, height: 10))
            textField.addConstraint(textField.heightAnchor.constraint(equalToConstant: 36))
            textField.font?.withSize(30)
            foodName = textField
        }
        
        alert.addAction(UIAlertAction(title: "取消", style: .destructive, handler:nil))
        alert.addAction(UIAlertAction(title: "確定", style: .default, handler: { _ in
            self.setButtonsDefaultColor(sender: sender)
            self.reqData.search_type = "STORE_NAME";
            self.reqData.name = foodName.text
            self.reqData.category = ""
            self.reqData.area = ""
            self.reqData.uuids = []
            if !self.reqData.name.elementsEqual("") {
                self.loadData(refresh: true)
            }else {
                Model.TMPE_RESTAURANT_LIST.removeAll()
                self.tableView.reloadData()
            }
        }))
        
        self.present(alert, animated: true, completion: nil)

    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func show(_ vc: UIViewController, sender: Any?) {
    }
    
    // TableView
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return Model.TMPE_RESTAURANT_LIST.count
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if let vc = UIStoryboard(name: UIIdentifier.USER.rawValue, bundle: nil).instantiateViewController(withIdentifier: "RestaurantStoreInfo") as? RestaurantStoreInfoVC {
            vc.restaurantIndex = indexPath.row
            vc.pageType = .RESTAURANT
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        
        let cell = tableView.dequeueReusableCell(withIdentifier: UIIdentifier.CELL.rawValue, for: indexPath) as! RestaurantTVCell
        
        cell.name.text = Model.TMPE_RESTAURANT_LIST[indexPath.row].name
        cell.address.text = Model.TMPE_RESTAURANT_LIST[indexPath.row].address
        cell.time.text = Model.TMPE_RESTAURANT_LIST[indexPath.row].store_start + " ~ " + Model.TMPE_RESTAURANT_LIST[indexPath.row].store_end
        
        cell.photo.setImage(with: URL(string:  Model.TMPE_RESTAURANT_LIST[indexPath.row].photo ?? ""), transformer: TransformerHelper.transformer(identifier:  Model.TMPE_RESTAURANT_LIST[indexPath.row].photo ?? ""),  completion: { image in
            if image == nil {
                cell.photo.image = UIImage(named: "Logo")
            }
        })
      
        cell.workStatus.textColor = UIColor.init(red: 234/255, green: 33/255, blue: 5/255, alpha: 1.0)
        if Model.TMPE_RESTAURANT_LIST[indexPath.row].not_business.count > 0 {
            cell.workStatus.text = "今日已結束接單"
        } else if Model.TMPE_RESTAURANT_LIST[indexPath.row].is_store_now_open.uppercased().elementsEqual("FALSE") {
            cell.workStatus.text = "該商家尚未營業"
        } else {
            cell.workStatus.text = "接單中"
            cell.workStatus.textColor = UIColor.init(red: 128/255, green: 228/255, blue: 56/255, alpha: 1.0)
        }
        
        if self.location != nil {
            let lant: Double = Double(Model.TMPE_RESTAURANT_LIST[indexPath.row].latitude)!
            let long: Double = Double(Model.TMPE_RESTAURANT_LIST[indexPath.row].longitude)!
            let distance: Double = self.location!.distance(from: CLLocation.init(latitude: lant, longitude: long))
            cell.distance.text = String(format: "%.01f 公里", (distance / 1000) < 0.1 ? 0.1 : (distance / 1000))
            
            if distance / 1000 >= 1000 {
                cell.distance.text = String(format: "%.01f Mm", (distance / 1000 / 1000) < 0.1 ? 0.1 : (distance / 1000 / 1000))
            }
        } else {
            cell.distance.text = ""
        }
        
        if Model.TMPE_RESTAURANT_LIST.count - 1 == indexPath.row  && self.reqData.loadingMore {
            self.loadData(refresh: false)
        }
        
        return cell
    }

    
    // GPS
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
            break
        case .denied:
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
        case .authorizedWhenInUse:
            self.LM.startUpdatingLocation()
            break
        case .notDetermined, .authorizedAlways:
            break
        }
    }
    
    
    func setButtonsDefaultColor(sender: UIButton){
        self.uiButtons.forEach { b in
            b.borderColor = UIColor.darkGray
            b.backgroundColor = UIColor.white
            b.setTitleColor(UIColor.darkGray, for: .normal)
        }
        
        sender.borderColor = NaberConstant.COLOR_BASIS
        sender.setTitleColor(UIColor.white, for: .normal)
        sender.backgroundColor = NaberConstant.COLOR_BASIS
    }
    

    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 2
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        if component == 0 {
            return self.schoolDivideds.count
        }else {
            return self.schoolDivideds[self.DIVIDED_INDEX].schools.count
        }
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        if component == 0 {
            return self.schoolDivideds[row].area
        }else {
            return self.schoolDivideds[self.DIVIDED_INDEX].schools[row]
        }
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        if component == 0 {
            self.DIVIDED_INDEX = row
        }else {
            self.SCHOOL_INDEX = row
        }
    }
}

