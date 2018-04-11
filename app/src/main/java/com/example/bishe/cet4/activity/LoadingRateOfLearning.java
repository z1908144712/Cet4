package com.example.bishe.cet4.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadingRateOfLearning extends Activity {
    private AVLoadingIndicatorView avLoadingIndicatorView=null;
    private List<String> groupName=null;
    private List<List<Map<String,String>>> childContext=null;
    private SQLiteDatabase db=null;
    private DBHelper dbHelper=null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_layout);
        avLoadingIndicatorView=findViewById(R.id.id_loading);
        WindowManager windowManager=(WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics=new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        avLoadingIndicatorView.setPadding(displayMetrics.widthPixels/2-48,0,0,0);
        avLoadingIndicatorView.smoothToShow();
        //初始化数据库
        initDataBase();
        //初始化数据
        initData();
        //跳转
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent();
                intent.setClass(LoadingRateOfLearning.this,RateOfLearningActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("groupName",(Serializable) groupName);
                bundle.putSerializable("childContext", (Serializable) childContext);
                intent.putExtra("RateOfLearning",bundle);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
                finish();
            }
        });
        thread.start();
    }

    private void initDataBase(){
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }

    private void initData(){
        groupName=new ArrayList<>();
        childContext=new ArrayList<>();
        List<String> learn_word=dbHelper.selectAllLearnWord();
        String []learned_word_num_array;
        List<Map<String,String>> childs;
        //已学习
        if(learn_word.size()>1){                        //除今天以外的数据
            for(int i=0;i<learn_word.size()-1;i++){
                learned_word_num_array=learn_word.get(i).split(",");
                childs=new ArrayList();
                for(int j=0;j<learned_word_num_array.length;j++){
                    Map map=new HashMap();
                    map.put("id",learned_word_num_array[j]);
                    map.put("english",dbHelper.selectEnglishById(Integer.parseInt(learned_word_num_array[j])));
                    childs.add(map);
                }
                groupName.add("第"+(i+1)+"天  共"+childs.size()+"个");
                childContext.add(childs);
            }
        }
        if(learn_word.size()==1){                   //今天的数据
            learned_word_num_array=learn_word.get(0).split(",");
        }else{
            learned_word_num_array=learn_word.get(learn_word.size()-1).split(",");
        }
        childs=new ArrayList();
        for(int i=0;i<learned_word_num_array.length;i++){
            Map map=new HashMap();
            map.put("id",learned_word_num_array[i]);
            map.put("english",dbHelper.selectEnglishById(Integer.parseInt(learned_word_num_array[i])));
            childs.add(map);
        }
        groupName.add("今天   共"+childs.size()+"个");
        childContext.add(childs);
        //未学习
        String learned_word_str=dbHelper.selectAllLearnWordNum();
        learned_word_num_array=learned_word_str.split(",");
        int []learned_words_int=new int[learned_word_num_array.length];
        for(int i=0;i<learned_word_num_array.length;i++){
            learned_words_int[i]=Integer.parseInt(learned_word_num_array[i]);
        }
        Arrays.sort(learned_words_int);
        childs=new ArrayList<>();
        int j=0,i=0;
        while(i<1000){
            if(j<learned_words_int.length&&i==learned_words_int[j]){
                i++;
                j++;
            }else{
                Map map=new HashMap();
                map.put("id",i);
                map.put("english",dbHelper.selectEnglishById(i));
                childs.add(map);
                i++;
            }
        }
        groupName.add("未学习  共"+childs.size()+"个");
        childContext.add(childs);
    }
}
