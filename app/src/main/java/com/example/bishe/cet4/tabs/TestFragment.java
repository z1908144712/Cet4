package com.example.bishe.cet4.tabs;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.activity.TestActivity;
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;
import com.example.bishe.cet4.function.GetQuestion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Skywilling on 2018/1/2.
 */

public class TestFragment extends Fragment implements View.OnClickListener{
    private SQLiteDatabase db=null;
    private DBHelper dbHelper=null;
    private AlertDialog alertDialog=null;
    private int plandays;
    private int column;
    private int row;
    private int count;
    private int day;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_tab_test,null);
        //初始化数据库
        initDataBase();
        //初始化数据
        initData();
        //初始化控件
        initViews(view.findViewById(R.id.id_test_list));
        return view;
    }

    private  void initDataBase(){
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }

    private void initData(){
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("plandays", Context.MODE_PRIVATE);
        plandays=sharedPreferences.getInt("plandays",-1);
        column=4;
        row=(plandays+column)/column;
        count=dbHelper.selectCountFromWordsPlan();
    }

    private void initViews(View view){
        LinearLayout linearLayout_test_list=new LinearLayout(view.getContext());
        linearLayout_test_list.setOrientation(LinearLayout.VERTICAL);
        linearLayout_test_list.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        LayoutInflater layoutInflater=LayoutInflater.from(view.getContext());
        int index=0;
        ViewHolder viewHolder;
        for(int i=0;i<row;i++){
            LinearLayout linearLayout=new LinearLayout(view.getContext());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout test_list_item;
            LinearLayout.LayoutParams test_list_item_layoutParams;
            for(int j=0;j<column;j++){
                index++;
                test_list_item=(LinearLayout)layoutInflater.inflate(R.layout.test_list_item_layout,null);
                test_list_item_layoutParams=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);
                test_list_item.setLayoutParams(test_list_item_layoutParams);
                if(index<=plandays){
                    viewHolder=new ViewHolder();
                    viewHolder.button_test_list_item=test_list_item.findViewById(R.id.id_test_list_item);
                    viewHolder.button_test_list_item.setText(String.valueOf(index));
                    if(index>count){
                        viewHolder.button_test_list_item.setClickable(false);
                        viewHolder.button_test_list_item.setTextColor(getResources().getColor(R.color.gray_white));
                    }else{
                        viewHolder.button_test_list_item.setOnClickListener(this);
                    }
                }else{
                    test_list_item.setVisibility(View.INVISIBLE);
                }
                linearLayout.addView(test_list_item);
            }
            linearLayout_test_list.addView(linearLayout);
        }
        ((ScrollView)view).addView(linearLayout_test_list);
    }


    @Override
    public void onClick(View v) {
        day=Integer.valueOf(((Button)v).getText().toString());
        new AlertDialog.Builder(getContext())
                .setTitle("提示")
                .setMessage("确定开始第"+day+"天的测试？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        View view=LayoutInflater.from(getContext()).inflate(R.layout.test_type_layout,null);
                        Button button_type1=view.findViewById(R.id.id_test_type_1);
                        Button button_type2=view.findViewById(R.id.id_test_type_2);
                        Button button_type3=view.findViewById(R.id.id_test_type_3);
                        button_type1.setOnClickListener(new TypeOnClickListener());
                        button_type2.setOnClickListener(new TypeOnClickListener());
                        button_type3.setOnClickListener(new TypeOnClickListener());
                        alertDialog=new AlertDialog.Builder(getContext()).create();
                        alertDialog.setView(view);
                        alertDialog.show();
                    }
                })
                .create()
                .show();
    }

    public void jump(int type){
        alertDialog.cancel();
        Intent intent=new Intent();
        intent.putExtra("id",day);
        intent.putExtra("type",type);
        intent.setClass(getContext(),TestActivity.class);
        startActivity(intent);
    }

    public class ViewHolder{
        public TextView button_test_list_item;
    }

    public class TypeOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.id_test_type_1:
                    jump(GetQuestion.TYPE_1);
                    break;
                case R.id.id_test_type_2:
                    jump(GetQuestion.TYPE_2);
                    break;
                case R.id.id_test_type_3:
                    jump(GetQuestion.TYPE_3);
                    break;
            }
        }
    }
}
