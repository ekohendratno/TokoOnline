package com.company.tokoonline.adapters;

import android.content.Context;
import android.text.TextUtils;

import com.company.tokoonline.models.SlideItem;

import java.util.List;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class SliderImageAdapter extends SliderAdapter {

    final List<SlideItem> sliders;

    public SliderImageAdapter(Context context, List<SlideItem> sliders) {
        this.sliders = sliders;
    }

    @Override
    public int getItemCount() {
        return sliders.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {
        SlideItem slideItem = sliders.get(position);

        if(!TextUtils.isEmpty(slideItem.image_url))
            viewHolder.bindImageSlide(slideItem.image_url);

    }
}
