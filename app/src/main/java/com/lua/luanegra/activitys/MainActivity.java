package com.lua.luanegra.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lua.luanegra.BuildConfig;
import com.lua.luanegra.R;
import com.lua.luanegra.activitys.Admins.AdminActivity;
import com.lua.luanegra.callservice.SendNotification;
import com.lua.luanegra.fragments.Home.HomeFragment;
import com.lua.luanegra.fragments.Notificacoes.NotificacoesMainFragment;
import com.lua.luanegra.fragments.Notificacoes.PedidosFragment;
import com.lua.luanegra.fragments.salas.SalasMainFragment;
import com.lua.luanegra.fragments.share.ShareFragment;
import com.lua.luanegra.tools.DelayedProgressDialog;
import com.lua.luanegra.tools.OnlineService;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.Timer;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ArrayList<String> listaAdmins;
    public  NavigationView navigationView;
    private ArrayList<String> adminList;
    private final DelayedProgressDialog progressDialog = new DelayedProgressDialog();
    private String TokenUserFCM;
    private String ConfirmacaoExit;
    private CircleImageView imagem_user;
    private TextView nome_user;
    private Timer timer;
    public static   String nav_Home = "nav_Home";
    public static  String nav_SalasPrivadas = "nav_SalasPrivadas";
    public static String nav_HallOfFame = "nav_HallOfFame";
    public static String nav_notificacoes = "nav_notificacoes";
    public static String nav_Selected = " ";
    private Toolbar toolbar;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
             toolbar = findViewById(R.id.toolbar);
            toolbar.setLogo(getDrawable(R.drawable.luanegra_logo));
            setSupportActionBar(toolbar);
            progressDialog.setCancelable(false);
            FirebaseApp.initializeApp(this);
            adminList = new ArrayList<>();
            GetAppKeys();
            listaAdmins = new ArrayList<>();
            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if (!"text/plain".equals(type)) {
                    if (type.startsWith("image/")) {
                        handleSendImage(intent);
                    }else {
                        handleSendFile(intent);
                    }
                }else {
                    if(intent.getStringExtra(Intent.EXTRA_TEXT).contains("youtube") || intent.getStringExtra(Intent.EXTRA_TEXT).contains("youtu.be")){
                        handleSendVideo(intent);
                    }else {
                        handleSendText(intent);
                    }
                }
            }

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
             navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            userIsLogIn();
        testeerror = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("admins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                   for(DataSnapshot childtesteesrror : dataSnapshot.getChildren()){
                       testeerror.add(childtesteesrror.getKey());
                   }
                   if(testeerror.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                       VerifyBD();
                   }else {
                       Runnable mPendingRunnable = new Runnable() {
                           @Override
                           public void run() {
                               Fragment fragment = new HomeFragment();
                               FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                               fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                       android.R.anim.fade_out);
                               fragmentTransaction.replace(R.id.content_frame, fragment);
                               fragmentTransaction.commitAllowingStateLoss();
                               nav_Selected = nav_Home;
                           }
                       };
                       mPendingRunnable.run();
                   }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
            ArrayList<String> testeerror;

    int numerodeuserscorrigidos;
    private void VerifyBD(){
        numerodeuserscorrigidos = 0;
        FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot childUsersSnapShot : dataSnapshot.getChildren()){
                        if(!childUsersSnapShot.getKey().equals("chat")){
                            if(!childUsersSnapShot.child("name").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("name").setValue("UserUnknown");
                            }
                            if(!childUsersSnapShot.child("bio").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("bio").setValue("Too lazy to write a Bio.");
                            }
                            if(!childUsersSnapShot.child("notificationKey").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("notificationKey").setValue("noKey");
                            }
                            if(!childUsersSnapShot.child("online").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("online").setValue("false");
                            }
                            if(!childUsersSnapShot.child("onlineDate").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("onlineDate").setValue("17 May, 2019");
                            }
                            if(!childUsersSnapShot.child("onlineTime").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("onlineTime").setValue("11:50");
                            }
                            if(!childUsersSnapShot.child("patrono").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("patrono").setValue("false");
                            }
                            if(!childUsersSnapShot.child("profile_image").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("profile_image").setValue("https://firebasestorage.googleapis.com/v0/b/bdlua-29c71.appspot.com/o/Profile_Images%2F73R6I0KnEuhFwmSD7WpK3TlVon02%2F-LewX1rv1EreGkzlW1lF?alt=media&token=237cb7cb-48e5-4e10-b4cd-27f20096de6d");
                            }
                            if(!childUsersSnapShot.child("registerDate").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("registerDate").setValue("17 May, 2019");
                            }
                            if(!childUsersSnapShot.child("registerTime").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("registerTime").setValue("11:50");
                            }
                            if(!childUsersSnapShot.child("opcoes").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("opcoes").child("exitConf").setValue("true");
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("opcoes").child("notificacoes").setValue("true");
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("opcoes").child("sociavel").setValue("true");
                            }
                            if(!childUsersSnapShot.child("registerNick").exists()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(childUsersSnapShot.getKey()).child("registerNick").setValue("UserUnknown");
                            }
                        }
                        numerodeuserscorrigidos++;
                    }
                            Runnable mPendingRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    Fragment fragment = new HomeFragment();
                                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                            android.R.anim.fade_out);
                                    fragmentTransaction.replace(R.id.content_frame, fragment);
                                    fragmentTransaction.commitAllowingStateLoss();
                                    nav_Selected = nav_Home;
                                }
                            };
                            mPendingRunnable.run();
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.verificacaocompleta) + " " + numerodeuserscorrigidos + " " + getString(R.string.verificados), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    private String BuildLinkUpdate = " ";
    private void VerificaUpdate(){
        FirebaseDatabase.getInstance().getReference().child("AppText").child("version").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String updatedBuildNumber = Objects.requireNonNull(dataSnapshot.child("number").getValue()).toString();
                    String BuildDateUpdate = Objects.requireNonNull(dataSnapshot.child("date").getValue()).toString();
                    BuildLinkUpdate = Objects.requireNonNull(dataSnapshot.child("link").getValue()).toString();
                    String BuildChangeLog = Objects.requireNonNull(dataSnapshot.child("changeLog").getValue()).toString();
                    StringTokenizer tokensUpdate = new StringTokenizer(updatedBuildNumber, ".");
                    StringTokenizer tokensCurrent = new StringTokenizer(BuildConfig.VERSION_NAME, ".");
                    long buildUpdate_Master = 0;
                    try {
                        buildUpdate_Master = Integer.parseInt(tokensUpdate.nextToken());
                    } catch(NumberFormatException nfe) {
                        System.out.println("Could not parse " + nfe);
                    }
                    long buildCurrent_Master = 0;
                    try {
                        buildCurrent_Master = Integer.parseInt(tokensCurrent.nextToken());
                    } catch(NumberFormatException nfe) {
                        System.out.println("Could not parse " + nfe);
                    }
                    long buildUpdate_Medium = 0;
                    long buildCurrent_Medium = 0;
                    try {
                        buildUpdate_Medium = Integer.parseInt(tokensUpdate.nextToken());
                    } catch(NumberFormatException nfe) {
                        System.out.println("Could not parse " + nfe);
                    }
                    try {
                        buildCurrent_Medium = Integer.parseInt(tokensCurrent.nextToken());
                    } catch(NumberFormatException nfe) {
                        System.out.println("Could not parse " + nfe);
                    }
                    long buildUpdate_Minor = 0;
                    long buildCurrent_Minor = 0;
                    try {
                        buildUpdate_Minor = Integer.parseInt(tokensUpdate.nextToken());
                    } catch(NumberFormatException nfe) {
                        System.out.println("Could not parse " + nfe);
                    }
                    try {
                        buildCurrent_Minor = Integer.parseInt(tokensCurrent.nextToken());
                    } catch(NumberFormatException nfe) {
                        System.out.println("Could not parse " + nfe);
                    }
                    if((buildUpdate_Master > buildCurrent_Master)){
                        final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(MainActivity.this);
                        LinearLayout layout = new LinearLayout(MainActivity.this);
                        layout.setOrientation(LinearLayout.VERTICAL);

                        builder3.setIcon(getDrawable(R.drawable.luanegra_logo));
                        builder3.setTitle(getResources().getString(R.string.novoupdate));
                        builder3.setMessage("Major UpDate");
                        final TextView textoshare = new TextView(MainActivity.this);
                        textoshare.setText(String.format("%s\n\n Data : %s", BuildChangeLog, BuildDateUpdate));
                        textoshare.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textoshare.setTextSize(14);
                        layout.addView(textoshare);
                        builder3.setCancelable(false);
                        final TextView espaco5 = new TextView(MainActivity.this);
                        espaco5.setText(" ");
                        layout.addView(espaco5);
                        builder3.setView(layout);
                        AlertDialog alert;
                        builder3.setPositiveButton(getResources().getString(R.string.update2), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(BuildLinkUpdate));
                                startActivity(browserIntent);
                            }
                        });
                        builder3.setNeutralButton(getResources().getString(R.string.fecharapp), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.this.finish();
                            }
                        });
                        alert = builder3.create();
                        alert.show();
                    }else if((buildUpdate_Medium > buildCurrent_Medium)){
                        final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(MainActivity.this);
                        LinearLayout layout = new LinearLayout(MainActivity.this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        builder3.setIcon(getDrawable(R.drawable.luanegra_logo));
                        builder3.setTitle(getResources().getString(R.string.novoupdate));
                        builder3.setMessage("Major UpDate");
                        final TextView textoshare = new TextView(MainActivity.this);
                        textoshare.setText(String.format("%s\n\n Data : %s", BuildChangeLog, BuildDateUpdate));
                        textoshare.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textoshare.setTextSize(14);
                        layout.addView(textoshare);
                        builder3.setCancelable(false);
                        final TextView espaco5 = new TextView(MainActivity.this);
                        espaco5.setText(" ");
                        layout.addView(espaco5);
                        builder3.setView(layout);
                        AlertDialog alert;
                        builder3.setPositiveButton(getResources().getString(R.string.update2), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(BuildLinkUpdate));
                                startActivity(browserIntent);
                            }
                        });
                        builder3.setNeutralButton(getResources().getString(R.string.fecharapp), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.this.finish();
                            }
                        });
                        alert = builder3.create();
                        alert.show();
                    }else if(buildUpdate_Minor > buildCurrent_Minor){
                        final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(MainActivity.this);
                        LinearLayout layout = new LinearLayout(MainActivity.this);
                        layout.setOrientation(LinearLayout.VERTICAL);

                        builder3.setIcon(getDrawable(R.drawable.luanegra_logo));
                        builder3.setTitle(getResources().getString(R.string.novoupdate));
                        builder3.setMessage("Minor UpDate");
                        final TextView textoshare = new TextView(MainActivity.this);
                        textoshare.setText(String.format("%s\n\n Data : %s", BuildChangeLog, BuildDateUpdate));
                        textoshare.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textoshare.setTextSize(14);
                        layout.addView(textoshare);
                        builder3.setCancelable(false);
                        final TextView espaco5 = new TextView(MainActivity.this);
                        espaco5.setText(" ");
                        layout.addView(espaco5);
                        builder3.setView(layout);
                        AlertDialog alert;
                        builder3.setPositiveButton(getResources().getString(R.string.update2), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(BuildLinkUpdate));
                                startActivity(browserIntent);
                            }
                        });
                        builder3.setNeutralButton(getResources().getString(R.string.maistrade), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        alert = builder3.create();
                        alert.show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPermissions() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.READ_PHONE_STATE},1);

    }

    private void VerificaBloqueios() {
        FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("salasPrivadas").exists()){
                        int numerodeBloqueios = 0;
                        for(DataSnapshot childBloqueiosSalasSnapShot : dataSnapshot.child("salasPrivadas").getChildren()){
                            if(Objects.requireNonNull(childBloqueiosSalasSnapShot.getValue()).toString().equals("false")){
                                numerodeBloqueios++;
                            }
                            if((numerodeBloqueios == 5)){
                                FirebaseDatabase.getInstance().getReference().child("bloqued_users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                                JSONObject notification = new JSONObject();
                                JSONObject notifcationBody = new JSONObject();
                                try {
                                    notifcationBody.put("title", getResources().getString(R.string.fostebloqueadoem5salas));
                                    notifcationBody.put("message", getResources().getString(R.string.toleranciazerotrols));

                                    notification.put("to", "/LuaNegra/" + TokenUserFCM);
                                    notification.put("data", notifcationBody);
                                } catch (Exception e) {
                                    FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
                                }
                                SendNotification sendNotification = new SendNotification();
                                sendNotification.sendNotification(notification, getApplicationContext(), FCMServer);
                            }
                        }
                    }
                    if(dataSnapshot.child("salasPublicas").exists()){
                        int numerodeBloqueios = 0;
                        for(DataSnapshot childBloqueiosSalasSnapShot : dataSnapshot.child("salasPublicas").getChildren()){
                            if(Objects.requireNonNull(childBloqueiosSalasSnapShot.getValue()).toString().equals("false")){
                                numerodeBloqueios++;
                            }
                            if((numerodeBloqueios == 5)){
                                FirebaseDatabase.getInstance().getReference().child("bloqued_users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                                JSONObject notification = new JSONObject();
                                JSONObject notifcationBody = new JSONObject();
                                try {
                                    notifcationBody.put("title", getResources().getString(R.string.fostebloqueadoem5salas));
                                    notifcationBody.put("message", getResources().getString(R.string.toleranciazerotrols));

                                    notification.put("to", "/LuaNegra/" + TokenUserFCM);
                                    notification.put("data", notifcationBody);
                                } catch (Exception e) {
                                    FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
                                }
                                SendNotification sendNotification = new SendNotification();
                                sendNotification.sendNotification(notification, getApplicationContext(), FCMServer);
                            }
                        }
                    }
                    if(dataSnapshot.child("advertencias").exists()){
                        if(dataSnapshot.child("advertencias").getChildrenCount() > 5){
                            FirebaseDatabase.getInstance().getReference().child("bloqued_users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                            JSONObject notification = new JSONObject();
                            JSONObject notifcationBody = new JSONObject();
                            try {
                                notifcationBody.put("title", getResources().getString(R.string.fostebloqueado5advertencias));
                                notifcationBody.put("message", getResources().getString(R.string.toleranciazerotrols));

                                notification.put("to", "/LuaNegra/" + TokenUserFCM);
                                notification.put("data", notifcationBody);
                            } catch (Exception e) {
                                FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
                            }
                            SendNotification sendNotification = new SendNotification();
                            sendNotification.sendNotification(notification, getApplicationContext(), FCMServer);
                        }
                    }
                    if(dataSnapshot.child("opcoes").exists()){
                        if(dataSnapshot.child("opcoes").child("exitConf").exists()){
                            ConfirmacaoExit = dataSnapshot.child("opcoes").child("exitConf").getValue().toString();
                        }else {
                            FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("opcoes").child("exitConf").setValue(true);
                            ConfirmacaoExit = "true";
                        }
                        if(dataSnapshot.child("opcoes").child("notificacoes").exists()){
                            FirebaseMessaging.getInstance().subscribeToTopic("LuaNegra");
                        }else {
                            FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("opcoes").child("notificacoes").setValue(true);
                            FirebaseMessaging.getInstance().subscribeToTopic("LuaNegra");
                        }
                    }else {
                        FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("opcoes").child("exitConf").setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("opcoes").child("notificacoes").setValue(true);
                        FirebaseMessaging.getInstance().subscribeToTopic("LuaNegra");
                        ConfirmacaoExit = "true";
                    }
                    if(imagem_user != null){
                        Picasso.get().load(dataSnapshot.child("profile_image").getValue().toString()).into(imagem_user);
                        imagem_user.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SendUserToSettingsActivity();
                            }
                        });
                        nome_user.setText(dataSnapshot.child("name").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_options_menu, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.home_option_menu){
           FirebaseDatabase.getInstance().getReference().child("AppText").addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   if(dataSnapshot.exists()){

                       final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(MainActivity.this);
                       LinearLayout layout = new LinearLayout(MainActivity.this);
                       layout.setOrientation(LinearLayout.VERTICAL);
                       builder3.setIcon(getDrawable(R.drawable.luanegra_logo));
                       builder3.setTitle(getResources().getString(R.string.motivacao));
                       final TextView textoshare = new TextView(MainActivity.this);
                       if(Locale.getDefault().getCountry().equals("PT") ) {
                           textoshare.setText("\n" + dataSnapshot.child("fraseDia").getValue().toString()+ "\n");
                       }else {
                           textoshare.setText("\n" + dataSnapshot.child("fraseDia_en").getValue().toString()+ "\n" );
                       }
                       textoshare.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                       textoshare.setTextSize(14);
                       layout.addView(textoshare);
                       builder3.setCancelable(false);
                       final TextView espaco5 = new TextView(MainActivity.this);
                       espaco5.setText(" ");
                       layout.addView(espaco5);
                       builder3.setView(layout);
                       AlertDialog alert;
                       builder3.setPositiveButton(getResources().getString(R.string.fecharapp), new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                           }
                       });
                       alert = builder3.create();
                       alert.show();
                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        try{
            if (getSupportFragmentManager().getBackStackEntryCount() > 0){
                super.onBackPressed();
            }else {
                if(ConfirmacaoExit.equals("true")){
                    final AlertDialog.Builder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                    LinearLayout layout = new LinearLayout(getApplicationContext());
                    layout.setOrientation(LinearLayout.VERTICAL);
                    builder.setIcon(getDrawable(R.drawable.luanegra_logo));
                    builder.setTitle(getResources().getString(R.string.fecharappbtn));
                    builder.setMessage(getResources().getString(R.string.queresmesmofecharaapp));
                    builder.setView(layout);
                    nav_Selected = " ";
                    builder.setPositiveButton(getResources().getString(R.string.yap), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.setNeutralButton(getResources().getString(R.string.nempensar), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { }
                    });
                    builder.show();
                }else {

                    super.onBackPressed();
                }
            }
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private void handleSendVideo(Intent intent) {
        try {
            Intent activityintent = new Intent(getApplicationContext(), WhereToShareActivity.class);
            final Bundle bundle = new Bundle();
            bundle.putString("imageURI", " ");
            bundle.putString("text", intent.getStringExtra(Intent.EXTRA_TEXT));
            bundle.putString("conteudo", "youtube");
            activityintent.putExtras(bundle);
            startActivity(activityintent);
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (!"text/plain".equals(type)) {
                if (type.startsWith("image/")) {
                    handleSendImage(intent);
                }else {
                    handleSendFile(intent);
                }
            }else {
                if(intent.getStringExtra(Intent.EXTRA_TEXT).contains("youtube") || intent.getStringExtra(Intent.EXTRA_TEXT).contains("youtu.be")){
                    handleSendVideo(intent);
                }else {
                    handleSendText(intent);
                }
            }
        }
        super.onNewIntent(intent);
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

    private void LoadTokenFCM() {
        try {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                return;
                            }
                            String token = Objects.requireNonNull(task.getResult()).getToken();
                            FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("notificationKey").setValue(token);
                            TokenUserFCM = token;
                        }
                    });
            VerificaBloqueios();
        } catch (Exception e) {
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(usersTrolsChildListener != null){
            UserTrolsRefs.removeEventListener(usersTrolsChildListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(usersTrolsChildListener != null){
            UserTrolsRefs.removeEventListener(usersTrolsChildListener);
        }
    }

    ChildEventListener usersTrolsChildListener;
    DatabaseReference UserTrolsRefs = FirebaseDatabase.getInstance().getReference().child("user");
    private void loadAdmins() {
        try {

            FirebaseDatabase.getInstance().getReference().child("admins").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                            String admin = childSnapshot.getKey();
                            adminList.add(admin);
                        }

                        if(adminList.contains(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                            navigationView = findViewById(R.id.nav_view);
                            navigationView.setItemIconTintList(null);

                            Menu nav_Menu = navigationView.getMenu();
                            nav_Menu.findItem(R.id.nav_admin).setVisible(true);

                            SpannableString s = new SpannableString( nav_Menu.findItem(R.id.nav_defenicoes).getTitle());
                            s.setSpan(new TextAppearanceSpan(MainActivity.this, R.style.NavTitle), 0, s.length(), 0);
                            nav_Menu.findItem(R.id.nav_defenicoes).setTitle(s);
                        }else {
                            navigationView = findViewById(R.id.nav_view);
                            navigationView.setItemIconTintList(null);
                            Menu nav_Menu = navigationView.getMenu();
                            nav_Menu.findItem(R.id.nav_admin).setVisible(false);
                            SpannableString s = new SpannableString( nav_Menu.findItem(R.id.nav_defenicoes).getTitle());
                            s.setSpan(new TextAppearanceSpan(MainActivity.this, R.style.NavTitle), 0, s.length(), 0);
                            nav_Menu.findItem(R.id.nav_defenicoes).setTitle(s);
                        }
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
        imagem_user = findViewById(R.id.imagem_nav_menu);
        nome_user = findViewById(R.id.titulo_nav_menu);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            VerificaUpdate();
            VerificaBloqueios();
                startService(new Intent(getBaseContext(), OnlineService.class));
        }else {
            userIsLogIn();
        }
    }



    private void userIsLogIn() {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user == null){
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }else {
                LoadTokenFCM();
                getPermissions();
                GetAppKeys();
                setNewUserName();
                loadAdmins();
                FirebaseDatabase.getInstance().getReference("user/"+ FirebaseAuth.getInstance().getCurrentUser().getUid() + "/online").onDisconnect().setValue("false");
                    startService(new Intent(getBaseContext(), OnlineService.class));
            }
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        try {

            int id = item.getItemId();
            if (id == R.id.nav_mensagens) {

                Runnable mPendingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Fragment fragment = new HomeFragment();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                        fragmentTransaction.replace(R.id.content_frame, fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                        nav_Selected = nav_Home;
                    }
                };
                mPendingRunnable.run();
            }  else if (id == R.id.nav_shares) {

                Runnable mPendingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Fragment fragment = new ShareFragment();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                        fragmentTransaction.replace(R.id.content_frame, fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                        nav_Selected = nav_HallOfFame;
                    }
                };
                mPendingRunnable.run();
            }else if (id == R.id.nav_notificacoes) {

                Runnable mPendingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Fragment fragment = new NotificacoesMainFragment();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                        fragmentTransaction.replace(R.id.content_frame, fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                        nav_Selected = nav_HallOfFame;
                    }
                };
                mPendingRunnable.run();

            } else if (id == R.id.nav_Swag) {

                SendUserToSettingsActivity();

            }else if (id == R.id.nav_logOut) {
                final AlertDialog.Builder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                LinearLayout layout = new LinearLayout(getApplicationContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                builder.setIcon(getDrawable(R.drawable.luanegra_logo));
                builder.setTitle("   LogOut\n");
                builder.setMessage(getResources().getString(R.string.queresmesmofazerlogout));
                builder.setView(layout);
                nav_Selected = " ";
                builder.setPositiveButton(getString(R.string.yap), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("LuaNegra");
                        FirebaseAuth.getInstance().signOut();
                        Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                        finish();
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });
                builder.show();

            }else if (id == R.id.nav_admin) {
                FirebaseDatabase.getInstance().getReference().child("admins").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for(DataSnapshot childSnapShot : dataSnapshot.getChildren()){
                                listaAdmins.add(childSnapShot.getKey());
                            }
                            if(listaAdmins.contains(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                                MainActivity.this.startActivity(intent);
                            }else {
                                Snackbar.make(findViewById(android.R.id.content), "ॐ LOL ॐ", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }else if (id == R.id.nav_Criar_sala_privada) {
                CriarSalaPrivada();
            }else if (id == R.id.nav_info) {

                Intent infoIntent = new Intent(getApplicationContext(), InfoActivity.class);
                startActivity(infoIntent);

            }else if (id == R.id.nav_sharemyapp) {

                ShareMyApp();

            }else if (id == R.id.nav_salasPrivadas) {

                Runnable mPendingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Fragment fragment = new SalasMainFragment();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                        fragmentTransaction.replace(R.id.content_frame, fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                        nav_Selected = nav_SalasPrivadas;
                    }
                };
                mPendingRunnable.run();
            }

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
        return true;
    }

    private void setNewUserName(){
        try {
            FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        if(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString().equals("UserUnknown")){
                            final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(MainActivity.this);
                            LinearLayout layout = new LinearLayout(MainActivity.this);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            builder3.setIcon(getDrawable(R.drawable.luanegra_logo));
                            builder3.setTitle(getString(R.string.novomenbro));
                            builder3.setMessage(getString(R.string.bemvindoaluanegra));
                            final EditText textoshare = new EditText(MainActivity.this);
                            textoshare.setHint(getString(R.string.escreveaquioteunome));
                            textoshare.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            textoshare.setTextSize(16);
                            layout.addView(textoshare);
                            builder3.setCancelable(false);
                            final TextView espaco5 = new TextView(MainActivity.this);
                            espaco5.setText(" ");
                            layout.addView(espaco5);
                            builder3.setView(layout);
                            AlertDialog alert = builder3.create();
                            final AlertDialog finalAlert = alert;
                            builder3.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(!textoshare.getText().toString().equals("")){
                                        if(textoshare.getText().length() < 20){
                                            final ArrayList<String> nomesUsersVerify = new ArrayList<>();
                                            FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.exists()){
                                                        for(DataSnapshot childNomesUserSnapShot : dataSnapshot.getChildren()){
                                                            if(!Objects.requireNonNull(childNomesUserSnapShot.getKey()).equals("chat")){
                                                                nomesUsersVerify.add(Objects.requireNonNull(childNomesUserSnapShot.child("name").getValue()).toString());
                                                            }
                                                        }
                                                        if(!nomesUsersVerify.contains(textoshare.getText().toString())){
                                                            progressDialog.show(getSupportFragmentManager(), "tag");
                                                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").setValue(textoshare.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("registerNick").setValue(textoshare.getText().toString());
                                                                    JSONObject notification = new JSONObject();
                                                                    JSONObject notifcationBody = new JSONObject();
                                                                    try {
                                                                        notifcationBody.put("title", getString(R.string.bemvindo));
                                                                        notifcationBody.put("message", getString(R.string.aproveitaestadia));

                                                                        notification.put("to", "/LuaNegra/" + TokenUserFCM);
                                                                        notification.put("data", notifcationBody);
                                                                    } catch (Exception e) {
                                                                        FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
                                                                    }
                                                                    SendNotification sendNotification = new SendNotification();
                                                                    sendNotification.sendNotification(notification, getApplicationContext(), FCMServer);
                                                                    FirebaseDatabase.getInstance().getReference().child("admins").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                            if(dataSnapshot.exists()){
                                                                                try {
                                                                                    final ArrayList<String> listaNotificationAdmins = new ArrayList<>();
                                                                                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                                                                                        listaNotificationAdmins.add(childSnapshot.getKey());
                                                                                    }
                                                                                    FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot3) {
                                                                                            if(dataSnapshot3.exists()){
                                                                                                for(DataSnapshot childUserSnapShot : dataSnapshot3.getChildren()){
                                                                                                    if (!Objects.requireNonNull(childUserSnapShot.getKey()).equals("chat")){
                                                                                                        for(int j = 0; j < listaNotificationAdmins.size(); j++){
                                                                                                            if(childUserSnapShot.getKey().equals(listaNotificationAdmins.get(j))){
                                                                                                                JSONObject notification2 = new JSONObject();
                                                                                                                JSONObject notifcationBody2 = new JSONObject();
                                                                                                                try {
                                                                                                                    notifcationBody2.put("title", getString(R.string.novouserregistado));
                                                                                                                    notifcationBody2.put("message", textoshare.getText().toString());

                                                                                                                    notification2.put("to", "/LuaNegra/" + Objects.requireNonNull(childUserSnapShot.child("notificationKey").getValue()).toString());
                                                                                                                    notification2.put("data", notifcationBody2);
                                                                                                                } catch (Exception e) {
                                                                                                                    FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
                                                                                                                }
                                                                                                                SendNotification sendNotification2 = new SendNotification();
                                                                                                                sendNotification2.sendNotification(notification2, getApplicationContext(), FCMServer);
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




                                                                                } catch (Exception e) {
                                                                                    FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
                                                                                }
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                        }
                                                                    });
                                                                    progressDialog.cancel();
                                                                    finalAlert.cancel();
                                                                }
                                                            });
                                                        }else {
                                                            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.nomemeutilizacao), Snackbar.LENGTH_LONG)
                                                                    .setAction("Action", null).show();
                                                            setNewUserName();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }else {
                                            Snackbar.make(findViewById(android.R.id.content), getString(R.string.nomecom20caracteres), Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                            setNewUserName();
                                        }
                                    }else {
                                        Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.necessarioumnick), Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        setNewUserName();
                                    }
                                }
                            });
                            alert = builder3.create();
                            alert.show();
                        }
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
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(nav_Selected.equals(" ")){
            FragmentManager fm = this.getSupportFragmentManager();
            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    Fragment fragment = new HomeFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                            android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.content_frame, fragment);
                    fragmentTransaction.commitAllowingStateLoss();
                    nav_Selected = nav_Home;
                }
            };
            mPendingRunnable.run();
        }else if(nav_Selected.equals(nav_Home)){
            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    Fragment fragment = new HomeFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                            android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.content_frame, fragment);
                    fragmentTransaction.commitAllowingStateLoss();
                    nav_Selected = nav_Home;
                }
            };
            mPendingRunnable.run();
        }else if(nav_Selected.equals(nav_HallOfFame)){
            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    Fragment fragment = new ShareFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                            android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.content_frame, fragment);
                    fragmentTransaction.commitAllowingStateLoss();
                    nav_Selected = nav_HallOfFame;
                }
            };
            mPendingRunnable.run();
        }else if(nav_Selected.equals(nav_SalasPrivadas)){
            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    Fragment fragment = new SalasMainFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                            android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.content_frame, fragment);
                    fragmentTransaction.commitAllowingStateLoss();
                    nav_Selected = nav_SalasPrivadas;
                }
            };
            mPendingRunnable.run();
        }else if(nav_Selected.equals(nav_notificacoes)){
            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    Fragment fragment = new PedidosFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                            android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.content_frame, fragment);
                    fragmentTransaction.commitAllowingStateLoss();
                    nav_Selected = nav_notificacoes;
                }
            };
            mPendingRunnable.run();
        }

    }



    private void ShareMyApp(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.vemdescobriraluanegra) + "\n\n" + getString(R.string.fazdownloadaqui) + "\n" + BuildLinkUpdate + "\n\n XDA Developers - https://forum.xda-developers.com/android/apps-games/app-lua-negra-t3934027");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getString(R.string.partilharapp)));
    }

    private void handleSendImage(Intent intent) {
        try {
            Intent activityintent = new Intent(getApplicationContext(), WhereToShareActivity.class);
            final Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            final Bundle bundle = new Bundle();
            bundle.putString("imageURI", imageUri.toString());
            bundle.putString("text", " ");
            bundle.putString("conteudo", "imagem");
            activityintent.putExtras(bundle);
            startActivity(activityintent);
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private void handleSendFile(Intent intent) {
        try {
            Intent activityintent = new Intent(getApplicationContext(), WhereToShareActivity.class);
            final Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            final Bundle bundle = new Bundle();
            bundle.putString("imageURI", imageUri.toString());
            bundle.putString("text", " ");
            bundle.putString("conteudo", "file");
            activityintent.putExtras(bundle);
            startActivity(activityintent);
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private void handleSendText(Intent intent) {
        try {
            Intent activityintent = new Intent(getApplicationContext(), WhereToShareActivity.class);
            final Bundle bundle = new Bundle();
            bundle.putString("imageURI", " ");
            bundle.putString("text", intent.getStringExtra(Intent.EXTRA_TEXT));
            bundle.putString("conteudo", "text");
            activityintent.putExtras(bundle);
            startActivity(activityintent);
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }


    private int totalMediaUploaded = 0;
    private void CriarSalaPrivada() {
        try {

            final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(MainActivity.this);
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);

            builder3.setIcon(getDrawable(R.drawable.luanegra_logo));
            builder3.setTitle(getString(R.string.criarnovasalaprivada));
            final TextView espaco4 = new TextView(this);
            espaco4.setText(" ");
            layout.addView(espaco4);
            final EditText NomeSala = new EditText(this);
            NomeSala.setHint(getString(R.string.escreveonomedasala));

            NomeSala.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            NomeSala.setTextSize(15);
            layout.addView(NomeSala);
            final TextView espaco3 = new TextView(this);
            espaco3.setText("\n");
            layout.addView(espaco3);
            final MaterialButton uploadImagem = new MaterialButton(this);
            uploadImagem.setText(R.string.logo_sala);
            uploadImagem.setTextSize(13);

            layout.addView(uploadImagem);
            uploadImagem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGallery();
                }
            });
            final TextView espaco7 = new TextView(this);
            espaco7.setText("\n");
            layout.addView(espaco7);
            final EditText descricaoSala = new EditText(this);
            descricaoSala.setHint(getString(R.string.escreveadescricaodasala));

            descricaoSala.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            descricaoSala.setTextSize(15);
            layout.addView(descricaoSala);
            final TextView espaco33 = new TextView(this);
            espaco33.setText("\n");
            layout.addView(espaco33);
            builder3.setCancelable(false);
            builder3.setView(layout);
            final SwitchMaterial tipoDeSala = new SwitchMaterial(this);
            tipoDeSala.setText(getString(R.string.salasprivada));
            tipoDeSala.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            tipoDeSala.setTextSize(15);
            tipoDeSala.setChecked(true);
            layout.addView(tipoDeSala);
            builder3.setPositiveButton(getResources().getString(R.string.criar), null);
            builder3.setNeutralButton(getResources().getString(R.string.cancelar), null);
            final AlertDialog alert = builder3.create();
            final ArrayList<String> mediaIDList = new ArrayList<>();
            alert.setCanceledOnTouchOutside(false);
            alert.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {

                    Button b = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!NomeSala.getText().toString().equals("")){
                                if(NomeSala.getText().length() < 20){
                                    if(!descricaoSala.getText().toString().equals("")){
                                    if(!mediaUriList.isEmpty()){
                                        if(tipoDeSala.isChecked()){
                                            final Map<String, Object> newSalaMap = new HashMap<>();
                                            progressDialog.show(getSupportFragmentManager(), "tag");
                                            final String SalaPrivada_key = FirebaseDatabase.getInstance().getReference().child("salasPrivadas").push().getKey();
                                            newSalaMap.put("creator", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                                            newSalaMap.put("descricao", descricaoSala.getText().toString());
                                            newSalaMap.put("nome", NomeSala.getText().toString());
                                            newSalaMap.put("users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid(), true);
                                            newSalaMap.put("admins/"+ FirebaseAuth.getInstance().getCurrentUser().getUid(), true);
                                            newSalaMap.put("coresSala/toolbarSuperior", "#414141");
                                            newSalaMap.put("coresSala/toolbarInferior", "#414141");
                                            newSalaMap.put("coresSala/fundo", "#000000");
                                            newSalaMap.put("coresSala/chatReciever", "#414141");
                                            newSalaMap.put("coresSala/chatSender", "#161616");
                                            newSalaMap.put("coresSala/texto", "#BEBEBE");
                                            newSalaMap.put("coresSala/dataHora", "#C07500");
                                            newSalaMap.put("coresSala/apresentacao", "#161616");
                                            newSalaMap.put("coresSala/barraMensagens", "#BEBEBE");
                                            AesCbcWithIntegrity.SecretKeys keys = null;
                                            try {
                                                keys = AesCbcWithIntegrity.generateKey();
                                            } catch (GeneralSecurityException e) {
                                                e.printStackTrace();
                                            }
                                            SharedPreferences prefs = getSharedPreferences(SalaPrivada_key, Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = prefs.edit();
                                            editor.putString(SalaPrivada_key, keys.toString());
                                            editor.apply();
                                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("salasPrivadas").child(Objects.requireNonNull(SalaPrivada_key)).setValue(true);
                                            for(String mediaUri : mediaUriList){
                                                String mediaID = FirebaseDatabase.getInstance().getReference().child("salasPrivadas").push().getKey();
                                                mediaIDList.add(mediaID);
                                                final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("SalasPrivadas").child(SalaPrivada_key).child(Objects.requireNonNull(mediaID));
                                                UploadTask uploadTask = filePath.putFile(Uri.parse(mediaUri));
                                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) { newSalaMap.put("logo", uri.toString());
                                                            totalMediaUploaded++;
                                                            if(totalMediaUploaded == mediaUriList.size()){
                                                                FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(SalaPrivada_key).updateChildren(newSalaMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.salaprivadacriadacomsucesso), Snackbar.LENGTH_LONG)
                                                                                .setAction("Action", null).show();
                                                                        alert.cancel();
                                                                        mediaIDList.clear();
                                                                        mediaUriList.clear();
                                                                        totalMediaUploaded = 0;
                                                                        progressDialog.cancel();
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                    }
                                                });

                                            }
                                        }else {
                                            final Map<String, Object> newSalaMap = new HashMap<>();
                                            progressDialog.show(getSupportFragmentManager(), "tag");
                                            final String SalaPrivada_key = FirebaseDatabase.getInstance().getReference().child("salasPublicas").push().getKey();
                                            newSalaMap.put("creator", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                                            newSalaMap.put("descricao", descricaoSala.getText().toString());
                                            newSalaMap.put("nome", NomeSala.getText().toString());
                                            newSalaMap.put("users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid(), true);
                                            newSalaMap.put("admins/"+ FirebaseAuth.getInstance().getCurrentUser().getUid(), true);
                                            newSalaMap.put("coresSala/toolbarSuperior", "#414141");
                                            newSalaMap.put("coresSala/toolbarInferior", "#414141");
                                            newSalaMap.put("coresSala/fundo", "#000000");
                                            newSalaMap.put("coresSala/chatReciever", "#414141");
                                            newSalaMap.put("coresSala/chatSender", "#161616");
                                            newSalaMap.put("coresSala/texto", "#BEBEBE");
                                            newSalaMap.put("coresSala/dataHora", "#C07500");
                                            newSalaMap.put("coresSala/apresentacao", "#161616");
                                            newSalaMap.put("coresSala/barraMensagens", "#BEBEBE");
                                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("salasPublicas").child(Objects.requireNonNull(SalaPrivada_key)).setValue(true);
                                            for(String mediaUri : mediaUriList){
                                                String mediaID = FirebaseDatabase.getInstance().getReference().child("salasPublicas").push().getKey();
                                                mediaIDList.add(mediaID);
                                                final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("salasPublicas").child(SalaPrivada_key).child(Objects.requireNonNull(mediaID));
                                                UploadTask uploadTask = filePath.putFile(Uri.parse(mediaUri));
                                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) { newSalaMap.put("logo", uri.toString());
                                                            totalMediaUploaded++;
                                                            if(totalMediaUploaded == mediaUriList.size()){
                                                                FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(SalaPrivada_key).updateChildren(newSalaMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.salaprivadacriadacomsucesso), Snackbar.LENGTH_LONG)
                                                                                .setAction("Action", null).show();
                                                                        alert.cancel();
                                                                        mediaIDList.clear();
                                                                        mediaUriList.clear();
                                                                        totalMediaUploaded = 0;
                                                                        progressDialog.cancel();
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                    }
                                                });

                                            }
                                        }
                                    }else {
                                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.enecessarioumaimagem), Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                    }else {
                                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.enecessariodescricaodasala), Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                }else {
                                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.nomecom20caracteres), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            }else {
                                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.necessarioumnick), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }

                        }
                    });
                    Button a = alert.getButton(AlertDialog.BUTTON_NEGATIVE);
                    a.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();

                        }
                    });
                }
            });
            alert.show();
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private void openGallery() {
        try{
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMaxCropResultSize(500,500)
                    .start(this);
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private final int PICK_IMAGE_INTENT = 15;
    private final ArrayList<String> mediaUriList = new ArrayList<>();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if(resultCode == RESULT_OK){

                if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        Uri resultUri = result.getUri();
                        mediaUriList.add(resultUri.toString());
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = result.getError();
                    }
                }
            }
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private void SendUserToSettingsActivity() {
        try {

            Intent settingsIntent = new Intent(getApplicationContext(), SwagActivity.class);
            startActivity(settingsIntent);
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }
}
