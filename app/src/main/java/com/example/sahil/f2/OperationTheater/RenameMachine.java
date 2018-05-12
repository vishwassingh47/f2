package com.example.sahil.f2.OperationTheater;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sahil.f2.Classes.MyFile;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.R;
import com.example.sahil.f2.Rooted.SuperUser;
import com.example.sahil.f2.StorageAccessFramework;
import com.example.sahil.f2.UiClasses.Refresher;
import com.example.sahil.f2.Utilities.RenameUtils;

import org.apache.ftpserver.command.impl.HELP;

import java.io.File;

/**
 * Created by hit4man47 on 12/31/2017.
 */

public class RenameMachine
{

    private Dialog dialog;
    private EditText editName,editExtension;
    private Button cancel,ok;
    private TextView title,extensionLabel;


    private boolean isSaf=false;
    private boolean isRoot;
    private final int storageId;
    private final  MainActivity mainActivityObject;
    private final MyFile myFile;

    public RenameMachine(MainActivity mainActivityObject,MyFile myFile,int storageId)
    {
        this.mainActivityObject=mainActivityObject;
        this.myFile=myFile;
        this.storageId=storageId;
    }

    public void setUpRename()
    {
        if(!isStorageOk())
        {
            return;
        }

        dialog = new Dialog(mainActivityObject);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layoutof_rename);
        dialog.setCanceledOnTouchOutside(false);

        editName=(EditText)dialog.findViewById(R.id.rename_edittext1);
        editExtension =(EditText)dialog.findViewById(R.id.rename_edittext2);
        cancel=(Button)dialog.findViewById(R.id.rename_cancel);
        ok=(Button)dialog.findViewById(R.id.rename_ok);
        title=(TextView) dialog.findViewById(R.id.renameTitle);
        extensionLabel=(TextView) dialog.findViewById(R.id.extension_label);

         /*
        name can be of these order:
        1   me.txt
        2   .txt
        3   me.
        4   me
         */

        title.setText("RENAME");
        if(myFile.isFolder())
        {
            editName.setText(myFile.getName());
            editName.setSelection(editName.getText().length());
            editExtension.setVisibility(View.GONE);
            extensionLabel.setVisibility(View.GONE);
        }
        else
        {
            editExtension.setVisibility(View.VISIBLE);
            extensionLabel.setVisibility(View.VISIBLE);
            //is a file
            final String extension=HelpingBot.getExtension(myFile.getName());
            final String initialName= HelpingBot.getInitialName(myFile.getName());

            editName.setText(initialName);
            editName.setSelection(editName.getText().length());
            editExtension.setText(extension);
            editExtension.setSelection(editExtension.getText().length());
        }


        ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                doRename();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.cancel();
            }
        });


        dialog.show();
    }

    private boolean isStorageOk()
    {
        isSaf=false;
        isRoot=false;

        if(storageId<=3)
        {
            String storageHomePath=PagerXUtilities.getLocalHomeStoragePath(myFile.getPath());
            boolean rootOperation=storageHomePath==null;
            if(rootOperation)
            {
                if(!SuperUser.hasUserEnabledSU)
                {
                    //if not rooted
                    Toast.makeText(mainActivityObject, "Root Access Required", Toast.LENGTH_SHORT).show();
                    return false;
                }
                else
                {
                    //root
                    isRoot=true;
                    return true;
                }
            }
            else
            {
                File file=new File(storageHomePath);
                if(!file.canWrite())
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    {
                        isSaf=true;

                        String [] breaker=storageHomePath.split("\\/");
                        String storageName=breaker[breaker.length-1];
                        if(MainActivity.SDCardUriMap.get(storageName)==null)
                        {
                            StorageAccessFramework storageAccessFramework=new StorageAccessFramework(mainActivityObject);
                            storageAccessFramework.showSaf(3,storageName);
                            return false;
                        }
                    }
                    else
                    {
                        Toast.makeText(mainActivityObject, "Error:This directory is not Writable", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                else
                {
                    return true;
                }
            }
        }
        return true;
    }

    private void doRename()
    {
        String newName;
        final String parentPath=HelpingBot.getParentPath(myFile.getPath());

        if(myFile.isFolder())
        {
            newName=editName.getText().toString();
        }
        else
        {
            final String extension=editExtension.getText().toString();
            final String initialName=editName.getText().toString();

            newName=initialName+extension;
            if(extension.length()>0 && !extension.startsWith("."))
            {
                Toast.makeText(mainActivityObject, "Extensions should start with '.'", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if(newName.equals(myFile.getName()))
        {
            Toast.makeText(mainActivityObject, "Success", Toast.LENGTH_SHORT).show();
            dialog.cancel();
            return;
        }
        if(newName.length()==0)
        {
            Toast.makeText(mainActivityObject, "Name Too Short", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] illegal={"\"","\\","/","*",":","?","|","<",">"};
        for(String s:illegal)
        {
            if(newName.contains(s))
            {
                Toast.makeText(mainActivityObject, "\" / \\ < > ? | : * are not allowed in file name", Toast.LENGTH_LONG).show();
                return;
            }
        }


        if(storageId<=3)
        {
            String newPath=slashAppender(parentPath,newName);
            File newFile=new File(newPath);
            if(newFile.exists())
            {
                Toast.makeText(mainActivityObject, "Item with same name already exists", Toast.LENGTH_LONG).show();
                return;
            }
        }


        class MyAsyncTask extends AsyncTask<String, Integer, String>
        {

            private final String newName,parentPath;
            private final RenameUtils renameUtils;

            private MyAsyncTask(String newName,String parentPath)
            {
                this.newName=newName;
                this.parentPath=parentPath;
                renameUtils=new RenameUtils(mainActivityObject);
            }

            private ProgressDialog pd;

            protected void onPreExecute()
            {
                super.onPreExecute();
                pd=new ProgressDialog(mainActivityObject);
                pd.setTitle("Rename in Progress");
                pd.setMessage(newName);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setIndeterminate(true);
                pd.setCancelable(true);
                pd.show();
            }

            protected String doInBackground(String... arg0)
            {
                boolean x=false;
                if(storageId<=3)
                {
                    if(isSaf)
                    {
                        x=renameUtils.renameInSaf(myFile,parentPath,newName,false);
                    }
                    else
                    {
                        if(isRoot)
                        {
                            //root
                            x=renameUtils.renameInRoot(myFile,parentPath,newName,false);
                        }
                        else
                        {
                            x=renameUtils.renameInInternal(myFile,parentPath,newName,false);
                        }
                    }
                }
                if(storageId==4)
                {
                    x=renameUtils.renameInDrive(myFile,newName);
                }
                if(storageId==5)
                {
                    x=renameUtils.renameInDropBox(myFile,parentPath,newName,true);
                }
                if(storageId==6)
                {
                    x=renameUtils.renameInFtp(myFile,parentPath,newName);
                }
                if(x)
                {
                    return "all done";
                }
                else
                {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String toPath)
            {
                if(toPath==null)
                {
                    Toast.makeText(mainActivityObject, "Rename Failed", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(mainActivityObject, "Success", Toast.LENGTH_SHORT).show();
                    Refresher refresher =new Refresher(mainActivityObject);
                    refresher.refresh();
                }
                pd.cancel();
                //refresh
            }

        }

        dialog.cancel();
        MyAsyncTask myAsyncTask=new MyAsyncTask(newName,parentPath);
        myAsyncTask.execute();

    }


    @NonNull
    private String slashAppender(String a, String b)
    {
        if(a.endsWith("/"))
            return a+b;
        else
            return a+"/"+b;
    }

}
