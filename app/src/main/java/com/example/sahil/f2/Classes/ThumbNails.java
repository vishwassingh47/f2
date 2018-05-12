package com.example.sahil.f2.Classes;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import com.example.sahil.f2.Cache.ThumbNailCache;
import com.example.sahil.f2.Classes.DropBox.DropBoxConnection;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.concurrent.TimeoutException;

import static com.example.sahil.f2.MainActivity.Physical_Storage_PATHS;

/**
 * Created by hit4man47 on 11/17/2017.
 */

public class ThumbNails
{
    private Context context;
    private String path;

    private final String TAG="ThumbNails";
    private final int iconType,storagePathType,modValue;


    public ThumbNails(Context context,String path,final int iconType,final int storagePathType,final int modValue)
    {
        this.context=context;
        this.path=path;
        this.iconType=iconType;
        this.storagePathType=storagePathType;
        this.modValue=modValue;
    }


    public void start()
    {
        Thread thumbThread=new Thread()
            {
                @Override
                public void run()
                {
                    switch (storagePathType)
                    {
                        case 1:
                            loadLocalThumbNail();
                            break;
                        case 2:
                            loadLocalThumbNail();
                            break;
                        case 3:
                            loadLocalThumbNail();
                            break;
                        case 4:
                            loadDriveThumbNail();
                            break;
                        case 5:
                            loadDropBoxThumbNail();
                            break;
                        case 6:
                            loadFtpThumbNail();
                            break;
                    }

                    switch (modValue)
                    {
                        case 0:
                            ThumbNailCache.mod0_minus();
                            break;
                        case 1:
                            ThumbNailCache.mod1_minus();
                            break;
                        case 2:
                            ThumbNailCache.mod2_minus();
                            break;
                    }

                }
            };
            thumbThread.start();
    }

    private void loadLocalThumbNail()
    {
        Bitmap bitmap=null;
        try
        {
            switch (iconType)
            {
                case 1:
                    bitmap=getLocalImageThumb(2);
                    break;
                case 2:
                    bitmap=getLocalVideoThumb();
                    break;
                case 3:
                    bitmap=getLocalAudioThumb();
                    break;
                case 4:
                    bitmap=getLocalApkThumb();
                    break;
            }

            addToThumb(bitmap);

        }
        catch (OutOfMemoryError e)
        {
            Log.e(TAG,"############################OutOfMemory");
            ThumbNailCache.hardClear();
        }
        catch (TimeoutException e)
        {
            Log.e(TAG,"*********************************************************TIMEOUT EXCEPTION");
        }
        catch (Exception e)
        {
            Log.e(TAG,"*********************************************************EXCEPTION"+e.getMessage());
        }
    }

    private void loadDriveThumbNail()
    {

        Bitmap bitmap=null;
        try
        {
            bitmap=getDriveThumb();
            addToThumb(bitmap);
        }
        catch (OutOfMemoryError e)
        {
            Log.e(TAG,"############################OutOfMemory");
            ThumbNailCache.hardClear();
        }
        catch (TimeoutException e)
        {
            Log.e(TAG,"*********************************************************TIMEOUT EXCEPTION");
        }
        catch (Exception e)
        {
            Log.e(TAG,"*********************************************************EXCEPTION"+e.getMessage());
        }

    }

    private void loadDropBoxThumbNail()
    {
        Bitmap bitmap=null;
        try
        {
            bitmap=getDropBoxThumb();
            addToThumb(bitmap);
        }
        catch (OutOfMemoryError e)
        {
            Log.e(TAG,"############################OutOfMemory");
            ThumbNailCache.hardClear();
        }
        catch (TimeoutException e)
        {
            Log.e(TAG,"*********************************************************TIMEOUT EXCEPTION");
        }
        catch (Exception e)
        {
            Log.e(TAG,"*********************************************************EXCEPTION"+e.getMessage());
        }
    }

    private void loadFtpThumbNail()
    {
        addToThumb(null);
    }






    private Bitmap getLocalImageThumb(int quality) throws OutOfMemoryError,TimeoutException,Exception
    {

        ContentResolver cr=context.getContentResolver();
        Bitmap bitmap=null;


        //***********************TRYING TO FETCH ALREADY EXISTING THUMBNAIL***********************
        /*
         try
        {
            Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.MediaColumns._ID }, MediaStore.MediaColumns.DATA + "=?", new String[] {path}, null);

            if (ca != null && ca.getCount()>0 && ca.moveToFirst())
            {
                int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
                bitmap=MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null );
                ca.close();
            }
            else
            {
                ca.close();
                bitmap=null;
            }
        }
        catch (Exception e)
        {
            Log.e("Error:","error while getting thumbnail from mediaStore");
            bitmap=null;
        }
         */




        //****************IF NO THUMBNAIL PRE-EXIST CREATE ONE*************
        if(bitmap==null)
        {

            try
            {

                BitmapFactory.Options bounds = new BitmapFactory.Options();

                bounds.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, bounds);

                if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
                    bitmap=null;
                else
                {

                    int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight : bounds.outWidth;
                    Log.e("original size:",originalSize+"--");
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    switch (quality)
                    {
                        case 1:
                            opts.inSampleSize = originalSize / 128;
                            break;
                        case 2:
                            opts.inSampleSize=8;
                            break;
                        case 3:

                            break;
                        case 4:

                            break;
                        case 5:
                            opts.inSampleSize=1;
                            break;
                        default:
                            opts.inSampleSize = originalSize / 128;
                    }



                    bitmap=BitmapFactory.decodeFile(path, opts);

                }

            }
            catch(Exception ex)
            {
                bitmap=null;
            }
        }
        return  bitmap;
    }

    private Bitmap getLocalVideoThumb() throws OutOfMemoryError,TimeoutException,Exception
    {
        ContentResolver cr=context.getContentResolver();
        Bitmap bitmap=null;

        //***********************TRYING TO FETCH ALREADY EXISTING THUMBNAIL***********************
        /*
        try
        {
            Cursor ca = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.MediaColumns._ID }, MediaStore.MediaColumns.DATA + "=?", new String[] {path}, null);

            if (ca != null && ca.getCount()>0 && ca.moveToFirst())
            {
                int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));

                bitmap=MediaStore.Video.Thumbnails.getThumbnail(cr, id, MediaStore.Video.Thumbnails.MICRO_KIND, null );
                ca.close();
            }
            else
            {
                ca.close();
                bitmap=null;
            }
        }
        catch (Exception e)
        {
            Log.e("Error:","error while getting thumbnail from mediaStore");
            bitmap=null;
        }

*/
        //****************IF NO THUMBNAIL PRE-EXIST CREATE ONE*************
        if(bitmap==null)
        {
            Log.e("creating self","---");
            try
            {
                final int THUMBNAIL_SIZE = 64;
                //Log.e("#############","1");
                bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
                //Log.e("#############","2");
                //Float width = new Float(bitmap.getWidth());
                //Float height = new Float(bitmap.getHeight());
                //Float ratio = width/height;
                //Log.e("#############","3");
                //bitmap = Bitmap.createScaledBitmap(bitmap, (int)(THUMBNAIL_SIZE * ratio), THUMBNAIL_SIZE, false);
                //Log.e("#############","4");
            }
            catch(Exception ex)
            {
                bitmap=null;
            }
        }

        return  bitmap;
    }

    private Bitmap getLocalApkThumb() throws OutOfMemoryError,TimeoutException,Exception
    {
        Bitmap bitmap=null;

        try
        {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageArchiveInfo(path, 0);
            // // the secret are these two lines....
            pi.applicationInfo.sourceDir = path;
            pi.applicationInfo.publicSourceDir = path;
            // //
            Drawable drawable= pi.applicationInfo.loadIcon(pm);
            bitmap=((BitmapDrawable)drawable).getBitmap();
        }
        catch(Exception e)
        {
            bitmap=null;
        }

        return bitmap;
    }

    private Bitmap getLocalAudioThumb() throws OutOfMemoryError,TimeoutException,Exception
    {
        Bitmap bitmap=null;

        //***********************TRYING TO FETCH ALREADY EXISTING THUMBNAIL***********************
        try
        {
            MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(path);
            byte[] data=mediaMetadataRetriever.getEmbeddedPicture();
            if(data!=null)
            {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                return bitmap;
            }
            else
            {
                return null;
            }
        }
        catch (Exception e)
        {
            Log.e("Error:","error while getting thumbnail from mediaStore");
        }
        return null;
    }




    private Bitmap getDriveThumb() throws OutOfMemoryError,TimeoutException,Exception
    {
        Bitmap bitmap=null;
        String url=null,name=null;
        int lastindex=path.lastIndexOf("@#$");
        url=path.substring(0,lastindex);
        name=path.substring(lastindex+3);



        final String DRIVE_THUMB_PATH=Physical_Storage_PATHS.get(0)+"/f2/Cache/.ThumbNails/Google Drive";
        File mainDirectory=new File(DRIVE_THUMB_PATH);
        if(!mainDirectory.exists())
        {
            boolean x=mainDirectory.mkdirs();
            if(!x)
            {
                Log.e(TAG,"DIRECTORY FAILED TO CREATE");
            }
        }

        if(!new File(DRIVE_THUMB_PATH+"/"+name).exists())
        {
            File toDownload=new File(DRIVE_THUMB_PATH+"/"+name);
            BufferedInputStream in = null;
            FileOutputStream fout = null;
            try
            {
                in = new BufferedInputStream(new URL(url).openStream());
                fout = new FileOutputStream(toDownload);
                final byte data[] = new byte[1024];
                int count;
                while ((count = in.read(data, 0, 1024)) != -1)
                {
                    fout.write(data, 0, count);
                }
                if(in!=null)
                in.close();
                if(fout!=null)
                fout.close();
            }
            catch (Exception e)
            {
                boolean x=toDownload.delete();
                return null;
            }
        }
        else
        {
            Log.e("Downloaded cache found","drives");
        }


        path=DRIVE_THUMB_PATH+"/"+name;
        bitmap= getLocalImageThumb(5);
        path=url+"@#$"+name;
        return bitmap;

    }




    private Bitmap getDropBoxThumb() throws OutOfMemoryError,TimeoutException,Exception
    {
        Bitmap bitmap=null;
        String url=null,name=null;
        int lastindex=path.lastIndexOf("@#$");
        url=path.substring(0,lastindex);
        name=path.substring(lastindex+3);

        final String DROPBOX_THUMB_PATH=Physical_Storage_PATHS.get(0)+"/f2/Cache/.ThumbNails/DropBox";
        File mainDirectory=new File(DROPBOX_THUMB_PATH);
        if(!mainDirectory.exists())
        {
            boolean x=mainDirectory.mkdirs();
            if(!x)
            {
                Log.e(TAG,"DIRECTORY FAILED TO CREATE");
            }
        }

        if(!new File(DROPBOX_THUMB_PATH+"/"+name).exists())
        {
            File toDownload=new File(DROPBOX_THUMB_PATH+"/"+name);
            BufferedInputStream in = null;
            FileOutputStream fout = null;
            try
            {
                fout = new FileOutputStream(toDownload);
                DropBoxConnection.mDbxClient.files().getThumbnail(url).download(fout);
                if(fout!=null)
                    fout.close();
            }
            catch (Exception e)
            {
                boolean x=toDownload.delete();
                return null;
            }
        }
        else
        {
            Log.e("Downloaded cache found","dropBox");
        }

        path=DROPBOX_THUMB_PATH+"/"+name;
        bitmap= getLocalImageThumb(5);
        path=url+"@#$"+name;
        return bitmap;

    }







    private void addToThumb(Bitmap bitmap)
    {
        synchronized (ThumbNailCache.path_bitmap_map)
        {
            if(!ThumbNailCache.path_bitmap_map.containsKey(path))
            {
                //ADD NULL BITMAP ALSO
                ThumbNailCache.path_bitmap_map.put(path,bitmap);
            }
        }
        if(bitmap!=null)
        {
            ThumbNailCache.bitmapSize+=bitmap.getByteCount();
        }
        else
        {
            Log.e(TAG,"null bitmap found");
        }
    }

}
