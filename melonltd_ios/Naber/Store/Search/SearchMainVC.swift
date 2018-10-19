//
//  SearchMainVC.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/6/8.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class SearchMainVC: UIViewController, UITextFieldDelegate,  UITableViewDelegate ,UITableViewDataSource {

    var orders: [OrderVo] = []
    @IBOutlet weak var phone: UITextField!{
        didSet {
            self.phone.leftViewMode = .always
            self.phone.leftView = UIView(frame: CGRect(x: 0, y: 0, width: 10, height: 10))
            self.phone.delegate = self
        }
    }
    
    @IBOutlet weak var table: UITableView! {
        didSet {
            self.table.dataSource = self
            self.table.delegate = self
            let refreshControl: UIRefreshControl = UIRefreshControl()
            refreshControl.attributedTitle = NSAttributedString(string: "Pull to refresh", attributes: [NSAttributedStringKey.foregroundColor: UIColor(red: 188/255, green: 188/255, blue: 188/255, alpha: 1.0)])
            refreshControl.addTarget(self, action: #selector(refresh), for: .valueChanged)
            refreshControl.tintColor = UIColor.clear
            self.table.addSubview(refreshControl)
        }
    }
    
    @objc func refresh(sender: UIRefreshControl){
//        sender.endRefreshing()
        self.loadData(refresh: true)
        sender.endRefreshing()
    }
    
    func loadData(refresh: Bool){
        self.view.endEditing(true)
        self.orders.removeAll()
        self.table.reloadData()
        if self.phone.text?.count != 4 {
            let alert = UIAlertController(title: "系統提示", message: "請正確輸入手機後四碼", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "取消", style: .default))
            self.present(alert, animated: false)
        }else {
            let reqData: ReqData = ReqData()
            reqData.phone = self.phone.text
            ApiManager.sellerQuickSearch(req: reqData, ui: self, onSuccess: { orders in
                self.orders.append(contentsOf: orders.map({ o -> OrderVo in
                    o.order_detail = OrderDetail.parse(src: o.order_data)!
                    return o
                }))
                self.table.reloadData()
            }) { err_msg in
                let alert = UIAlertController(title: "", message: "目前查無此訂單，\n請確認取餐時間，\n或手機號碼是否正確！！", preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "確定", style: .default))
                self.present(alert, animated: false)
            }
        }
    }
    
    @IBAction func quickSearch(_ sender: Any) {
        self.loadData(refresh: true)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // 限制輸入長度
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        guard let text = textField.text else { return true }
        let newLength: Int = text.count + string.count - range.length
        if !CharacterSet.decimalDigits.isSuperset(of: CharacterSet(charactersIn: string)) {
            return false
        }
        return newLength <= 4
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.orders.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

        let cell = tableView.dequeueReusableCell(withIdentifier: UIIdentifier.CELL.rawValue, for: indexPath) as! SearchMainTVCell

        cell.cancelBtn.tag = indexPath.row
        cell.failureBtn.tag = indexPath.row
        cell.processingBtn.tag = indexPath.row
        cell.canFetchBtn.tag = indexPath.row
        cell.finishBtn.tag = indexPath.row
        
        let status: OrderStatus = OrderStatus.of(name: self.orders[indexPath.row].status)
        cell.orderStatus.text = status.get().value
        cell.orderType.text = self.orders[indexPath.row].order_detail.order_type.delivery == "OUT" ? "外帶" : "內用"
        cell.count.text = "(" + self.orders[indexPath.row].order_detail.orders.count.description + ")"
        
        if self.orders[indexPath.row].order_detail.use_bonus != nil {
            let price: Int = Int(self.orders[indexPath.row].order_price)! - (Int(self.orders[indexPath.row].order_detail.use_bonus)! / 10 * 3)
            cell.price.text = "$" + price.description + ", div. " + self.orders[indexPath.row].order_detail.use_bonus
        }else {
            cell.price.text = "$" + self.orders[indexPath.row].order_price
        }
        
//        cell.price.text = "$" + self.orders[indexPath.row].order_price
        cell.name.text = self.orders[indexPath.row].order_detail.user_name
        cell.phone.text = self.orders[indexPath.row].order_detail.user_phone
        cell.fetchTime.text = DateTimeHelper.formToString(date: self.orders[indexPath.row].fetch_date, from: "dd日 HH時 mm分")
        cell.userMessage.text = self.orders[indexPath.row].user_message
        
        var content: String = ""
        for o in self.orders[indexPath.row].order_detail.orders {
            content +=  o.item.category_name + ": " +
            StringsHelper.padEnd(str: o.item.food_name, minLength: 10, of: " ") +
            StringsHelper.padEnd(str: "x" + o.count, minLength: 15, of: " ") +
            "$ " + o.item.price +
            "\n規格: " +
            StringsHelper.padEnd(str: o.item.scopes[0].name, minLength: 20 , of: " ") +
            "\n附加: " +
            o.item.opts.reduce("", { (s: String, i: ItemVo) -> String in
                return s + "\n" + StringsHelper.padEnd(str:"- " + i.name, minLength: 20 , of: " ") +
                        StringsHelper.padEnd(str: "  ", minLength: 10 , of: " ") + "$ " + i.price
            }) +
            (o.item.opts.count == 0 ? "- 無\n" : "\n") +
            "需求: "
            for d in o.item.demands {
                content += d.name + ": " + d.datas[0].name + ", "
            }
            content += "\n------------------------------------------\n\n"
        }
      
        cell.foodDatas.text = content
        
        cell.finishBtn.isHidden = true
        cell.failureBtn.isHidden = true
        cell.processingBtn.isHidden = true
        cell.canFetchBtn.isHidden = true
        cell.cancelBtn.isHidden = false
        
        switch status {
        case .UNFINISH:
            cell.processingBtn.isHidden = false
            cell.canFetchBtn.isHidden = false
            break
        case .PROCESSING:
            cell.canFetchBtn.isHidden = false
            cell.finishBtn.isHidden = false
            break
        case .CAN_FETCH:
            cell.failureBtn.isHidden = false
            cell.finishBtn.isHidden = false
            break
        case .LIVE, .CANCEL, .FAIL, .FINISH, .UNKNOWN:
            break
        }
        
        return cell
    }
    

    @IBAction func changeToCancel (_ sender: UIButton ) {
        var text: UITextField! = UITextField()
        let alert = UIAlertController(title: "確定要取消訂單嗎？\n客戶不會獲得任何紅利點數", message: "\n告訴客人原因\n", preferredStyle: .alert)
        
        alert.addTextField{ textField in
            textField.placeholder = "自行輸入"
            textField.leftView = UIView(frame: CGRect(x: 0, y: 0, width: 10, height: 10))
            textField.addConstraint(textField.heightAnchor.constraint(equalToConstant: 36))
            textField.font?.withSize(30)
            text = textField
        }
        
        alert.addAction(UIAlertAction(title: "使用自訂內容", style: .default, handler: { _ in
            text.text = StringsHelper.replace(str: text.text! , of: " ", with: "")
            self.changeToCancelHandler(dataIndex: sender.tag, message: text.text!)
        }))
        
        alert.addAction(UIAlertAction(title: "我現在忙不過來，抱歉", style: .default, handler: { _ in
            self.changeToCancelHandler(dataIndex: sender.tag, message: "我現在忙不過來，抱歉")
        }))
        
        alert.addAction(UIAlertAction(title: "產品賣完了，很抱歉請改選其他產品", style: .default,  handler: { _ in
            self.changeToCancelHandler(dataIndex: sender.tag, message: "產品賣完了，很抱歉請改選其他產品")
        }))

        alert.addAction(UIAlertAction(title: "取消", style: .destructive))
        self.present(alert, animated: false, completion: nil)
    }
    
    func changeToCancelHandler(dataIndex: Int, message: String){
        Loading.show()
        let alert = UIAlertController(title: message != "" ? "你要給客人的原因" : "", message: message != "" ? message : "至少選取一個原因，或自行輸入", preferredStyle: .alert)
        if message == "" {
            alert.addAction(UIAlertAction(title: "我知道了", style: .default))
        }else {
            alert.addAction(UIAlertAction(title: "返回", style: .destructive))
            alert.addAction(UIAlertAction(title: "送出", style: .default, handler: { _ in
                let reqData: ReqData = ReqData()
                reqData.uuid = self.orders[dataIndex].order_uuid
                reqData.type = OrderStatus.CANCEL.get().name
                reqData.message = message
                self.changeOreder(reqData: reqData)
            }))
        }
        Loading.hide()
        self.present(alert, animated: false, completion: nil)
    }
    
    @IBAction func changeToFailure (_ sender: UIButton ) {
        let alert = UIAlertController(title: Optional.none, message:"確定客戶跑單嗎？\n會影響客戶點餐的權益以及紅利點數", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "返回", style: .destructive))
        alert.addAction(UIAlertAction(title: "送出", style: .default, handler: { _ in
            let reqData: ReqData = ReqData()
            reqData.uuid = self.orders[sender.tag].order_uuid
            reqData.type = OrderStatus.FAIL.get().name
            reqData.message = "你的商品超過時間未領取，記點一次，請注意往後的訂餐權利！"
            self.changeOreder(reqData: reqData)
        }))
        self.present(alert, animated: false, completion: nil)
    }
    
    // change Failure & Cancel func
    func changeOreder(reqData: ReqData){
        ApiManager.sellerChangeOrder(req: reqData, ui: self, onSuccess: {
            self.loadData(refresh: true)
        }, onFail: { err_msg in
            print(err_msg)
            self.loadData(refresh: true)
        })
    }
    
    @IBAction func changeToProcessing (_ sender: UIButton ) {
       self.changeOrderStatus(status: OrderStatus.PROCESSING, dataIndex: sender.tag, alertMsg: "確認開始製作此訂單")
    }
    
    @IBAction func changeToCanFetch (_ sender: UIButton ) {
        self.changeOrderStatus(status: OrderStatus.CAN_FETCH, dataIndex: sender.tag, alertMsg: "確認此訂單已可領取")
    }
    
    @IBAction func changeToFinish (_ sender: UIButton ) {
        self.changeOrderStatus(status: OrderStatus.FINISH, dataIndex: sender.tag, alertMsg: "確認此訂單已交易完成")
    }
    
    func changeOrderStatus (status: OrderStatus, dataIndex: Int, alertMsg: String){
        
        let reqData: ReqData = ReqData()
        reqData.uuid = self.orders[dataIndex].order_uuid
        reqData.type = status.get().name
        
        let alert = UIAlertController(title: Optional.none, message: alertMsg, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "返回", style: .destructive))
        alert.addAction(UIAlertAction(title: "確定", style: .default, handler: { _ in
            ApiManager.sellerChangeOrder(req: reqData, ui: self, onSuccess: {
                self.loadData(refresh: true)
            }, onFail: { err_msg in
                print(err_msg)
            })
        }))
        self.present(alert, animated: false)
    }
    
    // 鍵盤點擊背景縮放
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.view.endEditing(true)
    }
    
}

