package com.yushilong.xwalkdemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.xwalk.core.XWalkCookieManager;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

/**
 * Created by yushilong on 16/5/13.
 */
public class WebActivity extends AppCompatActivity {
    XWalkCookieManager xWalkCookieManager;
    private long pre;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        final XWalkView xWalkView = (XWalkView) findViewById(R.id.webView);
        final EditText editText = (EditText) findViewById(R.id.editText);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.setMax(100);
        xWalkCookieManager = new XWalkCookieManager();
        xWalkView.setResourceClient(new XWalkResourceClient(xWalkView) {
            @Override
            public void onProgressChanged(XWalkView view, int progressInPercent) {
                super.onProgressChanged(view, progressInPercent);
                progressBar.setProgress(progressInPercent);
            }

            @Override
            public void onLoadStarted(XWalkView view, String url) {
                super.onLoadStarted(view, url);
                pre = System.currentTimeMillis();
            }

            @Override
            public void onLoadFinished(XWalkView view, String url) {
                super.onLoadFinished(view, url);
                Log.e("test", "onLoadFinished");
                long current = System.currentTimeMillis();
                long d = current - pre;
                new AlertDialog.Builder(WebActivity.this).setMessage("网页加载总耗时:" + d + "ms").setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            }
        });
        xWalkView.setUIClient(new XWalkUIClient(xWalkView) {
            @Override
            public void onPageLoadStopped(XWalkView view, String url, LoadStatus status) {
                super.onPageLoadStopped(view, url, status);
                Log.e("test", "onPageLoadStopped");
            }
        });
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(url))
                    xWalkView.load(url, null);
            }
        });
        findViewById(R.id.cookie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = xWalkView.getUrl();
                if (!TextUtils.isEmpty(url)) {
                    String cookie = xWalkCookieManager.getCookie(url);
                    new AlertDialog.Builder(WebActivity.this).setMessage(cookie).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                }
            }
        });
    }
}
