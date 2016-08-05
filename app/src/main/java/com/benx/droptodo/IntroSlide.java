package com.benx.droptodo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 * Project: DropTODO
 * Author:  Ben.X
 * Date:    2016/8/5
 *
 */

public class IntroSlide extends Fragment{

    private int layoutResId;

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";

    /**
     * 新建 introSlide 实例
     *
     * @param layoutResId 滑动页布局的 id
     * @return 一个实例化的滑动页
     */
    public static IntroSlide newInstance(int layoutResId) {
        IntroSlide sampleSlide = new IntroSlide();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    public IntroSlide() {}


    /**
     * 被创建时调用
     *
     * @param savedInstanceState 获取 Bundle 上下文
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID))
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
    }


    /**
     * View 被创建时调用，通常在 onCreate 之后
     *
     * @param inflater Layout投射器
     * @param container View 容器
     * @param savedInstanceState Bundle 上下文
     * @return 返回一个 View 实例
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(layoutResId, container, false);
    }

}
