package com.lua.luanegra.fragments.share;

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
import androidx.appcompat.widget.Toolbar;
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
import com.lua.luanegra.adapters.SharesAdapter;
import com.lua.luanegra.objects.SharesObject;
import com.lua.luanegra.objects.UserObject;
import com.lua.luanegra.tools.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class ShareFragment extends Fragment {
    private SharesAdapter mSharesListAdapter;
    private ArrayList<SharesObject> sharesList;
    private DatabaseReference sharesRef;
    private ChildEventListener mSharesListener;
    private View root;
    private TextView aindanaotens;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

         root = inflater.inflate(R.layout.fragment_share, container, false);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setLogo(getActivity().getDrawable(R.drawable.luanegra_logo));
        toolbar.setSubtitle("" + getString(R.string.hall_of_fame));
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
                mSharesListAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    private ArrayList<UserObject> fullUserList;
    private DatabaseReference fullUserListRef;
    private ChildEventListener childEventListenerUserList;
    private Long numerousers;
    private void GetAllUsers(){
        try {
            FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        numerousers = (dataSnapshot.getChildrenCount() -1);
                        childEventListenerUserList = fullUserListRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                if(dataSnapshot.exists()){

                                    if(!Objects.requireNonNull(dataSnapshot.getKey()).equals("chat")){
                                        UserObject newUser = new UserObject(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString(), dataSnapshot.getKey());
                                        newUser.setNotificationKey(Objects.requireNonNull(dataSnapshot.child("notificationKey").getValue()).toString());
                                        newUser.setImagemPerfilUri(Objects.requireNonNull(dataSnapshot.child("profile_image").getValue()).toString());
                                        newUser.setIsonline(Objects.requireNonNull(dataSnapshot.child("online").getValue()).toString());
                                        newUser.setRegistoData(Objects.requireNonNull(dataSnapshot.child("registerDate").getValue()).toString());
                                        newUser.setRegistoHora(Objects.requireNonNull(dataSnapshot.child("registerTime").getValue()).toString());
                                        newUser.setLastOnline("Ultima vez online: \n" + Objects.requireNonNull(dataSnapshot.child("onlineDate").getValue()).toString() + "  *  " + Objects.requireNonNull(dataSnapshot.child("onlineTime").getValue()).toString());
                                        fullUserList.add(newUser);
                                    }
                                    if (numerousers == fullUserList.size()){
                                        getNewSharesList();
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


        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}

    }

    @Override
    public void onPause() {
        super.onPause();
        if(mSharesListener != null){
            sharesRef.removeEventListener(mSharesListener);
        }
        if(childEventListenerUserList != null){
            fullUserListRef.removeEventListener(childEventListenerUserList);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSharesListener != null){
            sharesRef.removeEventListener(mSharesListener);
        }
        if(childEventListenerUserList != null){
            fullUserListRef.removeEventListener(childEventListenerUserList);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sharesList = new ArrayList<>();
        fullUserList = new ArrayList<>();
        fullUserListRef = FirebaseDatabase.getInstance().getReference().child("user");
        fullUserListRef.keepSynced(true);
        sharesRef = FirebaseDatabase.getInstance().getReference().child("shares");
        sharesRef.keepSynced(true);
        InitializeRecyclerView();
        GetAllUsers();
    }

    private void getNewSharesList() {
        try{
            aindanaotens = root.findViewById(R.id.txt_naotens_shares);
        sharesList.clear();
        mSharesListener = sharesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    Collections.reverse(sharesList);
                    String text = "", url, data = "", hora = "", sharecreator = "", imageuriCreator = "", UidCreator = "";
                    ArrayList<String> mediaUrlList = new ArrayList<>();
                    if (dataSnapshot.child("data").getValue() != null) {
                        data = Objects.requireNonNull(dataSnapshot.child("data").getValue()).toString();
                    }
                    if (dataSnapshot.child("hora").getValue() != null) {
                        hora = Objects.requireNonNull(dataSnapshot.child("hora").getValue()).toString();
                    }
                    if (dataSnapshot.child("text").getValue() != null) {
                        text = Objects.requireNonNull(dataSnapshot.child("text").getValue()).toString();
                    }
                    if (dataSnapshot.child("shares").getChildrenCount() > 0) {
                        for (DataSnapshot mediaSnapshot : dataSnapshot.child("shares").getChildren()){
                            mediaUrlList.add(Objects.requireNonNull(mediaSnapshot.getValue()).toString());
                        }
                    }
                    url = mediaUrlList.get(0);
                    for(int g = 0; g < fullUserList.size();g++){
                       if(fullUserList.get(g).getUid().equals(Objects.requireNonNull(dataSnapshot.child("creator").getValue()).toString())){
                           UidCreator = Objects.requireNonNull(dataSnapshot.child("creator").getValue()).toString();
                           sharecreator = fullUserList.get(g).getName();
                           imageuriCreator = fullUserList.get(g).getImagemPerfilUri();
                           break;
                       }
                    }
                    SharesObject sharesObject = new SharesObject(url ,dataSnapshot.getKey(),data, hora, text, sharecreator, imageuriCreator);
                    sharesObject.setSHareUiDCreator(UidCreator);
                    sharesList.add(sharesObject);
                    Collections.reverse(sharesList);
                    mSharesListAdapter.notifyDataSetChanged();
                    aindanaotens.setVisibility(View.GONE);
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
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }
    }

    private void InitializeRecyclerView() {
        try{
        EmptyRecyclerView mSharesList = root.findViewById(R.id.shares_List);
        mSharesList.setNestedScrollingEnabled(false);
        mSharesList.setHasFixedSize(true);
        EmptyRecyclerView.LayoutManager mSharesListLayoutManager = new LinearLayoutManager(getContext(), EmptyRecyclerView.VERTICAL, false);
        mSharesList.setLayoutManager(mSharesListLayoutManager);
        mSharesListAdapter = new SharesAdapter(sharesList);
        mSharesList.setAdapter(mSharesListAdapter);
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }
    }
}