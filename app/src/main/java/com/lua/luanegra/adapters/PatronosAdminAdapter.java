package com.lua.luanegra.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lua.luanegra.R;
import com.lua.luanegra.callservice.SendNotification;
import com.lua.luanegra.objects.UserObject;
import com.lua.luanegra.tools.EmptyRecyclerView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class PatronosAdminAdapter extends EmptyRecyclerView.Adapter<PatronosAdminAdapter.PatronosViewHolder> {

    private final ArrayList<UserObject> userList;
    private final DatabaseReference bloquedUsersRef = FirebaseDatabase.getInstance().getReference().child("user");

    public PatronosAdminAdapter(ArrayList<UserObject> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public PatronosAdminAdapter.PatronosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user_admin, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new PatronosAdminAdapter.PatronosViewHolder(layoutView);
    }

    private static String FCMServer, SinchKey, SinchSecret;
    private void GetAppKeys(){
        try {
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
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }


    @Override
    public void onBindViewHolder(@NonNull final PatronosAdminAdapter.PatronosViewHolder patronoViewHolder, int i) {
        try{
            GetAppKeys();
            patronoViewHolder.mName.setText(userList.get(patronoViewHolder.getAdapterPosition()).getName());
            Picasso.get().load( userList.get(patronoViewHolder.getAdapterPosition()).getImagemPerfilUri()).into(patronoViewHolder.imagemPerfil);
            bloquedUsersRef.child(userList.get(patronoViewHolder.getAdapterPosition()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                            if(dataSnapshot.child("patrono").getValue().toString().equals("true")){
                                patronoViewHolder.bloquear.setChecked(true);
                            }else {
                                patronoViewHolder.bloquear.setChecked(false);
                            }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            patronoViewHolder.bloquear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!patronoViewHolder.bloquear.isChecked()){
                        bloquedUsersRef.child(userList.get(patronoViewHolder.getAdapterPosition()).getUid()).child("patrono").setValue("false");
                        JSONObject notification = new JSONObject();
                        JSONObject notifcationBody = new JSONObject();
                        try {
                            notifcationBody.put("title",  v.getContext().getString(R.string.patrono));
                            notifcationBody.put("message",  v.getContext().getString(R.string.janaoespatrono));
                            notification.put("to", "/LuaNegra/" + userList.get(patronoViewHolder.getAdapterPosition()).getNotificationKey());
                            notification.put("data", notifcationBody);
                        } catch (Exception e) {
                            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(e.toString());
                        }
                        SendNotification sendNotification = new SendNotification();
                        sendNotification.sendNotification(notification, v.getContext(), FCMServer);
                        Snackbar.make(v, v.getContext().getString(R.string.utilizadordespromovidodepatrono), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }else {
                        bloquedUsersRef.child(userList.get(patronoViewHolder.getAdapterPosition()).getUid()).child("patrono").setValue("true");
                        JSONObject notification = new JSONObject();
                        JSONObject notifcationBody = new JSONObject();
                        try {
                            notifcationBody.put("title", patronoViewHolder.itemView.getContext().getString(R.string.patrono));
                            notifcationBody.put("message", v.getContext().getString(R.string.agoraespatrono));

                            notification.put("to", "/LuaNegra/" + userList.get(patronoViewHolder.getAdapterPosition()).getNotificationKey());
                            notification.put("data", notifcationBody);
                        } catch (Exception e) {
                            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(e.toString());
                        }
                        SendNotification sendNotification = new SendNotification();
                        sendNotification.sendNotification(notification, v.getContext(), FCMServer);
                        Snackbar.make(v, v.getContext().getString(R.string.utilizadorpromovidoapatrono), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return userList == null ? 0 : userList.size();
    }

    class PatronosViewHolder extends EmptyRecyclerView.ViewHolder{
        final TextView mName;
        final LinearLayout mLayout ;
        final ImageView imagemPerfil;
        final SwitchMaterial bloquear;
        PatronosViewHolder(View view){
            super(view);
            bloquear = view.findViewById(R.id.user_block_switch);
            imagemPerfil = view.findViewById(R.id.image_user_admin);
            mName = view.findViewById(R.id.name_user_admin);
            mLayout = view.findViewById(R.id.layout_item_user_admin);
        }
    }
}
