package com.example.melon.myfirstproject.service;

import android.content.SharedPreferences;

import java.util.List;

public class SPService {
    private static SPService SERVICE = null;
    private SharedPreferences preferences = null;

    public SPService() {

    }
    public SPService(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public static SPService getInstance(SharedPreferences preferences) {
        if (SERVICE == null) {
            SERVICE = new SPService(preferences);
        }
        return SERVICE;
    }

    public static void setOauth(String oauth) {
//        SERVICE.preferences.edit().putString(String.valueOf(R.string.oauth_token), oauth).commit();
    }
    public static String getOauth() {
//        return SERVICE.preferences.getString(String.valueOf(R.string.oauth_token),"");
        return "";
    }

    public static void setLoginLimit(long timeLimit) {
//        SERVICE.preferences.edit().putLong(String.valueOf(R.string.login_limit),timeLimit).commit();
    }
    public static long getLoginLimit() {
//        return SERVICE.preferences.getLong(String.valueOf(R.string.login_limit),0L);
        return 123;
    }
    public static void setRememberAccount(String account) {
//        SERVICE.preferences.edit().putString(String.valueOf(R.string.remember_account),account).commit();
    }
    public static String getRememberAccount() {
//        return SERVICE.preferences.getString(String.valueOf(R.string.remember_account),"");
        return "";
    }

    public static void setIdentity(String identity) {
//        SERVICE.preferences.edit().putString(String.valueOf(R.string.remember_identity),identity).commit();
    }
    public static String getIdentity() {
//        return SERVICE.preferences.getString(String.valueOf(R.string.remember_identity),"");
        return "";
    }

    // Notify setUp
    public static void setNotifySound(boolean isSound){
//        SERVICE.preferences.edit().putBoolean(String.valueOf(R.string.user_notify_sound),isSound).commit();
    }
    public static boolean getNotifySound(){
//        return SERVICE.preferences.getBoolean(String.valueOf(R.string.user_notify_sound),true);
        return true;
    }
    public static void setNotifyShake(boolean isShake){
//        SERVICE.preferences.edit().putBoolean(String.valueOf(R.string.user_notify_shake),isShake).commit();
    }
    public static boolean getNotifyShake(){
//        return SERVICE.preferences.getBoolean(String.valueOf(R.string.user_notify_shake),true);
        return true;
    }

    public static void setRememberMe(boolean rememberMe) {
//        SERVICE.preferences.edit().putBoolean(String.valueOf(R.string.remember_me),rememberMe).commit();
    }
    public static boolean getRememberMe() {
//        return SERVICE.preferences.getBoolean(String.valueOf(R.string.remember_me),false);
        return true;
    }

    public static void setAccout(String account) {
//        SERVICE.preferences.edit().putString(String.valueOf(R.string.user_account),account).commit();
    }
    public static String getAccout() {
//        return SERVICE.preferences.getString(String.valueOf(R.string.user_account),"");
        return "";
    }
    public static void setUserName(String userName) {
//        SERVICE.preferences.edit().putString(String.valueOf(R.string.user_name),userName).commit();
    }
    public static String getUserName() {
//        return SERVICE.preferences.getString(String.valueOf(R.string.user_name),"");
        return "";
    }
    public static void setUserPhone(String userPhone) {
//        SERVICE.preferences.edit().putString(String.valueOf(R.string.user_phone),userPhone).commit();
    }
    public static String getUserPhone() {
//        return SERVICE.preferences.getString(String.valueOf(R.string.user_phone),"");
        return "";
    }




    public static boolean getIsFirstLogin (){
//        return SERVICE.preferences.getBoolean(String.valueOf(R.string.is_first_use), true);
        return true;
    }

    public static void setIsFirstLogin (boolean isFirst){
//        SERVICE.preferences.edit().putBoolean(String.valueOf(R.string.is_first_use), isFirst).commit();
    }


    public static void setUserUID(String uid) {
//        SERVICE.preferences.edit().putString(String.valueOf(R.string.user_uid), uid).commit();
    }

    public static String getUserUID() {
//        return SERVICE.preferences.getString(String.valueOf(R.string.user_uid),"");
        return "";
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
    public static void clearAccount(){
        String account = getAccout();
        SERVICE.preferences.edit().clear().commit();
        setAccout(account);
    }

    public void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }
}
