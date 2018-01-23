package com.example.chaoyi.myapplication.book;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.view.View;
import android.view.ViewGroup;

import com.example.chaoyi.myapplication.R;

/**
 * Created by chaoyi on 2018/1/15.
 */

public class BookView extends View {

    private float Ax, Ay;
    private float Ox, Oy;
    private float Qx, Qy;
    private float Px, Py;
    private float Bx, By;
    private float Fx, Fy;
    private float Gx, Gy;
    private float Mx, My;
    private float Dx, Dy;
    private float Ix, Iy;
    private float GM;
    private float FM;
    private float EM;
    private float Ex;
    private float Ey;
    private float EF;
    private float FH;
    private float Hx;
    private float Hy;
    private float Nx;
    private float Ny;
    private float FN;
    private float FG;
    private float CF;
    private float Cx, Cy;
    private float FJ;
    private float Jx, Jy;
    private float Kx, Ky;
    private float AB;
    private float AK;
    private float OJ;
    private float PC;

    private int width, height;

    private Paint paint;
    private Bitmap bitmap;
    private Path pathA, pathB, pathB1, pathB2;

    private void init() {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        setLayoutParams(layoutParams);

        bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.img);
        paint = new Paint();
        paint.setStrokeWidth(1);
        paint.setColor(Color.argb(50, 255, 100, 100));
    }

    public BookView(Context context, int width, int height) {
        super(context);
        this.width = width;
        this.height = height;
        init();
        this.calculateVertex(width / 2, height / 2);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawA(canvas);
        drawB(canvas);
        drawC(canvas);
    }

    /**
     * 绘制B区域
     */
    private void drawB(Canvas canvas) {
        canvas.save();
        canvas.clipPath(pathB);
        canvas.clipPath(pathB1, Region.Op.INTERSECT);
        canvas.drawColor(Color.BLACK);
        canvas.restore();
    }

    /**
     * 绘制A区域
     */
    private void drawA(Canvas canvas) {
        canvas.save();
        canvas.clipPath(pathA, Region.Op.INTERSECT);
        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(0, 0, getWidth(), getHeight()), null);
        canvas.restore();
    }

    /**
     * 绘制C区域
     */
    private void drawC(Canvas canvas) {

        canvas.save();
        canvas.clipPath(pathB);
        canvas.clipPath(pathB2, Region.Op.INTERSECT);

        canvas.drawColor(Color.WHITE);

        canvas.translate(Ax, Ay); // 相当于把原点移动到A
        canvas.scale(1, -1);
        /**
         * 反三角函数算出来的结果是弧度
         * 角度 = 弧度 * 180 / π
         */
        float degrees = (float) (180 - Math.atan(EF / FH) * 180 / Math.PI * 2);
        canvas.rotate(degrees);
        canvas.translate(-width, -height);

        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(0, 0, width, height), null);

        /**
         * 蒙层
         */
        canvas.drawColor(Color.parseColor("#30000000"));
        canvas.restore();
    }

    /**
     * 计算各点位坐标 & path
     *
     * @return
     */
    private void calculateVertex(float thumbX, float thumbY) {
        Ax = thumbX;
        Ay = thumbY;

        Ox = width;
        Oy = 0;

        Px = 0;
        Py = height;

        Qx = 0;
        Qy = 0;

        Fx = width;
        Fy = height;

        Gx = (Ax + Fx) / 2f;
        Gy = (Ay + Fy) / 2f;

        Mx = Gx;
        My = Fy;

        GM = (float) Math.abs(Math.sqrt(Math.pow(Gx - Mx, 2) + Math.pow(Gy - My, 2)));
        FM = (float) Math.abs(Math.sqrt(Math.pow(Fx - Mx, 2) + Math.pow(Fy - My, 2)));
        EM = GM * GM / FM;

        Ex = Gx - EM;
        Ey = Fy;

        EF = (float) Math.abs(Math.sqrt(Math.pow(Ex - Fx, 2) + Math.pow(Ey - Fy, 2)));
        FH = EF * GM / EM;

        Hx = Fx;
        Hy = Fy - FH;

        Nx = (Ax + Gx) / 2f;
        Ny = (Ay + Gy) / 2f;

        FN = (float) Math.abs(Math.sqrt(Math.pow(Nx - Fx, 2) + Math.pow(Ny - Fy, 2)));
        FG = (float) Math.abs(Math.sqrt(Math.pow(Fx - Gx, 2) + Math.pow(Fy - Gy, 2)));
        CF = FN * EF / FG;
        Cx = Fx - CF;
        Cy = Fy;

        /**
         * C点坐标有可能越界
         */
        if (Cx < Qx) {
            Cx = Qx;
        }

        FJ = FH * CF / EF;
        Jx = Fx;
        Jy = Fy - FJ;
        /**
         * J点坐标有可能越界
         */
        if (Jy < Oy) {
            Jy = Oy;
        }

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
        Bx = (b2 - b1) / (k1 - k2);
        By = k1 * (b2 - b1) / (k1 - k2) + b1;

        /**
         * a-h 线
         */
        k1 = (Hy - Ay) / (Hx - Ax);
        b1 = (Hx * Ay - Hy * Ax) / (Hx - Ax);
        // a-h 和 c-j 的交点K
        Kx = (b2 - b1) / (k1 - k2);
        Ky = k1 * (b2 - b1) / (k1 - k2) + b1;

        /**
         * D点为 CB的中点 与 E 的中点
         */
        Dx = (Cx + 2 * Ex + Bx) / 4;
        Dy = (Cy + 2 * Ey + By) / 4;

        /**
         * I点为 KJ的中点 与 H 的中点
         */
        Ix = (Jx + 2 * Hx + Kx) / 4;
        Iy = (Jy + 2 * Hy + Ky) / 4;

        AB = (float) Math.abs(Math.sqrt(Math.pow(Ax - Bx, 2) + Math.pow(Ay - By, 2)));
        AK = (float) Math.abs(Math.sqrt(Math.pow(Ax - Kx, 2) + Math.pow(Ay - Ky, 2)));
        OJ = (float) Math.abs(Math.sqrt(Math.pow(Ox - Jx, 2) + Math.pow(Oy - Jy, 2)));
        PC = (float) Math.abs(Math.sqrt(Math.pow(Px - Cx, 2) + Math.pow(Py - Cy, 2)));

        pathA = new Path();
        pathA.moveTo(Qx, Qy);
        pathA.lineTo(Ox, Oy);
        pathA.lineTo(Jx, Jy);
        pathA.quadTo(Hx, Hy, Kx, Ky);
        pathA.lineTo(Ax, Ay);
        pathA.lineTo(Bx, By);
        pathA.quadTo(Ex, Ey, Cx, Cy);
        pathA.lineTo(Px, Py);
        pathA.close();

        pathB = new Path();
        pathB.moveTo(Jx, Jy);
        pathB.quadTo(Hx, Hy, Kx, Ky);
        pathB.lineTo(Ax, Ay);
        pathB.lineTo(Bx, By);
        pathB.quadTo(Ex, Ey, Cx, Cy);
        pathB.lineTo(Fx, Fy);
        pathB.close();

        pathB1 = new Path();
        pathB1.moveTo(Cx, Cy);
        pathB1.lineTo(Dx, Dy);
        pathB1.lineTo(Ix, Iy);
        pathB1.lineTo(Jx, Jy);
        pathB1.lineTo(Fx, Fy);
        pathB1.close();

        pathB2 = new Path();
        pathB2.moveTo(Ax, Ay);
        pathB2.lineTo(Bx, By);
        pathB2.lineTo(Dx, Dy);
        pathB2.lineTo(Ix, Iy);
        pathB2.lineTo(Kx, Ky);
        pathB2.close();

    }

}
