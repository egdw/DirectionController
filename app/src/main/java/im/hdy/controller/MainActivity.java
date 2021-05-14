package im.hdy.controller;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private GravityLisntener lisntener;
    private TextView ftext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ftext = findViewById(R.id.mtext);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lisntener = new GravityLisntener(ftext);
        new Thread(new SocketGravitySender(sensorManager, "192.168.123.27", 8889)).start();
    }

    protected void onResume() {
        Sensor sensor_accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(lisntener, sensor_accelerometer, SensorManager.SENSOR_DELAY_UI);
        super.onResume();

    }

    protected void onPause() {
        sensorManager.unregisterListener(lisntener);
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}