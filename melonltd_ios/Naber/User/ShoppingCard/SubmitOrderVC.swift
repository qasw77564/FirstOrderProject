//
//  SubmitOrderVC.swift
//  Naber
//
//  Created by 王淳彦 on 2018/7/31.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit
class SubmitOrderVC : UIViewController, UITextViewDelegate, UIPickerViewDelegate, UIPickerViewDataSource ,UITextFieldDelegate {

    var orderIndex: Int!
    var orderDetail: OrderDetail!
    
    @IBOutlet weak var table: UITableView! {
        didSet {
            let gestureRecognizer: UITapGestureRecognizer = UITapGestureRecognizer.init(target: self, action: #selector(hideKeyboard))
            gestureRecognizer.numberOfTapsRequired = 1
            gestureRecognizer.cancelsTouchesInView = false
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
    @IBOutlet weak var price: UILabel!
    @IBOutlet weak var bonus: UILabel!
    
    // 紅利選單
    var canBnonusDates: [String] = []
    var bnonusDate: Int = -1
    @IBOutlet weak var selectBnonus: UITextField! {
        didSet {
            self.selectBnonus.isEnabled = false
        }
    }
    var nonusToolbar: UIToolbar {
        get {
            let nonusToolbar = UIToolbar(frame: CGRect(x: 0, y: 0, width: view.frame.width, height: 44))
            let doneBtn = UIBarButtonItem(title: "選擇紅利" , style: .done, target: self, action: #selector(onDoneBtn))
            let cancelBtn = UIBarButtonItem(title: "取消折抵" , style: .plain, target: self, action: #selector(onCancelBtn))
            let space = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
            nonusToolbar.items = [cancelBtn, space, doneBtn]
            nonusToolbar.barTintColor = UIColor.white
            return nonusToolbar
        }
    }
    var nonusPicker: UIPickerView {
        get {
            let pickerView = UIPickerView()
            pickerView.dataSource = self
            pickerView.delegate = self
            pickerView.backgroundColor = UIColor.white
            return pickerView
        }
    }
    
    @objc func onDoneBtn(sender: UIBarButtonItem) {
        if self.selectBnonus.isEditing && self.bnonusDate >= 0 {
            self.selectBnonus.text = self.canBnonusDates[self.bnonusDate]
            // 訂單總金額 與紅利 重新計算
            let price: Double = self.orderDetail.orders.reduce(0.0, { (sum, num) -> Double in
                return sum + Double(num.item.price)!
            })
            self.calculatePrice(price: price - (Double(self.bnonusDate + 1) * 3.0))
        }
        self.view.endEditing(true)
    }
    
    @objc func onCancelBtn(sender: UIBarButtonItem) {
        // 取消折抵
        // 訂單總金額 與紅利 重新計算
        self.bnonusDate = -1
        self.selectBnonus.text = ""
        self.selectBnonus.placeholder = "選取紅利"
        let price: Double = self.orderDetail.orders.reduce(0.0, { (sum, num) -> Double in
            return sum + Double(num.item.price)!
        })
        self.calculatePrice(price: price)
        self.view.endEditing(true)
    }
    
    ///
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
        // 紅利選單
        self.selectBnonus.inputView = self.nonusPicker
        self.selectBnonus.inputAccessoryView = self.nonusToolbar
        
        //取餐方式
        self.delivery.inputView = self.deliveryPicker
        self.delivery.inputAccessoryView = self.deliveryToolbar
        self.delivery.text = self.deliveryDates[0]
    }

    override func viewWillAppear(_ animated: Bool) {
        self.reTimeRange(picker: self.datePicker, isInit: false)
        self.dateSelect.text = ""
        self.bnonusDate = -1
        self.selectBnonus.text = ""
        self.selectBnonus.placeholder = "選取紅利"
        self.orderDetail = UserSstorage.getShoppingCartDatas()[self.orderIndex]
//        let orderDates: [OrderData] = orderDetail.orders
        self.name.text = orderDetail.user_name
        self.phone.text = orderDetail.user_phone
        let price: Double = self.orderDetail.orders.reduce(0.0, { (sum, num) -> Double in
            return sum + Double(num.item.price)!
        })
        
        self.calculatePrice(price: price)
        
        if let can_discount: String = orderDetail.can_discount {
            if can_discount.elementsEqual("Y") {
                ApiManager.userFindAccountInfo(ui: self, onSuccess: { account in
                    if let userBonus: Int = Int((account?.bonus)!) {
                        var count: Int = 0
                        if let userUseBonus: Int = Int((account?.use_bonus)!) {
                            // 多端登入，使用紅利有可能超出所得紅利
                            if userUseBonus < userBonus {
                                count = (userBonus - userUseBonus) / 10
                            }
                        }
                        if count <= 0 {
                            self.selectBnonus.placeholder = "紅利不足折抵"
                        } else if price < 3 {
                            self.selectBnonus.placeholder = "該品項無法折抵"
                        } else if count > 0 {
                            // 產出pick資料
                            self.canBnonusDates.removeAll()
                            for index in 1..<count + 1 {
                                // 連同判斷 訂單總金額
                                if index > Int(price / 3) {
                                } else {
                                    self.canBnonusDates.append((index * 10).description + " 點紅利，折抵" + (index * 3).description + "元" )
                                }
                            }
                            self.selectBnonus.isEnabled = true
                        }
                    }
                }) { err_msg in
                    print(err_msg)
                    self.selectBnonus.placeholder = "紅利不足折抵"
                }
            }else {
               self.selectBnonus.placeholder = "該店家不提供紅利"
            }
        }
    }
    
    func calculatePrice (price: Double){
        self.price.text = "$" + Int(price).description
        if let can_discount: String = self.orderDetail.can_discount {
            self.bonus.text = can_discount == "Y" ? "應得紅利 " + Int(floor(price / 10.0)).description : "該店家不提供紅利"
        }
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
        // let title: UILabel = subView5.subviews[0] as! UILabel
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
                
                self.orderDetail = UserSstorage.getShoppingCartDatas()[self.orderIndex]
                self.orderDetail.fetch_date = DateTimeHelper.formToString(date: self.dateSelect.text!, fromDate: "yyyy-MM-dd HH:mm")
                self.orderDetail.user_message = StringsHelper.replace(str: self.userMssage.text!, of: " ", with: "")
                self.orderDetail.use_bonus = "0"
                
                // TODO 送出之前 判斷 order type
                // 如果計算金額結果 == 原購物車內訂單金額 detail.order_type.billing = "ORIGINAL"
                // 如果計算金額結果 ！= 原購物車內訂單金額 detail.order_type.billing = "DISCOUNT"
                if self.bnonusDate >= 0 {
                    self.orderDetail.order_type.billing = "DISCOUNT"
                    self.orderDetail.use_bonus = ((self.bnonusDate + 1) * 10).description
                }
                // 判斷外帶或內送
                self.orderDetail.order_type.delivery = self.deliveryDate == 0 ? "OUT" : "IN"
                
                ApiManager.userOrderSubmit(req: self.orderDetail, ui: self, onSuccess: {
                    // 提交訂單成功 把該筆訂單從手機記憶中移除
                    var shoppingCartData: [OrderDetail] = UserSstorage.getShoppingCartDatas()
                    shoppingCartData.remove(at: self.orderIndex)
                    UserSstorage.setShoppingCartDatas(datas: shoppingCartData)
                    
                    let msg: String = "商家已看到您的訂單囉！\n" +
                        "你可前往訂單頁面查看商品狀態，\n" +
                    "提醒您，商品只保留至取餐時間後20分鐘。"
                    let alert = UIAlertController(title: Optional.none, message: msg, preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "我知道了", style: .default, handler: { _ in
                        // 跳制訂記錄頁面
                        self.tabBarController?.selectedIndex = 3
                        // 把當前畫面返回到購物車列表
                    self.navigationController?.popToViewController((self.navigationController?.viewControllers[(self.navigationController?.viewControllers.count)! - 2])!, animated: true)
                    }))
                    
                    self.present(alert, animated: false)
                }) { err_msg in
                    let alert = UIAlertController(title: Optional.none, message: StringsHelper.replace(str: err_msg, of: "$split", with: "\n"), preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "我知道了", style: .default))
                    self.present(alert, animated: false)
                }
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
        if self.delivery.isEditing {
            return self.deliveryDates.count
        }else {
            return self.canBnonusDates.count
        }
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        
        if self.delivery.isEditing {
            return self.deliveryDates[row]
        }else {
            return self.canBnonusDates[row]
        }
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        if self.delivery.isEditing {
            self.deliveryDate = row
        }else {
            self.bnonusDate = row
        }
    }
    
    // 鍵盤點擊背景縮放
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.view.endEditing(true)
    }
    
}
