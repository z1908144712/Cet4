package com.example.bishe.cet4;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.tabs.LearnFragment;
import com.example.bishe.cet4.tabs.PersonalFragment;
import com.example.bishe.cet4.tabs.SearchFragment;
import com.example.bishe.cet4.tabs.TestFragment;
import com.example.bishe.cet4.tabs.WordFragment;


public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    //四个Tab对应的布局
    private LinearLayout tab_word;
    private LinearLayout tab_learn;
    private LinearLayout tab_search;
    private LinearLayout tab_test;
    private LinearLayout tab_personal;
    //四个Tab对应的Fragment
    private Fragment fragment_word;
    private Fragment fragment_learn;
    private Fragment fragment_search;
    private Fragment fragment_test;
    private Fragment fragment_personal;
    //四个Tab对应的ImageButton
    private ImageButton imageButton_word;
    private ImageButton imageButton_learn;
    private ImageButton imageButton_search;
    private ImageButton imageButton_test;
    private ImageButton imageButton_personal;
    //四个Tab对应的TextView
    private TextView textView_word;
    private TextView textView_learn;
    private TextView textView_search;
    private TextView textView_test;
    private TextView textView_personal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //初始化控件
        initViews();
        //初始化框架
        initFragmentManager();
        //初始化事件
        initEvents();

    }

    private void initViews(){
        tab_word=(LinearLayout) findViewById(R.id.id_tab_word);
        tab_learn=(LinearLayout)findViewById(R.id.id_tab_learn);
        tab_search=(LinearLayout)findViewById(R.id.id_tab_search);
        tab_test=(LinearLayout)findViewById(R.id.id_tab_test);
        tab_personal=(LinearLayout)findViewById(R.id.id_tab_personal);
        imageButton_word=(ImageButton)findViewById(R.id.id_tab_word_img);
        imageButton_learn=(ImageButton)findViewById(R.id.id_tab_learn_img);
        imageButton_search=(ImageButton)findViewById(R.id.id_tab_search_img);
        imageButton_test=(ImageButton)findViewById(R.id.id_tab_test_img);
        imageButton_personal=(ImageButton)findViewById(R.id.id_tab_personal_img);
        textView_word=(TextView)findViewById(R.id.id_text_word);
        textView_learn=(TextView)findViewById(R.id.id_text_learn);
        textView_search=(TextView)findViewById(R.id.id_text_search);
        textView_test=(TextView)findViewById(R.id.id_text_test);
        textView_personal=(TextView)findViewById(R.id.id_text_personal);
    }
    private void initFragmentManager(){
        fragmentManager=getSupportFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();
        fragment_word=new WordFragment();
        fragmentTransaction.add(R.id.id_fragments,fragment_word);
        fragmentTransaction.commit();
    }
    private void initEvents(){
        tab_word.setOnClickListener(this);
        tab_learn.setOnClickListener(this);
        tab_search.setOnClickListener(this);
        tab_test.setOnClickListener(this);
        tab_personal.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        resetImgs();
        switch (view.getId()){
            case R.id.id_tab_word:
                selectTab(0);
                break;
            case R.id.id_tab_learn:
                selectTab(1);
                break;
            case R.id.id_tab_search:
                selectTab(2);
                break;
            case R.id.id_tab_test:
                selectTab(3);
                break;
            case R.id.id_tab_personal:
                selectTab(4);
                break;
        }
    }

    private void resetImgs(){
        imageButton_word.setBackgroundResource(R.drawable.icon_word);
        imageButton_learn.setBackgroundResource(R.drawable.icon_learn);
        imageButton_search.setBackgroundResource(R.drawable.icon_search);
        imageButton_test.setBackgroundResource(R.drawable.icon_test);
        imageButton_personal.setBackgroundResource(R.drawable.icon_personal);
        textView_word.setTextColor(Color.BLACK);
        textView_learn.setTextColor(Color.BLACK);
        textView_search.setTextColor(Color.BLACK);
        textView_test.setTextColor(Color.BLACK);
        textView_personal.setTextColor(Color.BLACK);
    }
    private void hideFragments(FragmentTransaction transaction){
        if(fragment_word!=null){
            transaction.hide(fragment_word);
        }
        if(fragment_learn!=null){
            transaction.hide(fragment_learn);
        }
        if(fragment_search!=null){
            transaction.hide(fragment_search);
        }
        if(fragment_test!=null){
            transaction.hide(fragment_test);
        }
        if(fragment_personal!=null){
            transaction.hide(fragment_personal);
        }
    }
    private void selectTab(int i){
        fragmentTransaction=fragmentManager.beginTransaction();
        hideFragments(fragmentTransaction);
        switch (i){
            case 0:
                imageButton_word.setBackgroundResource(R.drawable.icon_word_select);
                textView_word.setTextColor(Color.rgb(40,207,87));
                if(fragment_word==null){
                    fragment_word=new WordFragment();
                    fragmentTransaction.add(R.id.id_fragments,fragment_word);
                }else{
                    fragmentTransaction.show(fragment_word);
                }
                break;
            case 1:
                imageButton_learn.setBackgroundResource(R.drawable.icon_learn_select);
                textView_learn.setTextColor(Color.rgb(40,207,87));
                if(fragment_learn==null){
                    fragment_learn=new LearnFragment();
                    fragmentTransaction.add(R.id.id_fragments,fragment_learn);
                }else{
                    fragmentTransaction.show(fragment_learn);
                }
                break;
            case 2:
                imageButton_search.setBackgroundResource(R.drawable.icon_search_select);
                textView_search.setTextColor(Color.rgb(40,207,87));
                if(fragment_search==null){
                    fragment_search=new SearchFragment();
                    fragmentTransaction.add(R.id.id_fragments,fragment_search);
                }else{
                    fragmentTransaction.show(fragment_search);
                }
                break;
            case 3:
                imageButton_test.setBackgroundResource(R.drawable.icon_test_select);
                textView_test.setTextColor(Color.rgb(40,207,87));
                if(fragment_test==null){
                    fragment_test=new TestFragment();
                    fragmentTransaction.add(R.id.id_fragments,fragment_test);
                }else{
                    fragmentTransaction.show(fragment_test);
                }
                break;
            case 4:
                imageButton_personal.setBackgroundResource(R.drawable.icon_personal_select);
                textView_personal.setTextColor(Color.rgb(40,207,87));
                if(fragment_personal==null){
                    fragment_personal=new PersonalFragment();
                    fragmentTransaction.add(R.id.id_fragments,fragment_personal);
                }else{
                    fragmentTransaction.show(fragment_personal);
                }
                break;
        }
        fragmentTransaction.commit();
    }
}
