package com.benx.droptodo;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Project: DropTODO
 * Author:  Ben.X
 * Date:    2016/8/5
 */

public class AddTodoActivity extends AppCompatActivity {

    /**
     * *********** 变量声明 ***********
     */

    /** 控件 */
    // Toolbar
    private Toolbar toolbar;
    // FloatingMenu
    private FloatingActionsMenu FloatMenu;
    // 主题
    private EditText setTitle;
    // 提示
    private EditText setItem1;
    private EditText setItem2;
    private EditText setItem3;
    // 优先级
    private RadioGroup setPriority;
    // 时间
    private Button setTodoTime;
    private Button setAlarmTime;
    private TextView todoTime_show;
    private TextView alarmTime_show;
    // 按钮
    private Button cancelBtn;
    private Button submitBtn;

    /** 新建待办事项 */
    private ToDo newTodo;       // 新添加的待办事项
    private String Title;       // 标题
    private int Priority = -1;  // 优先级 -1 为无
    private long TodoTime;      // 执行时间
    private long AlarmTime;     // 提醒时间
    private String Item1;       // 提醒1
    private String Item2;       // 提醒2
    private String Item3;       // 提醒3

    /** 时间 变量 */
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;



    /**
     * *********** 重写方法 ***********
     */

    /**
     * AddTodoActivity 创建时调用
     *
     * @param savedInstanceState bundle 环境
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtodo);



        /** FloatMenu 部分 */
        FloatMenu = (FloatingActionsMenu) findViewById(R.id.FAB);
        FloatMenu.setVisibility(View.GONE);



        /** Toolbar 部分 */
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setSubtitle("Add A New Todo");
        toolbar.setNavigationIcon(R.drawable.ic_nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        /** 主要控件 部分 */
        // 主题
        setTitle = (EditText) findViewById(R.id.addtodo_settitle);


        // 优先级
        setPriority = (RadioGroup) findViewById(R.id.addtodo_setpriority);
        setPriority.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                // 获取设置好的优先级
                if (checkedId == R.id.todo_priority_vimportant){
                    Priority = ToDo.V_IMPORTANT;
                } else if (checkedId == R.id.todo_priority_important) {
                    Priority = ToDo.IMPORTANT;
                } else if (checkedId == R.id.todo_priority_normal) {
                    Priority = ToDo.NORMAL;
                } else if (checkedId == R.id.todo_priority_casual) {
                    Priority = ToDo.CASUAL;
                }
            }
        });


        // 提醒
        setItem1 = (EditText) findViewById(R.id.addtodo_setitem1);
        setItem2 = (EditText) findViewById(R.id.addtodo_setitem2);
        setItem3 = (EditText) findViewById(R.id.addtodo_setitem3);


        // 时间
        setTodoTime = (Button) findViewById(R.id.addtodo_settodotime);
        setAlarmTime = (Button) findViewById(R.id.addtodo_setalarmtime);
        todoTime_show = (TextView) findViewById(R.id.addtodo_todotime_show);
        alarmTime_show = (TextView) findViewById(R.id.addtodo_toalarmtime_show);

        // 待办时间选择器
        setTodoTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取 timepicker Dialog 的 view
                View view = View.inflate(getApplicationContext(), R.layout.layout_timepicker, null);

                // 获取 View 中的 DatePicker 和 TimePicker
                final DatePicker datePicker = (DatePicker) view.findViewById(R.id.timepicker_date);
                final TimePicker timePicker = (TimePicker) view.findViewById(R.id.timepicker_time);

                // 获取当前时间
                Calendar calendar = Calendar.getInstance();

                Log.d("getin", System.currentTimeMillis()+"");
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_YEAR);
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);

                // 初始化 DatePicker
                datePicker.init(year-1, month+5, day, new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view,final int _year, int _monthOfYear, int _dayOfMonth) {
                        // 更新年月日
                        year = _year;
                        month = _monthOfYear;
                        day = _dayOfMonth;

                        // 换位选择时间
                        datePicker.setVisibility(View.GONE);
                        timePicker.setVisibility(View.VISIBLE);
                    }
                });

                // 初始化 TimePicker
                timePicker.setIs24HourView(true);               // 设为24小时制
                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int _minute) {
                        // 更新时，分
                        hour = hourOfDay;
                        minute = _minute;
                    }
                });


                // 设置 Dialog
                final AlertDialog.Builder builder = new AlertDialog.Builder(AddTodoActivity.this);
                builder.setView(view);
                builder.setTitle("Set Date and Time");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // 时间显示格式
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm ");

                        // 新建 Calendar 对象
                        Calendar c = Calendar.getInstance();
                        c.clear();
                        c.set(year,month,day,hour,minute);

                        // 显示结果并更新按钮
                        todoTime_show.setText(dateFormat.format(c.getTimeInMillis()));
                        setTodoTime.setText("Set");

                        // 获取设置好的TodoTime
                        TodoTime = c.getTimeInMillis();

                    }
                });

                builder.show();

            }
        });


        // 提醒时间选择器
        setAlarmTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取 timepicker Dialog 的view
                View view = View.inflate(getApplicationContext(), R.layout.layout_timepicker, null);

                // 获取 View 中的 DatePicker 和 TimePicker
                final DatePicker datePicker = (DatePicker) view.findViewById(R.id.timepicker_date);
                final TimePicker timePicker = (TimePicker) view.findViewById(R.id.timepicker_time);

                // 获取当前时间
                // TODO: 2016/8/5 测试当前时间
                Calendar calendar = Calendar.getInstance();


                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_YEAR);
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);

                Log.d("getin", "onClick: year: "+year);
                Log.d("getin", "onClick: month: "+month);

                // 初始化 DatePicker
                // TODO: 2016/8/5 BUG?
                datePicker.init(year-1, month+5, day, new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view,final int _year, int _monthOfYear, int _dayOfMonth) {
                        // 更新年月日
                        year = _year;
                        month = _monthOfYear;
                        day = _dayOfMonth;

                        // 换位选择时间
                        datePicker.setVisibility(View.GONE);
                        timePicker.setVisibility(View.VISIBLE);
                    }
                });


                // 初始化 TimePicker
                timePicker.setIs24HourView(true);        // 设为24小时制
                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int _minute) {
                        // 更新时，分
                        hour = hourOfDay;
                        minute = _minute;
                    }
                });


                // 设置 Dialog
                final AlertDialog.Builder builder = new AlertDialog.Builder(AddTodoActivity.this);
                builder.setView(view);
                builder.setTitle("Set Date and Time");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm ");
                        Calendar c = Calendar.getInstance();
                        c.clear();
                        c.set(year,month,day,hour,minute);

                        // 显示结果并更新按钮
                        alarmTime_show.setText(dateFormat.format(c.getTimeInMillis()));
                        setAlarmTime.setText("Set");

                        // 获取设置好的 AlarmTime
                        AlarmTime = c.getTimeInMillis();

                    }
                });

                builder.show();

            }
        });


        /** 提交按钮 部分 */
        cancelBtn = (Button) findViewById(R.id.addtodo_cancel);
        submitBtn = (Button) findViewById(R.id.addtodo_submit);

        // 取消
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 提交
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取新待办事项 Title
                Title = setTitle.getText().toString();

                // 获取新待办事项 提醒
                Item1 = setItem1.getText().toString();
                Item2 = setItem2.getText().toString();
                Item3 = setItem3.getText().toString();

                // Title 和 Priority 不能为空
                if (Title.equals("") || Title == null){

                    Toast.makeText(AddTodoActivity.this, "You will need a title.", Toast.LENGTH_SHORT).show();

                } else if (Priority == -1){

                    Toast.makeText(AddTodoActivity.this, "You will need a priority.", Toast.LENGTH_SHORT).show();
                } else {

                    // 生成新的待办事项
                    newTodo = new ToDo(Title,TodoTime,AlarmTime,Priority,Item1,Item2,Item3);

                    // 待办事项添加到数据集中
                    MainActivity.ToDoList.add(newTodo);

                    // 关闭当前页面
                    finish();
                }
            }
        });

    }
}
