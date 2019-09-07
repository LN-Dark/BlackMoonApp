package com.lua.luanegra.adapters;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.lua.luanegra.R;
import com.lua.luanegra.objects.NotificacaoObject;
import com.lua.luanegra.tools.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsListViewHolder>  implements Filterable {
    private ArrayList<NotificacaoObject> notificationList;


    public NotificationsAdapter(ArrayList<NotificacaoObject> notificationList) {
        fullnotificationList = notificationList;
        this.notificationList = notificationList;
    }

    private static ArrayList<NotificacaoObject> fullnotificationList;
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    notificationList = fullnotificationList;
                } else {
                    ArrayList<NotificacaoObject> filteredList = new ArrayList<>();
                    for (NotificacaoObject row : fullnotificationList) {

                        if (row.getTitulo().toLowerCase().contains(charString.toLowerCase()) || row.getTitulo().contains(charSequence)) {
                            filteredList.add(row);
                        }
                        if (row.getMensagem().toLowerCase().contains(charString.toLowerCase()) || row.getMensagem().contains(charSequence)) {
                            if(!filteredList.contains(row)){
                                filteredList.add(row);
                            }
                        }
                    }
                    notificationList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = notificationList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                notificationList = (ArrayList<NotificacaoObject>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public NotificationsAdapter.NotificationsListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notificacao, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new NotificationsAdapter.NotificationsListViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationsAdapter.NotificationsListViewHolder notificationListViewHolder, int i) {
        try{
            notificationListViewHolder.mName.setText(notificationList.get(notificationListViewHolder.getAdapterPosition()).getTitulo());
            notificationListViewHolder.mmensagem.setText(notificationList.get(notificationListViewHolder.getAdapterPosition()).getMensagem());
            notificationListViewHolder.mdata.setText(notificationList.get(notificationListViewHolder.getAdapterPosition()).getData());
            notificationListViewHolder.mhora.setText(notificationList.get(notificationListViewHolder.getAdapterPosition()).getHora() + " - ");
            notificationListViewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(v.getContext());
                    LinearLayout layout = new LinearLayout(v.getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);

                    builder3.setIcon(v.getContext().getDrawable(R.drawable.luanegra_logo));
                    builder3.setTitle(v.getContext().getString(R.string.donativo));
                    final TextView textoshare = new TextView(v.getContext());
                    textoshare.setText(v.getContext().getString(R.string.queresapagarestanotificacao));
                    textoshare.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    textoshare.setTextSize(15);
                    layout.addView(textoshare);
                    final TextView espaco4 = new TextView(v.getContext());
                    espaco4.setText(" ");
                    layout.addView(espaco4);
                    builder3.setCancelable(false);
                    builder3.setView(layout);
                    AlertDialog alert;
                    builder3.setPositiveButton(v.getContext().getString(R.string.apagar), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").child(notificationList.get(notificationListViewHolder.getAdapterPosition()).getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Snackbar.make(v, v.getContext().getString(R.string.apagado), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    notificationList.remove(notificationListViewHolder.getAdapterPosition());
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    builder3.setNeutralButton(v.getContext().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alert = builder3.create();
                    alert.show();
                }
            });
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(" " + this.getClass().getName() + " \n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
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
        return notificationList == null ? 0 : notificationList.size();
    }


    class NotificationsListViewHolder extends EmptyRecyclerView.ViewHolder{
        final TextView mName, mdata, mhora, mmensagem;
        final LinearLayout mLayout ;
        NotificationsListViewHolder(View view){
            super(view);
            mName = view.findViewById(R.id.titulo_notification);
            mLayout = view.findViewById(R.id.layout_notification);
            mdata = view.findViewById(R.id.data_notification);
            mhora = view.findViewById(R.id.hora_notification);
            mmensagem = view.findViewById(R.id.message_notification);
        }
    }
}
