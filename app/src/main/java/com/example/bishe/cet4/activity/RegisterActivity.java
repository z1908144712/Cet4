package com.example.bishe.cet4.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.function.MyToast;
import com.example.bishe.cet4.object.MD5;
import com.example.bishe.cet4.object.User;

import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends Activity implements View.OnClickListener{
    private EditText et_username=null;
    private EditText et_password=null;
    private EditText et_repeatpassword=null;
    private Button bt_register=null;
    private ImageView iv_back=null;
    private String username=null;
    private String password=null;
    private String repeatpassword=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity_layout);
        //初始化控件
        initViews();
        //初始化事件
        initEvents();
    }

    private void initViews(){
        iv_back=findViewById(R.id.id_back);
        bt_register=findViewById(R.id.id_bt_register);
        et_username=findViewById(R.id.id_et_username);
        et_password=findViewById(R.id.id_et_password);
        et_repeatpassword=findViewById(R.id.id_et_repeatpassword);
    }

    private void initEvents(){
        bt_register.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    private boolean check_input(){
        username=et_username.getText().toString().trim();
        password=et_password.getText().toString().trim();
        repeatpassword=et_repeatpassword.getText().toString().trim();
        if(username.equals("")||password.equals("")||password.equals("")){
            new MyToast(this,MyToast.EMPTY).show();
            return false;
        }else{
            if(username.length()>=4&&username.length()<=20){
                if(password.equals(repeatpassword)){
                    if(password.length()>=6&&password.length()<=20){
                        return true;
                    }else{
                        new MyToast(this,MyToast.PASSWORD_LENGTH).show();
                        return false;
                    }
                }else{
                    new MyToast(this,MyToast.PASSWORD_NOT_SAME).show();
                    return false;
                }
            }else{
                new MyToast(this,MyToast.NAME_LENGTH).show();
                return false;
            }

        }
    }

    private void register(){
        final User user=new User();
        user.setUsername(username);
        MD5 md5=new MD5(password);
        user.setPassword(md5.getMD5());
        user.setCreate_time(new BmobDate(new Date()));
        user.setLogin_time(new BmobDate(new Date(0)));
        BmobQuery<User> bmobQuery=new BmobQuery<User>();
        bmobQuery.addWhereEqualTo("username",username);
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(list!=null&&list.size()>0){
                    new MyToast(RegisterActivity.this,MyToast.ACCOUNT_EXIST).show();
                }else{
                    user.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                new AlertDialog.Builder(RegisterActivity.this)
                                        .setMessage("注册成功！")
                                        .setCancelable(false)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        })
                                        .create().show();
                            }else {
                                new MyToast(RegisterActivity.this,e.getErrorCode()).show();
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
            case R.id.id_bt_register:
                if(check_input()){
                    register();
                }
                break;
            case R.id.id_back:
                finish();
                break;
        }
    }

}
