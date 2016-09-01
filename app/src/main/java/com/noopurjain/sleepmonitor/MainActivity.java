package com.noopurjain.sleepmonitor;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity implements SensorEventListener, View.OnClickListener{


    private Button btnStart, btnStop;
    private SensorManager sensorManager;
    private ArrayList<AccelData> sensorData;
    private boolean started = false;

    MediaRecorder audioRecorder = null;
    private static String audioFileName = null;
    private static final String LOG_TAG = "AudioRecording";

    private void onRecord(boolean start){
        if (start) {
            startRecording();
        }
        else {
            stopRecording();
        }
    }

    private void setAudioFileName(){
        audioFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        audioFileName += "/audiorecordtest.3gp";
    }

    private void startRecording() {
        audioRecorder = new MediaRecorder();
        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        audioRecorder.setOutputFile(audioFileName);
        audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            audioRecorder.prepare();
        } catch(IOException e) {
            Log.e(LOG_TAG, "prepare() FAILED");
        }

        audioRecorder.start();
    }

    private void stopRecording() {
        audioRecorder.stop();
        audioRecorder.release();
        audioRecorder = null;
    }

    private void saveData() {
        File root = new File( Environment.getExternalStorageDirectory().getAbsolutePath());
        File file = new File(root, "AccelData.txt");
        int i = 0;

        try {
            FileWriter writer = new FileWriter(file);
            while(sensorData.get(i) != null) {
                writer.append(sensorData.get(i).toString());
                i++;
            }
            writer.flush();
            writer.close();
        } catch(IOException e){

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAudioFileName();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorData = new ArrayList<AccelData>();

        btnStart = (Button) findViewById(R.id.sleep_start);
        btnStop = (Button) findViewById(R.id.sleep_stop);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
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
                Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);

                // Audio recording
                onRecord(true);
                break;
            case R.id.sleep_stop:
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);

                started = false;
                sensorManager.unregisterListener(this);
                saveData();

                onRecord(false);
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
