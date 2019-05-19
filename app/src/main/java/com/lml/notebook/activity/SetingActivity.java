package com.lml.notebook.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lml.notebook.R;
import com.lml.notebook.bean.EventBusBean;
import com.lml.notebook.bean.UserBean;
import com.lml.notebook.db.DbHelper;   //sqlite数据库存储
import com.lml.notebook.db.DemoApplication;
import com.lml.notebook.util.Preferences;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class SetingActivity extends BaseActivity {
    @BindView(R.id.imageView)
    CircleImageView imageView;//@BindView---->绑定一个view；id为一个view 变量
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.zhanghao)
    LinearLayout zhanghao;
    @BindView(R.id.xiugaimima)
    LinearLayout xiugaimima;
    @BindView(R.id.yinsi)
    LinearLayout yinsi;

    @BindView(R.id.baitian)
    TextView baitian;
    @BindView(R.id.yejian)
    LinearLayout yejian;
    @BindView(R.id.exit)
    LinearLayout exit;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String user;
    UserBean userBean;

    private int mWidth;
    private int mHeight;
    private Uri uri;
    private String mPublicPhotoPath;
    private String imageFileName;
    public static String noteContent;
    private AlertDialog photoDialog;

    private static final int REQ_GALLERY = 33;
    private static final int REQUEST_CODE_PICK_IMAGE = 22;
    private String tempPath;
    private String path;
    private String url;
    //接受处理消息
    private Handler handler = new Handler(new Handler.Callback() {//暂时先让秒数动起来

        @Override
        public boolean handleMessage(Message arg0) {
            if (arg0.what == 1) {
                Glide.with(SetingActivity.this).load(path).into(imageView);
                userBean = DbHelper.getInstance(SetingActivity.this).findUser(new String[]{user});
                userBean.setUrl(path);
                DbHelper.getInstance(SetingActivity.this).updateUser(userBean);
                // indexMyList1Touxiang.setImageBitmap(getSmallBitmap(path, mWidth, mHeight));
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // ChangeModeController.getInstance().init(this, R.attr.class).setTheme(this, R.style.DayTheme, R.style.NightTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seting);
        ButterKnife.bind(this);
        DemoApplication.addActivity(this);
        sharedPreferences = getSharedPreferences(this.getPackageName(), this.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (!TextUtils.isEmpty(sharedPreferences.getString("phone", null))) {
            user = sharedPreferences.getString("phone", null);
            userBean = DbHelper.getInstance(SetingActivity.this).findUser(new String[]{user});
            textView.setText(userBean.getNickName());
            Glide.with(SetingActivity.this).load(userBean.getUrl()).error(R.drawable.head).into(imageView);
        }

        if (Preferences.isNightMode()) {
            baitian.setText("黑夜模式");
        }else {
            baitian.setText("白天模式");
        }
    }

    @OnClick({R.id.imageView, R.id.yejian, R.id.exit, R.id.zhanghao, R.id.xiugaimima, R.id.yinsi})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageView:
                showCamera();
                break;
            case R.id.yejian:
                EventBus.getDefault().post(new EventBusBean("0"));
                if (Preferences.isNightMode()) {
                    baitian.setText("黑夜模式");
                }else {
                    baitian.setText("白天模式");
                }
                break;
            case R.id.exit:
                editor.remove("phone").commit();
                DemoApplication.finishActivity();
                break;
            case R.id.zhanghao:
                Intent intent = new Intent(SetingActivity.this, PassWordActivity.class);

                intent.putExtra("ZHANGHAO", "0");
                startActivity(intent);
                break;
            case R.id.xiugaimima:
                Intent intents = new Intent(SetingActivity.this, PassWordActivity.class);
                intents.putExtra("ZHANGHAO", "1");
                startActivity(intents);
                break;
            case R.id.yinsi:
                Intent intentv = new Intent(SetingActivity.this, LoginPassActivity.class);
                intentv.putExtra("TYPE", "0");
                startActivity(intentv);
                break;
        }
    }



    // 拍照部分
    private void showCamera() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SetingActivity.this);
        View view = LayoutInflater.from(SetingActivity.this).inflate(R.layout.activity_popu, null);
        TextView tvPhoto = (TextView) view.findViewById(R.id.photo_take);
        TextView tvAlbum = (TextView) view.findViewById(R.id.photo_album);
        TextView tvCancel = (TextView) view.findViewById(R.id.photo_cancel);
        builder.setView(view);
        tvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showTakePicture();
                photoDialog.dismiss();
            }
        });
        tvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFromAlbum();
                photoDialog.dismiss();

            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoDialog.cancel();
            }
        });

        photoDialog = builder.show();
        photoDialog.getWindow().setGravity(Gravity.BOTTOM);//设置弹框在屏幕的下方
        photoDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //讲dialog框背景颜色设置为透明
        photoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    //获取拍照的权限
    private void showTakePicture() {//        判断手机版本,如果低于6.0 则不用申请权限,直接拍照

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//7.0及以上
            if (ContextCompat.checkSelfPermission(SetingActivity.this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(SetingActivity.this, Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(SetingActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    ActivityCompat.requestPermissions(SetingActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
            } else {
                startTake();
            }
        } else {
            startTake();
        }
    }    //权限申请的回调

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    if (i == 0) {
                        startTake();
                    }
                } else {
                    Toast.makeText(SetingActivity.this, "" + "权限" + permissions[i] + "申请失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private void startTake() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断是否有相机应用
        if (takePictureIntent.resolveActivity(SetingActivity.this.getPackageManager()) != null) {
            //创建临时图片文件
            File photoFile = null;
            try {
                photoFile = createPublicImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //设置Action为拍照
            if (photoFile != null) {
                takePictureIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                //这里加入flag
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri photoURI;
                if (Build.VERSION.SDK_INT > 23) {
                    //7.0及以上
                    photoURI = FileProvider.getUriForFile(SetingActivity.this, "com.lml.notebook.fileprovider", photoFile);
                } else {
                    photoURI = Uri.fromFile(photoFile);
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQ_GALLERY);
            }
        }
        //将照片添加到相册中
        galleryAddPic(mPublicPhotoPath, SetingActivity.this);
    }

    /**
     * 获取相册中的图片
     */
    public void getImageFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    /**
     * 创建临时图片文件     * @return     * @throws IOException
     */
    private File createPublicImageFile() throws IOException {
        File path = null;
        if (hasSdcard()) {
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        }
        Date date = new Date();
        String timeStamp = getTime(date, "yyyyMMdd_HHmmss", Locale.CHINA);
        imageFileName = "Camera/" + "IMG_" + timeStamp + ".jpg";
        File image = new File(path, imageFileName);
        mPublicPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * 判断sdcard是否被挂载     * @return
     */
    public static boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取时间的方法     * @param date     * @param mode     * @param locale     * @return
     */
    private String getTime(Date date, String mode, Locale locale) {
        SimpleDateFormat format = new SimpleDateFormat(mode, locale);
        return format.format(date);
    }

    /**
     * 将照片添加到相册中
     */
    public static void galleryAddPic(String mPublicPhotoPath, Context context) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mPublicPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * 拍照之后获取结果的方法     * @param requestCode     * @param resultCode     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取mImageView的宽高
        mWidth = imageView.getWidth();
        mHeight = imageView.getHeight();
        switch (requestCode) {
            //拍照
            case REQ_GALLERY:

                if (resultCode != Activity.RESULT_OK) return;
                uri = Uri.parse(mPublicPhotoPath);
                path = uri.getPath();
                break;
            //获取相册的图片
            case REQUEST_CODE_PICK_IMAGE:

                if (data == null) return;
                uri = data.getData();
                int sdkVersion = Integer.valueOf(Build.VERSION.SDK);
                if (sdkVersion >= 19) {
                    // 或者 android.os.Build.VERSION_CODES.KITKAT这个常量的值是19
                    path = this.uri.getPath();//5.0直接返回的是图片路径
                    // Uri.getPath is ：  /document/image:46 ，5.0以下是一个和数据库有关的索引值
                    //path_above19:/storage/emulated/0/girl.jpg 这里才是获取的图片的真实路径
                    path = getPath_above19(SetingActivity.this, this.uri);
                } else {
                    path = getFilePath_below19(SetingActivity.this, this.uri);
                }
                break;
        }
        url = path;
        handler.sendEmptyMessage(1);


    }

    /**
     * 获取小于api19时获取相册中图片真正的uri     * @param context     * @param uri     * @return
     */
    public static String getFilePath_below19(Context context, Uri uri) {
        //这里开始的第二部分，获取图片的路径：低版本的是没问题的，但是sdk>19会获取不到
        String[] proj = {MediaStore.Images.Media.DATA};
        //好像是android多媒体数据库的封装接口，具体的看Android文档
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        //获得用户选择的图片的索引值
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        //将光标移至开头 ，这个很重要，不小心很容易引起越界
        cursor.moveToFirst();
        //最后根据索引值获取图片路径   结果类似：/mnt/sdcard/DCIM/Camera/IMG_20180724_032212.jpg
        String path = cursor.getString(column_index);
        return path;
    }

    /**
     * 获取大于api19时获取相册中图片真正的uri     * @param context     * @param uri     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath_above19(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {            // Return the remote address
            if (isGooglePhotosUri(uri)) return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
