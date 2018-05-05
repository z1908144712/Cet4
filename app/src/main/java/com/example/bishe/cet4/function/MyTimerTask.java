package com.example.bishe.cet4.function;

import android.os.Handler;

import java.util.TimerTask;


public class MyTimerTask extends TimerTask {
    private Handler handler;
    private int time;

    public MyTimerTask(Handler handler, int time) {
        this.handler = handler;
        this.time = time;
    }

    @Override
    public void run() {
        handler.sendEmptyMessage(time);
    }
}
