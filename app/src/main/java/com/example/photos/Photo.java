package com.example.photos;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Photo implements Serializable {
    private String uri;
    private ArrayList<Tag> people;
    private ArrayList<Tag> locations;

    public ArrayList<String> getPeople() {
        return people.stream().map(
                tag -> tag.getTagValue()
        ).collect(Collectors.toCollection(ArrayList::new));
    }
    public ArrayList<String> getLocations() {
        return locations.stream().map(
                tag -> tag.getTagValue()
        ).collect(Collectors.toCollection(ArrayList::new));
    }
    public Photo(Uri uri) {
        this.uri = uri.toString();
        this.people = new ArrayList<Tag>();
        this.locations = new ArrayList<Tag>();
    }
    public Uri getUri() {
        return Uri.parse(uri);
    }
    public void setUri(Uri uri) {
        this.uri = uri.toString();
    }
    public void removePerson(String person){
        Tag tag = Photos.dictionaryGetTag("person", person);
        tag.removeTaggedPhoto(this);
        this.people.remove(tag);
        if ( tag.numTaggedPhoto() == 0 ){
            Photos.dictionaryRemoveTag(tag);
        }
    }
    public void removeLocation(String location){
        Tag tag = Photos.dictionaryGetTag("location", location);
        tag.removeTaggedPhoto(this);
        this.locations.remove(tag);
        if ( tag.numTaggedPhoto() == 0 ){
            Photos.dictionaryRemoveTag(tag);
        }
    }

    public boolean addPerson(String person){
        if (!Photos.dictionaryContainsTag("person", person)){
            Photos.dictionaryAdd(new Tag("person", person));
        }
        Tag tag = Photos.dictionaryGetTag("person", person);
        if (tag.isTagged(this)){
            return false;
        }
        tag.addTaggedPhoto(this);
        this.people.add(tag);
        return true;
    }
    public boolean addLocation(String location){
        if (!Photos.dictionaryContainsTag("location", location)){
            Photos.dictionaryAdd(new Tag("location", location));
        }
        Tag tag = Photos.dictionaryGetTag("location", location);
        if (tag.isTagged(this)){
            return false;
        }
        tag.addTaggedPhoto(this);
        this.locations.add(tag);
        return true;
    }
}

