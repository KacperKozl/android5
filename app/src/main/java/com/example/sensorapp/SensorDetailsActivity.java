package com.example.sensorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class SensorDetailsActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView sensorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);
        sensorTextView=findViewById(R.id.sensor_label);
        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        int sensorType=getIntent().getIntExtra(SensorActivity.KEY_SENSOR_TYPE,-1);
        if(sensorType==Sensor.TYPE_LIGHT||sensorType==Sensor.TYPE_ACCELEROMETER)//Pixel_3a Accelerometr i sensor światła
            sensor=sensorManager.getDefaultSensor(sensorType);
        else{
            sensor=null;
            sensorTextView.setText(R.string.missing_sensor);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(sensor!=null)
            sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType=event.sensor.getType();
        float[] values=event.values;
        switch (sensorType){
            case Sensor.TYPE_LIGHT:
                sensorTextView.setText(getResources().getString(R.string.light_sensor_label,values[0]));
                break;
            case  Sensor.TYPE_ACCELEROMETER:
                sensorTextView.setText(getResources().getString(R.string.accelerometr_label,values[0],values[1],values[2]));
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("Sensor","Wywołano onAccuracyChanged");
    }
}