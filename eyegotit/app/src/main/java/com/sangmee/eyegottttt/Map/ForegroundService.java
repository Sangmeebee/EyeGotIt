package com.sangmee.eyegottttt.Map;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.sangmee.eyegottttt.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    double lat;
    double lon;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        String address=intent.getStringExtra("address");
        createNotificationChannel();

        Bitmap LargeIconNoti = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        Intent notificationintent = new Intent(this, ProtecterMapActivity2.class); //알림창 누르면 액티비티로 넘어가는.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationintent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                //.setContentText(input)
                .setSmallIcon(R.drawable.background)
                .setLargeIcon(LargeIconNoti)
                .setContentIntent(pendingIntent)
                .setContentTitle("경고")
                .setContentText(address+"입니다!!!")
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE) //소리로 알림을 알려줌.
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .build();

        startForeground(1, notification);

        //do heavy work on a background thread


        //stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    /////주소를 위도, 경도로 바꿔주는 함수
    public void getAddressLang(Context mContext, String str){
        // 주소입력후 지도2버튼 클릭시 해당 위도경도값의 지도화면으로 이동
        Geocoder geocoder = new Geocoder(mContext, Locale.KOREA);
        List<Address> list = null;
        //
        try {
            list = geocoder.getFromLocationName
                    (str, // 지역 이름
                            10); // 읽을 개수
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
        }

        if (list != null) {
            Address addr = list.get(0);
            lat = addr.getLatitude();
            lon = addr.getLongitude();
            Log.i("hyori","위도 :"+lat+" , "+"경도 : "+lon);
            //Toast.makeText(MainActivity.this,"위도 :"+lat+" , "+"경도 : "+lon,Toast.LENGTH_LONG).show();
        }
    }
}