package com.benx.droptodo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 *
 * Project: DropTODO
 * Author:  Ben.X
 * Date:    2016/8/3
 *
 */

public class LoopText extends TextView {
    public LoopText(Context context) {
        super(context);
    }

    public LoopText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoopText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 重写，使始终返回被聚焦
     *
     * @return true
     */
    @Override
    public boolean isFocused() {
        return true;
    }
}
