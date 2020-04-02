package com.sate7.wlj.developerreader.sate7gems;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import org.w3c.dom.Text;

import java.util.Random;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        String[] data = new String[]{
                "45633214586321522321",
                "45633214586321522321\n北京测试",
                "王丽军华为",
                "45633214586321522321\n上海",
                "45633214586321522321",
                "45633214586321522321\n北京测试",
                "王丽军华为",
                "45633214586321522321\n上海"
        };
        FlexboxLayout flexboxLayout = findViewById(R.id.test_flex);
        for (int i = 0; i < 8; i++) {
            TextView textView = new TextView(this);
            textView.setText(data[i]);
            textView.setBackgroundColor(getResources().getColor(R.color.colorBlue1));
            flexboxLayout.addView(textView);
        }

        flexboxLayout.setMaxLine(6);

    }
}
