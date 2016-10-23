package com.example.yyh.test;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    Button bt;
    ImageView imageView1,imageView2;

    Bitmap bitmap2;

    //选择图片的标记
    private static final int CHOICE_PHOTO=999;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CHOICE_PHOTO:
                if (resultCode==RESULT_OK){
                    //判断手机系统版本
                    if (Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                        imageView2.setDrawingCacheEnabled(true);
                        //  bitmap2=Bitmap.createBitmap(imageView1.getDrawingCache());
                        imageView2.setImageDrawable(imageView1.getDrawable());
                        imageView2.setDrawingCacheEnabled(false);
                        // imageView2.setImageBitmap(bitmap2);
                    }else {
                        handleImageBeforeKitKat(data);
                        imageView2.setDrawingCacheEnabled(true);
                        //  bitmap2=Bitmap.createBitmap(imageView1.getDrawingCache());
                        imageView2.setImageDrawable(imageView1.getDrawable());
                        imageView2.setDrawingCacheEnabled(false);
                        // imageView2.setImageBitmap(bitmap2);
                    }
                }
        }
    }
    //手机不大于19的取数据方法
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri =data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }
    /**
     * 手机大于19的取数据方法
     * @param data
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data) {
        String imagePath=null;
        Uri uri=data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)){
            //如果是document类型的url,则通过document的id处理。
            String docId=DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id =docId.split(":")[1];//解析出数字格式的id;
                String selection= MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contenturi= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath=getImagePath(contenturi,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果不是document类型的uri,则使用普通的方式处理。
            imagePath=getImagePath(uri,null);
        }
        displayImage(imagePath);
    }
    /**
     * 显示图片
     * @param imagePath  //图片的路径。
     */
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (isHeigthBigWidth(bitmap)) {
                Bitmap bt = rotaingImageView(bitmap);//将图片旋转90度。
                Bitmap disbitmapt = ajustBitmap(bt);
                imageView1.setImageBitmap(disbitmapt);
            } else {
                Bitmap disbitmap = ajustBitmap(bitmap);
                imageView1.setImageBitmap(disbitmap);
            }
        }
    }

    /**
     * 判断图片高是否大于宽 如果大于则要旋转图片
     * @return
     */
    public boolean isHeigthBigWidth(Bitmap bitmap) {
        int width= bitmap.getWidth();
        int heigth=bitmap.getHeight();
        if (heigth>width){
            return true;
        }else {
            return false;
        }

    }

    //调整图片的大小
    private Bitmap ajustBitmap(Bitmap bitmap) {
        int width=getWindowManager().getDefaultDisplay().getWidth();
        int heigth=width/5*3;
        Bitmap scaledBitmap=Bitmap.createScaledBitmap(bitmap, width, heigth, true);
        return scaledBitmap;
    }



    /**
     * 调整图片的方向
     * @param bitmap
     * @return
     */
    private Bitmap rotaingImageView(Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();;
        matrix.postRotate(270);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }
    /**
     * 得到图片的路径
     * @param externalContentUri
     * @param selection
     * @return
     */
    private String getImagePath(Uri externalContentUri, String selection) {
        String path=null;
        Cursor cursor=getContentResolver().query(externalContentUri, null, selection, null, null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
        }
        cursor.close();
        return path;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt= (Button) findViewById(R.id.bt);
        imageView1= (ImageView) findViewById(R.id.image_view);
        imageView2= (ImageView) findViewById(R.id.image_view2);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, CHOICE_PHOTO);//打开相册
               imageView2.setDrawingCacheEnabled(true);
              //  bitmap2=Bitmap.createBitmap(imageView1.getDrawingCache());
                imageView2.setImageDrawable(imageView1.getDrawable());
                imageView2.setDrawingCacheEnabled(false);
               // imageView2.setImageBitmap(bitmap2);
            }
        });
    }
}
