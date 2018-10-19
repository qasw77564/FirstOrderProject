package com.melonltd.naber.view.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.common.base.Strings;
import com.melonltd.naber.BuildConfig;
import com.melonltd.naber.R;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.api.ThreadCallback;
import com.melonltd.naber.model.service.SPService;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.vo.AppVersionLogVo;

public class BaseIntroActivity extends AppCompatActivity {
    private static final String TAG =  BaseIntroActivity.class.getSimpleName();
    private SimpleDraweeView img_intro;
    private Button btn_intro;
    private String[] others = new String[]{"我知道了"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_baseintro);
    }

    @Override
    protected void onResume() {
        super.onResume();
        img_intro = findViewById(R.id.imageView_intro);
        btn_intro = findViewById(R.id.btn_intro);
        btn_intro.setVisibility(View.GONE);
        btn_intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity();
            }
        });
        others = new String[]{"我知道了"};
        this.checkapp();
    }

    private boolean checkapp() {
        ApiManager.checkAppVersion(new ThreadCallback(this) {
            @Override
            public void onSuccess(String responseBody) {
                AppVersionLogVo vo = Tools.JSONPARSE.fromJson(responseBody, AppVersionLogVo.class);
                if (!vo.version.equals(BuildConfig.VERSION_NAME)) {
                    if(vo.need_upgrade.equals("Y")){
                        SPService.getInstance(getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE));
                        SPService.removeAll();
                        others = new String[]{"前往更新"};
                    }

                    new AlertView.Builder()
                            .setContext(BaseIntroActivity.this)
                            .setTitle("NABER 系統提示")
                            .setMessage("您目前的APP版本(V" + BuildConfig.VERSION_NAME + ")，不是最新版本(V" + vo.version + ")，為了您的使用權益，\n請前往Google Play更新您的App")
                            .setStyle(AlertView.Style.Alert)
                            .setOthers(others)
                            .setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(Object o, int position) {
                                    if (position >= 0) {
                                        if (others[position].equals("前往更新")) {
                                            // TODO go to google play
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse("market://details?id=com.melonltd.naber"));
                                            startActivity(intent);
                                        }else {
                                            getIntro();
                                        }
                                    }
                                }
                            })
                            .build()
                            .setCancelable(false)
                            .show();
                }else {
                    getIntro();
                }

            }
            @Override
            public void onFail(Exception error, String msg) {
                Log.i(TAG,msg);
                getIntro();
            }
        });
        return true;
    }

    private void getIntro() {
        ApiManager.appIntroBulletin(new ThreadCallback(getApplicationContext()) {
            @Override
            public void onSuccess(String responseBody) {
                if (Strings.isNullOrEmpty(responseBody)) {
                    startActivity();
                } else {
                    img_intro.setImageURI(Uri.parse(Tools.JSONPARSE.fromJson(responseBody, String.class)));
                    btn_intro.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFail(Exception error, String msg) {
                startActivity();
            }
        });
    }

    private void startActivity () {
        startActivity(new Intent(BaseIntroActivity.this,BaseActivity.class));
    }
}
