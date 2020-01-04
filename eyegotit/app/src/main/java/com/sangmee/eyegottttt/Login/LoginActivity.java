package com.sangmee.eyegottttt.Login;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sangmee.eyegottttt.FirstviewActivity;
import com.sangmee.eyegottttt.Map.ProtecterMapActivity;
import com.sangmee.eyegottttt.R;
import com.sangmee.eyegottttt.CSSapi.APIExamTTS;

import java.util.ArrayList;


public class LoginActivity extends AppCompatActivity {

    EditText edtLoginID;
    EditText edtLoginPW;
    Button btnLoginSubmit;
    TextView txtLoginSignup;
    ArrayList<String> login_id_list;
    ArrayList<String> login_pw_list;
    String login_u_id;
    String login_u_pw;
    String pw;
    Intent intent;
    int okay;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.noactionbar);
        setContentView(R.layout.activity_login);


        /*String ttsText = "로그인 화면입니다";

        textString = new String[]{ttsText};

        //AsyncTask 실행
        mNaverTTSTask = new NaverTTSTask();
        mNaverTTSTask.execute(textString);*/


        //setTitle("로그인");
        edtLoginID = (EditText) findViewById(R.id.edtLoginID);
        edtLoginPW = (EditText) findViewById(R.id.edtLoginPW);
        btnLoginSubmit = (Button) findViewById(R.id.btnLoginSubmit);
        txtLoginSignup = (TextView) findViewById(R.id.txtLoginSignup);
        login_id_list = new ArrayList<>();
        login_pw_list = new ArrayList<>();

        /////////////////////자동로그인//////////////////
        SharedPreferences sf = getSharedPreferences("sFile", MODE_PRIVATE);
        //us_id/us_pw라는 key에 저장된 값이 있는지 확인. 아무값도 들어있지 않으면 ""를 반환
        String us_id = sf.getString("us_id", "");
        String us_pw = sf.getString("us_pw", "");
        edtLoginID.setText(us_id);
        edtLoginPW.setText(us_pw);
        ///////////////////////////자동로그인/////////////////////


        //focus 이벤트 (색 변환)
        edtLoginID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    view.setBackgroundResource(R.drawable.primary_border_login);
                else
                    view.setBackgroundResource(R.drawable.gray_border_login);
            }
        });


        edtLoginPW.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    view.setBackgroundResource(R.drawable.primary_border_login);
                else
                    view.setBackgroundResource(R.drawable.gray_border_login);
            }
        });
        ///////////////////색변환 끝//////////////////

        //로그인버튼 클릭시
        btnLoginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_u_id = edtLoginID.getText().toString();
                login_u_pw = edtLoginPW.getText().toString();
                okay = 0;

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        login_id_list.clear();
                        login_pw_list.clear();

                        for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                            String userid = messageData.getKey();
                            Log.d("okay", userid);
                            login_id_list.add(userid);
                        }

                        for (int i = 0; i < login_id_list.size(); i++) {
                            if (login_id_list.get(i).equals(login_u_id)) {
                                okay = 1;
                                Query recentPostsQuery = databaseReference.child(login_u_id).child("signup_u_pw");
                                recentPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        pw = dataSnapshot.getValue().toString();
                                        Log.d("sgsg", pw);
                                        if (pw.equals(login_u_pw)) {

                                            login_dialog();
                                        } else {

                                            pw_wrong_dialog();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                break;
                            }

                        }
                        if (okay == 0) {
                            id_wrong_dialog();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
            }
        });

        //회원가입 텍스트 눌렀을시
        txtLoginSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }


    void login_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.normal_dialog, null);
        final TextView location_edit = view.findViewById(R.id.delete_text);
        location_edit.setTextColor(Color.GRAY);
        location_edit.setText(login_u_id + "으로 로그인 되었습니다.");
        builder.setView(view);

        //확인버튼
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Query recentPostsQuery = databaseReference.child(login_u_id).child("who");
                recentPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String who = dataSnapshot.getValue().toString();
                        Log.d("sgsg", who);
                        if (who.equals("사용자")) {
                            SharedPreferences sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);

                            //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String us_id = edtLoginID.getText().toString();
                            String us_pw = edtLoginPW.getText().toString();
                            editor.putString("us_id", us_id); // key, value를 이용하여 저장하는 형태
                            editor.putString("us_pw", us_pw);
                            //최종 커밋
                            editor.commit();
                            intent = new Intent(LoginActivity.this, FirstviewActivity.class);
                            intent.putExtra("id", login_u_id);
                            startActivity(intent);
                            finish();
                        } else {
                            SharedPreferences sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);

                            //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String us_id = edtLoginID.getText().toString();
                            String us_pw = edtLoginPW.getText().toString();
                            editor.putString("us_id", us_id); // key, value를 이용하여 저장하는 형태
                            editor.putString("us_pw", us_pw);
                            //최종 커밋
                            editor.commit();
                            intent = new Intent(LoginActivity.this, ProtecterMapActivity.class);
                            intent.putExtra("id", login_u_id);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        builder.show();
    }

    void pw_wrong_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.normal_dialog, null);
        final TextView location_edit = view.findViewById(R.id.delete_text);
        location_edit.setTextColor(Color.GRAY);
        location_edit.setText("비밀번호가 틀렸습니다.");
        builder.setView(view);

        //확인버튼
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edtLoginPW.setText(null);
            }
        });

        builder.show();
    }

    void id_wrong_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.normal_dialog, null);
        final TextView location_edit = view.findViewById(R.id.delete_text);
        location_edit.setTextColor(Color.GRAY);
        location_edit.setText("등록된 아이디가 없습니다.");
        builder.setView(view);

        //확인버튼
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }

    void exit_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.normal_dialog, null);
        final TextView location_edit = view.findViewById(R.id.delete_text);
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
