package com.example.photos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import static  com.example.photos.AlbumAdapter.ViewHolder;

public class Albums extends AppCompatActivity {

    protected RecyclerView recyclerView;
    protected AlbumAdapter albumAdapter;
    protected List<Album> albumList;

    protected PhotoAdapter photoAdapter;

    protected Album album;

    // MartialButtons
    /*
        NEW ALBUM
        SELECT
        DELETE
        MOVE
        CANCEL
    */
    protected MaterialButton mButton1,mButton2, mButton3, mButton4,mButton5;

    // FAB
    // FloatingActionButton for search.
    // ExtendedFloatingActionButton to handle the FAB
    // TextViews are taken to make visible and invisible along
    // with FABs except parent FAB's action name
    // Boolean to check whether sub FABs are visible or not
    protected ExtendedFloatingActionButton mSearchFab;
    protected FloatingActionButton mSearchLocation, mSearchPerson, mCombineSearch;
    protected TextView mSearchLocationText, mSearchPersonText, mCombineSearchText;
    protected Boolean isAllFabsVisible;
    protected Toolbar toolbar;
    protected TextView toolbarTitle;

    protected ImageButton backButton;


    protected ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
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

    protected PhotosState currentState;
    protected PhotosAlbumState albumState;
    protected PhotosPhotosState photosState;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.albums);
        getWindow();
        Photos.readApp(getApplicationContext());

        PhotosState.albums = this;
        albumState = PhotosAlbumState.getInstance();
        photosState = PhotosPhotosState.getInstance();
        currentState = albumState;

        albumList = Photos.albumsGetAlbums();
        albumAdapter=new AlbumAdapter(this, albumList);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new CustomGridLayoutManager(this, 2));
        recyclerView.setAdapter(albumAdapter); // Use AlbumAdapter here

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        backButton = (ImageButton) findViewById(R.id.back_button);

        /* 1. Initialize top buttons.
         *  1.1. Get top martial buttons from XML by ID.
         * -- Get select, newAlbum, delete, cancel buttons.
         *  1.2. Hide cancel and delete buttons.
         * -- Use setVisibility(...) method.
         * 2. Set onClickListners for each button.
         *  2.1 select
         *  2.2 newAlbum.
         *  2.3 cancel.
         *  2.4 delete.
         */
        mButton1 = findViewById(R.id.button1); //NEW ALBUM
        mButton2 = findViewById(R.id.button2); //SELECT
        mButton3 = findViewById(R.id.button3); //DELETE
        mButton4 = findViewById(R.id.button4); //MOVE
        mButton5 = findViewById(R.id.button5); //CANCEL

        // Register all the FABs with their appropriate IDs
        // This FAB button is the Parent
        // FAB button
        mSearchFab = findViewById(R.id.add_fab);
        mSearchLocation = findViewById(R.id.add_alarm_fab);
        mSearchPerson = findViewById(R.id.add_person_fab);
        mCombineSearch = findViewById(R.id.combine_search);

        currentState.enter();
//
//        mButton3.setVisibility(View.GONE);
//        mButton4.setVisibility(View.GONE);
//        mButton5.setVisibility(View.GONE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotosAlbumState.lastButton = view;
                currentState.processEvent();
            }
        });

        /* 2.1 select:
         *  2.1.1 Make albums selectable.
         *  2.1.2 Hide select and newAlbum buttons.
         *  2.1.2 Make newAlbum and select buttons visible.
         */
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotosAlbumState.lastButton = view;
                currentState.processEvent();
//                albumAdapter.setSelectable(true);
//                mButton1.setVisibility(View.GONE);
//                mButton2.setVisibility(View.GONE);
//                mButton3.setVisibility(View.VISIBLE);
//                mButton5.setVisibility(View.VISIBLE);
            }
        });
        /* 2.2 newAlbum:
         *  2.2.1 Show input dialog to get the name of the new album.
         *  2.2.2 Validate album name, and add the album to the albumsList if the name does not exist.
         */
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotosAlbumState.lastButton = view;
                currentState.processEvent();
                /* 2.2.1 Show input dialog to get the name of the new album.
                 * -- Create an AlertDialog Builder
                 * -- Set 1) the title and message for the dialog,
                 * -- 2) the input field and 3) the buttons.
                 * -- Show the dialog
                */
//                AlertDialog.Builder builder = new AlertDialog.Builder(Albums.this);
//                builder.setTitle("Enter Album Name");
//                final EditText input = new EditText(Albums.this);
//                builder.setView(input);
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String albumName = input.getText().toString();
//                        if (!Photos.albumsValidateName(albumName)){
//                            toastMessage("Name conflict. Album name: "+ albumName);
//                            return;
//                        }
//                        albumList.add(new Album(null, albumName));
//                        Photos.writeAlbums(Albums.this);
//                        toastMessage("Change Applied. Album name: " + albumName);
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                builder.show();
            }
        });
        mButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotosState.lastButton = view;
                currentState.processEvent();
            }
        });
        /* 2.3 Cancel:
         *  2.1.1 Make albums unselectable.
         *  2.1.2 Hide delete and cancel buttons.
         *  2.1.2 Make newAlbum and select buttons visible.
         */
        mButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotosState.lastButton = view;
                currentState.processEvent();
//                albumAdapter.setSelectable(false);
//                albumAdapter.deselectAll();
//                mButton3.setVisibility(View.GONE);
//                mButton5.setVisibility(View.GONE);
//                mButton1.setVisibility(View.VISIBLE);
//                mButton2.setVisibility(View.VISIBLE);
            }
        });
        mButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotosAlbumState.lastButton = view;
                currentState.processEvent();
//                albumList.removeAll(albumAdapter.getSelectedAlbums());
//                Photos.writeAlbums(getApplicationContext());
//                albumAdapter.notifyDataSetChanged();
//                toastMessage("Change Applied.");
//
//                albumAdapter.setSelectable(false);
//                albumAdapter.deselectAll();
//                mButton3.setVisibility(View.GONE);
//                mButton5.setVisibility(View.GONE);
//                mButton1.setVisibility(View.VISIBLE);
//                mButton2.setVisibility(View.VISIBLE);
            }
        });

        // Also register the action name text, of all the
        // FABs. except parent FAB action name text
        mSearchLocationText = findViewById(R.id.add_alarm_action_text);
        mSearchPersonText = findViewById(R.id.add_person_action_text);
        mCombineSearchText = findViewById(R.id.combine_search_text);

        // Now set all the FABs and all the action name
        // texts as GONE
        mSearchLocation.setVisibility(View.GONE);
        mSearchPerson.setVisibility(View.GONE);
        mCombineSearch.setVisibility(View.GONE);
        mSearchLocationText.setVisibility(View.GONE);
        mSearchPersonText.setVisibility(View.GONE);
        mCombineSearchText.setVisibility(View.GONE);
        // make the boolean variable as false, as all the
        // action name texts and all the sub FABs are
        // invisible
        isAllFabsVisible = false;

        // Set the Extended floating action button to
        // shrinked state initially
        mSearchFab.shrink();

        // We will make all the FABs and action name texts
        // visible only when Parent FAB button is clicked So
        // we have to handle the Parent FAB button first, by
        // using setOnClickListener you can see below
        mSearchFab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PhotosAlbumState.lastButton = view;
                        currentState.processEvent();
//                        if (!isAllFabsVisible) {
//
//                            // when isAllFabsVisible becomes
//                            // true make all the action name
//                            // texts and FABs VISIBLE.
//                            mSearchLocation.show();
//                            mSearchPerson.show();
//                            mCombineSearch.show();
//                            mSearchLocationText.setVisibility(View.VISIBLE);
//                            mSearchPersonText.setVisibility(View.VISIBLE);
//                            mCombineSearchText.setVisibility(View.VISIBLE);
//                            // Now extend the parent FAB, as
//                            // user clicks on the shrinked
//                            // parent FAB
//                            mSearchFab.extend();
//
//                            // make the boolean variable true as
//                            // we have set the sub FABs
//                            // visibility to GONE
//                            isAllFabsVisible = true;
//                        } else {
//
//                            // when isAllFabsVisible becomes
//                            // true make all the action name
//                            // texts and FABs GONE.
//                            mSearchLocation.hide();
//                            mSearchPerson.hide();
//                            mCombineSearch.hide();
//                            mSearchLocationText.setVisibility(View.GONE);
//                            mSearchPersonText.setVisibility(View.GONE);
//                            mCombineSearchText.setVisibility(View.GONE);
//                            // Set the FAB to shrink after user
//                            // closes all the sub FABs
//                            mSearchFab.shrink();
//
//                            // make the boolean variable false
//                            // as we have set the sub FABs
//                            // visibility to GONE
//                            isAllFabsVisible = false;
//                        }
                    }
                });

        mCombineSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotosAlbumState.lastButton = view;
                currentState.processEvent();
//                PhotosAlbumState.lastButton = view;
//                currentState.processEvent();
//                // Create an AlertDialog Builder
//                AlertDialog.Builder builder = new AlertDialog.Builder(Albums.this);
//                builder.setTitle("Enter Name and Location");
//
//                // Create LinearLayout to hold EditTexts
//                LinearLayout layout = new LinearLayout(Albums.this);
//                layout.setOrientation(LinearLayout.VERTICAL);
//
//                LinearLayout layoutText1 = new LinearLayout(Albums.this);
//                layoutText1.setOrientation(LinearLayout.HORIZONTAL);
//
//                Spinner spinner1 = new Spinner(Albums.this);
//                ArrayList<String> spinnerItems = new ArrayList<>();
//                spinnerItems.add("Item 1");
//                spinnerItems.add("Item 2");
//                spinnerItems.add("Item 3");
//// Add more items as needed
//                ArrayAdapter<String> spinner1Adapter = new ArrayAdapter<>(Albums.this, android.R.layout.simple_spinner_item, spinnerItems);
//                spinner1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                spinner1.setAdapter(spinner1Adapter);
//                layoutText1.addView(spinner1);
//
//                // Create EditText for name
//                final AutoCompleteTextView nameInput = new AutoCompleteTextView(Albums.this);
//                nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
//                nameInput.setHint("Name");
//                layoutText1.addView(nameInput);
//                layout.addView(layoutText1);
//                nameInput.setMinWidth(400);
//                nameInput.setMaxWidth(400);
//
////                String[] suggestionsName = {"hello world"};
//                ArrayList<String> suggestionsName = Photos.dictionaryGetAllkey("person");
//                ArrayAdapter<String> adapterName = new ArrayAdapter<>(Albums.this, android.R.layout.simple_dropdown_item_1line, suggestionsName);
//                nameInput.setAdapter(adapterName);
//
//                LinearLayout layoutText2 = new LinearLayout(Albums.this);
//                layoutText2.setOrientation(LinearLayout.HORIZONTAL);
//
//                Spinner spinner2 = new Spinner(Albums.this);
//                ArrayAdapter<String> spinner2Adapter = new ArrayAdapter<>(Albums.this, android.R.layout.simple_spinner_item, spinnerItems);
//                spinner2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                spinner2.setAdapter(spinner2Adapter);
//                layoutText2.addView(spinner2);
//
//                // Create EditText for location
//                final AutoCompleteTextView locationInput = new AutoCompleteTextView(Albums.this);
//                locationInput.setInputType(InputType.TYPE_CLASS_TEXT);
//                locationInput.setHint("Location");
//                layoutText2.addView(locationInput);
//                layout.addView(layoutText2);
//                locationInput.setMinWidth(400);
//                locationInput.setMaxWidth(400);
//
//
////                String[] suggestionsLocation = {"hello world"};
//                ArrayList<String> suggestionsLocation = Photos.dictionaryGetAllkey("location");
//                ArrayAdapter<String> adapterLocation = new ArrayAdapter<>(Albums.this, android.R.layout.simple_dropdown_item_1line, suggestionsLocation);
//                locationInput.setAdapter(adapterLocation);
//
//
//                // Create radio buttons for search operator
//                RadioGroup radioGroup = new RadioGroup(Albums.this);
//                radioGroup.setOrientation(RadioGroup.HORIZONTAL);
//
//                RadioButton radioButtonAnd = new RadioButton(Albums.this);
//                radioButtonAnd.setText("AND");
//                radioGroup.addView(radioButtonAnd);
//
//                RadioButton radioButtonOr = new RadioButton(Albums.this);
//                radioButtonOr.setText("OR");
//                radioGroup.addView(radioButtonOr);
//
//                // Set default selection to "AND"
//                radioButtonAnd.setChecked(true);
//
//                layout.addView(radioGroup);
//
//                builder.setView(layout);
//
//                // Set up the buttons
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String name = nameInput.getText().toString().trim();
//                        String location = locationInput.getText().toString().trim();
//                        boolean useAndOperator = radioButtonAnd.isChecked();
//
//                        Album searchResult = new Album(null, "Search Result");
//                        searchResult.setPhotos(
//                                useAndOperator ? Photos.dictionarySearchByTagAnd("person",name, "location",location)
//                                        : Photos.dictionarySearchByTagOr("person",name, "location",location)
//                        );
//
//                        Photos.setCurrAlbum(searchResult);
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("album", searchResult);
//                        Intent intent = new Intent(Albums.this, ShowSearch.class);
//                        intent.putExtras(bundle);
//                        Albums.this.startActivity(intent);
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//
//                // Create and show the dialog
//                AlertDialog dialog = builder.create();
//                dialog.show();
            }
        });

        // below is the sample action to handle add person
        // FAB. Here it shows simple Toast msg. The Toast
        // will be shown only when they are visible and only
        // when user clicks on them
        mSearchPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotosAlbumState.lastButton = view;
                currentState.processEvent();
//                // Create an AlertDialog Builder
//                AlertDialog.Builder builder = new AlertDialog.Builder(Albums.this);
//                builder.setTitle("Enter Name");
//
//                // Create EditText to input the name
//                final AutoCompleteTextView input = new AutoCompleteTextView(Albums.this);
//                input.setInputType(InputType.TYPE_CLASS_TEXT);
//                builder.setView(input);
//
////                String[] suggestions = {"hello world"};
//                ArrayList<String> suggestions = Photos.dictionaryGetAllkey("person");
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(Albums.this, android.R.layout.simple_dropdown_item_1line, suggestions);
//                input.setAdapter(adapter);
//
//                // Set up the buttons
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String name = input.getText().toString().trim();
//                        Album searchResult = new Album(null, "Search Result");
//                        searchResult.setPhotos(Photos.dictionarySearchByTag("person", name));
//
//                        Photos.setCurrAlbum(searchResult);
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("album", searchResult);
//                        Intent intent = new Intent(Albums.this, ShowSearch.class);
//                        intent.putExtras(bundle);
//                        Albums.this.startActivity(intent);
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//
//                // Create and show the dialog
//                AlertDialog dialog = builder.create();
//                dialog.show();
            }
        });


        // below is the sample action to handle add alarm
        // FAB. Here it shows simple Toast msg The Toast
        // will be shown only when they are visible and only
        // when user clicks on them
        mSearchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotosAlbumState.lastButton = view;
                currentState.processEvent();
//                // Create an AlertDialog Builder
//                AlertDialog.Builder builder = new AlertDialog.Builder(Albums.this);
//                builder.setTitle("Enter Location");
//
//                // Create EditText to input the name
////                final EditText input = new EditText(Albums.this);
//                final AutoCompleteTextView input = new AutoCompleteTextView(Albums.this);
//                input.setInputType(InputType.TYPE_CLASS_TEXT);
//                builder.setView(input);
//
////                String[] suggestions = {"hello world"};
//                ArrayList<String> suggestions = Photos.dictionaryGetAllkey("location");
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(Albums.this, android.R.layout.simple_dropdown_item_1line, suggestions);
//                input.setAdapter(adapter);
//
//                // Set up the buttons
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String location = input.getText().toString().trim();
//                        Album searchResult = new Album(null, "Search Result");
//                        searchResult.setPhotos(Photos.dictionarySearchByTag("location", location));
//
//                        Photos.setCurrAlbum(searchResult);
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("album", searchResult);
//                        Intent intent = new Intent(Albums.this, ShowSearch.class);
//                        intent.putExtras(bundle);
//                        Albums.this.startActivity(intent);
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//
//                // Create and show the dialog
//                AlertDialog dialog = builder.create();
//                dialog.show();
            }
        });

    }
    public void toastMessage(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
    public void processEvent(View view) {
        PhotosState.lastButton = view;
        currentState = currentState.processEvent();
    }
}
