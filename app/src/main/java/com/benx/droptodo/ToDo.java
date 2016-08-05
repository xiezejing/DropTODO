package com.benx.droptodo;

import android.graphics.Color;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Ben.X on 2016/8/2.
 */
public class ToDo implements Serializable {
    /**
     * *********** 常量 ***********
     */
    public static final int REORDER_QUICKMODE = 1;
    public static final int REORDER_NORMALMODE = 0;

    /** 优先级 颜色 */
    public static final int V_IMPORTANT = 10;
    public static final int IMPORTANT = 11;
    public static final int NORMAL = 12;
    public static final int CASUAL = 13;



    /**
     * *********** 属性 ***********
     */

    /** 待办事项 标题 */
    public String todoTitle;

    /** 待办事项 提醒 */
    public String todoItem1;
    public String todoItem2;
    public String todoItem3;

    /** 待办事项 其他备注 */
    public String todoOthers;

    /** 待办事项 执行时间 */
    public long todoTime;

    /** 待办事项 提醒时间 */
    public long toAlarmTime;

    /** 待办事项 完成情况 */
    public boolean todoDone;

    /** 待办事项 优先级 */
    public int todoPriority;

    /** 待办事项 排序模式 */
    public int todoReorderMode;

    /** 待办事项 创建时间 */
    public long todoCreateTime;

    /**
     * *********** 构造方法 ***********
     */
    /**
     *
     *
     * @param Title 标题
     * @param TodoTime 执行时间
     * @param AlarmTime 提醒时间
     * @param Priority 优先级
     * @param Item1 提醒 1
     * @param Item2 提醒 2
     * @param Item3 提醒 3
     */
    public ToDo(String Title, long TodoTime, long AlarmTime, int Priority,String Item1, String Item2,String Item3) {

        Calendar c = Calendar.getInstance();
        todoCreateTime = c.getTimeInMillis();

        todoTitle = Title;
        todoTime = TodoTime;
        toAlarmTime = AlarmTime;
        todoPriority = Priority;
        todoItem1 = Item1;
        todoItem2 = Item2;
        todoItem3 = Item3;
        todoDone = false;
        todoReorderMode = REORDER_NORMALMODE;
    }
}
