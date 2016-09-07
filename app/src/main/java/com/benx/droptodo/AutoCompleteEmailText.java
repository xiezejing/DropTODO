package com.benx.droptodo;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;

/**
 * Project: DropTODO
 * Author:  Ben.X
 * Date:    2016/8/28
 */
public class AutoCompleteEmailText extends AutoCompleteTextView {

    private Context mContext = null;
    private static final String[] emailSuffix = { "@qq.com", "@163.com", "@126.com", "@gmail.com", "@sina.com", "@hotmail.com",
            "@yahoo.cn", "@sohu.com", "@foxmail.com", "@139.com"};

    public AutoCompleteEmailText(Context context) {
        this(context, null);
    }

    public AutoCompleteEmailText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {

        addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                clearListSelection();
                String input = s.toString();
                if (!isPopupShowing()) {
                    showDropDown();
                }
                createCandidateEmail(input);

            }
        });
        setThreshold(1);

    }

    private void createCandidateEmail(String name) {
        ArrayList<String> candidateString = new ArrayList<String>();
        int index = name.indexOf("@");
        if (index > 0) {
            String suffix = name.substring(index);
            for (String string : emailSuffix) {
                if (string.startsWith(suffix)) {
                    candidateString.add(name.substring(0, index) + string);
                }
            }
        } else {
            for (int i = 0; i < emailSuffix.length; ++i) {
                candidateString.add(name + emailSuffix[i]);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, candidateString);
        setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
