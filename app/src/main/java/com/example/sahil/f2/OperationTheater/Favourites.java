package com.example.sahil.f2.OperationTheater;

import android.util.Log;
import android.widget.Toast;

import com.example.sahil.f2.Cache.favouritesCache;
import com.example.sahil.f2.Classes.MyFile;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.UiClasses.Refresher;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 12/28/2017.
 */

public class Favourites
{
    public final MainActivity mainActivity;
    private final TinyDB tinyDB;
    private ArrayList<Integer> selectedIndexList;
    private ArrayList<MyFile> myFilesList;
    private int storageId;



    public Favourites(MainActivity mainActivity,int storageId, ArrayList<MyFile> myFilesList, ArrayList<Integer> selectedIndexList)
    {
        this.mainActivity=mainActivity;
        tinyDB=new TinyDB(mainActivity);
        this.selectedIndexList=selectedIndexList;
        this.myFilesList=myFilesList;
        this.storageId=storageId;
    }

    public void addToFavourites()
    {
        for(Integer item:selectedIndexList)
        {
            MyFile file=myFilesList.get(item);
            String path=file.getPath();
            if(!favouritesCache.favouritePaths.contains(path))
            {
                favouritesCache.favouritePaths.add(path);
                favouritesCache.favouriteStorageIdList.add(storageId);
            }
            file.setFavourite(true);
        }

        tinyDB.putListString("Favourites",favouritesCache.favouritePaths);
        tinyDB.putListInt("FavouritesIds",favouritesCache.favouriteStorageIdList);
        Toast.makeText(mainActivity, "Added to favourites", Toast.LENGTH_SHORT).show();

        checkIndexing();

        refresh();

    }

    public void removeFromFavourites()
    {
        for(Integer item:selectedIndexList)
        {
            MyFile file=myFilesList.get(item);
            String path=file.getPath();
            if(favouritesCache.favouritePaths.contains(path))
            {
                int index= favouritesCache.favouritePaths.indexOf(path);
                favouritesCache.favouritePaths.remove(index);
                favouritesCache.favouriteStorageIdList.remove(index);
            }
            file.setFavourite(false);
        }

        tinyDB.putListString("Favourites",favouritesCache.favouritePaths);
        tinyDB.putListInt("FavouritesIds",favouritesCache.favouriteStorageIdList);
        Toast.makeText(mainActivity, "Removed from favourites", Toast.LENGTH_SHORT).show();

        checkIndexing();

        refresh();
    }

    private void checkIndexing()
    {
        if(favouritesCache.favouritePaths.size()!=favouritesCache.favouriteStorageIdList.size())
        {
            Toast.makeText(mainActivity, "Error:Favourite list not properly indexed", Toast.LENGTH_SHORT).show();
            favouritesCache.favouritePaths.clear();
            favouritesCache.favouriteStorageIdList.clear();
            tinyDB.remove("Favourites");
            tinyDB.remove("FavouritesIds");
            Toast.makeText(mainActivity, "Favourite list cleared", Toast.LENGTH_SHORT).show();
        }
    }

    private void refresh()
    {
        Refresher refresher =new Refresher(mainActivity);
        refresher.refresh();
    }


}
//Favourites