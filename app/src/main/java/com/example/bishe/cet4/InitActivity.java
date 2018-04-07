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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
    private EditText my_input=null;
    private TextView item_result=null;
    private SQLiteDatabase db=null;
    private boolean isFirst=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //首次使用
        if(isFirstUse()){
            isFirst=true;
            showWordPlan();
        }else{
            init();
        }

    }

    private void init(){
        //初始化数据库
        initDatabase();
        //初始化数据
        initData();
    }
    private void jump(){
        //跳转到主界面
        Intent intent=new Intent();
        intent.setClass(InitActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void initViews(View view){
        item_1=view.findViewById(R.id.item_1);
        item_2=view.findViewById(R.id.item_2);
        item_3=view.findViewById(R.id.item_3);
        item_4=view.findViewById(R.id.item_4);
        item_5=view.findViewById(R.id.item_5);
        item_result=view.findViewById(R.id.item_result);
        item_my=view.findViewById(R.id.item_my);
        my_input=view.findViewById(R.id.my_input);
    }
    private void initEvents(){
        item_1.setOnClickListener(this);
        item_2.setOnClickListener(this);
        item_3.setOnClickListener(this);
        item_4.setOnClickListener(this);
        item_5.setOnClickListener(this);
        item_my.setOnClickListener(this);
    }
    private void showWordPlan(){
        View alertView=LayoutInflater.from(InitActivity.this).inflate(R.layout.word_plan_layout,null);
        initViews(alertView);
        initEvents();
        final AlertDialog alertDialog=new AlertDialog.Builder(InitActivity.this)
                .setTitle("你准备在几天内完成？（推荐30天）")
                .setView(alertView)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("确定", null)
                .create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(my_input.getVisibility()==View.VISIBLE){
                    String input=my_input.getText().toString();
                    input=input.trim();
                    if (null!=input||!("").equals(input)){
                        final int days=Integer.parseInt(input);
                        if(days>=20&&days<=60){
                            new AlertDialog.Builder(InitActivity.this)
                                    .setMessage("确定在"+days+"天内完成？")
                                    .setNegativeButton("取消",null)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            storePlanDays(days);
                                            alertDialog.dismiss();
                                            init();
                                        }
                                    })
                                    .create()
                                    .show();
                        }else{
                            Toast.makeText(InitActivity.this,"输入的数字应在20~60之间！",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(InitActivity.this,"输入你的计划！",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    String input=item_result.getText().toString();
                    input=input.trim();
                    final int days=Integer.parseInt(input);
                    new AlertDialog.Builder(InitActivity.this)
                            .setMessage("确定在"+days+"天内完成？")
                            .setNegativeButton("取消",null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    storePlanDays(days);
                                    alertDialog.dismiss();
                                    init();
                                }
                            })
                            .create()
                            .show();
                }
            }
        });
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object alertController = mAlert.get(alertDialog);
            Field mTitleView = alertController.getClass().getDeclaredField("mTitleView");
            mTitleView.setAccessible(true);
            TextView title = (TextView) mTitleView.get(alertController);
            title.setTextColor(Color.RED);
            title.setTextSize(14);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
    private void storePlanDays(int days){
        SharedPreferences sharedPreferences=getSharedPreferences("plandays",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("plandays",days);
        editor.putInt("plannum",1000%days==0?1000/days:(1000/days)+1);
        Date date=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        editor.putString("previous_time",simpleDateFormat.format(date));
        editor.putLong("begin_time",date.getTime());
        editor.commit();
    }
    private boolean isFirstUse(){
        SharedPreferences sharedPreferences=getSharedPreferences("isFirst", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        if(sharedPreferences.getBoolean("isFirst",true)){
            editor.putBoolean("isFirst",false);
            editor.commit();
            return true;
        }else{
            return false;
        }
    }
    private void initData(){
        SharedPreferences sharedPreferences=getSharedPreferences("plandays",Context.MODE_PRIVATE);
        int days=sharedPreferences.getInt("plandays",-1);
        int num=sharedPreferences.getInt("plannum",-1);
        long begin_time=sharedPreferences.getLong("begin_time",-1);
        String previous_time=sharedPreferences.getString("previous_time",null);
        if(days==-1||num==-1||null==previous_time||begin_time==-1){
            showWordPlan();
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
                isFirst=false;
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("previous_time",simpleDateFormat.format(date_now));
                editor.commit();
                AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
                db=assetsDatabaseManager.getDatabase("dict.db");
                DBHelper dbHelper=new DBHelper(db);
                String words_num_str=dbHelper.selectAllLearnWordNum();
                List<Integer> res;
                if(("").equals(words_num_str)){
                    res=random_num(num,null);
                }else{
                    String[] words_num_strs=words_num_str.split(",");
                    int []exp=new int[words_num_strs.length-1];
                    for(int i=0;i<exp.length;i++){
                        exp[i]=Integer.parseInt(words_num_strs[i]);
                    }
                    res=random_num(num,exp);
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
    private List<Integer> random_num(int num,int[] exp){
        List<Integer> res=new ArrayList();
        List<Integer> sug=new ArrayList();
        Random random=new Random();
        if(null==exp||exp.length==0){
            for(int i=0;i<1000;i++){
                sug.add(i);
            }
        }else{
            for(int i=0;i<1000;i++){
                if(!isExist(i,exp)){
                    sug.add(i);
                }
            }
        }
        for(int i=0;i<num;i++){
            res.add(sug.remove(random.nextInt(sug.size())));
            if(sug.size()==0){
                break;
            }
        }
        return res;
    }
    private Boolean isExist(int n,int [] exp){
        for(int i=0;i<exp.length;i++){
            if(n==exp[i]){
                return true;
            }
        }
        return false;
    }
    private void initDatabase(){
        AssetsDatabaseManager.initManager(getApplicationContext());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.item_1:
                unSelectedAll();
                item_1.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                item_result.setText("20");
                break;
            case R.id.item_2:
                unSelectedAll();
                item_2.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                item_result.setText("25");
                break;
            case R.id.item_3:
                unSelectedAll();
                item_3.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                item_result.setText("30");
                break;
            case R.id.item_4:
                unSelectedAll();
                item_4.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                item_result.setText("35");
                break;
            case R.id.item_5:
                unSelectedAll();
                item_5.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                item_result.setText("40");
                break;
            case R.id.item_my:
                my_input.setVisibility(View.VISIBLE);
                break;
        }
    }
    private void unSelectedAll(){
            item_1.setBackgroundResource(R.drawable.unselected_word_plan_shape_button);
            item_2.setBackgroundResource(R.drawable.unselected_word_plan_shape_button);
            item_3.setBackgroundResource(R.drawable.unselected_word_plan_shape_button);
            item_4.setBackgroundResource(R.drawable.unselected_word_plan_shape_button);
            item_5.setBackgroundResource(R.drawable.unselected_word_plan_shape_button);
            my_input.setText("");
            my_input.setVisibility(View.INVISIBLE);
    }
}
