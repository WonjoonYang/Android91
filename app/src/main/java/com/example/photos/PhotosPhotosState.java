package com.example.photos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class PhotosPhotosState extends PhotosState{
    private static PhotosPhotosState instance = null;

    private PhotosPhotosState() {

    }
    public static PhotosPhotosState getInstance() {
        if (instance == null) {
            instance = new PhotosPhotosState();
        }
        return instance;
    }

    @Override
    void enter() {
        albums.mSearchFab.shrink();
        albums.mSearchFab.hide();
        albums.mCombineSearch.hide();
        albums.mSearchPerson.hide();
        albums.mSearchLocation.hide();
        albums.mCombineSearchText.setVisibility(View.GONE);
        albums.mSearchPersonText.setVisibility(View.GONE);
        albums.mSearchLocationText.setVisibility(View.GONE);
        albums.mCombineSearchText.setVisibility(View.GONE);
        albums.isAllFabsVisible = false;

        albums.backButton.setVisibility(View.VISIBLE);

        albums.toolbarTitle.setText(albums.album.getName());
        albums.mButton1.setText("Add Photo");

        albums.photoAdapter = new PhotoAdapter(albums, albums.album.getPhotos());
        albums.recyclerView.setAdapter(albums.photoAdapter);

        albums.photoAdapter.notifyDataSetChanged();
    }
    @Override
    PhotosState processEvent() {
        if (lastButton.equals(albums.backButton)){
            albums.albumState.enter();
            albums.currentState = albums.albumState;
            return albums.albumState;
        }
        if (lastButton.equals(albums.mButton1)){
            albums.pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        }

        if (lastButton.equals(albums.mButton2)){
            albums.photoAdapter.setSelectable(true);
            albums.mButton1.setVisibility(View.GONE);
            albums.mButton2.setVisibility(View.GONE);
            albums.mButton3.setVisibility(View.VISIBLE);
            albums.mButton4.setVisibility(View.VISIBLE);
            albums.mButton5.setVisibility(View.VISIBLE);
            albums.toolbarTitle.setText("");
        }

        if(lastButton.equals(albums.mButton3)){
            ArrayList<Photo> selected = albums.photoAdapter.getSelectedPhotos();
            if(selected.size()==0){
                albums.toastMessage("0 pictures selected.");
                return albums.albumState;
            }
            albums.album.removeAllPhoto(selected);
            Photos.writeAlbums(albums);
            albums.photoAdapter.notifyDataSetChanged();
            albums.toastMessage("Change Applied.");

            albums.albumAdapter.setSelectable(false);
            albums.albumAdapter.deselectAll();
            albums.mButton3.setVisibility(View.GONE);
            albums.mButton4.setVisibility(View.GONE);
            albums.mButton5.setVisibility(View.GONE);
            albums.mButton1.setVisibility(View.VISIBLE);
            albums.mButton2.setVisibility(View.VISIBLE);
            albums.toolbarTitle.setText(albums.album.getName());
        }

        if(lastButton.equals(albums.mButton4)){

            ArrayList<Photo> selectedPhotos = albums.photoAdapter.getSelectedPhotos();
            if(selectedPhotos.size()==0){
                albums.toastMessage("0 pictures selected.");
                return albums.albumState;
            }

            ArrayList<String> albumNames = Photos.albumsGetAlbumListStr();
            // Create a dialog builder
            AlertDialog.Builder builder = new AlertDialog.Builder(albums);
            builder.setTitle("Select an Album");

            albumNames.removeIf(x->x.equals(albums.album.getName()));
            // Convert ArrayList to Array for the list adapter
            final String[] albumNamesArray = albumNames.toArray(new String[0]);

            // Set the single-choice items and handle item selection
            builder.setSingleChoiceItems(albumNamesArray, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Handle item selection here, for example:

                    // Do something with the selected album name
                    // For example, display it or use it in further processing
                }
            });

            // Add OK button
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Your OK button logic here
                    String selectedAlbumName = albumNamesArray[((AlertDialog) dialog).getListView().getCheckedItemPosition()];
                    albums.toastMessage("Selected Album: " + selectedAlbumName);

                    ArrayList<Photo> collect = new ArrayList<Photo>();
                    ArrayList<Photo> duplicates = Photos.movePhoto(selectedPhotos, collect, selectedAlbumName);
                    albums.album.getPhotos().removeAll(collect);

                    if (!duplicates.isEmpty()) {
                        handleDuplicates(duplicates, 0, selectedAlbumName); // Display first photo initially
                    }

                    Photos.writeAlbums(albums);
                    albums.photoAdapter.notifyDataSetChanged();

                    albums.photoAdapter.setSelectable(false);
                    albums.photoAdapter.deselectAll();
                    albums.mButton3.setVisibility(View.GONE);
                    albums.mButton4.setVisibility(View.GONE);
                    albums.mButton5.setVisibility(View.GONE);
                    albums.toolbarTitle.setText(albums.album.getName());
                    albums.mButton1.setVisibility(View.VISIBLE);
                    albums.mButton2.setVisibility(View.VISIBLE);
                }
            });

            // Add Cancel button
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    albums.photoAdapter.setSelectable(false);
                    albums.photoAdapter.deselectAll();
                    albums.mButton3.setVisibility(View.GONE);
                    albums.mButton4.setVisibility(View.GONE);
                    albums.mButton5.setVisibility(View.GONE);
                    albums.toolbarTitle.setText(albums.album.getName());
                    albums.mButton1.setVisibility(View.VISIBLE);
                    albums.mButton2.setVisibility(View.VISIBLE);
                    // Your Cancel button logic here
                    dialog.dismiss(); // Dismiss the dialog
                }

            });

            // Create and show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();

            albums.toastMessage("Change Applied.");
        }

        if(lastButton.equals(albums.mButton5)){
            albums.photoAdapter.setSelectable(false);
            albums.photoAdapter.deselectAll();
            albums.mButton3.setVisibility(View.GONE);
            albums.mButton4.setVisibility(View.GONE);
            albums.mButton5.setVisibility(View.GONE);
            albums.mButton1.setVisibility(View.VISIBLE);
            albums.mButton2.setVisibility(View.VISIBLE);
            albums.toolbarTitle.setText(albums.album.getName());
            return albums.albumState;
        }
        return null;
    }
    private void handleDuplicates(final ArrayList<Photo> photos, final int position, String selectedAlbumName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(albums);
        LayoutInflater inflater = albums.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.grid_item_photo, null);
        final ImageView imageView = dialogView.findViewById(R.id.image_view);

        // Display the photo
        imageView.setImageURI(photos.get(position).getUri());

        builder.setView(dialogView)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when "Yes" is clicked
                        // For example: Move the photo or perform any action
                        // Move to the next photo or close the dialog if all photos are displayed
                        Photo photo = photos.get(position);
                        Photo removed = Photos.movePhotoOverWrite(photo,selectedAlbumName);
                        albums.album.getPhotos().remove(photo);
                        albums.photoAdapter.notifyDataSetChanged();
                        int nextPosition = position + 1;
                        if (nextPosition < photos.size()) {
                            handleDuplicates(photos, nextPosition, selectedAlbumName);
                        } else {
                            Photos.writeAlbums(imageView.getContext());
                            dialog.dismiss(); // Dismiss the dialog when all photos are displayed
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when "No" is clicked
                        // For example: Do not overwrite the photo
                        Toast.makeText(albums, "Photo not overwritten", Toast.LENGTH_SHORT).show();
                        int nextPosition = position + 1;
                        if (nextPosition < photos.size()) {
                            handleDuplicates(photos, nextPosition, selectedAlbumName);
                        } else {
                            Photos.writeAlbums(imageView.getContext());
                            dialog.dismiss(); // Dismiss the dialog when all photos are displayed
                        }
                    }
                })
                .setMessage("Overwrite existing photo?")
                .show();
    }
}
