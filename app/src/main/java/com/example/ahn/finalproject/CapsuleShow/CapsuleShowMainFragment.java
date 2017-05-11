package com.example.ahn.finalproject.CapsuleShow;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahn.finalproject.GlobalValues.Main;
import com.example.ahn.finalproject.MainLogin.BackgroundTask;
import com.example.ahn.finalproject.MainLogin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by Ahn on 2017-04-16.
 */

public class CapsuleShowMainFragment extends Fragment {
    int flag=0;
    View view;
    Button testbtn;
    LinearLayout linearTest;
    String string;
    String userId;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_show_capsule, container, false);

        linearTest = (LinearLayout) view.findViewById(R.id.linearTest);

        BackgroundTask backgroundTask = new BackgroundTask(getContext());

        String userId = Main.getUserId();

        try {
            string = backgroundTask.execute("getCapsule", userId).get();
            Log.d("@@", string);
    } catch (InterruptedException e) {
        e.printStackTrace();
    } catch (ExecutionException e) {
        e.printStackTrace();
    }
/****
 * 전체 레이아웃 배경 적용 후
 * 텍스트, 버튼 붙이면 될 듯?
 */
        try {
            JSONArray jsonArray = new JSONArray(string);
            for(int i=0; i<jsonArray.length();i++){
                final JSONObject jsonObject = jsonArray.getJSONObject(i);
                // RelativeLayout 생성
                RelativeLayout rl = new RelativeLayout(getContext());

                // RelativeLayout width, height 설정


                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(400,600);

                // RelativeLayout에 width, height 설정 적용
                rl.setLayoutParams(params);

                ViewGroup.MarginLayoutParams margin1 = new ViewGroup.MarginLayoutParams(
                        rl.getLayoutParams()
                );
                if(flag%2==0){
                    //int length = getLength();
                    //해상도 참고 사이트 http://qits.tistory.com/entry/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EB%94%94%EB%B0%94%EC%9D%B4%EC%8A%A4-%ED%99%94%EB%A9%B4-%ED%81%AC%EA%B8%B0-%EA%B5%AC%ED%95%98%EA%B8%B0
                    DisplayMetrics display = getContext().getResources().getDisplayMetrics();
                    margin1.setMargins(display.widthPixels-450,0,0,50);
                    Log.d("@@@", ""+display.widthPixels);
                    flag=1;
                }else{
                    margin1.setMargins(50,0,0,50);
                    flag=0;
                }
                rl.setLayoutParams(new FrameLayout.LayoutParams(margin1));

                /*ViewGroup.MarginLayoutParams margin1 = new ViewGroup.MarginLayoutParams(
                        rl.getLayoutParams()
                );
                //RelativeLayout.LayoutParams margin1 = (RelativeLayout.LayoutParams) rl.getLayoutParams();
                margin1.setMargins(100,0,0,600);
                rl.setLayoutParams(margin1);*/
                rl.setBackgroundResource(R.drawable.capsuletest);

                //차이드 생성 (텍스트)
                TextView text = new TextView(getContext());
                RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams
                        (rl.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);
                text.setLayoutParams(textParams);
                text.setId('1');
                //text.setWidth(rl.getLayoutParams().width);
                text.setTextColor(Color.BLACK);
                //Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL
                text.setGravity(Gravity.CENTER_HORIZONTAL);
                text.setText(jsonObject.getString("makedate"));

                //차이드 생성 (버튼)
                Button button = new Button(getContext());
                RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams
                        (rl.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);
                buttonParams.addRule(RelativeLayout.BELOW, '1');
                button.setLayoutParams(buttonParams);
                button.setText("타임캡슐 보기");
                button.setTag(jsonObject.getString("content").toString());
                button.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view){
                        Intent intent = new Intent(getContext(), CapsulePrivate.class);
                        try {
                            intent.putExtra("idx", jsonObject.getString("idx"));
                            intent.putExtra("owner", jsonObject.getString("owner"));
                            intent.putExtra("content", jsonObject.getString("content"));
                            intent.putExtra("picturepath", jsonObject.getString("picturepath"));
                            startActivityForResult(intent, 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getContext(), view.getTag().toString(), Toast.LENGTH_LONG).show();

                    }
                });

                /*LinearLayout.LayoutParams topButtonParams = new LinearLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);*/

                rl.addView(text);
                rl.addView(button);

                /*//이미지 뷰 생성하는 곳
                ImageView iv = new ImageView(getContext());
                iv.setImageResource(R.drawable.alone);

                iv.setLayoutParams(new FrameLayout.LayoutParams(100,100));
                iv.setTag( "private" + jsonObject.getString("idx"));
                Log.d(i + "@@", "" + iv.getTag());

                ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(
                        iv.getLayoutParams()
                );
                margin.setMargins(10,0,0,200);
                iv.setLayoutParams(new FrameLayout.LayoutParams(margin));
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(),view.getTag().toString(),Toast.LENGTH_LONG).show();
                    }
                });*/
                FrameLayout frameLayout = new FrameLayout(getContext());
                //frameLayout.addView(iv);

                frameLayout.addView(rl);
                linearTest.addView(frameLayout);
            }
        } catch (JSONException ex){
            ex.printStackTrace();
        }


        /*for(int i=0;)*/

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 1){
            Toast.makeText(getContext(), "comeback!", Toast.LENGTH_LONG).show();
        }
    }

    public int getLength(){
        Log.w("Layout Width - ", String.valueOf(linearTest.getWidth()));
        return 1;
    }
}
