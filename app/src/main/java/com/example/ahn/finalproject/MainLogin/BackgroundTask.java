package com.example.ahn.finalproject.MainLogin;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.ahn.finalproject.GlobalValues.Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import static android.content.ContentValues.TAG;

/**
 * Created by Ahn on 2017-04-10.
 */

public class BackgroundTask extends AsyncTask<String, Void, String> {
    AlertDialog alertDialog;
    Context ctx;

    public BackgroundTask(Context ctx){
        this.ctx = ctx;
    }

    @Override
    public void onPreExecute() {
        alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.setTitle("Login Information");
    }

    @Override
    public void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    public String doInBackground(String... params) {

        /*********학교 pc서버로 접속할 때*********************/
        String reg_url = "http://210.123.254.219:3001" + "/reg";
        String login_url = "http://192.168.1.2/login.php";
        String id_check_url = "http://210.123.254.219:3001" + "/userIdCheck";
        String img_url = "http://210.123.254.219:3001" + "/makePrivateCapsule";
        String getCapsule = "http://210.123.254.219:3001" + "/getcapsule";
        String getImg = "http://210.123.254.219:3001" + "/getImg";
        String getStatus = "http://210.123.254.219:3001" + "/getStatus";
        //String addFriend = "http://210.123.254.219:3001" + "/addFriend";
        String addFriend = "http://210.123.254.219:3001" + "/push";
        /*********내 pc서버로 접속할 때*********************/
        /*String reg_url = "http://allanahn.iptime.org:3001" + "/reg";
        String login_url = "http://192.168.1.2/login.php";
        String id_check_url = "http://allanahn.iptime.org:3001" + "/userIdCheck";
        String img_url = "http://allanahn.iptime.org:3001" + "/makePrivateCapsule";
        String getCapsule = "http://allanahn.iptime.org:3001" + "/getcapsule";*/


        String method = params[0];
        if(method.equals("register")){
            String id = params[1];
            String password = params[2];
            String name = params[3];
            //String birth = params[4];
            String phone = params[4];
            String email = params[5];
            String profileImg = params[6];
            String profileImgName = params[7];
            String token = params[8];
            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id,"UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password,"UTF-8") + "&" +
                        URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name,"UTF-8") + "&" +
                        //URLEncoder.encode("birth", "UTF-8") + "=" + URLEncoder.encode(birth,"UTF-8") + "&" +
                        URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone,"UTF-8") + "&" +
                        URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email,"UTF-8") + "&" +
                        URLEncoder.encode("profileImg", "UTF-8") + "=" + URLEncoder.encode(profileImg,"UTF-8") + "&" +
                        URLEncoder.encode("profileImgName", "UTF-8") + "=" + URLEncoder.encode(profileImgName,"UTF-8")+ "&" +
                        URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(token,"UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                IS.close();
                return "Registration Success....";

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(method.equals("login")){
            String login_name = params[1];
            String login_pass = params[2];
            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("login_name", "UTF-8") + "=" + URLEncoder.encode(login_name,"UTF-8")+"&"+
                        URLEncoder.encode("login_pass", "UTF-8") + "=" + URLEncoder.encode(login_pass,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = "";
                String line = "";

                while((line = bufferedReader.readLine()) != null){
                    response += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(method.equals("checkUserId")){
            String id = params[1];
            System.out.println("들어왔습니다!!!!");
            try {
                URL url = new URL(id_check_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = "";
                String line = "";

                while((line = bufferedReader.readLine()) != null){
                    response += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(method.equals("makePrivateCapsule")){
            String img = params[1];
            String imgName = params[2];
            String capsuleContent = params[3];
            String userId = params[4];
            String makeDate = params[5];
            try {
                URL url = new URL(img_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("img", "UTF-8") + "=" + URLEncoder.encode(img,"UTF-8")+"&"+
                        URLEncoder.encode("imgName", "UTF-8") + "=" + URLEncoder.encode(imgName,"UTF-8")+"&"+
                        URLEncoder.encode("capsuleContent", "UTF-8") + "=" + URLEncoder.encode(capsuleContent,"UTF-8")+"&"+
                        URLEncoder.encode("owner", "UTF-8") + "=" + URLEncoder.encode(userId,"UTF-8")+"&"+
                        URLEncoder.encode("makedate", "UTF-8") + "=" + URLEncoder.encode(makeDate,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = "";
                String line = "";

                while((line = bufferedReader.readLine()) != null){
                    response += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(method.equals("getCapsule")){
            URL url;
            String response="";

            String userId = params[1];

            try {
                    url = new URL(getCapsule);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("owner", "UTF-8") + "=" + URLEncoder.encode(userId,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line = "";

                while((line = bufferedReader.readLine()) != null){
                    response += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(method.equals("getStatus")){
            URL url;
            String response="";

            String userId = Main.getUserId();
            String friendId = params[1];

            try {
                url = new URL(getStatus);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("myId", "UTF-8") + "=" + URLEncoder.encode(userId,"UTF-8")+"&"+
                        URLEncoder.encode("friendId", "UTF-8") + "=" + URLEncoder.encode(friendId,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line = "";

                while((line = bufferedReader.readLine()) != null){
                    response += line;
                }
                Log.d(TAG,response);
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(method.equals("addFriend")){
            URL url;
            String response="";

            String userId = Main.getUserId();
            String friendId = params[1];

            try {
                url = new URL(addFriend);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("myId", "UTF-8") + "=" + URLEncoder.encode(userId,"UTF-8")+"&"+
                        URLEncoder.encode("friendId", "UTF-8") + "=" + URLEncoder.encode(friendId,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line = "";

                while((line = bufferedReader.readLine()) != null){
                    response += line;
                }
                Log.d(TAG,response);
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public void onPostExecute(String result) {
       /* if(result.equals("Registration Success...")){
            Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
        }*//*else{ //알 수 없는 에러...
            alertDialog.setMessage(result);
            alertDialog.show();
        }*/
    }
}

/*public class BackgroundTask extends AsyncTask<String, Void, String> {

    AlertDialog alertDialog;
    String mainUrl = "http://210.123.254.219:3001";
    String response = null;
    HttpURLConnection conn = null;
    String id;
    String password;

    Context ctx;

    BackgroundTask(Context ctx){
        this.ctx = ctx;
    }

    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.setTitle("Login Information");
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            Log.d(TAG, "@@@@@@@" + params.toString());
            id = params[0];
            password = params[1];


            Map<String,Object> paramsData = new LinkedHashMap<>();
            paramsData.put("id", id);
            paramsData.put("password", password);

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String,Object> param : paramsData.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            URL mOpenURL = new URL(mainUrl);

            conn = (HttpURLConnection)mOpenURL.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            for (int c; (c = in.read()) >= 0;){
                System.out.print((char)c);
            }


        } catch (Exception e) {
            Log.e("logins", "login post ERR " + e.getStackTrace()[0].getFileName() + " -> " + e.getStackTrace()[0].getLineNumber());
            e.printStackTrace();
        }



        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    //인코딩이 깨질시 이함수에 InputStream을 넘겨서 한글을 받아오면됨.
    private String convertStreamToString(InputStream is){
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();

        String line = null;

        try{
            while ((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally{
            try{
                is.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        return sb.toString();
    }*/

//}