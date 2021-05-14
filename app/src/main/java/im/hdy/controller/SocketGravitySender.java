package im.hdy.controller;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SocketGravitySender implements Runnable{


    private String ip;
    private int code;
    private Socket socket;
    private OutputStream outputStream;
    private double mGravity = 5d;
    private SensorManager sensorManager;

    private Handler mHandler;
    public SocketGravitySender(SensorManager sensorManager,String ip, int code) {
        this.sensorManager = sensorManager;
        this.ip = ip;
        this.code = code;
    }


    @Override
    public void run() {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Sensor sensor_accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Looper.prepare();
        mHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.obj == null){
                    msg.obj = 0.0f;
                }
                send(msg.what,(float) msg.obj);
            }
        };
        sensorManager.registerListener(new SensorEventListener(){

            @Override
            public void onSensorChanged(SensorEvent event) {
                //重力传感器
                Log.d("linc", "value size: " + event.values.length);
                float xValue = event.values[0];// Acceleration minus Gx on the x-axis
                float yValue = event.values[1];//Acceleration minus Gy on the y-axis
                float zValue = event.values[2];//Acceleration minus Gz on the z-axis
                Message msg = new Message();
                if (xValue > mGravity) {
                    msg.what = 0x1;
                    msg.obj = xValue;
                } else if (xValue < -mGravity) {
                    msg.what = 0x2;
                    msg.obj = xValue;
                } else if (yValue > mGravity) {
                    msg.what = 0x3;
                    msg.obj = yValue;
                } else if (yValue < -mGravity) {
                    msg.what = 0x4;
                    msg.obj = yValue;
                } else if (zValue > mGravity) {
                    msg.what = 0x5;
                    msg.obj = zValue;
                } else if (zValue < -mGravity) {
                    msg.what = 0x6;
                    msg.obj = zValue;
                }
                mHandler.sendMessage(msg);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        },sensor_accelerometer,SensorManager.SENSOR_DELAY_UI);
        while (true){Looper.loop();}
    }


    private void init() throws IOException {
        socket = new Socket(ip,code);
        outputStream = socket.getOutputStream();
    }


    public void send(int type,float speed){
        int num = 0;
        num =  (int) Math.ceil(speed);
        StringBuffer sb = new StringBuffer();
        sb.append(type);
        if (num >= 10){
            num = 9;
        } else if(num <= -10){
            num = -9;
        }
        sb.append(num-1);
        Log.i("socketSend",sb.toString());
        try {
            if (outputStream!=null) {
                sb.append("\n");
                outputStream.write(sb.toString().getBytes());
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void destory(){
        if (socket != null){
            try {
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}