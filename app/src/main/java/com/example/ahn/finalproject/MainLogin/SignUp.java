package com.example.ahn.finalproject.MainLogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.ahn.finalproject.MainLogin.R.id.signup_email;
import static com.example.ahn.finalproject.MainLogin.R.id.signup_password;

public class SignUp extends AppCompatActivity {

    Button btnSignup;
    TextView btnLogin, btnForgotPass;
    EditText input_email, input_pw;
    RelativeLayout activity_sign_up;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnSignup = (Button) findViewById(R.id.signup_btn_register);
        btnLogin = (TextView) findViewById(R.id.signup_btn_login);
        btnForgotPass = (TextView) findViewById(R.id.signup_btn_forgot_pass);
        input_email = (EditText) findViewById(signup_email);
        input_pw= (EditText) findViewById(signup_password);
        activity_sign_up = (RelativeLayout) findViewById(R.id.activity_sign_up);

        btnSignup.setOnClickListener(listener);
        btnLogin.setOnClickListener(listener);
        btnForgotPass.setOnClickListener(listener);

        input_pw.post(new Runnable() {
            @Override
            public void run() {
                input_pw.setFocusableInTouchMode(true);
                input_pw.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(input_email, 0);
            }
        });
    }

    Button.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.signup_btn_login){
                startActivity(new Intent(SignUp.this, MainActivity.class));
                finish();
            }else if(view.getId() == R.id.signup_btn_forgot_pass){
                startActivity(new Intent(SignUp.this, ForgotPassword.class));
                finish();
            }else if(view.getId() == R.id.signup_btn_register){
                signUpUser(input_email.getText().toString(), input_pw.getText().toString());
            }
        }
    };

    class PostDataTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(SignUp.this);
            progressDialog.setMessage("Inserting Data...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try{
                return postData(params[0]);
            }catch (IOException ex){
                return "Network error!";
            }catch(JSONException ex){
                return "Data Invalid";
            }
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            //mResult.setText(result);

            if(progressDialog != null){
                progressDialog.dismiss();
            }
        }

        private String postData (String urlPath) throws IOException, JSONException{

            StringBuffer result = new StringBuffer();
            BufferedWriter bufferedWriter = null;
            BufferedReader bufferedReader = null;

            String id = input_email.getText().toString();
            String pw = input_pw.getText().toString();
            try {
                //create data to send to server
                JSONObject dataToSend = new JSONObject();
                dataToSend.put("id", id);
                dataToSend.put("pw", pw);

                //Initialize and config request, then connect to server.
                URL url = new URL(urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000 /*milliseconds*/);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true); //enable output (dody data)
                urlConnection.setRequestProperty("Content-Type", "application/json"); //set header
                urlConnection.connect();

                //write data into server
                OutputStream outputStream = urlConnection.getOutputStream();
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

                bufferedWriter.write(dataToSend.toString());
                bufferedWriter.flush();

                //Read data response from server
                InputStream inputStream = urlConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line).append("\n");
                }
            }finally {
                if(bufferedReader != null){
                    bufferedReader.close();
                }
                if(bufferedWriter != null){
                    bufferedWriter.close();
                }
            }
            return result.toString();
        }

    }

    private void signUpUser(String email, String password){
        if(input_pw.length() < 10){
            snackbar = Snackbar.make(activity_sign_up, "Error: 패스워드 10개 이상 입력!!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }else{
            new SignUp.PostDataTask().execute("http://192.168.1.4:3000/api/userData");
            Intent intent = new Intent(getApplicationContext(), SignUpPopup.class);
            startActivityForResult(intent, 1);
        }
    }

    protected void onActivityResult(int requestCode, int result, Intent data){
        if(result==RESULT_OK) {
            startActivity(new Intent(SignUp.this, MainActivity.class));
            finish();
        }
    }
}

