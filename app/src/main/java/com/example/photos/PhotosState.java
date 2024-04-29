package com.example.photos;

import android.view.View;

public abstract class PhotosState {
    static Albums albums;
    /**
     * This method is overridden by each subclass with a state-specific implementation.
     * It is called when a state is entered.
     */
    static View lastButton;
    abstract void enter();

    /**
     * This method is called when an event is fired, on the current state instance.
     * Each specific instance will override this method as needed.
     */
    abstract PhotosState processEvent();
}
