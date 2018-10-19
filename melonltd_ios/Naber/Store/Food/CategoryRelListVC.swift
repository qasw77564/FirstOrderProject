//
//  FoodVC.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/6/8.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit


class CategoryRelListVC: UIViewController, UITableViewDelegate, UITableViewDataSource, UITextFieldDelegate {

    
    var IS_SORT_CAT: Bool = false
    var categorys: [CategoryRelVo] = []
    @IBOutlet weak var sortBtnItem: UIBarButtonItem!
    
    @IBOutlet weak var tableView: UITableView! {
        didSet {
            self.tableView.dataSource = self
            self.tableView.delegate = self
            let refreshControl: UIRefreshControl = UIRefreshControl()
            refreshControl.attributedTitle = NSAttributedString(string: "Pull to refresh", attributes: [NSAttributedStringKey.foregroundColor: UIColor(red: 188/255, green: 188/255, blue: 188/255, alpha: 1.0)])
            refreshControl.addTarget(self, action: #selector(refresh), for: .valueChanged)
            refreshControl.tintColor = UIColor.clear
            self.tableView.addSubview(refreshControl)
            
            let gestureRecognizer: UITapGestureRecognizer = UITapGestureRecognizer.init(target: self, action: #selector(hideKeyboard))
            gestureRecognizer.numberOfTapsRequired = 1
            gestureRecognizer.cancelsTouchesInView = false
            self.tableView.addGestureRecognizer(gestureRecognizer)
        }
    }
    // UITableView click bk hide keyboard
    @objc func hideKeyboard(sender: Any){
        self.view.endEditing(true)
    }
    
    @objc func refresh(sender: UIRefreshControl){
        sender.endRefreshing()
        self.loadData(refresh: true)
    }
    
    func loadData(refresh: Bool){
        self.categorys.removeAll()
        self.tableView.reloadData()
        ApiManager.sellerCategoryList(ui: self, onSuccess: { categorys in
            let sortedArray = categorys.sorted(by: { (o1, o2) -> Bool in
                Int(o1.top)! < Int(o2.top)!
            })
            self.categorys.append(contentsOf: sortedArray)
            self.tableView.reloadData()
        }) { err_msg in
            print(err_msg)
        }
    }
    
    @IBOutlet weak var categoryName: UITextField! {
        didSet {
            self.categoryName.leftViewMode = .always
            self.categoryName.leftView = UIView(frame: CGRect(x: 0, y: 0, width: 10, height: 10))
        }
    }
    
    @IBOutlet weak var addCategoryBtn: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
   override func viewWillAppear(_ animated: Bool) {
        self.IS_SORT_CAT = false
        self.sortBtnItem.title = "編輯排序"
        self.loadData(refresh: true)
        self.categoryName.text = ""
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func show(_ vc: UIViewController, sender: Any?) {
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.categorys.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
    
        let cell = tableView.dequeueReusableCell(withIdentifier: UIIdentifier.CELL.rawValue, for: indexPath) as! CategoryRelCell
        let status: SwitchStatus = SwitchStatus.of(name: self.categorys[indexPath.row].status)
        
        cell.switchBtn.isOn = status.status()
        cell.deleteBtn.tag = indexPath.row
        cell.editBtn.tag = indexPath.row
        cell.switchBtn.tag = indexPath.row
        cell.name.text = self.categorys[indexPath.row].category_name
        cell.sortNum.delegate = self
        cell.sortNum.tag = indexPath.row
        cell.sortNum.text = self.categorys[indexPath.row].top
        cell.sortNum.isEnabled = self.IS_SORT_CAT
        return cell
    }
    
    @IBAction func addCategoryAction(_ sender: UIButton) {
        if self.categoryName.text == "" {
            let alert = UIAlertController(title: Optional.none, message: "請輸入種類名稱", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "我知道了", style: .default))
            self.present(alert, animated: false)
        }else {
            let reqData: ReqData = ReqData()
            reqData.name = StringsHelper.replace(str: self.categoryName.text!, of: " ", with: "")
            ApiManager.sellerAddCategory(req: reqData, ui: self, onSuccess: { categoryRel in
                if let vc = UIStoryboard(name: UIIdentifier.STORE.rawValue, bundle: nil).instantiateViewController(withIdentifier: "FoodList") as? FoodListVC {
                    vc.categoryRel = categoryRel
                    self.navigationController?.pushViewController(vc, animated: true)
                }
            }) { err_msg in
                print(err_msg)
            }
        }
    }
    
    @IBAction func deleteCategoryAction(_ sender: UIButton) {
        let alert = UIAlertController(title: Optional.none, message: "請注意刪除種類\n，將會影響種類下的產品!", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "取消", style: .destructive))
        alert.addAction(UIAlertAction(title: "確定刪除", style: .default, handler: { _ in
            let reqData: ReqData = ReqData()
            reqData.uuid = self.categorys[sender.tag].category_uuid
            ApiManager.sellerDeleteCategory(req: reqData, ui: self, onSuccess: {
                self.categorys.remove(at: sender.tag)
                self.tableView.reloadData()
            }) { err_msg in
                print(err_msg)
            }
        }))
        self.present(alert, animated: false)
    }
    
    @IBAction func editCategoryAction(_ sender: UIButton) {
        if let vc = UIStoryboard(name: UIIdentifier.STORE.rawValue, bundle: nil).instantiateViewController(withIdentifier: "FoodList") as? FoodListVC {
            vc.categoryRel = self.categorys[sender.tag]
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }

    @IBAction func changeCategoryAction(_ sender: UISwitch) {
        let status: SwitchStatus = SwitchStatus.of(name: self.categorys[sender.tag].status)
        
        if status.status() != sender.isOn {
            let reqData: ReqData = ReqData()
            reqData.uuid = self.categorys[sender.tag].category_uuid
            reqData.status = SwitchStatus.of(bool: sender.isOn)
            ApiManager.sellerChangeCategoryStatus(req: reqData, ui: self, onSuccess: {
                self.categorys[sender.tag].status = reqData.status
                self.tableView.reloadData()
            }) { err_msg in
                sender.isOn = status.status()
                self.tableView.reloadData()
            }
        }
    }
    
    @IBAction func changeSortNum(_ sender: UITextField) {
        let top: String = sender.text! == "" ? "0" : sender.text!
        self.categorys[sender.tag].top = Int(top)!.description
        print("changeSortNum : ", self.categorys[sender.tag].top)
    }
    
    // 限制輸入長度
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        guard let text = textField.text else { return true }
        let newLength: Int = text.count + string.count - range.length
        if !CharacterSet.decimalDigits.isSuperset(of: CharacterSet(charactersIn: string)) {
            return false
        }
        return newLength <= 2
    }
    
    
    @IBAction func sortCategoryAction(_ sender: UIBarButtonItem) {
        
        if self.IS_SORT_CAT {
            let alert = UIAlertController(title: Optional.none, message: "確認排序結果", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "取消", style: .destructive){ _ in
                self.loadData(refresh: true)
            })
            alert.addAction(UIAlertAction(title: "確定", style: .default){ _ in
                ApiManager.sellerSortCategory(req: self.categorys, ui: self, onSuccess: { categorys in
                    self.categorys.removeAll()
                    
                    let sortedArray = categorys.sorted(by: { (o1, o2) -> Bool in
                        Int(o1.top)! < Int(o2.top)!
                    })
                    
                    self.categorys.append(contentsOf: sortedArray)
                    self.tableView.reloadData()
                }) {err_msg in
                    print(err_msg)
                    self.loadData(refresh: true)
                }
            })
            self.present(alert, animated: false)
        }
        
        self.IS_SORT_CAT = !self.IS_SORT_CAT
        self.tableView.reloadData()
        sender.title = self.IS_SORT_CAT ? "儲存排序" : "編輯排序"
  
    }
}



