package com.benx.droptodo;

/**
 *
 *  主界面 MainActivity
 *
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     *
     *  变量声明
     *  FloatButton:
     *      1.  FloatMenu   菜单FloatingActionsMenu 可以addButton
     *      2.  CreateFloat 新建事务 Fab 钮
     *
     *  Toolbar:
     *      1.  toolbar     页面的 Toolbar
     *
     *  DrawerLayout:
     *      1.  drawer      页面上的 DrawerLayout
     *
     *  NavigationView:
     *      1.  navigationView 页面上的 NavigationView
     *
     */
    // FloatButton
    private FloatingActionsMenu FloatMenu;
    private FloatingActionButton CreateFloat;

    // Toolbar
    private Toolbar toolbar;

    // Drawer
    private DrawerLayout drawer;

    // Navigation
    private NavigationView navigationView;

    // 一些重要参数
    public static int CURRENT_THEME = 0;    // 当前的主题
    public static int ScreenWidth;    // 当前屏幕的宽度

    // 一些重要常量
    public static final List<ToDo> ToDoList = new ArrayList<>();          // 待办事项
    public static final List<ToDo> DeleteToDoList = new ArrayList<>();    // 删除的待办事项


    /**
     *
     *  活动被创建时
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar 部分
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        CreateFloat = (FloatingActionButton) findViewById(R.id.FAB_create);

        CreateFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "create",Snackbar.LENGTH_SHORT).setAction
                        ("Action",null).show();
            }
        });


        // 初始化数据
        initData();


        // 获取屏幕宽度
        WindowManager wm = (WindowManager) MainActivity.this
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();

        wm.getDefaultDisplay().getMetrics(outMetrics);
        ScreenWidth = outMetrics.widthPixels;


        // 初始化加载TodoListFragment
        TodoListFragment fragment = new TodoListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_main_framelayout1, fragment,"content")
                .addToBackStack(null)
                .commit();

    }

    /**
     *  返回键 (BACK) 被按下时
     */
    @Override
    public void onBackPressed() {

        // 处理侧栏
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            // 侧栏显示则收回
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // 侧栏隐藏则触发Back
            super.onBackPressed();
        }
    }


    /**
     *
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
     *
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
     *
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
        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_colors) {

//            if (getTheme() == getResources(R.style.AppTheme)) {
//
//            }


            // TODO 改变背景颜色

            int color_dark = Color.argb(255,66,66,66);
            int color_light = 0;

//            RelativeLayout layout =(RelativeLayout)findViewById(R.id
//                    .content_main);
//            layout.setBackgroundColor(color_light);

            if (CURRENT_THEME == color_dark) {
                findViewById(R.id.content_main).setBackgroundColor(color_light);
                CURRENT_THEME = color_light;
                Log.d("getin","to white");
            } else {

                Log.d("getin","to dark");

                findViewById(R.id.content_main).setBackgroundColor(color_dark);
                CURRENT_THEME = color_dark;
            }

        } else if (id == R.id.nav_help) {

            Toast.makeText(MainActivity.this, "help", Toast.LENGTH_SHORT).show();
            toolbar.setSubtitle("Help");
        }

        // 关闭当前 DrawerLayout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // TODO 初始化。目前以初始化实例为测试，最后将封装为数据库读取
    private void initData() {
        for (int i = 0; i < 10; i++) {
            ToDoList.add(new ToDo());
        }
    }
}
