package com.thoms.silonaorangtua;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ViewBantuanDalam extends PagerAdapter {

    private Context context;
    private LayoutInflater inflater;
    private Integer [] list = {
            R.drawable.img1,
            R.drawable.img2,
            R.drawable.img3

    };

    public ViewBantuanDalam(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {

        return list.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_bantuan, null);
        ImageView imageView = (ImageView)view.findViewById(R.id.imageView4);
        imageView.setImageResource(list[position]);

        ViewPager vp = (ViewPager) container;
        vp.addView(view,0 );
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}
