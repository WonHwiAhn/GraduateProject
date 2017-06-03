package com.example.ahn.finalproject.CapsuleGroup;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.ahn.finalproject.GlobalValues.Main;
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
 * Created by Ahn on 2017-05-28.
 */

public class CapsuleGroupMake extends AppCompatActivity{
    String userInvite ;
    String userGroupIdx;
    String user;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        /**********************************************************
         * fcm푸쉬로부터 얻어오는 데이터들
         ********************************************************/

        /*Intent intent = getIntent();
        Toast.makeText(getApplicationContext(), intent.getExtras().getString("groupIdx"), Toast.LENGTH_LONG).show();
        Log.e("@@@", "IDX   " + intent.getExtras().getString("groupIdx"));*/

        //CheckOwner checkOwner = new CheckOwner();
        //checkOwner.execute().get;

        if(true)
            setContentView(R.layout.capsule_group_make);
        else
            setContentView(R.layout.capsule_group_make1);

        CheckInviteStatus checkInviteStatus = new CheckInviteStatus();
        try {
            String result = checkInviteStatus.execute().get();

            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.getString("id").equals(Main.getUserId())) {
                    userInvite = jsonObject.getString("invite");
                    userGroupIdx = jsonObject.getString("groupIdx");
                    user = jsonObject.getString("user");
                }
            }
            Log.e("@@@@", "userInvite: " + userInvite + "   userGroupIdx: " + userGroupIdx + "   user:" + user);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(userInvite.equals("2")){
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.dialog_waiting_request, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Member Information");
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setView(dialogView);


            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            /********************이 부분 수정해야됨**************/
            /*while(true){

            }*/
        }
    }

    /***************************************
     * invite 값 체크하는 곳
     **************************************/

    class CheckInviteStatus extends AsyncTask<String, Integer, String> {
        URL url;
        //HttpURLConnection conn;
        String line;
        int menuSeq;


        @Override
        protected String doInBackground(String... urls) {

            BufferedReader bufferedReader = null;
            line = "";
            String userId = Main.getUserId();
            try {
                url = new URL("http://210.123.254.219:3001" + "/getGroupIdx");
                //conn = (HttpURLConnection) url.openConnection();

                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data = URLEncoder.encode("myId", "UTF-8") + "=" + URLEncoder.encode(userId,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
                line = br.readLine();
                Log.d("@@@@@", "CheckInviteStatusLine@@@@"+line);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Log.e("line",line);
            return line;
        }
    }

    /***************************************
     * owner 값 체크하는 곳
     **************************************/

    class CheckOwner extends AsyncTask<String, Integer, String> {
        URL url;
        //HttpURLConnection conn;
        String line;
        int menuSeq;


        @Override
        protected String doInBackground(String... urls) {

            BufferedReader bufferedReader = null;
            line = "";
            String userId = Main.getUserId();
            try {
                url = new URL("http://210.123.254.219:3001" + "/getOwner");
                //conn = (HttpURLConnection) url.openConnection();

                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data = URLEncoder.encode("myId", "UTF-8") + "=" + URLEncoder.encode(userId,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
                line = br.readLine();
                Log.d("@@@@@", "CheckInviteStatusLine@@@@"+line);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Log.e("line",line);
            return line;
        }
    }
}
