package com.example.sahil.f2.OperationTheater;

import android.app.Dialog;
import android.media.Image;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.example.sahil.f2.Cache.FtpCache;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.Classes.MyContainer;
import com.example.sahil.f2.Classes.MyFile;
import com.example.sahil.f2.FunkyAdapters.DeleteListAdapter;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.R;
import com.example.sahil.f2.Utilities.ExtensionUtil;
import com.google.api.services.drive.model.FileList;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hit4man47 on 12/31/2017.
 */

public class PropertiesMachine
{
    public final MainActivity mainActivity;
    private boolean runRootCommand,threadRunning,stopAllThread;
    private String currentPath;
    private int storageId,pagerId;
    private ArrayList<Integer> selectedIndexList;
    private ArrayList<MyContainer> containerList;
    private ArrayList<MyFile> myFilesList;
    private ArrayList<MyFile> selectedMyFilesList;
    private HelpingBot helpingBot;
    private Runnable runnable;
    private ExtensionUtil extensionUtil;


    private long totalSize=0;
    private int files=0,subFiles=0,subFolders=0,folders=0;



    public PropertiesMachine(MainActivity mainActivity, int pagerId, String currentPath, int storageId, boolean runRootCommand, ArrayList<MyFile> myFilesList, ArrayList<MyContainer> containerList, ArrayList<Integer> selectedIndexList)
    {
        this.mainActivity=mainActivity;
        this.runRootCommand=runRootCommand;
        this.currentPath=currentPath;
        this.storageId=storageId;
        this.pagerId=pagerId;
        this.selectedIndexList=selectedIndexList;
        this.containerList=containerList;
        this.myFilesList=myFilesList;
        helpingBot=new HelpingBot();
        extensionUtil=new ExtensionUtil();
        totalSize=0;
        files=0;
        folders=0;
        subFiles=0;
        subFolders=0;
    }

    private void getPropertiesData001()
    {
        selectedMyFilesList=new ArrayList<>();
        if(pagerId==3)
        {
            for (Integer item : selectedIndexList)
            {
                MyContainer container=containerList.get(item);
                selectedMyFilesList.addAll(container.getMyFileArrayList());
            }
        }
        else
        {
            for (Integer item : selectedIndexList)
            {
                MyFile file=myFilesList.get(item);
                selectedMyFilesList.add(file);
            }

        }
    }

    public void decisionMaker()
    {
        getPropertiesData001();

        Thread thread=new Thread()
        {
            @Override
            public void run()
            {
                sizeFetcher();
                threadRunning=false;
            }
        };
        stopAllThread=false;
        threadRunning=true;
        thread.start();

        if(selectedMyFilesList.size()==1)
        {
            show1();
        }
        else
        {
            show2();
        }

    }

    private void show1()
    {
        MyFile myFile=selectedMyFilesList.get(0);
        final Dialog dialog = new Dialog(mainActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layoutof_customdialog5);
        dialog.setCanceledOnTouchOutside(false);

        final TextView tv_Name=(TextView) dialog.findViewById(R.id.prop1_name);
        final TextView tv_Type=(TextView) dialog.findViewById(R.id.prop1_type);
        final TextView tv_TypeMore=(TextView) dialog.findViewById(R.id.prop1_typemore);
        final TextView tv_Path=(TextView) dialog.findViewById(R.id.prop1_path);
        final TextView tv_size=(TextView) dialog.findViewById(R.id.prop1_size);
        final TextView tv_time=(TextView) dialog.findViewById(R.id.prop1_time);
        final ImageView iv_logo1=(ImageView) dialog.findViewById(R.id.prop1_logo1);
        final ImageView iv_logo2=(ImageView) dialog.findViewById(R.id.prop1_logo2);
        final Button btn_ok=(Button) dialog.findViewById(R.id.prop1_button);
        final ProgressBar pb=(ProgressBar) dialog.findViewById(R.id.prop1_progress);

        String name=myFile.getName();
        tv_Name.setText(name);
        tv_Path.setText(myFile.getPath());
        tv_size.setText(helpingBot.sizeinwords(myFile.getSizeLong()));
        Date date=new Date(myFile.getLastModified());
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm");
        tv_time.setText(dateFormat.format(date));
        if(myFile.isFolder())
        {
            iv_logo1.setImageResource(R.mipmap.folder5);
            tv_Type.setText("Folder");
            tv_TypeMore.setText(subFolders+" subFolders + "+subFiles+" subFiles");
        }
        else
        {
            iv_logo1.setImageResource(extensionUtil.getKnownIcons(name));
            tv_Type.setText("File");
            tv_TypeMore.setVisibility(View.INVISIBLE);
        }
        if(storageId<=3)
        {
            iv_logo2.setImageResource(R.drawable.sd_card);
        }
        if(storageId==4)
        {
            iv_logo2.setImageResource(R.mipmap.google_drive);
        }
        if(storageId==5)
        {
            iv_logo2.setImageResource(R.mipmap.dropbox);
        }
        if(storageId==6)
        {
            iv_logo2.setImageResource(R.mipmap.server);
        }
        pb.setVisibility(View.VISIBLE);

        btn_ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                stopAllThread=true;
                threadRunning=false;
                dialog.cancel();
            }
        });
        dialog.show();
        final Handler handler=new Handler();
        runnable=new Runnable()
        {
            @Override
            public void run()
            {
                tv_TypeMore.setText(subFolders+" subFolders + "+subFiles+" subFiles");
                tv_size.setText(helpingBot.sizeinwords(totalSize));
                if(threadRunning)
                {
                    handler.postDelayed(runnable,1000);
                }
                else
                {
                    pb.setVisibility(View.GONE);
                    handler.removeCallbacks(runnable);
                }
            }
        };
        handler.post(runnable);


    }

    private void show2()
    {
        final Dialog dialog = new Dialog(mainActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layoutof_customdialog6);
        dialog.setCanceledOnTouchOutside(false);

        final TextView tv_Path=(TextView) dialog.findViewById(R.id.prop2_path);
        final TextView tv_size=(TextView) dialog.findViewById(R.id.prop2_size);
        final TextView tv_Folders=(TextView) dialog.findViewById(R.id.prop2_folders);
        final TextView tv_Files=(TextView) dialog.findViewById(R.id.prop2_files);
        final TextView tv_SubFolders=(TextView) dialog.findViewById(R.id.prop2_subfolders);
        final TextView tv_SubFiles=(TextView) dialog.findViewById(R.id.prop2_subfiles);


        final ImageView iv_logo1=(ImageView) dialog.findViewById(R.id.prop2_logo1);
        final ImageView iv_logo2=(ImageView) dialog.findViewById(R.id.prop2_logo2);
        final Button btn_ok=(Button) dialog.findViewById(R.id.prop2_button);
        final ProgressBar pb=(ProgressBar) dialog.findViewById(R.id.prop2_progress);


        tv_Path.setText(currentPath);
        tv_size.setText(helpingBot.sizeinwords(totalSize));
        iv_logo1.setImageResource(R.mipmap.copy);

        tv_Files.setText(files+"");
        tv_Folders.setText(folders+"");
        tv_SubFiles.setText(subFiles+"");
        tv_SubFolders.setText(subFolders+"");

        if(storageId<=3)
        {
            iv_logo2.setImageResource(R.drawable.sd_card);
        }
        if(storageId==4)
        {
            iv_logo2.setImageResource(R.mipmap.google_drive);
        }
        if(storageId==5)
        {
            iv_logo2.setImageResource(R.mipmap.dropbox);
        }
        if(storageId==6)
        {
            iv_logo2.setImageResource(R.mipmap.server);
        }

        pb.setVisibility(View.VISIBLE);

        btn_ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                stopAllThread=true;
                threadRunning=false;
                dialog.cancel();
            }
        });
        dialog.show();

        final Handler handler=new Handler();
        runnable=new Runnable()
        {
            @Override
            public void run()
            {
                tv_size.setText(helpingBot.sizeinwords(totalSize));
                tv_Files.setText(files+"");
                tv_Folders.setText(folders+"");
                tv_SubFiles.setText(subFiles+"");
                tv_SubFolders.setText(subFolders+"");

                if(threadRunning)
                {
                    handler.postDelayed(runnable,1000);
                }
                else
                {
                    pb.setVisibility(View.GONE);
                    handler.removeCallbacks(runnable);
                }
            }
        };
        handler.post(runnable);

    }


    private void sizeFetcher()
    {
        for(int i=0;i<selectedMyFilesList.size();i++)
        {
            if(stopAllThread)
                return;

            MyFile myFile=selectedMyFilesList.get(i);
            if(myFile.isFolder())
            {
                folders++;
                if(storageId<=3)
                {
                    File file=new File(myFile.getPath());
                    getSizeRecursive(file);
                }
                if(storageId==4)
                {
                    getSizeRecursiveDrive(myFile.getPath());
                }
                if(storageId==5)
                {
                    getSizeRecursiveDropBox(myFile.getPath());
                }
                if(storageId==6)
                {
                    getSizeRecursiveFtp(myFile.getPath());
                }
            }
            else
            {
                files++;
                totalSize+=myFile.getSizeLong();
            }

        }

    }


    private void getSizeRecursive(File fileOrDirectory)
    {
        if(stopAllThread)
        {
            return;
        }
        File [] files;
        try
        {
            files=fileOrDirectory.listFiles();
        }
        catch (Exception e)
        {
            return;
        }

        if(files!=null)
            for (File child :files)
            {
                if(stopAllThread)
                {
                    return;
                }
                if(child.exists() && child.canRead())
                {

                    if(child.isDirectory() )
                    {
                        subFolders++;
                        getSizeRecursive(child);
                    }

                    else
                    {
                        subFiles++;
                        totalSize+=child.length();
                    }
                }
            }
    }

    private void getSizeRecursiveDropBox(String path)
    {

        if(stopAllThread)
        {
            return;
        }

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
            return;
        }

        for (Metadata child : list)
        {
            if(stopAllThread)
            {
                return;
            }
            if(child instanceof FolderMetadata)
            {
                subFolders++;
                getSizeRecursiveDropBox(child.getPathDisplay());
            }
            else
            {
                subFiles++;
                totalSize+=((FileMetadata)child).getSize();
            }
        }
    }

    private void getSizeRecursiveDrive(String folderId)
    {


        if(stopAllThread)
        {
            return;
        }
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
                    return;
                }
            }
            while (request.getPageToken() != null && request.getPageToken().length() > 0);

            for(com.google.api.services.drive.model.File f:filesInFolder)
            {
                if(stopAllThread)
                {
                    return;
                }

                if(f.getMimeType().contains("folder"))
                {
                    subFolders++;
                    getSizeRecursiveDrive(f.getId());
                }
                else
                {
                    subFiles++;
                    totalSize+=f.getQuotaBytesUsed();
                }
            }
        }
        catch (IOException e)
        {
            return;
        }
    }

    private void getSizeRecursiveFtp(String folderPath)    //OKOK
    {
        if(stopAllThread)
        {
            return;
        }
        FTPFile[] filesFtp=null;
        try
        {
            filesFtp= FtpCache.mFTPClient.listFiles(folderPath);
        }
        catch (Exception e)
        {
            return ;
        }

        if(filesFtp!=null)
            for (FTPFile child :filesFtp)
            {
                if(stopAllThread)
                {
                    return;
                }
                String name=child.getName();
                String path=slashAppender(folderPath,name);

                if(child.isDirectory() )
                {
                    subFolders++;
                    getSizeRecursiveFtp(path);
                }

                else
                {
                    subFiles++;
                    totalSize+=child.getSize();
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
