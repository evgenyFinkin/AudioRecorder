package com.example.jonfi.audiorecorder;

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
import android.widget.Toast;
import android.support.v4.content.ContextCompat;

import java.io.IOException;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    Button buttRecord,buttStopRecording, buttStopPlaying, buttPlay;

    String AudioSavePathInDevice = null;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";


    MediaRecorder mMediaRecorder = new MediaRecorder();
    MediaPlayer mMediaPlayer ;

    public static final int RequestPermissionCode = 1986;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttRecord = (Button) findViewById(R.id.buttRecord);
        buttStopRecording = (Button) findViewById(R.id.buttStopRecording);
        buttStopRecording.setEnabled(false);
        buttStopPlaying = (Button) findViewById(R.id.buttStopPlaying);
        buttStopPlaying.setEnabled(false);
        buttPlay = (Button) findViewById(R.id.buttPlay);
        buttPlay.setEnabled(false);

        //random 64bit number
        Random random = new Random();

        buttRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission())   {
                    AudioSavePathInDevice=Environment
                            .getExternalStorageDirectory()
                            .getAbsolutePath()
                            +"/"+CreateRandomAudioFileName(5)+"AudioRecording.3gp";

                    try {
                        mMediaRecorder.prepare();
                        mMediaRecorder.start();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    buttRecord.setEnabled(false);
                    buttStopRecording.setEnabled(true);

                    Toast.makeText(MainActivity.this,"Recording started",Toast.LENGTH_LONG).show();
                }else   {
                    requestPermission();
                }
            }
        });

        buttStopRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaRecorder.stop();
                buttStopRecording.setEnabled(false);
                buttStopPlaying.setEnabled(false);
                buttPlay.setEnabled(true);
                buttRecord.setEnabled(true);

                Toast.makeText(MainActivity.this,"Recording Completed",Toast.LENGTH_LONG).show();
            }
        });

        buttPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {

                buttRecord.setEnabled(false);
                buttStopRecording.setEnabled(false);
                buttStopPlaying.setEnabled(true);

                mMediaPlayer=new MediaPlayer();

                try {
                    mMediaPlayer.setDataSource(AudioSavePathInDevice);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(MainActivity.this,"Recording Playing",Toast.LENGTH_LONG).show();
            }
        });

        buttStopPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                buttStopRecording.setEnabled(false);
                buttStopPlaying.setEnabled(false);
                buttRecord.setEnabled(true);
                buttPlay.setEnabled(true);

                if(mMediaPlayer!=null)    {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    MediaRecorderReady();
                }
            }
        });

    }

    private boolean checkPermission() {
        int writeExternalStoragePermission=ContextCompat
                .checkSelfPermission(getApplicationContext(),WRITE_EXTERNAL_STORAGE);
        int recordAudioPermission=ContextCompat
                .checkSelfPermission(getApplicationContext(),RECORD_AUDIO);
        return (writeExternalStoragePermission==PackageManager.PERMISSION_GRANTED
                &&recordAudioPermission==PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO},
                RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)   {
            case RequestPermissionCode:
                boolean StoragePermission=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                boolean RecordPermission=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                break;
        }
    }

    private String CreateRandomAudioFileName(int i) {
        StringBuilder stringBuilder=new StringBuilder(i);
        Random random=new Random();
        while(i>0) {
            stringBuilder.append(RandomAudioFileName.charAt(random.nextInt(RandomAudioFileName.length())));
            i--;
        }
        return stringBuilder.toString();
    }

    private void MediaRecorderReady() {
        mMediaRecorder=new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mMediaRecorder.setOutputFile(AudioSavePathInDevice);
    }
}
