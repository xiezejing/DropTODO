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
//    public static final int V_IMPORTANT = Color.argb(255,255,23,68);
//    public static final int IMPORTANT = Color.argb(255,255,165,79);
//    public static final int NORMAL = Color.argb(255,0,145,234);
//    public static final int CASUAL = Color.argb(255,29,233,182);
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

    /**
     * *********** 构造方法 ***********
     */

    // TODO　测试用，到时候记得删除
    public ToDo() {
        Calendar calendar = Calendar.getInstance();

        todoPriority = CASUAL;

        todoTitle = "This is Title";

        todoItem1 = "hello，there";
        todoItem2 = "hello, android!";
        todoItem3 = "what i have to do , i dont know yet.";

        todoTime = calendar.getTimeInMillis();

        todoDone = false;
        todoReorderMode = REORDER_NORMALMODE;
    }
}
