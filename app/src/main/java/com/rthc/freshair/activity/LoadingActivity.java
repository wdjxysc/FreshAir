package com.rthc.freshair.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rthc.freshair.R;

public class LoadingActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    ImageView firstImageView;

    /**
     * ViewPager
     */
    private ViewPager viewPager;


    /**
     * 装点点的ImageView数组
     */
    private ImageView[] tips;

    /**
     * 装ImageView数组
     */
    private ImageView[] mImageViews;

    /**
     * 图片资源id
     */
    private int[] imgIdArray ;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        context = this;

        ViewGroup group = (ViewGroup)findViewById(R.id.viewGroup);//指示器点
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        firstImageView = (ImageView) findViewById(R.id.firstImageView);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        firstImageView.setVisibility(View.GONE);
                    }
                });
            }
        }).start();


        //载入图片资源ID
        imgIdArray = new int[]{R.mipmap.loading_21,R.mipmap.loading_22,R.mipmap.loading_23};


        //将点点加入到ViewGroup中
        tips = new ImageView[imgIdArray.length];
        for(int i=0; i<tips.length; i++){
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(10, 10));
            tips[i] = imageView;
            if(i == 0){
                tips[i].setBackgroundResource(R.drawable.button_bg_2);
            }else{
                tips[i].setBackgroundResource(R.drawable.button_bg_gray);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            group.addView(imageView, layoutParams);
        }

        //将图片装载到数组中
        mImageViews = new ImageView[imgIdArray.length];
        for(int i=0; i<mImageViews.length; i++){
            ImageView imageView = new ImageView(this);
            mImageViews[i] = imageView;
            imageView.setBackgroundResource(imgIdArray[i]);
        }

        mImageViews[mImageViews.length-1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, LoginActivity.class));
                finish();
            }
        });


        //设置Adapter
        viewPager.setAdapter(new MyAdapter());
        //设置监听，主要是设置点点的背景
        viewPager.setOnPageChangeListener(this);
        //设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
        viewPager.setCurrentItem(0);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setImageBackground(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 设置选中的tip的背景
     * @param selectItems
     */
    private void setImageBackground(int selectItems){
        for(int i=0; i<tips.length; i++){
            if(i == selectItems){
                tips[i].setBackgroundResource(R.drawable.button_bg_2);
            }else{
                tips[i].setBackgroundResource(R.drawable.button_bg_gray);
            }
        }
    }


    /**
     *
     * @author xiaanming
     *
     */
    public class MyAdapter extends PagerAdapter{

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager)container).addView(mImageViews[position]);
            return mImageViews[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager)container).removeView(mImageViews[position]);
        }

        @Override
        public int getCount() {
            return imgIdArray.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }
}
