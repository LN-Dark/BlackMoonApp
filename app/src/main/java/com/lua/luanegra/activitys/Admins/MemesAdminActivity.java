package com.lua.luanegra.activitys.Admins;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.lua.luanegra.R;
import com.lua.luanegra.tools.DelayedProgressDialog;
import com.lua.luanegra.tools.OnlineService;

import java.util.Objects;

public class MemesAdminActivity extends AppCompatActivity {
    private final DelayedProgressDialog progressDialog = new DelayedProgressDialog();


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
        setContentView(R.layout.activity_memes_admin);
        Toolbar toolbar = findViewById(R.id.toolbarActivity);
        toolbar.setLogo(getDrawable(R.drawable.luanegra_logo));
        toolbar.setSubtitle("" + "Memes - Admin");
        setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            progressDialog.setCancelable(false);
        final ImageView memePreview = findViewById(R.id.img_memepreview);
        final MaterialButton adicionarMeme = findViewById(R.id.btn_adicionar_meme);
        adicionarMeme.setVisibility(View.INVISIBLE);
        final TextInputEditText linkMeme = findViewById(R.id.txt_meme_link);
            MaterialButton memeBtnPreview = findViewById(R.id.btn_preview_meme);
        memeBtnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show(getSupportFragmentManager(), "tag");
                Ion.with(memePreview).load(linkMeme.getText().toString()).setCallback(new FutureCallback<ImageView>() {
                    @Override
                    public void onCompleted(Exception e, ImageView result) {
                        adicionarMeme.setVisibility(View.VISIBLE);
                        progressDialog.cancel();
                    }
                });
               }
        });

        adicionarMeme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                progressDialog.show(getSupportFragmentManager(), "tag");
                String id =  FirebaseDatabase.getInstance().getReference().child("memes").push().getKey();
                FirebaseDatabase.getInstance().getReference().child("memes").child(Objects.requireNonNull(id)).child("uri").setValue(linkMeme.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.cancel();
                        Snackbar.make(v, getString(R.string.memeadicionadocomsucesso), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        linkMeme.setText("");
                        adicionarMeme.setVisibility(View.INVISIBLE);
                    }
                });

            }
        });

        }catch (Exception e){
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(getBaseContext(), OnlineService.class));
    }
}
