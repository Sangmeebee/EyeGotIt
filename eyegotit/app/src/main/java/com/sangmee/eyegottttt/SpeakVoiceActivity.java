package com.sangmee.eyegottttt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.ArrayList;

public class SpeakVoiceActivity {
    public TextToSpeech tts;
    public String text;
    private Context context;
    private String locationText;
    private SpeechRecognizer mRecognizer;
    Intent intent;


    //음성인식을 위한 메소드
    public SpeakVoiceActivity(Context context, TextToSpeech tts) {
        this.context = context;
        this.tts = tts;

    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) { //음성인식 시작하는것을 알림.
            Toast.makeText(context, "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {
        } //말하기 시작

        @Override
        public void onRmsChanged(float rmsdB) {
        } //입력받는 소리의 크기를 알려줌.

        @Override
        public void onBufferReceived(byte[] buffer) {
        } //사용자가 말한 단어들을 buffer에 담음.

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

            Toast.makeText(context, "에러가 발생하였습니다. 다시 눌러주세요.", Toast.LENGTH_LONG).show();
            speekTTS("에러가 발생하였습니다. 다시 눌러주세요.", tts);
        }

        //음성인식 결과 출력
        @Override
        public void onResults(Bundle results) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for (int i = 0; i < matches.size(); i++) {
                locationText = matches.get(i);
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    };

    //TTS
    public void speekTTS(String text, TextToSpeech tts) {

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        tts.setSpeechRate(1);
    }

}