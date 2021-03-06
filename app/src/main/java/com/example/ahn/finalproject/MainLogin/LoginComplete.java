package com.example.ahn.finalproject.MainLogin;

/**
 * Created by Ahn on 2017-03-24.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ahn.finalproject.Adapter.TabPagerAdapter;
import com.example.ahn.finalproject.CapsuleGroup.CapsuleGroupMake;
import com.example.ahn.finalproject.Friend.AddFriend;
import com.example.ahn.finalproject.Friend.RequestFriend;
import com.example.ahn.finalproject.GlobalValues.Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class LoginComplete extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String[] listItem={};
    private String result;
    private static final int ADD_FRIEND_CODE = 5;
    private static final int REQUEST_FRIEND_CODE = 6;
    private BackPressCloseHandler backPressCloseHandler;
    ImageView navImgView;
    TextView navText01, navText02, friendCount;
    NavigationView navigationView;
    View navHeader;
    ArrayList<String> fId;
    ArrayList<String> fStatus;
    boolean friendStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_complete);

        Intent intent = getIntent();
        String userId = intent.getStringExtra("id");
        String userIdx = intent.getStringExtra("idx");
        String profile = intent.getStringExtra("profile");

        ((Main) getApplication()).setUserId(userId); //userId값을 글로벌 변수에 넣어줌. getApplicationContext이용
        ((Main) getApplication()).setUserIdx(userIdx);
        ((Main) getApplication()).setProfile(profile);

        // Adding Toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CheckInviteStatus checkInviteStatus = new CheckInviteStatus();
        try {
            String inviteStatus = checkInviteStatus.execute().get();
            Toast.makeText(getApplicationContext(), ""+inviteStatus, Toast.LENGTH_LONG).show();
            /**
             * inviteStatus = 0 아무것도 아닌 상태 혹은 거절 상태
             * inviteStatus = 1 수락 요청 온 상태
             * inviteStatus = 2 수락 요청을 수락한 상태 (대기)
             * inviteStatus = 3 단체 타임 캡슐로 이동한 상태
             */
            if(inviteStatus.equals("1")){
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_waiting_request, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Member Information");
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setView(dialogView);
                builder.setPositiveButton("수락", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent getCapsuleIdx = getIntent();

                        UpdateInviteStatus updateInviteStatus = new UpdateInviteStatus();
                        updateInviteStatus.execute("2");

                        Intent intent = new Intent(LoginComplete.this, CapsuleGroupMake.class);
                        intent.putExtra("groupIdx", getCapsuleIdx.getExtras().getString("groupIdx"));
                        startActivityForResult(intent, 200);
                    }
                });
                builder.setNegativeButton("거절", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UpdateInviteStatus updateInviteStatus = new UpdateInviteStatus();
                        updateInviteStatus.execute("0");

                        Toast.makeText(getApplicationContext(), "초대를 거절하셨습니다.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Friend friend = new Friend();
        try {
            result = friend.execute().get();
            Log.e("@@@","friendStatus@@@" + result);
            if(!result.equals("noFriends")) {
                String[] together = result.split("\\*");
                fId = new ArrayList<>();
                fStatus = new ArrayList<>();
                for(int i=0; i<together.length;i++) {
                    if(together[i].split(",")[1].equals("1")) {
                        fId.add(together[i].split(",")[0]);
                        fStatus.add(together[i].split(",")[1]);
                    }
                }
            }
        } catch (InterruptedException e) { e.printStackTrace(); }
        catch (ExecutionException e) {e.printStackTrace();}

        /***********네비게이션바 설정 부분**************/

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHeader = navigationView.getHeaderView(0);

        navImgView = (ImageView) navHeader.findViewById(R.id.navImageView);
        navText01 = (TextView) navHeader.findViewById(R.id.navText01);
        navText02 = (TextView) navHeader.findViewById(R.id.navText02);

        navText01.setText(userId + "님 환영해요:)");

        friendCount = (TextView) findViewById(R.id.friendCount);
        if(!result.equals("noFriends")) {
            friendStatus = true;
            friendCount.setText("현재 수락 대기 친구는? " + fStatus.size() + "명 입니다.");
        }else {
            friendStatus = false;
            friendCount.setText("현재 수락 대기 친구는? 0명 입니다.");
        }

        /*getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#330000ff")));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#550000ff")));*/

        LinearLayout tabPrivate = (LinearLayout) getLayoutInflater().inflate(R.layout.tab_private_capsule, null);
        LinearLayout tabGroup = (LinearLayout) getLayoutInflater().inflate(R.layout.tab_group_capsule, null);
        LinearLayout tabShow = (LinearLayout) getLayoutInflater().inflate(R.layout.tab_show_capsule, null);
        LinearLayout tabOption = (LinearLayout) getLayoutInflater().inflate(R.layout.tab_option_setting, null);

        // Initializing the TabLayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Tab One").setCustomView(tabPrivate));
        tabLayout.addTab(tabLayout.newTab().setText("Tab Two").setCustomView(tabGroup));
        tabLayout.addTab(tabLayout.newTab().setText("Tab Three").setCustomView(tabShow));
        tabLayout.addTab(tabLayout.newTab().setText("Tab Four").setCustomView(tabOption));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Initializing ViewPager
        viewPager = (ViewPager) findViewById(R.id.pager);

        // Creating TabPagerAdapter adapter
        TabPagerAdapter pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), userId);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // Set TabSelectedListener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        /********************************************************
         *          친구 추가 수정 부분
         ******************************************************/
        /*FriendList friendList = new FriendList();
        try {
            String result = friendList.execute().get();
            if(!result.equals("no")) {
                listItem = result.split(",");

                //ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItem);
                ListView listView = (ListView) findViewById(R.id.navListView);
                listView.setAdapter(
                        new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItem)
                );
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/


        profile = Main.getProfile(); //유저 프로필 사진 경로
        if(profile!=null)
            profile = profile.replace("/usr/local/nodeServer/public", "http://210.123.254.219:3001");

        Glide.with(getApplicationContext()).load(profile).centerCrop().bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(navImgView);

        backPressCloseHandler = new BackPressCloseHandler(this);
    } //onCreate end

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Toast.makeText(getApplicationContext(), "232323", Toast.LENGTH_LONG).show();
        super.onActivityResult(requestCode,resultCode,data);
        /*if (resultCode == RESULT_CANCELED) {
            finish();
        }else if(resultCode == ADD_FRIEND_CODE){
            //친구 추가 페이지에서 백스페이스
        }*/
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
    /*private TextView navText;
    private Button mainBtn1, mainBtn2, mainBtn3, mainBtn4;
    private final static String TAG = "@@@";
    private android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
    private android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    private Fragment fragment=null;
    private EditText capsulePrivateLetter;
    private LinearLayout linearLayout;
    CapsuleGroupMain c = new CapsuleGroupMain();
    *//**********************************************
     *                 Animation                  *
     **********************************************//*
    //final Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_slide_in_left);

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_complete);

        getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;

        getWindow().getAttributes().height = WindowManager.LayoutParams.MATCH_PARENT;


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        *//*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*//*

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        *//************start****************//*
        Intent intent = getIntent();
        String userId = intent.getStringExtra("id");

        *//*LayoutInflater layoutInflater  = LayoutInflater.from(this);
        LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.nav_header_main, this, true);*//**//*

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.nav_header_main, this, false);

        //View header = getLayoutInflater().inflate(R.layout.nav_header_main, null, false);*//*

        *//**********************************************
         *       네비게이션 바 설정 부분              *
         **********************************************//*
        View navHeader = navigationView.getHeaderView(0);

        navText = (TextView) navHeader.findViewById(R.id.navText02);
        navText.setText(userId + "님 환영해요:)");

        *//**********************************************
         *               버튼 설정 부분               *
         **********************************************//*

        findViewById(R.id.mainBtn1).setOnClickListener(listener);
        findViewById(R.id.mainBtn2).setOnClickListener(listener);
        findViewById(R.id.mainBtn3).setOnClickListener(listener);
        findViewById(R.id.mainBtn4).setOnClickListener(listener);
        *//**********************************************
         *               FragmentManage               *
         **********************************************//*
        fragment = new CapsulePrivateMain();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
       *//* FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, new Fragment());
        fragmentTransaction.commit();*//*

        LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        linearLayout = (LinearLayout) inflater.inflate( R.layout.capsule_private_main, null );

        *//*FloatingActionButton fab = (FloatingActionButton) linearLayout.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                capsulePrivateLetter = (EditText) linearLayout.findViewById(R.id.capsulePrivateLetter);
                Log.d(TAG, "############"+capsulePrivateLetter.getText().toString());
            }
        });*//*
    }

    *//*public void getFromUser(String message) {
        sendMessage(message);
    }*//*

    public void test(View v){
        EditText t = (EditText) v.findViewById(R.id.capsulePrivateLetter);
        String string = t.getText().toString();
        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@허러ㅓㅓㅓㅓㅓ"+string);
    }

    *//**********************************************
     *               버튼 리스너 부분             *
     **********************************************//*
    Button.OnClickListener listener = new View.OnClickListener() {
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.mainBtn1:
                    Log.d(TAG,"@@@@@@@@@@@@@@@"+"111");
                    //startActivity(new Intent(LoginComplete.this, com.example.ahn.finalproject.PrivateCapsule.MainActivity.class));
                    fragment = new CapsulePrivateMain();
                    *//*CapsulePrivateMain fragment = new CapsulePrivateMain();
                    fragmentTransaction.replace(R.id.container, fragment);
                    fragmentTransaction.commit();*//*
                    break;
                case R.id.mainBtn2:
                    Log.d(TAG,"@@@@@@@@@@@@@@@"+"들어옴");
                    fragment = new CapsuleGroupMain();
                    *//*CapsuleGroupMain fragment1 = new CapsuleGroupMain();
                    fragmentTransaction.replace(R.id.container, fragment1);
                    fragmentTransaction.commit();*//*
                    break;
                case R.id.mainBtn3:
                    break;
                case R.id.mainBtn4:
                    break;
            }
            Log.d(TAG, "@@@@@@@@@@@@@fragment"+fragment);
            //fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }*/
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

    /**************친구 데이터 받아오는 AsyncTask********************/
    /*****************************************************************/

    class Friend extends AsyncTask<String, Integer, String> {
        URL url;
        //HttpURLConnection conn;
        String line;
        int menuSeq;


        @Override
        protected String doInBackground(String... urls) {

            BufferedReader bufferedReader = null;
            line = "";
            String userId = Main.getUserId();
            Log.e("@@@","friendStatusMYID@@@" + userId);
            try {
                url = new URL("http://210.123.254.219:3001" + "/getFriend");
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
                Log.d("@@@@@", "@@@@Line"+line);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Log.e("line",line);
            return line;
        }
    }

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
                url = new URL("http://210.123.254.219:3001" + "/getInvite");
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
     * 초대상태 업데이트 (수락 누른 상태), 단체 캡슐 테이블에 데이터 삽입
     ****************************************************************/

    class MakeGroupCapsule extends AsyncTask<String, Integer, String> {
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
     * 친구리스트 받아오는 곳
     ****************************************************************/

    class FriendList extends AsyncTask<String, Integer, String> {
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
                url = new URL("http://210.123.254.219:3001" + "/getFriendList");
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

    public void addFriend(View view){ //친구 추가 버튼

            Intent intent = new Intent(getApplicationContext(), AddFriend.class);
            startActivityForResult(intent, ADD_FRIEND_CODE);
            finish();

    }

    public void moveFriend(View view){ //요청 친구 확인 버튼
        if(!friendStatus)
            Toast.makeText(getApplicationContext(), "확인할 요청이 없습니다.", Toast.LENGTH_LONG).show();
        else {
            Intent intent = new Intent(getApplicationContext(), RequestFriend.class);
            startActivityForResult(intent, REQUEST_FRIEND_CODE);
            finish();
            Toast.makeText(getApplicationContext(), "확인", Toast.LENGTH_LONG).show();
        }
    }
}