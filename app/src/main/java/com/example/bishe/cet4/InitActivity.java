package com.example.bishe.cet4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.bishe.cet4.database.AssetsDatabaseManager;

/**
 * Created by Skywilling on 2018/1/10.
 */

public class InitActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //初始化数据库
        initDatabase();
        //初始化数据
        initData();
        //跳转到主界面
        Intent intent=new Intent();
        intent.setClass(this,MainActivity.class);
        startActivity(intent);
        finish();
        super.onCreate(savedInstanceState);
    }
    private void initData(){

    }
    private void initDatabase(){
        AssetsDatabaseManager.initManager(getApplicationContext());
    }
}
