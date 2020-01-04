package com.sangmee.eyegottttt.checkbox_listview;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sangmee.eyegottttt.R;

import java.util.ArrayList;

public class Delete_DatabaseActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener mChild;

    private ListView listView;
    ArrayList<String> child_name;

    CustomChoiceListViewAdapter adapter=new CustomChoiceListViewAdapter();
    Intent intentId;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_database);
        setTitle("경로 삭제하기");

        listView=(ListView)findViewById(R.id.delete_database_list);
        intentId=getIntent();
        user_id=intentId.getStringExtra("id");

        child_name=new ArrayList<>();
        initDatabase();


        listView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        Query recentPostsQuery = databaseReference.child(user_id).child("location");
        recentPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clearItem();

                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String name=messageData.getKey();
                    child_name.add(name);
                    adapter.addItem(name);
                }
                adapter.notifyDataSetChanged();
                listView.setSelection(adapter.getCount()-1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    //삭제 다이얼로그
    void delete_dialog() {
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
        location_edit.setText("선택한 경로를 삭제하시겠습니까?");
        builder.setView(view);



        //확인버튼
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //데이터 베이스 삭제
                SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
                int count = adapter.getCount() ;

                for (int i = count-1; i >= 0; i--) {
                    if (checkedItems.get(i)) {
                        databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child(user_id).child("location").child(child_name.get(i)).removeValue();
                        child_name.remove(i) ;
                    }
                }
                // 모든 선택 상태 초기화.
                listView.clearChoices() ;
                adapter.notifyDataSetChanged();

            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(mChild);
    }


    //메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.choice:
                int count = 0 ;
                count = adapter.getCount() ;

                for (int i=0; i<count; i++) {
                    listView.setItemChecked(i, true) ;
                }

                break;

            case R.id.not_choice:
                int not_count = 0 ;
                not_count = adapter.getCount() ;

                for (int i=0; i<not_count; i++) {
                    listView.setItemChecked(i, false); ;
                }

                break;

            case R.id.delete:
                delete_dialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
