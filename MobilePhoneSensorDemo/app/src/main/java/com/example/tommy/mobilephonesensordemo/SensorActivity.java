package com.example.tommy.mobilephonesensordemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class SensorActivity extends Activity implements SensorService.MsgListener{

    SensorService sensorService;
    Intent serviceIntent;

    private TextView mTextView;
    private static final String TAG = "SensorActivity";
    private TextView valueZ,valueX,valueY;

    int durationTime;
    private DataView dataView;
    private ArrayList<Float> datalist = new ArrayList<>();



    DBHelper databaseHelper;
    SQLiteDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_view);

        valueZ = (TextView)findViewById(R.id.valuez);
        dataView = (DataView)findViewById(R.id.dataView);
        Intent intent = getIntent();
        durationTime =Integer.parseInt(intent.getExtras().get("durationTime").toString());
        Log.i(TAG, "onCreate: "+durationTime);


        databaseHelper = new DBHelper(this);
        database = databaseHelper.getWritableDatabase();
        databaseHelper.onUpgrade(database,0,0);



        serviceIntent = new Intent(this,SensorService.class);
        bindService(serviceIntent,serviceConnection,BIND_AUTO_CREATE);
        startService(serviceIntent);

        TimeControl();
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            sensorService = ((SensorService.Mbinder)iBinder).getService();
            sensorService.setMsgListener(SensorActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    void TimeControl(){
        Timer mTimer = new Timer();
        TimerTask mTimerTask = new TimerTask() {//创建一个线程来执行run方法中的代码
            @Override
            public void run() {
                //要执行的代码
                finish();
            }
        };
        mTimer.schedule(mTimerTask, durationTime * 1000);
    }

    @Override
    public void getMsg(float acceleration) {
        dataView.updateView(acceleration);
        datalist.add(acceleration);
        valueZ.setText(acceleration+"");
    }

    public void onDestroy(){
        super.onDestroy();
        stopService(serviceIntent);
        unbindService(serviceConnection);
        Iterator iterator = datalist.iterator();
        while (iterator.hasNext()){
            float data = (float) iterator.next();
            databaseHelper.insertData(database,data);
        }
        databaseHelper.searchAll(database);
    }
}
