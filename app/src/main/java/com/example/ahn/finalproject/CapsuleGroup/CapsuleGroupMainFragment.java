package com.example.ahn.finalproject.CapsuleGroup;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
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

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;
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
    ArrayList selectedPerson;

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
    UsrInfo userInfo;
    Button sendmsg;


/******************권한 설정 변수****************************************/
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 107;
    private static final int REQUEST_CODE_GPS = 2001;//임의의 정수로 정의
    /*********************************************************************/

    /*******************GPS 변수******************************************/
    LocationManager lm;
    Button refresh;
    TextView getLoca;
    /**********************************************************************/

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group_capsule, container, false);


        /***************권한 설정 부분***********************/
        permissions.add(ACCESS_FINE_LOCATION);  //GPS 권한
        permissionsToRequest = findUnAskedPermissions(permissions);

        //마시멜로 이상 버전을 위한 부분
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        /////////////////////

        /****************GPS 센서가 켜있는지 꺼있는지 확인 꺼져있다면 설정 다이어로그 뿅****************/
        lm = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        getLoca = (TextView) view.findViewById(R.id.getLoca);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            getLoca.setText("위치서비스가 꺼져있습니다.");
            showGPSDisabledAlertToUser();
        }

        refresh = (Button) view.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() { //이전 페이지 클릭시
            @Override
            public void onClick(View v) {
                try {
                    if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        getLoca.setText("위치서비스가 꺼져있습니다.");
                        showGPSDisabledAlertToUser();
                    }else {
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                                100, // 통지사이의 최소 시간간격 (miliSecond)
                                1, // 통지사이의 최소 변경거리 (m)
                                mLocationListener);
                        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                                100, // 통지사이의 최소 시간간격 (miliSecond)
                                1, // 통지사이의 최소 변경거리 (m)
                                mLocationListener);
                    }
                }catch(SecurityException ex){
                }
            }
        });


        /*************************************************************************************************/

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        phoneWidth = dm.widthPixels;
        phoneHeight = dm.heightPixels;
        currentCnt = 0;
        selectedCnt = 0;
        Log.e("width", "" + phoneWidth + "height" + phoneHeight);
        Button preBtn = (Button) view.findViewById(R.id.pre);
        Button postBtn = (Button) view.findViewById(R.id.post);
        sendmsg = (Button) view.findViewById(R.id.sendmsg);

        nameArray = new ArrayList();
        distanceArray = new ArrayList();
        imgArray = new ArrayList();
        imgPath = new ArrayList();
        selectedPerson = new ArrayList();

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
            /**
             *  현재 내 주변인들의 정보를 각 ArrayList에 담는 곳
             */
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
                        }else{
                            Toast.makeText(getContext(),"마지막 페이지입니다.",Toast.LENGTH_LONG).show();
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

            /**********************************************************
             * 초대하기 버튼 눌렀을 때
             *********************************************************/

            sendmsg.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    InviteUser inviteUser = new InviteUser();
                    if(selectedPerson.size() != 0) {
                        try {
                            Toast.makeText(getContext(), "초대중 " + selectedPerson.toString(), Toast.LENGTH_LONG).show();

                            inviteUser.execute(selectedPerson.toString()).get();

                            UpdateInviteStatus updateInviteStatus = new UpdateInviteStatus();
                            updateInviteStatus.execute("2");

                            Intent intent = new Intent(getContext(), CapsuleGroupMake.class);
                            intent.putExtra("owner", Main.getUserId());
                            startActivityForResult(intent, 200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(getContext(), "초대된 사람이 없습니다!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        return view;
    }

    /*******************권한 관련 (마시멜로 이상을 위함)***********************/
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
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
        /*switch (requestCode){
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:{
                //권한 획득이 거부되면 결과 배열은 비어있게 됨
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }else{
                    Toast.makeText(getApplicationContext(), "현재 권한이 없습니다.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }*/
    }

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
                return (getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    /***************************************************************************/

    /**************************GPS관련*****************************************/
    //GPS 활성화를 위한 다이얼로그 보여주기
    private void showGPSDisabledAlertToUser()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("GPS가 비활성화 되어있습니다. 활성화 할까요?\n 활성화하지 않으면 단체캡슐 서비스를 이용할 수 없습니다.")
                .setCancelable(false)
                .setPositiveButton("설정", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(callGPSSettingIntent, REQUEST_CODE_GPS);
                    }
                });

        alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    //GPS 활성화를 위한 다이얼로그의 결과 처리
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);

        switch (requestCode) {
            case  REQUEST_CODE_GPS:
                //Log.d(TAG,""+resultCode);
                //if (resultCode == RESULT_OK)
                //사용자가 GPS 활성 시켰는지 검사
                if ( lm == null)
                    lm = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

                if ( lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
                {
                    // GPS 가 ON으로 변경되었을 때의 처리.
                }
                break;
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            Log.d("test", "onLocationChanged, location:" + location);
            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            double altitude = location.getAltitude();   //고도
            float accuracy = location.getAccuracy();    //정확도
            String provider = location.getProvider();   //위치제공자
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
            getLoca.setText("위치정보 : " + provider + "  위도 : " + latitude + "  경도 : " + longitude
                    + "  고도 : " + altitude + "  정확도 : "  + accuracy);

            UpdateMyLocation updateMyLocation = new UpdateMyLocation();
            try {
                String result = updateMyLocation.execute(Double.toString(latitude), Double.toString(longitude)).get();
                if(result.equals("success")){
                    Toast.makeText(getContext(),"내 위치 갱신완료", Toast.LENGTH_LONG).show();

                    getLocation(); //다시 사용자 불러오기

                    if(line != "") {
                        /**
                         *  현재 내 주변인들의 정보를 각 ArrayList에 담는 곳
                         */
                        eachUser = line.split("\\*");
                        nameArray.clear();
                        distanceArray.clear();
                        imgArray.clear();
                        imgPath.clear();
                        for (int i = 0; i < eachUser.length; i++) {
                            nameArray.add(eachUser[i].split(",")[0]);
                            distanceArray.add(eachUser[i].split(",")[1]);
                            imgArray.add(eachUser[i].split(",")[2]);
                        }
                        linearMain.post(new Runnable() {
                            @Override
                            public void run() {
                                linearMain.removeAllViews();
                                setNearbyUserImg(0, linearMain.getHeight());
                            }
                        });
                    }
                }else if(result.equals("no")){
                    Toast.makeText(getContext(),"내 위치 갱신실패", Toast.LENGTH_LONG).show();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };
    /**************************************************************************/

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
                imgPath.add(imgArray.get(i).toString().replace("/usr/local/nodeServer/public", "http://210.123.254.219:3001"));


                Glide.with(getContext()).load(imgPath.get(i)).centerCrop().bitmapTransform(new CropCircleTransformation(getContext())).into(iv);
                iv.setImageResource(R.drawable.round);
                iv.setLayoutParams(new FrameLayout.LayoutParams(200, 200));

                /**
                 * setTag 설정
                 */
                userInfo = new UsrInfo(i, (String) nameArray.get(i));

                iv.setTag(userInfo);

                final UsrInfo getInfo = (UsrInfo) iv.getTag();

                //image btnSetting
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UsrInfo getInfo = (UsrInfo) v.getTag();
                        iv = new ImageView(getContext());
                        //int index = (int) v.getTag();
                        int index = getInfo.getIndex();
                        String usrId = getInfo.getId();

                        hashMap.put(index, 1);
                        Glide.with(getContext()).load(imgPath.get(index)).centerCrop().bitmapTransform(new CropCircleTransformation(getContext())).into(iv);
                        iv.setImageResource(R.drawable.round);
                        iv.setLayoutParams(new FrameLayout.LayoutParams(200, 200));

                        iv.setTag(v.getTag());

                        Toast.makeText(getContext(), "클릭" + index + " 클릭한 사진의 아이디: " + usrId, Toast.LENGTH_LONG).show();

                        selectedPerson.add(usrId);

                        Toast.makeText(getContext(), "클릭된 사진들" + selectedPerson, Toast.LENGTH_LONG).show();

                        iv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) { //함께하는 친구에서 사람 뻈을 때
                                UsrInfo getInfo = (UsrInfo) v.getTag();

                                Toast.makeText(getContext(),"여기",Toast.LENGTH_LONG).show();
                                v.setVisibility(View.INVISIBLE);
                                hashMap.remove(getInfo.getIndex());
                                selectedPerson.remove(getInfo.getId());
                                selectedCnt--;
                                clickCheck = false;
                            }
                        });
                        /*if(!clickCheck) {
                            Toast.makeText(getContext(),"여기222",Toast.LENGTH_LONG).show();
                            v.setVisibility(View.VISIBLE);
                            clickCheck=true;
                        }
                        else {
                            Toast.makeText(getContext(),"여기3333",Toast.LENGTH_LONG).show();*/
                            v.setVisibility(View.INVISIBLE);
                            /*clickCheck=true;
                        }*/
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

class InviteUser extends AsyncTask<String, Integer, String> {
    /*URL url;
    HttpURLConnection conn;
    String line;
    int menuSeq;*/
    String line;
    String id = Main.getUserId();

    @Override
    protected String doInBackground(String... params) {
        //BufferedReader bufferedReader = null;
        line = "";
        String response = "";
        try {
            URL url = new URL("http://210.123.254.219:3001"+"/inviteUser");
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            OutputStream OS = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

            String data = URLEncoder.encode("myId", "UTF-8") + "=" + URLEncoder.encode(id,"UTF-8")+ "&" +
                    URLEncoder.encode("friends", "UTF-8") + "=" + URLEncoder.encode(params[0],"UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            OS.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

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

/****************************************************************
 * 초대상태 업데이트 (거절 누른 상태)
 ****************************************************************/

class UpdateInviteStatus extends AsyncTask<String, Integer, String> {
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
            url = new URL("http://210.123.254.219:3001" + "/updateInvite");
            //conn = (HttpURLConnection) url.openConnection();

            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
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
            line = br.readLine();
            Log.d("@@@@@", "CheckInviteStatusLine@@@@"+line);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Log.e("line",line);
        return line;
    }
}

/****************************************************************
 * 내 위도, 경도 업데이트
 ****************************************************************/

class UpdateMyLocation extends AsyncTask<String, Integer, String> {
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
            url = new URL("http://210.123.254.219:3001" + "/updateMyLocation");
            //conn = (HttpURLConnection) url.openConnection();

            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            OutputStream OS = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

            String data = URLEncoder.encode("myId", "UTF-8") + "=" + URLEncoder.encode(userId,"UTF-8")+ "&" +
                    URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(urls[0],"UTF-8")+ "&" +
                    URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(urls[1],"UTF-8");
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            OS.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
            line = br.readLine();
            Log.e("@@@@@", "UpdateMyLocationLine@@@@"+line);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Log.e("line",line);
        return line;
    }
}

class UsrInfo{
    String id;
    int index;
    public UsrInfo(int index, String id){
        this.index = index;
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public int getIndex(){
        return this.index;
    }
}
