//
//  BonusExchangeVC.swift
//  Naber
//
//  Created by 王淳彦 on 2018/8/2.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//


import UIKit


class BonusExchangeVC: UIViewController, UITableViewDataSource, UITableViewDelegate,UITextFieldDelegate {
    
    var activities: [ActivitiesVo] = []
    
//    @IBOutlet var eventDescription: UILabel!
    
    @IBOutlet var serial: UITextField! {
        didSet {
            self.serial.delegate = self
            self.serial.addTarget(self, action: #selector(uppercased), for: .editingChanged)
        }
    }
    
    @objc func uppercased (sender: UITextField){
        sender.text = sender.text?.uppercased()
    }
    
    @IBOutlet var eventContent: UILabel!
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
    @objc func hideKeyboard(sender: Any){
        self.view.endEditing(true)
    }
    
    @objc func refresh(sender: UIRefreshControl){
        self.loadData(refresh: true) {
            sender.endRefreshing()
            self.tableView.reloadData()
        }
    }
    
    
    func loadData (refresh: Bool, complete: @escaping () -> ()){
        if refresh {
            self.activities.removeAll()
            self.tableView.reloadData()
        }
        
        ApiManager.getAllActivities(ui: self, onSuccess: { activities in
            self.activities.append(contentsOf: activities)
            complete()
        }) { err_msg in
            print(err_msg)
        }
    }
        
    override func viewDidLoad() {
        super.viewDidLoad()
        self.loadData(refresh: true) {
            self.tableView.reloadData()
        }
    }
//
    override func viewWillAppear(_ animated: Bool) {
        self.serial.text = ""
//        ApiManager.getAllActivities(ui: self, onSuccess: { activities in
//            self.activities.append(contentsOf: activities)
//            self.tableView.reloadData()
//        }) { err_msg in
//            print(err_msg)
//        }
    }
//
    
    override func show(_ vc: UIViewController, sender: Any?) {
    }
    
    @IBAction func submitSerialAction(_ sender: UIButton){

        if (self.serial.text?.isEmpty)! || self.serial.text?.count != 8 {
            let alert = UIAlertController(title: "系統提示", message: "請正確輸入8碼兌換序號", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "我知道了", style: .default))
            self.present(alert, animated: false)
        }else {
            let req: ReqData = ReqData()
            req.data = self.serial.text?.uppercased()
            ApiManager.serialSubmit(req: req, ui: self, onSuccess: { activities in
                if activities.act_category == "RES_EVENT" {
                    if let order: OrderDetail = OrderDetail.parse(src: activities.data) {
                        if let vc = UIStoryboard(name: UIIdentifier.USER.rawValue, bundle: nil).instantiateViewController(withIdentifier: "ActRESItemSubmit") as? ActRESItemSubmitVC {
                            vc.orderDetail = order
                            vc.activities = activities
                            self.navigationController?.pushViewController(vc, animated: true)
                        }
                    }else {
                        let alert = UIAlertController(title: "兌換失敗", message: "該項目已結束兌換。", preferredStyle: .alert)
                        alert.addAction(UIAlertAction(title: "我知道了", style: .default))
                        self.present(alert, animated: false)
                    }
                }else if activities.act_category == "TICKET" {
                    let alert = UIAlertController(title: "兌換成功", message: activities.data + "紅利兌換成功。" , preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "我知道了", style: .default) {_ in
                        self.serial.text = ""
                    })
                    self.present(alert, animated: false)
                }
                
            }) { err_msg in
                let alert = UIAlertController(title: "兌換失敗", message: err_msg, preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "我知道了", style: .default))
                self.present(alert, animated: false)
            }
        }
    }
    
    func textData (){
        self.eventContent.text =
        "凡是透過NABER訂餐，\n一律回饋消費金額之10%紅利點數\n" +
        "，並能兌換NABER所提供之獎勵。\n\n" +
        "* 10月起 開放兌換獎勵及現金折抵\n" +
        "* 消費10元獲得1點紅利點數\n"
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    // Cell 所需數量
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.activities.count
    }
    
    //回傳 Ｃell 樣式
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: UIIdentifier.CELL.rawValue, for: indexPath) as! BonusExchangeTVCell
        cell.point.text = self.activities[indexPath.row].need_bonus
        cell.productName.text = self.activities[indexPath.row].title
        cell.remarks.text = self.activities[indexPath.row].content_text
        return cell
    }
    
    // 點擊事件
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if let vc = UIStoryboard(name: UIIdentifier.USER.rawValue, bundle: nil).instantiateViewController(withIdentifier: "ActivitiesSendSubmitVC") as? ActivitiesSendSubmitVC {
            vc.act = self.activities[indexPath.row]
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
    
    
    // 限制輸入長度
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        guard let text = textField.text else { return true }
        let newLength: Int = text.count + string.count - range.length
//        if !CharacterSet.decimalDigits.isSuperset(of: CharacterSet(charactersIn: string)) {
//            return false
//        }
        return newLength <= 8
    }
    
}
