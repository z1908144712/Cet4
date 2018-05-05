package com.example.bishe.cet4.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadingRateOfLearning extends Activity {
    private List<String> groupName=null;
    private List<List<Map<String,String>>> childContext=null;
    private SQLiteDatabase db=null;
    private DBHelper dbHelper=null;
    private Handler handler=null;
    private int words_count;
    private int learned_words_count;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle_loading_layout);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        //跳转
                        Intent intent=new Intent();
                        intent.setClass(LoadingRateOfLearning.this,RateOfLearningActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("groupName",(Serializable) groupName);
                        bundle.putSerializable("childContext", (Serializable) childContext);
                        intent.putExtra("RateOfLearning",bundle);
                        intent.putExtra("words_count",words_count);
                        intent.putExtra("learned_words_count",learned_words_count);
                        startActivity(intent);
                        finish();
                        break;
                }
                super.handleMessage(msg);
            }
        };
       new Thread(new Runnable() {
            @Override
            public void run() {
                //初始化数据库
                initDataBase();
                //初始化数据
                initData();
                handler.sendEmptyMessage(1);
            }
        }).start();
    }

    private void initDataBase(){
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }

    private void initData(){
        words_count=dbHelper.selectCountFromWords();
        learned_words_count=dbHelper.select_learned_words_num();
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
