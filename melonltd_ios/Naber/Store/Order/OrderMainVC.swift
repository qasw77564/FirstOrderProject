//
//  OrderMainVC.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/6/6.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit
import UserNotifications

class OrderMainVC: UIViewController, UITableViewDelegate, UITableViewDataSource, UNUserNotificationCenterDelegate{
    

    var orders: [OrderVo] = []
    var uiButtons: [UIButton] = []
    var timer: Timer!
    var queryStatus: OrderStatus = OrderStatus.LIVE
    var reqData: ReqData = ReqData()
    
    var toolbar: UIToolbar {
        get {
            let toolbar = UIToolbar(frame: CGRect(x: 0, y: 0, width: view.frame.width, height: 44))
            let doneBtn = UIBarButtonItem(title: "完成", style: .done, target: self, action: #selector(doneTimePick))
            let flexible = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
            let cancelBtn = UIBarButtonItem(title:"取消", style: .plain,target: self,action: #selector(cancelTimePick))
            toolbar.items = [cancelBtn, flexible, doneBtn]
            toolbar.barTintColor = UIColor.white
            return toolbar
        }
    }
    
    var datePicker: UIDatePicker {
        get {
            let datePicker = UIDatePicker()
            datePicker.datePickerMode = .date
            datePicker.locale = Locale.init(identifier: "zh_TW")
            datePicker.timeZone = TimeZone.init(identifier: "Asia/Taipei")
            self.reTimeRange(picker: datePicker)
            datePicker.addTarget(self, action: #selector(onDateChanged), for: .valueChanged)
            datePicker.backgroundColor = UIColor.white
            return datePicker
        }
    }
    
    @IBOutlet weak var timeRangeLayout: NSLayoutConstraint!
    @IBOutlet weak var dateSelect: UITextField! {
        didSet {
            self.dateSelect.text = DateTimeHelper.getNow(from: "yyyy年 MM月 dd日")
        }
    }
    
    @IBOutlet weak var liveBtn: UIButton!
    @IBOutlet weak var unfinishBtn: UIButton!
    @IBOutlet weak var processingBtn: UIButton!
    @IBOutlet weak var canFetchBtn: UIButton!
    
    @IBOutlet weak var stackView: UIStackView!
    @IBOutlet weak var table: UITableView! {
        didSet {
            self.table.dataSource = self
            self.table.delegate = self
            let refreshControl: UIRefreshControl = UIRefreshControl()
            refreshControl.attributedTitle = NSAttributedString(string: "Pull to refresh", attributes: [NSAttributedStringKey.foregroundColor: UIColor(red: 188/255, green: 188/255, blue: 188/255, alpha: 1.0)])
            refreshControl.addTarget(self, action: #selector(refresh), for: .valueChanged)
            refreshControl.tintColor = UIColor.clear
            self.table.addSubview(refreshControl)
        }
    }
    
    @objc func refresh(sender: UIRefreshControl){
        sender.endRefreshing()
        self.loadData(refresh: true)
    }

    @objc func onDateChanged(sender: UIDatePicker) {
        self.dateSelect.text = DateTimeHelper.dateToStringForm(date: sender.date, form: "yyyy年 MM月 dd日")
    }
    
    // 確定選取時間事件
    @objc func doneTimePick(sender: UIBarButtonItem){
        // TODO
        if self.dateSelect.text == "" {
            self.dateSelect.text = DateTimeHelper.dateToStringForm(date: self.datePicker.date, form: "yyyy年 MM月 dd日")
        }
        self.view.endEditing(true)
        if self.queryStatus != OrderStatus.LIVE {
            self.reqData.date = DateTimeHelper.formToString(date: self.dateSelect.text!, fromDate: "yyyy年 MM月 dd日")
            self.loadData(refresh: true)
        }
    }
    
    // 取消選取時間事件
    @objc func cancelTimePick(sender: UIBarButtonItem){
        self.view.endEditing(true)
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        self.dateSelect.inputView = self.datePicker
        self.dateSelect.inputAccessoryView = self.toolbar
        self.dateSelect.isHidden = true
        self.timeRangeLayout.constant = -200
        self.uiButtons.append(contentsOf: [self.liveBtn, self.unfinishBtn, self.processingBtn, self.canFetchBtn])
        // 第一次進入使用即時訂單
//        self.callLiveOrders()
        
        
        if #available(iOS 10, *) {
            UNUserNotificationCenter.current().delegate = self
            UNUserNotificationCenter.current().requestAuthorization(options:[.badge, .alert, .sound]){ (granted, error) in }
        }
    }

    func loadData(refresh: Bool){
        if (refresh){
            self.reqData.page = 0
            self.orders.removeAll()
            self.reqData.loadingMore = true
            self.table.reloadData()
        }
        
        if self.queryStatus == OrderStatus.LIVE {
            self.startTimer()
        } else {
            self.reqData.page = self.reqData.page + 1
            ApiManager.sellerOrderList(req: self.reqData, ui: self, onSuccess: { orders in
                self.orders.append(contentsOf: orders.map({ o -> OrderVo in
                    o.order_detail = OrderDetail.parse(src: o.order_data)!
                    return o
                }))
                self.reqData.loadingMore = orders.count % NaberConstant.PAGE == 0 && orders.count != 0
                self.table.reloadData()
            }) { err_msg in
                // print(err_msg)
            }
        }
    }
    
    // 進入該畫面開始 Timer
    override func viewWillAppear(_ animated: Bool) {
        self.reTimeRange(picker: self.datePicker)
        if queryStatus == OrderStatus.LIVE{
            self.startTimer()
        }
    }
    
    // 離開該畫面停止 Timer
    override func viewDidDisappear(_ animated: Bool) {
        self.stackView.isUserInteractionEnabled = true
        self.table.isUserInteractionEnabled = true
        self.timeRangeLayout.constant = -200
        self.view.layoutIfNeeded()
        self.stopTimer()
    }
    
    func callLiveOrders(){
        self.orders.removeAll()
        self.reqData.loadingMore = false
        ApiManager.sellerOrderLive(ui: self, onSuccess: {orders in
            self.orders.append(contentsOf: orders.map({ o -> OrderVo in
                o.order_detail = OrderDetail.parse(src: o.order_data)!
                return o
            }))
            // print(Date())
            self.table.reloadData()
        }) { err_msg in
            // print(err_msg)
        }
    }
    
    // timer 排程
    @objc func scheduledLiveOrder(){
        self.callLiveOrders()
    }
    
    // 開始執行timer
    func startTimer(){
        self.stopTimer()
        if self.timer == nil {
//            // print("call start timer ok")
            self.callLiveOrders()
            timer = Timer.scheduledTimer(timeInterval: NaberConstant.SELLER_LIVE_ORDER_REFRESH_TIMER, target: self, selector: #selector(scheduledLiveOrder), userInfo: nil, repeats: true)
        }
    }
    
    // 將timer的執行緒停止
    func stopTimer(){
        if self.timer != nil {
//            // print("call stop timer ok")
            self.timer.invalidate()
            self.timer = nil
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.orders.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

        let cell = tableView.dequeueReusableCell(withIdentifier: UIIdentifier.CELL.rawValue, for: indexPath) as! OrderMainTVCell
        
        cell.cancelBtn.tag = indexPath.row
        cell.failureBtn.tag = indexPath.row
        cell.processingBtn.tag = indexPath.row
        cell.canFetchBtn.tag = indexPath.row
        cell.finishBtn.tag = indexPath.row
        
//        let status: OrderStatus = OrderStatus.of(name: self.orders[indexPath.row].status)
//        cell.orderStatus.text = status.get().value
        cell.orderType.text = self.orders[indexPath.row].order_detail.order_type.delivery == "OUT" ? "外帶" : "內用"
        cell.orderStatus.isHidden = true
        
        cell.count.text = "(" + self.orders[indexPath.row].order_detail.orders.count.description + ")"
        
        if self.orders[indexPath.row].order_detail.use_bonus != nil {
            let price: Int = Int(self.orders[indexPath.row].order_price)! - (Int(self.orders[indexPath.row].order_detail.use_bonus)! / 10 * 3)
            cell.price.text = "$" + price.description + ", div. " + self.orders[indexPath.row].order_detail.use_bonus
        }else {
            cell.price.text = "$" + self.orders[indexPath.row].order_price
        }
        
//        cell.price.text = "$" + self.orders[indexPath.row].order_price
        cell.name.text = self.orders[indexPath.row].order_detail.user_name
        cell.phone.text = self.orders[indexPath.row].order_detail.user_phone
        cell.fetchTime.text = DateTimeHelper.formToString(date: self.orders[indexPath.row].fetch_date, from: "dd日 HH時 mm分")
        cell.userMessage.text = self.orders[indexPath.row].user_message
        
        var content: String = ""
        for o in self.orders[indexPath.row].order_detail.orders {
            content +=  o.item.category_name + ": " +
            StringsHelper.padEnd(str: o.item.food_name, minLength: 10, of: " ") +
            StringsHelper.padEnd(str: "x" + o.count, minLength: 15, of: " ") +
            "$ " + o.item.price +
            "\n規格: " +
            StringsHelper.padEnd(str: o.item.scopes[0].name, minLength: 20 , of: " ") +
            "\n附加: " +
            o.item.opts.reduce("", { (s: String, i: ItemVo) -> String in
                return s + "\n" + StringsHelper.padEnd(str:"- " + i.name, minLength: 20 , of: " ") +
                    StringsHelper.padEnd(str: "  ", minLength: 10 , of: " ") + "$ " + i.price
            }) + (o.item.opts.count == 0 ? "- 無\n" : "\n") +
            "需求: "
            for d in o.item.demands {
                content += d.name + ": " + d.datas[0].name + ", "
            }
            content += "\n------------------------------------------\n\n"
        }
        
        
        cell.foodDatas.text = content
        
        cell.finishBtn.isHidden = true
        cell.failureBtn.isHidden = true
        cell.processingBtn.isHidden = true
        cell.canFetchBtn.isHidden = true
        cell.cancelBtn.isHidden = false
        
        
        let status: OrderStatus = OrderStatus.of(name: self.orders[indexPath.row].status)
        switch status {
        case .UNFINISH:
            cell.processingBtn.isHidden = false
            cell.canFetchBtn.isHidden = false
            break
        case .PROCESSING:
            cell.canFetchBtn.isHidden = false
            cell.finishBtn.isHidden = false
            break
        case .CAN_FETCH:
            cell.failureBtn.isHidden = false
            cell.finishBtn.isHidden = false
            break
        case .LIVE, .CANCEL, .FAIL, .FINISH, .UNKNOWN:
            break
        }
        
        if self.orders.count - 1 == indexPath.row  && self.reqData.loadingMore {
            self.loadData(refresh: false)
        }
        
        return cell
    }
 
    // 即時 Tab
    @IBAction func liveSelect(_ sender: UIButton) {
        self.setButtonsDefaultColor(sender: sender)
        self.dateSelect.isHidden = true
        self.queryStatus = OrderStatus.LIVE
        self.loadData(refresh: true)
    }

    
    // 未處理 Tab
    @IBAction func unfinishSelect(_ sender: UIButton) {
        self.queryStatus = OrderStatus.UNFINISH
        self.setButtonsDefaultColor(sender: sender)
        self.reTimeRange(picker: self.datePicker)
        self.reqData.search_type = OrderStatus.UNFINISH.rawValue
        if self.dateSelect.text != "" {
            self.reqData.date = DateTimeHelper.formToString(date: self.dateSelect.text!, fromDate: "yyyy年 MM月 dd日")
            self.loadData(refresh: true)
        } else {
            self.orders.removeAll()
            self.table.reloadData()
        }
    }
    
    // 處理中 Tab
    @IBAction func processingSelect(_ sender: UIButton) {
        self.queryStatus = OrderStatus.PROCESSING
        self.setButtonsDefaultColor(sender: sender)
        self.reTimeRange(picker: self.datePicker)
        self.reqData.search_type = OrderStatus.PROCESSING.rawValue
        if self.dateSelect.text != "" {
            self.reqData.date = DateTimeHelper.formToString(date: self.dateSelect.text!, fromDate: "yyyy年 MM月 dd日")
            self.loadData(refresh: true)
        } else {
            self.orders.removeAll()
            self.table.reloadData()
        }
    }
    
    // 可領取 Tab
    @IBAction func canFetchSelect(_ sender: UIButton) {
        self.queryStatus = OrderStatus.FAIL
        self.setButtonsDefaultColor(sender: sender)
        self.reTimeRange(picker: self.datePicker)
        self.reqData.search_type = OrderStatus.CAN_FETCH.rawValue
        if self.dateSelect.text != "" {
            self.reqData.date = DateTimeHelper.formToString(date: self.dateSelect.text!, fromDate: "yyyy年 MM月 dd日")
            self.loadData(refresh: true)
        } else {
            self.orders.removeAll()
            self.table.reloadData()
        }
    }

    // 收合左側抽屜Layout
    @IBAction func organizeBtnPressed(_ sender: UIBarButtonItem) {
        // TODO
        self.stackView.isUserInteractionEnabled = self.timeRangeLayout.constant >= 0 ? true : false
        self.table.isUserInteractionEnabled = self.timeRangeLayout.constant >= 0 ? true : false
        self.timeRangeLayout.constant = self.timeRangeLayout.constant >= 0 ? -200 : 0
       
        UIView.animate(withDuration: 0.3, animations: {
            self.view.layoutIfNeeded()
        })
    }
    
    // 點擊 Button Tab 改變 Tab 焦點顏色
    func setButtonsDefaultColor(sender: UIButton){
        if self.queryStatus != OrderStatus.LIVE {
            self.stopTimer()
        }
        self.dateSelect.isHidden = false
        self.view.endEditing(true)
        self.uiButtons.forEach { b in
            b.backgroundColor = NaberConstant.COLOR_BASIS_GRAY
            b.setTitleColor(UIColor.darkGray, for: .normal)
        }
        sender.setTitleColor(UIColor.white, for: .normal)
        sender.backgroundColor = NaberConstant.COLOR_BASIS
    }
    
    // 更改訂單狀態取消
    @IBAction func changeToCancel (_ sender: UIButton ) {
        if self.queryStatus == OrderStatus.LIVE {
            self.stopTimer()
        }
        var text: UITextField! = UITextField()
        let alert = UIAlertController(title: "確定要取消訂單嗎？\n客戶不會獲得任何紅利點數", message: "\n告訴客人原因\n", preferredStyle: .alert)
        
        alert.addTextField{ textField in
            textField.placeholder = "自行輸入"
            textField.leftView = UIView(frame: CGRect(x: 0, y: 0, width: 10, height: 10))
            textField.addConstraint(textField.heightAnchor.constraint(equalToConstant: 36))
            textField.font?.withSize(30)
            text = textField
        }
        
        alert.addAction(UIAlertAction(title: "使用自訂內容", style: .default, handler: { _ in
            text.text = StringsHelper.replace(str: text.text! , of: " ", with: "")
            self.changeToCancelHandler(dataIndex: sender.tag, message: text.text!)
        }))
        
        alert.addAction(UIAlertAction(title: "我現在忙不過來，抱歉", style: .default, handler: { _ in
            self.changeToCancelHandler(dataIndex: sender.tag, message: "我現在忙不過來，抱歉")
        }))
        
        alert.addAction(UIAlertAction(title: "產品賣完了，很抱歉請改選其他產品", style: .default,  handler: { _ in
            self.changeToCancelHandler(dataIndex: sender.tag, message: "產品賣完了，很抱歉請改選其他產品")
        }))
        
        alert.addAction(UIAlertAction(title: "取消", style: .destructive, handler:{ _ in
            if self.queryStatus == OrderStatus.LIVE {
                self.startTimer()
            }
        }))
        self.present(alert, animated: false, completion: nil)
    }
    
    // 更改訂單狀態取消 後處理資訊
    func changeToCancelHandler(dataIndex: Int, message: String){
        Loading.show()
        let alert = UIAlertController(title: message != "" ? "你要給客人的原因" : "", message: message != "" ? message : "至少選取一個原因，或自行輸入", preferredStyle: .alert)
        if message == "" {
            alert.addAction(UIAlertAction(title: "我知道了", style: .default, handler: { _ in
                if self.queryStatus == OrderStatus.LIVE {
                    self.startTimer()
                }
            }))
        }else {
            alert.addAction(UIAlertAction(title: "返回", style: .destructive, handler:{ _ in
                if self.queryStatus == OrderStatus.LIVE {
                    self.startTimer()
                }
            }))
            alert.addAction(UIAlertAction(title: "送出", style: .default, handler: { _ in
                let reqData: ReqData = ReqData()
                reqData.uuid = self.orders[dataIndex].order_uuid
                reqData.type = OrderStatus.CANCEL.get().name
                reqData.message = message
                self.changeOreder(reqData: reqData)
            }))
        }
        Loading.hide()
        self.present(alert, animated: false, completion: nil)
    }
    
    // 更改訂單狀態跑單
    @IBAction func changeToFailure (_ sender: UIButton ) {
        if self.queryStatus == OrderStatus.LIVE {
            self.stopTimer()
        }
        let alert = UIAlertController(title: Optional.none, message:"確定客戶跑單嗎？\n會影響客戶點餐的權益以及紅利點數", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "返回", style: .destructive, handler:{ _ in
            if self.queryStatus == OrderStatus.LIVE {
                self.startTimer()
            }
        }))
        alert.addAction(UIAlertAction(title: "送出", style: .default, handler: { _ in
            let reqData: ReqData = ReqData()
            reqData.uuid = self.orders[sender.tag].order_uuid
            reqData.type = OrderStatus.FAIL.get().name
            reqData.message = "你的商品超過時間未領取，記點一次，請注意往後的訂餐權利！"
            self.changeOreder(reqData: reqData)
        }))
        self.present(alert, animated: false, completion: nil)
    }
    
    // change Failure & Cancel func
    func changeOreder(reqData: ReqData){
        ApiManager.sellerChangeOrder(req: reqData, ui: self, onSuccess: {
            self.loadData(refresh: true)
        }, onFail: { err_msg in
            // print(err_msg)
           self.loadData(refresh: true)
        })
    }
    
    // 更改訂單狀態製作中
    @IBAction func changeToProcessing (_ sender: UIButton ) {
        self.changeOrderStatus(status: OrderStatus.PROCESSING, dataIndex: sender.tag, alertMsg: "確認開始製作此訂單")
    }
    
    // 更改訂單狀態可領取
    @IBAction func changeToCanFetch (_ sender: UIButton ) {
        self.changeOrderStatus(status: OrderStatus.CAN_FETCH, dataIndex: sender.tag, alertMsg: "確認此訂單已可領取")
    }
    
    // 更改訂單狀態完成
    @IBAction func changeToFinish (_ sender: UIButton ) {
        self.changeOrderStatus(status: OrderStatus.FINISH, dataIndex: sender.tag, alertMsg: "確認此訂單已交易完成")
    }
    
    func changeOrderStatus (status: OrderStatus, dataIndex: Int, alertMsg: String){
        if self.queryStatus == OrderStatus.LIVE {
            self.stopTimer()
        }
        
        let reqData: ReqData = ReqData()
        reqData.uuid = self.orders[dataIndex].order_uuid
        reqData.type = status.get().name
        
        let alert = UIAlertController(title: Optional.none, message: alertMsg, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "返回", style: .destructive, handler:{ _ in
            if self.queryStatus == OrderStatus.LIVE {
                self.startTimer()
            }
        }))
        alert.addAction(UIAlertAction(title: "確定", style: .default, handler: { _ in
            ApiManager.sellerChangeOrder(req: reqData, ui: self, onSuccess: {
                self.loadData(refresh: true)
            }, onFail: { err_msg in
                // print(err_msg)
            })
        }))
        self.present(alert, animated: false)
    }
    
    func reTimeRange (picker: UIDatePicker){
        let currentDate: Date = Date()
        var calendar: Calendar = Calendar(identifier: Calendar.Identifier.gregorian)
        calendar.timeZone = TimeZone.init(identifier: "Asia/Taipei")!
        calendar.locale = Locale.init(identifier: "zh_TW")
        var components: DateComponents = DateComponents()
        components.calendar = calendar
        components.day = 3
        let maxDate: Date = calendar.date(byAdding: components, to: currentDate)!
        picker.maximumDate = maxDate
        components.day = 0
        let minDate: Date = calendar.date(byAdding: components, to: currentDate)!
        picker.minimumDate = minDate
    }
    
    
    @available(iOS 10, *)
    public func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Swift.Void) {
        let userInfo = notification.request.content.userInfo
        let currentId: Identity = UserSstorage.getCurrentId()!
        if let identity: Identity = Identity(rawValue: userInfo["identity"] as! String) {
            if identity == Identity.SELLERS  && currentId == Identity.SELLERS {
                completionHandler( [.alert, .badge, .sound])
                if self.queryStatus == OrderStatus.LIVE {
                    self.stopTimer()
                    self.startTimer()
                }
            }
        }
    }
    
    @available(iOS 10, *)
    public func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        // print("Do what ever you want")
        
    }

}




