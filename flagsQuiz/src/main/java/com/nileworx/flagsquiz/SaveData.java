package com.nileworx.flagsquiz;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Jakob2000 on 20.04.2018.
 */

public class SaveData {

    public void saveNumber(String name, int number, Context ctx){
        SharedPreferences prefs = ctx.getSharedPreferences("werte", 0);
        SharedPreferences.Editor editor =prefs.edit();

        editor.putInt(name, number);
        editor.commit();
    }

    public int getNumber(String name, Context ctx){
        SharedPreferences prefs = ctx.getSharedPreferences("werte", 0);
        int result =prefs.getInt(name, 99);

        return result;
    }

    public void saveBoolean(String name, Boolean bl, Context ctx){
        SharedPreferences prefs = ctx.getSharedPreferences("werte", 0);
        SharedPreferences.Editor editor =prefs.edit();

        editor.putBoolean(name, bl);
        editor.commit();
    }

    public boolean getBoolean(String name, Context ctx){
        SharedPreferences prefs = ctx.getSharedPreferences("werte", 0);
        boolean result =prefs.getBoolean(name, false);

        return result;
    }

    public void saveString(String name, String string, Context ctx){
        SharedPreferences prefs = ctx.getSharedPreferences("werte", 0);
        SharedPreferences.Editor editor =prefs.edit();

        editor.putString(name, string);
        editor.commit();
    }

    public String getString(String name, Context ctx){
        SharedPreferences prefs = ctx.getSharedPreferences("werte", 0);
        String result =prefs.getString(name, "-error-");

        return result;
    }
}
