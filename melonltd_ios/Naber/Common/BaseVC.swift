//
//  CommonVC.swift
//  Naber
//
//  Created by 王淳彦 on 2018/7/24.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit
import Firebase
//import UIApplication

class BaseVC: UIViewController{
    let USER_TYPES: [Identity] = Identity.getUserValues()
    
    
    @IBOutlet weak var btn: UIButton!
    @IBOutlet weak var intro: UIImageView!

    override func loadView() {
        super.loadView()
        self.btn.isHidden = true
    }
    
    
    @objc func applicationDidBecomeActive(notification: NSNotification) {
        self.checkAppVersion {
            self.gatIntroBulletin {
                self.checkLoginAccount()
            }
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        NotificationCenter.default.addObserver(self, selector: #selector(applicationDidBecomeActive), name: .UIApplicationDidBecomeActive, object: nil)
    }
    
    
    func gatIntroBulletin(complete: @escaping () -> ()){
        self.btn.isEnabled = false
        self.btn.isHidden = true
        ApiManager.appIntroBulletin(ui: self, onSuccess: { url in
            self.intro.setImage(with: URL(string: url), transformer: TransformerHelper.transformer(identifier: url),  completion: { image in
                if image == nil {
                    complete()
                } else {
                    self.btn.isEnabled = true
                    self.btn.isHidden = false
                }
            })
        }) { err_msg in
            self.btn.isEnabled = true
            self.btn.isHidden = false
            complete()
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        // 取得 FireBase 權限
        if Model.CURRENT_FIRUSER == nil {
            Auth.auth().signIn(withEmail: "naber_android@gmail.com", password: "melonltd1102") { (user, error) in
                Model.CURRENT_FIRUSER = user?.user
            }
        }
    }
    
    
    
    func checkAppVersion( complete: @escaping () -> ()){
        ApiManager.checkAppVersion(ui: self, onSuccess: { appVersion in
            if let version: String = Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString")! as? String {
                if !appVersion.version.elementsEqual(version) {
                    let alert = UIAlertController(title: "NABER 系統提示", message: "您目前的APP版本(V" + version + ")，不是最新版本(V" + appVersion.version + ")，\n為了您的使用權益，請前往App Store更新您的App", preferredStyle: .alert)
                    if appVersion.need_upgrade.elementsEqual("Y") {
                        UserSstorage.clearUserLoginTime()
                        alert.addAction(UIAlertAction(title: "前往更新", style: .default){ _ in
                            let urlStr = "https://itunes.apple.com/us/app/naber/id1426204136?l=zh&ls=1&mt=8"
                            if #available(iOS 10.0, *) {
                                UIApplication.shared.open(URL(string: urlStr)!, options: [ : ], completionHandler: { success in
                                })
                            } else {
                                UIApplication.shared.openURL(URL(string: urlStr)!)
                            }
                        })
                    }else {
                        alert.addAction(UIAlertAction(title: "我知道了", style: .default) { _ in
                            complete()
                        })
                    }
                    self.present(alert, animated: false)
                } else {
                    complete()
                }
            }
        }) { err_msg in
            complete()
        }
    }

    // 判斷是否登入過無超過兩週，並判斷上次登入的帳號類別
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
//        self.checkAppVersion {
//            self.gatIntroBulletin {
//                self.checkLoginAccount()
//            }
//        }
    }
    
    @IBAction func goToNext(_ sender: UIButton) {
        self.checkLoginAccount()
    }
    
    func checkLoginAccount(){
//        let now: Int = Int(Date().timeIntervalSince1970 * 1000)
        // 更改為 如果沒登出就一直保持登入狀態
//        let now: Int = Int(Date().timeIntervalSince1970 * 1000)
//        if now - NaberConstant.REMEMBER_DAY < UserSstorage.getLoginTime() {
        if let account: AccountInfoVo = UserSstorage.getAccountInfo() {
//            let account: AccountInfoVo? = UserSstorage.getAccountInfo()
            if self.USER_TYPES.contains(Identity(rawValue: (account.identity)!)!) {
                // 已登入使用者
                if let vc = UIStoryboard(name: UIIdentifier.USER.rawValue, bundle: nil).instantiateViewController(withIdentifier: "UserPage") as? UserPageUITabBarController {
                    self.present(vc, animated: false, completion: nil)
                }
            } else if Identity.SELLERS == Identity(rawValue: (account.identity)!)! {
                // 已登入過商家
                if let vc = UIStoryboard(name: UIIdentifier.STORE.rawValue, bundle: nil).instantiateViewController(withIdentifier: "StorePage") as? StorePageUITabBarController {
                    self.present(vc, animated: false, completion: nil)
                }
            } else {
                UserSstorage.clearUserData()
                if let vc = UIStoryboard(name: UIIdentifier.MAIN.rawValue, bundle: nil).instantiateViewController(withIdentifier: "LoginHomeRoot") as? LoginHomeRootUINC {
                    self.present(vc, animated: false, completion: nil)
                }
            }
        } else {
            if let vc = UIStoryboard(name: UIIdentifier.MAIN.rawValue, bundle: nil).instantiateViewController(withIdentifier: "LoginHomeRoot") as? LoginHomeRootUINC {
                self.present(vc, animated: false, completion: nil)
            }
        }
    }



    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

}
