package com.lua.luanegra.fragments.videos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lua.luanegra.R;
import com.lua.luanegra.adapters.VideoAdapter;
import com.lua.luanegra.objects.UserObject;
import com.lua.luanegra.objects.VideoObject;
import com.lua.luanegra.tools.EmptyRecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class VideosFragment extends Fragment {
    private EmptyRecyclerView.Adapter mVideoListAdapter;
    private ArrayList<VideoObject> videoList;
    private DatabaseReference videoRef;
    private String videoText;
    private String videoIcon;
    private String videourl;
    private EditText linkvideo;
    private final ArrayList<String> currentUserInfo = new ArrayList<>();
    private View root;
    private TextView aindanaotens;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
         root = inflater.inflate(R.layout.fragment_videos, container, false);
        videoList = new ArrayList<>();
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setLogo(getActivity().getDrawable(R.drawable.luanegra_logo));
        toolbar.setSubtitle("" + getString(R.string.youtube));
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        videoRef = FirebaseDatabase.getInstance().getReference().child("videos");
        fullUserList = new ArrayList<>();
        fullUserListRef = FirebaseDatabase.getInstance().getReference().child("user");
        fullUserListRef.keepSynced(true);
        videoRef.keepSynced(true);
        InitializeRecyclerView();
        GetAllUsers();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mVideoListener != null){
            videoRef.removeEventListener(mVideoListener);
        }
        if(childEventListenerUserList != null){
            fullUserListRef.removeEventListener(childEventListenerUserList);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mVideoListener != null){
            videoRef.removeEventListener(mVideoListener);
        }
        if(childEventListenerUserList != null){
            fullUserListRef.removeEventListener(childEventListenerUserList);
        }
    }

    private void CreateNewVideo(final String noticia, String videourl) {
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
        videoMap.put("text", noticia);
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
                Toast.makeText(getContext(), getString(R.string.videopublicadocomsucesso), Toast.LENGTH_LONG).show();
            }
        });
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }
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
                                        getNewVideoList();
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
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }

    }


    private ChildEventListener mVideoListener;
    private void getNewVideoList() {
        try{
            aindanaotens = root.findViewById(R.id.txt_naotens_videos);
            videoList.clear();
            mVideoListener = videoRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists()){
                        Collections.reverse(videoList);
                            String text = "", url = "", data = "", hora = "", videocreator = "", imageuriCreator = "", UiDCreator = "";
                            if (dataSnapshot.child("data").getValue() != null) {
                                data = Objects.requireNonNull(dataSnapshot.child("data").getValue()).toString();
                            }
                            if (dataSnapshot.child("hora").getValue() != null) {
                                hora = Objects.requireNonNull(dataSnapshot.child("hora").getValue()).toString();
                            }
                            if (dataSnapshot.child("text").getValue() != null) {
                                text = Objects.requireNonNull(dataSnapshot.child("text").getValue()).toString();
                            }
                            if (dataSnapshot.child("url").getValue() != null) {
                                url = Objects.requireNonNull(dataSnapshot.child("url").getValue()).toString();
                            }
                        for(int g = 0; g < fullUserList.size();g++){
                            if(fullUserList.get(g).getUid().equals(Objects.requireNonNull(dataSnapshot.child("creator").getValue()).toString())){
                                UiDCreator = Objects.requireNonNull(dataSnapshot.child("creator").getValue()).toString();
                                videocreator = fullUserList.get(g).getName();
                                imageuriCreator = fullUserList.get(g).getImagemPerfilUri();
                                break;
                            }
                        }
                            VideoObject videoObject = new VideoObject(url ,dataSnapshot.getKey(),data, hora, text, videocreator,imageuriCreator);
                        videoObject.setCreatorUID(UiDCreator);
                            videoList.add(videoObject);
                        Collections.reverse(videoList);
                            mVideoListAdapter.notifyDataSetChanged();
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
        EmptyRecyclerView mVideoList = root.findViewById(R.id.recycler_video_tab);
        mVideoList.setNestedScrollingEnabled(false);
        mVideoList.setHasFixedSize(true);
        EmptyRecyclerView.LayoutManager mVideoListLayoutManager = new LinearLayoutManager(getContext(), EmptyRecyclerView.VERTICAL, false);
        mVideoList.setLayoutManager(mVideoListLayoutManager);
        mVideoListAdapter = new VideoAdapter(videoList);
        mVideoList.setAdapter(mVideoListAdapter);
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }
    }
}