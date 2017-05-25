package com.example.ahn.finalproject.MainLogin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "@@@";

    private static boolean flag = true;
    private BackPressCloseHandler backPressCloseHandler;

    Button btnLogin;
    EditText input_email, input_password;
    TextView btnSignup, btnForgotPass;

    RelativeLayout activity_main;
    String imgPath, loginId, loginIdx, loginImgPath;
    SharedPreferences auto;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*CheckData checkData = new CheckData();
        checkData.execute();
        Intent intent = new Intent(this, test.class);
        startActivity(intent);
        finish();*/
        //View
        btnLogin = (Button) findViewById(R.id.login_btn_login);
        input_email = (EditText) findViewById(R.id.login_email);
        input_password = (EditText) findViewById(R.id.login_password);
        btnSignup = (TextView) findViewById(R.id.login_btn_signup);
        btnForgotPass = (TextView) findViewById(R.id.login_btn_forgot_password);
        activity_main = (RelativeLayout) findViewById(R.id.activity_main);

        btnSignup.setOnClickListener(listener);
        btnForgotPass.setOnClickListener(listener);
        btnLogin.setOnClickListener(listener);

        backPressCloseHandler = new BackPressCloseHandler(this);

        auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        loginId = auto.getString("inputId", null);
        loginIdx = auto.getString("inputIdx", null);

        if (loginId != null) {
                Intent intent = new Intent(MainActivity.this, LoginComplete.class);
                loginId = auto.getString("inputId",null);
                loginIdx = auto.getString("inputIdx",null);
                loginImgPath = auto.getString("inputProfile", null);
                intent.putExtra("id", loginId);
                intent.putExtra("idx", loginIdx);
                intent.putExtra("profile", loginImgPath);
                startActivity(intent);
                finish();
        }
        //id와 pwd가 null이면 Mainactivity가 보여짐.
        /*else if (loginId == null && loginPwd == null) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (id.getText().toString().equals("부르곰") && pwd.getText().toString().equals("네이버")) {
                        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                        //아이디가 '부르곰'이고 비밀번호가 '네이버'일 경우 SharedPreferences.Editor를 통해
                        //auto의 loginId와 loginPwd에 값을 저장해 줍니다.
                        SharedPreferences.Editor autoLogin = auto.edit();
                        autoLogin.putString("inputId", id.getText().toString());
                        autoLogin.putString("inputPwd", pwd.getText().toString());
                        //꼭 commit()을 해줘야 값이 저장됩니다 ㅎㅎ
                        autoLogin.commit();
                        Toast.makeText(MainActivity.this, id.getText().toString() + "님 환영합니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, SubActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }*/
    }

    Button.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.login_btn_forgot_password) {
                startActivity(new Intent(MainActivity.this, ForgotPassword.class));
            } else if (view.getId() == R.id.login_btn_signup) {
                startActivity(new Intent(MainActivity.this, SignUp.class));
                finish();
            } else if (view.getId() == R.id.login_btn_login) {
                loginUser(input_email.getText().toString(), input_password.getText().toString());
            }
        }
    };

    class GetDataTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading Data...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return getData(params[0]);
            } catch (IOException ex) {
                return "Network error!";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            String id = input_email.getText().toString();
            String pw = input_password.getText().toString();

            super.onPostExecute(result);
            HashMap<String, String> mapInfo = new HashMap<>();
            HashMap<String, String> userIdx = new HashMap<>();
            HashMap<String, String> mapImg= new HashMap<>();

            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    mapInfo.put(jsonObject.getString("id"), jsonObject.getString("password"));
                    userIdx.put(jsonObject.getString("id"), jsonObject.getString("idx"));
                    mapImg.put(jsonObject.getString("id"), jsonObject.getString("profileImgPath"));
                    imgPath = jsonObject.getString("profileImgPath");
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }

            Iterator<String> keySetIterator = mapInfo.keySet().iterator();

            while (keySetIterator.hasNext()) {
                String key = keySetIterator.next();
                if (key.equals(id)) {
                    if (mapInfo.get(key).equals(pw)) {
                        String idx = userIdx.get(key);
                        imgPath = mapImg.get(key);
                        flag = true;
                        Intent intent = new Intent(getApplicationContext(), LoginComplete.class);
                        SharedPreferences.Editor autoLogin = auto.edit();
                        autoLogin.putString("inputId", id);
                        autoLogin.putString("inputIdx", idx);
                        autoLogin.putString("inputProfile", imgPath);
                        //꼭 commit()을 해줘야 값이 저장됩니다 ㅎㅎ
                        autoLogin.commit();
                        intent.putExtra("id", id);
                        intent.putExtra("idx", idx);
                        intent.putExtra("profile", imgPath);
                        startActivity(intent);
                        finish();
                        break;
                    }
                } else {
                    flag = false;
                }
            }
            if (!flag) {
                //로그인 실패 했을 때
                Toast.makeText(getApplicationContext(), "로그인 실패!!!", Toast.LENGTH_LONG).show();
            }


            //cancel progress dialog
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }

        private String getData(String urlPath) throws IOException {
            StringBuilder result = new StringBuilder();
            BufferedReader bufferedReader = null;
            //Initialize and config request
            try {
                URL url = new URL(urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000 /*milliseconds*/);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json"); //set header
                urlConnection.connect();

                //Read data from server
                InputStream inputStream = urlConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line).append("\n");
                }
            } catch (IOException e) {
                return "Network error!";
            } finally {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
            return result.toString();
        }
    }

    private void loginUser(String email, final String password) {
        //학교 pc
        new GetDataTask().execute("http://210.123.254.219:3001");
        //내 pc  http://allanahn.iptime.org:3001
        //new GetDataTask().execute("http://allanahn.iptime.org:3001");
        //Snackbar snackBar = Snackbar.make(activity_main, "Password length must be over 6", Snackbar.LENGTH_SHORT);
        // snackBar.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //데이터 받기
                String result = data.getStringExtra("result");
            }
        }
    }

    private class CheckData extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... url) {
            for (int i = 0; i < 30; i++) {
                String token = FirebaseInstanceId.getInstance().getToken();
                Log.e("@@@", ""+token);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
            //try{

            //URL ImgUrl = new URL(url[0]);
            //String imgName = url[1];
            //String picturePath = url[1];

                /*HttpURLConnection httpURLConnection = (HttpURLConnection) ImgUrl.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("imgPath", "UTF-8") + "=" + URLEncoder.encode(picturePath,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                is.close();*/
            /*}catch(IOException e){
                e.printStackTrace();
            }*/
            //return "";


        protected void onPostExecute(Bitmap img) {

        }
    }

    public void onBackPressed(){
        backPressCloseHandler.onBackPressed();
    }
}
