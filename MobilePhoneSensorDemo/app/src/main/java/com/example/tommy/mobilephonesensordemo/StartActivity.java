package com.example.tommy.mobilephonesensordemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Tommy on 2017/9/29.
 */

public class StartActivity extends Activity {

    private final String TAG = "StartActivity";

    private Context context = this;
    Button StartDetectButton,GenerateFileButton;
    EditText editText;
    String durationTime;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);
        StartDetectButton = (Button)findViewById(R.id.StartService);
        editText = (EditText)findViewById(R.id.readTime);

        StartDetectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                durationTime = editText.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("durationTime",durationTime);
                intent.setClass(StartActivity.this,SensorActivity.class);
                startActivity(intent);
            }
        });

        GenerateFileButton = (Button)findViewById(R.id.GenerateTxt);
        GenerateFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFile();
            }
        });
    }


    void saveFile(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //执行存储sdcard方法
            String fileName = "SensorData.txt";
            FileOutputStream fos = null;
            try {
                DBHelper dbHelper = new DBHelper(context);
                SQLiteDatabase database = dbHelper.getReadableDatabase();
                dbHelper.onUpgrade(database,0,0);
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),fileName);
                Log.i(TAG, "onClick: "  + context.getFilesDir());
                fos = new FileOutputStream(file,true);

                String content = "";
                Cursor cursor=database.rawQuery(
                        "SELECT * FROM listdata ", null);
                cursor.moveToFirst();
                while(!cursor.isAfterLast()){
                    String data = cursor.getString(0);
                    content = content + data + " ";
                    cursor.moveToNext();
                }
                cursor.close();
                fos.write(content.getBytes("UTF-8"));
                Toast.makeText(context,"Complete:" + fileName,Toast.LENGTH_SHORT).show();
                fos.close();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(context,"Error" ,Toast.LENGTH_SHORT).show();
            }
        }
        else{
            //存储到手机中，或提示
            Toast.makeText(context,"No sdCard" ,Toast.LENGTH_SHORT).show();
        }
    }

}
