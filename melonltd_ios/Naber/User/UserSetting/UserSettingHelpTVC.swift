//
//  UserSettingHelpTableViewController.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/5/25.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class UserSettingHelpTVC: UITableViewController {

    @IBOutlet weak var contactUsText: UILabel!
    @IBOutlet weak var faqText: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.faqText.text = StringsHelper.replace(str: Model.ALL_BULLETINS["FAQ"]!, of: "$split", with: "\n" )

        self.contactUsText.text = StringsHelper.replace(str: Model.ALL_BULLETINS["CONTACT_US"]!,of: "$split", with: "\n" )
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}
