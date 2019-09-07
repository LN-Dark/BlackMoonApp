package com.lua.luanegra.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

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
import com.lua.luanegra.callservice.SendNotification;
import com.lua.luanegra.objects.UserObject;
import com.lua.luanegra.tools.EmptyRecyclerView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PedidosSalaPublicaAdapter extends EmptyRecyclerView.Adapter<PedidosSalaPublicaAdapter.PedidosViewHolder> {
    private final ArrayList<UserObject> userList;
    private DatabaseReference bloquedUsersRef;

    public PedidosSalaPublicaAdapter(ArrayList<UserObject> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public PedidosSalaPublicaAdapter.PedidosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user_toshare, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new PedidosSalaPublicaAdapter.PedidosViewHolder(layoutView);
    }

    private static String FCMServer, SinchKey, SinchSecret;

    @Override
    public void onBindViewHolder(@NonNull final PedidosSalaPublicaAdapter.PedidosViewHolder pedidosViewHolder, int i) {

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

                    pedidosViewHolder.cardView.setCardBackgroundColor(pedidosViewHolder.itemView.getContext().getColor(R.color.colorChatLighter));
                    pedidosViewHolder.mName.setText(userList.get(pedidosViewHolder.getAdapterPosition()).getName());
                    Picasso.get().load(userList.get(pedidosViewHolder.getAdapterPosition()).getImagemPerfilUri()).into(pedidosViewHolder.imagemPerfil);
                    pedidosViewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            final AlertDialog.Builder builder3= new MaterialAlertDialogBuilder(v.getContext());
                            LinearLayout layout = new LinearLayout(v.getContext());
                            layout.setOrientation(LinearLayout.VERTICAL);

                            builder3.setIcon(v.getContext().getDrawable(R.drawable.luanegra_logo));
                            builder3.setTitle(v.getResources().getString(R.string.pedidodeacesso));
                            final CircleImageView icon = new CircleImageView(v.getContext());
                            LinearLayout.LayoutParams layoutParams  = new LinearLayout.LayoutParams(200, 200);
                            layoutParams.gravity = Gravity.CENTER;
                            icon.setLayoutParams(layoutParams);
                            layout.addView(icon);
                            Picasso.get().load(userList.get(pedidosViewHolder.getAdapterPosition()).getImagemPerfilUri()).into(icon);
                            final TextView espaco4 = new TextView(v.getContext());
                            espaco4.setText(" ");
                            layout.addView(espaco4);
                            final TextView NomeSala = new TextView(v.getContext());
                            NomeSala.setText(String.format(v.getResources().getString(R.string.datapedido), userList.get(pedidosViewHolder.getAdapterPosition()).getName(), userList.get(pedidosViewHolder.getAdapterPosition()).getDataPedido(), userList.get(pedidosViewHolder.getAdapterPosition()).getHoraPedido()));

                            NomeSala.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                            NomeSala.setTextSize(15);
                            layout.addView(NomeSala);
                            final TextView espaco7 = new TextView(v.getContext());
                            espaco7.setText("\n");
                            layout.addView(espaco7);
                            builder3.setCancelable(false);
                            builder3.setPositiveButton(pedidosViewHolder.itemView.getResources().getString(R.string.aceitar), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(userList.get(pedidosViewHolder.getAdapterPosition()).getSalaPrivadaID()).child("users").child(userList.get(pedidosViewHolder.getAdapterPosition()).getUid()).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(userList.get(pedidosViewHolder.getAdapterPosition()).getSalaPrivadaID()).child("pedidos").child(userList.get(pedidosViewHolder.getAdapterPosition()).getPedidoID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    JSONObject notification2 = new JSONObject();
                                                    JSONObject notifcationBody2 = new JSONObject();
                                                    try {
                                                        notifcationBody2.put("title", "LॐN_Code");
                                                        SharedPreferences prefs = v.getContext().getSharedPreferences(userList.get(pedidosViewHolder.getAdapterPosition()).getSalaPrivadaID(), Context.MODE_PRIVATE);
                                                        String chatKey = " ";
                                                        chatKey = prefs.getString(userList.get(pedidosViewHolder.getAdapterPosition()).getSalaPrivadaID(), " ");

                                                        notifcationBody2.put("message", userList.get(pedidosViewHolder.getAdapterPosition()).getSalaPrivadaID() + "\n" +  chatKey);

                                                        notification2.put("to", "/LuaNegra/" + userList.get(pedidosViewHolder.getAdapterPosition()).getNotificationKey());
                                                        notification2.put("data", notifcationBody2);
                                                    } catch (Exception e) {
                                                        FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(e.toString());
                                                    }
                                                    SendNotification sendNotification2 = new SendNotification();
                                                    sendNotification2.sendNotification(notification2, v.getContext(), FCMServer);

                                                    JSONObject notification = new JSONObject();
                                                    JSONObject notifcationBody = new JSONObject();
                                                    try {
                                                        notifcationBody.put("title", v.getContext().getString(R.string.pedidoaceite));
                                                        notifcationBody.put("message", v.getResources().getString(R.string.oteupedidodeacesso) + " " + userList.get(pedidosViewHolder.getAdapterPosition()).getNomeSalaPrivada() + " " + v.getContext().getString(R.string.foiaceite));

                                                        notification.put("to", "/LuaNegra/" + userList.get(pedidosViewHolder.getAdapterPosition()).getNotificationKey());
                                                        notification.put("data", notifcationBody);
                                                    } catch (Exception e) {
                                                        FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(e.toString());
                                                    }
                                                    SendNotification sendNotification = new SendNotification();
                                                    sendNotification.sendNotification(notification, v.getContext(), FCMServer);

                                                    userList.remove(pedidosViewHolder.getAdapterPosition());
                                                    notifyDataSetChanged();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                            builder3.setNeutralButton(pedidosViewHolder.itemView.getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            AlertDialog alert = builder3.create();
                            alert.setCanceledOnTouchOutside(false);
                            alert.show();
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

    class PedidosViewHolder extends EmptyRecyclerView.ViewHolder {
        final TextView mName;
        final LinearLayout mLayout;
        final CardView cardView;
        final CircleImageView imagemPerfil;
        PedidosViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.usertoshare);
            imagemPerfil = view.findViewById(R.id.image_user_to_sahre);
            mName = view.findViewById(R.id.name_user_to_share);
            mLayout = view.findViewById(R.id.layout_item_usertoshare);
        }
    }

}
