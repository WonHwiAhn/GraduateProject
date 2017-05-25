package com.example.ahn.finalproject.VO;

/**
 * Created by Ahn on 2017-05-08.
 */

public class ListData {
    private String friendId, imgPath, status;

    public ListData(String friendId, String imgPath){
        this.friendId = friendId;
        this.imgPath = imgPath;
    }

    public ListData(String friendId, String status, String extra){
        this.friendId = friendId;
        this.status = status;
    }

    public String getFriendId(){
        return this.friendId;
    }
    public String getImgPath(){
        return this.imgPath;
    }
    public String getStatus() {
        return this.status;
    }
}
