package com.example.photos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

public class PhotosAlbumState extends PhotosState{
    private static PhotosAlbumState instance = null;

    private PhotosAlbumState() {

    }
    public static PhotosAlbumState getInstance() {
        if (instance == null) {
            instance = new PhotosAlbumState();
        }
        return instance;
    }

    @Override
    void enter() {
//
        albums.albumAdapter=new AlbumAdapter(albums, albums.albumList);
        albums.recyclerView.setAdapter(albums.albumAdapter);
        albums.albumAdapter.notifyDataSetChanged();
        albums.toolbarTitle.setText("Albums");
        albums.mButton1.setText("New Album");

        albums.mButton1.setVisibility(View.VISIBLE);
        albums.mButton2.setVisibility(View.VISIBLE);
        albums.backButton.setVisibility(View.GONE);
        albums.mButton3.setVisibility(View.GONE);
        albums.mButton4.setVisibility(View.GONE);
        albums.mButton5.setVisibility(View.GONE);

//        albums.mSearchFab.setVisibility(View.VISIBLE);
        albums.mSearchFab.show();
    }
    @Override
    PhotosState processEvent() {
        if (lastButton.equals(albums.mButton1)){
            /* 2.2.1 Show input dialog to get the name of the new album.
             * -- Create an AlertDialog Builder
             * -- Set 1) the title and message for the dialog,
             * -- 2) the input field and 3) the buttons.
             * -- Show the dialog
             */
            AlertDialog.Builder builder = new AlertDialog.Builder(albums);
            builder.setTitle("Enter Album Name");
            final EditText input = new EditText(albums);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String albumName = input.getText().toString();
                    if (!Photos.albumsValidateName(albumName)){
                        albums.toastMessage("Name conflict. Album name: "+ albumName);
                        return;
                    }
                    albums.albumList.add(new Album(null, albumName));
                    Photos.writeAlbums(albums);
                    albums.toastMessage("Change Applied. Album name: " + albumName);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();

            return albums.albumState;
        }else if (lastButton.equals(albums.mButton2)){
            albums.albumAdapter.setSelectable(true);
            albums.mButton1.setVisibility(View.GONE);
            albums.mButton2.setVisibility(View.GONE);
            albums.mButton3.setVisibility(View.VISIBLE);
            albums.mButton5.setVisibility(View.VISIBLE);

            return albums.albumState;
        }else if(lastButton.equals(albums.mButton3)){
            ArrayList<Album> selected = albums.albumAdapter.getSelectedAlbums();
            if(selected.size()==0){
                albums.toastMessage("0 albums selected.");
                return albums.albumState;
            }
            albums.albumList.removeAll(selected);
            Photos.writeAlbums(albums);
            albums.albumAdapter.notifyDataSetChanged();
            albums.toastMessage("Change Applied.");

            albums.albumAdapter.setSelectable(false);
            albums.albumAdapter.deselectAll();
            albums.mButton3.setVisibility(View.GONE);
            albums.mButton5.setVisibility(View.GONE);
            albums.mButton1.setVisibility(View.VISIBLE);
            albums.mButton2.setVisibility(View.VISIBLE);

            return albums.albumState;
        }else if(lastButton.equals(albums.mButton5)){
            albums.albumAdapter.setSelectable(false);
            albums.albumAdapter.deselectAll();
            albums.mButton3.setVisibility(View.GONE);
            albums.mButton5.setVisibility(View.GONE);
            albums.mButton1.setVisibility(View.VISIBLE);
            albums.mButton2.setVisibility(View.VISIBLE);

            return albums.albumState;
        }else if(lastButton.equals(albums.mSearchFab)){
            if (!albums.isAllFabsVisible) {

                // when isAllFabsVisible becomes
                // true make all the action name
                // texts and FABs VISIBLE.
                albums.mSearchLocation.show();
                albums.mSearchPerson.show();
                albums.mCombineSearch.show();
                albums.mSearchLocationText.setVisibility(View.VISIBLE);
                albums.mSearchPersonText.setVisibility(View.VISIBLE);
                albums.mCombineSearchText.setVisibility(View.VISIBLE);
                // Now extend the parent FAB, as
                // user clicks on the shrinked
                // parent FAB
                albums.mSearchFab.extend();

                // make the boolean variable true as
                // we have set the sub FABs
                // visibility to GONE
                albums.isAllFabsVisible = true;
            } else {

                // when isAllFabsVisible becomes
                // true make all the action name
                // texts and FABs GONE.
                albums.mSearchLocation.hide();
                albums.mSearchPerson.hide();
                albums.mCombineSearch.hide();
                albums.mSearchLocationText.setVisibility(View.GONE);
                albums.mSearchPersonText.setVisibility(View.GONE);
                albums.mCombineSearchText.setVisibility(View.GONE);
                // Set the FAB to shrink after user
                // closes all the sub FABs
                albums.mSearchFab.shrink();

                // make the boolean variable false
                // as we have set the sub FABs
                // visibility to GONE
                albums.isAllFabsVisible = false;
            }
        }else if (lastButton.equals(albums.mSearchLocation)){
            // Create an AlertDialog Builder
            AlertDialog.Builder builder = new AlertDialog.Builder(albums);
            builder.setTitle("Enter Location");

            // Create EditText to input the name
//                final EditText input = new EditText(Albums.this);
            final AutoCompleteTextView input = new AutoCompleteTextView(albums);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

//                String[] suggestions = {"hello world"};
            ArrayList<String> suggestions = Photos.dictionaryGetAllkey("location");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(albums, android.R.layout.simple_dropdown_item_1line, suggestions);
            input.setAdapter(adapter);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String location = input.getText().toString().trim();
                    ArrayList<Photo> taggedPhotos = Photos.dictionarySearchByTag("location", location);
                    if(taggedPhotos == null){
                        albums.toastMessage("0 pictures found.");
                        dialog.cancel();
                        return;
                    }
                    albums.toastMessage(Integer.toString(taggedPhotos.size()) + " pictures found.");
                    Album searchResult = new Album(null, "Search Result");
                    searchResult.setPhotos(taggedPhotos);

                    Photos.setCurrAlbum(searchResult);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("album", searchResult);
                    Intent intent = new Intent(albums, ShowSearch.class);
                    intent.putExtras(bundle);
                    albums.startActivity(intent);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Create and show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }else if (lastButton.equals(albums.mSearchPerson)){

            // Create an AlertDialog Builder
            AlertDialog.Builder builder = new AlertDialog.Builder(albums);
            builder.setTitle("Enter Name");

            // Create EditText to input the name
            final AutoCompleteTextView input = new AutoCompleteTextView(albums);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

//                String[] suggestions = {"hello world"};
            ArrayList<String> suggestions = Photos.dictionaryGetAllkey("person");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(albums, android.R.layout.simple_dropdown_item_1line, suggestions);
            input.setAdapter(adapter);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = input.getText().toString().trim();
                    ArrayList<Photo> taggedPhotos = Photos.dictionarySearchByTag("person", name);
                    if(taggedPhotos == null){
                        albums.toastMessage("0 pictures found.");
                        dialog.cancel();
                        return;
                    }
                    albums.toastMessage(Integer.toString(taggedPhotos.size()) + " pictures found.");
                    Album searchResult = new Album(null, "Search Result");
                    searchResult.setPhotos(taggedPhotos);

                    Photos.setCurrAlbum(searchResult);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("album", searchResult);
                    Intent intent = new Intent(albums, ShowSearch.class);
                    intent.putExtras(bundle);
                    albums.startActivity(intent);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Create and show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }else if (lastButton.equals(albums.mCombineSearch)){

            // Create an AlertDialog Builder
            AlertDialog.Builder builder = new AlertDialog.Builder(albums);
            builder.setTitle("Enter Name and Location");

            // Create LinearLayout to hold EditTexts
            LinearLayout layout = new LinearLayout(albums);
            layout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout layoutText1 = new LinearLayout(albums);
            layoutText1.setOrientation(LinearLayout.HORIZONTAL);

            Spinner spinner1 = new Spinner(albums);
//            ArrayList<String> spinnerItems = new ArrayList<>();
//            spinnerItems.add("Item 1");
//            spinnerItems.add("Item 2");
//            spinnerItems.add("Item 3");
// Add more items as needed
            ArrayList<String> spinnerItems = Photos.dictionaryGetAllTagName();
            ArrayAdapter<String> spinner1Adapter = new ArrayAdapter<>(albums, android.R.layout.simple_spinner_item, spinnerItems);
            spinner1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner1.setAdapter(spinner1Adapter);
            spinner1.setSelection(0);
            layoutText1.addView(spinner1);

            // Create EditText for name
            final AutoCompleteTextView nameInput = new AutoCompleteTextView(albums);
            nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
            nameInput.setHint("Name");
            layoutText1.addView(nameInput);
            layout.addView(layoutText1);
            nameInput.setMinWidth(400);
            nameInput.setMaxWidth(400);

//            spinner1Adapter.getI
            spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(adapterView.getItemAtPosition(i).toString().equals("person")){
                        nameInput.setHint("Name");
                        ArrayList<String> suggestionsName = Photos.dictionaryGetAllkey("person");
                        ArrayAdapter<String> adapterName = new ArrayAdapter<>(albums, android.R.layout.simple_dropdown_item_1line, suggestionsName);
                        nameInput.setAdapter(adapterName);
                    }else{
                        nameInput.setHint("Location");
                        ArrayList<String> suggestionsName = Photos.dictionaryGetAllkey("location");
                        ArrayAdapter<String> adapterName = new ArrayAdapter<>(albums, android.R.layout.simple_dropdown_item_1line, suggestionsName);
                        nameInput.setAdapter(adapterName);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            ArrayList<String> suggestionsName = Photos.dictionaryGetAllkey("person");
            ArrayAdapter<String> adapterName = new ArrayAdapter<>(albums, android.R.layout.simple_dropdown_item_1line, suggestionsName);
            nameInput.setAdapter(adapterName);

            LinearLayout layoutText2 = new LinearLayout(albums);
            layoutText2.setOrientation(LinearLayout.HORIZONTAL);

            Spinner spinner2 = new Spinner(albums);
            ArrayAdapter<String> spinner2Adapter = new ArrayAdapter<>(albums, android.R.layout.simple_spinner_item, spinnerItems);
            spinner2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner2.setAdapter(spinner2Adapter);
            spinner2.setSelection(1);
            layoutText2.addView(spinner2);

            // Create EditText for location
            final AutoCompleteTextView locationInput = new AutoCompleteTextView(albums);
            locationInput.setInputType(InputType.TYPE_CLASS_TEXT);
            locationInput.setHint("Location");
            layoutText2.addView(locationInput);
            layout.addView(layoutText2);
            locationInput.setMinWidth(400);
            locationInput.setMaxWidth(400);

            spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(adapterView.getItemAtPosition(i).toString().equals("person")){
                        locationInput.setHint("Name");
                        ArrayList<String> suggestionsName = Photos.dictionaryGetAllkey("person");
                        ArrayAdapter<String> adapterName = new ArrayAdapter<>(albums, android.R.layout.simple_dropdown_item_1line, suggestionsName);
                        locationInput.setAdapter(adapterName);
                    }else{
                        locationInput.setHint("Location");
                        ArrayList<String> suggestionsName = Photos.dictionaryGetAllkey("location");
                        ArrayAdapter<String> adapterName = new ArrayAdapter<>(albums, android.R.layout.simple_dropdown_item_1line, suggestionsName);
                        locationInput.setAdapter(adapterName);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            ArrayList<String> suggestionsLocation = Photos.dictionaryGetAllkey("location");
            ArrayAdapter<String> adapterLocation = new ArrayAdapter<>(albums, android.R.layout.simple_dropdown_item_1line, suggestionsLocation);
            locationInput.setAdapter(adapterLocation);


            // Create radio buttons for search operator
            RadioGroup radioGroup = new RadioGroup(albums);
            radioGroup.setOrientation(RadioGroup.HORIZONTAL);

            RadioButton radioButtonAnd = new RadioButton(albums);
            radioButtonAnd.setText("AND");
            radioGroup.addView(radioButtonAnd);

            RadioButton radioButtonOr = new RadioButton(albums);
            radioButtonOr.setText("OR");
            radioGroup.addView(radioButtonOr);

            // Set default selection to "AND"
            radioButtonAnd.setChecked(true);

            layout.addView(radioGroup);

            builder.setView(layout);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String tagValue1 = nameInput.getText().toString().trim();
                    String tagValue2 = locationInput.getText().toString().trim();
                    String tagName1 = spinner1.getSelectedItem().toString().equals("person") ? "person" : "location";
                    String tagName2 = spinner2.getSelectedItem().toString().equals("person") ? "person" : "location";
                    boolean useAndOperator = radioButtonAnd.isChecked();

                    ArrayList<Photo> taggedPhotos = useAndOperator ? Photos.dictionarySearchByTagAnd(tagName1,tagValue1, tagName2,tagValue2)
                            : Photos.dictionarySearchByTagOr(tagName1,tagValue1, tagName2,tagValue2);
                    if(taggedPhotos == null){
                        albums.toastMessage("0 pictures found.");
                        dialog.cancel();
                        return;
                    }
                    albums.toastMessage(Integer.toString(taggedPhotos.size()) + " pictures found.");

                    Album searchResult = new Album(null, "Search Result");
                    searchResult.setPhotos(taggedPhotos);

                    Photos.setCurrAlbum(searchResult);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("album", searchResult);
                    Intent intent = new Intent(albums, ShowSearch.class);
                    intent.putExtras(bundle);
                    albums.startActivity(intent);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Create and show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }else if(lastButton instanceof ImageView){
            AlbumAdapter.ViewHolder viewHolder = albums.albumAdapter.getViewHolder(lastButton);
            if (!albums.albumAdapter.isSelectable()){
                Album selectedAlbum = albums.albumList.get(viewHolder.getAdapterPosition());
                Photos.setCurrAlbum(selectedAlbum);
                albums.album = selectedAlbum;

                albums.photosState.enter();
                return albums.photosState;
            }else{

            }
        }
        return null;
    }


}
