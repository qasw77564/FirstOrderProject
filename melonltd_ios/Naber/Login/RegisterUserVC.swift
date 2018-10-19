//
//  RegisterUserViewController.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/5/22.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class RegisterUserVC: UIViewController, UIAlertViewDelegate {
    
    @IBOutlet weak var getVerifyCodeBtn: UIButton!
    @IBOutlet weak var checkImageView: UIImageView!
    @IBOutlet weak var phone: UITextField!
    @IBOutlet weak var verifyCode: UITextField!
    @IBOutlet weak var submitBtn: UIButton!

    var smsCode: SMSCodeVo!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.smsCode = SMSCodeVo()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        self.smsCode = SMSCodeVo()
        self.verifyCode.text = ""
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    @IBAction func getVerifyCode(_ sender: UIButton) {
        sender.isHidden = true
        if !ValidateHelper.shared.isVaildTelPhone(withCellPhone: self.phone.text!) {
            let alert = UIAlertController(title: Optional.none, message: "請輸入正確手機號碼，避免無法正常接收SMS", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "我知道了", style: .default){ _ in
               sender.isHidden = false
            })
            self.present(alert, animated: false)
            return
        }
        if (self.checkImageView.image?.isEqual(UIImage(named: "cbNoSelect")))! {
            let alert = UIAlertController(title: Optional.none, message: "請先閱讀隱私權政策條款，再進行註冊！", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "我知道了", style: .default){ _ in
                sender.isHidden = false
            })
            self.present(alert, animated: false)
            return
        }
        
        let reqCode: SMSCodeVo = SMSCodeVo()
        reqCode.phone = self.phone.text
        ApiManager.getSMSCode(structs: reqCode, ui: self, onSuccess: { code in
            sender.isHidden = false
            self.smsCode.batch_id = code.batch_id
        }) { err_msg in
            let alert = UIAlertController(title: Optional.none, message: StringsHelper.replace(str: err_msg, of: "$split", with: "\n"), preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "我知道了", style: .default){ _ in
                sender.isHidden = false
            })
            self.present(alert, animated: false)
        }
    }
    
    @IBAction func readPrivacyPolicy (_ sender: Any){
        let msg: String =
        "\n\n第一條 個人資料之蒐集與目的\n" +
        "成為NABER的會員，以便我們提供會員服務，需要您提供手機號碼、電子郵件、地址、生日、及建立新的帳號及密碼。在您加入NABER會員時，軟體系統將依您所要求的服務內容，擷取提供該等服務所必須蒐集之可供辨識個人之資料。若您不同意提供上述個人資料，請勿繼續使用NABER服務。\n\n" +
        "第二條 個人資料之處理及利用\n" +
        "您的個人資料會被我們蒐集，並將其使用在您所需的個別服務上。\nNABER亦可能將個人資料與其他資料合併，用以提供並改進或開發我們的產品、服務、內容及廣告。\n\n" +
        "第三條 個人資料之保護\n" +
        "您的資料將安全的儲存在NABER會員資料庫系統中，供軟體系統使用、處理，用以進行科技管理、資(通)訊業務與資料庫管理以及會員(籍)管理。\n\n" +
        "第四條 網站對外之相關連結\n" +
        "NABER可能提供其他網站或程式的連結，您可以經由NABER所提供的連結，點選進入其他網站。但該連結並不適用本App的隱私權保護政策，您必須參考該連結網站中的隱私保護政策。\n\n" +
        "第五條\n" +
        "與第三人共用個人資料之政策NABER絕不會提供、交換、出租或出售任何您的個人資料給其他個人、團體、私人企業或公務機關，但有法律依據或合約義務者，不在此限。\n\n" +
        "前項但書之情形包括不限於：\n" +
        "1.經由您書面同意。\n" +
        "2.法律明文規定。\n" +
        "3.為免除您生命、身體、自由或財產上之危險。\n" +
        "4.與公務機關或學術研究機構合作，基於公共利益為統計或學術研究而有必要，且資料經過提供者處理或蒐集著依其揭露方式無從識別特定之當事人。\n" +
        "5.當您在NABER裡的行為，違反服務條款或可能損害或妨礙NABER與其他使用者權益或導致任何人遭受損害時，經NABER管理單位研析揭露您的個人資料是為了辨識、聯絡或採取法律行動所必要者。\n" +
        "6.有利於您的權益。\n" +
        "7.NABER委託廠商協助蒐集、處理或利用您的個人資料時，將對委外廠商或個人善盡監督管理之責。\n\n"
        
        let alert = UIAlertController(title: "隱私權條款", message: msg, preferredStyle: .alert)
 
        let subView1: UIView = alert.view.subviews[0]
        let subView2: UIView = subView1.subviews[0]
        let subView3: UIView = subView2.subviews[0]
        let subView4: UIView = subView3.subviews[0]
        let subView5: UIView = subView4.subviews[0]
        // let title: UILabel = subView5.subviews[0] as! UILabel
        let message: UILabel = subView5.subviews[1] as! UILabel
        message.textAlignment = .left
        alert.addAction(UIAlertAction(title: "我知道了", style: .default){ _ in
            if (self.checkImageView.image?.isEqual(UIImage(named: "cbNoSelect")))! {
                self.checkImageView.image = UIImage(named: "cbSelect")
            }
        })
        self.present(alert, animated: false)
        
    }

    override func show(_ vc: UIViewController, sender: Any?) {

    }
    
    @IBAction func isReadBtn (_ sender: UIButton){
        if (self.checkImageView.image?.isEqual(UIImage(named: "cbNoSelect")))! {
            self.checkImageView.image = UIImage(named: "cbSelect")
        }
    }

    @IBAction func nextRegister (_ sender: UIButton){
        
        // TODO debug
//        if let vc = UIStoryboard(name: UIIdentifier.MAIN.rawValue, bundle: nil).instantiateViewController(withIdentifier: "RegisterUserDetail") as? RegisterUserDetailVC {
//            vc.phone = "12132423"
//            self.navigationController?.pushViewController(vc, animated: true)
//            return
//        }
        
        self.smsCode.phone = self.phone.text
        self.smsCode.code = self.verifyCode.text
        let alert = UIAlertController(title: "", message: "", preferredStyle: .alert)
        
        if self.smsCode.phone == nil && self.smsCode.batch_id == nil {
            alert.message = "請先進行簡訊驗證！"
            alert.addAction(UIAlertAction(title: "我知道了", style: .default){ _ in
                sender.isHidden = false
            })
            self.present(alert, animated: false)
            return
        }
        
        if (self.checkImageView.image?.isEqual(UIImage(named: "cbNoSelect")))! {
            alert.message = "請先閱讀隱私權政策條款，再進行註冊！"
            alert.addAction(UIAlertAction(title: "我知道了", style: .default))
            self.present(alert, animated: false)
            return
        }
        
        if self.verifyCode.text == "" {
            alert.message = "請輸入驗證碼！"
            alert.addAction(UIAlertAction(title: "我知道了", style: .default){ _ in
                sender.isHidden = false
            })
            self.present(alert, animated: false)
            return
        }
        
        if self.smsCode.batch_id == nil {
            alert.message = "驗證碼已失效！"
            alert.addAction(UIAlertAction(title: "我知道了", style: .default){ _ in
                sender.isHidden = false
            })
            self.present(alert, animated: false)
            return
        }

        ApiManager.verifySMSCode(structs: self.smsCode, ui: self, onSuccess: {
            if let vc = UIStoryboard(name: UIIdentifier.MAIN.rawValue, bundle: nil).instantiateViewController(withIdentifier: "RegisterUserDetail") as? RegisterUserDetailVC {
                vc.phone = self.smsCode.phone
                self.navigationController?.pushViewController(vc, animated: true)
            }
        }) { err_msg in
            let alert = UIAlertController(title: Optional.none, message: StringsHelper.replace(str: err_msg, of: "$split", with: "\n"), preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "我知道了", style: .default){ _ in
                self.verifyCode.text = ""
            })
            self.present(alert, animated: false)
        }
    }
    
    // 鍵盤點擊背景縮放
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.view.endEditing(true)
    }
    
}
