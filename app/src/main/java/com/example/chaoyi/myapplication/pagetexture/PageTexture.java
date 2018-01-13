package com.example.chaoyi.myapplication.pagetexture;

import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.Log;

import com.example.chaoyi.myapplication.page.Page;
import com.example.chaoyi.myapplication.utility.Util;

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

    private float perDegrees;  //曲面被切割的每一小份的角度
    private int vertexNum; //曲面切割的点个数

    private Context context;

    public PageTexture(float width, float height, float perDegrees, Context context) {
        this.width = width;
        this.height = height;
        this.context = context;
        this.perDegrees = perDegrees;
        this.vertexNum = (int) (180 / perDegrees) + 1;

        this.calculateVertex();
    }

    private void calculateVertex() {
        calculatePageVertex(0, 0);
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
                               float endX, float endY){
        Path path = new Path();
        path.moveTo(startX, startY);
        path.quadTo(controlPointX, controlPointY, endX, endY);

        return path;
    }

    /**
     * 获取曲面顶点集合
     * @param path1  上曲线
     * @param path2  下曲线
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

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, this.vertexBuffer);//指定顶点缓冲
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 5 + this.vertexNum * 2 + 1);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

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
