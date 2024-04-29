package com.example.photos;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class ShowPhoto extends AppCompatActivity {

    private ViewPager viewPager;
    private ArrayList<Photo> photoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_photo);
        Bundle bundle = getIntent().getExtras();
        Photo first = bundle.getSerializable("photo",Photo.class);
        int firstNum = bundle.getInt("number");
        photoList = Photos.getCurrAlbum().getPhotos();
        viewPager = findViewById(R.id.viewPager);

        // Assuming photoList is populated somewhere in your code
        // Set up ViewPager with adapter
        PhotoPagerAdapter adapter = new PhotoPagerAdapter(this, photoList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(firstNum);
    }
}