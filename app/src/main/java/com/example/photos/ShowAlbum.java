package com.example.photos;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static  com.example.photos.AlbumAdapter.ViewHolder;

public class ShowAlbum extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Album> albumList;
    private Album album;

    MaterialButton mSelectButton, mNewAlbumButton, mDeleteButton, mMoveButton, mCancelButton;

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
        setContentView(R.layout.show_album);

        albumList = Photos.albumsGetAlbums();
        Bundle bundle = getIntent().getExtras();
        this.album = Photos.albumsGetAlbum(bundle.getSerializable("album", Album.class).getName());
        this.photoAdapter =new PhotoAdapter(this, album.getPhotos());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new CustomGridLayoutManager(this, 2));
        recyclerView.setAdapter(photoAdapter); // Use AlbumAdapter here

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        TextView textView = (TextView) findViewById(R.id.toolbar_title);
        textView.setText(album.getName());

        mSelectButton = findViewById(R.id.selectButton);
        mNewAlbumButton = findViewById(R.id.newAlbumButton);
        mDeleteButton = findViewById(R.id.delete_button);
        mMoveButton = findViewById(R.id.move_button);
        mCancelButton = findViewById(R.id.cancel_button);

        mDeleteButton.setVisibility(View.GONE);
        mMoveButton.setVisibility(View.GONE);
        mCancelButton.setVisibility(View.GONE);

        mSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoAdapter.setSelectable(true);
                mNewAlbumButton.setVisibility(View.GONE);
                mSelectButton.setVisibility(View.GONE);
                mDeleteButton.setVisibility(View.VISIBLE);
                mMoveButton.setVisibility(View.VISIBLE);
                mCancelButton.setVisibility(View.VISIBLE);
                textView.setText("");
            }
        });
        mNewAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), getIntent().getExtras().getSerializable("album", Album.class).getName(), Toast.LENGTH_SHORT).show();
                photoAdapter.setSelectable(false);
                photoAdapter.deselectAll();
                mDeleteButton.setVisibility(View.GONE);
                mMoveButton.setVisibility(View.GONE);
                mCancelButton.setVisibility(View.GONE);
                textView.setText(album.getName());
                mNewAlbumButton.setVisibility(View.VISIBLE);
                mSelectButton.setVisibility(View.VISIBLE);
            }
        });
        mMoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> albumNames = Photos.albumsGetAlbumListStr();
                // Create a dialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Select an Album");

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
                        Toast.makeText(view.getContext(), "Selected Album: " + selectedAlbumName, Toast.LENGTH_SHORT).show();

                        ArrayList<Photo> selectedPhotos = photoAdapter.getSelectedPhotos(); // ArrayList<Photo>
                        ArrayList<Photo> collect = new ArrayList<Photo>();
                        ArrayList<Photo> duplicates = Photos.movePhoto(selectedPhotos, collect, selectedAlbumName);
                        album.getPhotos().removeAll(collect);

                        if (!duplicates.isEmpty()) {
                            handleDuplicates(duplicates, 0, selectedAlbumName); // Display first photo initially
                        }

                        Photos.writeAlbums(view.getContext());
                        photoAdapter.notifyDataSetChanged();

                        photoAdapter.setSelectable(false);
                        photoAdapter.deselectAll();
                        mDeleteButton.setVisibility(View.GONE);
                        mMoveButton.setVisibility(View.GONE);
                        mCancelButton.setVisibility(View.GONE);
                        textView.setText(album.getName());
                        mNewAlbumButton.setVisibility(View.VISIBLE);
                        mSelectButton.setVisibility(View.VISIBLE);
                    }
                });

                // Add Cancel button
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        photoAdapter.setSelectable(false);
                        photoAdapter.deselectAll();
                        mDeleteButton.setVisibility(View.GONE);
                        mMoveButton.setVisibility(View.GONE);
                        mCancelButton.setVisibility(View.GONE);
                        textView.setText(album.getName());
                        mNewAlbumButton.setVisibility(View.VISIBLE);
                        mSelectButton.setVisibility(View.VISIBLE);
                        // Your Cancel button logic here
                        dialog.dismiss(); // Dismiss the dialog
                    }

                });

                // Create and show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();

                Toast.makeText(getApplicationContext(), "Change Applied.", Toast.LENGTH_SHORT).show();


            }
        });
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                album.removeAllPhoto(photoAdapter.getSelectedPhotos());
                Photos.writeAlbums(view.getContext());
                photoAdapter.notifyDataSetChanged();

                toastMessage("Change Applied.");

                photoAdapter.setSelectable(false);
                photoAdapter.deselectAll();
                mDeleteButton.setVisibility(View.GONE);
                mMoveButton.setVisibility(View.GONE);
                mCancelButton.setVisibility(View.GONE);
                textView.setText(album.getName());
                mNewAlbumButton.setVisibility(View.VISIBLE);
                mSelectButton.setVisibility(View.VISIBLE);
            }
        });
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
                        Toast.makeText(ShowAlbum.this, removed.getUri().toString(), Toast.LENGTH_SHORT).show();
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
