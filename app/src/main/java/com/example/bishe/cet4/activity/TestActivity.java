package com.example.bishe.cet4.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;

import java.util.Random;

public class TestActivity extends Activity implements View.OnClickListener{
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private TextView textView_title;
    private TextView textView_num;
    private TextView textView_question_content;
    private Button button_A;
    private Button button_B;
    private Button button_C;
    private Button button_D;
    private String []words_num_array;
    private String []question_choices;
    private int []question_choices_num;
    private int words_index;
    private int words_sum;
    private int right_answer;
    private int right_answer_num;
    private int wrong_answer_num;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testactivity_layout);
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
        textView_title=findViewById(R.id.id_test_title);
        textView_num=findViewById(R.id.id_test_num);
        textView_question_content=findViewById(R.id.id_question_content);
        button_A=findViewById(R.id.id_button_A);
        button_B=findViewById(R.id.id_button_B);
        button_C=findViewById(R.id.id_button_C);
        button_D=findViewById(R.id.id_button_D);
        Drawable drawable_a=getResources().getDrawable(R.drawable.question_item_a);
        Drawable drawable_b=getResources().getDrawable(R.drawable.question_item_b);
        Drawable drawable_c=getResources().getDrawable(R.drawable.question_item_c);
        Drawable drawable_d=getResources().getDrawable(R.drawable.question_item_d);
        drawable_a.setBounds(0,0,80,80);
        drawable_b.setBounds(0,0,80,80);
        drawable_c.setBounds(0,0,80,80);
        drawable_d.setBounds(0,0,80,80);
        button_A.setCompoundDrawables(drawable_a,null,null,null);
        button_B.setCompoundDrawables(drawable_b,null,null,null);
        button_C.setCompoundDrawables(drawable_c,null,null,null);
        button_D.setCompoundDrawables(drawable_d,null,null,null);
    }

    private void initData(){
        Intent intent=getIntent();
        int id=intent.getIntExtra("id",-1);
        int count=intent.getIntExtra("count",-1);
        if(id==-1||count==-1){
            Toast.makeText(this,"发生未知错误！即将退出！",Toast.LENGTH_SHORT).show();
            finish();
        }else{
            if(id<count-1){
                set_title(id,1);
            }else if(id==count-1){
                set_title(id,2);
            }else if(id==count){
                set_title(id,3);
            }else{
                Toast.makeText(this,"发生未知错误！即将退出！",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        right_answer_num=0;
        wrong_answer_num=0;
        //初始化问题内容
        initQuestionContent();
        setQuestion();
    }

    private void setQuestion(){
        //设置题号
        textView_num.setText(words_index+"/"+words_sum);
        //设置问题内容
        set_QuestionContent();
        //初始化选项
        right_answer=initChoices();
        //设置选项
        set_Choices();
        Toast.makeText(this,"The right answer is "+question_choices[right_answer],Toast.LENGTH_SHORT).show();
    }
    private void initEvent(){
        button_A.setOnClickListener(this);
        button_B.setOnClickListener(this);
        button_C.setOnClickListener(this);
        button_D.setOnClickListener(this);
    }

    private void set_title(int id,int type){
        if(type==1) {
            textView_title.setText("第" + (id + 1) + "天");
            words_num_array=dbHelper.select_words_plan_byId(id+1).split(",");
        }else if(type==2){
            textView_title.setText("今天");
            words_num_array=dbHelper.select_words_plan_byId(id+1).split(",");
        }else{
            textView_title.setText("全部所学");
            words_num_array=dbHelper.selectAllLearnWordNum().split(",");
        }
        words_sum=words_num_array.length;
        words_index=1;
    }

    private void initQuestionContent(){
        for(int i=0;i<500;i++){
            Random random=new Random();
            int a=random.nextInt(words_sum);
            int b=random.nextInt(words_sum);
            if(a!=b){
                String temp=words_num_array[a];
                words_num_array[a]=words_num_array[b];
                words_num_array[b]=temp;
            }else{
                i--;
            }
        }
    }

    private void set_QuestionContent(){
        String english=dbHelper.selectEnglishById(Integer.parseInt(words_num_array[words_index-1]));
        textView_question_content.setText(words_index+". 下列符合"+english+"的意思的是：");
    }

    private int initChoices(){
        question_choices=new String[4];
        question_choices_num=new int[4];
        question_choices[0]=dbHelper.selectChineseById(Integer.parseInt(words_num_array[words_index-1]));
        System.out.println(question_choices[0]);
        question_choices_num[0]=Integer.parseInt(words_num_array[words_index-1]);
        for(int i=1;i<4;i++){
            Random random=new Random();
            int random_num=random.nextInt(1000);
            for(int j=0;j<i;j++){
                if(random_num==question_choices_num[j]){
                    i--;
                    break;
                }
            }
            question_choices_num[i]=random_num;
            question_choices[i]=dbHelper.selectChineseById(random_num);
        }
        int right_answer=0;
        for(int i=0;i<10;i++){
            Random random=new Random();
            int a=random.nextInt(4);
            int b=random.nextInt(4);
            if(a!=b){
                if(a==right_answer){
                    right_answer=b;
                }else if(b==right_answer){
                    right_answer=a;
                }
                int temp=question_choices_num[a];
                question_choices_num[a]=question_choices_num[b];
                question_choices_num[b]=temp;
                String temp_str=question_choices[a];
                question_choices[a]=question_choices[b];
                question_choices[b]=temp_str;
            }else{
                i--;
            }
        }
        return right_answer;
    }

    private void set_Choices(){
        button_A.setText("  "+question_choices[0]);
        button_B.setText("  "+question_choices[1]);
        button_C.setText("  "+question_choices[2]);
        button_D.setText("  "+question_choices[3]);
    }

    private void question_choice(final int id){
        if(words_index<=words_sum){
            words_index++;
            if(id==right_answer){
                right_answer_num++;
                if(words_index>words_sum){
                    question_choice(id);
                }else{
                    setQuestion();
                }
            }else{
                wrong_answer_num++;
                new AlertDialog.Builder(this)
                        .setMessage("正确答案为\n\n\t"+question_choices[right_answer])
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(words_index>words_sum){
                                    question_choice(id);
                                }else{
                                    setQuestion();
                                }
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }
        }else{
            new AlertDialog.Builder(this)
                    .setMessage("答对"+right_answer_num+"道题\n\n答错"+wrong_answer_num+"道题")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_button_A:
                question_choice(0);
                break;
            case R.id.id_button_B:
                question_choice(1);
                break;
            case R.id.id_button_C:
                question_choice(2);
                break;
            case R.id.id_button_D:
                question_choice(3);
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
