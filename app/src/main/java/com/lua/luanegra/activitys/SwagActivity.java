package com.lua.luanegra.activitys;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lua.luanegra.R;
import com.lua.luanegra.tools.DelayedProgressDialog;
import com.lua.luanegra.tools.OnlineService;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SwagActivity extends AppCompatActivity {

    private MaterialButton UpdateAccountSetting, BackupKeysBTN, inportKeysBTN;
    private TextInputEditText userName, Bio;
    private CircleImageView userProfileImage;
    private DatabaseReference UserRef;
    private static final int GalleryPick = 99;
    private String notificationKeyCurrntUser;
    private String currentUserName;
    private SwitchMaterial confirmarExit, ativarNotifi, temaselect;
    private final DelayedProgressDialog progressDialog = new DelayedProgressDialog();
    public static int PICK_FILE = 1;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        setContentView(R.layout.activity_swag);
        Toolbar toolbar = findViewById(R.id.toolbarActivity);
        toolbar.setLogo(getDrawable(R.drawable.luanegra_logo));
        toolbar.setSubtitle("" + getResources().getString(R.string.perfilsubmenu));
        setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        progressDialog.setCancelable(false);
        UserRef = FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            DatabaseReference allUserRef = FirebaseDatabase.getInstance().getReference();
        InicializeFields();
        BackupKeysBTN = findViewById(R.id.btn_swag_backupKeys);
        inportKeysBTN = findViewById(R.id.btn_swag_inportKeys);
        confirmarExit = findViewById(R.id.btn_confirmar_sair);
        ativarNotifi = findViewById(R.id.btn_desligar_notificacoes);
        temaselect = findViewById(R.id.btn_swag_tema);
        BackupKeysBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackupKeys();
            }
        });
        inportKeysBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/plain");
                startActivityForResult(intent, PICK_FILE);
            }
        });
        temaselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!temaselect.isChecked()){
                    SharedPreferences prefs = getSharedPreferences("AppTheme", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("AppTheme", "light");
                    editor.apply();
                    final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(SwagActivity.this);
                    LinearLayout layout = new LinearLayout(SwagActivity.this);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    builder3.setIcon(getDrawable(R.drawable.luanegra_logo));
                    builder3.setTitle(getResources().getString(R.string.temabrancoativo));
                    builder3.setCancelable(false);
                    final TextView espaco5 = new TextView(SwagActivity.this);
                    espaco5.setText(" ");
                    layout.addView(espaco5);
                    builder3.setView(layout);
                    AlertDialog alert = builder3.create();
                    final AlertDialog finalAlert = alert;
                    builder3.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            Intent mStartActivity = new Intent(getApplicationContext(), MainActivity.class);
                            int mPendingIntentId = 123456;
                            PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity,
                                    PendingIntent.FLAG_CANCEL_CURRENT);
                            AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                            System.exit(0);
                        }
                    });
                    alert = builder3.create();
                    alert.show();
                }else {
                    SharedPreferences prefs = getSharedPreferences("AppTheme", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("AppTheme", "dark");
                    editor.apply();
                    final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(SwagActivity.this);
                    LinearLayout layout = new LinearLayout(SwagActivity.this);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    builder3.setIcon(getDrawable(R.drawable.luanegra_logo));
                    builder3.setTitle(getResources().getString(R.string.temapretoativo));
                    builder3.setCancelable(false);
                    final TextView espaco5 = new TextView(SwagActivity.this);
                    espaco5.setText(" ");
                    layout.addView(espaco5);
                    builder3.setView(layout);
                    AlertDialog alert = builder3.create();
                    final AlertDialog finalAlert = alert;
                    builder3.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent mStartActivity = new Intent(getApplicationContext(), MainActivity.class);
                            int mPendingIntentId = 123456;
                            PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity,
                                    PendingIntent.FLAG_CANCEL_CURRENT);
                            AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                            System.exit(0);
                        }
                    });
                    alert = builder3.create();
                    alert.show();
                }
            }
        });
        ativarNotifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ativarNotifi.isChecked()){
                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("opcoes").child("notificacoes").setValue(false);
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.notificacoesdesativas), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else {
                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("opcoes").child("notificacoes").setValue(true);
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.notificacoesativas), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        confirmarExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!confirmarExit.isChecked()){
                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("opcoes").child("exitConf").setValue(false);
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.confirmacaosairappdesativada), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else {
                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("opcoes").child("exitConf").setValue(true);
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.confirmacaosairappativada), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
            MaterialButton deleteAccountBTN = findViewById(R.id.btn_swag_eliminar_conta);
        deleteAccountBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(SwagActivity.this);
                LinearLayout layout = new LinearLayout(SwagActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                builder3.setIcon(getDrawable(R.drawable.luanegra_logo));
                builder3.setTitle(getResources().getString(R.string.apagrconta));
                builder3.setMessage(getString(R.string.tensqueteracerteza));
                final TextView textoshare = new TextView(SwagActivity.this);
                textoshare.setText(getResources().getString(R.string.todaainformacaoseraapagada));
                textoshare.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textoshare.setTextSize(16);
                layout.addView(textoshare);
                builder3.setCancelable(false);
                final TextView espaco5 = new TextView(SwagActivity.this);
                espaco5.setText(" ");
                layout.addView(espaco5);
                builder3.setView(layout);
                AlertDialog alert = builder3.create();
                final AlertDialog finalAlert = alert;
                builder3.setPositiveButton(getString(R.string.apagar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("salasPrivadas").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    for(DataSnapshot childSalasPrivadasSnapShot : dataSnapshot.getChildren()){
                                        if(childSalasPrivadasSnapShot.child("creator").equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(childSalasPrivadasSnapShot.getKey()).removeValue();
                                        }
                                    }
                                    FirebaseDatabase.getInstance().getReference().child("salasPublicas").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                for(DataSnapshot childSalasPrivadasSnapShot : dataSnapshot.getChildren()){
                                                    if(childSalasPrivadasSnapShot.child("creator").equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                        FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(childSalasPrivadasSnapShot.getKey()).removeValue();
                                                    }
                                                }
                                                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                SendUserToMainActivity();
                                                            }
                                                        });
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
        UpdateAccountSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });
        RetrieveUserInfo();
        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
            SharedPreferences prefs = getSharedPreferences("AppTheme", Context.MODE_PRIVATE);
            String tema = " ";
            tema = prefs.getString("AppTheme", " ");
            if(tema != " "){
                if(tema.equals("light")){
                    temaselect.setChecked(false);
                }else {
                    temaselect.setChecked(true);
                }
            }else {
                temaselect.setChecked(false);
            }
        }catch (Exception e){
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private void SendUserToMainActivity() {
        try {

            Intent settingsIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(settingsIntent);
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private void InicializeFields() {
        try{
        UpdateAccountSetting = findViewById(R.id.update_settings_button);
        userName = findViewById(R.id.set_user_name);
      userProfileImage = findViewById(R.id.set_profile_image);
            Bio = findViewById(R.id.set_user_BIO);
        }catch (Exception e){
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private void UpdateSettings() {
        try{
            final String setBIO = Bio.getText().toString();
        final String setUserName = userName.getText().toString();
        if (TextUtils.isEmpty(setUserName)){
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.necessarioumnick), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        }else if(setUserName.length() > 20){
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.nomecom20caracteres), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else if(TextUtils.isEmpty(setBIO)){
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.enecessarioumaio), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else if(setBIO.length() > 150){
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.abiosopodeter150), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            progressDialog.show(getSupportFragmentManager(), "tag");
            final ArrayList<String> listaNomeUtilizadores = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot childNomesUserSnapShot : dataSnapshot.getChildren()){
                            if(!Objects.requireNonNull(childNomesUserSnapShot.getKey()).equals("chat")){
                                listaNomeUtilizadores.add(Objects.requireNonNull(childNomesUserSnapShot.child("name").getValue()).toString());
                            }
                        }
                        if(!listaNomeUtilizadores.contains(setUserName)){
                            if(!dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profile_image").equals(profileImage)){

                                String mediaID = UserRef.child("profile_image").push().getKey();

                                final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Profile_Images").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child(Objects.requireNonNull(mediaID));
                                final UploadTask uploadTask = filePath.putFile(Uri.parse(profileImage));
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                UserRef.child("profile_image").setValue(uri.toString());
                                                progressDialog.cancel();
                                            }
                                        });
                                        uploadTask.addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.cancel();
                                                Snackbar.make(findViewById(android.R.id.content), "Erro - " + e.getMessage(), Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                        });
                                    }
                                });
                                UserRef.child("name").setValue(setUserName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        UserRef.child("bio").setValue(setBIO).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressDialog.cancel();
                                                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.perfilatualizado), Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                        });
                                    }
                                });

                            }


                        }else if(currentUserName.equals(setUserName)){
                            if(!dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profile_image").equals(profileImage)){

                                String mediaID = UserRef.child("profile_image").push().getKey();

                                final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Profile_Images").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child(Objects.requireNonNull(mediaID));
                                final UploadTask uploadTask = filePath.putFile(Uri.parse(profileImage));
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                UserRef.child("profile_image").setValue(uri.toString());
                                                progressDialog.cancel();
                                            }
                                        });
                                        uploadTask.addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.cancel();
                                                Snackbar.make(findViewById(android.R.id.content), "Erro - " + e.getMessage(), Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                        });
                                    }
                                });
                                UserRef.child("name").setValue(setUserName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        UserRef.child("bio").setValue(setBIO).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressDialog.cancel();
                                                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.perfilatualizado), Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                        });
                                    }
                                });

                            }


                        }else {
                            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.nomemeutilizacao), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            progressDialog.cancel();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }
        }catch (Exception e){
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private final int PICK_IMAGE_INTENT = 156;
    private String profileImage = "";

    private void openGallery() {
        try{
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMaxCropResultSize(500,500)
                    .start(this);
        }catch (Exception e){
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
        if(resultCode == RESULT_OK){
            if(requestCode == PICK_IMAGE_INTENT){
                if(Objects.requireNonNull(data).getClipData()== null){
                    profileImage = Objects.requireNonNull(data.getData()).toString();
                    Picasso.get().load(profileImage).into(userProfileImage);
                }
                else{
                    profileImage = Objects.requireNonNull(data.getData()).toString();
                    Picasso.get().load(profileImage).into(userProfileImage);
                }
            }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    profileImage = resultUri.toString();
                    Picasso.get().load(profileImage).into(userProfileImage);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }else if (requestCode == PICK_FILE) {
                Uri uri = data.getData();
                String fileContent = readTextFile(uri);
                if(fileContent.contains("ॐ")){
                   if(fileContent.contains("✶")){
                       String[] filecontentarray = fileContent.split("✶");
                       try {
                           for (String s : filecontentarray) {
                               String[] arrayIDKEY = s.split("ॐ");
                               SharedPreferences prefs = getApplicationContext().getSharedPreferences(arrayIDKEY[0], Context.MODE_PRIVATE);
                               SharedPreferences.Editor editor = prefs.edit();
                               editor.putString(arrayIDKEY[0], arrayIDKEY[1]);
                               editor.apply();
                           }
                           Snackbar.make(findViewById(android.R.id.content), getString(R.string.ficheirocarregadocoomsucesso), Snackbar.LENGTH_LONG)
                                   .setAction("Action", null).show();
                       }catch (Exception e){
                           Snackbar.make(findViewById(android.R.id.content), getString(R.string.ficheiroinvalido), Snackbar.LENGTH_LONG)
                                   .setAction("Action", null).show();
                       }
                   }else {
                       Snackbar.make(findViewById(android.R.id.content), getString(R.string.ficheiroinvalido), Snackbar.LENGTH_LONG)
                               .setAction("Action", null).show();
                   }
                }else {
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.ficheiroinvalido), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

        }
        }catch (Exception e){
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private String readTextFile(Uri uri){
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));
            String line = "";

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }


    ArrayList<String> listaSalasPrivadas, listaKeys;
    private void BackupKeys(){
        listaSalasPrivadas = new ArrayList<>();
        listaKeys = new ArrayList<>();
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(SwagActivity.this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        builder.setIcon(getDrawable(R.drawable.luanegra_logo));
        builder.setTitle(getString(R.string.backupchavessalaprivada));
        layout.setGravity(Gravity.CENTER);
        final TextView espaco2 = new TextView(this);
        espaco2.setText(getString(R.string.secriastesalasprivadas));
        espaco2.setTextSize(16);
        espaco2.setGravity(Gravity.CENTER);
        layout.addView(espaco2);
        builder.setView(layout);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseDatabase.getInstance().getReference().child("salasPrivadas").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for(DataSnapshot childSalasPrivadasSnapShot : dataSnapshot.getChildren()){
                                if(childSalasPrivadasSnapShot.child("creator").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    listaSalasPrivadas.add(childSalasPrivadasSnapShot.getKey());
                                }
                            }
                            if(!listaSalasPrivadas.isEmpty()){
                                for(int h = 0 ; h < listaSalasPrivadas.size(); h++){
                                    SharedPreferences prefs = getSharedPreferences(listaSalasPrivadas.get(h), Context.MODE_PRIVATE);
                                    String chatKey = " ";
                                    chatKey = prefs.getString(listaSalasPrivadas.get(h), " ");
                                    if(!chatKey.equals(" ")){
                                        listaKeys.add(listaSalasPrivadas.get(h) + "ॐ" + chatKey);
                                    }
                                    if(!listaKeys.isEmpty()){
                                        String filetoSaveString = "";
                                        for(int j = 0 ; j < listaKeys.size(); j++){
                                            filetoSaveString = filetoSaveString + listaKeys.get(j) + "✶";
                                        }
                                        String filename = "LuaNegra_Keys.txt";

                                        File file;
                                        FileOutputStream outputStream;
                                        try {
                                            file = new File(Environment.getExternalStoragePublicDirectory(
                                                    Environment.DIRECTORY_DOWNLOADS),filename);
                                            outputStream = new FileOutputStream(file, true);
                                            outputStream.write(filetoSaveString.getBytes());
                                            outputStream.close();

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.ficheirobackupchavescriado), Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
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
        builder.setNeutralButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();

    }

    private void RetrieveUserInfo() {
        try{
        UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("profile_image")))){
                            final SwitchMaterial sociavelBtn = findViewById(R.id.btn_swag_novas_conversas);
                            sociavelBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(!sociavelBtn.isChecked()){
                                        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("opcoes").child("sociavel").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Snackbar.make(findViewById(android.R.id.content), getString(R.string.janaoessociavel), Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                        });
                                    }else {
                                        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("opcoes").child("sociavel").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Snackbar.make(findViewById(android.R.id.content), getString(R.string.voltasteasersociavel), Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                        });
                                    }
                                }
                            });
                            if(dataSnapshot.child("opcoes").exists()){
                                if(dataSnapshot.child("opcoes").child("sociavel").exists()){
                                    if(dataSnapshot.child("opcoes").child("sociavel").getValue().toString().equals("true")){
                                        sociavelBtn.setChecked(true);
                                    }else {
                                        sociavelBtn.setChecked(false);
                                    }
                                }else {
                                    sociavelBtn.setChecked(true);
                                }
                            }else {
                                sociavelBtn.setChecked(true);
                            }
                            String retrieveUserName = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                            String retrieveProfileImage = Objects.requireNonNull(dataSnapshot.child("profile_image").getValue()).toString();
                            userName.setText(retrieveUserName);
                            currentUserName = retrieveUserName;
                            notificationKeyCurrntUser = Objects.requireNonNull(dataSnapshot.child("notificationKey").getValue()).toString();
                            profileImage = retrieveProfileImage;
                            Picasso.get().load(retrieveProfileImage).into(userProfileImage);
                            if(dataSnapshot.child("opcoes").child("exitConf").getValue().toString().equals("true")){
                                confirmarExit.setChecked(true);
                            }else {
                                confirmarExit.setChecked(false);
                            }
                            if(dataSnapshot.child("opcoes").child("notificacoes").getValue().toString().equals("true")){
                                ativarNotifi.setChecked(true);
                            }else if(dataSnapshot.child("opcoes").child("notificacoes").getValue().toString().equals("false")) {
                                ativarNotifi.setChecked(false);
                            }
                            Bio.setText(dataSnapshot.child("bio").getValue().toString());
                        }
                        else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                            String retrieveUserName = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                            userName.setText(retrieveUserName);
                        }
                        else{
                            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.atualizaoperfil), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        }catch (Exception e){
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(getBaseContext(), OnlineService.class));
    }
}
