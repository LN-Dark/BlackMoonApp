package com.lua.luanegra.fragments.Home;

import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Display;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lua.luanegra.R;
import com.lua.luanegra.adapters.UserListAdapter;
import com.lua.luanegra.objects.UserObject;

import java.util.ArrayList;
import java.util.Objects;

public class ComunidadeFragment extends Fragment  {
    private UserListAdapter mUserListAdapter;
    private ArrayList<String> blockedUsersUid;
    private View root;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

         root = inflater.inflate(R.layout.fragment_comunidade, container, false);

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(childEventListenerUserList != null){
            fullUserListRef.removeEventListener(childEventListenerUserList);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(childEventListenerUserList != null){
            fullUserListRef.removeEventListener(childEventListenerUserList);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
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
                mUserListAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }


    @Override
    public void onPause() {
        super.onPause();
        if(childEventListenerUserList != null){
            fullUserListRef.removeEventListener(childEventListenerUserList);
        }

    }

    private ArrayList<UserObject> fullUserList;
    private DatabaseReference fullUserListRef;
    private ChildEventListener childEventListenerUserList;
    private TextView aindanaotens;
    private void GetAllUsers(){
        try {
            aindanaotens = root.findViewById(R.id.txt_naotens_comunidade);
            childEventListenerUserList = fullUserListRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    UpdateBlockedUsers();
                    if(dataSnapshot.exists()){
                        if(!Objects.requireNonNull(dataSnapshot.getKey()).equals("chat")){
                            UserObject newUser = new UserObject(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString(), dataSnapshot.getKey());
                            newUser.setNotificationKey(Objects.requireNonNull(dataSnapshot.child("notificationKey").getValue()).toString());
                            newUser.setImagemPerfilUri(Objects.requireNonNull(dataSnapshot.child("profile_image").getValue()).toString());
                            newUser.setRegistoData(Objects.requireNonNull(dataSnapshot.child("registerDate").getValue()).toString());
                            newUser.setRegistoHora(Objects.requireNonNull(dataSnapshot.child("registerTime").getValue()).toString());
                            newUser.setPrimeiroNick(Objects.requireNonNull(dataSnapshot.child("registerNick").getValue()).toString());
                            newUser.setBio(Objects.requireNonNull(dataSnapshot.child("bio").getValue()).toString());
                            newUser.setPatrono(Objects.requireNonNull(dataSnapshot.child("patrono").getValue()).toString());
                            if(dataSnapshot.child("opcoes").exists()){
                                if(dataSnapshot.child("opcoes").child("sociavel").exists()){
                                    newUser.setSociavel(dataSnapshot.child("opcoes").child("sociavel").getValue().toString());
                                }else {
                                    newUser.setSociavel("true");
                                }
                            }else {
                                newUser.setSociavel("true");
                            }
                            if(dataSnapshot.child("online").exists()){
                                newUser.setIsonline(Objects.requireNonNull(dataSnapshot.child("online").getValue()).toString());
                                newUser.setLastOnline(getResources().getString(R.string.ultimavezonline) +"\n" + Objects.requireNonNull(dataSnapshot.child("onlineDate").getValue()).toString() + "  *  " + Objects.requireNonNull(dataSnapshot.child("onlineTime").getValue()).toString());
                            }else {
                                newUser.setIsonline("true");
                                newUser.setLastOnline(getString(R.string.desconhecico));
                            }
                            if(!blockedUsersUid.contains(newUser.getUid())){
                                if(!newUser.getUid().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                if(!newUser.getName().equals("UserUnknown")) {
                                    fullUserList.add(newUser);
                                    mUserListAdapter.notifyDataSetChanged();
                                    aindanaotens.setVisibility(View.GONE);
                                }
                                }

                            }

                        }
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    UpdateBlockedUsers();
                    if(dataSnapshot.exists()){
                        if(!Objects.requireNonNull(dataSnapshot.getKey()).equals("chat")){
                            UserObject newUser = new UserObject(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString(), dataSnapshot.getKey());
                            newUser.setNotificationKey(Objects.requireNonNull(dataSnapshot.child("notificationKey").getValue()).toString());
                            newUser.setImagemPerfilUri(Objects.requireNonNull(dataSnapshot.child("profile_image").getValue()).toString());
                            newUser.setIsonline(Objects.requireNonNull(dataSnapshot.child("online").getValue()).toString());
                            newUser.setBio(Objects.requireNonNull(dataSnapshot.child("bio").getValue()).toString());
                            if(dataSnapshot.child("opcoes").exists()){
                                if(dataSnapshot.child("opcoes").child("sociavel").exists()){
                                    newUser.setSociavel(dataSnapshot.child("opcoes").child("sociavel").getValue().toString());

                                }else {
                                    newUser.setSociavel("true");
                                }
                            }else {
                                newUser.setSociavel("true");
                            }
                            newUser.setLastOnline(getResources().getString(R.string.ultimavezonline) +"\n" + Objects.requireNonNull(dataSnapshot.child("onlineDate").getValue()).toString() + "  *  " + Objects.requireNonNull(dataSnapshot.child("onlineTime").getValue()).toString());
                            for(int i = 0; i < fullUserList.size(); i++){
                                if(fullUserList.get(i).getUid().equals(newUser.getUid())){
                                    if(!blockedUsersUid.contains(newUser.getUid())){
                                        fullUserList.get(i).setLastOnline(newUser.getLastOnline());
                                        fullUserList.get(i).setIsonline(newUser.getIsonline());
                                        fullUserList.get(i).setImagemPerfilUri(newUser.getImagemPerfilUri());
                                        fullUserList.get(i).setNotificationKey(newUser.getNotificationKey());
                                        fullUserList.get(i).setName(newUser.getName());
                                        fullUserList.get(i).setBio(newUser.getBio());
                                        fullUserList.get(i).setSociavel(newUser.getSociavel());
                                        mUserListAdapter.notifyItemChanged(i);
                                    }
                                    break;
                                }
                            }

                        }
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    UpdateBlockedUsers();
                    if(dataSnapshot.exists()){
                        if(!Objects.requireNonNull(dataSnapshot.getKey()).equals("chat")){
                            UserObject newUser = new UserObject(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString(), dataSnapshot.getKey());
                            for(int i = 0; i< fullUserList.size(); i++){
                                if(fullUserList.get(i).getUid().equals(newUser.getUid())){
                                    fullUserList.remove(i);
                                    mUserListAdapter.notifyItemRemoved(i);
                                    break;
                                }
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
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
    }
    }

    @Override
    public void onResume() {
        super.onResume();
        blockedUsersUid = new ArrayList<>();
        fullUserList = new ArrayList<>();
        fullUserListRef = FirebaseDatabase.getInstance().getReference().child("user");
        fullUserListRef.keepSynced(true);
        UpdateBlockedUsers();
        InitializeRecyclerView();
        GetAllUsers();
    }

    private void UpdateBlockedUsers(){
        try{
        FirebaseDatabase.getInstance().getReference().child("bloqued_users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    blockedUsersUid.clear();
                    for(DataSnapshot childSnapShot : dataSnapshot.getChildren()){
                        blockedUsersUid.add(childSnapShot.getKey());
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


    private void InitializeRecyclerView() {
        try{
            RecyclerView mUserList = root.findViewById(R.id.userList);
            mUserList.setHasFixedSize(true);
            Display display = Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);
            GridLayoutManager lLayout;
            float density  = getResources().getDisplayMetrics().density;
            float dpWidth  = outMetrics.widthPixels / density;
            int columns = Math.round(dpWidth/160);
            lLayout = new GridLayoutManager(getActivity(),columns);
            mUserList.setLayoutManager(lLayout);
            mUserListAdapter = new UserListAdapter(fullUserList);
            mUserListAdapter.setHasStableIds(true);
            mUserList.setAdapter(mUserListAdapter);

        }catch (Exception e){
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }






}