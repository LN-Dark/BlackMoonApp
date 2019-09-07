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

public class AdminsSalaPublicaAdapter extends EmptyRecyclerView.Adapter<AdminsSalaPublicaAdapter.AdminsSalaViewHolder> {
    private final ArrayList<UserObject> userList;
    private DatabaseReference AdminsSalaPrivadaRef;

    public AdminsSalaPublicaAdapter(ArrayList<UserObject> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public AdminsSalaPublicaAdapter.AdminsSalaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user_admin, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new AdminsSalaPublicaAdapter.AdminsSalaViewHolder(layoutView);
    }

    private static String FCMServer, SinchKey, SinchSecret;

    @Override
    public void onBindViewHolder(@NonNull final AdminsSalaPublicaAdapter.AdminsSalaViewHolder adminsSalaViewHolder, int i) {

        DatabaseReference mAppKeysRef = FirebaseDatabase.getInstance().getReference().child("appKeys");
        mAppKeysRef.keepSynced(true);
        mAppKeysRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
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


                    AdminsSalaPrivadaRef = FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(userList.get(adminsSalaViewHolder.getAdapterPosition()).getSalaPrivadaID());
                    adminsSalaViewHolder.mName.setText(userList.get(adminsSalaViewHolder.getAdapterPosition()).getName());
                    Picasso.get().load(userList.get(adminsSalaViewHolder.getAdapterPosition()).getImagemPerfilUri()).into(adminsSalaViewHolder.imagemPerfil);
                    if(userList.get(0).getSuperAdminsList().contains(userList.get(adminsSalaViewHolder.getAdapterPosition()).getUid())){
                        adminsSalaViewHolder.bloquear.setChecked(true);
                    }else {
                        adminsSalaViewHolder.bloquear.setChecked(false);
                    }
                    adminsSalaViewHolder.bloquear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!adminsSalaViewHolder.bloquear.isChecked()) {
                                AdminsSalaPrivadaRef.child("admins").child(userList.get(adminsSalaViewHolder.getAdapterPosition()).getUid()).removeValue();
                                JSONObject notification = new JSONObject();
                                JSONObject notifcationBody = new JSONObject();
                                try {
                                    notifcationBody.put("title", v.getContext().getString(R.string.fostedespromovido));
                                    notifcationBody.put("message", v.getResources().getString(R.string.janaoesadmindasala) + " " + userList.get(adminsSalaViewHolder.getAdapterPosition()).getNomeSalaPrivada());

                                    notification.put("to", "/LuaNegra/" + userList.get(adminsSalaViewHolder.getAdapterPosition()).getNotificationKey());
                                    notification.put("data", notifcationBody);
                                } catch (Exception e) {
                                    FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(e.toString());
                                }
                                SendNotification sendNotification = new SendNotification();
                                sendNotification.sendNotification(notification, v.getContext(), FCMServer);
                                Snackbar.make(v, v.getResources().getString(R.string.admindespromovido), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            } else {
                                AdminsSalaPrivadaRef.child("admins").child(userList.get(adminsSalaViewHolder.getAdapterPosition()).getUid()).setValue(true);
                                JSONObject notification = new JSONObject();
                                JSONObject notifcationBody = new JSONObject();
                                try {
                                    notifcationBody.put("title", v.getContext().getString(R.string.fostepromovido));
                                    notifcationBody.put("message", v.getResources().getString(R.string.fostepromovidoaadmin) + " " + userList.get(adminsSalaViewHolder.getAdapterPosition()).getNomeSalaPrivada());

                                    notification.put("to", "/LuaNegra/" + userList.get(adminsSalaViewHolder.getAdapterPosition()).getNotificationKey());
                                    notification.put("data", notifcationBody);
                                } catch (Exception e) {
                                    FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(e.toString());
                                }
                                SendNotification sendNotification = new SendNotification();
                                sendNotification.sendNotification(notification, v.getContext(), FCMServer);
                                Snackbar.make(v, v.getResources().getString(R.string.adminpromovido), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    class AdminsSalaViewHolder extends EmptyRecyclerView.ViewHolder {
        final TextView mName;
        final LinearLayout mLayout;
        final ImageView imagemPerfil;
        final SwitchMaterial bloquear;

        AdminsSalaViewHolder(View view) {
            super(view);
            bloquear = view.findViewById(R.id.user_block_switch);
            imagemPerfil = view.findViewById(R.id.image_user_admin);
            mName = view.findViewById(R.id.name_user_admin);
            mLayout = view.findViewById(R.id.layout_item_user_admin);
        }
    }

}