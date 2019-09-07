package com.lua.luanegra.adapters;

import android.content.Intent;
import android.net.Uri;
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
import com.lua.luanegra.objects.GameObject;
import com.lua.luanegra.tools.EmptyRecyclerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class GameDownloadAdapter extends EmptyRecyclerView.Adapter<GameDownloadAdapter.GameDownloadViewHolder> {
    private final ArrayList<GameObject> gameList;

    public GameDownloadAdapter(ArrayList<GameObject> gameList) {
        this.gameList =  gameList;
    }

    @NonNull
    @Override
    public GameDownloadAdapter.GameDownloadViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gamedownload, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new GameDownloadAdapter.GameDownloadViewHolder(layoutView);
    }



    @Override
    public void onBindViewHolder(@NonNull final GameDownloadAdapter.GameDownloadViewHolder GameDownloadViewHolder, int i) {
        try{
            GameDownloadViewHolder.mGameName.setText(gameList.get(GameDownloadViewHolder.getAdapterPosition()).getGame_name());
            Picasso.get().load(gameList.get(GameDownloadViewHolder.getAdapterPosition()).getGame_Image_Uri()).into(GameDownloadViewHolder.mImageGame);
            GameDownloadViewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(gameList.get(GameDownloadViewHolder.getAdapterPosition()).getGame_link_download()));
                    GameDownloadViewHolder.itemView.getContext().startActivity(intent);
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
        return gameList == null ? 0 : gameList.size();
    }

    class GameDownloadViewHolder extends EmptyRecyclerView.ViewHolder{
        final LinearLayout mLayout ;
        final TextView mGameName;
        final ImageView mImageGame;
        GameDownloadViewHolder(View view){
            super(view);
            mLayout = view.findViewById(R.id.layout_item_gamedownload);
            mImageGame = view.findViewById(R.id.image_game_download);
            mGameName = view.findViewById(R.id.name_game_download);
        }
    }
}
