package com.example.sb_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        start();
        setContentView(R.layout.activity_main);
    }

    private void start() {
        FrameLayout myButton_picture = (FrameLayout) findViewById(R.id.gildong_jpg);
        Button myButton = (Button) findViewById(R.id.gildong_button);
        TextView myView = (TextView) findViewById(R.id.info_context);

        myButton_picture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myView.setText("프레임워크 클릭했습니다");
            }
        });
        myButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myView.setText("버튼을 클릭했습니다");
            }
        });



    }

}