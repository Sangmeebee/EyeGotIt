package com.sangmee.eyegottttt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.ArrayList;

public class ReplyVoiceActivity {
    SpeechRecognizer mRecognizer;
    Context context;
    String toastText;
    Intent intent;
    String text;
    TextToSpeech tts;
    SpeakVoiceActivity voiceActivity;
    String replyText;
    int flag=0;
    ImageButton button;
    Button disablebutton;
    Button ablebutton;
    ArrayAdapter<String> arrayAdapter;
    ListView listView;
    boolean checking=false;

    public ReplyVoiceActivity(Context context, TextToSpeech tts, String replyText, Button disablebutton, Button ablebutton,
                              ArrayAdapter<String> arrayAdapter, ListView listView){
        voiceActivity=new SpeakVoiceActivity(context,tts);
        this.tts=tts;
        this.context=context;
        this.replyText=replyText;
        this.disablebutton=disablebutton;
        this.ablebutton=ablebutton;
        this.arrayAdapter=arrayAdapter;
        this.listView=listView;

        //사용자에게 음성을 요구하고 음성 인식기를 통해 전송하는 활동을 시작함.
        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //음성인식을 위한 음성 인식기의 의도에 사용되는 여분의 키 설정
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,context.getPackageName());
        //음성을 번역할 언어 설정.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        //receiver();
    }
    public String receiver(){
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(context);  // 새 SpeechRecognizer를 만드는 팩토리 메서드.
        mRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) { //음성인식 시작하는것을 알림.
                Toast.makeText(context,"음성인식을 시작합니다.",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBeginningOfSpeech() {} //말하기 시작

            @Override
            public void onRmsChanged(float rmsdB) {} //입력받는 소리의 크기를 알려줌.

            @Override
            public void onBufferReceived(byte[] buffer) {} //사용자가 말한 단어들을 buffer에 담음.

            @Override
            public void onEndOfSpeech() {
            } //말하기 중지

            //음성인식 에러처리 부분
            @Override
            public void onError(int error) {
                String message;
                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = "오디오 에러";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        message = "클라이언트 에러";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        message = "퍼미션 없음";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        message = "네트워크 에러";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        message = "네트웍 타임아웃";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "찾을 수 없음";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "RECOGNIZER가 바쁨";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        message = "서버가 이상함";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "말하는 시간초과";
                        break;
                    default:
                        message = "알 수 없는 오류임";
                        break;
                }

                Toast.makeText(context, "에러가 발생하였습니다. 다시 눌러주세요.",Toast.LENGTH_LONG).show();
                voiceActivity.speekTTS("에러가 발생하였습니다. 다시 눌러주세요.",tts);
            }

            //음성인식 결과 출력
            @Override
            public void onResults(Bundle results) {
                // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
                ArrayList<String> matches =
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                for(int i = 0; i < matches.size() ; i++){
                    toastText=matches.get(i);
                }
                //Toast.makeText(context,toastText,Toast.LENGTH_LONG).show();

                if(context.getClass().equals(DatabaseActivity.class)){
                    datapathreplyAnswer(toastText);
                }
                /*else if(context.getClass().equals(FirstviewActivity.class)){
                    replyAnswer(toastText);
                }*/
                /*else if(checking==true){//경로 다시 한번 물어보는 경우
                    yesornoAnswer(toastText);
                }*/
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });

        mRecognizer.startListening(intent);
        return toastText;
    }

    private void datapathreplyAnswer(String input){
        if(input.equals("1번")||input.equals("일번")||input.equals("일본")||input.equals("1 번")) {
            Log.i("hyori","됫어");
            listView.performItemClick(listView,0,1);
        }
        else if(input.equals("2 번")||input.equals("입원")||input.equals("이번")){
            listView.performItemClick(listView,1,1);
        }
        else if(input.equals("산문")||input.equals("3 번")||input.equals("산본")||input.equals("삼번")){
            listView.performItemClick(listView,2,1);
        }
        else if(input.equals("사번")){
            listView.performItemClick(listView,3,1);
        }
        else if(input.equals("5번")||input.equals("오번")){
            listView.performItemClick(listView,4,1);
        }
        else{
            text="다시 말씀해주세요.";
            voiceActivity.speekTTS(text,tts);
        }

    }

    /*/////경로 찾는 함수
    public void replyAnswer(String input){
        //일단 노가다를 이렇게 해놓음... 넘어가 지겠금만
        if(input.equals("한아")||input.equals("하나 더")||input.equals("하남")||input.equals("화나")){//2번 화면
            text="등록해둔 경로화면으로 넘어가겠습니다.";
            voiceActivity.speekTTS(text,tts);
            this.flag=1;
            int noOfSecond = 1;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    //TODO Set your button auto perform click.
                    disablebutton.performClick();

                }
            }, noOfSecond * 1000);

        }
        else if(input.equals("둘")){//1번 화면
            text="새로운 경로설정을 시작하겠습니다.";
            voiceActivity.speekTTS(text,tts);
            this.flag=2;
            int noOfSecond = 1;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    //TODO Set your button auto perform click.
                    ablebutton.performClick();
                }
            }, noOfSecond * 1000);
        }
        else{//하나, 둘 어느경우도 아닌 경우에
            text="다시 말씀해주세요";
            voiceActivity.speekTTS(text,tts);
        }
    }*/

    /*private void yesornoAnswer(String input){
        if(input.equals("네")){
            Log.i("hyori","확인");
        }
        else if(input.equals("아니오")){
            text="어떤 경로를 선택 하시겠습니까?";
            voiceActivity.speekTTS(text,tts);
        }
        else{
            text="예 아니오 중에서 다시 말씀해주세요";
            voiceActivity.speekTTS(text,tts);
        }
    }*/

}