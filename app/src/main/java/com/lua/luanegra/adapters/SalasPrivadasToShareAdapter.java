package com.lua.luanegra.adapters;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lua.luanegra.R;
import com.lua.luanegra.activitys.Salas.Privadas.SalaPrivadaActivity;
import com.lua.luanegra.objects.GroupObject;
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

public class SalasPrivadasToShareAdapter extends EmptyRecyclerView.Adapter<SalasPrivadasToShareAdapter.SalaToShareViewHolder> {
    private final ArrayList<GroupObject> groupList;
    private final DelayedProgressDialog progressDialog = new DelayedProgressDialog();

    public SalasPrivadasToShareAdapter(ArrayList<GroupObject> groupList) {
        this.groupList = groupList;
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

    @NonNull
    @Override
    public SalasPrivadasToShareAdapter.SalaToShareViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_salatoshare, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new SalasPrivadasToShareAdapter.SalaToShareViewHolder(layoutView);
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

    private final ArrayList<String> mediaIDList = new ArrayList<>();
    private final ArrayList<String> mediaUriList = new ArrayList<>();
    private int totalMediaUploaded = 0;

    @Override
    public void onBindViewHolder(@NonNull final SalasPrivadasToShareAdapter.SalaToShareViewHolder SalaToShareViewHolder, int i) {
        try{
            loadSuperAdmins();
            SalaToShareViewHolder.mGroup.setText(groupList.get(SalaToShareViewHolder.getAdapterPosition()).getGroupName());
            Picasso.get().load(groupList.get(SalaToShareViewHolder.getAdapterPosition()).getLogoUri()).into(SalaToShareViewHolder.mGroupLogo);
            SalaToShareViewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    try{
                        FragmentManager manager = ((AppCompatActivity)SalaToShareViewHolder.itemView.getContext()).getSupportFragmentManager();
                        progressDialog.show(manager, "tag");
                        Snackbar.make(v, v.getResources().getString(R.string.aenviaraguarda), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        Calendar CalForDate = Calendar.getInstance();
                        if(!groupList.get(SalaToShareViewHolder.getAdapterPosition()).getImagemToShare().equals(" ")){
                            mediaUriList.add(groupList.get(SalaToShareViewHolder.getAdapterPosition()).getImagemToShare());
                        }
                        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);
                        String currentDate = currentDateFormat.format(CalForDate.getTime());
                        Calendar CalForTime = Calendar.getInstance();
                        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("H:mm", Locale.UK);
                        currentTimeFormat.toLocalizedPattern();
                        String currentTime = currentTimeFormat.format(CalForTime.getTime());
                        String messageID = FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupList.get(SalaToShareViewHolder.getAdapterPosition()).getUid()).child("mensagens").push().getKey();
                        final DatabaseReference newMessageDB = FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(groupList.get(SalaToShareViewHolder.getAdapterPosition()).getUid()).child("mensagens").child(Objects.requireNonNull(messageID));
                        final Map<String, Object> newMessageMap = new HashMap<>();
                        String fileNameToSend =  "";
                        if(!groupList.get(SalaToShareViewHolder.getAdapterPosition()).getImagemToShare().equals(" ")){
                            Uri uri = Uri.parse(groupList.get(SalaToShareViewHolder.getAdapterPosition()).getImagemToShare());
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
                            newMessageMap.put("text", Encrypt(fileNameToSend, groupList.get(SalaToShareViewHolder.getAdapterPosition()).getChatKey()));
                        }else {
                            newMessageMap.put("text", Encrypt(groupList.get(SalaToShareViewHolder.getAdapterPosition()).getTextToShare(), groupList.get(SalaToShareViewHolder.getAdapterPosition()).getChatKey()));
                        }
                        newMessageMap.put("creator", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                        newMessageMap.put("data", currentDate);
                        newMessageMap.put("hora", currentTime);
                        if(!mediaUriList.isEmpty()) {
                            for (String mediaUri : mediaUriList) {
                                String mediaID = newMessageDB.child("media").push().getKey();
                                mediaIDList.add(mediaID);
                                final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("Media_Salas").child(groupList.get(SalaToShareViewHolder.getAdapterPosition()).getUid()).child(messageID).child(Objects.requireNonNull(mediaID));
                                UploadTask uploadTask = filePath.putFile(Uri.parse(mediaUri));
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                newMessageMap.put("/media/" + mediaIDList.get(totalMediaUploaded) + "/", uri.toString());
                                                totalMediaUploaded++;
                                                if (totalMediaUploaded == mediaUriList.size()) {
                                                    updateDatabaseWtNewMessage(newMessageDB, newMessageMap);
                                                    String currentGroupName = groupList.get(SalaToShareViewHolder.getAdapterPosition()).getGroupName();
                                                    Intent groupIntent = new Intent(v.getContext(), SalaPrivadaActivity.class);
                                                    groupIntent.putExtra("groupName", currentGroupName);
                                                    groupIntent.putExtra("groupUid", groupList.get(SalaToShareViewHolder.getAdapterPosition()).getUid());
                                                    groupIntent.putExtra("key", groupList.get(SalaToShareViewHolder.getAdapterPosition()).getChatKey());
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
                            String currentGroupName = groupList.get(SalaToShareViewHolder.getAdapterPosition()).getGroupName();
                            Intent groupIntent = new Intent(v.getContext(), SalaPrivadaActivity.class);
                            groupIntent.putExtra("groupName", currentGroupName);
                            groupIntent.putExtra("groupUid", groupList.get(SalaToShareViewHolder.getAdapterPosition()).getUid());
                            groupIntent.putExtra("key", groupList.get(SalaToShareViewHolder.getAdapterPosition()).getChatKey());
                            progressDialog.dismiss();
                            v.getContext().startActivity(groupIntent);
                        }
                    }catch (Exception e){
                        FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
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
        return groupList == null ? 0 : groupList.size();
    }

    private void updateDatabaseWtNewMessage(DatabaseReference newMessageDB, Map<String, Object> newMessageMap) {
        try{
            newMessageDB.updateChildren(newMessageMap);
            mediaUriList.clear();
            totalMediaUploaded = 0;
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    class SalaToShareViewHolder extends EmptyRecyclerView.ViewHolder{
        final LinearLayout mLayout ;
        final TextView mGroup;
        final ImageView mGroupLogo;
        SalaToShareViewHolder(View view){
            super(view);
            mLayout = view.findViewById(R.id.layout_item_salatoshare);
            mGroup = view.findViewById(R.id.name_salaToShare);
            mGroupLogo = view.findViewById(R.id.image_user_toSalasShare);
        }
    }
}

