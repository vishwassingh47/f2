package com.example.sahil.f2.OperationTheater;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.users.SpaceUsage;
import com.example.sahil.f2.Cache.CopyData;
import com.example.sahil.f2.Cache.DownloadData;
import com.example.sahil.f2.Cache.MyCacheData;

import com.example.sahil.f2.Cache.UploadData;
import com.example.sahil.f2.Cache.tasksCache;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;
import com.example.sahil.f2.Classes.DropBox.GoogleDriveConnection;
import com.example.sahil.f2.Classes.OneFile;
import com.example.sahil.f2.Classes.Page;
import com.example.sahil.f2.FunkyAdapters.ClipBoardAdapter;
import com.example.sahil.f2.GokuFrags.storageAnalyser;
import com.example.sahil.f2.GokuFrags.storagePager;
import com.example.sahil.f2.MainActivity;
import com.example.sahil.f2.Maintenance.TinyDB;
import com.example.sahil.f2.R;
import com.example.sahil.f2.Rooted.SuperUser;
import com.example.sahil.f2.StorageAccessFramework;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.api.services.drive.model.About;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;

import static com.example.sahil.f2.OperationTheater.PagerXUtilities.getExternalSdCardPath;
import static com.example.sahil.f2.OperationTheater.PagerXUtilities.getFrameIdFromPageIndex;
import static com.example.sahil.f2.OperationTheater.PagerXUtilities.getStorageIdFromPageName;
import static com.example.sahil.f2.OperationTheater.PagerXUtilities.isExternalSdCardPath;
import static com.example.sahil.f2.OperationTheater.PagerXUtilities.isRootPath;
import static com.example.sahil.f2.OperationTheater.PagerXUtilities.isValidStoragePage;

/**
 * Created by hit4man47 on 12/23/2017.
 */

public class PasteButtonDecisionMaker
{
    public FloatingActionButton pasteButton,cancelButton,pasteClipBoard;
    public FloatingActionMenu pasteMenu;
    private final MainActivity mainActivity;
    private final TinyDB tinyDB;
    private final HelpingBot helpingBot;
    //CONSTRUCTOR WILL BE CALLED ONLY ONCE

    public PasteButtonDecisionMaker(Activity activity)
    {
        mainActivity=(MainActivity) activity;
        helpingBot=new HelpingBot();
        pasteButton = (FloatingActionButton) activity.findViewById(R.id.fab_paste);
        pasteClipBoard= (FloatingActionButton) activity.findViewById(R.id.fab_paste_clipboard);
        cancelButton = (FloatingActionButton)activity.findViewById(R.id.fab_paste_cancel);
        pasteMenu=(FloatingActionMenu) activity.findViewById(R.id.fab_paste_menu);
        pasteMenu.setClosedOnTouchOutside(true);

        tinyDB=new TinyDB(mainActivity);
        setClickListeners();
    }

    private void setClickListeners()
    {
        pasteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.e("Button pressed:","_______________________________________");
                closePasteMenu();
                pasteButtonClicked();
            }
        });

        pasteClipBoard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ClipBoardAdapter clipBoardAdapter=new ClipBoardAdapter(mainActivity,1212,PasteClipBoard.nameList,PasteClipBoard.sizeLongList);
                final Dialog dialog = new Dialog(mainActivity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layoutof_clipboard);
                dialog.setCanceledOnTouchOutside(true);
                ListView listView=(ListView) dialog.findViewById(R.id.clipboard_list);
                listView.setAdapter(clipBoardAdapter);
                Button hide=(Button) dialog.findViewById(R.id.clipboard_btn);
                hide.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dialog.cancel();
                    }
                });
                TextView tv_size=(TextView) dialog.findViewById(R.id.clipboard_sizeTotal);
                TextView tv_items=(TextView) dialog.findViewById(R.id.clipboard_itemsTotal);

                long totalSize=0;
                for(Long x:PasteClipBoard.sizeLongList)
                {
                    totalSize+=x;
                }

                tv_items.setText("Total Items : "+PasteClipBoard.nameList.size());
                tv_size.setText("Total Size : "+helpingBot.sizeinwords(totalSize));


                closePasteMenu();
                dialog.show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PasteClipBoard.clear();
                closePasteMenu();
                hidePasteMenu();
                //cancelButton.hide(true);
                //cancelButton.setVisibility(View.GONE);
                //pasteButton.hide(true);
                //pasteButton.setVisibility(View.GONE);
            }
        });
        pasteMenu.setOnMenuButtonClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.e("menu cicked","+++++++++++++");
                if(pasteMenu.isOpened())
                {
                    closePasteMenu();
                }
                else
                {
                    openPasteMenu();
                }
            }
        });
    }


    /**
     *
     * @param pageIndex if >=0 : it is called from fragment and should only work if that fragment is visible
     *                  if =-1 : viewPager called it after every swipe and should always work
     */
    public void showHidePasteButton(int pageIndex)
    {
        Log.e("showHidePasteButton"+pageIndex,"called");
        if(pageIndex>=0)
        {
            //called from fragment
            if(mainActivity.viewPager.getCurrentItem()!=pageIndex)
            {
                Log.e("showHidePasteButton"+pageIndex,"background");
                //not in foreground and should not work
                return;
            }
        }
        try
        {
            if(PasteClipBoard.pathList.size()>0)
            {
                Log.e("showHidePasteButton"+pageIndex,"showing");
                showPasteMenu();
                //pasteButton.show();
                //pasteButton.setVisibility(View.VISIBLE);
                //cancelButton.show();
                //cancelButton.setVisibility(View.VISIBLE);
            }
            else
            {
                if(pasteMenu.getVisibility()==View.VISIBLE  )
                {
                    Log.e("showHidePasteButton"+pageIndex,"hiding");
                    hidePasteMenu();
                }
                //pasteButton.hide();
                //pasteButton.setVisibility(View.GONE);
                //cancelButton.hide();
                //cancelButton.setVisibility(View.GONE);
            }
        }
        catch (Exception e)
        {
            Log.e("MainActivity","showPasteButton"+e.getMessage()+e.getLocalizedMessage());
        }
    }

    private boolean pasteButtonToEnable() throws Exception
    {
        int currentVisiblePageIndex=mainActivity.viewPager.getCurrentItem();
        int currentFrameId=getFrameIdFromPageIndex(currentVisiblePageIndex);

        Page page=MainActivity.pageList.get(currentVisiblePageIndex);
        if(isValidStoragePage(page) && PasteClipBoard.pathList.size()>0)
        {
            if(page.getPageId()==11)
            {
                storageAnalyser fragment=(storageAnalyser) mainActivity.getSupportFragmentManager().findFragmentById(currentFrameId);
                return fragment.isFolderOk;
            }
            if(page.getPageId()==12345 || (page.getPageId()==15 && !page.getCurrentPath().equals("FtpClient")))
            {
                storagePager fragment=(storagePager)mainActivity.getSupportFragmentManager().findFragmentById(currentFrameId);
                return fragment.isFolderOk;
            }
        }
        return  false;
    }


    private void showPasteMenu()
    {
        //pasteMenu.showMenu(true);
        pasteMenu.setVisibility(View.VISIBLE);
    }

    private void hidePasteMenu()
    {
        //pasteMenu.hideMenu(true);
        pasteMenu.setVisibility(View.GONE);
    }


    public void closePasteMenu()//closes the menu opened
    {
        pasteMenu.close(true);
        mainActivity.touchBlocker.setAlpha(0);
    }


    private void openPasteMenu()//open menu
    {
        pasteMenu.open(true);

        mainActivity.touchBlocker.setAlpha(0.8f);
    }


    private void pasteButtonClicked()
    {

        if(PasteClipBoard.pathList.size()<=0)
        {
            PasteClipBoard.clear();
            Toast.makeText(mainActivity, "nothing to paste", Toast.LENGTH_SHORT).show();
            hidePasteMenu();
        }
        else
        {
            try
            {
                if(!pasteButtonToEnable())
                {
                    Toast.makeText(mainActivity, "Cannot paste here", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            catch (Exception e)
            {
                Toast.makeText(mainActivity, "Cannot paste here", Toast.LENGTH_SHORT).show();
                return;
            }

            int currentVisiblePageIndex=mainActivity.viewPager.getCurrentItem();
            Page page=MainActivity.pageList.get(currentVisiblePageIndex);


            PasteClipBoard.toRootPath=page.getCurrentPath();
            PasteClipBoard.toStorageCode=getStorageIdFromPageName(page.getName());


            boolean isToPathCloud=PasteClipBoard.toStorageCode==4 || PasteClipBoard.toStorageCode==5 || PasteClipBoard.toStorageCode==6;
            boolean isFromPathCloud=PasteClipBoard.fromStorageCode==4 || PasteClipBoard.fromStorageCode==5 ||  PasteClipBoard.fromStorageCode==6;

            if(PasteClipBoard.fromStorageCode==6 && PasteClipBoard.toStorageCode==6)
            {
                mainActivity.showSmackBar("FTP to FTP transfer is not available,Please Download the file to Local Storage and then Upload to Ftp Server");
                return;
            }

            if(isFromPathCloud && isToPathCloud && PasteClipBoard.fromStorageCode!=PasteClipBoard.toStorageCode)
            {
                //dropbox to drive or  drive to FTP or vice-versa
                mainActivity.showSmackBar("Cloud to Cloud transfer is not available,Please Download the file to Local Storage and then Upload to Cloud");
                //Toast.makeText(mainActivity, , Toast.LENGTH_LONG).show();
                return;
            }
            //READY TO PASTE
            hidePasteMenu();
            //cancelButton.hide();
            //cancelButton.setVisibility(View.GONE);
            //pasteButton.hide();
            //pasteButton.setVisibility(View.GONE);

            if(PasteClipBoard.toStorageCode==6)
            {
                copyToFtpServer();
                return;
            }
            else
            {
                SizeFetcherAsyncTask myAsyncTask=new SizeFetcherAsyncTask(PasteClipBoard.toStorageCode);
                myAsyncTask.execute();
            }


        }
    }


    private class SizeFetcherAsyncTask extends AsyncTask<String, Integer, String>
    {
        private final int sId;
        private ProgressDialog pd;
        SizeFetcherAsyncTask(int sid)
        {
            this.sId=sid;
        }
        protected void onPreExecute()
        {
            super.onPreExecute();
            pd=new ProgressDialog(mainActivity);
            pd.setMessage("Checking available space");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... arg0)
        {
            if(sId<=3)
            {

            }
            if(sId==4)
            {
                try
                {
                    About about = GoogleDriveConnection.m_service_client.about().get().setFields("storageQuota, user").execute();
                    GoogleDriveConnection.totalSize = about.getStorageQuota().getLimit();
                    GoogleDriveConnection.usedSize = about.getStorageQuota().getUsageInDrive();
                }
                catch(IOException e)
                {
                    return null;
                }
                catch (Exception e)
                {
                    return null;
                }
            }
            if(sId==5)
            {
                try
                {
                    SpaceUsage spaceUsage =DropBoxConnection.mDbxClient.users().getSpaceUsage();
                    DropBoxConnection.totalSize=spaceUsage.getAllocation().getIndividualValue().getAllocated();
                    DropBoxConnection.usedSize=spaceUsage.getUsed();
                }
                catch(DbxException e)
                {
                    return null;
                }
                catch (Exception e)
                {
                    return null;
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(String toPath)
        {
            pd.cancel();
            if(sId<=3)
            {
                copyToLocal();
            }
            if(sId==4)
            {
                copyToDrive();
            }
            if(sId==5)
            {
                copyToDropbox();
            }
        }

    }



    //called when TO PASTE info is set
    // it will copy the paste data to cache and call machines
    void copyToLocal()
    {
        final String toFolderPath=PasteClipBoard.toRootPath;
        final OneFile oneFile=new OneFile(toFolderPath,mainActivity);


        if(!oneFile.isCanWrite())
        {
            if(oneFile.isJavaFile() && oneFile.isExist() && isExternalSdCardPath(toFolderPath))
            {
                String sdCardPath=getExternalSdCardPath(toFolderPath);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    if(MainActivity.SDCardUriMap.get(sdCardPath)==null)
                    {
                        StorageAccessFramework storageAccessFramework=new StorageAccessFramework(mainActivity);
                        storageAccessFramework.showSaf(1,sdCardPath);
                        return;
                    }
                }
                else
                {
                    //media store hack
                }
            }
            else
            {
                if(!SuperUser.hasUserEnabledSU)
                {
                    Toast.makeText(mainActivity, "Root Access Required", Toast.LENGTH_SHORT).show();
                    showPasteMenu();
                    return;
                }
            }
        }


        //FROM LOCAL STORAGE
        if(PasteClipBoard.fromStorageCode>=1 && PasteClipBoard.fromStorageCode<=3)
        {
            CopyData copyData;
            copyData= MyCacheData.getCopyDataFromCode(101);

            if(!copyData.isServiceRunning && !tasksCache.tasksId.contains("101"))
            {
                //second condrion for case when some previous task has failed with error and thus not to assign that service again to someone else
                copyData.getPasterDataAndResetPasterAndTinyDB(tinyDB);
                CopingMachine copingMachine=new CopingMachine(mainActivity,101);
                copingMachine.copying();
                return;
            }

            copyData= MyCacheData.getCopyDataFromCode(102);
            if(!copyData.isServiceRunning && !tasksCache.tasksId.contains("102"))
            {
                //second condrion for case when some previous task has failed with error and thus not to assign that service again to someone else
                copyData.getPasterDataAndResetPasterAndTinyDB(tinyDB);
                CopingMachine copingMachine=new CopingMachine(mainActivity,102);
                copingMachine.copying();
                return;
            }
            showHidePasteButton(-1);
            Toast.makeText(mainActivity, "server too busy", Toast.LENGTH_SHORT).show();
            return;

        }

        if(PasteClipBoard.fromStorageCode==4)
        {
            //downloading to internal from Google Drive

            DownloadData downloadData;
            downloadData= MyCacheData.getDownloadDataFromCode(203);

            if(!downloadData.isServiceRunning && !tasksCache.tasksId.contains("203"))
            {
                downloadData.getPasterDataAndResetPasterAndTinyDB(tinyDB);
                DownloadingMachine downloadingMachine=new DownloadingMachine(mainActivity,203);
                downloadingMachine.downloading();
                return;
            }

            downloadData= MyCacheData.getDownloadDataFromCode(204);
            if(!downloadData.isServiceRunning && !tasksCache.tasksId.contains("204"))
            {
                downloadData.getPasterDataAndResetPasterAndTinyDB(tinyDB);
                DownloadingMachine downloadingMachine=new DownloadingMachine(mainActivity,204);
                downloadingMachine.downloading();
                return;
            }

            showHidePasteButton(-1);
            Toast.makeText(mainActivity, "server too busy", Toast.LENGTH_SHORT).show();
            return;
        }


        if(PasteClipBoard.fromStorageCode==5)
        {
            //downloading to internal from dropbox

            DownloadData downloadData;
            downloadData= MyCacheData.getDownloadDataFromCode(201);

            if(!downloadData.isServiceRunning && !tasksCache.tasksId.contains("201"))
            {
                downloadData.getPasterDataAndResetPasterAndTinyDB(tinyDB);
                DownloadingMachine downloadingMachine=new DownloadingMachine(mainActivity,201);
                downloadingMachine.downloading();
                return;
            }

            downloadData= MyCacheData.getDownloadDataFromCode(202);
            if(!downloadData.isServiceRunning && !tasksCache.tasksId.contains("202"))
            {
                downloadData.getPasterDataAndResetPasterAndTinyDB(tinyDB);
                DownloadingMachine downloadingMachine=new DownloadingMachine(mainActivity,202);
                downloadingMachine.downloading();
                return;
            }

            showHidePasteButton(-1);
            Toast.makeText(mainActivity, "server too busy", Toast.LENGTH_SHORT).show();
            return;
        }

        if(PasteClipBoard.fromStorageCode==6)
        {
            //downloading to internal/sd from FTP
            DownloadData downloadData;
            downloadData= MyCacheData.getDownloadDataFromCode(205);

            if(!downloadData.isServiceRunning && !tasksCache.tasksId.contains("205"))
            {
                downloadData.getPasterDataAndResetPasterAndTinyDB(tinyDB);
                DownloadingMachine downloadingMachine=new DownloadingMachine(mainActivity,205);
                downloadingMachine.downloading();
                return;
            }

            downloadData= MyCacheData.getDownloadDataFromCode(206);

            if(!downloadData.isServiceRunning && !tasksCache.tasksId.contains("206"))
            {
                downloadData.getPasterDataAndResetPasterAndTinyDB(tinyDB);
                DownloadingMachine downloadingMachine=new DownloadingMachine(mainActivity,206);
                downloadingMachine.downloading();
                return;
            }


            showHidePasteButton(-1);
            Toast.makeText(mainActivity, "server too busy", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private void copyToFtpServer()
    {
        //FROM LOCAL STORAGE  //uploadinf to ftp server
        if(PasteClipBoard.fromStorageCode>=1 && PasteClipBoard.fromStorageCode<=3 )
        {
            DownloadData downloadData;
            downloadData= MyCacheData.getDownloadDataFromCode(305);

            if(!downloadData.isServiceRunning && !tasksCache.tasksId.contains("305"))
            {
                downloadData.getPasterDataAndResetPasterAndTinyDB(tinyDB);
                DownloadingMachine downloadingMachine=new DownloadingMachine(mainActivity,305);
                downloadingMachine.downloading();
                return;
            }

            downloadData= MyCacheData.getDownloadDataFromCode(306);

            if(!downloadData.isServiceRunning && !tasksCache.tasksId.contains("306"))
            {
                downloadData.getPasterDataAndResetPasterAndTinyDB(tinyDB);
                DownloadingMachine downloadingMachine=new DownloadingMachine(mainActivity,306);
                downloadingMachine.downloading();
                return;
            }

            showHidePasteButton(-1);
            Toast.makeText(mainActivity, "server too busy", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void copyToDropbox()
    {
        if(!mainActivity.isNetworkAvailable())
        {
            Toast.makeText(mainActivity, "No Internet Connection Found", Toast.LENGTH_SHORT).show();
            showHidePasteButton(-1);
            return;
        }

        //FROM LOCAL STORAGE
        if(PasteClipBoard.fromStorageCode!=5 )
        {
            UploadData uploadData;
            uploadData=MyCacheData.getUploadDataFromCode(301);
            if(!uploadData.isServiceRunning && !tasksCache.tasksId.contains("301"))
            {
                //second condrion for case when some previous task has failed with error and thus not to assign that service again to someone else
                uploadData.getPasterDataAndResetPasterAndTinyDB(tinyDB);
                UploadingMachine uploadingMachine=new UploadingMachine(mainActivity,301);
                uploadingMachine.uploading();
                return;
            }

            uploadData=MyCacheData.getUploadDataFromCode(302);
            if(!uploadData.isServiceRunning && !tasksCache.tasksId.contains("302"))
            {
                //second condrion for case when some previous task has failed with error and thus not to assign that service again to someone else
                uploadData.getPasterDataAndResetPasterAndTinyDB(tinyDB);
                UploadingMachine uploadingMachine=new UploadingMachine(mainActivity,302);
                uploadingMachine.uploading();
                return;
            }

            showHidePasteButton(-1);
            Toast.makeText(mainActivity, "server too busy", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            //dropbox to dropbox
            DropBoxMovement dropBoxMovement=new DropBoxMovement(mainActivity);
            dropBoxMovement.start();
            return;
        }

    }

    private void copyToDrive()
    {
        if(!mainActivity.isNetworkAvailable())
        {
            Toast.makeText(mainActivity, "No Internet Connection Found", Toast.LENGTH_SHORT).show();
            return;
        }

        //FROM LOCAL STORAGE
        if(PasteClipBoard.fromStorageCode!=4)
        {
            UploadData uploadData;
            uploadData=MyCacheData.getUploadDataFromCode(303);
            if(!uploadData.isServiceRunning && !tasksCache.tasksId.contains("303"))
            {
                //second condrion for case when some previous task has failed with error and thus not to assign that service again to someone else
                uploadData.getPasterDataAndResetPasterAndTinyDB(tinyDB);
                UploadingMachine uploadingMachine=new UploadingMachine(mainActivity,303);
                uploadingMachine.uploading();
                return;
            }

            uploadData=MyCacheData.getUploadDataFromCode(304);
            if(!uploadData.isServiceRunning && !tasksCache.tasksId.contains("304"))
            {
                //second condrion for case when some previous task has failed with error and thus not to assign that service again to someone else
                uploadData.getPasterDataAndResetPasterAndTinyDB(tinyDB);
                UploadingMachine uploadingMachine=new UploadingMachine(mainActivity,304);
                uploadingMachine.uploading();
                return;
            }

            showHidePasteButton(-1);
            Toast.makeText(mainActivity, "server too busy", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            //drive to drive
            GoogleDriveMovement googleDriveMovement=new GoogleDriveMovement(mainActivity);
            googleDriveMovement.start();
        }

    }


}
