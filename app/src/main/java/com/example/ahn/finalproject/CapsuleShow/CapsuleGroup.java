package com.example.ahn.finalproject.CapsuleShow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ahn.finalproject.MainLogin.R;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static android.content.ContentValues.TAG;

/**
 * Created by Ahn on 2017-06-05.
 */

public class CapsuleGroup extends AppCompatActivity{
    ImageView capsulePrivateImg;
    TextView capsuleContent, capsuleTitle;
    LinearLayout linearGroup;
    String content, picturepath;
    Bitmap bmImg;
    Bitmap decodedByte;
    ImageView imageContent;
    String picture;

    Handler handler = new Handler();
    LinearLayout rl1;
    TextView pictureUser;

    //FrameLayout frameLayout1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capsule_group);

        Intent intent = getIntent();

        String idx = intent.getStringExtra("idx");
        String owner = intent.getStringExtra("owner");
        picturepath = intent.getStringExtra("picturepath");
        String title = intent.getStringExtra("title");

        content = intent.getStringExtra("content");

       /* capsulePrivateImg = (ImageView) findViewById(R.id.capsulePrivateImg);
        capsuleContent = (TextView) findViewById(R.id.capsuleContent);*/
        linearGroup = (LinearLayout) findViewById(R.id.linearGroup);
        capsuleTitle = (TextView) findViewById(R.id.capsuleTitle);

        //frameLayout1 = new FrameLayout(getApplicationContext());

        capsuleTitle.setText(title);

        linearGroup.post(new Runnable() {
            @Override
            public void run() {
                linearGroup.removeAllViews();
                writeContent();
            }
        });
        /*locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

        //capsuleContent.setText(content);

        /*BackgroundTask backgroundTask = new BackgroundTask(getApplication());
        try {
            Log.d("@@@", "##@@"+picturepath);
            String string = backgroundTask.execute("getImg", picturepath).get();
            Log.d("@@@", string);
            string
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/


        /*CapsulePrivate.GetImg gi = new CapsulePrivate.GetImg();

        // imgName = picturepath.substring(picturepath.lastIndexOf("/"));

        gi.execute("http://210.123.254.219:3001" + "/getImg",picturepath);*/
    }

    public void writeContent(){

        String [] contentArray = content.split("#");
        final String [] picturepathArray = picturepath.split("#");

        /************************************************************
         * 단체 캡슐에 입력된 글 뽑아내는 곳
         ***********************************************************/

        for(int i=0; i<contentArray.length; i+=2) {
            LinearLayout rl = new LinearLayout(getApplicationContext());
            rl.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(linearGroup.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);

            // RelativeLayout에 width, height 설정 적용
            rl.setLayoutParams(params);
            //차이드 생성 (유저 아이디)
            TextView textTitle = new TextView(getApplicationContext());
            RelativeLayout.LayoutParams textTitleParams = new RelativeLayout.LayoutParams
                    (rl.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);
            /*if(i!=0)
                textTitleParams.addRule(RelativeLayout.BELOW, i-1);*/
            textTitle.setLayoutParams(textTitleParams);
            textTitle.setId(i);
            //text.setWidth(rl.getLayoutParams().width);
            textTitle.setTextColor(Color.BLACK);
            //Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL
            textTitle.setGravity(Gravity.CENTER_HORIZONTAL);
            textTitle.setText(contentArray[i]);

            //차이드 생성 (내용)
            TextView textContent = new TextView(getApplicationContext());
            RelativeLayout.LayoutParams textContentParams = new RelativeLayout.LayoutParams
                    (rl.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);
            //textContentParams.addRule(RelativeLayout.BELOW, i);
            textContent.setLayoutParams(textContentParams);
            textContent.setId(i+1);
            //text.setWidth(rl.getLayoutParams().width);
            textContent.setTextColor(Color.BLACK);
            //Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL
            textContent.setGravity(Gravity.CENTER_HORIZONTAL);
            textContent.setText("글쓴이: " + contentArray[i+1]);
            textContent.setTextSize(20);

            Log.e("@@@", "textContent@@@" + contentArray[i+1] + "    imagepicture @@@@" + picturepathArray[i]);

            rl.addView(textContent);
            rl.addView(textTitle);

            FrameLayout frameLayout = new FrameLayout(getApplicationContext());
            //frameLayout.addView(iv);

            frameLayout.addView(rl);
            linearGroup.addView(frameLayout);
        }

        /************************************************************
         * 단체 캡슐에 입력된 사진 뽑아내는 곳
         ***********************************************************/

        for(int i=0; i<picturepathArray.length; i+=2) {
            rl1 = new LinearLayout(getApplicationContext());
            rl1.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(linearGroup.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);

            // RelativeLayout에 width, height 설정 적용
            rl1.setLayoutParams(params);
            //차이드 생성 (유저 아이디)
            pictureUser = new TextView(getApplicationContext());
            RelativeLayout.LayoutParams textTitleParams = new RelativeLayout.LayoutParams
                    (rl1.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);
            /*if(i!=0)
                textTitleParams.addRule(RelativeLayout.BELOW, i-1);*/
            pictureUser.setLayoutParams(textTitleParams);
            pictureUser.setId(i);
            //text.setWidth(rl.getLayoutParams().width);
            pictureUser.setTextColor(Color.BLACK);
            //Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL
            pictureUser.setGravity(Gravity.CENTER_HORIZONTAL);
            pictureUser.setText("사진 올린 사람 : " + picturepathArray[i+1]);
            pictureUser.setTextSize(20);

            //차이드 생성 (내용)
            imageContent = new ImageView(getApplicationContext());
            /*RelativeLayout.LayoutParams textContentParams = new RelativeLayout.LayoutParams
                    (rl1.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);*/

            FrameLayout.LayoutParams frameLayout = new FrameLayout.LayoutParams(1000, 600);
            frameLayout.setMargins(linearGroup.getWidth()-linearGroup.getWidth()/2-500, 80, 0,0);
            imageContent.setLayoutParams(frameLayout);
            //textContentParams.addRule(RelativeLayout.BELOW, i);
            //imageContent.setLayoutParams(textContentParams);
            imageContent.setId(i+1);
            imageContent.setScaleType(ImageView.ScaleType.CENTER);

            //text.setWidth(rl.getLayoutParams().width);
            //Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL
            picturepathArray[i] = picturepathArray[i].replace("/usr/local/nodeServer/public", "http://210.123.254.219:3001");
            /*Bitmap bmImg = BitmapFactory.decodeFile(picturepathArray[i]);
            imageContent.setImageBitmap(bmImg);*/


            picture = picturepathArray[i];
            Glide.with(getApplicationContext()).load(picture).into(imageContent);
            /*byte [] encodeByte= Base64.decode(picture,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            imageContent.setImageBitmap(bitmap);*/
           /* new Thread(){
                public void run(){
                   Bitmap bitmap = getBitmapFromURL(picture);
                    Bundle bundle = new Bundle();
                    bundle.putString("test", bitmap);
                }
            }.start();*/
            /*rl1.post(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = getBitmapFromURL(picture);
                    imageContent.setImageBitmap(bitmap);
                }
            });*/


            //t.start();

            /*rl1.post(new Runnable() {
                @Override
                public void run() {
                    rl1.removeAllViews();
                    uploadImg();
                }
            });*/

            /*GetImg getImg = new GetImg();
            Bitmap test;

                getImg.execute(picture);*/
            /*rl1.post(new Runnable() {
                @Override
                public void run() {
                    rl1.removeAllViews();
                    uploadImg();
                }
            });*/
                //imageContent.setImageBitmap(test);



            /*GetPicture getPicture = new GetPicture();
            getPicture.setDaemon(true);
            getPicture.start();*/


            Log.e("@@@", "textContent@@@" + contentArray[i+1] + "    imagepicture @@@@" + picturepathArray[i]);


            //imageContent.setImageBitmap(Glide.with(getApplicationContext()).load(picture));

            FrameLayout frameLayout1 = new FrameLayout(getApplicationContext());
            //frameLayout.addView(iv);

            /*frameLayout1.addView(rl1);
            linearGroup.addView(frameLayout1);*/

            rl1.addView(pictureUser);
            //rl1.addView(imageContent);

            frameLayout1.addView(rl1);
            frameLayout1.addView(imageContent);
            linearGroup.addView(frameLayout1);
        }
    }

    /*************************************************
     * 이미지 가져올 때 URL로 불러오기 때문에 Stream을 거쳐야됨
     **********************************************/

    //이 방법은 쓰레드 안되서 안됨 무조건 Thread, handler나 Async써야됨
    public Bitmap getBitmapFromURL(String src) {
        HttpURLConnection connection = null;
        try { URL url = new URL(src);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            return myBitmap;
            //imageContent.setImageBitmap(bmImg);
        } catch (IOException e) { e.printStackTrace(); return null;
            }
        finally{ if(connection!=null)connection.disconnect(); }
    }

    private class GetImg extends AsyncTask<String, Integer, Bitmap> {
        String response="";
        @Override
        protected Bitmap doInBackground(String... url) {
            try{

                URL ImgUrl = new URL("http://210.123.254.219:3001" + "/getImgs");
                //String imgName = url[1];
                String picturePath = url[0];

                HttpURLConnection httpURLConnection = (HttpURLConnection) ImgUrl.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("imgPath", "UTF-8") + "=" + URLEncoder.encode(picturePath,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                /*final BitmapFactory.Options options = new BitmapFactory.Options();
                InputStream inputStream = httpURLConnection.getInputStream();

                BufferedInputStream bufferedReader = new BufferedInputStream(inputStream);

                bmImg = BitmapFactory.decodeStream(bufferedReader, null, options);
                bufferedReader.reset();

                options.inJustDecodeBounds = false;
                bmImg = BitmapFactory.decodeStream(bufferedReader, null, options);*/
                /*InputStream is = httpURLConnection.getInputStream();
                boolean test = is.markSupported();
                if(test){
                    is.reset();
                }*/

                /*Log.d("@@@", "isisisis   "+is);

                bmImg = BitmapFactory.decodeStream(is);
*/
                /*InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line;

                InputStream is = httpURLConnection.getInputStream();

                bmImg = BitmapFactory.decodeStream(is);*/
               /* while((line = bufferedReader.readLine()) != null){
                        response += line;
                }*/



                Log.d(TAG,"여기" + response);
                /*byte[] decodedString = Base64.decode(response, Base64.DEFAULT);
                decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);*/
                //bmImg = BitmapFactory.decodeStream(response);
                //bufferedReader.close();
                //inputStream.close();
                //is.close();
                httpURLConnection.disconnect();


                //is.close();
            }catch(IOException e){
                e.printStackTrace();
            }
            return bmImg;
        }
        protected void onPostExecute(Bitmap img){
            imageContent.setImageBitmap(bmImg);
        }
    }
    class GetPicture extends Thread{
            public void run() {
                try{
                    Log.e("@@@", "몇 번? @@" + picture);
                    URL url = new URL(picture);
                    InputStream is = url.openStream();
                    bmImg = BitmapFactory.decodeStream(is);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            rl1.removeAllViews();
                            uploadImg();
                            /*imageContent.setImageBitmap(bmImg);
                            rl1.addView(pictureUser);
                            rl1.addView(imageContent);
                            frameLayout1.addView(rl1);
                            linearGroup.addView(frameLayout1);*/
                            /*rl1.post(new Runnable() {
                                @Override
                                public void run() {
                                    rl1.removeAllViews();
                                    uploadImg();
                                }
                            });*/
                        }
                    });
                } catch(Exception e){}
            }
        }

    public void uploadImg(){
        /*GetPicture getPicture = new GetPicture();
        getPicture.setDaemon(true);
        getPicture.start();*/
        imageContent.setImageBitmap(bmImg);
        rl1.addView(pictureUser);
        rl1.addView(imageContent);

    }
}
