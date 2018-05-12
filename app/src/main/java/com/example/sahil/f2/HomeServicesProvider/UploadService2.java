package com.example.sahil.f2.HomeServicesProvider;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.CommitInfo;
import com.dropbox.core.v2.files.CreateFolderErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.GetMetadataErrorException;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.UploadSessionCursor;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.users.SpaceUsage;
import com.example.sahil.f2.Cache.MyCacheData;
import com.example.sahil.f2.Cache.UploadData;
import com.example.sahil.f2.Cache.tasksCache;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;

import com.example.sahil.f2.ErrorHandling.ErrorHandler;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.R;
import com.example.sahil.f2.StorageAccessFramework;
import com.example.sahil.f2.Utilities.DeleteUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;


/**
 * Created by Acer on 14-08-2017.
 */


public class UploadService2 extends Service
{

    public NotificationCompat.Builder mBuilder;
    public NotificationManager mNotifyManager;


    HelpingBot helpingBot;
    int oldTime = 0;
    boolean isSpace=true;
    public static Thread threadforCopy;
    public ArrayList<String> deleteList;
    private int operationCode=302;
    public final String TAG=operationCode+"_SERVICE";
    private UploadData uploadData;
    private DeleteUtils deleteUtils;
    TinyDB tinyDB;

    @Override
    public int onStartCommand(Intent intent, int flags, final int startid)
    {

        Log.i(TAG,"service called");

        helpingBot=new HelpingBot();
        Intent myIntent;
        PendingIntent myPendingIntent;
        deleteUtils=new DeleteUtils(null,getApplicationContext());

        myIntent= new Intent(this, MainActivity.class);
        myIntent.putExtra("notification",5);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        myPendingIntent=PendingIntent.getActivity(getApplicationContext(),operationCode,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);



        mNotifyManager=(NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder=new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.search_icon)
                .setContentTitle("Uploading to DropBox")
                .setContentText("Calculating...")
                .setOngoing(true)
                .setAutoCancel(false)
                .setContentIntent(myPendingIntent)
                .setProgress(100,0,true);
        mNotifyManager.notify(operationCode,mBuilder.build());

        deleteList=new ArrayList<>();

        threadforCopy=new Thread()
        {
            @Override
            public void run()
            {
                downloadTask();
            }
        };
        threadforCopy.start();


        return START_STICKY;
    }


    public void downloadTask()
    {
        isSpace=true;
        oldTime=-1;
        boolean resumable;
        final int startIndex= uploadData.currentFileIndex;
        for(int i = startIndex; i< uploadData.totalFiles; i++)
        {
            resumable=false;
            if(tinyDB.getString(operationCode+"Url").length()>0)
            {
                resumable=true;
            }

            //KEEP BOTH     **************************************************************************
            if(!resumable)
            {
                try
                {
                    Metadata m=DropBoxConnection.mDbxClient.files().getMetadata(slashAppender(uploadData.toRootPath, uploadData.namesList.get(i)));
                    //this path already exists
                    keepBoth(i);
                    tinyDB.putListString(operationCode+"namesList", uploadData.namesList);
                }
                catch (GetMetadataErrorException e)
                {
                    if(e.errorValue.isPath() && e.errorValue.getPathValue().isNotFound())
                    {
                        //this path doesnt exist and we can use this as ours
                    }
                    else
                    {
                        uploadData.isDownloadError=true;
                        uploadData.downloadErrorCode=4;
                        uploadData.errorDetails="Check your Internet Connection";
                        Log.e(TAG, ErrorHandler.getErrorName(4)+"-->"+ uploadData.errorDetails);
                    }
                }
                catch (DbxException e)
                {
                    uploadData.isDownloadError=true;
                    uploadData.downloadErrorCode=4;
                    uploadData.errorDetails="Check your Internet Connection";
                    Log.e(TAG, ErrorHandler.getErrorName(4)+"-->"+ uploadData.errorDetails);

                }
                if(uploadData.isDownloadError)
                {

                    uploadData.currentFileName= uploadData.namesList.get(i);
                    uploadData.currentFileIndex=i;

                    /*
                     THIS is done HERE also to deal with case when space is low ,,so that user can resume it later
                     */
                    //MAKING ALL THE VALUES SYNCRONIZE
                    tinyDB.remove(operationCode+"IsRunning");     //this is done to make sure that all these steps run
                    tinyDB.putString(operationCode+"CurrentSourcePath", uploadData.pathsList.get(i));
                    uploadToTinyDB1();
                    tinyDB.putString(operationCode+"IsRunning",operationCode+" is running");

                    notifyProgressWithMessage(i,"Uploading Error...",true);
                    stopSelf();
                    return;
                }
            }


            uploadData.currentFileName= uploadData.namesList.get(i);
            uploadData.currentFileIndex=i;

            /*
            THIS is done HERE also to deal with case when space is low ,,so that user can resume it later
             */
            //MAKING ALL THE VALUES SYNCRONIZE
            tinyDB.remove(operationCode+"IsRunning");     //this is done to make sure that all these steps run
            tinyDB.putString(operationCode+"CurrentSourcePath", uploadData.pathsList.get(i));
            uploadToTinyDB1();
            tinyDB.putString(operationCode+"IsRunning",operationCode+" is running");



            //CHECLING FOR SPACE ERRORS
            try
            {
                SpaceUsage spaceUsage =DropBoxConnection.mDbxClient.users().getSpaceUsage();
                long totalSize=spaceUsage.getAllocation().getIndividualValue().getAllocated();
                long usedSize=spaceUsage.getUsed();
                long partialUploaded=0;
                if(resumable)
                {
                    partialUploaded=tinyDB.getLong(operationCode+"ChunkStart",0);
                }
                long available=totalSize-usedSize;
                if(available<(uploadData.sizeLongList.get(i)-partialUploaded))
                    isSpace=false;

            }
            catch (DbxException e)
            {
                uploadData.isDownloadError=true;
                uploadData.downloadErrorCode=4;
                uploadData.errorDetails="Check your Internet Connection";
                Log.e(TAG, ErrorHandler.getErrorName(4)+"-->"+ uploadData.errorDetails);

                notifyProgressWithMessage(i,"Uploading Error...",true);
                stopSelf();
                return;
            }


            //SPACE IS THERE For THIS PARTICULAR FILE
            if(isSpace)
            {
                if (uploadData.folderList.get(i))
                {
                    foldercopier(i);
                }
                else
                {
                    filecopier(i,resumable);
                }

                if(!uploadData.isDownloadError && !uploadData.cancelDownloadingPlease && !uploadData.pauseDownloadingPlease )
                {
                    //no problem in uploading
                    if(uploadData.isMoving)
                    {
                        String []breaker= uploadData.namesList.get(i).split("\\/");//-----[0]=Whatsapp,[1]=media,[2]=file
                        if(uploadData.folderList.get(i)) //is a folder
                        {
                            if(breaker.length==1)
                            {
                                deleteList.add(uploadData.pathsList.get(i));
                            }
                        }
                        else
                        {
                            if(breaker.length==1)
                            {
                                File file=new File(uploadData.pathsList.get(i));
                                if(uploadData.fromStorageId%10==0 || uploadData.fromStorageId%11==0)
                                {
                                    if(uploadData.fromStorageId%10==0)
                                    {
                                        deleteUtils.deleteDocumentFile(file);
                                    }
                                    else
                                    {
                                        //media store hack
                                    }
                                }
                                else
                                {
                                    deleteUtils.deleteFromInternal(file);
                                }
                            }
                        }
                    }


                    //MAKING ALL THE VALUES SYNCRONIZE
                    tinyDB.remove(operationCode+"IsRunning");     //this is done to make sure that all these steps run
                    uploadData.currentFileName= uploadData.namesList.get(i);
                    uploadData.currentFileIndex++;
                    uploadToTinyDB1();
                    tinyDB.remove(operationCode+"CurrentSourcePath");
                    tinyDB.remove(operationCode+"LastSourcePath");
                    tinyDB.remove(operationCode+"Url");
                    tinyDB.remove(operationCode+"ChunkStart");
                    uploadToTinyDB2();
                    tinyDB.putString(operationCode+"IsRunning",operationCode+" is running");

                }
                else
                {
                    if(uploadData.isDownloadError)
                    {
                        notifyProgressWithMessage(i,"Uploading Error...",true);
                        stopSelf();
                        return;
                    }
                    if(uploadData.cancelDownloadingPlease)
                    {
                        notifyProgressWithMessage(i,"Uploading Cancelled...",false);
                        mNotifyManager.cancel(operationCode);
                        stopSelf();
                        return;
                    }
                    if(uploadData.pauseDownloadingPlease)
                    {
                        notifyProgressWithMessage(i,"Uploading Paused...",false);
                        stopSelf();
                        return;
                    }
                }

            }
            else   //NO SPACE IS THERE FOR THIS PARTICULAR FILE
            {
                uploadData.isDownloadError=true;
                uploadData.downloadErrorCode=0;
                uploadData.errorDetails="DropBox";
                notifyProgressWithMessage(i,"LOW SPACE ERROR...",true);
                Log.e(TAG, ErrorHandler.getErrorName(0)+"-->"+ uploadData.errorDetails);
                stopSelf();
                return;
            }

            Log.i(TAG,"Uploaded file "+ uploadData.currentFileIndex+" of " + uploadData.totalFiles);
        }

        if(uploadData.isMoving)
        {
            for(int j=0;j<deleteList.size();j++)
            {
                File file=new File(deleteList.get(j));
                if(uploadData.fromStorageId%10==0 || uploadData.fromStorageId%11==0)
                {
                    if(uploadData.fromStorageId%10==0)
                    {
                        deleteUtils.deleteDocumentFile(file);
                    }
                    else
                    {
                        //media store hack
                    }
                }
                else
                {
                    deleteUtils.deleteFromInternal(file);
                }
            }
        }


        uploadData.progress=100;
        uploadData.downloadedSize= uploadData.totalSizeToDownload;
        notifyProgressWithMessage(uploadData.namesList.size()-1,"Uploading Successful...",false);
        stopSelf();
    }



    public void filecopier(final int indexofpaster,final boolean resumable)
    {

        if(tinyDB.getString(operationCode+"CurrentSourcePath").equals(tinyDB.getString(operationCode+"LastSourcePath")))
        {
            //already uploaded
            return;
        }

        String pathfrom= uploadData.pathsList.get(indexofpaster);
        String pathto=slashAppender(uploadData.toRootPath , uploadData.namesList.get(indexofpaster));

        long CHUNK_SIZE=2 * 1024 * 1024;
        long chunkStart=0;

        if(resumable)
        {
            Log.i(TAG,"resuming...");
            chunkStart=tinyDB.getLong(operationCode+"ChunkStart",0);
        }


        //OPENING INPUT STREAM
        File fromFile=new File(pathfrom);
        FileInputStream fileInputStream;
        try
        {
            fileInputStream= new FileInputStream(fromFile);
            fileInputStream.getChannel().position(chunkStart);

        }
        catch (Exception e)
        {
            uploadData.isDownloadError=true;
            uploadData.downloadErrorCode=1;
            uploadData.errorDetails="'" + pathfrom + "'" + " does't exist OR read access denied";
            Log.e(TAG, ErrorHandler.getErrorName(1)+"-->"+ uploadData.errorDetails);
            stopSelf();
            return;
        }



        //START UPLOADING FIRST CHUNK AND GET SESSION ID
        if(!resumable)
        {

            if (chunkStart + CHUNK_SIZE > fromFile.length())
            {
                CHUNK_SIZE =  fromFile.length() - chunkStart;
            }

            String sessionId=null;
            try
            {
                sessionId=DropBoxConnection.mDbxClient.files().uploadSessionStart().uploadAndFinish(fileInputStream,CHUNK_SIZE).getSessionId();
            }
            catch (IOException e)
            {
                uploadData.isDownloadError=true;
                uploadData.downloadErrorCode=4;
                uploadData.errorDetails="'" + pathto + "'" + " upload failed, Check your Internet Connection";
                Log.e(TAG, ErrorHandler.getErrorName(4)+"-->"+ uploadData.errorDetails);
                stopSelf();
                return;
            }
            catch (DbxException e)
            {
                uploadData.isDownloadError=true;
                uploadData.downloadErrorCode=12;
                uploadData.errorDetails="'" + pathto + "'" + " upload failed";
                Log.e(TAG, ErrorHandler.getErrorName(12)+"-->"+ uploadData.errorDetails);
                stopSelf();
                return;
            }

            if(!fromFile.exists())
            {
                uploadData.isDownloadError=true;
                uploadData.downloadErrorCode=1;
                uploadData.errorDetails="'" + pathfrom + "'" + "is missing Or write access denied";
                Log.e(TAG, ErrorHandler.getErrorName(1)+"-->"+ uploadData.errorDetails);
                stopSelf();
                return;
            }

            uploadData.downloadedSize+=CHUNK_SIZE;
            if(uploadData.totalSizeToDownload==0)
            {
                uploadData.progress=0;
            }
            else
            {
                uploadData.progress= uploadData.downloadedSize*100/ uploadData.totalSizeToDownload;
            }
            chunkStart+=CHUNK_SIZE;

            if(sessionId!=null)
            {
                tinyDB.putString(operationCode+"Url",sessionId);
                tinyDB.putLong(operationCode+"ChunkStart",chunkStart);
            }

            if(oldTime< uploadData.timeInSec )
            {
                notifyProgressPlease(indexofpaster);
            }

            if(uploadData.cancelDownloadingPlease)
            {
                tinyDB.remove(operationCode+"Url");
                tinyDB.remove(operationCode+"ChunkStart");
                tinyDB.remove(operationCode+"IsRunning");
                tinyDB.remove(operationCode+"CurrentSourcePath");
                tinyDB.remove(operationCode+"LastSourcePath");
                return;
            }
            if(uploadData.pauseDownloadingPlease)
            {
                return;
            }
        }



        //APPENDING DATA
        String sessionId=tinyDB.getString(operationCode+"Url");
        UploadSessionCursor cursor = new UploadSessionCursor(sessionId, chunkStart);

        while ((fromFile.length() - chunkStart) > CHUNK_SIZE)
        {

            if(uploadData.cancelDownloadingPlease)
            {
                tinyDB.remove(operationCode+"Url");
                tinyDB.remove(operationCode+"ChunkStart");
                tinyDB.remove(operationCode+"IsRunning");
                tinyDB.remove(operationCode+"CurrentSourcePath");
                tinyDB.remove(operationCode+"LastSourcePath");
                return;
            }
            if(uploadData.pauseDownloadingPlease)
            {
                return;
            }

            try
            {
                DropBoxConnection.mDbxClient.files().uploadSessionAppendV2(cursor).uploadAndFinish(fileInputStream, CHUNK_SIZE);
            }
            catch (IOException e)
            {

                uploadData.isDownloadError=true;
                uploadData.downloadErrorCode=4;
                uploadData.errorDetails="'" + pathto + "'" + " upload failed, Check your Internet Connection";
                Log.e(TAG, ErrorHandler.getErrorName(4)+"-->"+ uploadData.errorDetails+e.getLocalizedMessage()+e.getMessage());
                stopSelf();
                return;
            }
            catch (DbxException e)
            {
                String message=e.getMessage();
                // Exception in 2/files/upload_session/append_v2: {".tag":"incorrect_offset","correct_offset":4194304}
                if(message.contains("incorrect_offset") && message.contains("correct_offset") )
                {
                    Log.e("wrong offset..",e.getMessage()+"--");
                    fixWrongChunk(message,indexofpaster);
                    return;
                }

                uploadData.isDownloadError=true;
                uploadData.downloadErrorCode=12;
                uploadData.errorDetails="'" + pathto + "'" + " upload failed";
                Log.e(TAG, ErrorHandler.getErrorName(12)+"-->"+ uploadData.errorDetails);
                stopSelf();
                return;
            }

            if(!fromFile.exists())
            {
                uploadData.isDownloadError=true;
                uploadData.downloadErrorCode=1;
                uploadData.errorDetails="'" + pathfrom + "'" + "is missing Or write access denied";
                Log.e(TAG, ErrorHandler.getErrorName(1)+"-->"+ uploadData.errorDetails);
                stopSelf();
                return;
            }

            chunkStart+=CHUNK_SIZE;
            tinyDB.putLong(operationCode+"ChunkStart",chunkStart);

            uploadData.downloadedSize+=CHUNK_SIZE;
            if(uploadData.totalSizeToDownload==0)
            {
                uploadData.progress=0;
            }
            else
            {
                uploadData.progress= uploadData.downloadedSize*100/ uploadData.totalSizeToDownload;
            }




            if(oldTime< uploadData.timeInSec )
            {
                notifyProgressPlease(indexofpaster);
            }

            cursor = new UploadSessionCursor(sessionId, chunkStart);
        }



        if(uploadData.cancelDownloadingPlease)
        {
            tinyDB.remove(operationCode+"Url");
            tinyDB.remove(operationCode+"ChunkStart");
            tinyDB.remove(operationCode+"IsRunning");
            tinyDB.remove(operationCode+"CurrentSourcePath");
            tinyDB.remove(operationCode+"LastSourcePath");
            return;
        }
        if(uploadData.pauseDownloadingPlease)
        {
            return;
        }



        // FINISHING UPLOADING
        long remaining = fromFile.length() - chunkStart;
        CommitInfo commitInfo = CommitInfo.newBuilder(pathto)
                .withMode(WriteMode.ADD)
                .withClientModified(new Date(fromFile.lastModified()))
                .build();
        try
        {
            FileMetadata metadata = DropBoxConnection.mDbxClient.files().uploadSessionFinish(cursor, commitInfo)
                    .uploadAndFinish(fileInputStream, remaining);

        }
        catch (IOException e)
        {
            uploadData.isDownloadError=true;
            uploadData.downloadErrorCode=4;
            uploadData.errorDetails="'" + pathto + "'" + " upload failed, Check your Internet Connection";
            Log.e(TAG, ErrorHandler.getErrorName(4)+"-->"+ uploadData.errorDetails);
            stopSelf();
            return;
        }
        catch (DbxException e)
        {
            String message=e.getMessage();
            //Exception in 2/files/upload_session/finish: {".tag":"lookup_failed","lookup_failed":{".tag":"incorrect_offset","correct_offset":20423392}}
            if(message.contains("incorrect_offset") && message.contains("correct_offset") )
            {
                Log.e("wrong offset..",e.getMessage()+"--");
                fixWrongChunk(message,indexofpaster);
                return;
            }
            uploadData.isDownloadError=true;
            uploadData.downloadErrorCode=12;
            uploadData.errorDetails="'" + pathto + "'" + " upload failed";
            Log.e(TAG, ErrorHandler.getErrorName(12)+"-->"+ uploadData.errorDetails+e.getMessage());
            stopSelf();
            return;
        }


        uploadData.downloadedSize+=remaining;
        if(uploadData.totalSizeToDownload==0)
        {
            uploadData.progress=0;
        }
        else
        {
            uploadData.progress= uploadData.downloadedSize*100/ uploadData.totalSizeToDownload;
        }

        chunkStart+=remaining;
        tinyDB.putLong(operationCode+"ChunkStart",chunkStart);
        tinyDB.putString(operationCode+"LastSourcePath",pathfrom);

    }



    public void foldercopier(int indexofpaster)
    {

        if(tinyDB.getString(operationCode+"CurrentSourcePath").equals(tinyDB.getString(operationCode+"LastSourcePath")))
        {
            //already downloaded
            return;
        }

        String topath=slashAppender(uploadData.toRootPath, uploadData.namesList.get(indexofpaster));

        try
        {
            FolderMetadata folder = DropBoxConnection.mDbxClient.files().createFolderV2(topath).getMetadata();
        }
        catch (CreateFolderErrorException err)
        {
            if (err.errorValue.isPath() && err.errorValue.getPathValue().isConflict())
            {
                uploadData.isDownloadError=true;
                uploadData.downloadErrorCode=7;
                uploadData.errorDetails="'"+topath+"' "+"This path already exists in dropbox";
                Log.e(TAG, ErrorHandler.getErrorName(7)+"-->"+ uploadData.errorDetails);
                return;

            }
            else
            {
                uploadData.isDownloadError=true;
                uploadData.downloadErrorCode=7;
                uploadData.errorDetails="Check your internet connection";
                Log.e(TAG, ErrorHandler.getErrorName(7)+"-->"+ uploadData.errorDetails);
                return;
            }
        }
        catch (DbxException err)
        {
            uploadData.isDownloadError=true;
            uploadData.downloadErrorCode=12;
            uploadData.errorDetails="'"+topath+"' "+" directory failed to create,Check your internet connection";
            Log.e(TAG, ErrorHandler.getErrorName(12)+"-->"+ uploadData.errorDetails);
            return;
        }


        tinyDB.putString(operationCode+"LastSourcePath", uploadData.pathsList.get(indexofpaster));


    }



    @Override
    public void onCreate()
    {
        operationCode=302;
        uploadData= MyCacheData.getUploadDataFromCode(operationCode);
        tinyDB=new TinyDB(getApplicationContext());
        tinyDB.putString(operationCode+"IsRunning",operationCode+" is running");
        uploadData.isServiceRunning = true;
        tasksCache.addTask(operationCode+"");
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void keepBoth(int i)
    {

        int filenumber=0;

        if(uploadData.folderList.get(i))
        {

            String x1= uploadData.namesList.get(i)+"("+(++filenumber)+")";
            /*loop unitil that new filename is not present....like (1)...(2)....(3).....
            *
            *
            * like if a.txt and a(1).txt is present how to copy???
            *
            */
            while(true)
            {
                try
                {
                    Metadata m=DropBoxConnection.mDbxClient.files().getMetadata(slashAppender(uploadData.toRootPath,x1));
                    //this path already exists
                    x1= uploadData.namesList.get(i)+"("+(++filenumber)+")";
                }
                catch (GetMetadataErrorException e)
                {
                    if(e.errorValue.isPath() && e.errorValue.getPathValue().isNotFound())
                    {
                        //this path doesnt exist and we can use this as ours
                        break;
                    }
                    else
                    {
                        uploadData.isDownloadError=true;
                        uploadData.downloadErrorCode=4;
                        uploadData.errorDetails="Check your Internet Connection";
                        Log.e(TAG, ErrorHandler.getErrorName(4)+"-->"+ uploadData.errorDetails);
                        return;
                    }
                }
                catch (DbxException e)
                {
                    uploadData.isDownloadError=true;
                    uploadData.downloadErrorCode=4;
                    uploadData.errorDetails="Check your Internet Connection";
                    Log.e(TAG, ErrorHandler.getErrorName(4)+"-->"+ uploadData.errorDetails);
                    return;
                }
            }
            //first changing the inside files name then at last change the foldder name
            for(int j = i+1; j< uploadData.namesList.size(); j++)
            {

                if(uploadData.namesList.get(j).startsWith(uploadData.namesList.get(i)))
                {

                    String x2= uploadData.namesList.get(j).replaceFirst(Pattern.quote(uploadData.namesList.get(i)),x1);
                    uploadData.namesList.set(j,x2);
                }
                else//no need to check for more files in the list
                {
                    break;
                }
            }
            uploadData.namesList.set(i,x1);
        }
        else //is a file
        {

            String x= uploadData.namesList.get(i); //.....'vishwas.txt'------'mydiiiicccc'
            String x2="";
            if(x.lastIndexOf('.')>=0)
            {
                x2=x.substring(x.lastIndexOf('.'),x.length());//.....'.txt'------''
            }


            String x1="";

            if(x.lastIndexOf('.')>=0)
            {
                x1=x.substring(0,x.lastIndexOf('.'))+"("+(++filenumber)+")";//....'vishwas(1)'
            }
            else
            {
                x1=x.substring(0,x.length())+"("+(++filenumber)+")";//------'mydiiiicccc(1'
            }


            while (true)
            {

                try
                {
                    Metadata m=DropBoxConnection.mDbxClient.files().getMetadata(slashAppender(uploadData.toRootPath,x1+x2));
                    //this path already exists
                    if(x.lastIndexOf('.')>=0)
                    {
                        x1=x.substring(0,x.lastIndexOf('.'))+"("+(++filenumber)+")";//....'vishwas(1)'
                    }
                    else
                    {
                        x1=x.substring(0,x.length())+"("+(++filenumber)+")";//------'mydiiiicccc(1'
                    }
                }
                catch (GetMetadataErrorException e)
                {
                    if(e.errorValue.isPath() && e.errorValue.getPathValue().isNotFound())
                    {
                        //this path doesnt exist and we can use this as ours
                        break;
                    }
                    else
                    {
                        uploadData.isDownloadError=true;
                        uploadData.downloadErrorCode=4;
                        uploadData.errorDetails="Check your Internet Connection";
                        Log.e(TAG, ErrorHandler.getErrorName(4)+"-->"+ uploadData.errorDetails);
                        return;
                    }
                }
                catch (DbxException e)
                {
                    uploadData.isDownloadError=true;
                    uploadData.downloadErrorCode=4;
                    uploadData.errorDetails="Check your Internet Connection";
                    Log.e(TAG, ErrorHandler.getErrorName(4)+"-->"+ uploadData.errorDetails);
                    return;
                }
            }
            uploadData.namesList.set(i,x1+x2);
        }
        Log.i(TAG,"renaming file");
    }


    @Override
    public void onDestroy()
    {
        Log.i(TAG,"destroyed");
        uploadData.isServiceRunning=false;//very very importanr

        if(uploadData.pauseDownloadingPlease)
        {
            notifyProgressWithMessage(uploadData.currentFileIndex,"Uploading Paused...",false);
        }

        if(uploadData.cancelDownloadingPlease || uploadData.totalFiles== uploadData.currentFileIndex)
        {
            tinyDB.remove(operationCode+"Url");
            tinyDB.remove(operationCode+"ChunkStart");
            tinyDB.remove(operationCode+"IsRunning");
            tinyDB.remove(operationCode+"CurrentSourcePath");
            tinyDB.remove(operationCode+"LastSourcePath");

            tasksCache.removeTask(operationCode+"");
        }
    }


    public void uploadToTinyDB1()
    {

        tinyDB.putString(operationCode+"currentFileName", uploadData.currentFileName);
        tinyDB.putInt(operationCode+"currentFileIndex", uploadData.currentFileIndex);

    }


    public void uploadToTinyDB2()
    {
        tinyDB.putLong(operationCode+"progress", uploadData.progress);
        tinyDB.putLong(operationCode+"downloadedSize", uploadData.downloadedSize);
    }

    private String slashAppender(String a,String b)
    {
        if(a.endsWith("/"))
            return a+b;
        else
            return a+"/"+b;
    }


    private void fixWrongChunk(String message,int indexofpaster)
    {
        //Exception in 2/files/upload_session/finish: {".tag":"lookup_failed","lookup_failed":{".tag":"incorrect_offset","correct_offset":20423392}}
        message=message.substring(message.lastIndexOf("correct_offset"));
        int index1=message.indexOf(':');//first index
        int index2=message.indexOf('}');

        String offset=message.substring(index1+1,index2);
        try
        {
            long x=Long.parseLong(offset);
            tinyDB.putLong(operationCode+"ChunkStart",x);
            filecopier(indexofpaster,true);
        }
        catch (Exception e)
        {
            uploadData.isDownloadError=true;
            uploadData.downloadErrorCode=12;
            uploadData.errorDetails="upload failed";
        }
    }


    private void notifyProgressPlease(int index)
    {
        oldTime= uploadData.timeInSec;
        mBuilder.setProgress(100,(int) uploadData.progress,false);
        mBuilder.setContentText(uploadData.namesList.get(index));
        mNotifyManager.notify(operationCode,mBuilder.build());
    }

    private void notifyProgressWithMessage(int index,String msg,boolean vibrate)
    {
        mBuilder.setContentTitle(msg)
                .setProgress(100,(int) uploadData.progress,false)
                .setContentText(uploadData.namesList.get(index))
                .setOngoing(false)
                .setAutoCancel(true);
        if(vibrate)
        {
            mBuilder.setVibrate(new long[]{1000,1000,1000});
        }

        mNotifyManager.notify(operationCode,mBuilder.build());
    }


}



/*
    302LastSourcePath:
    it is used to deal with cases when uploading of that file is completed but is asked again to upload next time when service starts
    case 1: when file is uploaded and service stops while doing isMoving OPERATION(which may be time taking)
    case 2: when file is uploaded but user asks it to pause

    302CurrentSourcePath:
    it holds the value,from the time when uploading starts till that file is uploaded(_and OR isMoving)


    resume:
    if ( 302SessionId )

    uploadToTinyDB1() deals with values of 302 cache
    uploadToTinyDB2()  deals with values of 302 cache and is run only when file is uploaded

 */


