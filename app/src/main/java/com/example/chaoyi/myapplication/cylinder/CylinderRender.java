package com.example.chaoyi.myapplication.cylinder;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by chaoyi on 2018/1/11.
 */

public class CylinderRender implements GLSurfaceView.Renderer {

    private Context context;
    private Cylinder cylinder;

    public CylinderRender(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
        //关闭抗抖动
        gl.glDisable(GL10.GL_DITHER);
        //设置特定Hint项目的模式，这里为设置为使用快速模式
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        //设置屏幕背景色黑色RGBA
        gl.glClearColor(0, 0, 0, 0);
        //设置着色模型为平滑着色
        gl.glShadeModel(GL10.GL_SMOOTH);
        //启用深度测试
        gl.glEnable(GL10.GL_DEPTH_TEST);
        // 启用2D纹理贴图
        gl.glEnable(GL10.GL_TEXTURE_2D);

        cylinder = new Cylinder(4, 8, 2, gl, this.context);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //设置视窗大小及位置
        gl.glViewport(0, 0, width, height);
        //设置当前矩阵为投影矩阵
        gl.glMatrixMode(GL10.GL_PROJECTION);
        //设置当前矩阵为单位矩阵
        gl.glLoadIdentity();
        //计算透视投影的比例
        float ratio = (float) width / height;
        //调用此方法计算产生透视投影矩阵
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 100);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //清除颜色缓存
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        //设置当前矩阵为模式矩阵
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        //设置当前矩阵为单位矩阵
        gl.glLoadIdentity();

        gl.glPushMatrix();//保护变换矩阵现场

        gl.glTranslatef(0, 0, -12f);//平移
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        cylinder.draw(gl);//绘制
        gl.glPopMatrix();//恢复变换矩阵现场
    }

    public void setAngle(float angleX, float angleY, float angleZ) {
        this.cylinder.setAngle(angleX, angleY, angleZ);
    }

    public void adjustAngle(float angleX, float angleY, float angleZ) {
        this.cylinder.adjustAngle(angleX, angleY, angleZ);
    }
}
