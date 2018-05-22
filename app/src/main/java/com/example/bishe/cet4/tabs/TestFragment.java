package com.example.bishe.cet4.tabs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.adapter.FragmentsPagerAdapter;
import com.example.bishe.cet4.fragment.TestFragmentOne;
import com.example.bishe.cet4.fragment.TestFragmentTwo;

import java.util.ArrayList;
import java.util.List;

public class TestFragment extends Fragment implements View.OnClickListener {
    private View view=null;
    private ViewPager viewPager=null;
    private TextView tv_tab1=null;
    private TextView tv_tab2=null;
    private View tab1_line=null;
    private View tab2_line=null;
    private List<Fragment> fragments=null;
    private FragmentsPagerAdapter fragmentsPagerAdapter=null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view=inflater.inflate(R.layout.activity_tab_test,container,false);
        }
        //初始化控件
        initViews();
        //初始化事件
        initEvents();
        //初始化数据
        initData();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden){
            //初始化数据
            initData();
        }
        super.onHiddenChanged(hidden);
    }

    private void initViews(){
        viewPager=view.findViewById(R.id.id_viewpager);
        tv_tab1=view.findViewById(R.id.id_tab1);
        tv_tab2=view.findViewById(R.id.id_tab2);
        tab1_line=view.findViewById(R.id.id_tab1_line);
        tab2_line=view.findViewById(R.id.id_tab2_line);
    }

    private void initEvents(){
        tv_tab1.setOnClickListener(this);
        tv_tab2.setOnClickListener(this);
        viewPager.setOnPageChangeListener(new TestFragment.PagerChangeListener());
    }

    private void initData(){
        fragments=new ArrayList<>();
        fragments.add(new TestFragmentOne());
        fragments.add(new TestFragmentTwo());
        resetTabs();
        tv_tab1.setTextColor(getResources().getColor(R.color.colorPrimary));
        tab1_line.setVisibility(View.VISIBLE);
        fragmentsPagerAdapter =new FragmentsPagerAdapter(getChildFragmentManager(),fragments);
        viewPager.setAdapter(fragmentsPagerAdapter);
        viewPager.setCurrentItem(0);
    }

    private void resetTabs(){
        tv_tab1.setTextColor(Color.GRAY);
        tv_tab2.setTextColor(Color.GRAY);
        tab1_line.setVisibility(View.INVISIBLE);
        tab2_line.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_tab1:
                resetTabs();
                viewPager.setCurrentItem(0);
                tv_tab1.setTextColor(getResources().getColor(R.color.colorPrimary));
                tab1_line.setVisibility(View.VISIBLE);
                break;
            case R.id.id_tab2:
                resetTabs();
                tv_tab2.setTextColor(getResources().getColor(R.color.colorPrimary));
                tab2_line.setVisibility(View.VISIBLE);
                viewPager.setCurrentItem(1);
                break;
        }
    }

    public class PagerChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            viewPager.setCurrentItem(position);
            resetTabs();
            switch (position){
                case 0:
                    tv_tab1.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tab1_line.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    tv_tab2.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tab2_line.setVisibility(View.VISIBLE);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
