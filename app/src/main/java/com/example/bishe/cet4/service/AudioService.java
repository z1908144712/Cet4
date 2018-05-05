package com.example.bishe.cet4.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.bishe.cet4.function.MyToast;
import com.example.bishe.cet4.function.NetWork;

public class AudioService extends Service {
    private MediaPlayer mediaPlayer=null;
    private String url="http://dict.youdao.com/dictvoice?audio=";
    private String query=null;
    private Uri location=null;
    private Handler handler=null;

    @Override
    public void onCreate() {
        super.onCreate();
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        new MyToast(getApplicationContext(),MyToast.AUDIO_PLAY_ERROR).show();
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void onStart(final Intent intent, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!NetWork.isNetworkConnected(getApplicationContext())){
                    handler.sendEmptyMessage(1);
                    return;
                }
                AudioManager audioManager= (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),AudioManager.FLAG_PLAY_SOUND);
                if(query!=null&&query.equals("query")&&mediaPlayer!=null){
                    mediaPlayer.start();
                }else{
                    query=intent.getStringExtra("query");
                    location=Uri.parse(url+query);
                    mediaPlayer=MediaPlayer.create(AudioService.this,location);
                    if(mediaPlayer!=null){
                        mediaPlayer.start();
                    }else{
                        handler.sendEmptyMessage(1);
                    }

                }
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer.release();
                    }
                });
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        handler.sendEmptyMessage(1);
                        mediaPlayer.release();
                        return false;
                    }
                });
            }
        }).start();
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer=null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
