package com.melonltd.naber.util;

import com.google.common.base.Strings;

import java.util.regex.Pattern;

public class VerifyUtil {

    public static boolean email(String mail) {
        if (Strings.isNullOrEmpty(mail)) {
            return false;
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }

    public static boolean phoneNumber(String number) {
        if (Strings.isNullOrEmpty(number)) {
            return false;
        }
        return  number.matches("(09)+[\\d]{8}");
    }

    public static boolean password(String password) {
        if (Strings.isNullOrEmpty(password)) {
            return false;
        }
        return Pattern.compile("^(?=.*[a-zA-Z]+)(?=.*\\d+)[a-zA-Z0-9]{6,20}$").matcher(password).matches();
    }
}
