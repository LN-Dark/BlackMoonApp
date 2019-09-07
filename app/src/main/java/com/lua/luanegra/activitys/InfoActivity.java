package com.lua.luanegra.activitys;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lua.luanegra.BuildConfig;
import com.lua.luanegra.R;
import com.lua.luanegra.tools.OnlineService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class InfoActivity extends AppCompatActivity {


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
        setContentView(R.layout.activity_info);
        Toolbar toolbar = findViewById(R.id.toolbarActivity);
        toolbar.setLogo(getDrawable(R.drawable.luanegra_logo));
        toolbar.setSubtitle("" + "Info");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        MaterialButton termosbtn = findViewById(R.id.btn_termos_info);
        termosbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TermosDeUtilizacaoActivity.class));
            }
        });
        ImageView image_xda = findViewById(R.id.image_xda_logo);
        image_xda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://forum.xda-developers.com/android/apps-games/app-lua-negra-t3934027"));
                startActivity(browserIntent);
            }
        });
        ImageView image_telegram = findViewById(R.id.image_telegram_logo);
        image_telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/lua_negra"));
                startActivity(browserIntent);
            }
        });
        ImageView doarPaypal = findViewById(R.id.btn_doarPaypal);
        doarPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(InfoActivity.this);
                LinearLayout layout = new LinearLayout(InfoActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                builder3.setIcon(getDrawable(R.drawable.luanegra_logo));
                builder3.setTitle(getString(R.string.donativo));
                final TextView textoshare = new TextView(InfoActivity.this);
                textoshare.setText(getString(R.string.aofazeresodonativo));
                textoshare.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                textoshare.setTextSize(15);
                layout.addView(textoshare);
                final TextView espaco4 = new TextView(InfoActivity.this);
                espaco4.setText(" ");
                layout.addView(espaco4);
                builder3.setCancelable(false);
                builder3.setView(layout);
                AlertDialog alert = builder3.create();
                final AlertDialog finalAlert = alert;
                builder3.setPositiveButton(getString(R.string.doar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://paypal.me/pedrocruz77"));
                        startActivity(browserIntent);
                    }
                });
                builder3.setNeutralButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finalAlert.dismiss();
                    }
                });
                alert = builder3.create();
                alert.show();
            }
        });
        MaterialButton pedirfuncionalidade = findViewById(R.id.btn_pedir_funcionalidade);
        pedirfuncionalidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(InfoActivity.this);
                LinearLayout layout = new LinearLayout(InfoActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                builder3.setIcon(getDrawable(R.drawable.luanegra_logo));
                builder3.setTitle(getResources().getString(R.string.pedir_funcionalidade));
                final EditText textoshare = new EditText(InfoActivity.this);
                textoshare.setHint(getResources().getString(R.string.descrevefuncionalidade));

                textoshare.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                textoshare.setTextSize(16);
                layout.addView(textoshare);
                final TextView espaco4 = new TextView(InfoActivity.this);
                espaco4.setText(" ");
                layout.addView(espaco4);
                builder3.setCancelable(false);
                builder3.setView(layout);
                AlertDialog alert = builder3.create();
                final AlertDialog finalAlert = alert;
                builder3.setPositiveButton(getResources().getString(R.string.pedir), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, Object> funcionalidadeMap = new HashMap<>();
                        Calendar CalForDate = Calendar.getInstance();
                        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                        String currentDate = currentDateFormat.format(CalForDate.getTime());
                        Calendar CalForTime = Calendar.getInstance();
                        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
                        currentTimeFormat.toLocalizedPattern();
                        String currentTime = currentTimeFormat.format(CalForTime.getTime());

                        funcionalidadeMap.put("func", textoshare.getText().toString());
                        funcionalidadeMap.put("userID", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                        funcionalidadeMap.put("data", currentDate);
                        funcionalidadeMap.put("hora", currentTime);
                        String key = FirebaseDatabase.getInstance().getReference().child("pedidoFuncionalidades").push().getKey();
                        FirebaseDatabase.getInstance().getReference().child("pedidoFuncionalidades").child(Objects.requireNonNull(key)).updateChildren(funcionalidadeMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.pedidoenviado), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                finalAlert.dismiss();
                            }
                        });
                    }
                });
                builder3.setNeutralButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finalAlert.dismiss();
                    }
                });
                alert = builder3.create();
                alert.show();
            }
        });


        String pkgName = getApplicationContext().getPackageName();
        PackageInfo pkgInfo = null;
        PackageManager pm = getApplicationContext().getPackageManager();
        try {
            pkgInfo = pm.getPackageInfo(pkgName, 0);
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
        long ver = Objects.requireNonNull(pkgInfo).lastUpdateTime;
        final DateFormat simple = new SimpleDateFormat("dd MMM yyyy H:mm", Locale.UK);
        final Date result = new Date(ver);

        final TextView versionName = findViewById(R.id.textView13);
        FirebaseDatabase.getInstance().getReference().child("AppText").child("version").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    versionName.setText("\n ✶  ID:  "+ BuildConfig.APPLICATION_ID +"\n\n ✶   Version:  " + BuildConfig.VERSION_NAME + "  -  " + simple.format(result)+ "  -  " + BuildConfig.BUILD_TYPE + "\n\n\n\n ✶  Last UpDate:  " + dataSnapshot.child("number").getValue().toString() + "  -  " + dataSnapshot.child("date").getValue().toString() + "\n\n" + dataSnapshot.child("changeLog").getValue().toString());

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
