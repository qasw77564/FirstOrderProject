//
//  RegisterUserDetailViewController.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/5/22.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class RegisterUserDetailVC: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource  {
    
    var phone: String! = Optional.none
    var identity: String = ""
    var schoolName: String = ""
    var identityPickerView: UIPickerView {
        get {
            let pickerView = UIPickerView()
            pickerView.dataSource = self
            pickerView.delegate = self
            pickerView.backgroundColor = UIColor.white
            return pickerView
        }
    }
    
    var dateToolbar: UIToolbar {
        get {
            let dateToolbar = UIToolbar(frame: CGRect(x: 0, y: 0, width: view.frame.width, height: 44))
            let doneBtn = UIBarButtonItem(title: "完成", style: .done, target: self, action: #selector(doneTimePick))
            let flexible = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
            let cancelBtn = UIBarButtonItem(title:"取消", style: .plain,target: self,action: #selector(cancelTimePick))
            dateToolbar.items = [cancelBtn, flexible, doneBtn]
            dateToolbar.barTintColor = UIColor.white
            return dateToolbar
        }
    }
    
    var birthdayTmp : String = DateTimeHelper.dateToStringForm(date: Date(), form: "yyyy-MM-dd")
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
    @objc func onDateChanged(sender: UIDatePicker) {
        self.birthdayTmp = DateTimeHelper.dateToStringForm(date: sender.date, form: "yyyy-MM-dd")
    }
    
    @objc func doneTimePick(sender: UIBarButtonItem){
        self.birthday.text = self.birthdayTmp
        self.view.endEditing(true)
    }

    @objc func cancelTimePick(sender: UIBarButtonItem){
        self.view.endEditing(true)
    }
    
    // 性別選擇器
    let genders : [String] = ["男","女"]
    var genderIndex : Int = 0
    var genderToolbar: UIToolbar {
        get {
            let genderToolbar = UIToolbar(frame: CGRect(x: 0, y: 0, width: view.frame.width, height: 44))
            let doneBtn = UIBarButtonItem(title: "完成", style: .done, target: self, action: #selector(doneGenderPick))
            let flexible = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
            let cancelBtn = UIBarButtonItem(title:"取消", style: .plain,target: self,action: #selector(cancelTimePick))
            genderToolbar.items = [cancelBtn, flexible, doneBtn]
            genderToolbar.barTintColor = UIColor.white
            return genderToolbar
        }
    }
    var genderPicker: UIPickerView {
        get {
            let pickerView = UIPickerView()
            pickerView.dataSource = self
            pickerView.delegate = self
            pickerView.backgroundColor = UIColor.white
            return pickerView
        }
    }
    
    @objc func doneGenderPick(sender: UIBarButtonItem){
        self.gender.text = self.genders[self.genderIndex]
        self.view.endEditing(true)
    }
    
    @IBOutlet weak var identityText : UITextField! {
        didSet {
            identityText.inputView = self.identityPickerView
        }
    }
    @IBOutlet weak var name: UITextField!
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var passwordCheck: UITextField!
    @IBOutlet weak var birthday: UITextField!
    @IBOutlet weak var gender: UITextField!

    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.identity = Array(NaberConstant.IDENTITY_OPTS_TEMP)[0].key
        self.schoolName = NaberConstant.IDENTITY_OPTS_TEMP[identity]![0]
        self.identityText.text =  self.identity + ", " + self.schoolName
        
        self.birthday.inputView = self.datePicker
        self.birthday.inputAccessoryView = self.dateToolbar
        
        // 性別選擇器
        self.gender.inputView = self.genderPicker
        self.gender.inputAccessoryView = self.genderToolbar
        
    }
    
    @IBAction func goBackHomePage(_ sender: Any) {
        if let vc = UIStoryboard(name: UIIdentifier.MAIN.rawValue, bundle: nil).instantiateViewController(withIdentifier: "LoginHomeRoot") as? LoginHomeRootUINC
        {
            present(vc, animated: false, completion: nil)
        }
    }
    
    @IBAction func sendToSeverForRegisterUser(_ sender: Any) {
        if verifyInput() {
            let account: AccountInfoVo = AccountInfoVo()
            account.name = StringsHelper.replace(str: self.name.text!, of: " ", with: "")
            account.password = self.password.text
            account.phone = self.phone
            account.level = "USER"
            account.identity = Identity.toEnum(name: self.identity).rawValue
            account.school_name = self.schoolName
            
            account.email = self.email.text
            account.birth_day = self.birthday.text
            account.gender = self.genderIndex == 0 ? "M" : "W"
           
            ApiManager.userRegistered(structs: account, ui: self, onSuccess: {
                let alert = UIAlertController(title: "註冊成功", message: "歡迎加入NABER！" , preferredStyle: .alert)
                alert.addAction(UIAlertAction.init(title: "返回登入畫面", style: .default, handler: { _ in
                    if let vc = UIStoryboard(name: UIIdentifier.MAIN.rawValue, bundle: nil).instantiateViewController(withIdentifier: "LoginHomeRoot") as? LoginHomeRootUINC {
                        self.present(vc, animated: false, completion: nil)
                    }
                }))
                self.present(alert, animated: false)
            }) { err_msg in
                let alert = UIAlertController(title: Optional.none, message: err_msg , preferredStyle: .alert)
                alert.addAction(UIAlertAction.init(title: "我知道了", style: .default))
                self.present(alert, animated: false)
            }
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func verifyInput() -> Bool {
        var msg: String = ""
        
        if self.gender.text == "" {
            msg = "性別不可為空"
        }
        
        if self.birthday.text == "" {
            msg = "生日不可為空"
        }
        
        if self.passwordCheck.text != self.password.text{
            msg = "請確定密碼與確認密碼一致"
        }
        
        if !ValidateHelper.validatePass(withPassword: self.password.text!){
            msg = "密碼長度需6~20碼，\n並英數組合，\n不可有特殊符號！"
        }
        
        if self.password.text == "" {
            msg = "密碼不可為空"
        }
        
        if (self.name.text?.count)! < 2 || (self.name.text?.count)! > 5 {
            msg = "姓名格式不正確長度為2~5"
        }
        
        if !ValidateHelper.shared.isValidEmail(withEmail: self.email.text!) {
            msg = "Email格式錯誤"
        }
        
        if self.email.text == "" {
            msg = "Email不可為空"
        }
        
        if self.name.text == "" {
            msg = "姓名不可為空"
        }
        
        if msg != "" {
            let alert = UIAlertController(title: Optional.none, message: msg,   preferredStyle: .alert)
            alert.addAction(UIAlertAction.init(title: "我知道了", style: .default, handler: { _ in
                self.passwordCheck.text = ""
            }
            ))
            self.present(alert, animated: false)
        }
        return msg == ""
    }
    
    //身份選擇
    // MARK: - UIPickerView Methods
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        if self.gender.isEditing {
            return 1
        }else {
            return 2
        }
    }
    
    // 身份類型 && 性別選擇器
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        if self.gender.isEditing {
            return self.genders.count
        }else {
            if component == 0 {
                return NaberConstant.IDENTITY_OPTS_TEMP.count
            } else {
                let array: [String] = Array(NaberConstant.IDENTITY_OPTS_TEMP)[pickerView.selectedRow(inComponent: 0)].value
                return array.count
            }
        }
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        if self.gender.isEditing {
            return self.genders[row]
        }else {
            if component == 0 {
                pickerView.reloadComponent(1)
                return Array(NaberConstant.IDENTITY_OPTS_TEMP)[row].key
            } else {
                let array: [String] =  Array(NaberConstant.IDENTITY_OPTS_TEMP)[pickerView.selectedRow(inComponent: 0)].value
                if row > array.count {
                    return ""
                } else {
                    return array[row]
                }
            }
        }
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        if self.gender.isEditing {
            self.genderIndex = row
        }else {
            //滑動左邊pick的時候
            if component == 0 {
                self.identity = Array(NaberConstant.IDENTITY_OPTS_TEMP)[row].key
                self.schoolName = NaberConstant.IDENTITY_OPTS_TEMP[self.identity]![0]
                pickerView.reloadComponent(1)
            } else {
                let array: [String] =  Array(NaberConstant.IDENTITY_OPTS_TEMP)[pickerView.selectedRow(inComponent: 0)].value
                if row < array.count {
                    self.schoolName = array[row]
                }
            }
            self.identityText.text =  self.identity + ", " + self.schoolName
        }
    }
    
    // 鍵盤點擊背景縮放
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.view.endEditing(true)
    }
    
    func reTimeRange (picker: UIDatePicker){
        let currentDate: Date = Date()
        var calendar: Calendar = Calendar(identifier: Calendar.Identifier.gregorian)
        calendar.timeZone = TimeZone.init(identifier: "Asia/Taipei")!
        calendar.locale = Locale.init(identifier: "zh_TW")
        var components: DateComponents = DateComponents()
        components.calendar = calendar
        let maxDate: Date = calendar.date(byAdding: components, to: currentDate)!
        picker.maximumDate = maxDate
    }

}


