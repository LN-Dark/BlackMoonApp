package com.lua.luanegra.callservice;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.lua.luanegra.R;
import com.lua.luanegra.activitys.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private final String ADMIN_CHANNEL_ID ="admin_channel";



    @Override
    public void onNewToken(String s) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("notificationKey").setValue(s);
            FirebaseMessaging.getInstance().subscribeToTopic("LuaNegra");

            loadAdmins(s);
            loadSuperAdmins(s);
        }
        super.onNewToken(s);
    }

    private ArrayList<String> listaAdmins;
    private ArrayList<String> listaAdminsNotificationToken;
    private void loadAdmins(final String s) {
        try {
            listaAdmins = new ArrayList<>();
            listaAdminsNotificationToken = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("admins").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                            listaAdmins.add(childSnapshot.getKey());
                            listaAdminsNotificationToken.add(Objects.requireNonNull(childSnapshot.getValue()).toString());
                        }
                        if (listaAdmins.contains(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                            FirebaseDatabase.getInstance().getReference().child("admins").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(s);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
        FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.toString());
        }
    }

    private ArrayList<String> listaSuperAdmins;
    private ArrayList<String> listaSuperAdminsNotificationToken;
    private void loadSuperAdmins(final String s) {
        try {
            listaSuperAdmins = new ArrayList<>();
            listaSuperAdminsNotificationToken = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("superAdmin").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                            listaSuperAdmins.add(childSnapshot.getKey());
                            listaSuperAdminsNotificationToken.add(Objects.requireNonNull(childSnapshot.getValue()).toString());
                        }
                        if (listaSuperAdmins.contains(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                            FirebaseDatabase.getInstance().getReference().child("superAdmin").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(s);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.toString());
        }
    }

    @Override
    public void onMessageReceived(final RemoteMessage message) {
        super.onMessageReceived(message);
        final Intent intent = new Intent(this, MainActivity.class);
        final NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        final int notificationID = new Random().nextInt(3000);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels(notificationManager);
        }
        Calendar CalForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
        final String currentDate = currentDateFormat.format(CalForDate.getTime());
        Calendar CalForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
        currentTimeFormat.toLocalizedPattern();
        final String currentTime = currentTimeFormat.format(CalForTime.getTime());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this , 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.luanegra_logo);

        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.luanegra_logo)
                .setLargeIcon(largeIcon)
                .setContentTitle(message.getData().get("title"))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message.getData().get("message")))
                .setContentText(message.getData().get("message"))
                .setAutoCancel(true)
                .setColorized(true)
                .setColor(getColor(R.color.colorPrimary))
                .setSound(notificationSoundUri)
                .setContentIntent(pendingIntent);
        notificationBuilder.setColor(Color.TRANSPARENT);
        if(Objects.equals(message.getData().get("title"), "LॐN_Code")){
            ArrayList<String> messageDe = new ArrayList<>();
            messageDe.add(Objects.requireNonNull(message.getData().get("message")).substring(0, Objects.requireNonNull(message.getData().get("message")).lastIndexOf("\n")));
            messageDe.add(Objects.requireNonNull(message.getData().get("message")).substring(Objects.requireNonNull(message.getData().get("message")).lastIndexOf("\n") +1));
            SharedPreferences prefs = getSharedPreferences(messageDe.get(0), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(messageDe.get(0), messageDe.get(1));
            editor.apply();
        }else if(Objects.equals(message.getData().get("title"), "Motivação")) {
            if (!Locale.getDefault().getCountry().equals("PT")) {
                notificationBuilder.setContentTitle("Motivation");
                FirebaseDatabase.getInstance().getReference().child("AppText").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(dataSnapshot.child("fraseDia_en").getValue().toString()));
                            notificationBuilder.setContentText(dataSnapshot.child("fraseDia_en").getValue().toString());
                            FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("opcoes").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        if (Objects.requireNonNull(dataSnapshot.child("notificacoes").getValue()).toString().equals("true")) {
                                            notificationManager.notify(notificationID, notificationBuilder.build());
                                            final String keyNotificacao = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").push().getKey();
                                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("titulo").setValue("Motivation").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("data").setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("hora").setValue(currentTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("mensagem").setValue(dataSnapshot.child("fraseDia_en").getValue().toString());
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                        }else {
                                            final String keyNotificacao = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").push().getKey();
                                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("titulo").setValue("Motivation").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("data").setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("hora").setValue(currentTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("mensagem").setValue(dataSnapshot.child("fraseDia_en").getValue().toString());
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
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
            }else {
                FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("opcoes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (Objects.requireNonNull(dataSnapshot.child("notificacoes").getValue()).toString().equals("true")) {
                                notificationManager.notify(notificationID, notificationBuilder.build());
                                final String keyNotificacao = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").push().getKey();
                                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("titulo").setValue("Motivação").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("data").setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("hora").setValue(currentTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("mensagem").setValue(message.getData().get("message"));
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }else {
                                final String keyNotificacao = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").push().getKey();
                                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("titulo").setValue("Motivação").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("data").setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("hora").setValue(currentTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("mensagem").setValue(message.getData().get("message"));
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }else if(Objects.equals(message.getData().get("title"), "Motivation")){
                if(!Locale.getDefault().getCountry().equals("PT") ){

                    FirebaseDatabase.getInstance().getReference().child("AppText").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(dataSnapshot.child("fraseDia_en").getValue().toString()));
                                notificationBuilder.setContentText(dataSnapshot.child("fraseDia_en").getValue().toString());
                                FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("opcoes").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot22) {
                                        if(dataSnapshot22.exists()){
                                            if(Objects.requireNonNull(dataSnapshot22.child("notificacoes").getValue()).toString().equals("true")){
                                                notificationManager.notify(notificationID, notificationBuilder.build());
                                                final String keyNotificacao = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").push().getKey();
                                                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("titulo").setValue("Motivation").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("data").setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("hora").setValue(currentTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("mensagem").setValue(dataSnapshot.child("fraseDia_en").getValue().toString());
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                            }else{
                                                final String keyNotificacao = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").push().getKey();
                                                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("titulo").setValue("Motivation").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("data").setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("hora").setValue(currentTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("mensagem").setValue(dataSnapshot.child("fraseDia_en").getValue().toString());
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
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
                }else {
                    notificationBuilder.setContentTitle("Motivação");
                    FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("opcoes").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot22) {
                            if(dataSnapshot22.exists()){
                                if(Objects.requireNonNull(dataSnapshot22.child("notificacoes").getValue()).toString().equals("true")){
                                    notificationManager.notify(notificationID, notificationBuilder.build());
                                    final String keyNotificacao = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").push().getKey();
                                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("titulo").setValue("Motivação").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("data").setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("hora").setValue(currentTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("mensagem").setValue(message.getData().get("message"));
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }else{
                                    final String keyNotificacao = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").push().getKey();
                                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("titulo").setValue("Motivação").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("data").setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("hora").setValue(currentTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("mensagem").setValue(message.getData().get("message"));
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

        }else {
            FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("opcoes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot22) {
                    if(dataSnapshot22.exists()){
                        if(Objects.requireNonNull(dataSnapshot22.child("notificacoes").getValue()).toString().equals("true")){
                            notificationManager.notify(notificationID, notificationBuilder.build());
                            final String keyNotificacao = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").push().getKey();
                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("titulo").setValue(message.getData().get("title")).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("data").setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("hora").setValue(currentTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(keyNotificacao).child("mensagem").setValue(message.getData().get("message"));
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(NotificationManager notificationManager){
        CharSequence adminChannelName = "Nova Notificação";
        String adminChannelDescription = "FCM_CHANNEL";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(getColor(R.color.colorAccent));
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }
}