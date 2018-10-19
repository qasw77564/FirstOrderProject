//
//  TrendCompleteOrderViewController.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/6/6.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit


class TrendCompleteOrderVC: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    
    var reqData: ReqData!
    var orders: [OrderVo] = []
    
    @IBOutlet weak var startSelect: UITextField!
    @IBOutlet weak var endSelect: UITextField!
    
    @IBOutlet weak var tabelView: UITableView! {
        didSet{
            self.tabelView.delegate = self
            self.tabelView.dataSource = self
            let refreshControl: UIRefreshControl = UIRefreshControl()
            refreshControl.attributedTitle = NSAttributedString(string: "Pull to refresh", attributes: [NSAttributedStringKey.foregroundColor: UIColor(red: 188/255, green: 188/255, blue: 188/255, alpha: 1.0)])
            refreshControl.addTarget(self, action: #selector(refresh), for: .valueChanged)
            refreshControl.tintColor = UIColor.clear
            self.tabelView.addSubview(refreshControl)
        }
    }
    
    var startDatePicker: UIDatePicker {
        get {
            let datePicker = UIDatePicker()
            datePicker.datePickerMode = .date
            datePicker.locale = Locale.init(identifier: "zh_TW")
            datePicker.timeZone = TimeZone.init(identifier: "Asia/Taipei")
            self.reTimeRange(picker: datePicker)
            datePicker.tag = 0
            datePicker.addTarget(self, action: #selector(onDateChanged), for: .valueChanged)
            datePicker.backgroundColor = UIColor.white
            return datePicker
        }
    }
    
    var startToolbar: UIToolbar {
        get {
            let accessory = UIToolbar(frame: CGRect(x: 0, y: 0, width: view.frame.width, height: 44))
            let doneBtn = UIBarButtonItem(title: "完成" , style: .done, target: self,action: #selector(onToolbarDone))
            doneBtn.tag = 0
            let space = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
            let cancelBtn = UIBarButtonItem(title:"取消" , style: .plain, target: self,action: #selector(onToolbarCancel))
            cancelBtn.tag = 0
            accessory.items = [cancelBtn, space , doneBtn]
            accessory.barTintColor = UIColor.white
            return accessory
        }
    }
    
    var endDatePicker: UIDatePicker {
        get {
            let datePicker = UIDatePicker()
            datePicker.datePickerMode = .date
            datePicker.locale = Locale.init(identifier: "zh_TW")
            datePicker.timeZone = TimeZone.init(identifier: "Asia/Taipei")
            self.reTimeRange(picker: datePicker)
            datePicker.tag = 1
            datePicker.addTarget(self, action: #selector(onDateChanged), for: .valueChanged)
            datePicker.backgroundColor = UIColor.white
            return datePicker
        }
    }
    
    var endToolbar: UIToolbar {
        get {
            let accessory = UIToolbar(frame: CGRect(x: 0, y: 0, width: view.frame.width, height: 44))
            let doneBtn = UIBarButtonItem(title: "完成" , style: .done, target: self, action: #selector(onToolbarDone))
            doneBtn.tag = 1
            let space = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
            let cancelBtn = UIBarButtonItem(title:"取消" , style: .plain, target: self,action: #selector(onToolbarCancel))
            cancelBtn.tag = 1
            accessory.items = [cancelBtn, space , doneBtn]
            accessory.barTintColor = UIColor.white
            return accessory
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.startSelect.inputView = self.startDatePicker
        self.startSelect.text = DateTimeHelper.getNow(from: "yyyy年 MM月 dd日")
        self.startSelect.tag = DateTimeHelper.startOfDateMiliseconds()
        self.startSelect.inputAccessoryView = self.startToolbar
        self.endSelect.inputView = self.endDatePicker
        self.endSelect.text = DateTimeHelper.getNow(from: "yyyy年 MM月 dd日")
        self.endSelect.tag = DateTimeHelper.endOfDateMiliseconds()
        self.endSelect.inputAccessoryView = self.endToolbar
        self.reqData = ReqData()
        self.loadData(refresh: true)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    @objc func refresh(sender: UIRefreshControl){
        sender.endRefreshing()
        self.loadData(refresh: true)
    }
    
    func loadData(refresh: Bool){
        if refresh {
            self.reqData.page = 0
            self.reqData.loadingMore = false
            self.orders.removeAll()
            self.tabelView.reloadData()
        }
        
        self.reqData.page = self.reqData.page + 1
        self.reqData.start_date = DateTimeHelper.formToString(date: self.startSelect.text!, fromDate: "yyyy年 MM月 dd日")
        self.reqData.end_date = DateTimeHelper.formToString(date: self.endSelect.text!, fromDate: "yyyy年 MM月 dd日")
        ApiManager.sellerStatLog(req: self.reqData, ui: self, onSuccess: { orders in
            self.orders.append(contentsOf: orders.map({ o -> OrderVo in
                o.order_detail = OrderDetail.parse(src: o.order_data)!
                return o
            }))
            
            print(self.orders.count)
            self.reqData.loadingMore = orders.count % NaberConstant.PAGE == 0 && orders.count != 0
            self.tabelView.reloadData()
            
        }) { err_msg in
            print(err_msg)
        }
    }
    
    @objc func onDateChanged(sender: UIDatePicker) {
        if sender.tag == 0 {
            self.startSelect.tag = DateTimeHelper.startOfDateMiliseconds(date: sender.date)
            self.startSelect.text = DateTimeHelper.dateToStringForm(date: sender.date, form: "yyyy年 MM月 dd日")
        } else {
            self.endSelect.tag = DateTimeHelper.endOfDateMiliseconds(date: sender.date)
            self.endSelect.text = DateTimeHelper.dateToStringForm(date: sender.date, form: "yyyy年 MM月 dd日")
        }
    }
    
    // tag 0 == start time
    // tag 1 == end time
    @objc func onToolbarDone(sender: UIBarButtonItem) {
        if sender.tag == 0 {
            self.startSelect.endEditing(true)
        } else {
            self.endSelect.endEditing(true)
        }

        if self.startSelect.tag > self.endSelect.tag {
            let alert = UIAlertController(title: "系統提示", message: "開始時間不可大於結束時間", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "我知道了", style: .default))
            self.present(alert, animated: false)
        } else {
            self.loadData(refresh: true)
        }
    }
    
    @objc func onToolbarCancel(sender: UIBarButtonItem) {
        if sender.tag == 0 {
            self.startSelect.endEditing(true)
        } else {
            self.endSelect.endEditing(true)
        }
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.orders.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: UIIdentifier.CELL.rawValue, for: indexPath) as! TrendCompleteOrderTVCell
        cell.tag = indexPath.row
        
        cell.name.text = self.orders[indexPath.row].order_detail.user_name
        cell.phone.text = self.orders[indexPath.row].order_detail.user_phone
        cell.price.text = "$ " + self.orders[indexPath.row].order_price
        
        let status: OrderStatus = OrderStatus.of(name: self.orders[indexPath.row].status)
        cell.orderStatus.backgroundColor = status.get().color
        cell.orderStatus.setTitle(status.get().value, for: .normal)
        
        if self.orders.count - 1 == indexPath.row && self.reqData.loadingMore {
            self.loadData(refresh: false)
        }
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if let vc = UIStoryboard(name: UIIdentifier.STORE.rawValue, bundle: nil).instantiateViewController(withIdentifier: "TrendOrderLogDetail") as? TrendOrderLogDetailVC {
            vc.order = self.orders[indexPath.row]
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
    
    override func show(_ vc: UIViewController, sender: Any?) {

    }
    
    func reTimeRange (picker: UIDatePicker){
        let currentDate: Date = Date()
        var calendar: Calendar = Calendar(identifier: Calendar.Identifier.gregorian)
        calendar.timeZone = TimeZone.init(identifier: "Asia/Taipei")!
        calendar.locale = Locale.init(identifier: "zh_TW")
        var components: DateComponents = DateComponents()
        components.calendar = calendar
        components.month = 0
        let maxDate: Date = calendar.date(byAdding: components, to: currentDate)!
        picker.maximumDate = maxDate
        components.month = -2
        let minDate: Date = calendar.date(byAdding: components, to: currentDate)!
        picker.minimumDate = minDate
    }
    
   

}

