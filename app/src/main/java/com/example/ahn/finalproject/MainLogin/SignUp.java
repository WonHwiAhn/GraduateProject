package com.example.ahn.finalproject.MainLogin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import static com.example.ahn.finalproject.MainLogin.R.id.view1;

public class SignUp extends AppCompatActivity {
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
    /************/

    /**
     * xml 기본 변수
     **/
    EditText userId, userPassword, userPasswordConfirm, userName, userBirth, userPhone, userEmail;
    Button btnSignup;
    TextView userIdCheck, userPasswordCheck, userNameCheck, userEmailCheck;
    ImageView userPasswordConfirmImg;

    /*****************/

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

        userPasswordConfirmImg = (ImageView) findViewById(view1);

        userId.setFilters(new InputFilter[]{filterAlphaNum}); //숫자와 영어 소문자 필터 넣어줌.
        userName.setFilters(new InputFilter[]{filterAlphaNum1});

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

        /*******************************************************************/
        GregorianCalendar calendar = new GregorianCalendar();  // 달력 API 사용하기 위함
        btnSignup = (Button) findViewById(R.id.signup_btn_register);

        activity_sign_up = (RelativeLayout) findViewById(R.id.activity_sign_up);

        btnSignup.setOnClickListener(listener);

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
            }
        }
    };


    public String userIdCheckResult() {
        if (userId.getText().toString().length() == 0)
            return "필수 입력 사항입니다.";
        else if (userId.getText().toString().length() < 5)
            return "아이디 조건을 충족하지 못합니다. 5글자 이상 입력하세요.";
        else if(userId.getText().toString().length() > 10)
            return "아이디 조건을 충족하지 못합니다. 10글자 이하로 입력하세요.";
        else{
            if(checkUserId()){
                userIdCheck.setTextColor(Color.CYAN);
                return "사용 가능한 아이디입니다.";
            }
            else
                return "사용 불가능한 아이디입니다.";
        }
    }

    public String userPasswordCheckResult(){
        if (userPassword.getText().toString().length() == 0)
            return "필수 입력 사항입니다.";
        else if (userPassword.getText().toString().length() < 6)
            return "패스워드 조건을 충족하지 못합니다. 6글자 이상 입력하세요.";
        else if(userPassword.getText().toString().contains("^[a-z0-9]+$")) //특수문자 1개이상, 대문자 1개이상 체크하는 법
            return "특수문자 ㅇㅇ";
        else
            return "사용가능한 패스워드입니다.";
    }

    public void test1(View view) {
        Toast.makeText(getApplicationContext(), "된다!!", Toast.LENGTH_LONG).show();
    }

    public void userBirthCalendar(View view) {
        new DatePickerDialog(SignUp.this, dateSetListener, year, month, day).show();
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

            /*SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
            String strCurMonth = CurYearFormat.format(date);*/
            if (strCurYear >= year) {
                if (strCurMonth >= monthOfYear + 1)
                    if (strCurDay >= dayOfMonth)
                        userBirth.setText(msg);
            }
        }
    };



    private void signUpUser() {
        /**
         userData정리
         */
        String id = userId.getText().toString();
        String password = userPassword.getText().toString();
        String name = userName.getText().toString();
        String birth = userBirth.getText().toString();
        String phone = userPhone.getText().toString();
        String email = userEmail.getText().toString();


        BackgroundTask backgroundTask = new BackgroundTask(this);

        backgroundTask.execute("register", id, password, name, birth, phone, email);

        Intent intent = new Intent(getApplicationContext(), SignUpPopup.class);
        startActivityForResult(intent, 1);
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

        if(a.equals(""))
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

    protected void onActivityResult(int requestCode, int result, Intent data) {
        if (result == RESULT_OK) {
            startActivity(new Intent(SignUp.this, MainActivity.class));
            finish();
        }
    }

    /****************글자 제한 필터 ***************************/
    //소문자랑 숫자만 입력가능
    public InputFilter filterAlphaNum = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-z0-9]+$");
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
            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$"); //백스페이스 누를 때도 나오는 문제!!!!!!!!!!!!!!!!
            if (!ps.matcher(source).matches()) {
                userNameCheck.setVisibility(View.VISIBLE);
                userNameCheck.setText("특수문자는 입력 불가합니다.");
                return "";
            }
            return null;
        }
    };
    /***********************************************************/
}

