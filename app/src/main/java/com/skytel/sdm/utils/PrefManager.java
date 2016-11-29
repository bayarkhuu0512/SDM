package com.skytel.sdm.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager implements Constants {

    public String TAG = PrefManager.class.getName();

    SharedPreferences pref;
    public static PrefManager sInstance;
    SharedPreferences.Editor editor;
    Context context;

    // Constructor
    public PrefManager(Context context) {
        this.context = context;
        pref = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public static PrefManager getSessionInstance() {
        return sInstance;
    }

    public static void setSessionInstance(PrefManager session) {
        sInstance = session;
    }

    public boolean getIsLoggedIn() {
        return pref.getBoolean(PREF_ISLOGGEDIN, false);
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(PREF_ISLOGGEDIN, isLoggedIn);
        editor.commit();
    }

    public void saveAuthToken(String value) {
        editor.putString(PREF_AUTH_TOKEN, value);
        editor.commit();
    }

    public String getAuthToken() {
        if (pref != null) {
            return pref.getString(PREF_AUTH_TOKEN, "");
        }
        return "";
    }

    public void saveDealerName(String value) {
        editor.putString(PREF_DEALER_NAME, value);
        editor.commit();
    }

    public String getDealerName() {
        if (pref != null) {
            return pref.getString(PREF_DEALER_NAME, "");
        }
        return "";
    }


    public void saveDealerBalance(String value) {
        editor.putString(PREF_DEALER_BALANCE, value);
        editor.commit();
    }

    public String getDealerBalance() {
        if (pref != null) {
            return pref.getString(PREF_DEALER_BALANCE, "");
        }
        return "";
    }

    public void saveDealerZone(String value) {
        editor.putString(PREF_DEALER_ZONE, value);
        editor.commit();
    }

    public String getDealerZone() {
        if (pref != null) {
            return pref.getString(PREF_DEALER_ZONE, "");
        }
        return "";
    }

}
