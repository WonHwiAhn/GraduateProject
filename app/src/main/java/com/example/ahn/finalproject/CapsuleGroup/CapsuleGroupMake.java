package com.example.ahn.finalproject.CapsuleGroup;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahn.finalproject.GlobalValues.Main;
import com.example.ahn.finalproject.MainLogin.BackPressCloseHandler;
import com.example.ahn.finalproject.MainLogin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

/**
 * Created by Ahn on 2017-05-28.
 */

public class CapsuleGroupMake extends AppCompatActivity{
    final static private String TAG = "@@@";
    static private int REQ_CODE_SELECT_IMAGE = 100;
    static private int REQ_CODE_CAPTURE_IMAGE = 101;
    private BackPressCloseHandler backPressCloseHandler;
    String userInvite ;
    String userGroupIdx;
    String user;
    String owner;

    String userList;
    TextView textShow, textShow1, textShow2, textShow3, textShow4, textShow5, textShow6;
    String result1;
    int acceptUserSize=0;
    int rejectUserSize=0;
    int time;
    AlertDialog dialog;
    ProgressDialog progressDialog;

    ArrayList<String> noActionUser = new ArrayList<>();//시간 내에 수락, 거절을 하지 못한 유저리스트
    /*ArrayList<String> userSize = new ArrayList<>();//초대한 모든 유저리스트
    ArrayList<String> acceptUser = new ArrayList<>();//초대에 응한 유저리스트
    ArrayList<String> rejectUser = new ArrayList<>();//초대를 거절한 유저리스트*/

    String[] userSize;
    String[] acceptUser;
    String[] rejectUser;
    String[] tempUser;

    String[] userValue; //전체유저
    /*************************dialog 변수*********************/
    AlertDialog.Builder builder;
    /*********************************************************/

    /************** 사진, 일정, 전송, 글 변수***************/
    EditText capsuleGroupLetter;
    Button groupMake;
    EditText capsuleGroupTitle; //방장일 경우에만 사용
    Button groupGallery;
    Button groupCamera;
    /*******************************************************/

    /***************권한 설정 변수***********************/
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 107;
    /*****************************************************/

    /***************사진 관련변수***************************/
    Bitmap image_bitmap;
    Bitmap bitmap;
    String imgPath;
    String imgName, executeName;
    StringBuilder encodedString;
    Uri fileUri, picUri;
    ImageView image; //사진 미리보기 역할해주는 변수
    int index=0;
    /***********************************************************/

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

        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        CheckInviteStatus checkInviteStatus = new CheckInviteStatus(); //groupIdx와 invite값을 가져오기 위함
        try {
            String result = checkInviteStatus.execute().get();

            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(jsonObject.getString("id").equals(Main.getUserId())) {
                    userInvite = jsonObject.getString("invite");
                    userGroupIdx = jsonObject.getString("groupIdx");
                    user = jsonObject.getString("user");
                    owner = jsonObject.getString("owner");
                }
            }
            Log.e("@@@@", "userInvite: " + userInvite + "   userGroupIdx: " + userGroupIdx + "   user:" + user + "   owner: " + owner);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(owner.equals(Main.getUserId())) {//방장일 경우
            setContentView(R.layout.capsule_group_make);
            capsuleGroupTitle = (EditText) findViewById(R.id.capsuleGroupTitle);
        }
        else //아닐 경우
            setContentView(R.layout.capsule_group_make1);

        if(userInvite.equals("2")){  //초기에 페이지 들어올 때 invite가 2일 경우 열로 들어옴.
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.dialog_waiting_request, null);
            builder = new AlertDialog.Builder(this);
            builder.setTitle("Member Information");
            builder.setIcon(android.R.drawable.ic_dialog_alert);

            /***팝업 변수 ***/
            textShow = (TextView) dialogView.findViewById(R.id.showText);
            textShow1 = (TextView) dialogView.findViewById(R.id.showText1);
            textShow2 = (TextView) dialogView.findViewById(R.id.showText2);
            textShow3 = (TextView) dialogView.findViewById(R.id.showText3);
            textShow4 = (TextView) dialogView.findViewById(R.id.showText4);
            textShow5 = (TextView) dialogView.findViewById(R.id.showText5);
            textShow6 = (TextView) dialogView.findViewById(R.id.showText6);
            /****************/

            //while(true) {
               /* CheckInviteStatus2 checkInviteStatus2 = new CheckInviteStatus2();
                try {
                    String result1 = checkInviteStatus2.execute().get();
                    textShow.setText(userList);Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }*/

                builder.setView(dialogView);

                dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

            BackThread thread = new BackThread();
            thread.setDaemon(true);
            thread.start();
                //((ViewGroup) dialogView.getParent()).removeAllViews();

            //}
            /********************이 부분 수정해야됨**************/
            /*while(true){

            }*/
        }

        /********본격적인 부분**************/
        capsuleGroupLetter = (EditText) findViewById(R.id.capsuleGroupLetter);
        groupMake = (Button) findViewById(R.id.groupMake);
        groupGallery = (Button) findViewById(R.id.groupGallery);
        groupCamera = (Button) findViewById(R.id.groupCamera);
        groupGallery.setOnClickListener(listener);
        groupCamera.setOnClickListener(listener);

        groupMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result;
                MakeGroupCapsule makeGroupCapsule = new MakeGroupCapsule();
                //try {

                ///if문 없애도 됨 나중에 작업 ㄱㄱ
                    if(owner.equals(Main.getUserId())) {
                        if (imgPath != null && !imgPath.isEmpty()) {
                            Log.e("Converting Image to ","Binary Data");
                            // Convert image to String using Base64
                            encodeImagetoString();
                            // When Image is not selected from Gallery
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "You must select image from gallery before you try to upload",
                                    Toast.LENGTH_LONG).show();
                        }
                        result="success";
                        //result = makeGroupCapsule.execute(capsuleGroupLetter.getText().toString(), capsuleGroupTitle.getText().toString()).get();
                    }else{
                            if (imgPath != null && !imgPath.isEmpty()) {
                                Log.e("Converting Image to ", "Binary Data");
                                // Convert image to String using Base64
                                encodeImagetoString();
                                // When Image is not selected from Gallery
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "You must select image from gallery before you try to upload",
                                        Toast.LENGTH_LONG).show();
                            }
                            result="success";
                        //result = makeGroupCapsule.execute(capsuleGroupLetter.getText().toString()).get();
                    }
                    if(result.equals("success")){
                        LayoutInflater inflater = getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.dialog_makegroup_success, null);
                        builder.setTitle("Make Successful");
                        builder.setIcon(android.R.drawable.ic_dialog_alert);
                        builder.setView(dialogView);

                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //종료될 때 사용자의 invite값을 0으로 변경
                                UpdateInviteStatus updateInviteStatus = new UpdateInviteStatus();
                                updateInviteStatus.execute("0");
                                dialog.dismiss();
                                finish();
                            }
                        });

                        dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                    }else{
                        LayoutInflater inflater = getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.dialog_makegroup_success, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                        builder.setTitle("Failed Make Capsule");
                        builder.setIcon(android.R.drawable.ic_dialog_alert);
                        builder.setView(dialogView);

                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialog.dismiss();
                                finish();
                            }
                        });

                        dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                    }
                /*} catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }*/

            }
        });
    }

    Button.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view){
            if(view.getId() == R.id.groupGallery){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }else if(view.getId() == R.id.groupCamera){
                startActivityForResult(getPickImageChooserIntent(), REQ_CODE_CAPTURE_IMAGE);
                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri(1);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, REQ_CODE_CAPTURE_IMAGE);*/
            }
    }};

    /*************************카메라 설정***************************************/
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
            //갤러리 사진 이름 만드는 곳 (선택해서 들어갔을 때) [갤러리버튼 말고]
            //mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg" );
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg" );
            Log.d(TAG, "@@@@@@@@@" + "make file");
        }else{
            Log.d(TAG, "@@@@@@@@@" + "We can't make file");
            return null;
        }
        return mediaFile;
    }
    /********************************************************************************/

    /****************************************************
     * 카메라 액티비티 갔다왔을 때
     ******************************************************/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        Toast.makeText(getApplicationContext(), "resultCode : "+resultCode,Toast.LENGTH_SHORT).show();

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
                    image_bitmap 	= MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    image = (ImageView) findViewById(R.id.privateImgView);

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
            /*if(data != null)
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
            image.setImageBitmap(bm);*/
            image = (ImageView) findViewById(R.id.privateImgView);

            if (getPickImageResultUri(data) != null) {
                picUri = getPickImageResultUri(data);

                try {
                    image_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
                    image_bitmap = rotateImageIfRequired(image_bitmap, picUri);
                    image_bitmap = getResizedBitmap(image_bitmap, 500);

                    imgPath = picUri.toString();
                    imgPath = imgPath.replace("file://", "");
                    //imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
                    imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
                    Log.e("ImagePath : ",imgPath);
                    Log.e("ImageName : ",imgName);
                    Toast.makeText(getApplicationContext(),""+imgName, Toast.LENGTH_LONG).show();
                    executeName = imgName.substring(imgName.lastIndexOf(".")+1);

                    /*CircleImageView croppedImageView = (CircleImageView) findViewById(R.id.img_profile);
                    croppedImageView.setImageBitmap(image_bitmap);*/  //원형 뷰
                    image.setImageBitmap(image_bitmap);  //그냥 뷰

                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else {
                bitmap = (Bitmap) data.getExtras().get("data");

                image_bitmap = bitmap;
                CircleImageView croppedImageView = (CircleImageView) findViewById(R.id.img_profile);
                if (croppedImageView != null) {
                    croppedImageView.setImageBitmap(image_bitmap);
                }
                image.setImageBitmap(image_bitmap);
            }
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

    /***************************************
     * user invite가 2일 때까지 체크해주는 곳
     **************************************/

    class CheckInviteStatus2 extends AsyncTask<String, Integer, String> {
        URL url;
        //HttpURLConnection conn;
        int menuSeq;


        @Override
        protected String doInBackground(String... urls) {

            BufferedReader bufferedReader = null;
            userList = "";
            //String userId = Main.getUserId();

            try {
                //while (true) {
                url = new URL("http://210.123.254.219:3001" + "/checkInviteStatus2");
                //conn = (HttpURLConnection) url.openConnection();

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
                userList = br.readLine();
                Log.d("@@@@@", "CheckInviteStatus22Line@@@@" + userList);
                //Thread.sleep(1000);
                //}
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Log.e("line", userList);
            return userList;
        }
    }

    /***************************************
     * updateFriendInvite 잠수탄 친구들의 invite값을 0으로 변경해준다. (요청 시간이 끝났을 때)
     **************************************/

    class UpdateFriendInvite extends AsyncTask<String, Integer, String> {
        URL url;
        //HttpURLConnection conn;
        int menuSeq;


        @Override
        protected String doInBackground(String... urls) {

            BufferedReader bufferedReader = null;
            userList = "";
            //String userId = Main.getUserId();

            try {
                //while (true) {
                url = new URL("http://210.123.254.219:3001" + "/updateFriendInvite");
                //conn = (HttpURLConnection) url.openConnection();

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(urls[0], "UTF-8")+ "&" +
                        URLEncoder.encode("inviteStatus", "UTF-8") + "=" + URLEncoder.encode("0","UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
                userList = br.readLine();
                Log.d("@@@@@", "UpdateFriendInviteLine@@@@" + userList);
                //Thread.sleep(1000);
                //}
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Log.e("line", userList);
            return userList;
        }
    }

    /********팝업 쓰레드 *******************/

    class BackThread extends Thread{
        public void run(){
            time=60;
            while(time>=-1){
                CheckInviteStatus2 checkInviteStatus2 = new CheckInviteStatus2();
                TimeChange timeChange = new TimeChange();
                GetTime getTime = new GetTime();
                try {
                    if(owner.equals(Main.getUserId())) //방장일 경우에만 시간 삽입
                        time = Integer.parseInt(timeChange.execute(Integer.toString(time), userGroupIdx).get());
                    else  //방장이 아닐 경우는 그냥 시간을 받아옴
                        time = Integer.parseInt(getTime.execute().get());
                    Log.e("@@@", "현재 받아온 time: " + time);
                    result1 = checkInviteStatus2.execute().get();

                    userValue = result1.split("#");
                    //Log.e("@@@", "userValue[0] : " + userValue[0] + "userValue[1] : " + userValue[1] + "userValue[2] : " + userValue[2] + "userValue[3] : " + userValue[3]);
                    acceptUser = userValue[0].split(",");
                    rejectUser = userValue[1].split(",");
                    userSize = userValue[2].split(",");
                    if(userValue.length>3)
                        tempUser = userValue[3].split(",");

                    handler.sendEmptyMessage(0);
                    //총 유저랑 거절유저, 수락유저 수 같을 때 (즉 모두가 요청에 응했을 때)
                    if(userSize.length == acceptUserSize+rejectUserSize) {
                        handler.sendEmptyMessage(0);
                        //int total = acceptUserSize+rejectUserSize;
                        //textShow3.setText("[ " + total + " / " + userSize.length + " ]    요청 종료! 5초 뒤 팝업이 사라집니다.");
                        //Thread.sleep(5000);
                        break;
                    }
                    Thread.sleep(1000);
                    time--;

                    Log.e("time", "time: " + time);
                    //textShow.setText(userList);Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } // end while
            //시간이 0초가 되면 while종료 이 순간에 초대를 수락한 사람이 없다면 방 해제
            if(time==0 && acceptUserSize==0) {
                //textShow3.setText("아무도 수락한 사람이 없습니다.    5초 뒤 메인페이지로이동합니다.");
                handler.sendEmptyMessage(0);
            }else{ //시간이 0초가 되고 수락한 사람이 있을 경우
                handler.sendEmptyMessage(0);
                //textShow3.setText("5초 뒤 단체캡슐 만드는 페이지로 이동합니다.");
                UpdateFriendInvite updateFriendInvite = new UpdateFriendInvite();
                if(userValue.length>3)
                    updateFriendInvite.execute(userValue[3]);

                //dialog.dismiss();
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dialog.cancel();
            if(acceptUserSize==0) {
                dialog.dismiss();
                finish();
            }
        } // end run
    } // end class BackThread

    /**********handler*********************/
    Handler handler = new Handler(){
      public void handleMessage(Message msg){
          //userValue = result1.split("#");

          acceptUser = userValue[0].split(",");
          rejectUser = userValue[1].split(",");
          userSize = userValue[2].split(",");
          if(userValue.length>3)
            tempUser = userValue[3].split(",");

          /*StringTokenizer st = new StringTokenizer(userValue[2], ",");
          while(st.hasMoreTokens()){
              noActionUser.clear();
              noActionUser.add(user);
          }

          //수락 거절한 유저 제거
          for(int i=0; i<rejectUser.length;i++){
              noActionUser.remove(rejectUser[i]);
          }
          //수락 응한 유저 제거
          for(int i=0; i<acceptUser.length;i++){
              noActionUser.remove(acceptUser[i]);
          }*/

          if(acceptUser[0].equals("")) {
              Log.e("@@@@", "NULLacceptUserSize: " + acceptUser[0] + "    acceptUserSize.length: " + acceptUserSize);
          }else{
              acceptUserSize = acceptUser.length;
              Log.e("@@@@", "NOTNULLacceptUserSize: " + acceptUser[0] + "    acceptUserSize.length: " + acceptUserSize);
          }

          if(rejectUser[0].equals("")) {
              Log.e("@@@@", "NULLrejectUserSize: " + rejectUser[0] + "      rejectUserSize.length: " + rejectUserSize);
          }else{
              rejectUserSize = rejectUser.length;
              Log.e("@@@@", "NOTNULLrejectUserSize: " + rejectUser[0] + "    rejctUserSize.length: " + rejectUserSize);
          }
          Log.e("@@@@", "userUserSize: " + userSize[0] + "userSize.length: " + userSize.length);
          //Log.e("@@@@", "tempUser: " + tempUser[0] + "tempUser.length: " + tempUser.length);
          textShow6.setText("방장: "+ owner);
          textShow.setText("초대한 사람들: "+userValue[2]);
          textShow1.setText("초대를 수락한 사람: "+userValue[0]);
          textShow2.setText("초대를 거절한 사람: "+userValue[1]);
          if(userValue.length>3)
            textShow5.setText("잠수중인 사람: "+ userValue[3]);
          else
              textShow5.setText("잠수중인 사람: ");

          int total = acceptUserSize + rejectUserSize;
          if(total == userSize.length) {
              textShow3.setText("[ " + total + " / " + userSize.length + " ]    5초 뒤 팝업이 사라집니다.");
          }else
              textShow3.setText("[ "+total+" / " + userSize.length + " ]");

          textShow4.setText("남은시간: "+time + "초");

          if(time==0 && acceptUserSize==0) {  //시간초가 다 된 상태에서 수락한 사람이 아무도 없을 때
              textShow3.setText("아무도 수락한 사람이 없습니다.    5초 뒤 메인페이지로이동합니다.");
          }else if(time==0 && acceptUserSize!=0){
              textShow3.setText("5초 뒤 타임캡슐 페이지로 이동합니다.");
          }
      }
    };

    /****************************단체캡슐 만들 때 *************************************/
    class MakeGroupCapsule extends AsyncTask<String, Integer, String> {
        URL url;
        //HttpURLConnection conn;
        int menuSeq;
        String data;

        @Override
        protected String doInBackground(String... urls) {

            BufferedReader bufferedReader = null;
            userList = "";
            String userId = Main.getUserId();

            try {
                //while (true) {
                url = new URL("http://210.123.254.219:3001" + "/makeGroupCapsule");
                //conn = (HttpURLConnection) url.openConnection();

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                if(urls.length==4) {
                    data = URLEncoder.encode("capsuleContent", "UTF-8") + "=" + URLEncoder.encode(urls[0], "UTF-8") + "&" +
                            URLEncoder.encode("myId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                            URLEncoder.encode("groupIdx", "UTF-8") + "=" + URLEncoder.encode(userGroupIdx, "UTF-8") + "&" +
                            URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(urls[1], "UTF-8") + "&" +
                            URLEncoder.encode("img", "UTF-8") + "=" + URLEncoder.encode(urls[2], "UTF-8") + "&" +
                            URLEncoder.encode("imgName", "UTF-8") + "=" + URLEncoder.encode(urls[3], "UTF-8");
                }if(urls.length==3) {
                    data = URLEncoder.encode("capsuleContent", "UTF-8") + "=" + URLEncoder.encode(urls[0], "UTF-8") + "&" +
                            URLEncoder.encode("myId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                            URLEncoder.encode("groupIdx", "UTF-8") + "=" + URLEncoder.encode(userGroupIdx, "UTF-8") + "&" +
                            URLEncoder.encode("img", "UTF-8") + "=" + URLEncoder.encode(urls[1], "UTF-8") + "&" +
                            URLEncoder.encode("imgName", "UTF-8") + "=" + URLEncoder.encode(urls[2], "UTF-8");
                }
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
                userList = br.readLine();
                Log.d("@@@@@", "UpdateFriendInviteLine@@@@" + userList);
                //Thread.sleep(1000);
                //}
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Log.e("line", userList);
            return userList;
        }
    }

    /****************************내 Invite상태 변경하기 *************************************/
    class UpdateInviteStatus extends AsyncTask<String, Integer, String> {
        URL url;
        //HttpURLConnection conn;
        int menuSeq;


        @Override
        protected String doInBackground(String... urls) {

            BufferedReader bufferedReader = null;
            userList = "";
            String userId = Main.getUserId();

            try {
                //while (true) {
                url = new URL("http://210.123.254.219:3001" + "/updateInvite");
                //conn = (HttpURLConnection) url.openConnection();

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data = URLEncoder.encode("myId", "UTF-8") + "=" + URLEncoder.encode(userId,"UTF-8")+ "&" +
                        URLEncoder.encode("inviteStatus", "UTF-8") + "=" + URLEncoder.encode(urls[0],"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
                userList = br.readLine();
                Log.d("@@@@@", "UpdateInivteLine@@@@" + userList);
                //Thread.sleep(1000);
                //}
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Log.e("line", userList);
            return userList;
        }
    }

    /****************************대기 시간 공유(방장일 때) *************************************/
    class TimeChange extends AsyncTask<String, Integer, String> {
        URL url;
        //HttpURLConnection conn;
        int menuSeq;


        @Override
        protected String doInBackground(String... urls) {

            BufferedReader bufferedReader = null;
            userList = "";
            String userId = Main.getUserId();

            try {
                //while (true) {
                url = new URL("http://210.123.254.219:3001" + "/timeChange");
                //conn = (HttpURLConnection) url.openConnection();

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data = URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(urls[0],"UTF-8")+ "&" +
                        URLEncoder.encode("groupIdx", "UTF-8") + "=" + URLEncoder.encode(urls[1],"UTF-8");

                Log.e("@@@", "time: " + urls[0] + "   groupIdx: " + urls[1]);

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
                userList = br.readLine();
                Log.d("@@@@@", "TimeChangeLine@@@@" + userList);
                //Thread.sleep(1000);
                //}
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Log.e("line", userList);
            return userList;
        }
    }

    /****************************대기 시간 공유(초대받은 유저일 때) *************************************/
    class GetTime extends AsyncTask<String, Integer, String> {
        URL url;
        //HttpURLConnection conn;
        int menuSeq;


        @Override
        protected String doInBackground(String... urls) {

            BufferedReader bufferedReader = null;
            userList = "";
            String userId = Main.getUserId();

            try {
                //while (true) {
                url = new URL("http://210.123.254.219:3001" + "/getTIme");
                //conn = (HttpURLConnection) url.openConnection();

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data = URLEncoder.encode("groupIdx", "UTF-8") + "=" + URLEncoder.encode(userGroupIdx,"UTF-8");

               // Log.e("@@@", "time: " + urls[0] + "   groupIdx: " + urls[1]);

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
                userList = br.readLine();
                Log.d("@@@@@", "getTimeLine@@@@" + userList);
                //Thread.sleep(1000);
                //}
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Log.e("line", userList);
            return userList;
        }
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            backPressCloseHandler.onBackPressed();
        }
        //Intent intent = new Intent(getApplicationContext(), ExitPopup.class);
        //startActivityForResult(intent, 1);
    }

    //파일명 추출
    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = this.getContentResolver().query(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        imgPath = cursor.getString(column_index);
        //imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
        imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
        Log.e("ImageName : ",imgName);
        executeName = imgName.substring(imgName.lastIndexOf(".")+1);
        Log.e("ExcuteName : ",executeName);
        return imgName;
    }

    //날짜 추출
    public String getDate(){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        String date = df.format(new Date());
        return date;
    }

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     * Gallery 이용했을 때 Uri가져오는 부분
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }


        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     * 갤러리, 카메라 등 다양한 보기 중 골라서 선택할 수 있게 만듬.
     */
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = this.getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        // 갤러리 인텐트로부터 얻을 때
        /*Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image*//*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }
*/
        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture by camera.
     * 카메라 이용했을 경우에 Uri가져오는 부분
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = this.getExternalCacheDir();
        if (getImage != null) {
            //outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), Main.getUserId() + userGroupIdx + ".png"));
            index++;
        }
        return outputFileUri;
    }
    /**********************************
     * 권한 설정부분
     */

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (this.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getApplicationContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (hasPermission(perms)) {
                    } else {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                //Log.d("API123", "permisionrejected " + permissionsRejected.size());
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;
        }
    }

    public void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                /*progressDialog = new ProgressDialog(getApplicationContext());
                progressDialog.setMessage("Loading Data...");
                progressDialog.show();*/
            };

            @Override
            public String doInBackground(Void... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                bitmap = BitmapFactory.decodeFile(imgPath, options);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy
                if(executeName.equals("png"))
                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                else if(executeName.equals("jpg") || executeName.equals("jpeg"))
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                encodedString = new StringBuilder(Base64.encodeToString(byte_arr, 0));
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
        /*String capsuleContent = capsulePrivateLetter.getText().toString();
        BackgroundTask backgroundTask = new BackgroundTask(getContext());*/
        MakeGroupCapsule makeGroupCapsule = new MakeGroupCapsule();
        /** 개인 캡슐 데이터 삽입하는 곳 *********/
        /*****************************************/
        String userId = Main.getUserId(); //현재 사용자 아이디
        String date = getDate(); //캡슐 만든 날짜

        try {
            String result;
            if(owner.equals(Main.getUserId())) {
                result = makeGroupCapsule.execute(capsuleGroupLetter.getText().toString(), capsuleGroupTitle.getText().toString(), encodedString.toString(), imgName).get();
            }else{
                result = makeGroupCapsule.execute(capsuleGroupLetter.getText().toString(), encodedString.toString(), imgName).get();
            }
            //String result = backgroundTask.execute("makePrivateCapsule",encodedString.toString(),imgName,capsuleContent, userId, date).get(); //개인 캡슐 서버로 넘기는곳
            if(result.equals("success")){  //캡슐이 성공적으로 보내졌다면
                //progressDialog.dismiss();
                image.setImageBitmap(null);  //이미지 공백으로
                capsuleGroupLetter.setText("");  //텍스트뷰 공백으로
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
