package com.example.sahil.f2.Cache;

import android.app.Dialog;

import java.util.ArrayList;

/**
 * Created by hit4man47 on 8/20/2017.
 */

public class tasksCache
{
    public static ArrayList<String> tasksId;
    public static ArrayList<Dialog> tasksDialog;



    public static void removeTask(String id)
    {

        Dialog dialog=getDialog(id);

        if(tasksId.contains(id))
        {
            tasksId.remove(id);
            tasksDialog.remove(dialog);
        }

    }


    public static  void  clear()
    {
        tasksId=new ArrayList<>();
        tasksDialog=new ArrayList<>();
    }


    public static void addTask(String id)
    {
        Dialog dialog=getDialog(id);


        if(!tasksId.contains(id))
        {
            tasksId.add(id);
            tasksDialog.add(dialog);
        }
        else
        {
            //if already exist replace it
            int index=tasksId.indexOf(id);

            tasksId.set(index,id);
            tasksDialog.set(index,dialog);
        }
    }


    private static Dialog getDialog(String id)
    {
        int operationId=Integer.parseInt(id);

        if(operationId==99)
        {
            return MyCacheData.getInstallData(99).dialog;
        }
        if(operationId==199)
        {
            return MyCacheData.getUnInstallData(199).dialog;
        }

        if(operationId>=101 && operationId<=102)
        {
            return MyCacheData.getCopyDataFromCode(operationId).dialog;
        }
        if((operationId>=201 && operationId<=206) || operationId==305 || operationId==306)
        {
            return MyCacheData.getDownloadDataFromCode(operationId).dialog;
        }
        if(operationId>=1 && operationId<=2)
        {
            return  MyCacheData.getDeleteDataFromCode(operationId).dialog;
        }
        if(operationId>=301 && operationId<=304)
        {
            return  MyCacheData.getUploadDataFromCode(operationId).dialog;
        }

        return null;

    }


}


