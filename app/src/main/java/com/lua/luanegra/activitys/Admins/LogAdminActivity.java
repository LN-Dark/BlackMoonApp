package com.lua.luanegra.activitys.Admins;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lua.luanegra.R;
import com.lua.luanegra.adapters.LogAdminAdapter;
import com.lua.luanegra.objects.LogObject;
import com.lua.luanegra.objects.UserObject;
import com.lua.luanegra.tools.EmptyRecyclerView;
import com.lua.luanegra.tools.OnlineService;

import java.util.ArrayList;
import java.util.Objects;

public class LogAdminActivity extends AppCompatActivity {
    private EmptyRecyclerView.Adapter mLogListAdapter;
    private ArrayList<LogObject> logList;
    private DatabaseReference LogRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_admin);
        try{
            Toolbar toolbar = findViewById(R.id.toolbarActivity);
            toolbar.setLogo(getDrawable(R.drawable.luanegra_logo));
            toolbar.setSubtitle("" + "LॐN Log");
            setSupportActionBar(toolbar);

        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }
    }

    @Override
    public void setTheme(int resId) {
        SharedPreferences prefs = getSharedPreferences("AppTheme", Context.MODE_PRIVATE);
        String tema = " ";
        tema = prefs.getString("AppTheme", " ");
        if(tema != " "){
            if(tema.equals("light")){
                super.setTheme(R.style.AppTheme_Light);
            }else {
                super.setTheme(R.style.AppTheme);
            }
        }else {
            super.setTheme(R.style.AppTheme_Light);
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.log_admin_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.apagar_all_log){
            FirebaseDatabase.getInstance().getReference().child("logError").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    logList.clear();
                    mLogListAdapter.notifyDataSetChanged();
                }
            });
        }else if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return true;
    }



    @Override
    protected void onResume() {
        super.onResume();
        logList = new ArrayList<>();
        LogRef = FirebaseDatabase.getInstance().getReference().child("logError");
        InitializeRecyclerView();
        GetAllUsers();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        startService(new Intent(getBaseContext(), OnlineService.class));
    }

    private ArrayList<UserObject> allUserList;
    private void GetAllUsers(){
        try{
            allUserList = new ArrayList<>();
            DatabaseReference userBDRef = FirebaseDatabase.getInstance().getReference().child("user");
            userBDRef.keepSynced(true);
            userBDRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                            if (!Objects.requireNonNull(childSnapshot.getKey()).equals("chat")){
                                UserObject userObject = new UserObject(Objects.requireNonNull(childSnapshot.child("name").getValue()).toString(), childSnapshot.getKey());
                                userObject.setNotificationKey(Objects.requireNonNull(childSnapshot.child("notificationKey").getValue()).toString());
                                userObject.setImagemPerfilUri(Objects.requireNonNull(childSnapshot.child("profile_image").getValue()).toString());
                                allUserList.add(userObject);
                            }
                        }
                        GetAllLogs();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(childEventListener != null){
            LogRef.removeEventListener(childEventListener);
        }
    }

    private ChildEventListener childEventListener;
    private void GetAllLogs(){
        try {
            LogRef.keepSynced(true);
           childEventListener = LogRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists()){
                        for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                            String logText, logCreator, uid;
                            logText = Objects.requireNonNull(childSnapshot.getValue()).toString();
                            uid = dataSnapshot.getKey();
                            logCreator = childSnapshot.getKey();
                            for(int i = 0; i <allUserList.size(); i++){
                                if(allUserList.get(i).getUid().equals(childSnapshot.getKey())){
                                    logCreator = allUserList.get(i).getName();
                                }
                            }
                            LogObject logObject = new LogObject(logText,logCreator ,uid);
                            logList.add(logObject);
                            mLogListAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
        FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }
    }

    private void InitializeRecyclerView() {
        try {
            EmptyRecyclerView mLogList = findViewById(R.id.Log_List);
            mLogList.setNestedScrollingEnabled(false);
            mLogList.setHasFixedSize(false);
            EmptyRecyclerView.LayoutManager mUserListLayoutManager = new LinearLayoutManager(LogAdminActivity.this, EmptyRecyclerView.VERTICAL, false);
            mLogList.setLayoutManager(mUserListLayoutManager);
            mLogListAdapter = new LogAdminAdapter(logList);
            mLogList.setAdapter(mLogListAdapter);
            mLogList.setItemViewCacheSize(0);
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }
    }
}
