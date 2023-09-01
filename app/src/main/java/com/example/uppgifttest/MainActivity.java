package com.example.uppgifttest;

//Maurice Uppgift 1

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private ImageView image;
    private FragmentManager fm;
    private MediaPlayer sound;
    private float imageRotatation = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView accText = findViewById(R.id.accText);
        TextView gyroText = findViewById(R.id.gyrotext);
        TextView brightnessText = findViewById(R.id.brightnessText);
        fm = getSupportFragmentManager();
        sound = MediaPlayer.create(this, R.raw.ayo);
        image = findViewById(R.id.image);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor acc = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Sensor gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        Sensor brightness = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                    float xAxisAcc = sensorEvent.values[0];
                    float yAxisAcc = sensorEvent.values[1];
                    float zAxisAcc = sensorEvent.values[2];

                    accText.setText(String.format(Locale.ROOT, "Accelerator values: \n\nX: %.2f\nY: %.2f\nZ:  %.2f", xAxisAcc, yAxisAcc, zAxisAcc));


                    float threshold = 5.0f;

                    if (Math.abs(xAxisAcc) > threshold || Math.abs(yAxisAcc) > threshold || Math.abs(zAxisAcc) > threshold) {
                        Toast.makeText(MainActivity.this, " Max threshold reached", Toast.LENGTH_SHORT).show();
                        sound.start();

                        fm.beginTransaction().add(R.id.fragmentContainerView, FirstFragment.class, null)
                                .commit();
                        findViewById(R.id.fragmentContainerView).setVisibility(View.VISIBLE);
                        Log.d("What", "is happening");
                    } else {
                        findViewById(R.id.fragmentContainerView).setVisibility(View.INVISIBLE);

                    }
                }

                if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE_UNCALIBRATED) {

                    float xAxisGyro = sensorEvent.values[0];
                    float yAxisGyro = sensorEvent.values[1];
                    float zAxisGyro = sensorEvent.values[2];

                    imageRotatation += zAxisGyro * 10;
                    image.setRotation(imageRotatation);

                    gyroText.setText(String.format(Locale.ROOT, "Gyrometer values: \n\nX: %.2f\nY: %.2f\nZ:  %.2f", xAxisGyro, yAxisGyro, zAxisGyro));
                }

                if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {

                    float brightness = sensorEvent.values[0];

                    if (brightness > 50) {
                        brightnessText.setText("Light");
                    } else {
                        brightnessText.setText("Dark");
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

        };
        sensorManager.registerListener(sensorEventListener, acc, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, gyro, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, brightness, SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(sensorEventListener);
    }
}