//
//  UserPageUITabBarController.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/6/8.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class UserPageUITabBarController: UITabBarController {

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
//        ApiManager.storeCategoryList(ui: self, onSuccess: { categorys in
//            NaberConstant.FILTER_CATEGORYS.removeAll()
//            NaberConstant.FILTER_CATEGORYS.append(contentsOf: categorys)
//        }) { err_msg in
//            print(err_msg)
//        }
//
//        ApiManager.storeAreaList(ui: self, onSuccess: { categorys in
//            NaberConstant.FILTER_AREAS.removeAll()
//            NaberConstant.FILTER_AREAS.append(contentsOf: categorys)
//        }) { err_msg in
//            print(err_msg)
//        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
