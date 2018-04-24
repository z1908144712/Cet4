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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;
import com.example.bishe.cet4.object.AppKey;
import com.youdao.sdk.app.YouDaoApplication;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.Bmob;

/**
 * Created by Skywilling on 2018/1/10.
 */

public class InitActivity extends Activity implements View.OnClickListener{
    private Button item_1=null;
    private Button item_2=null;
    private Button item_3=null;
    private Button item_4=null;
    private Button item_5=null;
    private Button item_my=null;
    private Button init_word_plan_button=null;
    private EditText my_input=null;
    private String input_plan="30";
    private SQLiteDatabase db=null;
    private DBHelper dbHelper=null;
    private int words_count=0;
    private boolean isFirst=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化Bmob
        initBmob();
        //初始化YouDaoApplication
        initYouDaoAppliaction();
        //初始化数据库
        initDatabase();
        //首次使用
        isFirst=isFirstUse();
        if(isFirst){
            setContentView(R.layout.activity_init_layout);
            //初始化控件
            initViews();
            //初始化事件
            initEvents();
        }else{
            //初始化计划
            initPlan();
        }
    }

    private void initBmob(){
        Bmob.initialize(this,AppKey.appKey_Bmob);
    }

    private void initYouDaoAppliaction(){
        YouDaoApplication.init(this, AppKey.appKey_YouDao);
    }

    private void initDatabase(){
        AssetsDatabaseManager.initManager(getApplicationContext());
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }

    private void jump(){
        //跳转到主界面
        Intent intent=new Intent();
        intent.setClass(InitActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
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

    private void restartApplication() {
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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

    private void setNotFirstUse(){
        SharedPreferences sharedPreferences=getSharedPreferences("isFirst", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("isFirst",false);
        editor.commit();
    }

    private void setFirstUse(){
        SharedPreferences sharedPreferences=getSharedPreferences("isFirst", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("isFirst",true);
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

   private void initPlan(){
        SharedPreferences sharedPreferences=getSharedPreferences("plandays",Context.MODE_PRIVATE);
        int days=sharedPreferences.getInt("plandays",-1);
        int num=sharedPreferences.getInt("plannum",-1);
        long begin_time=sharedPreferences.getLong("begin_time",-1);
        String previous_time=sharedPreferences.getString("previous_time",null);
        if(days==-1||num==-1||null==previous_time||begin_time==-1){
            setFirstUse();
            restartApplication();
        }else{
            Date date_now=new Date();
            Date date_previous=null;
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            try {
                date_now=simpleDateFormat.parse(simpleDateFormat.format(date_now));
                date_previous=simpleDateFormat.parse(previous_time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long during_time=date_now.getTime()-date_previous.getTime();
            if(during_time>0||isFirst){
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("previous_time",simpleDateFormat.format(date_now));
                System.out.println(simpleDateFormat.format(date_now));
                editor.commit();
                String all_learned_words_str=dbHelper.selectAllLearnWordNum();
                List<Integer> res;
                if(("").equals(all_learned_words_str.trim())){
                    res=random_num(num,null);
                }else{
                    res=random_num(num,all_learned_words_str.trim());
                }
                String res_str="";
                for(int i=0;i<res.size();i++){
                    res_str+=res.get(i)+",";
                }
                dbHelper.insertPlanWords(simpleDateFormat.format(date_now),res_str);
            }
            jump();
        }
    }

    private List<Integer> random_num(int num,String exp){
        List<Integer> res=new ArrayList();
        Random random=new Random();
        if(null==exp){
            exp="";
        }
        int temp=0;
        String temp_str="";
        for(int i=0;i<num;i++){
            temp=random.nextInt(dbHelper.selectCountFromWords());
            temp_str=String.valueOf(temp);
            if(!exp.contains(temp_str)){
                res.add(temp);
            }else{
                i--;
            }
            exp+=temp_str+",";
        }
        return res;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.item_1:
                unSelectedAll();
                item_1.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                item_1.setTextColor(Color.parseColor("#000000"));
                input_plan="20";
                break;
            case R.id.item_2:
                unSelectedAll();
                item_2.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                item_2.setTextColor(Color.parseColor("#000000"));
                input_plan="25";
                break;
            case R.id.item_3:
                unSelectedAll();
                item_3.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                item_3.setTextColor(Color.parseColor("#000000"));
                input_plan="30";
                break;
            case R.id.item_4:
                unSelectedAll();
                item_4.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                item_4.setTextColor(Color.parseColor("#000000"));
                input_plan="35";
                break;
            case R.id.item_5:
                unSelectedAll();
                item_5.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                item_5.setTextColor(Color.parseColor("#000000"));
                input_plan="40";
                break;
            case R.id.item_my:
                unSelectedAll();
                item_my.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                item_my.setTextColor(Color.parseColor("#000000"));
                final AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("自定义");
                my_input=new EditText(builder.getContext());
                my_input.setHint("输入20-60之间的数字");
                my_input.setBackgroundResource(R.drawable.word_plan_input_bg);
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
                            Toast.makeText(builder.getContext(),"未输入数字！",Toast.LENGTH_SHORT).show();
                        }else{
                            int input_plan_num=Integer.parseInt(input_plan);
                            if(input_plan_num<20||input_plan_num>60){
                                unSelectedAll();
                                item_3.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                                item_3.setTextColor(Color.parseColor("#000000"));
                                Toast.makeText(builder.getContext(),"数字应在20~60之间！",Toast.LENGTH_SHORT).show();
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
                //初始化计划
                initPlan();
                break;
        }
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
}
