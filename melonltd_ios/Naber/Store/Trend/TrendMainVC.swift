//
//  TrendMainViewController.swift
//  Naber
//
//  Created by LianYong-Jun on 2018/6/6.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import UIKit

class TrendMainVC: UIViewController {
    
    
    var timer: Timer!
    
    @IBOutlet weak var yearIncome: UILabel!
    @IBOutlet weak var monthIncome: UILabel!
    @IBOutlet weak var dayIncome: UILabel!
    @IBOutlet weak var finishCount: UILabel!
    
    @IBOutlet weak var statusDate: UILabel!
    
    @IBOutlet weak var unFinishCount: UILabel!
    @IBOutlet weak var canFetchCount: UILabel!
    @IBOutlet weak var cancelCount: UILabel!
    @IBOutlet weak var processingCount: UILabel!
    
    
    @IBOutlet weak var tableView: UITableView! {
        didSet {
            let refreshControl: UIRefreshControl = UIRefreshControl()
            refreshControl.attributedTitle = NSAttributedString(string: "Pull to refresh", attributes: [NSAttributedStringKey.foregroundColor: UIColor(red: 188/255, green: 188/255, blue: 188/255, alpha: 1.0)])
            refreshControl.addTarget(self, action: #selector(refresh), for: .valueChanged)
            refreshControl.tintColor = UIColor.clear
            self.tableView.addSubview(refreshControl)
        }
    }

    @objc func refresh(sender: UIRefreshControl){
        sender.endRefreshing()
        self.loadData(refresh: true)
    }
    
    func loadData(refresh: Bool){
        self.startTimer()
    }

    @IBAction func toFinishOredrLog (_ sender: UIButton) {
        if let vc = UIStoryboard(name: UIIdentifier.STORE.rawValue, bundle: nil).instantiateViewController(withIdentifier: "TrendCompleteOrder") as? TrendCompleteOrderVC {
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.startTimer()
    }
    
    // 離開該畫面停止 Timer
    override func viewDidDisappear(_ animated: Bool) {
        self.stopTimer()
    }
    
    
    func callStat(){
        ApiManager.sellerStat(ui: self, onSuccess: { stat in
            self.yearIncome.text = "$ " + (stat?.year_income)!
            self.monthIncome.text = "$ " + (stat?.month_income)!
            self.dayIncome.text = "$ " + (stat?.day_income)!
            self.finishCount.text = stat?.finish_count
            
            self.cancelCount.text = stat?.cancel_count
            self.processingCount.text = stat?.processing_count
            self.canFetchCount.text = stat?.can_fetch_count
            self.unFinishCount.text = stat?.unfinish_count
            self.statusDate.text = DateTimeHelper.formToString(date: (stat?.status_dates[0])!, from: "yyyy-MM-dd") +
                " ~ " + DateTimeHelper.formToString(date: (stat?.status_dates[1])!, from: "yyyy-MM-dd")
            // print(Date())
        }) { err_msg in
            // print(err_msg)
        }
    }
    
    
    func startTimer(){
        self.stopTimer()
        if self.timer == nil {
            // print("call start timer ok")
            self.callStat()
            timer = Timer.scheduledTimer(timeInterval: NaberConstant.SELLER_STAT_REFRESH_TIMER, target: self, selector: #selector(scheduledStat), userInfo: nil, repeats: true)
        }
    }
    
    // timer 排程
    @objc func scheduledStat(){
        self.callStat()
    }
    
    // 將timer的執行緒停止
    func stopTimer(){
        if self.timer != nil {
            // print("call stop timer ok")
            self.timer.invalidate()
            self.timer = nil
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // 攔截 事件
    override func show(_ vc: UIViewController, sender: Any?) {
    
    }

}
