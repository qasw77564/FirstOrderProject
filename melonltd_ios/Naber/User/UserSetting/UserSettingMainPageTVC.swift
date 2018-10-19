//
//  UserSettingMainPageTableViewController.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/5/24.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit
import Firebase

class UserSettingMainPageTVC: UITableViewController {

    var account: AccountInfoVo!
    
    @IBOutlet weak var photo: UIImageView!
    @IBOutlet weak var accunt: UILabel!
    @IBOutlet weak var bonus: UILabel!
    @IBOutlet weak var version: UILabel!
    @IBOutlet weak var sound: UISwitch!
    @IBOutlet weak var shake: UISwitch!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if Model.CURRENT_FIRUSER == Optional.none {
            Auth.auth().signIn(withEmail: "naber_android@gmail.com", password: "melonltd1102") { (user, error) in
                Model.CURRENT_FIRUSER = user?.user
            }
        }
        
        let refreshControl: UIRefreshControl = UIRefreshControl()
        refreshControl.attributedTitle = NSAttributedString(string: "Pull to refresh", attributes: [NSAttributedStringKey.foregroundColor: UIColor(red: 188/255, green: 188/255, blue: 188/255, alpha: 1.0)])
        refreshControl.addTarget(self, action: #selector(refresh), for: .valueChanged)
        refreshControl.tintColor = UIColor.clear
        
        self.view.addSubview(refreshControl)
    }
    
    @objc func refresh(sender: UIRefreshControl){
        sender.endRefreshing()
        self.loadData(refresh: true)
    }
    
    func loadData (refresh: Bool){
        ApiManager.userFindAccountInfo(ui: self, onSuccess: { account in
            self.account = account
            self.accunt.text = account?.account
            
            self.bonus.text = "0"
            if let userBonus: Int = Int((account?.bonus)!) {
                if let userUseBonus: Int = Int((account?.use_bonus)!) {
                    self.bonus.text = (userBonus - userUseBonus).description
                }
            }
            
            self.photo.setImage(with: URL(string: account?.photo ?? ""), transformer: TransformerHelper.transformer(identifier: account?.photo ?? ""),  completion: { image in
                if image == nil {
                    self.photo.image = UIImage(named: "LogoReverse")
                }
            })
        }) { err_msg in
            print(err_msg)
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.sound.isOn = UserSstorage.getSound()!;
        self.shake.isOn = UserSstorage.getShake()!;
        if let version: String = Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString")! as? String {
             self.version.text = "V." + version
        }
        
        self.loadData(refresh: true)
        
    }
    
    override func show(_ vc: UIViewController, sender: Any?) {
        if let nvc: UserSettingAccountDetailTVC = vc as? UserSettingAccountDetailTVC {
            nvc.account = self.account
            self.navigationController?.pushViewController(nvc, animated: true)
        }else {
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        
        // Dispose of any resources that can be recreated.
    }
    

     @IBAction func changeSound(_ sender: UISwitch) {
        UserSstorage.setSound(sender.isOn)
    }
    
    @IBAction func changeShake(_ sender: UISwitch) {
        UserSstorage.setShake(sender.isOn)
    }
}
