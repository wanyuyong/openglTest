package com.example.chaoyi.myapplication.pagetexture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.opengl.GLUtils;
import android.util.Log;

import com.example.chaoyi.myapplication.page.Page;
import com.example.chaoyi.myapplication.utility.Util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by chaoyi on 2018/1/12.
 */

public class PageTexture {

    /**
     * 纸页的宽高
     */
    private float width;
    private float height;

    private float R = .5f;

    private float centralPointX = 0f, centralPointY = 0f, centralPointZ = 0f; //纸张的中心点坐标


    private float angleX;
    private float angleY;
    private float angleZ;

    private FloatBuffer vertexBuffer;
    private FloatBuffer vertexTextureBuffer;

    private int texture;

    private float perDegrees;  //曲面被切割的每一小份的角度
    private int vertexNum; //曲面切割的点个数

    private Context context;

    public PageTexture(float width, float height, float perDegrees, GL10 gl, Context context) {
        this.width = width;
        this.height = height;
        this.context = context;
        this.perDegrees = perDegrees;
        this.vertexNum = (int) (180 / perDegrees) + 1;

        this.calculateVertex();
        this.loadTexture(gl);

    }

    private void calculateVertex() {
        float thumbX = 0, thumbY = 0;
        calculatePageVertex(thumbX, thumbY);
        calculateTextureVertex(thumbX, thumbY);
    }

    /**
     * 计算纹理坐标
     *
     * @return
     */
    private void calculateTextureVertex(float thumbX, float thumbY) {
        float Ax = thumbX, Ay = thumbY;
        float Fx = width / 2f, Fy = -height / 2f;
        float Gx = (Ax + Fx) / 2f, Gy = (Ay + Fy) / 2f;
        float Mx = Gx, My = Fy;
        float GM = (float) Math.abs(Math.sqrt(Math.pow(Gx - Mx, 2) + Math.pow(Gy - My, 2)));
        float FM = (float) Math.abs(Math.sqrt(Math.pow(Fx - Mx, 2) + Math.pow(Fy - My, 2)));
        float EM = GM * GM / FM;
        float Ex = Gx - EM, Ey = Fy;
        float EF = (float) Math.abs(Math.sqrt(Math.pow(Ex - Fx, 2) + Math.pow(Ey - Fy, 2)));
        float FH = EF * GM / EM;
        float Hx = Fx, Hy = Fy + FH;
        float Nx = (Ax + Gx) / 2f, Ny = (Ay + Gy) / 2f;
        float FN = (float) Math.abs(Math.sqrt(Math.pow(Nx - Fx, 2) + Math.pow(Ny - Fy, 2)));
        float FG = (float) Math.abs(Math.sqrt(Math.pow(Fx - Gx, 2) + Math.pow(Fy - Gy, 2)));
        float CF = FN * EF / FG;
        float Cx = Fx - CF, Cy = Fy;
        float FJ = FH * CF / EF;
        float Jx = Fx, Jy = Fy + FJ;
        float Ox = width / 2f, Oy = height / 2f;
        float Px = -width / 2f, Py = -height / 2f;

        /**
         * 直线公式 y = kx + b
         * 已知两点（x1,y1) ,(x2,y2) ; k = (y2-y1)/(x2-x1), b =  (x2*y1-y2*x1)/(x2-x1)
         * 两条直线的交点： x = (b2-b1)/(k1-k2) , y = k1 * (b2-b1)/(k1-k2) + b1
         */

        /**
         * a-e 线
         */
        float k1 = (Ey - Ay) / (Ex - Ax);
        float b1 = (Ex * Ay - Ey * Ax) / (Ex - Ax);
        /**
         * c-j线
         */
        float k2 = (Jy - Cy) / (Jx - Cx);
        float b2 = (Jx * Cy - Jy * Cx) / (Jx - Cx);
        // a-e 和 c-j 的交点B
        float Bx = (b2 - b1) / (k1 - k2), By = k1 * (b2 - b1) / (k1 - k2) + b1;

        /**
         * a-h 线
         */
        k1 = (Hy - Ay) / (Hx - Ax);
        b1 = (Hx * Ay - Hy * Ax) / (Hx - Ax);
        // a-h 和 c-j 的交点K
        float Kx = (b2 - b1) / (k1 - k2), Ky = k1 * (b2 - b1) / (k1 - k2) + b1;

        float AB = (float) Math.abs(Math.sqrt(Math.pow(Ax - Bx, 2) + Math.pow(Ay - By, 2)));
        float AK = (float) Math.abs(Math.sqrt(Math.pow(Ax - Kx, 2) + Math.pow(Ay - Ky, 2)));
        float OJ = (float) Math.abs(Math.sqrt(Math.pow(Ox - Jx, 2) + Math.pow(Oy - Jy, 2)));
        float PC = (float) Math.abs(Math.sqrt(Math.pow(Px - Cx, 2) + Math.pow(Py - Cy, 2)));


        int totalNum = 5 + this.vertexNum * 2 + 1; // 总点数
        int size = totalNum * 2;
        float[] vertexs = new float[size];

        /**
         *  先添加底部纸张5个点
         */

        vertexs[0] = 0f;
        vertexs[1] = 0f;

        vertexs[2] = 1f;
        vertexs[3] = 0f;

        vertexs[4] = 0f;
        vertexs[5] = 1f;

        vertexs[6] = 1;
        vertexs[7] = OJ / height;

        vertexs[8] = 1 - PC / width;
        vertexs[9] = 1;

        /**
         *  曲面被分割成 vertexNum - 1 份
         */
        int start = 10;
        float len1 = this.height - AK - OJ; //上弧线的长度
        float len2 = this.width - AB - PC; //下弧线的长度
        for (int i = 0; i < vertexNum; i++) {
            vertexs[start + i * 4] = 1;
            vertexs[start + i * 4 + 1] = ((len1 / (vertexNum - 1)) * i + OJ) / height;

            vertexs[start + i * 4 + 2] = ((len2 / (vertexNum - 1)) * i + PC) / width;
            vertexs[start + i * 4 + 3] = 1;
        }

        //最后一个点
        vertexs[size - 2] = 1;
        vertexs[size - 1] = 1;

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertexs.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertexs);
        vertexBuffer.position(0);

        this.vertexTextureBuffer = vertexBuffer;
    }

    /**
     * @param thumbX 折角的坐标x
     * @param thumbY 折角的坐标y
     * @return
     */
    private void calculatePageVertex(float thumbX, float thumbY) {

        float Ax = thumbX, Ay = thumbY;
        float Fx = width / 2f, Fy = -height / 2f;
        float Gx = (Ax + Fx) / 2f, Gy = (Ay + Fy) / 2f;
        float Mx = Gx, My = Fy;
        float GM = (float) Math.abs(Math.sqrt(Math.pow(Gx - Mx, 2) + Math.pow(Gy - My, 2)));
        float FM = (float) Math.abs(Math.sqrt(Math.pow(Fx - Mx, 2) + Math.pow(Fy - My, 2)));
        float EM = GM * GM / FM;
        float Ex = Gx - EM, Ey = Fy;
        float EF = (float) Math.abs(Math.sqrt(Math.pow(Ex - Fx, 2) + Math.pow(Ey - Fy, 2)));
        float FH = EF * GM / EM;
        float Hx = Fx, Hy = Fy + FH;
        float Nx = (Ax + Gx) / 2f, Ny = (Ay + Gy) / 2f;
        float FN = (float) Math.abs(Math.sqrt(Math.pow(Nx - Fx, 2) + Math.pow(Ny - Fy, 2)));
        float FG = (float) Math.abs(Math.sqrt(Math.pow(Fx - Gx, 2) + Math.pow(Fy - Gy, 2)));
        float CF = FN * EF / FG;
        float Cx = Fx - CF, Cy = Fy;
        float FJ = FH * CF / EF;
        float Jx = Fx, Jy = Fy + FJ;

        /**
         * 直线公式 y = kx + b
         * 已知两点（x1,y1) ,(x2,y2) ; k = (y2-y1)/(x2-x1), b =  (x2*y1-y2*x1)/(x2-x1)
         * 两条直线的交点： x = (b2-b1)/(k1-k2) , y = k1 * (b2-b1)/(k1-k2) + b1
         */

        /**
         * a-e 线
         */
        float k1 = (Ey - Ay) / (Ex - Ax);
        float b1 = (Ex * Ay - Ey * Ax) / (Ex - Ax);
        /**
         * c-j线
         */
        float k2 = (Jy - Cy) / (Jx - Cx);
        float b2 = (Jx * Cy - Jy * Cx) / (Jx - Cx);
        // a-e 和 c-j 的交点B
        float Bx = (b2 - b1) / (k1 - k2), By = k1 * (b2 - b1) / (k1 - k2) + b1;

        /**
         * a-h 线
         */
        k1 = (Hy - Ay) / (Hx - Ax);
        b1 = (Hx * Ay - Hy * Ax) / (Hx - Ax);
        // a-h 和 c-j 的交点K
        float Kx = (b2 - b1) / (k1 - k2), Ky = k1 * (b2 - b1) / (k1 - k2) + b1;

        ArrayList<Float> arrayList = new ArrayList<>();

        /**
         * 计算底部纸张的5个点
         */
        arrayList.add(centralPointX - width / 2f);
        arrayList.add(centralPointY + height / 2f);
        arrayList.add(0f);

        arrayList.add(centralPointX + width / 2f);
        arrayList.add(centralPointY + height / 2f);
        arrayList.add(0f);

        arrayList.add(centralPointX - width / 2f);
        arrayList.add(centralPointY - height / 2f);
        arrayList.add(0f);

        arrayList.add(Jx);
        arrayList.add(Jy);
        arrayList.add(0f);

        arrayList.add(Cx);
        arrayList.add(Cy);
        arrayList.add(0f);

        Path path1 = getBezierPath(Jx, Jy, Hx, Hy, Kx, Ky);
        Path path2 = getBezierPath(Cx, Cy, Ex, Ey, Bx, By);
        arrayList.addAll(getCurvedVertex(path1, path2, vertexNum));

        /**
         *  最后加上（thumbX， thumbY）点
         */
        arrayList.add(thumbX);
        arrayList.add(thumbY);
        arrayList.add(2 * R + centralPointZ);

        this.vertexBuffer = Util.getFloatBuffer(arrayList);

    }

    private Path getBezierPath(float startX, float startY,
                               float controlPointX, float controlPointY,
                               float endX, float endY) {
        Path path = new Path();
        path.moveTo(startX, startY);
        path.quadTo(controlPointX, controlPointY, endX, endY);

        return path;
    }

    /**
     * 获取曲面顶点集合
     *
     * @param path1     上曲线
     * @param path2     下曲线
     * @param vertexNum 曲线被分割的点数
     * @return
     */
    private ArrayList<Float> getCurvedVertex(Path path1, Path path2, float vertexNum) {

        PathMeasure pathMeasure1 = new PathMeasure(path1, false);
        float len1 = pathMeasure1.getLength();

        PathMeasure pathMeasure2 = new PathMeasure(path2, false);
        float len2 = pathMeasure2.getLength();

        ArrayList<Float> arrayList = new ArrayList<>();
        for (int i = 0; i < vertexNum; i++) {
            // patch1 上的点
            float[] pos = new float[2];
            float[] tan = new float[2];

            float distance = len1 / (vertexNum - 1) * i;
            pathMeasure1.getPosTan(distance, pos, tan);

            /**
             * 根据弧面半径和弧度计算去z坐标值
             */
            float sin = (float) Math.sin(perDegrees * i / 2 * Math.PI / 180);
            float z = sin * 2 * R * sin;

            arrayList.add(pos[0]);
            arrayList.add(pos[1]);
            arrayList.add(z);

            // patch2 上的点
            pos = new float[2];
            tan = new float[2];

            distance = len2 / (vertexNum - 1) * i;
            pathMeasure2.getPosTan(distance, pos, tan);

            /**
             * 根据弧面半径和弧度计算去z坐标值
             */
            sin = (float) Math.sin(perDegrees * i / 2 * Math.PI / 180);
            z = sin * 2 * R * sin;

            arrayList.add(pos[0]);
            arrayList.add(pos[1]);
            arrayList.add(z);
        }

        return arrayList;
    }


    public void draw(GL10 gl) {
        gl.glRotatef(angleX, 1, 0, 0);//旋转
        gl.glRotatef(angleY, 0, 1, 0);
        gl.glRotatef(angleZ, 0, 0, 1);

        // 启用顶点座标数据
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // 启用贴图座标数组数据
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.vertexBuffer);//指定顶点缓冲
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, this.vertexTextureBuffer);// 设置贴图的的座标数据

        // 执行纹理贴图
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 5 + (this.vertexNum * 2) + 1);

        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

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
}
