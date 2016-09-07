package com.benx.droptodo;

/**
 * 主界面 MainActivity
 */

import android.content.Context;
import android.content.DialogInterface;
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
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.wilddog.client.AuthData;
import com.wilddog.client.DataSnapshot;
import com.wilddog.client.Query;
import com.wilddog.client.ValueEventListener;
import com.wilddog.client.Wilddog;
import com.wilddog.client.WilddogError;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    // TODO: 2016/8/27 测试用  注册和登录的控件
    private ImageView head_logo;
    private TextView head_mail;

    // TodoListFragment
    private TodoListFragment fragment;
    // RecycleBinFragment
    private RecycleBinFragment recyclefragment;


    private Wilddog mWilddogRef;

    /**
     * 重要参数
     */

    // 一些重要参数
    public static int CURRENT_THEME = 0;    // 当前的主题
    public static int ScreenWidth;          // 当前屏幕的宽度
    private static boolean isExit = false;  // 是否退出程序（计时器标志）
    public static boolean draggable;
    public static boolean swappable;
    public static boolean clickable;
    public static boolean isMarquee = true;     // 是否开启跑马灯模式
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";

    /**
     * 登录信息
     */
    public static boolean hasLogin;     // 标记是否登录
    public static String userEmail;     // 标记用户名
    public static String UID;           // wilddog账户uid

    public static String cache_email;    // 缓存的邮箱
    public static String cache_password; // 缓存的密码
    public static boolean cache_remember;


    /**
     * 数据集 和 数据库
     */
    // 存储信息
    private static SharedPreferences getPrefs;
    // 数据集
    public static int AlarmCount = 0;
    public static List<ToDo> ToDoList = new ArrayList<>();            // 待办事项
    public static final List<ToDo> DeleteToDoList = new ArrayList<>();      // 删除的待办事项
    public static final List<String> FeedbackList = new ArrayList<>();      // 仅保留Feedback的id
    public static DBHelper dbHelper;
    public static SQLiteDatabase database;


    // TODO: 2016/8/5  测试变量
    public static final String TAG = "getin";


    /**
     * 活动被创建时
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: 2016/8/5  
        Log.d(TAG, "onCreate: ");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Wilddog.setAndroidContext(this);


        // Wilddog 部分
        mWilddogRef = new Wilddog(getResources().getString(R.string.wilddog_url));


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
        // TODO: 2016/8/27  测试登录
        // TODO: 2016/8/27 测试用 注册和登录
        View headerView = navigationView.getHeaderView(0);

        head_logo = (ImageView) headerView.findViewById(R.id.user_profilePic);
        head_mail = (TextView) headerView.findViewById(R.id.user_email);

        head_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SnackbarHelper.LongSnackbar((DrawerLayout)findViewById(R.id.drawer_layout),"in", SnackbarHelper.Confirm).show();
                if (hasLogin && !TextUtils.isEmpty(userEmail)) {
                    SnackbarHelper.LongSnackbar(findViewById(R.id.drawer_layout), "You have logged in " + userEmail, SnackbarHelper.Alert).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

            }
        });


        // FloatMenu 部分
        FloatMenu = (FloatingActionsMenu) findViewById(R.id.FAB);
        ReorderFloat = (FloatingActionButton) findViewById(R.id.fab_reoder);
        NewFloat = (FloatingActionButton) findViewById(R.id.fab_add);

        ReorderFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<ToDo> donelist = new ArrayList<ToDo>();

                for (int i = 0; i < ToDoList.size(); i++) {

                    ToDo newtodo = ToDoList.get(i);

                    if (newtodo.todoDone) {
                        donelist.add(newtodo);
                        ToDoList.remove(i);
                    }

                }

                for (int i = 0; i < donelist.size(); i++) {
                    ToDoList.add(donelist.get(i));
                }

                Snackbar.make(v, "Reorder done!", Snackbar.LENGTH_SHORT).setAction
                        ("Action", null).show();


                fragment.toDoAdapter.notifyDataSetChanged();


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


        // 初始化加载 TodoListFragment
        fragment = new TodoListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main_framelayout1, fragment, "content")
                .commit();

        // 初始化加载 RecycleBinFragment
        recyclefragment = new RecycleBinFragment();


        // 提醒没有待办事项
        if (ToDoList.isEmpty()) {
            //Toast.makeText(MainActivity.this, "You have no todo yet", Toast.LENGTH_SHORT).show();
            SnackbarHelper.LongSnackbar((DrawerLayout) findViewById(R.id.drawer_layout), "You have no todo yet.", SnackbarHelper.Confirm).show();
        }

    }

    @Override
    protected void onStart() {
        // TODO: 2016/8/5  
        Log.d(TAG, "onStart: ");

        super.onStart();
        fragment.toDoAdapter.notifyDataSetChanged();
        recyclefragment.toDoAdapter.notifyDataSetChanged();
        // TODO: 2016/8/6 此时刷新界面为佳

        MenuItem mi = navigationView.getMenu().findItem(R.id.nav_login);
        //MenuItem mi = (MenuItem) findViewById(R.id.nav_login);

        if (hasLogin) {
            mi.setTitle("Logout");
            head_mail.setText(userEmail);
        } else {
            mi.setTitle("Login");
            head_mail.setText(R.string.app_name);
        }

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
     * 返回键 (BACK) 被按下时，1.收回侧栏；2.双击退出程序
     */
    @Override
    public void onBackPressed() {

        // 处理侧栏
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {

            // 侧栏显示则收回
            drawer.closeDrawer(GravityCompat.START);

        } else {

            // 保存结果
            saveData();


            // 计时器
            Timer toExit;
            if (!isExit) {

                // 双击退出程序
                isExit = true;
                //Toast.makeText(this, "Double click BACK to quit.", Toast.LENGTH_SHORT).show();
                SnackbarHelper.LongSnackbar(findViewById(R.id.drawer_layout), "Double click BACK to quit.", SnackbarHelper.Info).show();

                toExit = new Timer();
                toExit.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);

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
     * 选项菜单 (OptionMenu) 被创建时
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
     * 选项菜单 (OptionMenu) 选项被选中时
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // 获取选项id
        int id = item.getItemId();

        // 如果选中了
        if (id == R.id.action_ocdmode) {
            if (!isMarquee) {
                isMarquee = true;

                item.setTitle(R.string.action_ocdmode);
            } else {
                isMarquee = false;
                item.setTitle(R.string.action_marquee);
            }
            fragment.toDoAdapter.notifyDataSetChanged();
            recyclefragment.toDoAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 侧栏选项 (NabigationItem) 被选中时
     *
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        // 获取被点击的 item ID
        int id = item.getItemId();

        // 对不同 item 进行不同的操作
        if (id == R.id.nav_login) {
            Log.d(TAG, "onNavigationItemSelected: id ");
            if (hasLogin && !TextUtils.isEmpty(userEmail)) {
                SnackbarHelper.LongSnackbar(findViewById(R.id.drawer_layout), "Want to logout? " + userEmail, SnackbarHelper.Warning).setAction("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hasLogin = false;
                        userEmail = null;

                        item.setTitle("Login");
                        onStart();
                    }
                }).setActionTextColor(Color.WHITE).show();
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }

        } else if (id == R.id.nav_upload) {
            if (hasLogin) {
                doUpload();
            } else {
                SnackbarHelper.LongSnackbar(findViewById(R.id.drawer_layout), "You have to login first? ", SnackbarHelper.Alert).show();
            }

        } else if (id == R.id.nav_download) {
            if (hasLogin) {
                doDownload();
            } else {
                SnackbarHelper.LongSnackbar(findViewById(R.id.drawer_layout), "You have to login first? ", SnackbarHelper.Alert).show();
            }

        } else if (id == R.id.nav_todolist) {
            FloatMenu.setVisibility(View.VISIBLE);
            toolbar.setSubtitle("My Todo List");

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_main_framelayout1, fragment)
// Todo 考虑不用压栈效果               .addToBackStack(null)
                    .commit();
            fragment.toDoAdapter.notifyDataSetChanged();

            // item.setChecked(false);

        } else if (id == R.id.nav_deletebox) {
            toolbar.setSubtitle("Recycle Bin");
            FloatMenu.setVisibility(View.GONE);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_main_framelayout1, recyclefragment)
                    .commit();
            recyclefragment.toDoAdapter.notifyDataSetChanged();

            //item.setChecked(false);

        } else if (id == R.id.nav_colors) {
            // 改变背景颜色
            int color_dark = Color.argb(255, 66, 66, 66);
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
            clickable = !clickable;

            if (clickable) {
                item.setIcon(R.drawable.ic_float_check);
                item.setTitle("Check Mode");
            } else {
                item.setIcon(R.drawable.ic_menu_editmode);
                item.setTitle("Edit Mode");
            }

        } else if (id == R.id.nav_help) {
            // TODO: 2016/8/5
            // 加载 SplashIntro 引导页
            Intent i = new Intent(this, SplashIntro.class);
            startActivity(i);

            //item.setChecked(false);
        } else if (id == R.id.nav_feedback) {
            Intent i = new Intent(this, FeedbackActivity.class);
            startActivity(i);

            //item.setChecked(false);

        } else if (id == R.id.nav_copyright) {

            SnackbarHelper.LongSnackbar(findViewById(R.id.drawer_layout), "©2016-2017 Made By Ben.X ", SnackbarHelper.Confirm).show();

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
        getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // 第一次启动时设置标记
        boolean nodata = getPrefs.getBoolean("nodata", true);

        // 获取闹铃数
        AlarmCount = getPrefs.getInt("AlarmCount", 0);
        Log.d(TAG, "initData: get alarmcount:"+AlarmCount);

        // 如果是第一次启动
        if (nodata) {
            Calendar calendar = Calendar.getInstance();
            long time = calendar.getTimeInMillis();
            ToDoList.add(new ToDo("This's Title", time, time, ToDo.V_IMPORTANT, "The Red bar means Priority.", "You have 4 priorities to choose.", "Red means very important"));
            ToDoList.add(new ToDo("这是标题", time, time, ToDo.IMPORTANT, "Title & Priority are essential.", "Item is up to you.", "Items remind you points of issues."));
            ToDoList.add(new ToDo("Using Tips", time, time, ToDo.NORMAL, "Swap to remove todo.", "Press and drag to change orders.", "You will create more tips ."));
            ToDoList.add(new ToDo("Thanks for using our APP", time, time, ToDo.CASUAL, "This APP is not perfect.", "It's made by a rookie.", "I'll  appreciate your comments."));
            ToDo t = new ToDo("This a deleted todo", time, time, ToDo.CASUAL, "This APP is not perfect.", "It's made by a rookie.", "I'll  appreciate your comments.");
            t.todoDone = true;
            DeleteToDoList.add(t);

            // 改写标记
            SharedPreferences.Editor e = getPrefs.edit();
            e.putBoolean("nodata", false);
            e.apply();

            return;
        }


        // 读取登录缓存信息
        cache_email = getPrefs.getString("cache_email", null);
        cache_password = getPrefs.getString("cache_password", null);
        cache_remember = getPrefs.getBoolean("cache_remember", false);
        userEmail = getPrefs.getString("userEmail", null);
        hasLogin = getPrefs.getBoolean("hasLogin", false);
        UID = getPrefs.getString("UID", null);


        // 读取 待办事项
        Cursor cursor = database.query(DBHelper.Todo_Table, null, null, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Log.d("getin", "read a cursor:" + cursor.getString(0));
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
                } while (cursor.moveToNext());
            }
        } else {
            Log.d("getin", "cursor null");
        }
        cursor.close();


        // 读取 删除的待办事项
        Cursor cursor1 = database.query(DBHelper.DeletedTodo_Table, null, null, null, null, null, null);
        if (cursor1 != null) {
            if (cursor1.moveToFirst()) {
                Log.d("getin", "get in cursor1");
                do {
                    Log.d("getin", "read a cursor1:" + cursor1.getString(0));
                    byte data[] = cursor1.getBlob(cursor1.getColumnIndex("Deleted"));
                    ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);

                    try {
                        ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
                        ToDo getTodo = (ToDo) inputStream.readObject();
                        DeleteToDoList.add(getTodo);
                        // TODO: 2016/8/6 监测是否读出
                        Log.d("getin", "read a deleted todo:" + getTodo.todoTitle);
                        arrayInputStream.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (cursor1.moveToNext());
            }
            Log.d("getin", "cursor1 count " + cursor1.getCount());
        } else {
            Log.d("getin", "cursor1 null");
        }
        cursor1.close();


        // 读取 反馈的Id
        Cursor cursor2 = database.query(DBHelper.Feedback_Table, null, null, null, null, null, null);
        if (cursor2 != null) {
            if (cursor2.moveToFirst()) {
                do {
                    Log.d("getin", "read a cursor2:" + cursor2.getString(0));
                    String getId = cursor2.getString(cursor2.getColumnIndex("FeedbackId"));
                    Log.d("getin", "read a feedback id :" + getId);
                    FeedbackList.add(getId);
                } while (cursor2.moveToNext());
            }
        } else {
            Log.d("getin", "cursor2 null");
        }
        cursor2.close();

        database.close();

        // TODO: 2016/8/5 还有删除的待办事项, 没有读取


        Log.d(TAG, "initData: todolist size" + ToDoList.size());
        Log.d(TAG, "initData: deletedlist size" + DeleteToDoList.size());
        Log.d(TAG, "initData: feedback size" + FeedbackList.size());
    }


    private static void saveTodo(ToDo todo, String table, String column) {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            Log.d("getin", "saveTodo: " + todo.todoTitle);

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(todo);
            objectOutputStream.flush();

            byte data[] = arrayOutputStream.toByteArray();
            objectOutputStream.close();

            database = dbHelper.getWritableDatabase();
            database.execSQL("insert into " + table + " (" + column + ") values(?)", new Object[]{data});
            database.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveTodo(String string, String table) {

        Log.d("getin", "saveFeedbacks id: " + string);

        database = dbHelper.getWritableDatabase();
        database.execSQL("insert into " + table + " (FeedbackId) values(?)", new String[]{string});
        database.close();

    }

    public static void saveData() {
        database = dbHelper.getWritableDatabase();
        database.delete(DBHelper.Todo_Table, null, null);
        database.delete(DBHelper.DeletedTodo_Table, null, null);
        database.delete(DBHelper.Feedback_Table, null, null);
        database.close();

        // 保存待办事项
        for (ToDo toDo : ToDoList) {
            saveTodo(toDo, DBHelper.Todo_Table, "Todo");
        }

        // 保存删除的待办事项
        for (ToDo toDo : DeleteToDoList) {
            Log.d("getin", "saved deleted todo id: " + toDo.todoTitle);
            saveTodo(toDo, DBHelper.DeletedTodo_Table, "Deleted");
        }

        // 保存Feedback的id
        for (String s : FeedbackList) {
            saveTodo(s, DBHelper.Feedback_Table);
        }


        // 保存登录的缓存信息
        SharedPreferences.Editor e = getPrefs.edit();
        e.putString("cache_email", cache_email);
        e.putString("cache_password", cache_password);
        e.putBoolean("cache_remember", cache_remember);
        e.putString("userEmail", userEmail);
        e.putString("UID", UID);
        e.putBoolean("hasLogin", hasLogin);
        e.apply();

    }

    private void doUpload() {
        //mWilddogRef.setValue("hehhlo");
        if (TextUtils.isEmpty(UID)) {
            SnackbarHelper.ShortSnackbar(findViewById(R.id.drawer_layout), "Login again please.", SnackbarHelper.Alert).show();
            return;
        }

        Wilddog first = mWilddogRef.child("todos").child(UID);
        first.setValue(null);


        for (int i = 0; i < ToDoList.size(); i++) {
            ToDo todo = ToDoList.get(i);

            Wilddog upData = first.push();

            upData.child("email").setValue(userEmail);

            upData.child("id").setValue(i);
            upData.child("todoTitle").setValue(todo.todoTitle);
            upData.child("todoTime").setValue(todo.todoTime);
            upData.child("toAlarmTime").setValue(todo.toAlarmTime);
            upData.child("todoPriority").setValue(todo.todoPriority);
            upData.child("todoItem1").setValue(todo.todoItem1);
            upData.child("todoItem2").setValue(todo.todoItem2);
            upData.child("todoItem3").setValue(todo.todoItem3);
            upData.child("todoDone").setValue(todo.todoDone);
            upData.child("todoReorderMode").setValue(todo.todoReorderMode);
            upData.child("todoCreateTime").setValue(todo.todoCreateTime);
        }

        SnackbarHelper.LongSnackbar(findViewById(R.id.drawer_layout), "Upload finished.", SnackbarHelper.Confirm).show();
    }

    private void doDownload() {
        if (TextUtils.isEmpty(UID)) {
            SnackbarHelper.ShortSnackbar(findViewById(R.id.drawer_layout), "Login again please.", SnackbarHelper.Alert).show();
            return;
        }

        Wilddog ref = new Wilddog("https://droptodo.wilddogio.com/todos/" + UID);
        Query queryref = ref.orderByKey();
        queryref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "query onDataChange: childrencount " + dataSnapshot.getChildrenCount());
                long count = dataSnapshot.getChildrenCount();
                Iterator children = dataSnapshot.getChildren().iterator();


                final List<ToDo> DownloadList = new ArrayList<ToDo>();

                while (children.hasNext()) {
                    DataSnapshot child = (DataSnapshot) children.next();
                    Log.d(TAG, "onDataChange: child is:" + child);
                    Map keymap = (Map) child.getValue();

                    String todoTitle = (String) keymap.get("todoTitle");
                    long todoTime = (long) keymap.get("todoTime");
                    long toAlarmTime = (long) keymap.get("toAlarmTime");
                    int todoPriority = Integer.parseInt(keymap.get("todoPriority").toString());
                    String todoItem1 = (String) keymap.get("todoItem1");
                    String todoItem2 = (String) keymap.get("todoItem2");
                    String todoItem3 = (String) keymap.get("todoItem3");
                    boolean todoDone = (boolean) keymap.get("todoDone");
                    int todoReorderMode = Integer.parseInt(keymap.get("todoReorderMode").toString());
                    long createtime = (long) keymap.get("todoCreateTime");

                    ToDo newtodo = new ToDo(todoTitle, todoTime, toAlarmTime, todoPriority, todoItem1, todoItem2, todoItem3);
                    newtodo.todoDone = todoDone;
                    newtodo.todoReorderMode = todoReorderMode;
                    newtodo.todoCreateTime = createtime;

                    DownloadList.add(newtodo);
                    Log.d(TAG, "onDataChange: add todo:" + newtodo.todoTitle);
                }

                SnackbarHelper.LongSnackbar(findViewById(R.id.drawer_layout), "Download finished. Update now? It will delete the local data!! " + userEmail, SnackbarHelper.Warning).setAction("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToDoList.clear();
                        for (int i = 0; i < DownloadList.size(); i++) {
                            ToDoList.add(DownloadList.get(i));
                        }

                        SnackbarHelper.LongSnackbar(findViewById(R.id.drawer_layout), "Data Refreshed! " + userEmail, SnackbarHelper.Confirm).show();
                        fragment.toDoAdapter.notifyDataSetChanged();
                    }
                }).setActionTextColor(Color.WHITE).show();

            }

            @Override
            public void onCancelled(WilddogError wilddogError) {
                if (wilddogError != null) {
                    SnackbarHelper.ShortSnackbar(findViewById(R.id.drawer_layout), "Error: " + wilddogError.toString(), SnackbarHelper.Warning).show();
                }
            }
        });
    }
}
