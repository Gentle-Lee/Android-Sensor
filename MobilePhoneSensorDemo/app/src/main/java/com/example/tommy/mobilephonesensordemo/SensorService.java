package com.example.tommy.mobilephonesensordemo;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;


/**
 * Created by Tommy on 2017/9/30.
 */

public class SensorService extends Service implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor acceleratioinSensor,gryscopeSensor;
    private Mbinder mbinder = new Mbinder();
    private MsgListener msgListener;


    @Override
    public void onCreate(){
        super.onCreate();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        acceleratioinSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        sensorManager.registerListener(this,acceleratioinSensor,sensorManager.SENSOR_DELAY_GAME);
        gryscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this,gryscopeSensor,sensorManager.SENSOR_DELAY_GAME);
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        if (msgListener == null){
            return;
        }
        msgListener.getMsg(event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mbinder;
    }


    public void onDestory(){
        super.onDestroy();

    }



    public class Mbinder extends android.os.Binder{
        public SensorService getService(){
            return SensorService.this;
        }
    }

    public interface MsgListener{
        void getMsg(float acceleration);
    }

    void setMsgListener(MsgListener msgListener){
        this.msgListener = msgListener;
    }
}
