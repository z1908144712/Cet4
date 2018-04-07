package com.example.bishe.cet4.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;

public class TestActivity extends Activity{
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private TextView textView_title;
    private TextView textView_num;
    private String []words_num_array;
    private int words_index;
    private int words_sum;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testactivity_layout);
        //初始化数据库
        initDataBase();
        //初始化控件
        initViews();
        //初始化数据
        initData();
    }

    private void initDataBase(){
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }

    private void initViews(){
        textView_title=findViewById(R.id.id_test_title);
        textView_num=findViewById(R.id.id_test_num);
    }

    private void initData(){
        Intent intent=getIntent();
        int id=intent.getIntExtra("id",-1);
        int count=intent.getIntExtra("count",-1);
        if(id==-1||count==-1){
            Toast.makeText(this,"发生未知错误！即将退出！",Toast.LENGTH_SHORT).show();
            finish();
        }else{
            if(id<count-1){
                set_title(id,1);
            }else if(id==count-1){
                set_title(id,2);
            }else if(id==count){
                set_title(id,3);
            }else{
                Toast.makeText(this,"发生未知错误！即将退出！",Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    private void set_title(int id,int type){
        if(type==1) {
            textView_title.setText("第" + (id + 1) + "天");
            words_num_array=dbHelper.select_words_plan_byId(id+1).split(",");
        }else if(type==2){
            textView_title.setText("今天");
            words_num_array=dbHelper.select_words_plan_byId(id+1).split(",");
        }else{
            textView_title.setText("全部所学");
            words_num_array=dbHelper.selectAllLearnWordNum().split(",");
        }
        words_sum=words_num_array.length;
        words_index=1;
        textView_num.setText(words_index+"/"+words_sum);
    }

}
