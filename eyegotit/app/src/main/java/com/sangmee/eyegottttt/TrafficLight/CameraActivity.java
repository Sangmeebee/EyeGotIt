/* Copyright 2017 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.sangmee.eyegottttt.TrafficLight;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.sangmee.eyegottttt.R;
import com.sangmee.eyegottttt.SpeakVoiceActivity;

import java.util.Locale;

/** Main {@code Activity} class for the Camera app. */
public class CameraActivity extends Activity implements TextToSpeech.OnInitListener{
    FrameLayout container;
    final int MOVE_HAND=350;//얼마나 밀었을때
    float sx,sy,ssx,ssy;//시작지점
    SpeakVoiceActivity voiceActivity;
    TextToSpeech tts;
    final int PERMISSION=1;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        container = findViewById(R.id.container);
        tts=new TextToSpeech(CameraActivity.this,CameraActivity.this);
        voiceActivity=new SpeakVoiceActivity(CameraActivity.this,tts);

        Handler delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if ( Build.VERSION.SDK_INT >= 23 ) {
                    // 퍼미션 체크
                    ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.INTERNET,
                            Manifest.permission.RECORD_AUDIO}, PERMISSION);
                }
                if (null == savedInstanceState) {
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, Camera2BasicFragment.newInstance(tts,voiceActivity))
                            .commit();
                }

                container.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent e) {
                        if(e.getAction() == MotionEvent.ACTION_DOWN){
                            sx = e.getRawX();
                            sy = e.getRawY();
                        }
                        if(e.getAction() == MotionEvent.ACTION_MOVE){
                            ssx = e.getRawX();
                            ssy = e.getRawY();
                        }
                        else if(e.getAction() == MotionEvent.ACTION_UP){
                            float diffxx = sx-e.getRawX();
                            float diffyy = sy - e.getRawY();
                            if(Math.abs(diffxx)>Math.abs(diffyy)){
                                if(diffxx>MOVE_HAND) {
                                    //왼쪽
                                }
                                else if (diffxx<-MOVE_HAND) {
                                    //오른쪽
                                }
                            }
                            else {
                                if (diffyy > MOVE_HAND){
                                    //"아래에서 위로"
                                }
                                else if (diffyy < -MOVE_HAND){
                                    //"위에서 아래로"
                                    onBackPressed();
                                }
                            }
                        }
                        return true;
                    }
                });
            }
        },1200);

    }

}