package com.codedevstudio.orders.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.codedevstudio.orders.R;

/**
 * RecyclerView с добавлеными разделителями
 */

public class StrippedRecyclerView extends RecyclerView {
    private int SeparatorColor;

    public Float getThickness() {
        return Thickness;
    }

    private Float Thickness;


    private void AddDivider(AttributeSet attrs){
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.StrippedRecyclerView, 0, 0);
        try {
            SeparatorColor = ta.getColor(R.styleable.StrippedRecyclerView_Color, Color.BLACK);
            Thickness = ta.getFloat(R.styleable.StrippedRecyclerView_Thickness, 0f);
            SeparatorDecoration decoration = new SeparatorDecoration(SeparatorColor, Thickness,0f,0f);
            this.addItemDecoration(decoration);
        } finally {
            ta.recycle();
        }
    }


    public StrippedRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        AddDivider(attrs);
    }

    public StrippedRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        AddDivider(attrs);
    }

    public StrippedRecyclerView(Context context) {
        super(context);
    }


    public int getSeparatorColor() {
        return SeparatorColor;
    }
}
