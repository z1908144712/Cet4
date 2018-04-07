package com.example.bishe.cet4.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.bishe.cet4.R;

public class TestActivity extends Activity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testactivity_layout);
    }
}
