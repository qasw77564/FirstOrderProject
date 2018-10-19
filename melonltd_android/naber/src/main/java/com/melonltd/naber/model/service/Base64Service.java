package com.melonltd.naber.model.service;


import android.util.Base64;

import com.melonltd.naber.model.constant.NaberConstant;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;


public class Base64Service {
//    private static final String TAG = Base64Service.class.getSimpleName();

    public static String encryptBASE64(String key) {
        try {
            if (NaberConstant.IS_DEBUG){
                return key;
            }else {
                return Base64.encodeToString(URLEncoder.encode(key, "UTF-8").getBytes("UTF-8"), Base64.NO_WRAP);
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encryption error !", e);
        }
    }

    public static String decryptBASE64(String key) {
        try {
            if (NaberConstant.IS_DEBUG){
                return key;
            }else {
                return URLDecoder.decode(new String(Base64.decode(key, Base64.NO_WRAP),"UTF-8"), "UTF-8");
            }
        } catch (RuntimeException | UnsupportedEncodingException e) {
            throw new RuntimeException("Decryption error or The Cookie was tampered with !", e);
        }
    }


}
