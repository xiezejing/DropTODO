package com.benx.droptodo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.wilddog.client.Wilddog;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

//import cn.bmob.v3.Bmob;
//import cn.bmob.v3.exception.BmobException;
//import cn.bmob.v3.listener.SaveListener;

public class FeedbackActivity extends AppCompatActivity {
    /**
     * *********** 变量声明 ***********
     */

    /** 控件 */
    // Toolbar
    private Toolbar toolbar;
    private static int times = 0;

    // FloatingMenu
    private FloatingActionsMenu FloatMenu;

    // EditText
    private EditText email;
    private EditText content;

    // Button
    private Button cancel;
    private Button submit;

    private Wilddog mWilddogRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);


        /** Wilddog 初始化 */
        // Wilddog 部分
        mWilddogRef = new Wilddog(getResources().getString(R.string.wilddog_url));


        /** FloatMenu 部分 */
        FloatMenu = (FloatingActionsMenu) findViewById(R.id.FAB);
        FloatMenu.setVisibility(View.GONE);


        /** Toolbar 部分 */
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setSubtitle("Submit Feedback");
        toolbar.setNavigationIcon(R.drawable.ic_nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timer toExit;
                if (times == 0) {
                    times++;

                    toExit = new Timer();
                    toExit.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            times = 0;
                        }
                    },5000);

                } else {
                    times++;

                    if (times == 5) {
                        times = 0;

                        SnackbarHelper.LongSnackbar(findViewById(R.id.activity_feedback), "SuperUser Time.", SnackbarHelper.Confirm).show();
                        // TODO: 2016/8/6 跳转管理模式
                    }
                }
            }
        });


        /** EditText 部分 */
        email = (EditText) findViewById(R.id.feedback_email);
        content = (EditText) findViewById(R.id.feedback_content);


        /** Button 部分 */
        cancel = (Button) findViewById(R.id.feedback_cancel);
        submit = (Button) findViewById(R.id.feedback_submit);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 判断不能为空
                if (email.getText().toString().equals("") || content.getText().toString().equals("")) {
                    SnackbarHelper.LongSnackbar(findViewById(R.id.activity_feedback), "You have to finish all.", SnackbarHelper.Alert).show();

                } else {

                    // 提交表单
                    Feedback feedback = new Feedback(email.getText().toString(),content.getText().toString());

                    Wilddog feed = mWilddogRef.child("feedbacks").push();
                    Map<String,String> feeddata = new HashMap<String, String>();
                    feeddata.put("email",feedback.getEmail());
                    feeddata.put("content",feedback.getContent());
                    feeddata.put("reply",feedback.getReply());

                    feed.setValue(feeddata);

                    SnackbarHelper.LongSnackbar(findViewById(R.id.activity_feedback), "We will reply soon.", SnackbarHelper.Confirm).show();

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
