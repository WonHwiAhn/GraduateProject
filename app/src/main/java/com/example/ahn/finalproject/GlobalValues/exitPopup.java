package com.example.ahn.finalproject.GlobalValues;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.example.ahn.finalproject.MainLogin.R;

/**
 * Created by Ahn on 2017-04-30.
 */

public class ExitPopup extends Activity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_exit_popup);
    }

    //예 버튼 클릭
    public void btnPositive(View v){
        //데이터 전달하기
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_CANCELED, intent);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //액티비티(팝업) 닫기
        finish();
    }

    //아니오 버튼 클릭
    public void btnNegative(View v){
        //데이터 전달하기
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}
