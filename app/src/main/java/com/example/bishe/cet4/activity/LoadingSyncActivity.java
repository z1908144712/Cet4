package com.example.bishe.cet4.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;
import com.example.bishe.cet4.function.MyTimerTask;
import com.example.bishe.cet4.function.MyToast;
import com.example.bishe.cet4.function.NetWork;
import com.example.bishe.cet4.object.UserData;

import java.util.List;
import java.util.Timer;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class LoadingSyncActivity extends Activity {
    private String username=null;
    private SQLiteDatabase db=null;
    private DBHelper dbHelper=null;
    private String sync_time=null;
    private boolean checked;
    private Handler handler=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_loading_layout);
        init();
    }

    private void init(){
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        Intent intent=new Intent(LoadingSyncActivity.this,SyncActivity.class);
                        intent.putExtra("sync_time",sync_time);
                        intent.putExtra("checked",checked);
                        intent.putExtra("username",username);
                        startActivity(intent);
                        finish();
                        break;
                }
                super.handleMessage(msg);
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                //初始化数据库
                initDataBase();
                //初始化数据
                initData();
            }
        }).start();
    }


    private void initDataBase(){
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }

    private void initData(){
        SharedPreferences sharedPreferences=getSharedPreferences("login",MODE_PRIVATE);
        username=sharedPreferences.getString("username",null);
        BmobQuery<UserData> bmobQuery=new BmobQuery();
        bmobQuery.addWhereEqualTo("username",username);
        bmobQuery.findObjects(new FindListener<UserData>() {
            @Override
            public void done(List<UserData> list, BmobException e) {
                if(list!=null&&list.size()>0){
                    UserData userData=list.get(0);
                    sync_time=userData.getSynctime().getDate();
                }else{
                    sync_time="还未同步过";
                    new MyToast(LoadingSyncActivity.this,e.getErrorCode()).show();
                }
                handler.sendEmptyMessage(1);
            }
        });
        if(isSyncServiceRunning("com.example.bishe.cet4.service.SyncService")){
            checked=true;
        }else{
            checked=false;
        }
    }

    private boolean isSyncServiceRunning(String serviceName){
        ActivityManager activityManager= (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningService=activityManager.getRunningServices(100);
        for(int i=0;i<runningService.size();i++){
            if(runningService.get(i).service.getClassName().equals(serviceName)){
                return true;
            }
        }
        return false;
    }
}
