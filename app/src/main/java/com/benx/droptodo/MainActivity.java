package com.benx.droptodo;

/**
 *
 *  主界面 MainActivity
 *
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * 重要控件
     */
    // FloatButton
    private FloatingActionsMenu FloatMenu;
    private FloatingActionButton NewFloat;
    private FloatingActionButton ReorderFloat;

    // Toolbar
    private Toolbar toolbar;

    // Drawer
    private DrawerLayout drawer;

    // Navigation
    private NavigationView navigationView;

    // TodoListFragment
    private TodoListFragment fragment;


    /**
     *
     * 重要参数
     *
     */

    // 一些重要参数
    public static int CURRENT_THEME = 0;    // 当前的主题
    public static int ScreenWidth;          // 当前屏幕的宽度
    private static boolean isExit = false;  // 是否退出程序（计时器标志）
    public static boolean draggable;
    public static boolean swappable;
    public static boolean clickable;


    /**
     *
     * 数据集 和 数据库
     *
     */
    // 数据集
    public static final List<ToDo> ToDoList = new ArrayList<>();          // 待办事项
    public static final List<ToDo> DeleteToDoList = new ArrayList<>();    // 删除的待办事项
    public static DBHelper dbHelper;
    public static SQLiteDatabase database;


    // TODO: 2016/8/5  测试变量
    public static final String TAG = "getin";

    /**
     *
     *  活动被创建时
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: 2016/8/5  
        Log.d(TAG, "onCreate: ");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化变量
        draggable = true;
        swappable = false;
        clickable = false;

        // Toolbar 部分
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle("My Todo List");
        setSupportActionBar(toolbar);


        // Drawer 部分
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        // Navigation 部分
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // FloatMenu 部分
        FloatMenu = (FloatingActionsMenu) findViewById(R.id.FAB);
        ReorderFloat = (FloatingActionButton) findViewById(R.id.fab_reoder);
        NewFloat = (FloatingActionButton) findViewById(R.id.fab_add);

        ReorderFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "create",Snackbar.LENGTH_SHORT).setAction
                        ("Action",null).show();

                // 收回菜单
                FloatMenu.collapse();
            }
        });

        NewFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddTodoActivity.class);

                // 收回菜单
                FloatMenu.collapse();

                startActivity(intent);
            }
        });


        // 数据库初始化
        initDatabase();


        // 初始化数据
        initData();


        // 获取屏幕宽度
        WindowManager wm = (WindowManager) MainActivity.this
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();

        wm.getDefaultDisplay().getMetrics(outMetrics);
        ScreenWidth = outMetrics.widthPixels;


        // 初始化加载TodoListFragment
        fragment = new TodoListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main_framelayout1, fragment,"content")
// Todo 考虑不用压栈效果               .addToBackStack(null)
                .commit();

    }

    @Override
    protected void onStart() {
        // TODO: 2016/8/5  
        Log.d(TAG, "onStart: ");
        
        super.onStart();
        fragment.toDoAdapter.notifyDataSetChanged();
        // TODO: 2016/8/5 测试 invalidate（）
        //fragment.recyclerView.invalidateItemDecorations(););
    }

    @Override
    protected void onResume() {
        // TODO: 2016/8/5  
        Log.d(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO: 2016/8/5  
        Log.d(TAG, "onPause: ");
        
        super.onPause();
    }

    @Override
    protected void onStop() {
        // TODO: 2016/8/5  
        Log.d(TAG, "onStop: ");

        saveData();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        // TODO: 2016/8/5  
        Log.d(TAG, "onRestart: ");
        
        super.onRestart();
        
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("getin", "onDestroy: ");

    }

    /**
     *  返回键 (BACK) 被按下时，1.收回侧栏；2.双击退出程序
     */
    @Override
    public void onBackPressed() {

        // 处理侧栏
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {

            // 侧栏显示则收回
            drawer.closeDrawer(GravityCompat.START);

        } else {
            // 计时器
            Timer toExit;
            if (!isExit) {

                // 双击退出程序
                isExit = true;
                Toast.makeText(this, "Double click BACK to quit.", Toast.LENGTH_SHORT).show();
                toExit = new Timer();
                toExit.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                },2000);

            } else {

                // 关闭当前 Activity 并退出程序
                saveData();
                finish();
                System.exit(0);

                super.onBackPressed();
            }
        }
    }


    /**
     *  选项菜单 (OptionMenu) 被创建时
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 加载
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    /**
     *  选项菜单 (OptionMenu) 选项被选中时
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     *  侧栏选项 (NabigationItem) 被选中时
     *
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // 获取被点击的 item ID
        int id = item.getItemId();

        // 对不同 item 进行不同的操作
        if (id == R.id.nav_todolist) {

           // item.setChecked(false);

        } else if (id == R.id.nav_deletebox) {

            //item.setChecked(false);

        } else if (id == R.id.nav_colors) {
            // 改变背景颜色
            int color_dark = Color.argb(255,66,66,66);
            int color_light = 0;

            if (CURRENT_THEME == color_dark) {

                findViewById(R.id.content_main).setBackgroundColor(color_light);
                CURRENT_THEME = color_light;

            } else {

                findViewById(R.id.content_main).setBackgroundColor(color_dark);
                CURRENT_THEME = color_dark;
            }

            //item.setChecked(false);

        } else if (id == R.id.nav_delete) {

            swappable = !swappable;

            if (swappable) {
                item.setTitle("Stop delete");
            } else {
                item.setTitle("Delete");
            }

            //item.setChecked(false);

        } else if (id == R.id.nav_quickreorder) {
            // 更改 item 标题
            if (!ToDoList.isEmpty()) {
                if (ToDoList.get(0).todoReorderMode == ToDo.REORDER_NORMALMODE) {
                    item.setTitle("Normal Drag");
                } else {
                    item.setTitle("Quick Drag");
                }
            } else {
                Toast.makeText(MainActivity.this, "You should have a todo first.", Toast.LENGTH_SHORT).show();
            }

            // 更新数据集
            for (ToDo toDo : ToDoList) {
                toDo.todoReorderMode = toDo.todoReorderMode == ToDo.REORDER_NORMALMODE?ToDo.REORDER_QUICKMODE:ToDo.REORDER_NORMALMODE;
            }

            // 刷新界面
            fragment.toDoAdapter.notifyDataSetChanged();

           // item.setChecked(false);

        } else if (id == R.id.nav_drag) {

            // 设置可否拖拽
            draggable = !draggable;

            if (draggable) {
                item.setTitle("Undraggable");
                item.setIcon(R.drawable.ic_menu_undraggable);
            } else {
                item.setTitle("Draggable");
                item.setIcon(R.drawable.ic_menu_draggable);
            }

           // item.setChecked(false);

        } else if (id == R.id.nav_checkoredit) {
            clickable =!clickable;

            if (clickable) {
                item.setIcon(R.drawable.ic_float_check);
                item.setTitle("Check Mode");
            } else {
                item.setIcon(R.drawable.ic_menu_editmode);
                item.setTitle("Edit Mode");
            }

        } else if (id == R.id.nav_help) {

//            Toast.makeText(MainActivity.this, "help", Toast.LENGTH_SHORT).show();
//            toolbar.setSubtitle("Help");
            // TODO: 2016/8/5
            // 加载 SplashIntro 引导页
            Intent i = new Intent(this, SplashIntro.class);
            startActivity(i);

            //item.setChecked(false);
        } else if (id == R.id.nav_feedback) {

            //item.setChecked(false);

        } else if (id == R.id.nav_copyright) {

            //item.setChecked(false);

        }

        // 关闭当前 DrawerLayout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * 初始化数据库
     */
    private void initDatabase() {
        dbHelper = new DBHelper(this);
    }


    /**
     * 初始化数据
     */
    private void initData() {

        // 获得数据库
        database = dbHelper.getWritableDatabase();

        // 获取 SharedPreference 记录
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // 第一次启动时设置标记
        boolean nodata = getPrefs.getBoolean("nodata", true);

        // 如果是第一次启动
        if (nodata) {
            Calendar calendar = Calendar.getInstance();
            long time = calendar.getTimeInMillis();
            ToDoList.add(new ToDo("This's Title",time,time, ToDo.V_IMPORTANT,"The Red bar means Priority.","You have 4 priorities to choose.","Red means very important"));
            ToDoList.add(new ToDo("这是标题",time,time, ToDo.IMPORTANT,"Title & Priority are essential.","Item is up to you.","Items remind you points of issues."));
            ToDoList.add(new ToDo("Using Tips",time,time, ToDo.NORMAL,"Swap to remove todo.","Press and drag to change orders.","You will create more tips ."));
            ToDoList.add(new ToDo("Thanks",time,time, ToDo.CASUAL,"This APP is not perfect.","It's made by a rookie.","I'll  appreciate your comments."));

            // 改写标记
            SharedPreferences.Editor e = getPrefs.edit();
            e.putBoolean("nodata", false);
            e.apply();

            return;
        }

        
        // 读取 待办事项
        Cursor cursor = database.query("Todos",null,null,null,null,null,null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Log.d("getin", "read a cursor"+cursor.getString(0));
                    byte data[] = cursor.getBlob(cursor.getColumnIndex("Todo"));
                    ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);

                    try {
                        ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
                        ToDo getTodo = (ToDo) inputStream.readObject();
                        ToDoList.add(getTodo);
                        arrayInputStream.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }while (cursor.moveToNext());
            }
        } else {
            Log.d("getin", "cursor null");
        }
        cursor.close();
        database.close();

        // TODO: 2016/8/5 还有删除的待办事项没有读取 


        Log.d(TAG, "initData: todolist size" + ToDoList.size());
    }

//    public ArrayList<ToDo> get

    private void saveTodo(ToDo todo, String table) {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            Log.d("getin", "saveTodo: " + todo.todoTitle);

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(todo);
            objectOutputStream.flush();

            byte data[] = arrayOutputStream.toByteArray();
            objectOutputStream.close();

            database = dbHelper.getWritableDatabase();
            database.execSQL("insert into "+ table + " (Todo) values(?)", new Object[]{data});
            database.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveData() {
        database = dbHelper.getWritableDatabase();
        database.delete(DBHelper.Todo_Table, null,null);
        database.close();

        for (ToDo toDo : ToDoList) {
            saveTodo(toDo, DBHelper.Todo_Table);
        }

        // TODO: 2016/8/5 还有删除的没有保存
    }


}
