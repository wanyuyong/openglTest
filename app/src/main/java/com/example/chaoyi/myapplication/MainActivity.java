package com.example.chaoyi.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.chaoyi.myapplication.cylinder.CylinderActivity;
import com.example.chaoyi.myapplication.page.PageActivity;
import com.example.chaoyi.myapplication.pagetexture.PageTextureActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.cylinder_btn).setOnClickListener(this);
        findViewById(R.id.page_btn_1).setOnClickListener(this);
        findViewById(R.id.page_btn_2).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cylinder_btn:
                startActivity(new Intent(getApplicationContext(), CylinderActivity.class));
                break;
            case R.id.page_btn_1:
                startActivity(new Intent(getApplicationContext(), PageActivity.class));
                break;
            case R.id.page_btn_2:
                startActivity(new Intent(getApplicationContext(), PageTextureActivity.class));
                break;
        }
    }
}
