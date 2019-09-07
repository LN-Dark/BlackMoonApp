package com.lua.luanegra.activitys;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lua.luanegra.R;
import com.lua.luanegra.adapters.GameDownloadAdapter;
import com.lua.luanegra.objects.GameObject;
import com.lua.luanegra.tools.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class GameDownloadActivity extends AppCompatActivity {
    private EmptyRecyclerView.Adapter mGameListAdapter;
    private ArrayList<GameObject> gameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_download);
        Toolbar toolbar = findViewById(R.id.toolbarActivity);
        toolbar.setLogo(getDrawable(R.drawable.luanegra_logo));
        toolbar.setSubtitle("       ✶ Game Links ✶");
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

    private void InitializeRecyclerView() {
        try {
            EmptyRecyclerView mGameList = findViewById(R.id.recycler_gamedownload);
            mGameList.setNestedScrollingEnabled(false);
            mGameList.setHasFixedSize(false);
            Display display = GameDownloadActivity.this.getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);
            GridLayoutManager lLayout;
            float density  = getResources().getDisplayMetrics().density;
            float dpWidth  = outMetrics.widthPixels / density;
            int columns = Math.round(dpWidth/200);
            lLayout = new GridLayoutManager(GameDownloadActivity.this,columns);
            mGameList.setLayoutManager(lLayout);
            mGameListAdapter = new GameDownloadAdapter(gameList);
            mGameList.setAdapter(mGameListAdapter);
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameList = new ArrayList<>();
        InitializeRecyclerView();
        DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference().child("salas");
        gameRef.keepSynced(true);
        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot childSnapShot : dataSnapshot.getChildren()){
                        GameObject gameObject = new GameObject(Objects.requireNonNull(childSnapShot.child("nome").getValue()).toString(), Objects.requireNonNull(childSnapShot.child("linkDownload").getValue()).toString(), Objects.requireNonNull(childSnapShot.child("logouri").getValue()).toString());
                        if(!gameObject.getGame_name().equals("Admin")){
                            gameList.add(gameObject);
                        }
                    }
                    mGameListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
