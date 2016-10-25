package com.noopurjain.sleepmonitor;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity implements SensorEventListener, View.OnClickListener{


    private Button btnStart, btnStop;
    private SensorManager sensorManager;
    private ArrayList<AccelData> sensorData;
    private boolean started = false;
    private final String relativeDirectory = "/SleepMonitor/Accel";

    private AudioRecorderThread samplingThread;

    private static final String LOG_TAG = "MainActivity";

    // Accelerometer data
    private void saveData() {
        File path = new File(Environment.getExternalStorageDirectory().getPath() + relativeDirectory);
        path.mkdirs();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH'h'mm'm'ss.SSS's'");
        String nowStr = df.format(new Date());
        File outPath = new File(path, "accel" + nowStr + ".txt");
        int i = 0;

        try {
            FileWriter writer = new FileWriter(outPath);
            writer.append("t, x, y, z\n");
            while(i < sensorData.size()) {
                writer.append(sensorData.get(i).getTimestamp() + ", " + sensorData.get(i).getX() + ", "
                        + sensorData.get(i).getY() + ", " + sensorData.get(i).getZ() + "\n");
                i++;
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Log.w(LOG_TAG, "start(): Error writing " + outPath, e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Activity Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorData = new ArrayList<AccelData>();

        btnStart = (Button) findViewById(R.id.sleep_start);
        btnStop = (Button) findViewById(R.id.sleep_stop);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);

        samplingThread = new AudioRecorderThread();
    }

    @Override
    public void onClick(View v){
        switch(v.getId()) {
            case R.id.sleep_start:
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);

                // Accelerometer data
                sensorData = new ArrayList();
                started = true;
                Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
                sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);

                // Audio
                samplingThread.start();
                break;
            case R.id.sleep_stop:
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);

                started = false;
                sensorManager.unregisterListener(this);
                saveData();

                samplingThread.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onPause(){ super.onPause(); }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(started) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            long timestamp = System.currentTimeMillis();
            AccelData data = new AccelData(timestamp, x, y, z);
            sensorData.add(data);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }
}