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

public class TestActivity extends Activity implements View.OnClickListener{
    private SQLiteDatabase db=null;
    private DBHelper dbHelper=null;
    private TextView textView_question=null;
    private Button button_item_1=null;
    private Button button_item_2=null;
    private Button button_item_3=null;
    private Button button_item_4=null;
    private String []words_num_array=null;
    private Question question=null;
    private GetQuestion getQuestion=null;
    private WordTimer wordTimer=null;
    private ProgressBar progressBar=null;
    private int words_index;
    private int right_answer;
    private int right_answer_num;
    private int id;
    private int words_count;
    private int type;
    private int type3_random_num;
    private int mode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_layout);
        //初始化数据库
        initDataBase();
        //初始化控件
        initViews();
        //初始化数据
        initData();
        //初始化事件
        initEvent();
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

    private void initData(){
        Intent intent=getIntent();
        id=intent.getIntExtra("id",-1);
        type=intent.getIntExtra("type",-1);
        mode=intent.getIntExtra("mode",-1);
        if(mode==WordActivity.MODE_3){
            words_num_array=intent.getStringArrayExtra("words");
        }else{
            words_num_array=dbHelper.select_words_plan_byId(id).split(",");
        }
        words_count=words_num_array.length;
        progressBar.setMax(words_count);
        words_index=0;
        wordTimer=new WordTimer();
        wordTimer.startTimer();
        setQuestion();
    }

    private void initEvent(){
        button_item_1.setOnClickListener(this);
        button_item_2.setOnClickListener(this);
        button_item_3.setOnClickListener(this);
        button_item_4.setOnClickListener(this);
    }

    private void setQuestion(){
        progressBar.setProgress(words_index+1);
        getQuestion=new GetQuestion(dbHelper,Integer.valueOf(words_num_array[words_index]),type);
        type3_random_num=getQuestion.getRandom_num();
        question=getQuestion.getQuestion();
        right_answer=question.getRight_answer();
        textView_question.setText(question.getQuestion());
        button_item_1.setText(question.getItems()[0]);
        button_item_2.setText(question.getItems()[1]);
        button_item_3.setText(question.getItems()[2]);
        button_item_4.setText(question.getItems()[3]);
    }

    private void addTime(){
        SharedPreferences sharedPreferences=getSharedPreferences("plandays",MODE_PRIVATE);
        String previous_time=sharedPreferences.getString("previous_time",null);
        String learned_time=dbHelper.selectLearnTimeFromWordPlanByTime(previous_time);
        wordTimer.stopTimer();
        dbHelper.updateTimePlanWords(previous_time,WordTimer.time_long_to_string(wordTimer.getTime_long()+WordTimer.time_string_to_long(learned_time)));
    }

    private void addWrongNum(){
        String english;
        switch (type3_random_num){
            case GetQuestion.TYPE_1:
                english=question.getItems()[right_answer];
                dbHelper.addWrongNumWordCollection(dbHelper.selectIdByEnglish(english),english,id);
                break;
            case GetQuestion.TYPE_2:
                english=question.getQuestion();
                dbHelper.addWrongNumWordCollection(dbHelper.selectIdByEnglish(english),english,id);
                break;
            default:
                switch (type){
                    case GetQuestion.TYPE_1:
                        english=question.getItems()[right_answer];
                        dbHelper.addWrongNumWordCollection(dbHelper.selectIdByEnglish(english),english,id);
                        break;
                    case GetQuestion.TYPE_2:
                        english=question.getQuestion();
                        dbHelper.addWrongNumWordCollection(dbHelper.selectIdByEnglish(english),english,id);
                        break;
                }
                break;
        }
    }

    private void nextQuestion(){
        words_index++;
        if(words_index<words_count){
            setQuestion();
        }else{
            addTime();
            if(right_answer_num==words_count){
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("恭喜你!答对了全部的题目!")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create()
                        .show();
            }else{
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("本次测试答对了"+right_answer_num+"道题!")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create()
                        .show();
            }
        }
    }

    private void checkAnswer(int answer){
        if(right_answer==answer){
            right_answer_num++;
            nextQuestion();
        }else{
            addWrongNum();
            new AlertDialog.Builder(this)
                    .setTitle("正确答案为")
                    .setMessage(question.getItems()[right_answer])
                    .setCancelable(false)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            nextQuestion();
                        }
                    })
                    .create()
                    .show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_item_1:
                checkAnswer(0);
                break;
            case R.id.id_item_2:
                checkAnswer(1);
                break;
            case R.id.id_item_3:
                checkAnswer(2);
                break;
            case R.id.id_item_4:
                checkAnswer(3);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(this)
                    .setMessage("确定退出测试？")
                    .setNegativeButton("取消",null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addTime();
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }
        return super.onKeyDown(keyCode, event);
    }
}
