package com.lua.luanegra.activitys.Salas.Privadas;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lua.luanegra.R;
import com.lua.luanegra.activitys.MemesActivity;
import com.lua.luanegra.adapters.CallSalaAdapter;
import com.lua.luanegra.adapters.MediaAdapter;
import com.lua.luanegra.adapters.MessageSalaAdapter;
import com.lua.luanegra.callservice.CallService;
import com.lua.luanegra.callservice.SendNotification;
import com.lua.luanegra.objects.ChatObject;
import com.lua.luanegra.objects.MessageObject;
import com.lua.luanegra.objects.UserObject;
import com.lua.luanegra.tools.DelayedProgressDialog;
import com.lua.luanegra.tools.EmptyRecyclerView;
import com.lua.luanegra.tools.OnlineService;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SalaPrivadaActivity extends AppCompatActivity {
    private MessageSalaAdapter mChatAdapter;
    private RecyclerView.Adapter<CallSalaAdapter.ChatListViewHolder> mCallChatAdapter;
    private EmptyRecyclerView.LayoutManager mChatLayoutManager;
    private RecyclerView.Adapter<MediaAdapter.MediaViewHolder> mMediaAdapter;
    private ArrayList<MessageObject> messageList;
    private String groupUID;
    private String  currentGroupName, chatKey;
    private ArrayList<ChatObject> userCallList;
    private ArrayList<String> listaMultimedia;
    private SwitchMaterial callMake;
    private int totalMediaUploaded = 0;
    private TextInputEditText mMessage;
    private TextInputLayout layoutMessage;
    private final ArrayList<String> mediaIDList = new ArrayList<>();
    private String fileNameToSend = "";
    private final DelayedProgressDialog progressDialog = new DelayedProgressDialog();
    private Boolean isClosedView;
    private View myView;
    private SwitchMaterial mMic,mSpeaker;
    private ConstraintLayout layoutSendSMS, layout_sala_privada;
    private String cor_barraMensagens, cor_chatReciever, cor_chatSender, cor_dataHora, cor_fundo, cor_texto, cor_toolbarInferior, cor_toolbarSuperior;
    private Toolbar toolbar;

    private boolean mShouldUnbind;
    private CallService mBoundService;
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

    private final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((CallService.LocalBinder)service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
        }
    };

    private void doBindService() {
        if (bindService(new Intent(SalaPrivadaActivity.this, CallService.class),
                mConnection, Context.BIND_AUTO_CREATE)) {
            mShouldUnbind = true;
        } else {
            Log.e("LuaNegra", "Error: The requested service doesn't " +
                    "exist, or this client isn't allowed access to it.");
        }
    }

    private void doUnbindService() {
        if (mShouldUnbind) {
            unbindService(mConnection);
            mShouldUnbind = false;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        SharedPreferences prefs = this.getSharedPreferences("LN_Touch_Time", Context.MODE_PRIVATE);
        long previousTime = prefs.getLong("LN_Touch_Time", 0);
        long currentTime = new Date().getTime();
        if (currentTime - previousTime > 7*60*1000){
            menu.findItem(R.id.option_privada_enviarNotificacao).setVisible(true);
        } else {
            menu.findItem(R.id.option_privada_enviarNotificacao).setVisible(false);
            new CountDownTimer(currentTime - previousTime, 1000){
                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    menu.findItem(R.id.option_privada_enviarNotificacao).setVisible(true);
                }
            }.start();
        }
        FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            if(dataSnapshot2.exists()){
                                if(!dataSnapshot.child("creator").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    adminsList = new ArrayList<>();
                                    for(DataSnapshot childAdminsSnapShot : dataSnapshot.child("admins").getChildren()){
                                        adminsList.add(childAdminsSnapShot.getKey());
                                    }
                                    if(adminsList.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        menu.findItem(R.id.admin_option_sala_privada).setVisible(true);
                                        menu.findItem(R.id.sair_sala_BTN).setVisible(false);
                                    }else {
                                        menu.findItem(R.id.admin_option_sala_privada).setVisible(false);
                                        menu.findItem(R.id.sair_sala_BTN).setVisible(true);
                                    }
                                }
                                if(dataSnapshot2.child("favoritos").exists()){
                                    if(dataSnapshot2.child("favoritos").child(groupUID).exists()){
                                        if(dataSnapshot2.child("favoritos").child(groupUID).getValue().toString().equals("true")){
                                            menu.findItem(R.id.favoritos_salaprivada_btn).setIcon(getDrawable(R.drawable.dislike));
                                        }else {
                                            menu.findItem(R.id.favoritos_salaprivada_btn).setIcon(getDrawable(R.drawable.ic_like));
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        slideDown(myView);
        myView.setVisibility(View.GONE);
        isClosedView = false;
        return super.onPrepareOptionsMenu(menu);
    }


    ImageButton mSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sala_privada);
        try{
        callMake = findViewById(R.id.entrarCallGrupo);
        layout_sala_privada = findViewById(R.id.layout_chat_sala_privada);
        callMake.setText(R.string.ligar);
        myView = findViewById(R.id.call_menu_salaPrivada);
        isClosedView = false;
        currentGroupName = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("groupName")).toString();
        groupUID = Objects.requireNonNull(getIntent().getExtras().get("groupUid")).toString();
        chatKey = Objects.requireNonNull(getIntent().getExtras().get("key")).toString();
        progressDialog.setCancelable(false);
        userCallList = new ArrayList<>();
        mSend = findViewById(R.id.sendButton_grupoPrivada);
        layoutSendSMS = findViewById(R.id.layout_SendSMS_salasPrivada);
        mMic = findViewById(R.id.micCallGrupo);
        mSpeaker = findViewById(R.id.speakerCallGrupo);
        mSpeaker.setChecked(false);
        mMic.setChecked(true);
        mMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mMic.isChecked()){

                    mBoundService.MicOFF();
                    Snackbar.make(v, getString(R.string.micdesligado), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }else {

                    mBoundService.MicON();
                    Snackbar.make(v, getString(R.string.micligado), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
            }
        });
        mSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mSpeaker.isChecked()){
                    mBoundService.SpeakerOFF();
                    Snackbar.make(v, getString(R.string.speakerdesligado), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else {
                    mBoundService.SpeakerON();
                    Snackbar.make(v, getString(R.string.speakerligado), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
            }
        });
        callMake.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StringFormatInvalid")
            @Override
            public void onClick(View v) {
                if(!callMake.isChecked()){
                    leaveChannel();
                    callMake.setText(R.string.iniciar_call);
                    mMessage.setText(getString(R.string.saidacall) + " " + currentGroupName);
                    Drawable img = SalaPrivadaActivity.this.getDrawable(R.drawable.call);
                    Objects.requireNonNull(img).setBounds( 0, 0, 60, 60 );
                    callMake.setCompoundDrawables( img, null, null, null );
                    sendMessage();
                }else {
                    if (mSpeaker.isChecked()){
                        mSpeaker.performClick();
                    }
                    callMake.setText(R.string.terminar_call);
                    mMessage.setText(getString(R.string.entreinacall) + " " + currentGroupName);
                    Drawable img = SalaPrivadaActivity.this.getDrawable(R.drawable.end_call);
                    Objects.requireNonNull(img).setBounds( 0, 0, 60, 60 );
                    callMake.setCompoundDrawables( img, null, null, null );
                    joinChannel();
                    sendMessage();
                }
            }
        });
        listaMultimedia = new ArrayList<>();
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        InitializeMessage();
        GetAdminsList();
    }catch (Exception e){
        FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.toString());
    }
}

    ArrayList<String> adminsList;
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.salas_privadas_options, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        menu.findItem(R.id.action_search).setVisible(true);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_ACTION_SEARCH | EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        searchView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mChatAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(item.getItemId() == R.id.reportar_salaprivada_btn){
            final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(SalaPrivadaActivity.this);
            LinearLayout layout = new LinearLayout(SalaPrivadaActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);

            builder3.setIcon(getDrawable(R.drawable.luanegra_logo));
            builder3.setTitle(getString(R.string.reportarsala));
            builder3.setMessage(getString(R.string.osreportssalaprivadasaolevadosaserio));
            final EditText textoshare = new EditText(SalaPrivadaActivity.this);
            textoshare.setHint(getString(R.string.escreveaquiomotivo));

            textoshare.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textoshare.setTextSize(14);
            layout.addView(textoshare);
            builder3.setCancelable(false);
            final TextView espaco5 = new TextView(SalaPrivadaActivity.this);
            espaco5.setText(" ");
            layout.addView(espaco5);
            builder3.setView(layout);
            AlertDialog alert = null;
            builder3.setPositiveButton(getResources().getString(R.string.yap), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                if(dataSnapshot.child("reportsTemporarios").exists()){
                                    int flagteste = 0;
                                    for(DataSnapshot childReportsSanpShot : dataSnapshot.child("reportsTemporarios").getChildren()){
                                        if(childReportsSanpShot.child("creator").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            flagteste = 1;
                                            break;
                                        }
                                    }
                                    if(flagteste == 0){
                                        Calendar CalForDate = Calendar.getInstance();
                                        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                                        final String currentDate = currentDateFormat.format(CalForDate.getTime());
                                        Calendar CalForTime = Calendar.getInstance();
                                        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
                                        currentTimeFormat.toLocalizedPattern();
                                        final String currentTime = currentTimeFormat.format(CalForTime.getTime());
                                        final String key =  FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("reportsTemporarios").push().getKey();
                                        FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("reportsTemporarios").child(key).child("creator").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("reportsTemporarios").child(key).child("data").setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("reportsTemporarios").child(key).child("hora").setValue(currentTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("reportsTemporarios").child(key).child("report").setValue(textoshare.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.reportenviado), Snackbar.LENGTH_LONG)
                                                                                .setAction("Action", null).show();
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }else {
                                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.jaenviasteumreport), Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                }else{
                                    Calendar CalForDate = Calendar.getInstance();
                                    SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                                    final String currentDate = currentDateFormat.format(CalForDate.getTime());
                                    Calendar CalForTime = Calendar.getInstance();
                                    SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
                                    currentTimeFormat.toLocalizedPattern();
                                    final String currentTime = currentTimeFormat.format(CalForTime.getTime());
                                    final String key =  FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(groupUID).child("reportsTemporarios").push().getKey();
                                    FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("reportsTemporarios").child(key).child("creator").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("reportsTemporarios").child(key).child("data").setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("reportsTemporarios").child(key).child("hora").setValue(currentTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("reportsTemporarios").child(key).child("report").setValue(textoshare.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.reportenviado), Snackbar.LENGTH_LONG)
                                                                            .setAction("Action", null).show();
                                                                }
                                                            });
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
            });
            builder3.setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alert = builder3.create();
            alert.show();
        }else if(item.getItemId() == R.id.deixar_de_ser_admin_btn){
            final AlertDialog.Builder builder312 = new MaterialAlertDialogBuilder(SalaPrivadaActivity.this);
            LinearLayout layout12 = new LinearLayout(SalaPrivadaActivity.this);
            layout12.setOrientation(LinearLayout.VERTICAL);

            builder312.setIcon(getDrawable(R.drawable.luanegra_logo));
            builder312.setTitle(getString(R.string.deixardeseradministrador));
            final TextView textoshare12 = new TextView(SalaPrivadaActivity.this);
            textoshare12.setText(getString(R.string.tensacertezaquepretendesdeixardeseradminsistrador));
            textoshare12.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textoshare12.setTextSize(14);
            layout12.addView(textoshare12);
            builder312.setCancelable(false);
            final TextView espaco512 = new TextView(SalaPrivadaActivity.this);
            espaco512.setText(" ");
            layout12.addView(espaco512);
            builder312.setView(layout12);
            AlertDialog alert12;
            builder312.setPositiveButton(getResources().getString(R.string.yap), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("admins").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            item.setVisible(false);
                            Snackbar.make(findViewById(android.R.id.content), getString(R.string.janaoesadmin), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                }
            });
            builder312.setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alert12 = builder312.create();
            alert12.show();
        }else if(item.getItemId() == R.id.sair_sala_BTN){
            final AlertDialog.Builder builder31 = new MaterialAlertDialogBuilder(SalaPrivadaActivity.this);
            LinearLayout layout1 = new LinearLayout(SalaPrivadaActivity.this);
            layout1.setOrientation(LinearLayout.VERTICAL);

            builder31.setIcon(getDrawable(R.drawable.luanegra_logo));
            builder31.setTitle(getString(R.string.sairdasalaprivada));
            final TextView textoshare1 = new TextView(SalaPrivadaActivity.this);
            textoshare1.setText(getString(R.string.aosairesdestasalajanaorecebesnotificacoes));

            textoshare1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textoshare1.setTextSize(14);
            layout1.addView(textoshare1);
            builder31.setCancelable(false);
            final TextView espaco51 = new TextView(SalaPrivadaActivity.this);
            espaco51.setText(" ");
            layout1.addView(espaco51);
            builder31.setView(layout1);
            AlertDialog alert1;
            builder31.setPositiveButton(getResources().getString(R.string.yap), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            SharedPreferences preferences = getSharedPreferences(groupUID, MODE_PRIVATE);
                            preferences.edit().remove(groupUID).apply();
                            SalaPrivadaActivity.this.finish();
                        }
                    });
                }
            });
            builder31.setNeutralButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alert1 = builder31.create();
            alert1.show();
        }else if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }else if(item.getItemId() == R.id.favoritos_salaprivada_btn){
            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        if(dataSnapshot.child("favoritos").exists()){
                            if(dataSnapshot.child("favoritos").child(groupUID).exists()){
                                if(dataSnapshot.child("favoritos").child(groupUID).getValue().toString().equals("true")){
                                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("favoritos").child(groupUID).setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Snackbar.make(findViewById(android.R.id.content), getString(R.string.salaremovidadosfavoritos), Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                            item.setIcon(getDrawable(R.drawable.ic_like));

                                        }
                                    });
                                }else {
                                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("favoritos").child(groupUID).setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Snackbar.make(findViewById(android.R.id.content), getString(R.string.salaadicionadaaosfavoritos), Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                            item.setIcon(getDrawable(R.drawable.dislike));
                                        }
                                    });
                                }
                            }else {
                                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("favoritos").child(groupUID).setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Snackbar.make(findViewById(android.R.id.content),  getString(R.string.salaadicionadaaosfavoritos), Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        item.setIcon(getDrawable(R.drawable.dislike));
                                    }
                                });
                            }
                        }else {
                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("favoritos").child(groupUID).setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Snackbar.make(findViewById(android.R.id.content),  getString(R.string.salaadicionadaaosfavoritos), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    item.setIcon(getDrawable(R.drawable.dislike));
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else if(item.getItemId() == R.id.option_privada_chamadas){
            if (isClosedView) {
                slideDown(myView);
            } else {
                slideUp(myView);
            }
            isClosedView = !isClosedView;
        }else if(item.getItemId() == R.id.option_privada_defenicoes){
            Intent intent = new Intent(SalaPrivadaActivity.this, DefenicoesSalaPrivadaActivity.class);
            intent.putExtra("groupUID", groupUID);
            intent.putExtra("groupName", currentGroupName);
            startActivity(intent);
        }else if(item.getItemId() == R.id.option_publicas_enviarNotificacao){
            EnviarSigaaa();
            SharedPreferences prefs = getSharedPreferences("LN_Touch_Time", Context.MODE_PRIVATE);
            long currentTime = new Date().getTime();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("LN_Touch_Time", currentTime);
            editor.apply();
            item.setVisible(false);
        }else if(item.getItemId() == R.id.option_privada_enviarMultimedia){
            openGallery();
        }else if(item.getItemId() == R.id.option_privada_enviarMeme){
            Intent intent2 = new Intent(SalaPrivadaActivity.this, MemesActivity.class);
            final Bundle bundle2 = new Bundle();
            bundle2.putString("chatID", groupUID);
            bundle2.putString("currentUser", currentUserObject.getUid());
            bundle2.putString("activity", "salaPublica");
            intent2.putExtras(bundle2);
            startActivity(intent2);
        }
        return true;
    }


    ArrayList<String> listAdminsFull;
    private void GetAdminsList(){
        listAdminsFull = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("admins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for(DataSnapshot childAdminsSnapShot : dataSnapshot.getChildren()){
                        listAdminsFull.add(childAdminsSnapShot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void slideUp(View view){
        layoutSendSMS.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                (view.getWidth()),
                0,
                0,
                0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    private void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,
                (view.getWidth()),
                0,
                0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        layoutSendSMS.setVisibility(View.VISIBLE);
    }

    private String FCMServer, SinchKey, SinchSecret;
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

    private void EnviarSigaaa() {
        try{
            FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        final ArrayList<String> userIds = new ArrayList<>();
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                            userIds.add(Objects.requireNonNull(childSnapshot.getKey()));
                        }
                        FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                if(dataSnapshot2.exists()){
                                    for(DataSnapshot childUserSnapShot : dataSnapshot2.getChildren()){
                                        if(!Objects.requireNonNull(childUserSnapShot.getKey()).equals("chat")){
                                            for(int k = 0 ; k < userIds.size(); k++){
                                                if(childUserSnapShot.getKey().equals(userIds.get(k))){
                                                    if(!Objects.equals(childUserSnapShot.getKey(), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                                        JSONObject notification = new JSONObject();
                                                        JSONObject notifcationBody = new JSONObject();
                                                        try {
                                                            notifcationBody.put("title", currentUserObject.getName());
                                                            notifcationBody.put("message", getString(R.string.novasmssalaadmins)+ " " + currentGroupName);

                                                            notification.put("to", "/LuaNegra/" + Objects.requireNonNull(childUserSnapShot.child("notificationKey").getValue()).toString());
                                                            notification.put("data", notifcationBody);
                                                        } catch (Exception e) {
                                                            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.toString());
                                                        }
                                                        SendNotification sendNotification = new SendNotification();
                                                        sendNotification.sendNotification(notification, getApplicationContext(), FCMServer);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.notificacaoenviada), Snackbar.LENGTH_LONG)
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
    protected void onStop() {
        super.onStop();
        doUnbindService();
    }


    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    cor_barraMensagens = Objects.requireNonNull(dataSnapshot.child("coresSala").child("barraMensagens").getValue()).toString();
                    cor_chatReciever = Objects.requireNonNull(dataSnapshot.child("coresSala").child("chatReciever").getValue()).toString();
                    cor_chatSender = Objects.requireNonNull(dataSnapshot.child("coresSala").child("chatSender").getValue()).toString();
                    cor_dataHora = Objects.requireNonNull(dataSnapshot.child("coresSala").child("dataHora").getValue()).toString();
                    cor_fundo = Objects.requireNonNull(dataSnapshot.child("coresSala").child("fundo").getValue()).toString();
                    cor_texto = Objects.requireNonNull(dataSnapshot.child("coresSala").child("texto").getValue()).toString();
                    cor_toolbarInferior = Objects.requireNonNull(dataSnapshot.child("coresSala").child("toolbarInferior").getValue()).toString();
                    cor_toolbarSuperior = Objects.requireNonNull(dataSnapshot.child("coresSala").child("toolbarSuperior").getValue()).toString();
                    mMessage = findViewById(R.id.messageChat_GrupoPrivada);
                    int color = Color.parseColor(cor_barraMensagens);
                    layoutMessage = findViewById(R.id.layoutmensagem);
                    layoutMessage.setBoxStrokeColor(color);
                    mSend = findViewById(R.id.sendButton_grupoPrivada);
                    color = Color.parseColor(cor_dataHora);
                    toolbar.setSubtitleTextColor(color);
                    color = Color.parseColor(cor_texto);
                    mMessage.setTextColor(color);
                    mMessage.setHintTextColor(color);
                    callMake.setTextColor(color);
                    mMic.setTextColor(color);
                    mSpeaker.setTextColor(color);
                    color = Color.parseColor(cor_fundo);
                    myView.setBackgroundColor(color);
                    layout_sala_privada.setBackgroundColor(color);
                    color = Color.parseColor(cor_toolbarSuperior);
                    toolbar.setBackgroundColor(color);
                    callMake.setBackgroundColor(color);
                    mSpeaker.setBackgroundColor(color);
                    mMic.setBackgroundColor(color);
                    layoutSendSMS = findViewById(R.id.layout_SendSMS_salasPrivada);
                    if(dataSnapshot.child("opcoes").exists()){
                        if(dataSnapshot.child("opcoes").child("readOnly").exists()){
                            if(dataSnapshot.child("opcoes").child("readOnly").getValue().toString().equals("true")){
                                if(!dataSnapshot.child("creator").equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    layoutMessage.setVisibility(View.GONE);
                                    mSend.setVisibility(View.GONE);

                                }else {
                                    layoutMessage.setVisibility(View.VISIBLE);
                                    mSend.setVisibility(View.VISIBLE);
                                }
                            }else {
                                layoutMessage.setVisibility(View.VISIBLE);
                                mSend.setVisibility(View.VISIBLE);
                            }
                        }else {
                            layoutMessage.setVisibility(View.VISIBLE);
                            mSend.setVisibility(View.VISIBLE);
                        }
                    }else {
                        layoutMessage.setVisibility(View.VISIBLE);
                        mSend.setVisibility(View.VISIBLE);
                    }
                    GetAppKeys();
                    RetrieveUsersInCall();
                    GetCurrentUser();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        toolbar = findViewById(R.id.toolbarActivity);
        toolbar.setLogo(getDrawable(R.drawable.luanegra_logo));
        toolbar.setSubtitle("" + currentGroupName + "");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        doBindService();
        myView.setVisibility(View.GONE);
        startService(new Intent(getBaseContext(), OnlineService.class));
    }

    private ChildEventListener userInCallChildListener;
    private DatabaseReference UserInCallRef;
    private void RetrieveUsersInCall() {
        try{
            userCallList.clear();
            UserInCallRef = FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("online");
            userInCallChildListener = UserInCallRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists()) {
                        ChatObject useronlinenow = new ChatObject(dataSnapshot.getKey());
                        if(dataSnapshot.child("name").getValue() != null){
                            useronlinenow.setUserName(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                        }
                        if(dataSnapshot.child("imagemPerfilUri").getValue() != null){
                            useronlinenow.setImagemPerfilUri(Objects.requireNonNull(dataSnapshot.child("imagemPerfilUri").getValue()).toString());
                        }
                        if(Objects.requireNonNull(dataSnapshot.getKey()).equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                            callMake.setChecked(true);
                        }
                        userCallList.add(useronlinenow);
                        mCallChatAdapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists()) {
                        ChatObject useronlinenow = new ChatObject(dataSnapshot.getKey());
                        if(dataSnapshot.child("name").getValue() != null){
                            useronlinenow.setUserName(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                        }
                        if(dataSnapshot.child("imagemPerfilUri").getValue() != null){
                            useronlinenow.setImagemPerfilUri(Objects.requireNonNull(dataSnapshot.child("imagemPerfilUri").getValue()).toString());
                        }
                        for(int i = 0; i < userCallList.size(); i++){
                            if(userCallList.get(i).getChatID().equals(useronlinenow.getChatID())){
                                userCallList.get(i).setUserName(useronlinenow.getUserName());
                                if(useronlinenow.getImagemPerfilUri() != null){
                                    userCallList.get(i).setImagemPerfilUri(useronlinenow.getImagemPerfilUri());
                                }
                                mCallChatAdapter.notifyItemChanged(i);
                                break;
                            }
                        }

                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        ChatObject useronlinenow = new ChatObject(dataSnapshot.getKey());
                        for(int i = 0; i < userCallList.size(); i++){
                            if(userCallList.get(i).getChatID().equals(useronlinenow.getChatID())){
                                userCallList.remove(i);
                                mCallChatAdapter.notifyItemRemoved(i);
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private final int PICK_IMAGE_INTENT = 86;
    private ArrayList<String> mediaUriList = new ArrayList<>();

    private void openGallery() {
        try{
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.escolheacor)), PICK_IMAGE_INTENT);
            sendMessage();
        }catch (Exception e){
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                assert cursor != null;
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = Objects.requireNonNull(result).lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(resultCode == RESULT_OK){
                if(requestCode == PICK_IMAGE_INTENT){
                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                if(dataSnapshot.child("patrono").getValue().toString().equals("true")){
                                    if(Objects.requireNonNull(data).getClipData()== null){
                                        Uri fileUri = data.getData();
                                        Cursor cursor = SalaPrivadaActivity.this.getContentResolver().query(fileUri,
                                                null, null, null, null);
                                        cursor.moveToFirst();
                                        long size = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
                                        cursor.close();
                                        if(size < 104857600){
                                            mediaUriList.add(Objects.requireNonNull(data.getData()).toString());
                                            fileNameToSend = getFileName(Uri.parse(data.getData().toString())) ;
                                            mMediaAdapter.notifyDataSetChanged();
                                            sendMessage();
                                            progressDialog.show(getSupportFragmentManager(), "tag");
                                        }else {
                                            Snackbar.make(findViewById(android.R.id.content), getString(R.string.limitede25), Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                        }
                                    }else{
                                        for(int i = 0; i < Objects.requireNonNull(data.getClipData()).getItemCount(); i++){
                                            Uri fileUri = data.getClipData().getItemAt(i).getUri();
                                            Cursor cursor = SalaPrivadaActivity.this.getContentResolver().query(fileUri,
                                                    null, null, null, null);
                                            cursor.moveToFirst();
                                            long size = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
                                            cursor.close();
                                            if(size < 104857600){
                                                mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
                                                fileNameToSend = getFileName(Uri.parse(data.getClipData().getItemAt(i).getUri().toString()));
                                                mMediaAdapter.notifyDataSetChanged();
                                                sendMessage();
                                                progressDialog.show(getSupportFragmentManager(), "tag");
                                            }else {
                                                Snackbar.make(findViewById(android.R.id.content), getString(R.string.limitede25), Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                        }
                                    }
                                }else {
                                    if(!listAdminsFull.contains(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                        if(Objects.requireNonNull(data).getClipData()== null){
                                            Uri fileUri = data.getData();
                                            Cursor cursor = SalaPrivadaActivity.this.getContentResolver().query(fileUri,
                                                    null, null, null, null);
                                            cursor.moveToFirst();
                                            long size = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
                                            cursor.close();
                                            if(size < 5242880){
                                                mediaUriList.add(Objects.requireNonNull(data.getData()).toString());
                                                fileNameToSend = getFileName(Uri.parse(data.getData().toString())) ;
                                                mMediaAdapter.notifyDataSetChanged();
                                                sendMessage();
                                                progressDialog.show(getSupportFragmentManager(), "tag");
                                            }else {
                                                Snackbar.make(findViewById(android.R.id.content), getString(R.string.limitede25), Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                        }else{
                                            for(int i = 0; i < Objects.requireNonNull(data.getClipData()).getItemCount(); i++){
                                                Uri fileUri = data.getClipData().getItemAt(i).getUri();
                                                Cursor cursor = SalaPrivadaActivity.this.getContentResolver().query(fileUri,
                                                        null, null, null, null);
                                                cursor.moveToFirst();
                                                long size = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
                                                cursor.close();
                                                if(size < 5242880){
                                                    mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
                                                    fileNameToSend = getFileName(Uri.parse(data.getClipData().getItemAt(i).getUri().toString()));
                                                    mMediaAdapter.notifyDataSetChanged();
                                                    sendMessage();
                                                    progressDialog.show(getSupportFragmentManager(), "tag");
                                                }else {
                                                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.limitede25), Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                }
                                            }
                                        }

                                    }else {
                                        if(Objects.requireNonNull(data).getClipData()== null){
                                            Uri fileUri = data.getData();
                                            Cursor cursor = SalaPrivadaActivity.this.getContentResolver().query(fileUri,
                                                    null, null, null, null);
                                            cursor.moveToFirst();
                                            long size = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
                                            cursor.close();
                                            if(size < 15728640){
                                                mediaUriList.add(Objects.requireNonNull(data.getData()).toString());
                                                fileNameToSend = getFileName(Uri.parse(data.getData().toString())) ;
                                                mMediaAdapter.notifyDataSetChanged();
                                                sendMessage();
                                                progressDialog.show(getSupportFragmentManager(), "tag");
                                            }else {
                                                Snackbar.make(findViewById(android.R.id.content), getString(R.string.limitede25), Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                        }else{
                                            for(int i = 0; i < Objects.requireNonNull(data.getClipData()).getItemCount(); i++){
                                                Uri fileUri = data.getClipData().getItemAt(i).getUri();
                                                Cursor cursor = SalaPrivadaActivity.this.getContentResolver().query(fileUri,
                                                        null, null, null, null);
                                                cursor.moveToFirst();
                                                long size = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
                                                cursor.close();
                                                if(size < 15728640){
                                                    mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
                                                    fileNameToSend = getFileName(Uri.parse(data.getClipData().getItemAt(i).getUri().toString()));
                                                    mMediaAdapter.notifyDataSetChanged();
                                                    sendMessage();
                                                    progressDialog.show(getSupportFragmentManager(), "tag");
                                                }else {
                                                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.limitede25), Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                }
                                            }
                                        }
                                        mMediaAdapter.notifyDataSetChanged();
                                        sendMessage();
                                        progressDialog.show(getSupportFragmentManager(), "tag");
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
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    @Override
    protected void onPause() {
        super.onPause();
        mChatRef.removeEventListener(mChatListener);
        UserInCallRef.removeEventListener(userInCallChildListener);
        doUnbindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChatRef.removeEventListener(mChatListener);
        UserInCallRef.removeEventListener(userInCallChildListener);
        doUnbindService();
    }

    private DatabaseReference mChatRef;
    private ChildEventListener mChatListener;
    private ArrayList<UserObject> listacompletaUsers;
    private void getChatMessages() {
        try{
            messageList.clear();
            listaMultimedia.clear();
            mChatAdapter.notifyDataSetChanged();
            mChatRef = FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("mensagens");
            mChatListener = mChatRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                    if(dataSnapshot.exists()){
                        listacompletaUsers = new ArrayList<>();
                        DatabaseReference allUserRef = FirebaseDatabase.getInstance().getReference().child("user");
                        allUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                if(dataSnapshot2.exists()){
                                    for(DataSnapshot childSnapshot : dataSnapshot2.getChildren()){
                                        if(!Objects.requireNonNull(childSnapshot.getKey()).equals("chat")){
                                            UserObject userObject = new UserObject(Objects.requireNonNull(childSnapshot.child("name").getValue()).toString(), childSnapshot.getKey());
                                            userObject.setNotificationKey(Objects.requireNonNull(childSnapshot.child("notificationKey").getValue()).toString());
                                            userObject.setImagemPerfilUri(Objects.requireNonNull(childSnapshot.child("profile_image").getValue()).toString());
                                            listacompletaUsers.add(userObject);
                                        }
                                    }
                                    String text = "", creator = "", data = "", hora = "";
                                    ArrayList<String> mediaUrlList = new ArrayList<>();
                                    if (dataSnapshot.child("text").getValue() != null) {
                                        text = Decrypt(Objects.requireNonNull(dataSnapshot.child("text").getValue()).toString(), chatKey);
                                    }
                                    if (dataSnapshot.child("creator").getValue() != null) {
                                        creator = Objects.requireNonNull(dataSnapshot.child("creator").getValue()).toString();
                                    }
                                    if (dataSnapshot.child("data").getValue() != null) {
                                        data = Objects.requireNonNull(dataSnapshot.child("data").getValue()).toString();
                                    }
                                    if (dataSnapshot.child("hora").getValue() != null) {
                                        hora = Objects.requireNonNull(dataSnapshot.child("hora").getValue()).toString();
                                    }
                                    if (dataSnapshot.child("media").getChildrenCount() > 0) {

                                        for (DataSnapshot mediaSnapshot : dataSnapshot.child("media").getChildren()){
                                            mediaUrlList.add(Objects.requireNonNull(mediaSnapshot.getValue()).toString());
                                            if(!listaMultimedia.contains(Objects.requireNonNull(mediaSnapshot.getValue()).toString())){
                                                listaMultimedia.add(mediaSnapshot.getValue().toString());
                                            }
                                        }
                                    }
                                    MessageObject mMessage = new MessageObject(creator, text, mediaUrlList, data, hora);
                                    mMessage.setChatID(dataSnapshot.getKey());
                                    mMessage.setListaUsers(listacompletaUsers);
                                    mMessage.setCor_chatReciever(cor_chatReciever);
                                    mMessage.setCor_chatSender(cor_chatSender);
                                    mMessage.setCor_dataHora(cor_dataHora);
                                    mMessage.setCorTexto(cor_texto);
                                    messageList.add(mMessage);
                                    mChatLayoutManager.scrollToPosition(messageList.size() -1);
                                    mChatAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }catch (Exception e){
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private UserObject currentUserObject;
    private void GetCurrentUser(){
        try{
            DatabaseReference currentUserBDRef = FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            currentUserBDRef.keepSynced(true);
            currentUserBDRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        currentUserObject = new UserObject(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString(), dataSnapshot.getKey());
                        currentUserObject.setNotificationKey(Objects.requireNonNull(dataSnapshot.child("notificationKey").getValue()).toString());
                        currentUserObject.setImagemPerfilUri(Objects.requireNonNull(dataSnapshot.child("profile_image").getValue()).toString());
                        currentUserObject.setIsonline(Objects.requireNonNull(dataSnapshot.child("online").getValue()).toString());
                        currentUserObject.setLastOnline("Ultima vez online: \n" + Objects.requireNonNull(dataSnapshot.child("onlineDate").getValue()).toString() + "  *  " + Objects.requireNonNull(dataSnapshot.child("onlineTime").getValue()).toString());
                        getChatMessages();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(this.getClass().getName() + " - " + e.toString());
        }
    }


    private void ComparaDatas(String databd){
        Calendar CalForDate = Calendar.getInstance();

        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
        String currentDate = currentTimeFormat.format(CalForDate.getTime());

        Calendar c = Calendar.getInstance();
        try {
            c.setTime(currentTimeFormat.parse(databd));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.MINUTE, 30);
        String output = currentTimeFormat.format(c.getTime());
        Date date1 = null;
        Date date2 = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("H:mm", Locale.UK);
            date1 = sdf.parse(currentDate);
            date2 = sdf.parse(output);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (date1.compareTo(date2) > 0) {
            EnviarSigaaa();
        }
    }

    private void sendMessage() {
        try{
            Calendar CalForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
            String currentDate = currentDateFormat.format(CalForDate.getTime());
            Calendar CalForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
            currentTimeFormat.toLocalizedPattern();
            String currentTime = currentTimeFormat.format(CalForTime.getTime());
            String messageID = FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("mensagens").push().getKey();
            final DatabaseReference newMessageDB = FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("mensagens").child(Objects.requireNonNull(messageID));
            final Map<String, Object> newMessageMap = new HashMap<>();
            if(!fileNameToSend.equals("")){
                newMessageMap.put("text", Encrypt(fileNameToSend, chatKey));
            }else {
                if(!mMessage.getText().toString().isEmpty()){
                    newMessageMap.put("text", Encrypt(mMessage.getText().toString(), chatKey));
                }
            }
            newMessageMap.put("creator", currentUserObject.getUid());
            newMessageMap.put("data", currentDate);
            newMessageMap.put("hora", currentTime);
            if(messageList.isEmpty()){
                EnviarSigaaa();
            }else {
                ComparaDatas(messageList.get(messageList.size() - 1).getHora());
            }
            if(!mediaUriList.isEmpty()){
                for(String mediaUri : mediaUriList){
                    String mediaID = newMessageDB.child("media").push().getKey();
                    mediaIDList.add(mediaID);
                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Media_Salas").child(groupUID).child(messageID).child(Objects.requireNonNull(mediaID));
                    UploadTask uploadTask = filePath.putFile(Uri.parse(mediaUri));
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newMessageMap.put("/media/" + mediaIDList.get(totalMediaUploaded) + "/", uri.toString());
                                    totalMediaUploaded++;
                                    if(totalMediaUploaded == mediaUriList.size()){
                                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.ficheiroenviado), Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        updateDatabaseWtNewMessage(newMessageDB, newMessageMap);
                                        fileNameToSend = "";
                                        progressDialog.cancel();
                                    }
                                }
                            });
                        }
                    });
                }
            }
            else{
                if(!mMessage.getText().toString().isEmpty()){
                    updateDatabaseWtNewMessage(newMessageDB, newMessageMap);
                }
            }
            mMessage.setText(null);
        }catch (Exception e){
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private void updateDatabaseWtNewMessage(DatabaseReference newMessageDB, Map<String, Object> newMessageMap) {
        try{
            newMessageDB.updateChildren(newMessageMap);
            mMessage.setText(null);
            mediaUriList.clear();
            mediaIDList.clear();
            totalMediaUploaded = 0;
            mMediaAdapter.notifyDataSetChanged();
        }catch (Exception e){
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private void InitializeMessage() {
        try{
            messageList = new ArrayList<>();
            EmptyRecyclerView mChat = findViewById(R.id.messageRecyclerView_GrupoPrivada);
            mChat.setNestedScrollingEnabled(false);
            mChat.setHasFixedSize(false);
            mChatLayoutManager = new LinearLayoutManager(this, EmptyRecyclerView.VERTICAL, false);
            mMessage = findViewById(R.id.messageChat_GrupoPrivada);
            mChatAdapter = new MessageSalaAdapter(messageList);
            mChat.setLayoutManager(mChatLayoutManager);
            mChat.setAdapter(mChatAdapter);
            mChat.setItemViewCacheSize(0);
            mediaUriList = new ArrayList<>();
            mMediaAdapter = new MediaAdapter(mediaUriList);
            userCallList = new ArrayList<>();
            EmptyRecyclerView mCallChat = findViewById(R.id.messageRecyclerView_Grupo_Call);
            mCallChat.setNestedScrollingEnabled(false);
            mCallChat.setHasFixedSize(false);
            Display display = Objects.requireNonNull(this).getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);
            GridLayoutManager lLayout;
            float density  = getResources().getDisplayMetrics().density;
            float dpWidth  = outMetrics.widthPixels / density;
            int columns = Math.round(dpWidth/160);
            lLayout = new GridLayoutManager(this,columns);
            mCallChatAdapter = new CallSalaAdapter(userCallList);
            mCallChat.setLayoutManager(lLayout);
            mCallChat.setAdapter(mCallChatAdapter);
        }catch (Exception e){
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private void joinChannel() {
        try {
            startService(new Intent(this, CallService.class));
            mBoundService.StartCall(groupUID,currentGroupName, "salaPrivada", SalaPrivadaActivity.this);
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private void leaveChannel() {
        mBoundService.EndCall(groupUID, currentGroupName, "salaPrivada");
        mBoundService.stopForeground(true);
    }

    private String Encrypt(String text, String key){
        String result = " ";
        try {
            AesCbcWithIntegrity.SecretKeys keys = AesCbcWithIntegrity.keys(key);
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = AesCbcWithIntegrity.encrypt(text, keys);
            result = cipherTextIvMac.toString();
        } catch (UnsupportedEncodingException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String Decrypt(String text, String key){
        String result = " ";
        try {
            AesCbcWithIntegrity.SecretKeys keys = AesCbcWithIntegrity.keys(key);
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = new AesCbcWithIntegrity.CipherTextIvMac(text);
            result = AesCbcWithIntegrity.decryptString(cipherTextIvMac, keys);
        } catch (UnsupportedEncodingException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        return result;
    }
}
