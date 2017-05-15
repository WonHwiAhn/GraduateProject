package com.example.ahn.finalproject.Friend;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

import com.example.ahn.finalproject.Adapter.ListViewAdapter;
import com.example.ahn.finalproject.GlobalValues.Main;
import com.example.ahn.finalproject.MainLogin.LoginComplete;
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
 * Created by Ahn on 2017-05-08.
 */

public class AddFriend extends AppCompatActivity {
    EditText searchFriend;
    private ListView friendList;
    private ListViewAdapter mAdapter;
    public ArrayList<ListData> friends = new ArrayList<>();
    private String friend;
    private static final int ADD_FRIEND_CODE = 5;

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.friend_add);

        searchFriend = (EditText) findViewById(R.id.searchFriend);
        friendList = (ListView) findViewById(R.id.friendList);

        /**********리스트뷰************/
        friends = new ArrayList<>();

        mAdapter = new ListViewAdapter(this, R.layout.friend_search_list, friends);

        friendList.setAdapter(mAdapter);
        /******************************/


        searchFriend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /*int size = mAdapter.getCount();
                for(int j=0;j<size;j++)
                    friends.remove(mAdapter.getItem(j-1));
                mAdapter.notifyDataSetChanged();*/
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mAdapter.dataClear();
                mAdapter.notifyDataSetChanged();
                final FriendName friendName = new FriendName();
                try {
                    friend = friendName.execute(searchFriend.getText().toString()).get();
                    if(!friend.equals("null")) {
                        String[] eachFriendId = friend.split(",");
                        for (int j = 0; j < eachFriendId.length; j+=2) {
                            eachFriendId[j+1] = eachFriendId[j+1].replace("/usr/local/nodeServer/public", "http://210.123.254.219:3001");
                            friends.add(new ListData(eachFriendId[j], eachFriendId[j+1]));  //리스트에 추가할 데이터
                        }
                    }else{
                        mAdapter.dataClear();
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    class FriendName extends AsyncTask<String, Integer, String> {
        URL url;
        HttpURLConnection conn;
        String line;
        int menuSeq;


        @Override
        protected String doInBackground(String... urls) {

            BufferedReader bufferedReader = null;
            line = "";
            String word = urls[0];
            Log.d("@@@", "word @@@@ " + word);
            try {
                url = new URL("http://210.123.254.219:3001" + "/getUser");

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("word", "UTF-8") + "=" + URLEncoder.encode(word,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));

                line = br.readLine();

                Log.d("@@@@", "friend@@@@" + line);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Log.e("line", line);
            return line;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), LoginComplete.class);
        intent.putExtra("id", Main.getUserId());
        intent.putExtra("idx", Main.getUserIdx());
        intent.putExtra("profile", Main.getProfile());
        startActivity(intent);
        finish();
    }
}
