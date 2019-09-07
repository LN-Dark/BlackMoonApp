package com.lua.luanegra.fragments.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.lua.luanegra.fragments.Notificacoes.NotificacoesFragment;
import com.lua.luanegra.fragments.Notificacoes.PedidosFragment;

public class FragmentNotificacoesPagerdapter extends FragmentStatePagerAdapter {

    public FragmentNotificacoesPagerdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int arg0) {

        switch(arg0){
            case 0:
                return new PedidosFragment();
            case 1:
                return new NotificacoesFragment();
            default:
                return new NotificacoesFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}