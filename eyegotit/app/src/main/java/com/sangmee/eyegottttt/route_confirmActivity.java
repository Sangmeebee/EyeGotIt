package com.sangmee.eyegottttt;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sangmee.eyegottttt.Map.MapActivity;

import java.util.Calendar;
import java.util.Locale;

public class route_confirmActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    final int PERMISSION = 1;
    Intent intent;
    String s_location;
    LinearLayout linearLayout;
    TextView textview1, textview2;
    String user_id;
    final int MOVE_HAND=350;//얼마나 밀었을때
    float sx,sy,ssx,ssy;//시작지점
    boolean checking=true;

    TextToSpeech tts;
    SpeakVoiceActivity voiceActivity;
    ReplyVoiceActivity replyVoiceActivity;

    final Calendar alarmCalendar = Calendar.getInstance();
    long alarmTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // style 다른거 쓸라면 이렇게 해야됨.
        setTheme(R.style.noactionbar);
        // 핸들러
        Handler delayHandler = new Handler();
        Handler delayHandler2=new Handler();



        setContentView(R.layout.activity_route_confirm);
        intent = getIntent();
        s_location = intent.getStringExtra("s_location");
        user_id = intent.getStringExtra("id");

        if (Build.VERSION.SDK_INT >= 23) {
            // 퍼미션 체크
            ActivityCompat.requestPermissions(route_confirmActivity.this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }

        linearLayout = findViewById(R.id.layoutlayout);
        textview1 = findViewById(R.id.textView1);
        textview2 = findViewById(R.id.textView2);
        final ImageView iv = (ImageView)findViewById(R.id.imageView);
        iv.setVisibility(View.INVISIBLE);

        textview1.setText(s_location + " 경로를 안내 받으시겠습니까?\n\n");
        textview2.setText("1. 예\n2. 아니오\n\n (1번 : 오른쪽 드래그)\n(2번 : 왼쪽 드래그)");

        tts = new TextToSpeech(route_confirmActivity.this, route_confirmActivity.this);
        voiceActivity = new SpeakVoiceActivity(route_confirmActivity.this, tts);

        // 딜레이 거는 방법 밑에 있는 숫자로 조정 가능
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 이 부분이 alpha0으로 둔것을 천천히 나타나게 하는 부분
                if(checking==true) {
                    textview1.animate().alpha(1f).setDuration(2000);

                    voiceActivity.text = s_location + "경로를 안내 받으시겠습니까?";
                    voiceActivity.speekTTS(voiceActivity.text, tts);
                }

            }
        }, 0);

        delayHandler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 이 부분이 alpha0으로 둔것을 천천히 나타나게 하는 부분
                textview2.animate().alpha(1f).setDuration(2000);
                if(checking==true) {
                    voiceActivity.text = "일번 네 ";
                    voiceActivity.speekTTS(voiceActivity.text, tts);
                }

            }
        }, 4500);

        delayHandler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 이 부분이 alpha0으로 둔것을 천천히 나타나게 하는 부분
                if(checking==true) {
                    voiceActivity.text = " 이번 아니오  일번 선택 시 오른쪽 드래그... 이번 선택 시 왼쪽 드래그..를 하세요";
                    voiceActivity.speekTTS(voiceActivity.text, tts);
                }

            }
        }, 5300);




        linearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    sx = e.getRawX();
                    sy = e.getRawY();
                    iv.setX(sx);
                    iv.setY(sy);
                    iv.setVisibility(View.VISIBLE);
                }
                if(e.getAction() == MotionEvent.ACTION_MOVE){
                    ssx = e.getRawX();
                    ssy = e.getRawY();
                    iv.setX(ssx);
                    iv.setY(ssy);
                } else if (e.getAction() == MotionEvent.ACTION_UP) {
                    float diffxx = sx - e.getRawX();
                    float diffyy = sy - e.getRawY();
                    iv.setVisibility(View.INVISIBLE);
                    if (Math.abs(diffxx) > Math.abs(diffyy)) {
                        if (diffxx > MOVE_HAND) {
                            onBackPressed();
                            checking=false;
                            tts.stop();

                        } else if (diffxx < -MOVE_HAND) {
                            Intent intent = new Intent(route_confirmActivity.this, MapActivity.class);
                            intent.putExtra("s_location", s_location);
                            intent.putExtra("id", user_id);
                            startActivity(intent);
                            finish();

                            checking=false;
                            tts.stop();
                        }
                    } else {
                        if (diffyy > MOVE_HAND) {
                            //"아래에서 위로"
                        } else if (diffyy < -MOVE_HAND) {
                            //"위에서 아래로"
                        }
                    }
                }
                return true;
            }
        });



    }

    @Override
    protected void onStop() {
        tts.stop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        //TTS 멈추기
        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {//TTS 보내기 위한 함수
        if(status==TextToSpeech.SUCCESS){
            int result=tts.setLanguage(Locale.KOREA);
            if(result==TextToSpeech.LANG_MISSING_DATA){
                Log.d("hyori","no tts data");
            }
            else if(result==TextToSpeech.LANG_NOT_SUPPORTED){
                Log.d("hyori","language wrong");
            }
            else{
                //mRecognizer.stopListening();
                voiceActivity.speekTTS(voiceActivity.text,tts);
            }
        }
        else{
            Log.d("hyori","failed");
        }

    }

}
