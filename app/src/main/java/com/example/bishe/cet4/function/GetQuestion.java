package com.example.bishe.cet4.function;

import com.example.bishe.cet4.database.DBHelper;
import com.example.bishe.cet4.object.Question;

import java.util.Random;

public class GetQuestion {
    private Question question=null;
    private DBHelper dbHelper=null;
    private String[] question_choices=null;
    private int[] question_choices_num=null;
    private int id;
    private int type;
    private int random_num=-1;
    /*中译英模式*/
    public static final int TYPE_1=1;
    /*英译中模式*/
    public static final int TYPE_2=2;
    /*中译英、英译中混合模式*/
    public static final int TYPE_3=3;

    public GetQuestion(DBHelper dbHelper,int id,int type) {
        this.dbHelper=dbHelper;
        this.id = id;
        this.type=type;
    }


    public Question getQuestion(){
        question=new Question();
        switch (type){
            case TYPE_1:
                question.setQuestion(dbHelper.selectChineseById(id));
                break;
            case TYPE_2:
                question.setQuestion(dbHelper.selectEnglishById(id));
                break;
            case TYPE_3:
                Random random=new Random();
                this.random_num=random.nextInt(2);
                switch (random_num){
                    case 0:
                        question.setQuestion(dbHelper.selectChineseById(id));
                        break;
                    case 1:
                        question.setQuestion(dbHelper.selectEnglishById(id));
                        break;
                }
                break;
        }
        question.setRight_answer(initChoices());
        question.setItems(question_choices);
        return question;
    }

    private int initChoices(){
        int right_answer=0;
        switch (type){
            case TYPE_1:
                right_answer=getChoicesType1();
                break;
            case TYPE_2:
                right_answer=getChoicesType2();
                break;
            case TYPE_3:
                right_answer=getChoicesType3();
                break;
        }
        return right_answer;
    }

    private int getChoicesType1(){
        question_choices=new String[4];
        question_choices_num=new int[4];
        question_choices[0]=dbHelper.selectEnglishById(id);
        question_choices_num[0]=id;
        int words_num=dbHelper.selectCountFromWords();
        //生成四个选项
        for(int i=1;i<4;i++){
            Random random=new Random();
            int j;
            int random_num=random.nextInt(words_num);
            for(j=0;j<i;j++){
                if(random_num==question_choices_num[j]){
                    i--;
                    break;
                }
            }
            if(j==i){
                question_choices_num[i]=random_num;
                question_choices[i]=dbHelper.selectEnglishById(random_num);
            }
        }
        return randomChange();
    }

    private int getChoicesType2(){
        question_choices=new String[4];
        question_choices_num=new int[4];
        question_choices[0]=dbHelper.selectChineseById(id);
        question_choices_num[0]=id;
        int words_num=dbHelper.selectCountFromWords();
        //生成四个选项
        for(int i=1;i<4;i++){
            Random random=new Random();
            int j;
            int random_num=random.nextInt(words_num);
            for(j=0;j<i;j++){
                if(random_num==question_choices_num[j]){
                    i--;
                    break;
                }
            }
            if(j==i){
                question_choices_num[i]=random_num;
                question_choices[i]=dbHelper.selectChineseById(random_num);
            }
        }
        return randomChange();
    }

    private int getChoicesType3(){
        question_choices=new String[4];
        question_choices_num=new int[4];
        switch (random_num){
            case 0:
                question_choices[0]=dbHelper.selectEnglishById(id);
                break;
            case 1:
                question_choices[0]=dbHelper.selectChineseById(id);
                break;
        }
        question_choices_num[0]=id;
        int words_num=dbHelper.selectCountFromWords();
        //生成四个选项
        for(int i=1;i<4;i++){
            Random random=new Random();
            int j;
            int random_num=random.nextInt(words_num);
            for(j=0;j<i;j++){
                if(random_num==question_choices_num[j]){
                    i--;
                    break;
                }
            }
            if(j==i){
                question_choices_num[i]=random_num;
                switch (this.random_num){
                    case 0:
                        question_choices[i]=dbHelper.selectEnglishById(random_num);
                        break;
                    case 1:
                        question_choices[i]=dbHelper.selectChineseById(random_num);
                        break;
                }
            }
        }
        return randomChange();
    }

    private int randomChange(){
        int right_answer=0;
        //随机交换四个选项的顺序
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

    public int getRandom_num() {
        return random_num;
    }
}
