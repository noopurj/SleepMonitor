package com.noopurjain.sleepmonitor;

import android.app.Activity;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends Activity implements SensorEventListener, View.OnClickListener{


    private Button btnStart, btnStop;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAudioFileName();

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
                onRecord(true);
                break;
            case R.id.sleep_stop:
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                onRecord(false);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onSensorChanged(SensorEvent event){

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }
}
