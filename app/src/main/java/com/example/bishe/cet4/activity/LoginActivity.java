package com.example.bishe.cet4.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;
import com.example.bishe.cet4.function.MyTimerTask;
import com.example.bishe.cet4.function.MyToast;
import com.example.bishe.cet4.function.NetWork;
import com.example.bishe.cet4.object.MD5;
import com.example.bishe.cet4.object.User;
import com.example.bishe.cet4.object.UserData;
import com.example.bishe.cet4.object.WordCollection;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class LoginActivity extends Activity implements View.OnClickListener{
    private ImageView iv_back=null;
    private TextView tv_register=null;
    private EditText et_username=null;
    private EditText et_password=null;
    private Button bt_login=null;
    private String username=null;
    private String password=null;
    private SQLiteDatabase db=null;
    private DBHelper dbHelper=null;
    private ZLoadingDialog zLoadingDialog=null;
    private Handler handler=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_layout);
        //初始化数据库
        initDataBase();
        //初始化控件
        initViews();
        //初始化事件
        initEvents();
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        zLoadingDialog.cancel();
                        break;
                    case 1:
                        zLoadingDialog.cancel();
                        Intent intent=new Intent("android.intent.action.syncService");
                        intent.setPackage(getPackageName());
                        startService(intent);
                        break;
                    case 2:
                        zLoadingDialog.cancel();
                        Intent intent1=getPackageManager().getLaunchIntentForPackage(getPackageName());
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent1);
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    private void initDataBase(){
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }

    private void initViews(){
        iv_back=findViewById(R.id.id_back);
        tv_register=findViewById(R.id.id_tv_register);
        et_username=findViewById(R.id.id_et_username);
        et_password=findViewById(R.id.id_et_password);
        bt_login=findViewById(R.id.id_bt_login);
        zLoadingDialog=new ZLoadingDialog(LoginActivity.this);
        zLoadingDialog
                .setCancelable(false)
                .setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
                .setLoadingColor(Color.RED)
                .setHintText("Loading");
    }

    private void initEvents(){
        iv_back.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        bt_login.setOnClickListener(this);
    }

    private boolean check_input(){
        username=et_username.getText().toString().trim();
        password=et_password.getText().toString().trim();
        if(username.equals("")||password.equals("")){
            new MyToast(LoginActivity.this,MyToast.EMPTY).show();
            return false;
        }else{
            return true;
        }
    }

    private void login(){
        final BmobQuery<User> bmobQuery=new BmobQuery<User>();
        bmobQuery.addWhereEqualTo("username",username);
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null&&list.size()==1){
                    final User user=list.get(0);
                    if(new MD5(password).getMD5().equals(user.getPassword())){
                        final Date date=new Date();
                        user.setValue("login_time",new BmobDate(date));
                        user.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    final SharedPreferences sharedPreferences=getSharedPreferences("login",MODE_PRIVATE);
                                    final SharedPreferences.Editor editor=sharedPreferences.edit();
                                    editor.putBoolean("isLogin",true);
                                    editor.putString("user_id",user.getObjectId());
                                    editor.putString("username",username);
                                    editor.putLong("login_time",date.getTime());
                                    editor.commit();
                                    BmobQuery<UserData> bmobQuery1=new BmobQuery<>();
                                    bmobQuery1.addWhereEqualTo("username",username);
                                    bmobQuery1.findObjects(new FindListener<UserData>() {
                                        @Override
                                        public void done(List<UserData> list, BmobException e) {
                                            final UserData userData_now=dbHelper.getUserData();
                                            userData_now.setUsername(username);
                                            final SharedPreferences sharedPreferences1=getSharedPreferences("plandays",MODE_PRIVATE);
                                            userData_now.setPlandays(sharedPreferences1.getInt("plandays",-1));
                                            userData_now.setPlannum(sharedPreferences1.getInt("plannum",-1));
                                            userData_now.setPrevious_time(sharedPreferences1.getString("previous_time",null));
                                            userData_now.setBegin_time(new BmobDate(new Date(sharedPreferences1.getLong("begin_time",-1))));
                                            if(list!=null&&list.size()>0){
                                                final UserData userData=list.get(0);
                                                if(userData.equals(userData_now)){
                                                    userData.setSynctime(new BmobDate(new Date()));
                                                    userData.update(new UpdateListener() {
                                                        @Override
                                                        public void done(BmobException e) {
                                                            if(e==null){
                                                                handler.sendEmptyMessage(1);
                                                            }else{
                                                                handler.sendEmptyMessage(0);
                                                                new MyToast(LoginActivity.this,MyToast.SYNC_ERROR).show();
                                                            }
                                                            finish();
                                                        }
                                                    });
                                                }else{
                                                    new AlertDialog.Builder(LoginActivity.this)
                                                            .setMessage("检测到您有数据可以同步，是否同步到本地？点击取消即覆盖原数据！")
                                                            .setCancelable(false)
                                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    zLoadingDialog.show();
                                                                    userData_now.setSynctime(new BmobDate(new Date()));
                                                                    userData_now.update(new UpdateListener() {
                                                                        @Override
                                                                        public void done(BmobException e) {
                                                                            if(e==null){
                                                                                handler.sendEmptyMessage(1);
                                                                                new MyToast(LoginActivity.this,MyToast.SYNC_SUCCESS).show();
                                                                            }else{
                                                                                handler.sendEmptyMessage(0);
                                                                                new MyToast(LoginActivity.this,MyToast.SYNC_ERROR).show();
                                                                            }
                                                                            finish();
                                                                        }
                                                                    });
                                                                }
                                                            })
                                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    zLoadingDialog.show();
                                                                    SharedPreferences.Editor editor1=sharedPreferences1.edit();
                                                                    if(userData.getPlandays()!=userData_now.getPlandays()||userData.getBegin_time()!=userData_now.getBegin_time()||!userData.getPrevious_time().equals(userData_now.getPrevious_time())){
                                                                        editor1.putInt("plandays",userData.getPlandays());
                                                                        editor1.putInt("plannum",userData.getPlannum());
                                                                        editor1.putString("previous_time",userData.getPrevious_time());
                                                                        editor1.putLong("begin_time",date_string_to_long(userData.getBegin_time().getDate()));
                                                                        editor1.commit();
                                                                    }
                                                                    if(!userData.getWrongwords().equals(userData_now.getWrongwords())){
                                                                        dbHelper.updateAll_Word_Collection(userData.getWrongwords());
                                                                    }
                                                                    if(!userData.getWordplan().equals(userData_now.getWordplan())){
                                                                        dbHelper.updateAll_WordPlan(userData.getWordplan());
                                                                    }
                                                                    handler.sendEmptyMessage(0);
                                                                    new AlertDialog.Builder(LoginActivity.this)
                                                                            .setMessage("同步成功，重启应用")
                                                                            .setCancelable(false)
                                                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    handler.sendEmptyMessage(2);
                                                                                }
                                                                            })
                                                                            .create().show();
                                                                }
                                                            })
                                                            .setCancelable(false)
                                                            .create().show();
                                                }
                                            }else{
                                                userData_now.setSynctime(new BmobDate(new Date()));
                                                userData_now.save(new SaveListener<String>() {
                                                    @Override
                                                    public void done(String s, BmobException e) {
                                                        if(e==null){
                                                            handler.sendEmptyMessage(1);
                                                            new MyToast(LoginActivity.this,MyToast.SYNC_SUCCESS).show();
                                                        }else{
                                                            handler.sendEmptyMessage(0);
                                                            new MyToast(LoginActivity.this,MyToast.SYNC_ERROR).show();
                                                        }
                                                        finish();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else{
                                    handler.sendEmptyMessage(0);
                                    new MyToast(LoginActivity.this,e.getErrorCode()).show();
                                }
                            }
                        });
                    }else{
                        handler.sendEmptyMessage(0);
                        if(e==null){
                            new MyToast(LoginActivity.this,MyToast.ACCOUNT_ERROR);
                        }else{
                            new MyToast(LoginActivity.this,e.getErrorCode()).show();
                        }
                    }
                }else{
                    handler.sendEmptyMessage(0);
                    if(e==null){
                        new MyToast(LoginActivity.this,MyToast.ACCOUNT_ERROR);
                    }else{
                        new MyToast(LoginActivity.this,e.getErrorCode()).show();
                    }
                }
            }
        });
    }

    private long date_string_to_long(String str){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date=simpleDateFormat.parse(str);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_back:
                finish();
                break;
            case R.id.id_tv_register:
                startActivity(new Intent(this,RegisterActivity.class));
                break;
            case R.id.id_bt_login:
                if(check_input()){
                    zLoadingDialog.show();
                    login();
                }
                break;
        }
    }
}
