package com.lua.luanegra.fragments.Home;

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
import com.lua.luanegra.fragments.Adapters.FragmentHomePagerAdapter;


public class HomeFragment extends Fragment {
    private View root;
    private ViewPager pager;
    private FragmentManager fm;
    private FragmentHomePagerAdapter pagerAdapter;
    public HomeFragment() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pager = getActivity().findViewById(R.id.viewpager_home);
        fm = getActivity().getSupportFragmentManager();
        pagerAdapter = new FragmentHomePagerAdapter(fm, 1);
        pager.setAdapter(pagerAdapter);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setLogo(getActivity().getDrawable(R.drawable.luanegra_logo));
        toolbar.setSubtitle(getString(R.string.home));
        final MaterialButton ComunidadeBTN = getActivity().findViewById(R.id.btn_home_comunidade);
        final MaterialButton ComunidadeFavoritosBTN = getActivity().findViewById(R.id.btn_home_favoritas);
        final MaterialButton MensagensBTN = getActivity().findViewById(R.id.btn_home_mensagens);
        ComunidadeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(0, true);
                ComunidadeBTN.setEnabled(false);
                ComunidadeFavoritosBTN.setEnabled(true);

                MensagensBTN.setEnabled(true);

            }
        });
        ComunidadeFavoritosBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(2, true);
                ComunidadeBTN.setEnabled(true);
                ComunidadeFavoritosBTN.setEnabled(false);
                MensagensBTN.setEnabled(true);

            }
        });
        MensagensBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(1, true);
                ComunidadeBTN.setEnabled(true);
                ComunidadeFavoritosBTN.setEnabled(true);
                MensagensBTN.setEnabled(false);

            }
        });
        pager.setCurrentItem(1, true);
        ComunidadeBTN.setEnabled(true);
        ComunidadeFavoritosBTN.setEnabled(true);
        MensagensBTN.setEnabled(false);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    pager.setCurrentItem(0, true);
                    ComunidadeBTN.setEnabled(false);
                    ComunidadeFavoritosBTN.setEnabled(true);

                    MensagensBTN.setEnabled(true);

                }else if(position == 1){
                    pager.setCurrentItem(1, true);
                    ComunidadeBTN.setEnabled(true);
                    ComunidadeFavoritosBTN.setEnabled(true);
                    MensagensBTN.setEnabled(false);

                }else if(position == 2){
                    pager.setCurrentItem(2, true);
                    ComunidadeBTN.setEnabled(true);
                    ComunidadeFavoritosBTN.setEnabled(false);
                    MensagensBTN.setEnabled(true);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}
