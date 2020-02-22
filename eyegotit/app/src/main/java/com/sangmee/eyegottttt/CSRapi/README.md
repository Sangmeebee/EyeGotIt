# Naver Clova Speech Recognition(CSR) 어플리케이션에 적용시키기

##  Android API 사용하기    
  
### 1. 다음 구문을  app/build.gradle  파일에 추가한다.
~~~
repositories {
    jcenter()
}
dependencies {
    compile 'com.naver.speech.clientapi:naverspeech-ncp-sdk-android:1.1.6'
}
~~~

### 2. Android Manifest 파일을 다음과 같이 설정한다.
    - 사용자의 음성 입력을 마이크를 통해 녹음해야 하고 녹음된 데이터를 서버로 전송해야 한다. 
    - 따라서,  android.permission.INTERNET 와  android.permission.RECORD_AUDIO 에 대한 권한이 반드시 필요하다. 
~~~
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
          package="com.naver.naverspeech.client"
          android:versionCode="1" android:versionName="1.0" >
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
~~~

### 3. 허용 버전 문제
    - 네이버 Open API는 Android SDK 버전 10 이상을 지원
    - 따라서, build.gradle 파일의  minSdkVersion  값을 이에 맞게 설정해야 한다. 

### 4. AudioWriterPCM.java , CsrProc.java, DatabaseActivity.java
     - DatabaseActivity : SpeechRecognitionListener를 초기화하고, 이후 이벤트를 handleMessage에서 받아 처리한다. 
     - CsrProc : 음성인식 서버 연결, 음성전달, 인식결과 발생등의 이벤트에 따른 결과 처리 방법 정의한다. 
     - AudioWriterPCM : 음성인식 데이터 open, write, close 

### 5. DatabaseActivity
-변수설정
    - ★★★ : "내 애플리케이션"에서 Client ID를 확인하여 작성한다. 
    - Client ID는 CsrProc 객체의 인수로 사용되어 내 어플리케이션을 서버와 연결할 수 있게 설정한다. 
~~~
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CLIENT_ID = "★★★"; 
    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private TextView txtResult;
    private Button btnStart;
    private String mResult;
    private AudioWriterPCM writer;
~~~

-handleMessage 메소드 : Handle speech recognition Messages
~~~
private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady: // 음성인식 준비 가능
                txtResult.setText("Connected");
                writer = new AudioWriterPCM(Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;
            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;
            case R.id.partialResult:
                mResult = (String) (msg.obj);
                txtResult.setText(mResult);
                break;
            case R.id.finalResult: // 최종 인식 결과
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
                StringBuilder strBuf = new StringBuilder();
                for(String result : results) {
                    strBuf.append(result);
                    strBuf.append("\n");
                }
                mResult = strBuf.toString();
                txtResult.setText(mResult);
                break;
            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }
                mResult = "Error code : " + msg.obj.toString();
                txtResult.setText(mResult);
                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                break;
            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }
                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                break;
        }
    }
~~~

-음성 인식 버튼 작용시키기
~~~
@Override
        public void onClick(View v) {
            if (!naverRecognizer.getSpeechRecognizer().isRunning()) {
                txtResult.setText("Connecting...");
                naverRecognizer.recognize(); 
            } else {
                Log.d(TAG, "stop and wait Final Result");
                naverRecognizer.getSpeechRecognizer().stop();
            }
        }
~~~

-Handler를 상속받는 RecognitionHandler 클래스 작성하기
~~~
static class RecognitionHandler extends Handler {
        private final WeakReference<DatabaseActivity> mActivity;

        RecognitionHandler(DatabaseActivity activity) {
            mActivity = new WeakReference<DatabaseActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            DatabaseActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg); //앞에서 만든 handleMessage와 상호작용되는 부분
            }
        }
    }
~~~



