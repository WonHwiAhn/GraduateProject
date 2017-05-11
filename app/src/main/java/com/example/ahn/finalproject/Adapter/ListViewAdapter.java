package com.example.ahn.finalproject.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ahn.finalproject.MainLogin.R;
import com.example.ahn.finalproject.VO.ListData;

import java.util.ArrayList;

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
    private int position;

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
    public View getView(int position, View convertView, final ViewGroup viewGroup) {

        if(convertView==null) {
            convertView = inflater.inflate(this.layout, viewGroup, false);
            holder = new ViewHolder();

            holder.requestFriend = (Button) convertView.findViewById(requestFriend);
            holder.requestFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("@@@", "리스너 버튼 클릭됨!!!");
                    /*
                     * select 했을 때 db에서 널 값일 경우 insert 가능하게
                     * 널값이 아니면 status값 체크 후 친구상태인지 대기상태인지 파악
                     */
                }
            });
        }

        TextView friendId = (TextView) convertView.findViewById(R.id.friendId);
        ImageView friendImg = (ImageView) convertView.findViewById(R.id.friendImg);

        friendId.setText(mListData.get(position).getFriendId() + " | ");
        friendImg.setImageResource(mListData.get(position).getImg());
        holder.requestFriend.setTag(position);

        return convertView;
    }

    public void dataClear(){
        mListData.clear();
    }

    static class ViewHolder{
        Button requestFriend;
    }
}
