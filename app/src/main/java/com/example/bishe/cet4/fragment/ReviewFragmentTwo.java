package com.example.bishe.cet4.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.activity.WordActivity;
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReviewFragmentTwo extends Fragment implements View.OnClickListener{
    private View view=null;
    private ScrollView scrollView=null;
    private SQLiteDatabase db=null;
    private DBHelper dbHelper=null;
    private List<Map<String,Object>> datas=null;
    private int days;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view=inflater.inflate(R.layout.review_tab_fragment_2_layout,container,false);
        }
        //初始化数据库
        initDataBase();
        return view;
    }

    @Override
    public void onStart() {
        //初始化数据
        initData();
        //初始化控件
        initViews();
        super.onStart();
    }


    private void initDataBase(){
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }

    private void initData(){
        days=dbHelper.selectCountFromWordsPlan();
        datas=new ArrayList<>();
        for(int i=1;i<=days;i++){
            Map<String,Object> map=dbHelper.select_words_and_count_from_collection_word_by_belongto(i);
            if(map!=null){
                datas.add(map);
            }
        }
    }

    private void initViews(){
        if(datas.size()>0){
            scrollView=view.findViewById(R.id.id_scrollView);
            LinearLayout linearLayout=new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            for(int i=0;i<datas.size();i++){
                View view=LayoutInflater.from(getContext()).inflate(R.layout.fragment_two_item_layout,null);
                view.setOnClickListener(this);
                view.setTag(i);
                ViewHolder viewHolder=new ViewHolder();
                viewHolder.tv_num=view.findViewById(R.id.id_daynum);
                viewHolder.tv_content=view.findViewById(R.id.id_content);
                viewHolder.tv_num.setText(String.valueOf(datas.get(i).get("day")));
                viewHolder.tv_content.setText((String)datas.get(i).get("num"));
                linearLayout.addView(view);
            }
            scrollView.removeAllViews();
            scrollView.addView(linearLayout);
        }
    }

    @Override
    public void onClick(View v) {
        final int id=(int)v.getTag();
        new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage("开始第"+datas.get(id).get("day")+"天的查缺？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(getContext(), WordActivity.class);
                        intent.putExtra("day",(int)datas.get(id).get("day"));
                        intent.putExtra("words",(String)datas.get(id).get("words"));
                        intent.putExtra("mode",WordActivity.MODE_3);
                        startActivity(intent);
                    }
                })
                .create().show();
    }

    public class ViewHolder{
        TextView tv_num;
        TextView tv_content;
    }
}
