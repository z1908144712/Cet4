package com.example.bishe.cet4.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class AudioService extends Service {
    private MediaPlayer mediaPlayer=null;
    private String url="http://dict.youdao.com/dictvoice?audio=";
    private String query=null;
    private Uri location=null;

    @Override
    public void onCreate() {
        System.out.println("初始化音乐资源");
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if(query!=null&&query.equals("query")&&mediaPlayer!=null){
            mediaPlayer.start();
        }else{
            query=intent.getStringExtra("query");
            location=Uri.parse(url+query);
            mediaPlayer=MediaPlayer.create(this,location);
            mediaPlayer.start();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.release();
                System.out.println("音乐播放结束");
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mediaPlayer.release();
                System.out.println("播发发生错误");
                return false;
            }
        });
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
