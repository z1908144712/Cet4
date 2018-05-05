package com.example.bishe.cet4.function;

import android.content.Context;
import android.widget.Toast;

public class MyToast{
    private Context context;
    private String text=null;
    private int type;
    private Toast myToast=null;
    public static final int UNKNOWN_ERROR=-1;
    public static final int NO_NETWORK=9016;
    public static final int ACCOUNT_ERROR=0;
    public static final int SYNC_SUCCESS=1;
    public static final int SYNC_ERROR=2;
    public static final int EMPTY=3;
    public static final int NAME_LENGTH =4;
    public static final int PASSWORD_LENGTH=5;
    public static final int PASSWORD_NOT_SAME=6;
    public static final int ACCOUNT_EXIST=7;
    public static final int UPDATE_PASSWORD_ERROR=8;
    public static final int PASSWORD_SAME=9;
    public static final int AUTO_SYNC_SUCCESS=10;
    public static final int AUTO_SYNC_ERROR=11;
    public static final int AUDIO_PLAY_ERROR=12;
    public static final int NUMBER_RANGE=13;
    public static final int NUMBER_SAME=14;


    public MyToast(Context context) {
        this.context=context;
    }

    public MyToast(Context context, String text) {
        this.context=context;
        this.text = text;
    }

    public MyToast(Context context,int type){
        this.context=context;
        switch (type){
            case ACCOUNT_ERROR:
                text="用户名或密码错误";
                break;
            case SYNC_SUCCESS:
                text="同步成功";
                break;
            case SYNC_ERROR:
                text="同步失败";
                break;
            case NO_NETWORK:
                text="无网络连接，请检查您的手机网络";
                break;
            case EMPTY:
                text="不能为空";
                break;
            case NAME_LENGTH:
                text="用户名的长度4~20";
                break;
            case PASSWORD_LENGTH:
                text="密码长度6~20";
                break;
            case PASSWORD_NOT_SAME:
                text="两次输入的密码不一致";
                break;
            case ACCOUNT_EXIST:
                text="该用户已存在";
                break;
            case UPDATE_PASSWORD_ERROR:
                text="修改密码失败";
                break;
            case PASSWORD_SAME:
                text="与原密码相同";
                break;
            case AUTO_SYNC_SUCCESS:
                text="自动同步成功";
                break;
            case AUTO_SYNC_ERROR:
                text="自动同步失败";
                break;
            case AUDIO_PLAY_ERROR:
                text="播放失败";
                break;
            case NUMBER_RANGE:
                text="数字应在20~60之间";
                break;
            case NUMBER_SAME:
                text="与原计划天数相同";
                break;
            default:
                text="未知错误";
                break;
        }
    }

    public void show() {
        myToast=Toast.makeText(context,text,Toast.LENGTH_SHORT);
        myToast.show();
    }
}
