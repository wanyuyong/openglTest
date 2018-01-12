package com.example.chaoyi.myapplication.cylinder;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * Created by chaoyi on 2018/1/11.
 */

public class CylinderView extends GLSurfaceView {

    private final float suo = 180.0f / 320;//角度缩放比例
    private float lastX;//上次的触控位置Y坐标
    private float lastY;//上次的触控位置Y坐标

    private CylinderRender mRenderer;

    public CylinderView(Context context) {
        super(context);
        mRenderer = new CylinderRender(context);   //创建场景渲染器
        setRenderer(mRenderer);             //设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY); //设置渲染模式为主动渲染
    }

    //触摸事件回调方法
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = y - lastY;//计算触控笔Y位移
                float dx = x - lastX;//计算触控笔Y位移
                mRenderer.adjustAngle(dy * suo, dx * suo, 0);
                requestRender();//重绘画面
        }
        lastX = x;
        lastY = y;
        return true;
    }
}
