package com.example.bishe.cet4.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.function.MyToast;
import com.example.bishe.cet4.function.NetWork;
import com.example.bishe.cet4.object.MD5;
import com.example.bishe.cet4.object.User;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class UpdateUserPasswordActivity extends Activity implements View.OnClickListener {
    private ImageView iv_back=null;
    private EditText et_new_password=null;
    private EditText et_repeat_password=null;
    private Button bt_update=null;
    private String new_password=null;
    private String repeat_password=null;
    private ZLoadingDialog zLoadingDialog=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_user_password_activity_layout);
        //初始化控件
        initViews();
        //初始化事件
        initEvents();
    }

    private void initViews(){
        iv_back=findViewById(R.id.id_back);
        et_new_password=findViewById(R.id.id_new_password);
        et_repeat_password=findViewById(R.id.id_repeat_password);
        bt_update=findViewById(R.id.id_update);
        zLoadingDialog=new ZLoadingDialog(this);
        zLoadingDialog.setCancelable(false)
                .setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
                .setLoadingColor(Color.RED)
                .setHintText("Loading");
    }

    private void initEvents(){
        iv_back.setOnClickListener(this);
        bt_update.setOnClickListener(this);
    }

    private boolean check_input(){
        new_password=et_new_password.getText().toString().trim();
        repeat_password=et_repeat_password.getText().toString().trim();
        if(new_password.equals("")||repeat_password.equals("")){
            new MyToast(UpdateUserPasswordActivity.this,MyToast.EMPTY).show();
            return false;
        }else{
            if(new_password.equals(repeat_password)){
                if(new_password.length()>=6&&new_password.length()<=20){
                    return true;
                }else{
                    new MyToast(UpdateUserPasswordActivity.this,MyToast.PASSWORD_LENGTH).show();
                    return false;
                }
            }else{
                new MyToast(UpdateUserPasswordActivity.this,MyToast.PASSWORD_NOT_SAME).show();
                return false;
            }
        }
    }

    private void update(){
        Intent intent=getIntent();
        String user_id=intent.getStringExtra("user_id");
        BmobQuery<User> bmobQuery=new BmobQuery<>();
        bmobQuery.getObject(user_id, new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if(e==null){
                    if(user.getPassword().equals(new MD5(new_password).getMD5())){
                        zLoadingDialog.cancel();
                        new MyToast(UpdateUserPasswordActivity.this,MyToast.PASSWORD_SAME).show();
                    }else{
                        user.setPassword(new MD5(new_password).getMD5());
                        user.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                zLoadingDialog.cancel();
                                if(e==null){
                                    new AlertDialog.Builder(UpdateUserPasswordActivity.this)
                                            .setMessage("修改密码成功！")
                                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            })
                                            .setCancelable(false)
                                            .create().show();
                                }else{
                                    new MyToast(UpdateUserPasswordActivity.this,MyToast.UPDATE_PASSWORD_ERROR).show();
                                    finish();
                                }
                            }
                        });
                    }
                }else{
                    new MyToast(UpdateUserPasswordActivity.this,e.getErrorCode()).show();
                    finish();
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
            case R.id.id_update:
                if(check_input()){
                    zLoadingDialog.show();
                    update();
                }
                break;
        }
    }
}
