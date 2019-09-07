package com.lua.luanegra.activitys.Admins;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lua.luanegra.R;
import com.lua.luanegra.adapters.RegistodeAcessoAdminsAdapter;
import com.lua.luanegra.objects.LogObject;
import com.lua.luanegra.objects.UserObject;
import com.lua.luanegra.tools.EmptyRecyclerView;
import com.lua.luanegra.tools.OnlineService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class RegistoAcessoAdminsActivity extends AppCompatActivity {
    private EmptyRecyclerView.Adapter funcionalidadesListAdapter;
    private ArrayList<LogObject> funcionalidadesList;
    private DatabaseReference funcionalidadesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registo_acesso_admins);
        try{
            Toolbar toolbar = findViewById(R.id.toolbarActivity);
            toolbar.setLogo(getDrawable(R.drawable.luanegra_logo));
            toolbar.setSubtitle("" + getString(R.string.registodeacessoadmins));
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        funcionalidadesList = new ArrayList<>();
        funcionalidadesRef = FirebaseDatabase.getInstance().getReference().child("adminAccess");
        InitializeRecyclerView();
        GetAllUsers();
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
                                userObject.setIsonline(Objects.requireNonNull(childSnapshot.child("online").getValue()).toString());
                                userObject.setLastOnline("Ultima vez online: \n" + Objects.requireNonNull(childSnapshot.child("onlineDate").getValue()).toString() + "  *  " + Objects.requireNonNull(childSnapshot.child("onlineTime").getValue()).toString());
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
            funcionalidadesRef.removeEventListener(childEventListener);
        }
    }

    private ChildEventListener childEventListener;
    private void GetAllLogs(){
        try {
            funcionalidadesRef.keepSynced(true);
            childEventListener = funcionalidadesRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists()){
                        for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                            Collections.reverse(funcionalidadesList);
                            String logText, logCreator, uid;
                            if(childSnapshot.child("data").exists()){
                                if(childSnapshot.child("hora").exists()){
                                    logText = childSnapshot.child("data").getValue().toString() + "  -  " + childSnapshot.child("hora").getValue().toString();
                                    uid = dataSnapshot.getKey();
                                    logCreator = childSnapshot.getKey();
                                    for(int i = 0; i <allUserList.size(); i++){
                                        if(allUserList.get(i).getUid().equals(childSnapshot.getKey())){
                                            logCreator = allUserList.get(i).getName();
                                        }
                                    }
                                    LogObject logObject = new LogObject(logText,logCreator ,uid);
                                    funcionalidadesList.add(logObject);
                                    Collections.reverse(funcionalidadesList);
                                    funcionalidadesListAdapter.notifyDataSetChanged();
                                }
                            }
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
            EmptyRecyclerView mLogList = findViewById(R.id.acessoAdmins_recycler);
            mLogList.setNestedScrollingEnabled(false);
            mLogList.setHasFixedSize(false);
            EmptyRecyclerView.LayoutManager mUserListLayoutManager = new LinearLayoutManager(RegistoAcessoAdminsActivity.this, EmptyRecyclerView.VERTICAL, false);
            mLogList.setLayoutManager(mUserListLayoutManager);
            funcionalidadesListAdapter = new RegistodeAcessoAdminsAdapter(funcionalidadesList);
            mLogList.setAdapter(funcionalidadesListAdapter);
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }
    }
}
