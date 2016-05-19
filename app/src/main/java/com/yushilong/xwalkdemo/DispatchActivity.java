package com.yushilong.xwalkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by yushilong on 16/5/13.
 */
public class DispatchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch);
    }

    public void testLoadUrl(View view) {
        startActivity(new Intent(this, WebActivity.class));
    }

    public void testJockeyJs(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
