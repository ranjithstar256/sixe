package com.example.gaayathri.a6eskills.Activites;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Lincoln on 05/05/16.
 */
public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "androidhive-welcome";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    String myMobNum="";
    private static final String IS_Uploaded = "IS_Uploaded";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void setisContactUpload(boolean isuploaded) {
        editor.putBoolean(IS_Uploaded,isuploaded);
        editor.commit();
    }
    public boolean isContactUpload(){
        return pref.getBoolean(IS_Uploaded,false);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }
    public void setMyMobNum(String num) {
        editor.putString("num",num);
        editor.commit();
    }
    public String getMyMobNum(){
        return pref.getString("num","123456");
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

}
