package com.example.bishe.cet4.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.activity.TestActivity;
import com.example.bishe.cet4.activity.WordActivity;
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;
import com.example.bishe.cet4.function.GetQuestion;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class TestFragmentTwo extends Fragment implements View.OnClickListener{
    private View view=null;
    private LinearLayout linearLayout=null;
    private List<String> data=null;
    private SQLiteDatabase db=null;
    private DBHelper dbHelper=null;
    private ZLoadingDialog zLoadingDialog=null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view=inflater.inflate(R.layout.test_fragment_2_layout,container,false);
        }
        //初始化数据库
        initDataBase();
        //初始化数据
        initData();
        //初始化控件
        initViews();
        return view;
    }

    private void initDataBase(){
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }

    private void initViews(){
        linearLayout=view.findViewById(R.id.id_lineLayout);
        linearLayout.removeAllViews();
        int i=0;
        while (i<data.size()){
            LinearLayout linearLayout1=new LinearLayout(getContext());
            linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
            View view1= LinearLayout.inflate(getContext(),R.layout.test_list_item_layout,null);
            LinearLayout.LayoutParams view1_layoutParams= new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);
            view1.setLayoutParams(view1_layoutParams);
            Button button=view1.findViewById(R.id.id_test_list_item);
            button.setTag(i);
            button.setOnClickListener(this);
            button.setText(data.get(i++));
            button.setTextSize(18f);
            linearLayout1.addView(view1);
            view1=LinearLayout.inflate(getContext(),R.layout.test_list_item_layout,null);
            view1.setLayoutParams(view1_layoutParams);
            if(i<data.size()){
                button=view1.findViewById(R.id.id_test_list_item);
                button.setTag(i);
                button.setOnClickListener(this);
                button.setText(data.get(i++));
                button.setTextSize(18f);
            }else{
                view1.setVisibility(View.INVISIBLE);
            }
            linearLayout1.addView(view1);
            linearLayout.addView(linearLayout1);
        }
        zLoadingDialog=new ZLoadingDialog(getContext());
        zLoadingDialog
                .setCancelable(false)
                .setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE)
                .setLoadingColor(Color.RED)
                .setHintText("正在生成测试题目");
    }

    private void initData(){
        data=new ArrayList<>();
        data.add("中译英");
        data.add("英译中");
        data.add("中译英|英译中");
    }

    private String[] getWords(){
        String []words_array=dbHelper.selectAllLearnWordNum().split(",");
        String []words=new String[30];
        if(words_array.length<=30){
            return words_array;
        }else{
            int i=0;
            Random random=new Random();
            String word;
            while(i<30){
                word=words_array[random.nextInt(words_array.length)];
                if(!isExist(words,word)){
                    words[i++]=word;
                }
            }
            return words;
        }
    }

    private boolean isExist(String[]words,String word){
        for(int i=0;i<words.length;i++){
            if(word.equals(words[i])){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(final View v) {
        new AlertDialog.Builder(getContext())
                .setMessage("确认开始单项测试？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(getContext(), TestActivity.class);
                        intent.putExtra("mode", WordActivity.MODE_3);
                        switch ((int)v.getTag()){
                            case 0:
                                intent.putExtra("type", GetQuestion.TYPE_1);
                                break;
                            case 1:
                                intent.putExtra("type", GetQuestion.TYPE_2);
                                break;
                            case 2:
                                intent.putExtra("type", GetQuestion.TYPE_3);
                                break;
                        }
                        zLoadingDialog.show();
                        intent.putExtra("words",getWords());
                        zLoadingDialog.cancel();
                        startActivity(intent);
                    }
                })
                .create().show();
    }
}
