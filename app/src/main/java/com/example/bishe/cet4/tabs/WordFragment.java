package com.example.bishe.cet4.tabs;


import android.content.Context;
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
import android.widget.TextView;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.activity.WordActivity;
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;


/**
 * Created by Skywilling on 2018/1/2.
 */

public class WordFragment extends Fragment implements View.OnClickListener{
    private TextView textView_last_words_num=null;
    private TextView textView_learned_days=null;
    private TextView textView_learn_words_today=null;
    private TextView textView_learned_time_today=null;
    private Button button_begin_learn_today=null;
    private SQLiteDatabase db=null;
    private DBHelper dbHelper=null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_tab_word,container,false);
        //初始化数据库
        initDatabase();
        //初始化控件
        initViews(view);
        //初始化事件
        initEvents();
        return view;
    }

    @Override
    public void onStart() {
        //初始化数据
        initData();
        super.onStart();
    }


    private void initDatabase(){
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }

    private void initViews(View view){
        textView_last_words_num=view.findViewById(R.id.id_last_words_num);
        textView_learned_days=view.findViewById(R.id.id_learned_days);
        textView_learn_words_today=view.findViewById(R.id.id_learn_words_today);
        textView_learned_time_today=view.findViewById(R.id.id_learned_time_today);
        button_begin_learn_today=view.findViewById(R.id.id_begin_learn_today);
    }

    private void initEvents(){
        button_begin_learn_today.setOnClickListener(this);
    }

    private void initData(){
        textView_last_words_num.setText(String.valueOf(dbHelper.select_learned_words_num_except_today()));
        textView_learned_days.setText(String.valueOf(dbHelper.selectCountFromWordsPlan()));
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("plandays",Context.MODE_PRIVATE);
        String previous_time=sharedPreferences.getString("previous_time",null);
        int word_index=sharedPreferences.getInt("word_index",0);
        textView_learn_words_today.setText(String.valueOf(dbHelper.selectCountToday(previous_time)));
        textView_learned_time_today.setText(dbHelper.selectLearnTimeFromWordPlanByTime(previous_time));
        if(word_index>0){
            button_begin_learn_today.setText("继续学习");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_begin_learn_today:
                Intent intent=new Intent();
                intent.setClass(getContext(), WordActivity.class);
                intent.putExtra("mode",WordActivity.MODE_1);
                startActivity(intent);
                break;
        }
    }
}

