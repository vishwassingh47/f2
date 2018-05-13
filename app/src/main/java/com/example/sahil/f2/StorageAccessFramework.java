package com.example.sahil.f2;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.v4.provider.DocumentFile;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.sahil.f2.OperationTheater.PagerXUtilities;

import java.io.File;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.example.sahil.f2.MainActivity.SDCardUriMap;
import static com.example.sahil.f2.MainActivity.Physical_Storage_PATHS;

/**
 * Created by sahil on 25-07-2017.
 */

public class StorageAccessFramework
{
     private static int whyDocumentTreeIntent;
     private static String sdCardPathForPostSafIntent;

    private final MainActivity mainActivity;

    public StorageAccessFramework(Activity activity)
    {
        mainActivity=(MainActivity) activity;
    }

    void refreshSDCardUri()
    {
        SDCardUriMap=new HashMap<>();
        SharedPreferences pref=mainActivity.getApplicationContext().getSharedPreferences("SDCardPref",0);

        for(int i=1;i<Physical_Storage_PATHS.size();i++)
        {
            if(pref.getString(Physical_Storage_PATHS.get(i),null)==null)
            {
                //no uri stoed in sharedPref
            }
            else
            {
                Uri uri=Uri.parse(pref.getString(Physical_Storage_PATHS.get(i),null));
                if(uri!=null)
                {
                    SDCardUriMap.put(Physical_Storage_PATHS.get(i),uri);
                }
            }
        }

    }

    void postSafIntent(int resultCode,Intent resultIntent)
    {
        if(resultCode==RESULT_OK)//SOME PATH IS SELECTED
        {
            Uri treeUri=resultIntent.getData();

            if(treeUri==null)
            {
                Toast.makeText(mainActivity, "FAILED TO GET SDCARD PATH", Toast.LENGTH_LONG).show();
                return;
            }
            else
            {
                String sdCardName=treeUri.getPath();////   /tree/026D-10CF:DCIM  or /tree/026D-10CF:

                if(sdCardName.endsWith(":"))
                {
                    int lastIndexOfSlash=sdCardName.lastIndexOf('/');
                    try
                    {
                        sdCardName=sdCardName.substring(lastIndexOfSlash+1,sdCardName.length()-1);
                        if(!sdCardPathForPostSafIntent.endsWith(sdCardName))
                        {
                            sdCardPathForPostSafIntent=null;
                        }
                    }
                    catch (Exception e)
                    {
                        sdCardPathForPostSafIntent=null;
                    }
                }
                else
                {
                    sdCardPathForPostSafIntent=null;
                }

                if(sdCardPathForPostSafIntent==null)
                {
                    Toast.makeText(mainActivity, "INVALID PATH SELECTED,PLEASE SELECT ROOT PATH", Toast.LENGTH_LONG).show();
                }
                else
                {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)   //ALWAYS TRUE
                    {
                        mainActivity.getContentResolver().takePersistableUriPermission(treeUri,Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }

                    SharedPreferences pref=mainActivity.getApplicationContext().getSharedPreferences("SDCardPref",0);
                    SharedPreferences.Editor editor=pref.edit();
                    editor.putString(sdCardPathForPostSafIntent,treeUri.toString());    //  content://com.android.externalstorage.documents/tree/026D-10CF%3ADCIM
                    editor.apply();

                    refreshSDCardUri();

                    Toast.makeText(mainActivity, "Permission Granted Succesfully", Toast.LENGTH_SHORT).show();
                /*
                WHAT DO YOU WANT TO DO NEXT

                ***SDCARD1****
                1.from CUT AND TO PASTE

                3.DELETE
                4.Rename
                5.CREATE


                ***SDCARD2****
                6.COPYING
                7.CUT
                8.DELETE
                9.Rename
                10.CREATE

                 */

                    if(whyDocumentTreeIntent==1)
                    {
                        mainActivity.pasteButtonDecisionMaker.pasteButton.performClick();
                    }
                    if(whyDocumentTreeIntent==3)
                    {

                    }
                    if(whyDocumentTreeIntent==4)
                    {
                        //do nothing user hsave to select files again to RENAME
                    }
                    if(whyDocumentTreeIntent==5 || whyDocumentTreeIntent==10)
                    {
                        //CreateNew x=new CreateNew();
                        //x.start();
                    }

                }

            }
        }
        else//NO PATH SELECTED
        {
            Toast.makeText(mainActivity, "ERROR: NO PATH SELECTED", Toast.LENGTH_SHORT).show();
        }

    }

    public void showSaf(final int whatToDo, final String storagePath)
    {
        final Dialog dialog = new Dialog(mainActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layoutof_saf);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Button ok=(Button)dialog.findViewById(R.id.saf_ok);
        ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.cancel();
                whyDocumentTreeIntent=whatToDo;
                sdCardPathForPostSafIntent=storagePath;

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)   //ALWAYS TRUE
                {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    mainActivity.startActivityForResult(intent, 42);
                }
                else
                {
                    Toast.makeText(mainActivity, "Sorry SAF failed on this device", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Button cancel=(Button)dialog.findViewById(R.id.saf_cancel);
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.cancel();
                Toast.makeText(mainActivity, "Permission denied by user", Toast.LENGTH_LONG).show();
            }
        });

    }


    public static DocumentFile fileToDocumentFileConverter(String path, boolean isDirectory,Context context)
    {

        DocumentFile papa=null;
        DocumentFile docfile = null;

        final String storagePath= PagerXUtilities.getExternalSdCardPath(path);

        try             //TRYING TO CATCH INVALID URI
        {
            papa = DocumentFile.fromTreeUri(context,SDCardUriMap.get(storagePath));
        }
        catch (Exception e)
        {
            if(SDCardUriMap.containsKey(storagePath))
            {
                SDCardUriMap.remove(storagePath);
            }
            return null;
        }


        String relativePath = path.substring(storagePath.length() + 1);//_------Whatapp/media/file
        String[] smallPaths = relativePath.split("\\/");//-----[0]=Whatsapp,[1]=media,[2]=file

        Log.w("SAF 3...",".........");
        for (int i = 0; i < smallPaths.length; i++)
        {

            if (papa == null)
            {
                if(SDCardUriMap.containsKey(storagePath))
                {
                    SDCardUriMap.remove(storagePath);
                }

                return null;
            }
            docfile = papa.findFile(smallPaths[i]);

            if (docfile == null)
            {
                if ((i < smallPaths.length - 1) || isDirectory)
                {
                    docfile = papa.createDirectory(smallPaths[i]);
                }
                else
                {
                    docfile = papa.createFile(null, smallPaths[i]);
                }
            }
            papa = docfile;

        }
        Log.w("SAF 4...",".........");
        return docfile;

    }


}
