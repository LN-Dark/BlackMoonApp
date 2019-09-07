package com.lua.luanegra.fragments.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.lua.luanegra.fragments.salas.SalasFavoritosFragment;
import com.lua.luanegra.fragments.salas.SalasPrivadasFragment;
import com.lua.luanegra.fragments.salas.SalasPublicasFragment;

public class FragmentSalasPagerAdapter extends FragmentStatePagerAdapter {

    public FragmentSalasPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int arg0) {

        switch(arg0){
            case 2:
                return new SalasPublicasFragment();
            case 0:
                return new SalasFavoritosFragment();
            case 1:
                return new SalasPrivadasFragment();
            default:
                return new SalasPrivadasFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}