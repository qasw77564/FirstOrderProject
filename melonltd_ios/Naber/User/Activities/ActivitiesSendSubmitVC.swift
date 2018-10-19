//
//  ActivitiesDetailVC.swift
//  Naber
//
//  Created by 王淳彦 on 2018/9/21.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class ActivitiesSendSubmitVC: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource {
    
    var act: ActivitiesVo!
    var account: AccountInfoVo!
    var regions: [SubjectionRegionVo] = []
    
    @IBOutlet weak var tableView: UITableView! {
        didSet {
            let gestureRecognizer: UITapGestureRecognizer = UITapGestureRecognizer.init(target: self, action: #selector(hideKeyboard))
            gestureRecognizer.numberOfTapsRequired = 1
            gestureRecognizer.cancelsTouchesInView = false
            self.tableView.addGestureRecognizer(gestureRecognizer)
        }
    }
    // UITableView click bk hide keyboard
    @objc func hideKeyboard(sender: Any){
        self.view.endEditing(true)
    }
    
    @IBOutlet weak var photo: UIImageView!
    @IBOutlet weak var name: UITextField!
    @IBOutlet weak var userBonus: UILabel!
    @IBOutlet weak var phone : UITextField!
    @IBOutlet weak var email : UITextField!
    @IBOutlet weak var city : UITextField!
    @IBOutlet weak var area : UITextField!
    
    @IBOutlet weak var address : UITextField!
    
    @IBOutlet weak var titleText: UILabel!
    @IBOutlet weak var needBonusText: UILabel!
    @IBOutlet weak var contentText: UILabel!
    
    var cityIndex: Int = 0
    var areaIndex: Int = 0;
    var addsToolbar: UIToolbar {
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
    var addsPicker: UIPickerView {
        get {
            let pickerView = UIPickerView()
            pickerView.dataSource = self
            pickerView.delegate = self
            pickerView.backgroundColor = UIColor.white
            return pickerView
        }
    }
    @objc func onDoneBtn(sender: UIBarButtonItem) {
        if self.city.isEditing {
            self.city.text = self.regions[self.cityIndex].city
        }else {
            self.area.text = self.regions[self.cityIndex].areas[self.areaIndex].area
        }
        
        self.addsPicker.reloadComponent(0)
        self.addsPicker.reloadInputViews()
        self.addsPicker.reloadAllComponents()
        self.area.isHidden = false
        self.view.endEditing(true)
    }
    
    @objc func onCancelBtn(sender: UIBarButtonItem) {
        self.view.endEditing(true)
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        self.city.inputView = self.addsPicker
        self.city.inputAccessoryView = self.addsToolbar
        self.area.inputView = self.addsPicker
        self.area.inputAccessoryView = self.addsToolbar
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.area.isHidden = true
        
        ApiManager.userFindAccountInfo(ui: self, onSuccess: { account in
            self.account = account
            
            if let userBonus: Int = Int((account?.bonus)!) {
                if let userUseBonus: Int = Int((account?.use_bonus)!) {
                    self.userBonus.text = "擁有紅利 " + (userBonus - userUseBonus).description
                }
            }
        }) { err_msg in
            
        }
        
        self.regions.removeAll()
        ApiManager.getSubjectionRegions(ui: self, onSuccess: { regions in
            self.regions.append(contentsOf: regions)
        }) { err_msg in
            self.regions.append(contentsOf: SubjectionRegionVo.parseArray(src: NaberConstant.SUBJECTION_REGIONS))
        }
        let account:AccountInfoVo = UserSstorage.getAccountInfo()!
        
        self.name.text = account.name
        self.phone.text = account.phone
        
        self.titleText.text = self.act.title
        self.needBonusText.text = "所需紅利(" + self.act.need_bonus + ")"
//        self.contentText.text = self.act.content_text
        
        self.photo.setImage(with:URL(string: self.act.photo ?? ""), transformer: TransformerHelper.transformer(identifier: self.act.photo ?? ""), completion :{ image in
            if image == nil {
                self.photo.image = UIImage(named: "naber_default_image.png")
            }
        })
    }
    
    override func show(_ vc: UIViewController, sender: Any?) {
        print(vc)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    @IBAction func exchangeBtnAction(_ sender: UIButton){
        let errMsg: String = self.verifyInput();
        if !errMsg.isEmpty {
            let alert = UIAlertController(title: "系統提示", message: errMsg, preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "我知道了", style: .default))
            self.present(alert, animated: false)
        }else {
            
            let alert = UIAlertController(title: "系統提示", message: "確定使用" + self.act.need_bonus.description + "紅利，兌換" + self.act.title, preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "確定", style: .default){ _ in
                let req: ReqData = ReqData()
                req.uuid = self.act.act_uuid
                let subjection: SubjectionRegionVo = self.regions[self.cityIndex]
                
                let contact: ContactInfo = ContactInfo()
                contact.name = self.name.text
                contact.phone = self.phone.text
                contact.email = self.email.text
                contact.code = subjection.areas[self.areaIndex].postal_code
                contact.city = self.city.text
                contact.area = self.area.text
                contact.address = self.address.text
                req.data = ContactInfo.toJson(structs: contact)
                
                ApiManager.actSubmit(req: req, ui: self, onSuccess: {
                    let alert = UIAlertController(title: "系統提示", message: "兌換成功，Naber專人將與您聯", preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "我知道了", style: .default) {_ in
                        self.navigationController?.popViewController(animated: true)
                    })
                    self.present(alert, animated: false)
                }) { err_msg in
                    let alert = UIAlertController(title: "系統提示", message: err_msg, preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: "我知道了", style: .default))
                    self.present(alert, animated: false)
                }
            })
            alert.addAction(UIAlertAction(title: "取消", style: .destructive))
            self.present(alert, animated: false)
        }
    }

    func verifyInput () -> String {
        
        if (self.name.text?.isEmpty)! {
            return "請確認已輸入名稱"
        }
        if (self.city.text?.isEmpty)! {
            return "請確認已輸入完整地址"
        }
        if (self.email.text?.isEmpty)! {
            return "請確認已輸入Email"
        }
        if !ValidateHelper.shared.isValidEmail(withEmail: self.email.text!) {
             return "請確輸入正確Email"
        }
        if (self.area.text?.isEmpty)! {
            return "請確認已輸入完整地址"
        }
        if (self.address.text?.isEmpty)! {
            return "請確認已輸入完整地址"
        }
        
        // TODO 判斷使用者所剩下紅利
        if self.account != nil {
            let bonus: Int = Int(self.account.bonus)!
            let needBonus: Int = Int(self.act.need_bonus)!
            if needBonus > bonus {
                return "紅利點數不足兌換該項目"
            }
        }else {
            return "紅利點數不足兌換該項目"
        }
        return ""
    }
    
    // Address Optional Pick
    // MARK: - UIPickerView Methods
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        if self.city.isEditing {
            return self.regions.count
        }else {
            return self.regions[self.cityIndex].areas.count
        }
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        if self.city.isEditing {
            return self.regions[row].city
        } else {
            return self.regions[self.cityIndex].areas[row].area
        }
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        if self.city.isEditing {
            if self.cityIndex != row {
                self.area.text = ""
                self.areaIndex = 0
                self.area.inputView = self.addsPicker
            }
            self.cityIndex = row
        }else {
            self.areaIndex = row
        }
    }
    
    // 鍵盤點擊背景縮放
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.view.endEditing(true)
    }
    
}
