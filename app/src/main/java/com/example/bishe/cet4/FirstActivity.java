package com.example.bishe.cet4;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;
import com.example.bishe.cet4.function.MyToast;
import com.example.bishe.cet4.object.AppKey;
import com.example.bishe.cet4.object.WordPlanNum;
import com.youdao.sdk.app.YouDaoApplication;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.Bmob;

public class FirstActivity extends Activity implements View.OnClickListener{
    private SQLiteDatabase db=null;
    private DBHelper dbHelper=null;
    private Button item_1=null;
    private Button item_2=null;
    private Button item_3=null;
    private Button item_4=null;
    private Button item_5=null;
    private Button item_my=null;
    private Button init_word_plan_button=null;
    private EditText my_input=null;
    private String input_plan="40";
    private int words_count=0;
    private int plandaysmin=0;
    private int plandaysmax=0;
    private boolean isFirst=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity_layout);
        //初始化Bmob
        Bmob.initialize(this, AppKey.appKey_Bmob);
        //初始化YouDaoApplication
        YouDaoApplication.init(this, AppKey.appKey_YouDao);
        //初始化数据库
        initDatabase();
        words_count=dbHelper.selectCountFromWords();
        plandaysmin=words_count% WordPlanNum.max==0?words_count/ WordPlanNum.max:(words_count/ WordPlanNum.max)+1;
        plandaysmax=words_count% WordPlanNum.min==0?words_count/ WordPlanNum.min:(words_count/ WordPlanNum.min)+1;
        isFirst=isFirstUse();
        if(isFirst){
            //初始化控件
            initViews();
            //初始化事件
            initEvents();
        }else{
            jump();
        }
    }


    private void initDatabase(){
        AssetsDatabaseManager.initManager(getApplicationContext());
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }

    private void initViews(){
        item_1=findViewById(R.id.item_1);
        item_2=findViewById(R.id.item_2);
        item_3=findViewById(R.id.item_3);
        item_4=findViewById(R.id.item_4);
        item_5=findViewById(R.id.item_5);
        item_my=findViewById(R.id.item_my);
        init_word_plan_button=findViewById(R.id.init_word_plan_button);
    }

    private void initEvents(){
        item_1.setOnClickListener(this);
        item_2.setOnClickListener(this);
        item_3.setOnClickListener(this);
        item_4.setOnClickListener(this);
        item_5.setOnClickListener(this);
        item_my.setOnClickListener(this);
        init_word_plan_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.item_1:
                unSelectedAll();
                item_1.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                item_1.setTextColor(Color.parseColor("#000000"));
                input_plan="30";
                break;
            case R.id.item_2:
                unSelectedAll();
                item_2.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                item_2.setTextColor(Color.parseColor("#000000"));
                input_plan="35";
                break;
            case R.id.item_3:
                unSelectedAll();
                item_3.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                item_3.setTextColor(Color.parseColor("#000000"));
                input_plan="40";
                break;
            case R.id.item_4:
                unSelectedAll();
                item_4.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                item_4.setTextColor(Color.parseColor("#000000"));
                input_plan="45";
                break;
            case R.id.item_5:
                unSelectedAll();
                item_5.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                item_5.setTextColor(Color.parseColor("#000000"));
                input_plan="50";
                break;
            case R.id.item_my:
                unSelectedAll();
                item_my.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                item_my.setTextColor(Color.parseColor("#000000"));
                final AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("自定义");
                my_input=new EditText(builder.getContext());
                my_input.setHint("输入"+plandaysmin+"-"+plandaysmax+"之间的数字");
                my_input.setBackgroundResource(R.drawable.word_plan_input_bg);
                my_input.setBackgroundColor(getResources().getColor(R.color.gray_white));
                my_input.setInputType(InputType.TYPE_CLASS_NUMBER);
                my_input.setWidth(800);
                LinearLayout linearLayout=new LinearLayout(builder.getContext());
                linearLayout.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearLayout.setLayoutParams(layoutParams);
                linearLayout.addView(my_input);
                builder.setView(linearLayout);
                builder.setCancelable(false);
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        unSelectedAll();
                        item_3.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                        item_3.setTextColor(Color.parseColor("#000000"));
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        input_plan=my_input.getText().toString().trim();
                        if(input_plan.equals("")){
                            unSelectedAll();
                            item_3.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                            item_3.setTextColor(Color.parseColor("#000000"));
                            new MyToast(getApplicationContext(),MyToast.EMPTY).show();
                        }else{
                            int input_plan_num=Integer.parseInt(input_plan);
                            if(input_plan_num<plandaysmin||input_plan_num>plandaysmax){
                                unSelectedAll();
                                item_3.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                                item_3.setTextColor(Color.parseColor("#000000"));
                                new MyToast(getApplicationContext(),"输入"+plandaysmin+"-"+plandaysmax+"之间的数字").show();
                            }else{
                                item_my.setText(input_plan+"天");
                            }
                        }
                    }
                });
                builder.create().show();
                break;
            case R.id.init_word_plan_button:
                setNotFirstUse();
                storePlanDays(Integer.parseInt(input_plan));
                jump();
                break;
        }
    }

    private void storePlanDays(int days){
        SharedPreferences sharedPreferences=getSharedPreferences("plandays",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("plandays",days);
        words_count=dbHelper.selectCountFromWords();
        editor.putInt("plannum",words_count%days==0?words_count/days:(words_count/days)+1);
        Date date=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        editor.putString("previous_time",simpleDateFormat.format(date));
        editor.putLong("begin_time",date.getTime());
        editor.commit();
    }

    private void unSelectedAll(){
        item_1.setBackgroundResource(R.drawable.unselected_word_plan_shape_button);
        item_1.setTextColor(Color.parseColor("#ffffff"));
        item_2.setBackgroundResource(R.drawable.unselected_word_plan_shape_button);
        item_2.setTextColor(Color.parseColor("#ffffff"));
        item_3.setBackgroundResource(R.drawable.unselected_word_plan_shape_button);
        item_3.setTextColor(Color.parseColor("#ffffff"));
        item_4.setBackgroundResource(R.drawable.unselected_word_plan_shape_button);
        item_4.setTextColor(Color.parseColor("#ffffff"));
        item_5.setBackgroundResource(R.drawable.unselected_word_plan_shape_button);
        item_5.setTextColor(Color.parseColor("#ffffff"));
        item_my.setText("自定义");
        item_my.setBackgroundResource(R.drawable.unselected_word_plan_shape_button);
        item_my.setTextColor(Color.parseColor("#ffffff"));
    }

    private void setNotFirstUse(){
        SharedPreferences sharedPreferences=getSharedPreferences("isFirst", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("isFirst",false);
        editor.commit();
    }

    private boolean isFirstUse(){
        SharedPreferences sharedPreferences=getSharedPreferences("isFirst", Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("isFirst",true)){
            return true;
        }else{
            return false;
        }
    }

    private void jump(){
        //跳转到初始化界面
        Intent intent=new Intent();
        intent.setClass(FirstActivity.this,InitActivity.class);
        intent.putExtra("isFirst",isFirst);
        startActivity(intent);
        finish();
    }
}
