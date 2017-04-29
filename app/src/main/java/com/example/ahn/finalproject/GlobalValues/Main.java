package com.example.ahn.finalproject.GlobalValues;

import android.app.Application;

/**
 * Created by Ahn on 2017-04-29.
 */

public class Main extends Application {
    private static String userId;
    public static String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId = userId;
    }
}
