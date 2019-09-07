package com.lua.luanegra.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.lua.luanegra.R;
import com.lua.luanegra.objects.LogObject;
import com.lua.luanegra.tools.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class RegistodeAcessoAdminsAdapter extends EmptyRecyclerView.Adapter<RegistodeAcessoAdminsAdapter.AcessoAdminsViewHolder> {
    private final ArrayList<LogObject> logList;

    public RegistodeAcessoAdminsAdapter(ArrayList<LogObject> news) {
        this.logList =  news;
    }
    @NonNull
    @Override
    public RegistodeAcessoAdminsAdapter.AcessoAdminsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_applog, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new RegistodeAcessoAdminsAdapter.AcessoAdminsViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RegistodeAcessoAdminsAdapter.AcessoAdminsViewHolder AcessoAdminsViewHolder, int i) {
        try{
            AcessoAdminsViewHolder.mLogText.setText(logList.get(i).getLogText());
            AcessoAdminsViewHolder.mLoguserID.setText(logList.get(AcessoAdminsViewHolder.getAdapterPosition()).getLogCreator());
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

    class AcessoAdminsViewHolder extends EmptyRecyclerView.ViewHolder{
        final MaterialCardView mLayout ;
        final TextView mLogText;
        final TextView mLoguserID;
        AcessoAdminsViewHolder(View view){
            super(view);
            mLayout = view.findViewById(R.id.layout_adminacces);
            mLogText = view.findViewById(R.id.log_text);
            mLoguserID = view.findViewById(R.id.id_log_user);
        }
    }
}

