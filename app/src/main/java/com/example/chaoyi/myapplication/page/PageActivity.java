package com.example.chaoyi.myapplication.page;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PageActivity extends AppCompatActivity {

    private PageView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new PageView(this);

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
