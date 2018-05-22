package com.example.bishe.cet4.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.bishe.cet4.MainActivity;
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;
import com.example.bishe.cet4.function.MyTimerTask;
import com.example.bishe.cet4.function.MyToast;
import com.example.bishe.cet4.function.NetWork;
import com.example.bishe.cet4.object.UserData;
import com.example.bishe.cet4.object.Word;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class SyncService extends Service {
    private SQLiteDatabase db=null;
    private DBHelper dbHelper=null;
    private String username=null;
    private Timer timer=null;
    private Handler handler=null;
    private int skip_num=0;
    private List<Word> bmobWords=null;
    private List<Word> words=null;
    private List<Word> changeWords=null;
    private List<Word> addWords=null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("service start");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updateUserData();
                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bmobWords=new ArrayList<>();
                        getBmobWords();
                    }
                }).start();
            }
        },1000,60*60*1000);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateUserData(){
        BmobQuery<UserData> bmobQuery=new BmobQuery<>();
        bmobQuery.addWhereEqualTo("username",username);
        bmobQuery.findObjects(new FindListener<UserData>() {
            @Override
            public void done(List<UserData> list, BmobException e) {
                if(list!=null&&list.size()>0){
                    UserData userData=list.get(0);
                    UserData userData_now=dbHelper.getUserData();
                    userData_now.setUsername(username);
                    SharedPreferences sharedPreferences=getSharedPreferences("plandays",MODE_PRIVATE);
                    userData_now.setPlandays(sharedPreferences.getInt("plandays",-1));
                    userData_now.setPlannum(sharedPreferences.getInt("plannum",-1));
                    userData_now.setPrevious_time(sharedPreferences.getString("previous_time",null));
                    userData_now.setBegin_time(new BmobDate(new Date(sharedPreferences.getLong("begin_time",0))));
                    if(!userData_now.equals(userData)){
                        userData_now.setObjectId(userData.getObjectId());
                        userData_now.setSynctime(new BmobDate(new Date()));
                        userData_now.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    handler.sendEmptyMessage(1);
                                }else{
                                    handler.sendEmptyMessage(0);
                                }
                            }
                        });
                    }
                }else{
                    handler.sendEmptyMessage(e.getErrorCode());
                }
            }
        });
    }

    private void updateWords(){
        words=dbHelper.selectAllWords();
        String updateInfo="";
        checkChange();
        if(words.size()!=bmobWords.size()){
            checkAdd();
        }
        if(changeWords.size()>0||(addWords!=null&&addWords.size()>0)){
            if(changeWords.size()>0){
                updateInfo+="更新的单词：\n";
                for(int i=0;i<changeWords.size();i++){
                    updateInfo+=changeWords.get(i).getEnglish()+"\n";
                }
            }
            if(addWords!=null&&addWords.size()>0){
                updateInfo+="新增的单词：\n";
                for(int i=0;i<addWords.size();i++){
                    updateInfo+=addWords.get(i).getEnglish()+"\n";
                }
            }
            AlertDialog.Builder builder=new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("单词库更新")
                    .setMessage(updateInfo)
                    .setNegativeButton("取消",null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if(changeWords.size()>0){
                                        for(int i=0;i<changeWords.size();i++){
                                            dbHelper.updateWord(changeWords.get(i));
                                            dbHelper.updateTestQuestion(changeWords.get(i));
                                        }
                                    }
                                    if(addWords!=null&&addWords.size()>0){
                                        for(int i=0;i<addWords.size();i++){
                                            dbHelper.insertWord(addWords.get(i));
                                            dbHelper.insertWord(addWords.get(i));
                                            updateWordPlan();
                                        }
                                    }
                                }
                            }).start();
                        }
                    });
            AlertDialog alertDialog=builder.create();
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alertDialog.show();
        }
    }

    private void updateWordPlan(){
        int words_count=dbHelper.selectCountFromWords();
        SharedPreferences sharedPreferences=getSharedPreferences("plandays",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        int plandays=sharedPreferences.getInt("plandays",-1);
        int plannum=sharedPreferences.getInt("plannum",-1);
        int old_max_num=plandays*plannum;
        if (old_max_num<words_count){
            int days=words_count%plannum==0?words_count/plannum:(words_count/plannum)+1;
            editor.putInt("plandays",days);
            editor.commit();
        }
    }

    private void checkChange(){
        changeWords=new ArrayList<>();
        Word word1,word2;
        for(int i=0;i<words.size();i++){
            word1=words.get(i);
            for(int j=0;j<bmobWords.size();j++){
                word2=bmobWords.get(j);
                if(word1.getId().equals(word2.getId())){
                    if(!word1.equals(word2)){
                        changeWords.add(word2);
                    }
                    break;
                }

            }
        }
    }

    private void checkAdd(){
        addWords=new ArrayList<>();
        for(int i=0;i<bmobWords.size();i++){
            boolean isExist=false;
            for(int j=0;j<words.size();j++){
                if(bmobWords.get(i).getId().equals(words.get(j).getId())){
                    isExist=true;
                    break;
                }
            }
            if(!isExist){
                addWords.add(bmobWords.get(i));
            }
        }
    }

    private void getBmobWords(){
        final BmobQuery<Word> bmobQuery=new BmobQuery<>();
        bmobQuery.setLimit(1000);
        bmobQuery.setSkip(skip_num);
        bmobQuery.findObjects(new FindListener<Word>() {
            @Override
            public void done(List<Word> list, BmobException e) {
                if(e==null){
                    if(list!=null&&list.size()>0){
                        skip_num+=list.size();
                        for(int i=0;i<list.size();i++){
                            bmobWords.add(list.get(i));
                        }
                        getBmobWords();
                    }else{
                        handler.sendEmptyMessage(2);
                        return;
                    }
                }else {
                    System.out.println(e.getMessage());
                    new MyToast(getApplicationContext(),e.getErrorCode()).show();
                    return;
                }
            }
        });
    }

    @Override
    public void onCreate() {
        //初始化数据库
        initDataBase();
        //初始化数据
        initData();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        System.out.println("service stop");
        timer.cancel();
        super.onDestroy();
    }

    private void initDataBase(){
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }

    private void initData(){
        SharedPreferences sharedPreferences=getSharedPreferences("login",MODE_PRIVATE);
        username=sharedPreferences.getString("username",null);
        timer=new Timer();
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        new MyToast(getApplicationContext(),MyToast.AUTO_SYNC_ERROR).show();
                        break;
                    case 1:
                        new MyToast(getApplicationContext(),MyToast.AUTO_SYNC_SUCCESS).show();
                        break;
                    case 2:
                        updateWords();
                        break;
                    default:
                        new MyToast(getApplicationContext(),msg.what).show();
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

}
