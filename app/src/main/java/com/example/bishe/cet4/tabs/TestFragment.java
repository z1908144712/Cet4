package com.example.bishe.cet4.tabs;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Skywilling on 2018/1/2.
 */

public class TestFragment extends Fragment {
    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private ListView test_list;
    private List<Map<String,String>> test_list_data;
    private SimpleAdapter simpleAdapter;
    private int count;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_tab_test,container,false);
        //初始化数据库
        initDataBase();
        //初始化控件
        initViews(view);
        //初始化数据
        initData();
        //初始化事件
        initEvents();
        return view;
    }

    private  void initDataBase(){
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }

    private void initData(){
        count=dbHelper.selectCount();
        test_list_data=new ArrayList<>();
        for(int i=0;i<count-1;i++){
            Map<String,String> map=new HashMap<>();
            map.put("test_item","测试第"+(i+1)+"天所学");
            test_list_data.add(map);
        }
        Map<String,String> map=new HashMap<>();
        map.put("test_item","测试今天所学");
        test_list_data.add(map);
        map=new HashMap<>();
        map.put("test_item","测试所有已学");
        test_list_data.add(map);
        simpleAdapter=new SimpleAdapter(getContext(),test_list_data,R.layout.test_list_item_layout,new String[]{"test_item"},new int[]{R.id.id_test_list_item});
    }

    private void initViews(View view){
        test_list=view.findViewById(R.id.id_test_list);
    }

    private void initEvents(){
        test_list.setAdapter(simpleAdapter);
        test_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(getActivity())
                        .setMessage("确定开始测试？")
                        .setNegativeButton("取消",null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(),">>>"+position,Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create()
                        .show();

            }
        });
    }
}
