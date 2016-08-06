package com.benx.droptodo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        /** Bmob 初始化 */
        Bmob.initialize(FeedbackActivity.this, "d5bf221e30a248f584f3e58da67dc9e7");


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
                    final Feedback feedback = new Feedback(email.getText().toString(),content.getText().toString());
                    feedback.save(new SaveListener<String>(){
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
//                                SnackbarHelper.ShortSnackbar(findViewById(R.id.activity_feedback), "We've got your feedback.", SnackbarHelper.Confirm).show();
                                Toast.makeText(FeedbackActivity.this, "We've got your feedback.", Toast.LENGTH_SHORT).show();
                                MainActivity.FeedbackList.add(feedback.getObjectId());
                                Log.d("getin", "done: add a new feedback'id :" + feedback.getObjectId());

                                finish();
                            } else{
                                SnackbarHelper.ShortSnackbar(findViewById(R.id.activity_feedback), "Oops! Submit later.", SnackbarHelper.Warning).show();
                                Log.d("getin", "done: "+e.getMessage()+","+e.getErrorCode());
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("getin", "onBackPressed: finish");
        finish();
    }
}
