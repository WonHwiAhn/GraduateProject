package com.example.ahn.finalproject.Friend;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

import com.example.ahn.finalproject.Adapter.RequestFriendListAdapter;
import com.example.ahn.finalproject.GlobalValues.Main;
import com.example.ahn.finalproject.MainLogin.R;
import com.example.ahn.finalproject.VO.ListData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Ahn on 2017-05-22.
 */

public class RequestFriend extends AppCompatActivity {
    EditText searchFriend;
    ListView friendList;
    ArrayList<ListData> friends = new ArrayList<>();
    ArrayList<String> id = new ArrayList<>();
    ArrayList<String> status = new ArrayList<>();
    private RequestFriendListAdapter mAdapter;
    String result;


    protected void onCreate(Bundle savedStateInstance){
        super.onCreate(savedStateInstance);
        setContentView(R.layout.friend_request);

        searchFriend = (EditText) findViewById(R.id.searchFriend);
        friendList = (ListView) findViewById(R.id.friendList);

        /**********리스트뷰************/
        friends = new ArrayList<>();

        mAdapter = new  RequestFriendListAdapter(this, R.layout.friend_request_list, friends);

        friendList.setAdapter(mAdapter);

        FriendCheck friendCheck = new FriendCheck();
        try {
            mAdapter.dataClear();
            mAdapter.notifyDataSetChanged();
            result = friendCheck.execute().get();
            String[] check = result.split("\\*");
            for(int i=0;i<check.length;i++){
                id.add(check[i].split(",")[0]);
                status.add(check[i].split(",")[1]);
                Log.e("@@@", "status(i)@@@@@@@@@@@"+status.get(i));
                if(status.get(i).equals("1"))
                    friends.add(new ListData(id.get(i), status.get(i), ""));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    class FriendCheck extends AsyncTask<String, Integer, String> {
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
                url = new URL("http://210.123.254.219:3001" + "/getFriend1");
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
                Log.d("@@@@@", "LineLineLine@@@@"+line);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Log.e("line",line);
            return line;
        }

    }
}
