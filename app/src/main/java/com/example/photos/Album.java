package com.example.photos;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Pair;
import android.widget.Toast;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import kotlin.TuplesKt;

public class Album implements Serializable {
    private Bitmap image;
    private String name;


    private ArrayList<Photo> photos;
//    private static ArrayList<Album> albumsList;
//    private static final String storeFile = "albums.dat";

//    public static ArrayList<Album> getAlbumsList() {
//        return albumsList;
//    }
    public Album(Bitmap image, String name) {
        this.image = image;
        this.name = name;
        this.photos = new ArrayList<Photo>();
    }
    public ArrayList<Photo> getPhotos() {
        return photos;
    }
    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }
    public int addPhotos(List<Uri> uris){
        List<Photo> add = uris.stream().filter(
                u -> photos.stream().filter(
                        p -> p.getUri().equals(u)
                ).collect(Collectors.toList()).size()==0
        ).collect(Collectors.toList()).stream().map(Photo::new).collect(Collectors.toList());
        photos.addAll(add);
        return add.size();
    }
    public void removeAllPhoto(ArrayList<Photo> photosToRemove){
        photosToRemove.forEach(Album.this::removePhoto);
        this.photos.removeAll(photosToRemove);
    }
    public void removePhoto(Photo photoToRemove){
        photoToRemove.getPeople().forEach(photoToRemove::removePerson);
        photoToRemove.getLocations().forEach(photoToRemove::removeLocation);
        this.photos.remove(photoToRemove);
    }

    private static <T, U> List<Pair<T, U>> zip(List<T> list1, List<U> list2) {
        List<Pair<T, U>> zippedList = new ArrayList<>();
        int size = Math.min(list1.size(), list2.size());
        for (int i = 0; i < size; i++) {
            zippedList.add(new Pair<>(list1.get(i), list2.get(i)));
        }
        return zippedList;
    }

//    public static boolean albumExists(Album album){
//        for (Album album1: albumsList){
//            if(album.equals(album1)){
//                return true;
//            }
//        }
//        return false;
//    }
//    public static ArrayList<Photo> movePhoto(ArrayList<Photo> photosToMove, ArrayList<Photo> collect, String albumName){
//        Album target = getAlbum(albumName);
//        ArrayList<Photo> duplicates = new ArrayList<Photo>();
//        for (Photo photo : photosToMove){
//            if (target.getPhotos().stream().filter(x-> photo.getUri().equals(x.getUri())).collect(Collectors.toList()).size()==1){
//                duplicates.add(photo);
//            }else{
//                target.getPhotos().add(photo);
//                collect.add(photo);
//            }
//        }
//        return duplicates;
//    }
//    public static Photo movePhotoOverWrite(Photo photo,String albumName){
//        Album target = getAlbum(albumName);
//        Photo photoRemove = target.getPhotos().stream().filter(x-> photo.getUri().equals(x.getUri())).collect(Collectors.toList()).get(0);
//        target.getPhotos().remove(photoRemove);
//        target.getPhotos().add(photo);
//        return photoRemove;
//    }
//    public static ArrayList<String> getAlbumListStr(){
//        ArrayList<String> arr = new ArrayList<String>();
//        for (Album album : albumsList){
//            arr.add(album.getName());
//        }
//        return arr;
//    }
//    public static Album getAlbum(String name){
//        for (Album album : albumsList){
//            if(album.getName().equals(name)){
//                return album;
//            }
//        }
//        return null;
//    }
//    public static boolean validateName(String name){
//        for (Album album : albumsList){
//            if(album.getName().equals(name)){
//                return false;
//            }
//        }
//        return true;
//    }
//    public static void writeAlbums(ArrayList<Album> albums, Context ctxt){
//        try {
//            FileOutputStream fos = ctxt.openFileOutput(storeFile, Context.MODE_PRIVATE);
//            ObjectOutputStream oos = new ObjectOutputStream(fos);
//            oos.writeObject(albums);
//            oos.close();
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void writeAlbums(Context ctxt){
//        try {
//            FileOutputStream fos = ctxt.openFileOutput(storeFile, Context.MODE_PRIVATE);
//            ObjectOutputStream oos = new ObjectOutputStream(fos);
//            oos.writeObject(albumsList);
//            oos.close();
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static ArrayList<Album> readAlbums(Context ctxt){
//        ArrayList<Album> albums = null;
//        try {
//            FileInputStream fis = ctxt.openFileInput(storeFile);
//            ObjectInputStream ois = new ObjectInputStream(fis);
//            albums = (ArrayList<Album>)ois.readObject();
//            ois.close();
//            fis.close();
//        } catch(FileNotFoundException e) {
//            albums = new ArrayList<Album>();
//            writeAlbums(albums,ctxt);
//        } catch (Exception e){
//            System.exit(1);
//        } finally {
//            albumsList = albums;
//            return albums;
//        }
//    }


    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

