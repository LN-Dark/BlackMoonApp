package com.lua.luanegra.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lua.luanegra.R;

import java.util.ArrayList;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {
    private final ArrayList<String> mediaList;

    public MediaAdapter(ArrayList<String> mediaList){
        this.mediaList = mediaList;

    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_media, viewGroup, false);
        return new MediaViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder mediaViewHolder, int i) {


    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }


    public class MediaViewHolder extends RecyclerView.ViewHolder {
        final ImageView mMedia;
        MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            mMedia = itemView.findViewById(R.id.media);
        }
    }
}
