package com.lua.luanegra.activitys.Admins;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lua.luanegra.R;
import com.lua.luanegra.callservice.SendNotification;
import com.lua.luanegra.tools.OnlineService;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class NotificationAdminActivity extends AppCompatActivity {
    private ArrayList<String> listaFrasesMotiva, listaFrasesMotiva_en;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        setContentView(R.layout.activity_notification_admin);
        Toolbar toolbar = findViewById(R.id.toolbarActivity);
        toolbar.setLogo(getDrawable(R.drawable.luanegra_logo));
        toolbar.setSubtitle("" + getString(R.string.notifiicacoesadmin));
        setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        listaFrasesMotiva = new ArrayList<>();
            listaFrasesMotiva_en = new ArrayList<>();
            MaterialButton NotifyUpdate = findViewById(R.id.notificarUpdate);
        GetAppKeys();
            MaterialButton EnviarMensagemMotivacional = findViewById(R.id.notificarMensagemMotivacional);
        EnviarMensagemMotivacional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                FirebaseDatabase.getInstance().getReference().child("frasesMotiva").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for(DataSnapshot childSnapSHot :  dataSnapshot.getChildren()){
                                listaFrasesMotiva.add("✶ " + Objects.requireNonNull(childSnapSHot.getValue()).toString() + " ✶");
                            }
                            FirebaseDatabase.getInstance().getReference().child("frasesMotiva_en").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot33) {
                                    if(dataSnapshot33.exists()){
                                        for(DataSnapshot childSnapSHot22 :  dataSnapshot33.getChildren()){
                                            listaFrasesMotiva_en.add("✶ " + Objects.requireNonNull(childSnapSHot22.getValue()).toString() + " ✶");
                                        }
                                        Random rand = new Random();
                                        final int n = rand.nextInt(listaFrasesMotiva.size());
                                        FirebaseDatabase.getInstance().getReference().child("AppText").child("fraseDia").setValue(listaFrasesMotiva.get(n));
                                        FirebaseDatabase.getInstance().getReference().child("AppText").child("fraseDia_en").setValue(listaFrasesMotiva_en.get(n));
                                        FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()){
                                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                                                        if(!Objects.equals(childSnapshot.getKey(), "chat")){
                                                            JSONObject notification = new JSONObject();
                                                            JSONObject notifcationBody = new JSONObject();
                                                            try {
                                                                notifcationBody.put("title", v.getContext().getString(R.string.motivacao));
                                                                notifcationBody.put("message", listaFrasesMotiva.get(n));

                                                                notification.put("to", "/LuaNegra/" + Objects.requireNonNull(childSnapshot.child("notificationKey").getValue()).toString());
                                                                notification.put("data", notifcationBody);
                                                            } catch (Exception e) {
                                                                FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
                                                            SendNotification sendNotification = new SendNotification();
                                                            sendNotification.sendNotification(notification, getApplicationContext(), FCMServer);
                                                            Snackbar.make(v, getString(R.string.mensagemmotivacionalenviada), Snackbar.LENGTH_LONG)
                                                                    .setAction("Action", null).show();
                                                        }
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        NotifyUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(NotificationAdminActivity.this);
                LinearLayout layout = new LinearLayout(NotificationAdminActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                builder3.setIcon(getDrawable(R.drawable.luanegra_logo));
                builder3.setTitle(getString(R.string.notificarnovoupdate));
                final EditText textoshare = new EditText(NotificationAdminActivity.this);
                textoshare.setHint(getString(R.string.changelogaqui));

                textoshare.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textoshare.setTextSize(14);
                layout.addView(textoshare);
                final TextView espaco7 = new TextView(NotificationAdminActivity.this);
                espaco7.setText(" ");
                layout.addView(espaco7);
                final EditText LinkUpdate = new EditText(NotificationAdminActivity.this);
                LinkUpdate.setHint(getString(R.string.linkdownloadaqui));
                LinkUpdate.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                LinkUpdate.setTextSize(14);
                layout.addView(LinkUpdate);
                final TextView espaco6 = new TextView(NotificationAdminActivity.this);
                espaco6.setText(" ");
                layout.addView(espaco6);
                final EditText versao = new EditText(NotificationAdminActivity.this);
                versao.setHint(getString(R.string.versaodoupdate));
                versao.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                versao.setTextSize(14);
                layout.addView(versao);
                builder3.setCancelable(false);
                final TextView espaco5 = new TextView(NotificationAdminActivity.this);
                espaco5.setText(" ");
                layout.addView(espaco5);

                final Switch checkNotifi = new Switch( NotificationAdminActivity.this);
                checkNotifi.setText(R.string.enviar_notificacao);
                checkNotifi.setGravity(Gravity.END);

                layout.addView(checkNotifi);
                final TextView espaco2 = new TextView(NotificationAdminActivity.this);
                espaco2.setText(" ");
                layout.addView(espaco2);
                builder3.setView(layout);
                AlertDialog alert;
                builder3.setPositiveButton(getString(R.string.notificar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Calendar CalForDate = Calendar.getInstance();
                        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                        String currentDate = currentDateFormat.format(CalForDate.getTime());
                        if(!textoshare.getText().toString().equals("")){
                            if(!versao.getText().toString().equals("")){
                                if(!LinkUpdate.getText().toString().equals("")){
                                    FirebaseDatabase.getInstance().getReference().child("AppText").child("version").child("changeLog").setValue(textoshare.getText().toString());
                                    FirebaseDatabase.getInstance().getReference().child("AppText").child("version").child("date").setValue(currentDate);
                                    FirebaseDatabase.getInstance().getReference().child("AppText").child("version").child("number").setValue(versao.getText().toString());
                                    FirebaseDatabase.getInstance().getReference().child("AppText").child("version").child("link").setValue(LinkUpdate.getText().toString());
                                    if(checkNotifi.isChecked()){
                                        FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()){
                                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                                                        if(!childSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                            if(!Objects.equals(childSnapshot.getKey(), "chat")){
                                                                JSONObject notification = new JSONObject();
                                                                JSONObject notifcationBody = new JSONObject();
                                                                try {
                                                                    notifcationBody.put("title", "✶ UpDate ✶ ");
                                                                    notifcationBody.put("message", v.getContext().getString(R.string.novoupdatedisponivel));
                                                                    notification.put("to", "/LuaNegra/" + Objects.requireNonNull(childSnapshot.child("notificationKey").getValue()).toString());
                                                                    notification.put("data", notifcationBody);
                                                                } catch (Exception e) {
                                                                    FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
                                                                SendNotification sendNotification = new SendNotification();
                                                                sendNotification.sendNotification(notification, getApplicationContext(), FCMServer);

                                                            }
                                                        }
                                                    }
                                                    Snackbar.make(v, getString(R.string.notificacaoactualizacao), Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        });
                                    }
                                }else {
                                    Snackbar.make(v, getString(R.string.faltalinkdownload), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            }else {
                                Snackbar.make(v, getString(R.string.faltaversaoupdate), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        }else {
                            Snackbar.make(v, getString(R.string.faltachangelog), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                });
                builder3.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alert = builder3.create();
                alert.show();

            }
        });
        final TextInputEditText text_notificacao_perso = findViewById(R.id.txt_notificacao_personalizada);
            MaterialButton enviarNotificacao_personalizada = findViewById(R.id.btn_notificacao_broadcast);
        enviarNotificacao_personalizada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String textoaenviar = text_notificacao_perso.getText().toString();
                FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                                if(!Objects.equals(childSnapshot.getKey(), "chat")){
                                    JSONObject notification = new JSONObject();
                                    JSONObject notifcationBody = new JSONObject();
                                    try {
                                        notifcationBody.put("title", R.string.notificacaobroadcast);
                                        notifcationBody.put("message", textoaenviar);

                                        notification.put("to", "/LuaNegra/" + Objects.requireNonNull(childSnapshot.child("notificationKey").getValue()).toString());
                                        notification.put("data", notifcationBody);
                                    } catch (Exception e) {
                            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
                                    SendNotification sendNotification = new SendNotification();
                                    sendNotification.sendNotification(notification, getApplicationContext(), FCMServer);
                                    Snackbar.make(v, getString(R.string.notificacaobroadcastenviada), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        }catch (Exception e){
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }
    private static String FCMServer, SinchKey, SinchSecret;
    private void GetAppKeys(){
        DatabaseReference mAppKeysRef = FirebaseDatabase.getInstance().getReference().child("appKeys");
        mAppKeysRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        switch (Objects.requireNonNull(childSnapshot.getKey())) {
                            case "serverFCM":
                                FCMServer = Objects.requireNonNull(childSnapshot.getValue()).toString();
                                break;
                            case "sinchAppKey":
                                SinchKey = Objects.requireNonNull(childSnapshot.getValue()).toString();
                                break;
                            case "sinchAppSecret":
                                SinchSecret = Objects.requireNonNull(childSnapshot.getValue()).toString();
                                break;
                        }
                    }
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
        startService(new Intent(getBaseContext(), OnlineService.class));
    }
}
