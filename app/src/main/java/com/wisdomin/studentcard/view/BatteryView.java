package com.wisdomin.studentcard.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.BatteryManager;
import android.util.AttributeSet;
import android.view.View;

public class BatteryView extends View {
    private int mMargin = 1;    //电池内芯与边框的距离
    private int mBoder = 1;     //电池外框的宽带
    private int mWidth = 24;    //总长
    private int mHeight = 12;   //总高
    private int mHeadWidth = 2;
    private int mHeadHeight = 3;

    private RectF mMainRect;
    private RectF mHeadRect;
    private float mRadius = 0f;   //圆角
    private float mPower;
    private Path path;
    private boolean mIsCharging;    //是否在充电


    public BatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BatteryView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        mHeadRect = new RectF(0, (mHeight - mHeadHeight)/2, mHeadWidth, (mHeight + mHeadHeight)/2);

        float left = mHeadRect.width();
        float top = mBoder;
        float right = mWidth-mBoder;
        float bottom = mHeight-mBoder;
        mMainRect = new RectF(left, top, right, bottom);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint1 = new Paint();

        //画电池头
        paint1.setStyle(Paint.Style.FILL);  //实心
        paint1.setColor(Color.WHITE);
        canvas.drawRect(mHeadRect, paint1);

        //画外框
        paint1.setStyle(Paint.Style.STROKE);    //设置空心矩形
        paint1.setStrokeWidth(mBoder);          //设置边框宽度
        paint1.setColor(Color.WHITE);
        canvas.drawRoundRect(mMainRect, mRadius, mRadius, paint1);

        //画电池芯
        Paint paint = new Paint();
        if (mIsCharging) {
            paint.setColor(Color.GREEN);
            int width   = (int) (mPower * (mMainRect.width() - mMargin*2));
            int left    = (int) (mMainRect.right - mMargin - width);
            int right   = (int) (mMainRect.right - mMargin*2);
            int top     = (int) (mMainRect.top + mMargin);
            int bottom  = (int) (mMainRect.bottom - mMargin*2);
            Rect rect = new Rect(left,top,right, bottom);
            canvas.drawRect(rect, paint);

            Paint paintLight = new Paint();
            paintLight.setColor(Color.WHITE);
            paintLight.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawPath(path, paintLight);//绘制完全的闪电
        } else {
            if (mPower < 0.1) {
                paint.setColor(Color.RED);
            } else {
                paint.setColor(Color.WHITE);
            }
            int width   = (int) (mPower * (mMainRect.width() - mMargin*2));
            int left    = (int) (mMainRect.right - mMargin - width);
            int right   = (int) (mMainRect.right - mMargin*2);
            int top     = (int) (mMainRect.top + mMargin);
            int bottom  = (int) (mMainRect.bottom - mMargin*2);
            Rect rect = new Rect(left,top,right, bottom);
            canvas.drawRect(rect, paint);
        }



    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);

        //使用path绘制闪电形状
        path = new Path();
        path.moveTo(mWidth * 4 / 7, 0);

        path.lineTo(mWidth * 5 / 14, 4 * mHeight / 7);
        path.lineTo(mWidth * 3 / 7, 4 * mHeight / 7);
        path.lineTo(mWidth * 3 / 7, mHeight);
        path.lineTo(mWidth  * 9 / 14, 3 * mHeight / 7);
        path.lineTo(mWidth * 4 / 7, 3 * mHeight / 7);
        path.close();

    }

    private void setPower(float power) {
        mPower = power;
        invalidate();
    }

    private BroadcastReceiver mPowerConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            mIsCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            setPower(((float) level)/scale);
        }
    };

    @Override
    protected void onAttachedToWindow() {
        getContext().registerReceiver(mPowerConnectionReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        getContext().unregisterReceiver(mPowerConnectionReceiver);
        super.onDetachedFromWindow();
    }
}