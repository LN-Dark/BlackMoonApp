package com.lua.luanegra.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.koushikdutta.ion.Ion;
import com.lua.luanegra.R;
import com.lua.luanegra.objects.MemesObject;
import com.lua.luanegra.tools.EmptyRecyclerView;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MemesAdapter extends EmptyRecyclerView.Adapter<MemesAdapter.MemesViewHolder> {
    private final ArrayList<MemesObject> memesList;
    private final Context context;

    public MemesAdapter(Context context, ArrayList<MemesObject> memesList){
        this.context = context;
        this.memesList = memesList;
    }

    @NonNull
    @Override
    public MemesAdapter.MemesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_memes, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new MemesViewHolder(layoutView);
    }

    private String Encrypt(String text, String key){
        String result = " ";
        try {
            AesCbcWithIntegrity.SecretKeys keys = AesCbcWithIntegrity.keys(key);
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = AesCbcWithIntegrity.encrypt(text, keys);
            result = cipherTextIvMac.toString();
        } catch (UnsupportedEncodingException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public void onBindViewHolder(@NonNull final MemesAdapter.MemesViewHolder memesViewHolder, int i) {
try{
        Ion.with(memesViewHolder.mMedia).load(memesList.get(i).getUri());
        memesViewHolder.mMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (memesList.get(memesViewHolder.getAdapterPosition()).getWhatActivity()) {
                    case "chat": {
                        DatabaseReference mChatDB = FirebaseDatabase.getInstance().getReference().child("user").child("chat").child(memesList.get(memesViewHolder.getAdapterPosition()).getChatID());
                        Calendar CalForDate = Calendar.getInstance();
                        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                        String currentDate = currentDateFormat.format(CalForDate.getTime());
                        Calendar CalForTime = Calendar.getInstance();
                        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
                        currentTimeFormat.toLocalizedPattern();
                        String currentTime = currentTimeFormat.format(CalForTime.getTime());
                        String messageID = mChatDB.push().getKey();
                        final DatabaseReference newMessageDB = mChatDB.child(Objects.requireNonNull(messageID));
                        final Map<String, Object> newMessageMap = new HashMap<>();
                        newMessageMap.put("text", Encrypt("meme.gif", (memesList.get(memesViewHolder.getAdapterPosition()).getChatKey())));
                        newMessageMap.put("creator", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                        newMessageMap.put("data", currentDate);
                        newMessageMap.put("hora", currentTime);
                        String mediaID = newMessageDB.child("media").push().getKey();
                        newMessageMap.put("/media/" + mediaID + "/", memesList.get(memesViewHolder.getAdapterPosition()).getUri());
                        newMessageDB.updateChildren(newMessageMap);
                        Snackbar.make(v, context.getString(R.string.memeenviado), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        ((Activity) context).finish();
                        break;
                    }
                    case "groupChat": {
                        DatabaseReference mChatDB = FirebaseDatabase.getInstance().getReference().child("salas").child(memesList.get(memesViewHolder.getAdapterPosition()).getChatID());
                        Calendar CalForDate = Calendar.getInstance();
                        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                        String currentDate = currentDateFormat.format(CalForDate.getTime());
                        Calendar CalForTime = Calendar.getInstance();
                        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
                        currentTimeFormat.toLocalizedPattern();
                        String currentTime = currentTimeFormat.format(CalForTime.getTime());
                        String messageID = mChatDB.push().getKey();
                        final DatabaseReference newMessageDB = mChatDB.child("mensagens").child(Objects.requireNonNull(messageID));
                        final Map<String, Object> newMessageMap = new HashMap<>();
                        newMessageMap.put("text", "meme.gif");
                        newMessageMap.put("creator", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                        newMessageMap.put("data", currentDate);
                        newMessageMap.put("hora", currentTime);
                        String mediaID = newMessageDB.child("media").push().getKey();
                        newMessageMap.put("/media/" + mediaID + "/", memesList.get(memesViewHolder.getAdapterPosition()).getUri());
                        newMessageDB.updateChildren(newMessageMap);
                        Snackbar.make(v, context.getString(R.string.memeenviado), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        ((Activity) context).finish();
                        break;
                    }
                    case "salaPublica": {
                        DatabaseReference mChatDB = FirebaseDatabase.getInstance().getReference().child("salasPublicas").child(memesList.get(memesViewHolder.getAdapterPosition()).getChatID());
                        Calendar CalForDate = Calendar.getInstance();
                        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                        String currentDate = currentDateFormat.format(CalForDate.getTime());
                        Calendar CalForTime = Calendar.getInstance();
                        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
                        currentTimeFormat.toLocalizedPattern();
                        String currentTime = currentTimeFormat.format(CalForTime.getTime());
                        String messageID = mChatDB.push().getKey();
                        final DatabaseReference newMessageDB = mChatDB.child("mensagens").child(Objects.requireNonNull(messageID));
                        final Map<String, Object> newMessageMap = new HashMap<>();
                        newMessageMap.put("text", "meme.gif");
                        newMessageMap.put("creator", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                        newMessageMap.put("data", currentDate);
                        newMessageMap.put("hora", currentTime);
                        String mediaID = newMessageDB.child("media").push().getKey();
                        newMessageMap.put("/media/" + mediaID + "/", memesList.get(memesViewHolder.getAdapterPosition()).getUri());
                        newMessageDB.updateChildren(newMessageMap);
                        Snackbar.make(v, context.getString(R.string.memeenviado), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        ((Activity) context).finish();
                        break;
                    }
                    case "salaPrivada": {
                        DatabaseReference mChatDB = FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(memesList.get(memesViewHolder.getAdapterPosition()).getChatID());
                        Calendar CalForDate = Calendar.getInstance();
                        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                        String currentDate = currentDateFormat.format(CalForDate.getTime());
                        Calendar CalForTime = Calendar.getInstance();
                        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
                        currentTimeFormat.toLocalizedPattern();
                        String currentTime = currentTimeFormat.format(CalForTime.getTime());
                        String messageID = mChatDB.push().getKey();
                        final DatabaseReference newMessageDB = mChatDB.child("mensagens").child(Objects.requireNonNull(messageID));
                        final Map<String, Object> newMessageMap = new HashMap<>();
                        newMessageMap.put("text", Encrypt("meme.gif", (memesList.get(memesViewHolder.getAdapterPosition()).getChatKey())));
                        newMessageMap.put("creator", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                        newMessageMap.put("data", currentDate);
                        newMessageMap.put("hora", currentTime);
                        String mediaID = newMessageDB.child("media").push().getKey();
                        newMessageMap.put("/media/" + mediaID + "/", memesList.get(memesViewHolder.getAdapterPosition()).getUri());
                        newMessageDB.updateChildren(newMessageMap);
                        Snackbar.make(v, context.getString(R.string.memeenviado), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        ((Activity) context).finish();
                        break;
                    }
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
        return memesList == null ? 0 : memesList.size();
    }

    class MemesViewHolder extends EmptyRecyclerView.ViewHolder {
        final ImageView mMedia;
        MemesViewHolder(@NonNull View itemView) {
            super(itemView);
            mMedia = itemView.findViewById(R.id.richLinkView_memes);
        }
    }
}
