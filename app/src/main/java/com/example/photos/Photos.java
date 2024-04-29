package com.example.photos;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Optional;
import java.util.stream.Collectors;

public class Photos implements Serializable {
    public static Photos photosApp;
    private static final String storeFile = "photos.dat";

    private static Album currAlbum;
    private ArrayList<Album> albumsList;
    private ArrayList<Tag> tagList;
    private Hashtable<String,Hashtable<String, Tag>> tagDictionary;

    private Photos(){
        this.albumsList = new ArrayList<Album>();
        this.tagDictionary = new Hashtable<String,Hashtable<String, Tag>>();
        this.tagDictionary.put("person", new Hashtable<String, Tag>());
        this.tagDictionary.put("location", new Hashtable<String, Tag>());
    }
    public static void setCurrAlbum(Album album){
        currAlbum = album;
    }
    public static Album getCurrAlbum(){
        return currAlbum;
    }
    public static void dictionaryAdd(Tag tag){
        if (!photosApp.tagDictionary.containsKey(tag.getTagName())){
            photosApp.tagDictionary.put(tag.getTagName(), new Hashtable<String, Tag>());
        }
        Hashtable<String, Tag> tags = photosApp.tagDictionary.get(tag.getTagName());
        tags.put(tag.getTagValue(), tag);
    }
    public static Tag dictionaryGetTag(String tagName, String tagValue){
        if (!photosApp. tagDictionary.containsKey(tagName)){
            return null;
        }
        Hashtable<String, Tag> tags = photosApp.tagDictionary.get(tagName);
        return tags.get(tagValue);
    }
    public static boolean dictionaryContainsTag(String tagName, String tagValue){
        if (!photosApp. tagDictionary.containsKey(tagName)){
            return false;
        }
        Hashtable<String, Tag> tags = photosApp.tagDictionary.get(tagName);
        return tags.containsKey(tagValue);
    }
    public static boolean dictionaryContainsTag(Tag tag){
        if (!photosApp.tagDictionary.containsKey(tag.getTagName())){
            return false;
        }
        Hashtable<String, Tag> tags = photosApp.tagDictionary.get(tag.getTagName());
        return tags.containsKey(tag.getTagValue());
    }
    public static ArrayList<String> dictionaryGetAllTagName(){
        ArrayList<String> tagNames =new ArrayList<>(photosApp.tagDictionary.keySet());
        tagNames.sort(Collections.reverseOrder());

        return tagNames;
    }
    public static ArrayList<String> dictionaryGetAllkey(String tagName){
        if (!photosApp.tagDictionary.containsKey(tagName)){
            return null;
        }
        return new ArrayList<>(photosApp.tagDictionary.get(tagName).keySet());
    }
    public static void dictionaryRemoveTag(Tag tag){
        Hashtable<String, Tag> tags = photosApp.tagDictionary.get(tag.getTagName());
        tags.remove(tag.getTagValue());
    }
    public static ArrayList<Photo> dictionarySearchByTag(String tagName, String tagValue){
        if (!dictionaryContainsTag(tagName,tagValue)){
            return null;
        }
        return dictionaryGetTag(tagName,tagValue).getTaggedPhoto();
    }

    public static ArrayList<Photo> dictionarySearchByTagAnd(String tag1Name, String tag1Value, String tag2Name, String tag2Value){
        if (!dictionaryContainsTag(tag1Name,tag1Value) || !dictionaryContainsTag(tag2Name,tag2Value)){
            return null;
        }
        ArrayList<Photo> picturesInTag2 = dictionaryGetTag(tag2Name,tag2Value).getTaggedPhoto();
        return dictionaryGetTag(tag1Name,tag1Value).getTaggedPhoto().stream().filter(
                picturesInTag2::contains
        ).collect(Collectors.toCollection(ArrayList::new));
    }

    public static ArrayList<Photo> dictionarySearchByTagOr(String tag1Name, String tag1Value, String tag2Name, String tag2Value){
        if (!dictionaryContainsTag(tag1Name,tag1Value) && !dictionaryContainsTag(tag2Name,tag2Value)){
            return null;
        }else if(!dictionaryContainsTag(tag1Name,tag1Value)){
            return dictionaryGetTag(tag2Name,tag2Value).getTaggedPhoto();
        }else if(!dictionaryContainsTag(tag2Name,tag2Value)){
            return dictionaryGetTag(tag1Name,tag1Value).getTaggedPhoto();
        }
        ArrayList<Photo> picturesInTag1 = dictionaryGetTag(tag1Name,tag1Value).getTaggedPhoto();
        ArrayList<Photo> picturesInTag2 = dictionaryGetTag(tag2Name,tag2Value).getTaggedPhoto();

        ArrayList<Photo> picturesNotInTag2 = picturesInTag1.stream().filter(
                photo -> !picturesInTag2.contains(photo)
        ).collect(Collectors.toCollection(ArrayList::new));

        picturesNotInTag2.addAll(picturesInTag2);

        return picturesNotInTag2;
    }
    public static boolean albumsValidateName(String name){
        return photosApp.albumsList.stream().noneMatch(album -> album.getName().equals(name));
    }
    public static Album albumsGetAlbum(String name){
        Optional<Album> optionalAlbum = photosApp.albumsList.stream().filter(album -> album.getName().equals(name)).findAny();
        if (optionalAlbum.isPresent()){
            return optionalAlbum.get();
        }
        return null;
    }
    public static ArrayList<String> albumsGetAlbumListStr(){
        return photosApp.albumsList.stream().map(
                Album::getName
        ).collect(Collectors.toCollection(ArrayList::new));
    }
    public static ArrayList<Album> albumsGetAlbums(){
        return photosApp.albumsList;
    }

    public static ArrayList<Photo> movePhoto(ArrayList<Photo> photosToMove, ArrayList<Photo> collect, String albumName){
        Album target = albumsGetAlbum(albumName);
        ArrayList<Photo> duplicates = new ArrayList<Photo>();
        for (Photo photo : photosToMove){
            boolean anyMatch = target.getPhotos().stream().anyMatch(x-> photo.getUri().equals(x.getUri()));
            if (anyMatch){
                duplicates.add(photo);
            }else{
                target.getPhotos().add(photo);
                collect.add(photo);
            }
        }
        return duplicates;
    }
    public static Photo movePhotoOverWrite(Photo photo,String albumName){
        Album target = albumsGetAlbum(albumName);
        Photo photoRemove = target.getPhotos().stream().filter(x-> photo.getUri().equals(x.getUri())).collect(Collectors.toList()).get(0);
        target.getPhotos().remove(photoRemove);
        target.getPhotos().add(photo);
        return photoRemove;
    }
    public static void readApp(Context ctxt){
        try {
            FileInputStream fis = ctxt.openFileInput(storeFile);
            ObjectInputStream ois = new ObjectInputStream(fis);

            photosApp = (Photos)ois.readObject();

            ois.close();
            fis.close();
        } catch(FileNotFoundException e) {
            photosApp = new Photos();

            writeAlbums(ctxt);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void writeAlbums(Context ctxt){
        try {
            FileOutputStream fos = ctxt.openFileOutput(storeFile, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(photosApp);

            oos.close();
            fos.close();
        } catch (IOException e) {

            e.printStackTrace();

        }
    }
}
