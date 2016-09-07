package com.benx.droptodo;

import android.graphics.Color;
import android.renderscript.Script;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.wilddog.client.AuthData;
import com.wilddog.client.Wilddog;
import com.wilddog.client.WilddogError;

import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private static final String[] emailFix = {"@qq.com", "@163.com", "@126.com", "@gmail.com", "@sina.com", "@hotmail.com",
            "@yahoo.cn", "@sohu.com", "@foxmail.com", "@139.com"};

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    //private static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;

    /**
     * *********** 变量声明 ***********
     */

    /** 控件 */
    // Form
    private AutoCompleteEmailText mEmail;
    private EditText mPassword;
    private EditText mPasswordCheck;
    private CheckBox mRemember;
    private Button mSignup;
    private Button mLogin;

    // Toolbar
    private Toolbar toolbar;
    private static int times = 0;

    // FloatingMenu
    private FloatingActionsMenu FloatMenu;

    // Wilddog
    private Wilddog mWilddogRef;

    /** 重要参数 */
    private String TAG = "getin";
    private static final int LOGIN = 0;
    private static final int SIGNUP = 1;
    private String email;
    private String password;
    private String password_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /** Wilddog 部分 */
        mWilddogRef = new Wilddog(getResources().getString(R.string.wilddog_url));

        /** Toolbar 部分 */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle("Login");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /** FloatMenu 部分 */
        FloatMenu = (FloatingActionsMenu) findViewById(R.id.FAB);
        FloatMenu.setVisibility(View.GONE);

        /** 主要控件 部分 */
        mEmail = (AutoCompleteEmailText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mPasswordCheck = (EditText) findViewById(R.id.password_check);
        mRemember = (CheckBox) findViewById(R.id.remember);
        mSignup = (Button) findViewById(R.id.login_signup_btn);
        mLogin = (Button) findViewById(R.id.login_login_btn);

        if (MainActivity.cache_remember) {
            mEmail.setText(MainActivity.cache_email);
            mPassword.setText(MainActivity.cache_password);
            mRemember.setChecked(MainActivity.cache_remember);
        }

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidInput()) {
                    if (mRemember.isChecked()) {
                        MainActivity.cache_email = email;
                        MainActivity.cache_password = password;
                    }

                    if (mPasswordCheck.getVisibility() != View.VISIBLE) {

                        SnackbarHelper.ShortSnackbar(findViewById(R.id.activity_login),"Input your password again.", SnackbarHelper.Info).show();
                        mPasswordCheck.setVisibility(View.VISIBLE);
                    }

                    if (password.equals(password_check) && !TextUtils.isEmpty(password)) {
                        Log.d(TAG, "onClick: signup");
                        doLogin(SIGNUP);
                    } else if (!password.equals(password_check)) {
                        SnackbarHelper.ShortSnackbar(findViewById(R.id.activity_login),"Passwords are different!", SnackbarHelper.Warning).show();
                    }
                }



            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidInput()){
                    if (mRemember.isChecked()) {
                        MainActivity.cache_email = email;
                        MainActivity.cache_password = password;
                    }

                    if (mPasswordCheck.getVisibility() == View.VISIBLE) {
                        mPasswordCheck.setVisibility(View.GONE);
                    }

                    Log.d(TAG, "onClick: login");
                    doLogin(LOGIN);

                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private boolean isValidInput(){
        email = mEmail.getText().toString();
        password = mPassword.getText().toString();
        password_check = mPasswordCheck.getText().toString();
        
        boolean isValid = false;

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isValidPassword(password)) {
            mPassword.setError(getString(R.string.login_error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.login_error_field_required));
            focusView = mEmail;
            cancel = true;
        } else if (!isValidEmail(email)) {
            mEmail.setError(getString(R.string.login_error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            isValid = true;
        }

        return isValid;
    }

    private boolean isValidEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() > 5;
    }


    private class AuthResultHandler implements Wilddog.AuthResultHandler {

        public AuthResultHandler() {}


        @Override
        public void onAuthenticated(AuthData authData) {
            MainActivity.hasLogin = true;
            MainActivity.userEmail = email;

            MainActivity.UID = authData.getUid();

            Toast.makeText(LoginActivity.this, "Welcome! "+ email, Toast.LENGTH_SHORT).show();

            finish();
            Log.i(TAG, " auth successful");
        }

        @Override
        public void onAuthenticationError(WilddogError wilddogError) {
            String warning = null;

            Log.d(TAG, "onAuthenticationError: error");

            warning = "Check your email or password again.";

            SnackbarHelper.LongSnackbar(findViewById(R.id.activity_login), warning , SnackbarHelper.Warning).setAction("Find password?", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWilddogRef.resetPassword(email,new ResetResultHandler());
                }
            }).setActionTextColor(Color.WHITE).show();
        }
    }

    public class SignUpResultHandler implements Wilddog.ResultHandler {
        public void onSuccess() {
            SnackbarHelper.ShortSnackbar(findViewById(R.id.activity_login),"success! Please login now.", SnackbarHelper.Confirm).show();

        }

        public void onError(WilddogError error) {
            if(error != null){
                SnackbarHelper.ShortSnackbar(findViewById(R.id.activity_login),"Failed! This email maybe taken.", SnackbarHelper.Warning).show();
            }
        }

    }

    public class ResetResultHandler implements Wilddog.ResultHandler {
        public void onSuccess() {
            SnackbarHelper.ShortSnackbar(findViewById(R.id.activity_login),"Success! We send a ResetEmail to this address.", SnackbarHelper.Confirm).show();

        }

        public void onError(WilddogError error) {
            if(error != null){
                SnackbarHelper.ShortSnackbar(findViewById(R.id.activity_login),"Failed! Did you sign up with this email?.", SnackbarHelper.Warning).show();
            }
        }

    }

    public void doLogin(int type) {
        switch (type) {
            case LOGIN:
                if (!MainActivity.hasLogin) {
                    Log.d(TAG, "doLogin: type:login " + email + " " + password);
                    mWilddogRef.authWithPassword(email, password, new AuthResultHandler());
                } else {
                    SnackbarHelper.ShortSnackbar(findViewById(R.id.activity_login),"You've logged in already.", SnackbarHelper.Alert).show();
                }
                break;
            case SIGNUP:
                Log.d(TAG, "doLogin: type:signup " + email +" " +password);
                mWilddogRef.createUser(email,password, new SignUpResultHandler());
                break;
        }

    }

}
