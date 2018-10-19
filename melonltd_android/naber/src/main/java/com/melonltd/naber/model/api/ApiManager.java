package com.melonltd.naber.model.api;

import android.util.Log;

import com.google.common.collect.Maps;
import com.melonltd.naber.model.service.Base64Service;
import com.melonltd.naber.model.service.SPService;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.vo.AccountInfoVo;
import com.melonltd.naber.vo.CategoryRelVo;
import com.melonltd.naber.vo.ContactInfo;
import com.melonltd.naber.vo.FoodVo;
import com.melonltd.naber.vo.OrderDetail;
import com.melonltd.naber.vo.ReqData;
import com.melonltd.naber.vo.RestaurantInfoVo;

import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by evan on 2018/1/5.
 */

public class ApiManager {
//    private static final String TAG = ApiManager.class.getSimpleName();
    private static ClientManager CLIENT_MANAGER = ClientManager.getInstance();

    public static ClientManager getClient() {
        if (CLIENT_MANAGER == null) {
            CLIENT_MANAGER = ClientManager.getInstance();
        }
        return CLIENT_MANAGER;
    }

    /**
     * 以下為共用 API
     */
    // 取得驗證碼
    public static void getSMSCode(Map<String, String> map, ThreadCallback callback) {
        Call call = getClient().post(ApiUrl.GET_SMS_CODE, Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(map)));
        call.enqueue(callback);
    }

    // 驗證SMS密碼
    public static void verifySMSCode(Map<String, String> map, ThreadCallback callback) {
        Call call = getClient().post(ApiUrl.SMS_VERIFY_CODE, Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(map)));
        call.enqueue(callback);
    }

    // 使用者註冊
    public static void userRegistered(AccountInfoVo req, ThreadCallback callback) {
        Call call = getClient().post(ApiUrl.USER_REGISTERED, Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 商家註冊
    public static void sellerRegistered(Map<String, String> map, ThreadCallback callback) {
        Call call = getClient().post(ApiUrl.SELLER_REGISTERED, Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(map)));
        call.enqueue(callback);
    }

    // 登入
    public static void login(AccountInfoVo req, ThreadCallback callback) {
        Call call = getClient().post(ApiUrl.LOGIN, Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 登出
    public static void logout(Map<String, String> req, ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.LOGOUT, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }


    /**
     * common
     */
    // 1.檢查app版本，並確認是否需要更新
    public static void checkAppVersion(ThreadCallback callback) {
        Map<String, String> req = Maps.newHashMap();

        Call call = getClient().post(ApiUrl.CHECK_APP_VERSION, Base64Service.encryptBASE64("ANDROID"));
        call.enqueue(callback);
    }

    // 2.取得店家類型列表
    public static void storeCategoryList (ApiCallback callback) {
        Call call = getClient().post(ApiUrl.STORE_CATEGORY_LIST);
        call.enqueue(callback);
    }

    // 3.取得店家類型列表
    public static void storeAreaList (ApiCallback callback) {
        Call call = getClient().post(ApiUrl.STORE_AREA_LIST);
        call.enqueue(callback);
    }

    // 4.取得App進入引導公告圖
    public static void appIntroBulletin (ThreadCallback callback) {
        Call call = getClient().post(ApiUrl.APP_INTRO_BULLETIN);
        call.enqueue(callback);
    }

    // 5.取得全部營消活動列表
    public static void getAllActivities(ThreadCallback callback){
        Call call = getClient().post(ApiUrl.ACT_LIST);
        call.enqueue(callback);
    }

    // 6.取得行政區域列表
    public static void getSubjectionRegions(ThreadCallback callback){
        Call call = getClient().post(ApiUrl.SUBJECTION_REGIONS);
        call.enqueue(callback);
    }
    //7.取得區域學校列表
    public static void getSchoolDivided(ThreadCallback callback){
        Call call = getClient().post(ApiUrl.SCHOOL_DIVIDED_LIST);
        call.enqueue(callback);
    }

    /**
     * 以下為使用者是使用 API
     */
    // 輪播圖
    public static void advertisement(ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.ADVERTISEMENT, SPService.getOauth());
        call.enqueue(callback);
    }

    // 全部公告
    public static void bulletin(ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.BULLETIN, SPService.getOauth());
        call.enqueue(callback);
    }

    // 取得餐館地理位置模板
    public static void restaurantTemplate(ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.RESTAURANT_TEMPLATE, SPService.getOauth());
        call.enqueue(callback);
    }

    // 餐館列表 TOP, AREA, CATEGORY, DISTANCE
    public static void restaurantList(ReqData req, ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.RESTAURANT_LIST, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 餐館細節，系列列表
    public static void restaurantDetail(String uuid, ThreadCallback callback) {
        ReqData req = new ReqData();
        req.uuid = uuid;
        Call call = getClient().postHeader(ApiUrl.RESTAURANT_DETAIL, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 系列下品項列表
    public static void restaurantFoodList(String uuid, ThreadCallback callback) {
        ReqData req = new ReqData();
        req.uuid = uuid;
        Call call = getClient().postHeader(ApiUrl.RESTAURANT_FOOD_LIST, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 品項細節
    public static void restaurantFoodDetail(String uuid, ThreadCallback callback) {
        ReqData req = new ReqData();
        req.uuid = uuid;
        Call call = getClient().postHeader(ApiUrl.RESTAURANT_FOOD_DETAIL, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 使用者訂單記錄
    public static void userOrderHistory(ReqData req, ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.USER_ORDER_HISTORY, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 使用者資訊
    public static void userFindAccountInfo(ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.FIND_ACCOUNT_INFO, SPService.getOauth());
        call.enqueue(callback);
    }

    // 上傳圖片
    public static void uploadPhoto(ReqData req, ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.IMAGE_UPLOAD, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 更新密碼
    public static void reseatPassword(Map<String, String> req, ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.RESEAT_PSW, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 忘記密碼
    public static void forgetPassword(Map<String, String> req, ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.FORGET_PSW, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 提交訂單
    public static void userOrderSubmit(OrderDetail req, ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.ORDER_SUBMIT, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }


    /////// SELLSE API /////

    // 取得每日營業時段
    public static void sellerBusinessTime(ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.BUSINESS_TIME, SPService.getOauth());
        call.enqueue(callback);
    }

    // 更新每日營業時段
    public static void sellerChangeBusinessTime(RestaurantInfoVo req, ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.CHANGE_BUSINESS_TIME, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 快速查詢訂單
    public static void sellerQuickSearch(Map<String, String> req, ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.QUICK_SEARCH, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 更改訂單狀況
    public static void sellerChangeOrder(ReqData req, ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.CHANGE_ORDER, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 取得訂單列表
    public static void sellerOrderList(ReqData req, ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.ORDER_LIST, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 取得即時訂單列表
    public static void sellerOrderLive(ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.ORDER_LIVE, SPService.getOauth());
        call.enqueue(callback);
    }

    // 取得營運概況
    public static void sellerStat(ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.SELLER_STAT, SPService.getOauth());
        call.enqueue(callback);
    }

    // 取得營運概況已完成訂單列表
    public static void sellerStatLog(ReqData req, ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.SELLER_STAT_LOG, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 取得種類列表
    public static void sellerCategoryList(ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.SELLER_CATEGORY_LIST, SPService.getOauth());
        call.enqueue(callback);
    }

    // 新增種類
    public static void sellerAddCategory(ReqData req, ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.SELLER_ADD_CATEGORY, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 更新種類狀態
    public static void sellerChangeCategoryStatus(ReqData req, ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.SELLER_CHANGE_CATEGORY, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 刪除種類
    public static void sellerDeleteCategory(ReqData req, ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.SELLER_DELETE_CATEGORY, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 排序種類
    public static void sellerSortCategory(List<CategoryRelVo> req , ThreadCallback callback){
        Call call = getClient().postHeader(ApiUrl.SELLER_SORT_CATEGORY, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JsonParse.toJson(req)));
        call.enqueue(callback);
    }

    // 品項列表
    public static void sellerFoodList(ReqData req, ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.SELLER_FOOD_LIST, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 品項更新
    public static void sellerFoodUpdate(FoodVo req, ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.SELLER_CHANGE_FOOD, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 品項刪除
    public static void sellerFoodDelete(ReqData req, ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.SELLER_DELETE_FOOD, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 品項刪除
    public static void sellerFoodAdd(FoodVo req, ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.SELLER_ADD_FOOD, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 品項排序
    public static void sellerFoodSort(List<FoodVo> req, ThreadCallback callback){
        Call call = getClient().postHeader(ApiUrl.SELLER_SORT_FOOD,SPService.getOauth(),Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 取得餐館資訊
    public static void sellerRestaurantInfo(ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.SELLER_RESTAURANT_INFO, SPService.getOauth());
        call.enqueue(callback);
    }

    // 設定餐館資訊
    public static void sellerRestaurantSetting(RestaurantInfoVo req, ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.SELLER_RESTAURANT_SETTING, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }

    // 設定餐館隔日接單開關
    public static void sellerRestaurantSettingBusiness(Map<String, String> req, ThreadCallback callback) {
        Call call = getClient().postHeader(ApiUrl.SELLER_RESTAURANT_SETTING_BUSINESS, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }
    //提交兌換
    public static void actSubmit(ReqData req,ThreadCallback callback){
        Call call = getClient().postHeader(ApiUrl.ACT_SUBMIT,SPService.getOauth(),Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }
    //提交兌換序號
    public static void serialSubmit(ReqData req,ThreadCallback callback){
        Call call = getClient().postHeader(ApiUrl.SERIAL_SUBMIT,SPService.getOauth(),Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }
    //送出活動
    public static void resEventSubmit(ReqData req,ThreadCallback callback){
        Call call = getClient().postHeader(ApiUrl.SERIAL, SPService.getOauth(), Base64Service.encryptBASE64(Tools.JSONPARSE.toJson(req)));
        call.enqueue(callback);
    }
}
