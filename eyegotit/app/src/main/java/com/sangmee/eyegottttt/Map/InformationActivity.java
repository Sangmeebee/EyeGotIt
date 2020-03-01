package com.sangmee.eyegottttt.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sangmee.eyegottttt.R;

public class InformationActivity extends AppCompatActivity {
    private String user_id;
    private Intent intent;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private String getUser_id;

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

        Query recentPostsQuery = databaseReference.child(user_id).child("user_id");
        recentPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getUser_id= dataSnapshot.getValue().toString();
                TextView getTextView=findViewById(R.id.userid_textview4);
                getTextView.setText(getUser_id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
