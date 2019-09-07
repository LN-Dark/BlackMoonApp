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

public class AdminAdapter extends EmptyRecyclerView.Adapter<AdminAdapter.AdminUserListViewHolder> {

    private final ArrayList<UserObject> userList;
    private final DatabaseReference bloquedUsersRef = FirebaseDatabase.getInstance().getReference().child("bloqued_users");

    public AdminAdapter(ArrayList<UserObject> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public AdminAdapter.AdminUserListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user_admin, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new AdminUserListViewHolder(layoutView);
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
    public void onBindViewHolder(@NonNull final AdminAdapter.AdminUserListViewHolder adminUserListViewHolder, int i) {
        try{
            GetAppKeys();
        adminUserListViewHolder.mName.setText(userList.get(adminUserListViewHolder.getAdapterPosition()).getName());
        Picasso.get().load( userList.get(adminUserListViewHolder.getAdapterPosition()).getImagemPerfilUri()).into(adminUserListViewHolder.imagemPerfil);
        bloquedUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        if(Objects.equals(childSnapshot.getKey(), userList.get(adminUserListViewHolder.getAdapterPosition()).getUid())){
                            adminUserListViewHolder.bloquear.setChecked(true);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        adminUserListViewHolder.bloquear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!adminUserListViewHolder.bloquear.isChecked()){
                    bloquedUsersRef.child(userList.get(adminUserListViewHolder.getAdapterPosition()).getUid()).removeValue();
                    JSONObject notification = new JSONObject();
                    JSONObject notifcationBody = new JSONObject();
                    try {
                        notifcationBody.put("title", v.getContext().getString(R.string.fostedesbloqueado));
                        notifcationBody.put("message", v.getContext().getString(R.string.estasemquarentena));

                        notification.put("to", "/LuaNegra/" + userList.get(adminUserListViewHolder.getAdapterPosition()).getNotificationKey());
                        notification.put("data", notifcationBody);
                    } catch (Exception e) {
                        FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(e.toString());
                    }
                    SendNotification sendNotification = new SendNotification();
                    sendNotification.sendNotification(notification, v.getContext(), FCMServer);
                    for(int i = 0 ; i < userList.get(0).getSuperAdminsList().size(); i++){
                        JSONObject notification2 = new JSONObject();
                        JSONObject notifcationBody2 = new JSONObject();
                        try {
                            notifcationBody2.put("title", "✶ Admins ✶");
                            for(int a = 0; a < userList.size(); a++){
                                if(userList.get(a).getUid().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                    notifcationBody2.put("message", "✶ " + userList.get(adminUserListViewHolder.getAdapterPosition()).getName() + " " + v.getContext().getString(R.string.foidesbloqueadopor) + " " + userList.get(a).getName() + " ✶");
                                break;
                                }
                            }
                            notification2.put("to", "/LuaNegra/" + userList.get(adminUserListViewHolder.getAdapterPosition()).getSuperAdminsList().get(i));
                            notification2.put("data", notifcationBody2);
                        } catch (Exception e) {
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
                        }
                        SendNotification sendNotification2 = new SendNotification();
                        sendNotification2.sendNotification(notification2, v.getContext(), FCMServer);
                    }
                    Snackbar.make(v, v.getContext().getString(R.string.utilizadordesbloqueado), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else {
                    bloquedUsersRef.child(userList.get(adminUserListViewHolder.getAdapterPosition()).getUid()).setValue(true);
                    JSONObject notification = new JSONObject();
                    JSONObject notifcationBody = new JSONObject();
                    try {
                        notifcationBody.put("title", v.getContext().getString(R.string.fostedesbloqueadoo));
                        notifcationBody.put("message", "✶ Já está, aproveita :D ✶");

                        notification.put("to", "/LuaNegra/" + userList.get(adminUserListViewHolder.getAdapterPosition()).getNotificationKey());
                        notification.put("data", notifcationBody);
                    } catch (Exception e) {
                        FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(e.toString());
                    }
                    SendNotification sendNotification = new SendNotification();
                    sendNotification.sendNotification(notification, v.getContext(), FCMServer);
                    for(int i = 0 ; i < userList.get(0).getSuperAdminsList().size(); i++){
                        JSONObject notification2 = new JSONObject();
                        JSONObject notifcationBody2 = new JSONObject();
                        try {
                            notifcationBody2.put("title", "✶ Admins ✶");
                            for(int a = 0; a < userList.size(); a++){
                                if(userList.get(a).getUid().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                    notifcationBody2.put("message", "✶ " + userList.get(adminUserListViewHolder.getAdapterPosition()).getName() + " " + v.getResources().getString(R.string.foibloqueadopor) + " " + userList.get(a).getName() + " ✶");
                                    break;
                                }
                            }

                            notification2.put("to", "/LuaNegra/" + userList.get(adminUserListViewHolder.getAdapterPosition()).getSuperAdminsList().get(i));
                            notification2.put("data", notifcationBody2);
                        } catch (Exception e) {
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
                        }
                        SendNotification sendNotification2 = new SendNotification();
                        sendNotification2.sendNotification(notification2, v.getContext(), FCMServer);
                    }
                    Snackbar.make(v, v.getContext().getString(R.string.utilizadorbloqueado), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        for(int f = 0; f < userList.get(0).getSuperAdminsList().size(); f++){
            if(userList.get(0).getSuperAdminsList().contains(userList.get(adminUserListViewHolder.getAdapterPosition()).getUid())){
                adminUserListViewHolder.mLayout.setVisibility(View.GONE);
                ViewGroup.LayoutParams layoutParams = adminUserListViewHolder.mLayout.getLayoutParams();
                layoutParams.height = 0;
                adminUserListViewHolder.mLayout.setLayoutParams(layoutParams);
            }
        }
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

    class AdminUserListViewHolder extends EmptyRecyclerView.ViewHolder{
        final TextView mName;
        final LinearLayout mLayout ;
        final ImageView imagemPerfil;
        final SwitchMaterial bloquear;
        AdminUserListViewHolder(View view){
            super(view);
            bloquear = view.findViewById(R.id.user_block_switch);
            imagemPerfil = view.findViewById(R.id.image_user_admin);
            mName = view.findViewById(R.id.name_user_admin);
            mLayout = view.findViewById(R.id.layout_item_user_admin);
        }
    }
}
