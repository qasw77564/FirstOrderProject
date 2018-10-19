//
//  ApiManager.swift
//  Naber
//
//  Created by 王淳彦 on 2018/7/18.
//  Copyright © 2018年 Melone.L.T.D. All rights reserved.
//

import Foundation
import Alamofire

class ApiManager {
    
    private static let SESSION_MANAGER: SessionManager = {
        let configuration = URLSessionConfiguration.default
        configuration.timeoutIntervalForRequest = 15 // seconds
        configuration.timeoutIntervalForResource = 15 //seconds
        return Alamofire.SessionManager(configuration: configuration)
    }()

    private static let HTTP_HEADERS: HTTPHeaders = ["Content-Type": "application/x-www-form-urlencoded"]

    /**
     * 以下為共用 API
     */
    
    // 取得SMS驗證碼
    public static func getSMSCode (structs: SMSCodeVo?, ui: UIViewController, onSuccess: @escaping (SMSCodeVo) -> (), onFail: @escaping (String) -> ()) {
        self.postData(url: ApiUrl.GET_SMS_CODE, data: SMSCodeVo.toJson(structs: structs!), ui:ui, complete: { response in
            let resp: SMSCodeResp = SMSCodeResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    // 送出SMS驗證碼
    public static func verifySMSCode (structs: SMSCodeVo?, ui: UIViewController, onSuccess: @escaping () -> (), onFail: @escaping (String) -> ()) {
        self.postData(url: ApiUrl.SMS_VERIFY_CODE, data: SMSCodeVo.toJson(structs: structs!), ui:ui, complete: { response in
            let resp: RespData = RespData.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess()
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    // 使用者註冊
    public static func userRegistered (structs: AccountInfoVo?, ui: UIViewController, onSuccess: @escaping () -> (), onFail: @escaping (String) -> ()) {
        self.postData(url: ApiUrl.USER_REGISTERED, data: AccountInfoVo.toJson(structs: structs!), ui:ui, complete: { response in
            let resp: AccountInfoResp = AccountInfoResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess()
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    
    // 註冊商家
    public static func sellerRegistered (structs: SellerRegisteredVo?, ui: UIViewController, onSuccess: @escaping () -> (), onFail: @escaping (String) -> ()) {
        self.postData(url: ApiUrl.SELLER_REGISTERED, data: SellerRegisteredVo.toJson(structs: structs!), ui:ui, complete: { response in
            let resp: SellerRegisteredResp = SellerRegisteredResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess()
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    // 登入
    public static func login (structs: AccountInfoVo?, ui: UIViewController, onSuccess: @escaping (AccountInfoVo?) -> (), onFail: @escaping (String) -> ()) {
        self.postData(url: ApiUrl.LOGIN, data: AccountInfoVo.toJson(structs: structs!), ui:ui, complete: { response in
            let resp: AccountInfoResp = AccountInfoResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    // 登出
    public static func logout (structs: AccountInfoVo?, ui: UIViewController, onSuccess: @escaping () -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.LOGOUT, data: AccountInfoVo.toJson(structs: structs!), ui:ui, complete: { response in
            let resp: RespData = RespData.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess()
            }else {
                onFail(resp.err_msg)
            }
        })
    }

    
    /**
     * common
     */
    
    // 1.檢查app版本，並確認是否需要更新
    public static func checkAppVersion (ui: UIViewController, onSuccess: @escaping (AppVersionLogVo) -> (), onFail: @escaping (String) -> ()) {
        self.postData(url: ApiUrl.CHECK_APP_VERSION, data: "IOS", ui:ui, complete: { response in
            let resp: AppVersionLogResp = AppVersionLogResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    
    // 2.取得店家類型列表
    public static func storeCategoryList (ui: UIViewController, onSuccess: @escaping ([String]) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.STORE_CATEGORY_LIST, data: "", ui:ui, complete: { response in
            let resp: StoreCategoryListResp = StoreCategoryListResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    
    // 3.取得店家類型列表
    public static func storeAreaList (ui: UIViewController, onSuccess: @escaping ([String]) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.STORE_AREA_LIST, data: "", ui:ui, complete: { response in
            let resp: StoreAreaListResp = StoreAreaListResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    // 4.取得App進入引導公告圖
    public static func appIntroBulletin (ui: UIViewController, onSuccess: @escaping (String) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.APP_INTRO_BULLETIN, data: "", ui:ui, complete: { response in
            let resp: AppIntroBulletinResp = AppIntroBulletinResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    
    // 5.取得全部營消活動列表
    public static func getAllActivities (ui: UIViewController, onSuccess: @escaping ([ActivitiesVo]) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.ACT_LIST, data: "", ui:ui, complete: { response in
            let resp: ActivitiesListResp = ActivitiesListResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    
    // 6.取得行政區域列表
    public static func getSubjectionRegions (ui: UIViewController, onSuccess: @escaping ([SubjectionRegionVo]) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.SUBJECTION_REGIONS, data: "", ui:ui, complete: { response in
            let resp: SubjectionRegionResp = SubjectionRegionResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    
    // 7.取得學校選單
    public static func getSchoolDivides (ui: UIViewController, onSuccess: @escaping ([SchoolDividedVo]) -> (), onFail: @escaping (String) -> ()) {
        self.postData(url: ApiUrl.SCHOOL_DIVIDED, data: "", ui:ui, complete: { response in
            let resp: SchoolDividedResp = SchoolDividedResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    
    
    /**
     * 以下為使用者是使用 API
     */
    //1.首頁
    // 輪播圖(測試OK)
    public static func advertisement (ui: UIViewController, onSuccess: @escaping ([AdvertisementVo]) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.ADVERTISEMENT, data: "", ui:ui, complete: { response in
            let resp: AdvertisementResp = AdvertisementResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    //2.首頁須取得的公告
    // 全部公告(測試OK)
    public static func bulletin (ui: UIViewController, onSuccess: @escaping ([BulletinVo?]) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.BULLETIN, data: "", ui:ui, complete: { response in
            let resp: BulletinResp = BulletinResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    //Login成功就要去加載User位置
    // 取得餐館地理位置模板(測試OK)
    public static func restaurantTemplate (ui: UIViewController, onSuccess: @escaping ([RestaurantTemplateVo?]) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.RESTAURANT_TEMPLATE, data: "", ui:ui, complete: { response in
            let resp: RestaurantTemplateResp = RestaurantTemplateResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    //Category=種類,Detail=細節,Area=區域, STORE_NAME=店家名稱
    //3.取得餐館列表(TOP,列表無分頁) 4.餐館頁面取得(AREA,CATEGORY)
    // 餐館列表 TOP, AREA, CATEGORY, DISTANCE(測試OK), STORE_NAME
    public static func restaurantList (req: ReqData, ui: UIViewController, onSuccess: @escaping ([RestaurantInfoVo]) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.RESTAURANT_LIST, data: ReqData.toJson(structs: req) , ui:ui, complete: { response in
            let resp: RestaurantListResp = RestaurantListResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }

    // 餐館細節，系列列表
    public static func restaurantCategoryList (uuid: String, ui: UIViewController, onSuccess: @escaping ([CategoryRelVo]) -> (), onFail: @escaping (String) -> ()) {
        let req: ReqData = ReqData()
        req.uuid = uuid
        self.postAutho(url: ApiUrl.RESTAURANT_DETAIL, data: ReqData.toJson(structs: req) , ui:ui, complete: { response in
            let resp: CategoryRelListResp = CategoryRelListResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    // 系列下品項列表
    public static func restaurantFoodList (uuid: String, ui: UIViewController, onSuccess: @escaping ([FoodVo]) -> (), onFail: @escaping (String) -> ()) {
        let req: ReqData = ReqData()
        req.uuid = uuid
        self.postAutho(url: ApiUrl.RESTAURANT_FOOD_LIST, data: ReqData.toJson(structs: req) , ui:ui, complete: { response in
            let resp: FoodListResp = FoodListResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    // 品項細節
    public static func restaurantFoodDetail (uuid: String, ui: UIViewController, onSuccess: @escaping (FoodVo?) -> (), onFail: @escaping (String) -> ()) {
        let req: ReqData = ReqData()
        req.uuid = uuid
        self.postAutho(url: ApiUrl.RESTAURANT_FOOD_DETAIL, data: ReqData.toJson(structs: req) , ui:ui, complete: { response in
            let resp: FoodResp = FoodResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    // 使用者訂單記錄
    public static func userOrderHistory (req: ReqData, ui: UIViewController, onSuccess: @escaping ([OrderVo]) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.USER_ORDER_HISTORY, data: ReqData.toJson(structs: req) , ui:ui, complete: { response in
            let resp: OrderResp = OrderResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    // 使用者資訊(未測試）
    public static func userFindAccountInfo( ui: UIViewController, onSuccess: @escaping (AccountInfoVo?) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.FIND_ACCOUNT_INFO, data: "" , ui:ui, complete: { response in
            let resp: AccountInfoResp = AccountInfoResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    // 上傳圖片
    public static func uploadPhoto(req: ReqData, ui: UIViewController, onSuccess: @escaping (String) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.IMAGE_UPLOAD, data: ReqData.toJson(structs: req) , ui:ui, complete: { response in
            let resp: UploadFileResp = UploadFileResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }

    // 更新密碼(未測試）
    public static func reseatPassword (req: ReqData, ui: UIViewController, onSuccess: @escaping () -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.RESEAT_PSW, data: ReqData.toJson(structs: req) , ui:ui, complete: { response in
            let resp: RespData = RespData.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess()
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    // 忘記密碼(未測試）
    public static func forgetPassword (req: ReqData, ui: UIViewController, onSuccess: @escaping () -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.FORGET_PSW, data: ReqData.toJson(structs: req) , ui:ui, complete: { response in
            let resp: RespData = RespData.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess()
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    // 提交訂單
    public static func userOrderSubmit (req: OrderDetail, ui: UIViewController, onSuccess: @escaping () -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.ORDER_SUBMIT, data: OrderDetail.toJson(structs: req) , ui:ui, complete: { response in
            let resp: RespData = RespData.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess()
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    //提交兌換
    public static func actSubmit (req: ReqData, ui: UIViewController, onSuccess: @escaping () -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.ACT_SUBMIT, data: ReqData.toJson(structs: req) , ui:ui, complete: { response in
            let resp: RespData = RespData.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess()
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    //提交兌換序號
    public static func serialSubmit (req: ReqData, ui: UIViewController, onSuccess: @escaping (ActivitiesVo) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.SERIAL_SUBMIT, data: ReqData.toJson(structs: req) , ui:ui, complete: { response in
            let resp: ActivitiesResp = ActivitiesResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    
    // 商家活動兌換品項提交， 搶紅包，數量限制
    public static func resEventSubmit (req: ReqData, ui: UIViewController, onSuccess: @escaping () -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.RES_EVENT_SUBMIT, data: ReqData.toJson(structs: req) , ui:ui, complete: { response in
            let resp: RespData = RespData.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess()
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    
    
    
    
    
    
    
    /////// SELLSE API /////
    
    // 取得每日營業時段
    public static func sellerBusinessTime (ui: UIViewController, onSuccess: @escaping ([DateRangeVo]) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.BUSINESS_TIME, data: "" , ui:ui, complete: { response in
            let resp:  DateRangeResp = DateRangeResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    // 更新每日營業時段
    public static func sellerChangeBusinessTime (req: RestaurantInfoVo, ui: UIViewController, onSuccess: @escaping ([DateRangeVo]) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.CHANGE_BUSINESS_TIME, data: RestaurantInfoVo.toJson(structs: req) , ui:ui, complete: { response in
            let resp:  DateRangeResp = DateRangeResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    
    // 快速查詢訂單
    public static func sellerQuickSearch (req: ReqData, ui: UIViewController, onSuccess: @escaping ([OrderVo]) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.QUICK_SEARCH, data: ReqData.toJson(structs: req) , ui:ui, complete: { response in
            let resp: OrderResp = OrderResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    // 更改訂單狀況
    public static func sellerChangeOrder (req: ReqData, ui: UIViewController, onSuccess: @escaping () -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.CHANGE_ORDER, data: ReqData.toJson(structs: req) , ui:ui, complete: { response in
            let resp:  RespData = RespData.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess()
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    // 取得訂單列表
    public static func sellerOrderList (req: ReqData, ui: UIViewController, onSuccess: @escaping ([OrderVo]) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.ORDER_LIST, data: ReqData.toJson(structs: req) , ui:ui, complete: { response in
            let resp:  OrderResp = OrderResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }

    // 取得即時訂單列表
    public static func sellerOrderLive (ui: UIViewController, onSuccess: @escaping ([OrderVo]) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.ORDER_LIVE, data: "" , ui:ui, complete: { response in
            let resp:  OrderResp = OrderResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    // 取得營運概況
    public static func sellerStat (ui: UIViewController, onSuccess: @escaping (SellerStatVo?) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.SELLER_STAT, data: "" , ui:ui, complete: { response in
            let resp:  SellerStatResp = SellerStatResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    // 取得營運概況已完成訂單列表
    public static func sellerStatLog (req: ReqData, ui: UIViewController, onSuccess: @escaping ([OrderVo]) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.SELLER_STAT_LOG, data: ReqData.toJson(structs: req) , ui:ui, complete: { response in
            let resp: OrderResp = OrderResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    // 取得種類列表
    public static func sellerCategoryList (ui: UIViewController, onSuccess: @escaping ([CategoryRelVo]) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.SELLER_CATEGORY_LIST, data: "", ui:ui, complete: { response in
            let resp: CategoryRelListResp = CategoryRelListResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    // 新增種類
    public static func sellerAddCategory (req: ReqData, ui: UIViewController, onSuccess: @escaping (CategoryRelVo?) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.SELLER_ADD_CATEGORY, data: ReqData.toJson(structs: req) , ui:ui, complete: { response in
            let resp: CategoryRelResp = CategoryRelResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
     // 更新種類狀態
    public static func sellerChangeCategoryStatus (req: ReqData, ui: UIViewController, onSuccess: @escaping () -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.SELLER_CHANGE_CATEGORY, data: ReqData.toJson(structs: req) , ui:ui, complete: { response in
            let resp:  RespData = RespData.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess()
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    // 刪除種類
    public static func sellerDeleteCategory (req: ReqData, ui: UIViewController, onSuccess: @escaping () -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.SELLER_DELETE_CATEGORY, data: ReqData.toJson(structs: req) , ui:ui, complete: { response in
            let resp:  RespData = RespData.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess()
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    // 排序種類
    public static func sellerSortCategory (req: [CategoryRelVo], ui: UIViewController, onSuccess: @escaping ([CategoryRelVo]) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.SELLER_SORT_CATEGORY, data: CategoryRelVo.toJsonArray(structs: req) , ui:ui, complete: { response in
            let resp:  CategoryRelListResp = CategoryRelListResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
   // 品項列表
    public static func sellerFoodList (req: ReqData, ui: UIViewController, onSuccess: @escaping ([FoodVo]) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.SELLER_FOOD_LIST, data: ReqData.toJson(structs: req) , ui:ui, complete: { response in
            let resp:  FoodListResp = FoodListResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    // 品項更新
    public static func sellerFoodUpdate (req: FoodVo, ui: UIViewController, onSuccess: @escaping () -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.SELLER_CHANGE_FOOD, data: FoodVo.toJson(structs: req) , ui:ui, complete: { response in
            let resp:  RespData = RespData.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess()
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    // 品項刪除
    public static func sellerFoodDelete (req: ReqData, ui: UIViewController, onSuccess: @escaping () -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.SELLER_DELETE_FOOD, data: ReqData.toJson(structs: req) , ui:ui, complete: { response in
            let resp:  RespData = RespData.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess()
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    // 品項加入
    public static func sellerFoodAdd (req: FoodVo, ui: UIViewController, onSuccess: @escaping (FoodVo) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.SELLER_ADD_FOOD, data: FoodVo.toJson(structs: req) , ui:ui, complete: { response in
            let resp: FoodResp = FoodResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    // 品項排序
    public static func sellerFoodSort (req: [FoodVo], ui: UIViewController, onSuccess: @escaping ( [FoodVo]) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.SELLER_SORT_FOOD, data: FoodVo.toJsonArray(structs: req) , ui:ui, complete: { response in
            let resp: FoodListResp = FoodListResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    
    // 取得餐館資訊(未測試)
    public static func sellerRestaurantInfo (ui: UIViewController, onSuccess: @escaping (RestaurantInfoVo) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.SELLER_RESTAURANT_INFO, data: "" , ui: ui, complete: { response in
            let resp:  RestaurantInfoResp = RestaurantInfoResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    // 設定餐館資訊(未測試)
    public static func sellerRestaurantSetting (req: RestaurantInfoVo ,ui: UIViewController, onSuccess: @escaping (RestaurantInfoVo) -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.SELLER_RESTAURANT_SETTING, data: RestaurantInfoVo.toJson(structs: req) , ui:ui, complete: { response in
            let resp:  RestaurantInfoResp = RestaurantInfoResp.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess(resp.data)
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    // 設定餐館隔日接單開關(未測試)
    public static func sellerRestaurantSettingBusiness (req: ReqData,ui: UIViewController, onSuccess: @escaping () -> (), onFail: @escaping (String) -> ()) {
        self.postAutho(url: ApiUrl.SELLER_RESTAURANT_SETTING_BUSINESS, data: ReqData.toJson(structs: req) , ui:ui, complete: { response in
            let resp:  RespData = RespData.parse(src: base64Decoding(decode: response.result.value!))!
            if resp.status.uppercased().elementsEqual(RespStatus.TRUE.rawValue) {
                onSuccess()
            }else {
                onFail(resp.err_msg)
            }
        })
    }
    
    
    //要 Data的,但是沒有header
    private static func postData(url: String, data: String, ui: UIViewController, complete: @escaping (DataResponse<String>) -> ()) {
        let parameter: Parameters =  ["data": base64Encoding(encod: data)]
        self.post(url: url, parameter: parameter, header: self.HTTP_HEADERS, ui: ui) { response in
            complete(response)
        }
    }
    //傳header的POST,不要Data傳空字串
    //header的key=Authorization,Value=acount_uuid
    private static func postAutho(url: String, data: String, ui: UIViewController, complete: @escaping (DataResponse<String>) -> ()) {
        let autho: String = UserSstorage.getAutho()
        let parameter: Parameters =  ["data": base64Encoding(encod: data)]
        let header: HTTPHeaders = ["Authorization" : autho, "Content-Type": "application/x-www-form-urlencoded"]

        self.post(url: url, parameter: parameter, header: header, ui: ui) { response in
            complete(response)
        }
    }
    
    //主要的POST Method
    private static func post(url: String, parameter: Parameters, header: HTTPHeaders, ui: UIViewController, complete: @escaping (DataResponse<String>) -> ()) {
        Loading.show()
        SESSION_MANAGER.request(url, method: HTTPMethod.post, parameters:parameter, headers:header).validate().responseString{ response in
            
            if response.result.isSuccess {
                complete(response)
            }else if (response.result.error != nil) {
                let alert = UIAlertController(title: "系統提示", message: "請確認裝置有連結網路！", preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "我知道了", style: .default))
                ui.present(alert, animated: false)
            }else if response.result.isFailure {
                let alert = UIAlertController(title: "系統提示", message: "資料請求失敗，\n可能現在網路處於不穩定狀態，\n請稍後再試！", preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "我知道了", style: .default))
                ui.present(alert, animated: false)
            }
            Loading.hide()
        }
    }
    
    
    
    // Base64 加密
    private static func base64Encoding(encod: String) -> String {
        
        if NaberConstant.IS_DEBUG {
            return encod
        }
        let plainData = urlEncoded(str: encod).data(using: String.Encoding.utf8)
        let base64String = plainData?.base64EncodedString(options: Data.Base64EncodingOptions.init(rawValue: 0))
        return base64String!
    }
    
    // Base64解密
    private static func base64Decoding(decode: String) -> String {
        if NaberConstant.IS_DEBUG {
            return decode
        }
        let decodedData: Data! = Data(base64Encoded: decode, options: [.ignoreUnknownCharacters, Data.Base64DecodingOptions(rawValue: 0)])
        let decodedString: String = String(data: decodedData! as Data, encoding: String.Encoding(rawValue: String.Encoding.utf8.rawValue))! as String
        return urlDecoded(str: decodedString)
    }
    
    // URL 加密
    private static func urlEncoded(str : String) -> String {
        return str.addingPercentEncoding(withAllowedCharacters: .alphanumerics)!
    }
    
    // URL 解密
    private static func urlDecoded(str : String) -> String {
        // 處理解碼後空白字傳變成'+'號問題
        return StringsHelper.replace(str: str, of: "+", with: "%20").removingPercentEncoding!
    }
    
}
