package com.example.ahn.finalproject.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.example.ahn.finalproject.MainLogin.R.id.acceptFriend;
import static com.example.ahn.finalproject.MainLogin.R.id.rejectFriend;

/**
 * Created by Ahn on 2017-05-22.
 */

public class RequestFriendListAdapter extends BaseAdapter {

    private Context mContext = null;
    private ArrayList<ListData> mListData = new ArrayList<>();
    private int layout;
    private LayoutInflater inflater;
    RequestFriendListAdapter.ViewHolder holder;
    private int position, status;
    private String result, myId, friendId, imgPath, fId;
    private Bitmap bmImg;

    public RequestFriendListAdapter(Context mContext, int layout, ArrayList<ListData> friends){
        this.mContext = mContext;
        this.mListData = friends;
        this.layout = layout;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        this.position = position;
        return this.position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup viewGroup) {
        if(convertView==null) {
            convertView = inflater.inflate(this.layout, viewGroup, false);
            holder = new RequestFriendListAdapter.ViewHolder();
        }

        fId = mListData.get(position).getFriendId();
        final String fStatus = mListData.get(position).getStatus();

        holder.acceptFriend = (Button) convertView.findViewById(acceptFriend);
        holder.rejectFriend = (Button) convertView.findViewById(rejectFriend);
        /*BackgroundTask backgroundTask = new BackgroundTask(mContext);
        try {
            result = backgroundTask.execute("getStatus", fId).get();
            holder.requestFriend.setTag(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        try {
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject jsonObject = jsonArray.getJSONObject(i);
                myId = jsonObject.getString("myId");
                friendId = jsonObject.getString("friendId");
                status = jsonObject.getInt("status");
            }
        }catch (JSONException e){}

        if(!(fId.equals(myId)) && !(fId.equals(friendId))){
            holder.requestFriend.setBackgroundResource(R.drawable.add_friend_button);
            holder.requestFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BackgroundTask backgroundTask = new BackgroundTask(mContext);
                    backgroundTask.execute("addFriend", fId);
                }
            });
        }*/

        TextView friendId = (TextView) convertView.findViewById(R.id.friendId);
        //ImageView friendImg = (ImageView) convertView.findViewById(R.id.friendImg);

        friendId.setText(mListData.get(position).getFriendId());

        holder.acceptFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendReq friendReq = new FriendReq();
                try {
                    String result = friendReq.execute("0").get();
                    if(result.equals("1")){
                        Toast.makeText(mContext, fId + "님과 친구가 되셨습니다.", Toast.LENGTH_LONG).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.rejectFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendReq friendReq = new FriendReq();
                try {
                    String result = friendReq.execute("1").get();
                    if(result.equals("1")){
                        Toast.makeText(mContext, "친구를 거절하셨습니다.", Toast.LENGTH_LONG).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        //imgPath = mListData.get(position).getImgPath();

        //Glide.with(mContext).load(imgPath).centerCrop().bitmapTransform(new CropCircleTransformation(mContext)).into(friendImg);
        //이미지 원형으로 출력해주는 API

        return convertView;
    }

    public void dataClear(){
        mListData.clear();
    }

    static class ViewHolder{
        Button acceptFriend;
        Button rejectFriend;
    }

    class FriendReq extends AsyncTask<String, Integer, String> {
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
                if(urls[0].equals("0")){
                    url = new URL("http://210.123.254.219:3001" + "/AcceptFriend");
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                    Log.e("@@@", "userId @@@@@@@@@@@@  " + userId);
                    Log.e("@@@", "fId @@@@@@@@@@@@  " + fId);

                    String data = URLEncoder.encode("myId", "UTF-8") + "=" + URLEncoder.encode(userId,"UTF-8")+ "&" +
                            URLEncoder.encode("friendId", "UTF-8") + "=" + URLEncoder.encode(fId,"UTF-8");;
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    OS.close();
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
                    line = br.readLine();
                }else if(urls[0].equals("1")){
                    url = new URL("http://210.123.254.219:3001" + "/rejectFriend");
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                    Log.e("@@@", "userId @@@@@@@@@@@@  " + userId);
                    Log.e("@@@", "fId @@@@@@@@@@@@  " + fId);

                    String data = URLEncoder.encode("myId", "UTF-8") + "=" + URLEncoder.encode(userId,"UTF-8")+ "&" +
                            URLEncoder.encode("friendId", "UTF-8") + "=" + URLEncoder.encode(fId,"UTF-8");
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    OS.close();
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
                    line = br.readLine();
                }
                //conn = (HttpURLConnection) url.openConnection();


                Log.d("@@@@@", "LineLineLine@@@@"+line);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Log.e("line",line);
            return line;
        }

    }
}