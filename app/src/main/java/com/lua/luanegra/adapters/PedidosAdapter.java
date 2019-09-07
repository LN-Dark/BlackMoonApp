package com.lua.luanegra.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lua.luanegra.R;
import com.lua.luanegra.activitys.MensagensActivity;
import com.lua.luanegra.callservice.SendNotification;
import com.lua.luanegra.objects.UserObject;
import com.lua.luanegra.tools.EmptyRecyclerView;
import com.squareup.picasso.Picasso;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.NotificationsListViewHolder>  implements Filterable {
    private ArrayList<UserObject> userList;

    private int flagExist = 0;
    private int flagPartnerExist = 0;
    private static  String FCMServer ;
    private Context c;

    public PedidosAdapter(ArrayList<UserObject> userList) {
        fulluserlist = userList;
        this.userList = userList;
    }

    private static ArrayList<UserObject> fulluserlist;
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    userList = fulluserlist;
                } else {
                    ArrayList<UserObject> filteredList = new ArrayList<>();
                    for (UserObject row : fulluserlist) {

                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getName().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    userList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = userList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                userList = (ArrayList<UserObject>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public PedidosAdapter.NotificationsListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new PedidosAdapter.NotificationsListViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PedidosAdapter.NotificationsListViewHolder userListViewHolder, int i) {
        try{
            c = userListViewHolder.itemView.getContext();
            if(userList.get(userListViewHolder.getAdapterPosition()).getUid().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                userListViewHolder.mName.setText(userListViewHolder.itemView.getResources().getString(R.string.mensagens_guardadas));
            }else {
                userListViewHolder.mName.setText(userList.get(userListViewHolder.getAdapterPosition()).getName());
            }

            Picasso.get().load(userList.get(userListViewHolder.getAdapterPosition()).getImagemPerfilUri()).into(userListViewHolder.imagemPerfil);
            if(userList.get(userListViewHolder.getAdapterPosition()).getIsonline().equals("true")){
                userListViewHolder.onlineimage.setVisibility(View.VISIBLE);
                Uri path = Uri.parse("android.resource://com.lua.luanegra/" + R.drawable.online);
                Picasso.get().load(path).into(userListViewHolder.onlineimage);
            }else {
                userListViewHolder.onlineimage.setVisibility(View.INVISIBLE);
            }

            userListViewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(v.getContext());
                    LinearLayout layout = new LinearLayout(v.getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);

                    builder3.setIcon(v.getContext().getDrawable(R.drawable.luanegra_logo));
                    builder3.setTitle(v.getContext().getString(R.string.pedidodeconverca));
                    final TextView espaco9 = new TextView(v.getContext());
                    espaco9.setText(" ");
                    layout.addView(espaco9);
                    final CircleImageView icon = new CircleImageView(v.getContext());
                    LinearLayout.LayoutParams layoutParams  = new LinearLayout.LayoutParams(150, 150);
                    layoutParams.gravity = Gravity.CENTER;
                    icon.setLayoutParams(layoutParams);
                    layout.addView(icon);
                    Picasso.get().load(userList.get(userListViewHolder.getAdapterPosition()).getImagemPerfilUri()).into(icon);
                    final TextView titulo = new TextView(v.getContext());
                    titulo.setText(String.format(v.getResources().getString(R.string.primeironome), userList.get(userListViewHolder.getAdapterPosition()).getName(), userList.get(userListViewHolder.getAdapterPosition()).getPrimeiroNick()));
                    titulo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    titulo.setTextSize(16);
                    layout.addView(titulo);
                    final TextView espaco4 = new TextView(v.getContext());
                    espaco4.setText(" ");
                    layout.addView(espaco4);
                    final TextView bio = new TextView(v.getContext());
                    bio.setText("Bio: \n\n" + userList.get(userListViewHolder.getAdapterPosition()).getBio());
                    bio.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    bio.setTextSize(16);
                    layout.addView(bio);
                    final TextView espaco42 = new TextView(v.getContext());
                    espaco42.setText(" ");
                    layout.addView(espaco42);
                    final TextView titulo2 = new TextView(v.getContext());
                    titulo2.setText(userList.get(userListViewHolder.getAdapterPosition()).getLastOnline());
                    titulo2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    titulo2.setTextSize(13);
                    layout.addView(titulo2);
                    final TextView espaco43 = new TextView(v.getContext());
                    espaco43.setText(" ");
                    layout.addView(espaco43);
                    final TextView datainscri = new TextView(v.getContext());
                    datainscri.setText(v.getResources().getString(R.string.utilizadorregistadodesde)+ "\n" + userList.get(userListViewHolder.getAdapterPosition()).getRegistoData() + "  -  " +userList.get(userListViewHolder.getAdapterPosition()).getRegistoHora());
                    datainscri.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    datainscri.setTextSize(13);
                    layout.addView(datainscri);
                    final TextView espaco433 = new TextView(v.getContext());
                    espaco433.setText(" ");
                    layout.addView(espaco433);
                    builder3.setCancelable(false);
                    builder3.setView(layout);
                    AlertDialog alert = builder3.create();
                    final AlertDialog finalAlert = alert;
                    builder3.setPositiveButton(v.getResources().getString(R.string.fecharapp), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finalAlert.dismiss();
                        }
                    });

                        builder3.setNeutralButton(v.getResources().getString(R.string.conversar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String key = FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).push().getKey();

                                DatabaseReference mAppKeysRef = FirebaseDatabase.getInstance().getReference().child("appKeys");
                                mAppKeysRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()) {

                                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                if ("serverFCM".equals(Objects.requireNonNull(childSnapshot.getKey()))) {
                                                    FCMServer = (childSnapshot.getValue().toString());
                                                    break;
                                                }
                                            }
                                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.exists()){
                                                        if(dataSnapshot.child("chat").exists()){
                                                            for(final DataSnapshot childChatSnapShot : dataSnapshot.child("chat").getChildren()){
                                                                if(Objects.requireNonNull(childChatSnapShot.child("userUID").getValue()).toString().equals(userList.get(userListViewHolder.getAdapterPosition()).getUid())){
                                                                    flagExist = 1;
                                                                    FirebaseDatabase.getInstance().getReference().child("user").child(userList.get(userListViewHolder.getAdapterPosition()).getUid()).child("chat").child(Objects.requireNonNull(childChatSnapShot.getKey())).child("userUID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                    final Intent groupIntent = new Intent(v.getContext(), MensagensActivity.class);
                                                                    groupIntent.putExtra("groupName", userList.get(userListViewHolder.getAdapterPosition()).getName());
                                                                    groupIntent.putExtra("groupUid", childChatSnapShot.getKey());
                                                                    groupIntent.putExtra("partnerUID", userList.get(userListViewHolder.getAdapterPosition()).getUid());
                                                                    groupIntent.putExtra("partnerNotificationKey", userList.get(userListViewHolder.getAdapterPosition()).getNotificationKey());
                                                                    SharedPreferences prefs = v.getContext().getSharedPreferences(childChatSnapShot.getKey(), Context.MODE_PRIVATE);
                                                                    String chatKey = " ";
                                                                    chatKey = prefs.getString(childChatSnapShot.getKey(), " ");
                                                                    if (!chatKey.equals(" ")){
                                                                        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("pedidosComunidade").child(userList.get(userListViewHolder.getAdapterPosition()).getUid()).removeValue();
                                                                        groupIntent.putExtra("key", chatKey);
                                                                        v.getContext().startActivity(groupIntent);
                                                                    } else {
                                                                        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(v.getContext());
                                                                        LinearLayout layout = new LinearLayout(v.getContext());
                                                                        layout.setOrientation(LinearLayout.VERTICAL);

                                                                        builder.setIcon(v.getContext().getDrawable(R.drawable.luanegra_logo));
                                                                        builder.setTitle(v.getResources().getString(R.string.chatprivado));
                                                                        builder.setMessage(v.getContext().getString(R.string.janaotensachavedestaconversa)  + " " + userList.get(userListViewHolder.getAdapterPosition()).getName());
                                                                        layout.setGravity(Gravity.CENTER);
                                                                        final TextView espaco4 = new TextView(v.getContext());
                                                                        espaco4.setText("\n");
                                                                        layout.addView(espaco4);
                                                                        final CircleImageView imagemPerfil = new CircleImageView(v.getContext());
                                                                        layout.addView(imagemPerfil);
                                                                        Picasso.get().load(userList.get(userListViewHolder.getAdapterPosition()).getImagemPerfilUri()).into(imagemPerfil);
                                                                        final TextView espaco2 = new TextView(v.getContext());
                                                                        espaco2.setText(String.format("\n  %s \n", userList.get(userListViewHolder.getAdapterPosition()).getName()));
                                                                        espaco2.setTextSize(16);

                                                                        espaco2.setGravity(Gravity.CENTER);
                                                                        layout.addView(espaco2);
                                                                        android.view.ViewGroup.LayoutParams layoutParams = imagemPerfil.getLayoutParams();
                                                                        layoutParams.width = 150;
                                                                        layoutParams.height = 150;
                                                                        imagemPerfil.setLayoutParams(layoutParams);
                                                                        builder.setView(layout);
                                                                        builder.setPositiveButton(v.getContext().getString(R.string.pedirchavedeacesso), new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {

                                                                                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshotUser) {
                                                                                        if(dataSnapshotUser.exists()){
                                                                                            JSONObject notification = new JSONObject();
                                                                                            JSONObject notifcationBody = new JSONObject();
                                                                                            try {
                                                                                                notifcationBody.put("title", v.getContext().getString(R.string.mensagensprivadas));
                                                                                                notifcationBody.put("message", v.getContext().getString(R.string.outilizador) + dataSnapshotUser.child("name").getValue().toString() + v.getContext().getString(R.string.janaotemchaveeestaapediracesso));

                                                                                                notification.put("to", "/LuaNegra/" + userList.get(userListViewHolder.getAdapterPosition()).getNotificationKey());
                                                                                                notification.put("data", notifcationBody);
                                                                                            } catch (Exception e) {
                                                                                                FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(" " + this.getClass().getName() + " \n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
                                                                                            }
                                                                                            SendNotification sendNotification = new SendNotification();
                                                                                            sendNotification.sendNotification(notification, v.getContext(), FCMServer);
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                    }
                                                                                });
                                                                            }



                                                                        });
                                                                        builder.setNeutralButton(v.getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                dialog.dismiss();
                                                                            }
                                                                        });
                                                                        builder.show();
                                                                    }

                                                                }
                                                            }
                                                            if(flagExist == 0){
                                                                FirebaseDatabase.getInstance().getReference().child("user").child(userList.get(userListViewHolder.getAdapterPosition()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        if(dataSnapshot.exists()){
                                                                            if(dataSnapshot.child("chat").exists()){
                                                                                for(final DataSnapshot childPartnerChatSnapShot : dataSnapshot.child("chat").getChildren()){
                                                                                    if(Objects.requireNonNull(childPartnerChatSnapShot.child("userUID").getValue()).toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                                                        flagPartnerExist = 1;
                                                                                        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chat").child(Objects.requireNonNull(childPartnerChatSnapShot.getKey())).child("userUID").setValue(userList.get(userListViewHolder.getAdapterPosition()).getUid());
                                                                                        final Intent groupIntent = new Intent(v.getContext(), MensagensActivity.class);
                                                                                        groupIntent.putExtra("groupName", userList.get(userListViewHolder.getAdapterPosition()).getName());
                                                                                        groupIntent.putExtra("groupUid", childPartnerChatSnapShot.getKey());
                                                                                        groupIntent.putExtra("partnerUID", userList.get(userListViewHolder.getAdapterPosition()).getUid());
                                                                                        groupIntent.putExtra("partnerNotificationKey", userList.get(userListViewHolder.getAdapterPosition()).getNotificationKey());
                                                                                        SharedPreferences prefs = v.getContext().getSharedPreferences(childPartnerChatSnapShot.getKey(), Context.MODE_PRIVATE);
                                                                                        String chatKey = " ";
                                                                                        chatKey = prefs.getString(childPartnerChatSnapShot.getKey(), " ");
                                                                                        if (!chatKey.equals(" ")){
                                                                                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("pedidosComunidade").child(userList.get(userListViewHolder.getAdapterPosition()).getUid()).removeValue();
                                                                                            groupIntent.putExtra("key", chatKey);
                                                                                            v.getContext().startActivity(groupIntent);
                                                                                        } else {
                                                                                            AlertDialog.Builder builder = new MaterialAlertDialogBuilder(v.getContext());
                                                                                            LinearLayout layout = new LinearLayout(v.getContext());
                                                                                            layout.setOrientation(LinearLayout.VERTICAL);

                                                                                            builder.setIcon(v.getContext().getDrawable(R.drawable.luanegra_logo));
                                                                                            builder.setTitle(v.getResources().getString(R.string.chatprivado));
                                                                                            builder.setMessage(v.getContext().getString(R.string.janaotensachavedestaconversa)  + " " +  userList.get(userListViewHolder.getAdapterPosition()).getName());
                                                                                            layout.setGravity(Gravity.CENTER);
                                                                                            final TextView espaco4 = new TextView(v.getContext());
                                                                                            espaco4.setText("\n");
                                                                                            layout.addView(espaco4);
                                                                                            final CircleImageView imagemPerfil = new CircleImageView(v.getContext());
                                                                                            layout.addView(imagemPerfil);
                                                                                            Picasso.get().load(userList.get(userListViewHolder.getAdapterPosition()).getImagemPerfilUri()).into(imagemPerfil);
                                                                                            final TextView espaco2 = new TextView(v.getContext());
                                                                                            espaco2.setText(String.format("\n  %s \n", userList.get(userListViewHolder.getAdapterPosition()).getName()));
                                                                                            espaco2.setTextSize(16);

                                                                                            espaco2.setGravity(Gravity.CENTER);
                                                                                            layout.addView(espaco2);
                                                                                            android.view.ViewGroup.LayoutParams layoutParams = imagemPerfil.getLayoutParams();
                                                                                            layoutParams.width = 150;
                                                                                            layoutParams.height = 150;
                                                                                            imagemPerfil.setLayoutParams(layoutParams);
                                                                                            builder.setView(layout);
                                                                                            builder.setPositiveButton(v.getContext().getString(R.string.pedirchavedeacesso), new DialogInterface.OnClickListener() {
                                                                                                @Override
                                                                                                public void onClick(DialogInterface dialog, int which) {

                                                                                                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                        @Override
                                                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshotUser) {
                                                                                                            if(dataSnapshotUser.exists()){
                                                                                                                JSONObject notification = new JSONObject();
                                                                                                                JSONObject notifcationBody = new JSONObject();
                                                                                                                try {
                                                                                                                    notifcationBody.put("title", v.getContext().getString(R.string.mensagensprivadas));
                                                                                                                    notifcationBody.put("message", v.getContext().getString(R.string.outilizador) + dataSnapshotUser.child("name").getValue().toString() + v.getContext().getString(R.string.janaotemchaveeestaapediracesso));

                                                                                                                    notification.put("to", "/LuaNegra/" + userList.get(userListViewHolder.getAdapterPosition()).getNotificationKey());
                                                                                                                    notification.put("data", notifcationBody);
                                                                                                                } catch (Exception e) {
                                                                                                                    FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(" " + this.getClass().getName() + " \n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
                                                                                                                }
                                                                                                                SendNotification sendNotification = new SendNotification();
                                                                                                                sendNotification.sendNotification(notification, v.getContext(), FCMServer);
                                                                                                            }
                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                        }
                                                                                                    });
                                                                                                }

                                                                                            });
                                                                                            builder.setNeutralButton(v.getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                                                                                                @Override
                                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                                    dialog.dismiss();
                                                                                                }
                                                                                            });
                                                                                            builder.show();
                                                                                        }
                                                                                    }
                                                                                }
                                                                                if(flagPartnerExist == 0){
                                                                                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chat").child(Objects.requireNonNull(key)).child("userUID").setValue(userList.get(userListViewHolder.getAdapterPosition()).getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            FirebaseDatabase.getInstance().getReference().child("user").child(userList.get(userListViewHolder.getAdapterPosition()).getUid()).child("chat").child(key).child("userUID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    final Intent groupIntent = new Intent(v.getContext(), MensagensActivity.class);
                                                                                                    groupIntent.putExtra("groupName", userList.get(userListViewHolder.getAdapterPosition()).getName());
                                                                                                    groupIntent.putExtra("groupUid", key);
                                                                                                    groupIntent.putExtra("partnerUID", userList.get(userListViewHolder.getAdapterPosition()).getUid());
                                                                                                    groupIntent.putExtra("partnerNotificationKey", userList.get(userListViewHolder.getAdapterPosition()).getNotificationKey());

                                                                                                    AesCbcWithIntegrity.SecretKeys keys = null;
                                                                                                    try {
                                                                                                        keys = AesCbcWithIntegrity.generateKey();
                                                                                                    } catch (GeneralSecurityException e) {
                                                                                                        e.printStackTrace();
                                                                                                    }
                                                                                                    final AesCbcWithIntegrity.SecretKeys finalKeys = keys;
                                                                                                    SharedPreferences prefs = v.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
                                                                                                    SharedPreferences.Editor editor = prefs.edit();
                                                                                                    editor.putString(key, finalKeys.toString());
                                                                                                    editor.apply();
                                                                                                    JSONObject notification = new JSONObject();
                                                                                                    JSONObject notifcationBody = new JSONObject();
                                                                                                    try {
                                                                                                        notifcationBody.put("title", "LॐN_Code");
                                                                                                        notifcationBody.put("message", key + "\n" + finalKeys.toString());

                                                                                                        notification.put("to", "/LuaNegra/" + userList.get(userListViewHolder.getAdapterPosition()).getNotificationKey());
                                                                                                        notification.put("data", notifcationBody);
                                                                                                    } catch (Exception e) {
                                                                                                        FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.toString());
                                                                                                    }
                                                                                                    SendNotification sendNotification = new SendNotification();
                                                                                                    sendNotification.sendNotification(notification, v.getContext(), FCMServer);
                                                                                                    groupIntent.putExtra("key", finalKeys.toString());
                                                                                                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("pedidosComunidade").child(userList.get(userListViewHolder.getAdapterPosition()).getUid()).removeValue();
                                                                                                    v.getContext().startActivity(groupIntent);
                                                                                                }
                                                                                            });

                                                                                        }
                                                                                    });

                                                                                }
                                                                            }else {
                                                                                if(flagPartnerExist == 0){
                                                                                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chat").child(Objects.requireNonNull(key)).child("userUID").setValue(userList.get(userListViewHolder.getAdapterPosition()).getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            FirebaseDatabase.getInstance().getReference().child("user").child(userList.get(userListViewHolder.getAdapterPosition()).getUid()).child("chat").child(key).child("userUID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    final Intent groupIntent = new Intent(v.getContext(), MensagensActivity.class);
                                                                                                    groupIntent.putExtra("groupName", userList.get(userListViewHolder.getAdapterPosition()).getName());
                                                                                                    groupIntent.putExtra("groupUid", key);
                                                                                                    groupIntent.putExtra("partnerUID", userList.get(userListViewHolder.getAdapterPosition()).getUid());
                                                                                                    groupIntent.putExtra("partnerNotificationKey", userList.get(userListViewHolder.getAdapterPosition()).getNotificationKey());
                                                                                                    AesCbcWithIntegrity.SecretKeys keys = null;
                                                                                                    try {
                                                                                                        keys = AesCbcWithIntegrity.generateKey();
                                                                                                    } catch (GeneralSecurityException e) {
                                                                                                        e.printStackTrace();
                                                                                                    }
                                                                                                    final AesCbcWithIntegrity.SecretKeys finalKeys = keys;
                                                                                                    SharedPreferences prefs = v.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
                                                                                                    SharedPreferences.Editor editor = prefs.edit();
                                                                                                    editor.putString(key, finalKeys.toString());
                                                                                                    editor.apply();
                                                                                                    JSONObject notification = new JSONObject();
                                                                                                    JSONObject notifcationBody = new JSONObject();
                                                                                                    try {
                                                                                                        notifcationBody.put("title", "LॐN_Code");
                                                                                                        notifcationBody.put("message", key + "\n" + finalKeys.toString());

                                                                                                        notification.put("to", "/LuaNegra/" + userList.get(userListViewHolder.getAdapterPosition()).getNotificationKey());
                                                                                                        notification.put("data", notifcationBody);
                                                                                                    } catch (Exception e) {
                                                                                                        FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.toString());
                                                                                                    }
                                                                                                    SendNotification sendNotification = new SendNotification();
                                                                                                    sendNotification.sendNotification(notification, v.getContext(), FCMServer);
                                                                                                    groupIntent.putExtra("key", finalKeys.toString());
                                                                                                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("pedidosComunidade").child(userList.get(userListViewHolder.getAdapterPosition()).getUid()).removeValue();
                                                                                                    v.getContext().startActivity(groupIntent);
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });
                                                            }
                                                        }else {
                                                            FirebaseDatabase.getInstance().getReference().child("user").child(userList.get(userListViewHolder.getAdapterPosition()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    if(dataSnapshot.exists()){
                                                                        if(dataSnapshot.child("chat").exists()){
                                                                            for(final DataSnapshot childPartnerChatSnapShot : dataSnapshot.child("chat").getChildren()){
                                                                                if(Objects.requireNonNull(childPartnerChatSnapShot.child("userUID").getValue()).toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                                                    flagPartnerExist = 1;
                                                                                    FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chat").child(Objects.requireNonNull(childPartnerChatSnapShot.getKey())).child("userUID").setValue(userList.get(userListViewHolder.getAdapterPosition()).getUid());
                                                                                    final Intent groupIntent = new Intent(v.getContext(), MensagensActivity.class);
                                                                                    groupIntent.putExtra("groupName", userList.get(userListViewHolder.getAdapterPosition()).getName());
                                                                                    groupIntent.putExtra("groupUid", childPartnerChatSnapShot.getKey());
                                                                                    groupIntent.putExtra("partnerUID", userList.get(userListViewHolder.getAdapterPosition()).getUid());
                                                                                    groupIntent.putExtra("partnerNotificationKey", userList.get(userListViewHolder.getAdapterPosition()).getNotificationKey());
                                                                                    SharedPreferences prefs = v.getContext().getSharedPreferences(childPartnerChatSnapShot.getKey(), Context.MODE_PRIVATE);
                                                                                    String chatKey = " ";
                                                                                    chatKey = prefs.getString(childPartnerChatSnapShot.getKey(), " ");
                                                                                    if (!chatKey.equals(" ")){
                                                                                        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("pedidosComunidade").child(userList.get(userListViewHolder.getAdapterPosition()).getUid()).removeValue();
                                                                                        groupIntent.putExtra("key", chatKey);
                                                                                        v.getContext().startActivity(groupIntent);
                                                                                    } else {
                                                                                        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(v.getContext());
                                                                                        LinearLayout layout = new LinearLayout(v.getContext());
                                                                                        layout.setOrientation(LinearLayout.VERTICAL);

                                                                                        builder.setIcon(v.getContext().getDrawable(R.drawable.luanegra_logo));
                                                                                        builder.setTitle(v.getResources().getString(R.string.chatprivado));
                                                                                        builder.setMessage(v.getContext().getString(R.string.janaotensachavedestaconversa) + userList.get(userListViewHolder.getAdapterPosition()).getName());
                                                                                        layout.setGravity(Gravity.CENTER);
                                                                                        final TextView espaco4 = new TextView(v.getContext());
                                                                                        espaco4.setText("\n");
                                                                                        layout.addView(espaco4);
                                                                                        final CircleImageView imagemPerfil = new CircleImageView(v.getContext());
                                                                                        layout.addView(imagemPerfil);
                                                                                        Picasso.get().load(userList.get(userListViewHolder.getAdapterPosition()).getImagemPerfilUri()).into(imagemPerfil);
                                                                                        final TextView espaco2 = new TextView(v.getContext());
                                                                                        espaco2.setText(String.format("\n  %s \n", userList.get(userListViewHolder.getAdapterPosition()).getName()));
                                                                                        espaco2.setTextSize(16);

                                                                                        espaco2.setGravity(Gravity.CENTER);
                                                                                        layout.addView(espaco2);
                                                                                        android.view.ViewGroup.LayoutParams layoutParams = imagemPerfil.getLayoutParams();
                                                                                        layoutParams.width = 150;
                                                                                        layoutParams.height = 150;
                                                                                        imagemPerfil.setLayoutParams(layoutParams);
                                                                                        builder.setView(layout);
                                                                                        builder.setPositiveButton(v.getContext().getString(R.string.pedirchavedeacesso), new DialogInterface.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                    @Override
                                                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshotUser) {
                                                                                                        if(dataSnapshotUser.exists()){
                                                                                                            JSONObject notification = new JSONObject();
                                                                                                            JSONObject notifcationBody = new JSONObject();
                                                                                                            try {
                                                                                                                notifcationBody.put("title", v.getContext().getString(R.string.mensagensprivadas));
                                                                                                                notifcationBody.put("message", v.getContext().getString(R.string.outilizador) + dataSnapshotUser.child("name").getValue().toString() + v.getContext().getString(R.string.janaotemchaveeestaapediracesso));

                                                                                                                notification.put("to", "/LuaNegra/" + userList.get(userListViewHolder.getAdapterPosition()).getNotificationKey());
                                                                                                                notification.put("data", notifcationBody);
                                                                                                            } catch (Exception e) {
                                                                                                                FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(" " + this.getClass().getName() + " \n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
                                                                                                            }
                                                                                                            SendNotification sendNotification = new SendNotification();
                                                                                                            sendNotification.sendNotification(notification, v.getContext(), FCMServer);
                                                                                                        }
                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                        });
                                                                                        builder.setNeutralButton(v.getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                                dialog.dismiss();
                                                                                            }
                                                                                        });
                                                                                        builder.show();
                                                                                    }
                                                                                }
                                                                            }
                                                                            if(flagPartnerExist == 0){
                                                                                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chat").child(Objects.requireNonNull(key)).child("userUID").setValue(userList.get(userListViewHolder.getAdapterPosition()).getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        FirebaseDatabase.getInstance().getReference().child("user").child(userList.get(userListViewHolder.getAdapterPosition()).getUid()).child("chat").child(key).child("userUID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                final Intent groupIntent = new Intent(v.getContext(), MensagensActivity.class);
                                                                                                groupIntent.putExtra("groupName", userList.get(userListViewHolder.getAdapterPosition()).getName());
                                                                                                groupIntent.putExtra("groupUid", key);
                                                                                                groupIntent.putExtra("partnerUID", userList.get(userListViewHolder.getAdapterPosition()).getUid());
                                                                                                groupIntent.putExtra("partnerNotificationKey", userList.get(userListViewHolder.getAdapterPosition()).getNotificationKey());
                                                                                                AesCbcWithIntegrity.SecretKeys keys = null;
                                                                                                try {
                                                                                                    keys = AesCbcWithIntegrity.generateKey();
                                                                                                } catch (GeneralSecurityException e) {
                                                                                                    e.printStackTrace();
                                                                                                }
                                                                                                final AesCbcWithIntegrity.SecretKeys finalKeys = keys;
                                                                                                SharedPreferences prefs = v.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
                                                                                                SharedPreferences.Editor editor = prefs.edit();
                                                                                                editor.putString(key, finalKeys.toString());
                                                                                                editor.apply();
                                                                                                JSONObject notification = new JSONObject();
                                                                                                JSONObject notifcationBody = new JSONObject();
                                                                                                try {
                                                                                                    notifcationBody.put("title", "LॐN_Code");
                                                                                                    notifcationBody.put("message", key + "\n" + finalKeys.toString());

                                                                                                    notification.put("to", "/LuaNegra/" + userList.get(userListViewHolder.getAdapterPosition()).getNotificationKey());
                                                                                                    notification.put("data", notifcationBody);
                                                                                                } catch (Exception e) {
                                                                                                    FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.toString());
                                                                                                }
                                                                                                SendNotification sendNotification = new SendNotification();
                                                                                                sendNotification.sendNotification(notification, v.getContext(), FCMServer);
                                                                                                groupIntent.putExtra("key", finalKeys.toString());
                                                                                                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("pedidosComunidade").child(userList.get(userListViewHolder.getAdapterPosition()).getUid()).removeValue();
                                                                                                v.getContext().startActivity(groupIntent);
                                                                                            }
                                                                                        });

                                                                                    }
                                                                                });


                                                                            }
                                                                        }else {
                                                                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chat").child(Objects.requireNonNull(key)).child("userUID").setValue(userList.get(userListViewHolder.getAdapterPosition()).getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    FirebaseDatabase.getInstance().getReference().child("user").child(userList.get(userListViewHolder.getAdapterPosition()).getUid()).child("chat").child(key).child("userUID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            final Intent groupIntent = new Intent(v.getContext(), MensagensActivity.class);
                                                                                            groupIntent.putExtra("groupName", userList.get(userListViewHolder.getAdapterPosition()).getName());
                                                                                            groupIntent.putExtra("groupUid", key);
                                                                                            groupIntent.putExtra("partnerUID", userList.get(userListViewHolder.getAdapterPosition()).getUid());
                                                                                            groupIntent.putExtra("partnerNotificationKey", userList.get(userListViewHolder.getAdapterPosition()).getNotificationKey());

                                                                                            AesCbcWithIntegrity.SecretKeys keys = null;
                                                                                            try {
                                                                                                keys = AesCbcWithIntegrity.generateKey();
                                                                                            } catch (GeneralSecurityException e) {
                                                                                                e.printStackTrace();
                                                                                            }
                                                                                            final AesCbcWithIntegrity.SecretKeys finalKeys = keys;
                                                                                            SharedPreferences prefs = v.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
                                                                                            SharedPreferences.Editor editor = prefs.edit();
                                                                                            editor.putString(key, finalKeys.toString());
                                                                                            editor.apply();
                                                                                            JSONObject notification = new JSONObject();
                                                                                            JSONObject notifcationBody = new JSONObject();
                                                                                            try {
                                                                                                notifcationBody.put("title", "LॐN_Code");
                                                                                                notifcationBody.put("message", key + "\n" + finalKeys.toString());

                                                                                                notification.put("to", "/LuaNegra/" + userList.get(userListViewHolder.getAdapterPosition()).getNotificationKey());
                                                                                                notification.put("data", notifcationBody);
                                                                                            } catch (Exception e) {
                                                                                                FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.toString());
                                                                                            }
                                                                                            SendNotification sendNotification = new SendNotification();
                                                                                            sendNotification.sendNotification(notification, v.getContext(), FCMServer);
                                                                                            groupIntent.putExtra("key", finalKeys.toString());
                                                                                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("pedidosComunidade").child(userList.get(userListViewHolder.getAdapterPosition()).getUid()).removeValue();
                                                                                            v.getContext().startActivity(groupIntent);
                                                                                        }
                                                                                    });

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
                                                    }
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

                            }
                        });
                    alert = builder3.create();
                    alert.show();
                }
            });
            FirebaseDatabase.getInstance().getReference().child("admins").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(DataSnapshot childSnapShot : dataSnapshot.getChildren()){
                            if(userListViewHolder.getAdapterPosition() > -1){
                                if(userList.get(userListViewHolder.getAdapterPosition()).getUid().equals(childSnapShot.getKey())){
                                    // userListViewHolder.mName.setTextColor(userListViewHolder.itemView.getContext().getColor(R.color.colorRedAdmin));
                                }
                            }
                        }
                        FirebaseDatabase.getInstance().getReference().child("superAdmin").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot22) {
                                if(dataSnapshot22.exists()){
                                    for(DataSnapshot childSnapShot22 : dataSnapshot22.getChildren()){
                                        if(userListViewHolder.getAdapterPosition() > -1){
                                            if(userList.get(userListViewHolder.getAdapterPosition()).getUid().equals(childSnapShot22.getKey())){
                                                //    userListViewHolder.mName.setTextColor(userListViewHolder.itemView.getContext().getColor(R.color.colorRedAdmin));
                                            }
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

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            if(userList.get(userListViewHolder.getAdapterPosition()).getPatrono().equals("true")){
                userListViewHolder.mName.setTextColor(userListViewHolder.itemView.getContext().getColor(R.color.colorGold));
            }

        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(" " + this.getClass().getName() + " \n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
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


    class NotificationsListViewHolder extends EmptyRecyclerView.ViewHolder{
        final TextView mName;
        final LinearLayout mLayout ;
        final ImageView imagemPerfil;
        final ImageView onlineimage;
        NotificationsListViewHolder(View view){
            super(view);
            imagemPerfil = view.findViewById(R.id.image_user);
            mName = view.findViewById(R.id.name);
            mLayout = view.findViewById(R.id.layout_item_user);
            onlineimage = view.findViewById(R.id.image_user_online);
            onlineimage.setVisibility(View.INVISIBLE);
        }

    }


}
