package com.example.ahn.finalproject.CapsuleShow;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahn.finalproject.GlobalValues.Main;
import com.example.ahn.finalproject.MainLogin.BackgroundTask;
import com.example.ahn.finalproject.MainLogin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

/**
 * Created by Ahn on 2017-04-16.
 */

public class CapsuleShowMainFragment extends Fragment {
    int flag=0;
    View view;
    Button testbtn;
    LinearLayout linearTest;
    String string; //개인캡슐 정보
    String string1; // 단체캡슐 정보
    String userId;
    String userList;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_show_capsule, container, false);

        linearTest = (LinearLayout) view.findViewById(R.id.linearTest);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);


        /***********스피너 시작****************/
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.스피너목록, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent , View view, int pos ,long id) {
                if(parent.getItemAtPosition(pos).toString().equals("개인캡슐")){

                    /*******************캡슐 기본 정보 가져오는 곳************************/
                    BackgroundTask backgroundTask = new BackgroundTask(getContext());

                    String userId = Main.getUserId();

                    try {
                        string = backgroundTask.execute("getCapsule", userId).get();  //userId를 통해서 privateCapsule가옴
                        Log.d("@@", string);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    /************************************************************************/

                    linearTest.post(new Runnable() {
                        @Override
                        public void run() {
                            linearTest.removeAllViews();
                            showPrivatecapsule();
                        }
                    });

                }else if(parent.getItemAtPosition(pos).toString().equals("단체캡슐")){
                    /*******************단체 캡슐 기본 정보 가져오는 곳************************/
                    GetGroupcapsule getGroupcapsule = new GetGroupcapsule();

                    String userId = Main.getUserId();

                    try {
                        string1 = getGroupcapsule.execute(userId).get();  //userId를 통해서 privateCapsule가옴
                        Log.d("@@@", "getGroupcapsule information" + string1);
                        string1 = string1.replaceAll("]", ",");
                        string1 = string1.replaceAll("\\[", "");
                        string1 = string1.substring(0, string1.length()-1);
                        string1 = "["+string1+"]";
                        Log.d("@@@", "Changed getGroupcapsule information" + string1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    linearTest.post(new Runnable() {
                        @Override
                        public void run() {
                            linearTest.removeAllViews();
                            showGroupcapsule();
                        }
                    });
                }else if(parent.getItemAtPosition(pos).toString().equals("종합캡슐")){

                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
/****
 * 전체 레이아웃 배경 적용 후
 * 텍스트, 버튼 붙이면 될 듯?
 */
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

    public void showPrivatecapsule(){
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
                            intent.putExtra("makedate", jsonObject.getString("makedate"));
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
                //frameLayout.removeAllViews();
            }
        } catch (JSONException ex){
            ex.printStackTrace();
        }
    }

    public void showGroupcapsule(){
        try {
            JSONArray jsonArray = new JSONArray(string1);
            for(int i=0; i<jsonArray.length();i++){
                final JSONObject jsonObject = jsonArray.getJSONObject(i);
                // RelativeLayout 생성

                RelativeLayout rl = new RelativeLayout(getContext());

                // RelativeLayout width, height 설정

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(400,800);

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

                //차이드 생성 (제목)
                TextView textTitle = new TextView(getContext());
                RelativeLayout.LayoutParams textTitleParams = new RelativeLayout.LayoutParams
                        (rl.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);
                textTitle.setLayoutParams(textTitleParams);
                textTitle.setId('0');
                //text.setWidth(rl.getLayoutParams().width);
                textTitle.setTextColor(Color.BLACK);
                //Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL
                textTitle.setGravity(Gravity.CENTER_HORIZONTAL);
                textTitle.setText("--Title--\n"+jsonObject.getString("title"));

                //차이드 생성 (텍스트)
                TextView text = new TextView(getContext());
                RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams
                        (rl.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);
                textParams.addRule(RelativeLayout.BELOW, '0');
                text.setLayoutParams(textParams);

                text.setId('1');
                //text.setWidth(rl.getLayoutParams().width);
                text.setTextColor(Color.BLACK);
                //Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL
                text.setGravity(Gravity.CENTER_HORIZONTAL);
                text.setText("--MakeDate--\n"+jsonObject.getString("makedate"));

                //차이드 생성 (방장)
                TextView textOwner = new TextView(getContext());
                RelativeLayout.LayoutParams textOwnerParams = new RelativeLayout.LayoutParams
                        (rl.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);
                textOwnerParams.addRule(RelativeLayout.BELOW, '1');
                textOwner.setLayoutParams(textOwnerParams);
                textOwner.setId('2');
                //text.setWidth(rl.getLayoutParams().width);
                textOwner.setTextColor(Color.BLACK);
                //Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL
                textOwner.setGravity(Gravity.CENTER_HORIZONTAL);
                textOwner.setText("--Owner--\n"+jsonObject.getString("owner"));

                //차이드 생성 (유저)
                TextView textUser = new TextView(getContext());
                RelativeLayout.LayoutParams textUserParams = new RelativeLayout.LayoutParams
                        (rl.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);
                textUserParams.addRule(RelativeLayout.BELOW, '2');
                textUser.setLayoutParams(textUserParams);
                textUser.setId('3');
                //text.setWidth(rl.getLayoutParams().width);
                textUser.setTextColor(Color.BLACK);
                //Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL
                textUser.setGravity(Gravity.CENTER_HORIZONTAL);
                if(jsonObject.getString("user").length() > 15){
                    String parseData = jsonObject.getString("user").substring(0, 15);
                    parseData += "...";
                    textUser.setText("--User--\n"+parseData);
                }else {
                    textUser.setText("--User--\n" + jsonObject.getString("user"));
                }
                //차이드 생성 (버튼)
                Button button = new Button(getContext());
                RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams
                        (rl.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);
                buttonParams.addRule(RelativeLayout.BELOW, '3');
                button.setLayoutParams(buttonParams);
                button.setText("타임캡슐 보기");
                button.setTag(jsonObject.getString("content").toString());
                button.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view){
                        Intent intent = new Intent(getContext(), CapsuleGroup.class);
                        try {
                            intent.putExtra("idx", jsonObject.getString("idx"));
                            intent.putExtra("owner", jsonObject.getString("owner"));
                            intent.putExtra("content", jsonObject.getString("content"));
                            intent.putExtra("title", jsonObject.getString("title"));
                            intent.putExtra("picturepath", jsonObject.getString("picturepath"));
                            startActivityForResult(intent, 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getContext(), view.getTag().toString(), Toast.LENGTH_LONG).show();

                    }
                });

                rl.addView(textTitle);
                rl.addView(text);
                rl.addView(textOwner);
                rl.addView(textUser);
                rl.addView(button);
                FrameLayout frameLayout = new FrameLayout(getContext());

                frameLayout.addView(rl);
                linearTest.addView(frameLayout);
            }
        } catch (JSONException ex){
            ex.printStackTrace();
        }
    }


    /****************************대기 시간 공유(초대받은 유저일 때) *************************************/
    class GetGroupcapsule extends AsyncTask<String, Integer, String> {
        URL url;
        //HttpURLConnection conn;
        int menuSeq;


        @Override
        protected String doInBackground(String... urls) {

            BufferedReader bufferedReader = null;
            userList = "";
            String userId = Main.getUserId();

            try {
                //while (true) {
                url = new URL("http://210.123.254.219:3001" + "/getGroupcapsule");
                //conn = (HttpURLConnection) url.openConnection();

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data = URLEncoder.encode("myId", "UTF-8") + "=" + URLEncoder.encode(urls[0],"UTF-8");

                // Log.e("@@@", "time: " + urls[0] + "   groupIdx: " + urls[1]);

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
                userList = br.readLine();
                Log.d("@@@@@", "getGroupcapsuleLine@@@@" + userList);
                //Thread.sleep(1000);
                //}
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Log.e("line", userList);
            return userList;
        }
    }
}
