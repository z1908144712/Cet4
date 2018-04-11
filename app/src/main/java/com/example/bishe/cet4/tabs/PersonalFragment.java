package com.example.bishe.cet4.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.activity.RateOfLearningActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Skywilling on 2018/1/2.
 */

public class PersonalFragment extends Fragment implements AdapterView.OnItemClickListener{
    private ListView listView_personal_item=null;
    private SimpleAdapter simpleAdapter=null;
    private List<Map<String,Object>> personal_items;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_tab_personal,container,false);
        //初始化数据
        initData();
        //初始化控件
        initViews(view);
        //初始化监听器
        initEvents();
        return view;
    }

    private void initData(){
        personal_items=new ArrayList<>();
        Map<String,Object> map=new HashMap<>();
        map.put("icon",R.drawable.ic_insert_chart);
        map.put("name","学习进度");
        map.put("right",R.drawable.ic_keyboard_arrow_right);
        personal_items.add(map);
        map=new HashMap<>();
        map.put("icon",R.drawable.ic_receip);
        map.put("name","单词收藏");
        map.put("right",R.drawable.ic_keyboard_arrow_right);
        personal_items.add(map);
        map=new HashMap<>();
        map.put("icon",R.drawable.ic_cloud_queue);
        map.put("name","数据同步");
        map.put("right",R.drawable.ic_keyboard_arrow_right);
        personal_items.add(map);
        simpleAdapter=new SimpleAdapter(getContext(),personal_items,R.layout.personal_item_layout,new String[]{"icon","name","right"},new int[]{R.id.id_personal_item_icon,R.id.id_personal_item_name,R.id.id_personal_item_right});
    }

    private void initViews(View view){
        listView_personal_item=view.findViewById(R.id.id_personal_item);
        listView_personal_item.setAdapter(simpleAdapter);

    }

    private void initEvents(){
        listView_personal_item.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                Intent intent=new Intent();
                intent.setClass(getContext(),RateOfLearningActivity.class);
                startActivity(intent);
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }
}
