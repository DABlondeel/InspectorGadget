package net.dimi3.pdfwriter;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;




public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_CODE = 1000;
    private static final int COMBINED_CODE = 2000;
    private static final int IMAGE_CAPTURE_CODE = 3000;
    EditText mTextEt;
    Button mSaveBtn;
    ImageView mImageView;
    Button mBtnCapturePicture;
    Uri Image_Uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // SafePdf (oncreate)
        mTextEt = findViewById(R.id.textEdit);
        mSaveBtn = findViewById(R.id.SavePDF);
        mImageView = findViewById(R.id.ImageView);
        mBtnCapturePicture = findViewById(R.id.btnCapturePicture);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void  onClick(View v ){
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                        //permission not granted, Request here
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions,STORAGE_CODE);
                    }
                    else{
                        //permission is already granted, call save pdf method
                       savePdf();
                    }
                }
                else{
                    //system OS < Marshmellow = no check req , call pdf method
                    savePdf();
                }
             }
        });
        // capture picture(oncreate)

        mBtnCapturePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED ){
                        //permission not granted, Request here
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
                        requestPermissions(permissions,COMBINED_CODE);
                    }
                    else{
                        //permission already granted
                        openCamera();
                    }
                }
                else{
                    //system > marshmellow
                    openCamera();
                }
            }
        });
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From the camera");
        Image_Uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        //camera intent
        Intent cameraIntent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,Image_Uri);
        startActivityForResult(cameraIntent,IMAGE_CAPTURE_CODE);
    }


    private void savePdf() {
        //create object of Document Class (Itext)
        Document mDoc = new Document();
        //pdf File Name
        String mFileName = new SimpleDateFormat("yyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());
        //pdf file path
        String mFilePath = Environment.getExternalStorageDirectory()+"/"+mFileName + ".pdf";

        try{
            PdfWriter.getInstance(mDoc, new FileOutputStream(mFilePath));
            //open document for writing
            mDoc.open();
            //get info from EditText
            String mText = mTextEt.getText().toString();
            //add author of doc.
            mDoc.addAuthor("Dimitri Blondeel");
            //You can add more features with mDoc.XXX
            //and add extra paragaphs or elements
            //add paragraph to doc
            mDoc.add(new Paragraph(mText));
            mDoc.close();
            //show message the file is saved and the path
            Toast.makeText(this, mFileName +".pdf\nis saved to\n "+mFilePath, Toast.LENGTH_LONG).show();
        }catch (Exception e){
            //if anything goes wrong causing an exception, this will break and show message
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
        //handle permission result
    @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case STORAGE_CODE:{
                if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                //permission granted, call savepdf
                    savePdf();
                }
                else{
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
                }
            }
            case COMBINED_CODE:{
                if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    //permission granted, call camera
                    openCamera();
                }
                else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      if(resultCode == RESULT_OK){

          //set the image captured to our IMAGEVIEW
          mImageView.setImageURI(Image_Uri);
      }
    }
}
