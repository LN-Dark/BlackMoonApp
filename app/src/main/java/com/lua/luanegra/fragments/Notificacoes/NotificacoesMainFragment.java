package com.lua.luanegra.fragments.Notificacoes;

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
import com.lua.luanegra.fragments.Adapters.FragmentNotificacoesPagerdapter;

public class NotificacoesMainFragment extends Fragment {
    private View root;
    private ViewPager pager;
    private FragmentManager fm;
    private FragmentNotificacoesPagerdapter pagerAdapter;

    public NotificacoesMainFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pager = getActivity().findViewById(R.id.viewpager_notificacoes);
        fm = getActivity().getSupportFragmentManager();
        pagerAdapter = new FragmentNotificacoesPagerdapter(fm, 1);
        pager.setAdapter(pagerAdapter);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setLogo(getActivity().getDrawable(R.drawable.luanegra_logo));
        toolbar.setSubtitle(getString(R.string.notifica_es));
        final MaterialButton PedidosBTN = getActivity().findViewById(R.id.btn_pedidos_notificacoes);
        final MaterialButton NotificacoesBTN = getActivity().findViewById(R.id.btn_notificacoes_notifcacoes);
        PedidosBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(0, true);
                PedidosBTN.setEnabled(false);
                NotificacoesBTN.setEnabled(true);


            }
        });
        NotificacoesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(1, true);
                PedidosBTN.setEnabled(true);
                NotificacoesBTN.setEnabled(false);

            }
        });

        pager.setCurrentItem(1, true);
        PedidosBTN.setEnabled(true);
        NotificacoesBTN.setEnabled(false);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    pager.setCurrentItem(0, true);
                    PedidosBTN.setEnabled(false);
                    NotificacoesBTN.setEnabled(true);

                }else if(position == 1){
                    pager.setCurrentItem(1, true);
                    PedidosBTN.setEnabled(true);
                    NotificacoesBTN.setEnabled(false);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_notificacoes_main, container, false);
    }


}
