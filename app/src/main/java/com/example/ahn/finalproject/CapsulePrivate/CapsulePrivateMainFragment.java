package com.example.ahn.finalproject.CapsulePrivate;

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
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ahn.finalproject.GlobalValues.Main;
import com.example.ahn.finalproject.MainLogin.BackgroundTask;
import com.example.ahn.finalproject.MainLogin.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
 * Created by Ahn on 2017-04-16.
 */

public class CapsulePrivateMainFragment extends Fragment {
    final static private String TAG = "@@@";
    static private Button privateGallery, privateCamera, privateMake;
    static private int REQ_CODE_SELECT_IMAGE = 100;
    static private int REQ_CODE_CAPTURE_IMAGE = 101;
    Uri albumImage, fileUri, picUri;
    Bitmap image_bitmap;
    Bitmap bitmap;
    String imgPath;
    String imgName, executeName;
    StringBuilder encodedString;
    View view;
    ImageView image; //사진 미리보기 역할해주는 변수
    EditText capsulePrivateLetter;
    String userId;
    ProgressDialog progressDialog;
    //CapsulePrivateActivity cpa = new CapsulePrivateActivity();

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 107;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_private_capsule, container, false);
        capsulePrivateLetter = (EditText) view.findViewById(R.id.capsulePrivateLetter);
        privateGallery = (Button) view.findViewById(R.id.privateGallery);
        privateCamera = (Button) view.findViewById(R.id.privateCamera);
        privateMake = (Button) view.findViewById(R.id.privateMake);
        privateGallery.setOnClickListener(listener);
        privateCamera.setOnClickListener(listener);
        privateMake.setOnClickListener(listener);

        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        return view;
    }

    Button.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view){
            if(view.getId() == R.id.privateGallery){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
            }else if(view.getId() == R.id.privateCamera){
                startActivityForResult(getPickImageChooserIntent(), REQ_CODE_CAPTURE_IMAGE);
                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFileUri(1);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, REQ_CODE_CAPTURE_IMAGE);*/
            }else if(view.getId() == R.id.privateMake){
                // When Image is selected from Gallery
                if (imgPath != null && !imgPath.isEmpty()) {
                    Log.e("Converting Image to ","Binary Data");
                    // Convert image to String using Base64
                    encodeImagetoString();
                    // When Image is not selected from Gallery
                } else {
                    Toast.makeText(
                            getContext(),
                            "You must select image from gallery before you try to upload",
                            Toast.LENGTH_LONG).show();
                }
            }/*else if(view.getId() == R.id.login_btn_login){
                loginUser(input_email.getText().toString(), input_password.getText().toString());
            }*/
        }
    };

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
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg" );
            Log.d(TAG, "@@@@@@@@@" + "make file");
        }else{
            Log.d(TAG, "@@@@@@@@@" + "We can't make file");
            return null;
        }
        return mediaFile;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        Toast.makeText(getContext(), "resultCode : "+resultCode,Toast.LENGTH_SHORT).show();

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
                    image_bitmap 	= MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                    //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    image = (ImageView) view.findViewById(R.id.privateImgView);

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
            image = (ImageView) view.findViewById(R.id.privateImgView);

            if (getPickImageResultUri(data) != null) {
                picUri = getPickImageResultUri(data);

                try {
                    image_bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), picUri);
                    image_bitmap = rotateImageIfRequired(image_bitmap, picUri);
                    image_bitmap = getResizedBitmap(image_bitmap, 500);

                    imgPath = picUri.toString();
                    imgPath = imgPath.replace("file://", "");
                    //imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
                    imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
                    Log.e("ImageName : ",imgName);
                    Toast.makeText(getContext(),""+imgName, Toast.LENGTH_LONG).show();
                    executeName = imgName.substring(imgName.lastIndexOf(".")+1);

                    CircleImageView croppedImageView = (CircleImageView) view.findViewById(R.id.img_profile);
                    croppedImageView.setImageBitmap(image_bitmap);
                    image.setImageBitmap(image_bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else {
                bitmap = (Bitmap) data.getExtras().get("data");

                image_bitmap = bitmap;
                CircleImageView croppedImageView = (CircleImageView) view.findViewById(R.id.img_profile);
                if (croppedImageView != null) {
                    croppedImageView.setImageBitmap(image_bitmap);
                }
                image.setImageBitmap(image_bitmap);
            }
        }
    }

    public void encodeImagetoString() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Loading Data...");
                progressDialog.show();
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
        String capsuleContent = capsulePrivateLetter.getText().toString();
        BackgroundTask backgroundTask = new BackgroundTask(getContext());
        /** 개인 캡슐 데이터 삽입하는 곳 *********/
        /*****************************************/
        String userId = Main.getUserId(); //현재 사용자 아이디
        String date = getDate(); //캡슐 만든 날짜

        try {
            String result = backgroundTask.execute("makePrivateCapsule",encodedString.toString(),imgName,capsuleContent, userId, date).get(); //개인 캡슐 서버로 넘기는곳
            if(result.equals("success")){  //캡슐이 성공적으로 보내졌다면
                progressDialog.dismiss();
                image.setImageBitmap(null);  //이미지 공백으로
                capsulePrivateLetter.setText("");  //텍스트뷰 공백으로
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    //파일명 추출
    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContext().getContentResolver().query(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        imgPath = cursor.getString(column_index);
        //imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
        imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
        Log.e("ImageName : ",imgName);
        executeName = imgName.substring(imgName.lastIndexOf(".")+1);
        return imgName;
    }

    //날짜 추출
    public String getDate(){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        String date = df.format(new Date());
        return date;
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
        PackageManager packageManager = getActivity().getPackageManager();

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
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

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
        File getImage = getActivity().getExternalCacheDir();
        if (getImage != null) {

            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
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
                return (getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getContext())
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
}