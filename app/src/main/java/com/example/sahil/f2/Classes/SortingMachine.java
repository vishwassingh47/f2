package com.example.sahil.f2.Classes;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by hit4man47 on 12/22/2017.
 */

public class SortingMachine
{
    final private int sortBy;

    public SortingMachine(int sortBy)
    {
        this.sortBy=sortBy;
    }


    public void sortMyFile(ArrayList<MyFile> myFiles)
    {
        switch (sortBy)
        {
            case 1:
                Collections.sort(myFiles,sortMyFile1);
                break;
            case 2:
                Collections.sort(myFiles,sortMyFile2);
                break;
            case 3:
                Collections.sort(myFiles,sortMyFile3);
                break;
            case 4:
                Collections.sort(myFiles,sortMyFile4);
                break;
            case 5:
                Collections.sort(myFiles,sortMyFile5);
                break;
            case 6:
                Collections.sort(myFiles,sortMyFile6);
                break;

            case 10:
                Collections.sort(myFiles,sortMyFile10);
                break;

            case 11:
                Collections.sort(myFiles,sortMyFile11);
                break;
            case 12:
                Collections.sort(myFiles,sortMyFile12);
                break;
            case 13:
                Collections.sort(myFiles,sortMyFile13);
                break;
            case 14:
                Collections.sort(myFiles,sortMyFile14);
                break;
            case 15:
                Collections.sort(myFiles,sortMyFile15);
                break;
            case 16:
                Collections.sort(myFiles,sortMyFile16);
                break;

        }
    }



    public void sortMyContainer(ArrayList<MyContainer> myContainers)
    {
        switch (sortBy)
        {
            case 1:
                Collections.sort(myContainers,sortMyContainer1);
                break;
            case 2:
                Collections.sort(myContainers,sortMyContainer2);
                break;
            case 3:
                Collections.sort(myContainers,sortMyContainer3);
                break;
            case 4:
                Collections.sort(myContainers,sortMyContainer4);
                break;
        }
    }


    public void sortMyApps(ArrayList<MyApp> myAppArrayList)
    {
        switch (sortBy)
        {
            case 1:
                Collections.sort(myAppArrayList, sortMyApp1);
                break;
            case 2:
                Collections.sort(myAppArrayList, sortMyApp2);
                break;
            case 3:
                Collections.sort(myAppArrayList, sortMyApp3);
                break;
            case 4:
                Collections.sort(myAppArrayList, sortMyApp4);
                break;
            case 5:
                Collections.sort(myAppArrayList, sortMyApp5);
                break;
            case 6:
                Collections.sort(myAppArrayList, sortMyApp6);
                break;
        }
    }


    private Comparator<MyFile> sortMyFile1=new Comparator<MyFile>()
    {
        @Override
        public int compare(MyFile o1, MyFile o2)
        {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    };
    private Comparator<MyFile> sortMyFile2=new Comparator<MyFile>()
    {
        @Override
        public int compare(MyFile o1, MyFile o2)
        {
            return o2.getName().compareToIgnoreCase(o1.getName());
        }
    };

    private Comparator<MyFile> sortMyFile3=new Comparator<MyFile>()
    {
        @Override
        public int compare(MyFile o1, MyFile o2)
        {
            long date1=o1.getLastModified();
            long date2=o2.getLastModified();

            if(date1>date2)
                return -1;
            if(date1<date2)
                return 1;
            return 0;
        }
    };

    private Comparator<MyFile> sortMyFile4=new Comparator<MyFile>()
    {
        @Override
        public int compare(MyFile o1, MyFile o2)
        {
            long date1=o1.getLastModified();
            long date2=o2.getLastModified();

            if(date1>date2)
                return 1;
            if(date1<date2)
                return -1;
            return 0;
        }
    };

    private Comparator<MyFile> sortMyFile5=new Comparator<MyFile>()
    {
        @Override
        public int compare(MyFile o1, MyFile o2)
        {
            long size1=o1.getSizeLong();
            long size2=o2.getSizeLong();

            if(size1>size2)
                return -1;
            if(size1<size2)
                return 1;
            return 0;
        }
    };

    private Comparator<MyFile> sortMyFile6=new Comparator<MyFile>()
    {
        @Override
        public int compare(MyFile o1, MyFile o2)
        {
            long size1=o1.getSizeLong();
            long size2=o2.getSizeLong();

            if(size1>size2)
                return 1;
            if(size1<size2)
                return -1;
            return 0;
        }
    };


    private Comparator<MyFile> sortMyFile10=new Comparator<MyFile>()
    {
        @Override
        public int compare(MyFile o1, MyFile o2)
        {
            String name1=o1.getName();
            String name2=o2.getName();

            boolean isHidden1=name1.startsWith(".");
            boolean isHidden2=name2.startsWith(".");
            boolean isFolder1=o1.isFolder();
            boolean isFolder2=o2.isFolder();

            if(isHidden1 && isHidden2)      //BOTH HIDDEN
            {
                if(!isFolder1 && !isFolder2)
                {
                    return name1.compareToIgnoreCase(name2);
                }
                if(isFolder1 && isFolder2)
                {
                    return name1.compareToIgnoreCase(name2);
                }
                if(!isFolder1)
                {
                    return -1;
                }
                return 1;
            }
            else
            {
                if(!isHidden1 && !isHidden2)        //BOTH NOT HIDDEN
                {
                    if(!isFolder1 && !isFolder2)
                    {
                        return name1.compareToIgnoreCase(name2);
                    }
                    if(isFolder1 && isFolder2)
                    {
                        return name1.compareToIgnoreCase(name2);
                    }
                    if(!isFolder1)
                    {
                        return 1;
                    }
                    return -1;
                }
                else
                {
                    if(isHidden1)   //FIRST HIDDEN
                    {
                        return -1;
                    }
                    else            //SECOND HIDDEN
                    {
                        return 1;
                    }
                }

            }
        }
    };

    private Comparator<MyFile> sortMyFile11=new Comparator<MyFile>()
    {
        @Override
        public int compare(MyFile o1, MyFile o2)
        {
            String name1=o1.getName();
            String name2=o2.getName();

            boolean isHidden1=name1.startsWith(".");
            boolean isHidden2=name2.startsWith(".");

            if(isHidden1 && isHidden2)      //BOTH HIDDEN
            {
                return name1.compareToIgnoreCase(name2);
            }
            else
            {
                if(!isHidden1 && !isHidden2)        //BOTH NOT HIDDEN
                {
                    return name1.compareToIgnoreCase(name2);
                }
                else
                {
                    if(isHidden1)   //FIRST HIDDEN
                    {
                        return -1;
                    }
                    else            //SECOND HIDDEN
                    {
                        return 1;
                    }
                }

            }
        }
    };

    private Comparator<MyFile> sortMyFile12=new Comparator<MyFile>()
    {
        @Override
        public int compare(MyFile o1, MyFile o2)
        {
            String name1=o1.getName();
            String name2=o2.getName();

            boolean isHidden1=name1.startsWith(".");
            boolean isHidden2=name2.startsWith(".");

            if(isHidden1 && isHidden2)      //BOTH HIDDEN
            {
                return name2.compareToIgnoreCase(name1);
            }
            else
            {
                if(!isHidden1 && !isHidden2)        //BOTH NOT HIDDEN
                {
                    return name2.compareToIgnoreCase(name1);
                }
                else
                {
                    if(isHidden1)   //FIRST HIDDEN
                    {
                        return -1;
                    }
                    else            //SECOND HIDDEN
                    {
                        return 1;
                    }
                }

            }
        }
    };

    private Comparator<MyFile> sortMyFile13=new Comparator<MyFile>()
    {
        @Override
        public int compare(MyFile o1, MyFile o2)
        {
            long date1=o1.getLastModified();
            long date2=o2.getLastModified();

            boolean isHidden1=o1.getName().startsWith(".");
            boolean isHidden2=o2.getName().startsWith(".");

            if(isHidden1 && isHidden2)      //BOTH HIDDEN
            {
                if(date1>date2)
                    return -1;
                if(date1<date2)
                    return 1;
                return 0;
            }
            else
            {
                if(!isHidden1 && !isHidden2)        //BOTH NOT HIDDEN
                {
                    if(date1>date2)
                        return -1;
                    if(date1<date2)
                        return 1;
                    return 0;
                }
                else
                {
                    if(isHidden1)   //FIRST HIDDEN
                    {
                        return -1;
                    }
                    else            //SECOND HIDDEN
                    {
                        return 1;
                    }
                }

            }
        }
    };

    private Comparator<MyFile> sortMyFile14=new Comparator<MyFile>()
    {
        @Override
        public int compare(MyFile o1, MyFile o2)
        {
            long date1=o1.getLastModified();
            long date2=o2.getLastModified();

            boolean isHidden1=o1.getName().startsWith(".");
            boolean isHidden2=o2.getName().startsWith(".");

            if(isHidden1 && isHidden2)      //BOTH HIDDEN
            {
                if(date1>date2)
                    return 1;
                if(date1<date2)
                    return -1;
                return 0;
            }
            else
            {
                if(!isHidden1 && !isHidden2)        //BOTH NOT HIDDEN
                {
                    if(date1>date2)
                        return 1;
                    if(date1<date2)
                        return -1;
                    return 0;
                }
                else
                {
                    if(isHidden1)   //FIRST HIDDEN
                    {
                        return -1;
                    }
                    else            //SECOND HIDDEN
                    {
                        return 1;
                    }
                }

            }
        }
    };


    private Comparator<MyFile> sortMyFile15=new Comparator<MyFile>()
    {
        @Override
        public int compare(MyFile o1, MyFile o2)
        {
            long size1=o1.getSizeLong();
            long size2=o2.getSizeLong();

            boolean isHidden1=o1.getName().startsWith(".");
            boolean isHidden2=o2.getName().startsWith(".");

            if(isHidden1 && isHidden2)      //BOTH HIDDEN
            {
                if(size1>size2)
                    return -1;
                if(size1<size2)
                    return 1;
                return 0;
            }
            else
            {
                if(!isHidden1 && !isHidden2)        //BOTH NOT HIDDEN
                {
                    if(size1>size2)
                        return -1;
                    if(size1<size2)
                        return 1;
                    return 0;
                }
                else
                {
                    if(isHidden1)   //FIRST HIDDEN
                    {
                        return -1;
                    }
                    else            //SECOND HIDDEN
                    {
                        return 1;
                    }
                }

            }
        }
    };

    private Comparator<MyFile> sortMyFile16=new Comparator<MyFile>()
    {
        @Override
        public int compare(MyFile o1, MyFile o2)
        {
            long size1=o1.getSizeLong();
            long size2=o2.getSizeLong();

            boolean isHidden1=o1.getName().startsWith(".");
            boolean isHidden2=o2.getName().startsWith(".");

            if(isHidden1 && isHidden2)      //BOTH HIDDEN
            {
                if(size1>size2)
                    return 1;
                if(size1<size2)
                    return -1;
                return 0;
            }
            else
            {
                if(!isHidden1 && !isHidden2)        //BOTH NOT HIDDEN
                {
                    if(size1>size2)
                        return 1;
                    if(size1<size2)
                        return -1;
                    return 0;
                }
                else
                {
                    if(isHidden1)   //FIRST HIDDEN
                    {
                        return -1;
                    }
                    else            //SECOND HIDDEN
                    {
                        return 1;
                    }
                }

            }
        }
    };





    private Comparator<MyContainer> sortMyContainer1=new Comparator<MyContainer>()
    {
        @Override
        public int compare(MyContainer o1, MyContainer o2)
        {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    };
    private Comparator<MyContainer> sortMyContainer2=new Comparator<MyContainer>()
    {
        @Override
        public int compare(MyContainer o1, MyContainer o2)
        {
            return o2.getName().compareToIgnoreCase(o1.getName());
        }
    };

    private Comparator<MyContainer> sortMyContainer3=new Comparator<MyContainer>()
    {
        @Override
        public int compare(MyContainer o1, MyContainer o2)
        {
            long date1=o1.getLastModified();
            long date2=o2.getLastModified();

            if(date1>date2)
                return -1;
            if(date1<date2)
                return 1;
            return 0;
        }
    };

    private Comparator<MyContainer> sortMyContainer4=new Comparator<MyContainer>()
    {
        @Override
        public int compare(MyContainer o1, MyContainer o2)
        {
            long date1=o1.getLastModified();
            long date2=o2.getLastModified();

            if(date1>date2)
                return 1;
            if(date1<date2)
                return -1;
            return 0;
        }
    };



    private Comparator<MyApp> sortMyApp1=new Comparator<MyApp>()
    {
        @Override
        public int compare(MyApp o1, MyApp o2)
        {
            return o1.getAppName().compareToIgnoreCase(o2.getAppName());
        }
    };
    private Comparator<MyApp> sortMyApp2=new Comparator<MyApp>()
    {
        @Override
        public int compare(MyApp o1, MyApp o2)
        {
            return o2.getAppName().compareToIgnoreCase(o1.getAppName());
        }
    };

    private Comparator<MyApp> sortMyApp3=new Comparator<MyApp>()
    {
        @Override
        public int compare(MyApp o1, MyApp o2)
        {
            long date1=o1.getLastModified();
            long date2=o2.getLastModified();

            if(date1>date2)
                return -1;
            if(date1<date2)
                return 1;
            return 0;
        }
    };

    private Comparator<MyApp> sortMyApp4=new Comparator<MyApp>()
    {
        @Override
        public int compare(MyApp o1, MyApp o2)
        {
            long date1=o1.getLastModified();
            long date2=o2.getLastModified();

            if(date1>date2)
                return 1;
            if(date1<date2)
                return -1;
            return 0;
        }
    };

    private Comparator<MyApp> sortMyApp5=new Comparator<MyApp>()
    {
        @Override
        public int compare(MyApp o1, MyApp o2)
        {
            long size1=o1.getSizeOfApk();
            long size2=o2.getSizeOfApk();

            if(size1>size2)
                return -1;
            if(size1<size2)
                return 1;
            return 0;
        }
    };

    private Comparator<MyApp> sortMyApp6=new Comparator<MyApp>()
    {
        @Override
        public int compare(MyApp o1, MyApp o2)
        {
            long size1=o1.getSizeOfApk();
            long size2=o2.getSizeOfApk();

            if(size1>size2)
                return 1;
            if(size1<size2)
                return -1;
            return 0;
        }
    };





}
