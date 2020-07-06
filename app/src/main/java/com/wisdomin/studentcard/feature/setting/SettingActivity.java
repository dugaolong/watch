package com.wisdomin.studentcard.feature.setting;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wisdomin.studentcard.R;
import com.wisdomin.studentcard.api.OnReceiveListener;
import com.wisdomin.studentcard.base.BaseActivity;
import com.wisdomin.studentcard.base.BaseSDK;
import com.wisdomin.studentcard.broadcast.BroadcastConstant;
import com.wisdomin.studentcard.feature.setting.about.AboutActivity;
import com.wisdomin.studentcard.feature.setting.sound.SoundActivity;
import com.wisdomin.studentcard.feature.setting.wallpaper.WallpaperActivity;
import com.wisdomin.studentcard.util.LogUtil;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private ImageView back_top;
    private RelativeLayout rl_voice;
    private RelativeLayout rl_wall_paper;
    private RelativeLayout rl_update_version;
    private RelativeLayout rl_about;

    private Context mContext;
    private UpdateReceiver mUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_view);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mContext = this;
        initView();

    }


    @Override
    protected void onResume() {
        super.onResume();
        mUpdateReceiver = new UpdateReceiver();
        final IntentFilter intentFilter = new IntentFilter(BroadcastConstant.UPDATE);
        mContext.registerReceiver(mUpdateReceiver, intentFilter);
    }

    private void initView() {
        back_top = findViewById(R.id.back_top);
        back_top.setOnClickListener(this);
        rl_voice = findViewById(R.id.rl_voice);
        rl_wall_paper = findViewById(R.id.rl_wall_paper);
        rl_update_version = findViewById(R.id.rl_update_version);
        rl_about = findViewById(R.id.rl_about);
        rl_voice.setOnClickListener(this);
        rl_wall_paper.setOnClickListener(this);
        rl_update_version.setOnClickListener(this);
        rl_about.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.rl_voice) {

            startActivity(new Intent(SettingActivity.this, SoundActivity.class));

        } else if (id == R.id.rl_wall_paper) {
            startActivity(new Intent(SettingActivity.this, WallpaperActivity.class));

        } else if (id == R.id.rl_update_version) {
            //电量
//            if (MainActivity.getSystemBattery(mContext) >= 50) {//电量大于50%
            //请求更新apk
            BaseSDK.getInstance().geUpdate("1@", new OnReceiveListener() {
                @Override
                public void onResponse(final String msg) {
                }
            });
            Toast.makeText(mContext, "正在检测新版本更新", Toast.LENGTH_SHORT).show();
//            }else {
//                Toast.makeText(mContext,"电量少于50%，无法更新",Toast.LENGTH_SHORT).show();
//            }
//            startActivity(new Intent(Settings.ACTION_SETTINGS));
        } else if (id == R.id.rl_about) {

            startActivity(new Intent(mContext, AboutActivity.class));

        } else if (id == R.id.back_top) {

            finish();
        }
    }


    class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.e("onReceive: action: " + action);
            if (action.equals(BroadcastConstant.UPDATE)) {//Action

                boolean is_head = intent.getExtras().getBoolean("is_head");
                if (!is_head) {
                    Toast.makeText(mContext, "正在更新，请勿操作", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "已经是最新版本", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    private void unregisterReceiver() {
        if (null != mUpdateReceiver) {
            mContext.unregisterReceiver(mUpdateReceiver);
        }
    }
}