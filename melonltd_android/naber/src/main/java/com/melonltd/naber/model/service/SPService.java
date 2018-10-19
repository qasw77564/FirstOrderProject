package com.melonltd.naber.model.service;

import android.content.SharedPreferences;

import com.melonltd.naber.R;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.vo.OrderDetail;

import java.util.List;

public class SPService {
    private static SPService SERVICE = null;
    private SharedPreferences preferences = null;

    public SPService() {

    }

    public static SPService getInstance(SharedPreferences preferences) {
        if (SERVICE == null) {
            SERVICE = new SPService(preferences);
        }
        return SERVICE;
    }


    public static void setOauth(String oauth) {
        SERVICE.preferences.edit().putString(String.valueOf(R.string.oauth_token), oauth).commit();
    }
    public static String getOauth() {
        return SERVICE.preferences.getString(String.valueOf(R.string.oauth_token),"");
    }

    public static void setLoginLimit(long timeLimit) {
        SERVICE.preferences.edit().putLong(String.valueOf(R.string.login_limit),timeLimit).commit();
    }
    public static long getLoginLimit() {
        return SERVICE.preferences.getLong(String.valueOf(R.string.login_limit),0L);
    }
    public static void setRememberAccount(String account) {
        SERVICE.preferences.edit().putString(String.valueOf(R.string.remember_account),account).commit();
    }
    public static String getRememberAccount() {
        return SERVICE.preferences.getString(String.valueOf(R.string.remember_account),"");
    }

    public static void setIdentity(String identity) {
        SERVICE.preferences.edit().putString(String.valueOf(R.string.remember_identity),identity).commit();
    }
    public static String getIdentity() {
        return SERVICE.preferences.getString(String.valueOf(R.string.remember_identity),"");
    }

    // Notify setUp
    public static void setNotifySound(boolean isSound){
        SERVICE.preferences.edit().putBoolean(String.valueOf(R.string.user_notify_sound),isSound).commit();
    }
    public static boolean getNotifySound(){
        return SERVICE.preferences.getBoolean(String.valueOf(R.string.user_notify_sound),true);
    }
    public static void setNotifyShake(boolean isShake){
        SERVICE.preferences.edit().putBoolean(String.valueOf(R.string.user_notify_shake),isShake).commit();
    }
    public static boolean getNotifyShake(){
        return SERVICE.preferences.getBoolean(String.valueOf(R.string.user_notify_shake),true);
    }


    // 緩存使用者購物車訂單
    public static void setUserCacheShoppingCarData(List<OrderDetail> data) {
        SERVICE.preferences.edit().putString(String.valueOf(R.string.user_cache_shopping_car_data),Tools.JSONPARSE.toJson(data)).commit();
    }
    public static List<OrderDetail> getUserCacheShoppingCarData() {
        String data = SERVICE.preferences.getString(String.valueOf(R.string.user_cache_shopping_car_data),"");
        return Tools.JSONPARSE.fromJsonList(data,OrderDetail[].class);
    }


    public static void setRememberMe(boolean rememberMe) {
        SERVICE.preferences.edit().putBoolean(String.valueOf(R.string.remember_me),rememberMe).commit();
    }
    public static boolean getRememberMe() {
        return SERVICE.preferences.getBoolean(String.valueOf(R.string.remember_me),false);
    }

    public static void setAccout(String account) {
        SERVICE.preferences.edit().putString(String.valueOf(R.string.user_account),account).commit();
    }
    public static String getAccout() {
        return SERVICE.preferences.getString(String.valueOf(R.string.user_account),"");
    }
    public static void setUserName(String userName) {
        SERVICE.preferences.edit().putString(String.valueOf(R.string.user_name),userName).commit();
    }
    public static String getUserName() {
        return SERVICE.preferences.getString(String.valueOf(R.string.user_name),"");
    }
    public static void setUserPhone(String userPhone) {
        SERVICE.preferences.edit().putString(String.valueOf(R.string.user_phone),userPhone).commit();
    }
    public static String getUserPhone() {
        return SERVICE.preferences.getString(String.valueOf(R.string.user_phone),"");
    }

    public SPService(SharedPreferences preferences) {
        this.preferences = preferences;
    }


    public static boolean getIsFirstLogin (){
        return SERVICE.preferences.getBoolean(String.valueOf(R.string.is_first_use), true);
    }

    public static void setIsFirstLogin (boolean isFirst){
        SERVICE.preferences.edit().putBoolean(String.valueOf(R.string.is_first_use), isFirst).commit();
    }


    public static void setUserUID(String uid) {
        SERVICE.preferences.edit().putString(String.valueOf(R.string.user_uid), uid).commit();
    }

    public static String getUserUID() {
        return SERVICE.preferences.getString(String.valueOf(R.string.user_uid),"");
    }

    public static void removeAll() {
        if (getRememberMe()){
            String account = getAccout();
            boolean rememberMe = getRememberMe();
            SERVICE.preferences.edit().clear().commit();
            setAccout(account);
            setRememberMe(rememberMe);
        }else {
            SERVICE.preferences.edit().clear().commit();
        }
    }

    public void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }
}
