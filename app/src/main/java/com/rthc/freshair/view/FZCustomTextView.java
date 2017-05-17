package com.rthc.freshair.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.rthc.freshair.MyApplication;

/**
 * Created by Administrator on 2016/6/7.
 */
public class FZCustomTextView extends TextView {


    public FZCustomTextView(Context context) {
        super(context);
        init(context);
    }

    public FZCustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FZCustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    void init(Context context){
        //以下方式加载自定义字体空间时 界面会出现卡顿 资源使用过多
//        AssetManager assetManager = context.getAssets();
//        Typeface typeface = Typeface.createFromAsset(assetManager,"font/fz_shuizhu_jian.ttf");
//        setTypeface(typeface);

        //这种方式会在可视化设计时报错 需加上isInEditMode
        if(isInEditMode()) return;
        setTypeface(MyApplication.getInstance().getTypeface());
    }

}
