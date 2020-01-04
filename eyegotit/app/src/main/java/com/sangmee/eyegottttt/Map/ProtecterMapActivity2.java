package com.sangmee.eyegottttt.Map;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Align;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.LocationButtonView;
import com.sangmee.eyegottttt.Login.ListViewAdapterSwipt;
import com.sangmee.eyegottttt.Login.LoginActivity;
import com.sangmee.eyegottttt.R;

import java.util.ArrayList;

public class ProtecterMapActivity2 extends AppCompatActivity implements OnMapReadyCallback,NavigationView.OnNavigationItemSelectedListener {

    //지도 변수
    // NaverMap API 3.0
    private MapView mapView;
    private LocationButtonView locationButtonView;

    double longitude;
    double latitude;

    NaverMap naverMap;
    ArrayList<Marker> arrayList1=new ArrayList<>();
    Marker marker;

    // FusedLocationSource (Google)
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;

    private DatabaseReference databaseReference;
    String user_id;

    //notification bar
    ListViewAdapterSwipt adapter;
    ListView listView;
    ImageButton menu_btn;
    DrawerLayout drawer;
    MapActivity mapActivity=new MapActivity();
    boolean locationcha=mapActivity.location_changed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protecter_map2);

        setTitle("");

        menu_btn=findViewById(R.id.menuButton);
        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.END);
            }
        });

        //맵 변수
        mapView = findViewById(R.id.main_map_view);
        mapView.onCreate(savedInstanceState);
        naverMapBasicSettings();
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(ProtecterMapActivity2.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ProtecterMapActivity2.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


       /* CameraPosition cameraPosition = new CameraPosition(new LatLng(latitude_L, longitude_L), 17);
        //naverMap.setCameraPosition(cameraPosition); //여기서 오류가남.*/

        listView=findViewById(R.id.list_view_inside_nav);
        adapter= new ListViewAdapterSwipt();
        listView.setAdapter(adapter);
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.recruitment), "내 정보");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.exit), "로그아웃");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                switch (position){
                    case 0 :
                        break;
                    case 1 :
                        logout_dialog();
                        break;
                }
            }
        }) ;



        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        ////////////


        //DrawMarker

    }

    //맵 부분
    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
            longitude = location.getLongitude(); //경도
            latitude = location.getLatitude();   //위도

        }

        public void onProviderDisabled(String provider) {

        }

        public void onProviderEnabled(String provider) {
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    };


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

    public NaverMap getNaverMap() {
        return naverMap;
    }

    @Override
    public void onMapReady(@NonNull final NaverMap naverMap) {
        this.naverMap = naverMap;
        locationButtonView.setMap(naverMap);

        // Location Change Listener을 사용하기 위한 FusedLocationSource 설정
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        SharedPreferences tmsg = getSharedPreferences("tmsg", MODE_PRIVATE);
        String lati_L = tmsg.getString("latitude", "");
        String longi_L = tmsg.getString("longitude", "");

        Log.v("SEYUN_TAG", lati_L);
        Log.v("SEYUN_TAG", longi_L);

        double latitude_L = Double.parseDouble(lati_L);
        double longitude_L = Double.parseDouble(longi_L);

        marker=new Marker();
        marker.setPosition((new LatLng(latitude_L, longitude_L)));
        marker.setCaptionText("사용자의 위치");
        marker.setCaptionTextSize(16);
        marker.setCaptionColor(Color.BLACK);
        marker.setCaptionAlign(Align.Top);
        marker.setIconTintColor(Color.RED);
        marker.setMap(naverMap);

        Double latitude=marker.getPosition().latitude;
        Double longtitude=marker.getPosition().longitude;
        //Toast.makeText(this,"위치 : "+latitude+","+longtitude,Toast.LENGTH_LONG).show();

        CameraPosition cameraPosition = new CameraPosition(new LatLng(latitude_L, longitude_L), 17);
        naverMap.setCameraPosition(cameraPosition);



    }
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
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
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    void logout_dialog() {
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
                Intent intent=new Intent(ProtecterMapActivity2.this, LoginActivity.class);
                ActivityCompat.finishAffinity(ProtecterMapActivity2.this);
                startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }
}