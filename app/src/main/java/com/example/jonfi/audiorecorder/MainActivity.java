package com.example.jonfi.audiorecorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;


import java.io.File;
import java.io.IOException;

import static android.os.Environment.getExternalStoragePublicDirectory;


public class MainActivity extends AppCompatActivity {

    Switch swiRecord, swiPlay;
    Button buttReadFile;
    TextView test;

    String mFileName = null;
    File file;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();
    }

    MediaRecorder mMediaRecorder = new MediaRecorder();
    MediaPlayer mMediaPlayer ;

    public static final int REQUEST_RECORD_AUDIO_PERMISSION = 1986;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        swiRecord=(Switch)findViewById(R.id.swiRecord);
        swiPlay=(Switch)findViewById(R.id.swiPlay);
        buttReadFile=(Button)findViewById(R.id.buttReadFile);

        test=(TextView)findViewById(R.id.test);
        test.setText("off");




        if(isExternalStorageWritable())    {
            mFileName=getExternalFilesDir(Environment.DIRECTORY_MUSIC)
                    .getAbsolutePath();
        }else {
            mFileName = getExternalCacheDir().getAbsolutePath();
        }

        mFileName += "/audiorecordtest.3gp";
        test.setText(mFileName);

        swiRecord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)    {
                    test.setText("on");

                    MediaRecorderReady();

                    try {
                        mMediaRecorder.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mMediaRecorder.start();
                }else   {
                    test.setText("off");

                    mMediaRecorder.stop();
                    mMediaRecorder.release();
                    mMediaRecorder=null;
                }
            }
        });

        swiPlay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)   {

                    mMediaPlayer=new MediaPlayer();
                    try {
                        mMediaPlayer.setDataSource(mFileName);
                        mMediaPlayer.prepare();
                        mMediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else   {
                    mMediaPlayer.release();
                    mMediaPlayer=null;
                }
            }
        });

        buttReadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    private void MediaRecorderReady() {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mMediaRecorder.setOutputFile(mFileName);
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

}
