package com.example.ahn.finalproject.CapsulePrivate;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.ahn.finalproject.MainLogin.R;

import java.io.IOException;

/**
 * Created by Ahn on 2017-04-23.
 */

public class CapsulePrivateActivity extends FragmentActivity{
    Bitmap image_bitmap;
    String imgPath;
    String imgName;
    View view;
    /*protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_private_capsule);
    }*/

    public void test(Intent data) {
        String name_Str = getImageNameToUri(data.getData());
        imgName=name_Str;
        Log.e("fileName : ",name_Str);
        //이미지 데이터를 비트맵으로 받아온다.
        try {
            image_bitmap 	= MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
        ImageView image = (ImageView) view.findViewById(R.id.privateImgView);

        //배치해놓은 ImageView에 set
        image.setImageBitmap(image_bitmap);
    }
    //파일명 추출
    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        imgPath = cursor.getString(column_index);
        imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
        Log.e("ImageName : ",imgName);
        return imgName;
    }
}
