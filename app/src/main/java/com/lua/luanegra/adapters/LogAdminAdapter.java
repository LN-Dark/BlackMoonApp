package com.lua.luanegra.adapters;


import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.lua.luanegra.objects.LogObject;
import com.lua.luanegra.tools.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class LogAdminAdapter extends EmptyRecyclerView.Adapter<LogAdminAdapter.LogAdminViewHolder> {
    private final ArrayList<LogObject> logList;

    public LogAdminAdapter(ArrayList<LogObject> news) {
        this.logList =  news;
    }

    @NonNull
    @Override
    public LogAdminAdapter.LogAdminViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_log, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new LogAdminAdapter.LogAdminViewHolder(layoutView);
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
    public void onBindViewHolder(@NonNull final LogAdminAdapter.LogAdminViewHolder LogAdminViewHolder, int i) {
        try{
            loadSuperAdmins();
            LogAdminViewHolder.mLogText.setText(logList.get(i).getLogText());
            LogAdminViewHolder.mLoguserID.setText(logList.get(LogAdminViewHolder.getAdapterPosition()).getLogCreator());
            LogAdminViewHolder.mLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    if(listaSuperAdmins.contains(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(v.getContext());
                        LinearLayout layout = new LinearLayout(v.getContext());
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setGravity(Gravity.CENTER);
                        builder.setIcon(v.getContext().getDrawable(R.drawable.luanegra_logo));
                        builder.setTitle(v.getResources().getString(R.string.apagarlog));

                        final TextView espaco2 = new TextView(v.getContext());
                        espaco2.setText(String.format("\n ✶ %s ✶ \n", logList.get(LogAdminViewHolder.getAdapterPosition()).getLogText()));
                        espaco2.setTextSize(12);
                        espaco2.setGravity(Gravity.CENTER);
                        layout.addView(espaco2);

                        builder.setView(layout);
                        builder.setPositiveButton(v.getResources().getString(R.string.apagar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance().getReference().child("logError").child(logList.get(LogAdminViewHolder.getAdapterPosition()).getUid()).removeValue();
                                logList.remove(LogAdminViewHolder.getAdapterPosition());
                                Snackbar.make(v,  v.getResources().getString(R.string.logapagado), Snackbar.LENGTH_LONG)
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
        return logList == null ? 0 : logList.size();
    }

    class LogAdminViewHolder extends EmptyRecyclerView.ViewHolder{
        final LinearLayout mLayout ;
        final TextView mLogText;
        final TextView mLoguserID;
        LogAdminViewHolder(View view){
            super(view);
            mLayout = view.findViewById(R.id.layout_log_admin);
            mLogText = view.findViewById(R.id.log_text);
            mLoguserID = view.findViewById(R.id.id_log_user);
        }
    }
}
