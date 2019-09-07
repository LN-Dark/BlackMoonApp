package com.lua.luanegra.fragments.salas;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.button.MaterialButton;
import com.lua.luanegra.R;
import com.lua.luanegra.fragments.Adapters.FragmentSalasPagerAdapter;


public class SalasMainFragment extends Fragment {
    private View root;
    private ViewPager pager;
    private FragmentManager fm;
    private FragmentSalasPagerAdapter pagerAdapter;

    public SalasMainFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_salas_main, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pager = getActivity().findViewById(R.id.viewpager_salas);
         fm = getActivity().getSupportFragmentManager();
         pagerAdapter = new FragmentSalasPagerAdapter(fm, 1);
        pager.setAdapter(pagerAdapter);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setLogo(getActivity().getDrawable(R.drawable.luanegra_logo));
        toolbar.setSubtitle(getString(R.string.salas));
        final MaterialButton SalasTematicasPrivadasBTN = getActivity().findViewById(R.id.btn_Tematicas_privadas);
        final MaterialButton SalasTematicasPublicasBTN = getActivity().findViewById(R.id.btn_Tematicas_publicas);
        final MaterialButton SalasFavoritasBTN = getActivity().findViewById(R.id.btn_salas_favoritas);
        SalasTematicasPrivadasBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(1, true);
                SalasTematicasPrivadasBTN.setEnabled(false);
                SalasTematicasPublicasBTN.setEnabled(true);


                SalasFavoritasBTN.setEnabled(true);

            }
        });
        SalasTematicasPublicasBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(2, true);
                SalasTematicasPrivadasBTN.setEnabled(true);
                SalasTematicasPublicasBTN.setEnabled(false);
                SalasFavoritasBTN.setEnabled(true);



            }
        });
        SalasFavoritasBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(0, true);
                SalasTematicasPrivadasBTN.setEnabled(true);
                SalasTematicasPublicasBTN.setEnabled(true);
                SalasFavoritasBTN.setEnabled(false);

            }
        });
        pager.setCurrentItem(0, true);
        SalasTematicasPrivadasBTN.setEnabled(true);
        SalasTematicasPublicasBTN.setEnabled(true);
        SalasFavoritasBTN.setEnabled(false);


        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    pager.setCurrentItem(0, true);
                    SalasTematicasPrivadasBTN.setEnabled(true);
                    SalasTematicasPublicasBTN.setEnabled(true);
                    SalasFavoritasBTN.setEnabled(false);

                }else if(position == 1){
                    pager.setCurrentItem(1, true);
                    SalasTematicasPrivadasBTN.setEnabled(false);
                    SalasTematicasPublicasBTN.setEnabled(true);
                    SalasFavoritasBTN.setEnabled(true);

                }else if(position == 2){
                    pager.setCurrentItem(2, true);
                    SalasTematicasPrivadasBTN.setEnabled(true);
                    SalasTematicasPublicasBTN.setEnabled(false);
                    SalasFavoritasBTN.setEnabled(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
