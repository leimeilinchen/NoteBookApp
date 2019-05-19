package com.lml.notebook.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.lml.notebook.R;
import com.lml.notebook.db.DemoApplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


/*
此活动不与任何体系结构组件通信，
它只是将值传递回主活动，然后通过视图模型将它们输入到数据库中。
*/
public class AddNoteActivity extends BaseActivity {
    public static final String EXTRA_TITLE = "com.example.architecturecomponents.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.example.architecturecomponents.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY = "com.example.architecturecomponents.EXTRA_PRIORITY";
    public static final String EXTRA_PATH="PATH";
    // 用于保存对这些UI小部件的引用的本地成员变量。
    private EditText editTextTitle;
    private EditText editTextDescription;
    private NumberPicker numberPickerPriority;
    private ImageView iv_add_photo;
    private AlertDialog photoDialog;
    private String imageName;
    String filePath = Environment.getExternalStorageDirectory().getPath();
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;//
    private static final int PHOTO_REQUEST_GALLERY = 2;//
    private static final int PHOTO_REQUEST_CUT = 3;//
    private static final int camraCode = 222;
    private static final int storageCode = 333;
    private String tempPath;
    private String path;
    //接受处理消息
    private Handler handler = new Handler(new Handler.Callback() {//暂时先让秒数动起来

        @Override
        public boolean handleMessage(Message arg0) {
            if (arg0.what == 1) {
                Glide.with(AddNoteActivity.this).load(path).into(iv_add_photo);
                // indexMyList1Touxiang.setImageBitmap(getSmallBitmap(path, mWidth, mHeight));
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        DemoApplication.addActivity(this);
        // 在创建时存储对小部件的引用
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        numberPickerPriority = findViewById(R.id.number_picker_priority);
        iv_add_photo = findViewById(R.id.iv_add_photo);
        // 设置可使用数字选择器选择的最小值和最大值。
        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(10);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        // Adds the "close button"
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        // 更改此活动的标题，使其与默认的应用程序标题不同。
        setTitle("Add Note");
        iv_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //相册数据
                showCamera();
            }
        });
    }

    private void saveNote() {
        // 从文本/数字输入字段中获取值
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        int priority = numberPickerPriority.getValue();

        // 如果标题或描述为空（使用trim（）删除任何前导/尾随空格，则运行toast。
        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            // 通知用户他们需要插入标题和说明
            Toast.makeText(this, "请输入标题和内容", Toast.LENGTH_SHORT).show();
            // 返回方法的开头
            return;
        }

        //  创建将数据传递到静态字符串的intent，以便可以在主活动中检索它们。
        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_PRIORITY, priority);
        data.putExtra(EXTRA_PATH, path);
        // 如果输入按预期进行，则返回（例如，是否按下了关闭按钮？）
        setResult(RESULT_OK, data);
        finish();
    }

    // 在活动栏上创建保存图标。
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Instantiate a MenuInflater instance
        MenuInflater menuInflater = getMenuInflater();
        // 使用它来传递add_note_menu，使用传递给此方法的菜单值。
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }

    // Runs when save icon is clicked.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Uses switch statement to handle click
        switch (item.getItemId()) {
            case android.R.id.home:   //返回键的id
                this.finish();
                return false;
            // If the ID of the item selected is save_note
            case R.id.save_note:
                // Runs save note method, returns true.
                saveNote();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    // 拍照部分
    private void showCamera() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddNoteActivity.this);
        View view = LayoutInflater.from(AddNoteActivity.this).inflate(R.layout.activity_popu, null);
        TextView tvPhoto = (TextView) view.findViewById(R.id.photo_take);//拍照
        TextView tvAlbum = (TextView) view.findViewById(R.id.photo_album);//相册选择
        TextView tvCancel = (TextView) view.findViewById(R.id.photo_cancel);//取消
        builder.setView(view);
        //拍照
        tvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    int checkCallPhonePermission = ContextCompat.checkSelfPermission(AddNoteActivity.this, Manifest.permission.CAMERA);
                    if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddNoteActivity.this, new String[]{Manifest.permission.CAMERA}, camraCode);
                        return;
                    } else {

                        openCamra();//调用具体方法
                    }
                } else {

                    openCamra();//调用具体方法
                }
            }
        });
        //相册选择
        tvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNowTime();//获取当前时间
                imageName = getNowTime() + ".jpg";
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                photoDialog.dismiss();

            }
        });
        //取消
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

    private void openCamra() {
        imageName = getNowTime() + ".jpg";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定调用相机拍照后照片的储存路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(filePath, imageName)));
        startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
        photoDialog.cancel();
    }

    @SuppressLint("SimpleDateFormat")
    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dateFormat.format(date);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_REQUEST_TAKEPHOTO:

                    startPhotoZoom(
                            Uri.fromFile(new File(filePath, imageName)),
                            100);
                    break;

                case PHOTO_REQUEST_GALLERY:
                    if (data != null)
                        startPhotoZoom(data.getData(), 100);
                    break;

                case PHOTO_REQUEST_CUT:
                    if (Build.VERSION.SDK_INT >= 23) {

                        // 检查该权限是否已经获取
                        int i = ContextCompat.checkSelfPermission(AddNoteActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                        if (i != PackageManager.PERMISSION_GRANTED) {
                            // 如果没有授予该权限，就去提示用户请求
                            ActivityCompat.requestPermissions(AddNoteActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                                    , storageCode);
                        } else {
                            path = filePath
                                    + "/" + imageName;
                            handler.sendEmptyMessage(1);


                        }
                    } else {
                        path = filePath
                                + "/" + imageName;
                        handler.sendEmptyMessage(1);

                    }


                    break;

            }
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    private void startPhotoZoom(Uri uri1, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri1, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", false);

        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(filePath, imageName)));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }
}
