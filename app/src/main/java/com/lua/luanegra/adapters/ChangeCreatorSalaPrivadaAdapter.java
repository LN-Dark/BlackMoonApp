package com.lua.luanegra.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.lua.luanegra.R;
import com.lua.luanegra.activitys.MainActivity;
import com.lua.luanegra.objects.UserObject;
import com.lua.luanegra.tools.EmptyRecyclerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChangeCreatorSalaPrivadaAdapter  extends EmptyRecyclerView.Adapter<ChangeCreatorSalaPrivadaAdapter.ChangeCreatorViewHolder> {
    private final ArrayList<UserObject> userList;

    public ChangeCreatorSalaPrivadaAdapter(ArrayList<UserObject> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public ChangeCreatorSalaPrivadaAdapter.ChangeCreatorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user_toshare, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new ChangeCreatorSalaPrivadaAdapter.ChangeCreatorViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChangeCreatorSalaPrivadaAdapter.ChangeCreatorViewHolder changeCreatorViewHolder, int i) {

                    changeCreatorViewHolder.cardView.setCardBackgroundColor(changeCreatorViewHolder.itemView.getContext().getColor(R.color.colorChatLighter));
                    changeCreatorViewHolder.mName.setText(userList.get(changeCreatorViewHolder.getAdapterPosition()).getName());
                    Picasso.get().load(userList.get(changeCreatorViewHolder.getAdapterPosition()).getImagemPerfilUri()).into(changeCreatorViewHolder.imagemPerfil);
                    changeCreatorViewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(v.getContext());
                            LinearLayout layout = new LinearLayout(v.getContext());
                            layout.setOrientation(LinearLayout.VERTICAL);
                            builder3.setIcon(v.getContext().getDrawable(R.drawable.luanegra_logo));
                            builder3.setTitle(v.getContext().getString(R.string.trasnferirpossedesala));
                            final CircleImageView icon = new CircleImageView(v.getContext());
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
                            layoutParams.gravity = Gravity.CENTER;
                            icon.setLayoutParams(layoutParams);
                            layout.addView(icon);
                            Picasso.get().load(userList.get(changeCreatorViewHolder.getAdapterPosition()).getImagemPerfilUri()).into(icon);
                            final TextView NomeSala2 = new TextView(v.getContext());
                            NomeSala2.setText(userList.get(changeCreatorViewHolder.getAdapterPosition()).getName());

                            NomeSala2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            NomeSala2.setTextSize(15);
                            layout.addView(NomeSala2);
                            final TextView espaco77 = new TextView(v.getContext());
                            espaco77.setText("\n");
                            layout.addView(espaco77);
                            final TextView espaco4 = new TextView(v.getContext());
                            espaco4.setText(" ");
                            layout.addView(espaco4);
                            final TextView NomeSala = new TextView(v.getContext());
                            NomeSala.setText(v.getContext().getString(R.string.aocarregaremtransferirnaoserapossivelvoltaraatraz));
                            NomeSala.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            NomeSala.setTextSize(15);
                            layout.addView(NomeSala);
                            final TextView espaco7 = new TextView(v.getContext());
                            espaco7.setText("\n");
                            layout.addView(espaco7);
                            builder3.setCancelable(false);
                            builder3.setView(layout);
                            AlertDialog alert = null;
                            builder3.setPositiveButton(v.getContext().getString(R.string.transferir), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(userList.get(changeCreatorViewHolder.getAdapterPosition()).getSalaPrivadaID()).child("creator").setValue(userList.get(changeCreatorViewHolder.getAdapterPosition()).getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            FirebaseDatabase.getInstance().getReference().child("salasPrivadas").child(userList.get(changeCreatorViewHolder.getAdapterPosition()).getSalaPrivadaID()).child("admins").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Intent mainIntent = new Intent(v.getContext(), MainActivity.class);
                                                    v.getContext().startActivity(mainIntent);
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                            builder3.setNeutralButton(v.getContext().getResources().getString(R.string.fecharapp), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            alert = builder3.create();
                            alert.show();
                        }
        });

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

    class ChangeCreatorViewHolder extends EmptyRecyclerView.ViewHolder {
        final TextView mName;
        final LinearLayout mLayout;
        final CardView cardView;
        final CircleImageView imagemPerfil;
        ChangeCreatorViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.usertoshare);
            imagemPerfil = view.findViewById(R.id.image_user_to_sahre);
            mName = view.findViewById(R.id.name_user_to_share);
            mLayout = view.findViewById(R.id.layout_item_usertoshare);
        }
    }

}