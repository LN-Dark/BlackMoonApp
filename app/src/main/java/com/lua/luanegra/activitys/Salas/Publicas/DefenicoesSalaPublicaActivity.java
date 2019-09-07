package com.lua.luanegra.activitys.Salas.Publicas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lua.luanegra.R;
import com.lua.luanegra.callservice.SendNotification;
import com.lua.luanegra.tools.OnlineService;

import org.json.JSONObject;

import java.util.Objects;

public class DefenicoesSalaPublicaActivity extends AppCompatActivity {

    private String groupUID;
    private String  currentGroupName;


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
        setContentView(R.layout.activity_defenicoes_sala_publica);
        Toolbar toolbar = findViewById(R.id.toolbarActivity);
        toolbar.setLogo(getDrawable(R.drawable.luanegra_logo));
        toolbar.setSubtitle("" + getResources().getString(R.string.defenicoessalaprivada));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        groupUID = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("groupUID")).toString();
        currentGroupName = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("groupName")).toString();
        final MaterialButton coresBtn = findViewById(R.id.btn_cores_sala_publica);
        MaterialButton bloquearBtn = findViewById(R.id.btn_bloquearuser_sala_publica);
        final MaterialButton defCreatorSala = findViewById(R.id.btn_defCreator_sala_publica);
        final MaterialButton adminsBtn = findViewById(R.id.btn_admins_sala_publica);
        final MaterialButton btn_verificar_Sala = findViewById(R.id.btn_verificarSalasAdmit);
        btn_verificar_Sala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(getApplicationContext());
                LinearLayout layout = new LinearLayout(DefenicoesSalaPublicaActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                builder3.setIcon(getDrawable(R.drawable.luanegra_logo));
                builder3.setTitle(getString(R.string.permitirverificacaodeacesso));
                builder3.setMessage("A tua sala foi reportada 5x");
                final TextInputEditText textoshare = new TextInputEditText(DefenicoesSalaPublicaActivity.this);
                textoshare.setText(getString(R.string.devidoatersidoreportada5xsala));
                textoshare.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textoshare.setTextSize(12);
                layout.addView(textoshare);
                builder3.setCancelable(false);
                final TextInputEditText espaco5 = new TextInputEditText(DefenicoesSalaPublicaActivity.this);
                espaco5.setText(" ");
                layout.addView(espaco5);
                builder3.setView(layout);
                AlertDialog alert;
                builder3.setPositiveButton(getResources().getString(R.string.yap), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(groupUID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(groupUID).child("users").child(dataSnapshot.child("verifiRequest").child("admin").getValue().toString()).setValue(true);
                                    final JSONObject notification = new JSONObject();
                                    JSONObject notifcationBody = new JSONObject();
                                    SharedPreferences prefs = getSharedPreferences(groupUID, Context.MODE_PRIVATE);
                                    String chatKey = " ";
                                    chatKey = prefs.getString(groupUID, " ");
                                    try {
                                        notifcationBody.put("title", "LॐN_Code");
                                        notifcationBody.put("message", groupUID + "\n" + chatKey);

                                        notification.put("to", "/LuaNegra/" + dataSnapshot.child("verifiRequest").child("notifi").getValue().toString());
                                        notification.put("data", notifcationBody);
                                    } catch (Exception e) {
                                        FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.toString());
                                    }
                                    FirebaseDatabase.getInstance().getReference().child("appKeys").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                SendNotification sendNotification = new SendNotification();
                                                sendNotification.sendNotification(notification, DefenicoesSalaPublicaActivity.this, dataSnapshot.child("serverFCM").getValue().toString());
                                                Snackbar.make(findViewById(android.R.id.content), getString(R.string.chavedeacessoenviada), Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                                FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(groupUID).child("verifiRequest").child("auto").setValue("true");
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
                builder3.setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert = builder3.create();
                alert.show();
            }
        });
        FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(groupUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().equals(Objects.requireNonNull(dataSnapshot.child("creator").getValue()).toString())){
                        adminsBtn.setVisibility(View.VISIBLE);
                        adminsBtn.setMaxHeight(40);
                        coresBtn.setVisibility(View.VISIBLE);
                        coresBtn.setMaxHeight(40);
                        defCreatorSala.setVisibility(View.VISIBLE);
                        defCreatorSala.setMaxHeight(40);
                        if(dataSnapshot.child("verifiRequest").exists()){
                            if(dataSnapshot.child("verifiRequest").child("auto").getValue().toString().equals("false")){
                                btn_verificar_Sala.setVisibility(View.VISIBLE);
                            }else if(dataSnapshot.child("verifiRequest").child("auto").getValue().toString().equals("true")) {
                                btn_verificar_Sala.setVisibility(View.INVISIBLE);
                            }
                        }else {
                            btn_verificar_Sala.setVisibility(View.INVISIBLE);
                        }
                    }else {
                        btn_verificar_Sala.setVisibility(View.INVISIBLE);
                        adminsBtn.setVisibility(View.INVISIBLE);
                        adminsBtn.setMaxHeight(0);
                        coresBtn.setVisibility(View.INVISIBLE);
                        coresBtn.setMaxHeight(0);
                        defCreatorSala.setVisibility(View.INVISIBLE);
                        defCreatorSala.setMaxHeight(0);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        defCreatorSala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DefenicoesSalaPublicaActivity.this, DefenicoesMainSalaPublicaActivity.class);
                intent.putExtra("groupUID", groupUID);
                intent.putExtra("groupName", currentGroupName);
                startActivity(intent);
            }
        });
        coresBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DefenicoesSalaPublicaActivity.this, CoresSalaPublicaActivity.class);
                intent.putExtra("groupUID", groupUID);
                intent.putExtra("groupName", currentGroupName);
                startActivity(intent);
            }
        });
        adminsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DefenicoesSalaPublicaActivity.this, AdminsSalaPublicaActivity.class);
                intent.putExtra("groupUID", groupUID);
                intent.putExtra("groupName", currentGroupName);
                startActivity(intent);
            }
        });
        bloquearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DefenicoesSalaPublicaActivity.this, BloquearUserSalaPublicaActivity.class);
                intent.putExtra("groupUID", groupUID);
                intent.putExtra("groupName", currentGroupName);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        groupUID = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("groupUID")).toString();
        currentGroupName = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("groupName")).toString();
        startService(new Intent(getBaseContext(), OnlineService.class));
    }
}
