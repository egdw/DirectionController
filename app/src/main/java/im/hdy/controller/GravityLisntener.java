package im.hdy.controller;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class GravityLisntener implements SensorEventListener {
    private TextView tv_orientation;
    private double mGravity = 3d;
    private SocketTools socketTools;
    private Handler msgHandler;


    private Handler socketHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    socketTools =  new SocketTools("192.168.1.2",8889);
                    msgHandler = socketTools.getHandler();
                }
            }).start();
        }
    };

    public GravityLisntener(TextView textView) {
        this.tv_orientation = textView;
//        socketHandler.sendEmptyMessage(0x1);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //重力传感器
        Log.d("linc", "value size: " + event.values.length);
        float xValue = event.values[0];// Acceleration minus Gx on the x-axis
        float yValue = event.values[1];//Acceleration minus Gy on the y-axis
        float zValue = event.values[2];//Acceleration minus Gz on the z-axis
        tv_orientation.setText("x轴： " + xValue + "  y轴： " + yValue + "  z轴： " + zValue);
        if (xValue > mGravity) {
            tv_orientation.append("\n重力指向设备左边");
        } else if (xValue < -mGravity) {
            tv_orientation.append("\n重力指向设备右边");
        } else if (yValue > mGravity) {
            tv_orientation.append("\n重力指向设备下边");
        } else if (yValue < -mGravity) {
            tv_orientation.append("\n重力指向设备上边");
        } else if (zValue > mGravity) {
            tv_orientation.append("\n屏幕朝上");
        } else if (zValue < -mGravity) {
            tv_orientation.append("\n屏幕朝下");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}