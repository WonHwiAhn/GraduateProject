package com.example.ahn.finalproject.CapsuleGroup;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ahn.finalproject.GlobalValues.Main;
import com.example.ahn.finalproject.MainLogin.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.example.ahn.finalproject.MainLogin.R.id.layMain;

/**
 * Created by Ahn on 2017-04-16.
 */

public class CapsuleGroupMainFragment extends Fragment {
    View view;
    ArrayList<ImageView> arrayList = new ArrayList();
    HorizontalScrollView scrollBtn;
    FrameLayout linearBottom;
    FrameLayout linearMain;
    ImageView iv;
    int i;
    int currentCnt;
    FrameLayout.LayoutParams param;
    ArrayList<FrameLayout> frameLayoutList;
    ArrayList nameArray;
    ArrayList distanceArray;
    ArrayList imgArray;

    ArrayList<Integer> alreadyX;
    ArrayList<Integer> alreadyY;

    ArrayList imgPath;

    String[] eachUser;
    int phoneWidth;
    int phoneHeight;
    int selectedCnt;
    int frameWidth, frameHeight=0;
    String line = "";
    HashMap<Integer, Integer> hashMap = new HashMap<>();
    boolean clickCheck=true;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group_capsule, container, false);

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        phoneWidth = dm.widthPixels;
        phoneHeight = dm.heightPixels;
        currentCnt = 0;
        selectedCnt = 0;
        Log.e("width", "" + phoneWidth + "height" + phoneHeight);
        Button preBtn = (Button) view.findViewById(R.id.pre);
        Button postBtn = (Button) view.findViewById(R.id.post);

        nameArray = new ArrayList();
        distanceArray = new ArrayList();
        imgArray = new ArrayList();
        imgPath = new ArrayList();

        alreadyX = new ArrayList();
        alreadyY = new ArrayList();

        linearBottom = (FrameLayout) view.findViewById(R.id.layBtm);
        linearMain = (FrameLayout) view.findViewById(layMain);
        scrollBtn = (HorizontalScrollView) view.findViewById(R.id.hzScroll);
        //bottom 스크롤뷰 추가
        frameLayoutList = new ArrayList();
        param = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        getLocation();

        if(line != "") {
            eachUser = line.split("\\*");
            for (int i = 0; i < eachUser.length; i++) {
                nameArray.add(eachUser[i].split(",")[0]);
                distanceArray.add(eachUser[i].split(",")[1]);
                imgArray.add(eachUser[i].split(",")[2]);
            }
            linearMain.post(new Runnable() {
                @Override
                public void run() {
                    setNearbyUserImg(0, linearMain.getHeight());
                }
            });

            preBtn.setOnClickListener(new View.OnClickListener() { //이전 페이지 클릭시
                @Override
                public void onClick(View v) {
                    if (currentCnt > 0) {
                        linearMain.removeAllViews();
                        currentCnt -= 1;
                        linearMain.post(new Runnable() {
                            @Override
                            public void run() {
                                setNearbyUserImg(currentCnt, linearMain.getHeight());
                            }
                        });
                    }else{
                        Toast.makeText(getContext(),"첫 번째 페이지입니다.",Toast.LENGTH_LONG).show();
                    }
                }
            });

            postBtn.setOnClickListener(new View.OnClickListener() {//post버튼 클릭시
                @Override
                public void onClick(View v) {
                    if (eachUser.length % 5 == 0) { //유저가 5명일 때
                        if (currentCnt < (eachUser.length - 1) / 5) {
                            linearMain.removeAllViews();  //이전 페이지 삭제
                            currentCnt += 1;
                            linearMain.post(new Runnable() {
                                @Override
                                public void run() {
                                    setNearbyUserImg(currentCnt, linearMain.getHeight());
                                }
                            });
                        }
                    } else { //유저 5명 아닐 때
                        if (currentCnt < eachUser.length / 5) {
                            linearMain.removeAllViews(); //이전 페이지 삭제
                            currentCnt += 1;
                            linearMain.post(new Runnable() {
                                @Override
                                public void run() {
                                    setNearbyUserImg(currentCnt, linearMain.getHeight());
                                }
                            });
                        }else{
                            Toast.makeText(getContext(),"마지막 페이지입니다.",Toast.LENGTH_LONG).show();
                        }
                    }

                }
            });
        }
        return view;
    }

    public void getLocation(){
        //조건에 맞는 내 주변에 있는 사용자들 리스트를 불러옴.
        GetNearbyUser getNearbyUser = new GetNearbyUser();

        try {
            line = getNearbyUser.execute(Main.getUserId()).get();
            Log.e("line####", line);
            getNearbyUser.isCancelled();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void setNearbyUserImg(int curIndex, int height) {
        for (int i = 5 * curIndex; i < 5 * curIndex + 5; i++) {
            if (i == eachUser.length) {
                break;
            }
            int randomX;
            int randomY;
            while (true) {
                randomX = (int) (Math.random() * (phoneWidth - 300));
                randomY = (int) (Math.random() * (height - 500));
                boolean checkDup = true;
                for (int j = 0; j < alreadyX.size(); j++) {
                    if (Math.abs(randomX - alreadyX.get(j)) < 200 && Math.abs(randomY - alreadyY.get(j)) < 200) {
                        checkDup = false;
                        break;
                    }
                }
                if (checkDup) break;
            }
            alreadyX.add(randomX);
            alreadyY.add(randomY);


            iv = new ImageView(getContext());
            if (!hashMap.containsKey(i)) {
                Log.e("hashhash@@@", "" + hashMap);
                Log.e("iiiiii@@@", "" + i);
                imgPath.add(imgArray.get(i).toString().replace("/usr/local/nodeServer/public", "http://210.123.254.219:3001"));


                Glide.with(getContext()).load(imgPath.get(i)).centerCrop().bitmapTransform(new CropCircleTransformation(getContext())).into(iv);
                iv.setImageResource(R.drawable.round);
                iv.setLayoutParams(new FrameLayout.LayoutParams(200, 200));
                iv.setTag(i);
                //image btnSetting
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iv = new ImageView(getContext());
                        int index = (int) v.getTag();
                        hashMap.put(index, 1);
                        Glide.with(getContext()).load(imgPath.get(index)).centerCrop().bitmapTransform(new CropCircleTransformation(getContext())).into(iv);
                        iv.setImageResource(R.drawable.round);
                        iv.setLayoutParams(new FrameLayout.LayoutParams(200, 200));
                        iv.setTag(v.getTag());
                        iv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) { //함께하는 친구에서 사람 뻈을 때
                                Toast.makeText(getContext(),"여기",Toast.LENGTH_LONG).show();
                                v.setVisibility(View.INVISIBLE);
                                hashMap.remove((int) v.getTag());
                                selectedCnt--;
                                clickCheck = false;
                            }
                        });
                        if(!clickCheck) {
                            Toast.makeText(getContext(),"여기222",Toast.LENGTH_LONG).show();
                            v.setVisibility(View.VISIBLE);
                            clickCheck=true;
                        }
                        else {
                            Toast.makeText(getContext(),"여기3333",Toast.LENGTH_LONG).show();
                            v.setVisibility(View.INVISIBLE);
                            clickCheck=true;
                        }
                        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(
                                iv.getLayoutParams());
                        margin.setMargins(200 * selectedCnt, 0, 0, 0);
                        iv.setLayoutParams(new FrameLayout.LayoutParams(margin));
                        arrayList.add(iv);
                        FrameLayout frameLayout = new FrameLayout(getContext());
                        frameLayout.addView(iv);
                        frameLayoutList.add(frameLayout);
                        linearBottom.addView(frameLayout);
                        Log.e("@@@", "booleanCHeck@@@@" + clickCheck);


                        selectedCnt++;
                        scrollBtn.smoothScrollBy(200, 0);
                        Log.e("iv xy", (int) linearBottom.getRight() + 200 + "" + (int) iv.getY());
                    }
                });

                ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(
                        iv.getLayoutParams());
                Log.e("x", "" + randomX + "y: " + randomY);
                margin.setMargins(randomX, randomY, 0, 0);
                iv.setLayoutParams(new FrameLayout.LayoutParams(margin));
                arrayList.add(iv);
                FrameLayout frameLayout = new FrameLayout(getContext());
                frameLayout.addView(iv);
                frameLayoutList.add(frameLayout);
                linearMain.addView(frameLayout);
            }
        }

        alreadyX.clear();
        alreadyY.clear();
    }
}
class GetNearbyUser extends AsyncTask<String, Integer, String> {
    /*URL url;
    HttpURLConnection conn;
    String line;
    int menuSeq;*/
    String line;

    @Override
    protected String doInBackground(String... params) {
        //BufferedReader bufferedReader = null;
        line = "";
        String response = "";
        try {
            URL url = new URL("http://210.123.254.219:3001"+"/getLocation");
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            OutputStream OS = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

            String data = URLEncoder.encode("myId", "UTF-8") + "=" + URLEncoder.encode(params[0],"UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            OS.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

            while((line = bufferedReader.readLine()) != null){
                response += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            Log.e("@@@", "result###@@@"+response);
            /*url = new URL("http://yys6910.dothome.co.kr/studyA/distanceCheck.php");
            conn = (HttpURLConnection) url.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            line = br.readLine();*/


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;
    }
}
