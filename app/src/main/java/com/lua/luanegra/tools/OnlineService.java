package com.lua.luanegra.tools;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class OnlineService extends Service {
    public OnlineService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        if(dataSnapshot.child("registerTime").exists()){
                            Calendar CalForDate = Calendar.getInstance();
                            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                            String currentDate = currentDateFormat.format(CalForDate.getTime());
                            Calendar CalForTime = Calendar.getInstance();
                            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
                            currentTimeFormat.toLocalizedPattern();
                            String currentTime = currentTimeFormat.format(CalForTime.getTime());
                            final HashMap<String, Object> onlineStateMap = new HashMap<>();
                            onlineStateMap.put("onlineTime", currentTime);
                            onlineStateMap.put("onlineDate", currentDate);
                            onlineStateMap.put("online", "true");
                            FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).updateChildren(onlineStateMap);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        if(dataSnapshot.child("registerTime").exists()){
                            Calendar CalForDate = Calendar.getInstance();
                            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                            String currentDate = currentDateFormat.format(CalForDate.getTime());
                            Calendar CalForTime = Calendar.getInstance();
                            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
                            currentTimeFormat.toLocalizedPattern();
                            String currentTime = currentTimeFormat.format(CalForTime.getTime());
                            final HashMap<String, Object> onlineStateMap = new HashMap<>();
                            onlineStateMap.put("onlineTime", currentTime);
                            onlineStateMap.put("onlineDate", currentDate);
                            onlineStateMap.put("online", "false");
                            FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).updateChildren(onlineStateMap);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        stopSelf();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        if(dataSnapshot.child("registerTime").exists()){
                            Calendar CalForDate = Calendar.getInstance();
                            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                            String currentDate = currentDateFormat.format(CalForDate.getTime());
                            Calendar CalForTime = Calendar.getInstance();
                            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
                            currentTimeFormat.toLocalizedPattern();
                            String currentTime = currentTimeFormat.format(CalForTime.getTime());
                            final HashMap<String, Object> onlineStateMap = new HashMap<>();
                            onlineStateMap.put("onlineTime", currentTime);
                            onlineStateMap.put("onlineDate", currentDate);
                            onlineStateMap.put("online", "false");
                            FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).updateChildren(onlineStateMap);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        stopSelf();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        if(dataSnapshot.child("registerTime").exists()){
                            Calendar CalForDate = Calendar.getInstance();
                            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                            String currentDate = currentDateFormat.format(CalForDate.getTime());
                            Calendar CalForTime = Calendar.getInstance();
                            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
                            currentTimeFormat.toLocalizedPattern();
                            String currentTime = currentTimeFormat.format(CalForTime.getTime());
                            final HashMap<String, Object> onlineStateMap = new HashMap<>();
                            onlineStateMap.put("onlineTime", currentTime);
                            onlineStateMap.put("onlineDate", currentDate);
                            onlineStateMap.put("online", "false");
                            FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).updateChildren(onlineStateMap);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        stopSelf();
    }
}
