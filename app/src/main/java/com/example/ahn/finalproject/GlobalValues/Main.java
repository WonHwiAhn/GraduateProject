package com.example.ahn.finalproject.GlobalValues;

import android.app.Application;

/**
 * Created by Ahn on 2017-04-29.
 */

public class Main extends Application {
    private static String userId;
    private static String userIdx;

    public static String getUserIdx() { return userIdx; }
    public static void setUserIdx(String userIdx) { Main.userIdx = userIdx;}

    public static String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId = userId;
    }
}
