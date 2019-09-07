package com.lua.luanegra.fragments.Notificacoes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lua.luanegra.R;
import com.lua.luanegra.adapters.NotificationsAdapter;
import com.lua.luanegra.objects.NotificacaoObject;
import com.lua.luanegra.tools.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class NotificacoesFragment extends Fragment {
    private NotificationsAdapter mNotificationsListAdapter;
    private View root;
    private TextView aindanaotens;
    private ArrayList<NotificacaoObject> fullNotificationsList;
    private DatabaseReference fullNotificationsListRef;


    public NotificacoesFragment() {
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        menu.findItem(R.id.action_search).setVisible(true);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_ACTION_SEARCH | EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        searchView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mNotificationsListAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }
    FloatingActionButton fab;
    private void GetAllUsers(){
        try {
            fullNotificationsListRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        if(dataSnapshot.child("notificacoesRecebidas").exists()){
                            for(DataSnapshot childNotificacoesSnapShot : dataSnapshot.child("notificacoesRecebidas").getChildren()){
                                NotificacaoObject newUser = new NotificacaoObject(childNotificacoesSnapShot.child("titulo").getValue().toString(), childNotificacoesSnapShot.child("data").getValue().toString(), childNotificacoesSnapShot.child("hora").getValue().toString(), childNotificacoesSnapShot.child("mensagem").getValue().toString(),childNotificacoesSnapShot.getKey());
                                fullNotificationsList.add(newUser);
                                aindanaotens = root.findViewById(R.id.txt_naoexistem_publicacoes);
                                aindanaotens.setVisibility(View.GONE);
                                fab.setVisibility(View.VISIBLE);
                            }
                            Collections.reverse(fullNotificationsList);
                            mNotificationsListAdapter.notifyDataSetChanged();

                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            fab = root.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog.Builder builder3 = new MaterialAlertDialogBuilder(getActivity());
                    LinearLayout layout = new LinearLayout(getActivity());
                    layout.setOrientation(LinearLayout.VERTICAL);

                    builder3.setIcon(getActivity().getDrawable(R.drawable.luanegra_logo));
                    builder3.setTitle(getString(R.string.apagar));
                    final TextView textoshare = new TextView(getActivity());
                    textoshare.setText(getString(R.string.tensacertezaquepretendesapagarnotificacoes));
                    textoshare.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    textoshare.setTextSize(15);
                    layout.addView(textoshare);
                    final TextView espaco4 = new TextView(getActivity());
                    espaco4.setText(" ");
                    layout.addView(espaco4);
                    builder3.setCancelable(false);
                    builder3.setView(layout);
                    AlertDialog alert;
                    builder3.setPositiveButton(getString(R.string.apagar), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notificacoesRecebidas").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Snackbar.make(root, getString(R.string.todasasnotificacoesapagadas), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    fullNotificationsList.clear();
                                    mNotificationsListAdapter.notifyDataSetChanged();
                                    aindanaotens = root.findViewById(R.id.txt_naoexistem_publicacoes);
                                    aindanaotens.setVisibility(View.VISIBLE);
                                    fab.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    });
                    builder3.setNeutralButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alert = builder3.create();
                    alert.show();
                }
            });
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        fullNotificationsList = new ArrayList<>();
        fullNotificationsListRef = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        fullNotificationsListRef.keepSynced(true);
        InitializeRecyclerView();
        GetAllUsers();
    }


    private void InitializeRecyclerView() {
        try{
            EmptyRecyclerView mNotificationsLinst = root.findViewById(R.id.notifications_list);
            mNotificationsLinst.setNestedScrollingEnabled(false);
            mNotificationsLinst.setHasFixedSize(false);
            EmptyRecyclerView.LayoutManager mUserListLayoutManager = new LinearLayoutManager(getActivity(), EmptyRecyclerView.VERTICAL, false);
            mNotificationsLinst.setLayoutManager(mUserListLayoutManager);
            mNotificationsListAdapter = new NotificationsAdapter(fullNotificationsList);
            mNotificationsLinst.setAdapter(mNotificationsListAdapter);
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_notificacoes, container, false);

        return root;
    }

}

