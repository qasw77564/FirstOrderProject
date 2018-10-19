//
//  UserSettingTutorialTableViewController.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/5/25.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class UserSettingTutorialTVC: UITableViewController {

    @IBOutlet weak var teachingText: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.teachingText.text = StringsHelper.replace(str: Model.ALL_BULLETINS["TEACHING"]!, of: "$split", with: "\n" )
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
              // Dispose of any resources that can be recreated.
    }

}
