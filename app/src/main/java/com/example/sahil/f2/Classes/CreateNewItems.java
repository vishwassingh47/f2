package com.example.sahil.f2.Classes;

/**
 * Created by hit4man47 on 1/19/2018.
 */

public class CreateNewItems
{
    final private String name,pageName,firstPath;
    final private int iconId,itemId;

    public CreateNewItems(String name,String pageName,String firstPath,int iconId,int itemId)
    {
        this.name=name;
        this.pageName=pageName;
        this.firstPath=firstPath;
        this.itemId=itemId;
        this.iconId=iconId;
    }

    public String getName() {
        return name;
    }

    public String getPageName() {
        return pageName;
    }

    public String getFirstPath() {
        return firstPath;
    }

    public int getIconId() {
        return iconId;
    }

    public int getItemId() {
        return itemId;
    }
}
