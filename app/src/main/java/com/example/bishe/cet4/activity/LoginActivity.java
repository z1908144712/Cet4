package com.example.bishe.cet4.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.example.bishe.cet4.R;

public class LoginActivity extends Activity implements View.OnClickListener{
    private FloatingActionButton fab=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_layout);
        //初始化控件
        initViews();
        //初始化事件
        initEvents();
    }

    private void initViews(){
        fab=findViewById(R.id.fab);
    }

    private void initEvents(){
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                    ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(this,fab,fab.getTransitionName());
                    startActivity(new Intent(this,RegisterActivity.class),activityOptions.toBundle());
                }else{
                    startActivity(new Intent(this,RegisterActivity.class));
                }
                break;
        }
    }
}
