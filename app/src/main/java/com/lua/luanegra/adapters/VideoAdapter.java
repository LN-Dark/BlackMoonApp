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
import com.lua.luanegra.callservice.SendNotification;
import com.lua.luanegra.objects.VideoObject;
import com.lua.luanegra.tools.EmptyRecyclerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoAdapter extends EmptyRecyclerView.Adapter<VideoAdapter.VideoViewHolder>  {
    private final ArrayList<VideoObject> videoList;

    public VideoAdapter(ArrayList<VideoObject> video) {
        this.videoList =  video;
    }
    private String FCMServer, SinchKey, SinchSecret;

    @NonNull
    @Override
    public VideoAdapter.VideoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_video, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new VideoAdapter.VideoViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoAdapter.VideoViewHolder videoViewHolder, final int i) {
        try{
            DatabaseReference mAppKeysRef = FirebaseDatabase.getInstance().getReference().child("appKeys");
            mAppKeysRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot6) {
                    if(dataSnapshot6.exists()){
                        for(DataSnapshot childSnapshot : dataSnapshot6.getChildren()){
                            switch (Objects.requireNonNull(childSnapshot.getKey())) {
                                case "serverFCM":
                                    FCMServer = Objects.requireNonNull(childSnapshot.getValue()).toString();
                                    break;
                                case "sinchAppKey":
                                    SinchKey = Objects.requireNonNull(childSnapshot.getValue()).toString();
                                    break;
                                case "sinchAppSecret":
                                    SinchSecret = Objects.requireNonNull(childSnapshot.getValue()).toString();
                                    break;
                            }
                        }
                        videoViewHolder.tituloVideo.setText(String.format("✶  %s  ✶", videoList.get(videoViewHolder.getAdapterPosition()).getVideoCreator()));
                        videoViewHolder.mVideoDate.setText(String.format("%s   -   ", videoList.get(videoViewHolder.getAdapterPosition()).getData()));
                        videoViewHolder.mVideo.setText(videoList.get(videoViewHolder.getAdapterPosition()).getVideoText());

                        Picasso.get().load(videoList.get(videoViewHolder.getAdapterPosition()).getVideoImageCreator()).into(videoViewHolder.mImage);
                        videoViewHolder.videoLink.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                            @Override
                            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                                if(videoViewHolder.getAdapterPosition() != -1){
                                    String videoId = videoList.get(videoViewHolder.getAdapterPosition()).getVideoLink();
                                    youTubePlayer.loadVideo(videoId, 0);
                                    youTubePlayer.pause();
                                }

                            }
                        });
                        final ArrayList<String> listaAdmins = new ArrayList<>();
                        videoViewHolder.mVideoHora.setText(videoList.get(videoViewHolder.getAdapterPosition()).getHora());
                        videoViewHolder.mLayout.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(final View v) {
                                FirebaseDatabase.getInstance().getReference().child("admins").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                                                String idAdmin = childSnapshot.getKey();
                                                listaAdmins.add(idAdmin);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                if(listaAdmins.contains(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                    AlertDialog.Builder builder = new MaterialAlertDialogBuilder(v.getContext());
                                    LinearLayout layout = new LinearLayout(v.getContext());
                                    layout.setOrientation(LinearLayout.VERTICAL);
                                    layout.setGravity(Gravity.CENTER);
                                    builder.setIcon(v.getContext().getDrawable(R.drawable.luanegra_logo));
                                    builder.setTitle(v.getResources().getString(R.string.apagarvideo));

                                    final TextView espaco2 = new TextView(v.getContext());
                                    espaco2.setText(String.format("\n\n %s ✶ \n\n", videoList.get(videoViewHolder.getAdapterPosition()).getVideoText()));
                                    espaco2.setTextSize(12);

                                    espaco2.setGravity(Gravity.CENTER);
                                    layout.addView(espaco2);

                                    builder.setView(layout);
                                    builder.setNeutralButton(v.getResources().getString(R.string.apagarerepreender), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Snackbar.make(v,  v.getResources().getString(R.string.videoapagado), Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                            FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.exists()){
                                                        for(DataSnapshot childUserSnapShot : dataSnapshot.getChildren()){



                                                            if (Objects.requireNonNull(childUserSnapShot.getKey()).equals(videoList.get(videoViewHolder.getAdapterPosition()).getUid())) {
                                                                JSONObject notification2 = new JSONObject();
                                                                JSONObject notifcationBody2 = new JSONObject();
                                                                try {
                                                                    notifcationBody2.put("title", v.getResources().getString(R.string.advertencia));
                                                                    notifcationBody2.put("message", v.getContext().getString(R.string.videoimproprioquepartilhaste));

                                                                    notification2.put("to", "/LuaNegra/" + Objects.requireNonNull(childUserSnapShot.child("notificationKey").getValue()).toString());
                                                                    notification2.put("data", notifcationBody2);
                                                                } catch (Exception e) {
                                                                    FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
                                                                }
                                                                SendNotification sendNotification2 = new SendNotification();
                                                                sendNotification2.sendNotification(notification2, v.getContext(), FCMServer);
                                                            }

                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            String keyAdvert = FirebaseDatabase.getInstance().getReference().child("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("advertencias").push().getKey();
                                            FirebaseDatabase.getInstance().getReference().child("videos").child(videoList.get(videoViewHolder.getAdapterPosition()).getUid()).removeValue();
                                            FirebaseDatabase.getInstance().getReference().child("user").child(videoList.get(videoViewHolder.getAdapterPosition()).getCreatorUID()).child("advertencias").child(Objects.requireNonNull(keyAdvert)).setValue(true);
                                            videoList.remove(videoViewHolder.getAdapterPosition());
                                            notifyDataSetChanged();
                                        }
                                    });
                                    builder.setPositiveButton(v.getResources().getString(R.string.apagar), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FirebaseDatabase.getInstance().getReference().child("videos").child(videoList.get(videoViewHolder.getAdapterPosition()).getUid()).removeValue();
                                            Snackbar.make(v,  v.getResources().getString(R.string.videoapagado), Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                            videoList.remove(videoViewHolder.getAdapterPosition());
                                            notifyDataSetChanged();
                                        }
                                    });
                                    builder.setNegativeButton(v.getContext().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                        }
                                    });
                                    builder.show();
                                }else if(videoList.get(videoViewHolder.getAdapterPosition()).getVideoCreator().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    AlertDialog.Builder builder = new MaterialAlertDialogBuilder(v.getContext());
                                    LinearLayout layout = new LinearLayout(v.getContext());
                                    layout.setOrientation(LinearLayout.VERTICAL);
                                    layout.setGravity(Gravity.CENTER);

                                    builder.setIcon(v.getContext().getDrawable(R.drawable.luanegra_logo));
                                    builder.setTitle(v.getResources().getString(R.string.apagarvideo));

                                    final TextView espaco2 = new TextView(v.getContext());
                                    espaco2.setText(String.format("\n\n %s ✶ \n\n", videoList.get(videoViewHolder.getAdapterPosition()).getVideoText()));
                                    espaco2.setTextSize(12);

                                    espaco2.setGravity(Gravity.CENTER);
                                    layout.addView(espaco2);

                                    builder.setView(layout);
                                    builder.setPositiveButton(v.getResources().getString(R.string.apagar), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            Snackbar.make(v,  v.getResources().getString(R.string.videoapagado), Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                            FirebaseDatabase.getInstance().getReference().child("videos").child(videoList.get(videoViewHolder.getAdapterPosition()).getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    videoList.remove(videoViewHolder.getAdapterPosition());
                                                    notifyDataSetChanged();
                                                }
                                            });

                                        }
                                    });
                                    builder.setNegativeButton(v.getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return videoList == null ? 0 : videoList.size();
    }

    class VideoViewHolder extends EmptyRecyclerView.ViewHolder {
        final LinearLayout mLayout ;
        final CircleImageView mImage;
        final YouTubePlayerView videoLink;
        final TextView mVideo;
        final TextView mVideoDate;
        final TextView mVideoHora;
        final TextView tituloVideo;
        VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            videoLink = itemView.findViewById(R.id.richLinkView_video);
            tituloVideo = itemView.findViewById(R.id.txt_titulo_video);
            mImage = itemView.findViewById(R.id.icon_video);
            mLayout = itemView.findViewById(R.id.item_video_layout);
            mVideo = itemView.findViewById(R.id.message_video);
            mVideoDate = itemView.findViewById(R.id.video_Date);
            mVideoHora = itemView.findViewById(R.id.video_Hora);
        }
    }
}
