package com.example.bishe.cet4.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;
import com.example.bishe.cet4.function.MyTimerTask;
import com.example.bishe.cet4.function.MyToast;
import com.example.bishe.cet4.function.NetWork;
import com.example.bishe.cet4.object.UserData;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.util.Date;
import java.util.List;
import java.util.Timer;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class SyncActivity extends Activity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{
    private ImageView iv_back=null;
    private TextView tv_sync_time=null;
    private Button bt_sync=null;
    private String username=null;
    private SQLiteDatabase db=null;
    private DBHelper dbHelper=null;
    private Switch auto_sync=null;
    private Handler handler=null;
    private Thread thread=null;
    private ZLoadingDialog zLoadingDialog=null;
    private boolean checked;
    private String sync_time=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sync_activity_layout);
        //初始化数据库
        initDataBase();
        //初始化控件
        initViews();
        //初始化事件
        initEvents();
        //初始化数据
        initData();
    }


    private void initDataBase(){
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }

    private void initViews(){
        iv_back=findViewById(R.id.id_back);
        tv_sync_time=findViewById(R.id.id_sync_time);
        bt_sync=findViewById(R.id.id_bt_sync);
        auto_sync=findViewById(R.id.id_auto_sync);
        zLoadingDialog=new ZLoadingDialog(this);
        zLoadingDialog.setLoadingColor(Color.RED)
                .setHintText("Loading")
                .setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
                .setCancelable(false);
    }

    private void initEvents(){
        iv_back.setOnClickListener(this);
        bt_sync.setOnClickListener(this);
        auto_sync.setOnCheckedChangeListener(this);
    }

    private void initData(){
        Intent intent=getIntent();
        username=intent.getStringExtra("username");
        sync_time=intent.getStringExtra("sync_time");
        checked=intent.getBooleanExtra("checked",false);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        zLoadingDialog.cancel();
                        break;
                    case 1:
                        zLoadingDialog.cancel();
                        onResume();
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_sync_time.setText(sync_time);
        auto_sync.setChecked(checked);
    }

    private void sync(){
        BmobQuery<UserData> bmobQuery=new BmobQuery();
        bmobQuery.addWhereEqualTo("username",username);
        bmobQuery.findObjects(new FindListener<UserData>() {
            @Override
            public void done(List<UserData> list, BmobException e) {
                UserData userData=dbHelper.getUserData();
                userData.setUsername(username);
                SharedPreferences sharedPreferences=getSharedPreferences("plandays",MODE_PRIVATE);
                int plandays=sharedPreferences.getInt("plandays",-1);
                int plannum=sharedPreferences.getInt("plannum",-1);
                long begin_time=sharedPreferences.getLong("begin_time",-1);
                String previous_time=sharedPreferences.getString("previous_time",null);
                userData.setPlandays(plandays);
                userData.setPlannum(plannum);
                userData.setBegin_time(new BmobDate(new Date(begin_time)));
                userData.setPrevious_time(previous_time);
                if(list!=null&&list.size()>0){
                    UserData userData_pre=list.get(0);
                    if(userData.equals(userData_pre)){
                        BmobDate date=new BmobDate(new Date());
                        sync_time=date.getDate();
                        userData_pre.setSynctime(date);
                        userData_pre.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    new MyToast(SyncActivity.this,MyToast.SYNC_SUCCESS).show();
                                    handler.sendEmptyMessage(1);
                                }else{
                                    new MyToast(SyncActivity.this,MyToast.SYNC_ERROR).show();
                                    handler.sendEmptyMessage(0);
                                }
                            }
                        });
                    }else{
                        userData.setObjectId(userData_pre.getObjectId());
                        BmobDate date=new BmobDate(new Date());
                        sync_time=date.getDate();
                        userData_pre.setSynctime(date);
                        userData.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    new MyToast(SyncActivity.this,MyToast.SYNC_SUCCESS).show();
                                    handler.sendEmptyMessage(1);
                                }else{
                                    new MyToast(SyncActivity.this,MyToast.SYNC_ERROR).show();
                                    handler.sendEmptyMessage(0);
                                }
                            }
                        });
                    }
                }else{
                    BmobDate date=new BmobDate(new Date());
                    sync_time=date.getDate();
                    userData.setSynctime(date);
                    userData.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                new MyToast(SyncActivity.this,MyToast.SYNC_SUCCESS).show();
                                handler.sendEmptyMessage(1);
                            }else{
                                new MyToast(SyncActivity.this,MyToast.SYNC_ERROR).show();
                                handler.sendEmptyMessage(0);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_back:
                finish();
                break;
            case R.id.id_bt_sync:
                thread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sync();
                    }
                });
                thread.start();
                zLoadingDialog.show();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.id_auto_sync:
                Intent intent=new Intent("android.intent.action.syncService");
                intent.setPackage(getPackageName());
                if(isChecked){
                    startService(intent);
                }else{
                    stopService(intent);
                }
                break;
        }
    }
}
