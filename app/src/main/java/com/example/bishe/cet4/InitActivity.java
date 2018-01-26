package com.example.bishe.cet4;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AlertDialogLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bishe.cet4.database.AssetsDatabaseManager;

import java.lang.reflect.Field;

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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //是否首次使用
        if(isFirstUse()){
            //首次使用
            System.out.println("1");
        }else{
            //非首次使用
            System.out.println("0");
        }
        showWordPlan();

        //初始化数据库
//        initDatabase();
        //初始化数据
//        initData();
        //跳转到主界面
//        Intent intent=new Intent();
//        intent.setClass(this,MainActivity.class);
//        startActivity(intent);
//        finish();
        super.onCreate(savedInstanceState);
    }
    private void initViews(View view){
        item_1=view.findViewById(R.id.item_1);
        item_2=view.findViewById(R.id.item_2);
        item_3=view.findViewById(R.id.item_3);
        item_4=view.findViewById(R.id.item_4);
        item_5=view.findViewById(R.id.item_5);
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
        View alertView=LayoutInflater.from(this).inflate(R.layout.word_plan_layout,null);
        initViews(alertView);
        initEvents();
        AlertDialog alertDialog=new AlertDialog.Builder(this)
                .setTitle("你准备在几天内完成？（推荐30天）")
                .setView(alertView)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
        alertDialog.show();
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
                break;
            case R.id.item_2:
                unSelectedAll();
                item_2.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                break;
            case R.id.item_3:
                unSelectedAll();
                item_3.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                break;
            case R.id.item_4:
                unSelectedAll();
                item_4.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
                break;
            case R.id.item_5:
                unSelectedAll();
                item_5.setBackgroundResource(R.drawable.selected_word_plan_shape_button);
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
            my_input.setVisibility(View.INVISIBLE);
    }
}
