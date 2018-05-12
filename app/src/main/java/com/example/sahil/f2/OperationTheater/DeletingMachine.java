package com.example.sahil.f2.OperationTheater;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.example.sahil.f2.Cache.DeleteData;
import com.example.sahil.f2.Cache.FtpCache;
import com.example.sahil.f2.Cache.GalleryData;
import com.example.sahil.f2.Cache.MyCacheData;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.Classes.MyContainer;
import com.example.sahil.f2.Classes.MyFile;
import com.example.sahil.f2.FunkyAdapters.DeleteListAdapter;
import com.example.sahil.f2.HomeServicesProvider.DeletingService1;
import com.example.sahil.f2.HomeServicesProvider.DeletingService2;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.R;
import com.example.sahil.f2.Rooted.SuperUser;
import com.example.sahil.f2.StorageAccessFramework;
import com.example.sahil.f2.UiClasses.Refresher;
import com.google.api.services.drive.model.FileList;

import org.apache.commons.net.ftp.FTPFile;

import static com.example.sahil.f2.MainActivity.Physical_Storage_PATHS;
import static com.example.sahil.f2.MainActivity.SDCardUriMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hit4man47 on 12/25/2017.
 */

public class DeletingMachine
{
    public final MainActivity mainActivity;
    private boolean temporaryDeletePossible;
    private boolean runRootCommand;
    private int storageId,pagerId;
    private ArrayList<Integer> selectedIndexList;
    private ArrayList<MyContainer> containerList;
    private ArrayList<MyFile> myFilesList;
    private HelpingBot helpingBot;
    private Runnable runnable;
    private TextView title,checkbox_details,totalSize,details;
    private Dialog dialog;
    private CheckBox checkBox;
    private ProgressBar progressBar;
    private ListView listView;
    private DeleteListAdapter deleteListAdapter;
    private Button delete_hide,cancel_delete;
    private final boolean fromRecycleBin;
    private GalleryData galleryData;
    private DeleteData deleteData;
    private Intent serviceIntent;
    private int operationId=0;


    public DeletingMachine(MainActivity mainActivity, int pagerId,boolean fromRecycleBin, int storageId, boolean runRootCommand, ArrayList<MyFile> myFilesList, ArrayList<MyContainer> containerList, ArrayList<Integer> selectedIndexList)
    {
        this.mainActivity=mainActivity;
        this.runRootCommand=runRootCommand;
        this.storageId=storageId;
        this.pagerId=pagerId;
        this.selectedIndexList=selectedIndexList;
        this.containerList=containerList;
        this.myFilesList=myFilesList;
        helpingBot=new HelpingBot();
        this.fromRecycleBin=fromRecycleBin;
    }

    public void decisionMaker()
    {
        deleteData=MyCacheData.getDeleteDataFromCode(1);
        if(!deleteData.isServiceRunning)
        {
            operationId=1;
            showDeleteDialog();
            return;
        }
        deleteData=MyCacheData.getDeleteDataFromCode(2);
        if(!deleteData.isServiceRunning)
        {
            operationId=2;
            showDeleteDialog();
            return;
        }
        Toast.makeText(mainActivity, "Server Busy", Toast.LENGTH_LONG).show();
    }

    private void showDeleteDialog()
    {
        temporaryDeletePossible=false;

        deleteData.clear();
        deleteData.fromStorageId=storageId;


        if(pagerId==3 || pagerId==4)
        {
            String pageName=MainActivity.pageList.get(mainActivity.viewPager.getCurrentItem()).getName();
            int galleryType=PagerXUtilities.getGalleryType(pageName);
            galleryData= MyCacheData.getGalleryFromCode(galleryType);
        }
        else
        {
            galleryData=null;
        }





        if(storageId<=3)
        {
            if(!checkLocalStorage())
            {
                deleteData.clear();
                return;
            }
        }
        if(storageId==4 || storageId==5)
        {
            //for dropBox and Google drive
            temporaryDeletePossible=true;
        }
        if(storageId==6)
        {
            temporaryDeletePossible=false;
        }

        if(fromRecycleBin)
        {
            temporaryDeletePossible=false;
        }

        getDeleteData();


        dialog = new Dialog(mainActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layoutof_delete1);
        dialog.setCanceledOnTouchOutside(false);
        title=(TextView) dialog.findViewById(R.id.delete1_title);
        totalSize=(TextView) dialog.findViewById(R.id.delete1_size);
        checkbox_details=(TextView) dialog.findViewById(R.id.delete1_checkbox_details);
        details=(TextView) dialog.findViewById(R.id.delete1_details);
        listView=(ListView) dialog.findViewById(R.id.delete1_list);
        checkBox=(CheckBox) dialog.findViewById(R.id.delete1_checkbox);
        delete_hide=(Button) dialog.findViewById(R.id.delete1_ok);
        cancel_delete=(Button) dialog.findViewById(R.id.delete1_cancel);
        progressBar=(ProgressBar) dialog.findViewById(R.id.delete1_progress);


        deleteListAdapter=new DeleteListAdapter(mainActivity,1,deleteData.namesList,deleteData.sizeLongList,operationId);
        totalSize.setText(helpingBot.sizeinwords(deleteData.totalSizeToDownload));
        deleteData.dialog=dialog;


        View view=deleteListAdapter.getView(0,null,listView);
        view.measure(0,0);
        if(deleteData.totalFiles>5)
        {
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(int) (5.8 *view.getMeasuredHeight()));
            listView.setLayoutParams(params);
        }

        listView.setAdapter(deleteListAdapter);

        checkbox_details.setVisibility(View.GONE);
        checkBoxListener();
        deleteHideButtonClickListener();
        cancelButtonClickListener();

        details.setVisibility(View.GONE);
        delete_hide.setText("DELETE");


        if(temporaryDeletePossible)
        {
            if(storageId==5)
            {
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(true);
                checkbox_details.setText("DropBox items cannot be deleted permanently (by this app).You can restore these items later from Menu->RecycleBin within 30 days of deletion.After 30 days of deletion,items will be permanently deleted if not restored");
                checkBox.setClickable(false);
            }
            else
            {
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(true);
            }
            //checkbox_details.setVisibility(View.VISIBLE);
        }
        else
        {
            checkbox_details.setVisibility(View.GONE);
            checkBox.setVisibility(View.GONE);
        }

        progressBar.setVisibility(View.VISIBLE);

        dialog.show();


        final Thread thread=new Thread()
        {
            @Override
            public void run()
            {
                sizeFetcherAndSetter();
            }
        };
        thread.start();
    }

    private void startDeleteRunner()
    {
        Log.e("DELETE DETAILS:",deleteData.shouldRecycle+"---"+deleteData.pathsList.get(0));
        if(operationId==1)
        {
            serviceIntent= new Intent(mainActivity, DeletingService1.class);
        }
        if(operationId==2)
        {
            serviceIntent= new Intent(mainActivity, DeletingService2.class);
        }

        final Handler handler=new Handler();
        runnable =new Runnable()
        {
            @Override
            public void run()
            {
                Log.e("1RUNNER",deleteData.timeInSec+"......................");
                deleteData.timeInSec++;

                deleteListAdapter.notifyDataSetChanged();
                listView.setSelection(deleteData.currentFileIndex-3);

                if(deleteData.isServiceRunning)
                {
                    if(progressBar.getVisibility()!=View.VISIBLE)
                    {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    handler.postDelayed(runnable,1000);
                }
                else
                {
                    if(pagerId==2 || pagerId==3|| pagerId==4)
                    {
                        refreshPager234();
                    }
                    int totalErrors=0;
                    for(boolean x:deleteData.isErrorList)
                    {
                        if(x)
                        {
                            totalErrors++;
                        }
                    }

                    progressBar.setVisibility(View.GONE);
                    details.setVisibility(View.VISIBLE);
                    if(deleteData.cancelDownloadingPlease)
                    {
                        details.setText("Deletion cancelled");
                        Toast.makeText(mainActivity, "Deletion cancelled", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(totalErrors==0)
                        {
                            details.setText("Deleted Successfully");
                            Toast.makeText(mainActivity, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String s="Deletion Complete with "+totalErrors+" error(s)";
                            details.setText(s);
                            Toast.makeText(mainActivity, s, Toast.LENGTH_SHORT).show();
                        }
                    }
                    cancel_delete.setVisibility(View.GONE);
                    delete_hide.setText("CLOSE");

                    Refresher refresher =new Refresher(mainActivity);
                    refresher.refresh();

                    handler.removeCallbacks(runnable);
                }
            }
        };

        deleteData.isServiceRunning=true;
        mainActivity.startService(serviceIntent);

        handler.post(runnable);

    }

    private void sizeFetcherAndSetter()
    {
        for(int i=0;i<deleteData.namesList.size();i++)
        {
            if(deleteData.folderList.get(i))
            {
                long size=0;
                if(storageId<=3)
                {
                    File file=new File(deleteData.pathsList.get(i));
                    size=getSizeRecursive(file);
                }
                if(storageId==4)
                {
                    size=getSizeRecursiveDrive(deleteData.pathsList.get(i));
                }
                if(storageId==5)
                {
                    size=getSizeRecursiveDropBox(deleteData.pathsList.get(i));
                }
                if(storageId==6)
                {
                    size=getSizeRecursiveFtp(deleteData.pathsList.get(i));
                }

                final long newSize=size;
                final int index=i;
                Handler mainHandler = new Handler(Looper.getMainLooper());
                Runnable myRunnable = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(!deleteData.isServiceRunning)
                        {
                            deleteData.totalSizeToDownload+=newSize;
                            totalSize.setText(helpingBot.sizeinwords(deleteData.totalSizeToDownload));
                            deleteData.sizeLongList.set(index,newSize);
                            deleteListAdapter.notifyDataSetChanged();
                        }
                    }
                };
                mainHandler.post(myRunnable);
            }
        }



        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                if(!deleteData.isServiceRunning)
                {
                    progressBar.setVisibility(View.GONE);
                }
                else
                {
                    totalSize.setText("UnKnown Size");
                }
            }
        };
        mainHandler.post(myRunnable);
    }


    private void getDeleteData()
    {

        if(pagerId==3)
        {
            for (Integer item : selectedIndexList)
            {
                MyContainer container=containerList.get(item);
                for(MyFile file:container.getMyFileArrayList())
                {
                    deleteData.myFilesToDeleteList.add(file);
                    deleteData.deleteFromWhichContainer.add(container);

                    deleteData.isErrorList.add(false);
                    deleteData.pathsList.add(file.getPath());
                    deleteData.namesList.add(file.getName());
                    deleteData.sizeLongList.add(file.getSizeLong());
                    deleteData.folderList.add(file.isFolder());
                    deleteData.totalSizeToDownload+=file.getSizeLong();
                    deleteData.totalFiles++;
                }
            }
        }
        else
        {
            for (Integer item : selectedIndexList)
            {
                MyFile file=myFilesList.get(item);
                if(runRootCommand && file.getSymLink())
                {
                    String path=null;
                    int indexOfLastSlash=path.lastIndexOf('/');
                    String name=path.substring(indexOfLastSlash+1);
                    deleteData.isErrorList.add(false);
                    deleteData.pathsList.add(path);
                    deleteData.namesList.add(name);
                    deleteData.sizeLongList.add(file.getSizeLong());
                    deleteData.folderList.add(file.isFolder());
                    deleteData.totalSizeToDownload+=file.getSizeLong();
                    deleteData.totalFiles++;
                }
                else
                {
                    deleteData.isErrorList.add(false);
                    deleteData.pathsList.add(file.getPath());
                    deleteData.namesList.add(file.getName());
                    deleteData.sizeLongList.add(file.getSizeLong());
                    deleteData.folderList.add(file.isFolder());
                    deleteData.totalSizeToDownload+=file.getSizeLong();
                    deleteData.totalFiles++;
                }
                if(pagerId==2 || pagerId==4)//FOR SEARCH CACHE DELETE
                {
                    deleteData.myFilesToDeleteList.add(file);
                    if(pagerId==4)
                        deleteData.deleteFromWhichContainer.add(galleryData.currentMyContainer);
                    else
                        deleteData.deleteFromWhichContainer.add(null);
                }
            }
        }
    }


    private boolean checkLocalStorage()
    {
        temporaryDeletePossible=false;

        MyFile myFile=myFilesList.get(selectedIndexList.get(0));
        String storageHomePath=PagerXUtilities.getLocalHomeStoragePath(myFile.getPath());
        boolean rootOperation=storageHomePath==null;

        if(rootOperation)
        {
            if(!SuperUser.hasUserEnabledSU)
            {
                //if not rooted
                Toast.makeText(mainActivity, "Root Access Required", Toast.LENGTH_SHORT).show();
                return false;
            }
            else
            {
                //root
                deleteData.fromStorageId=0;
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
                    deleteData.fromStorageId*=10;

                    String [] breaker=storageHomePath.split("\\/");
                    String storageName=breaker[breaker.length-1];
                    if(MainActivity.SDCardUriMap.get(storageName)==null)
                    {
                        StorageAccessFramework storageAccessFramework=new StorageAccessFramework(mainActivity);
                        storageAccessFramework.showSaf(3,storageName);
                        return false;
                    }
                }
                else
                {
                    Toast.makeText(mainActivity, "Error:This directory is not Writable", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            else
            {
                if(storageHomePath.equals(Physical_Storage_PATHS.get(0)))
                {
                    temporaryDeletePossible=true;
                }
            }
            return true;
        }

    }


    private long getSizeRecursive(File fileOrDirectory)    //OKOK
    {
        long sizeToReturn=0;

        if(deleteData.isServiceRunning)
            return 0;

        File [] files;
        try
        {
            files=fileOrDirectory.listFiles();
        }
        catch (Exception e)
        {
            return 0;
        }

        if(files!=null)
            for (File child :files)
            {
                if(deleteData.isServiceRunning)
                    return 0;

                if(child.exists() && child.canRead())
                {
                    if(child.isDirectory() )
                    {
                        sizeToReturn+=getSizeRecursive(child);
                    }
                    else
                    {
                        sizeToReturn+=child.length();
                    }
                }
            }
            return sizeToReturn;
    }

    private long getSizeRecursiveDropBox(String path)
    {
        long sizeToReturn=0;

        if(deleteData.isServiceRunning)
            return 0;

        List<Metadata> list=new ArrayList<>();
        try
        {
            ListFolderResult result = DropBoxConnection.mDbxClient.files().listFolder(path);
            while (true)
            {
                for (Metadata metadata : result.getEntries())
                {
                    list.add(metadata);
                }

                if (!result.getHasMore())
                {
                    break;
                }

                result = DropBoxConnection.mDbxClient.files().listFolderContinue(result.getCursor());
            }

        }
        catch (Exception e)
        {
            return 0;
        }

        for (Metadata child : list)
        {
            if(deleteData.isServiceRunning)
                return 0;

            if(child instanceof FolderMetadata)
            {
                sizeToReturn+=getSizeRecursiveDropBox(child.getPathDisplay());
            }
            else
            {
                sizeToReturn+=((FileMetadata)child).getSize();
            }
        }

        return sizeToReturn;
    }

    private long getSizeRecursiveDrive(String folderId)
    {
        long sizeToReturn=0;

        if(deleteData.isServiceRunning)
            return 0;

        try
        {
            com.google.api.services.drive.Drive.Files.List request= GoogleDriveConnection.m_service_client.files().list().setFields("files(id,name,mimeType,quotaBytesUsed)").setQ("'" +folderId+ "' in parents");
            List<com.google.api.services.drive.model.File> filesInFolder= new ArrayList<com.google.api.services.drive.model.File>();
            do
            {
                try
                {
                    FileList filelist = request.execute();
                    filesInFolder.addAll(filelist.getFiles());
                    request.setPageToken(filelist.getNextPageToken());
                }
                catch (IOException e)
                {
                    System.out.println("An error occurred: " + e);
                    request.setPageToken(null);
                    return 0;
                }
            }
            while (request.getPageToken() != null && request.getPageToken().length() > 0);

            for(com.google.api.services.drive.model.File f:filesInFolder)
            {
                if(deleteData.isServiceRunning)
                    return 0;

                if(f.getMimeType().contains("folder"))
                {
                    sizeToReturn+=getSizeRecursiveDrive(f.getId());
                }
                else
                {
                   sizeToReturn+=f.getQuotaBytesUsed();
                }
            }
        }
        catch (IOException e)
        {
            return 0;
        }

        return sizeToReturn;
    }

    private long getSizeRecursiveFtp(String folderPath)    //OKOK
    {
        long sizeToReturn=0;

        if(deleteData.isServiceRunning)
            return 0;

        FTPFile[] filesFtp=null;
        try
        {
            filesFtp= FtpCache.mFTPClient.listFiles(folderPath);
        }
        catch (Exception e)
        {
            return 0;
        }

        if(filesFtp!=null)
            for (FTPFile child :filesFtp)
            {
                if(deleteData.isServiceRunning)
                    return 0;

                String name=child.getName();
                String path=slashAppender(folderPath,name);
                if(child.isDirectory())
                {
                    sizeToReturn+=getSizeRecursiveFtp(path);
                }
                else
                {
                    sizeToReturn+=child.getSize();
                }
            }
        return sizeToReturn;
    }



    private void checkBoxListener()
    {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if(isChecked)
                {
                    deleteData.shouldRecycle=true;
                    checkbox_details.setVisibility(View.VISIBLE);
                }
                else
                {
                    deleteData.shouldRecycle=false;
                    checkbox_details.setVisibility(View.GONE);
                }
            }
        });
    }

    private void deleteHideButtonClickListener()
    {
        delete_hide.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(delete_hide.getText().equals("DELETE"))
                {
                    checkBox.setClickable(false);
                    delete_hide.setText("HIDE");
                    title.setText("Deleting");
                        startDeleteRunner();
                }
                else
                {
                    if(delete_hide.getText().equals("HIDE"))
                    {
                        dialog.hide();
                    }
                    if(delete_hide.getText().equals("CLOSE"))
                    {
                        dialog.cancel();
                    }
                }

            }
        });
    }

    private void cancelButtonClickListener()
    {
        cancel_delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(deleteData.isServiceRunning)
                {
                    deleteData.cancelDownloadingPlease=true;
                    cancel_delete.setText("Cancelling");
                    return;
                }
                 dialog.cancel();

            }
        });
    }


    private void refreshPager234()
    {
        for(int i=0;i<deleteData.myFilesToDeleteList.size();i++)
        {
            if(!deleteData.isErrorList.get(i))
            {
                MyFile myFile=deleteData.myFilesToDeleteList.get(i);
                myFilesList.remove(myFile);

                if(pagerId==3 || pagerId==4)
                {
                    MyContainer myContainer=deleteData.deleteFromWhichContainer.get(i);
                    if(myContainer!=null)
                    {
                        myContainer.getMyFileArrayList().remove(myFile);
                        galleryData.filteredMyFileList.remove(myFile);
                    }
                }

            }
        }
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

