package com.lua.luanegra.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.lua.luanegra.R;
import com.lua.luanegra.objects.MessageObject;
import com.lua.luanegra.tools.EmptyRecyclerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.ponnamkarthik.richlinkpreview.RichLinkViewTwitter;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;

public class MessageAdapter extends EmptyRecyclerView.Adapter<MessageAdapter.MessageViewHolder>  implements Filterable {
    private ArrayList<MessageObject> messageList;
    private SimpleExoPlayer player;
    public MessageAdapter(ArrayList<MessageObject> message) {
        this.messageList = message;
        fullMessagelist = message;
    }

    private static ArrayList<MessageObject> fullMessagelist;
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    messageList = fullMessagelist;
                } else {
                    ArrayList<MessageObject> filteredList = new ArrayList<>();
                    for (MessageObject row : fullMessagelist) {

                        if (row.getmessage().toLowerCase().contains(charString.toLowerCase()) || row.getmessage().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    messageList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = messageList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                messageList = (ArrayList<MessageObject>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new MessageViewHolder(layoutView);
    }

    public void onBindViewHolder(@NonNull final MessageViewHolder MessageViewHolder, int i) {
        try{
            for(int q = 0; q < messageList.get(MessageViewHolder.getAdapterPosition()).getListaUsers().size(); q++){
                if(messageList.get(MessageViewHolder.getAdapterPosition()).getListaUsers().get(q).getUid().equals(messageList.get(MessageViewHolder.getAdapterPosition()).getSenderID())){
                    Picasso.get().load(messageList.get(MessageViewHolder.getAdapterPosition()).getListaUsers().get(q).getImagemPerfilUri()).into(MessageViewHolder.userImage);
                    MessageViewHolder.mMessage.setText(messageList.get(MessageViewHolder.getAdapterPosition()).getmessage());
                    if(MessageViewHolder.mMessage.getText().toString().equals("meme.gif")){
                        MessageViewHolder.mMessage.setVisibility(View.INVISIBLE);
                    }else {
                        MessageViewHolder.mMessage.setVisibility(View.VISIBLE);
                    }
                    MessageViewHolder.userImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            FirebaseDatabase.getInstance().getReference().child("user").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot222) {
                                    if(dataSnapshot222.exists()){
                                        for(DataSnapshot childuserPerfilSnapShot : dataSnapshot222.getChildren()){
                                            if(childuserPerfilSnapShot.getKey().equals(messageList.get(MessageViewHolder.getAdapterPosition()).getSenderID())){
                                                final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(v.getContext());
                                                LinearLayout layout = new LinearLayout(v.getContext());
                                                layout.setOrientation(LinearLayout.VERTICAL);
                                                builder3.setIcon(v.getContext().getDrawable(R.drawable.luanegra_logo));
                                                builder3.setTitle(v.getResources().getString(R.string.perfiltitulo));
                                                final TextView espaco9 = new TextView(v.getContext());
                                                espaco9.setText(" ");
                                                layout.addView(espaco9);
                                                final CircleImageView icon = new CircleImageView(v.getContext());
                                                LinearLayout.LayoutParams layoutParams  = new LinearLayout.LayoutParams(150, 150);
                                                layoutParams.gravity = Gravity.CENTER;
                                                icon.setLayoutParams(layoutParams);
                                                layout.addView(icon);
                                                Picasso.get().load(childuserPerfilSnapShot.child("profile_image").getValue().toString()).into(icon);
                                                final TextView titulo = new TextView(v.getContext());
                                                titulo.setText(String.format(v.getResources().getString(R.string.primeironome), childuserPerfilSnapShot.child("name").getValue().toString(), childuserPerfilSnapShot.child("registerNick").getValue().toString()));
                                                titulo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                                                titulo.setTextSize(16);
                                                layout.addView(titulo);
                                                final TextView espaco4 = new TextView(v.getContext());
                                                espaco4.setText(" ");
                                                layout.addView(espaco4);
                                                final TextView bio = new TextView(v.getContext());
                                                bio.setText("Bio: \n\n" + childuserPerfilSnapShot.child("bio").getValue().toString());
                                                bio.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                                                bio.setTextSize(16);
                                                layout.addView(bio);
                                                final TextView espaco42 = new TextView(v.getContext());
                                                espaco42.setText(" ");
                                                layout.addView(espaco42);
                                                final TextView titulo2 = new TextView(v.getContext());
                                                titulo2.setText(v.getContext().getString(R.string.ultimavezonline) +"\n" + Objects.requireNonNull(childuserPerfilSnapShot.child("onlineDate").getValue()).toString() + "  *  " + Objects.requireNonNull(childuserPerfilSnapShot.child("onlineTime").getValue()).toString());
                                                titulo2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                                                titulo2.setTextSize(13);
                                                layout.addView(titulo2);
                                                final TextView espaco43 = new TextView(v.getContext());
                                                espaco43.setText(" ");
                                                layout.addView(espaco43);
                                                final TextView datainscri = new TextView(v.getContext());
                                                datainscri.setText(v.getResources().getString(R.string.utilizadorregistadodesde) + childuserPerfilSnapShot.child("registerDate").getValue().toString() + "  -  " + childuserPerfilSnapShot.child("registerTime").getValue().toString());
                                                datainscri.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                                                datainscri.setTextSize(13);
                                                layout.addView(datainscri);
                                                final TextView espaco433 = new TextView(v.getContext());
                                                espaco433.setText(" ");
                                                layout.addView(espaco433);
                                                builder3.setCancelable(false);
                                                builder3.setView(layout);
                                                builder3.setPositiveButton(v.getContext().getResources().getString(R.string.fecharapp), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });
                                                AlertDialog alert = builder3.create();
                                                alert.show();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                    MessageViewHolder.mSender.setText(String.format("%s  ✶  %s - %s", messageList.get(MessageViewHolder.getAdapterPosition()).getListaUsers().get(q).getName(), messageList.get(MessageViewHolder.getAdapterPosition()).getHora(), messageList.get(MessageViewHolder.getAdapterPosition()).getData()));
                    if(MessageViewHolder.mMessage.getText().toString().contains(".mp4") || MessageViewHolder.mMessage.getText().toString().contains(".3gp") || MessageViewHolder.mMessage.getText().toString().contains(".wmv")|| MessageViewHolder.mMessage.getText().toString().contains(".wma")|| MessageViewHolder.mMessage.getText().toString().contains(".mp3")|| MessageViewHolder.mMessage.getText().toString().contains(".wav")){
                        player = ExoPlayerFactory.newSimpleInstance(MessageViewHolder.itemView.getContext());
                        MessageViewHolder.exoplayer.setPlayer(player);
                        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(MessageViewHolder.itemView.getContext(),
                                Util.getUserAgent(MessageViewHolder.itemView.getContext(), "✶ Lua ॐ Negra ✶"));
                        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(messageList.get(MessageViewHolder.getAdapterPosition()).getMediaUrlList().get(0)));
                        player.prepare(videoSource);
                        player.addListener(new Player.EventListener() {
                            @Override
                            public void onLoadingChanged(boolean isLoading) {
                                if(MessageViewHolder.exoplayer.getWidth() > MessageViewHolder.exoplayer.getHeight()){
                                    ViewGroup.LayoutParams layoutParams = MessageViewHolder.exoplayer.getLayoutParams();
                                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                    layoutParams.height = 700;
                                    MessageViewHolder.exoplayer.setLayoutParams(layoutParams);
                                }else {
                                    ViewGroup.LayoutParams layoutParams = MessageViewHolder.exoplayer.getLayoutParams();
                                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                    layoutParams.height = 500;
                                    MessageViewHolder.exoplayer.setLayoutParams(layoutParams);
                                }
                            }
                        });
                    }else if(MessageViewHolder.mMessage.getText().toString().contains(".zip") || MessageViewHolder.mMessage.getText().toString().contains(".exe")|| MessageViewHolder.mMessage.getText().toString().contains(".rar")|| MessageViewHolder.mMessage.getText().toString().contains(".jar")|| MessageViewHolder.mMessage.getText().toString().contains(".deb")|| MessageViewHolder.mMessage.getText().toString().contains(".7zip")|| MessageViewHolder.mMessage.getText().toString().contains(".zip")|| MessageViewHolder.mMessage.getText().toString().contains(".apk")){
                        MessageViewHolder.mDownloadMedia.setVisibility(View.VISIBLE);

                    }else if(messageList.get(MessageViewHolder.getAdapterPosition()).mediaUrlList.size() > 0){
                        MessageViewHolder.mDownloadMedia.setVisibility(View.VISIBLE);
                        MessageViewHolder.mPreviewImage.setVisibility(View.VISIBLE);
                        Ion.with(MessageViewHolder.mPreviewImage).load(messageList.get(MessageViewHolder.getAdapterPosition()).getMediaUrlList().get(0)).setCallback(new FutureCallback<ImageView>() {
                            @Override
                            public void onCompleted(Exception e, ImageView result) {
                                if(result != null){
                                    if(Ion.with(MessageViewHolder.mPreviewImage).getBitmap().getWidth() < Ion.with(MessageViewHolder.mPreviewImage).getBitmap().getHeight()){
                                        result.setMaxHeight(500);
                                        ViewGroup.LayoutParams layoutParams = result.getLayoutParams();
                                        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                                        layoutParams.height = result.getHeight();
                                        result.setLayoutParams(layoutParams);
                                        MessageViewHolder.mPreviewImage.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent();
                                                intent.setAction(Intent.ACTION_VIEW);
                                                intent.setDataAndType(Uri.parse(messageList.get(MessageViewHolder.getAdapterPosition()).getMediaUrlList().get(0)), "image/*");
                                                v.getContext().startActivity(intent);
                                            }
                                        });
                                    }else {
                                        MessageViewHolder.mPreviewImage.setForegroundGravity(Gravity.END);
                                        MessageViewHolder.mPreviewImage.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent();
                                                intent.setAction(Intent.ACTION_VIEW);
                                                intent.setDataAndType(Uri.parse(messageList.get(MessageViewHolder.getAdapterPosition()).getMediaUrlList().get(0)), "image/*");
                                                v.getContext().startActivity(intent);
                                            }
                                        });
                                    }
                                }
                            }
                        });
                        MessageViewHolder.mDownloadMedia.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                for (int f = 0; f < messageList.get(MessageViewHolder.getAdapterPosition()).getMediaUrlList().size(); f++) {
                                    final Context c = v.getContext();
                                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(messageList.get(MessageViewHolder.getAdapterPosition()).getMediaUrlList().get(f)));
                                    request.setTitle("✶ Lua ॐ Negra ✶");
                                    request.allowScanningByMediaScanner();
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, messageList.get(MessageViewHolder.getAdapterPosition()).getmessage());
                                    DownloadManager manager = (DownloadManager)c.getSystemService(Context.DOWNLOAD_SERVICE);
                                    manager.enqueue(request);
                                }
                            }
                        });
                    }else {
                        if(MessageViewHolder.mMessage.getText().toString().contains("https://") || MessageViewHolder.mMessage.getText().toString().contains("http://")){
                            MessageViewHolder.mMessagesLink.setLink(extractUrls(MessageViewHolder.mMessage.getText().toString()).get(0), new ViewListener() {
                                @Override
                                public void onSuccess(boolean status) {

                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });
                        }
                    }
                    if (Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().equals(messageList.get(MessageViewHolder.getAdapterPosition()).getListaUsers().get(q).getUid())){
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) MessageViewHolder.mDownloadMedia.getLayoutParams();
                        params.width = 0;
                        params.leftMargin = 0;
                        params.rightMargin = 0;
                        params.height = 0;
                        MessageViewHolder.mDownloadMedia.setLayoutParams(params);
                        MessageViewHolder.mSender.setText(MessageViewHolder.itemView.getResources().getString(R.string.eu) + "  ✶  " + messageList.get(MessageViewHolder.getAdapterPosition()).getHora() + "  -  " + messageList.get(MessageViewHolder.getAdapterPosition()).getData());
                        MessageViewHolder.layoutImagePreview.setGravity(Gravity.END);
                        if(MessageViewHolder.mMessage.getText().toString().contains(MessageViewHolder.itemView.getResources().getString(R.string.entreinacall))){
                            MessageViewHolder.mMessage.setGravity(Gravity.CENTER);
                            MessageViewHolder.mMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        } else {
                            MessageViewHolder.mMessage.setGravity(Gravity.END);
                            MessageViewHolder.mMessage.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                        }
                        if(MessageViewHolder.mMessage.getText().toString().contains(MessageViewHolder.itemView.getResources().getString(R.string.saidacall))){
                            MessageViewHolder.mMessage.setGravity(Gravity.CENTER);
                            MessageViewHolder.mMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        }else {
                            MessageViewHolder.mMessage.setGravity(Gravity.END);
                            MessageViewHolder.mMessage.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                        }
                        MessageViewHolder.mDownloadMedia.setVisibility(View.INVISIBLE);
                        MessageViewHolder.userImage.setVisibility(View.INVISIBLE);
                    }else {

                    }
                    if(MessageViewHolder.mMessage.getText().toString().contains(MessageViewHolder.itemView.getResources().getString(R.string.entreinacall))){
                        MessageViewHolder.mMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    }
                    if(MessageViewHolder.mMessage.getText().toString().contains(MessageViewHolder.itemView.getResources().getString(R.string.saidacall))){
                        MessageViewHolder.mMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    }
                    break;
                }
            }
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }
    }

    private static List<String> extractUrls(String text)
    {
        List<String> containedUrls = new ArrayList<>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?+-=\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);
        while (urlMatcher.find())
        {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }

    @Override
    public void onViewRecycled(@NonNull MessageAdapter.MessageViewHolder holder) {
        if(player != null){
            player.release();
        }
        super.onViewRecycled(holder);
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
        return messageList == null ? 0 : messageList.size();
    }

     public class MessageViewHolder extends EmptyRecyclerView.ViewHolder{
        final LinearLayout mLayout, layoutImagePreview ;
        final Button mDownloadMedia;
        final TextView mMessage;
         final TextView mSender;
        final CircleImageView userImage;
        final ImageView mPreviewImage;
         final CardView mCardView;
         final PlayerView exoplayer;
         final RichLinkViewTwitter mMessagesLink;
        MessageViewHolder(View view){
            super(view);
            exoplayer = view.findViewById(R.id.exoplayer_mensagens);
            mMessagesLink = view.findViewById(R.id.richLinkView_message);
            layoutImagePreview = view.findViewById(R.id.layout_);
            mCardView = view.findViewById(R.id.cardView_message);
            mPreviewImage = view.findViewById(R.id.img_preview_sms);
            userImage = view.findViewById(R.id.user_image_chat);
            mLayout = view.findViewById(R.id.layout_chat_user);
            mMessage = view.findViewById(R.id.message);
            mSender = view.findViewById(R.id.sender);
            mDownloadMedia = view.findViewById(R.id.button_messagemedia);
            mDownloadMedia.setVisibility(View.INVISIBLE);
            mDownloadMedia.setVisibility(View.INVISIBLE);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mCardView.getLayoutParams();
            params.topMargin = 20;
            mCardView.setLayoutParams(params);
            mPreviewImage.setVisibility(View.INVISIBLE);
        }
    }
}