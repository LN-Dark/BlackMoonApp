package com.lua.luanegra.activitys;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
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
import com.lua.luanegra.adapters.SalasPrivadasToShareAdapter;
import com.lua.luanegra.adapters.SalasPublicasToShareAdapter;
import com.lua.luanegra.adapters.UserToShareAdapter;
import com.lua.luanegra.objects.ChatObject;
import com.lua.luanegra.objects.GroupObject;
import com.lua.luanegra.objects.UserObject;
import com.lua.luanegra.tools.DelayedProgressDialog;
import com.lua.luanegra.tools.EmptyRecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class WhereToShareActivity extends AppCompatActivity {
    private DatabaseReference mSharesDB;
    private int totalMediaUploaded = 0;
    private final ArrayList<String> mediaIDList = new ArrayList<>();
    private final ArrayList<String> mediaUriList = new ArrayList<>();
    private Uri imageUri;
    private EmptyRecyclerView.Adapter mSalaPublicaAdapter, mSalaPrivadaAdapter;
    private ArrayList<GroupObject> salasPublicasList, salasPrivadasList;
    private String TextToShare;
    private final DelayedProgressDialog progressDialog = new DelayedProgressDialog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where_to_share);
        try{
            Toolbar toolbar = findViewById(R.id.toolbarActivity);
            toolbar.setLogo(getDrawable(R.drawable.luanegra_logo));
            toolbar.setSubtitle("" + getString(R.string.partilhar));
            setSupportActionBar(toolbar);
            imageUri = Uri.parse(Objects.requireNonNull(getIntent().getExtras()).getString("imageURI"));
            String conteudoToShare = getIntent().getExtras().getString("conteudo");
            TextToShare = getIntent().getExtras().getString("text");
            chatList = new ArrayList<>();
            salasPublicasList = new ArrayList<>();
            salasPrivadasList = new ArrayList<>();
            InitializeRecyclerView();
            progressDialog.setCancelable(false);
            mSharesDB = FirebaseDatabase.getInstance().getReference().child("shares");
            Button btn_SharesToSHares = findViewById(R.id.btn_shares_shares);
            switch (Objects.requireNonNull(conteudoToShare)) {
                case "file":
                case "text":
                    btn_SharesToSHares.setVisibility(View.INVISIBLE);
                    break;
                case "imagem":
                    btn_SharesToSHares.setVisibility(View.VISIBLE);
                    break;
                case "youtube":
                    btn_SharesToSHares.setVisibility(View.INVISIBLE);
                    break;
            }
            btn_SharesToSHares.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        SharetoShares();
                }
            });
        }catch (Exception e){
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + e.getCause().toString() + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
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

    private void SharetoShares(){

        if (imageUri != null) {
            final AlertDialog.Builder builder2= new MaterialAlertDialogBuilder(WhereToShareActivity.this);
            LinearLayout layout = new LinearLayout(WhereToShareActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            builder2.setIcon(getDrawable(R.drawable.luanegra_logo));
            builder2.setTitle(getString(R.string.partilharemhalloffame));
            final TextView espaco2 = new TextView(WhereToShareActivity.this);
            espaco2.setText("\n");
            layout.addView(espaco2);
            final EditText textoshare = new EditText(WhereToShareActivity.this);
            textoshare.setHint(getString(R.string.escreveaqui));
            textoshare.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textoshare.setTextSize(13);
            layout.addView(textoshare);
            builder2.setView(layout);
            builder2.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder2.setPositiveButton(getString(R.string.partilhar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mediaUriList.add(imageUri.toString());
                    Calendar CalForDate = Calendar.getInstance();
                    SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                    String currentDate = currentDateFormat.format(CalForDate.getTime());
                    Calendar CalForTime = Calendar.getInstance();
                    SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
                    currentTimeFormat.toLocalizedPattern();
                    String currentTime = currentTimeFormat.format(CalForTime.getTime());
                    final Map<String, Object> newMessageMap = new HashMap<>();
                    newMessageMap.put("creator", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    String sharesID = mSharesDB.push().getKey();
                    final DatabaseReference newMessageDB = mSharesDB.child(Objects.requireNonNull(sharesID));
                    if(textoshare.getText() == null){
                        newMessageMap.put("text", " ");
                    }else {
                        newMessageMap.put("text", textoshare.getText().toString());
                    }
                    newMessageMap.put("data", currentDate);
                    newMessageMap.put("hora", currentTime);
                    for(String mediaUri : mediaUriList){
                        progressDialog.show(getSupportFragmentManager(), "tag");
                        String mediaID = newMessageDB.child("url").push().getKey();
                        mediaIDList.add(mediaID);
                        final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("shares").child(sharesID).child(Objects.requireNonNull(mediaID));
                        UploadTask uploadTask = filePath.putFile(Uri.parse(mediaUri));
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        newMessageMap.put("/shares/" + mediaIDList.get(totalMediaUploaded) + "/", uri.toString());
                                        totalMediaUploaded++;
                                        if(totalMediaUploaded == mediaUriList.size()){
                                            updateDatabaseWtNewMessage(newMessageDB, newMessageMap);
                                        }
                                    }
                                });
                            }
                        });
                    }
                }

            });
            builder2.show();
        } else{
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.errolinkinvalido), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        }
    }

    private void GetCurrentUserAndCreateNewVideo(final String videourl){
        try{
            Calendar CalForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
            String currentDate = currentDateFormat.format(CalForDate.getTime());
            Calendar CalForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
            currentTimeFormat.toLocalizedPattern();
            String currentTime = currentTimeFormat.format(CalForTime.getTime());
            DatabaseReference mVideoDB = FirebaseDatabase.getInstance().getReference().child("videos").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("videos").push().getKey()));

            Map<String, Object> videoMap = new HashMap<>();
            videoMap.put("text", " ");
            videoMap.put("data", currentDate);
            videoMap.put("hora", currentTime);
            if(videourl.contains("youtube")){
                videoMap.put("url", videourl.substring(videourl.lastIndexOf("v=") + 2));
            }else if(videourl.contains("youtu.be")){
                videoMap.put("url", videourl.substring(videourl.lastIndexOf("/") + 1));
            }
            videoMap.put("creator", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            mVideoDB.updateChildren(videoMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.videopublicadocomsucesso), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    finish();
                }
            });
        } catch (Exception e) {
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(this.getClass().getName() + " - " + e.toString());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        UserChatRef.removeEventListener(mSmSListener);
        currentUserBDRef.removeEventListener(UsersChildEventListener);
        this.finish();
    }


    private ChildEventListener mSmSListener;
    private DatabaseReference UserChatRef;
    private ArrayList<ChatObject> chatList;
    private ArrayList<UserObject> listaUsers;
    private ChildEventListener UsersChildEventListener;
    private DatabaseReference currentUserBDRef;
    private void GetCurrentUser(){
        try{
            listaUsers = new ArrayList<>();
            chatList.clear();
            currentUserBDRef = FirebaseDatabase.getInstance().getReference().child("user");
            currentUserBDRef.keepSynced(true);
            final ArrayList<Long> numerodeUsers = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        numerodeUsers.add(dataSnapshot.getChildrenCount() - 1);
                        UsersChildEventListener = currentUserBDRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot2, @Nullable String s) {
                                if(dataSnapshot2.exists()){
                                    if(!Objects.requireNonNull(dataSnapshot2.getKey()).equals("chat")){
                                        UserObject currentUserObject = new UserObject(Objects.requireNonNull(dataSnapshot2.child("name").getValue()).toString(), dataSnapshot2.getKey());
                                        currentUserObject.setNotificationKey(Objects.requireNonNull(dataSnapshot2.child("notificationKey").getValue()).toString());
                                        currentUserObject.setImagemPerfilUri(Objects.requireNonNull(dataSnapshot2.child("profile_image").getValue()).toString());
                                        currentUserObject.setIsonline(Objects.requireNonNull(dataSnapshot2.child("online").getValue()).toString());
                                        currentUserObject.setLastOnline("Ultima vez online: \n" + Objects.requireNonNull(dataSnapshot2.child("onlineDate").getValue()).toString() + "  *  " + Objects.requireNonNull(dataSnapshot2.child("onlineTime").getValue()).toString());
                                        listaUsers.add(currentUserObject);
                                        if(listaUsers.size() == numerodeUsers.get(0)){
                                            mSmSListener = UserChatRef.addChildEventListener(new ChildEventListener() {
                                                @Override
                                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot3, @Nullable String s) {
                                                    ChatObject mChat = new ChatObject(dataSnapshot3.getKey());

                                                    SharedPreferences prefs = getSharedPreferences(mChat.getChatID(), Context.MODE_PRIVATE);
                                                    String chatKey = " ";
                                                    chatKey = prefs.getString(mChat.getChatID(), " ");
                                                    if(!chatKey.equals(" ")){
                                                        mChat.setPartenrUid(Objects.requireNonNull(dataSnapshot3.child("userUID").getValue()).toString());
                                                        for(int f = 0; f <listaUsers.size(); f++){
                                                            if(listaUsers.get(f).getUid().equals(mChat.getPartenrUid())){
                                                                mChat.setUserName(listaUsers.get(f).getName());
                                                                mChat.setPartnerNotificationKey(listaUsers.get(f).getNotificationKey());
                                                                mChat.setImagemPerfilUri(listaUsers.get(f).getImagemPerfilUri());
                                                                mChat.setImageToShare(imageUri.toString());
                                                                mChat.setTextToShare(TextToShare);
                                                            }
                                                        }

                                                        if(mChat.getPartenrUid().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                                            mChat.setUserName(getString(R.string.mensagens_guardadas));
                                                            chatList.add(mChat);
                                                        }else {
                                                            chatList.add(mChat);
                                                        }
                                                        mChatListAdapter.notifyDataSetChanged();
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
                                        }
                                    }
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
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            getSalasPublicasList();
        } catch (Exception e) {
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(this.getClass().getName() + " - " + e.toString());
        }
    }


    private void updateDatabaseWtNewMessage(DatabaseReference newMessageDB, Map<String, Object> newMessageMap){
        try {
            newMessageDB.updateChildren(newMessageMap);
            mediaUriList.clear();
            mediaIDList.clear();
            totalMediaUploaded = 0;
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.publicadocomsucessoemhalloffame), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            progressDialog.cancel();
            finish();
        }catch (Exception e){
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + e.getCause().toString() + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private ArrayList<String> usersIdsBloqued_SalasPublicas;
    private ArrayList<String> usersIdsBloqued_SalasPrivadas;
    private ArrayList<String> usersIds_SalasPublicas;
    private ArrayList<String> usersIds_SalasPrivadas;
    private void getSalasPublicasList() {
        try{
            usersIds_SalasPrivadas = new ArrayList<>();
            usersIds_SalasPublicas = new ArrayList<>();
            usersIdsBloqued_SalasPrivadas = new ArrayList<>();
            usersIdsBloqued_SalasPublicas = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("salasPublicas").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        salasPublicasList.clear();

                        for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                            usersIds_SalasPublicas = new ArrayList<>();
                            usersIdsBloqued_SalasPublicas = new ArrayList<>();
                            if(childSnapshot.child("bloquedUsers").exists()){
                                for(DataSnapshot childbloqueduserssalapublicaSnapShot : childSnapshot.child("bloquedUsers").getChildren()){
                                    usersIdsBloqued_SalasPublicas.add(childbloqueduserssalapublicaSnapShot.getKey());
                                }
                            }
                            if(!usersIdsBloqued_SalasPublicas.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                for(DataSnapshot childuserssalapublicaSnapshot : childSnapshot.child("users").getChildren()){
                                    usersIds_SalasPublicas.add(childuserssalapublicaSnapshot.getKey());
                                }
                                if(usersIds_SalasPublicas.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    String groupLogoUri = Objects.requireNonNull(childSnapshot.child("logo").getValue()).toString();
                                    String groupNome = Objects.requireNonNull(childSnapshot.child("nome").getValue()).toString();
                                    GroupObject groupObject = new GroupObject(childSnapshot.getKey(), groupNome, groupLogoUri);
                                    if(imageUri == null){
                                        groupObject.setImagemToShare(" ");
                                    }else {
                                        groupObject.setImagemToShare(imageUri.toString());
                                    }
                                    groupObject.setTextToShare(TextToShare);
                                    if(!groupNome.equals("Admin")){
                                        salasPublicasList.add(groupObject);
                                    }
                                }
                            }
                        }
                        mSalaPublicaAdapter.notifyDataSetChanged();
                        getSalasPrivadasList();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }
    }

    private void getSalasPrivadasList() {
        try{
            usersIds_SalasPrivadas = new ArrayList<>();
            usersIds_SalasPublicas = new ArrayList<>();
            usersIdsBloqued_SalasPrivadas = new ArrayList<>();
            usersIdsBloqued_SalasPublicas = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("salasPrivadas").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        salasPrivadasList.clear();

                        for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                            usersIds_SalasPrivadas = new ArrayList<>();
                            usersIdsBloqued_SalasPrivadas = new ArrayList<>();
                            if(childSnapshot.child("bloquedUsers").exists()){
                                for(DataSnapshot childbloqueduserssalapublicaSnapShot : childSnapshot.child("bloquedUsers").getChildren()){
                                    usersIdsBloqued_SalasPrivadas.add(childbloqueduserssalapublicaSnapShot.getKey());
                                }
                            }
                            if(!usersIdsBloqued_SalasPrivadas.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                for(DataSnapshot childuserssalapublicaSnapshot : childSnapshot.child("users").getChildren()){
                                    usersIds_SalasPrivadas.add(childuserssalapublicaSnapshot.getKey());
                                }
                                if(usersIds_SalasPrivadas.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                                    SharedPreferences prefs = getSharedPreferences(childSnapshot.getKey(), Context.MODE_PRIVATE);
                                    String chatKey = prefs.getString(childSnapshot.getKey(), " ");
                                    if(!chatKey.equals(" ")){
                                        String groupLogoUri = Objects.requireNonNull(childSnapshot.child("logo").getValue()).toString();
                                        String groupNome = Objects.requireNonNull(childSnapshot.child("nome").getValue()).toString();
                                        GroupObject groupObject = new GroupObject(childSnapshot.getKey(), groupNome, groupLogoUri);
                                        groupObject.setChatKey(chatKey);
                                        if(imageUri == null){
                                            groupObject.setImagemToShare(" ");
                                        }else {
                                            groupObject.setImagemToShare(imageUri.toString());
                                        }
                                        groupObject.setTextToShare(TextToShare);
                                        if(!groupNome.equals("Admin")){
                                            salasPrivadasList.add(groupObject);
                                        }
                                    }
                                }
                            }
                        }
                        mSalaPrivadaAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }
    }

    private EmptyRecyclerView.Adapter mChatListAdapter;
    private void InitializeRecyclerView() {
        try{
            EmptyRecyclerView mCHatList = findViewById(R.id.recycler_SHares_mensagens);
            mCHatList.setNestedScrollingEnabled(false);
            mCHatList.setHasFixedSize(true);
            mCHatList.setItemViewCacheSize(0);
            Display display = this.getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);
            GridLayoutManager lLayout, lLayout2, lLayout3;
            float density  = getResources().getDisplayMetrics().density;
            float dpWidth  = outMetrics.widthPixels / density;
            int columns = Math.round(dpWidth/200);
            lLayout = new GridLayoutManager(this,columns);
            lLayout2 = new GridLayoutManager(this, columns);
            lLayout3 = new GridLayoutManager(this, columns);
            mCHatList.setLayoutManager(lLayout);
            mChatListAdapter = new UserToShareAdapter(chatList);
            mCHatList.setAdapter(mChatListAdapter);
            EmptyRecyclerView mSalasPublicasList = findViewById(R.id.recycler_Shares_salas);
            mSalasPublicasList.setNestedScrollingEnabled(false);
            mSalasPublicasList.setHasFixedSize(false);
            mSalasPublicasList.setLayoutManager(lLayout2);
            mSalaPublicaAdapter = new SalasPublicasToShareAdapter(salasPublicasList);
            mSalasPublicasList.setAdapter(mSalaPublicaAdapter);
            UserChatRef = FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("chat");
            UserChatRef.keepSynced(true);
            EmptyRecyclerView mSalasPrivadasList = findViewById(R.id.recycler_salasPrivadas_salas);
            mSalasPrivadasList.setNestedScrollingEnabled(false);
            mSalasPrivadasList.setHasFixedSize(false);
            mSalasPrivadasList.setLayoutManager(lLayout3);
            mSalaPrivadaAdapter = new SalasPrivadasToShareAdapter(salasPrivadasList);
            mSalasPrivadasList.setAdapter(mSalaPrivadaAdapter);
            GetCurrentUser();
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }

    }
}
