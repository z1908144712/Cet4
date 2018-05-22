package com.example.bishe.cet4.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;
import com.example.bishe.cet4.function.GetQuestion;
import com.example.bishe.cet4.function.MyToast;
import com.example.bishe.cet4.function.NetWork;
import com.example.bishe.cet4.function.WordTimer;
import com.example.bishe.cet4.object.Question;
import com.example.bishe.cet4.object.Word;


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
    private int word_index;
    private Question question;
    private GetQuestion getQuestion;
    private boolean haveChoice=false;
    private WordTimer wordTimer=null;
    private String previous_time=null;
    private Handler handler=null;
    private ProgressBar progressBar=null;
    private SharedPreferences sharedPreferences=null;
    private SharedPreferences.Editor editor=null;
    private int mode;
    private int day;
    public static final int MODE_1=1;
    public static final int MODE_2=2;
    public static final int MODE_3=3;

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
        progressBar=findViewById(R.id.id_progressBar);
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
        mode=getIntent().getIntExtra("mode",0);
        sharedPreferences=getSharedPreferences("plandays",Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        previous_time=sharedPreferences.getString("previous_time",null);
        if(mode==MODE_1){
            words_index_array=dbHelper.selectWordPlanByTime(previous_time).split(",");
            words_num=words_index_array.length;
            word_index=sharedPreferences.getInt("word_index",0);
            progressBar.setMax(words_num);
            day=dbHelper.selectCountFromWordsPlan();
        }else if(mode==MODE_2){
            day=getIntent().getIntExtra("day",-1);
            words_index_array=dbHelper.select_words_plan_byId(day).split(",");
            words_num=words_index_array.length;
            word_index=0;
            progressBar.setMax(words_num);
        }else if(mode==MODE_3){
            day=getIntent().getIntExtra("day",-1);
            words_index_array=getIntent().getStringExtra("words").split(",");
            words_num=words_index_array.length;
            word_index=0;
            progressBar.setMax(words_num);
        }else{
            finish();
        }
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
        progressBar.setProgress(word_index+1);
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
            if(mode==MODE_1){
                editor.putInt("word_index",word_index);
                editor.commit();
            }
            setWord();
        }else{
            if(mode==MODE_1){
                editor.putInt("word_index",0);
                editor.commit();
            }
            wordTimer.stopTimer();
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("你已完成今天的计划，是否开始今天的测试？")
                    .setCancelable(false)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setLearnedTime();
                            View view= LayoutInflater.from(WordActivity.this).inflate(R.layout.test_type_layout,null);
                            TextView tv_type1=view.findViewById(R.id.id_test_type_1);
                            TextView tv_type2=view.findViewById(R.id.id_test_type_2);
                            TextView tv_type3=view.findViewById(R.id.id_test_type_3);
                            tv_type1.setOnClickListener(new WordActivity.TypeOnClickListener());
                            tv_type2.setOnClickListener(new WordActivity.TypeOnClickListener());
                            tv_type3.setOnClickListener(new WordActivity.TypeOnClickListener());
                            new AlertDialog.Builder(WordActivity.this)
                                    .setView(view)
                                    .create().show();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
        dbHelper.addWrongNumWordCollection(word.getId(),word.getEnglish(),day);
    }

    private void setLearnedTime(){
        String learned_time=dbHelper.selectLearnTimeFromWordPlanByTime(previous_time);
        dbHelper.updateTimePlanWords(previous_time,WordTimer.time_long_to_string(WordTimer.time_string_to_long(learned_time)+wordTimer.getTime_long()));
    }

    private void showWordDetail(){
        if(NetWork.isNetworkConnected(getApplicationContext())){
            Intent intent=new Intent();
            intent.setClass(this,WordDetailActivity.class);
            intent.putExtra("keyword",word.getEnglish());
            intent.putExtra("type",WordDetailActivity.TYPE_ENGLISH);
            intent.putExtra("type_show",WordDetailActivity.TYPE_WORD);
            startActivity(intent);
        }else{
            new MyToast(WordActivity.this,MyToast.NO_NETWORK).show();
        }

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

    public void jump(int type){
        Intent intent=new Intent();
        intent.putExtra("id",day);
        intent.putExtra("type",type);
        if(mode==MODE_3){
            intent.putExtra("mode",mode);
            intent.putExtra("words",words_index_array);
        }
        intent.setClass(WordActivity.this,TestActivity.class);
        startActivity(intent);
        finish();
    }
}
