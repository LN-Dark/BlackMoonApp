package com.lua.luanegra.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koushikdutta.ion.Ion;
import com.lua.luanegra.R;
import com.lua.luanegra.activitys.Salas.Privadas.SalaPrivadaActivity;
import com.lua.luanegra.callservice.SendNotification;
import com.lua.luanegra.objects.GroupObject;
import com.lua.luanegra.tools.EmptyRecyclerView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class SalaPrivadaAdapter extends EmptyRecyclerView.Adapter<SalaPrivadaAdapter.SalaPrivadaViewHolder> implements Filterable {
    private ArrayList<GroupObject> salaList;
    private String CurrentUserName = "";
    private String creatorNotificationKey = "";

    public SalaPrivadaAdapter(ArrayList<GroupObject> salaList) {
        this.salaList = salaList;
        fullSalalist = salaList;
    }

    private static ArrayList<GroupObject> fullSalalist;
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    salaList = fullSalalist;
                } else {
                    ArrayList<GroupObject> filteredList = new ArrayList<>();
                    for (GroupObject row : fullSalalist) {

                        if (row.getGroupName().toLowerCase().contains(charString.toLowerCase()) || row.getGroupName().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    salaList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = salaList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                salaList = (ArrayList<GroupObject>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public SalaPrivadaAdapter.SalaPrivadaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_group, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new SalaPrivadaViewHolder(layoutView);
    }

    private String FCMServer, SinchKey, SinchSecret;

    @Override
    public void onBindViewHolder(@NonNull final SalaPrivadaAdapter.SalaPrivadaViewHolder SalaPrivadaViewHolder, int i) {
        try{
            int color = Color.parseColor(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getCorApresentacao());
            SalaPrivadaViewHolder.numeroutilizadores.setVisibility(View.VISIBLE);
             float density = SalaPrivadaViewHolder.itemView.getContext().getResources().getDisplayMetrics().density;
             Drawable drawable = SalaPrivadaViewHolder.itemView.getContext().getDrawable(R.drawable.bros_icon_tabs);
             int width = Math.round(15 * density);
             int height = Math.round(15 * density);
            drawable.setBounds(0, 0, width, height);
            SalaPrivadaViewHolder.numeroutilizadores.setCompoundDrawables(drawable, null, null, null);
            SalaPrivadaViewHolder.numeroutilizadores.setText("  " + salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getNumeroDeUtilizadores());
            SalaPrivadaViewHolder.cardView.setCardBackgroundColor(color);
            SalaPrivadaViewHolder.mGroup.setText(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getGroupName());

            Ion.with(SalaPrivadaViewHolder.itemView.getContext()).load(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getLogoUri()).intoImageView(SalaPrivadaViewHolder.mGroupLogo);
            SalaPrivadaViewHolder.mGroupLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                       FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    ArrayList<String> listaIdsUsers = new ArrayList<>();
                                    ArrayList<String> listaIdsBloquedUsers = new ArrayList<>();
                                    for(DataSnapshot childSnapShot : dataSnapshot.child("users").getChildren()){
                                        listaIdsUsers.add(childSnapShot.getKey());
                                    }
                                    if(dataSnapshot.child("bloquedUsers").exists()){
                                        for(DataSnapshot childBloquedUserSnapShot : dataSnapshot.child("bloquedUsers").getChildren()){
                                            listaIdsBloquedUsers.add(childBloquedUserSnapShot.getKey());
                                        }
                                    }
                                     if ((listaIdsUsers.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) && (!listaIdsBloquedUsers.contains(FirebaseAuth.getInstance().getCurrentUser().getUid()))){
                                        final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(v.getContext());
                                        LinearLayout layout = new LinearLayout(v.getContext());
                                        layout.setOrientation(LinearLayout.VERTICAL);

                                        builder3.setIcon(v.getContext().getDrawable(R.drawable.luanegra_logo));
                                        builder3.setTitle(v.getResources().getString(R.string.pedidodeacesso));
                                        final ImageView icon = new ImageView(v.getContext());
                                        LinearLayout.LayoutParams layoutParams  = new LinearLayout.LayoutParams(200, 200);
                                        layoutParams.gravity = Gravity.CENTER;
                                        icon.setLayoutParams(layoutParams);
                                        layout.addView(icon);
                                        Ion.with(SalaPrivadaViewHolder.itemView.getContext()).load(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getLogoUri()).intoImageView(icon);
                                        final TextView titulo = new TextView(v.getContext());
                                        titulo.setText(String.format("\n %s\n ", salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getGroupName()));
                                        titulo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                                        titulo.setTextSize(15);
                                        layout.addView(titulo);
                                        final TextView espaco4 = new TextView(v.getContext());
                                        espaco4.setText(" ");
                                        layout.addView(espaco4);
                                        final TextView descricao = new TextView(v.getContext());
                                        descricao.setText(String.format("%s%s", v.getContext().getString(R.string.descricaosala), salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getDescricaoSalaPrivada()));
                                        descricao.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                                        descricao.setTextSize(13);
                                        layout.addView(descricao);
                                        final TextView espaco44 = new TextView(v.getContext());
                                        espaco44.setText(" ");
                                        layout.addView(espaco44);
                                        builder3.setCancelable(false);
                                        builder3.setView(layout);
                                        AlertDialog alert = builder3.create();
                                        alert.setCanceledOnTouchOutside(false);
                                        final AlertDialog finalAlert = alert;
                                        builder3.setPositiveButton(v.getContext().getString(R.string.entrar), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String currentGroupName = salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getGroupName();
                                                Intent groupIntent = new Intent(v.getContext(), SalaPrivadaActivity.class);
                                                groupIntent.putExtra("groupName", currentGroupName);
                                                groupIntent.putExtra("groupUid", salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getUid());
                                                SharedPreferences prefs = v.getContext().getSharedPreferences(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getUid(), Context.MODE_PRIVATE);
                                                String chatKey = " ";
                                                chatKey = prefs.getString(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getUid(), " ");
                                                if(!chatKey.equals(" ")){
                                                    groupIntent.putExtra("key", chatKey);
                                                    v.getContext().startActivity(groupIntent);
                                                }else {
                                                    final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(v.getContext());
                                                    LinearLayout layout = new LinearLayout(v.getContext());
                                                    layout.setOrientation(LinearLayout.VERTICAL);

                                                    builder3.setIcon( v.getContext().getDrawable(R.drawable.luanegra_logo));
                                                    builder3.setTitle(v.getContext().getString(R.string.chavedasalaprivada));
                                                    builder3.setMessage(v.getContext().getString(R.string.janaotensachavedestasala));
                                                    builder3.setCancelable(false);
                                                    final TextView espaco5 = new TextView(v.getContext());
                                                    espaco5.setText(" ");
                                                    layout.addView(espaco5);
                                                    builder3.setView(layout);
                                                    AlertDialog alert;
                                                    builder3.setPositiveButton(v.getContext().getString(R.string.enviarpedido), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
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
                                                                        FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                                                                if(dataSnapshot2.exists()){

                                                                                    for(DataSnapshot childSnapshot : dataSnapshot2.getChildren()){
                                                                                        if(!Objects.requireNonNull(childSnapshot.getKey()).equals("chat")){
                                                                                            if(childSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                                                                CurrentUserName = Objects.requireNonNull(childSnapshot.child("name").getValue()).toString();
                                                                                            }
                                                                                            if(childSnapshot.getKey().equals(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getCreator())){
                                                                                                creatorNotificationKey = Objects.requireNonNull(childSnapshot.child("notificationKey").getValue()).toString();
                                                                                            }
                                                                                        }

                                                                                    }
                                                                                    FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot3) {
                                                                                            if(dataSnapshot3.exists()){
                                                                                                String Keypedidos = FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getUid()).child("pedidos").push().getKey();
                                                                                                Calendar CalForDate = Calendar.getInstance();
                                                                                                SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                                                                                                String currentDate = currentDateFormat.format(CalForDate.getTime());
                                                                                                Calendar CalForTime = Calendar.getInstance();
                                                                                                SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
                                                                                                currentTimeFormat.toLocalizedPattern();
                                                                                                String currentTime = currentTimeFormat.format(CalForTime.getTime());
                                                                                                if(dataSnapshot3.child("pedidos").exists()){
                                                                                                    ArrayList<String> listaPedidos = new ArrayList<>();
                                                                                                    for(DataSnapshot childPedidosSnapShot : dataSnapshot3.child("pedidos").getChildren()){
                                                                                                        listaPedidos.add(Objects.requireNonNull(childPedidosSnapShot.child("idUser").getValue()).toString());
                                                                                                    }
                                                                                                    if(!listaPedidos.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                                                                        JSONObject notification = new JSONObject();
                                                                                                        JSONObject notifcationBody = new JSONObject();
                                                                                                        try {
                                                                                                            notifcationBody.put("title", v.getContext().getString(R.string.novopedidodeacesso));
                                                                                                            notifcationBody.put("message", v.getResources().getString(R.string.o) + " " +  CurrentUserName + " " + v.getResources().getString(R.string.estaapediracessoa) + " " +  salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getGroupName());

                                                                                                            notification.put("to", "/LuaNegra/" + creatorNotificationKey);
                                                                                                            notification.put("data", notifcationBody);
                                                                                                        } catch (Exception e) {
                                                                                                            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(e.toString());
                                                                                                        }
                                                                                                        SendNotification sendNotification = new SendNotification();
                                                                                                        sendNotification.sendNotification(notification, v.getContext(), FCMServer);
                                                                                                        FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getUid()).child("pedidos").child(Objects.requireNonNull(Keypedidos)).child("idUser").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                                                        FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getUid()).child("pedidos").child(Keypedidos).child("data").setValue(currentDate);
                                                                                                        FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getUid()).child("pedidos").child(Keypedidos).child("hora").setValue(currentTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                Snackbar.make(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getView(), v.getContext().getString(R.string.pedidodeacessoasalaprivada), Snackbar.LENGTH_LONG)
                                                                                                                        .setAction("Action", null).show();
                                                                                                                finalAlert.dismiss();
                                                                                                            }
                                                                                                        });
                                                                                                    }else {
                                                                                                        Snackbar.make(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getView(), v.getResources().getString(R.string.jaenviasteumpedidoagiarda), Snackbar.LENGTH_LONG)
                                                                                                                .setAction("Action", null).show();
                                                                                                        finalAlert.dismiss();
                                                                                                    }
                                                                                                }else {
                                                                                                    JSONObject notification = new JSONObject();
                                                                                                    JSONObject notifcationBody = new JSONObject();
                                                                                                    try {
                                                                                                        notifcationBody.put("title", v.getContext().getString(R.string.novopedidodeacesso));
                                                                                                        notifcationBody.put("message", v.getContext().getString(R.string.o) + " " +  CurrentUserName  + " " + v.getResources().getString(R.string.estaapediracessoa)  + " " +  salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getGroupName());

                                                                                                        notification.put("to", "/LuaNegra/" + creatorNotificationKey);
                                                                                                        notification.put("data", notifcationBody);
                                                                                                    } catch (Exception e) {
                                                                                                        FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(e.toString());
                                                                                                    }
                                                                                                    SendNotification sendNotification = new SendNotification();
                                                                                                    sendNotification.sendNotification(notification, v.getContext(), FCMServer);
                                                                                                    FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getUid()).child("pedidos").child(Objects.requireNonNull(Keypedidos)).child("idUser").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                                                    FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getUid()).child("pedidos").child(Keypedidos).child("data").setValue(currentDate);
                                                                                                    FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getUid()).child("pedidos").child(Keypedidos).child("hora").setValue(currentTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            Snackbar.make(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getView(), v.getResources().getString(R.string.pedidoenviado), Snackbar.LENGTH_LONG)
                                                                                                                    .setAction("Action", null).show();
                                                                                                            finalAlert.dismiss();
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
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                        }
                                                    });
                                                    builder3.setNeutralButton(v.getContext().getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    });
                                                    alert = builder3.create();
                                                    alert.show();
                                                }
                                            }
                                        });
                                        builder3.setNeutralButton(v.getContext().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finalAlert.dismiss();
                                            }
                                        });
                                        alert = builder3.create();
                                        alert.show();
                                    }else if(!listaIdsBloquedUsers.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                        final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(v.getContext());
                                        LinearLayout layout = new LinearLayout(v.getContext());
                                        layout.setOrientation(LinearLayout.VERTICAL);
                                        builder3.setIcon(v.getContext().getDrawable(R.drawable.luanegra_logo));
                                        builder3.setTitle(v.getResources().getString(R.string.pedidodeacesso));
                                        final ImageView icon = new ImageView(v.getContext());
                                        LinearLayout.LayoutParams layoutParams  = new LinearLayout.LayoutParams(200, 200);
                                        layoutParams.gravity = Gravity.CENTER;
                                        icon.setLayoutParams(layoutParams);
                                        layout.addView(icon);
                                        Ion.with(SalaPrivadaViewHolder.itemView.getContext()).load(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getLogoUri()).intoImageView(icon);
                                        final TextView titulo = new TextView(v.getContext());
                                        titulo.setText(String.format("\n %s\n ", salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getGroupName()));
                                        titulo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                                        titulo.setTextSize(15);

                                        layout.addView(titulo);
                                        final TextView espaco4 = new TextView(v.getContext());
                                        espaco4.setText(" ");
                                        layout.addView(espaco4);
                                        final TextView descricao = new TextView(v.getContext());
                                        descricao.setText(String.format("%s%s", v.getContext().getString(R.string.descricaosala), salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getDescricaoSalaPrivada()));
                                        descricao.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                                        descricao.setTextSize(13);

                                        layout.addView(descricao);
                                        final TextView espaco44 = new TextView(v.getContext());
                                        espaco44.setText(" ");
                                        layout.addView(espaco44);
                                        final TextView NomeSala = new TextView(v.getContext());
                                        NomeSala.setText(v.getResources().getString(R.string.aindanaoestasregistado));

                                        NomeSala.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                                        NomeSala.setTextSize(15);
                                        layout.addView(NomeSala);
                                        final TextView espaco7 = new TextView(v.getContext());
                                        espaco7.setText("\n");
                                        layout.addView(espaco7);
                                        builder3.setCancelable(false);
                                        builder3.setView(layout);
                                        AlertDialog alert = builder3.create();
                                        alert.setCanceledOnTouchOutside(false);
                                        final AlertDialog finalAlert = alert;
                                        builder3.setPositiveButton(v.getResources().getString(R.string.sim), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
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
                                                            FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                                                    if(dataSnapshot2.exists()){

                                                                        for(DataSnapshot childSnapshot : dataSnapshot2.getChildren()){
                                                                            if(!Objects.requireNonNull(childSnapshot.getKey()).equals("chat")){
                                                                                if(childSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                                                    CurrentUserName = Objects.requireNonNull(childSnapshot.child("name").getValue()).toString();
                                                                                }
                                                                                if(childSnapshot.getKey().equals(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getCreator())){
                                                                                    creatorNotificationKey = Objects.requireNonNull(childSnapshot.child("notificationKey").getValue()).toString();
                                                                                }
                                                                            }

                                                                        }
                                                                        FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot3) {
                                                                                if(dataSnapshot3.exists()){
                                                                                    String Keypedidos = FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getUid()).child("pedidos").push().getKey();
                                                                                    Calendar CalForDate = Calendar.getInstance();
                                                                                    SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                                                                                    String currentDate = currentDateFormat.format(CalForDate.getTime());
                                                                                    Calendar CalForTime = Calendar.getInstance();
                                                                                    SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
                                                                                    currentTimeFormat.toLocalizedPattern();
                                                                                    String currentTime = currentTimeFormat.format(CalForTime.getTime());
                                                                                    if(dataSnapshot3.child("pedidos").exists()){
                                                                                        ArrayList<String> listaPedidos = new ArrayList<>();
                                                                                        for(DataSnapshot childPedidosSnapShot : dataSnapshot3.child("pedidos").getChildren()){
                                                                                            listaPedidos.add(Objects.requireNonNull(childPedidosSnapShot.child("idUser").getValue()).toString());
                                                                                        }
                                                                                        if(!listaPedidos.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                                                            JSONObject notification = new JSONObject();
                                                                                            JSONObject notifcationBody = new JSONObject();
                                                                                            try {
                                                                                                notifcationBody.put("title", v.getContext().getString(R.string.novopedidodeacesso));
                                                                                                notifcationBody.put("message", v.getResources().getString(R.string.o) + " " +  CurrentUserName + " " +  v.getResources().getString(R.string.estaapediracessoa)  + " " +  salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getGroupName());

                                                                                                notification.put("to", "/LuaNegra/" + creatorNotificationKey);
                                                                                                notification.put("data", notifcationBody);
                                                                                            } catch (Exception e) {
                                                                                                FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(e.toString());
                                                                                            }
                                                                                            SendNotification sendNotification = new SendNotification();
                                                                                            sendNotification.sendNotification(notification, v.getContext(), FCMServer);
                                                                                            FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getUid()).child("pedidos").child(Objects.requireNonNull(Keypedidos)).child("idUser").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                                            FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getUid()).child("pedidos").child(Keypedidos).child("data").setValue(currentDate);
                                                                                            FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getUid()).child("pedidos").child(Keypedidos).child("hora").setValue(currentTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    Snackbar.make(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getView(), v.getContext().getString(R.string.pedidodeacessoasalaprivada), Snackbar.LENGTH_LONG)
                                                                                                            .setAction("Action", null).show();
                                                                                                    finalAlert.dismiss();
                                                                                                }
                                                                                            });
                                                                                        }else {
                                                                                            Snackbar.make(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getView(), v.getResources().getString(R.string.jaenviasteumpedidoagiarda), Snackbar.LENGTH_LONG)
                                                                                                    .setAction("Action", null).show();
                                                                                            finalAlert.dismiss();
                                                                                        }
                                                                                    }else {
                                                                                        JSONObject notification = new JSONObject();
                                                                                        JSONObject notifcationBody = new JSONObject();
                                                                                        try {
                                                                                            notifcationBody.put("title", v.getContext().getString(R.string.novopedidodeacesso));
                                                                                            notifcationBody.put("message", v.getContext().getString(R.string.o)  + " " +  CurrentUserName + v.getResources().getString(R.string.estaapediracessoa)  + " " +  salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getGroupName());

                                                                                            notification.put("to", "/LuaNegra/" + creatorNotificationKey);
                                                                                            notification.put("data", notifcationBody);
                                                                                        } catch (Exception e) {
                                                                                            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(e.toString());
                                                                                        }
                                                                                        SendNotification sendNotification = new SendNotification();
                                                                                        sendNotification.sendNotification(notification, v.getContext(), FCMServer);
                                                                                        FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getUid()).child("pedidos").child(Objects.requireNonNull(Keypedidos)).child("idUser").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                                        FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getUid()).child("pedidos").child(Keypedidos).child("data").setValue(currentDate);
                                                                                        FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getUid()).child("pedidos").child(Keypedidos).child("hora").setValue(currentTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                Snackbar.make(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getView(), v.getResources().getString(R.string.pedidoenviado), Snackbar.LENGTH_LONG)
                                                                                                        .setAction("Action", null).show();
                                                                                                finalAlert.dismiss();
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
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        });
                                        builder3.setNeutralButton(v.getContext().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finalAlert.dismiss();
                                            }
                                        });
                                        alert = builder3.create();
                                        alert.show();
                                    }else if(listaIdsBloquedUsers.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        Snackbar.make(salaList.get(SalaPrivadaViewHolder.getAdapterPosition()).getView(), v.getResources().getString(R.string.estasbloqueadonestasala), Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
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
        return salaList == null ? 0 : salaList.size();
    }

    class SalaPrivadaViewHolder extends EmptyRecyclerView.ViewHolder{
        final LinearLayout mLayout ;
        final TextView mGroup, numeroutilizadores;
        final ImageView mGroupLogo;
        final CardView cardView;
        SalaPrivadaViewHolder(View view){
            super(view);
            numeroutilizadores = view.findViewById(R.id.numeroutilizadores_sala);
            cardView = view.findViewById(R.id.cardview_sala);
            mLayout = view.findViewById(R.id.layout_group);
            mGroup = view.findViewById(R.id.group_name);
            mGroupLogo = view.findViewById(R.id.group_logo);
        }
    }
}
