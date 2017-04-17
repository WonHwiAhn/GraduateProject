package com.example.ahn.finalproject.MainLogin;

/**
 * Created by Ahn on 2017-03-24.
 */

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.example.ahn.finalproject.Adapter.TabPagerAdapter;

public class LoginComplete extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_complete);

        // Adding Toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        TabPagerAdapter pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
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
}