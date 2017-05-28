package com.mrqyoung.sms302;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;

/**
 * Created by Yorn on 2017/4/30.
 * 1. save = user, pass, aes-key
 * 2. plain-user; aes-pass; default-aes-key;
 */

class DataHelper {

    private static final String defaultKey = "C87AFAC14CD2AF79D2FD639DC9C19525";
    private static final String PREFS_NAME = "Cache1";
    private static final String EMPTY = "";
    private static String KEY;
    private final Context mContext;
    // SharedPreferencesTags
    private String EMAIL = "EMAIL";  // User saved email address
    private String TOKEN = "TOKEN";  // User email password cipher text
    private String THEKEY = "*#06#";  // Main AES key, for encrypt SMS and the password
    private String SMTP_INFO = "SMTP";  // SMTP server info, default is smtp.qq.com:587

    private boolean useDefaultKeyAndDoNotEncryptSMS = false;
    boolean DEBUG = false;

    DataHelper(Context context) {
        mContext = context;
        refresh();
        debug("[KEY=]" + KEY);
    }

    void refresh() {
        KEY = loadTheKey();
        if (KEY.isEmpty()) {
            debug("WARN!!!!!!!! 404 EMPTY");
            KEY = defaultKey;
            useDefaultKeyAndDoNotEncryptSMS = true;
        }
    }

    boolean isNotReady() {
        return EMPTY.equals(loadEmail());
    }

    boolean isPlainSMS() {
        return useDefaultKeyAndDoNotEncryptSMS;
    }

    private void debug(String s) {
        if (DEBUG) {
            Log.d("DataHelper", s);
        }
    }

    boolean keepTheKey(String msg) {
        try {
            String data = AESCrypt.encrypt(defaultKey, msg);
            keep(THEKEY, data);
            return true;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String loadTheKey() {
        String data = load(THEKEY);
        debug("loadTheKey-data:" + data);
        if (EMPTY.equals(data)) {
            debug("WARN!!!!!!!! 404 EMPTY");
            return EMPTY;
        }
        try {
            String msg = AESCrypt.decrypt(defaultKey, data);
            debug("loadTheKey-msg:" + msg);
            return msg;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return EMPTY;
    }

    void keep(String key, String value) {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    String load(String key) {
        return load(key, EMPTY);
    }

    String load(String key, String defValue) {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
        String value = settings.getString(key, defValue);
        debug(key + "|" + value);
        return value;
    }


    void saveEmail(String e) {
        keep(EMAIL, e);
    }

    String loadEmail() {
        return load(EMAIL);
    }

    boolean saveToken(String t) {
        String token = encode(t);
        if (EMPTY.equals(token)) {
            return false;
        }
        keep(TOKEN, token);
        return true;
    }

    String loadToken() {
        String token = load(TOKEN);
        return decode(token);
    }

    void saveSMTPInfo(String s) {
        keep(SMTP_INFO, s);
    }

    String loadSMTPInfo() {
        return load(SMTP_INFO);
    }

    void clearCache() {
        mContext.getSharedPreferences(PREFS_NAME, 0).edit().clear().apply();
    }


    String encode(String msg) {
        String data = EMPTY;
        try {
            data = AESCrypt.encrypt(KEY, msg);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return data;
    }

    String decode(String data) {
        String msg = EMPTY;
        try {
            msg = AESCrypt.decrypt(KEY, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }
}
