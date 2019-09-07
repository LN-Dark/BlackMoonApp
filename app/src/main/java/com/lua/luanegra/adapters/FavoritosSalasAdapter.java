package com.lua.luanegra.adapters;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.lua.luanegra.R;
import com.lua.luanegra.activitys.Salas.Privadas.SalaPrivadaActivity;
import com.lua.luanegra.activitys.Salas.Publicas.SalaPublicaActivity;
import com.lua.luanegra.objects.GroupObject;
import com.lua.luanegra.tools.EmptyRecyclerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class FavoritosSalasAdapter extends EmptyRecyclerView.Adapter<FavoritosSalasAdapter.SalaViewHolder> implements Filterable {
    private ArrayList<GroupObject> salaList;

    public FavoritosSalasAdapter(ArrayList<GroupObject> salaList) {
        this.salaList = salaList;
        fullSalalist = salaList;
    }

    @NonNull
    @Override
    public FavoritosSalasAdapter.SalaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_group, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new FavoritosSalasAdapter.SalaViewHolder(layoutView);
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
    public void onBindViewHolder(@NonNull final FavoritosSalasAdapter.SalaViewHolder SalaViewHolder, int i) {
        try{

            SalaViewHolder.mGroup.setText(salaList.get(SalaViewHolder.getAdapterPosition()).getGroupName());
            Picasso.get().load(salaList.get(SalaViewHolder.getAdapterPosition()).getLogoUri()).into(SalaViewHolder.mGroupLogo);
            SalaViewHolder.numeroutilizadores.setVisibility(View.VISIBLE);
            float density = SalaViewHolder.itemView.getContext().getResources().getDisplayMetrics().density;
            Drawable drawable = SalaViewHolder.itemView.getContext().getDrawable(R.drawable.bros_icon_tabs);
            int width = Math.round(15 * density);
            int height = Math.round(15 * density);
            drawable.setBounds(0, 0, width, height);
            SalaViewHolder.numeroutilizadores.setCompoundDrawables(drawable, null, null, null);
            SalaViewHolder.numeroutilizadores.setText("  " + salaList.get(SalaViewHolder.getAdapterPosition()).getNumeroDeUtilizadores());
            SalaViewHolder.mLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {

                        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(v.getContext());
                        LinearLayout layout = new LinearLayout(v.getContext());
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setGravity(Gravity.CENTER);

                        builder.setIcon(v.getContext().getDrawable(R.drawable.luanegra_logo));
                        builder.setTitle(v.getContext().getString(R.string.apagardosfavoritos));
                        final ImageView icon = new ImageView(v.getContext());
                        LinearLayout.LayoutParams layoutParams  = new LinearLayout.LayoutParams(200, 200);
                        layoutParams.gravity = Gravity.CENTER;
                        icon.setLayoutParams(layoutParams);
                        layout.addView(icon);
                        Picasso.get().load(salaList.get(SalaViewHolder.getAdapterPosition()).getLogoUri()).into(icon);
                        final TextView espaco2 = new TextView(v.getContext());
                        espaco2.setText(String.format("\n\n %s \n\n", salaList.get(SalaViewHolder.getAdapterPosition()).getGroupName()));
                        espaco2.setTextSize(12);

                        espaco2.setGravity(Gravity.CENTER);
                        layout.addView(espaco2);

                        builder.setView(layout);
                        builder.setPositiveButton(v.getContext().getString(R.string.removerdosfavoritos), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("favoritos").child(salaList.get(SalaViewHolder.getAdapterPosition()).getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Snackbar.make(v,  v.getResources().getString(R.string.salaremovidadosfavoritos), Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        notifyDataSetChanged();
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

                    return false;
                }
            });
            SalaViewHolder.mGroupLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(salaList.get(SalaViewHolder.getAdapterPosition()).getWhatKindOfRoom().equals("public")){
                        String currentGroupName = salaList.get(SalaViewHolder.getAdapterPosition()).getGroupName();
                        Intent groupIntent = new Intent(v.getContext(), SalaPublicaActivity.class);
                        groupIntent.putExtra("groupName", currentGroupName);
                        groupIntent.putExtra("groupUid", salaList.get(SalaViewHolder.getAdapterPosition()).getUid());
                        v.getContext().startActivity(groupIntent);
                    }else if(salaList.get(SalaViewHolder.getAdapterPosition()).getWhatKindOfRoom().equals("private")){
                        String currentGroupName = salaList.get(SalaViewHolder.getAdapterPosition()).getGroupName();
                        Intent groupIntent = new Intent(v.getContext(), SalaPrivadaActivity.class);
                        groupIntent.putExtra("groupName", currentGroupName);
                        groupIntent.putExtra("groupUid", salaList.get(SalaViewHolder.getAdapterPosition()).getUid());
                        groupIntent.putExtra("key", salaList.get(SalaViewHolder.getAdapterPosition()).getChatKey());
                        v.getContext().startActivity(groupIntent);
                        v.getContext().startActivity(groupIntent);
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
        return salaList == null ? 0 : salaList.size();
    }

    class SalaViewHolder extends EmptyRecyclerView.ViewHolder{
        final LinearLayout mLayout ;
        final TextView mGroup,numeroutilizadores;
        final ImageView mGroupLogo;
        SalaViewHolder(View view){
            super(view);
            numeroutilizadores = view.findViewById(R.id.numeroutilizadores_sala);
            mLayout = view.findViewById(R.id.layout_group);
            mGroup = view.findViewById(R.id.group_name);
            mGroupLogo = view.findViewById(R.id.group_logo);
        }
    }
}

