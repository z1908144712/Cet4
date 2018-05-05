package com.example.bishe.cet4;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;
import com.example.bishe.cet4.object.AppKey;
import com.youdao.sdk.app.YouDaoApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.Bmob;

/**
 * Created by Skywilling on 2018/1/10.
 */

public class InitActivity extends Activity{

    private SQLiteDatabase db=null;
    private DBHelper dbHelper=null;
    private boolean isFirst=false;
    private Handler handler=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_activity_layout);
        init();
    }

    private void init(){
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        jump();
                        break;
                }
                super.handleMessage(msg);
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                //初始化数据库
                initDatabase();
                isFirst=getIntent().getBooleanExtra("isFirst",false);
                //初始化计划
                initPlan();
                handler.sendEmptyMessage(1);
            }
        }).start();
    }

    private void initDatabase(){
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }

    private void jump(){
        //跳转到主界面
        Intent intent=new Intent();
        intent.setClass(InitActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void restartApplication() {
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void setFirstUse(){
        SharedPreferences sharedPreferences=getSharedPreferences("isFirst", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("isFirst",true);
        editor.commit();
    }

   private void initPlan(){
        SharedPreferences sharedPreferences=getSharedPreferences("plandays",Context.MODE_PRIVATE);
        int days=sharedPreferences.getInt("plandays",-1);
        int num=sharedPreferences.getInt("plannum",-1);
        long begin_time=sharedPreferences.getLong("begin_time",-1);
        String previous_time=sharedPreferences.getString("previous_time",null);
        if(days==-1||num==-1||null==previous_time||begin_time==-1){
            setFirstUse();
            restartApplication();
        }else{
            Date date_now=new Date();
            Date date_previous=null;
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            try {
                date_now=simpleDateFormat.parse(simpleDateFormat.format(date_now));
                date_previous=simpleDateFormat.parse(previous_time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long during_time=date_now.getTime()-date_previous.getTime();
            if(during_time>0||isFirst){
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("previous_time",simpleDateFormat.format(date_now));
                editor.putInt("word_index",0);
                editor.commit();
                String all_learned_words_str=dbHelper.selectAllLearnWordNum();
                List<Integer> res;
                if(("").equals(all_learned_words_str.trim())){
                    res=random_num(num,null);
                }else{
                    res=random_num(num,all_learned_words_str.trim());
                }
                String res_str="";
                for(int i=0;i<res.size();i++){
                    res_str+=res.get(i)+",";
                }
                dbHelper.insertPlanWords(simpleDateFormat.format(date_now),res_str);
            }
        }
    }

    private List<Integer> random_num(int num,String exp){
        List<Integer> res=new ArrayList();
        Random random=new Random();
        if(null==exp){
            exp="";
        }
        int temp=0;
        String temp_str="";
        for(int i=0;i<num;i++){
            temp=random.nextInt(dbHelper.selectCountFromWords());
            temp_str=String.valueOf(temp);
            if(!exp.contains(temp_str)){
                res.add(temp);
            }else{
                i--;
            }
            exp+=temp_str+",";
        }
        return res;
    }
}
