package com.example.chaoyi.myapplication.pagetexture;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class PageTextureActivity extends AppCompatActivity {

    private PageTextureView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new PageTextureView(this);

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
