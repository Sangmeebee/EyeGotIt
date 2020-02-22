# Map 액티비티 소개

##  MapActivity.java    
  
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
