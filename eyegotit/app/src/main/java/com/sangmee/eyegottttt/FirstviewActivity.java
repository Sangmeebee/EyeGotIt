package com.sangmee.eyegottttt;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sangmee.eyegottttt.Login.LoginActivity;
import com.sangmee.eyegottttt.Map.MainActivity;

import java.util.Locale;

public class FirstviewActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    final int PERMISSION = 1;
    TextToSpeech tts;
    SpeakVoiceActivity voiceActivity;
    ReplyVoiceActivity replyVoiceActivity;
    Intent intentId;
    TextView textview1, textview2, textview3, textview4, textview_logout, textview_logout_btn;
    RelativeLayout relativeLayout;
    LinearLayout linearLayout;
    final int MOVE_HAND=350;//얼마나 밀었을때
    float sx,sy,ssx,ssy;//시작지점



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // style 다른거 쓸라면 이렇게 해야됨.
        //setTheme(R.style.nomenubar);
        setTheme(R.style.noactionbar);

        setContentView(R.layout.activity_firstview);

        linearLayout = findViewById(R.id.layoutlayout);
        relativeLayout=findViewById(R.id.relativelayouts);
        textview1 = findViewById(R.id.textView1);
        textview2 = findViewById(R.id.textView2);
        textview3 = findViewById(R.id.textView3);
        textview4 = findViewById(R.id.textView4);
        textview_logout=findViewById(R.id.txtLogoutInfo);
        textview_logout_btn=findViewById(R.id.txtLogout);
        final ImageView iv = (ImageView)findViewById(R.id.imageView);
        iv.setVisibility(View.INVISIBLE);

        Handler delayHandler = new Handler();

        // textview.animate().getDuration()

        intentId=getIntent();
        Log.d("sangminIntent", intentId.getStringExtra("id"));

        textview1.setText("안녕하세요");
        textview2.setText("eye got it 입니다.\n어떤 기능을 사용할건가요?\n");
        textview3.setText("1. 등록해둔 경로\n2. 새로운 경로\n");
        textview4.setText("(1번 : 오른쪽 드래그)\n(2번 : 왼쪽 드래그)");
        //로그아웃 텍스트뷰 버튼 클릭시 로그인화면으로 돌아가며 로그아웃
        textview_logout.setText(intentId.getStringExtra("id")+"님이 아니십니까?");
        textview_logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               confirm_dialog();
            }
        });
        textview1.animate().alpha(1f).setDuration(2000);


// 딜레이 거는 방법 밑에 있는 숫자로 조정 가능
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 이 부분이 alpha0으로 둔것을 천천히 나타나게 하는 부분
                textview2.animate().alpha(1f).setDuration(2000);
            }
        },900);

        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                textview3.animate().alpha(1f).setDuration(2000);
            }
        },3000);

        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                textview4.animate().alpha(1f).setDuration(2000);
            }
        },6000);




        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_DOWN){
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
                }
                else if(e.getAction() == MotionEvent.ACTION_UP){
                    float diffxx = sx-e.getRawX();
                    float diffyy = sy - e.getRawY();
                    iv.setVisibility(View.INVISIBLE);
                    if(Math.abs(diffxx)>Math.abs(diffyy)){
                        if(diffxx>MOVE_HAND) {
                            Intent intent=new Intent(FirstviewActivity.this, MainActivity.class);
                            intent.putExtra("id", intentId.getStringExtra("id"));
                            startActivity(intent);
                            tts.stop();

                        }
                        else if (diffxx<-MOVE_HAND) {
                            Intent intent=new Intent(FirstviewActivity.this, DatabaseActivity.class);
                            intent.putExtra("id", intentId.getStringExtra("id"));
                            startActivity(intent);
                            tts.stop();
                        }
                    }
                    else {
                        if (diffyy > MOVE_HAND){
                            //"아래에서 위로"
                        }
                        else if (diffyy < -MOVE_HAND){
                            //"위에서 아래로"
                        }
                    }
                }
                return true;
            }
        });


        if (Build.VERSION.SDK_INT >= 23) {
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }
        tts = new TextToSpeech(this, this);
        voiceActivity = new SpeakVoiceActivity(FirstviewActivity.this, tts);

        voiceActivity.text = "안녕하세요 eye got it 입니다.... 어떤 기능을 사용할건가요?1번 등록해둔 경로..  2번 새로운 경로...  1번 선택 시 오른쪽 드래그... 2번 선택 시 왼쪽 드래그..를 하세요";
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

    View.OnClickListener voicereplyListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            replyVoiceActivity.receiver();
        }
    };

    View.OnClickListener fordisablemanListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(FirstviewActivity.this, DatabaseActivity.class);
            intent.putExtra("id", intentId.getStringExtra("id"));
            startActivity(intent);
            tts.stop();
        }
    };
    View.OnClickListener forablemanListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(FirstviewActivity.this, MainActivity.class);
            intent.putExtra("id", intentId.getStringExtra("id"));
            startActivity(intent);
            tts.stop();
        }
    };

    @Override
    protected void onStop() {
        tts.stop();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        voiceActivity.speekTTS(voiceActivity.text,tts);
        super.onRestart();
    }

    void confirm_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyAlertDialogStyle);
        //tilte 부분 xml

        /*TextView title = new TextView(this);
        title.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        title.setPadding(40,30,0,30);
        title.setLayoutParams(lp);
        title.setText("알림");
        title.setGravity(Gravity.LEFT);
        title.setBackgroundColor(Color.rgb(139,195,74));
        builder.setCustomTitle(title);*/
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.normal_dialog, null);
        final TextView location_edit=view.findViewById(R.id.delete_text);
        location_edit.setTextColor(Color.GRAY);
        location_edit.setText("로그아웃 하시겠습니까?");
        builder.setView(view);

        //확인버튼
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);

                //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("us_id", ""); // key, value를 이용하여 저장하는 형태
                editor.putString("us_pw", "");
                //최종 커밋
                editor.commit();
                ActivityCompat.finishAffinity(FirstviewActivity.this);
                Intent intent=new Intent(FirstviewActivity.this, LoginActivity.class);

                startActivity(intent);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    void exit_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyAlertDialogStyle);
        //tilte 부분 xml

       /* TextView title = new TextView(this);
        title.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        title.setPadding(40,30,0,30);
        title.setLayoutParams(lp);
        title.setText("알림");
        title.setGravity(Gravity.LEFT);
        title.setBackgroundColor(Color.rgb(139,195,74));
        builder.setCustomTitle(title);*/
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.normal_dialog, null);
        final TextView location_edit=view.findViewById(R.id.delete_text);
        location_edit.setTextColor(Color.GRAY);
        location_edit.setText("Eye got it을 종료하시겠습니까?");
        builder.setView(view);

        //확인버튼
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }
    //뒤로가기 버튼 클릭시
    @Override
    public void onBackPressed() {

        exit_dialog();
    }



}