package com.example.SpeedometerView;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.accelerate_button);
        final SpeedometerView speedometerView = findViewById(R.id.speedometer_view);
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    speedometerView.accelerate();
                }
                else if(event.getAction() == MotionEvent.ACTION_UP) {
                    speedometerView.decelerate();
                }
                return true;
            }
        });
    }
}
