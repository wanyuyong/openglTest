package com.example.chaoyi.myapplication.cylinder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.example.chaoyi.myapplication.utility.Util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by chaoyi on 2018/1/11.
 */

public class Cylinder {
    private Context context;

    private boolean onlyDrawLine = false; //只画线

    private FloatBuffer vertexBufferPillar; //柱面顶点
    private FloatBuffer vertexBufferTexture; //纹理顶点

    private float R; //半径
    private float height; //圆柱高度
    private float perDegrees;  //圆被切割的每一小份的角度
    private int vertexNum; //圆顶点个数

    private int texture;

    private float centralPointX = 0f, centralPointY = 0f, centralPointZ = 0f; //圆柱体的中心点坐标

    private float angleX;
    private float angleY;
    private float angleZ;

    public Cylinder(float r, float height, float perDegrees, GL10 gl, Context context) {
        this.context = context;
        R = r;
        this.height = height;
        this.perDegrees = perDegrees;
        this.vertexNum = (int) (360 / perDegrees) + 1;
        this.calculateVertex();
        this.loadTexture(gl);
    }

    public void setAngle(float angleX, float angleY, float angleZ) {
        this.angleX = angleX;
        this.angleY = angleY;
        this.angleZ = angleZ;
    }

    public void adjustAngle(float angleX, float angleY, float angleZ) {
        this.angleX = angleX + this.angleX;
        this.angleY = angleY + this.angleY;
        this.angleZ = angleZ + this.angleZ;
    }

    private void calculateVertex() {
        this.vertexBufferPillar = calculatePillarVertex(centralPointX, centralPointY, centralPointZ);
        this.vertexBufferTexture = calculateTextureVertex();
    }

    /**
     * 计算柱面纹理坐标
     *
     * @return
     */
    private FloatBuffer calculateTextureVertex() {
        float[] vertexs = new float[this.vertexNum * 2 * 2];
        float step = 1f / (this.vertexNum - 1);
        for (int i = 0; i < this.vertexNum; i += 1) {
            vertexs[i * 4] = 1 - i * step;
            vertexs[i * 4 + 1] = 0;

            vertexs[i * 4 + 2] = 1 - i * step;
            vertexs[i * 4 + 3] = 1;
        }

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertexs.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertexs);
        vertexBuffer.position(0);

        return vertexBuffer;
    }

    /**
     * 通过圆心，计算圆的各顶点坐标
     *
     * @param x 中心点x
     * @param y 中心点y
     * @param z 中心点z
     */
    private FloatBuffer calculatePillarVertex(float x, float y, float z) {
        ArrayList<Float> vertexArray = new ArrayList<Float>(); //顶点存放列表

        for (float degrees = 0; degrees <= 360; degrees += perDegrees) {
            vertexArray.add((float) (x + R * Math.cos(degrees * Math.PI / 180)));
            vertexArray.add(y + height / 2);
            vertexArray.add((float) (z + R * Math.sin(degrees * Math.PI / 180)));

            vertexArray.add((float) (x + R * Math.cos(degrees * Math.PI / 180)));
            vertexArray.add(y - height / 2);
            vertexArray.add((float) (z + R * Math.sin(degrees * Math.PI / 180)));
        }

        return Util.getFloatBuffer(vertexArray);
    }

    private void loadTexture(GL10 gl) {
        Bitmap bitmap = null;
        try {
            // 加载位图
            bitmap = BitmapFactory.decodeResource(this.context.getResources(),
                    com.example.chaoyi.myapplication.R.mipmap.img);
            int[] textures = new int[1];
            // 指定生成N个纹理（第一个参数指定生成1个纹理），
            // textures数组将负责存储所有纹理的代号。
            gl.glGenTextures(1, textures, 0);
            // 获取textures纹理数组中的第一个纹理
            texture = textures[0];
            // 通知OpenGL将texture纹理绑定到GL10.GL_TEXTURE_2D目标中
            gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);
            // 设置纹理被缩小（距离视点很远时被缩小）时候的滤波方式
            gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                    GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            // 设置纹理被放大（距离视点很近时被方法）时候的滤波方式
            gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                    GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            // 设置在横向、纵向上都是平铺纹理
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                    GL10.GL_REPEAT);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                    GL10.GL_REPEAT);
            // 加载位图生成纹理
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        } finally {
            // 生成纹理之后，回收位图
            if (bitmap != null)
                bitmap.recycle();
        }
    }

    public void draw(GL10 gl) {


        gl.glRotatef(angleX, 1, 0, 0);//旋转
        gl.glRotatef(angleY, 0, 1, 0);
        gl.glRotatef(angleZ, 0, 0, 1);

        if (onlyDrawLine){
            // 启用顶点座标数据
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.vertexBufferPillar);//指定顶点缓冲
            gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, this.vertexNum * 2);
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        } else {
            // 启用顶点座标数据
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            // 启用贴图座标数组数据
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.vertexBufferPillar);//指定顶点缓冲
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, this.vertexBufferTexture);// 设置贴图的的座标数据

            // 执行纹理贴图
            gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, this.vertexNum * 2);

            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        }
    }

}
