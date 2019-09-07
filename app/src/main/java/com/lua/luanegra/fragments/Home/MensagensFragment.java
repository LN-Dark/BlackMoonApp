package com.lua.luanegra.fragments.Home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lua.luanegra.R;
import com.lua.luanegra.adapters.ChatListAdapter;
import com.lua.luanegra.objects.ChatObject;
import com.lua.luanegra.objects.UserObject;
import com.lua.luanegra.tools.EmptyRecyclerView;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Objects;

public class MensagensFragment extends Fragment {
    private ChatListAdapter mChatListAdapter;
    private ArrayList<ChatObject> chatList;
    private View root;
    private ChildEventListener mSmSListener, mensagensListener;
    private DatabaseReference UserChatRef;
    private final DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference().child("user").child("chat");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

         root = inflater.inflate(R.layout.fragment_mensagens, container, false);
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private Menu menuSearch;
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        menu.findItem(R.id.action_search).setVisible(true);
        menuSearch = menu;
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
                mChatListAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }


    private ArrayList<UserObject> listaUsers;
    private ChildEventListener UsersChildEventListener;
    private DatabaseReference currentUserBDRef;
    private TextView aindanaotens;
    private void GetCurrentUser(){
        try{
            listaUsers = new ArrayList<>();
            aindanaotens = root.findViewById(R.id.txt_naotens_mensagens);
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
                                                    final ChatObject mChat = new ChatObject(dataSnapshot3.getKey());
                                                    mChat.setPartenrUid(Objects.requireNonNull(dataSnapshot3.child("userUID").getValue()).toString());
                                                    for(int f = 0; f <listaUsers.size(); f++){
                                                        if(listaUsers.get(f).getUid().equals(mChat.getPartenrUid())){
                                                            mChat.setUserName(listaUsers.get(f).getName());
                                                            mChat.setPartnerNotificationKey(listaUsers.get(f).getNotificationKey());
                                                            mChat.setPartnerUserImageUri(listaUsers.get(f).getImagemPerfilUri());
                                                            for(int k = 0; k < listaUsers.size(); k++){
                                                                if(listaUsers.get(k).getUid().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                                                    mChat.setCurrentUserImageUri(listaUsers.get(k).getImagemPerfilUri());
                                                                    mChat.setCurrentUserName(listaUsers.get(k).getName());
                                                                    break;
                                                                }
                                                            }
                                                            break;
                                                        }
                                                    }
                                                    mChat.setLastmessage(" ");
                                                    mChat.setLastmessageData(" ");
                                                    mChat.setLastMessageHora(" ");
                                                    if(mChat.getPartenrUid().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                                        mChat.setUserName(getString(R.string.mensagens_guardadas));
                                                        chatList.add(0, mChat);
                                                    }else {
                                                        chatList.add(mChat);
                                                    }
                                                    mChatListAdapter.notifyDataSetChanged();
                                                    aindanaotens.setVisibility(View.GONE);
                                                    GetMessages();
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
        } catch (Exception e) {
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
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


    private void GetMessages() {
        mensagensListener = messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot childSnapShot : dataSnapshot.getChildren()){
                        for(int i = 0; i < chatList.size(); i++){
                            if(Objects.requireNonNull(dataSnapshot.getKey()).equals(chatList.get(i).getChatID())){
                                SharedPreferences prefs = getContext().getSharedPreferences(chatList.get(i).getChatID(), Context.MODE_PRIVATE);
                                String chatKey = " ";
                                chatKey = prefs.getString(chatList.get(i).getChatID(), " ");
                                if(!chatKey.equals(" ")){
                                    chatList.get(i).setLastMessageHora(Objects.requireNonNull(childSnapShot.child("hora").getValue()).toString());
                                    chatList.get(i).setLastmessageData(Objects.requireNonNull(childSnapShot.child("data").getValue()).toString());
                                    chatList.get(i).setLastmessage(Decrypt(Objects.requireNonNull(childSnapShot.child("text").getValue()).toString(),chatKey));
                                    mChatListAdapter.notifyItemChanged(i);
                                }else {
                                    chatList.get(i).setLastMessageHora(" ");
                                    chatList.get(i).setLastmessageData(" ");
                                    chatList.get(i).setLastmessage(getString(R.string.naofoipossivelencontrarachavedestechat));
                                    mChatListAdapter.notifyItemChanged(i);
                                }
                                break;
                            }
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


    @Override
    public void onResume() {
        super.onResume();
        chatList = new ArrayList<>();
        UserChatRef = FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("chat");
        UserChatRef.keepSynced(true);
        messageRef.keepSynced(true);
        InitializeRecyclerView();
        GetCurrentUser();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mSmSListener != null){
            UserChatRef.removeEventListener(mSmSListener);
        }
        if(UsersChildEventListener != null){
            currentUserBDRef.removeEventListener(UsersChildEventListener);
        }
        if(mensagensListener != null){
            messageRef.removeEventListener(mensagensListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSmSListener != null){
            UserChatRef.removeEventListener(mSmSListener);
        }
        if(UsersChildEventListener != null){
            currentUserBDRef.removeEventListener(UsersChildEventListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mSmSListener != null){
            UserChatRef.removeEventListener(mSmSListener);
        }
        if(UsersChildEventListener != null){
            currentUserBDRef.removeEventListener(UsersChildEventListener);
        }
        if(mensagensListener != null){
            messageRef.removeEventListener(mensagensListener);
        }
    }

    private void InitializeRecyclerView() {
        try{
            EmptyRecyclerView mCHatList = root.findViewById(R.id.chatList);
        mCHatList.setNestedScrollingEnabled(false);
        mCHatList.setHasFixedSize(true);
        mCHatList.setItemViewCacheSize(0);
        EmptyRecyclerView.LayoutManager mCHatListLayoutManager = new LinearLayoutManager(getContext(), EmptyRecyclerView.VERTICAL, false);
        mCHatList.setLayoutManager(mCHatListLayoutManager);
        mChatListAdapter = new ChatListAdapter(chatList);
        mCHatList.setItemViewCacheSize(0);
        mCHatList.setAdapter(mChatListAdapter);
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }

    }
}