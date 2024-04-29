package com.example.photos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ShowSearch extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Album> albumList;
    private Album album;

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(), uri -> {
                if (uri != null) {
                    int numAdded = album.addPhotos(uri);
                    uri.forEach(x -> getContentResolver().takePersistableUriPermission(x, Intent.FLAG_GRANT_READ_URI_PERMISSION));
                    Photos.writeAlbums(this);
                    photoAdapter.notifyDataSetChanged();
                    toastMessage(Integer.toString(numAdded) + " images added " + Integer.toString(uri.size() - numAdded) + " duplicates ");
                } else {
                    toastMessage("image not added");
                }
            });

    public void showPhoto(Photo photo) {

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_search);

        albumList = Photos.albumsGetAlbums();
        Bundle bundle = getIntent().getExtras();

        this.album = bundle.getSerializable("album", Album.class);
        this.photoAdapter =new PhotoAdapter(this, album.getPhotos());
        photoAdapter.hidePopUpButton();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new CustomGridLayoutManager(this, 2));
        recyclerView.setAdapter(photoAdapter); // Use AlbumAdapter here

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        TextView textView = (TextView) findViewById(R.id.toolbar_title);
        textView.setText(album.getName());
    }

    public void toastMessage(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
    private void handleDuplicates(final ArrayList<Photo> photos, final int position, String selectedAlbumName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
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
                        album.getPhotos().remove(photo);
                        photoAdapter.notifyDataSetChanged();
                        Toast.makeText(ShowSearch.this, removed.getUri().toString(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), "Photo not overwritten", Toast.LENGTH_SHORT).show();
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
