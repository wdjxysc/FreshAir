package com.rthc.freshair.listener;

import android.view.View;

/**
 * Created by Administrator on 2016/6/8.
 */
public class RcListener{
    public interface MyItemClickListener {
        void onItemClick(View view, int position);
    }
    public interface MyItemLongClickListener{
        void onItemLongClick(View view, int position);
    }
}

