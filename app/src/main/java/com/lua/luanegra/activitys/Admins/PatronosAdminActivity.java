package com.lua.luanegra.activitys.Admins;

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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lua.luanegra.R;
import com.lua.luanegra.adapters.PatronosAdminAdapter;
import com.lua.luanegra.objects.UserObject;
import com.lua.luanegra.tools.EmptyRecyclerView;
import com.lua.luanegra.tools.OnlineService;

import java.util.ArrayList;
import java.util.Objects;

public class PatronosAdminActivity extends AppCompatActivity {
    private RecyclerView.Adapter mUserListAdapter;
    private ArrayList<UserObject> userList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patronos_admin);
        Toolbar toolbar = findViewById(R.id.toolbarActivity);
        toolbar.setLogo(getDrawable(R.drawable.luanegra_logo));
        toolbar.setSubtitle("" + "Patronos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return true;
    }

    private void getSuperAdmins() {
        FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                if (dataSnapshot2.exists()) {
                    for (DataSnapshot childSnapShot2 : dataSnapshot2.getChildren()) {
                        if (!Objects.requireNonNull(childSnapShot2.getKey()).equals("chat")) {
                            UserObject userObject = new UserObject(Objects.requireNonNull(childSnapShot2.child("name").getValue()).toString(), childSnapShot2.getKey());
                            userObject.setImagemPerfilUri(Objects.requireNonNull(childSnapShot2.child("profile_image").getValue()).toString());
                            userObject.setNotificationKey(Objects.requireNonNull(childSnapShot2.child("notificationKey").getValue()).toString());
                            userObject.setName(Objects.requireNonNull(childSnapShot2.child("name").getValue()).toString());
                            if (!userObject.getUid().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                                    userList.add(userObject);
                            }
                        }
                    }
                    mUserListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        userList = new ArrayList<>();
        InitializeRecyclerView();
        getSuperAdmins();
        startService(new Intent(getBaseContext(), OnlineService.class));
    }

    private void InitializeRecyclerView() {
        try {
            EmptyRecyclerView mUserList = findViewById(R.id.recycler_patronos);
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
            mUserListAdapter = new PatronosAdminAdapter(userList);
            mUserList.setAdapter(mUserListAdapter);
            mUserList.setItemViewCacheSize(0);
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }
    }
}
