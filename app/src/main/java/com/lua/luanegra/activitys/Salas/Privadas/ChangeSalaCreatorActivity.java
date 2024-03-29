package com.lua.luanegra.activitys.Salas.Privadas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lua.luanegra.R;
import com.lua.luanegra.adapters.ChangeCreatorSalaPrivadaAdapter;
import com.lua.luanegra.objects.UserObject;
import com.lua.luanegra.tools.EmptyRecyclerView;
import com.lua.luanegra.tools.OnlineService;

import java.util.ArrayList;
import java.util.Objects;

public class ChangeSalaCreatorActivity extends AppCompatActivity {
    private String groupUID;
    private ArrayList<String> listaNotifcationAdmins, listaIdsAdmins, idsUsersSala;
    private RecyclerView.Adapter mUserListAdapter;
    private ArrayList<UserObject> listaUsersSala;
    private String  currentGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_sala_creator);
        groupUID = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("groupUID")).toString();
        currentGroupName = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("groupName")).toString();
        Toolbar toolbar = findViewById(R.id.toolbarActivity);
        toolbar.setLogo(getDrawable(R.drawable.luanegra_logo));
        toolbar.setSubtitle("" + getString(R.string.pedidossalaprivada));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
        GetAllUsers();
        startService(new Intent(getBaseContext(), OnlineService.class));
    }

    private void GetAllUsers(){
        listaIdsAdmins = new ArrayList<>();
        listaNotifcationAdmins = new ArrayList<>();
        listaUsersSala = new ArrayList<>();
        idsUsersSala = new ArrayList<>();
        InitializeRecyclerView();
        DatabaseReference dataRef =  FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID);
        dataRef.keepSynced(true);
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("users").exists()){
                        for(DataSnapshot childSnapShotBloqedUser : dataSnapshot.child("users").getChildren()){
                            idsUsersSala.add(childSnapShotBloqedUser.getKey());
                        }
                    }
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("user");
                    userRef.keepSynced(true);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            if(dataSnapshot2.exists()){
                                for(DataSnapshot childSnapShotUsers : dataSnapshot2.getChildren()){
                                    if(!Objects.requireNonNull(childSnapShotUsers.getKey()).equals("chat")){

                                        UserObject newUser = new UserObject(Objects.requireNonNull(childSnapShotUsers.child("name").getValue()).toString(), childSnapShotUsers.getKey());
                                        newUser.setNotificationKey(Objects.requireNonNull(childSnapShotUsers.child("notificationKey").getValue()).toString());
                                        newUser.setImagemPerfilUri(Objects.requireNonNull(childSnapShotUsers.child("profile_image").getValue()).toString());
                                        newUser.setIsonline(Objects.requireNonNull(childSnapShotUsers.child("online").getValue()).toString());
                                        newUser.setRegistoData(Objects.requireNonNull(childSnapShotUsers.child("registerDate").getValue()).toString());
                                        newUser.setRegistoHora(Objects.requireNonNull(childSnapShotUsers.child("registerTime").getValue()).toString());
                                        newUser.setLastOnline("Ultima vez online: \n" + Objects.requireNonNull(childSnapShotUsers.child("onlineDate").getValue()).toString() + "  *  " + Objects.requireNonNull(childSnapShotUsers.child("onlineTime").getValue()).toString());
                                        newUser.setSalaPrivadaID(groupUID);
                                        newUser.setNomeSalaPrivada(currentGroupName);
                                        if(idsUsersSala.contains(newUser.getUid())){
                                            listaUsersSala.add(newUser);
                                        }
                                    }
                                }
                                if(listaUsersSala.size() > 0){
                                    listaUsersSala.get(0).setSuperAdminsList(listaNotifcationAdmins);
                                    mUserListAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void InitializeRecyclerView() {
        try {
            EmptyRecyclerView mUserList = findViewById(R.id.changeCreator_salaPrivada_recycler);
            mUserList.setNestedScrollingEnabled(false);
            mUserList.setHasFixedSize(false);
            Display display = this.getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);
            GridLayoutManager lLayout;
            float density  = getResources().getDisplayMetrics().density;
            float dpWidth  = outMetrics.widthPixels / density;
            int columns = Math.round(dpWidth/200);
            lLayout = new GridLayoutManager(this,columns);
            mUserList.setLayoutManager(lLayout);
            mUserListAdapter = new ChangeCreatorSalaPrivadaAdapter(listaUsersSala);
            mUserList.setAdapter(mUserListAdapter);
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + e.getCause().toString() + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }
}
