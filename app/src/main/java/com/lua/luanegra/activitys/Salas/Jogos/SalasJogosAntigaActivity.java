package com.lua.luanegra.activitys.Salas.Jogos;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.OpenableColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
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

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SalasJogosAntigaActivity extends AppCompatActivity {
    private RecyclerView.Adapter<MessageSalaAdapter.MessageViewHolder> mChatAdapter;
    private RecyclerView.Adapter<CallSalaAdapter.ChatListViewHolder> mCallChatAdapter;
    private EmptyRecyclerView.LayoutManager mChatLayoutManager;
    private RecyclerView.Adapter<MediaAdapter.MediaViewHolder> mMediaAdapter;
    private  ArrayList<MessageObject> messageList;
    private String groupUID;
    private String  currentGroupName;
    private ArrayList<ChatObject> userCallList;
    private ArrayList<String> listaMultimedia;
    private SwitchMaterial callMake;
    private int totalMediaUploaded = 0;
    private TextInputEditText mMessage;
    private final ArrayList<String> mediaIDList = new ArrayList<>();
    private String fileNameToSend = "";
    private ImageButton touch_Chat, mSendMedia;
    private final DelayedProgressDialog progressDialog = new DelayedProgressDialog();
    private Boolean isClosedView;
    private View myView;
    private SwitchMaterial mMic,mSpeaker;
    private ConstraintLayout layoutSendSMS;

    private boolean mShouldUnbind;
    private CallService mBoundService;


    private final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((CallService.LocalBinder)service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
        }
    };

    private void doBindService() {
        if (bindService(new Intent(SalasJogosAntigaActivity.this, CallService.class),
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
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        try{
        setContentView(R.layout.activity_salas_chat);
        touch_Chat = findViewById(R.id.btn_touch_chatGrupo);
        callMake = findViewById(R.id.entrarCallGrupo);
        callMake.setText(R.string.ligar);
        myView = findViewById(R.id.call_menu_sala);
        isClosedView = true;
        currentGroupName = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("groupName")).toString();
        groupUID = Objects.requireNonNull(getIntent().getExtras().get("groupUid")).toString();
        progressDialog.setCancelable(false);
            Toolbar toolbar = findViewById(R.id.toolbarActivity);
            toolbar.setLogo(getDrawable(R.drawable.luanegra_logo));
            toolbar.setSubtitle("                " + currentGroupName + " ");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            ImageButton makeView = findViewById(R.id.btn_call_make_sala);
            makeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClosedView) {
                        slideDown(myView);
                    } else {
                        slideUp(myView);
                    }
                    isClosedView = !isClosedView;
                }
            });
        userCallList = new ArrayList<>();
            ImageButton memesbtn = findViewById(R.id.btn_memes_groupChat);
        memesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MemesActivity.class);
                final Bundle bundle = new Bundle();
                bundle.putString("chatID", groupUID);
                bundle.putString("currentUser", currentUserObject.getUid());
                bundle.putString("chatKey", " ");
                bundle.putString("activity", "groupChat");
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        });
            ImageButton mSend = findViewById(R.id.sendButton_grupo);
         mSendMedia = findViewById(R.id.sendMedia_grupo);
            SharedPreferences prefs = this.getSharedPreferences("LN_Touch_Time", Context.MODE_PRIVATE);
            long previousTime = prefs.getLong("LN_Touch_Time", 0);
            long currentTime = new Date().getTime();
            if (currentTime - previousTime > 15*60*1000){
                touch_Chat.setVisibility(View.VISIBLE);
            } else {
                touch_Chat.setVisibility(View.INVISIBLE);
                new CountDownTimer(currentTime - previousTime, 1000){
                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        touch_Chat.setVisibility(View.VISIBLE);
                    }
                }.start();
            }
        touch_Chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                EnviarSigaaa();
                SharedPreferences prefs = getSharedPreferences("LN_Touch_Time", Context.MODE_PRIVATE);
                long currentTime = new Date().getTime();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("LN_Touch_Time", currentTime);
                editor.apply();
                touch_Chat.setVisibility(View.INVISIBLE);
            }
        });
            layoutSendSMS = findViewById(R.id.layout_SendSMS_salas);
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
                    Drawable img = SalasJogosAntigaActivity.this.getDrawable(R.drawable.call);
                    Objects.requireNonNull(img).setBounds( 0, 0, 60, 60 );
                    callMake.setCompoundDrawables( img, null, null, null );
                    sendMessage();
                }else {
                    if (mSpeaker.isChecked()){
                        mSpeaker.performClick();
                    }
                    callMake.setText(R.string.terminar_call);
                    mMessage.setText(getString(R.string.entreinacall) + " " + currentGroupName);
                    Drawable img = SalasJogosAntigaActivity.this.getDrawable(R.drawable.end_call);
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
        makeView.performClick();
        myView.setVisibility(View.GONE);
        GetAdminsList();
        }catch (Exception e){
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
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
                        mSendMedia.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openGallery();
                            }
                        });
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
        if(currentGroupName.equals("Admin")){
            FirebaseDatabase.getInstance().getReference().child("admins").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                            if(!Objects.equals(childSnapshot.getKey(), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                if(currentGroupName.equals("Admin")){
                                    JSONObject notification = new JSONObject();
                                    JSONObject notifcationBody = new JSONObject();
                                    try {
                                        notifcationBody.put("title", currentUserObject.getName());
                                        notifcationBody.put("message", R.string.novasmssalaadmins);
                                        notification.put("to", "/LuaNegra/" + Objects.requireNonNull(childSnapshot.getValue()).toString());
                                        notification.put("data", notifcationBody);
                                    } catch (Exception e) {
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.toString());
                                    }
                                    SendNotification sendNotification = new SendNotification();
                                    sendNotification.sendNotification(notification, getApplicationContext(), FCMServer);

                                }

                            }
                        }
                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.notificacaoenviada), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }else {
            FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){

                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                            if(!Objects.equals(childSnapshot.getKey(), "chat")){
                                if(!Objects.equals(childSnapshot.getKey(), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                  JSONObject notification = new JSONObject();
                                    JSONObject notifcationBody = new JSONObject();
                                    try {
                                        notifcationBody.put("title", currentUserObject.getName());
                                        notifcationBody.put("message", getString(R.string.sigaviciarno) + currentGroupName + " ✶");
                                        notification.put("to", "/LuaNegra/" + Objects.requireNonNull(childSnapshot.child("notificationKey").getValue()).toString());
                                        notification.put("data", notifcationBody);
                                    } catch (Exception e) {
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.toString());
                                    }
                                    SendNotification sendNotification = new SendNotification();
                                    sendNotification.sendNotification(notification, getApplicationContext(), FCMServer);
                                }
                            }
                        }
                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.notificacaoenviada), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
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

    @Override
    protected void onStop() {
        super.onStop();
        doUnbindService();
    }


    @Override
    protected void onResume() {
        super.onResume();
        GetAppKeys();
        RetrieveUsersInCall();
        GetCurrentUser();
        SharedPreferences prefs = this.getSharedPreferences("LN_Touch_Time", Context.MODE_PRIVATE);
        long previousTime = prefs.getLong("LN_Touch_Time", 0);
        long currentTime = new Date().getTime();
        if (currentTime - previousTime > 15*60*1000){
            touch_Chat.setVisibility(View.VISIBLE);
        } else {
            touch_Chat.setVisibility(View.INVISIBLE);
            new CountDownTimer(currentTime - previousTime, 1000){
                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    touch_Chat.setVisibility(View.VISIBLE);
                }
            }.start();
        }
        doBindService();
        startService(new Intent(getBaseContext(), OnlineService.class));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences prefs = this.getSharedPreferences("LN_Touch_Time", Context.MODE_PRIVATE);
        long previousTime = prefs.getLong("LN_Touch_Time", 0);
        long currentTime = new Date().getTime();
        if (currentTime - previousTime > 15*60*1000){
            touch_Chat.setVisibility(View.VISIBLE);
        } else {
            touch_Chat.setVisibility(View.INVISIBLE);
            new CountDownTimer(currentTime - previousTime, 1000){
                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    touch_Chat.setVisibility(View.VISIBLE);
                }
            }.start();
        }
    }

    private ChildEventListener userInCallChildListener;
    private DatabaseReference UserInCallRef;
    private void RetrieveUsersInCall() {
        try{
            userCallList.clear();
            UserInCallRef = FirebaseDatabase.getInstance().getReference().child("salas").child(groupUID).child("online");
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

    private final int PICK_IMAGE_INTENT = 85;
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
                                        Cursor cursor = SalasJogosAntigaActivity.this.getContentResolver().query(fileUri,
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
                                            Cursor cursor = SalasJogosAntigaActivity.this.getContentResolver().query(fileUri,
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
                                            Cursor cursor = SalasJogosAntigaActivity.this.getContentResolver().query(fileUri,
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
                                                Cursor cursor = SalasJogosAntigaActivity.this.getContentResolver().query(fileUri,
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
                                            mediaUriList.add(Objects.requireNonNull(data.getData()).toString());
                                            fileNameToSend = getFileName(Uri.parse(data.getData().toString())) ;
                                        }else{
                                            for(int i = 0; i < Objects.requireNonNull(data.getClipData()).getItemCount(); i++){
                                                mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
                                                fileNameToSend = getFileName(Uri.parse(data.getClipData().getItemAt(i).getUri().toString()));
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
            mChatRef = FirebaseDatabase.getInstance().getReference().child("salas").child(groupUID).child("mensagens");
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
                               text = Objects.requireNonNull(dataSnapshot.child("text").getValue()).toString();
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

    private void sendMessage() {
        try{
        Calendar CalForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
        String currentDate = currentDateFormat.format(CalForDate.getTime());
        Calendar CalForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
        currentTimeFormat.toLocalizedPattern();
        String currentTime = currentTimeFormat.format(CalForTime.getTime());
        String messageID = FirebaseDatabase.getInstance().getReference().child("salas").child(groupUID).child("mensagens").push().getKey();
        final DatabaseReference newMessageDB = FirebaseDatabase.getInstance().getReference().child("salas").child(groupUID).child("mensagens").child(Objects.requireNonNull(messageID));
        final Map<String, Object> newMessageMap = new HashMap<>();
        if(!fileNameToSend.equals("")){
            newMessageMap.put("text", fileNameToSend);
        }else {
            if(!mMessage.getText().toString().isEmpty()){
                newMessageMap.put("text", mMessage.getText().toString());
            }
        }
        newMessageMap.put("creator", currentUserObject.getUid());
        newMessageMap.put("data", currentDate);
        newMessageMap.put("hora", currentTime);
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
        EmptyRecyclerView mChat = findViewById(R.id.messageRecyclerView_Grupo);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(this, EmptyRecyclerView.VERTICAL, false);
        mMessage = findViewById(R.id.messageChat_Grupo);
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
        mCallChat.setItemViewCacheSize(0);
        mCallChatAdapter = new CallSalaAdapter(userCallList);
        mCallChat.setLayoutManager(lLayout);
        mCallChat.setAdapter(mCallChatAdapter);
        }catch (Exception e){
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private void joinChannel() {
        try {
            startService(new Intent(this, CallService.class));
            mBoundService.StartCall(groupUID,currentGroupName, "sala", SalasJogosAntigaActivity.this);
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private void leaveChannel() {
        mBoundService.EndCall(groupUID, currentGroupName, "sala");
        mBoundService.stopForeground(true);
    }
}






