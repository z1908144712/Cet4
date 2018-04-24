package com.example.bishe.cet4.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;
import com.example.bishe.cet4.function.GetQuestion;
import com.example.bishe.cet4.function.WordTimer;
import com.example.bishe.cet4.object.Question;
import com.example.bishe.cet4.object.Word;
import com.youdao.sdk.app.Language;
import com.youdao.sdk.app.LanguageUtils;
import com.youdao.sdk.common.Constants;
import com.youdao.sdk.ydonlinetranslate.SpeechTranslateParameters;
import com.youdao.sdk.ydonlinetranslate.Translator;
import com.youdao.sdk.ydtranslate.Translate;
import com.youdao.sdk.ydtranslate.TranslateErrorCode;
import com.youdao.sdk.ydtranslate.TranslateListener;
import com.youdao.sdk.ydtranslate.TranslateParameters;

import java.util.List;


public class WordActivity extends Activity implements View.OnClickListener{
    private SQLiteDatabase db=null;
    private DBHelper dbHelper=null;
    private TextView textView_word=null;
    private TextView textView_phonetic=null;
    private ImageView imageView_voice=null;
    private Button button_item_1=null;
    private Button button_item_2=null;
    private Button button_item_3=null;
    private Button button_item_4=null;
    private Button button_unknown=null;
    private Button button_next=null;
    private Word word=null;
    private String []words_index_array=null;
    private int words_num=0;
    private int word_index=0;
    private Question question;
    private GetQuestion getQuestion;
    private boolean haveChoice=false;
    private WordTimer wordTimer=null;
    private String previous_time=null;
    private Handler handler=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_layout);
        //初始化数据库
        initDatabase();
        //初始化控件
        initViews();
        //初始化事件
        initEvents();
        //初始化数据
        initData();
        //设置单词
        setWord();
    }


    private void initDatabase(){
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }

    private void initViews(){
        textView_word=findViewById(R.id.id_word);
        textView_phonetic=findViewById(R.id.id_phonetic);
        imageView_voice=findViewById(R.id.id_voice);
        button_item_1=findViewById(R.id.id_item_1);
        button_item_2=findViewById(R.id.id_item_2);
        button_item_3=findViewById(R.id.id_item_3);
        button_item_4=findViewById(R.id.id_item_4);
        button_unknown=findViewById(R.id.id_unknown);
        button_next=findViewById(R.id.id_next_word);
    }

    private void initEvents(){
        imageView_voice.setOnClickListener(this);
        button_item_1.setOnClickListener(this);
        button_item_2.setOnClickListener(this);
        button_item_3.setOnClickListener(this);
        button_item_4.setOnClickListener(this);
        button_unknown.setOnClickListener(this);
        button_next.setOnClickListener(this);
    }

    private void initData(){
        SharedPreferences sharedPreferences=getSharedPreferences("plandays",Context.MODE_PRIVATE);
        previous_time=sharedPreferences.getString("previous_time",null);
        words_index_array=dbHelper.selectWordPlanByTime(previous_time).split(",");
        words_num=words_index_array.length;
        wordTimer=new WordTimer();
        wordTimer.startTimer();
    }

    private void playAudio(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent();
                intent.putExtra("query",word.getEnglish());
                intent.setAction("android.intent.action.audioService");
                intent.setPackage(getPackageName());
                startService(intent);
            }
        });
    }


    private void setWord(){
        haveChoice=false;
        word=dbHelper.selectWordById(words_index_array[word_index]);
        if(null!=word){
            getQuestion=new GetQuestion(dbHelper,Integer.valueOf(words_index_array[word_index]),GetQuestion.TYPE_2);
            question=getQuestion.getQuestion();
            handler=new Handler();
            playAudio();
            textView_word.setText(word.getEnglish());
            textView_phonetic.setText("/"+word.getPhonetic().split("#")[0]+"/");
            button_item_1.setText(question.getItems()[0]);
            button_item_2.setText(question.getItems()[1]);
            button_item_3.setText(question.getItems()[2]);
            button_item_4.setText(question.getItems()[3]);
        }
    }

    private void nextWord(){
        word_index++;
        if(word_index<words_num){
            setWord();
        }else{
            wordTimer.stopTimer();
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("本次学习时长为"+wordTimer.getTime_string())
                    .setCancelable(false)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setLearnedTime();
                            finish();
                        }
                    })
                    .create()
                    .show();
        }
    }

    private void check_answer(int answer){
        if(answer!=question.getRight_answer()){
            showWordDetail();
        }else{
            nextWord();
        }
    }


    private void setUnknownWord(){
        dbHelper.addWrongNumWordCollection(word.getEnglish());
    }

    private void setLearnedTime(){
        String learned_time=dbHelper.selectLearnTimeFromWordPlanByTime(previous_time);
        dbHelper.updateTimePlanWords(previous_time,WordTimer.time_long_to_string(WordTimer.time_string_to_long(learned_time)+wordTimer.getTime_long()));
    }

    private void showWordDetail(){
        Intent intent=new Intent();
        intent.setClass(this,WordDetailActivity.class);
        intent.putExtra("keyword",word.getEnglish());
        intent.putExtra("type",WordDetailActivity.TYPE_ENGLISH);
        intent.putExtra("type_show",WordDetailActivity.TYPE_WORD);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_voice:
                playAudio();
                break;
            case R.id.id_item_1:
                haveChoice=true;
                check_answer(0);
                break;
            case R.id.id_item_2:
                haveChoice=true;
                check_answer(1);
                break;
            case R.id.id_item_3:
                haveChoice=true;
                check_answer(2);
                break;
            case R.id.id_item_4:
                haveChoice=true;
                check_answer(3);
                break;
            case R.id.id_unknown:
                setUnknownWord();
                showWordDetail();
                break;
            case R.id.id_next_word:
                if(haveChoice){
                    nextWord();
                }else{
                    new AlertDialog.Builder(this)
                            .setTitle("提示")
                            .setMessage("确定跳过该单词？")
                            .setCancelable(false)
                            .setNegativeButton("取消",null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setUnknownWord();
                                    nextWord();
                                }
                            })
                            .create()
                            .show();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            wordTimer.stopTimer();
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("本次学习时长为"+wordTimer.getTime_string()+"\n确定结束学习？")
                    .setCancelable(false)
                    .setNegativeButton("取消",null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setLearnedTime();
                            finish();
                        }
                    })
                    .create()
                    .show();
        }
        return super.onKeyUp(keyCode, event);
    }
}
