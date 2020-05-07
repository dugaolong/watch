package com.ctop.studentcard.feature.temperature;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ctop.studentcard.R;
import com.ctop.studentcard.base.BaseActivity;
import com.ctop.studentcard.base.BaseSDK;
import com.ctop.studentcard.broadcast.BroadcastConstant;
import com.ctop.studentcard.util.AlphaAnimationUtil;
import com.ctop.studentcard.util.LogUtil;


public class TemActivity extends BaseActivity implements View.OnClickListener {

    private PercentCircle percentCircle;
    private RippleView mRippleView;
    private RelativeLayout start_rl;
    private RelativeLayout tem_rl;
    private TextView tv_tem;
    private TemPostReceiver mTemPostReceiver;
    private String timeTem;
    private String valueTem;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                percentCircle.setVisibility(View.VISIBLE);
                percentCircle.setTargetPercent(100, mHandler);
                mRippleView.setVisibility(View.GONE);

            } else if (msg.what == 1) {

                AlphaAnimationUtil.startAlphaOut(percentCircle);
                tem_rl.setVisibility(View.VISIBLE);
                tv_tem.setText(valueTem);
                AlphaAnimationUtil.translationXIn(tem_rl);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tem);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        percentCircle = findViewById(R.id.percentCircle);
        mRippleView = findViewById(R.id.rippleView);
        start_rl = findViewById(R.id.start_rl);
        tem_rl = findViewById(R.id.tem_rl);
        tv_tem = findViewById(R.id.tv_tem);
        percentCircle.setVisibility(View.GONE);

        start_rl.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.start_rl) {

            ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setFillAfter(true);
            scaleAnimation.setDuration(4000L);

            RotateAnimation rotateAnimation = new RotateAnimation(0f, 3600f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setFillAfter(true);
            rotateAnimation.setDuration(4000L);

            AnimationSet animationSet = new AnimationSet(false);
            animationSet.addAnimation(scaleAnimation);
            animationSet.addAnimation(rotateAnimation);
            animationSet.setFillAfter(true);

            start_rl.startAnimation(animationSet);
            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    start_rl.setVisibility(View.GONE);
                    tem_rl.setVisibility(View.GONE);
                    mRippleView.setVisibility(View.VISIBLE);
                    mRippleView.setRadius(70, mHandler);
                    //注册温度结果广播
                    mTemPostReceiver = new TemPostReceiver();
                    final IntentFilter intentFilter = new IntentFilter(BroadcastConstant.TEMPERATURE_RESULT_POST);
                    registerReceiver(mTemPostReceiver, intentFilter);
                    //发广播，请求测温
                    sendBrodcast();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });


        }
    }

    private void sendBrodcast() {
        Intent intent = new Intent();
        intent.setAction(BroadcastConstant.TEMPERATURE_START);
        sendBroadcast(intent);// 发送
        LogUtil.e("sendTemBrodcast");
    }

    class TemPostReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.e("TemReceiver");
            String action = intent.getAction();
            if (action.equals(BroadcastConstant.TEMPERATURE_RESULT_POST)) {
                valueTem = intent.getStringExtra("value");
                LogUtil.e("valueTem===" + valueTem);
                tv_tem.setText(valueTem);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTemPostReceiver != null) {
            unregisterReceiver(mTemPostReceiver);
        }
    }

    private void scaleDown(View view, ScaleAnimation scaleAnimation) {

    }


}
