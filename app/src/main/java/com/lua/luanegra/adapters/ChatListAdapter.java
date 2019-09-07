package com.lua.luanegra.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.lua.luanegra.activitys.MensagensActivity;
import com.lua.luanegra.callservice.SendNotification;
import com.lua.luanegra.objects.ChatObject;
import com.lua.luanegra.tools.EmptyRecyclerView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends EmptyRecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>  implements Filterable {
    private ArrayList<ChatObject> chatList;

    public ChatListAdapter(ArrayList<ChatObject> chatList) {
        this.chatList = chatList;
        fullchatlist = chatList;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new ChatListViewHolder(layoutView);
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

    String FCMServer;
    @Override
    public void onBindViewHolder(@NonNull final ChatListViewHolder ChatListViewHolder, int i) {
        try{
        Picasso.get().load(chatList.get(ChatListViewHolder.getAdapterPosition()).getPartnerUserImageUri()).into(ChatListViewHolder.mImagemPerfil);
        ChatListViewHolder.mTitle.setText(chatList.get(ChatListViewHolder.getAdapterPosition()).getUserName());
        String[] separated;
        if(!chatList.get(ChatListViewHolder.getAdapterPosition()).getLastmessage().equals(" ")){
            separated = chatList.get(ChatListViewHolder.getAdapterPosition()).getLastmessage().split("\n");
            ChatListViewHolder.mLastMessage.setText(String.format("%s... \n %s  -  %s", separated[0], chatList.get(ChatListViewHolder.getAdapterPosition()).getLastMessageHora(), chatList.get(ChatListViewHolder.getAdapterPosition()).getLastmessageData()));
        }else {
            ChatListViewHolder.mLastMessage.setText(String.format("  \n %s  -  %s", chatList.get(ChatListViewHolder.getAdapterPosition()).getLastMessageHora(), chatList.get(ChatListViewHolder.getAdapterPosition()).getLastmessageData()));

        }
        if(ChatListViewHolder.mTitle.getText().toString().equals(ChatListViewHolder.itemView.getResources().getString(R.string.mensagens_guardadas))){
            ChatListViewHolder.mLastMessage.setVisibility(View.INVISIBLE);
        }else {
            ChatListViewHolder.mLastMessage.setVisibility(View.VISIBLE);
        }
        if(ChatListViewHolder.mLastMessage.getText().equals("  \n " + " " + "  -  " + " ")){
            ChatListViewHolder.mLastMessage.setText(ChatListViewHolder.itemView.getResources().getString(R.string.aindanaotensmensagens));
        }
        if(ChatListViewHolder.mLastMessage.getText().equals("null \n " + "null" + "  -  " + "null")){
            ChatListViewHolder.mLastMessage.setText(ChatListViewHolder.itemView.getResources().getString(R.string.aindanaotensmensagens));
        }
        ChatListViewHolder.mLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                AlertDialog.Builder builder = new MaterialAlertDialogBuilder(v.getContext());
                LinearLayout layout = new LinearLayout(v.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                builder.setIcon(v.getContext().getDrawable(R.drawable.luanegra_logo));
                builder.setTitle(v.getResources().getString(R.string.chatprivado));
                layout.setGravity(Gravity.CENTER);
                final TextView espaco4 = new TextView(v.getContext());
                espaco4.setText("\n");
                layout.addView(espaco4);
                final CircleImageView imagemPerfil = new CircleImageView(v.getContext());
                layout.addView(imagemPerfil);
                Picasso.get().load(chatList.get(ChatListViewHolder.getAdapterPosition()).getPartnerUserImageUri()).into(imagemPerfil);
                final TextView espaco2 = new TextView(v.getContext());
                espaco2.setText(String.format("\n  %s \n", chatList.get(ChatListViewHolder.getAdapterPosition()).getUserName()));
                espaco2.setTextSize(16);
                espaco2.setGravity(Gravity.CENTER);
                layout.addView(espaco2);
                android.view.ViewGroup.LayoutParams layoutParams = imagemPerfil.getLayoutParams();
                layoutParams.width = 150;
                layoutParams.height = 150;
                imagemPerfil.setLayoutParams(layoutParams);
                builder.setView(layout);
                builder.setPositiveButton(v.getResources().getString(R.string.apagarparamim), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("chat").child(chatList.get(ChatListViewHolder.getAdapterPosition()).getChatID()).removeValue();
                        Snackbar.make(v, chatList.get(ChatListViewHolder.getAdapterPosition()).getUserName() + v.getResources().getString(R.string.apagado), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        chatList.remove(ChatListViewHolder.getAdapterPosition());
                        notifyDataSetChanged();
                    }
                });
                builder.setNeutralButton(v.getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                SharedPreferences prefs = v.getContext().getSharedPreferences(chatList.get(ChatListViewHolder.getAdapterPosition()).getChatID(), Context.MODE_PRIVATE);
                String chatKey = " ";
                chatKey = prefs.getString(chatList.get(ChatListViewHolder.getAdapterPosition()).getChatID(), " ");
                if (!chatKey.equals(" ")){
                    builder.setNegativeButton(v.getResources().getString(R.string.apagarparaosdois), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("chat").child(chatList.get(ChatListViewHolder.getAdapterPosition()).getChatID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    FirebaseDatabase.getInstance().getReference().child("user").child(chatList.get(ChatListViewHolder.getAdapterPosition()).getPartenrUid()).child("chat").child(chatList.get(ChatListViewHolder.getAdapterPosition()).getChatID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            FirebaseDatabase.getInstance().getReference().child("user").child("chat").child(chatList.get(ChatListViewHolder.getAdapterPosition()).getChatID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Snackbar.make(v, chatList.get(ChatListViewHolder.getAdapterPosition()).getUserName() + v.getResources().getString(R.string.apagado), Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();
                                                    chatList.remove(ChatListViewHolder.getAdapterPosition());
                                                    notifyDataSetChanged();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }

                builder.show();
                return false;
            }
});
        ChatListViewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                String currentGroupName = chatList.get(ChatListViewHolder.getAdapterPosition()).getUserName();
                final Intent groupIntent = new Intent(v.getContext(), MensagensActivity.class);
                groupIntent.putExtra("groupName", currentGroupName);
                groupIntent.putExtra("groupUid", chatList.get(ChatListViewHolder.getAdapterPosition()).getChatID());
                groupIntent.putExtra("partnerUID", chatList.get(ChatListViewHolder.getAdapterPosition()).getPartenrUid());
                groupIntent.putExtra("partnerNotificationKey", chatList.get(ChatListViewHolder.getAdapterPosition()).getPartnerNotificationKey());
                SharedPreferences prefs = v.getContext().getSharedPreferences(chatList.get(ChatListViewHolder.getAdapterPosition()).getChatID(), Context.MODE_PRIVATE);
                String chatKey = " ";
                chatKey = prefs.getString(chatList.get(ChatListViewHolder.getAdapterPosition()).getChatID(), " ");
                if (!chatKey.equals(" ")){
                    groupIntent.putExtra("key", chatKey);
                    v.getContext().startActivity(groupIntent);
                }else{
                    AlertDialog.Builder builder = new MaterialAlertDialogBuilder(v.getContext());
                    LinearLayout layout = new LinearLayout(v.getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);

                    builder.setIcon(v.getContext().getDrawable(R.drawable.luanegra_logo));
                    builder.setTitle(v.getResources().getString(R.string.chatprivado));
                    builder.setMessage(v.getContext().getString(R.string.janaotensachavedestaconversa) + chatList.get(ChatListViewHolder.getAdapterPosition()).getUserName());
                    layout.setGravity(Gravity.CENTER);
                    final TextView espaco4 = new TextView(v.getContext());
                    espaco4.setText("\n");
                    layout.addView(espaco4);
                    final CircleImageView imagemPerfil = new CircleImageView(v.getContext());
                    layout.addView(imagemPerfil);
                    Picasso.get().load(chatList.get(ChatListViewHolder.getAdapterPosition()).getPartnerUserImageUri()).into(imagemPerfil);
                    final TextView espaco2 = new TextView(v.getContext());
                    espaco2.setText(String.format("\n  %s \n", chatList.get(ChatListViewHolder.getAdapterPosition()).getUserName()));
                    espaco2.setTextSize(16);
                    espaco2.setGravity(Gravity.CENTER);
                    layout.addView(espaco2);
                    android.view.ViewGroup.LayoutParams layoutParams = imagemPerfil.getLayoutParams();
                    layoutParams.width = 150;
                    layoutParams.height = 150;
                    imagemPerfil.setLayoutParams(layoutParams);
                    builder.setView(layout);
                    builder.setPositiveButton(v.getContext().getString(R.string.pedirchavedeacesso), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            DatabaseReference mAppKeysRef = FirebaseDatabase.getInstance().getReference().child("appKeys");
                            mAppKeysRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                                            if ("serverFCM".equals(Objects.requireNonNull(childSnapshot.getKey()))) {
                                                FCMServer = Objects.requireNonNull(childSnapshot.getValue()).toString();
                                                break;
                                            }
                                        }
                                        JSONObject notification = new JSONObject();
                                        JSONObject notifcationBody = new JSONObject();
                                        try {
                                            notifcationBody.put("title", v.getContext().getString(R.string.mensagensprivadas));
                                            notifcationBody.put("message", v.getContext().getString(R.string.outilizador)  + " " + chatList.get(ChatListViewHolder.getAdapterPosition()).getCurrentUserName()  + " " +  v.getContext().getString(R.string.janaotemchaveeestaapediracesso));

                                            notification.put("to", "/LuaNegra/" + chatList.get(ChatListViewHolder.getAdapterPosition()).getPartnerNotificationKey());
                                            notification.put("data", notifcationBody);
                                        } catch (Exception e) {
                                            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(" " + this.getClass().getName() + " \n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
                                        }
                                        SendNotification sendNotification = new SendNotification();
                                        sendNotification.sendNotification(notification, v.getContext(), FCMServer);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

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
                }
            }
        });
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(" " + this.getClass().getName() + " \n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    private static ArrayList<ChatObject> fullchatlist;
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    chatList = fullchatlist;
                } else {
                    ArrayList<ChatObject> filteredList = new ArrayList<>();
                    for (ChatObject row : fullchatlist) {

                        if (row.getUserName().toLowerCase().contains(charString.toLowerCase()) || row.getUserName().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    chatList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = chatList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                chatList = (ArrayList<ChatObject>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class ChatListViewHolder extends EmptyRecyclerView.ViewHolder{
        final TextView mTitle;
        final TextView mLastMessage;
        final LinearLayout mLayout ;
        final ImageView mImagemPerfil;
        ChatListViewHolder(View view){
            super(view);
            mImagemPerfil = view.findViewById(R.id.chatUserImage);
            mTitle = view.findViewById(R.id.title);
            mLastMessage = view.findViewById(R.id.lastmessage);
            mLayout = view.findViewById(R.id.layout_chat_user);
        }
    }
}