package com.entrolabs.rbsk.Common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.scottyab.aescrypt.AESCrypt;

import org.json.JSONObject;

import java.util.HashMap;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Context
    Context _context;



    // Sharedpref file name
    public static final String PREF_NAME = "com.entrolabs.rbsk.SessionFile";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // All Shared Preferences Keys
    private static final String IS_LOCATION = "IsLocation";



    // User name (make variable public to access from outside)
    public static final String USER_NAME = "username";

    // Email address (make variable public to access from outside)
    public static final String USER_EMAIL = "email";


    // User mobile (make variable public to access from outside)
    public static final String USER_MOBILE = "mobile";

    // Email address (make variable public to access from outside)
    public static final String USER_LEVEL = "userlevel";

    public static final String USER_DIVISION = "division";
    public static final String USER_PREFIX = "prefix";

    // User Full name (make variable public to access from outside)
    public static final String USER_FULL_NAME = "name";

    // Last active timestamp address (make variable public to access from outside)
    public static final String USER_TIME = "Last Active time";
    private static final int RC_INTERNET_STATE = 111;


    // Constructor
    public SessionManager(Context context) {

        try {
            this._context = context;
            pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            editor = pref.edit();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }



    public void createLoginSession(JSONObject res) {
      try{
          //{"result":"success","error":"","data":[],"username":"ct_atp_agali1","district_code":"22","mandal_code":"5354",
          // "secretariat_code":"0","district":"ANANTAPUR","mandal":"Agali","secretariat":"","cases":"3"}
          editor.putBoolean(IS_LOGIN, true);
          storeVal(com.entrolabs.rbsk.Common.Helper.RBSK_Name, res.getString("name"));
          storeVal(com.entrolabs.rbsk.Common.Helper.RBSK_Username, res.getString("username"));
          storeVal(com.entrolabs.rbsk.Common.Helper.RBSK_DistCode, res.getString("district_code"));
          storeVal(com.entrolabs.rbsk.Common.Helper.RBSK_DistName, res.getString("district"));
          storeVal(com.entrolabs.rbsk.Common.Helper.RBSK_MandCode, res.getString("mandal_code"));
          storeVal(com.entrolabs.rbsk.Common.Helper.RBSK_MandName, res.getString("mandal"));

          storeVal(com.entrolabs.rbsk.Common.Helper.RBSK_Mobile, res.getString("mobile"));
          storeVal(com.entrolabs.rbsk.Common.Helper.RBSK_Status, res.getString("status"));
          storeVal(com.entrolabs.rbsk.Common.Helper.RBSK_token, res.getString("token"));

          editor.commit();
      }
      catch (Exception e){
          e.printStackTrace();
      }
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }


    public void storeVal(String key, String value) {
        editor.putString(key, encrypt(value));
        editor.commit();
    }


    public Boolean hasVal(String key) {
        return pref.contains(key);
    }

    public String getStrVal(String key){
        return decrypt(pref.getString(key,""));
    }



    public void removeVal(String key) {

        editor.remove(key);
        editor.commit();

    }

    protected String encrypt(String value) {
        try {
            String encryptedMsg = AESCrypt.encrypt(com.entrolabs.rbsk.Common.Constants.PASSWORD, value);

            return encryptedMsg;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String decrypt(String value) {
        try {
            String messageAfterDecrypt = AESCrypt.decrypt(com.entrolabs.rbsk.Common.Constants.PASSWORD, value);
            return messageAfterDecrypt;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(USER_NAME, pref.getString(USER_NAME, null));
        // user email id
        user.put(USER_EMAIL, pref.getString(USER_EMAIL, null));
        // user full name
        user.put(USER_FULL_NAME, pref.getString(USER_FULL_NAME, null));
        // user level
        user.put(USER_LEVEL, pref.getString(USER_LEVEL, null));
        // user mobile
        user.put(USER_MOBILE, pref.getString(USER_MOBILE, null));


        // return user
        return user;
    }

    /**
     * Clear session Details
     */
    public void logoutUser() {

        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
        SharedPreferences ratePrefs = _context.getSharedPreferences("com.entrolabs.contacttracing.routines", 0);

        Editor redit = ratePrefs.edit();
        redit.clear();
        redit.commit();


        SharedPreferences ratPrefs = _context.getSharedPreferences(PREF_NAME, 0);

        Editor rdit = ratePrefs.edit();
        rdit.clear();
        rdit.commit();
    }




}