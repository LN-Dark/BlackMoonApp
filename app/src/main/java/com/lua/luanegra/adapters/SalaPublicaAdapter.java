package com.lua.luanegra.adapters;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koushikdutta.ion.Ion;
import com.lua.luanegra.R;
import com.lua.luanegra.activitys.Salas.Publicas.SalaPublicaActivity;
import com.lua.luanegra.objects.GroupObject;
import com.lua.luanegra.tools.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class SalaPublicaAdapter extends EmptyRecyclerView.Adapter<SalaPublicaAdapter.SalaViewHolder>  implements Filterable {
    private ArrayList<GroupObject> salaList;

    public SalaPublicaAdapter(ArrayList<GroupObject> salaList) {
        this.salaList = salaList;
        fullSalalist = salaList;
    }

    @NonNull
    @Override
    public SalaPublicaAdapter.SalaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_group, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new SalaPublicaAdapter.SalaViewHolder(layoutView);
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



    @Override
    public void onBindViewHolder(@NonNull final SalaPublicaAdapter.SalaViewHolder SalaViewHolder, int i) {
        try{
            int color = Color.parseColor(salaList.get(SalaViewHolder.getAdapterPosition()).getCorApresentacao());
            SalaViewHolder.numeroutilizadores.setVisibility(View.VISIBLE);
            float density = SalaViewHolder.itemView.getContext().getResources().getDisplayMetrics().density;
            Drawable drawable = SalaViewHolder.itemView.getContext().getDrawable(R.drawable.bros_icon_tabs);
            int width = Math.round(15 * density);
            int height = Math.round(15 * density);
            drawable.setBounds(0, 0, width, height);
            SalaViewHolder.numeroutilizadores.setCompoundDrawables(drawable, null, null, null);
            SalaViewHolder.numeroutilizadores.setText("  " + salaList.get(SalaViewHolder.getAdapterPosition()).getNumeroDeUtilizadores());
            SalaViewHolder.cardView.setCardBackgroundColor(color);
            SalaViewHolder.mGroup.setText(salaList.get(SalaViewHolder.getAdapterPosition()).getGroupName());

            Ion.with(SalaViewHolder.itemView.getContext()).load(salaList.get(SalaViewHolder.getAdapterPosition()).getLogoUri()).intoImageView(SalaViewHolder.mGroupLogo);
            SalaViewHolder.mGroupLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(salaList.get(SalaViewHolder.getAdapterPosition()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                    builder3.setTitle(v.getResources().getString(R.string.salas_privadas));
                                    final ImageView icon = new ImageView(v.getContext());
                                    LinearLayout.LayoutParams layoutParams  = new LinearLayout.LayoutParams(200, 200);
                                    layoutParams.gravity = Gravity.CENTER;
                                    icon.setLayoutParams(layoutParams);
                                    layout.addView(icon);
                                    Ion.with(SalaViewHolder.itemView.getContext()).load(salaList.get(SalaViewHolder.getAdapterPosition()).getLogoUri()).intoImageView(icon);
                                    final TextView titulo = new TextView(v.getContext());
                                    titulo.setText(String.format("\n %s\n ", salaList.get(SalaViewHolder.getAdapterPosition()).getGroupName()));
                                    titulo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                                    titulo.setTextSize(15);
                                    layout.addView(titulo);
                                    final TextView espaco4 = new TextView(v.getContext());
                                    espaco4.setText(" ");
                                    layout.addView(espaco4);
                                    final TextView descricao = new TextView(v.getContext());
                                    descricao.setText(String.format("%s%s", v.getContext().getString(R.string.descricaosala), salaList.get(SalaViewHolder.getAdapterPosition()).getDescricaoSalaPrivada()));
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
                                            String currentGroupName = salaList.get(SalaViewHolder.getAdapterPosition()).getGroupName();
                                            Intent groupIntent = new Intent(v.getContext(), SalaPublicaActivity.class);
                                            groupIntent.putExtra("groupName", currentGroupName);
                                            groupIntent.putExtra("groupUid", salaList.get(SalaViewHolder.getAdapterPosition()).getUid());
                                            v.getContext().startActivity(groupIntent);
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
                                    builder3.setTitle(v.getResources().getString(R.string.salas_privadas));
                                    final ImageView icon = new ImageView(v.getContext());
                                    LinearLayout.LayoutParams layoutParams  = new LinearLayout.LayoutParams(200, 200);
                                    layoutParams.gravity = Gravity.CENTER;
                                    icon.setLayoutParams(layoutParams);
                                    layout.addView(icon);
                                    Ion.with(SalaViewHolder.itemView.getContext()).load(salaList.get(SalaViewHolder.getAdapterPosition()).getLogoUri()).intoImageView(icon);
                                    final TextView titulo = new TextView(v.getContext());
                                    titulo.setText(String.format("\n %s\n ", salaList.get(SalaViewHolder.getAdapterPosition()).getGroupName()));
                                    titulo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                                    titulo.setTextSize(15);

                                    layout.addView(titulo);
                                    final TextView espaco4 = new TextView(v.getContext());
                                    espaco4.setText(" ");
                                    layout.addView(espaco4);
                                    final TextView descricao = new TextView(v.getContext());
                                    descricao.setText(String.format("%s%s", v.getContext().getString(R.string.descricaosala), salaList.get(SalaViewHolder.getAdapterPosition()).getDescricaoSalaPrivada()));
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
                                    builder3.setPositiveButton("Subscrever", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(salaList.get(SalaViewHolder.getAdapterPosition()).getUid()).child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    String currentGroupName = salaList.get(SalaViewHolder.getAdapterPosition()).getGroupName();
                                                    Intent groupIntent = new Intent(v.getContext(), SalaPublicaActivity.class);
                                                    groupIntent.putExtra("groupName", currentGroupName);
                                                    groupIntent.putExtra("groupUid", salaList.get(SalaViewHolder.getAdapterPosition()).getUid());
                                                    v.getContext().startActivity(groupIntent);
                                                }
                                            });
                                        }
                                    });
                                    builder3.setNeutralButton(v.getContext().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    alert = builder3.create();
                                    alert.show();
                                }else if(listaIdsBloquedUsers.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    Snackbar.make(salaList.get(SalaViewHolder.getAdapterPosition()).getView(), v.getResources().getString(R.string.estasbloqueadonestasala), Snackbar.LENGTH_LONG)
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

    class SalaViewHolder extends EmptyRecyclerView.ViewHolder{
        final LinearLayout mLayout ;
        final TextView mGroup, numeroutilizadores;
        final ImageView mGroupLogo;
        final CardView cardView;
        SalaViewHolder(View view){
            super(view);
            numeroutilizadores = view.findViewById(R.id.numeroutilizadores_sala);
            cardView = view.findViewById(R.id.cardview_sala);
            mLayout = view.findViewById(R.id.layout_group);
            mGroup = view.findViewById(R.id.group_name);
            mGroupLogo = view.findViewById(R.id.group_logo);
        }
    }
}
