/*******************************************************************************
 * Copyright (c) 2013,  Paul Daniels
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.yushilong.xwalkdemo;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jockeyjs.Jockey;
import com.jockeyjs.JockeyAsyncHandler;
import com.jockeyjs.JockeyCallback;
import com.jockeyjs.JockeyHandler;
import com.jockeyjs.JockeyImpl;

import org.xwalk.core.XWalkJavascriptResult;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import java.util.HashMap;
import java.util.Map;

import static com.jockeyjs.NativeOS.nativeOS;

public class MainActivity extends AppCompatActivity {

    public XWalkView webView;

    public LinearLayout toolbar;
    public boolean isFullscreen = false;

    private Jockey jockey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (LinearLayout) findViewById(R.id.colorsView);

        webView = (XWalkView) findViewById(R.id.webView);

        OnClickListener toolbarListener = new OnClickListener() {

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                ImageButton btn = (ImageButton) v;
                ColorDrawable background = (ColorDrawable) btn.getBackground();
                int colorId = background.getColor();
                String hex = String.format("#%06X", 0xFFFFFF & colorId);

                HashMap<String, String> payload = new HashMap<String, String>();
                payload.put("color", hex);

                updateColor(payload);
            }
        };

        ImageButton btnRed = (ImageButton) findViewById(R.id.color_red);
        ImageButton btnGreen = (ImageButton) findViewById(R.id.color_green);
        ImageButton btnYellow = (ImageButton) findViewById(R.id.color_yellow);
        ImageButton btnOrange = (ImageButton) findViewById(R.id.color_orange);
        ImageButton btnPink = (ImageButton) findViewById(R.id.color_pink);
        Button btnBlue = (Button) findViewById(R.id.color_blue);
        ImageButton btnWhite = (ImageButton) findViewById(R.id.color_white);

        btnRed.setOnClickListener(toolbarListener);
        btnGreen.setOnClickListener(toolbarListener);
        btnYellow.setOnClickListener(toolbarListener);
        btnOrange.setOnClickListener(toolbarListener);
        btnPink.setOnClickListener(toolbarListener);
//        btnBlue.setOnClickListener(toolbarListener);
        btnWhite.setOnClickListener(toolbarListener);
        //
        btnBlue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Map map = new HashMap();
                map.put("key", "收到了app发送过来的事件" + SystemClock.currentThreadTimeMillis());
                jockey.send("event", webView, map);
            }
        });
    }

    protected void updateColor(Map<String, String> payload) {
        jockey.send("color-change", webView, payload);
    }

    @Override
    protected void onStart() {
        super.onStart();

        jockey = JockeyImpl.getDefault(webView);

        jockey.configure(webView);

        jockey.setXWalkViewClient(new XWalkResourceClient(webView) {
            @Override
            public void onLoadFinished(XWalkView view, String url) {
                super.onLoadFinished(view, url);
                Log.d("webViewClient", "page finished loading!");
            }
        });


        setJockeyEvents();

        webView.setUIClient(new XWalkUIClient(webView) {
            @Override
            public boolean onJsAlert(XWalkView view, String url, String message, XWalkJavascriptResult result) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT)
                        .show();
                result.confirm();
                return true;
            }
        });

        webView.load("file:///android_asset/index.html", null);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_showimage:
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("feed", "http://www.google.com/doodles/doodles.xml");

                jockey.send("show-image", webView, new JockeyCallback() {
                    public void call() {
                        AlertDialog.Builder alert = new AlertDialog.Builder(
                                MainActivity.this);
                        alert.setTitle("Image loaded");
                        alert.setMessage("callback in Android from JS event");
                        alert.setNegativeButton("Score!",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                    }
                                });
                        alert.show();
                    }
                });
                break;
        }

        return true;
    }

    private Handler _handler = new Handler();

    public void setJockeyEvents() {

        jockey.on("toggle-fullscreen",
                nativeOS(this)
                        .vibrate(50)
                        .toast("Event clicked", Toast.LENGTH_SHORT),
                new JockeyHandler() {
                    @Override
                    protected void doPerform(Map<Object, Object> payload) {
                        toggleFullscreen();
                    }
                });

        jockey.on("toggle-fullscreen-with-callback", new JockeyAsyncHandler() {
            @Override
            protected void doPerform(Map<Object, Object> payload) {
                _handler.post(new Runnable() {
                    @Override
                    public void run() {
                        toggleFullscreen();
                    }
                });

            }
        });

        jockey.on("log", new JockeyHandler() {
            @Override
            public void doPerform(Map<Object, Object> payload) {
                String value = "color=" + payload.get("color");
                Log.d("jockey", value);
                //get cookie
            }
        });

        jockey.on("event", new JockeyHandler() {
            @Override
            public void doPerform(Map<Object, Object> payload) {
                String value = (String) payload.get("key");
                new android.support.v7.app.AlertDialog.Builder(MainActivity.this).setMessage(value).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            }
        });
    }

    public void toggleFullscreen() {
        Window w = getWindow();

        if (isFullscreen) {
            w.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            w.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            toolbar.setVisibility(LinearLayout.VISIBLE);
            isFullscreen = false;
        } else {
            w.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            w.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

            toolbar.setVisibility(LinearLayout.GONE);
            isFullscreen = true;
        }
    }
}
