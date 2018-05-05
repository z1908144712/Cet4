package com.example.bishe.cet4.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;
import com.example.bishe.cet4.function.GetQuestion;
import com.example.bishe.cet4.function.WordTimer;
import com.example.bishe.cet4.object.Question;


public class WordCollectionDeleteActivity extends Activity implements View.OnClickListener{
    private String english=null;
    private SQLiteDatabase db=null;
    private DBHelper dbHelper=null;
    private TextView textView_question=null;
    private Button button_item_1=null;
    private Button button_item_2=null;
    private Button button_item_3=null;
    private Button button_item_4=null;
    private Question question=null;
    private GetQuestion getQuestion=null;
    private WordTimer wordTimer=null;
    private ProgressBar progressBar=null;
    private int id;
    private int right_answer;
    private int right_num;
    private int question_index;
    private int question_num;
    private String previous_time=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_collection_delete_activity_layout);
        //初始化数据库
        initDataBase();
        //初始化控件
        initViews();
        //初始化事件
        initEvents();
        //初始化数据
        initData();
    }

    private void initDataBase(){
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }

    private void initViews(){
        textView_question=findViewById(R.id.id_question);
        button_item_1=findViewById(R.id.id_item_1);
        button_item_2=findViewById(R.id.id_item_2);
        button_item_3=findViewById(R.id.id_item_3);
        button_item_4=findViewById(R.id.id_item_4);
        progressBar=findViewById(R.id.id_progressBar);
    }

    private void initEvents(){
        button_item_1.setOnClickListener(this);
        button_item_2.setOnClickListener(this);
        button_item_3.setOnClickListener(this);
        button_item_4.setOnClickListener(this);
    }

    private void initData(){
        wordTimer=new WordTimer();
        SharedPreferences sharedPreferences=getSharedPreferences("plandays",MODE_PRIVATE);
        previous_time=sharedPreferences.getString("previous_time",null);
        right_num=0;
        question_index=0;
        question_num=5;
        progressBar.setMax(question_num);
        getEnglish();
        getQuestion=new GetQuestion(dbHelper,id,GetQuestion.TYPE_3);
        wordTimer.startTimer();
        setQuestion();
    }

    private void setQuestion(){
        progressBar.setProgress(question_index+1);
        question=getQuestion.getQuestion();
        right_answer=question.getRight_answer();
        textView_question.setText(question.getQuestion());
        button_item_1.setText(question.getItems()[0]);
        button_item_2.setText(question.getItems()[1]);
        button_item_3.setText(question.getItems()[2]);
        button_item_4.setText(question.getItems()[3]);
    }

    private void getEnglish(){
        Intent intent=getIntent();
        english=intent.getStringExtra("english");
        id=dbHelper.getIdByEnglish_Test_Question(english);
    }

    private void addTime(){
        wordTimer.stopTimer();
        String learned_time=dbHelper.selectLearnTimeFromWordPlanByTime(previous_time);
        dbHelper.updateTimePlanWords(previous_time,WordTimer.time_long_to_string(WordTimer.time_string_to_long(learned_time)+wordTimer.getTime_long()));
    }

    private void nextQuestion(int choice){
        question_index++;
        if(right_answer==choice){
            right_num++;
        }
        if(question_index<question_num){
            setQuestion();
        }else{
            if(right_num<question_num){
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("你只答对了"+right_num+"道题\n很遗憾没有通过本次测试！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addTime();
                                finish();
                            }
                        })
                        .create()
                        .show();
            }else{
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("恭喜你！你答对了全部的题目，通过了本次测试！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper.decWrongNumWordCollection(english);
                                addTime();
                                finish();
                            }
                        })
                        .create()
                        .show();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("确定放弃测试？")
                    .setNegativeButton("取消",null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addTime();
                            finish();
                        }
                    })
                    .create()
                    .show();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_item_1:
                nextQuestion(0);
                break;
            case R.id.id_item_2:
                nextQuestion(1);
                break;
            case R.id.id_item_3:
                nextQuestion(2);
                break;
            case R.id.id_item_4:
                nextQuestion(3);
                break;
        }
    }
}
