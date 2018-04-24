package com.example.bishe.cet4.function;

import java.util.Date;

public class WordTimer {
    private long begintime;
    private long endtime;
    private long duration;
    private Date date;

    public WordTimer(){
        this.begintime=0;
        this.endtime=0;
        this.duration=0;
    }

    public void startTimer(){
        this.date=new Date();
        this.begintime=date.getTime();
    }

    public void stopTimer(){
        this.date=new Date();
        this.endtime=date.getTime();
        this.duration=endtime-begintime;
    }


    public String getTime_string(){
        int seconds=(int)this.duration/1000%60;
        int minutes=(int)this.duration/1000/60%60;
        int hours=(int)this.duration/1000/60/60%60;
        return hours+":"+minutes+":"+seconds;
    }

    public long getTime_long(){
        return duration/1000;
    }

    public static long time_string_to_long(String time){
        String[] times=time.split(":");
        return Integer.valueOf(times[0])*60*60+Integer.valueOf(times[1])*60+Integer.valueOf(times[2]);
    }

    public static String time_long_to_string(long time){
        int seconds=(int)time%60;
        int minutes=(int)time/60%60;
        int hours=(int)time/60/60%60;
        return hours+":"+minutes+":"+seconds;
    }
}
