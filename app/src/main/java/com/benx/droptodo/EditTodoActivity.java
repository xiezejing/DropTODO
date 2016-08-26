package com.benx.droptodo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Project: DropTODO
 * Author:  Ben.X
 * Date:    2016/8/5
 */
public class EditTodoActivity extends AppCompatActivity {

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


    /** 修改待办事项的基本属性 */
    private int position;       /** 获取的数据集中的位置 */
    private ToDo thisTodo;      /** 编辑的目标待办事项  */
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

    // TODO: 2016/8/5 临时
    public String TAG = "getin";

    /**
     * *********** 重写方法 ***********
     */

    @Override
    public void onBackPressed() {
        MainActivity.saveData();
        super.onBackPressed();
    }

    /**
     * EditTodoActivity 创建时调用
     *
     * @param savedInstanceState bundle 环境
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** 借用 AddTodoActivity 的布局 */
        setContentView(R.layout.activity_addtodo);


        /** 获取目标待办事项 */
        position = getIntent().getIntExtra("position", -1);
        if (position == -1) {
            Toast.makeText(EditTodoActivity.this, "Failed to get position", Toast.LENGTH_SHORT).show();
            finish();
        }

        thisTodo = MainActivity.ToDoList.get(position);


        /** FloatMenu 部分 */
        FloatMenu = (FloatingActionsMenu) findViewById(R.id.FAB);
        FloatMenu.setVisibility(View.GONE);


        /** Toolbar 部分 */
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setSubtitle("Edit Your Todo");
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
        setTitle.setText(thisTodo.todoTitle);


        // 优先级
        setPriority = (RadioGroup) findViewById(R.id.addtodo_setpriority);
        if (thisTodo.todoPriority == ToDo.V_IMPORTANT){
            setPriority.check(R.id.todo_priority_vimportant);
            Priority = ToDo.V_IMPORTANT;
        } else if (thisTodo.todoPriority == ToDo.IMPORTANT) {
            setPriority.check(R.id.todo_priority_important);
            Priority = ToDo.IMPORTANT;
        } else if (thisTodo.todoPriority == ToDo.NORMAL) {
            setPriority.check(R.id.todo_priority_normal);
            Priority = ToDo.NORMAL;
        } else if (thisTodo.todoPriority == ToDo.CASUAL) {
            setPriority.check(R.id.todo_priority_casual);
            Priority = ToDo.CASUAL;
        }
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
        setItem1.setText(thisTodo.todoItem1);
        setItem2.setText(thisTodo.todoItem2);
        setItem3.setText(thisTodo.todoItem3);


        // 时间
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm ");
        TodoTime = thisTodo.todoTime;
        AlarmTime = thisTodo.toAlarmTime;
        setTodoTime = (Button) findViewById(R.id.addtodo_settodotime);
        setAlarmTime = (Button) findViewById(R.id.addtodo_setalarmtime);
        todoTime_show = (TextView) findViewById(R.id.addtodo_todotime_show);
        alarmTime_show = (TextView) findViewById(R.id.addtodo_toalarmtime_show);


        setTodoTime.setText("Set");
        setAlarmTime.setText("Set");
        todoTime_show.setText(dateFormat.format(thisTodo.todoTime));
        alarmTime_show.setText(dateFormat.format(thisTodo.toAlarmTime));


        // TODO: 2016/8/7 编辑时间必须设置后提交，不编辑提交会置空。目测与dateset有关，可能初始化的时候就会触发了dateset
        // 待办时间选择器
        setTodoTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 获取当前待办时间的待办时间
                final Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(thisTodo.todoTime);

                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);

                 DatePickerDialog pickdialog = new DatePickerDialog(EditTodoActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int _year, int _month, int _day) {

                        // 更新年月日
                        year = _year;
                        month = _month;
                        day = _day;


                        // 选择时间
                        new TimePickerDialog(EditTodoActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int _hourOfDay, int _minute) {

                                // 更新时分
                                hour = _hourOfDay;
                                minute = _minute;

                                // 更新 calendar 时间
                                calendar.clear();
                                calendar.set(year,month,day,hour,minute);

                                // 更新显示时间
                                todoTime_show.setText(dateFormat.format(calendar.getTimeInMillis()));

                                // 更新更改时间
                                TodoTime = calendar.getTimeInMillis();

                                Log.d(TAG, "onDateSet: "+todoTime_show.getText());

                            }
                        },hour,minute,true).show();
                    }
                }, year, month, day);

                pickdialog.show();


                // TODO: 2016/8/7 以下自定义控件无法再5.0设备上使用

//                // 获取 timepicker Dialog 的 view
//                View view = View.inflate(getApplicationContext(), R.layout.layout_timepicker, null);
//
//
//                // 获取 View 中的 DatePicker 和 TimePicker
//                final DatePicker datePicker = (DatePicker) view.findViewById(R.id.timepicker_date);
//                final TimePicker timePicker = (TimePicker) view.findViewById(R.id.timepicker_time);
//
//                // 获取当前时间
//                Calendar calendar = Calendar.getInstance();
//
//                year = calendar.get(Calendar.YEAR);
//                month = calendar.get(Calendar.MONTH);
//                day = calendar.get(Calendar.DAY_OF_MONTH);
//                hour = calendar.get(Calendar.HOUR_OF_DAY);
//                minute = calendar.get(Calendar.MINUTE);
//
//                // 初始化 DatePicker
//                datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
//                    @Override
//                    public void onDateChanged(DatePicker view,final int _year, int _monthOfYear, int _dayOfMonth) {
//                        // 更新年月日
//                        year = _year;
//                        month = _monthOfYear;
//                        day = _dayOfMonth;
//
//                        // 换位选择时间
//                        datePicker.setVisibility(View.GONE);
//                        timePicker.setVisibility(View.VISIBLE);
//                    }
//                });
//
//                // 初始化 TimePicker
//                timePicker.setIs24HourView(true);               // 设为24小时制
//                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
//                    @Override
//                    public void onTimeChanged(TimePicker view, int hourOfDay, int _minute) {
//                        // 更新时，分
//                        hour = hourOfDay;
//                        minute = _minute;
//                    }
//                });
//
//
//                // 设置 Dialog
//                final AlertDialog.Builder builder = new AlertDialog.Builder(EditTodoActivity.this);
//                builder.setView(view);
//                builder.setTitle("Set Date and Time");
//                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        // 时间显示格式
//                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm ");
//
//                        // 新建 Calendar 对象
//                        Calendar c = Calendar.getInstance();
//                        c.clear();
//                        c.set(year,month,day,hour,minute);
//
//                        // 显示结果并更新按钮
//                        todoTime_show.setText(dateFormat.format(c.getTimeInMillis()));
//
//                        // 获取设置好的TodoTime
//                        TodoTime = c.getTimeInMillis();
//
//                    }
//                });
//
//                builder.show();

            }
        });


        // 提醒时间选择器
        setAlarmTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 获取当前待办事项的提醒时间
                final Calendar calendar1 = Calendar.getInstance();
                calendar1.setTimeInMillis(thisTodo.toAlarmTime);

                year = calendar1.get(Calendar.YEAR);
                month = calendar1.get(Calendar.MONTH);
                day = calendar1.get(Calendar.DAY_OF_MONTH);
                hour = calendar1.get(Calendar.HOUR_OF_DAY);
                minute = calendar1.get(Calendar.MINUTE);

                final DatePickerDialog pickdialog = new DatePickerDialog(EditTodoActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int _year, int _month, int _day) {

                        // 更新年月日
                        year = _year;
                        month = _month;
                        day = _day;

                        new TimePickerDialog(EditTodoActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int _hourOfDay, int _minute) {

                                // 更新时分
                                hour = _hourOfDay;
                                minute = _minute;

                                // 更新 calendar1 时间
                                calendar1.clear();
                                calendar1.set(year,month,day,hour,minute);

                                // 更新显示时间
                                alarmTime_show.setText(dateFormat.format(calendar1.getTimeInMillis()));

                                // 获取设置好的 AlarmTime
                                AlarmTime = calendar1.getTimeInMillis();
                            }
                        },hour,minute,true).show();

                    }
                }, year, month, day );


                pickdialog.show();


                // TODO: 2016/8/7 以下自定义控件不支持5.0
                // 获取 timepicker Dialog 的view
//                View view = View.inflate(getApplicationContext(), R.layout.layout_timepicker, null);
//
//                // 获取 View 中的 DatePicker 和 TimePicker
//                final DatePicker datePicker = (DatePicker) view.findViewById(R.id.timepicker_date);
//                final TimePicker timePicker = (TimePicker) view.findViewById(R.id.timepicker_time);
//
//                // 获取当前时间
//                Calendar calendar = Calendar.getInstance();
//
//                year = calendar.get(Calendar.YEAR);
//                month = calendar.get(Calendar.MONTH);
//                day = calendar.get(Calendar.DAY_OF_MONTH);
//                hour = calendar.get(Calendar.HOUR_OF_DAY);
//                minute = calendar.get(Calendar.MINUTE);
//
//                // 初始化 DatePicker
//                datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
//                    @Override
//                    public void onDateChanged(DatePicker view,final int _year, int _monthOfYear, int _dayOfMonth) {
//                        // 更新年月日
//                        year = _year;
//                        month = _monthOfYear;
//                        day = _dayOfMonth;
//
//                        // 换位选择时间
//                        datePicker.setVisibility(View.GONE);
//                        timePicker.setVisibility(View.VISIBLE);
//                    }
//                });
//
//                // 初始化 TimePicker
//                timePicker.setIs24HourView(true);        // 设为24小时制
//                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
//                    @Override
//                    public void onTimeChanged(TimePicker view, int hourOfDay, int _minute) {
//                        // 更新时，分
//                        hour = hourOfDay;
//                        minute = _minute;
//                    }
//                });
//
//
//                // 设置 Dialog
//                final AlertDialog.Builder builder = new AlertDialog.Builder(EditTodoActivity.this);
//                builder.setView(view);
//                builder.setTitle("Set Date and Time");
//                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm ");
//                        Calendar c = Calendar.getInstance();
//                        c.clear();
//                        c.set(year,month,day,hour,minute);
//
//                        // 显示结果并更新按钮
//                        alarmTime_show.setText(dateFormat.format(c.getTimeInMillis()));
//
//                        // 获取设置好的 AlarmTime
//                        AlarmTime = c.getTimeInMillis();
//
//                    }
//                });
//
//                builder.show();

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
                // 更新待办事项 Title
                Title = setTitle.getText().toString();

                // 更新新待办事项 提醒
                Item1 = setItem1.getText().toString();
                Item2 = setItem2.getText().toString();
                Item3 = setItem3.getText().toString();

                // Title 和 Priority 不能为空
                if (Title.equals("") || Title == null){

                    Toast.makeText(EditTodoActivity.this, "You will need a title.", Toast.LENGTH_SHORT).show();

                } else if (Priority == -1){

                    Toast.makeText(EditTodoActivity.this, "You will need a priority.", Toast.LENGTH_SHORT).show();
                } else {

                    // 更新待办事项
                    thisTodo.todoTitle = Title;
                    thisTodo.todoPriority = Priority;
                    thisTodo.todoTime = TodoTime;
                    thisTodo.toAlarmTime = AlarmTime;
                    thisTodo.todoItem1 = Item1;
                    thisTodo.todoItem2 = Item2;
                    thisTodo.todoItem3 = Item3;
                    thisTodo.todoCreateTime = System.currentTimeMillis();


                    // 关闭当前页面
                    finish();
                }
            }
        });



    }


}
