package com.company.tokoonline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.company.tokoonline.adapters.SliderAdapter;
import com.company.tokoonline.adapters.TerkiniAdapter;
import com.company.tokoonline.adapters.KategoriAdapter;
import com.company.tokoonline.adapters.RekomendasiAdapter;
import com.company.tokoonline.models.SliderItem;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    SliderView sliderView;
    private SliderAdapter adapter;
    private RecyclerView recyleviewTerkini;
    private RecyclerView recyleviewKategori;
    private RecyclerView recyleviewRekomendasi;
    private TerkiniAdapter terkiniAdapter;
    private KategoriAdapter kategoriAdapter;
    private RekomendasiAdapter rekomendasiAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sliderView = findViewById(R.id.imageSlider);


        adapter = new SliderAdapter(this);
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();


        sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                Log.i("GGG", "onIndicatorClicked: " + sliderView.getCurrentPagePosition());
            }
        });


        renewItems();









        recyleviewTerkini = findViewById(R.id.recyle_view0);
        recyleviewKategori = findViewById(R.id.recyle_view1);
        recyleviewRekomendasi = findViewById(R.id.recyle_view2);

        recyleviewTerkini.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) );
        terkiniAdapter = new TerkiniAdapter(this);
        recyleviewTerkini.setAdapter(terkiniAdapter);

        recyleviewKategori.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) );
        kategoriAdapter = new KategoriAdapter(this);
        recyleviewKategori.setAdapter(kategoriAdapter);

        recyleviewRekomendasi.setLayoutManager( new GridLayoutManager(this, 2) );
        rekomendasiAdapter = new RekomendasiAdapter(this);
        recyleviewRekomendasi.setAdapter(rekomendasiAdapter);

    }

    public void renewItems() {
        List<SliderItem> sliderItemList = new ArrayList<>();
        //dummy data
        for (int i = 0; i < 5; i++) {
            SliderItem sliderItem = new SliderItem();
            sliderItem.setDescription("Slider Item " + i);
            if (i % 2 == 0) {
                sliderItem.setImageUrl("https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
            } else {
                sliderItem.setImageUrl("https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260");
            }
            sliderItemList.add(sliderItem);
        }
        adapter.renewItems(sliderItemList);
    }

    public void removeLastItem(View view) {
        if (adapter.getCount() - 1 >= 0)
            adapter.deleteItem(adapter.getCount() - 1);
    }

    public void addNewItem(View view) {
        SliderItem sliderItem = new SliderItem();
        sliderItem.setDescription("Slider Item Added Manually");
        sliderItem.setImageUrl("https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
        adapter.addItem(sliderItem);
    }


}