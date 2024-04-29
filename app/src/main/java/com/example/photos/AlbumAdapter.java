package com.example.photos;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
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
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    private Context mContext;
    private List<Album> mAlbumList;
    private ArrayList<Album> selectedAlbums = new ArrayList<Album>();
    private boolean selectable = false;

    public ArrayList<ViewHolder> viewHolders = new ArrayList<>();
    public AlbumAdapter(Context context, List<Album> albumList) {
        mContext = context;
        mAlbumList = albumList;
    }
    public void setSelectable(boolean bool){
        this.selectable = bool;
    }
    public boolean isSelectable(){
        return this.selectable;
    }
    public ArrayList<Album> getSelectedAlbums(){
        return this.selectedAlbums;
    }
    public void deselectAll(){
        this.viewHolders.stream().forEach(v -> v.imageView.setColorFilter(Color.argb(0, 0, 0, 0)));
        this.selectedAlbums.clear();
    }

    public ViewHolder getViewHolder(View v){
        for (ViewHolder viewHolder : viewHolders){
            if (viewHolder.imageView.equals(v)){
                return viewHolder;
            }
        }
        return null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_item_album, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        ImageView imageView = viewHolder.imageView;
        imageView.setClickable(true);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSelectable()){
                    PhotosAlbumState.lastButton = v;
                    ((Albums)mContext).currentState = ((Albums)mContext).currentState.processEvent();


//                    Album selectedAlbum = mAlbumList.get(viewHolder.getAdapterPosition());
//                    Photos.setCurrAlbum(selectedAlbum);
//                    ((Albums)mContext).album = selectedAlbum;


//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("album", selectedAlbum);
//                    Intent intent = new Intent(mContext, ShowAlbum.class);
//                    intent.putExtras(bundle);
//                    mContext.startActivity(intent);
                    return;
                }
                if (!viewHolder.isSelected) {
                    // Change tint color to semi-transparent black
                    imageView.setColorFilter(Color.argb(150, 0, 0, 0));
                    AlbumAdapter.this.selectedAlbums.add(mAlbumList.get(viewHolder.getAdapterPosition()));
                } else {
                    // Change tint color to semi-transparent white
                    imageView.setColorFilter(Color.argb(0, 0, 0, 0));
                    AlbumAdapter.this.selectedAlbums.remove(mAlbumList.get(viewHolder.getAdapterPosition()));
                }

                // Toggle the flag
                viewHolder.isSelected = !viewHolder.isSelected;
            }
        });
        viewHolders.add(viewHolder);

        ImageButton menuButton = viewHolder.menuButton;
        View itemView = viewHolder.itemView;

        viewHolder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflating the popup menu
                PopupMenu popupMenu = new PopupMenu(itemView.getContext(), menuButton);
                popupMenu.inflate(R.menu.popup_menu_album);

                // Registering menu item click listener
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.action_rename) {
                            Toast.makeText(imageView.getContext(), "Image clicked", Toast.LENGTH_SHORT).show();
                            // Create an AlertDialog Builder
                            AlertDialog.Builder builder = new AlertDialog.Builder(imageView.getContext());

                            // Set the title and message for the dialog
                            builder.setTitle("Enter Album Name");

                            // Set up the input field
                            final EditText input = new EditText(imageView.getContext());
                            builder.setView(input);

                            // Set up the buttons
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String albumName = input.getText().toString();
                                    // You can perform further actions here, like creating a new album with the entered name
                                    if (!Photos.albumsValidateName(albumName)){
                                        Toast.makeText(imageView.getContext(), "Name conflict. Album name:" + albumName, Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    mAlbumList.get(viewHolder.getAdapterPosition()).setName(albumName);
                                    Photos.writeAlbums(imageView.getContext());
                                    notifyItemChanged(viewHolder.getAdapterPosition());
                                    Toast.makeText(imageView.getContext(), "Change Applied. Album name: " + albumName, Toast.LENGTH_SHORT).show();
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
                        }else if(id == R.id.action_delete) {
                            Toast.makeText(imageView.getContext(), "DELETE clicked", Toast.LENGTH_SHORT).show();
                            int position = viewHolder.getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                mAlbumList.remove(position);
                                notifyItemRemoved(position);
                                Photos.writeAlbums(imageView.getContext());
                                deselectAll();
                                return true;
                            }
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
        Album album = mAlbumList.get(position);
        holder.textView.setText(album.getName());
        if (album.getImage() != null) {
            holder.imageView.setImageBitmap(album.getImage());
        } else {
            holder.imageView.setImageResource(R.drawable.square);
        }
    }


    @Override
    public int getItemCount() {
        return mAlbumList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        SquareImageView imageView;
        TextView textView;
        ImageButton menuButton;
        boolean isSelected;

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
