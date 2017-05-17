package com.rthc.freshair.activity;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rthc.freshair.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestFragment extends Fragment {


    public TestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    /**
     * 4      * 静态工厂方法需要一个int型的值来初始化fragment的参数，
     * 5      * 然后返回新的fragment到调用者
     * 6
     */
    public static TestFragment newInstance(int index) {
        TestFragment f = new TestFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

}
