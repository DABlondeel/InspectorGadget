package net.dimi3.pdfwriter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.lang.annotation.Documented;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_CODE = 1000;
    EditText mTextEt;
    Button mSaveBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextEt = findViewById(R.id.textEdit);
        mSaveBtn = findViewById(R.id.SavePDF);

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
            Toast.makeText(this, mFileName +".pdf\nis saved to\n "+mFileName, Toast.LENGTH_LONG).show();
        }catch (Exception e){
            //if anything goes wrong causeign an exception, this will break and show message
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
    }
    }
}
