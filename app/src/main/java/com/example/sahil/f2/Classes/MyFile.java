package com.example.sahil.f2.Classes;

import com.example.sahil.f2.Cache.variablesCache;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 12/13/2017.
 */

public class MyFile
{
    private String path,size,name,fileId,thumbUrl;
    private boolean symLink=false;
    private long sizeLong,lastModified;
    private String lastModifiedDate;
    private MyApp myApp=null;
    private String permission=null;


    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }


    public MyApp getMyApp() {
        return myApp;
    }

    public void setMyApp(MyApp myApp) {
        this.myApp = myApp;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    private boolean isFolder,isChecked,isFavourite;

    //only for imageGalleryFrag1 adapter

    public int getContainerItems()
    {
        return containerItems;
    }

    public void setContainerItems(int containerItems) {
        this.containerItems = containerItems;
    }

    private int containerItems;

    public long getLastModified()
    {
        return lastModified;
    }

    public void setLastModified(long lastModified)
    {
        this.lastModified = lastModified;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getSymLink() {
        return symLink;
    }

    public void setSymLink(boolean symLink) {
        this.symLink = symLink;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public long getSizeLong() {
        return sizeLong;
    }

    public void setSizeLong(long sizeLong) {
        this.sizeLong = sizeLong;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isFavourite()
    {
        return isFavourite;
    }

    public void setFavourite(boolean favourite)
    {
        isFavourite = favourite;
    }
}
