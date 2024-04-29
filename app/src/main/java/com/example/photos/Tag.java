package com.example.photos;

import java.io.Serializable;
import java.util.ArrayList;

public class Tag implements Serializable {
    String tagName;
    String tagValue;
    ArrayList<Photo> taggedPhoto;
    public Tag(String tagName, String tagValue){
        this.tagName = tagName;
        this.tagValue = tagValue;
        this.taggedPhoto = new ArrayList<Photo>();
    }
    public String getTagName() {
        return tagName;
    }
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
    public String getTagValue() {
        return tagValue;
    }
    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }
    public void addTaggedPhoto(Photo photo) {
        taggedPhoto.add(photo);
    }
    public ArrayList<Photo> getTaggedPhoto() {
        return taggedPhoto;
    }
    public boolean isTagged(Photo photo){
        return taggedPhoto.contains(photo);
    }
    public void removeTaggedPhoto(Photo photo){
        this.taggedPhoto.remove(photo);
    }
    public int numTaggedPhoto(){
        return this.taggedPhoto.size();
    }
}
