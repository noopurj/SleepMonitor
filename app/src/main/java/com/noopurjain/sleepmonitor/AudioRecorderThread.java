package com.noopurjain.sleepmonitor;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by noopurjain on 19/09/16.
 */
public class AudioRecorderThread extends Thread {
    AudioRecord record;
    private int sampleRate = 16000;
    private static final String LOG_TAG = "AudioRecorderThread";
    volatile boolean isRunning = true;

    @Override
    public void run() {
        int minBytes = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        if (minBytes == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(LOG_TAG, "AudioRecorderThread::run(): Invalid AudioRecord parameter.\n");
            minBytes = sampleRate * 2;
        }
        short[] audioBuffer = new short[minBytes / 2];

        AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBytes);

        if (record.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(LOG_TAG, "Audio Record can't initialize!");
            return;
        }

        WavWriter wavWriter = new WavWriter(sampleRate);
        wavWriter.start();
        Log.i(LOG_TAG, "PCM write to file " + wavWriter.getPath());

        record.startRecording();

        Log.v(LOG_TAG, "Start recording");

        long shortsRead = 0;
        while (isRunning){
            int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
            shortsRead += numberOfShort;
            wavWriter.pushAudioShort(audioBuffer, numberOfShort);
        }

        Log.i(LOG_TAG, "Stopped recording, shorts read: " + shortsRead);
        record.stop();
        record.release();
        wavWriter.stop();
    }

    public void finish() {
        isRunning = false;
        interrupt();
    }
}