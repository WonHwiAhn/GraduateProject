package com.example.ahn.finalproject.CapsulePrivate;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ahn.finalproject.GlobalValues.Main;
import com.example.ahn.finalproject.MainLogin.BackgroundTask;
import com.example.ahn.finalproject.MainLogin.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Created by Ahn on 2017-04-16.
 */

public class CapsulePrivateMainFragment extends Fragment {
    final static private String TAG = "@@@";
    static private Button privateGallery, privateCamera, privateMake;
    static private int REQ_CODE_SELECT_IMAGE = 100;
    static private int REQ_CODE_CAPTURE_IMAGE = 101;
    Uri albumImage, fileUri;
    Bitmap image_bitmap;
    Bitmap bitmap;
    String imgPath;
    String imgName;
    StringBuilder encodedString;
    View view;
    ImageView image; //사진 미리보기 역할해주는 변수
    EditText capsulePrivateLetter;
    String userId;
    //CapsulePrivateActivity cpa = new CapsulePrivateActivity();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_private_capsule, container, false);
        capsulePrivateLetter = (EditText) view.findViewById(R.id.capsulePrivateLetter);
        privateGallery = (Button) view.findViewById(R.id.privateGallery);
        privateCamera = (Button) view.findViewById(R.id.privateCamera);
        privateMake = (Button) view.findViewById(R.id.privateMake);
        privateGallery.setOnClickListener(listener);
        privateCamera.setOnClickListener(listener);
        privateMake.setOnClickListener(listener);

        return view;
    }

    Button.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view){
            if(view.getId() == R.id.privateGallery){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }else if(view.getId() == R.id.privateCamera){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri(1);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, REQ_CODE_CAPTURE_IMAGE);
            }else if(view.getId() == R.id.privateMake){
                // When Image is selected from Gallery
                if (imgPath != null && !imgPath.isEmpty()) {
                    Log.e("Converting Image to ","Binary Data");
                    // Convert image to String using Base64
                    encodeImagetoString();
                    // When Image is not selected from Gallery
                } else {
                    Toast.makeText(
                            getContext(),
                            "You must select image from gallery before you try to upload",
                            Toast.LENGTH_LONG).show();
                }
            }/*else if(view.getId() == R.id.login_btn_login){
                loginUser(input_email.getText().toString(), input_password.getText().toString());
            }*/
        }
    };

    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        if(!mediaStorageDir.exists()){
            Log.d(TAG, "@@@@@@@@@" + "Null Directory");
            if(!mediaStorageDir.mkdir()){
                Log.d(TAG, "@@@@@@@" + "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if(type == 1){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg" );
            Log.d(TAG, "@@@@@@@@@" + "make file");
        }else{
            Log.d(TAG, "@@@@@@@@@" + "We can't make file");
            return null;
        }
        return mediaFile;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        Toast.makeText(getContext(), "resultCode : "+resultCode,Toast.LENGTH_SHORT).show();

        if(requestCode == REQ_CODE_SELECT_IMAGE)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                try {
                    //cpa.test(data);

                    //Uri에서 이미지 이름을 얻어온다.
                    String name_Str = getImageNameToUri(data.getData());
                    imgName=name_Str;
                    Log.e("fileName : ",name_Str);
                    //이미지 데이터를 비트맵으로 받아온다.
                    image_bitmap 	= MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                    //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    image = (ImageView) view.findViewById(R.id.privateImgView);

                    //배치해놓은 ImageView에 set
                    image.setImageBitmap(image_bitmap);


                    //Toast.makeText(getBaseContext(), "name_Str : "+name_Str , Toast.LENGTH_SHORT).show();


                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }else if(requestCode == REQ_CODE_CAPTURE_IMAGE){
            if(data != null)
                Toast.makeText(getContext(), "Image saved to:" + data.getData(), Toast.LENGTH_LONG).show();
            else if(data == null)
                Toast.makeText(getContext(), "null 입니다", Toast.LENGTH_LONG).show();

            image = (ImageView) view.findViewById(R.id.privateImgView);
            Bitmap bm = null;
            try {
                bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
            image.setImageBitmap(bm);
        }
    }

    public void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {

            public void onPreExecute() {

            };

            @Override
            public String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                bitmap = BitmapFactory.decodeFile(imgPath,
                        options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                encodedString = new StringBuilder(Base64.encodeToString(byte_arr, 0));
                Log.e("mainSideImg","");
                Log.e("mainSideImg",""+encodedString.length());
                return "";
            }

            @Override
            public void onPostExecute(String msg) {
                // Trigger Image upload
                makeHTTPCall();
            }
        }.execute(null, null, null);
    }

    public void makeHTTPCall() {
        String capsuleContent = capsulePrivateLetter.getText().toString();
        BackgroundTask backgroundTask = new BackgroundTask(getContext());
        String userId = Main.getUserId();
        String date = getDate();
        Log.e("Main","excute start");
        try {
            String result = backgroundTask.execute("makePrivateCapsule",encodedString.toString(),imgName,capsuleContent, userId, date).get();
            if(result.equals("success")){
                image.setImageBitmap(null);
                capsulePrivateLetter.setText("");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    //파일명 추출
    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContext().getContentResolver().query(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        imgPath = cursor.getString(column_index);
        //imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
        imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
        Log.e("ImageName : ",imgName);
        return imgName;
    }

    public String getDate(){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        String date = df.format(new Date());
        return date;
    }

}