package com.company.tokoonline.models;

public class SlideItem {
    public String image_url,slider_link;
    public int id;
    public SlideItem(int id, String image_url, String slider_link) {
        this.id = id;
        this.image_url = image_url;
        this.slider_link = slider_link;
    }
}