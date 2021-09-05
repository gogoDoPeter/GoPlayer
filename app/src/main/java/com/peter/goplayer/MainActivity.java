package com.peter.goplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.peter.myplayer.TimeInfoBean;
import com.peter.myplayer.listener.MyOnCompleteListener;
import com.peter.myplayer.listener.MyOnErrorListener;
import com.peter.myplayer.listener.MyOnLoadListener;
import com.peter.myplayer.listener.MyOnParparedListener;
import com.peter.myplayer.listener.MyOnPauseResumeListener;
import com.peter.myplayer.listener.MyOnTimeInfoListener;
import com.peter.myplayer.log.MyLog;
import com.peter.myplayer.player.WePlayer;
import com.peter.myplayer.util.MyTimeUtil;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "my_tag_" + MainActivity.class.getSimpleName();
    private WePlayer wePlayer;
    private TextView tvTime;
    private static final String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        tvTime = (TextView)findViewById(R.id.tv_time);

        wePlayer = new WePlayer();
        wePlayer.setOnParparedListener(new MyOnParparedListener() {
            @Override
            public void onParpared() {
                MyLog.d("准备好了，可以开始播放声音了");
                wePlayer.start();
            }
        });
        wePlayer.setOnLoadListener(new MyOnLoadListener() {
            @Override
            public void onLoad(boolean load) {
                if(load)
                {
                    MyLog.d("加载中...");
                }
                else
                {
                    MyLog.d("播放中...");
                }
            }
        });

        wePlayer.setOnPauseResumeListener(new MyOnPauseResumeListener() {
            @Override
            public void onPause(boolean pause) {
                if(pause)
                {
                    MyLog.d("暂停中...");
                }
                else
                {
                    MyLog.d("播放中...");
                }
            }
        });

        wePlayer.setOnTimeInfoListener(new MyOnTimeInfoListener() {
            @Override
            public void onTimeInfo(TimeInfoBean timeInfoBean) {
//                MyLog.d(timeInfoBean.toString());
                Message message = Message.obtain();
                message.what = 1;
                message.obj = timeInfoBean;
                handler.sendMessage(message);

            }
        });

        wePlayer.setOnErrorListener(new MyOnErrorListener() {
            @Override
            public void onError(int code, String msg) {
                MyLog.d("code:" + code + ", msg:" + msg);
            }
        });

        wePlayer.setOnCompleteListener(new MyOnCompleteListener() {
            @Override
            public void onComplete() {
                MyLog.d("播放完成了");
            }
        });

    }

    private void checkPermission() {
        Log.d(TAG, "checkPermission +");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, 200);
                    return;
                }
            }
        }
        Log.d(TAG, "checkPermission -");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requestCode == 200) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, 200);
                    return;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            checkPermission();
        }
    }

    public void begin(View view) {
        //        wlPlayer.setSource("/mnt/shared/Other/testvideo/楚乔传第一集.mp4");
        wePlayer.setSource("/mnt/sdcard/海贼王-900-720P.mp4");
//        wlPlayer.setSource("/mnt/sdcard/寻龙传说-1080P.mkv");
//        wlPlayer.setSource("/mnt/sdcard/云南虫谷-01.mp4");  //1920x804
//        wlPlayer.setSource("/mnt/sdcard/名侦探柯南774.rmvb");//848x480
//        wlPlayer.setSource("http://mpge.5nd.com/2015/2015-11-26/69708/1.mp3");
//        wlPlayer.setSource("http://ngcdn004.cnr.cn/live/dszs/index12.m3u8");
        wePlayer.parpared();
    }

    public void stop(View view) {
        wePlayer.stop();
    }

    public void pause(View view) {
        wePlayer.pause();
    }

    public void resume(View view) {
        wePlayer.resume();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1)
            {
                TimeInfoBean wlTimeInfoBean = (TimeInfoBean) msg.obj;
                tvTime.setText(MyTimeUtil.secdsToDateFormat(wlTimeInfoBean.getTotalTime(), wlTimeInfoBean.getTotalTime())
                        + "/" + MyTimeUtil.secdsToDateFormat(wlTimeInfoBean.getCurrentTime(), wlTimeInfoBean.getTotalTime()));
            }
        }
    };

    public void seek(View view) {
        wePlayer.seek(215);
    }

    public void next(View view) {
        //wlPlayer.playNext("/mnt/shared/Other/testvideo/楚乔传第一集.mp4");
    }
}