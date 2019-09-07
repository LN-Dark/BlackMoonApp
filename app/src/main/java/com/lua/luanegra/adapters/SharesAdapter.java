package com.lua.luanegra.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.lua.luanegra.R;
import com.lua.luanegra.callservice.SendNotification;
import com.lua.luanegra.objects.SharesObject;
import com.lua.luanegra.tools.EmptyRecyclerView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SharesAdapter extends EmptyRecyclerView.Adapter<SharesAdapter.SharesViewHolder> implements Filterable {
    private  ArrayList<SharesObject> sharesList;

    public SharesAdapter(ArrayList<SharesObject> shares) {
        this.sharesList =  shares;
        fullShareslist = shares;
    }
    private String FCMServer, SinchKey, SinchSecret;

    @NonNull
    @Override
    public SharesAdapter.SharesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_share, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new SharesViewHolder(layoutView);
    }


    private static ArrayList<SharesObject> fullShareslist;
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    sharesList = fullShareslist;
                } else {
                    ArrayList<SharesObject> filteredList = new ArrayList<>();
                    for (SharesObject row : fullShareslist) {

                        if (row.getShareCreator().toLowerCase().contains(charString.toLowerCase()) || row.getShareCreator().contains(charSequence)) {
                            filteredList.add(row);
                        }
                        if (row.getSharesText().toLowerCase().contains(charString.toLowerCase()) || row.getSharesText().contains(charSequence)) {
                            if(!filteredList.contains(row)){
                                filteredList.add(row);
                            }
                        }
                    }

                    sharesList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = sharesList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                sharesList = (ArrayList<SharesObject>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull final SharesAdapter.SharesViewHolder sharesViewHolder, int i) {
        try{
            DatabaseReference mAppKeysRef = FirebaseDatabase.getInstance().getReference().child("appKeys");
            mAppKeysRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot6) {
                    if(dataSnapshot6.exists()){
                        for(DataSnapshot childSnapshot : dataSnapshot6.getChildren()){
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
                        sharesViewHolder.tituloShares.setText(String.format("✶  %s  ✶", sharesList.get(sharesViewHolder.getAdapterPosition()).getShareCreator()));
                        sharesViewHolder.mSharesDate.setText(String.format("%s   -   ", sharesList.get(sharesViewHolder.getAdapterPosition()).getData()));
                        sharesViewHolder.mShares.setText(sharesList.get(sharesViewHolder.getAdapterPosition()).getSharesText());
                        sharesViewHolder.sharesLink.setBackgroundColor(Color.TRANSPARENT);
                        Picasso.get().load(sharesList.get(sharesViewHolder.getAdapterPosition()).getShareImageCreator()).into(sharesViewHolder.mImage);
                        sharesViewHolder.mImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {
                                FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot222) {
                                        if(dataSnapshot222.exists()){
                                            for(DataSnapshot childuserPerfilSnapShot : dataSnapshot222.getChildren()){
                                                if(childuserPerfilSnapShot.getKey().equals(sharesList.get(sharesViewHolder.getAdapterPosition()).getSHareUiDCreator())){
                                                    final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(v.getContext());
                                                    LinearLayout layout = new LinearLayout(v.getContext());
                                                    layout.setOrientation(LinearLayout.VERTICAL);

                                                    builder3.setIcon(v.getContext().getDrawable(R.drawable.luanegra_logo));
                                                    builder3.setTitle(v.getResources().getString(R.string.perfiltitulo));
                                                    final TextView espaco9 = new TextView(v.getContext());
                                                    espaco9.setText(" ");
                                                    layout.addView(espaco9);
                                                    final CircleImageView icon = new CircleImageView(v.getContext());
                                                    LinearLayout.LayoutParams layoutParams  = new LinearLayout.LayoutParams(150, 150);
                                                    layoutParams.gravity = Gravity.CENTER;
                                                    icon.setLayoutParams(layoutParams);
                                                    layout.addView(icon);
                                                    Picasso.get().load(childuserPerfilSnapShot.child("profile_image").getValue().toString()).into(icon);
                                                    final TextView titulo = new TextView(v.getContext());
                                                    titulo.setText(String.format(v.getResources().getString(R.string.primeironome), childuserPerfilSnapShot.child("name").getValue().toString(), childuserPerfilSnapShot.child("registerNick").getValue().toString()));
                                                    titulo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                                                    titulo.setTextSize(16);
                                                    layout.addView(titulo);
                                                    final TextView espaco4 = new TextView(v.getContext());
                                                    espaco4.setText(" ");
                                                    layout.addView(espaco4);
                                                    final TextView bio = new TextView(v.getContext());
                                                    bio.setText("Bio: \n\n" + childuserPerfilSnapShot.child("bio").getValue().toString());
                                                    bio.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                                                    bio.setTextSize(16);
                                                    layout.addView(bio);
                                                    final TextView espaco42 = new TextView(v.getContext());
                                                    espaco42.setText(" ");
                                                    layout.addView(espaco42);
                                                    final TextView titulo2 = new TextView(v.getContext());
                                                    titulo2.setText(v.getContext().getString(R.string.ultimavezonline) +"\n" + Objects.requireNonNull(childuserPerfilSnapShot.child("onlineDate").getValue()).toString() + "  *  " + Objects.requireNonNull(childuserPerfilSnapShot.child("onlineTime").getValue()).toString());
                                                    titulo2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                                                    titulo2.setTextSize(13);
                                                    layout.addView(titulo2);
                                                    final TextView espaco43 = new TextView(v.getContext());
                                                    espaco43.setText(" ");
                                                    layout.addView(espaco43);
                                                    final TextView datainscri = new TextView(v.getContext());
                                                    datainscri.setText(v.getResources().getString(R.string.utilizadorregistadodesde) + childuserPerfilSnapShot.child("registerDate").getValue().toString() + "  -  " + childuserPerfilSnapShot.child("registerTime").getValue().toString());
                                                    datainscri.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                                                    datainscri.setTextSize(13);
                                                    layout.addView(datainscri);
                                                    final TextView espaco433 = new TextView(v.getContext());
                                                    espaco433.setText(" ");
                                                    layout.addView(espaco433);
                                                    builder3.setCancelable(false);
                                                    builder3.setView(layout);
                                                    builder3.setPositiveButton(v.getContext().getResources().getString(R.string.fecharapp), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    });
                                                    AlertDialog alert = builder3.create();
                                                    alert.show();
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                        Picasso.get().load(sharesList.get(sharesViewHolder.getAdapterPosition()).getShareLink()).into(sharesViewHolder.sharesLink);




                        sharesViewHolder.sharesLink.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.parse(sharesList.get(sharesViewHolder.getAdapterPosition()).getShareLink()), "image/*");
                                v.getContext().startActivity(intent);
                            }
                        });
                        sharesViewHolder.mSharesHora.setText(sharesList.get(sharesViewHolder.getAdapterPosition()).getHora());
                        final ArrayList<String> listaAdmins = new ArrayList<>();
                        sharesViewHolder.mLayout.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(final View v) {

                                FirebaseDatabase.getInstance().getReference().child("admins").push();
                                FirebaseDatabase.getInstance().getReference().child("admins").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                                                String idAdmin = childSnapshot.getKey();
                                                listaAdmins.add(idAdmin);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                if(listaAdmins.contains(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                    AlertDialog.Builder builder = new MaterialAlertDialogBuilder(v.getContext());
                                    LinearLayout layout = new LinearLayout(v.getContext());
                                    layout.setOrientation(LinearLayout.VERTICAL);
                                    layout.setGravity(Gravity.CENTER);

                                    builder.setIcon(v.getContext().getDrawable(R.drawable.luanegra_logo));
                                    builder.setTitle(v.getResources().getString(R.string.apagarpartilha));

                                    final TextView espaco2 = new TextView(v.getContext());
                                    espaco2.setText(String.format("\n\n %s ✶ \n\n", sharesList.get(sharesViewHolder.getAdapterPosition()).getSharesText()));
                                    espaco2.setTextSize(12);

                                    espaco2.setGravity(Gravity.CENTER);
                                    layout.addView(espaco2);

                                    builder.setView(layout);
                                    builder.setPositiveButton(v.getResources().getString(R.string.apagar), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FirebaseDatabase.getInstance().getReference().child("shares").child(sharesList.get(sharesViewHolder.getAdapterPosition()).getUid()).removeValue();
                                            Snackbar.make(v,  v.getResources().getString(R.string.partilhaapagada), Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                            sharesList.remove(sharesViewHolder.getAdapterPosition());
                                            notifyDataSetChanged();
                                        }
                                    });
                                    builder.setNeutralButton(v.getResources().getString(R.string.apagarerepreender), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.exists()){
                                                        for(DataSnapshot childUserSnapShot : dataSnapshot.getChildren()){
                                                            if(sharesViewHolder.getAdapterPosition() > -1){
                                                            if (Objects.requireNonNull(childUserSnapShot.getKey()).equals(sharesList.get(sharesViewHolder.getAdapterPosition()).getUid())) {
                                                                JSONObject notification2 = new JSONObject();
                                                                JSONObject notifcationBody2 = new JSONObject();
                                                                try {
                                                                    notifcationBody2.put("title", v.getContext().getString(R.string.advertencia));
                                                                    notifcationBody2.put("message", v.getContext().getString(R.string.partilhainapropriadahalloffame));

                                                                    notification2.put("to", "/LuaNegra/" + Objects.requireNonNull(childUserSnapShot.child("notificationKey").getValue()).toString());
                                                                    notification2.put("data", notifcationBody2);
                                                                } catch (Exception e) {
                                                                    FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
                                                                }
                                                                SendNotification sendNotification2 = new SendNotification();
                                                                sendNotification2.sendNotification(notification2, v.getContext(), FCMServer);
                                                            }
                                                            }
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            String keyAdvert = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("advertencias").push().getKey();
                                            FirebaseDatabase.getInstance().getReference().child("user").child(sharesList.get(sharesViewHolder.getAdapterPosition()).getSHareUiDCreator()).child("advertencias").child(Objects.requireNonNull(keyAdvert)).setValue(true);
                                            FirebaseDatabase.getInstance().getReference().child("videos").child(sharesList.get(sharesViewHolder.getAdapterPosition()).getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    sharesList.remove(sharesViewHolder.getAdapterPosition());
                                                    notifyDataSetChanged();
                                                    Snackbar.make(v,  v.getContext().getString(R.string.videoapagado), Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                }
                                            });

                                        }
                                    });
                                    builder.setNegativeButton(v.getContext().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                        }
                                    });
                                    builder.show();
                                }else if(sharesList.get(sharesViewHolder.getAdapterPosition()).getShareCreator().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    AlertDialog.Builder builder = new MaterialAlertDialogBuilder(v.getContext());
                                    LinearLayout layout = new LinearLayout(v.getContext());
                                    layout.setOrientation(LinearLayout.VERTICAL);
                                    layout.setGravity(Gravity.CENTER);
                                    builder.setIcon(v.getContext().getDrawable(R.drawable.luanegra_logo));
                                    builder.setTitle(v.getResources().getString(R.string.apagarpartilha));

                                    final TextView espaco2 = new TextView(v.getContext());
                                    espaco2.setText(String.format("\n\n %s ✶ \n\n", sharesList.get(sharesViewHolder.getAdapterPosition()).getSharesText()));
                                    espaco2.setTextSize(12);

                                    espaco2.setGravity(Gravity.CENTER);
                                    layout.addView(espaco2);

                                    builder.setView(layout);
                                    builder.setPositiveButton(v.getResources().getString(R.string.apagar), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FirebaseDatabase.getInstance().getReference().child("shares").child(sharesList.get(sharesViewHolder.getAdapterPosition()).getUid()).removeValue();
                                            Snackbar.make(v,  v.getResources().getString(R.string.partilhaapagada), Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                            sharesList.remove(sharesViewHolder.getAdapterPosition());
                                            notifyDataSetChanged();
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
                                return false;
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return sharesList == null ? 0 : sharesList.size();
    }

    class SharesViewHolder extends EmptyRecyclerView.ViewHolder {
        final LinearLayout mLayout ;
        final CircleImageView mImage;
        final ImageView sharesLink;
        final TextView mShares;
        final TextView mSharesDate;
        final TextView mSharesHora;
        final TextView tituloShares;
        SharesViewHolder(@NonNull View itemView) {
            super(itemView);
            sharesLink = itemView.findViewById(R.id.richLinkView_share);
            tituloShares = itemView.findViewById(R.id.titulo_notification);
            mImage = itemView.findViewById(R.id.icon_share);
            mLayout = itemView.findViewById(R.id.layout_notification);
            mShares = itemView.findViewById(R.id.message_notification);
            mSharesDate = itemView.findViewById(R.id.data_notification);
            mSharesHora = itemView.findViewById(R.id.hora_notification);
        }
    }
}
