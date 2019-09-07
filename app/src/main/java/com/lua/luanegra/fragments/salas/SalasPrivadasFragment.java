package com.lua.luanegra.fragments.salas;


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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lua.luanegra.R;
import com.lua.luanegra.adapters.SalaPrivadaAdapter;
import com.lua.luanegra.objects.GroupObject;
import com.lua.luanegra.tools.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.Objects;

public class SalasPrivadasFragment extends Fragment {
    private SalaPrivadaAdapter mSalasPrivadasListAdapter;
    private ArrayList<GroupObject> salasPrivadasList;
    private int numerodeutilizadores;
    private final DatabaseReference salasPrivadasRef = FirebaseDatabase.getInstance().getReference().child("salasPrivadas");


    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_salas_privadas, container, false);
        return root;
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
                mSalasPrivadasListAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }


    @Override
    public void onStop() {
        super.onStop();
        if(mSalasPrivadasChildListener != null){
            salasPrivadasRef.removeEventListener(mSalasPrivadasChildListener);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mSalasPrivadasChildListener != null){
            salasPrivadasRef.removeEventListener(mSalasPrivadasChildListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSalasPrivadasChildListener != null){
            salasPrivadasRef.removeEventListener(mSalasPrivadasChildListener);
        }
    }

    private TextView aindanaotens;
    private ChildEventListener mSalasPrivadasChildListener;
    @Override
    public void onResume() {
        super.onResume();
        aindanaotens = root.findViewById(R.id.txt_naotens_salas_privadas);
        numerodeutilizadores = 0;
        salasPrivadasList = new ArrayList<>();
        InitializeRecyclerView();
        salasPrivadasRef.keepSynced(true);
        mSalasPrivadasChildListener = salasPrivadasRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    String groupLogoUri = Objects.requireNonNull(dataSnapshot.child("logo").getValue()).toString();
                    String groupNome = Objects.requireNonNull(dataSnapshot.child("nome").getValue()).toString();
                    GroupObject groupObject = new GroupObject(dataSnapshot.getKey(), groupNome, groupLogoUri);
                    groupObject.setCreator(Objects.requireNonNull(dataSnapshot.child("creator").getValue()).toString());
                    groupObject.setDescricaoSalaPrivada(Objects.requireNonNull(dataSnapshot.child("descricao").getValue()).toString());
                    groupObject.setCorApresentacao(Objects.requireNonNull(dataSnapshot.child("coresSala").child("apresentacao").getValue()).toString());
                    groupObject.setTextColor(Objects.requireNonNull(dataSnapshot.child("coresSala").child("texto").getValue()).toString());
                    groupObject.setView(SalasPrivadasFragment.this.root);
                    for(DataSnapshot childsnapShot : dataSnapshot.child("users").getChildren()){
                        numerodeutilizadores++;
                    }
                    String teste = String.valueOf(numerodeutilizadores);
                    groupObject.setNumeroDeUtilizadores(teste);
                    salasPrivadasList.add(groupObject);
                    numerodeutilizadores = 0;
                    mSalasPrivadasListAdapter.notifyDataSetChanged();
                    aindanaotens.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

    public static SalasPrivadasFragment newInstance() {
        SalasPrivadasFragment fragment = new SalasPrivadasFragment();
        return fragment;
    }

    private void InitializeRecyclerView() {
        try{
            EmptyRecyclerView mGroupList = root.findViewById(R.id.salasPrivadas_recycler);
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
            mSalasPrivadasListAdapter = new SalaPrivadaAdapter(salasPrivadasList);
            mGroupList.setAdapter(mSalasPrivadasListAdapter);
            mGroupList.setItemViewCacheSize(0);
        }catch (Exception e){
            FirebaseDatabase.getInstance().getReference().child("logError").child(Objects.requireNonNull(FirebaseDatabase.getInstance().getReference().child("logError").push().getKey())).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue("✶ " + this.getClass().getName() + " ✶\n\n " + e.getMessage() + "\n" + "\n" + e.getLocalizedMessage() + "\n" + e.toString() + "\n" + e.fillInStackTrace().toString());
        }
    }


}
