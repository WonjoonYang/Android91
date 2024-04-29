package com.example.photos;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ImageDecoder;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private Context mContext;
    private List<Photo> mPhotoList;
    private ArrayList<Photo> selectedPhotos;
    private boolean selectable = false;
    private boolean hidePopUp = false;
    private ArrayList<Album> albumsList;

    public ArrayList<ViewHolder> viewHolders = new ArrayList<>();
    public PhotoAdapter(Context context, List<Photo> photoList) {
        this.mContext = context;
        this.mPhotoList = photoList;
        this.selectedPhotos = new ArrayList<Photo>();
    }
    public void setSelectable(boolean bool){
        this.selectable = bool;
    }
    public boolean isSelectable(){
        return this.selectable;
    }

    public ArrayList<Photo> getSelectedPhotos(){
        return this.selectedPhotos;
    }
    public void deselectAll(){
        this.viewHolders.stream().forEach(v -> v.imageView.setColorFilter(Color.argb(0, 0, 0, 0)));
        this.selectedPhotos.clear();
    }
    public void hidePopUpButton(){
        this.hidePopUp = true;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_item_photo, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        ImageView imageView = viewHolder.imageView;
        imageView.setClickable(true);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSelectable()){
                    int photoIdx = viewHolder.getAdapterPosition();
                    Bundle bundle = new Bundle();
                    bundle.putInt("number", photoIdx);
                    bundle.putSerializable("photo", mPhotoList.get(photoIdx));
                    Intent intent = new Intent(mContext, ShowPhoto.class);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                    return;
                }
                if (!viewHolder.isSelected) {
                    // Change tint color to semi-transparent black
                    imageView.setColorFilter(Color.argb(150, 0, 0, 0));
                    PhotoAdapter.this.selectedPhotos.add(mPhotoList.get(viewHolder.getAdapterPosition()));
                } else {
                    // Change tint color to semi-transparent white
                    imageView.setColorFilter(Color.argb(0, 0, 0, 0));
                    PhotoAdapter.this.selectedPhotos.remove(mPhotoList.get(viewHolder.getAdapterPosition()));
                }

                // Toggle the flag
                viewHolder.isSelected = !viewHolder.isSelected;

                // Show a Toast
                Toast.makeText(imageView.getContext(), "Image clicked", Toast.LENGTH_SHORT).show();
            }
        });
        this.viewHolders.add(viewHolder);

        ImageButton menuButton = viewHolder.menuButton;

        if(hidePopUp){
            menuButton.setVisibility(View.GONE);
        }

        View itemView = viewHolder.itemView;

        viewHolder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflating the popup menu
                PopupMenu popupMenu = new PopupMenu(itemView.getContext(), menuButton);
                popupMenu.inflate(R.menu.popup_menu_photo);

                // Registering menu item click listener
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.action_add_person) {
                            Toast.makeText(imageView.getContext(), "Image clicked", Toast.LENGTH_SHORT).show();
                            // Create an AlertDialog Builder
                            AlertDialog.Builder builder = new AlertDialog.Builder(imageView.getContext());

                            // Set the title and message for the dialog
                            builder.setTitle("Enter Person");

                            // Set up the input field
                            final EditText input = new EditText(imageView.getContext());
                            builder.setView(input);

                            // Set up the buttons
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String personName = input.getText().toString();
                                    // You can perform further actions here, like creating a new album with the entered name
                                    if (!mPhotoList.get(viewHolder.getAdapterPosition()).addPerson(personName)){
                                        Toast.makeText(imageView.getContext(), "Name conflict. Person: " + personName, Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    Photos.writeAlbums(imageView.getContext());
                                    notifyItemChanged(viewHolder.getAdapterPosition());
                                    Toast.makeText(imageView.getContext(), "Change Applied. Person:  " + personName, Toast.LENGTH_SHORT).show();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            // Show the dialog
                            builder.show();
                        }else if(id == R.id.action_add_location){
                            Toast.makeText(imageView.getContext(), "Image clicked", Toast.LENGTH_SHORT).show();
                            // Create an AlertDialog Builder
                            AlertDialog.Builder builder = new AlertDialog.Builder(imageView.getContext());

                            // Set the title and message for the dialog
                            builder.setTitle("Enter Location");

                            // Set up the input field
                            final EditText input = new EditText(imageView.getContext());
                            builder.setView(input);

                            // Set up the buttons
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String location = input.getText().toString();
                                    // You can perform further actions here, like creating a new album with the entered name
                                    if (!mPhotoList.get(viewHolder.getAdapterPosition()).addLocation(location)){
                                        Toast.makeText(imageView.getContext(), "Name conflict. Location: " + location, Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    Photos.writeAlbums(imageView.getContext());
                                    notifyItemChanged(viewHolder.getAdapterPosition());
                                    Toast.makeText(imageView.getContext(), "Change Applied. Location: " + location, Toast.LENGTH_SHORT).show();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            // Show the dialog
                            builder.show();
                        } else if (id == R.id.action_remove_person) {
                            // Show dialog or bottom sheet with selectable list of people
                            Photo photo = mPhotoList.get(viewHolder.getAdapterPosition());
                            ArrayList<String> peopleList = photo.getPeople();

                            AlertDialog.Builder builder = new AlertDialog.Builder(imageView.getContext());
                            builder.setTitle("Select People to Remove");

                            // Convert ArrayList to array for dialog
                            final String[] peopleArray = peopleList.toArray(new String[0]);

                            // Set up checkboxes for each person
                            builder.setMultiChoiceItems(peopleArray, null, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    // Handle checkbox clicks if needed
                                }
                            });

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Perform deletion for selected people
                                    SparseBooleanArray checkedItems = ((AlertDialog) dialog).getListView().getCheckedItemPositions();
                                    for (int i = 0; i < checkedItems.size(); i++) {
                                        int position = checkedItems.keyAt(i);
                                        if (checkedItems.valueAt(i)) {
                                            String selectedPerson = peopleArray[position];
                                            photo.removePerson(selectedPerson);
                                            Photos.writeAlbums(imageView.getContext());
                                        }
                                    }
                                    dialog.dismiss();
                                }
                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }else if (id == R.id.action_remove_location) {
                            // Show dialog or bottom sheet with selectable list of people
                            Photo photo = mPhotoList.get(viewHolder.getAdapterPosition());
                            ArrayList<String> locationsList = photo.getLocations();

                            AlertDialog.Builder builder = new AlertDialog.Builder(imageView.getContext());
                            builder.setTitle("Select Locations to Remove");

                            // Convert ArrayList to array for dialog
                            final String[] locationsArray = locationsList.toArray(new String[0]);

                            // Set up checkboxes for each person
                            builder.setMultiChoiceItems(locationsArray, null, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    // Handle checkbox clicks if needed
                                }
                            });

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Perform deletion for selected people
                                    SparseBooleanArray checkedItems = ((AlertDialog) dialog).getListView().getCheckedItemPositions();
                                    for (int i = 0; i < checkedItems.size(); i++) {
                                        int position = checkedItems.keyAt(i);
                                        if (checkedItems.valueAt(i)) {
                                            String selectedPerson = locationsArray[position];
                                            photo.removeLocation(selectedPerson);
                                            Photos.writeAlbums(imageView.getContext());
                                        }
                                    }
                                    dialog.dismiss();
                                }
                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }else if(id == R.id.action_delete) {
                            Toast.makeText(imageView.getContext(), "DELETE clicked", Toast.LENGTH_SHORT).show();
                            int position = viewHolder.getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                mPhotoList.get(position);
                                mPhotoList.remove(position);
                                notifyItemRemoved(position);
                                Photos.writeAlbums(imageView.getContext());
                                deselectAll();
                                return true;
                            }
                        }else if(id == R.id.action_move){
                            Toast.makeText(imageView.getContext(), "MOVE clicked", Toast.LENGTH_SHORT).show();
                            ArrayList<String> albumNames = Photos.albumsGetAlbumListStr();
                            // Create a dialog builder
                            AlertDialog.Builder builder = new AlertDialog.Builder(PhotosState.albums);
                            builder.setTitle("Select an Album");

                            albumNames.removeIf(x->x.equals(PhotosState.albums.album.getName()));
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
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int position = viewHolder.getAdapterPosition();
                                        String selectedAlbumName = albumNamesArray[((AlertDialog) dialog).getListView().getCheckedItemPosition()];
                                        PhotosState.albums.toastMessage("Selected Album: " + selectedAlbumName);
                                        ArrayList<Photo> selected = new ArrayList<Photo>();
                                        selected.add(mPhotoList.get(position));
                                        ArrayList<Photo> collect = new ArrayList<Photo>();
                                        ArrayList<Photo> duplicates = Photos.movePhoto(selected, collect, selectedAlbumName);
                                        PhotosState.albums.album.getPhotos().removeAll(collect);
                                        if (!duplicates.isEmpty()) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(PhotosState.albums);
                                            LayoutInflater inflater = PhotosState.albums.getLayoutInflater();
                                            View dialogView = inflater.inflate(R.layout.grid_item_photo, null);
                                            final ImageView imageView = dialogView.findViewById(R.id.image_view);
                                            imageView.setImageURI(selected.get(0).getUri());
                                            builder.setView(dialogView)
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // Do something when "Yes" is clicked
                                                            // For example: Move the photo or perform any action
                                                            // Move to the next photo or close the dialog if all photos are displayed
                                                            Photo photo = selected.get(0);
                                                            Photo removed = Photos.movePhotoOverWrite(photo,selectedAlbumName);
                                                            PhotosState.albums.album.getPhotos().remove(photo);
                                                            PhotosState.albums.photoAdapter.notifyDataSetChanged();
                                                            int nextPosition = position + 1;
                                                            Photos.writeAlbums(imageView.getContext());
                                                            dialog.dismiss(); // Dismiss the dialog when all photos are displayed
                                                        }
                                                    })
                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // Do something when "No" is clicked
                                                            // For example: Do not overwrite the photo
                                                            int nextPosition = position + 1;
                                                                Photos.writeAlbums(imageView.getContext());
                                                                dialog.dismiss(); // Dismiss the dialog when all photos are displayed
                                                        }
                                                    })
                                                    .setMessage("Overwrite existing photo?")
                                                    .show();
                                        }
                                        Photos.writeAlbums(PhotosPhotosState.albums);
                                        notifyDataSetChanged();
                                    }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss(); // Dismiss the dialog
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();

                            PhotosState.albums.toastMessage("Change Applied.");
                        }
                        return false;
                    }
                });
                // Showing the popup menu
                popupMenu.show();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo photo = mPhotoList.get(position);
        ImageDecoder.Source source = ImageDecoder.createSource(mContext.getContentResolver(), photo.getUri());
        try {
            Bitmap bitmap = ImageDecoder.decodeBitmap(source);
            holder.imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        SquareImageView imageView;
        TextView textView;
        ImageButton menuButton;
        boolean isSelected;

        public static ArrayList<Album> albums;

        public static void setAlbums(ArrayList<Album> albumsList){
            albums = albumsList;
        }

        public boolean isSelected(){
            return isSelected;
        }
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            textView = itemView.findViewById(R.id.text_view);
            menuButton = itemView.findViewById(R.id.menu_button);
            isSelected = false;
        }
    }
}
