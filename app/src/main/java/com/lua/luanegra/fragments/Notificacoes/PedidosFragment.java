package com.lua.luanegra.fragments.Notificacoes;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Display;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lua.luanegra.R;
import com.lua.luanegra.adapters.PedidosAdapter;
import com.lua.luanegra.objects.UserObject;

import java.util.ArrayList;
import java.util.Objects;


public class PedidosFragment extends Fragment {
    private PedidosAdapter mUserListAdapter;
    private View root;
    private TextView aindanaotens;
    private ArrayList<UserObject> fullUserList;
    private ArrayList<String> favoriteUserList;
    private DatabaseReference fullUserListRef;
    private ChildEventListener childEventListenerUserList;

    public PedidosFragment() {

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
                mUserListAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    private void GetAllUsers(){
        try {

            favoriteUserList = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot5) {
                    if(dataSnapshot5.exists()){
                        if(dataSnapshot5.child("pedidosComunidade").exists()){
                            for(DataSnapshot childfavoritosUserSnapShot : dataSnapshot5.child("pedidosComunidade").getChildren()){
                                if(childfavoritosUserSnapShot.getValue().toString().equals("true")){
                                    favoriteUserList.add((childfavoritosUserSnapShot.getKey()));
                                }
                            }
                            childEventListenerUserList = fullUserListRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    if(dataSnapshot.exists()){
                                        if(!Objects.requireNonNull(dataSnapshot.getKey()).equals("chat")){
                                            UserObject newUser = new UserObject(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString(), dataSnapshot.getKey());
                                            newUser.setNotificationKey(Objects.requireNonNull(dataSnapshot.child("notificationKey").getValue()).toString());
                                            newUser.setImagemPerfilUri(Objects.requireNonNull(dataSnapshot.child("profile_image").getValue()).toString());
                                            newUser.setRegistoData(Objects.requireNonNull(dataSnapshot.child("registerDate").getValue()).toString());
                                            newUser.setRegistoHora(Objects.requireNonNull(dataSnapshot.child("registerTime").getValue()).toString());
                                            newUser.setPrimeiroNick(Objects.requireNonNull(dataSnapshot.child("registerNick").getValue()).toString());
                                            newUser.setBio(Objects.requireNonNull(dataSnapshot.child("bio").getValue()).toString());
                                            newUser.setPatrono(Objects.requireNonNull(dataSnapshot.child("patrono").getValue()).toString());
                                            if(dataSnapshot.child("online").exists()){
                                                newUser.setIsonline(Objects.requireNonNull(dataSnapshot.child("online").getValue()).toString());
                                                newUser.setLastOnline(getResources().getString(R.string.ultimavezonline) +"\n" + Objects.requireNonNull(dataSnapshot.child("onlineDate").getValue()).toString() + "  *  " + Objects.requireNonNull(dataSnapshot.child("onlineTime").getValue()).toString());
                                            }else {
                                                newUser.setIsonline("true");
                                                newUser.setLastOnline(getString(R.string.desconhecico));
                                            }
                                            if(dataSnapshot.child("opcoes").exists()){
                                                if(dataSnapshot.child("opcoes").child("sociavel").exists()){
                                                    newUser.setSociavel(dataSnapshot.child("opcoes").child("sociavel").getValue().toString());
                                                }else {
                                                    newUser.setSociavel("true");
                                                }
                                            }else {
                                                newUser.setSociavel("true");
                                            }
                                            if(favoriteUserList.contains(newUser.getUid())){
                                                if(newUser.getUid().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                                    fullUserList.add(0, newUser);
                                                }else if(!newUser.getName().equals("UserUnknown")) {
                                                    fullUserList.add(newUser);
                                                }
                                                mUserListAdapter.notifyDataSetChanged();
                                                aindanaotens = root.findViewById(R.id.txt_naoexistem_publicacoes);
                                                aindanaotens.setVisibility(View.GONE);
                                                fab.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    if(dataSnapshot.exists()){
                                        if(!Objects.requireNonNull(dataSnapshot.getKey()).equals("chat")){
                                            UserObject newUser = new UserObject(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString(), dataSnapshot.getKey());
                                            newUser.setNotificationKey(Objects.requireNonNull(dataSnapshot.child("notificationKey").getValue()).toString());
                                            newUser.setImagemPerfilUri(Objects.requireNonNull(dataSnapshot.child("profile_image").getValue()).toString());
                                            newUser.setIsonline(Objects.requireNonNull(dataSnapshot.child("online").getValue()).toString());
                                            if(dataSnapshot.child("opcoes").exists()){
                                                if(dataSnapshot.child("opcoes").child("sociavel").exists()){
                                                    newUser.setSociavel(dataSnapshot.child("opcoes").child("sociavel").getValue().toString());
                                                }else {
                                                    newUser.setSociavel("true");
                                                }
                                            }else {
                                                newUser.setSociavel("true");
                                            }
                                            newUser.setBio(Objects.requireNonNull(dataSnapshot.child("bio").getValue()).toString());
                                            newUser.setLastOnline(getResources().getString(R.string.ultimavezonline) +"\n" + Objects.requireNonNull(dataSnapshot.child("onlineDate").getValue()).toString() + "  *  " + Objects.requireNonNull(dataSnapshot.child("onlineTime").getValue()).toString());
                                            for(int i = 0; i < fullUserList.size(); i++){
                                                if(fullUserList.get(i).getUid().equals(newUser.getUid())){

                                                    fullUserList.get(i).setLastOnline(newUser.getLastOnline());
                                                    fullUserList.get(i).setIsonline(newUser.getIsonline());
                                                    fullUserList.get(i).setImagemPerfilUri(newUser.getImagemPerfilUri());
                                                    fullUserList.get(i).setNotificationKey(newUser.getNotificationKey());
                                                    fullUserList.get(i).setName(newUser.getName());
                                                    fullUserList.get(i).setBio(newUser.getBio());
                                                    fullUserList.get(i).setSociavel(newUser.getSociavel());
                                                    mUserListAdapter.notifyItemChanged(i);

                                                    break;
                                                }
                                            }

                                        }
                                    }
                                }

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(childEventListenerUserList != null){
            fullUserListRef.removeEventListener(childEventListenerUserList);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(childEventListenerUserList != null){
            fullUserListRef.removeEventListener(childEventListenerUserList);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(childEventListenerUserList != null){
            fullUserListRef.removeEventListener(childEventListenerUserList);
        }
    }

    @Override
    public void onResume() {
        super.onResume();


        fullUserList = new ArrayList<>();
        fullUserListRef = FirebaseDatabase.getInstance().getReference().child("user");
        fullUserListRef.keepSynced(true);
        InitializeRecyclerView();
        GetAllUsers();
    }

    FloatingActionButton fab;
    private void InitializeRecyclerView() {
        try{
            RecyclerView mUserList = root.findViewById(R.id.pedidos_list);
            mUserList.setNestedScrollingEnabled(true);
            mUserList.setHasFixedSize(false);
            Display display = Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);
            GridLayoutManager lLayout;
            aindanaotens = root.findViewById(R.id.txt_naoexistem_publicacoes);
            float density  = getResources().getDisplayMetrics().density;
            float dpWidth  = outMetrics.widthPixels / density;
            int columns = Math.round(dpWidth/160);
            lLayout = new GridLayoutManager(getActivity(),columns);
            mUserList.setLayoutManager(lLayout);
            mUserListAdapter = new PedidosAdapter(fullUserList);
            mUserListAdapter.setHasStableIds(true);
            mUserList.setAdapter(mUserListAdapter);
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
                            FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("pedidosComunidade").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Snackbar.make(root, getString(R.string.todasasnotificacoesapagadas), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    fullUserList.clear();
                                    mUserListAdapter.notifyDataSetChanged();
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
        root = inflater.inflate(R.layout.fragment_pedidos, container, false);

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}
