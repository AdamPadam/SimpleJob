package com.codedevstudio.orders.utils;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.codedevstudio.orders.R;
import com.codedevstudio.orders.fragments.DisputOrderFragment;

/**
 * Created by FOX on 20.04.18.
 */

public class CustomSwipeAdaptor extends PagerAdapter{
    private int [] imagesSample = {R.drawable.sample1,R.drawable.sample2};
    private Context ctx;
    private LayoutInflater layoutInflater;

    public CustomSwipeAdaptor(DisputOrderFragment ctx){
       // this.ctx = ctx;
    }
    @Override
    public int getCount() {
        return imagesSample.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==(LinearLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.swipe_layout,container,false);
        ImageView imageView = (ImageView)item_view.findViewById(R.id.image_for_swiping);
        imageView.setImageResource(imagesSample[position]);
        container.addView(item_view);
        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }
}
