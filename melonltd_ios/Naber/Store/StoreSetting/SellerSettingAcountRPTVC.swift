//
//  SellerSettingAcountRPTVC.swift
//  Naber
//
//  Created by 王淳彦 on 2018/9/13.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

//
//  UserSettingAcountResetPasswordTableViewController.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/5/25.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class SellerSettingAcountRPTVC: UITableViewController {
    
    @IBOutlet weak var oldPassword: UITextField!
    @IBOutlet weak var newPassword: UITextField!
    @IBOutlet weak var checkPassword: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    @IBAction func submitAction(_ sender: UIButton) {
        if(judgmentInput()){
            let resetpassword: ReqData = ReqData()
            resetpassword.old_password = self.oldPassword.text
            resetpassword.password = self.newPassword.text
            resetpassword.phone = UserSstorage.getAccountInfo()?.phone
            ApiManager.reseatPassword(req: resetpassword, ui: self, onSuccess: {
                let alert = UIAlertController(title: "", message: "重設成功！" , preferredStyle: .alert)
                alert.addAction(UIAlertAction.init(title: "返回登入畫面", style: .default, handler: { _ in
                    if let vc = UIStoryboard(name: UIIdentifier.MAIN.rawValue, bundle: nil).instantiateViewController(withIdentifier: "LoginHomeRoot") as? LoginHomeRootUINC {
                        UserSstorage.clearUserLoginTime()
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
    
    func judgmentInput() -> Bool {
        var msg: String = ""
        if(self.newPassword.text!) != (self.checkPassword.text!){
            msg = "請確定新的密碼與確認密碼一致"
        }
        if(!ValidateHelper.validatePass(withPassword: self.newPassword.text!) ){
            msg = "密碼長度需6~20碼，\n並英數組合，\n不可為特殊符號！"
        }
        if(self.newPassword.text == ""){
            msg = "請輸入新的密碼"
        }
        if(self.oldPassword.text == "" ){
            msg =  "請輸入舊密碼"
        }
        if msg != "" {
            let alert = UIAlertController(title: Optional.none, message: msg, preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "我知道了", style: .default))
            self.present(alert, animated: false)
        }
        return msg == ""
    }
}

