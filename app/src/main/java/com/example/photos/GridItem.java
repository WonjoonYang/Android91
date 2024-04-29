package com.example.photos;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
public class GridItem<T> {
    private T image;
    private String name;

    public GridItem(T image, String name) {
        this.image = image;
        this.name = name;
    }

    public T getImage() {
        return image;
    }

    public void setImage(T image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}