package com.sangmee.eyegottttt.Map;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Align;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.LocationButtonView;
import com.sangmee.eyegottttt.FirstviewActivity;
import com.sangmee.eyegottttt.Login.Signup;
import com.sangmee.eyegottttt.R;
import com.sangmee.eyegottttt.SpeakVoiceActivity;
import com.sangmee.eyegottttt.SplashActivity;
import com.sangmee.eyegottttt.TrafficLight.CameraActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

//카카오 import
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.LocationTemplate;
import com.kakao.message.template.TextTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, TextToSpeech.OnInitListener, SensorEventListener {

    public static final String CHANNEL_ID = "notificationChannel";
    private DatabaseReference databaseReference;
    Intent intent;
    Intent intentId;
    String s_location, user_id;
    ArrayList<Double> longitude_list = new ArrayList<>();
    ArrayList<Double> latitude_list = new ArrayList<>();
    ArrayList<LatLng> latlng_list = new ArrayList<>();
    String message[] = new String[20];
    boolean location_changed = false;

    Handler delayHandler = new Handler();

    Location[] saved_location;

    int[] intArray = {100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
    int confirm_num = 0;
    int confirm = 0;

    String spot_str = "p";
    String message_str = "p";
    int index = 1000;
    int i_index;
    ArrayList<Integer> indexList = new ArrayList<>();

    //지도 변수
    // NaverMap API 3.0
    private MapView mapView;
    private LocationButtonView locationButtonView;

    double longitude = 0;
    double latitude = 0;

    NaverMap naverMap;
    ArrayList<Marker> arrayList1 = new ArrayList<>();

    Marker marker_location = new Marker();
    // FusedLocationSource (Google)
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    ProtecterMapActivity2 protecterMapActivity2;

    //mqtt 변수
    private ImageButton alert_btn;
    String topicStr = "사용자의 현재위치입니다.";
    String topicStr2 = "길을 잃었어요!!!";
    String topicStr3="사용자의 핸드폰이 심각히 흔들렸습니다.";
    String topic_value;
    MqttAndroidClient client;
    double d_longi;
    double d_lati;
    Context context = this;

    SpeakVoiceActivity voiceActivity;
    TextToSpeech tts;


    SharedPreferences.Editor editor;
    String lati;
    String longi;
    Marker marker = new Marker();

    //Shake 감지
    private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;
    private float x, y, z;

    private static final int SHAKE_THRESHOLD = 1500;
    private static final int DATA_X = 0;
    private static final int DATA_Y = 1;
    private static final int DATA_Z = 2;

    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;

    boolean checking_shake=false;

    SharedPreferences.OnSharedPreferenceChangeListener mPrefChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        }
    };

    //카카오 변수
    //새로운 버전
    private Button kakao_share;
    private Button kakao_friends;
    private Button kakao_share_map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int MOVE_HAND = 350;//얼마나 밀었을때
        final float[] sx = new float[1]; //시작지점
        final float[] sy = new float[1];
        final float[] ssx = new float[1];
        final float[] ssy = new float[1];


        setTheme(R.style.noactionbar);
        setContentView(R.layout.activity_map);

        //Shake 감지
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        intent = getIntent();
        intentId = getIntent();
        s_location = intent.getStringExtra("s_location");
        user_id = intent.getStringExtra("id");


        //맵 변수
        mapView = findViewById(R.id.main_map_view);
        mapView.onCreate(savedInstanceState);
        naverMapBasicSettings();
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            return;
        }

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                100, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                100, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
        // Toast.makeText(practice.this,(int)longtitude,Toast.LENGTH_SHORT).show();

        //  lm.removeUpdates(mLocationListener);
        //여기까지

        tts = new TextToSpeech(MapActivity.this, MapActivity.this);
        voiceActivity = new SpeakVoiceActivity(MapActivity.this, tts);

        //mqtt 코드!!!!!!!!!!!!!!!!!!!!!!

        databaseReference = FirebaseDatabase.getInstance().getReference();
        Query recentPostsQuery = databaseReference.child(user_id).child("topic");
        recentPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                topic_value = dataSnapshot.getValue().toString();
                Log.d("sangminTopic", topic_value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(MapActivity.this, "tcp://broker.hivemq.com:1883", clientId);

        alert_btn = (ImageButton) findViewById(R.id.alert_btn);
        //connect하는 부분
        try {
            IMqttToken token = client.connect(getMqttConnectionOption());
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) { //연결에 성공한 경우
                    Log.v("SEYUN_TAG", "connection1");
                    try {
                        client.subscribe(topic_value, 0); //topic값 받음.
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) { //연결에 실패한 경우
                    Toast.makeText(MapActivity.this, "연결에 실패하였습니다...(1)", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }


        alert_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    sx[0] = e.getRawX();
                    sy[0] = e.getRawY();
                }
                if (e.getAction() == MotionEvent.ACTION_MOVE) {
                    ssx[0] = e.getRawX();
                    ssy[0] = e.getRawY();
                } else if (e.getAction() == MotionEvent.ACTION_UP) {
                    float diffxx = sx[0] - e.getRawX();
                    float diffyy = sy[0] - e.getRawY();
                    if (Math.abs(diffxx) > Math.abs(diffyy)) {
                        if (diffxx > MOVE_HAND) {// 왼쪽 드래그
                            String address = getAddress(MapActivity.this, latitude, longitude);
                            //카카오톡 연동
                            shareKaKaoLinkWithMap(address);

                            topicStr = topicStr + "####" + latitude + "####" + longitude + "####사용자####";
                            String msg = new String(topicStr);
                            String word1 = msg.split("####")[0];
                            lati = msg.split("####")[1];
                            longi = msg.split("####")[2];
                            String user = msg.split("####")[3];

                            Log.v("SEYUN_TAG", lati);
                            Log.v("SEYUN_TAG", longi);

                            int qos = 0;
                            try {
                                IMqttToken subToken = client.publish(topic_value, topicStr.getBytes(), qos, false);
                                subToken.setActionCallback(new IMqttActionListener() {
                                    @Override
                                    public void onSuccess(IMqttToken asyncActionToken) { //연결에 성공한 경우
                                        Log.v("SEYUN_TAG", "connection2");
                                        String text = "보호자에게 현재위치를 전송합니다.";
                                        Toast.makeText(MapActivity.this, text, Toast.LENGTH_SHORT).show();

                                        //여기서 토스트문을 음성으로 말해줘야함.
                                        voiceActivity.text = text;
                                        voiceActivity.speekTTS(voiceActivity.text, tts);

                                        topicStr = "사용자의 현재위치입니다.";


                                        Log.v("SEYUN_TAG", "데이터저장");
                                        SharedPreferences tmsg = getSharedPreferences("tmsg", MODE_PRIVATE);
                                        //tmsg.registerOnSharedPreferenceChangeListener(mPrefChangeListener);
                                        SharedPreferences.Editor editor;
                                        editor = tmsg.edit();
                                        editor.putString("latitude", lati);
                                        editor.putString("longitude", longi);

                                        editor.apply();
                                        editor.commit();

                                        //createNotification();

                                    }

                                    @Override
                                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) { //연결에 실패한 경우
                                        Toast.makeText(MapActivity.this, "연결에 실패하였습니다...(2)", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (MqttException fe) {
                                fe.printStackTrace();
                            }
                        } else if (diffxx < -MOVE_HAND) {    // 이ㅣ부분이 오른쪽 드래그
                            topicStr2 = topicStr2 + "####" + latitude + "####" + longitude + "####사용자####";
                            Log.v("SEYUN_TAG", topicStr2);
                            String msg = new String(topicStr2);
                            String word1 = msg.split("####")[0];
                            String lati = msg.split("####")[1];
                            String longi = msg.split("####")[2];
                            String user = msg.split("####")[3];
                            Log.v("SEYUN_TAG", lati);
                            Log.v("SEYUN_TAG", longi);

                            int qos = 0;
                            try {
                                IMqttToken subToken = client.publish(topic_value, topicStr2.getBytes(), qos, false);
                                subToken.setActionCallback(new IMqttActionListener() {
                                    @Override
                                    public void onSuccess(IMqttToken asyncActionToken) { //연결에 성공한 경우
                                        Log.v("SEYUN_TAG", "connection2");
                                        String text = "위험 알림 전송되었습니다.";
                                        Toast.makeText(MapActivity.this, text, Toast.LENGTH_SHORT).show();

                                        //여기서 토스트문을 음성으로 말해줘야함.
                                        voiceActivity.text = text;
                                        voiceActivity.speekTTS(voiceActivity.text, tts);

                                        topicStr2 = "길을 잃었어요!!!";

                                        SharedPreferences tmsg = getSharedPreferences("tmsg", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = tmsg.edit();
                                        editor.putString("latitude", lati);
                                        editor.putString("longitude", longi);
                                        editor.apply();
                                        //editor.commit();
                                        Log.v("SEYUN_TAG", "데이터저장2");
                                    }

                                    @Override
                                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) { //연결에 실패한 경우
                                        Toast.makeText(MapActivity.this, "연결에 실패하였습니다...(2)", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (MqttException fe) {
                                fe.printStackTrace();
                            }
                        }
                    } else {
                        if (diffyy > MOVE_HAND) {
                            Intent intent = new Intent(MapActivity.this, CameraActivity.class);
                            startActivity(intent);
                        } else if (diffyy < -MOVE_HAND) {
                            Intent intent = new Intent(MapActivity.this, FirstviewActivity.class);
                            intent.putExtra("id", user_id);
                            startActivity(intent);
                        }
                    }
                }
                return true;
            }
        });


        Query recentPostsQuery1 = databaseReference.child(user_id).child("location").child(s_location);
        recentPostsQuery1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String key = dataSnapshot.getKey();
                Log.d("sangminkey", key);
                i_index = 0;
                longitude_list.clear();
                latitude_list.clear();
                long n = dataSnapshot.getChildrenCount();
                Log.d("sangconfirm", "" + n);

                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String data_key = messageData.getKey();
                    Log.d("sangmin", data_key);

                    String sLongitude = messageData.child("sLongitude").getValue().toString();
                    String sLatitude = messageData.child("sLatitude").getValue().toString();
                    Log.d("sangmin", sLongitude);
                    Log.d("sangmin", sLatitude);
                    double d_longitude = Double.parseDouble(sLongitude);
                    double d_latitude = Double.parseDouble(sLatitude);
                    i_index++;
                    longitude_list.add(new Double(d_longitude));
                    latitude_list.add(new Double(d_latitude));
                    latlng_list.add(new LatLng(d_latitude, d_longitude));


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String getAddress(Context mContext, double lat, double lng) {
        String nowAddress = "현재 위치를 확인 할 수 없습니다.";
        Geocoder geocoder = new Geocoder(mContext, Locale.KOREA);
        List<Address> address;
        try {
            if (geocoder != null) {
                //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
                //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
                address = geocoder.getFromLocation(lat, lng, 1);

                if (address != null && address.size() > 0) {
                    // 주소 받아오기
                    String currentLocationAddress = address.get(0).getAddressLine(0).toString();
                    nowAddress = currentLocationAddress;
                }
            }
        } catch (IOException e) {
            nowAddress = "주소를 가져올 수 없습니다.";
            System.out.println("주소를 가져올 수 없습니다.");
            e.printStackTrace();
        }
        return nowAddress;
    }


    private MqttConnectOptions getMqttConnectionOption() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setWill("aaa", "I am going offline".getBytes(), 1, true);
        return mqttConnectOptions;
    }
    //mqtt끝!!!!!!!!!!

    //알림창
    private void createNotification() {
        Log.v("SEYUN_TAG", "알림");

        //알림표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Bitmap LargeIconNoti = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        Intent intent = new Intent(this, SplashActivity.class); //알림창 누르면 액티비티로 넘어가는.
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.background)
                .setLargeIcon(LargeIconNoti) //알림창뜨는데 옆 큰 아이콘배치.
                .setContentTitle("경고")
                .setContentText("사용자가 길을 잃었습니다!!!")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE) //소리로 알림을 알려줌.
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        notificationManager.notify(0, builder.build());

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "기본채널", NotificationManager.IMPORTANCE_DEFAULT));
        }*/
    }
    //맵 부분

    @Override
    public void onMapReady(@NonNull final NaverMap naverMap) {
        //naverMap.getUiSettings().setLocationButtonEnabled(true);
        this.naverMap = naverMap;
        //naverMap 크기
        /*
        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        Log.d("sangmin", locationOverlay.getPosition().toString());
        locationOverlay.setVisible(true);
        locationOverlay.setCircleRadius(100);
        locationOverlay.setCircleOutlineWidth(10);
        locationOverlay.setCircleOutlineColor(Color.BLACK);
        */
        locationButtonView.setMap(naverMap);

        // Location Change Listener을 사용하기 위한 FusedLocationSource 설정
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
        //longtitude = locationSource.getLastLocation().getLongitude();
        //latitude = locationSource.getLastLocation().getLatitude();


        saved_location = new Location[longitude_list.size()];
        for (int i = 0; i < longitude_list.size(); i++) {
            saved_location[i] = new Location("point" + i);
            saved_location[i].setLatitude(latitude_list.get(i));
            saved_location[i].setLongitude(longitude_list.get(i));

        }

        for (int i = 0; i < longitude_list.size(); i++) {
            Marker marker = new Marker();
            marker.setPosition((new LatLng(latitude_list.get(i), longitude_list.get(i))));

            if (i == 0) {

                marker.setCaptionText(s_location + " 출발지");
                marker.setCaptionTextSize(16);
                marker.setCaptionColor(Color.BLUE);
                marker.setCaptionAlign(Align.Top);
                marker.setIconTintColor(Color.RED);
            } else if (i == (longitude_list.size() - 1)) {

                marker.setCaptionText(s_location + " 도착지");
                marker.setCaptionTextSize(16);
                marker.setCaptionColor(Color.BLUE);
                marker.setCaptionAlign(Align.Top);
                marker.setIconTintColor(Color.BLUE);
            } else {
                marker.setCaptionText("지점" + i);
            }
            marker.setMap(naverMap);
        }


        //경로선 그리는 코드
        PathOverlay path = new PathOverlay();
        List<LatLng> coords = new ArrayList<>();
        for (int j = 0; j < latlng_list.size(); j++) {
            Collections.addAll(coords, latlng_list.get(j));
        }

        CameraPosition cameraPosition = new CameraPosition(latlng_list.get(0), 18);
        naverMap.setCameraPosition(cameraPosition);

        path.setCoords(coords);
        path.setMap(naverMap);


        for (int k = 0; k < saved_location.length; k++) {
            Query recentPostsQuery = databaseReference.child(user_id).child("location").child(s_location).child("지점" + k).child("message");
            final int finalK = k;
            recentPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    message[finalK] = dataSnapshot.getValue().toString();
                    Log.d("sangminSpod", finalK + String.valueOf(message[finalK]));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }


    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.  eds
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
            location_changed = true;
            longitude = location.getLongitude(); //경도
            latitude = location.getLatitude();   //위도
            postFirebaseDatabase(true);

           /* String stringlong=Double.toString(longitude);
            String stringlat=Double.toString(latitude);

            editor.putString("latitude", stringlong);
            editor.putString("longitude", stringlat);
            editor.commit();*/

            Location trash_location = new Location("trash");
            trash_location.setLatitude(51.5072);
            trash_location.setLongitude(-0.1275);

            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(latitude, longitude)).animate(CameraAnimation.Easing);
            naverMap.moveCamera(cameraUpdate);

            for (int k = 0; k < saved_location.length; k++) {
                if (location.distanceTo(saved_location[k]) <= 10) {
                    index = k;
                    Log.d("sangminSpot", String.valueOf(index));
                    break;
                }
            }
            for (int i = 0; i < saved_location.length; i++) {
                if (index == i) {
                    if (index == 0) {
                        if (message[1].equals("")) {
                            voiceActivity.text = "출발지입니다. 다음 지점까지 가세요.";
                            Toast.makeText(MapActivity.this, voiceActivity.text, Toast.LENGTH_SHORT).show();
                        } else {
                            voiceActivity.text = "출발지입니다. 다음 지점까지 가는 방법은" + message[1] + "입니다.";
                            Toast.makeText(MapActivity.this, voiceActivity.text, Toast.LENGTH_SHORT).show();
                        }

                        saved_location[index] = trash_location;
                        index = 1000;
                    } else if (index == saved_location.length - 1) {
                        voiceActivity.text = "도착지입니다. 안내를 종료하겠습니다.";
                        Toast.makeText(MapActivity.this, voiceActivity.text, Toast.LENGTH_SHORT).show();
                        saved_location[index] = trash_location;
                        index = 1000;
                    } else {
                        if (message[index + 1].equals("")) {
                            voiceActivity.text = "지점" + index + "입니다. 다음 지점까지 가세요.";
                            Toast.makeText(MapActivity.this, voiceActivity.text, Toast.LENGTH_SHORT).show();
                        } else {
                            voiceActivity.text = "지점" + index + "입니다. 다음 지점까지 가는 방법은 " + message[index + 1] + "입니다.";
                            Toast.makeText(MapActivity.this, voiceActivity.text, Toast.LENGTH_SHORT).show();
                        }

                        saved_location[index] = trash_location;
                        index = 1000;
                    }
                    voiceActivity.speekTTS(voiceActivity.text, tts);
                }
            }
            /*
            switch(index) {
                case 0:

                    break;
                case 1:
                    Toast.makeText(MapActivity.this, "지점1", Toast.LENGTH_SHORT).show();
                    saved_location[index] = trash_location;
                    index = 100;
                    break;
                case 2:
                    Toast.makeText(MapActivity.this, "지점2", Toast.LENGTH_SHORT).show();
                    saved_location[index] = trash_location;
                    index = 100;
                    break;
                case 3:
                    Toast.makeText(MapActivity.this, "지점3", Toast.LENGTH_SHORT).show();
                    saved_location[index] = trash_location;
                    index = 100;
                    break;
                case 4:
                    Toast.makeText(MapActivity.this, "지점4", Toast.LENGTH_SHORT).show();
                    saved_location[index] = trash_location;
                    index = 100;
                    break;
            }
            */
        }

        public void onProviderDisabled(String provider) {

        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    };

    public void postFirebaseDatabase(boolean add){
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            CurrentLocation currentLocation = new CurrentLocation(Double.toString(longitude), Double.toString(latitude), s_location);
            postValues = currentLocation.toMap();
        }
        //database 추가 ->pint_list:child , spot: title, postValues :키와 값

        childUpdates.put("/"+user_id+"/currentLocation", postValues);
        databaseReference.updateChildren(childUpdates);
    }


    public void naverMapBasicSettings() {
        mapView.getMapAsync(this);

        //내위치 버튼
        locationButtonView = findViewById(R.id.locationbuttonview);
        // 내위치 찾기 위한 source
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onInit(int status) {//TTS 보내기 위한 함수
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.KOREA);
            if (result == TextToSpeech.LANG_MISSING_DATA) {
                Log.d("hyori", "no tts data");
            } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.d("hyori", "language wrong");
            } else {
                //mRecognizer.stopListening();
                voiceActivity.speekTTS(voiceActivity.text, tts);
            }
        } else {
            Log.d("hyori", "failed");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        if (accelerormeterSensor != null)
            sensorManager.registerListener(this, accelerormeterSensor,
                    SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(user_id).child("currentLocation").removeValue();
        super.onDestroy();
    }

    public void shareKaKaoLinkWithMap(String address) {
        LocationTemplate params =
                LocationTemplate.newBuilder(address,
                        ContentObject.newBuilder("위급상황입니다.",
                                "http://www.kakaocorp.com/images/logo/og_daumkakao_151001.png",
                                LinkObject.newBuilder()
                                        .setWebUrl("https://developers.kakao.com")
                                        .setMobileWebUrl("https://developers.kakao.com")
                                        .build())
                                .setDescrption("사용자님의 현재 위치입니다.")
                                .build())

                        .setAddressTitle("위급상황입니다.")
                        .build();

        Map<String, String> serverCallbackArgs = new HashMap<String, String>();
        serverCallbackArgs.put("user_id", "${current_user_id}");
        serverCallbackArgs.put("product_id", "${shared_product_id}");

        KakaoLinkService.getInstance().sendDefault(this, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
                // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
            }
        });
    }

    //Shake 감지
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if (gabOfTime > 100) {
                lastTime = currentTime;
                x = event.values[DATA_X];
                y = event.values[DATA_Y];
                z = event.values[DATA_Z];

                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;

                if (speed > SHAKE_THRESHOLD) {// 이벤트발생!!
                    topicStr3 = topicStr3 + "####" + latitude + "####" + longitude + "####사용자####";
                    Log.v("SEYUN_TAG", topicStr3);
                    String msg = new String(topicStr3);
                    String word1 = msg.split("####")[0];
                    String lati = msg.split("####")[1];
                    String longi = msg.split("####")[2];
                    String user = msg.split("####")[3];
                    Log.v("SEYUN_TAG", lati);
                    Log.v("SEYUN_TAG", longi);

                    int qos = 0;
                    try {
                        IMqttToken subToken = client.publish(topic_value, topicStr3.getBytes(), qos, false);
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) { //연결에 성공한 경우
                                Log.v("SEYUN_TAG", "connection2");
                                String text = "사용자의 핸드폰이 심각히 흔들렸습니다.";
                                Toast.makeText(MapActivity.this, text, Toast.LENGTH_SHORT).show();

                                //여기서 토스트문을 음성으로 말해줘야함.
                                voiceActivity.text = text;
                                voiceActivity.speekTTS(voiceActivity.text, tts);

                                topicStr3 = "사용자의 핸드폰이 심각히 흔들렸습니다.";

                                SharedPreferences tmsg = getSharedPreferences("tmsg", MODE_PRIVATE);
                                SharedPreferences.Editor editor = tmsg.edit();
                                editor.putString("latitude", lati);
                                editor.putString("longitude", longi);
                                editor.apply();
                                //editor.commit();
                                Log.v("SEYUN_TAG", "데이터저장2");
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) { //연결에 실패한 경우
                                Toast.makeText(MapActivity.this, "연결에 실패하였습니다...(2)", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (MqttException fe) {
                        fe.printStackTrace();
                    }
                }

                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];
            }

        }

    }
}