//
//  StoreSettingMainPageTableViewController.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/6/6.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class StoreSettingMainPageTVC: UITableViewController {

    
    @IBOutlet weak var account: UILabel!
    @IBOutlet weak var version: UILabel!
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if let version: String = Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString")! as? String {
            self.version.text = "V." + version
        }
        
        ApiManager.bulletin(ui: self, onSuccess: { bulletins in
            Model.ALL_BULLETINS.removeAll()
            Model.NABER_BULLETINS.removeAll()
            bulletins.forEach({ b in
                Model.ALL_BULLETINS[(b?.bulletin_category)!] = b?.content_text
            })
        }) { err_msg in
            print(err_msg)
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.account.text = UserSstorage.getAccountInfo()?.account
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
   
}
