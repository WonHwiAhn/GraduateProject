package com.example.ahn.finalproject.VO;

/**
 * Created by Ahn on 2017-05-08.
 */

public class ListData {
    private String friendId;
    private int Img;

    public ListData(String friendId, int Img){
        this.friendId = friendId;
        this.Img = Img;
    }

    public String getFriendId(){
        return this.friendId;
    }
    public int getImg(){
        return this.Img;
    }
}
