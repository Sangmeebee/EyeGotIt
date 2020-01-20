package com.sangmee.eyegottttt;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sangmee.eyegottttt.CSRapi.AudioWriterPCM;
import com.sangmee.eyegottttt.CSRapi.CsrProc;
import com.sangmee.eyegottttt.checkbox_listview.Delete_DatabaseActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.naver.speech.clientapi.SpeechRecognitionResult;

public class DatabaseActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener mChild;

    private ListView listView;
    ArrayList<String> child_name;
    ArrayAdapter<String> arrayAdapter;
    String user_id;
    ImageButton button;
    SpeakVoiceActivity voiceActivity;
    //ReplyVoiceActivity replyVoiceActivity;
    TextToSpeech tts;
    final int PERMISSION = 1;
    String arrayListText;
    String [] strings;


    ArrayList<String> n_sLongitude;
    ArrayList<String> n_sLatitude;
    String s_location;
    Intent intent, intentId;
    String text;

    //////CSR//////
    private static final String TAG = DatabaseActivity.class.getSimpleName();
    private RecognitionHandler handler;
    private CsrProc naverRecognizer;
    private TextView txtResult;
    private Button btnStart;
    private String mResult;
    private AudioWriterPCM writer;
    private GlideDrawableImageViewTarget gifImage;
    ////////////////



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        setTitle("나의 경로 리스트");


        if ( Build.VERSION.SDK_INT >= 23 ){
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO},PERMISSION);
        }

        //voiceActivity.text="안녕";


        listView=(ListView)findViewById(R.id.database_list);
        intentId=getIntent();
        user_id=intentId.getStringExtra("id");
        txtResult = findViewById(R.id.textViewwww);
        button=findViewById(R.id.imageButton4);
        button.setOnClickListener(voicereplyListener);

        gifImage=new GlideDrawableImageViewTarget(button);
        Glide.with(this).load(R.drawable.loader).into(gifImage);

        handler = new RecognitionHandler(this);
        naverRecognizer = CsrProc.getCsrProc(this, "ssbj4qersa");
        naverRecognizer.setHandler(handler);

        child_name=new ArrayList<>();
        initDatabase();

        arrayAdapter=new ArrayAdapter<String>(this,R.layout.listviewtext, new ArrayList<String>()){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Cast the list view each item as text view
                TextView item = (TextView) super.getView(position,convertView,parent);

                item.setTextColor(Color.parseColor("#484848"));

                // Change the item text size
                item.setTextSize(TypedValue.COMPLEX_UNIT_DIP,17);

                item.setPadding(50,5,5,5);

                // return the view
                return item;
            }
        };
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(onItemClickListener);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        Query recentPostsQuery = databaseReference.child(user_id).child("location");
        recentPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayAdapter.clear();

                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String name=messageData.getKey();
                    child_name.add(name);
                    arrayAdapter.add(name);

                    strings=new String[child_name.size()];

                    arrayListText=" ";
                    for(int i=0;i<child_name.size();i++){
                        strings[i]=child_name.get(i);

                        arrayListText=arrayListText.concat(i+1+"번, "+strings[i]+"  ,  ");
                    }

                }
                arrayAdapter.notifyDataSetChanged();
                listView.setSelection(arrayAdapter.getCount()-1);

                tts=new TextToSpeech(DatabaseActivity.this,DatabaseActivity.this);
                voiceActivity=new SpeakVoiceActivity(DatabaseActivity.this,tts);
                if(arrayListText!=null) {
                    voiceActivity.text = "나의 경로는 " + arrayListText + arrayAdapter.getCount() + "개의 경로가 있습니다." +
                            "어떤 경로를 선택하시겠습니까? 번호를 말해주세요.";
                }
                else if(arrayListText==null){
                    voiceActivity.text="경로가 없습니다. 먼저 경로를 등록해주세요.";
                }

                //replyVoiceActivity = new ReplyVoiceActivity(DatabaseActivity.this, tts, "하나",null,null,arrayAdapter,listView);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onStart() {
        System.out.println("시작!!!");
        super.onStart(); // 음성인식 서버 초기화는 여기서
        naverRecognizer.getSpeechRecognizer().initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mResult = "";
        txtResult.setText("");
        //btnStart.setText("시작");
        //btnStart.setEnabled(true);
    }

    @Override
    protected void onStop() {
        tts.stop();
        GlideDrawableImageViewTarget gifImage=new GlideDrawableImageViewTarget(button);
        Glide.with(DatabaseActivity.this).load(R.drawable.loader).into(gifImage);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        //TTS 멈추기
        if(tts!=null){
            tts.stop();
            tts.shutdown();

        }
        databaseReference.removeEventListener(mChild);
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

    /////////////////////////////////////////////////////////////
    // 음성 인식 버튼 //

    View.OnClickListener voicereplyListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(tts!=null) {
                tts.stop();
            }

            GlideDrawableImageViewTarget gifImage=new GlideDrawableImageViewTarget(button);
            Glide.with(DatabaseActivity.this).load(R.drawable.loader2).into(gifImage);

            //replyVoiceActivity.receiver();

            if (!naverRecognizer.getSpeechRecognizer().isRunning()) {

                mResult = "";
                txtResult.setText("Connecting...");
                //btnStart.setText("그만");
                naverRecognizer.recognize();
            } else {
                Log.d(TAG, "stop and wait Final Result");
                //btnStart.setEnabled(false);
                naverRecognizer.getSpeechRecognizer().stop();
            }



        }

    };

    /////////////////////////////////////////////////////////////

    ////////////////////Csr//////////////////
    private void handleMessage(Message msg) {
        switch (msg.what) {
            case 1: // 음성인식 준비 가능
                txtResult.setText("Connected");
                writer = new AudioWriterPCM(Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;
            case 2:
                writer.write((short[]) msg.obj);
                break;
            case 3:
                mResult = (String) (msg.obj);
                txtResult.setText(mResult);
                break;
            case 4: // 최종 인식 결과
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
                StringBuilder strBuf = new StringBuilder();
                for(String result : results) {
                    strBuf.append(result);
                    //strBuf.append("\n");
                    break;
                }
                mResult = strBuf.toString();

                txtResult.setText(mResult);
                datapathreplyAnswer(mResult);

                //GlideDrawableImageViewTarget gifImage=new GlideDrawableImageViewTarget(button);
                Glide.with(this).load(R.drawable.loader).into(gifImage);

                break;
            case 5:
                if (writer != null) {
                    writer.close();
                }
                mResult = "Error code : " + msg.obj.toString();
                txtResult.setText(mResult);
                //btnStart.setText("시작");
                //btnStart.setEnabled(true);
                break;
            case 6:
                if (writer != null) {
                    writer.close();
                }
                //btnStart.setText("시작");
                //btnStart.setEnabled(true);
                break;
        }
    }

    private void datapathreplyAnswer(String input){
        String[] array = input.split("번");
        for(int i=1;i<=arrayAdapter.getCount();i++){
            if(i==Integer.parseInt(array[0])){
                listView.performItemClick(listView,i-1,1);
            }
        }
    }

    private void initDatabase() {

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mChild = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference.addChildEventListener(mChild);
    }




    AdapterView.OnItemClickListener onItemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            s_location=adapterView.getAdapter().getItem(i).toString();
            Log.d("sangmin", s_location);
            intent=new Intent(DatabaseActivity.this, route_confirmActivity.class);
            intent.putExtra("s_location", s_location);
            intent.putExtra("id", user_id);
            startActivity(intent);
            tts.stop();
        }
    };
    @Override
    protected void onRestart() {

        voiceActivity.speekTTS(voiceActivity.text,tts);
        super.onRestart();
    }

    //메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.switchs:
                break;*/
            case R.id.delete:
                intent=new Intent(DatabaseActivity.this, Delete_DatabaseActivity.class);
                intent.putExtra("id", user_id);
                startActivity(intent);

                return true;




        }
        return super.onOptionsItemSelected(item);
    }

    static class RecognitionHandler extends Handler {
        private final WeakReference<DatabaseActivity> mActivity;
        RecognitionHandler(DatabaseActivity activity) {
            mActivity = new WeakReference<DatabaseActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            DatabaseActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }


}