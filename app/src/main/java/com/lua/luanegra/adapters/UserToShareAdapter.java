package com.lua.luanegra.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lua.luanegra.R;
import com.lua.luanegra.activitys.MensagensActivity;
import com.lua.luanegra.objects.ChatObject;
import com.lua.luanegra.tools.DelayedProgressDialog;
import com.lua.luanegra.tools.EmptyRecyclerView;
import com.squareup.picasso.Picasso;
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

import de.hdodenhof.circleimageview.CircleImageView;


public class UserToShareAdapter extends EmptyRecyclerView.Adapter<UserToShareAdapter.UserListToShareViewHolder> {
    private final ArrayList<ChatObject> userList;
    private int totalMediaUploaded = 0;
    private final ArrayList<String> mediaIDList = new ArrayList<>();
    private final ArrayList<String> mediaUriList = new ArrayList<>();
    private final DelayedProgressDialog progressDialog = new DelayedProgressDialog();

    public UserToShareAdapter(ArrayList<ChatObject> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserToShareAdapter.UserListToShareViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user_toshare, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new UserToShareAdapter.UserListToShareViewHolder(layoutView);
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
    public void onBindViewHolder(@NonNull final UserToShareAdapter.UserListToShareViewHolder userListToShareViewHolder, int i) {
        try{
            SharedPreferences prefs =userListToShareViewHolder.itemView.getContext().getSharedPreferences(userList.get(userListToShareViewHolder.getAdapterPosition()).getChatID(), Context.MODE_PRIVATE);
            final String chatKey = prefs.getString(userList.get(userListToShareViewHolder.getAdapterPosition()).getChatID(), " ");

            userListToShareViewHolder.mName.setText(userList.get(userListToShareViewHolder.getAdapterPosition()).getUserName());
            Picasso.get().load(userList.get(userListToShareViewHolder.getAdapterPosition()).getImagemPerfilUri()).into(userListToShareViewHolder.imagemPerfil);
            userListToShareViewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                        try {
                            FragmentManager manager = ((AppCompatActivity)userListToShareViewHolder.itemView.getContext()).getSupportFragmentManager();
                            progressDialog.show(manager, "tag");
                            Snackbar.make(v, v.getResources().getString(R.string.aenviaraguarda), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            Calendar CalForDate = Calendar.getInstance();
                            if(!userList.get(userListToShareViewHolder.getAdapterPosition()).getImageToShare().equals(" ")){
                                mediaUriList.add(userList.get(userListToShareViewHolder.getAdapterPosition()).getImageToShare());
                            }
                            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                            String currentDate = currentDateFormat.format(CalForDate.getTime());
                            Calendar CalForTime = Calendar.getInstance();
                            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
                            currentTimeFormat.toLocalizedPattern();
                            final String currentTime = currentTimeFormat.format(CalForTime.getTime());
                            final String messageID = FirebaseDatabase.getInstance().getReference().child("user").child("chat").child(userList.get(userListToShareViewHolder.getAdapterPosition()).getChatID()).push().getKey();
                            final DatabaseReference newMessageDB = FirebaseDatabase.getInstance().getReference().child("user").child("chat").child(userList.get(userListToShareViewHolder.getAdapterPosition()).getChatID()).child(Objects.requireNonNull(messageID));
                            final Map<String, Object> newMessageMap = new HashMap<>();
                            String fileNameToSend =  "";
                            if(!userList.get(userListToShareViewHolder.getAdapterPosition()).getImageToShare().equals(" ")){
                                Uri uri = Uri.parse(userList.get(userListToShareViewHolder.getAdapterPosition()).getImageToShare());
                                Cursor cursor = v.getContext().getContentResolver().query(uri, null, null, null, null);
                                int nameIndex;
                                if (cursor != null) {
                                    nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                                    cursor.moveToFirst();
                                    fileNameToSend = cursor.getString(nameIndex);
                                    cursor.close();
                                }
                            }

                            if(!fileNameToSend.equals("")){
                                FirebaseDatabase.getInstance().getReference().child("user").child("chat").child(userList.get(userListToShareViewHolder.getAdapterPosition()).getChatID()).child(Objects.requireNonNull(messageID)).child("text").setValue(fileNameToSend);
                                newMessageMap.put("text", Encrypt(fileNameToSend, chatKey));
                            }else {
                                FirebaseDatabase.getInstance().getReference().child("user").child("chat").child(userList.get(userListToShareViewHolder.getAdapterPosition()).getChatID()).child(Objects.requireNonNull(messageID)).child("text").setValue(userList.get(userListToShareViewHolder.getAdapterPosition()).getTextToShare());
                                newMessageMap.put("text", Encrypt(userList.get(userListToShareViewHolder.getAdapterPosition()).getTextToShare(), chatKey));
                            }
                            FirebaseDatabase.getInstance().getReference().child("user").child("chat").child(userList.get(userListToShareViewHolder.getAdapterPosition()).getChatID()).child(Objects.requireNonNull(messageID)).child("creator").setValue(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                            FirebaseDatabase.getInstance().getReference().child("user").child("chat").child(userList.get(userListToShareViewHolder.getAdapterPosition()).getChatID()).child(Objects.requireNonNull(messageID)).child("data").setValue(currentDate);
                            FirebaseDatabase.getInstance().getReference().child("user").child("chat").child(userList.get(userListToShareViewHolder.getAdapterPosition()).getChatID()).child(Objects.requireNonNull(messageID)).child("hora").setValue(currentTime);
                            newMessageMap.put("creator", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            newMessageMap.put("data", currentDate);
                            newMessageMap.put("hora", currentTime);
                            if(!mediaUriList.isEmpty()) {
                                for (String mediaUri : mediaUriList) {
                                    String mediaID = newMessageDB.child("media").push().getKey();
                                    mediaIDList.add(mediaID);
                                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Media_Mensagens_Privadas").child(userList.get(userListToShareViewHolder.getAdapterPosition()).getChatID()).child(messageID).child(Objects.requireNonNull(mediaID));
                                    UploadTask uploadTask = filePath.putFile(Uri.parse(mediaUri));
                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    FirebaseDatabase.getInstance().getReference().child("user").child("chat").child(userList.get(userListToShareViewHolder.getAdapterPosition()).getChatID()).child(Objects.requireNonNull(messageID)).child("/media/" + mediaIDList.get(totalMediaUploaded) + "/").setValue(uri.toString());
                                                    totalMediaUploaded++;
                                                    if (totalMediaUploaded == mediaUriList.size()) {
                                                        String currentGroupName = userList.get(userListToShareViewHolder.getAdapterPosition()).getUserName();
                                                        Intent groupIntent = new Intent(v.getContext(), MensagensActivity.class);
                                                        groupIntent.putExtra("groupName", currentGroupName);
                                                        groupIntent.putExtra("groupUid", userList.get(userListToShareViewHolder.getAdapterPosition()).getChatID());
                                                        groupIntent.putExtra("partnerNotificationKey", userList.get(userListToShareViewHolder.getAdapterPosition()).getPartnerNotificationKey());
                                                        groupIntent.putExtra("key", chatKey);
                                                        updateDatabaseWtNewMessage(newMessageDB, newMessageMap);
                                                        progressDialog.dismiss();
                                                        v.getContext().startActivity(groupIntent);
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            }else{
                                updateDatabaseWtNewMessage(newMessageDB, newMessageMap);
                                String currentGroupName = userList.get(userListToShareViewHolder.getAdapterPosition()).getUserName();
                                Intent groupIntent = new Intent(v.getContext(), MensagensActivity.class);
                                groupIntent.putExtra("groupName", currentGroupName);
                                groupIntent.putExtra("groupUid", userList.get(userListToShareViewHolder.getAdapterPosition()).getChatID());
                                groupIntent.putExtra("partnerNotificationKey", userList.get(userListToShareViewHolder.getAdapterPosition()).getPartnerNotificationKey());
                                groupIntent.putExtra("key", chatKey);
                                progressDialog.dismiss();
                                v.getContext().startActivity(groupIntent);
                            }
                        }catch (Exception e){
                            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
                }
            });
            if(chatKey.equals(" ")){
                userListToShareViewHolder.mLayout.setEnabled(false);

            }
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
        return userList == null ? 0 : userList.size();
    }

    private void updateDatabaseWtNewMessage(DatabaseReference newMessageDB, Map<String, Object> newMessageMap){
        try{
            newMessageDB.updateChildren(newMessageMap);
            mediaIDList.clear();
            totalMediaUploaded = 0;
        }catch (Exception e){
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    class UserListToShareViewHolder extends EmptyRecyclerView.ViewHolder{
        final TextView mName;
        final LinearLayout mLayout ;
        final CircleImageView imagemPerfil;
        UserListToShareViewHolder(View view){
            super(view);
            imagemPerfil = view.findViewById(R.id.image_user_to_sahre);
            mName = view.findViewById(R.id.name_user_to_share);
            mLayout = view.findViewById(R.id.layout_item_usertoshare);
        }

    }
}
