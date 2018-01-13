package com.example.chaoyi.myapplication.utility;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaoyi on 2018/1/13.
 */

public class Util {
    public static FloatBuffer getFloatBuffer(ArrayList<Float> arrayList){
        int arraySize = arrayList.size();

        //顶点
        float[] vertexs = new float[arraySize];
        for (int i = 0; i < arraySize; i++) {
            vertexs[i] = arrayList.get(i);
        }

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(arraySize * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertexs);
        vertexBuffer.position(0);

        return vertexBuffer;
    }
}
