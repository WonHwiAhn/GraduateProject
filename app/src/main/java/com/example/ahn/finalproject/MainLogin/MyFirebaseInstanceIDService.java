package com.example.ahn.finalproject.MainLogin;

/**
 * Created by Kwangmin on 2017-05-02.
 */
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;



public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + token);

        // 생성등록된 토큰을 개인 앱서버에 보내 저장해 두었다가 추가 뭔가를 하고 싶으면 할 수 있도록 한다.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.

        /*SaveToken saveToken = new SaveToken();
        saveToken.execute(token);*/


    }
}

/*
class SaveToken extends AsyncTask<String, Integer, String> {
    URL url;
    HttpURLConnection conn;
    String line;
    int menuSeq;

    @Override
    protected String doInBackground(String... urls) {

        BufferedReader bufferedReader = null;
        line = "";
        try {
            //url = new URL("http://yys6910.dothome.co.kr/disaster/disasterTokenSave.php?token="+urls[0]);
            url = new URL("http:210.123.254.219:3001"+"/push");
            conn = (HttpURLConnection) url.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            line = br.readLine();
            Log.e("SaveToken",line);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.cancel(true);
        return line;
    }
}*/
