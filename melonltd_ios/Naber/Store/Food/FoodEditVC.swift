//
//  FoodEditVC.swift
//  Naber
//
//  Created by 王淳彦 on 2018/8/7.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit
import AVFoundation
import Photos
import Firebase

class FoodEditVC : UIViewController, UITableViewDelegate, UITableViewDataSource, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    
    var food: FoodVo!
    
    @IBOutlet weak var photo: UIImageView!
    @IBOutlet weak var foodName: UITextField!
    @IBOutlet weak var tableView: UITableView! {
        didSet {
            self.tableView.delegate = self
            self.tableView.dataSource = self
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.foodName.text = self.food.food_name
        // TODO
        
        self.photo.setImage(with: URL(string: self.food.photo ?? ""), transformer: TransformerHelper.transformer(identifier: self.food.photo ?? ""),  completion: { image in
            if image == nil {
                self.photo.image = UIImage(named: "Logo")
            }
        })
        
        if Model.CURRENT_FIRUSER == Optional.none {
            Auth.auth().signIn(withEmail: "naber_android@gmail.com", password: "melonltd1102") { (user, error) in
                Model.CURRENT_FIRUSER = user?.user
            }
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return food.food_data.demands.count + 2
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: UIIdentifier.CELL.rawValue, for: indexPath) as! FoodMainCell
        
        cell.name.isHidden = true
        cell.demandsName.isHidden = true
        cell.deleteDemandsBtn.isHidden = true
        cell.rootIndex = indexPath.row
        cell.items.removeAll()
        
        switch (indexPath.row) {
        case 0:
            cell.items.append(contentsOf: self.food.food_data.scopes)
            cell.tag = indexPath.row
            cell.addBtn.tag = indexPath.row
            cell.name.isHidden = false
            cell.name.text = "規格(必選)"
            break
        case 1:
            cell.items.append(contentsOf: self.food.food_data.opts)
            cell.tag = indexPath.row
            cell.addBtn.tag = indexPath.row
            cell.name.isHidden = false
            cell.name.text = "追加項目"
            break
        case 2...:
            cell.items.append(contentsOf: self.food.food_data.demands[indexPath.row - 2].datas)
            cell.addBtn.tag = indexPath.row
            cell.tag = indexPath.row - 2
            cell.demandsName.isHidden = false
            cell.demandsName.tag = indexPath.row - 2
            cell.deleteDemandsBtn.isHidden = false
            cell.deleteDemandsBtn.tag = indexPath.row
            cell.demandsName.text = self.food.food_data.demands[indexPath.row - 2].name
            break
        default:
            break
        }
       cell.subTableView.reloadData()
        return cell
    }

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        switch (indexPath.row) {
        case 0:
            return CGFloat(food.food_data.scopes.count * 40 + 40 + 20)
        case 1:
            return CGFloat(food.food_data.opts.count * 40 + 40 + 20)
        case 2...:
            return CGFloat(food.food_data.demands[indexPath.row - 2 ].datas.count * 40 + 40 + 20)
        default:
            return 0
        }
    }
    
    @IBAction func addItems(_ sender: UIButton) {
        switch (sender.tag) {
        case 0:
            self.food.food_data.scopes.append(ItemVo.init(name: "", price: "0", tag: 0))
            break
        case 1:
            self.food.food_data.opts.append(ItemVo.init(name: "", price: "0", tag: 0))
            break
        case 2...:
            self.food.food_data.demands[sender.tag - 2].datas.append(ItemVo.init(name: "", price: Optional.none, tag: 0))
            break
        default:
            break
        }
        self.tableView.reloadData()
    }
    
    
    // UIButton tag = rootIndex UIButton.image tag = subIndex
    @IBAction func deleteItems(_ sender: UIButton) {
        switch (sender.tag) {
        case 0:
            if self.food.food_data.scopes.count == 1 {
                let alert = UIAlertController(title: "不可刪除", message: "NABER提示\n品項規格至少需要一筆資了以上", preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "我知道了", style: .default))
                self.present(alert, animated: false)
            }else {
                self.food.food_data.scopes.remove(at: (sender.imageView?.tag)!)
            }
            break
        case 1:
            self.food.food_data.opts.remove(at: (sender.imageView?.tag)!)
            break
        case 2...:
            self.food.food_data.demands[sender.tag - 2].datas.remove(at: (sender.imageView?.tag)!)
            break
        default:
            break
        }
        self.tableView.reloadData()
    }
    
    @IBAction func addDemands(_ sender: UIButton) {
        self.food.food_data.demands.append(DemandsItemVo.init(name: ""))
        self.tableView.reloadData()
    }

    @IBAction func deleteDemands(_ sender: UIButton) {
        self.food.food_data.demands.remove(at: sender.tag - 2 )
        self.tableView.reloadData()
    }
    
    @IBAction func changeDemandName(_ sender: UITextField) {
        self.food.food_data.demands[sender.tag].name = sender.text
    }

    // leftView == root index
    @IBAction func changeName(_ sender: UITextField) {
        switch (sender.leftView?.tag)! {
        case 0:
            self.food.food_data.scopes[sender.tag].name = sender.text
            break
        case 1:
            self.food.food_data.opts[sender.tag].name = sender.text
            break
        case 2...:
            self.food.food_data.demands[(sender.leftView?.tag)! - 2].datas[sender.tag].name = sender.text
            break
        default:
            break
        }
    }
    
    // leftView == root index
    @IBAction func changePrice(_ sender: UITextField) {
        switch (sender.leftView?.tag)! {
        case 0:
            self.food.food_data.scopes[sender.tag].price = sender.text
            break
        case 1:
            self.food.food_data.opts[sender.tag].price = sender.text
            break
        default:
            break
        }
    }
    
    // 儲存編輯
    @IBAction func saveEdit(_ sender: UIButton) {
        let message: String = self.verifyData()
        if message != "" {
            let alert = UIAlertController(title: "", message: "NABER提示\n" + message, preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "我知道了", style: .default))
            self.present(alert, animated: false)
        }else {
            self.food.food_data.scopes.forEach { item in
                item.name = StringsHelper.replace(str: item.name, of: " ", with: "")
                item.price = (item.price as NSString).integerValue.description
            }
            self.food.food_data.opts.forEach { item in
                item.name = StringsHelper.replace(str: item.name, of: " ", with: "")
                item.price = (item.price as NSString).integerValue.description
            }
            
            self.food.food_data.demands.forEach { dItem in
                dItem.name = StringsHelper.replace(str: dItem.name, of: " ", with: "")
                dItem.datas.forEach { item in
                    item.name = StringsHelper.replace(str: item.name, of: " ", with: "")
                }
            }
            
            ApiManager.sellerFoodUpdate(req: self.food, ui: self, onSuccess: {
                self.navigationController?.popViewController(animated: true)
            }) { err_msg in
                print(err_msg)
            }
        }
    }
    
    // 取消編輯
    @IBAction func cancelEdit(_ sender: UIButton) {
        let alert = UIAlertController(title: "", message: "請確認您所編輯的產品是否已經儲存，\n否則離開後所編輯之數據將會清空！", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "取消", style: .destructive))
        alert.addAction(UIAlertAction(title: "確定離開", style: .default, handler : { _ in
            self.navigationController?.popViewController(animated: true)
        }))
        self.present(alert, animated: false)
    }
    
    @IBAction func uploadPhoto(_ sender: UIButton) {
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
        let resizedImage: UIImage = ImageHelper.resizeImage(originalImage: originalImage, minLenDP: 100)
        ImageHelper.upLoadImage(data: UIImageJPEGRepresentation(resizedImage, 1.0)!, sourcePath: NaberConstant.STORAGE_PATH_FOOD, fileName: self.food.food_uuid + ".jpg", onGetUrl: { url in
            let reqData: ReqData = ReqData()
            reqData.uuid = self.food.food_uuid
            reqData.date = url.absoluteString
            reqData.type = "FOOD"
            ApiManager.uploadPhoto(req: reqData, ui: self, onSuccess: { urlString in
                self.photo.setImage(with: URL(string: url.absoluteString), transformer: TransformerHelper.transformer(identifier: url.absoluteString),  completion: { image in
                    if image == nil {
                        self.photo.image = UIImage(named: "Logo")
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
    
    func verifyData() -> String {
        
        let checkScopesName: Bool = self.food.food_data.scopes.filter { item -> Bool in return item.name == ""}.count != 0
        if checkScopesName {
            return "規格名稱不可空白！"
        }
        
        let checkScopesPrice: Bool = self.food.food_data.scopes.filter { item -> Bool in return item.price == "0" || item.price == "" }.count != 0
        if checkScopesPrice {
            return "規格價格不可為0！"
        }
        
        let checkOptsName: Bool = self.food.food_data.opts.filter { item -> Bool in return item.name == "" }.count != 0
        if checkOptsName {
            return "追加項目名稱不可空白！"
        }
        
        let checkOptsPrice: Bool = self.food.food_data.opts.filter { item -> Bool in return item.price == "0" || item.price == "" }.count != 0
        if checkOptsPrice {
            return "追加項目價格不可為0！"
        }
        
        let chechkDemandName: Bool = self.food.food_data.demands.filter { demand -> Bool in return demand.name == "" }.count != 0
        if chechkDemandName {
            return "需求主名稱不可空白！"
        }
        
        var msg: String = ""
        self.food.food_data.demands.forEach { demand in
            let checkSubName: Bool = demand.datas.filter{ item -> Bool in return item.name == "" }.count != 0
            if checkSubName {
                msg = "需求細項名稱不可空白！"
            }
        }
        
        self.food.food_data.demands.forEach{ demand in
            if demand.datas.count == 0 {
                msg = "需求:(" + demand.name + ")不可少餘一筆需求細項"
            }
        }
        
        return msg
    }
}
