package com.benx.droptodo;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro2;

/**
 *
 * Project: DropTODO
 * Author:  Ben.X
 * Date:    2016/8/5
 *
 */

public class SplashIntro extends AppIntro2 {

    /**
     * 加载主 Activity
     */
    private void loadMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * 启动时调用
     */
    @Override
    protected void onStart() {
        super.onStart();

        // 加载引导页
        addSlide(IntroSlide.newInstance(R.layout.layout_splash_info1));
        addSlide(IntroSlide.newInstance(R.layout.layout_splash_info2));
        addSlide(IntroSlide.newInstance(R.layout.layout_splash_info3));
        addSlide(IntroSlide.newInstance(R.layout.layout_splash_info4));

    }


    /**
     * 点击跳过按钮时调用
     *
     * @param currentFragment 目标 Fragment
     */
    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);

        // 加载主 Activity
        loadMainActivity();

        // 显示提示
        Toast.makeText(SplashIntro.this, "You can check it in Help", Toast.LENGTH_SHORT).show();

    }


    /**
     * 确认按钮被点击时调用
     *
     * @param currentFragment 目标 Fragment
     */
    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        // 加载主 Activity
        loadMainActivity();
    }
}
