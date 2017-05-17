package com.rthc.freshair.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.rthc.freshair.MyApplication;

/**
 * Created by Administrator on 2016/6/7.
 */
public class FZCustomEditText extends EditText {
    public FZCustomEditText(Context context) {
        super(context);
        init(context);
    }

    public FZCustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FZCustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    void init(Context context){
        if(isInEditMode()) return;
        setTypeface(MyApplication.getInstance().getTypeface());
    }
}
