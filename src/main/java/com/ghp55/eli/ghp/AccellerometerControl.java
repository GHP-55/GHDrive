package com.ghp55.eli.ghp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by elijn on 7/7/2018.
 */

public class AccellerometerControl implements SensorEventListener{
    Context ctx;
    PiInterface piInterface;
    SensorManager senSensorManager;
    Sensor senAccelerometer;
    public AccellerometerControl(Context c, PiInterface pi){
        this.ctx = c;
        this.piInterface = pi;
        senSensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent e) {
        float fwdBkd = e.values[1];
        float leftRight = e.values[0];
        //lr: -right : +left
        //ud: -down: +up
        //12.5 Fwd
        //7.5  Stop
        //2.5 Back
        double leftPwr = ((fwdBkd*11)-(leftRight*11));
        double rightPwr = ((fwdBkd*11)-(leftRight*-11));
        piInterface.setLeftMotor(leftPwr);
        piInterface.setRightMotor(rightPwr);
        //System.out.println(leftPwr+", "+rightPwr);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
