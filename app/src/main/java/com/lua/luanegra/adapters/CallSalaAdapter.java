package com.lua.luanegra.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.lua.luanegra.R;
import com.lua.luanegra.objects.ChatObject;
import com.lua.luanegra.tools.EmptyRecyclerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class CallSalaAdapter extends EmptyRecyclerView.Adapter<CallSalaAdapter.ChatListViewHolder> {
    private final ArrayList<ChatObject> chatList;

    public CallSalaAdapter(ArrayList<ChatObject> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new ChatListViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListViewHolder ChatListViewHolder, final int i) {
        try{
        Picasso.get().load(chatList.get(ChatListViewHolder.getAdapterPosition()).getImagemPerfilUri()).into(ChatListViewHolder.mImagemPerfil);
        ChatListViewHolder.mTitle.setText(chatList.get(ChatListViewHolder.getAdapterPosition()).getUserName());
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }
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
        return chatList == null ? 0 : chatList.size();
    }


    public class ChatListViewHolder extends EmptyRecyclerView.ViewHolder{
        final TextView mTitle;
        final LinearLayout mLayout ;
        final ImageView mImagemPerfil;
        ChatListViewHolder(View view){
            super(view);
            mImagemPerfil = view.findViewById(R.id.image_user);
            mTitle = view.findViewById(R.id.name);
            mLayout = view.findViewById(R.id.layout_chat_user);
        }
    }
}