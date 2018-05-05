package com.example.bishe.cet4.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.function.MyTimerTask;
import com.example.bishe.cet4.function.MyToast;
import com.example.bishe.cet4.function.NetWork;
import com.example.bishe.cet4.object.User;

import java.util.Timer;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class LoadingUserInfoActivity extends Activity {
    private String user_id=null;
    private Handler handler=null;
    private User muser=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_loading_layout);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        Intent intent1=new Intent(LoadingUserInfoActivity.this,UserInfoActivity.class);
                        intent1.putExtra("user",muser);
                        startActivity(intent1);
                        finish();
                        break;
                }
                super.handleMessage(msg);
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                //初始化数据
                initData();
            }
        }).start();
    }


    private void initData(){
        Intent intent=getIntent();
        user_id=intent.getStringExtra("user_id");
        BmobQuery<User> bmobQuery=new BmobQuery<>();
        bmobQuery.getObject(user_id, new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if(e==null){
                    muser=user;
                    handler.sendEmptyMessage(1);
                }else{
                    new MyToast(LoadingUserInfoActivity.this,e.getErrorCode()).show();
                }
                finish();
            }
        });
    }
}
