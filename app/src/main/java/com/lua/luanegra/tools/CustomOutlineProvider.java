package com.lua.luanegra.tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.lua.luanegra.R;

public class CustomOutlineProvider extends LinearLayout {
    public CustomOutlineProvider(Context context) {
        super(context);
        initBackground();
    }

    public CustomOutlineProvider(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initBackground();
    }

    public CustomOutlineProvider(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBackground();
    }

    private void initBackground() {
        setBackground(ViewUtils.generateBackgroundWithShadow(this, R.color.colorPrimary,
                R.dimen.curvatura,R.color.md_black_1000,R.dimen.elevation, Gravity.BOTTOM));
    }
}