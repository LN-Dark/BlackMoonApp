package com.lua.luanegra.activitys.Salas.Publicas;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import com.lua.luanegra.activitys.MainActivity;
import com.lua.luanegra.activitys.Salas.Privadas.ChangeSalaCreatorActivity;
import com.lua.luanegra.tools.DelayedProgressDialog;
import com.lua.luanegra.tools.OnlineService;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Objects;

public class DefenicoesMainSalaPublicaActivity extends AppCompatActivity {

    private String groupUID;
    private String  currentGroupName;
    private ImageView imagemGroup;
    private TextInputEditText nomeSala, descricaoSala;
    private final DelayedProgressDialog progressDialog = new DelayedProgressDialog();

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
        setContentView(R.layout.activity_defenicoes_main_sala_privada);
        Toolbar toolbar = findViewById(R.id.toolbarActivity);
        toolbar.setLogo(getDrawable(R.drawable.luanegra_logo));
        toolbar.setSubtitle("" + getResources().getString(R.string.defenicoesalaprivada));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        groupUID = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("groupUID")).toString();
        imagemGroup = findViewById(R.id.image_sala_change);
        nomeSala = findViewById(R.id.txt_nome_sala_change);
        descricaoSala = findViewById(R.id.txt_descricao_sala_change);
        currentGroupName = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("groupName")).toString();
        SalasPublicasRef = FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(groupUID);
        MaterialButton gravarnewInfo = findViewById(R.id.btn_gravar_info_SalaPrivadaChange);
        MaterialButton EliminarSalaPrivada = findViewById(R.id.btn_Eliminar_salaPrivada);
        MaterialButton trasnferirSala = findViewById(R.id.btn_PassarPosse_salaPrivada);
        trasnferirSala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DefenicoesMainSalaPublicaActivity.this, ChangeSalaCreatorActivity.class);
                intent.putExtra("groupUID", groupUID);
                intent.putExtra("groupName", currentGroupName);
                startActivity(intent);
            }
        });
        EliminarSalaPrivada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new MaterialAlertDialogBuilder(getApplicationContext());
                LinearLayout layout = new LinearLayout(getApplicationContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                builder.setIcon(getDrawable(R.drawable.luanegra_logo));
                builder.setTitle(getResources().getString(R.string.apagarsalaprivada));
                builder.setMessage(getResources().getString(R.string.aoeliminarsala) + "\n" + getResources().getString(R.string.tensdeteracertezaeliminarsala));
                builder.setView(layout);
                builder.setPositiveButton(getResources().getString(R.string.yap), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(groupUID).removeValue();
                        SendUserToMainActivity();
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.nempensar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });
                builder.show();
            }
        });
        gravarnewInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });
        progressDialog.setCancelable(false);
        imagemGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        final SwitchMaterial readonlybutton = findViewById(R.id.btn_readonly);
        readonlybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!readonlybutton.isChecked()){
                    FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(groupUID).child("opcoes").child("readOnly").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Snackbar.make(findViewById(android.R.id.content), getString(R.string.asalajanaoeleitura), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                }else {
                    FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(groupUID).child("opcoes").child("readOnly").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Snackbar.make(findViewById(android.R.id.content), getString(R.string.salasodeleitura), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                }
            }
        });
        FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(groupUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentGroupName = dataSnapshot.child("nome").getValue().toString();
                    profileImage = dataSnapshot.child("logo").getValue().toString();
                    descricaoSala.setText(dataSnapshot.child("descricao").getValue().toString());
                    Picasso.get().load(profileImage).into(imagemGroup);
                    nomeSala.setText(currentGroupName);
                    if(dataSnapshot.child("opcoes").exists()){
                        if(dataSnapshot.child("opcoes").child("readOnly").exists()){
                            if(dataSnapshot.child("opcoes").child("readOnly").getValue().toString().equals("true")){
                                readonlybutton.setChecked(true);
                            }else {
                                readonlybutton.setChecked(false);
                            }
                        }else {
                            readonlybutton.setChecked(false);
                        }
                    }else {
                        readonlybutton.setChecked(false);
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
        groupUID = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("groupUID")).toString();
        currentGroupName = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("groupName")).toString();
        startService(new Intent(getBaseContext(), OnlineService.class));
    }

    private void SendUserToMainActivity() {
        try{
            Intent mainIntent = new Intent(DefenicoesMainSalaPublicaActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    DatabaseReference SalasPublicasRef ;
    private void UpdateSettings() {
            try{
                final String setBIO = descricaoSala.getText().toString();
                final String setUserName = nomeSala.getText().toString();
                if (TextUtils.isEmpty(setUserName)){
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.necessarioumnick), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }else if(setUserName.length() > 20){
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.nomecom20caracteres), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else if(TextUtils.isEmpty(setBIO)){
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.enecessarioumadescricao), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else if(setBIO.length() > 150){
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.adescricaosopodeter150), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    progressDialog.show(getSupportFragmentManager(), "tag");
                    final ArrayList<String> listaNomeUtilizadores = new ArrayList<>();
                    FirebaseDatabase.getInstance().getReference().child("salasPublicas").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                for(DataSnapshot childNomesUserSnapShot : dataSnapshot.getChildren()){
                                        listaNomeUtilizadores.add(Objects.requireNonNull(childNomesUserSnapShot.child("nome").getValue()).toString());
                                }
                                if(!listaNomeUtilizadores.contains(setUserName)){
                                    if(!dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("logo").equals(profileImage)){

                                        String mediaID = SalasPublicasRef.child("logo").push().getKey();

                                        final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("SalasPublicas").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child(Objects.requireNonNull(mediaID));
                                        final UploadTask uploadTask = filePath.putFile(Uri.parse(profileImage));
                                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        SalasPublicasRef.child("logo").setValue(uri.toString());
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
                                        SalasPublicasRef.child("nome").setValue(setUserName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                SalasPublicasRef.child("descricao").setValue(setBIO).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        progressDialog.cancel();
                                                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.salaatualizadacomsucesso), Snackbar.LENGTH_LONG)
                                                                .setAction("Action", null).show();
                                                    }
                                                });
                                            }
                                        });

                                    }


                                }else if(currentGroupName.equals(setUserName)){
                                    if(!dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("logo").equals(profileImage)){

                                        String mediaID = SalasPublicasRef.child("logo").push().getKey();

                                        final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("SalasPublicas").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child(Objects.requireNonNull(mediaID));
                                        final UploadTask uploadTask = filePath.putFile(Uri.parse(profileImage));
                                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        SalasPublicasRef.child("logo").setValue(uri.toString());
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
                                        SalasPublicasRef.child("nome").setValue(setUserName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                SalasPublicasRef.child("descricao").setValue(setBIO).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        progressDialog.cancel();
                                                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.salaatualizadacomsucesso), Snackbar.LENGTH_LONG)
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
                if(resultCode == RESULT_OK) {
                    if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                        CropImage.ActivityResult result = CropImage.getActivityResult(data);
                        if (resultCode == RESULT_OK) {
                            Uri resultUri = result.getUri();
                            profileImage = resultUri.toString();
                            Picasso.get().load(profileImage).into(imagemGroup);
                        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                            Exception error = result.getError();
                        }

                    }
                }

            }
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

}
