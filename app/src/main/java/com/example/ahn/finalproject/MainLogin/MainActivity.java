package com.example.ahn.finalproject.MainLogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    Button btnLogin;
    EditText input_email, input_password;
    TextView btnSignup, btnForgotPass;

    RelativeLayout activity_main;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

    Button.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view){
            if(view.getId() == R.id.login_btn_forgot_password){
                startActivity(new Intent(MainActivity.this, ForgotPassword.class));
            }else if(view.getId() == R.id.login_btn_signup){
                startActivity(new Intent(MainActivity.this, SignUp.class));
            }else if(view.getId() == R.id.login_btn_login){
                loginUser(input_email.getText().toString(), input_password.getText().toString());
            }
        }
    };

    class GetDataTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading Data...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return getData(params[0]);
            }catch (IOException ex){
                return "Network error!";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            String id = input_email.getText().toString();
            String pw = input_password.getText().toString();

            super.onPostExecute(result);
            HashMap<String, String> mapInfo = new HashMap<>();

            try {
                JSONArray jsonArray = new JSONArray(result);
                for(int i=0; i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    mapInfo.put(jsonObject.getString("id"), jsonObject.getString("password"));
                }
            } catch (JSONException ex){
                ex.printStackTrace();
            }

            Iterator<String> keySetIterator = mapInfo.keySet().iterator();

            while(keySetIterator.hasNext()) {
                String key = keySetIterator.next();
                if(key.equals(id)){
                    if(mapInfo.get(key).equals(pw)){
                        flag=true;
                        Intent intent = new Intent(getApplicationContext(), LoginComplete.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
                        finish();
                        break;
                    }
                }else{
                    flag=false;
                }
            }
            if(!flag){
                //로그인 실패 했을 때
            }


            //cancel progress dialog
            if(progressDialog != null){
                progressDialog.dismiss();
            }
        }

        private String getData(String urlPath) throws IOException{
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
                while((line = bufferedReader.readLine()) != null){
                    result.append(line).append("\n");
                }
                Log.d(TAG, "@@@@@@@@@@@"+result);
            }catch (IOException e){
                return "Network error!";
            } finally {
                if(bufferedReader != null){
                    bufferedReader.close();
                }
            }
            return result.toString();
        }
    }

    private void loginUser(String email, final String password){

        new GetDataTask().execute("http://210.123.254.219:3001");
        //Snackbar snackBar = Snackbar.make(activity_main, "Password length must be over 6", Snackbar.LENGTH_SHORT);
        // snackBar.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                //데이터 받기
                String result = data.getStringExtra("result");
            }
        }
    }
}
