package com.example.bishe.cet4.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.object.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserInfoActivity extends Activity implements AdapterView.OnItemClickListener,View.OnClickListener{
    private ListView user_info_listview=null;
    private SimpleAdapter simpleAdapter=null;
    private ImageView iv_back=null;
    private Button bt_logout=null;
    private User user=null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_activity_layout);
        //初始化控件
        initViews();
        //初始化事件
        initEvents();
        //初始化数据
        initData();
    }

    private void initViews(){
        user_info_listview=findViewById(R.id.id_user_item_listview);
        iv_back=findViewById(R.id.id_back);
        bt_logout=findViewById(R.id.id_logout);
    }

    private void initEvents(){
        iv_back.setOnClickListener(this);
        bt_logout.setOnClickListener(this);
        user_info_listview.setOnItemClickListener(this);
    }

    private void initData(){
        Intent intent=getIntent();
        user= (User) intent.getSerializableExtra("user");
        setUserInfo();
    }

    private void setUserInfo(){
        List<Map<String,String>> user_list=new ArrayList<>();
        Map map=new HashMap();
        map.put("name","用户ID");
        map.put("value",user.getObjectId());
        user_list.add(map);
        map=new HashMap();
        map.put("name","用户名");
        map.put("value",user.getUsername());
        user_list.add(map);
        map=new HashMap();
        map.put("name","注册时间");
        map.put("value",user.getCreate_time().getDate());
        user_list.add(map);
        map=new HashMap();
        map.put("name","上次登陆时间");
        map.put("value",user.getLogin_time().getDate());
        user_list.add(map);
        map=new HashMap();
        map.put("name","修改密码");
        map.put("value","");
        user_list.add(map);
        simpleAdapter=new SimpleAdapter(this,user_list,R.layout.userinfo_item_layout,new String[]{"name","value"},new int[]{R.id.id_name,R.id.id_value});
        user_info_listview.setAdapter(simpleAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position==4){
            Intent intent=new Intent(this,UpdateUserPasswordActivity.class);
            intent.putExtra("user_id",user.getObjectId());
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_back:
                finish();
                break;
            case R.id.id_logout:
                Intent intent=new Intent("android.intent.action.syncService");
                intent.setPackage(getPackageName());
                stopService(intent);
                SharedPreferences sharedPreferences=getSharedPreferences("login",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("login_time",null);
                editor.putString("user_id",null);
                editor.putString("username",null);
                editor.putBoolean("isLogin",false);
                editor.commit();
                finish();
                break;
        }
    }
}
