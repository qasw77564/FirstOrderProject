//
//  UserSettingAboutUsTableViewController.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/5/24.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class UserSettingAboutUsTVC: UITableViewController {

    @IBOutlet weak var AboutUsText: UILabel!
    @IBOutlet weak var ApplyOfSellerText: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.AboutUsText.text = StringsHelper.replace(str: Model.ALL_BULLETINS["ABOUT_US"]!, of: "$split", with: "\n" )
        
        self.ApplyOfSellerText.text = StringsHelper.replace(str: Model.ALL_BULLETINS["APPLY_OF_SELLER"]!,of: "$split", with: "\n" )
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
  
}




