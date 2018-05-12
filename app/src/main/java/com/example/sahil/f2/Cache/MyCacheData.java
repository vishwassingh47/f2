package com.example.sahil.f2.Cache;

/**
 * Created by hit4man47 on 1/3/2018.
 */

public class MyCacheData
{
    public static DownloadData downloadData201=null;
    public static DownloadData downloadData202=null;
    public static DownloadData downloadData203=null;
    public static DownloadData downloadData204=null;
    public static DownloadData downloadData205=null;
    public static DownloadData downloadData206=null;
    public static DownloadData downloadData305=null;
    public static DownloadData downloadData306=null;
    public static UploadData uploadData301=null;
    public static UploadData uploadData302=null;
    public static UploadData uploadData303=null;
    public static UploadData uploadData304=null;
    public static CopyData copyData101=null;
    public static CopyData copyData102=null;
    public static GalleryData galleryData1=null;
    public static GalleryData galleryData2=null;
    public static GalleryData galleryData3=null;
    public static GalleryData galleryData4=null;
    public static DeleteData deleteData1=null;
    public static DeleteData deleteData2=null;
    public static SearchData searchData1=null;
    public static SearchData searchData2=null;
    public static SearchData searchData3=null;
    public static SearchData searchData4=null;
    public static SearchData searchData5=null;
    public static StorageAnalyserData storageAnalyserData1=null;
    public static StorageAnalyserData storageAnalyserData2=null;
    public static StorageAnalyserData storageAnalyserData3=null;
    public static StorageAnalyserData storageAnalyserData4=null;
    public static StorageAnalyserData storageAnalyserData5=null;
    public static UnInstallData unInstallData=null;
    public static InstallData installData=null;


    public static DownloadData getDownloadDataFromCode(int code)
    {
        switch (code)
        {
            case 201:
                if(downloadData201==null)
                {
                    downloadData201=new DownloadData("201");
                }
                return downloadData201;
            case 202:
                if(downloadData202==null)
                {
                    downloadData202=new DownloadData("202");
                }
                return downloadData202;
            case 203:
                if(downloadData203==null)
                {
                    downloadData203=new DownloadData("203");
                }
                return downloadData203;
            case 204:
                if(downloadData204==null)
                {
                    downloadData204=new DownloadData("204");
                }
                return downloadData204;
            case 205:
                if(downloadData205==null)
                {
                    downloadData205=new DownloadData("205");
                }
                return downloadData205;
            case 206:
                if(downloadData206==null)
                {
                    downloadData206=new DownloadData("206");
                }
                return downloadData206;
            case 305:
                if(downloadData305==null)
                {
                    downloadData305=new DownloadData("305");
                }
                return downloadData305;
            case 306:
                if(downloadData306==null)
                {
                    downloadData306=new DownloadData("306");
                }
                return downloadData306;
        }
        return null;
    }

    public static CopyData getCopyDataFromCode(int code)
    {
        switch (code)
        {
            case 101:
                if(copyData101==null)
                {
                    copyData101=new CopyData("101");
                }
                return copyData101;
            case 102:
                if(copyData102==null)
                {
                    copyData102=new CopyData("102");
                }
                return copyData102;
        }
        return null;
    }

    public static GalleryData getGalleryFromCode(int code)
    {
        switch (code)
        {
            case 1:
                if(galleryData1==null)
                {
                    galleryData1=new GalleryData();
                }
                return galleryData1;
            case 2:
                if(galleryData2==null)
                {
                    galleryData2=new GalleryData();
                }
                return galleryData2;
            case 3:
                if(galleryData3==null)
                {
                    galleryData3=new GalleryData();
                }
                return galleryData3;
            case 4:
                if(galleryData4==null)
                {
                    galleryData4=new GalleryData();
                }
                return galleryData4;
        }
        return null;
    }

    public static DeleteData getDeleteDataFromCode(int code)
    {
        switch (code)
        {
            case 1:
                if(deleteData1==null)
                {
                    deleteData1=new DeleteData();
                }
                return deleteData1;
            case 2:
                if(deleteData2==null)
                {
                    deleteData2=new DeleteData();
                }
                return deleteData2;
        }
        return null;
    }

    public static UploadData getUploadDataFromCode(int code)
    {
        switch (code)
        {
            case 301:
                if(uploadData301==null)
                {
                    uploadData301=new UploadData("301");
                }
                return uploadData301;
            case 302:
                if(uploadData302==null)
                {
                    uploadData302=new UploadData("302");
                }
                return uploadData302;
            case 303:
                if(uploadData303==null)
                {
                    uploadData303=new UploadData("303");
                }
                return uploadData303;
            case 304:
                if(uploadData304==null)
                {
                    uploadData304=new UploadData("304");
                }
                return uploadData304;
        }
        return null;
    }

    public static SearchData getSearchData(int code)
    {
        switch (code)
        {
            case 1:
                if(searchData1==null)
                {
                    searchData1=new SearchData();
                }
                return searchData1;
            case 2:
                if(searchData2==null)
                {
                    searchData2=new SearchData();
                }
                return searchData2;
            case 3:
                if(searchData3==null)
                {
                    searchData3=new SearchData();
                }
                return searchData3;
            case 4:
                if(searchData4==null)
                {
                    searchData4=new SearchData();
                }
                return searchData4;
            case 5:
                if(searchData5==null)
                {
                    searchData5=new SearchData();
                }
                return searchData5;
        }
        return null;
    }

    public static StorageAnalyserData getStorageAnalyserData(int code)
    {
        switch (code)
        {
            case 1:
                if (storageAnalyserData1 == null)
                {
                    storageAnalyserData1 = new StorageAnalyserData();
                }
                return storageAnalyserData1;
            case 2:
                if (storageAnalyserData2 == null)
                {
                    storageAnalyserData2 = new StorageAnalyserData();
                }
                return storageAnalyserData2;
            case 3:
                if (storageAnalyserData3 == null)
                {
                    storageAnalyserData3 = new StorageAnalyserData();
                }
                return storageAnalyserData3;
            case 4:
                if (storageAnalyserData4 == null)
                {
                    storageAnalyserData4 = new StorageAnalyserData();
                }
                return storageAnalyserData4;
            case 5:
                if (storageAnalyserData5 == null)
                {
                    storageAnalyserData5 = new StorageAnalyserData();
                }
                return storageAnalyserData5;
        }

        return null;
    }

    public static InstallData getInstallData(int operationCode)
    {
        if(operationCode==99)
        {
            if(installData==null)
            {
                installData=new InstallData();
            }
            return installData;
        }
            return null;
    }
    public static UnInstallData getUnInstallData(int operationCode)
    {
        if(operationCode==199)
        {
            if(unInstallData==null)
            {
                unInstallData=new UnInstallData();
            }
            return unInstallData;
        }
        return null;
    }

    public static class GlobalStorageAnalyser
    {
        public static int storageId=0;
    }




}
