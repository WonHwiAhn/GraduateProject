package com.example.ahn.finalproject.MainLogin;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.example.ahn.finalproject.MainLogin.R.id.view1;

public class SignUp extends AppCompatActivity {
    final static int REQ_CODE_SELECT_IMAGE = 100;
    //private ProgressDialog loadingDlg;
    private Dialog loadingDlg;
    final static String TAG = "@@@";

    TextView btnLogin, btnForgotPass;
    EditText input_email, input_pw;
    RelativeLayout activity_sign_up;
    Snackbar snackbar;

    /**
     * 추가변수
     **/
    int year, month, day;
    Uri fileUri;
    Bitmap image_bitmap;
    Bitmap bitmap;
    String imgPath;
    String imgName;
    StringBuilder encodedString;
    ImageView profileimg;
    /************/

    /**
     * xml 기본 변수
     **/
    EditText userId, userPassword, userPasswordConfirm, userName, userBirth, userPhone, userEmail;
    Button btnSignup, btnUploadProfile;
    TextView userIdCheck, userPasswordCheck, userNameCheck, userEmailCheck, userPhoneCheck;
    ImageView userPasswordConfirmImg;

    /*****************/

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /****/
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 107;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userId = (EditText) findViewById(R.id.userId);
        userPassword = (EditText) findViewById(R.id.userPassword);
        userName = (EditText) findViewById(R.id.userName);
        userBirth = (EditText) findViewById(R.id.userBirth);
        userPhone = (EditText) findViewById(R.id.userPhone);
        userEmail = (EditText) findViewById(R.id.userEmail);
        userPasswordConfirm = (EditText) findViewById(R.id.userPasswordConfirm);

        userIdCheck = (TextView) findViewById(R.id.userIdCheck);
        userNameCheck = (TextView) findViewById(R.id.userNameCheck);
        userPasswordCheck = (TextView) findViewById(R.id.userpasswordCheck);
        userEmailCheck = (TextView) findViewById(R.id.userEmailCheck);
        userPhoneCheck = (TextView) findViewById(R.id.userPhoneCheck);

        userPasswordConfirmImg = (ImageView) findViewById(view1);

        userId.setFilters(new InputFilter[]{filterAlphaNum}); //숫자와 영어 소문자 필터 넣어줌.
        userName.setFilters(new InputFilter[]{filterAlphaNum1});

        /**
         *  폴더 접근 권한 설정 부분
         */
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //마시멜로 이상 버전만 권한 체크
            if (ContextCompat.checkSelfPermission(SignUp.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) { //권한이 있는지 없는지 체크 (권한 없으면 들어옴)
                if (ActivityCompat.shouldShowRequestPermissionRationale(SignUp.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //사용자에게 권한 획득에 대한 설명을 보여준 후 권한 요청을 수행
                    //권한을 한번이라도 거부한 적 있는지 검사
                    //있다면 true, 없다면 false
                    Toast.makeText(getApplicationContext(), "현재 권한 있음", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "현재 권한 없음", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(SignUp.this, PERMISSIONS_STORAGE, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }
        }*/
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(CAMERA);
        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        /***************************************************************
         *         TextChangedListener이벤트 처리 부분                  *
         ***************************************************************/

        //아이디 입력 부분 체크 (현재 사실상 필요 없음.)
        userId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //charSequence = 입력된 문자 전체
                //int 첫 번째 매개변수 (i) = 글자수 -1
                String str = "abAB12!@#~`!@#$%^&*()_+=-|][}{;:";
                boolean chk = Pattern.matches("[0-9|a-z]*", charSequence);
                //0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝
                Log.d("@@@@@@@", chk ? "통과" : "실패");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //패스워드 입력 부분 체크
        userPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Toast.makeText(getApplicationContext(), charSequence.subSequence(i,i+1), Toast.LENGTH_LONG).show();
                //백스페이스할 때 문제

                //boolean chk = Pattern.matches("[0-9|a-z]*", charSequence.subSequence(i,i));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        /***************************************************************/

        /***************************************************************
         *                  FocusOut 이벤트 처리 부분                  *
         ***************************************************************/

        userId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    userIdCheck.setVisibility(View.VISIBLE);
                    userIdCheck.setText(userIdCheckResult());
                }
            }
        });

        userPassword.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    userPasswordCheck.setVisibility(View.VISIBLE);
                    userPasswordCheck.setText(userPasswordCheckResult());
                }
            }
        });

        userPasswordConfirm.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                   if(userPasswordConfirm.getText().toString().equals(userPassword.getText().toString()))
                       userPasswordConfirmImg.setVisibility(View.VISIBLE);
                    else
                       userPasswordConfirmImg.setVisibility(View.GONE);
                }
            }
        });

        userEmail.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View view, boolean hasFocus){
                if(!hasFocus){
                    userEmailCheck.setVisibility(View.VISIBLE);
                    userEmailCheck.setText(userEmailCheckResult());
                }
            }
        });

        userPhone.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View view, boolean hasFocus){
                if(!hasFocus){
                    userPhoneCheck.setVisibility(View.VISIBLE);
                    userPhoneCheck.setText(userPhoneCheckResult());
                }
            }
        });

        /*******************************************************************/
        GregorianCalendar calendar = new GregorianCalendar();  // 달력 API 사용하기 위함
        btnSignup = (Button) findViewById(R.id.signup_btn_register);
        btnUploadProfile = (Button) findViewById(R.id.profileupload);

        activity_sign_up = (RelativeLayout) findViewById(R.id.activity_sign_up);

        btnSignup.setOnClickListener(listener);
        btnUploadProfile.setOnClickListener(listener);

        /**추가된부분**/
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        /***/
    }

    Button.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (view.getId() == R.id.signup_btn_register) {
                signUpUser();
            }else if(view.getId() == R.id.profileupload){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }
        }
    };

    /***************************************************************
     *                  userId 입력값 체크하는 곳                  *
     ***************************************************************/
    public String userIdCheckResult() {
        if (userId.getText().toString().length() == 0) {
            userIdCheck.setTextColor(Color.RED);
            return "필수 입력 사항입니다.";
        }
        else if (userId.getText().toString().length() < 5) {
            userIdCheck.setTextColor(Color.RED);
            return "아이디 조건을 충족하지 못합니다. 5글자 이상 입력하세요.";
        }
        else if(userId.getText().toString().length() > 10) {
            userIdCheck.setTextColor(Color.RED);
            return "아이디 조건을 충족하지 못합니다. 10글자 이하로 입력하세요.";
        }
        else{
            if(checkUserId()){
                userIdCheck.setTextColor(Color.CYAN);
                return "사용 가능한 아이디입니다.";
            }
            else {
                userIdCheck.setTextColor(Color.RED);
                return "사용 불가능한 아이디입니다.";
            }
        }
    }
    /***************************************************************
     *                  userPW 입력값 체크하는 곳                  *
     ***************************************************************/
    public String userPasswordCheckResult() {
        //패스워드 한 글자씩 읽어서 arrayList에 저장하는 곳
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < userPassword.getText().toString().length(); i++) {
            arrayList.add(userPassword.getText().toString().substring(i, i + 1));
        }

        Pattern ps = Pattern.compile("^[`~!@#$%^&*()-_=+|;:'\",.<>/?]+$"); //특수문자 정규식

        if (userPassword.getText().toString().length() == 0) {
            userPasswordCheck.setTextColor(Color.RED);
            return "필수 입력 사항입니다.";
        }
        else if (userPassword.getText().toString().length() < 6) {
            userPasswordCheck.setTextColor(Color.RED);
            return "패스워드 조건을 충족하지 못합니다. 6글자 이상 입력하세요.";
        }
        /*else if (!userPassword.getText().toString().matches("^[A-Z]+$")){ //특수문자 1개이상, 대문자 1개이상 체크하는 법
            if(!userPassword.getText().toString().matches("^[a-zA-Z0-9`~!@#$%^&*()-_=+|;:'\",.<>/?]+$"))
                return "사용가능";
            return "특수문자 ㅇ11ㅇ";
        }*/
        else if (ps.matcher(userPassword.getText().toString()).matches()) {
            return "sdsdsd";
        }
        else {
            userPasswordCheck.setTextColor(Color.CYAN);
            return "사용가능한 패스워드입니다.";
        }
    }

    public void test1(View view) {
        Toast.makeText(getApplicationContext(), "된다!!", Toast.LENGTH_LONG).show();
    }
    /***************************************************************
     *                  user 생년월일 입력값 체크하는 곳           *
     ***************************************************************/
    public void userBirthCalendar(View view) {
        Context context = new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // API 24 이상일 경우 시스템 기본 테마 사용
            context = this;
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, dateSetListener, year, month, day);
        datePickerDialog.show();
        //new DatePickerDialog(SignUp.this, dateSetListener, year, month, day).show();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            // TODO Auto-generated method stub
            String msg = String.format("%d%d%d", year, monthOfYear + 1, dayOfMonth);

            SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyyMd");
            SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
            SimpleDateFormat CurMonthFormat = new SimpleDateFormat("MM");
            SimpleDateFormat CurDayFormat = new SimpleDateFormat("dd");

            String strCurDate = CurDateFormat.format(date);
            int strCurYear = Integer.parseInt(CurYearFormat.format(date));
            int strCurMonth = Integer.parseInt(CurMonthFormat.format(date));
            int strCurDay = Integer.parseInt(CurDayFormat.format(date));

            /*//*SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
            String strCurMonth = CurYearFormat.format(date);*/
            //***오늘보다 미래의 값은 설정할 수 없게 만들어놓는 부분**//*
            if (strCurYear >= year) {
                if (strCurMonth >= monthOfYear + 1)
                    if (strCurDay >= dayOfMonth)
                        userBirth.setText(msg);
            }

        }
    };

    /***************************************************************
     *                  userEmail입력값 체크하는 곳                  *
     ***************************************************************/

    public String userEmailCheckResult(){
        Pattern ps_email = Pattern.compile("[\\w\\~\\-\\.]+@[\\w\\~\\-]+(\\.[\\w\\~\\-]+)+");
        if(ps_email.matcher(userEmail.getText().toString()).matches()) {
            userEmailCheck.setTextColor(Color.CYAN);
            return "올바른 이메일 입니다.";
        }
        else{
            userEmailCheck.setTextColor(Color.RED);
            return "이메일 형식이 아닙니다.";
        }
    }

    public String userPhoneCheckResult(){
        String phoneNum = userPhone.getText().toString();
        if(phoneNum.length()>3){
            String frontNum = phoneNum.substring(0,3);
            if(!frontNum.equals("010")){
                userPhoneCheck.setTextColor(Color.RED);
                return "올바른 핸드폰 번호를 입력해주세요.";
            }else if(phoneNum.length() <11) {
                userPhoneCheck.setTextColor(Color.RED);
                return "올바른 핸드폰 번호를 입력해주세요.";
            }else{
                userPhoneCheck.setTextColor(Color.CYAN);
                return "올바른 핸드폰 번호입니다.";
            }
        }
        return "";
    }


//회원가입 버튼 눌렀을 떄 호출되는 함수
    private void signUpUser() {
        /**
         userData정리
         */
        String name_Str = getImageNameToUri(fileUri);
        imgName=name_Str;
        Log.e("fileName : ",name_Str);

        encodeImagetoString();

    }

    /*****아이디 중복 디비에서 체크하는 곳******/
    private boolean checkUserId(){
        boolean flag = true;
        BackgroundTask backgroundTask = new BackgroundTask(this);

        String id = userId.getText().toString();
        String a = null;
        try {
            a = backgroundTask.execute("checkUserId", id).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(a.equals("no"))
            flag = true;
        else
            flag = false;
        return flag;
    }
    /******************************************/
    protected void onStop() {
        super.onStop();
        if (loadingDlg != null) {
            loadingDlg.dismiss();
            loadingDlg = null;
        }
    }

    /****************글자 제한 필터 ***************************/
    //소문자랑 숫자만 입력가능
    public InputFilter filterAlphaNum = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-z0-9]+$"); //a-z0-9\n
            if (!ps.matcher(source).matches()) {
                userIdCheck.setVisibility(View.VISIBLE);
                userIdCheck.setText("대문자, 특수문자는 입력 불가합니다.");
                return "";
            }
            return null;
        }
    };

    //소문자, 대문자, 숫자 입력가능
    public InputFilter filterAlphaNum1 = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣]+$"); //백스페이스 누를 때도 나오는 문제!!!!!!!!!!!!!!!!
            if (!ps.matcher(source).matches()) {
                userNameCheck.setVisibility(View.VISIBLE);
                userNameCheck.setText("특수문자는 입력 불가합니다.");
                if(end!=0){
                    userName.setText(source.subSequence(start, end-1));
                    userName.setSelection(end-1);
                }
                Toast.makeText(getApplicationContext(), ""+source + "start" + dstart +"    end" + dend,Toast.LENGTH_LONG).show();
                return "";
            }
            return null;
        }
    };
    /***********************************************************/


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQ_CODE_SELECT_IMAGE)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                try {
                    //cpa.test(data);

                    //Uri에서 이미지 이름을 얻어온다.
                    fileUri = data.getData();
                    /*String name_Str = getImageNameToUri(data.getData());
                    imgName=name_Str;
                    Log.e("fileName : ",name_Str);*/

                    //이미지 데이터를 비트맵으로 받아온다.
                    image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    profileimg = (ImageView) findViewById(R.id.profileimg);
                    profileimg.setImageBitmap(image_bitmap);
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
        }else if(resultCode == RESULT_OK) {
                startActivity(new Intent(SignUp.this, MainActivity.class));
                finish();
            }
    }

    public void encodeImagetoString() {
        new AsyncTask<Object, Object, String>() {

            public void onPreExecute() {

            };

            @Override
            public String doInBackground(Object... params) {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                bitmap = BitmapFactory.decodeFile(imgPath,options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                encodedString = new StringBuilder(Base64.encodeToString(byte_arr, 0));
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

    public void makeHTTPCall(){
        String id = userId.getText().toString();
        String password = userPassword.getText().toString();
        String name = userName.getText().toString();
        String birth = userBirth.getText().toString();
        String phone = userPhone.getText().toString();
        String email = userEmail.getText().toString();

        BackgroundTask backgroundTask = new BackgroundTask(this);
        String token = FirebaseInstanceId.getInstance().getToken();
        //backgroundTask.execute("register", id, password, name, birth, phone, email, encodedString.toString(),imgName);
        backgroundTask.execute("register", id, password, name, phone, email,  encodedString.toString(),imgName, token);
        //아이디, 패스워드, 이름, 생년월일, 번호, 이메일 background에 보냄
        //나중에 backgroundTask.execute의 리턴 값을 받고 그 값에 따라 popup실행
        Intent intent = new Intent(getApplicationContext(), SignUpPopup.class);
        startActivityForResult(intent, 1);
    }

    //파일명 추출
    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        imgPath = cursor.getString(column_index);
        //imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
        String extendsName = imgPath.substring(imgPath.lastIndexOf(".")+1);
        imgName = userId.getText().toString()+"."+extendsName;
        //Log.e("ImageName : ",imgName);
        return imgName;
    }

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
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}

