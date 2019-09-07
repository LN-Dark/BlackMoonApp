package com.lua.luanegra.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lua.luanegra.R;
import com.lua.luanegra.activitys.Salas.Jogos.SalasJogosAntigaActivity;
import com.lua.luanegra.objects.GroupObject;
import com.lua.luanegra.tools.EmptyRecyclerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class SalaAdapter extends EmptyRecyclerView.Adapter<SalaAdapter.SalaViewHolder> {
    private final ArrayList<GroupObject> salaList;

    public SalaAdapter(ArrayList<GroupObject> salaList) {
        this.salaList = salaList;
    }

    @NonNull
    @Override
    public SalaAdapter.SalaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_group, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new SalaViewHolder(layoutView);
    }

    private ArrayList<String> listaSuperAdmins;
    private ArrayList<String> listaSuperAdminsNotificationToken;
    private void loadSuperAdmins() {
        try {
            listaSuperAdmins = new ArrayList<>();
            listaSuperAdminsNotificationToken = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("superAdmin").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                            listaSuperAdmins.add(childSnapshot.getKey());
                            listaSuperAdminsNotificationToken.add(Objects.requireNonNull(childSnapshot.getValue()).toString());
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
    public void onBindViewHolder(@NonNull final SalaAdapter.SalaViewHolder SalaViewHolder, int i) {
        try{
            loadSuperAdmins();
        SalaViewHolder.mGroup.setText(salaList.get(SalaViewHolder.getAdapterPosition()).getGroupName());
        Picasso.get().load(salaList.get(SalaViewHolder.getAdapterPosition()).getLogoUri()).into(SalaViewHolder.mGroupLogo);

        SalaViewHolder.mLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                if(listaSuperAdmins.contains(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                    AlertDialog.Builder builder = new MaterialAlertDialogBuilder(v.getContext());
                    LinearLayout layout = new LinearLayout(v.getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setGravity(Gravity.CENTER);

                    builder.setIcon(v.getContext().getDrawable(R.drawable.luanegra_logo));
                    builder.setTitle(v.getResources().getString(R.string.apagarsala));
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
                    builder.setPositiveButton(v.getResources().getString(R.string.apagar), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child("salas").child(salaList.get(SalaViewHolder.getAdapterPosition()).getUid()).removeValue();
                            Snackbar.make(v,  v.getResources().getString(R.string.salaapagada), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
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
        SalaViewHolder.mGroupLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentGroupName = salaList.get(SalaViewHolder.getAdapterPosition()).getGroupName();
                Intent groupIntent = new Intent(v.getContext(), SalasJogosAntigaActivity.class);
                groupIntent.putExtra("groupName", currentGroupName);
                groupIntent.putExtra("groupUid", salaList.get(SalaViewHolder.getAdapterPosition()).getUid());
                v.getContext().startActivity(groupIntent);
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
        final TextView mGroup;
        final ImageView mGroupLogo;
        SalaViewHolder(View view){
            super(view);
            mLayout = view.findViewById(R.id.layout_group);
            mGroup = view.findViewById(R.id.group_name);
            mGroupLogo = view.findViewById(R.id.group_logo);
        }
    }
}