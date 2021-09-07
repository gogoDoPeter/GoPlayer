package com.peter.myplayer.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

public class MyGLSurfaceView extends GLSurfaceView {
    private static final String TAG="my_tag_"+MyGLSurfaceView.class.getSimpleName();
    private MyRender myRender;

    public MyGLSurfaceView(Context context) {
        this(context, null);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        myRender = new MyRender(context);
        setRenderer(myRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setYUVData(int width, int height, byte[] y, byte[] u, byte[] v)
    {
        if(myRender != null)
        {
            Log.d(TAG,"setYUVRenderData");
            myRender.setYUVRenderData(width, height, y, u, v);
            requestRender();//setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY); 调用requestRender后,myRender执行onDrawFrame刷线数据到surface
        }
    }
}
