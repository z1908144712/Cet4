package com.example.bishe.cet4.tabs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.activity.LearnInfoActivity;
import com.example.bishe.cet4.activity.LoadingRateOfLearning;
import com.example.bishe.cet4.activity.LoadingUserInfoActivity;
import com.example.bishe.cet4.activity.LoadingSyncActivity;
import com.example.bishe.cet4.activity.LoginActivity;
import com.example.bishe.cet4.activity.WordCollectionActivity;
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;
import com.example.bishe.cet4.function.MyToast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Skywilling on 2018/1/2.
 */

public class PersonalFragment extends Fragment implements AdapterView.OnItemClickListener{
    private ListView listView_personal_item=null;
    private SimpleAdapter simpleAdapter=null;
    private List<Map<String,Object>> personal_items=null;
    private LinearLayout ll_login=null;
    private String user_id=null;
    private int plandays;
    private int learneddays;
    private int words_count;
    private int learned_words_num;
    private int plandays_min;
    private int plandays_max;
    private int last_words_num;
    private SQLiteDatabase db=null;
    private DBHelper dbHelper=null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_tab_personal,container,false);
        //初始化数据库
        initDataBase();
        //初始化控件
        initViews(view);
        //初始化监听器
        initEvents();
        return view;
    }

    private void initDataBase(){
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }

    private void initData(){
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("plandays",Context.MODE_PRIVATE);
        plandays=sharedPreferences.getInt("plandays",-1);
        words_count=dbHelper.selectCountFromWords();
        learneddays=dbHelper.selectCountFromWordsPlan();
        learned_words_num=dbHelper.select_learned_words_num();
        last_words_num=words_count-learned_words_num;
        plandays_min=(last_words_num%50==0?last_words_num/50:(last_words_num/50+1))+learneddays;
        plandays_max=(last_words_num%16==0?last_words_num/16:(last_words_num/16+1))+learneddays;
        personal_items=new ArrayList<>();
        Map<String,Object> map=new HashMap<>();
        map.put("icon",R.drawable.ic_assignment_turned);
        map.put("name","单词计划");
        map.put("value",plandays+"天");
        personal_items.add(map);
        map=new HashMap<>();
        map.put("icon",R.drawable.ic_insert_chart);
        map.put("name","学习进度");
        map.put("value","");
        personal_items.add(map);
        map=new HashMap<>();
        map.put("icon",R.drawable.ic_date_range);
        map.put("name","学习情况");
        map.put("value","");
        personal_items.add(map);
        map=new HashMap<>();
        map.put("icon",R.drawable.ic_receip);
        map.put("name","单词收藏");
        map.put("value","");
        personal_items.add(map);
        map=new HashMap<>();
        map.put("icon",R.drawable.ic_cloud_queue);
        map.put("name","数据同步");
        map.put("value","");
        personal_items.add(map);
        simpleAdapter=new SimpleAdapter(getContext(),personal_items,R.layout.personal_item_layout,new String[]{"icon","name","value"},new int[]{R.id.id_personal_item_icon,R.id.id_personal_item_name,R.id.id_personal_item_value});
        listView_personal_item.setAdapter(simpleAdapter);
    }

    private void initViews(View view){
        ll_login=view.findViewById(R.id.id_login_icon);
        listView_personal_item=view.findViewById(R.id.id_personal_item);
    }

    private void initEvents(){
        listView_personal_item.setOnItemClickListener(this);
    }

    private void changeView1(){
        ll_login.removeAllViews();
        user_id=null;
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("login",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("username",null);
        editor.putLong("login_time",-1);
        editor.putBoolean("isLogin",false);
        editor.putString("user_id",null);
        editor.commit();
        View view=LayoutInflater.from(getContext()).inflate(R.layout.login_layout,null);
        ImageView iv_login=view.findViewById(R.id.id_login);
        iv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),LoginActivity.class));
            }
        });
        ll_login.addView(view);
    }

    private void changeView2(){
        ll_login.removeAllViews();
        CardView cardView=new CardView(getContext());
        View view=LayoutInflater.from(getContext()).inflate(R.layout.logined_layout,null);
        TextView tv_username=view.findViewById(R.id.id_tv_username);
        TextView tv_id=view.findViewById(R.id.id_tv_id);
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("login",Context.MODE_PRIVATE);
        user_id=sharedPreferences.getString("user_id",null);
        String username=sharedPreferences.getString("username",null);
        tv_username.setText(username);
        tv_id.setText("ID:"+user_id);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), LoadingUserInfoActivity.class);
                intent.putExtra("user_id",user_id);
                startActivity(intent);
            }
        });
        cardView.addView(view);
        ll_login.addView(cardView);
    }

    private void reViews(){
        if(isLogin()){
            changeView2();
        }else{
            changeView1();
        }
    }

    private boolean isLogin(){
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        long login_time=sharedPreferences.getLong("login_time",-1);
        boolean isLogin=sharedPreferences.getBoolean("isLogin",false);
        if(login_time==-1||isLogin==false){
            return false;
        }else{
            Date date=new Date();
            if(date.getTime()-login_time>24*60*60*1000){
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("username",null);
                editor.putLong("login_time",-1);
                editor.putBoolean("isLogin",false);
                editor.putString("user_id",null);
                editor.commit();
                return false;
            }else{
                return true;
            }
        }
    }

    private void updateSharePreferences(){
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("plandays",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("plandays",plandays);
        editor.putInt("plannum",last_words_num%(plandays-learneddays)==0?last_words_num/(plandays-learneddays):(last_words_num/(plandays-learneddays))+1);
        editor.commit();
    }

    private void updatePlanDays() {
        new AlertDialog.Builder(getContext())
                .setMessage("确定修改单词计划")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("单词计划");
                        final EditText my_input = new EditText(builder.getContext());
                        my_input.setHint("输入"+plandays_min+"-"+plandays_max+"之间的数字");
                        my_input.setBackgroundResource(R.drawable.word_plan_input_bg);
                        my_input.setInputType(InputType.TYPE_CLASS_NUMBER);
                        my_input.setBackgroundColor(getResources().getColor(R.color.gray_white));
                        my_input.setWidth(800);
                        LinearLayout linearLayout = new LinearLayout(builder.getContext());
                        linearLayout.setGravity(Gravity.CENTER);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        linearLayout.setLayoutParams(layoutParams);
                        linearLayout.addView(my_input);
                        builder.setView(linearLayout);
                        builder.setCancelable(false);
                        builder.setPositiveButton("确定", null);
                        builder.setNegativeButton("取消", null);
                        final AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String input = my_input.getText().toString().trim();
                                if (input.equals("")) {
                                    new MyToast(getContext(), MyToast.EMPTY).show();
                                } else {
                                    int days = Integer.valueOf(my_input.getText().toString());
                                    if (days >= plandays_min && days <= plandays_max) {
                                        if (days != plandays) {
                                            plandays = days;
                                            updateSharePreferences();
                                            alertDialog.dismiss();
                                            new AlertDialog.Builder(getContext())
                                                    .setMessage("计划将于明天生效")
                                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            onStart();
                                                        }
                                                    })
                                                    .setCancelable(false)
                                                    .create().show();
                                        } else {
                                            new MyToast(getContext(), MyToast.NUMBER_SAME).show();
                                        }
                                    } else {
                                        new MyToast(getContext(), "数字应在"+plandays_min+"-"+plandays_max+"之间").show();
                                    }
                                }
                            }
                        });
                    }
                }).create().show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                updatePlanDays();
                break;
            case 1:
                startActivity(new Intent(getContext(),LoadingRateOfLearning.class));
                break;
            case 2:
                startActivity(new Intent(getContext(),LearnInfoActivity.class));
                break;
            case 3:
                startActivity(new Intent(getContext(),WordCollectionActivity.class));
                break;
            case 4:
                if(user_id==null){
                    startActivity(new Intent(getContext(), LoginActivity.class));
                }else{
                    startActivity(new Intent(getContext(), LoadingSyncActivity.class));
                }
                break;
        }
    }

    @Override
    public void onStart() {
        //初始化数据
        initData();
        reViews();
        super.onStart();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            //初始化数据
            initData();
            reViews();
        }
    }
}
