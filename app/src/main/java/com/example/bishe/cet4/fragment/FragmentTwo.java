package com.example.bishe.cet4.fragment;

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
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;
import com.example.bishe.cet4.object.WordCollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentTwo extends Fragment {
    private View view=null;
    private ScrollView scrollView=null;
    private SQLiteDatabase db=null;
    private DBHelper dbHelper=null;
    private List<Map<String,Integer>> datas=null;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view=inflater.inflate(R.layout.reviewtab_fragment_2_layout,container,false);
        }
        //初始化数据库
        initDataBase();
        //初始化数据
        initData();
        //初始化控件
        initViews();
        return view;
    }

    private void initDataBase(){
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }

    private void initData(){
        List<WordCollection> allCollectionWords=dbHelper.getAll_Word_WordCollection();
        datas=new ArrayList<>();
        if(allCollectionWords.size()>0){
            List<String> allLearnedWords=dbHelper.selectAllLearnWord();
            for(int i=0;i<allLearnedWords.size();i++){
                System.out.println(allLearnedWords.get(i));
                int num=0;
                for(int j=0;j<allCollectionWords.size();j++){
                    if(allLearnedWords.get(i).contains(String.valueOf(dbHelper.selectIdByEnglish(allCollectionWords.get(j).getEnglish())))){
                        num++;
                    }
                }
                if(num>0){
                    Map<String,Integer> map=new HashMap<>();
                    map.put("num",num);
                    datas.add(map);
                    map=new HashMap<>();
                    map.put("day",i+1);
                    datas.add(map);
                }
            }
        }
    }

    private void initViews(){
        System.out.println(datas.size());
        if(datas.size()>0){
            scrollView=view.findViewById(R.id.id_scrollView);
            LinearLayout linearLayout=new LinearLayout(getContext());
            for(int i=0;i<datas.size();i++){
                View view=LayoutInflater.from(getContext()).inflate(R.layout.fragment_two_item_layout,null);
                ViewHolder viewHolder=new ViewHolder();
                viewHolder.tv_num=view.findViewById(R.id.id_daynum);
                viewHolder.tv_content=view.findViewById(R.id.id_content);
                viewHolder.tv_num.setText(datas.get(i).get("day"));
                viewHolder.tv_content.setText(datas.get(i).get("num"));
                linearLayout.addView(view);
            }
            scrollView.removeAllViews();
            scrollView.addView(linearLayout);
        }
    }

    public class ViewHolder{
        TextView tv_num;
        TextView tv_content;
    }
}
