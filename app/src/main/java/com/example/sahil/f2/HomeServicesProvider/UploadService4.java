package com.example.sahil.f2.HomeServicesProvider;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;


import com.example.sahil.f2.Cache.MyCacheData;
import com.example.sahil.f2.Cache.UploadData;
import com.example.sahil.f2.Cache.tasksCache;


import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.ErrorHandling.ErrorHandler;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.OperationTheater.HelpingBot;
import com.example.sahil.f2.R;
import com.example.sahil.f2.StorageAccessFramework;
import com.example.sahil.f2.Utilities.DeleteUtils;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.drive.Metadata;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.FileList;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


/**
 * Created by Acer on 10-18-2017.
 */

public class UploadService4 extends Service
{
    public NotificationCompat.Builder mBuilder;
    public NotificationManager mNotifyManager;

    HelpingBot helpingBot;
    int oldTime = 0;
    boolean isSpace=true;
    public static Thread threadforCopy;
    public ArrayList<String> deleteList;

    private int operationCode=304;
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
        myPendingIntent= PendingIntent.getActivity(getApplicationContext(),operationCode,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);



        mNotifyManager=(NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder=new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.search_icon)
                .setContentTitle("Uploading to Google Drive")
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
        for(int i=startIndex;i<uploadData.totalFiles;i++)
        {
            resumable = false;
            if (tinyDB.getString(operationCode+"Url").length() > 0)
            {
                resumable = true;
            }
            //NO NEED FOR KEEP BOTH IN GOOGLE DRIVE AS FILES ARE DISTINGUISHED BY THEIR IDS AND NOT NAMES

            uploadData.currentFileName = uploadData.namesList.get(i);
            uploadData.currentFileIndex =i;

            /*
            THIS is done HERE also to deal with case when space is low ,,so that user can resume it later
             */
            //MAKING ALL THE VALUES SYNCRONIZE
            tinyDB.remove(operationCode+"IsRunning");     //this is done to make sure that all these steps run
            tinyDB.putString(operationCode+"CurrentSourcePath",uploadData.pathsList.get(i));
            uploadToTinyDB1();
            tinyDB.putString(operationCode+"IsRunning",operationCode+" is running");


            //CHECLING FOR SPACE ERRORS
            try
            {
                About about = GoogleDriveConnection.m_service_client.about().get().setFields("storageQuota, user").execute();
                long totalSize=about.getStorageQuota().getLimit();
                long usedSize=about.getStorageQuota().getUsageInDrive();
                long available=totalSize-usedSize;
                long partialUploaded=0;
                if(resumable)
                {
                    partialUploaded=tinyDB.getLong(operationCode+"ChunkStart",0);
                }
                if(available<(uploadData.sizeLongList.get(i)-partialUploaded))
                    isSpace=false;

            }
            catch(Exception e)
            {
                uploadData.isDownloadError=true;
                uploadData.downloadErrorCode=4;
                uploadData.errorDetails="Check your Internet Connection";
                Log.e(TAG, ErrorHandler.getErrorName(4)+"-->"+uploadData.errorDetails);
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
                    uploadData.currentFileName = uploadData.namesList.get(i);
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
            else    //NO SPACE IS THERE FOR THIS PARTICULAR FILE
            {
                uploadData.isDownloadError=true;
                uploadData.downloadErrorCode=0;
                uploadData.errorDetails="Google Drive";
                notifyProgressWithMessage(i,"LOW SPACE ERROR...",true);
                Log.e(TAG, ErrorHandler.getErrorName(0)+"-->"+uploadData.errorDetails);
                stopSelf();
                return;
            }

            Log.i(TAG,"Uploaded file "+uploadData.currentFileIndex+ " of " +uploadData.totalFiles);
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
        uploadData.downloadedSize=uploadData.totalSizeToDownload;
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


        //GETTING ROOT PATH ID
        String rootPathtoId=null;
        String []breaker= uploadData.namesList.get(indexofpaster).split("\\/");//-----[0]=Whatsapp,[1]=media,[2]=file
        if(breaker.length==1)//not a recursive file
        {
            rootPathtoId=uploadData.toRootPath ;
        }
        else    //is a recurssive file
        {
            rootPathtoId=findTrueParent(uploadData.toRootPath,breaker);
        }
        if(rootPathtoId==null)
        {
            uploadData.isDownloadError=true;
            uploadData.downloadErrorCode=4;
            uploadData.errorDetails="Check your internet connection";
            Log.e(TAG, ErrorHandler.getErrorName(4)+"-->"+uploadData.errorDetails);
            stopSelf();
            return;
        }


        String pathfrom= uploadData.pathsList.get(indexofpaster);
        File fromFile = new File(pathfrom);


        //if file is empty do not upload ,just create a new file
        if(fromFile.length()==0)
        {
            com.google.api.services.drive.model.File newFile=new com.google.api.services.drive.model.File();
            newFile.setName(fromFile.getName());

            List<String> list=new ArrayList<>();
            list.add(rootPathtoId);
            newFile.setParents(list);

            newFile.setMimeType(null);

            try
            {
                com.google.api.services.drive.model.File file = GoogleDriveConnection.m_service_client.files().create(newFile).setFields("id").execute();
                //modifying date
                com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
                fileMetadata.setModifiedTime(new DateTime(System.currentTimeMillis()));
                file =  GoogleDriveConnection.m_service_client.files().update(file.getId(), fileMetadata).setFields("id, modifiedTime").execute();
            }
            catch(Exception e)
            {
                uploadData.isDownloadError=true;
                uploadData.downloadErrorCode=4;
                uploadData.errorDetails="'" + uploadData.namesList.get(indexofpaster) + "'" + " upload failed, Check your Internet Connection"+e.getMessage()+e.getLocalizedMessage()+e.getCause();
                Log.e(TAG, ErrorHandler.getErrorName(4)+"-->"+uploadData.errorDetails);
                stopSelf();
                return;
            }

            tinyDB.putString(operationCode+"LastSourcePath",pathfrom);
            return;
        }



        //OPENING INPUT STREAM

        FileInputStream fileInputStream;
        try
        {
            fileInputStream = new FileInputStream(fromFile);
        }
        catch (Exception e)
        {
            uploadData.isDownloadError=true;
            uploadData.downloadErrorCode=1;
            uploadData.errorDetails="'" + pathfrom + "'" + " does't exist OR read access denied";
            Log.e(TAG, ErrorHandler.getErrorName(1)+"-->"+uploadData.errorDetails);
            stopSelf();
            return;
        }






        //CREATE A URL
        if(!resumable)
        {
            HttpURLConnection request;
            int responseCode=-5;
            try
            {
                final GoogleAccountCredential m_credential=GoogleAccountCredential.usingOAuth2(getApplicationContext(), Collections.singleton(DriveScopes.DRIVE));
                m_credential.setSelectedAccountName(GoogleDriveConnection.userName);

                URL url = new URL("https://www.googleapis.com/upload/drive/v3/files?uploadType=resumable");
                request = (HttpURLConnection) url.openConnection();
                request.setRequestMethod("POST");
                request.setDoInput(true);
                request.setDoOutput(true);
                request.setRequestProperty("Authorization", "Bearer " + m_credential.getToken());
                request.setRequestProperty("X-Upload-Content-Type", null);
                request.setRequestProperty("X-Upload-Content-Length", String.format(Locale.ENGLISH, "%d", fromFile.length()));
                request.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                String body = "{\"name\": \"" + fromFile.getName() + "\", \"parents\": [\"" + rootPathtoId+ "\"]}";
                request.setRequestProperty("Content-Length", String.format(Locale.ENGLISH, "%d", body.getBytes().length));
                OutputStream outputStream = request.getOutputStream();
                outputStream.write(body.getBytes());
                outputStream.close();
                request.connect();
                responseCode=request.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK)
                {
                    tinyDB.putString(operationCode+"Url",request.getHeaderField("location"));
                    tinyDB.putLong(operationCode+"ChunkStart",0);
                    // Log.e("upload progress:","successfully initiated a chunked, resumable upload"+"--"+tinyDB.getString(operationCode+"Url"));
                }
                else
                {
                    throw new Exception("_-_");
                }
            }
            catch (GoogleAuthException e)
            {
                uploadData.isDownloadError=true;
                uploadData.downloadErrorCode=13;
                uploadData.errorDetails="account token not found";
                Log.e(TAG, ErrorHandler.getErrorName(13)+"-->"+uploadData.errorDetails);
                stopSelf();
                return;
            }
            catch (Exception e)
            {
                uploadData.isDownloadError=true;
                uploadData.downloadErrorCode=4;
                uploadData.errorDetails="'" + uploadData.namesList.get(indexofpaster) + "'" + " cannot initiate a upload request, Check your Internet Connection";
                Log.e(TAG, ErrorHandler.getErrorName(4)+"-->"+uploadData.errorDetails);
                stopSelf();
                return;
            }
        }
        else
        {
            Log.i(TAG,"resuming...");
        }



        //UPLOADING
        URL url2;
        OutputStream outputStream2;
        final String urlx=tinyDB.getString(operationCode+"Url");
        HttpURLConnection request2;
        long chunkStart=tinyDB.getLong(operationCode+"ChunkStart",0);
        long newchunkStart=0;

        while(true)
        {
            try
            {
                if(uploadData.pauseDownloadingPlease)
                {
                    return;
                }
                if(uploadData.cancelDownloadingPlease)
                {
                    tinyDB.remove(operationCode+"IsRunning");
                    tinyDB.remove(operationCode+"CurrentSourcePath");
                    tinyDB.remove(operationCode+"LastSourcePath");
                    tinyDB.remove(operationCode+"Url");
                    tinyDB.remove(operationCode+"ChunkStart");
                    break;
                }

                url2 = new URL(urlx);
                request2 = (HttpURLConnection) url2.openConnection();
                request2.setRequestMethod("PUT");
                request2.setDoOutput(true);
                request2.setConnectTimeout(10000);
                request2.setRequestProperty("Content-Type", null);
                long uploadedBytes = 2 * 1024 * 1024;
                if (chunkStart + uploadedBytes > fromFile.length())
                {
                    uploadedBytes =  fromFile.length() - chunkStart;
                }
                request2.setRequestProperty("Content-Length", String.format(Locale.ENGLISH, "%d", uploadedBytes));
                request2.setRequestProperty("Content-Range", "bytes " + chunkStart + "-" + (chunkStart + uploadedBytes - 1) + "/" + fromFile.length());

                byte[] buffer = new byte[(int) uploadedBytes];

                try
                {
                    fileInputStream.getChannel().position(chunkStart);
                    if (fileInputStream.read(buffer, 0, (int) uploadedBytes) == -1)
                    {
                        /* break, return, exit*/
                        throw new Exception("-_-");
                    }
                    if(!fromFile.exists())
                        throw new Exception("-_-");
                }
                catch (Exception e)
                {
                    uploadData.isDownloadError=true;
                    uploadData.downloadErrorCode=1;
                    uploadData.errorDetails="'" + pathfrom + "'" + " does't exist OR read access denied";
                    Log.e(TAG, ErrorHandler.getErrorName(1)+"-->"+uploadData.errorDetails);
                    stopSelf();
                    return;
                }


                outputStream2 = request2.getOutputStream();
                outputStream2.write(buffer);
                request2.connect();
                int responseCode=request2.getResponseCode();
                Log.e("connection done","--"+responseCode);
                if (responseCode == 308)
                {
                    String range = request2.getHeaderField("range");
                    newchunkStart=Long.parseLong(range.substring(range.lastIndexOf("-") + 1, range.length())) + 1;
                    uploadData.downloadedSize+=(newchunkStart-chunkStart);
                    if(uploadData.totalSizeToDownload==0)
                    {
                        uploadData.progress=0;
                    }
                    else
                    {
                        uploadData.progress=uploadData.downloadedSize*100/uploadData.totalSizeToDownload;
                    }
                    chunkStart = newchunkStart;
                    tinyDB.putLong(operationCode+"ChunkStart",chunkStart);
                }
                if(responseCode ==200)
                {
                    //upload success
                    newchunkStart=fromFile.length();
                    uploadData.downloadedSize+=(newchunkStart-chunkStart);
                    if(uploadData.totalSizeToDownload==0)
                    {
                        uploadData.progress=0;
                    }
                    else
                    {
                        uploadData.progress=uploadData.downloadedSize*100/uploadData.totalSizeToDownload;
                    }

                    chunkStart = newchunkStart;
                    tinyDB.putLong(operationCode+"ChunkStart",chunkStart);
                    tinyDB.putString(operationCode+"LastSourcePath",pathfrom);
                    break;

                }
                if(responseCode!=308 && responseCode!=200)
                {
                    uploadData.isDownloadError=true;
                    uploadData.downloadErrorCode=13;
                    uploadData.errorDetails="'" + uploadData.namesList.get(indexofpaster) + "'" + " upload failed, Server Error";
                    Log.e(TAG, ErrorHandler.getErrorName(13)+"-->"+uploadData.errorDetails);
                    stopSelf();
                    return;
                }


                if(oldTime< uploadData.timeInSec )
                {
                    notifyProgressPlease(indexofpaster);
                }


            }
            catch (MalformedURLException e)
            {
                uploadData.isDownloadError=true;
                uploadData.downloadErrorCode=13;
                uploadData.errorDetails="'" + uploadData.namesList.get(indexofpaster) + "'" + " upload failed,Invaild Upload Link";
                Log.e(TAG, ErrorHandler.getErrorName(13)+"-->"+uploadData.errorDetails);
                stopSelf();
                return;
            }
            catch (Exception e)
            {
                uploadData.isDownloadError=true;
                uploadData.downloadErrorCode=4;
                uploadData.errorDetails="'" + uploadData.namesList.get(indexofpaster) + "'" + " upload failed, Check your Internet Connection";
                Log.e(TAG, ErrorHandler.getErrorName(4)+"-->"+uploadData.errorDetails);
                stopSelf();
                return;
            }
        }

        tinyDB.putString(operationCode+"LastSourcePath",pathfrom);

    }


    public void foldercopier(int indexofpaster)
    {

        if(tinyDB.getString(operationCode+"CurrentSourcePath").equals(tinyDB.getString(operationCode+"LastSourcePath")))
        {
            //already uploaded
            return;
        }

        String rootPathtoId=null;

        String []breaker= uploadData.namesList.get(indexofpaster).split("\\/");//-----[0]=Whatsapp,[1]=media,[2]=file
        if(breaker.length==1)//not a recursive file
        {
            rootPathtoId=uploadData.toRootPath ;
        }
        else    //is a recurssive file
        {
            rootPathtoId=findTrueParent(uploadData.toRootPath,breaker);
        }

        if(rootPathtoId==null)
        {
            uploadData.isDownloadError=true;
            uploadData.downloadErrorCode=4;
            uploadData.errorDetails="Check your internet connection";
            Log.e(TAG, ErrorHandler.getErrorName(4)+"-->"+uploadData.errorDetails);
            stopSelf();
            return;
        }


        try
        {
            com.google.api.services.drive.model.File folder=new com.google.api.services.drive.model.File();
            folder.setName(breaker[breaker.length-1]);

            List<String> list=new ArrayList<>();
            list.add(rootPathtoId);
            folder.setParents(list);
            folder.setMimeType("application/vnd.google-apps.folder");

            com.google.api.services.drive.model.File file = GoogleDriveConnection.m_service_client.files().create(folder).setFields("id,name").execute();
            Log.e("folder created:",file.getId()+"--"+file.getName());
        }
        catch (Exception e)
        {
            uploadData.isDownloadError=true;
            uploadData.downloadErrorCode=4;
            uploadData.errorDetails="'" + uploadData.namesList.get(indexofpaster) + "'" + " cannot create directory, Check your Internet Connection";
            Log.e(TAG, ErrorHandler.getErrorName(4)+"-->"+uploadData.errorDetails);
            stopSelf();
            return;
        }

        tinyDB.putString(operationCode+"LastSourcePath",uploadData.pathsList.get(indexofpaster));
    }


    @Override
    public void onCreate()
    {
        operationCode=304;
        uploadData= MyCacheData.getUploadDataFromCode(operationCode);
        tinyDB=new TinyDB(getApplicationContext());
        tinyDB.putString(operationCode+"IsRunning",operationCode+" is running");
        uploadData.isServiceRunning=true;
        tasksCache.addTask(operationCode+"");
    }



    @Override
    public IBinder onBind(Intent intent)
    {
        return  null;
    }



    @Override
    public void onDestroy()
    {
        Log.i(TAG,"destroyed");
        uploadData.isServiceRunning=false;//very very important

        if(uploadData.pauseDownloadingPlease)
        {
            notifyProgressWithMessage(uploadData.currentFileIndex,"Uploading Paused...",false);
        }

        if(uploadData.cancelDownloadingPlease || uploadData.totalFiles==uploadData.currentFileIndex)
        {
            tinyDB.remove(operationCode+"IsRunning");
            tinyDB.remove(operationCode+"CurrentSourcePath");
            tinyDB.remove(operationCode+"LastSourcePath");
            tinyDB.remove(operationCode+"Url");
            tinyDB.remove(operationCode+"ChunkStart");

            tasksCache.removeTask(operationCode+"");
        }

    }


    public void uploadToTinyDB1()
    {
        tinyDB.putString(operationCode+"currentFileName",uploadData.currentFileName);
        tinyDB.putInt(operationCode+"currentFileIndex",uploadData.currentFileIndex);
    }


    public void uploadToTinyDB2()
    {
        tinyDB.putLong(operationCode+"progress",uploadData.progress);
        tinyDB.putLong(operationCode+"downloadedSize",uploadData.downloadedSize);
    }


    public String findTrueParent(String pID,String[] breaker)
    {
        try
        {
            com.google.api.services.drive.Drive.Files.List request;
            for(int i=0;i<breaker.length-1;i++)
            {

                request= GoogleDriveConnection.m_service_client.files().list().setFields("files(id)").setQ("'" +pID+ "' in parents and name='"+breaker[i]+"'").setOrderBy("modifiedTime desc");
                List<com.google.api.services.drive.model.File> files = new ArrayList<com.google.api.services.drive.model.File>();
                do
                {
                    FileList filelist = request.execute();
                    files.addAll(filelist.getFiles());
                    request.setPageToken(filelist.getNextPageToken());
                }
                while (request.getPageToken() != null && request.getPageToken().length() > 0);

                pID=files.get(0).getId();
            }

            return pID;

        }
        catch (Exception e)
        {
            return null;
        }

    }


    private void notifyProgressPlease(int index)
    {
        oldTime= uploadData.timeInSec;
        mBuilder.setProgress(100,(int)uploadData.progress,false);
        mBuilder.setContentText(uploadData.namesList.get(index));
        mNotifyManager.notify(operationCode,mBuilder.build());
    }

    private void notifyProgressWithMessage(int index,String msg,boolean vibrate)
    {
        mBuilder.setContentTitle(msg)
                .setProgress(100,(int)uploadData.progress,false)
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
    304LastSourcePath:
    it is used to deal with cases when uploading of that file is completed but is asked again to upload next time when service starts
    case 1: when file is uploaded and service stops while doing isMoving OPERATION(which may be time taking)
    case 2: when file is uploaded but user asks it to pause

    304CurrentSourcePath:
    it holds the value,from the time when uploading starts till that file is uploaded(_and OR isMoving)


    resume:
    if ( 304Url )

    uploadToTinyDB1() deals with values of 304 cache
    uploadToTinyDB2()  deals with values of 304 cache and is run only when file is uploaded

 */