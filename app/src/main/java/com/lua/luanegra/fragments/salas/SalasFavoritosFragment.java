package com.lua.luanegra.fragments.salas;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lua.luanegra.R;
import com.lua.luanegra.adapters.FavoritosSalasAdapter;
import com.lua.luanegra.objects.GroupObject;
import com.lua.luanegra.tools.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class SalasFavoritosFragment extends Fragment {
    private FavoritosSalasAdapter mGroupListAdapter;
    private ArrayList<GroupObject> groupList;
    private View root;
    private final DatabaseReference SalasPublicasRef = FirebaseDatabase.getInstance().getReference().child("salasPublicas");
    private final DatabaseReference SalasPrivadasRef = FirebaseDatabase.getInstance().getReference().child("salasPrivadas");


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

         root = inflater.inflate(R.layout.fragment_salas_favoritos, container, false);

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
                mGroupListAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    private TextView aindanaotens;
    private ArrayList<String> salasEmQueEstouBloqueado;
    private int numerodeutilizadores;
    @Override
    public void onResume() {
        super.onResume();
        aindanaotens =root.findViewById(R.id.txt_naotens_salas_favoritos);
        groupList = new ArrayList<>();
        InitializeRecyclerView();
        SalasPublicasRef.keepSynced(true);
        SalasPrivadasRef.keepSynced(true);
        salasEmQueEstouBloqueado = new ArrayList<>();
        numerodeutilizadores = 0;
        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("favoritos").exists()){
                        final ArrayList<String> idsSalasFavoritos = new ArrayList<>();
                        for(DataSnapshot childfavoritosSnapshot : dataSnapshot.child("favoritos").getChildren()){
                            if(childfavoritosSnapshot.getValue().toString().equals("true")){
                                idsSalasFavoritos.add(childfavoritosSnapshot.getKey());
                            }
                        }
                        SalasPublicasRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                if(dataSnapshot2.exists()){
                                    for(DataSnapshot childsalasPublicasSnapShot : dataSnapshot2.getChildren()){
                                        if(childsalasPublicasSnapShot.child("bloquedUsers").exists()){
                                            for(DataSnapshot bloqueduserspublicassnapshot : childsalasPublicasSnapShot.child("bloquedUsers").getChildren()){
                                                if(bloqueduserspublicassnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                    salasEmQueEstouBloqueado.add(childsalasPublicasSnapShot.getKey());
                                                }
                                            }
                                        }
                                        if(idsSalasFavoritos.contains(childsalasPublicasSnapShot.getKey())){
                                            String groupLogoUri = Objects.requireNonNull(childsalasPublicasSnapShot.child("logo").getValue()).toString();
                                            String groupNome = Objects.requireNonNull(childsalasPublicasSnapShot.child("nome").getValue()).toString();
                                            GroupObject groupObject = new GroupObject(childsalasPublicasSnapShot.getKey(), groupNome, groupLogoUri);
                                            groupObject.setCreator(Objects.requireNonNull(childsalasPublicasSnapShot.child("creator").getValue()).toString());
                                            groupObject.setDescricaoSalaPrivada(Objects.requireNonNull(childsalasPublicasSnapShot.child("descricao").getValue()).toString());
                                            groupObject.setCorApresentacao(Objects.requireNonNull(childsalasPublicasSnapShot.child("coresSala").child("apresentacao").getValue()).toString());
                                            groupObject.setTextColor(Objects.requireNonNull(childsalasPublicasSnapShot.child("coresSala").child("texto").getValue()).toString());
                                            groupObject.setView(SalasFavoritosFragment.this.root);
                                            groupObject.setWhatKindOfRoom("public");
                                            for (DataSnapshot childsnapShot : childsalasPublicasSnapShot.child("users").getChildren()) {
                                                numerodeutilizadores++;
                                            }
                                            String teste = String.valueOf(numerodeutilizadores);
                                            groupObject.setNumeroDeUtilizadores(teste);
                                            if(!salasEmQueEstouBloqueado.contains(groupObject.getUid())){
                                                groupList.add(groupObject);
                                                mGroupListAdapter.notifyDataSetChanged();
                                                aindanaotens.setVisibility(View.GONE);
                                            }
                                            numerodeutilizadores = 0;
                                        }
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        SalasPrivadasRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot3) {
                                if(dataSnapshot3.exists()){
                                    for(DataSnapshot childsalasPrivadasSnapShot : dataSnapshot3.getChildren()){
                                        if(childsalasPrivadasSnapShot.child("bloquedUsers").exists()){
                                            for(DataSnapshot bloqueduserspublicassnapshot : childsalasPrivadasSnapShot.child("bloquedUsers").getChildren()){
                                                if(bloqueduserspublicassnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                    salasEmQueEstouBloqueado.add(childsalasPrivadasSnapShot.getKey());
                                                }
                                            }
                                        }
                                        if(idsSalasFavoritos.contains(childsalasPrivadasSnapShot.getKey())){
                                            String groupLogoUri = Objects.requireNonNull(childsalasPrivadasSnapShot.child("logo").getValue()).toString();
                                            String groupNome = Objects.requireNonNull(childsalasPrivadasSnapShot.child("nome").getValue()).toString();
                                            GroupObject groupObject = new GroupObject(childsalasPrivadasSnapShot.getKey(), groupNome, groupLogoUri);
                                            groupObject.setCreator(Objects.requireNonNull(childsalasPrivadasSnapShot.child("creator").getValue()).toString());
                                            groupObject.setDescricaoSalaPrivada(Objects.requireNonNull(childsalasPrivadasSnapShot.child("descricao").getValue()).toString());
                                            groupObject.setCorApresentacao(Objects.requireNonNull(childsalasPrivadasSnapShot.child("coresSala").child("apresentacao").getValue()).toString());
                                            groupObject.setTextColor(Objects.requireNonNull(childsalasPrivadasSnapShot.child("coresSala").child("texto").getValue()).toString());
                                            groupObject.setView(SalasFavoritosFragment.this.root);
                                            groupObject.setWhatKindOfRoom("private");
                                            for (DataSnapshot childsnapShot : childsalasPrivadasSnapShot.child("users").getChildren()) {
                                                numerodeutilizadores++;
                                            }
                                            String teste = String.valueOf(numerodeutilizadores);
                                            groupObject.setNumeroDeUtilizadores(teste);
                                            SharedPreferences prefs = getActivity().getSharedPreferences(groupObject.getUid(), Context.MODE_PRIVATE);
                                            String chatKey = " ";
                                            chatKey = prefs.getString(groupObject.getUid(), " ");
                                            if(!chatKey.equals(" ")){
                                                groupObject.setChatKey(chatKey);
                                                if(!salasEmQueEstouBloqueado.contains(groupObject.getUid())){
                                                    groupList.add(groupObject);
                                                    mGroupListAdapter.notifyDataSetChanged();
                                                }
                                            }
                                            numerodeutilizadores = 0;
                                        }
                                    }
                                }
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
    }

    private void InitializeRecyclerView() {
        try{
        EmptyRecyclerView mGroupList = root.findViewById(R.id.group_list);
        mGroupList.setNestedScrollingEnabled(false);
        mGroupList.setHasFixedSize(false);
            Display display = Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);
            GridLayoutManager lLayout;
            float density  = getResources().getDisplayMetrics().density;
            float dpWidth  = outMetrics.widthPixels / density;
            int columns = Math.round(dpWidth/200);
            lLayout = new GridLayoutManager(getActivity(),columns);
        mGroupList.setLayoutManager(lLayout);
        mGroupListAdapter = new FavoritosSalasAdapter(groupList);
        mGroupList.setAdapter(mGroupListAdapter);
        }catch (Exception e){
FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());}
    }
}