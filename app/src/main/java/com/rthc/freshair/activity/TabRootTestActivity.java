package com.rthc.freshair.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rthc.freshair.R;
import com.rthc.freshair.util.Const;

import java.util.ArrayList;
import java.util.List;



public class TabRootTestActivity extends Activity {

    LinearLayout tab1;
    LinearLayout tab2;
    LinearLayout tab3;
    LinearLayout tab4;

    ImageView tab1ImageView;
    ImageView tab2ImageView;
    ImageView tab3ImageView;
    ImageView tab4ImageView;

    TextView tab1TextView;
    TextView tab2TextView;
    TextView tab3TextView;
    TextView tab4TextView;

    FrameLayout contentPanel;


    Fragment fragment1;
    Fragment fragment2;
    Fragment fragment3;
    Fragment fragment4;

    Context context;

    int currentTabIndex = 0;

    List<Fragment> fragments =new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_root_test);

        context = this;

        initData();

        initView();
    }

    void initData(){
        fragment1 = TestFragment.newInstance(1);
        fragment2 = TestFragment.newInstance(2);
        fragment3 = TestFragment.newInstance(3);
        fragment4 = TestFragment.newInstance(4);
        fragments.add(fragment1);
        fragments.add(fragment2);
        fragments.add(fragment3);
        fragments.add(fragment4);
    }

    void initView(){
        tab1 = (LinearLayout)findViewById(R.id.tab1);
        tab2 = (LinearLayout)findViewById(R.id.tab2);
        tab3 = (LinearLayout)findViewById(R.id.tab3);
        tab4 = (LinearLayout)findViewById(R.id.tab4);
        tab1ImageView = (ImageView)findViewById(R.id.tab1ImageView);
        tab2ImageView = (ImageView)findViewById(R.id.tab2ImageView);
        tab3ImageView = (ImageView)findViewById(R.id.tab3ImageView);
        tab4ImageView = (ImageView)findViewById(R.id.tab4ImageView);
        tab1TextView = (TextView)findViewById(R.id.tab1TextView);
        tab2TextView = (TextView)findViewById(R.id.tab2TextView);
        tab3TextView = (TextView)findViewById(R.id.tab3TextView);
        tab4TextView = (TextView)findViewById(R.id.tab4TextView);

        contentPanel = (FrameLayout)findViewById(R.id.contentPanel);

        tab1ImageView.setImageResource(R.mipmap.home_black);
        tab2ImageView.setImageResource(R.mipmap.home_gray);
        tab3ImageView.setImageResource(R.mipmap.home_gray);
        tab4ImageView.setImageResource(R.mipmap.home_gray);



        tab1TextView.setText("首页");
        tab2TextView.setText("首页");
        tab3TextView.setText("首页");
        tab4TextView.setText("首页");

        tab1.setOnClickListener(myClickListener);
        tab2.setOnClickListener(myClickListener);
        tab3.setOnClickListener(myClickListener);
        tab4.setOnClickListener(myClickListener);


        FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        for (int i=0;i<4;i++){
            transaction.add(contentPanel.getId(),fragments.get(i));
            transaction.hide(fragments.get(i));
        }
        transaction.show(fragment1);
        transaction.commit();
    }

    View.OnClickListener myClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(Const.TAG,"点击" + v.getId());
            switch (v.getId()){
                case R.id.tab1:
                    showFragment(0);
                    break;
                case R.id.tab2:
                    showFragment(1);
                    break;
                case R.id.tab3:
                    showFragment(2);
                    break;
                case R.id.tab4:
                    showFragment(3);
                    break;
            }
        }
    };


    void showFragment(int index){
        FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
        transaction.hide(fragments.get(currentTabIndex));
        transaction.show(fragments.get(index));
        currentTabIndex = index;
        transaction.commit();

        switch (index){
            case 0:
                tab1ImageView.setImageResource(R.mipmap.home_black);
                tab2ImageView.setImageResource(R.mipmap.home_gray);
                tab3ImageView.setImageResource(R.mipmap.home_gray);
                tab4ImageView.setImageResource(R.mipmap.home_gray);
                break;
            case 1:
                tab1ImageView.setImageResource(R.mipmap.home_gray);
                tab2ImageView.setImageResource(R.mipmap.home_black);
                tab3ImageView.setImageResource(R.mipmap.home_gray);
                tab4ImageView.setImageResource(R.mipmap.home_gray);
                break;
            case 2:
                tab1ImageView.setImageResource(R.mipmap.home_gray);
                tab2ImageView.setImageResource(R.mipmap.home_gray);
                tab3ImageView.setImageResource(R.mipmap.home_black);
                tab4ImageView.setImageResource(R.mipmap.home_gray);
                break;
            case 3:
                tab1ImageView.setImageResource(R.mipmap.home_gray);
                tab2ImageView.setImageResource(R.mipmap.home_gray);
                tab3ImageView.setImageResource(R.mipmap.home_gray);
                tab4ImageView.setImageResource(R.mipmap.home_black);
                break;
        }
    }
}
