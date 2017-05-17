package com.rthc.freshair.activity;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.rthc.freshair.R;
import com.rthc.freshair.util.ThemeUtils;

public class SplashActivity extends AppCompatActivity {
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.zoomin, 0);
        setContentView(R.layout.activity_splash);

        context = this;

        ThemeUtils.setStatusBarTranslucent(this);

        startAnimation();
    }


    private void startAnimation() {
        final View splashIv = findViewById(R.id.splash_iv);
        ValueAnimator animator = ValueAnimator.ofObject(new FloatEvaluator(), 1.0f, 1.2f);
        animator.setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                if (value != 1.2f) {
                    splashIv.setScaleX(value);
                    splashIv.setScaleY(value);
                } else {
                    goToActivity();
                }
            }

            private void goToActivity() {
                Intent intent = new Intent(context,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, android.R.anim.fade_out);
                finish();
            }
        });
        animator.start();
    }
}
