//
//  UserSettingAccountDetailTableViewController.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/5/24.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit
import AVFoundation
import Photos

class UserSettingAccountDetailTVC: UITableViewController , UIImagePickerControllerDelegate , UINavigationControllerDelegate {
    
    var account: AccountInfoVo! = Optional.none
    
    @IBOutlet weak var photo: UIImageView!
    @IBOutlet weak var name: UILabel!
    @IBOutlet weak var phone: UILabel!
    @IBOutlet weak var email: UILabel!
    @IBOutlet weak var birthday: UILabel!
    @IBOutlet weak var bonus: UILabel!
    @IBOutlet weak var identity: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        
        if self.account != nil {
            self.name.text = self.account.name
            self.phone.text = self.account.phone
            self.email.text = self.account.email
            self.birthday.text = self.account.birth_day
            
            self.bonus.text = "0"
            if let userBonus: Int = Int((self.account?.bonus)!) {
                if let userUseBonus: Int = Int((self.account?.use_bonus)!) {
                    self.bonus.text = (userBonus - userUseBonus).description
                }
            }
            self.identity.text = Identity.init(rawValue: self.account.identity)?.getName()
            
            self.photo.setImage(with: URL(string: account?.photo ?? "" ), transformer: TransformerHelper.transformer(identifier: account?.photo ?? "" ),  completion: { image in
                if image == nil {
                    self.photo.image = UIImage(named: "LogoReverse")
                }
            })
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    @IBAction func changePhoto(_ sender: UIButton) {
        let picker: UIImagePickerController = UIImagePickerController()
        picker.delegate = self
        
        let alert = UIAlertController.init(title: Optional.none , message: Optional.none , preferredStyle: .actionSheet)
        
        alert.addAction(UIAlertAction(title: "相機", style: .default, handler: { _ in
            self.openCamera()
        }))
        
        alert.addAction(UIAlertAction(title: "相簿", style: .default, handler: { _ in
            self.openPhotoLibrary()
        }))
        
        alert.addAction(UIAlertAction(title: "取消", style: .destructive))
        
        alert.popoverPresentationController?.sourceView = self.view
        alert.popoverPresentationController?.sourceRect = CGRect.init(x: self.view.bounds.width/2 ,y: self.view.bounds.height , width: 1.0, height: 1.0)
        self.present(alert, animated: true, completion: nil)
    }
    
    //.notDetermined:  第一次安裝App，尚未選取狀態
    //.restricted:  此應用程序沒有被授權訪問的照片數據
    //.denied:  已經選取並拒絕，無訪問權限狀態
    //.authorized:  已经有权限
    func openPhotoLibrary(){
        let pickCtl: UIImagePickerController = UIImagePickerController.init()
        if  UIImagePickerController.isSourceTypeAvailable(.photoLibrary){
            PHPhotoLibrary.requestAuthorization({ status in
                if status == .authorized {
                    pickCtl.delegate = self as UIImagePickerControllerDelegate & UINavigationControllerDelegate
                    pickCtl.sourceType = .photoLibrary;
                    pickCtl.allowsEditing = true
                    self.present(pickCtl, animated: true, completion: Optional.none)
                }else if status == .denied || status == .restricted {
                    self.showAlert(withTitle: "相簿權限已關閉", andMessage: "如要開啟相簿權限，可以點\"前往設置\"，\n將相簿讀取和寫入權限開啟。", isGoSetting: true)
                }
            })
        }else {
            self.showAlert(withTitle: "沒有相簿功能", andMessage: "You can't take photo, there is no photo library.", isGoSetting: false)
        }
    }
    
    func openCamera() {
        let pickCtl: UIImagePickerController = UIImagePickerController()
        if UIImagePickerController.isSourceTypeAvailable(.camera) {
            AVCaptureDevice.requestAccess(for: .video, completionHandler: { _ in
                let status: AVAuthorizationStatus = AVCaptureDevice.authorizationStatus(for: .video)
                if status == .authorized {
                    pickCtl.delegate = self as UIImagePickerControllerDelegate & UINavigationControllerDelegate
                    pickCtl.sourceType = .camera
                    pickCtl.allowsEditing = true
                    self.present(pickCtl, animated: true, completion: Optional.none)
                }else if  status == .denied || status == .restricted {
                    self.showAlert(withTitle: "相機權限已關閉", andMessage: "如要開啟相機權限，可以點\"前往設置\"，\n將相機權限開啟。", isGoSetting: true)
                }
            })
        } else {
            self.showAlert(withTitle: "沒有相機設備", andMessage: "You can't take photo, there is no camera.", isGoSetting: false)
        }
    }
    
    // 可選去設定頁面
    func showAlert(withTitle title: String, andMessage message: String , isGoSetting: Bool) {
        let alert: UIAlertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        if isGoSetting {
            alert.addAction(UIAlertAction(title: "前往設置", style: .default) { _ in
                let url = URL(string: UIApplicationOpenSettingsURLString)
                if UIApplication.shared.canOpenURL(url!){
                    UIApplication.shared.open(url!, options: [:])
//                    UIApplication.shared.open(url!, options: [:], completionHandler: { (ist) in
//                        print(ist)
//                    })
                }
            })
            alert.addAction(UIAlertAction(title: "返回", style: .destructive))
        } else {
            alert.addAction(UIAlertAction(title: "我知道了", style: .default))
        }
        self.present(alert, animated: true)
    }
    
    

    // 接到相機 ＆ 相簿回傳的data
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        
        let originalImage: UIImage = info[UIImagePickerControllerOriginalImage] as! UIImage
        let resizedImage: UIImage = ImageHelper.resizeImage(originalImage: originalImage, minLenDP: 120)

        var imageName: String = "defult.jpg"
        if self.account != nil {
            imageName = self.account.account_uuid + ".jpg"
        }else {
            imageName = UserSstorage.getAutho() + ".jpg"
        }
        
        ImageHelper.upLoadImage(data: UIImageJPEGRepresentation(resizedImage, 1.0)!, sourcePath: NaberConstant.STORAGE_PATH_USER, fileName: imageName, onGetUrl: { url in
            let reqData: ReqData = ReqData()
            reqData.uuid = self.account.account_uuid
            reqData.date = url.absoluteString
            reqData.type = "USER"
            ApiManager.uploadPhoto(req: reqData, ui: self, onSuccess: { urlString in
                self.photo.setImage(with: URL(string:  url.absoluteString ), transformer: TransformerHelper.transformer(identifier:  url.absoluteString ),  completion: { image in
                    if image == nil {
                        self.photo.image = UIImage(named: "LogoReverse")
                    }
                })
            }, onFail: { err_msg in
                print(err_msg)
            })
        }) { err_msg in
            print(err_msg)
        }
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func logout(_ sender: Any) {
        
        ApiManager.logout(structs: UserSstorage.getAccountInfo(), ui: self, onSuccess: {
            UserSstorage.clearUserData()
            if let vc = UIStoryboard(name: UIIdentifier.MAIN.rawValue, bundle: nil).instantiateViewController(withIdentifier: "LoginHomeRoot") as? LoginHomeRootUINC {
                self.present(vc, animated: false, completion: nil)
            }
        }) { err_msg in
            UserSstorage.clearUserData()
            if let vc = UIStoryboard(name: UIIdentifier.MAIN.rawValue, bundle: nil).instantiateViewController(withIdentifier: "LoginHomeRoot") as? LoginHomeRootUINC {
                self.present(vc, animated: false, completion: nil)
            }
        }
    }
}
