package com.example.chaoyi.myapplication.cylinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CylinderActivity extends AppCompatActivity {

    private CylinderView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new CylinderView(this);

        setContentView(mGLSurfaceView);

        mGLSurfaceView.setFocusableInTouchMode(true);
        mGLSurfaceView.requestFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }
}
