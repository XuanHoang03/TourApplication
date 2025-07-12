package com.hashmal.tourapplication.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.hashmal.tourapplication.service.dto.Account;
import com.hashmal.tourapplication.service.dto.Profile;
import com.hashmal.tourapplication.service.dto.SysUserDTO;
import com.hashmal.tourapplication.service.dto.UserDTO;

public class LocalDataService {

    private static final String PREF_NAME = "user_session";
    private static final String KEY_USER_JSON = "user_json";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_EXPIRY_TIME = "expired_at";
    private static LocalDataService instance;
    private final SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();
    private LocalDataService(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized LocalDataService getInstance(Context context) {
        if (instance == null) {
            instance = new LocalDataService(context);
        }
        return instance;
    }

    public void useGuestAccount() {
        String guest = "GUEST";
        Account account = new Account(guest, guest, null, 0, "GUEST",1, null, null);
        Profile profile = new Profile(null, "Tài khoản khách", guest, null, null, null, null, null, null, null, null, null, null, null, null, null);
        UserDTO user = new UserDTO(account, profile);
        saveUserInfo(user);
    }

    public void saveUserInfo(Object data) {
        String json = gson.toJson(data);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_JSON, json);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_EXPIRY_TIME, null);
        editor.apply();
    }

    public UserDTO getCurrentUser() {
        String userJson = sharedPreferences.getString(KEY_USER_JSON, null);
        if (userJson == null) {
            return  null;
        }
        try {
            return gson.fromJson(userJson, UserDTO.class);
        } catch (Exception e) {
            Log.e("GSON_ERROR", "Parse lỗi", e);
            return null;
        }
//        return gson.fromJson(userJson, UserDTO.class);
    }

    public SysUserDTO getSysUser() {
        String userJson = sharedPreferences.getString(KEY_USER_JSON, null);
        if (userJson == null) {
            return  null;
        }
        try {
            return gson.fromJson(userJson, SysUserDTO.class);
        } catch (Exception e) {
            Log.e("GSON_ERROR", "Parse lỗi", e);
            return null;
        }
    }

    public void clearUserData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Clear all user-related data
        editor.remove(KEY_USER_JSON);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_EXPIRY_TIME);
        editor.apply();
        
        // Log for debugging
        Log.d("LocalDataService", "User data cleared successfully");
    }
}
