package com.sangmee.eyegottttt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sangmee.eyegottttt.CSSapi.APIExamTTS;
import com.sangmee.eyegottttt.Login.LoginActivity;
import com.sangmee.eyegottttt.Map.ProtecterMapActivity;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {
    Intent intent;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();;

    ArrayList<String> login_id_list=new ArrayList<>();
    ArrayList<String> login_pw_list=new ArrayList<>();
    String us_id;
    String us_pw;
    int okay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // style 다른거 쓸라면 이렇게 해야됨.
        setTheme(R.style.nomenubar);
        setContentView(R.layout.activity_splash);

        //자동로그인/////////////////////////////
        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        //us_id/us_pw라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        us_id = sf.getString("us_id","");
        us_pw = sf.getString("us_pw","");
        okay=0;

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                login_id_list.clear();
                login_pw_list.clear();

                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String userid=messageData.getKey();
                    Log.d("okay", userid);
                    login_id_list.add(userid);
                }

                for(int i=0; i<login_id_list.size(); i++) {
                    if (login_id_list.get(i).equals(us_id)) {
                        okay=1;
                        Query recentPostsQuery = databaseReference.child(us_id).child("signup_u_pw");
                        recentPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String pw = dataSnapshot.getValue().toString();
                                Log.d("sgsg", pw);
                                if(pw.equals(us_pw)){
                                    Query rPostsQuery = databaseReference.child(us_id).child("who");
                                    rPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String who = dataSnapshot.getValue().toString();
                                            Log.d("sgsg", who);
                                            if(who.equals("사용자")){
                                                intent=new Intent(SplashActivity.this, FirstviewActivity.class);
                                                intent.putExtra("id", us_id);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else{

                                                intent=new Intent(SplashActivity.this, ProtecterMapActivity.class);
                                                intent.putExtra("id", us_id);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        break;
                    }
                }
                if(okay==0){
                    Handler handler=new Handler(){
                        public void handleMessage(Message msg){
                            super.handleMessage(msg);
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                            finish();
                        }
                    };
                    handler.sendEmptyMessageDelayed(0, 3000);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


        ///////////////////////////자동로그인

    }

}
