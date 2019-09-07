package com.lua.luanegra.fragments.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.lua.luanegra.fragments.Home.ComunidadeFavoritosFragment;
import com.lua.luanegra.fragments.Home.ComunidadeFragment;
import com.lua.luanegra.fragments.Home.MensagensFragment;

public class FragmentHomePagerAdapter extends FragmentStatePagerAdapter {

    public FragmentHomePagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int arg0) {

        switch(arg0){
            case 2:
                return new ComunidadeFavoritosFragment();
            case 0:
                return new ComunidadeFragment();
            case 1:
                return new MensagensFragment();
            default:
                return new MensagensFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}