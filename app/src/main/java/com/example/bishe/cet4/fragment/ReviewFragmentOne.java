package com.example.bishe.cet4.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.activity.WordActivity;
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;


public class ReviewFragmentOne extends Fragment implements View.OnClickListener{
    private View view=null;
    private SQLiteDatabase db=null;
    private DBHelper dbHelper=null;
    private ScrollView scrollView=null;
    private int plandays=-1;
    private int days;
    private int column;
    private int row;
    private int count;
    private int day;
    private boolean isChanged=true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view==null){
            view=inflater.inflate(R.layout.review_tab_fragment_1_layout,container,false);
        }
        //初始化数据库
        initDataBase();
        return view;
    }


    @Override
    public void onStart() {
        //初始化数据
        initData();
        //初始化控件
        initViews();
        super.onStart();
    }


    private void initDataBase(){
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }

    private void initViews(){
        if(!isChanged){
            return;
        }
        LinearLayout linearLayout_test_list=new LinearLayout(view.getContext());
        linearLayout_test_list.setOrientation(LinearLayout.VERTICAL);
        linearLayout_test_list.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        LayoutInflater layoutInflater=LayoutInflater.from(view.getContext());
        int index=0;
        ReviewFragmentOne.ViewHolder viewHolder;
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
                    viewHolder=new ReviewFragmentOne.ViewHolder();
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
        scrollView=view.findViewById(R.id.id_test_list);
        scrollView.removeAllViews();
        scrollView.addView(linearLayout_test_list);
    }

    private void initData(){
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("plandays", Context.MODE_PRIVATE);
        days=sharedPreferences.getInt("plandays",-1);
        if(plandays==-1||plandays!=days) {
            plandays = days;
            column=4;
            row=plandays%column==0?plandays/column:(plandays/column+1);
            count=dbHelper.selectCountFromWordsPlan();
            isChanged=true;
        }else{
            isChanged=false;
        }
    }

    @Override
    public void onClick(View v) {
        day=Integer.valueOf(((Button)v).getText().toString());
        new AlertDialog.Builder(getContext())
                .setTitle("提示")
                .setMessage("确定开始学习第"+day+"天的单词？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(getContext(), WordActivity.class);
                        intent.putExtra("mode",WordActivity.MODE_2);
                        intent.putExtra("day",day);
                        startActivity(intent);
                    }
                })
                .create()
                .show();
    }

    public class ViewHolder{
        public TextView button_test_list_item;
    }
}
