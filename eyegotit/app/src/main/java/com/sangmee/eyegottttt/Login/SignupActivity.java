package com.sangmee.eyegottttt.Login;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sangmee.eyegottttt.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SignupActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    EditText edtSignupID;
    EditText edtSignupPW;
    EditText edtSignupPWConfirm;
    EditText edtUserId;
    Button btnSignupSubmit;
    RadioButton manager_button, user_button;
    Intent intent;
    String who="사용자";
    String signup_u_id;
    String signup_u_pw;
    String topic;
    String user_id;
    ArrayList<String> sign_up_id_list;
    int okay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.noactionbar);
        setContentView(R.layout.activity_signup);
        //setTitle("회원가입");
        edtSignupID=(EditText) findViewById(R.id.edtSignupID);
        edtSignupPW=(EditText)findViewById(R.id.edtSignupPW);
        edtSignupPWConfirm=findViewById(R.id.edtSignupPWConfirm);
        edtUserId=findViewById(R.id.edtUserId);
        btnSignupSubmit=(Button)findViewById(R.id.btnSignupSubmit);

        RadioGroup group=(RadioGroup)findViewById(R.id.radio_group);
        manager_button=(RadioButton)findViewById(R.id.manager_radioButton);
        user_button=(RadioButton)findViewById(R.id.user_radioButton);
        sign_up_id_list=new ArrayList<>();

        edtUserId.setVisibility(View.GONE);


        //focus 이벤트 (색 변환)
        edtSignupID.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    view.setBackgroundResource(R.drawable.primary_border_login);
                else
                    view.setBackgroundResource(R.drawable.gray_border_login);
            }
        });

        edtSignupPW.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    view.setBackgroundResource(R.drawable.primary_border_login);
                else
                    view.setBackgroundResource(R.drawable.gray_border_login);
            }
        });

        edtSignupPWConfirm.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    view.setBackgroundResource(R.drawable.primary_border_login);
                else
                    view.setBackgroundResource(R.drawable.gray_border_login);
            }
        });
        ///////////////////////////색변환 끝////////////////

        //라디오 버튼 선택 함수
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.user_radioButton:
                        edtUserId.setText("");
                        edtUserId.setVisibility(View.GONE);
                        who="사용자";
                        break;
                    case R.id.manager_radioButton:
                        edtUserId.setVisibility(View.VISIBLE);
                       who="보호자";
                        break;
                }
            }
        });

        //회원가입 버튼 클릭 되었을 때
        btnSignupSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                okay=0;
                signup_u_id = edtSignupID.getText().toString();
                signup_u_pw = edtSignupPW.getText().toString();
                user_id=edtUserId.getText().toString();
                String signup_u_pw_confirm = edtSignupPWConfirm.getText().toString();
                topic=randomString();

                //비밀번호와 비밀번호 확인 이 같은지 확인
                if(signup_u_pw.equals(signup_u_pw_confirm)){
                    //데이터베이스에 같은 아이디 있는지 확인
                    databaseReference = FirebaseDatabase.getInstance().getReference(); // 변경값을 확인할 child 이름
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                                String userid=messageData.getKey();
                                Log.d("okay", userid);
                                sign_up_id_list.add(userid);
                            }

                            for(int i=0; i<sign_up_id_list.size(); i++){
                                if(sign_up_id_list.get(i).equals(signup_u_id)){

                                    okay=1;
                                    break;
                                }
                            }
                            Log.d("okay", ":"+okay);

                            //고유 아이디를 썻다면
                            if(okay==0){
                                databaseReference.child(signup_u_id);
                                postFirebaseDatabase(true);
                                //보호자 모드로 선택 되었다면 사용자랑 topic 값 같게 한다.
                                if(!user_id.equals("")){
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                                                String key=messageData.getKey();
                                                if(key.equals(user_id)){
                                                    //Query로 데이터베이스의 특정한 값 가져오기
                                                    Query recentPostsQuery = databaseReference.child(key).child("topic");
                                                    recentPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            topic=dataSnapshot.getValue().toString();
                                                            Log.d("sgsg", topic);
                                                            postFirebaseDatabase(true);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });

                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                }
                                signup_dialog();


                            }
                            else{

                                normal_dialog();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                }
                else{
                    password_dialog();
                }
            }
        });

    }

    void normal_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyAlertDialogStyle);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.normal_dialog, null);
        final TextView location_edit=view.findViewById(R.id.delete_text);
        location_edit.setTextColor(Color.GRAY);
        location_edit.setText("같은 아이디가 존재합니다.");
        builder.setView(view);

        //확인버튼
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.show();
    }

    void password_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyAlertDialogStyle);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.normal_dialog, null);
        final TextView location_edit=view.findViewById(R.id.delete_text);
        location_edit.setTextColor(Color.GRAY);
        location_edit.setText("비밀번호가 일치하지 않습니다.");
        builder.setView(view);

        //확인버튼
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }

    void signup_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyAlertDialogStyle);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.normal_dialog, null);
        final TextView location_edit=view.findViewById(R.id.delete_text);
        location_edit.setTextColor(Color.GRAY);
        location_edit.setText("회원가입 되었습니다.");
        builder.setView(view);

        //확인버튼
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.show();
    }

    public void postFirebaseDatabase(boolean add){
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            Signup post = new Signup(signup_u_pw, user_id, who, topic);
            postValues = post.toMap();
        }
        //database 추가 ->pint_list:child , spot: title, postValues :키와 값

        childUpdates.put("/"+signup_u_id+"/" , postValues);
        databaseReference.updateChildren(childUpdates);
    }

    public String randomString(){
        StringBuffer topic = new StringBuffer();
        Random rnd = new Random();
        for (int i = 0; i < 20; i++) {
            int rIndex = rnd.nextInt(3);
            switch (rIndex) {
                case 0:
                    // a-z
                    topic.append((char) ((int) (rnd.nextInt(26)) + 97));
                    break;
                case 1:
                    // A-Z
                    topic.append((char) ((int) (rnd.nextInt(26)) + 65));
                    break;
                case 2:
                    // 0-9
                    topic.append((rnd.nextInt(10)));
                    break;
            }
        }
        return topic.toString();
    }


}