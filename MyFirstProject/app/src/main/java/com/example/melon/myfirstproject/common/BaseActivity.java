package com.example.melon.myfirstproject.common;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.melon.myfirstproject.R;

public class BaseActivity extends BaseCore {
    public static Context context;
    public static Toolbar toolbar;
    public static FragmentManager FM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

    }
}
