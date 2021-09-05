package com.peter.myplayer.player;

import android.text.TextUtils;

import com.peter.myplayer.TimeInfoBean;
import com.peter.myplayer.listener.MyOnCompleteListener;
import com.peter.myplayer.listener.MyOnErrorListener;
import com.peter.myplayer.listener.MyOnLoadListener;
import com.peter.myplayer.listener.MyOnParparedListener;
import com.peter.myplayer.listener.MyOnPauseResumeListener;
import com.peter.myplayer.listener.MyOnTimeInfoListener;
import com.peter.myplayer.log.MyLog;

/**
 * Created by yangw on 2018-2-28.
 */

public class WePlayer {

    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("avcodec-57");
//        System.loadLibrary("avdevice-57");
//        System.loadLibrary("avfilter-6");
        System.loadLibrary("avformat-57");
        System.loadLibrary("avutil-55");
//        System.loadLibrary("postproc-54");
        System.loadLibrary("swresample-2");
        System.loadLibrary("swscale-4");
    }

    private static String source;//数据源
    private static TimeInfoBean timeInfoBean;
    private static boolean playNext = false;
    private MyOnParparedListener onParparedListener;
    private MyOnLoadListener onLoadListener;
    private MyOnPauseResumeListener onPauseResumeListener;
    private MyOnTimeInfoListener onTimeInfoListener;
    private MyOnErrorListener onErrorListener;
    private MyOnCompleteListener onCompleteListener;


    public WePlayer()
    {}

    /**
     * 设置数据源
     * @param source
     */
    public void setSource(String source)
    {
        this.source = source;
    }

    /**
     * 设置准备接口回调
     * @param onParparedListener
     */
    public void setOnParparedListener(MyOnParparedListener onParparedListener)
    {
        this.onParparedListener = onParparedListener;
    }

    public void setOnLoadListener(MyOnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    public void setOnPauseResumeListener(MyOnPauseResumeListener onPauseResumeListener) {
        this.onPauseResumeListener = onPauseResumeListener;
    }

    public void setOnTimeInfoListener(MyOnTimeInfoListener onTimeInfoListener) {
        this.onTimeInfoListener = onTimeInfoListener;
    }

    public void setOnErrorListener(MyOnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    public void setOnCompleteListener(MyOnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    public void parpared()
    {
        if(TextUtils.isEmpty(source))
        {
            MyLog.d("source not be empty");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                nativePrepared(source);
            }
        }).start();

    }

    public void start()
    {
        if(TextUtils.isEmpty(source))
        {
            MyLog.d("source is empty");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                nativeStart();
            }
        }).start();
    }

    public void pause()
    {
        nativePause();
        if(onPauseResumeListener != null)
        {
            onPauseResumeListener.onPause(true);
        }
    }

    public void resume()
    {
        nativeResume();
        if(onPauseResumeListener != null)
        {
            onPauseResumeListener.onPause(false);
        }
    }

    public void stop()
    {
        timeInfoBean = null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                nativeStop();
            }
        }).start();
    }

    public void seek(int secds)
    {
        nativeSeek(secds);
    }

    public void playNext(String url)
    {
        source = url;
        playNext = true;
        stop();
    }


    /**
     * c++回调java的方法
     */
    public void onCallParpared()
    {
        if(onParparedListener != null)
        {
            onParparedListener.onParpared();
        }
    }

    public void onCallLoad(boolean load)
    {
        if(onLoadListener != null)
        {
            onLoadListener.onLoad(load);
        }
    }

    public void onCallTimeInfo(int currentTime, int totalTime)
    {
        if(onTimeInfoListener != null)
        {
            if(timeInfoBean == null)
            {
                timeInfoBean = new TimeInfoBean();
            }
            timeInfoBean.setCurrentTime(currentTime);
            timeInfoBean.setTotalTime(totalTime);
            onTimeInfoListener.onTimeInfo(timeInfoBean);
        }
    }

    public void onCallError(int code, String msg)
    {
        if(onErrorListener != null)
        {
            stop();
            onErrorListener.onError(code, msg);
        }
    }

    public void onCallComplete()
    {
        if(onCompleteListener != null)
        {
            stop();
            onCompleteListener.onComplete();
        }
    }

    public void onCallNext()
    {
        if(playNext)
        {
            playNext = false;
            parpared();
        }
    }

    public void onCallRenderYUV(int width, int height, byte[] y, byte[] u, byte[] v)
    {
        MyLog.d("获取到视频的yuv数据");
    }

    private native void nativePrepared(String source);
    private native void nativeStart();
    private native void nativePause();
    private native void nativeResume();
    private native void nativeStop();
    private native void nativeSeek(int secds);

}
