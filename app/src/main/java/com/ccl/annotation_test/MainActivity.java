package com.ccl.annotation_test;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.ccl.bind_annotation.BindView;
import com.ccl.bind_lib.BindTools;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.tv_1)
    TextView tv1;
    @BindView(R.id.btn_1)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BindTools.inject(this);
        tv1.setText("11123");
        button.setText("123123");
    }
}
