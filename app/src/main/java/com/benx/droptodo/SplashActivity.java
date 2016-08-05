package com.benx.droptodo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.WindowManager;

/**
 *
 * Project: DropTODO
 * Author:  Ben.X
 * Date:    2016/8/5
 *
 */

public class SplashActivity extends Activity {

    /**
     * Activity 启动时
     *
     * @param icicle 上下文 Bundle
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 加载内容
        setContentView(R.layout.activity_splash);

        // 处理是否是第一次启动
        new Handler().postDelayed(new Runnable() {
            public void run() {
                {
                    // 获取 SharedPreference 记录
                    SharedPreferences getPrefs = PreferenceManager
                            .getDefaultSharedPreferences(getBaseContext());

                    // 第一次启动时设置标记
                    boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                    // 如果是第一次启动
                    if (isFirstStart) {

                        // 加载 SplashIntro 引导页
                        Intent i = new Intent(SplashActivity.this, SplashIntro.class);
                        startActivity(i);

                        // 改写启动记录
                        SharedPreferences.Editor e = getPrefs.edit();
                        e.putBoolean("firstStart", false);
                        e.apply();

                    } else {

                        // 启动主 MainActivity
                        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                        SplashActivity.this.startActivity(mainIntent);

                        // 关闭当前 Activity
                        finish();
                    }
                }
            }
        }, 2000);
    }
}
