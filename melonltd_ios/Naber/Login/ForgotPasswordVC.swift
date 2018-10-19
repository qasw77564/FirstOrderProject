//
//  ForgotPasswordViewController.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/5/22.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//
import UIKit

class ForgotPasswordVC: UIViewController {
    
    @IBOutlet weak var phone: UITextField!
    @IBOutlet weak var email: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.phone.text = ""
        self.email.isHidden = true
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    @IBAction func sendToServerForAskPassword(_ sender: Any) {
        
        if judgmentInput(){
            let reqDate : ReqData = ReqData()
//            reqDate.email = self.email.text
            reqDate.email = ""
            reqDate.phone = self.phone.text
            ApiManager.forgetPassword(req: reqDate, ui: self, onSuccess: {
                let alert = UIAlertController(title: Optional.none, message: "密碼已經傳送至於您的手機，請於簡訊功能查看！", preferredStyle: .alert)
                alert.addAction(UIAlertAction.init(title: "關閉", style: .default, handler: { _ in
                    if let vc = UIStoryboard(name: UIIdentifier.MAIN.rawValue, bundle: nil).instantiateViewController(withIdentifier: "LoginHomeRoot") as? LoginHomeRootUINC {
                        self.present(vc, animated: false, completion: nil)
                    }
                }))
                self.present(alert, animated: false)
            }) { err_msg in
                let alert = UIAlertController(title: Optional.none, message: err_msg, preferredStyle: .alert)
                alert.addAction(UIAlertAction.init(title: "cancel", style: .cancel, handler: { _ in
                    
                }))
                self.present(alert, animated: false)
            }
        }
    }
//    找回密碼判斷
        func judgmentInput() -> Bool {
            var msg: String = ""
//            var email: String = ""
            if(!ValidateHelper.shared.isVaildTelPhone(withCellPhone: self.phone.text!) ){
                msg = "請輸入正確的手機號碼"
            }
            if self.phone.text == "" {
                msg = "手機號碼不可為空"
            }
            if msg != "" {
                let alert = UIAlertController(title: Optional.none, message: msg, preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "關閉", style: .cancel, handler: { _ in
                }))
                self.present(alert, animated: false)
            }
            return msg == ""
        }
    // 鍵盤點擊背景縮放
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.view.endEditing(true)
    }
}
