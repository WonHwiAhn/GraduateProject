package com.example.ahn.finalproject.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ahn.finalproject.MainLogin.BackgroundTask;
import com.example.ahn.finalproject.MainLogin.R;
import com.example.ahn.finalproject.VO.ListData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.example.ahn.finalproject.MainLogin.R.id.requestFriend;

/**
 * Created by Ahn on 2017-05-08.
 */

public class ListViewAdapter extends BaseAdapter {

    private Context mContext = null;
    private ArrayList<ListData> mListData = new ArrayList<>();
    private int layout;
    private LayoutInflater inflater;
    ViewHolder holder;
    private int position, status;
    private String result, myId, friendId, imgPath;
    private Bitmap bmImg;

    public ListViewAdapter(Context mContext, int layout, ArrayList<ListData> friends){
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
            holder = new ViewHolder();
        }

        final String fId = mListData.get(position).getFriendId();

        holder.requestFriend = (Button) convertView.findViewById(requestFriend);
        BackgroundTask backgroundTask = new BackgroundTask(mContext);
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
        }else if(fId.equals(myId) || fId.equals(friendId)){
            if(status == 1){
                holder.requestFriend.setBackgroundResource(R.drawable.wait_friend_button);
                holder.requestFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(mContext, "친구 수락 요청 대기중 입니다!", Toast.LENGTH_LONG).show();
                    }
                });
            }else if(status == 2){
                holder.requestFriend.setBackgroundResource(R.drawable.already_friend_button);
                holder.requestFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(mContext, "이미 친구 상태입니다!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        TextView friendId = (TextView) convertView.findViewById(R.id.friendId);
        ImageView friendImg = (ImageView) convertView.findViewById(R.id.friendImg);

        friendId.setText(mListData.get(position).getFriendId());

        imgPath = mListData.get(position).getImgPath();

        Glide.with(mContext).load(imgPath).centerCrop().bitmapTransform(new CropCircleTransformation(mContext)).into(friendImg);
        //이미지 원형으로 출력해주는 API

        return convertView;
    }

    public void dataClear(){
        mListData.clear();
    }

    static class ViewHolder{
        Button requestFriend;
    }
}
