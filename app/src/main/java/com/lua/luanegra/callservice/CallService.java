package com.lua.luanegra.callservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lua.luanegra.R;
import com.lua.luanegra.activitys.MensagensActivity;
import com.lua.luanegra.objects.UserObject;
import com.lua.luanegra.tools.DelayedProgressDialog;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class CallService extends Service {
    private Notification notification;
    private NotificationChannel channel;
    private final String CHANNEL_ID = "LuaNegra_Call_Service_Channel";
    private SinchClient sinchClient;
    private CallClient callClient;
    private Call currentcall;
    private String groupUID, activityInUse, groupName;
    private final DelayedProgressDialog progressDialog = new DelayedProgressDialog();
    private Context context;

    public CallService() {
    }


    private final IBinder mBinder = new LocalBinder();   // interface for clients that bind
    private boolean mAllowRebind;

    @Override
    public void onCreate() {
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    @Override
    public void onRebind(Intent intent) {
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    public class LocalBinder extends Binder {
        public CallService getService() {
            return CallService.this;
        }
    }

    public void EndCall(String groupUID, String groupName, String activityInUse){
        if(!groupUID.equals("")){
            this.groupUID = groupUID;
            this.groupName = groupName;
            this.activityInUse = activityInUse;
            leaveChannel();
        }
    }

    public void StartCall(String groupUID, String groupName, String activityInUse, Context context){
        this.groupUID = groupUID;
        this.groupName = groupName;
        this.activityInUse = activityInUse;
        this.context = context;
        GetAppKeys();
    }

    private String FCMServer, SinchKey, SinchSecret;
    private void GetAppKeys(){
        FirebaseApp.initializeApp(this);
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
                    GetCurrentUser();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                        joinChannel(groupUID, groupName, activityInUse, context);
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

    private void joinChannel(final String channelUid, final String groupName, final String activityInUse, Context context) {
        try {
            progressDialog.setCancelable(false);
            FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
            progressDialog.show(manager, "tag");
            sinchClient = Sinch.getSinchClientBuilder().context(getApplicationContext())
                    .applicationKey(SinchKey)
                    .applicationSecret(SinchSecret)
                    .environmentHost("clientapi.sinch.com")
                    .userId(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                    .build();
            sinchClient.setSupportActiveConnectionInBackground(true);
            sinchClient.setSupportCalling(true);
            sinchClient.startListeningOnActiveConnection();
            sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
            sinchClient.start();
            sinchClient.addSinchClientListener(new SinchClientListener() {
                @Override
                public void onClientStarted(SinchClient sinchClient) {
                    callClient = sinchClient.getCallClient();
                    currentcall = callClient.callConference(channelUid);
                    currentcall.addCallListener(new CallListener() {
                        @Override
                        public void onCallProgressing(Call call) {

                        }

                        @Override
                        public void onCallEstablished(Call call) {
                            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                                    new Intent(getApplicationContext(), MensagensActivity.class), 0);
                            if (Build.VERSION.SDK_INT >= 26) {
                                channel = new NotificationChannel(CHANNEL_ID,
                                        getString(R.string.servicocall),
                                        NotificationManager.IMPORTANCE_HIGH);
                                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
                                notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                        .setContentTitle(getString(R.string.servicocall))
                                        .setSmallIcon(R.drawable.luanegra_logo)
                                        .setColorized(true)
                                        .setColor(getColor(R.color.colorPrimary))
                                        .setContentIntent(contentIntent)
                                        .setContentText(getString(R.string.callativa))
                                        .build();

                                startForeground(1, notification);
                                progressDialog.dismiss();
                            }
                            switch (activityInUse) {
                                case "mensagemPrivada":
                                    FirebaseDatabase.getInstance().getReference().child("onlineCalls").child(groupUID).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                    if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(childSnapshot.getKey())) {
                                                        JSONObject notification = new JSONObject();
                                                        JSONObject notifcationBody = new JSONObject();
                                                        try {
                                                            notifcationBody.put("title", currentUserObject.getName());
                                                            notifcationBody.put("message", getString(R.string.entreinacallmensagem));

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
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    FirebaseDatabase.getInstance().getReference().child("onlineCalls").child(groupUID).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("name").setValue(currentUserObject.getName());
                                    FirebaseDatabase.getInstance().getReference().child("onlineCalls").child(groupUID).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("imagemPerfilUri").setValue(currentUserObject.getImagemPerfilUri());
                                    FirebaseDatabase.getInstance().getReference().child("onlineCalls").child(groupUID).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificationKey").setValue(currentUserObject.getNotificationKey());
                                    Toast.makeText(CallService.this, "ॐ Call iniciada ॐ", Toast.LENGTH_LONG).show();
                                    break;
                                case "salaPrivada":
                                    FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("online").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                if (dataSnapshot.getChildrenCount() > 0) {
                                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                        if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(childSnapshot.getKey())) {
                                                            JSONObject notification = new JSONObject();
                                                            JSONObject notifcationBody = new JSONObject();
                                                            try {
                                                                notifcationBody.put("title", currentUserObject.getName());
                                                                notifcationBody.put("message", getString(R.string.entreinacall) + groupName + " ✶");

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
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("online").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("name").setValue(currentUserObject.getName());
                                    FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("online").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("imagemPerfilUri").setValue(currentUserObject.getImagemPerfilUri());
                                    FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("online").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificationKey").setValue(currentUserObject.getNotificationKey());

                                    break;
                                case "salaPublica":
                                    FirebaseDatabase.getInstance().getReference().child("salaPublica").child(groupUID).child("online").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                if (dataSnapshot.getChildrenCount() > 0) {
                                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                        if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(childSnapshot.getKey())) {
                                                            JSONObject notification = new JSONObject();
                                                            JSONObject notifcationBody = new JSONObject();
                                                            try {
                                                                notifcationBody.put("title", currentUserObject.getName());
                                                                notifcationBody.put("message", getString(R.string.entreinacall) + groupName + " ✶");

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
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    FirebaseDatabase.getInstance().getReference().child("salaPublica").child(groupUID).child("online").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("name").setValue(currentUserObject.getName());
                                    FirebaseDatabase.getInstance().getReference().child("salaPublica").child(groupUID).child("online").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("imagemPerfilUri").setValue(currentUserObject.getImagemPerfilUri());
                                    FirebaseDatabase.getInstance().getReference().child("salaPublica").child(groupUID).child("online").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificationKey").setValue(currentUserObject.getNotificationKey());

                                    break;
                                case "sala":
                                    if (groupName.equals("Admin")) {
                                        FirebaseDatabase.getInstance().getReference().child("admins").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                        if (!Objects.equals(childSnapshot.getKey(), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {

                                                            JSONObject notification = new JSONObject();
                                                            JSONObject notifcationBody = new JSONObject();
                                                            try {
                                                                notifcationBody.put("title", currentUserObject.getName());
                                                                notifcationBody.put("message", getString(R.string.entreinacall) + groupName + " ✶");

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
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        });
                                    } else {
                                        FirebaseDatabase.getInstance().getReference().child("salas").child(groupUID).child("online").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    if (dataSnapshot.getChildrenCount() > 0) {
                                                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                            if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(childSnapshot.getKey())) {
                                                                JSONObject notification = new JSONObject();
                                                                JSONObject notifcationBody = new JSONObject();
                                                                try {
                                                                    notifcationBody.put("title", currentUserObject.getName());
                                                                    notifcationBody.put("message", getString(R.string.entreinacall) + groupName + " ✶");

                                                                    notification.put("to", "/LuaNegra/" + Objects.requireNonNull(childSnapshot.child("notificationKey").getValue()).toString());
                                                                    notification.put("data", notifcationBody);
                                                                } catch (Exception e) {
                                                                    FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.toString());
                                                                }
                                                                SendNotification sendNotification = new SendNotification();
                                                                sendNotification.sendNotification(notification, getApplicationContext(), FCMServer);
                                                            }
                                                        }
                                                    } else {
                                                        FirebaseDatabase.getInstance().getReference().child("admins").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists()) {
                                                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                                        if (!Objects.equals(childSnapshot.getKey(), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {

                                                                            JSONObject notification = new JSONObject();
                                                                            JSONObject notifcationBody = new JSONObject();
                                                                            try {
                                                                                notifcationBody.put("title", currentUserObject.getName());
                                                                                notifcationBody.put("message", getString(R.string.entreinacall) + groupName + " ✶");

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
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
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
                                    FirebaseDatabase.getInstance().getReference().child("salas").child(groupUID).child("online").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("name").setValue(currentUserObject.getName());
                                    FirebaseDatabase.getInstance().getReference().child("salas").child(groupUID).child("online").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("imagemPerfilUri").setValue(currentUserObject.getImagemPerfilUri());
                                    FirebaseDatabase.getInstance().getReference().child("salas").child(groupUID).child("online").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificationKey").setValue(currentUserObject.getNotificationKey());
                                    break;
                            }
                        }

                        @Override
                        public void onCallEnded(Call call) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                stopForeground(true);
                            } else {
                                stopSelf();
                            }
                            switch (activityInUse) {
                                case "mensagemPrivada":
                                    FirebaseDatabase.getInstance().getReference().child("onlineCalls").child(groupUID).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                if (dataSnapshot.getChildrenCount() > 0) {
                                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                        if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(childSnapshot.getKey())) {
                                                            JSONObject notification = new JSONObject();
                                                            JSONObject notifcationBody = new JSONObject();
                                                            try {
                                                                notifcationBody.put("title", currentUserObject.getName());
                                                                notifcationBody.put("message", getString(R.string.saidacalldasala));

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
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            progressDialog.dismiss();
                                        }
                                    });
                                    Toast.makeText(CallService.this, getString(R.string.calldesligada), Toast.LENGTH_LONG).show();
                                    break;
                                case "salaPrivada":

                                    FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("online").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                if (dataSnapshot.getChildrenCount() > 0) {
                                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                        if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(childSnapshot.getKey())) {
                                                            JSONObject notification = new JSONObject();
                                                            JSONObject notifcationBody = new JSONObject();
                                                            try {
                                                                notifcationBody.put("title", currentUserObject.getName());
                                                                notifcationBody.put("message", getString(R.string.saidacall) + groupName + " ✶");

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
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    break;
                                case "sala":
                                    FirebaseDatabase.getInstance().getReference().child("salas").child(groupUID).child("online").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                if (dataSnapshot.getChildrenCount() > 0) {
                                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                        if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(childSnapshot.getKey())) {
                                                            JSONObject notification = new JSONObject();
                                                            JSONObject notifcationBody = new JSONObject();
                                                            try {
                                                                notifcationBody.put("title", currentUserObject.getName());
                                                                notifcationBody.put("message", getString(R.string.saidacall) + groupName + " ✶");

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
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            progressDialog.dismiss();
                                        }
                                    });
                                    break;
                            }
                        }

                        @Override
                        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

                        }
                    });

                }

                @Override
                public void onClientStopped(SinchClient sinchClient) {
                    progressDialog.dismiss();
                }

                @Override
                public void onClientFailed(SinchClient sinchClient, SinchError sinchError) {
                    progressDialog.dismiss();
                }

                @Override
                public void onRegistrationCredentialsRequired(SinchClient sinchClient, ClientRegistration clientRegistration) {

                }

                @Override
                public void onLogMessage(int i, String s, String s1) {

                }
            });
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    public void SpeakerON(){
        if(this.sinchClient != null){
            this.sinchClient.getAudioController().enableSpeaker();
        }
    }

    public void SpeakerOFF(){
        if(this.sinchClient != null){
            this.sinchClient.getAudioController().disableSpeaker();
        }

    }

    public void MicON(){
        if(this.sinchClient != null){
            this.sinchClient.getAudioController().unmute();
        }

    }

    public void MicOFF(){
        if(this.sinchClient != null){
            this.sinchClient.getAudioController().mute();
        }

    }

    private void leaveChannel() {
        try {
            if(currentcall != null){
                currentcall.hangup();
                currentcall = null;
            }
            if(sinchClient != null){
                sinchClient = null;

            }

            if(!groupUID.equals("")){
                switch (activityInUse) {
                    case "mensagemPrivada":
                        FirebaseDatabase.getInstance().getReference().child("onlineCalls").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.child(groupUID).exists()) {
                                        if (dataSnapshot.child(groupUID).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).exists()) {
                                            FirebaseDatabase.getInstance().getReference().child("onlineCalls").child(groupUID).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).removeValue();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        break;
                    case "salaPrivada":
                        FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("online").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).exists()) {
                                        FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupUID).child("online").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        break;
                    case "sala":
                        FirebaseDatabase.getInstance().getReference().child("salas").child(groupUID).child("online").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).exists()) {
                                        FirebaseDatabase.getInstance().getReference().child("salas").child(groupUID).child("online").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        break;
                }
            }

            stopForeground(STOP_FOREGROUND_REMOVE);
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call call) {
            try {
                currentcall = call;
                currentcall.answer();
            }catch (Exception e){
                FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
        }
    }


}