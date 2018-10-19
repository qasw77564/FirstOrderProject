//
//  ActivitiesRESItemSubmitVC.swift
//  Naber
//
//  Created by 王淳彦 on 2018/10/8.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit


class ActRESItemSubmitVC: UIViewController, UITextViewDelegate, UIPickerViewDelegate, UIPickerViewDataSource ,UITextFieldDelegate, UITableViewDelegate, UITableViewDataSource {
    
    var activities: ActivitiesVo!
    var orderDetail: OrderDetail!
    
    @IBOutlet weak var titleText: UILabel!
        
    @IBOutlet weak var table: UITableView! {
        didSet {
            let gestureRecognizer: UITapGestureRecognizer = UITapGestureRecognizer.init(target: self, action: #selector(hideKeyboard))
            gestureRecognizer.numberOfTapsRequired = 1
            gestureRecognizer.cancelsTouchesInView = false
            self.table.delegate = self
            self.table.dataSource = self
            self.table.addGestureRecognizer(gestureRecognizer)
        }
    }
    // UITableView click bk hide keyboard
    @objc func hideKeyboard(sender: Any){
        self.view.endEditing(true)
    }
    
    var selectDate: String = ""
    var datePicker: UIDatePicker {
        get {
            let picker = UIDatePicker()
            picker.datePickerMode = .dateAndTime
            picker.locale = Locale.init(identifier: "zh_TW")
            picker.timeZone = TimeZone.init(identifier: "Asia/Taipei")
            picker.addTarget(self, action: #selector(onDateChanged), for: .valueChanged)
            picker.backgroundColor = UIColor.white
            self.reTimeRange(picker: picker, isInit: true)
            return picker
        }
    }
    
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
    
    @IBOutlet weak var dateSelect: UITextField! {
        didSet{
            self.dateSelect.delegate = self
        }
    }
    
    @IBOutlet weak var placeHolder: UILabel! {
        didSet {
            self.placeHolder.text = ""
        }
    }
    @IBOutlet weak var userMssage: UITextView! {
        didSet {
            self.userMssage.delegate = self
        }
    }
    
    @IBOutlet weak var name: UITextField!
    @IBOutlet weak var phone: UITextField!
    
    @IBOutlet weak var delivery: UITextField!
    var deliveryDates: [String] = ["外帶", "內用"]
    var deliveryDate: Int = 0
    var deliveryToolbar: UIToolbar {
        get {
            let deliveryToolbar = UIToolbar(frame: CGRect(x: 0, y: 0, width: view.frame.width, height: 44))
            let doneBtn = UIBarButtonItem(title: "確定" , style: .done, target: self, action: #selector(onDeliveryDoneBtn))
            let cancelBtn = UIBarButtonItem(title: "取消" , style: .plain, target: self, action: #selector(onDeliveryCancelBtn))
            let space = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
            deliveryToolbar.items = [cancelBtn, space, doneBtn]
            deliveryToolbar.barTintColor = UIColor.white
            return deliveryToolbar
        }
    }
    var deliveryPicker: UIPickerView {
        get {
            let pickerView = UIPickerView()
            pickerView.dataSource = self
            pickerView.delegate = self
            pickerView.backgroundColor = UIColor.white
            return pickerView
        }
    }
    
    @objc func onDeliveryDoneBtn(sender: UIBarButtonItem) {
        self.delivery.text = self.deliveryDates[self.deliveryDate]
        self.view.endEditing(true)
    }
    
    @objc func onDeliveryCancelBtn(sender: UIBarButtonItem) {
        self.view.endEditing(true)
    }
    
    @IBOutlet weak var readRuleBtn: UIButton!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.dateSelect.inputView = self.datePicker
        self.dateSelect.inputAccessoryView = self.toolbar
       
        //取餐方式
        self.delivery.inputView = self.deliveryPicker
        self.delivery.inputAccessoryView = self.deliveryToolbar
        self.delivery.text = self.deliveryDates[0]
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.reTimeRange(picker: self.datePicker, isInit: false)
        self.dateSelect.text = ""
        self.titleText.text = self.activities.title
        self.name.text = UserSstorage.getAccountInfo()?.name
        self.phone.text = UserSstorage.getAccountInfo()?.phone

    }
    
    @objc func onDateChanged(sender: UIDatePicker) {
        self.selectDate = DateTimeHelper.dateToStringForm(date: sender.date, form: "yyyy-MM-dd HH:mm")
    }
    
    // 確定選取時間事件
    @objc func doneTimePick(sender: UIBarButtonItem){
        self.dateSelect.text = self.selectDate
        self.view.endEditing(true)
    }
    
    // 取消選取時間事件
    @objc func cancelTimePick(sender: UIBarButtonItem){
        self.view.endEditing(true)
    }
    
    @IBAction func readRuleAction(_ sender: UIButton) {
        let msg: String = "1.您同意無論任何理由，您都會在所填選擇之取餐時間到場取餐。\n" +
            "2.您非常同意，取餐時間、外送地址是由您本人自行填寫。\n" +
            "3.當您使用外送服務時，您全權擔保您所填寫的地址，可以聯繫到您本人。\n" +
            "4.當我們接獲店家投訴，您棄單造成店家損失，我們可能會與您聯繫。\n" +
        "5.當您棄單造成店家損失，我們可能會依照法律途徑處理。"
        
        let alert = UIAlertController(title: "使用者規範", message: msg, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "我知道了", style: .default){ _ in
            if !self.readRuleBtn.isSelected {
                self.readRuleBtn.isSelected = true
                self.readRuleBtn.setImage(UIImage(named:"cbSelect"), for: .normal)
            }
        })
        
        let subView1: UIView = alert.view.subviews[0]
        let subView2: UIView = subView1.subviews[0]
        let subView3: UIView = subView2.subviews[0]
        let subView4: UIView = subView3.subviews[0]
        let subView5: UIView = subView4.subviews[0]
        let message: UILabel = subView5.subviews[1] as! UILabel
        message.textAlignment = .left
        self.present(alert, animated: false)
    }
    
    @IBAction func submitAction(_ sender: UIButton) {
        
        if self.dateSelect.text == "" {
            let alert = UIAlertController(title: Optional.none, message: "請選擇取餐時間", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "我知道了", style: .default))
            self.present(alert, animated: false)
        }else if !self.readRuleBtn.isSelected {
            let alert = UIAlertController(title: Optional.none, message: "請先閱讀\"使用者規範\"，再提交訂單。", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "我知道了", style: .default))
            self.present(alert, animated: false)
        }else {
            let alert = UIAlertController(title: "確認訂單", message: "取餐時間: " + self.dateSelect.text!, preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "取消", style: .destructive))
            alert.addAction(UIAlertAction(title: "確認", style: .default){ _ in
                
                // TODO 送出訂單
                self.orderDetail.fetch_date = DateTimeHelper.formToString(date: self.dateSelect.text!, fromDate: "yyyy-MM-dd HH:mm")
                self.orderDetail.user_message = StringsHelper.replace(str: self.userMssage.text!, of: " ", with: "")
                self.orderDetail.use_bonus = "0"
                self.orderDetail.user_name = self.name.text
                self.orderDetail.user_phone = self.phone.text
                
                // 判斷外帶或內送
                self.orderDetail.order_type = OrderType()
                self.orderDetail.order_type.billing = "COUPON"
                self.orderDetail.order_type.delivery = self.deliveryDate == 0 ? "OUT" : "IN"
                
                
                let req: ReqData = ReqData()
                req.uuid = self.activities.act_uuid
                req.data = OrderDetail.toJson(structs: self.orderDetail)
                ApiManager.resEventSubmit(req: req, ui: self, onSuccess: {
                    let alert = UIAlertController(title: "兌換成功", message: "", preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "我知道了", style: .default) { _ in
                        self.navigationController?.popViewController(animated: true)
                    })
                    self.present(alert, animated: false)
                }, onFail: { err_msg in
                    let alert = UIAlertController(title: "兌換失敗", message: err_msg, preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "我知道了", style: .default) { _ in
                        
                    })
                    self.present(alert, animated: false)
                })
            })
            self.present(alert, animated: false)
        }
    }
    
    func reTimeRange (picker: UIDatePicker, isInit: Bool){
        
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
        components.minute = 20
        let minDate: Date = calendar.date(byAdding: components, to: currentDate)!
        picker.minimumDate = minDate
        
        self.selectDate = DateTimeHelper.dateToStringForm(date: minDate, form: "yyyy-MM-dd HH:mm")
        
        if !isInit {
            self.dateSelect.inputView = picker
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func textViewDidChange(_ textView: UITextView) {
        if textView.text.count == 0 {
            self.placeHolder.isHidden = false
        } else {
            self.placeHolder.isHidden = true
        }
    }
    
    // 攔截 input view open
    func textFieldDidBeginEditing(_ textField: UITextField) {
        if self.dateSelect.isEditing {
            self.reTimeRange(picker: self.datePicker, isInit: false)
            self.dateSelect.text = self.selectDate
        }
    }
    
    //紅利選擇
    // MARK: - UIPickerView Methods
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return self.deliveryDates.count
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return self.deliveryDates[row]
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        self.deliveryDate = row
    }
    
    // 鍵盤點擊背景縮放
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.view.endEditing(true)
    }
    
    
    // item table
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.orderDetail != nil ? self.orderDetail.orders.count : 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: UIIdentifier.CELL.rawValue, for: indexPath) as! ActRESItemSubmitTVCell
        
        
        var content: String = ""
    
        let data: OrderData = self.orderDetail.orders[indexPath.row]
        print(data)
        
        content += data.item.food_name + ": " +
        StringsHelper.padEnd(str: "x" + data.count, minLength: 15, of: " ") +
        "\n規格: " +
        StringsHelper.padEnd(str: data.item.scopes[0].name, minLength: 20 , of: " ") +
        "\n附加: " +
        data.item.opts.reduce("", { (s: String, i: ItemVo) -> String in
                return s + "\n" + StringsHelper.padEnd(str:"- " + i.name, minLength: 20 , of: " ") +
                    StringsHelper.padEnd(str: "  ", minLength: 10 , of: " ")
            }) + (data.item.opts.count == 0 ? "- 無\n" : "\n") +
        "需求: "
        for d in data.item.demands {
            content += d.name + ": " + d.datas[0].name + ", "
        }
        
        cell.item.text = content
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    }
}
