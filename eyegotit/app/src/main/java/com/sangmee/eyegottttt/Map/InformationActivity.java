package com.sangmee.eyegottttt.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.sangmee.eyegottttt.R;

public class InformationActivity extends AppCompatActivity {
    private String user_id;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.noactionbar);
        setContentView(R.layout.activity_info);

        intent= getIntent();
        user_id = intent.getStringExtra("id");

        TextView textView = findViewById(R.id.textView);
        textView.setText(user_id+"님, 안녕하세요.");
        TextView userid_TextView = findViewById(R.id.userid_textview);
        userid_TextView.setText(user_id);

    }
}
